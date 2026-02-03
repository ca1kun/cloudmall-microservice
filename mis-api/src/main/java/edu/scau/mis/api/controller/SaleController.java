package edu.scau.mis.api.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import edu.scau.mis.common.domain.ApiResult;
import edu.scau.mis.product.domain.Payment;
import edu.scau.mis.common.domain.Product;
import edu.scau.mis.product.domain.Sale;
import edu.scau.mis.product.domain.SaleItem;
import edu.scau.mis.product.service.IProductService;
import edu.scau.mis.product.service.ISaleService;
import edu.scau.mis.common.vo.PaymentResultVo;
import edu.scau.mis.common.vo.SaleItemVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sale")
@Tag(name = "销售管理", description = "提供POS机销售流程的核心API")
public class SaleController {

    @Autowired
    private ISaleService saleService;

    @Autowired
    private IProductService productService;

    @PostMapping("/new")
    @Operation(summary = "1. 开始一次新的销售", description = "创建一个持久化的、状态为'未支付'的订单，并返回其ID。客户端需保存此ID用于后续操作。")
    public ApiResult<Sale> makeNewSale() {
        return ApiResult.success("新订单已创建", saleService.makeNewSale());
    }

    @PostMapping("/{saleId}/items")
    @Operation(summary = "2. 向指定订单添加商品", description = "根据商品编码(itemSn)和数量，向指定ID的订单中添加商品。")
    public ApiResult<List<SaleItemVo>> enterItem(
            @Parameter(description = "订单ID") @PathVariable("saleId") Long saleId,
            @Parameter(description = "商品编码") @RequestParam("itemSn") String itemSn,
            @Parameter(description = "数量") @RequestParam("quantity") int quantity) {

        Product product = productService.getProductBySn(itemSn);
        if (product == null) {
            return ApiResult.error("商品不存在: " + itemSn);
        }

        // 调用已改造的、需要saleId的Service方法
        List<SaleItem> updatedSaleItems = saleService.makeLineItem(saleId, product, quantity);

        return ApiResult.success(transformSaleItemsToSaleItemVos(updatedSaleItems));
    }

    @GetMapping("/{saleId}")
    @Operation(summary = "3. 获取订单最终状态", description = "在支付前，获取指定订单的最终商品列表和总价。")
    public ApiResult<Sale> getSaleDetails(@Parameter(description = "订单ID") @PathVariable("saleId") Long saleId) {
        Sale sale = saleService.endSale(saleId);

        // 【关键修复】增加健壮性检查，确保total不为null
        if (sale != null && sale.getTotal() == null) {
            sale.setTotal(BigDecimal.ZERO); // 如果total为null，则设置为0
        }

        return ApiResult.success(sale);
    }

    @PostMapping("/{saleId}/payment")
    @Operation(summary = "4. 对指定订单进行支付", description = "完成支付流程，并返回找零信息。")
    public ApiResult<PaymentResultVo> makePayment(
            @Parameter(description = "订单ID") @PathVariable("saleId") Long saleId,
            @Parameter(description = "顾客支付金额") @RequestParam("cashTendered") BigDecimal cashTendered,
            @Parameter(description = "支付方式") @RequestParam("payMethod") String payMethod) {
        return ApiResult.success(saleService.makePayment(saleId, cashTendered, payMethod));
    }

    @PutMapping("/{saleId}/items/{productId}")
    @Operation(summary = "修改订单中商品的数量", description = "修改指定订单中某个商品的数量。")
    public ApiResult<List<SaleItemVo>> changeQuantity(
            @Parameter(description = "订单ID") @PathVariable("saleId") Long saleId,
            @Parameter(description = "商品ID") @PathVariable("productId") Long productId,
            @Parameter(description = "新的数量") @RequestParam("quantity") int quantity) {

        List<SaleItem> updatedItems = saleService.changeItemQuantity(saleId, productId, quantity);
        return ApiResult.success(transformSaleItemsToSaleItemVos(updatedItems));
    }

    @DeleteMapping("/{saleId}/items/{productId}")
    @Operation(summary = "从订单中删除商品", description = "从指定订单中移除一个商品。")
    public ApiResult<List<SaleItemVo>> deleteSaleItem(
            @Parameter(description = "订单ID") @PathVariable("saleId") Long saleId,
            @Parameter(description = "商品ID") @PathVariable("productId") Long productId) {

        List<SaleItem> updatedItems = saleService.deleteSaleItem(saleId, productId);
        return ApiResult.success(transformSaleItemsToSaleItemVos(updatedItems));
    }

    @PostMapping("/{saleId}/hold")
    @Operation(summary = "挂起指定订单", description = "将当前订单置为挂起状态，以便后续处理。")
    public ApiResult<Sale> holdOrder(@Parameter(description = "订单ID") @PathVariable("saleId") Long saleId) {
        return ApiResult.success(saleService.holdCurrentOrder(saleId));
    }

    // --- 其他管理类API ---

    @GetMapping("/listSaleItemById/{saleId}")
    @Operation(summary = "查询指定订单的明细", description = "用于后台管理查看订单详情。")
    public ApiResult<List<SaleItemVo>> listSaleItemVosById(@Parameter(description = "订单ID") @PathVariable("saleId") Long saleId) {
        List<SaleItem> saleItems = saleService.listSaleItemBySaleId(saleId);
        return ApiResult.success(this.transformSaleItemsToSaleItemVos(saleItems));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询订单列表", description = "用于后台管理，支持按条件筛选。")
    public ApiResult<PageInfo<Sale>> page(
            @Parameter(description = "页码") @RequestParam("pageNum") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam("pageSize") Integer pageSize,
            Sale sale) { // Spring会自动将查询参数绑定到sale对象
        PageHelper.startPage(pageNum, pageSize);
        List<Sale> list = saleService.selectSaleList(sale);
        PageInfo<Sale> pageInfo = new PageInfo<>(list);
        return ApiResult.success(pageInfo);
    }

    @PostMapping("/addPayment")
    @Operation(summary = "补录支付信息", description = "为未支付的订单手动添加一笔支付记录。")
    public ApiResult<String> addPayment(@RequestBody Payment payment) {
        saleService.insertPayment(payment);
        return ApiResult.success("支付成功");
    }

    /**
     * 辅助方法：将后端的 SaleItem 领域对象列表转换为前端需要的 SaleItemVo 视图对象列表。
     * @param saleItems 领域对象列表
     * @return 视图对象列表
     */
    private List<SaleItemVo> transformSaleItemsToSaleItemVos(List<SaleItem> saleItems) {
        if (saleItems == null) {
            return Collections.emptyList();
        }

        return saleItems.stream().map(item -> {
            SaleItemVo vo = new SaleItemVo();
            // 【最终版本】增加 productId 的映射
            vo.setProductId(item.getProductId());
            vo.setItemSn(item.getProductSn());
            vo.setProductName(item.getProductName());
            vo.setPrice(item.getPrice());
            vo.setQuantity(item.getQuantity());
            return vo;
        }).collect(Collectors.toList());
    }

}
