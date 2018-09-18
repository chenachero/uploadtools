package com.happok.xiyan.uploadtools.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "datetime")
public class DatatimeConfig {
    private String begintime;
    private String endtime;
}
