package edu.scau.mis.cart.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.scau.mis.cart.config.RabbitConfig;
import edu.scau.mis.cart.mapper.CartItemMapper;
import edu.scau.mis.common.domain.OmsCartItem;
import edu.scau.mis.cart.feign.RemoteProductService;
import edu.scau.mis.common.domain.ApiResult;
import edu.scau.mis.common.domain.Product;
import edu.scau.mis.common.exception.ServiceException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CartService extends ServiceImpl<CartItemMapper, OmsCartItem> {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate; // 使用 common 里的 JSON 配置
    @Autowired
    private RabbitTemplate rabbitTemplate;

    // ✅ 注入远程服务客户端
    @Autowired
    private RemoteProductService remoteProductService;


    private static final String CART_KEY_PREFIX = "cart:user:";

    /**
     * 添加/修改购物车
     */
    public void addCart(Long userId, OmsCartItem cartItem) {
        String key = CART_KEY_PREFIX + userId;
        String hashKey = cartItem.getProductId().toString();

        OmsCartItem existingItem = (OmsCartItem) redisTemplate.opsForHash().get(key, hashKey);

        if (existingItem != null) {
            // 有数据 -> 累加数量
            existingItem.setQuantity(existingItem.getQuantity() + cartItem.getQuantity());
            existingItem.setModifyDate(new Date());
            cartItem = existingItem;
        } else {
            // 无数据 -> 新增

            // 🚀【关键点】通过 Feign 远程调用 8081 获取商品信息
            ApiResult<Product> result = remoteProductService.getProductById(cartItem.getProductId());

            // 👇👇👇 加这行调试日志 👇👇👇
            System.out.println("远程调用结果: " + result);
            if (result != null) {
                System.out.println("Code: " + result.getCode());
                System.out.println("Msg: " + result.getMessage());
                System.out.println("Data: " + result.getData());
            }
            if (result == null || result.getData() == null) {
                throw new ServiceException("商品不存在或已下架");
            }
            Product product = result.getData();

            // 补全信息
            cartItem.setMemberId(userId);
            cartItem.setPrice(product.getPrice());
            cartItem.setProductName(product.getProductName());
            cartItem.setProductPic(product.getImageUrl());
            cartItem.setCreateDate(new Date());
            cartItem.setModifyDate(new Date());
        }

        // 保存到Redis前确保ID被设置
        if (cartItem.getId() == null) {
            // 如果cartItem没有ID，从数据库查询或创建新的
            LambdaQueryWrapper<OmsCartItem> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OmsCartItem::getMemberId, userId)
                    .eq(OmsCartItem::getProductId, cartItem.getProductId());
            OmsCartItem existingDbItem = this.getOne(wrapper);

            if (existingDbItem != null) {
                cartItem.setId(existingDbItem.getId());
            }
        }
        // ... 写 Redis 和 发 MQ (保持不变) ...
        redisTemplate.opsForHash().put(key, hashKey, cartItem);
        redisTemplate.expire(key, 30, TimeUnit.DAYS);
        rabbitTemplate.convertAndSend(RabbitConfig.CART_SYNC_QUEUE, cartItem);
    }

    /**
     * 查询购物车列表
     */
    public List<OmsCartItem> list(Long userId) {
        String key = CART_KEY_PREFIX + userId;
        List<OmsCartItem> cartList = new ArrayList<>();

        // 1. 先查 Redis
        List<Object> values = redisTemplate.opsForHash().values(key);

        if (!values.isEmpty()) {
            cartList = values.stream().map(o -> (OmsCartItem) o).collect(Collectors.toList());

            // 优化建议：ID 应该在 addCart 时就生成好存入 Redis
            // 但如果旧数据确实没 ID，这里做一个简单的补救（还是会查库，影响性能，但逻辑是对的）
            boolean needUpdateRedis = false;
            for (OmsCartItem item : cartList) {
                if (item.getId() == null) {
                    // 查库补 ID
                    OmsCartItem dbItem = this.getOne(new LambdaQueryWrapper<OmsCartItem>()
                            .eq(OmsCartItem::getMemberId, userId)
                            .eq(OmsCartItem::getProductId, item.getProductId()));
                    if (dbItem != null) {
                        item.setId(dbItem.getId());
                        // 既然查到了 ID，顺便更新回 Redis，下次就不用查了
                        redisTemplate.opsForHash().put(key, item.getProductId().toString(), item);
                        needUpdateRedis = true;
                    }
                }
            }
            if (needUpdateRedis) {
                redisTemplate.expire(key, 30, TimeUnit.DAYS);
            }

        } else {
            // 2. Redis 没数据，查 MySQL 兜底
            cartList = this.list(new LambdaQueryWrapper<OmsCartItem>().eq(OmsCartItem::getMemberId, userId));

            // 3. 回填 Redis
            if (!cartList.isEmpty()) {
                Map<String, Object> map = cartList.stream()
                        .collect(Collectors.toMap(k -> k.getProductId().toString(), v -> v));
                redisTemplate.opsForHash().putAll(key, map);
                redisTemplate.expire(key, 30, TimeUnit.DAYS);
            }
        }

        if (cartList.isEmpty()) return cartList;

        // ================== 核心新增：批量回填最新价格 ==================

        // 4. 收集商品ID
        List<Long> productIds = cartList.stream()
                .map(OmsCartItem::getProductId)
                .collect(Collectors.toList());

        // 5. 远程批量查询 (Feign -> mis-web)
        try {
            ApiResult<List<Product>> productRes = remoteProductService.getProductsByIds(productIds);

            if (productRes.getCode() == 200 && productRes.getData() != null) {
                Map<Long, BigDecimal> priceMap = productRes.getData().stream()
                        .collect(Collectors.toMap(Product::getProductId, Product::getPrice));

                for (OmsCartItem item : cartList) {
                    BigDecimal currentPrice = priceMap.get(item.getProductId());
                    // 如果查到了最新价，填入；没查到(下架)，填 0
                    item.setCurrentPrice(currentPrice != null ? currentPrice : BigDecimal.ZERO);
                }
            }
        } catch (Exception e) {
            // 降级：远程调用失败，用旧价格兜底
            cartList.forEach(item -> item.setCurrentPrice(item.getPrice()));
            System.err.println("远程查询价格失败: " + e.getMessage());
        }

        return cartList;
    }


    /**
     * 删除购物车商品
     */
    public void delete(Long userId, Long productId) {
        String key = CART_KEY_PREFIX + userId;

        // 1. 删 Redis
        redisTemplate.opsForHash().delete(key, productId.toString());

        // 2. 发 MQ 删数据库 (或者直接调 DB 删)
        // 这里简单点，直接由 Service 删数据库（因为删除操作频率低，且不需要保证极高性能）
        LambdaQueryWrapper<OmsCartItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OmsCartItem::getMemberId, userId)
                .eq(OmsCartItem::getProductId, productId);
        this.remove(wrapper);

        System.out.println("🛒 商品 " + productId + " 已从购物车移除");
    }

    /**
     * 批量删除购物车项目
     */
    public void deleteBatch(List<Long> productIds, Long userId) {
        // 验证输入参数
        if (productIds == null || productIds.isEmpty() || userId == null) {
            System.out.println("⚠️ 批量删除参数无效: productIds=" + productIds + ", userId=" + userId);
            return;
        }

        String key = CART_KEY_PREFIX + userId;

        // 过滤掉 null 值并转换为字符串
        List<String> hashKeys = productIds.stream()
                .filter(Objects::nonNull)
                .map(productId -> productId.toString())
                .collect(Collectors.toList());

        if (!hashKeys.isEmpty()) {
            redisTemplate.opsForHash().delete(key, hashKeys.toArray(new String[0]));
        }

        // 从数据库中批量删除
        LambdaQueryWrapper<OmsCartItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OmsCartItem::getMemberId, userId)
                .in(OmsCartItem::getProductId, productIds);
        this.remove(wrapper);

        System.out.println("🛒 批量删除购物车项目: " + productIds);
    }


}