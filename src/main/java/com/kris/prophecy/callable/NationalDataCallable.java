package com.kris.prophecy.callable;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.constant.NationalDataConstant;
import com.kris.prophecy.enums.*;
import com.kris.prophecy.framework.ConcurrentCallable;
import com.kris.prophecy.framework.DispatchService;
import com.kris.prophecy.framework.RedisService;
import com.kris.prophecy.model.DispatchRequest;
import com.kris.prophecy.model.Result;
import com.kris.prophecy.utils.AppendUrlUtil;
import com.kris.prophecy.utils.CommonCheckUtil;
import com.kris.prophecy.utils.KeyUtil;
import com.kris.prophecy.utils.LogUtil;
import lombok.Data;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Kris
 * @date 2019/3/13
 */
@Component(ServiceCode.NATIONAL_DATA)
@Scope("prototype")
@ConfigurationProperties(prefix = "data")
@Data
public class NationalDataCallable implements ConcurrentCallable {

    private JSONObject paramJson;

    private String url;

    private String searchKey;

    private boolean isEnable = true;

    private int timeOut = 2000;

    @Autowired
    private DispatchService dispatchService;

    @Autowired
    private RedisService redisService;

    @Override
    public void init(JSONObject paramJson) {
        this.paramJson = paramJson;
    }

    @Override
    public Result checkParam(JSONObject paramJson) {
        if (StringUtils.isBlank(paramJson.getString(NationalDataConstant.SEARCH_PHRASE))) {
            return Result.fail("key invalid !");
        }
        if (paramJson.containsKey(NationalDataConstant.PAGE)) {
            if (!CommonCheckUtil.validateNumber(paramJson.getString(NationalDataConstant.PAGE))) {
                return Result.fail("page invalid !");
            }
        }
        return Result.success();
    }

    @Override
    public Result call() {
        DateFormat df = new SimpleDateFormat(CommonConstant.DATE_FORMAT_DEFAULT);
        String requestTime = df.format(new Date());
        String key = paramJson.getString(NationalDataConstant.SEARCH_PHRASE);
        String page = paramJson.getString(NationalDataConstant.PAGE) == null ?
                "0" : paramJson.getString(NationalDataConstant.PAGE);
        String conditionMessage = String.format("key=%s,page=%s", key, page);
        try {
            return getData(key, page);
        } catch (Exception e) {
            LogUtil.logError(requestTime, conditionMessage, "请求国家统计局国家数据接口失败", e);
            return new Result(DataErrorCode.FAIL);
        }
    }

    private Result getData(String key, String page) throws IOException {
        JSONObject queryString = new JSONObject();
        queryString.put(NationalDataConstant.DATASOURCE_SEARCH, key);
        queryString.put(NationalDataConstant.SEARCH_KEY, searchKey);
        queryString.put(NationalDataConstant.DATASOURCE_PAGE, page);
        JSONObject keyString = new JSONObject();
        keyString.put(NationalDataConstant.SEARCH_PHRASE, key);
        keyString.put(NationalDataConstant.PAGE, page);
        DispatchRequest dispatchRequest = DispatchRequest.builder()
                .key(KeyUtil.structureKey(keyString, ServiceIdEnum.D006.getId()))
                .request(getRequest(queryString))
                .requestParam(queryString)
                .callId(ServiceIdEnum.D006.getId())
                .okHttpClient(new OkHttpClient())
                .timeOut(timeOut)
                .isEnable(isEnable)
                .build();
        Result result = dispatchService.dispatch(dispatchRequest, true);
        if (DataFromEnum.DATA_FROM_DATASOURCE == result.getDataFrom()) {
            return dealQueryResult(result, dispatchRequest);
        }
        return result;
    }

    private Request getRequest(JSONObject queryString) {
        String datasourceUrl = AppendUrlUtil.getURL(queryString, url);
        Request request = new Request.Builder().url(datasourceUrl).build();
        return request;
    }

    private Result dealQueryResult(Result result, DispatchRequest dispatchRequest) {
        JSONObject jsonResult = result.getJsonResult();
        JSONArray resultArray = jsonResult.getJSONArray(NationalDataConstant.RESULT);
        for (int i = 0; i < resultArray.size(); i++) {
            resultArray.getJSONObject(i).remove(NationalDataConstant.PRANK);
            resultArray.getJSONObject(i).remove(NationalDataConstant.RANK);
            resultArray.getJSONObject(i).remove(NationalDataConstant.REPORT);
            resultArray.getJSONObject(i).remove(NationalDataConstant.EXP);
        }
        jsonResult.put(NationalDataConstant.RESULT, resultArray);
        result.setJsonResult(jsonResult);
        redisService.asyncSet(dispatchRequest.getKey(), result.getJsonResult().toJSONString(), 30 * 24 * 3600);
        return result;
    }
}
