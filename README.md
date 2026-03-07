# ⚡️ Cloud-Mall: 微服务全栈高并发电商系统

&nbsp;

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1-green.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud Alibaba](https://img.shields.io/badge/Spring%20Cloud%20Alibaba-2022.0.0.0-orange.svg)](https://github.com/alibaba/spring-cloud-alibaba)
[![Vue 3](https://img.shields.io/badge/Vue-3.3-brightgreen.svg)](https://vuejs.org/)
[![Nacos](https://img.shields.io/badge/Nacos-2.2-blue.svg)](https://nacos.io/)
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3.12-orange.svg)](https://www.rabbitmq.com/)

&nbsp;

> **Cloud-Mall** 是一个基于 **Spring Cloud Alibaba** + **Vue 3** 架构开发的现代化全栈分布式电商项目。项目旨在模拟高并发场景下的真实电商业务流程，涵盖了从用户认证、商品浏览、购物车、订单交易到支付结算的完整闭环。

---

## 📢 重要声明 (README FIRST)

> [!WARNING]
> **关于图片资源失效风险**
> 本项目演示数据中的商品图片目前托管于作者个人的 **阿里云 OSS** 上。由于云服务存在欠费或到期失效的可能，访问图片可能会在未来某天出现 404。
> **建议方案：**
> 1. 想要长期稳定运行，请在 `mis-web` 模块中配置并绑定您自己的阿里云 OSS 账号。
> 2. 您也可以手动上传本项目 `sql/product-images` 文件夹内的图片到您的存储空间。

> [!NOTE]
> **项目完善度说明**
> 本系统目前已打通核心业务链（下单、支付、高并发抢券），但作为一个开源学习项目，仍有许多细节（如退款流、物流对接、复杂SKU）尚未完全覆盖。
> **学习建议：**
> 欢迎开发者拉取代码后自行完善功能。强烈建议使用 **JMeter** 针对各模块（如购物车写操作、秒杀接口）进行压力测试，观察微服务在高负载下的表现。

---

## 🚀 项目核心亮点

*   **微服务架构治理**：基于 **Nacos** 实现全服务注册、发现及配置中心管理，利用 **Spring Cloud Gateway** 实现统一网关鉴权。
*   **高并发秒杀系统**：设计 **Redis 预热 + Lua 脚本** 的原子性扣减方案，经 JMeter 压测确保在高并发下 **绝对不超卖**。
*   **高性能购物车**：采用 **Write-Behind (写缓冲)** 模式，优先读写 Redis，通过 **RabbitMQ** 异步同步至 MySQL。
*   **分布式事务补偿**：利用 RabbitMQ 延时队列实现 30 分钟超时未支付自动关单及库存回滚。
*   **真实支付闭环**：对接 **支付宝沙箱 (Alipay SDK)**，实现完整的支付、同步跳转、异步通知流程。

---

## 🏗️ 系统模块设计

| 模块名 | 端口 | 核心职责 |
| :--- | :--- | :--- |
| **mis-gateway** | 8080 | 统一网关入口、跨域处理、JWT 令牌校验 |
| **mis-auth** | 9201 | 认证中心、双模登录 (密码/验证码)、Token 颁发 |
| **mis-web (api)** | 8081 | 商品中心、分类管理、OSS 文件上传、库存管理 |
| **mis-marketing**| 8083 | 营销中心、优惠券秒杀、Redis 缓存预热 |
| **mis-cart** | 8084 | 购物车服务、Redis 哈希结构存储、异步同步 |
| **mis-order** | 8085 | 订单中心、状态机流转、Feign 远程调用 |
| **mis-pay** | 8086 | 支付服务、支付宝沙箱对接、流水记录 |

---

## 📊 压力测试表现 (Performance)

针对核心秒杀接口（领券中心）进行了 JMeter 压测：
*   **并发规模**：400 线程瞬时爆发。
*   **平均响应**：**4ms** (本地环境)。
*   **拦截表现**：Lua 脚本精准拦截超额请求，异常率严格符合库存逻辑预期。

---

## 🛠️ 本地快速部署

### 1. 基础设施
*   **MySQL 8.0**: 执行 `sql/scau_mall.sql` 初始化表结构及商品演示数据。
*   **Redis / RabbitMQ**: 保持默认配置启动。
*   **Nacos**: `docker run -d --name nacos -e MODE=standalone -p 8848:8848 nacos/nacos-server:v2.2.3`

### 2. 后端启动
1. 导入项目到 IDEA，Maven 刷新依赖。
2. 登录 Nacos (`localhost:8848`) 导入配置。
3. 按照 `Gateway` -> `Auth` -> `业务模块` 的顺序启动。

### 3. 前端启动
```bash
cd mis-ui-web
npm install
npm run dev
```

---

💡 声明
本项目仅供学习交流，严禁商业用途。 如果觉得不错，请点一个 Star ⭐ 鼓励作者！
