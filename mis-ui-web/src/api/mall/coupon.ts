import request from '@/utils/request'

export interface Coupon {
  id: number
  name: string
  amount: number
  count: number
  startTime: string
  endTime: string
}

// 获取可领取列表
export const getCouponListApi = () => {
  return request.get<any, any>('/coupon/list')
}

// 领取优惠券 (秒杀)
export const receiveCouponApi = (couponId: number) => {
  return request.post<any, any>(`/coupon/seckill/${couponId}`)
}

// 获取我已领取的优惠券ID列表
export const getMyCouponIdsApi = () => {
  return request.get<any, any>('/coupon/my/ids')
}