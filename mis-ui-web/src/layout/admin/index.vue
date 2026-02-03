<template>
    <el-container class="layout-container-demo">
        <el-aside width="200px">
            <el-scrollbar>
                <side-bar />
            </el-scrollbar>
        </el-aside>

        <el-container>
            <el-header style="text-align: right; font-size: 12px">
                <nav-bar />
            </el-header>

            <el-main>
                <el-scrollbar>
                    <router-view v-slot="{ Component }">
                        <keep-alive>
                            <component :is="Component" />
                        </keep-alive>
                    </router-view>
                </el-scrollbar>
            </el-main>
        </el-container>
    </el-container>
</template>

<script lang="ts" setup>
import NavBar from '@/layout/admin/navbar/index.vue'
import SideBar from '@/layout/admin/sidebar/index.vue'
</script>

<style scoped>
.layout-container-demo .el-header {
    position: relative;
    background-color: var(--el-color-primary-light-7);
    color: var(--el-text-color-primary);
}

.layout-container-demo .el-aside {
    color: var(--el-text-color-primary);
    background: var(--el-color-primary-light-8);
    height: 100vh;
}

.layout-container-demo .el-main {
    padding: 0;
}
</style>
