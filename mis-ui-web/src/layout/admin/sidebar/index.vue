<template>
    <div>
        <div class="logo">
            <img src="@/assets/logo.svg" style="height:35px"><span class="title">商城管理系统</span>
        </div>
        <el-menu default-active="/home" router>
            <div v-for="(item, index) in routerList" :key="index">
                <el-sub-menu :index="item.path" v-if="item.children">
                    <template #title>
                        <el-icon>
                            <component :is="item.meta.icon"></component>
                        </el-icon>{{ item.meta.title }}
                    </template>
                    <el-menu-item-group v-for="val in item.children" :key="val.path">
                        <el-menu-item :index="val.path">
                            <el-icon>
                                <component :is="val.meta.icon"></component>
                            </el-icon>
                            <span>{{ val.meta.title }}</span></el-menu-item>
                    </el-menu-item-group>
                </el-sub-menu>
                <el-menu-item :index="item.path" v-else>
                    <el-icon>
                        <component :is="item.meta.icon"></component>
                    </el-icon>
                    <span>{{ item.meta.title }}</span>
                </el-menu-item>
            </div>
        </el-menu>
    </div>
</template>

<script lang="ts" setup>
import { adminRouters } from '@/router/adminRouter'
const routerList = [...adminRouters]
</script>

<style scoped>
.layout-container-demo .el-menu {
    border-right: none;
}

.logo {
    text-align: center;
    color: #079465;
    line-height: 40px;
    margin-top: 10px;
}

.title {
    font-weight: 500;
    font-size: 1.5rem;
    position: relative;
    top: -10px;
}
</style>