package com.happok.xiyan.uploadtools.mapper.old;

import com.happok.xiyan.uploadtools.entity.BasicInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PathMapper {
    // 从表中获取路径
    List<BasicInfoEntity> getResourcePath(@Param("begintime") String begintime,
                                          @Param("endtime") String endtime);
}
