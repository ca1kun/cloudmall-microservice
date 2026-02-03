package edu.scau.mis.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MisGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(MisGatewayApplication.class, args);
    }
}