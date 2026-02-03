export const adminRouters = [  
  {
    path: '/home',
    name: 'home',
    component: () => import('@/views/admin/home/index.vue'),
    meta: {
      icon: 'HomeFilled',
      title: '首页',
    },
  },
  {
    path: '/profile', // 访问 /profile
    name: 'AdminProfile',
    component: () => import('@/views/profile/index.vue'),
    meta: { title: '个人中心', icon: 'User' },
  },
  {
    path: '/item',
    name: 'item',
    redirect: '/item/category',
    meta: {
      icon: 'Grid',
      title: '商品管理',
    },
    children: [
      {
        path: '/item/category',
        name: 'category',
        meta: {
          icon: 'Folder',
          title: '类别',
        },
        component: () => import('@/views/admin/item/category/index.vue'),
      },
      {
        path: '/item/product',
        name: 'product',
        meta: {
          icon: 'Goods',
          title: '商品',
        },
        component: () => import('@/views/admin/item/product/index.vue'),
      },
    ],
  },
  {
    path: '/pos',
    name: 'pos',
    redirect: '/pos/sale',
    meta: {
      icon: 'Shop',
      title: '销售管理',
    },
    children: [
      {
        path: '/pos/sale',
        name: 'sale',
        meta: {
          icon: 'Sell',
          title: '销售',
        },
        component: () => import('@/views/admin/pos/sale/index.vue'),
      },
      {
        path: '/pos/payment',
        name: 'payment',
        meta: {
          icon: 'Wallet',
          title: '支付',
        },
        component: () => import('@/views/admin/pos/payment/index.vue'),
      },
    ],
  },
  {
    path: '/marketing',
    name: 'Marketing',
    meta: { title: '营销管理', icon: 'Present' },
    children: [
      {
        path: '/marketing/coupon',
        name: 'CouponManage',
        component: () => import('@/views/admin/marketing/coupon/index.vue'),
        meta: { title: '优惠券管理', icon: 'Ticket' }
      }
    ]
  },
  {
    path: '/about',
    name: 'about',
    meta: {
      icon: 'InfoFilled',
      title: '关于',
    },
    component: () => import('@/views/admin/about/index.vue'),
  },
]
