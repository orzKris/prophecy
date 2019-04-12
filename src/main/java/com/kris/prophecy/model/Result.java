package com.kris.prophecy.model;


import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.enums.DataFromEnum;
import com.kris.prophecy.enums.ResponseConstant;
import lombok.Data;

/**
 * @author Kris
 * @date 2018/11/29
 */
@Data
public class Result {

    private String name;

    private String status;

    private JSONObject jsonResult;

    private String contentNotParsed;

    private DataFromEnum dataFrom;

    public Result() {
    }

    public Result(String status) {
        this.status = status;
    }

    public Result(String status, JSONObject jsonResult) {
        this.status = status;
        this.jsonResult = jsonResult;
    }

    public Result(String name, String status) {
        this.name = name;
        this.status = status;
    }

    public Result(String name, String status, JSONObject jsonResult, DataFromEnum dataFrom) {
        this.name = name;
        this.status = status;
        this.jsonResult = jsonResult;
        this.dataFrom = dataFrom;
    }

    public static Result success() {
        Result result = new Result(ResponseConstant.SUCCESS);
        return result;
    }

    public static Result fail(String s) {
        Result result = new Result();
        JSONObject jsonResult = new JSONObject();
        jsonResult.put(ResponseConstant.RESPONSE, s);
        result.setStatus(ResponseConstant.PARAM_ERROR);
        result.setJsonResult(jsonResult);
        return result;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("status", status);
        jsonObject.put("jsonResult", jsonResult);
        jsonObject.put("dataFrom", dataFrom);
        return jsonObject;
    }

}
