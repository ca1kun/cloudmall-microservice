<template>
  <div style="text-align: center; padding-top: 100px;">
    <el-icon color="#67C23A" size="80"><SuccessFilled /></el-icon>
    <h2>支付成功！</h2>
    <p>商家正在备货中...</p>
    <el-button type="primary" @click="$router.push('/mall/home')">返回商城</el-button>
  </div>
</template>