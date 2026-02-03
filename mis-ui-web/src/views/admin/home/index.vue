<template>
  <div class="dashboard-container">
    <!-- 1. 数据卡片区 -->
    <el-row :gutter="20">
      <el-col :span="6" v-for="item in cards" :key="item.title">
        <el-card shadow="hover" class="data-card">
          <div class="card-header">
            <span>{{ item.title }}</span>
            <el-tag :type="item.type" effect="dark">{{ item.tag }}</el-tag>
          </div>
          <div class="card-num">{{ item.num }}</div>
          <div class="card-footer">
            <span>较昨日</span>
            <span :class="item.up ? 'up' : 'down'">
              {{ item.rate }}% <el-icon><component :is="item.up ? 'Top' : 'Bottom'" /></el-icon>
            </span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 2. 图表区 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="16">
        <el-card header="📊 近七日销售趋势">
          <div ref="chartRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card header="🔥 热销商品 Top 5">
          <el-table :data="rankList" style="width: 100%" :show-header="false">
            <el-table-column type="index" width="50" />
            <el-table-column prop="name" label="商品" />
            <el-table-column prop="count" label="销量" width="80" align="right" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts' // 需安装 echarts
import { Top, Bottom } from '@element-plus/icons-vue'

// 1. 卡片数据 (Mock)
const cards = [
  { title: '总销售额', num: '¥ 126,560', tag: '月', type: 'primary', rate: 12.5, up: true },
  { title: '今日订单', num: '35', tag: '日', type: 'success', rate: 8.2, up: true },
  { title: '商品总数', num: '120', tag: '全', type: 'warning', rate: 0.0, up: true },
  { title: '注册用户', num: '58', tag: '人', type: 'danger', rate: 2.1, up: false },
]

// 2. 排行榜数据 (Mock)
const rankList = [
  { name: '小米14 Pro 钛金属版', count: 120 },
  { name: 'iPhone 15 Pro Max', count: 85 },
  { name: '三只松鼠 每日坚果', count: 76 },
  { name: '维达 抽纸 4层', count: 60 },
  { name: '罗技 MX Master 3S', count: 45 },
]

// 3. 图表初始化
const chartRef = ref()
let myChart: any = null

const initChart = () => {
  if (!chartRef.value) return
  myChart = echarts.init(chartRef.value)
  
  const option = {
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: { type: 'value' },
    series: [
      {
        name: '销售额',
        type: 'line',
        smooth: true, // 平滑曲线
        data: [820, 932, 901, 934, 1290, 1330, 1320],
        areaStyle: { opacity: 0.3 }, // 填充颜色
        itemStyle: { color: '#409EFF' }
      },
      {
        name: '订单量',
        type: 'line',
        smooth: true,
        data: [20, 32, 21, 34, 90, 30, 20],
        itemStyle: { color: '#67C23A' }
      }
    ]
  }
  
  myChart.setOption(option)
}

// 窗口大小改变时重绘图表
const handleResize = () => myChart?.resize()

onMounted(() => {
  initChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.dashboard-container { padding: 20px; }
.data-card { height: 160px; display: flex; flex-direction: column; justify-content: space-between; }
.card-header { display: flex; justify-content: space-between; align-items: center; color: #909399; }
.card-num { font-size: 28px; font-weight: bold; color: #303133; margin-top: 10px; }
.card-footer { margin-top: 15px; font-size: 14px; color: #606266; display: flex; align-items: center; }
.up { color: #f56c6c; margin-left: 5px; display: flex; align-items: center; }
.down { color: #67c23a; margin-left: 5px; display: flex; align-items: center; }
</style>