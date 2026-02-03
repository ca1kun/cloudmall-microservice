import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getCartListApi } from '@/api/mall/cart'

export const useCartStore = defineStore('cart', () => {
  // 购物车商品数量
  const count = ref(0)

  // 动作：更新购物车数量
  const updateCount = async () => {
    try {
      const res = await getCartListApi()
      if (res.code === 200) {
        // 如果后端返回的是数组，取长度
        // 如果后端返回的是 null，取 0
        count.value = res.data ? res.data.length : 0
      }
    } catch (error) {
      console.error('获取购物车数量失败', error)
    }
  }

  // 动作：重置（登出时用）
  const reset = () => {
    count.value = 0
  }

  return { count, updateCount, reset }
})