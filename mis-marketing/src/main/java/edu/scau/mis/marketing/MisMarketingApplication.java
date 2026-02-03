package edu.scau.mis.marketing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"edu.scau.mis"}) // 扫描 common 和 marketing
@EnableDiscoveryClient // 开启服务注册与发现功能
public class MisMarketingApplication {
    public static void main(String[] args) {
        SpringApplication.run(MisMarketingApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  营销服务启动成功   ლ(´ڡ`ლ)ﾞ");
    }
}