package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "sky.minio")
public class MinioOSSProperties { // 配置项，其他组件自动注入即可获得
    private String endpoint;
    private String bucketName;
    private String accessKey;
    private String secretKey;
}