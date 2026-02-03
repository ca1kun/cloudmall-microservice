import Mock from 'mockjs'

/** 用户信息接口 */
interface UserInfo {
  avatar: string
  userName: string
  password: string
  desc: string
  roles: string[]
  token: string
}

/** 登录请求体接口 */
export interface LoginBody {
  userName: string
  password: string
}

/** Mock 请求接口 */
interface MockRequest {
  body: LoginBody
  headers: {
    token?: string
  }
}

/** 静态指定mock数据 */
function createStaticUserList(): UserInfo[] {
  return [
    {
      avatar: 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif',
      userName: 'admin',
      password: '123',
      desc: '系统管理员',
      roles: ['admin'],
      token: 'admin_token',
    },
    {
      avatar: 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif',
      userName: 'user',
      password: '123456',
      desc: '普通用户',
      roles: ['common'],
      token: 'user_token',
    },
  ]
}

/** 配置Mock接口 */
/** 注意与api/user.ts路径接口名保持一致*/
export default [
  {
    url: '/api/user/login',
    method: 'post',
    response: ({ body }: { body: LoginBody }) => {
      const { userName, password } = body
      const user = createStaticUserList().find(
        (item) => item.userName === userName && item.password === password,
      )
      if (!user) {
        return { code: 401, msg: '认证失败，无法访问系统资源', data: {} }
      }
      const { token } = user
      return { code: 200, msg: '用户登录成功', data: { token, userName } }
    },
  },

  {
    url: '/api/user/info',
    method: 'get',
    response: (request: MockRequest) => {
      const token = request.headers.token
      const user = createStaticUserList().find((item) => item.token === token)
      if (!user) {
        return { code: 201, msg: '查询用户信息失败', data: {} }
      }
      return { code: 200, msg: '查询用户成功', data: { user } }
    },
  },
]