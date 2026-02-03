package edu.scau.mis.order.service;

import edu.scau.mis.common.domain.*;
import edu.scau.mis.common.dto.StockLockDTO;
import edu.scau.mis.common.exception.ServiceException;
import edu.scau.mis.order.domain.OmsOrderItem;
import edu.scau.mis.order.dto.OrderParam;
import edu.scau.mis.order.feign.RemoteCartService;
import edu.scau.mis.order.feign.RemoteMarketingService;
import edu.scau.mis.order.feign.RemoteProductService;
import edu.scau.mis.order.mapper.OmsOrderItemMapper;
import edu.scau.mis.common.mapper.OmsOrderMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OmsOrderMapper orderMapper;
    @Autowired
    private OmsOrderItemMapper orderItemMapper;
    @Autowired
    private RemoteCartService cartService;
    @Autowired
    private RemoteProductService productService;
    // 👇 新增：注入营销服务，用于查券和核销
    @Autowired
    private RemoteMarketingService marketingService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // 线程安全的日期格式化器
    private static final DateTimeFormatter ORDER_SN_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    /**
     * 创建订单
     * 核心流程：查购物车 -> 算总价 -> 减优惠 -> 锁库存 -> 生成订单 -> 锁优惠券 -> 清购物车 -> 发延时消息
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> createOrder(OrderParam orderParam) {
        // 1. 获取当前登录用户信息
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getId();

        // 2. 远程查询购物车商品
        ApiResult<List<OmsCartItem>> cartResult = cartService.list();
        List<OmsCartItem> cartList = cartResult.getData();
        if (cartList == null || cartList.isEmpty()) {
            throw new ServiceException("购物车为空，无法下单");
        }

        // 收集需要购买的商品ID (用于后续清空购物车)
        List<Long> productIds = cartList.stream()
                .map(OmsCartItem::getProductId)
                .collect(Collectors.toList());

        // 3. 准备订单项 & 计算原始总价
        List<OmsOrderItem> orderItemList = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<StockLockDTO> stockLockList = new ArrayList<>();

        for (OmsCartItem cartItem : cartList) {
            // 转换为订单详情对象
            OmsOrderItem orderItem = new OmsOrderItem();
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setProductName(cartItem.getProductName());
            orderItem.setProductPic(cartItem.getProductPic());
            orderItem.setProductPrice(cartItem.getPrice());
            orderItem.setProductQuantity(cartItem.getQuantity());
            orderItemList.add(orderItem);

            // 累加总价 (单价 * 数量)
            BigDecimal itemAmount = cartItem.getPrice().multiply(new BigDecimal(cartItem.getQuantity()));
            totalAmount = totalAmount.add(itemAmount);

            // 准备扣库存参数
            StockLockDTO lockDTO = new StockLockDTO();
            lockDTO.setProductId(cartItem.getProductId());
            lockDTO.setCount(cartItem.getQuantity());
            stockLockList.add(lockDTO);
        }

        // 4. 处理优惠券逻辑 (新增部分)
        BigDecimal payAmount = totalAmount; // 默认实付 = 总价
        BigDecimal couponAmount = BigDecimal.ZERO; // 优惠金额

        if (orderParam.getCouponId() != null) {
            // 4.1 远程查询优惠券详情
            ApiResult<SmsCoupon> couponRes = marketingService.getCouponInfo(orderParam.getCouponId());
            if (couponRes.getCode() != 200 || couponRes.getData() == null) {
                throw new ServiceException("优惠券不存在或无法使用");
            }
            SmsCoupon coupon = couponRes.getData();

            // 4.2 校验使用门槛
            if (totalAmount.compareTo(coupon.getMinPoint()) < 0) {
                throw new ServiceException("未满足优惠券使用门槛：满 " + coupon.getMinPoint() + " 可用");
            }

            // 4.3 计算实付金额 (防止负数)
            couponAmount = coupon.getAmount();
            payAmount = totalAmount.subtract(couponAmount);
            if (payAmount.compareTo(BigDecimal.ZERO) < 0) {
                payAmount = new BigDecimal("0.01"); // 最少付1分钱
            }
        }

        // 5. 远程锁定库存 (mis-web)
        // 注意：这一步是分布式操作，如果失败会抛异常回滚本地事务
        ApiResult<String> lockResult = productService.lockStock(stockLockList);
        if (lockResult.getCode() != 200) {
            throw new ServiceException("库存不足: " + lockResult.getMessage());
        }

        // 6. 插入订单主表
        OmsOrder order = new OmsOrder();
        order.setOrderSn(generateOrderSn());
        order.setMemberId(userId);
        order.setMemberUsername(loginUser.getUsername());
        order.setCreateTime(LocalDateTime.now());

        order.setTotalAmount(totalAmount); // 原价
        order.setPayAmount(payAmount);     // 实付价
        order.setCouponAmount(couponAmount); // 优惠金额

        order.setStatus(0); // 0-待付款
        order.setNote(orderParam.getNote());

        orderMapper.insert(order);

        // 7. 插入订单详情表
        for (OmsOrderItem item : orderItemList) {
            item.setOrderId(order.getId());
            item.setOrderSn(order.getOrderSn());
            orderItemMapper.insert(item);
        }

        // 8. 远程核销优惠券 (新增部分)
        if (orderParam.getCouponId() != null) {
            ApiResult<String> useResult = marketingService.useCoupon(orderParam.getCouponId(), order.getId());
            if (useResult.getCode() != 200) {
                // 如果核销失败（比如券已经被用了），必须回滚整个订单
                throw new ServiceException("优惠券核销失败: " + useResult.getMessage());
            }
        }

        // 9. 发送延迟消息 (30分钟未支付自动关单)
        rabbitTemplate.convertAndSend("order.event.exchange", "order.create", order.getId());

        // 10. 同步清空购物车
        if (!productIds.isEmpty()) {
            ApiResult<String> cartRes = cartService.deleteBatch(productIds);
            if (cartRes.getCode() != 200) {
                throw new ServiceException("清空购物车失败");
            }
        }

        // 返回结果给前端
        Map<String, Object> result = new HashMap<>();
        result.put("orderId", order.getId());
        result.put("orderSn", order.getOrderSn());
        result.put("payAmount", order.getPayAmount());
        return result;
    }

    /**
     * 根据ID查询订单详情
     */
    public OmsOrder getOrderById(Long id) {
        return orderMapper.selectById(id);
    }

    /**
     * 生成订单号 (时间戳 + 随机数)
     * 格式：20260125203000123 + 4位随机
     */
    private String generateOrderSn() {
        String timestamp = LocalDateTime.now().format(ORDER_SN_FORMATTER);
        int randomNum = ThreadLocalRandom.current().nextInt(1000, 10000);
        return timestamp + randomNum;
    }
}