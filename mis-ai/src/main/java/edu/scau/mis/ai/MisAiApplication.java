package edu.scau.mis.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@Slf4j
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "edu.scau.mis.ai.feign")
@SpringBootApplication(
        scanBasePackages = {"edu.scau.mis"},
        exclude = {
                org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
                com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration.class
        }
)
public class MisAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MisAiApplication.class, args);
        log.info("AI service started successfully");
    }
}
