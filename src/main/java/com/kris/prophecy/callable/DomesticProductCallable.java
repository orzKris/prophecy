package com.kris.prophecy.callable;

import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.constant.DomesticProductConstant;
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
@Component(ServiceCode.GDP)
@Scope("prototype")
@ConfigurationProperties(prefix = "gdp")
@Data
public class DomesticProductCallable implements ConcurrentCallable {

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
        if (!CommonCheckUtil.validateYear(paramJson.getString(DomesticProductConstant.YEAR))) {
            return Result.fail("year invalid !");
        }
        if (!CommonCheckUtil.validateString(paramJson.getString(DomesticProductConstant.AREA))) {
            return Result.fail("area invalid !");
        }
        if (paramJson.containsKey(DomesticProductConstant.PAGE)) {
            if (!CommonCheckUtil.validateNumber(paramJson.getString(DomesticProductConstant.PAGE))) {
                return Result.fail("page invalid !");
            }
        }
        return Result.success();
    }

    @Override
    public Result call() {
        DateFormat df = new SimpleDateFormat(CommonConstant.DATE_FORMAT_DEFAULT);
        String requestTime = df.format(new Date());
        String year = paramJson.getString(DomesticProductConstant.YEAR);
        String area = paramJson.getString(DomesticProductConstant.AREA);
        String page = paramJson.getString(DomesticProductConstant.PAGE) == null ?
                "0" : paramJson.getString(DomesticProductConstant.PAGE);
        String conditionMessage = String.format("year=%s,area=%s,page=%s", year, area, page);
        try {
            return getData(year, area, page, requestTime);
        } catch (Exception e) {
            LogUtil.logError(requestTime, conditionMessage, "请求国家统计局地区生产总值接口失败", e);
            return new Result(DataErrorCode.FAIL);
        }
    }

    private Result getData(String year, String area, String page, String requestTime) throws IOException {
        JSONObject queryString = new JSONObject();
        queryString.put(DomesticProductConstant.DATASOURCE_SEARCH, year + area + DomesticProductConstant.GROSS_DOMESTIC_PRODUCT);
        queryString.put(DomesticProductConstant.SEARCH_KEY, searchKey);
        queryString.put(DomesticProductConstant.DATASOURCE_PAGE, page);
        JSONObject keyString = new JSONObject();
        keyString.put(DomesticProductConstant.YEAR, year);
        keyString.put(DomesticProductConstant.AREA, area);
        keyString.put(DomesticProductConstant.PAGE, page);
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
        redisService.asyncSet(dispatchRequest.getKey(), result.getJsonResult().toJSONString(), 30 * 24 * 3600);
        return result;
    }
}
