package edu.scau.mis.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "aliyun.pnvs")
public class AliyunPnvsProperties {

    private String endpoint = "dypnsapi.aliyuncs.com";

    private String signName;

    private String templateCode;

    private String schemeName = "默认方案";

    private Integer codeLength = 6;

    private Integer validTime = 60;

    private Integer interval = 60;
}