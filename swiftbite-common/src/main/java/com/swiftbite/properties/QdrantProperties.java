package com.swiftbite.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "swiftbite.qdrant")
public class QdrantProperties {
    private String host;
    private int port;
    private String collectionName;
}
