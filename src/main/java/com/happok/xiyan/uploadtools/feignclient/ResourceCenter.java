package com.happok.xiyan.uploadtools.feignclient;

import com.alibaba.fastjson.JSONObject;
import com.happok.xiyan.uploadtools.entity.SharedEntity;
import com.happok.xiyan.uploadtools.entity.SourceEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "education-resource")
public interface ResourceCenter {
    // 添加资源
    @RequestMapping(value = "/education_resource/my_resources/add", method = RequestMethod.POST)
    JSONObject addResource(@RequestHeader("User") String header, @RequestBody SourceEntity sourceEntity);

    // 添加免审核用户
    @RequestMapping(value = "/education_resource/share_managed", method = RequestMethod.POST)
    JSONObject addAssessorUser(@RequestHeader("User") String header, @RequestBody Object userId);

    // 设置资源公开
    @RequestMapping(value = "/education_resource/share_resources", method = RequestMethod.POST)
    JSONObject adSharedSource(@RequestHeader("User") String header, @RequestBody SharedEntity sharedEntity);
}
