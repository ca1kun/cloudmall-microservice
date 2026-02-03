<template>
    <div class="app-container">
        <!-- 步骤条保持不变 -->
        <el-steps :active="step" finish-status="success" align-center>
            <el-step title="MakeNewSale" />
            <el-step title="EnterItem" />
            <el-step title="EndSale" />
            <el-step title="MakePayment" />
        </el-steps>
        <el-divider />
        <el-row :gutter="20">
            <!-- ========== 左侧操作区 ========== -->
            <el-col :span="5">
                <!-- 商品录入卡片 -->
                <el-card>
                    <template #header>
                        <span>商品录入</span>
                    </template>
                    <el-form :model="enterItemForm" label-width="auto">
                        <el-form-item label="商品编码">
                            <el-select v-model="enterItemForm.itemSn" placeholder="请选择商品" filterable>
                                <el-option v-for="item in productOptions" :key="item.productSn"
                                    :label="item.productName" :value="item.productSn" />
                            </el-select>
                        </el-form-item>
                        <el-form-item label="订购数量">
                            <el-input-number v-model="enterItemForm.quantity" :min="1" controls-position="right" />
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" size="small" :disabled="step !== 1" @click="handleEnterItem">
                                添加商品
                            </el-button>
                            <el-button type="success" size="small" :disabled="step !== 1 || tableData.length === 0"
                                @click="handleEndSale">
                                结束录入
                            </el-button>
                        </el-form-item>
                    </el-form>
                </el-card>
                <el-divider />
                <!-- 订单支付卡片 -->
                <el-card>
                    <template #header>
                        <span>订单支付</span>
                    </template>
                    <el-form :model="makePaymentForm" label-width="auto">
                        <!-- 新增：支付方式选择 -->
                        <el-form-item label="支付方式">
                            <el-select v-model="makePaymentForm.payMethod" :disabled="step !== 3">
                                <el-option label="现金" value="CASH" />
                                <el-option label="支付宝" value="ALIPAY" />
                                <el-option label="微信支付" value="WECHAT_PAY" />
                            </el-select>
                        </el-form-item>
                        <!-- 付款金额改为 el-input-number -->
                        <el-form-item label="实收金额">
                            <el-input-number v-model="makePaymentForm.cashTendered" :min="0" :precision="2"
                                controls-position="right" :disabled="step !== 3" style="width: 100%;" />
                        </el-form-item>
                        <!-- 找零改为只读显示 -->
                        <el-form-item label="找零">
                            <el-input :value="makePaymentForm.changeDue.toFixed(2)" readonly />
                        </el-form-item>
                        <el-form-item>
                            <el-button type="danger" size="small" :disabled="step !== 3" @click="handleMakePayment">
                                确认支付
                            </el-button>
                        </el-form-item>
                    </el-form>
                </el-card>
            </el-col>

            <!-- ========== 右侧展示区 ========== -->
            <el-col :span="19">
                <el-descriptions title="订单信息" :column="3" border>
                    <template #extra>
                        <!-- 新增：挂单按钮 -->
                        <el-button type="warning" :disabled="step !== 1 || tableData.length === 0"
                            @click="handleHoldOrder">
                            挂起订单
                        </el-button>
                        <el-button type="success" :disabled="!(step === 0 || step === 4)" @click="handleMakeNewSale">
                            开始新销售
                        </el-button>
                    </template>
                    <!-- 其他描述项保持不变 -->
                    <el-descriptions-item label="会员">{{ customerName }}</el-descriptions-item>
                    <el-descriptions-item label="订单号">{{ sale.saleNo }}</el-descriptions-item>
                    <el-descriptions-item label="总金额">{{ sale.total.toFixed(2) }}</el-descriptions-item>
                    <el-descriptions-item label="总件数">{{ totalQuantity }}</el-descriptions-item>
                    <el-descriptions-item label="状态">{{ sale.status }}</el-descriptions-item>
                </el-descriptions>
                <el-divider />
                <!-- 订单明细卡片保持不变 -->
                <el-card>
                    <template #header>
                        <span>订单明细</span>
                    </template>
                    <el-table :data="tableData" style="width: 100%" :row-class-name="tableRowClassName">
                        <el-table-column prop="index" label="序号" width="80" />
                        <el-table-column prop="itemSn" label="商品编码" />
                        <el-table-column prop="productName" label="商品名称" />
                        <el-table-column prop="price" label="销售价格" />
                        <el-table-column prop="quantity" label="订购数量" width="200">
                            <template #default="scope">
                                <el-input-number size="small" v-model="scope.row.quantity" :disabled="step !== 1"
                                    :min="1" @change="handleChangeQuantity(scope.row)" />
                            </template>
                        </el-table-column>
                        <el-table-column label="操作" align="center">
                            <template #default="scope">
                                <el-button link type="primary" icon="Delete" size="small" :disabled="step !== 1"
                                    @click="handleDelete(scope.row)">删除</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                    <template #footer>总件数: {{ totalQuantity }}件 ｜ 总金额: {{ totalAmount.toFixed(2) }} 元</template>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
    enterItem,
    makeNewSale,
    endSale,
    makePayment,
    changeQuantity,
    deleteSaleItem,
    holdCurrentOrder
} from '@/api/system/sale';
import type { Sale, Product, EnterItemForm, SaleItem, MakePaymentForm } from "@/types/types";
import { listAllProduct } from '@/api/item/product.ts';

