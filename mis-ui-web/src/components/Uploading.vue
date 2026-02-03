<template>
  <el-upload
    class="avatar-uploader"
    :action="uploadUrl"
    :show-file-list="false"
    :headers="headers"
    :on-success="handleSuccess"
    :before-upload="beforeUpload"
  >
    <img v-if="modelValue" :src="modelValue" class="avatar" />
    <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
  </el-upload>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

// 接收父组件传来的 v-model (图片地址)
const props = defineProps({
  modelValue: String
})
const emit = defineEmits(['update:modelValue'])

const userStore = useUserStore()

// 1. 上传接口地址 (通过 Vite 代理)
const uploadUrl = '/api/common/upload'

// 2. 设置请求头 (必须带 Token，否则后端过滤器会拦截 401)
const headers = computed(() => {
  return {
    Authorization: 'Bearer ' + userStore.token
  }
})

// 3. 上传成功回调
const handleSuccess = (res: any) => {
  if (res.code === 200) {
    // res.data 就是后端返回的阿里云图片 URL
    emit('update:modelValue', res.data) 
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(res.message || '上传失败')
  }
}

// 4. 上传前校验
const beforeUpload = (file: any) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('只能上传 JPG/PNG 文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}
</script>

<style scoped>
.avatar-uploader .avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style>

<style>
/* Element Plus 官方样式补丁 */
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}
.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
}
</style>