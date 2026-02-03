package edu.scau.mis.pay;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"edu.scau.mis"}) // 扫 common
@EnableDiscoveryClient // 注册到 Nacos
@EnableFeignClients    // 开启远程调用 (为了查订单)
@MapperScan({"edu.scau.mis.pay.mapper", "edu.scau.mis.common.mapper"}) // 扫 Mapper
public class MisPayApplication {
    public static void main(String[] args) {
        SpringApplication.run(MisPayApplication.class, args);
    }
}