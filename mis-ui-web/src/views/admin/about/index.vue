<template>
  <div class="about-container">
    <el-card class="box-card">
      <!-- 头部 Logo 区 -->
      <div class="header-section">
        <el-image
          src="https://element-plus.org/images/element-plus-logo.svg"
          style="width: 150px; margin-bottom: 20px;"
        />
        <h1>SCAU 微服务电商系统</h1>
        <p class="subtitle">一个基于 Spring Cloud Alibaba + Vue3 的高并发分布式商城实战项目</p>
        <div class="tags">
          <el-tag effect="dark">v1.0.0</el-tag>
          <el-tag type="success" effect="dark" style="margin-left: 10px;">开源</el-tag>
        </div>
      </div>

      <el-divider content-position="center">核心技术栈</el-divider>

      <!-- 技术栈展示 -->
      <el-row :gutter="20" class="tech-stack">
        <el-col :span="8">
          <el-card shadow="hover" class="tech-card">
            <template #header>
              <div class="card-header">
                <el-icon><Monitor /></el-icon>
                <span>前端技术</span>
              </div>
            </template>
            <ul>
              <li>Vue 3 + Composition API</li>
              <li>Vite + TypeScript</li>
              <li>Element Plus UI</li>
              <li>Pinia 状态管理</li>
              <li>Vue Router 4</li>
            </ul>
          </el-card>
        </el-col>

        <el-col :span="8">
          <el-card shadow="hover" class="tech-card">
            <template #header>
              <div class="card-header">
                <el-icon><Platform /></el-icon>
                <span>后端技术</span>
              </div>
            </template>
            <ul>
              <li>Spring Boot 3</li>
              <li>Spring Cloud Alibaba</li>
              <li>Nacos 注册/配置中心</li>
              <li>Spring Cloud Gateway</li>
              <li>OpenFeign 远程调用</li>
            </ul>
          </el-card>
        </el-col>

        <el-col :span="8">
          <el-card shadow="hover" class="tech-card">
            <template #header>
              <div class="card-header">
                <el-icon><Cpu /></el-icon>
                <span>中间件 & 架构</span>
              </div>
            </template>
            <ul>
              <li>MySQL + MyBatis-Plus</li>
              <li>Redis 分布式缓存</li>
              <li>RabbitMQ 消息队列</li>
              <li>Redisson 分布式锁</li>
              <li>阿里云 OSS 对象存储</li>
            </ul>
          </el-card>
        </el-col>
      </el-row>

      <el-divider content-position="center">联系我们</el-divider>

      <div class="footer-section">
        <el-button type="primary" icon="Link" @click="openLink('https://github.com/ca1kun/cloudmall-microservice.git')">
          GitHub 仓库
        </el-button>
        <el-button icon="Message">联系作者</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { Monitor, Platform, Cpu, Link, Message } from '@element-plus/icons-vue'

const openLink = (url: string) => {
  window.open(url, '_blank')
}
</script>

<style scoped>
.about-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.header-section {
  text-align: center;
  padding: 40px 0;
}

h1 {
  font-size: 28px;
  color: #303133;
  margin-bottom: 10px;
}

.subtitle {
  color: #909399;
  font-size: 16px;
  margin-bottom: 20px;
}

.tech-stack {
  margin-top: 30px;
}

.tech-card {
  height: 100%;
}

.card-header {
  display: flex;
  align-items: center;
  font-weight: bold;
}

.card-header .el-icon {
  margin-right: 8px;
  font-size: 18px;
}

ul {
  list-style: none;
  padding: 0;
  margin: 0;
  color: #606266;
  line-height: 2;
  font-size: 14px;
}

ul li::before {
  content: "•";
  color: #409EFF;
  display: inline-block;
  width: 1em;
  margin-left: -1em;
}

.footer-section {
  text-align: center;
  padding: 30px 0;
}
</style>
