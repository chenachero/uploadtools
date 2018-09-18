package com.happok.xiyan.uploadtools.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SourceEntity {
    private Integer id;
    private String path_name;// 资源路径
    private String author;// 作者
    private String source_name;// 资源名称
    private String media_type;// video
    private String origin;// 来源 import
}
