import request from '@/utils/request'

export interface OrderParam {
  addressId: number
  payType: number // 1:支付宝 2:微信
  note: string
}

// 创建订单
export const createOrderApi = (data: OrderParam) => {
  return request.post<any, any>('/order/create', data)
}