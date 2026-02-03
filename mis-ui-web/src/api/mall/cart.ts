import request from '@/utils/request'

// 购物车项类型定义
export interface CartItem {
  id?: number
  productId: number
  productName: string
  productPic: string
  price: number
  quantity: number
}

// 获取购物车列表
export const getCartListApi = () => {
  return request.get<any, any>('/cart/list')
}

// 添加/修改购物车
// 注意：后端逻辑是“累加”，传入 1 代表数量+1，传入 -1 代表数量-1
export const addCartApi = (data: { productId: number; quantity: number }) => {
  return request.post<any, any>('/cart/add', data)
}

// 删除购物车商品
export const deleteCartApi = (productId: number) => {
    console.log('准备删除，ID:', productId)
  return request.delete<any, any>(`/cart/delete/${productId}`)
}