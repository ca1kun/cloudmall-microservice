package edu.scau.mis.cart;

import jakarta.validation.constraints.Email;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
// 1. 扫描 common 组件 (Filter, RedisConfig 等)
@SpringBootApplication(scanBasePackages = {"edu.scau.mis"}) 
// 2. 扫描当前模块的 Mapper
@MapperScan("edu.scau.mis.cart.mapper")
@EnableDiscoveryClient
public class MisCartApplication {
    public static void main(String[] args) {
        SpringApplication.run(MisCartApplication.class, args);
    }
}