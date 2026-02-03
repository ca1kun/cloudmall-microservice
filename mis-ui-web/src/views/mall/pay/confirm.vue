<template>
    <div class="pay-container">
        <el-card class="box-card">
            <div class="success-icon">
                <el-icon color="#67C23A" size="60">
                    <CircleCheckFilled />
                </el-icon>
            </div>
            <h2 class="text-center">订单提交成功！</h2>
            <p class="text-center tip">请在 30 分钟内完成支付，否则订单将自动取消</p>

            <div class="info-list">
                <div class="item">
                    <span>订单编号：</span>
                    <span>{{ orderId }}</span>
                </div>
                <div class="item">
                    <span>支付金额：</span>
                    <span class="price">¥ {{ money }}</span>
                </div>
            </div>

            <div class="btn-group">
                <el-button type="primary" size="large" :loading="loading" @click="handleAlipay">
                    支付宝支付
                </el-button>
                <!-- 微信支付暂未实现，可以置灰 -->
                <el-button type="success" size="large" disabled> 微信支付 </el-button>
            </div>
        </el-card>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { alipayApi } from '@/api/mall/pay'
import { CircleCheckFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const orderId = ref('')
const money = ref('')
const loading = ref(false)

onMounted(() => {
    // 从 URL 参数获取订单信息
    // 之前的 checkout.vue 跳转时应该写的是：router.push(`/mall/pay/confirm?orderId=${res.data.orderId}&money=${...}`)
    orderId.value = route.query.orderId as string
    money.value = (route.query.money as string) || '0.00'
})

const handleAlipay = async () => {
    loading.value = true
    try {
        // 1. 调用接口，强制转为 any 类型，不再受 TS 类型报错干扰
        const res = await alipayApi(Number(orderId.value)) as any
        
        console.log('支付接口返回:', res) 

        // 2. 直接取 code，不要加 .data
        if (res.code === 200) {
            const formHtml = res.data // 这里直接取 data，就是那个 HTML 字符串
            
            // 3. 创建容器并提交
            const div = document.createElement('div')
            div.innerHTML = formHtml
            document.body.appendChild(div)
            
            // 4. 提交表单 (兼容性写法)
            const form = div.querySelector('form')
            if (form) {
                form.submit()
            } else {
                ElMessage.error('无法跳转支付：未找到表单')
            }
        } else {
            // 如果走到这里，说明 res.code 不等于 200
            ElMessage.error(res.message || '发起支付失败')
        }
    } catch (error) {
        console.error(error)
        ElMessage.error('系统错误')
    } finally {
        loading.value = false
    }
}
</script>

<style scoped>
.pay-container {
    display: flex;
    justify-content: center;
    padding-top: 50px;
}

.box-card {
    width: 500px;
    padding: 20px;
}

.text-center {
    text-align: center;
}

.success-icon {
    text-align: center;
    margin-bottom: 10px;
}

.tip {
    color: #909399;
    font-size: 14px;
    margin-bottom: 30px;
}

.info-list {
    border-top: 1px solid #eee;
    border-bottom: 1px solid #eee;
    padding: 20px 0;
    margin-bottom: 30px;
}

.item {
    display: flex;
    justify-content: space-between;
    margin-bottom: 10px;
    font-size: 16px;
}

.price {
    color: #f56c6c;
    font-size: 20px;
    font-weight: bold;
}

.btn-group {
    display: flex;
    justify-content: space-around;
}
</style>
