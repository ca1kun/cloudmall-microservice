import request from '@/utils/request'
import type { EnterItemForm, MakePaymentForm } from '@/types/types'
import type { SaleQueryParams, SaleDTO, PaymentForm } from '@/types/index.d.ts'
// 开始新的销售
export function makeNewSale() {
  return request({
    url: '/sale/new',
    method: 'post',
  })
}
// 录入订单明细
export function enterItem(saleId: number, params: EnterItemForm) {
  return request({
    url: `/sale/${saleId}/items`,
    method: 'post',
    params: params, // itemSn, quantity as query params (matches @RequestParam)
  })
}
// 结束录入
export function endSale(saleId: number) {
  return request({
    url: `/sale/${saleId}`,
    method: 'get',
  })
}

// 确认支付
export function makePayment(saleId: number, params: MakePaymentForm) {
  return request({
    url: `/sale/${saleId}/payment`,
    method: 'post',
    params: params, // cashTendered, payMethod as query params
  })
}

/**
 * 修改订单明细数量
 * @param data 包含 itemSn 和新 quantity 的对象
 */
export function changeQuantity(saleId: number, productId: number, quantity: number) {
  return request({
    url: `/sale/${saleId}/items/${productId}`,
    method: 'put',
    params: { quantity }, // 后端是 @RequestParam("quantity")
  })
}

/**
 * 删除一条订单明细
 * @param itemSn 商品SN
 */
export function deleteSaleItem(saleId: number, productId: number) {
  return request({
    url: `/sale/${saleId}/items/${productId}`,
    method: 'delete',
  })
}

// 根据Id查询订单
export function getSaleById(saleId: number) {
  return request({
    url: '/sale/' + saleId,
    method: 'get',
  })
}

// 根据Id查询订单明细
export function listSaleItemById(saleId: number) {
  return request({
    url: '/sale/listSaleItemById/' + saleId,
    method: 'get',
  })
}

// 分页查询订单
export function listSaleByPage(query: SaleQueryParams) {
  return request({
    url: '/sale/page',
    method: 'get',
    params: query,
  })
}

// 新增支付
export function addPayment(data: PaymentForm) {
  return request({
    url: '/sale/addPayment',
    method: 'post',
    data: data,
  })
}

// 挂起当前订单
export function holdCurrentOrder(saleId: number) {
  return request({
    url: `/sale/${saleId}/hold`,
    method: 'post',
  })
}
