package com.happok.xiyan.uploadtools.common;

import com.alibaba.fastjson.JSONObject;
import com.happok.xiyan.uploadtools.util.Base64Util;

import java.util.List;

public class UserInfo {
    // 用户id
    private Integer id;
    // 用户名称
    private String name;
    // 角色名称
    private Integer role;
    // 机构列表
    private Integer organization_id;
    // 数据权限
    private List<Integer> data_perms;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public Integer getOrganization_id() {
        return organization_id;
    }

    public void setOrganization_id(Integer organization_id) {
        this.organization_id = organization_id;
    }

    public List<Integer> getData_perms() {
        return data_perms;
    }

    public void setData_perms(List<Integer> data_perms) {
        this.data_perms = data_perms;
    }

    public static String getBase64Head(UserInfo user) {
        JSONObject res = new JSONObject(true);

        res.put("id", user.getId());
        res.put("name", user.getName());
        res.put("role", user.getRole());
        res.put("organization_id", user.getOrganization_id());
        res.put("data_perms", user.getData_perms());

        return Base64Util.encodeData(res.toString());
    }


    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", role=" + role +
                ", organization_id=" + organization_id +
                ", data_perms=" + data_perms +
                '}';
    }
}
