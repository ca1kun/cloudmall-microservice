import request from '@/utils/request'

export interface CouponForm {
  id?: number
  name: string
  count: number
  amount: number
  minPoint: number
  perLimit: number
  startTime: string
  endTime: string
}

// 分页查询优惠券列表 (管理员用)
export const getCouponPageApi = (params: any) => {
  return request.get<any, any>('/coupon/page', { params }) // 后端需要补一个分页接口，或者直接用 list
}

// 新增优惠券
export const addCouponApi = (data: CouponForm) => {
  return request.post<any, any>('/coupon/add', data)
}

// 预热优惠券
export const preheatCouponApi = (id: number) => {
  return request.post<any, any>(`/coupon/preheat/${id}`)
}