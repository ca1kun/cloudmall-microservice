export const mallRoutes = [
  {
    path: 'home', // 最终路径： /mall/home
    name: 'MallHome',
    component: () => import('@/views/mall/home/index.vue'),
    meta: { title: '商城首页' },
  },
  {
    // 👇 修改这里：去掉 /mall/，直接写 'profile'
    // 父级是 /mall，子级是 profile -> 最终拼接为 /mall/profile
    path: 'profile',
    name: 'MallProfile',
    component: () => import('@/views/profile/index.vue'),
    meta: { title: '个人中心' },
  },
  {
    path: 'cart',
    name: 'MallCart',
    component: () => import('@/views/mall/cart/index.vue'),
    meta: { title: '购物车' }
  },
  {
    path: 'checkout',
    name: 'MallCheckout',
    component: () => import('@/views/mall/order/checkout.vue'),
    meta: { title: '确认订单' }
  },
  {
    path: 'pay/confirm',
    name: 'PayConfirm',
    component: () => import('@/views/mall/pay/confirm.vue'),
    meta: { title: '收银台' }
  },
  // 支付成功的回调页 (支付宝跳回来的页面)
  {
    path: 'pay/success',
    name: 'PaySuccess',
    component: () => import('@/views/mall/pay/success.vue'),
    meta: { title: '支付成功' }
  },
    {
    path: 'coupon', // 对应 URL: /mall/coupon
    name: 'MallCoupon',
    component: () => import('@/views/mall/coupon/index.vue'),
    meta: { title: '领券中心' }
  }
]
