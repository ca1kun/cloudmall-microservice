import request from '@/utils/request'

// 1. 定义通用的后端返回结构 (根据你的后端 ApiResult 类)
// T 是 data 字段的类型，默认为 any
export interface ApiResult<T = any> {
  code: number
  message: string // 或者是 msg，取决于你后端字段名
  data: T
}

// 2. 定义商品实体接口
export interface Product {
  productId?: number // ID 通常是有的，且修改时必传
  productName: string
  productSn: string
  productDescription?: string
  price: number | string // 价格可能是数字或字符串
  stock: number;
  productCategoryId: number | string
  imageUrl?: string // 对应数据库 image 字段
  detailUrl?: string
  // ... 其他字段
}

// ==========================================
// API 定义 (使用泛型指定返回类型)
// 语法: request.get<请求参数类型, 响应数据类型>(url, config)
// ==========================================

// 根据 SN 获取商品
export const getProdctBySnApi = (sn: string) => {
  // 第二个泛型参数 ApiResult<Product> 告诉 TS 返回值里有 code, message, data(是Product)
  return request.get<any, ApiResult<Product>>(`/product/getBySn/${sn}`)
}

// 获取所有商品
export const listAllProduct = () => {
  // data 是 Product 数组
  return request.get<any, ApiResult<Product[]>>('/product/listAll')
}

// 新增商品
export const addProductApi = (product: Product) => {
  return request.post<any, ApiResult<string>>('/product/add', product)
}

// 更新商品
export const updateProductApi = (product: Product) => {
  return request.put<any, ApiResult<string>>('/product/update', product)
}

// 根据 ID 获取商品
export const getProdctByIdApi = (id: number) => {
  return request.get<any, ApiResult<Product>>(`/product/${id}`)
}

// 删除商品
export const deleteProductApi = (id: number) => {
  return request.delete<any, ApiResult<string>>(`/product/delete/${id}`)
}

// 分页查询
export const getProductPageApi = (params: any) => {
  // 假设分页返回的数据结构比较复杂，暂时用 any，或者定义 PageResult<Product>
  return request.get<any, ApiResult<any>>('/product/page', { params: params });
}

// 批量删除
export const deleteProductsByIdsApi = (productIds: number[] | string[]) => {
    // 处理数组转字符串逻辑
    const idsString = productIds.join(',');
    return request.delete<any, ApiResult<string>>(`/product/deleteByIds/${idsString}`);
}