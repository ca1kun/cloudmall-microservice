package edu.scau.mis.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

// 排除数据源的自动配置，因为 mis-auth 可能不直接操作数据库，而是通过 mis-common 中的 Mapper
@SpringBootApplication(scanBasePackages = "edu.scau.mis") // 扫描所有 edu.scau.mis 包下的组件
@MapperScan("edu.scau.mis.common.mapper") // 👈 如果加了 @Mapper 注解，这行其实可以省，但加上更保险
@EnableDiscoveryClient // 开启服务注册与发现功能
public class MisAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(MisAuthApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  认证授权中心启动成功   ლ(´ڡ`ლ)ﾞ");
    }
}