// 控制业务步骤
const step = ref(0)

// --- 数据模型 ---
const totalQuantity = ref(0)
const totalAmount = ref(0.00)
const customerName = ref('guest') // 默认值
const sale = ref<Sale>({
    saleId: undefined,
    saleNo: '',
    total: 0.00,
    totalQuantity: 0,
    status: '',
})
const productOptions = ref<Product[]>([])
const enterItemForm = ref<EnterItemForm>({ itemSn: '', quantity: 1 })
const tableData = ref<SaleItem[]>([])
const makePaymentForm = ref<MakePaymentForm>({
    payMethod: 'CASH', // 默认支付方式
    cashTendered: 0.00,
    changeDue: 0.00
})

// --- 核心业务流程 ---

// 1. 开始新的销售
function handleMakeNewSale() {
    // 【关键修复】第一步：立即重置所有本地状态，尤其是 sale 对象
    tableData.value = []
    enterItemForm.value = { itemSn: '', quantity: 1 }
    makePaymentForm.value = { payMethod: 'CASH', cashTendered: 0.00, changeDue: 0.00 }
    sale.value = { // 强制重置 sale 对象
        saleId: undefined,
        saleNo: '---',
        total: 0.00,
        totalQuantity: 0,
        status: '正在创建...',
    }
    step.value = 0; // 暂时将步骤退回，禁用所有操作

    // 第二步：发起异步请求
    makeNewSale().then(response => {
        // 第三步：用后端返回的新订单数据完全覆盖本地状态
        sale.value = response.data

        ElMessage.success(`新订单创建成功，订单号: ${sale.value.saleNo}`)

        // 第四步：在所有状态都更新完毕后，再安全地进入下一步
        step.value = 1

    }).catch(error => {
        console.error('创建订单失败:', error)
        ElMessage.error('创建订单失败，请重试')
        sale.value.status = '创建失败'; // 更新状态提示用户
        step.value = 0; // 保持在初始步骤
    })
}


// 2. 输入商品明细
async function handleEnterItem() {
    if (!enterItemForm.value.itemSn) {
        ElMessage.error('请选择一个商品')
        return
    }
    try {
        console.log('调用 enterItem，参数:', {
            saleId: sale.value.saleId,
            itemSn: enterItemForm.value.itemSn,
            quantity: enterItemForm.value.quantity,
            saleStatus: sale.value.status
        })
        const response = await enterItem(sale.value.saleId!, enterItemForm.value)
        // 后端返回完整的 SaleItem 列表，直接覆盖
        tableData.value = response.data
        ElMessage.success('商品添加成功')
    } catch (error) {
        console.error('添加商品失败:', error)
    } finally {
        enterItemForm.value.itemSn = ''
        enterItemForm.value.quantity = 1
    }
}

