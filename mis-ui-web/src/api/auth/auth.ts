import request from '@/utils/request'

// 发送验证码
export function sendCodeApi(phone: string) {
  return request({
    url: '/auth/code',
    method: 'post',
    data: { phone }
  })
}

// 登录 (参数改为 LoginDTO)
export interface LoginDTO {
  loginType: 'sms' | 'account'
  username?: string
  password?: string
  phone?: string
  code?: string
}

export function loginApi(data: LoginDTO) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

/**
 * 退出登录
 */
export function logoutApi() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}