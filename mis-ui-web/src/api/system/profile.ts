import request from '@/utils/request'
import type { ApiResult } from '@/utils/request' 

// 2. 定义用户信息的结构（可选，写了会有代码提示）
export interface UserInfo {
  username: string
  avatar: string
  phone: string
  createTime: string
  role: string
}

// 3. 修改接口函数
export function getUserProfile() {
  // 核心修复：添加类型断言 as unknown as Promise<ApiResponse<UserInfo>>
  return request({
    url: '/system/user/profile',
    method: 'get'
  }) as unknown as Promise<ApiResult<UserInfo>>
}

// 修改资料
export function updateUserProfile(data: Partial<UserInfo>) {
  return request<any, ApiResult<UserInfo>>({
    url: '/system/user/profile',
    method: 'put',
    data
  })
}

// 修改密码
// 这里的 <any, ApiResult<string>> 意思是：
// 第一个参数是请求数据的类型(不重要，写any)，第二个参数是【响应数据的类型】
export function updateUserPwd(data: any) {
  return request<any, ApiResult<string>>({
    url: '/system/user/profile/password',
    method: 'put',
    data
  })
}