// 3. 结束录入
function handleEndSale() {
    endSale(sale.value.saleId!).then(response => {
        // 后端应返回完整的 Sale 对象，包含计算好的 total 和 totalQuantity
        sale.value = response.data
        // 同步总金额到支付表单的实收金额，方便收银员操作
        makePaymentForm.value.cashTendered = sale.value.total
        step.value = 3
    })
}

// 4. 确认支付
async function handleMakePayment() {
    if (makePaymentForm.value.cashTendered < sale.value.total) {
        ElMessage.warning('实收金额不能小于订单总金额')
        return
    }
    try {
        // makePayment API 现在需要一个包含 cashTendered 和 payMethod 的对象
        const response = await makePayment(sale.value.saleId!, makePaymentForm.value)
        // 后端返回 PaymentResultVo，其中 data.changeDue 是找零
        makePaymentForm.value.changeDue = response.data.changeDue
        ElMessage.success(`支付成功！应找零: ${response.data.changeDue.toFixed(2)} 元`)
        step.value = 4
    } catch (error) {
        console.error('支付请求失败:', error)
    }
}

// (新增) 挂起订单
function handleHoldOrder() {
    ElMessageBox.confirm(
        '您确定要挂起当前订单吗？挂起后将清空收银台。',
        '挂单确认',
        {
            confirmButtonText: '确定挂单',
            cancelButtonText: '取消',
            type: 'warning',
        }
    ).then(async () => {
        try {
            await holdCurrentOrder(sale.value.saleId!);
            ElMessage.success('挂单成功！收银台已清空。');
            // 挂单成功后，最简单的方式是重新开始一轮新销售
            handleMakeNewSale();
        } catch (error) {
            console.error("挂单失败:", error);
            ElMessage.error('挂单失败，请重试');
        }
    }).catch(() => {
        ElMessage.info('已取消挂单操作');
    });
}

// --- 订单明细维护 ---

// 监听 tableData 变化，并从后端获取最新总计
// 这样可以确保总金额和总数量永远与后端保持一致
watch(tableData, (newTableData) => {
    if (newTableData.length > 0) {
        let amount = 0;
        let quantity = 0;
        newTableData.forEach(item => {
            amount += item.price * item.quantity;
            quantity += item.quantity;
        });
        totalAmount.value = amount;
        totalQuantity.value = quantity;
    } else {
        totalAmount.value = 0;
        totalQuantity.value = 0;
    }
}, { deep: true });

// 修改数量
const handleChangeQuantity = async (row: SaleItem) => {
    try {
        const response = await changeQuantity(sale.value.saleId!, row.productId!, row.quantity);
        tableData.value = response.data; // 后端返回新列表，直接覆盖
        ElMessage.success('数量修改成功');
    } catch (error) {
        console.error('修改数量失败:', error);
    }
}

// 删除明细
const handleDelete = (row: SaleItem) => {
    ElMessageBox.confirm(`确定删除商品 "${row.productName}" 吗?`, '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(async () => {
        try {
            const response = await deleteSaleItem(sale.value.saleId!, row.productId!);
            tableData.value = response.data; // 后端返回新列表，直接覆盖
            ElMessage.success('商品已删除');
        } catch (error) {
            console.error('删除失败:', error);
        }
    });
}

// --- 初始化与辅助 ---

// 加载商品列表数据
onMounted(() => {
    listAllProduct().then((response: any) => {
        productOptions.value = response.data
    })
})

// 表格行样式
const tableRowClassName = ({ row, rowIndex }: { row: SaleItem; rowIndex: number }) => {
    row.index = rowIndex + 1;
    return (rowIndex % 2 === 0) ? 'warning-row' : 'success-row';
}

</script>

<style scoped>
/* 您的样式保持不变 */
.el-descriptions {
    margin-top: 20px;
}

.cell-item {
    display: flex;
    align-items: center;
}

.margin-top {
    margin-top: 20px;
}

.el-table .warning-row {
    --el-table-tr-bg-color: var(--el-color-warning-light-9);
}

.el-table .success-row {
    --el-table-tr-bg-color: var(--el-color-success-light-9);
}
</style>