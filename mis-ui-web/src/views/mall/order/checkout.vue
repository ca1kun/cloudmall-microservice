<template>
  <div class="checkout-container">
    <el-card header="填写并核对订单信息">

      <!-- 1. 收货地址 (模拟) -->
      <div class="section">
        <h3>📍 收货人信息</h3>
        <el-radio-group v-model="orderForm.addressId">
          <el-radio :label="1" border>
            张三 13800138000 <br /> 广东省广州市天河区华南农业大学
          </el-radio>
          <el-radio :label="2" border>
            李四 13900139000 <br /> 广东省广州市天河区五山路483号
          </el-radio>
        </el-radio-group>
      </div>

      <!-- 2. 商品清单 -->
      <div class="section">
        <h3>📦 商品清单</h3>
        <el-table :data="cartList" border>
          <el-table-column prop="productName" label="商品名称" />
          <el-table-column prop="price" label="单价" width="120" />
          <el-table-column prop="quantity" label="数量" width="120" />
          <el-table-column label="小计" width="120">
            <template #default="{ row }">
              {{ (row.price * row.quantity).toFixed(2) }}
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 3. 优惠券选择区域 -->
      <div class="section">
        <h3>🎫 优惠券</h3>
        <el-select v-model="orderForm.couponId" placeholder="请选择优惠券" clearable style="width: 300px"
          @change="handleCouponChange">
          <!-- 这里使用 couponId 作为 value -->
          <el-option v-for="item in couponList" :key="item.id" :label="getLabel(item)" :value="item.couponId"
            :disabled="!checkCouponAvailable(item)" />
        </el-select>

        <div v-if="orderForm.couponId" style="margin-top: 10px; color: #f56c6c; font-size: 14px;">
          已抵扣: ¥ {{ currentCouponAmount }}
        </div>
      </div>

      <!-- 4. 备注 -->
      <div class="section">
        <h3>📝 备注信息</h3>
        <el-input v-model="orderForm.note" type="textarea" placeholder="选填：请填写备注信息" />
      </div>

      <!-- 5. 底部提交 -->
      <div class="footer-bar">
        <div class="total-wrapper">
          <span class="label">商品总额:</span>
          <span class="value">¥ {{ goodsTotalPrice.toFixed(2) }}</span>

          <span class="label" style="margin-left: 20px;" v-if="currentCouponAmount > 0">优惠:</span>
          <span class="value" style="color: #67C23A;" v-if="currentCouponAmount > 0">- ¥ {{ currentCouponAmount
            }}</span>

          <span class="label" style="margin-left: 20px;">实付金额:</span>
          <span class="real-price">¥ {{ finalPrice }}</span>
        </div>

        <el-button type="primary" size="large" :loading="submitting" @click="submitOrder">
          提交订单
        </el-button>
      </div>

    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCartListApi } from '@/api/mall/cart'
import { createOrderApi } from '@/api/mall/order'
import { getMyCouponIdsApi } from '@/api/mall/coupon' // ✅ 修正：引入获取列表的接口
import { ElMessage } from 'element-plus'

const router = useRouter()
const cartList = ref<any[]>([])
const submitting = ref(false)
const couponList = ref<any[]>([])

const orderForm = reactive({
  addressId: 1,
  payType: 1,
  note: '',
  couponId: null as number | null
})

// 计算商品总价
const goodsTotalPrice = computed(() => {
  return cartList.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
})

// 计算当前优惠金额
const currentCouponAmount = computed(() => {
  if (!orderForm.couponId) return 0
  // 注意：这里 find 依据的是 couponId
  const coupon = couponList.value.find(c => c.couponId === orderForm.couponId)
  return coupon ? coupon.amount : 0
})

// 计算最终实付
const finalPrice = computed(() => {
  let final = goodsTotalPrice.value - currentCouponAmount.value
  return final > 0 ? final.toFixed(2) : '0.01'
})

// 加载购物车 (原 init 改名 fetchCartData)
const fetchCartData = async () => {
  const res = await getCartListApi()
  if (res.code === 200) {
    cartList.value = res.data || []
    if (cartList.value.length === 0) {
      ElMessage.warning('购物车为空，无法下单')
      router.push('/mall/cart')
    }
  }
}

// 加载优惠券
const fetchCoupons = async () => {
  try {
    // ✅ 修正：调用列表接口
    const res = await getMyCouponIdsApi()
    if (res.code === 200) {
      // 过滤出未使用的券 (useStatus === 0)
      couponList.value = (res.data || []).filter((item: any) => item.useStatus === 0)
    }
  } catch (e) {
    // 忽略错误，不影响下单
  }
}

// 提交订单
const submitOrder = async () => {
  submitting.value = true
  try {
    const res = await createOrderApi(orderForm)
    if (res.code === 200) {
      ElMessage.success('订单提交成功！')

      // 跳转收银台，带上订单ID和金额
      router.push({
        path: '/mall/pay/confirm',
        query: {
          orderId: res.data.orderId,
          money: res.data.payAmount || finalPrice.value
        }
      })
    } else {
      ElMessage.error(res.message || '下单失败')
    }
  } finally {
    submitting.value = false
  }
}

// 辅助函数
const checkCouponAvailable = (item: any) => {
  return goodsTotalPrice.value >= (item.minPoint || 0)
}

const getLabel = (item: any) => {
  let label = `${item.name} (省${item.amount})`
  label += item.minPoint > 0 ? ` [满${item.minPoint}可用]` : ` [无门槛]`
  if (!checkCouponAvailable(item)) {
    label += ' (未满足门槛)'
  }
  return label
}

const handleCouponChange = () => {
  // 可以在这里加个校验，如果选了不可用的券自动清除
  if (orderForm.couponId) {
    const coupon = couponList.value.find(c => c.couponId === orderForm.couponId)
    if (coupon && !checkCouponAvailable(coupon)) {
      ElMessage.warning('当前金额未满足该优惠券门槛')
      orderForm.couponId = null
    }
  }
}

onMounted(() => {
  fetchCartData() // 1. 查购物车
  fetchCoupons()  // 2. 查优惠券
})
</script>

<style scoped>
.checkout-container {
  max-width: 1000px;
  margin: 20px auto;
}

.section {
  margin-bottom: 30px;
}

h3 {
  margin-bottom: 15px;
  font-size: 16px;
  color: #333;
}

.footer-bar {
  text-align: right;
  margin-top: 40px;
  border-top: 1px solid #eee;
  padding-top: 20px;
}

.real-price {
  color: #f56c6c;
  font-size: 28px;
  font-weight: bold;
  margin-left: 10px;
}

.total-wrapper .value {
  font-weight: bold;
  margin-right: 5px;
}
</style>