import axios, {
  AxiosError,
  type AxiosInstance,
  type AxiosResponse,
  type InternalAxiosRequestConfig,
} from 'axios'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import errorCode from '@/utils/errorCode'
import router from '@/router'

axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8'
// 创建axios实例
const service: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

// 请求拦截器
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 获取 Token
    const userStore = useUserStore()
    if (userStore.token) {
      // 这里的 header key 要跟后端 Filter 里取的一致，后端写的是 request.getHeader("token")
      config.headers['Authorization'] = 'Bearer ' + userStore.token
    }
    return config
  },
  (error: AxiosError) => {
    console.log(error)
    Promise.reject(error)
  },
)

// 响应拦截器
service.interceptors.response.use(
  (res: AxiosResponse) => {
    const code = res.data.code || 200
    // 处理后端业务逻辑定义的 401
    if (code === 401) {
      ElMessage.error('登录状态已过期，请重新登录')
      localStorage.clear() // 清除过期的 token
      router.push('/login') // 👈 强制跳转
      return Promise.reject(new Error('登录过期'))
    }
    const msg = res.data.message || res.data.msg || errorCode[code] || '未知错误'
    if (code === 200) {
      return Promise.resolve(res.data)
    } else {
      ElMessage({ message: msg, type: 'error' })
      return Promise.reject(new Error(msg))
    }
  },
  (error: AxiosError) => {
    console.log('err' + error)
    let { message } = error
    // 处理 HTTP 状态码为 401 的情况
    if (error.response && error.response.status === 401) {
      ElMessage.error('认证失败，请重新登录')
      localStorage.clear()
      router.push('/login') // 👈 强制跳转
    }
    if (message == 'Network Error') {
      message = '后端接口连接异常'
    } else if (message.includes('timeout')) {
      message = '系统接口请求超时'
    } else if (message.includes('Request failed with status code')) {
      message = '系统接口' + message.slice(message.length - 3) + '异常'
    }
    ElMessage({ message: message, type: 'error', duration: 5 * 1000 })
    return Promise.reject(error)
  },
)

export default service

// src/utils/request.ts

// 1. 定义后端返回的标准格式
export interface ApiResult<T = any> {
  code: number
  message: string // 或者 msg，根据你后端 ApiResult 的字段名来
  data: T
}

// ... 你的 axios 创建代码 ...

