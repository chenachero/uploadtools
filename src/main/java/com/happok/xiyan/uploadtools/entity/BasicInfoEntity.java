package com.happok.xiyan.uploadtools.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BasicInfoEntity {
    private String path_name;// 资源路径
    private String author;// 作者
    private String source_name;// 资源名称
}
