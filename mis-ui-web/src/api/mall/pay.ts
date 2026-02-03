import request from '@/utils/request'

// 发起支付宝支付
// 注意：后端返回的是 HTML 字符串，不是 JSON，所以不要用 request.get<ApiResult>
// 只要能拿到 raw data 就行
export const alipayApi = (orderId: number) => {
  return request({
    url: '/pay/alipay',
    method: 'get',
    params: { orderId }
  })
}