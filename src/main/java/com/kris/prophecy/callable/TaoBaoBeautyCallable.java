package com.kris.prophecy.callable;

import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.constant.TaoBaoBeautyConstant;
import com.kris.prophecy.enums.*;
import com.kris.prophecy.framework.ConcurrentCallable;
import com.kris.prophecy.framework.DispatchService;
import com.kris.prophecy.framework.RedisService;
import com.kris.prophecy.model.DispatchRequest;
import com.kris.prophecy.model.Result;
import com.kris.prophecy.utils.*;
import lombok.Data;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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
 * @date 2020/1/19
 */
@Component(ServiceCode.BEAUTY_GIRLS)
@Scope("prototype")
@ConfigurationProperties(prefix = "beauty")
@Data
public class TaoBaoBeautyCallable implements ConcurrentCallable {
    private JSONObject paramJson;

    private String url;

    private String appId;

    private String appSecret;

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
        if (!BeautyTypeEnum.hasType(paramJson.getString(TaoBaoBeautyConstant.TYPE))) {
            return Result.fail("type invalid !");
        }
        if (paramJson.containsKey(TaoBaoBeautyConstant.PAGE)) {
            if (!CommonCheckUtil.validateNumber(paramJson.getString(TaoBaoBeautyConstant.PAGE))) {
                return Result.fail("page invalid !");
            }
        }
        return Result.success();
    }

    @Override
    public Result call() {
        DateFormat df = new SimpleDateFormat(CommonConstant.DATE_FORMAT_DEFAULT);
        String requestTime = df.format(new Date());
        String type = BeautyTypeEnum.getTypeDesc(paramJson.getString(TaoBaoBeautyConstant.TYPE));
        String page = paramJson.getString(TaoBaoBeautyConstant.PAGE) == null ?
                "0" : paramJson.getString(TaoBaoBeautyConstant.PAGE);
        String conditionMessage = String.format("type=%s,page=%s", type, page);
        try {
            return getData(type, page);
        } catch (Exception e) {
            LogUtil.logError(requestTime, conditionMessage, "请求淘女郎查询接口失败", e);
            return new Result(DataErrorCode.FAIL);
        }
    }

    private Result getData(String type, String page) throws IOException {
        JSONObject queryString = new JSONObject();
        queryString.put(TaoBaoBeautyConstant.PAGE, page);
        queryString.put(TaoBaoBeautyConstant.SHOW_API_APP_ID, appId);
        queryString.put(TaoBaoBeautyConstant.SHOW_API_TIMESTAMP, BeautySignUtil.generateTimestamp());
        queryString.put(TaoBaoBeautyConstant.TYPE, type);
        queryString.put(TaoBaoBeautyConstant.SHOW_API_SIGN, BeautySignUtil.signRequest(queryString, appSecret));
        JSONObject keyString = new JSONObject();
        keyString.put(TaoBaoBeautyConstant.TYPE, type);
        keyString.put(TaoBaoBeautyConstant.PAGE, page);
        DispatchRequest dispatchRequest = DispatchRequest.builder()
                .key(KeyUtil.structureKey(keyString, ServiceIdEnum.D007.getId()))
                .request(getRequest(queryString))
                .requestParam(queryString)
                .callId(ServiceIdEnum.D007.getId())
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
        return new Request.Builder().url(datasourceUrl).build();
    }

    private Result dealQueryResult(Result result, DispatchRequest dispatchRequest) {
        redisService.asyncSet(dispatchRequest.getKey(), result.getJsonResult().toJSONString(), 30 * 24 * 3600);
        return result;
    }

}
