<template>
    <div class="navbar">
        <!-- 左侧面包屑或标题 -->
        <div class="left-breadcrumb">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
                <el-breadcrumb-item>后台管理</el-breadcrumb-item>
            </el-breadcrumb>
        </div>

        <!-- 右侧菜单 -->
        <div class="right-menu">
            <div class="user-block">
                <span class="username">你好，{{ userStore.username }}</span>

                <el-dropdown trigger="click" class="avatar-container">
                    <div class="avatar-wrapper">
                        <el-avatar :size="32" :src="userStore.avatar" :icon="UserFilled" class="user-avatar" />
                        <el-icon class="caret-icon">
                            <CaretBottom />
                        </el-icon>
                    </div>

                    <template #dropdown>
                        <!-- 👇 关键修改：设置 class 并去掉 router-link -->
                        <el-dropdown-menu class="user-dropdown">
                            <el-dropdown-item icon="User" @click="toProfile">
                                个人中心
                            </el-dropdown-item>

                            <el-dropdown-item divided icon="SwitchButton" @click="handleLogout">
                                退出登录
                            </el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router' // 引入 router
import { ElMessage, ElMessageBox } from 'element-plus'
import { UserFilled, CaretBottom, User, SwitchButton } from '@element-plus/icons-vue' // 引入更多图标

const userStore = useUserStore()
const router = useRouter()

// 跳转逻辑
const toProfile = () => {
    if (userStore.role === 'ADMIN') {
        router.push('/profile')
    } else {
        router.push('/mall/profile')
    }
}

const handleLogout = () => {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    })
        .then(async () => {
            await userStore.logout()
            ElMessage.success('退出成功')
            router.replace('/login') // 强制跳转
        })
        .catch(() => { })
}
</script>

<style scoped>
.navbar {
    height: 50px;
    overflow: hidden;
    position: relative;
    background: #fff;
    box-shadow: 0 1px 4px rgba(0, 21, 41, .08);
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 20px;
}

.left-breadcrumb {
    font-size: 14px;
}

.right-menu {
    display: flex;
    align-items: center;
}

.user-block {
    display: flex;
    align-items: center;
}

.username {
    font-size: 14px;
    color: #606266;
    margin-right: 15px;
}

.avatar-container {
    cursor: pointer;
}

.avatar-wrapper {
    display: flex;
    align-items: center;
}

.user-avatar {
    cursor: pointer;
    border-radius: 10px;
    /* 方形圆角头像更商务 */
}

.caret-icon {
    margin-left: 5px;
    font-size: 12px;
    color: #909399;
}
</style>

<style>
/* 👇 全局样式修改下拉框宽度 👇 */
/* 注意：el-dropdown-menu 是挂载在 body 上的，scoped 样式对它无效，必须写在全局 style 里 */
.user-dropdown {
    min-width: 140px !important;
    /* 固定宽度，不再被文字撑得太宽 */
    text-align: center;
    /* 如果你想居中 */
}

/* 如果想让里面的字靠左对齐 (更推荐) */
.user-dropdown .el-dropdown-menu__item {
    justify-content: flex-start;
    /* 图标和文字靠左 */
    padding: 10px 20px;
}
</style>