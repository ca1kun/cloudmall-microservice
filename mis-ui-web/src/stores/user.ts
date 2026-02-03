import { defineStore } from 'pinia'
import { ref } from 'vue'
// 👇 1. 修改引入的类型，从 LoginData 改为 LoginDTO
// 请确保路径和你 api 文件夹里的文件名一致，通常是 '@/api/auth' 或 '@/api/auth/index'
import { loginApi,logoutApi, type LoginDTO } from '@/api/auth/auth' 
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  // 从本地缓存初始化数据
  const token = ref(localStorage.getItem('token') || '')
  const role = ref(localStorage.getItem('role') || '')
  const username = ref(localStorage.getItem('username') || '')
  const avatar = ref(localStorage.getItem('avatar') || '')

  // 登录动作 (Action)
  // 👇 2. 参数类型改为 LoginDTO
  const login = async (loginForm: LoginDTO) => {
    try {
      // 调用真实接口
      // res 是后端返回的完整对象: { code: 200, msg: "...", data: { token: "...", role: "..." } }
      const res = await loginApi(loginForm)
      
      // 👇 3. 关键点：根据你的 Axios 拦截器逻辑提取数据
      // 如果你的 request.ts 拦截器里写的是 return Promise.resolve(res.data)
      // 那么这里的 res 就是 ApiResult 对象，真实的数据在 res.data 里
      const data = res.data 

      // 赋值到 Pinia
      token.value = data.token
      role.value = data.role
      username.value = data.username
       avatar.value = data.avatar || '' // 防止 null
      localStorage.setItem('avatar', data.avatar || '')

      // 持久化存储
      localStorage.setItem('token', data.token)
      localStorage.setItem('role', data.role)
      localStorage.setItem('username', data.username)

      return data
    } catch (error) {
      throw error
    }
  }

 // 修改后的登出动作
  const logout = async () => {
    try {
      // 1. 尝试通知后端清除 Redis
      // 使用 await 等待后端处理，但包裹在 try-catch 中
      // 即使后端报错（比如 Token 已经过期了），前端也要强制登出
      await logoutApi()
    } catch (error) {
      console.warn('后端登出失败或Token已失效，强制本地登出')
    } finally {
      // 2. 无论后端是否成功，前端都要清空状态
      token.value = ''
      role.value = ''
      username.value = ''
      avatar.value = ''
      localStorage.clear() // 清空所有本地存储
      
      // 3. 跳转回登录页
      router.push('/login')
      
      // 可选：弹个窗
      // ElMessage.success('已安全退出') 
    }
  }

  return { 
    token, 
    role, 
    username,
    avatar, 
    login, 
    logout 
  }
})