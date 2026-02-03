package edu.scau.mis.api; // 👈 包名也已更新

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@Slf4j
// 关键：扫描所有需要用到的 Mapper 接口
@MapperScan({
        "edu.scau.mis.product.mapper", // 扫描商品模块的 Mapper
        "edu.scau.mis.common.mapper"   // 扫描公共模块的 Mapper
})
// 关键：扫描所有需要加载为 Spring Bean 的包
@SpringBootApplication(scanBasePackages = {
        "edu.scau.mis.common",    // 1. 公共模块 (Filter, AOP, 工具类等)
        "edu.scau.mis.product",   // 2. 商品服务模块 (Service 实现类)
        "edu.scau.mis.api"        // 3. 当前 API 模块 (Controller)
})
@EnableDiscoveryClient
public class MisApiApplication { // 👈 类名已更新
    public static void main(String[] args) {
        SpringApplication.run(MisApiApplication.class, args); // 👈 使用新的类名
        log.info("(♥◠‿◠)ﾉﾞ  API服务启动成功   ლ(´ڡ`ლ)ﾞ");
    }
}
