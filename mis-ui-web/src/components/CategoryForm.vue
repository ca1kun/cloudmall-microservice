<template>
  <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
    <el-form-item label="上级ID" prop="parentId">
      <!-- 简单点先用数字输入，以后可以改成树形选择 -->
      <el-input-number v-model="form.parentId" :min="0" />
      <span style="margin-left: 10px; font-size: 12px; color: #999;">0 表示顶级分类</span>
    </el-form-item>
    <el-form-item label="类别名称" prop="categoryName">
      <el-input v-model="form.categoryName" placeholder="请输入类别名称" />
    </el-form-item>
    
    <div style="text-align: right; margin-top: 20px;">
      <el-button @click="cancel">取消</el-button>
      <el-button type="primary" @click="submit" :loading="loading">确定</el-button>
    </div>
  </el-form>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { addCategoryApi, updateCategoryApi } from '@/api/item/category'
import { ElMessage } from 'element-plus'

// 接收父组件传来的 ID (如果是修改模式)
const props = defineProps<{
  categoryId: number
}>()

// 定义事件：通知父组件关闭弹窗、刷新列表
const emit = defineEmits(['close', 'success'])

const formRef = ref()
const loading = ref(false)

const form = reactive({
  categoryId: undefined, // 修改时用到
  parentId: 0,
  categoryName: ''
})

const rules = {
  categoryName: [{ required: true, message: '请输入类别名称', trigger: 'blur' }]
}

// 监听 ID 变化，如果是修改模式，回显数据
// (这里简化，假设修改时父组件会传入 row 数据，或者组件内根据 ID 查详情)
// 为了简单，我们只做新增，修改的逻辑你可以仿照这个写
watch(() => props.categoryId, (newId) => {
  if (newId) {
    // 模拟回显：实际应该调用 getCategoryByIdApi(newId)
    // form.categoryId = newId
    // ...
  } else {
    // 重置表单
    form.categoryId = undefined
    form.parentId = 0
    form.categoryName = ''
  }
}, { immediate: true })

const cancel = () => {
  emit('close')
}

const submit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      loading.value = true
      try {
        let res
        if (form.categoryId) {
          res = await updateCategoryApi(form)
        } else {
          res = await addCategoryApi(form)
        }
        
        if (res.code === 200) {
          ElMessage.success(form.categoryId ? '修改成功' : '新增成功')
          emit('success') // 通知父组件刷新
          emit('close')   // 关闭弹窗
        } else {
          ElMessage.error(res.message || '操作失败')
        }
      } finally {
        loading.value = false
      }
    }
  })
}
</script>