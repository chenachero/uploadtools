package com.happok.xiyan.uploadtools.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "common")
public class CommonConfig {
    private String base64;
    private String url;
    private Boolean sql;
}
