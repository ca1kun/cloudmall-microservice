<template>
    <div class="app-container">
        <el-row :gutter="10">
            <el-col :span="1.5">
                <el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
            </el-col>
            <el-col :span="1.5" v-if="categoryList.length > 0">
                <el-button type="warning" plain icon="Download" @click="handleExport">导出</el-button>
            </el-col>
        </el-row>
        <el-divider />
        <el-row :gutter="20">
            <el-table :data="categoryList" style="width: 100%">
                <el-table-column prop="categoryId" label="类别ID" align="center" width="200"></el-table-column>
                <el-table-column prop="parentId" label="上级ID" align="center" width="200"></el-table-column>
                <el-table-column prop="categoryName" label="类别名称" align="center"></el-table-column>
                <el-table-column label="操作" align="center">
                    <template #default="scope">
                        <el-button link type="primary" icon="Edit" size="small"
                            @click="handleUpdate(scope.row)">修改</el-button>
                        <!-- 👇 新增删除按钮 -->
                        <el-button link type="danger" icon="Delete" size="small"
                            @click="handleDelete(scope.row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-row>

        <el-dialog v-model="dialogOpen" :title="title" width="500" @close="resetForm">
            <!-- 监听 success 事件刷新列表，监听 close 事件关闭弹窗 -->
            <category-form v-if="dialogOpen" :category-id="categoryId" @success="getCategoryList"
                @close="dialogOpen = false" />
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ElMessage, ElMessageBox } from 'element-plus'
import { ref } from 'vue'
import { onMounted } from 'vue'
import CategoryForm from '@/components/CategoryForm.vue'
// 导入api接口
import { listCategory } from '@/api/item/category'
import { deleteCategoryApi } from '@/api/item/category' // 引入接口


onMounted(() => {
    getCategoryList()
})

interface Category {
    categoryId: number
    parentId: number
    categoryName: string
}
const categoryList = ref<Category[]>([])

// 关闭弹窗时重置 ID，防止下次打开新增时还残留着修改的 ID
function resetForm() {
    categoryId.value = 0
}



const dialogOpen = ref(false) // 对话框 v-model
const title = ref("") // 对话框 v-bind

const categoryId = ref(0)

/** 获取类别列表 */
function getCategoryList() {
    listCategory().then(res => {
        categoryList.value = res.data
    })
}

/** 新增按钮 */
function handleAdd() {
    categoryId.value = 0 // 确保是新增模式
    dialogOpen.value = true
    title.value = "新增类别"
}

/** 修改按钮 */
function handleUpdate(row: Category) {
    categoryId.value = row.categoryId
    dialogOpen.value = true
    title.value = "修改类别"
}

// 单个删除
function handleDelete(row: Category) {
    ElMessageBox.confirm(
        `确定要删除类别 "${row.categoryName}" 吗？`,
        '警告',
        { confirmButtonText: '是', cancelButtonText: '否', type: 'warning' }
    )
        .then(async () => {
            const res = await deleteCategoryApi(row.categoryId)
            if (res.code === 200) {
                ElMessage.success('删除成功')
                getCategoryList() // 刷新列表
            } else {
                ElMessage.error(res.message || '删除失败')
            }
        })
        .catch(() => { })
}

/** 导出按钮 */
function handleExport() {
    ElMessage({ type: 'info', message: '导出数据', })
}

</script>

<style>
@media (min-width: 1024px) {}
</style>