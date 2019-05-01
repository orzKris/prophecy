package com.kris.prophecy.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class User {
    private Integer id;

    private String name;

    private Integer age;

    private String password;

    private String sex;

    private String school;

    private String subordinateClass;

    private Integer graduationTime;

    private String registerTime;

    private String uid;

    private Integer balance;

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("age", age);
        jsonObject.put("password", password);
        jsonObject.put("sex", sex);
        jsonObject.put("school", school);
        jsonObject.put("subordinateClass", subordinateClass);
        jsonObject.put("graduationTime", graduationTime);
        jsonObject.put("registerTime", registerTime);
        jsonObject.put("uid", uid);
        return jsonObject;
    }
}