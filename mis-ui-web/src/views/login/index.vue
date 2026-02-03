<template>
  <div class="login-container">
    <div class="login-box">
      <!-- 头部 Logo 或 标题区 -->
      <div class="login-header">
        <div class="logo-circle">
          <el-icon :size="30" color="#fff"><Shop /></el-icon>
        </div>
        <h2 class="title">SCAU 商城系统</h2>
        <p class="subtitle">微服务分布式电商平台</p>
      </div>

      <el-tabs v-model="activeTab" class="login-tabs" stretch>
        <!-- 验证码登录 -->
        <el-tab-pane label="验证码登录" name="sms">
          <el-form ref="smsFormRef" :model="smsForm" :rules="smsRules" class="login-form">
            <el-form-item prop="phone">
              <el-input 
                v-model="smsForm.phone" 
                placeholder="请输入手机号" 
                prefix-icon="Iphone" 
                size="large"
                @keyup.enter="handleLogin" 
              />
            </el-form-item>
            <el-form-item prop="code">
              <div class="code-wrapper">
                <el-input 
                  v-model="smsForm.code" 
                  placeholder="验证码" 
                  prefix-icon="Message" 
                  size="large"
                  @keyup.enter="handleLogin" 
                />
                <el-button 
                  type="primary" 
                  :disabled="isCounting" 
                  class="code-btn"
                  @click="handleSendCode"
                >
                  {{ isCounting ? `${count}s` : '获取验证码' }}
                </el-button>
              </div>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 密码登录 -->
        <el-tab-pane label="密码登录" name="account">
          <el-form ref="accountFormRef" :model="accountForm" :rules="accountRules" class="login-form">
            <el-form-item prop="username">
              <el-input 
                v-model="accountForm.username" 
                placeholder="用户名/手机号" 
                prefix-icon="User"
                size="large"
                @keyup.enter="handleLogin" 
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input 
                v-model="accountForm.password" 
                type="password" 
                placeholder="请输入密码" 
                prefix-icon="Lock" 
                show-password
                size="large"
                @keyup.enter="handleLogin" 
              />
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>

      <el-button 
        type="primary" 
        class="login-btn" 
        size="large"
        :loading="loading" 
        @click="handleLogin"
      >
        {{ activeTab === 'sms' ? '登录 / 自动注册' : '登 录' }}
      </el-button>

      <div class="login-footer">
        <el-divider>
          <span class="footer-text">SCAU MIS v1.0</span>
        </el-divider>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { sendCodeApi } from '@/api/auth/auth'
import { ElMessage } from 'element-plus'
import { User, Lock, Iphone, Message, Shop } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('sms')
const loading = ref(false)

// --- 倒计时逻辑 ---
const isCounting = ref(false)
const count = ref(60)
let timer: any = null

const smsForm = reactive({ phone: '', code: '' })
const accountForm = reactive({ username: '', password: '' })
const smsFormRef = ref()
const accountFormRef = ref()

const smsRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不对', trigger: 'blur' }
  ],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}
const accountRules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleSendCode = async () => {
  smsFormRef.value.validateField('phone', async (valid: boolean) => {
    if (valid) {
      try {
        await sendCodeApi(smsForm.phone)
        ElMessage.success('验证码已发送，请查看控制台')
        isCounting.value = true
        count.value = 60
        timer = setInterval(() => {
          count.value--
          if (count.value <= 0) {
            clearInterval(timer)
            isCounting.value = false
          }
        }, 1000)
      } catch (error) {}
    }
  })
}

const handleLogin = async () => {
  const isSms = activeTab.value === 'sms'
  const formRef = isSms ? smsFormRef.value : accountFormRef.value
  if (!formRef) return

  await formRef.validate(async (valid: boolean) => {
    if (valid) {
      loading.value = true
      try {
        const loginData = {
          loginType: isSms ? ('sms' as const) : ('account' as const),
          ...(isSms ? smsForm : accountForm)
        }
        await userStore.login(loginData)
        ElMessage.success('登录成功')
        if (userStore.role === 'ADMIN') {
          router.push('/home')
        } else {
          router.push('/mall/home')
        }
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  /* 升级为深蓝色渐变，更有科技感 */
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  position: relative;
  overflow: hidden;
}

/* 装饰性的小圆球，增加页面丰富度 */
.login-container::before {
  content: "";
  position: absolute;
  width: 300px; height: 300px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  top: -100px; right: -100px;
}

.login-box {
  width: 420px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  z-index: 10;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.logo-circle {
  width: 60px;
  height: 60px;
  background: #409EFF;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0 auto 15px;
  box-shadow: 0 4px 10px rgba(64, 158, 255, 0.3);
}

.title {
  font-size: 26px;
  color: #303133;
  font-weight: 700;
  margin: 0;
}

.subtitle {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}

.login-tabs {
  margin-bottom: 20px;
}

/* 覆盖 tabs 底部横线样式 */
:deep(.el-tabs__nav-wrap::after) {
  height: 1px;
  background-color: #f0f0f0;
}

.code-wrapper {
  display: flex;
  gap: 12px;
  width: 100%;
}

.code-btn {
  width: 120px;
}

.login-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
  margin-top: 10px;
  letter-spacing: 1px;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
}

.login-footer {
  margin-top: 30px;
}

.footer-text {
  color: #c0c4cc;
  font-size: 12px;
  font-weight: 400;
}

.login-form :deep(.el-input__wrapper) {
  background-color: #f5f7fa;
  box-shadow: none !important;
  border: 1px solid #e4e7ed;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  border-color: #409EFF;
  background-color: #fff;
}
</style>