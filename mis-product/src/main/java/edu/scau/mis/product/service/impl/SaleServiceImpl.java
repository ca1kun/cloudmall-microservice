package edu.scau.mis.product.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.scau.mis.common.utils.SecurityUtils;
import edu.scau.mis.product.domain.Payment;
import edu.scau.mis.common.domain.Product;
import edu.scau.mis.product.domain.Sale;
import edu.scau.mis.product.domain.SaleItem;
import edu.scau.mis.product.enums.SaleStatusEnum;
import edu.scau.mis.product.mapper.IPaymentMapper;
import edu.scau.mis.product.mapper.ISaleItemMapper;
import edu.scau.mis.product.mapper.ISaleMapper;
import edu.scau.mis.product.service.ISaleService;
import edu.scau.mis.product.utils.RedisIdWorker;
import edu.scau.mis.common.vo.PaymentResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class SaleServiceImpl extends ServiceImpl<IPaymentMapper, Payment> implements ISaleService {

    @Autowired
    private ISaleMapper saleMapper;
    @Autowired
    private ISaleItemMapper saleItemMapper;
    @Autowired
    private IPaymentMapper paymentMapper;
    @Autowired
    private RedisIdWorker redisIdWorker;
    @Autowired
    private StringRedisTemplate stringRedisTemplate; // 注入Redis模板

    // 定义购物车在Redis中的缓存key前缀
    private static final String SALE_CART_KEY_PREFIX = "cart:sale:";
    // 定义购物车缓存的过期时间（分钟）
    private static final long SALE_CART_TTL =30;


    @Override
    public Sale makeNewSale() {
        // 1. 使用RedisIdWorker生成唯一的ID作为saleId
        long saleId = redisIdWorker.nextId("sale");
        String saleNo = "so-" + saleId;

        // 2. 创建一个临时的Sale对象，注意此时它还没有被存入数据库
        Sale newSale = new Sale(saleNo);
        newSale.setSaleId(saleId); // 手动设置ID

        // 3. 将这个新订单（作为购物车）放入Redis缓存
        cacheSale(newSale);
        return newSale;
    }


    @Override
    public List<SaleItem> makeLineItem(Long saleId, Product product, int quantity) {
        // 1. 从缓存中加载购物车
        Sale sale = this.loadSaleFromCache(saleId);

        // 2. 【关键修复】如果缓存中没有，增加数据库检查作为后备诊断
        if (sale == null) {
            // 尝试从数据库加载这个订单，以判断它是一个无效ID还是一个已完成的订单
            Sale persistedSale = saleMapper.selectSaleById(saleId);

            if (persistedSale != null) {
                // 如果数据库中存在这个订单，说明它已经被支付或挂起
                // 返回一个更具体的错误信息
                throw new IllegalStateException("操作失败：该订单已支付或已挂起，不能再添加商品。");
            } else {
                // 如果数据库中也不存在，说明这是一个无效的ID，或者缓存确实已过期
                throw new IllegalStateException("无法向一个不存在或已过期的订单中添加商品，请开始新的销售。");
            }
        }

        // 3. 调用领域方法更新内存中的购物车（现在可以确保 sale 不是 null）
        sale.addItem(product, quantity);

        // 4. 将更新后的购物车写回缓存
        cacheSale(sale);
        return sale.getSaleItems();
    }


    @Override
    public Sale endSale(Long saleId) {
        // 结束销售时，也从缓存中获取最新的购物车状态
        return this.loadSaleFromCache(saleId);
    }

    @Override
    @Transactional
    public PaymentResultVo makePayment(Long saleId, BigDecimal cashTendered, String payMethod) {
        // 1. 从Redis缓存中加载最终的购物车状态
        Sale saleToPay = this.loadSaleFromCache(saleId);
        if (saleToPay == null) {
            throw new IllegalStateException("订单不存在或已过期，无法支付");
        }

        // 2. 业务校验
        if (CollectionUtil.isEmpty(saleToPay.getSaleItems())) {
            throw new IllegalStateException("购物车为空，无法支付");
        }
        saleToPay.completePayment(); // 更新内存中对象的状态为“已支付”

        // 3. 【新增】持久化Sale主记录
        // 因为之前没有入库，所以这里是insert
        saleMapper.insertSale(saleToPay);

        // 4. 创建并持久化支付记录
        Payment payment = new Payment();
        payment.setSaleId(saleToPay.getSaleId());
        payment.setAmount(saleToPay.getTotal());
        payment.setPayMethod(payMethod);
        payment.setPayTime(new Date());
        payment.setPaymentNo("PAY-" + redisIdWorker.nextId("payment"));
        paymentMapper.insert(payment);

        // 5. 调用业务方法记录支付ID
        saleToPay.recordPayment(payment.getPaymentId());
        // 【新增】更新Sale记录，把paymentId更新进去
        saleMapper.updateSale(saleToPay);

        // 6. 持久化SaleItem列表
        List<SaleItem> saleItemsToInsert = saleToPay.getSaleItems();
        if (!saleItemsToInsert.isEmpty()) {
            for (SaleItem item : saleItemsToInsert) {
                item.setSaleId(saleToPay.getSaleId());
                item.setProductId(item.getProductId());
                item.setPrice(item.getPrice()); // 创建价格快照
                item.setStatus(SaleStatusEnum.PAID.getCode());
            }
            saleItemMapper.batchInsertSaleItemOfCurrentSale(saleItemsToInsert);
        }

        // 7. 计算找零
        BigDecimal change = cashTendered.subtract(saleToPay.getTotal());

        // 8. 清除购物车缓存
        clearSaleCache(saleId);

        return new PaymentResultVo(change);
    }

    @Override
    public List<SaleItem> changeItemQuantity(Long saleId, Long productId, int quantity) {
        Sale sale = this.loadSaleFromCache(saleId);
        // 【关键修复】增加健壮性检查
        if (sale == null) {
            // 修改数量的操作必须基于一个活跃的购物车，如果缓存没有，直接拒绝操作。
            throw new IllegalStateException("操作失败：当前订单不存在或已过期，无法修改商品数量。");
        }
        sale.changeItemQuantity(productId, quantity);
        cacheSale(sale);
        return sale.getSaleItems();
    }

    @Override
    public List<SaleItem> deleteSaleItem(Long saleId, Long productId) {
        Sale sale = this.loadSaleFromCache(saleId);
        // 【关键修复】增加健壮性检查
        if (sale == null) {
            // 删除商品的操作也必须基于一个活跃的购物车。
            throw new IllegalStateException("操作失败：当前订单不存在或已过期，无法删除商品。");
        }
        sale.removeItem(productId);
        cacheSale(sale);
        return sale.getSaleItems();
    }


    @Override
    @Transactional
    public Sale holdCurrentOrder(Long saleId) {
        // 1. 从Redis加载购物车
        Sale saleToHold = this.loadSaleFromCache(saleId);
        if (saleToHold == null) {
            throw new IllegalStateException("无法挂起一个不存在或已过期的订单");
        }

        // 2. 调用业务方法来改变状态
        saleToHold.hold();

        // 3. 持久化Sale聚合根（主表和子表）
        // 3.1 插入Sale主记录
        saleMapper.insertSale(saleToHold);

        // 3.2 插入SaleItem子表记录
        List<SaleItem> itemsToHold = saleToHold.getSaleItems();
        if (!itemsToHold.isEmpty()) {
            for (SaleItem item : itemsToHold) {
                item.setSaleId(saleToHold.getSaleId());
                item.setProductId(item.getProductId());
                item.setPrice(item.getPrice());
                item.setStatus(SaleStatusEnum.RESERVED.getCode());
            }
            saleItemMapper.batchInsertSaleItemOfCurrentSale(itemsToHold);
        }

        // 4. 清除Redis中的购物车缓存
        clearSaleCache(saleId);

        return saleToHold;
    }



    // --- 缓存辅助方法 ---

    /**
     * 从Redis缓存中加载购物车
     * @param saleId 订单ID
     * @return Sale 聚合根
     */
    private Sale loadSaleFromCache(Long saleId) {
        String key = SALE_CART_KEY_PREFIX + saleId;
        String saleJson = stringRedisTemplate.opsForValue().get(key);
        if (saleJson == null) {
            // 在新逻辑下，如果缓存没有，通常意味着购物车不存在或已过期。
            // 如果需要支持处理已挂起的订单，则需要从数据库加载。
            // 为保持购物车逻辑的纯粹性，这里返回null，让调用方决定如何处理。
            return null;
        }
        return JSONUtil.toBean(saleJson, Sale.class);
    }

    /**
     * 将购物车状态写入Redis缓存
     * @param sale 要缓存的Sale对象
     */
    private void cacheSale(Sale sale) {
        String key = SALE_CART_KEY_PREFIX + sale.getSaleId();
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(sale), SALE_CART_TTL, TimeUnit.MINUTES);
    }

    /**
     * 清除Redis中的购物车缓存
     * @param saleId 订单ID
     */
    private void clearSaleCache(Long saleId) {
        String key = SALE_CART_KEY_PREFIX + saleId;
        stringRedisTemplate.delete(key);
    }

    // --- 数据库加载辅助方法 ---
    private Sale loadSaleAggregate(Long saleId) {
        Sale sale = saleMapper.selectSaleById(saleId);
        if (sale == null) {
            // 【优化】如果 sale 为 null，直接返回，避免后续的空指针
            return null;
        }
        List<SaleItem> items = saleItemMapper.selectSaleItemsBySaleId(saleId);
        sale.loadItems(items); // 将查询到的 SaleItem 列表加载到 Sale 对象中
        return sale;
    }


    @Override
    public List<SaleItem> listSaleItemBySaleId(Long saleId) {
        return saleItemMapper.selectSaleItemsBySaleId(saleId);
    }

    @Override
    public List<Sale> selectSaleList(Sale sale) {
        return saleMapper.selectSaleList(sale);
    }

    @Transactional
    @Override
    public void insertPayment(Payment payment) {
        // 【关键修复】使用 loadSaleAggregate 方法加载完整的 Sale 聚合根
        // Sale sale = saleMapper.selectSaleById(payment.getSaleId()); // <-- 替换这行
        Sale sale = this.loadSaleAggregate(payment.getSaleId()); // <-- 使用这行

        // 下面的校验现在可以正常工作了，因为 sale.getSaleItems() 不再为空
        if (sale == null || SaleStatusEnum.PAID.getCode().equals(sale.getStatus())) {
            throw new RuntimeException("订单不存在或已支付");
        }

        payment.setPaymentNo("PAY-" + redisIdWorker.nextId("payment"));
        payment.setPayTime(new Date());
        payment.setAmount(sale.getTotal());
        paymentMapper.insert(payment);

        // 【修改点】调用业务方法来更新状态和ID
        // 这里的 sale 对象现在是完整的，所以 completePayment() 不会再报错
        sale.completePayment();
        sale.recordPayment(payment.getPaymentId());

        int updatedSaleRows = saleMapper.updateSale(sale);

        if (updatedSaleRows > 0) {
            // 【优化建议】这里的 updateStatusBySaleId 也可以用 AOP 自动填充
            // 但为了保持逻辑清晰，暂时保留手动设置
            // 【关键修复】在调用时，手动传入 updateBy 的值
            String currentUser = SecurityUtils.getUsername(); // 从SecurityUtils获取当前用户
            saleItemMapper.updateStatusBySaleId(
                    payment.getSaleId(),
                    SaleStatusEnum.PAID.getCode(),
                    (currentUser != null) ? currentUser : "system", // <-- 传入用户名，提供默认值
                    new Date()
            );
        }
    }


}
