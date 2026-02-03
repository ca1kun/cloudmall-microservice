<template>
  <div class="app-container">
    <!-- 表格展示 -->
    <el-table v-loading="loading" :data="saleList" row-key="saleId">
      <el-table-column label="订单ID" prop="saleId" align="center" width="80" />
      <el-table-column label="订单号" prop="saleNo" align="center" />
      <el-table-column label="订单总额" prop="total" align="center">
        <template #default="scope">
          <span>¥ {{ (scope.row.total || 0).toFixed(2) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="订单状态" prop="status" align="center">
        <template #default="scope">
          <el-tag :type="statusTagType(scope.row.status)">{{ formatStatus(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="支付方式" prop="payment.payMethod" align="center">
        <template #default="scope">
          <span>{{ scope.row.payment?.payMethod || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="支付流水号" prop="payment.paymentNo" align="center" width="200">
        <template #default="scope">
          <span>{{ scope.row.payment?.paymentNo || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="220">
        <template #default="scope">
          <el-button link type="primary" @click="handleView(scope.row)">查看订单</el-button>
          <el-button v-if="scope.row.status === 'reserved'" link type="success" @click="handlePay(scope.row)">
            支付订单
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <el-pagination v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize"
      :page-sizes="[10, 20, 50, 100]" :total="total" layout="total, sizes, prev, pager, next, jumper"
      @size-change="getList" @current-change="getList" />

    <!-- 查看订单详情 - 抽屉 -->
    <el-drawer v-model="drawerVisible" title="订单详情" direction="rtl" size="50%">
      <div class="drawer-content" v-if="currentSale">
        <h3>订单信息</h3>
        <p><strong>订单号:</strong> {{ currentSale.saleNo }}</p>
        <p><strong>订单总额:</strong> ¥ {{ (currentSale.total || 0).toFixed(2) }}</p>
        <p><strong>状态:</strong> {{ formatStatus(currentSale.status) }}</p>
        <el-divider />
        <h3>商品明细</h3>
        <el-table :data="currentSaleItems" border>
          <el-table-column property="productName" label="商品名称" />
          <el-table-column property="price" label="单价" />
          <el-table-column property="quantity" label="数量" />
        </el-table>
      </div>
    </el-drawer>

    <!-- 支付 - 对话框 -->
    <el-dialog v-model="paymentDialogVisible" title="订单支付" width="30%">
      <el-form :model="paymentForm" label-width="100px">
        <el-form-item label="订单总额">
          <span>¥ {{ paymentForm.amount.toFixed(2) }}</span>
        </el-form-item>
        <el-form-item label="支付方式" prop="payMethod">
          <el-select v-model="paymentForm.payMethod" placeholder="请选择支付方式">
            <el-option label="现金" value="CASH" />
            <el-option label="支付宝" value="ALIPAY" />
            <el-option label="微信支付" value="WECHAT_PAY" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="paymentDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPayment">确认支付</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive, onActivated } from 'vue'
import { listSaleByPage, listSaleItemById, addPayment } from '@/api/system/sale'
import type { SaleDTO, SaleQueryParams, PaymentForm } from '@/types/index'
import type { SaleItem } from '@/types/types'
import { ElMessage } from 'element-plus'

// --- 响应式状态定义 ---
const loading = ref(true)
const saleList = ref<SaleDTO[]>([])
const total = ref(0)
const queryParams = reactive<SaleQueryParams>({
  pageNum: 1,
  pageSize: 20,
})

const drawerVisible = ref(false)
const paymentDialogVisible = ref(false)
const currentSale = ref<SaleDTO | null>(null)
const currentSaleItems = ref<SaleItem[]>([])

const paymentForm = reactive<PaymentForm>({
  saleId: 0,
  payMethod: 'CASH',
  amount: 0.00,
})

// --- 业务方法 ---

// 获取订单列表
const getList = async () => {
  loading.value = true
  try {
    const response = await listSaleByPage(queryParams)
    saleList.value = response.data.list
    total.value = response.data.total
  } finally {
    loading.value = false
  }
}

// 查看订单详情
const handleView = async (row: SaleDTO) => {
  currentSale.value = row
  const response = await listSaleItemById(row.saleId)
  currentSaleItems.value = response.data
  drawerVisible.value = true
}

// 打开支付对话框
const handlePay = (row: SaleDTO) => {
  paymentForm.saleId = row.saleId
  paymentForm.amount = row.total
  paymentForm.payMethod = 'CASH' // 默认值
  paymentDialogVisible.value = true
}

// 提交支付
const submitPayment = async () => {
  try {
    await addPayment(paymentForm)
    ElMessage.success('支付成功！')
    paymentDialogVisible.value = false
    await getList() // 刷新列表
  } catch (error) {
    console.error('支付失败:', error)
    ElMessage.error('支付操作失败')
  }
}

// --- 辅助方法 ---
const formatStatus = (status: string): string => {
  const statusMap: Record<string, string> = {
    completed: '已完成',
    delivered: '已发货',
    paid: '已支付',
    reserved: '已预定',
    cancelled: '已取消',
  }
  return statusMap[status] || '未知'
}

const statusTagType = (status: string) => {
  if (status === 'paid' || status === 'completed' || status === 'delivered') return 'success'
  if (status === 'reserved') return 'warning'
  if (status === 'cancelled') return 'danger'
  return 'info'
}


// --- 生命周期钩子 ---
onMounted(() => {
  getList()
})

onActivated(() => {
  console.log('支付页面被激活，重新获取数据！')
  getList()
})
</script>

<style scoped>
.drawer-content {
  padding: 20px;
}
</style>