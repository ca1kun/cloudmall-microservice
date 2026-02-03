import request from '@/utils/request'
import type { LoginUser } from '@/types/types'

export function login(data: LoginUser) {
  return request({
    url: '/user/login',
    method: 'post',
    data: data,
  })
}

export function info() {
  return request({
    url: '/user/info',
    method: 'get',
  })
}
