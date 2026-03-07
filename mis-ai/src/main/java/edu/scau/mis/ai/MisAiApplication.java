package edu.scau.mis.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


// 👇 关键：排除掉数据库和 MyBatis 的自动配置
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"edu.scau.mis"},exclude = {
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
        com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration.class
})
public class MisAiApplication {
    public static void main(String[] args) {
        SpringApplication.run(MisAiApplication.class, args);
    }
}