import request from '@/utils/request'

// 定义后端统一返回格式
export interface ApiResult<T = any> {
  code: number
  message: string
  data: T
}
// src/api/item/category.ts
export const listCategory = () => {
  // <any, ApiResult<any[]>> 
  // 第一个 any 是请求参数类型，第二个是【响应体】类型
  return request.get<any, ApiResult<any[]>>('/category/listAll')
}

export function getCategoryById(categoryId: number) {
  return request({
    url: '/category/' + categoryId,
    method: 'get',
  })
}

// 删除类别
export function deleteCategoryApi(id: number) {
  return request.delete<any, any>(`/category/delete/${id}`)
}

// 新增类别
export function addCategoryApi(data: any) {
  return request.post<any, any>('/category/add', data)
}

// 修改类别
export function updateCategoryApi(data: any) {
  return request.put<any, any>('/category/update', data)
}