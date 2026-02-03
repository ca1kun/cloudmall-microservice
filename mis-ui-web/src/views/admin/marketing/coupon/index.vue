<template>
  <div class="app-container">
    <el-card>
      <!-- 顶部操作栏 -->
      <div class="header-actions">
        <el-button type="primary" icon="Plus" @click="handleAdd">新增优惠券</el-button>
        <el-button icon="Refresh" @click="fetchData">刷新</el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" border style="width: 100%; margin-top: 20px;" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="amount" label="面额" width="100">
          <template #default="{ row }">¥{{ row.amount }}</template>
        </el-table-column>
        <el-table-column prop="minPoint" label="门槛" width="120">
          <template #default="{ row }">满 {{ row.minPoint }}</template>
        </el-table-column>
        <el-table-column prop="count" label="库存" width="100" />
        <el-table-column label="有效期" width="320">
          <template #default="{ row }">
            {{ formatTime(row.startTime) }} ~ {{ formatTime(row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <!-- 预热按钮 -->
            <el-button 
              type="warning" 
              size="small" 
              icon="Lightning" 
              @click="handlePreheat(row)"
            >
              秒杀预热
            </el-button>
            <el-button type="danger" size="small" link>删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增弹窗 -->
    <el-dialog v-model="dialogVisible" title="新增优惠券" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="优惠券名称" prop="name">
          <el-input v-model="form.name" placeholder="例如：满100减10" />
        </el-form-item>
        <el-form-item label="面额" prop="amount">
          <el-input-number v-model="form.amount" :min="1" :precision="2" />
        </el-form-item>
        <el-form-item label="使用门槛" prop="minPoint">
          <el-input-number v-model="form.minPoint" :min="0" :precision="2" />
          <span style="margin-left: 10px; font-size: 12px; color: #999;">0表示无门槛</span>
        </el-form-item>
        <el-form-item label="发行数量" prop="count">
          <el-input-number v-model="form.count" :min="1" :step="10" />
        </el-form-item>
        <el-form-item label="每人限领" prop="perLimit">
          <el-input-number v-model="form.perLimit" :min="1" />
        </el-form-item>
        <el-form-item label="有效期" prop="timeRange">
          <el-date-picker
            v-model="form.timeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getCouponPageApi, addCouponApi, preheatCouponApi } from '@/api/marketing/coupon'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh, Lightning } from '@element-plus/icons-vue'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const formRef = ref()

const form = reactive({
  name: '',
  amount: 10,
  minPoint: 0,
  count: 100,
  perLimit: 1,
  timeRange: [] as string[]
})

const rules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  amount: [{ required: true, message: '请输入面额', trigger: 'blur' }],
  count: [{ required: true, message: '请输入数量', trigger: 'blur' }],
  timeRange: [{ required: true, message: '请选择时间范围', trigger: 'change' }]
}

// 查询列表
const fetchData = async () => {
  loading.value = true
  const res = await getCouponPageApi({})
  if (res.code === 200) {
    tableData.value = res.data.records || res.data
  }
  loading.value = false
}

// 打开新增
const handleAdd = () => {
  dialogVisible.value = true
  // 重置表单逻辑...
}

// 提交新增
const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      // 拆分时间
      const postData = {
        ...form,
        startTime: form.timeRange?.[0] || '',
        endTime: form.timeRange?.[1] || ''
      }
      const res = await addCouponApi(postData)
      if (res.code === 200) {
        ElMessage.success('添加成功')
        dialogVisible.value = false
        fetchData()
      }
    }
  })
}

// 预热操作
const handlePreheat = (row: any) => {
  ElMessageBox.confirm(
    `确定要将【${row.name}】的库存加载到 Redis 吗？\n注意：这会重置秒杀状态！`,
    '预热确认',
    { type: 'warning' }
  ).then(async () => {
    const res = await preheatCouponApi(row.id)
    if (res.code === 200) {
      ElMessage.success('预热成功！可以开始秒杀了')
    }
  })
}

const formatTime = (time: string) => {
  return time ? time.replace('T', ' ') : ''
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.header-actions {
  display: flex;
  gap: 10px;
}
</style>