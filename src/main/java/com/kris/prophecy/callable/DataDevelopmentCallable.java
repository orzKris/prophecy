package com.kris.prophecy.callable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.constant.DataDevelopmentConstant;
import com.kris.prophecy.enums.*;
import com.kris.prophecy.framework.ConcurrentCallable;
import com.kris.prophecy.framework.MongoService;
import com.kris.prophecy.model.DataCenter;
import com.kris.prophecy.model.Result;
import com.kris.prophecy.utils.CommonCheckUtil;
import com.kris.prophecy.utils.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Kris
 * @date 2019/3/13
 */
@Component(ServiceCode.DATA_DUMP)
@Scope("prototype")
public class DataDevelopmentCallable implements ConcurrentCallable {

    private JSONObject paramJson;

    @Autowired
    MongoService mongoService;

    @Override
    public void init(JSONObject paramJson) {
        this.paramJson = paramJson;
    }

    @Override
    public Result checkParam(JSONObject paramJson) {
        if (paramJson.containsKey(DataDevelopmentConstant.PAGE_NUM)) {
            if (!CommonCheckUtil.validateNumber(paramJson.getString(DataDevelopmentConstant.PAGE_NUM))) {
                return Result.fail("page invalid !");
            }
        }
        if (paramJson.containsKey(DataDevelopmentConstant.PAGE_SIZE)) {
            if (!CommonCheckUtil.validateNumber(paramJson.getString(DataDevelopmentConstant.PAGE_SIZE))) {
                return Result.fail("pageSize invalid !");
            }
        }
        return Result.success();
    }

    @Override
    public Result call() {
        DateFormat df = new SimpleDateFormat(CommonConstant.DATE_FORMAT_DEFAULT);
        String requestTime = df.format(new Date());
        String interfaceId = paramJson.getString(DataDevelopmentConstant.INTERFACE_ID);
        Integer page = paramJson.getInteger(DataDevelopmentConstant.PAGE_NUM) == null ? 0 : paramJson.getInteger(DataDevelopmentConstant.PAGE_NUM);
        Integer pageSize = paramJson.getInteger(DataDevelopmentConstant.PAGE_SIZE) == null ? 10 : paramJson.getInteger(DataDevelopmentConstant.PAGE_SIZE);
        String conditionMessage = String.format("interfaceId=%s,page=%s,pageSize=%s", interfaceId, page, pageSize);
        long start = System.currentTimeMillis();

        Result result = new Result(DataErrorCode.SUCCESS);
        JSONObject jsonResult = new JSONObject();
        JSONArray jsonArray;
        try {
            if (paramJson.containsKey(DataDevelopmentConstant.INTERFACE_ID)) {
                Page<DataCenter> data = mongoService.findByIdLike(interfaceId, page, pageSize);
                LogUtil.logInfoMongo(JSON.toJSONString(data.getContent()), start, conditionMessage);
                jsonArray = JSONArray.parseArray(JSON.toJSONString(data.getContent()));
                jsonResult.put(DataDevelopmentConstant.COUNT, mongoService.countByIdLike(interfaceId));
            } else {
                Page<DataCenter> data = mongoService.findAll(page, pageSize);
                LogUtil.logInfoMongo(JSON.toJSONString(data.getContent()), start, conditionMessage);
                jsonArray = JSONArray.parseArray(JSON.toJSONString(data.getContent()));
                jsonResult.put(DataDevelopmentConstant.COUNT, mongoService.count());
            }
        } catch (Exception e) {
            LogUtil.logError(requestTime, conditionMessage, "大数据输出失败", e);
            return new Result(DataErrorCode.FAIL);
        }
        jsonResult.put(DataDevelopmentConstant.RESULT_ARRAY, jsonArray);
        jsonResult.put(DataDevelopmentConstant.PAGE_SIZE, pageSize);
        jsonResult.put(DataDevelopmentConstant.PAGE_NUM, page);
        result.setJsonResult(jsonResult);
        result.setDataFrom(DataFromEnum.DATA_FROM_MONGO);
        return result;
    }
}
