package com.happok.xiyan.uploadtools.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;

@Configuration
public class FileUploadConfig {
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //单个文件最大
        factory.setMaxFileSize("4096MB"); //KB,MB
        /// 设置总上传数据总大小
        factory.setMaxRequestSize("5242880KB");
        return factory.createMultipartConfig();
    }
}
