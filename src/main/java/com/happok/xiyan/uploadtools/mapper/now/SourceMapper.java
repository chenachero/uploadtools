package com.happok.xiyan.uploadtools.mapper.now;

import com.happok.xiyan.uploadtools.entity.BasicInfoEntity;
import com.happok.xiyan.uploadtools.entity.SourceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SourceMapper {
    // 批量插入
    boolean addSourceInfo(@Param("lists") List<BasicInfoEntity> lists);

    // 获取全部的数据
    List<SourceEntity> getSourceList();

    // 获取一条数据
    SourceEntity getSourceInfo();

    // 设置已经上传
    boolean updateSourceUpload(Integer sourceId);
}
