<template>
    <div class="app-container">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>个人中心</span>
                </div>
            </template>

            <el-row :gutter="20">
                <!-- 左侧：个人信息展示卡片 -->
                <el-col :span="8" :xs="24">
                    <div class="user-info text-center">
                        <!-- 头像展示 -->
                        <el-avatar :size="100" :src="userInfo.avatar || defaultAvatar" />
                        <h3>{{ userInfo.username }}</h3>
                        <p>{{ userInfo.role === 'ADMIN' ? '管理员' : '普通用户' }}</p>
                        <div class="user-desc">
                            <p><el-icon>
                                    <Iphone />
                                </el-icon> {{ userInfo.phone || '暂未绑定手机' }}</p>
                            <p><el-icon>
                                    <Clock />
                                </el-icon> 注册时间: {{ userInfo.createTime }}</p>
                        </div>
                    </div>
                </el-col>

                <!-- 右侧：修改表单 -->
                <el-col :span="16" :xs="24">
                    <el-tabs v-model="activeTab">

                        <!-- Tab 1: 修改资料 -->
                        <el-tab-pane label="基本资料" name="info">
                            <!-- 👇 1. 绑定 ref="infoFormRef" 和 :rules="infoRules" -->
                            <el-form ref="infoFormRef" :model="infoForm" :rules="infoRules" label-width="80px"
                                style="margin-top: 20px;">
                                <el-form-item label="用户头像">
                                    <upload-img v-model="infoForm.avatar" />
                                </el-form-item>

                                <!-- 👇 2. 必须加 prop="phone"，校验规则才会生效 -->
                                <el-form-item label="手机号码" prop="phone">
                                    <el-input v-model="infoForm.phone" maxlength="11" placeholder="请输入11位手机号" />
                                </el-form-item>

                                <el-form-item>
                                    <el-button type="primary" @click="handleUpdateInfo">保存配置</el-button>
                                </el-form-item>
                            </el-form>
                        </el-tab-pane>
                        <!-- Tab 2: 修改密码 -->
                        <el-tab-pane label="修改密码" name="password">
                            <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="80px"
                                style="margin-top: 20px;">
                                <el-form-item label="旧密码" prop="oldPassword">
                                    <el-input v-model="pwdForm.oldPassword" type="password" show-password />
                                </el-form-item>
                                <el-form-item label="新密码" prop="newPassword">
                                    <el-input v-model="pwdForm.newPassword" type="password" show-password />
                                </el-form-item>
                                <el-form-item label="确认密码" prop="confirmPassword">
                                    <el-input v-model="pwdForm.confirmPassword" type="password" show-password />
                                </el-form-item>
                                <el-form-item>
                                    <el-button type="primary" @click="handleUpdatePwd">保存密码</el-button>
                                </el-form-item>
                            </el-form>
                        </el-tab-pane>

                    </el-tabs>
                </el-col>
            </el-row>
        </el-card>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getUserProfile, updateUserProfile, updateUserPwd } from '@/api/system/profile'
import UploadImg from '@/components/Uploading.vue'
import { ElMessage } from 'element-plus'
import { Iphone, Clock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const activeTab = ref('info')
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 用户完整信息
const userInfo = ref<any>({})

// 表单数据
const infoForm = reactive({
    avatar: '',
    phone: ''
})

const pwdForm = reactive({
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
})

const pwdFormRef = ref()
// --- 新增：表单 Ref ---
const infoFormRef = ref()

// --- 新增：基本资料校验规则 ---
const infoRules = {
    phone: [
        { required: true, message: '手机号不能为空', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的11位手机号码', trigger: 'blur' }
    ]
}

// 密码校验规则
const validatePass2 = (rule: any, value: any, callback: any) => {
    if (value !== pwdForm.newPassword) {
        callback(new Error('两次输入密码不一致!'))
    } else {
        callback()
    }
}

const pwdRules = {
    oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
    newPassword: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
        { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
    ],
    confirmPassword: [
        { required: true, message: '请再次输入密码', trigger: 'blur' },
        { validator: validatePass2, trigger: 'blur' }
    ]
}

// ...existing code...
const initData = async () => {
    // 1. 调用接口
    const response = await getUserProfile()
    
    // 2. 判断业务状态码 (现在不会报红了)
    if (response.code === 200) {
        // 3. 获取真实数据
        // response 是 { code: 200, msg: '...', data: { username: '...', avatar: '...' } }
        const userObj = response.data 
        
        // 4. 赋值给页面展示
        userInfo.value = userObj
        
        // 5. 赋值给表单回显
        // ✅ 正确写法：直接从 userObj 取值
        infoForm.avatar = userObj.avatar
        infoForm.phone = userObj.phone
    }
}
// ...existing code...
// --- 修改：资料保存逻辑 ---
const handleUpdateInfo = async () => {
    // 1. 校验表单
    if (!infoFormRef.value) return

    await infoFormRef.value.validate(async (valid: boolean) => {
        if (valid) {
            // 2. 校验通过，发送请求
            try {
                const res = await updateUserProfile(infoForm)
                if (res.code === 200) {
                    // 3. 成功提示
                    ElMessage.success('资料保存成功！')

                    // 更新页面数据和缓存
                    userInfo.value.avatar = infoForm.avatar
                    userInfo.value.phone = infoForm.phone
                    userStore.avatar = infoForm.avatar
                    localStorage.setItem('avatar', infoForm.avatar)
                } else {
                    ElMessage.error(res.message || '保存失败')
                }
            } catch (error) {
                // 网络或其他错误已经在 request.ts 拦截器处理了，这里可以不写，或者写个 log
                console.error(error)
            }
        } else {
            // 校验不通过
            ElMessage.warning('请检查输入格式是否正确')
            return false
        }
    })
}
// ...existing code...
const handleUpdatePwd = async () => {
    if (!pwdFormRef.value) return
    await pwdFormRef.value.validate(async (valid: boolean) => {
        if (valid) {
            // 1. 发起请求
            const res = await updateUserPwd(pwdForm)
            
            // 👇 调试大法：先打印看看结构，你就明白了
            console.log('修改密码接口返回:', res)

            // 2. 修改判定逻辑
            // ❌ 错误：if (res?.data?.code === 200) 
            // ✅ 正确：直接取 code
            if (res.code === 200) {
                ElMessage.success('密码修改成功，请重新登录')
                // 3. 强制登出
                userStore.logout()
            } else {
                // 可选：处理非 200 但没被拦截器拦截的情况
                ElMessage.error(res.message || '修改失败')
            }
        }
    })
}
// ...existing code...

onMounted(() => {
    initData()
})
</script>

<style scoped>
.app-container {
    padding: 20px;
}

.text-center {
    text-align: center;
}

.user-info {
    padding: 20px;
}

.user-desc {
    margin-top: 20px;
    text-align: left;
    color: #666;
    font-size: 14px;
}

.user-desc p {
    margin-bottom: 10px;
    display: flex;
    align-items: center;
    gap: 8px;
}
</style>