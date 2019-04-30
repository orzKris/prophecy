package com.kris.prophecy.callable;

import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.constant.*;
import com.kris.prophecy.model.Result;
import com.kris.prophecy.enums.*;
import com.kris.prophecy.model.DispatchRequest;
import com.kris.prophecy.framework.ConcurrentCallable;
import com.kris.prophecy.framework.DispatchService;
import com.kris.prophecy.framework.RedisService;
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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Kris
 * @date 2019/2/11
 */
@Component(ServiceCode.MOVIE_INFORMATION)
@Scope("prototype")
@ConfigurationProperties(prefix = "movie")
@Data
public class MovieInformationCallable implements ConcurrentCallable {

    private JSONObject paramJson;

    private String url;

    private String key;

    private boolean isEnable = true;

    private int timeOut = 5000;

    @Autowired
    DispatchService dispatchService;

    @Autowired
    RedisService redisService;

    @Override
    public void init(JSONObject paramJson) {
        this.paramJson = paramJson;
    }

    @Override
    public Result checkParam(JSONObject paramJson) {
        if (!CommonCheckUtil.validateString(paramJson.getString(MovieInformationConstant.TITLE))) {
            return Result.fail("title invalid !");
        }
        return Result.success();
    }

    @Override
    public Result call() throws UnsupportedEncodingException {
        DateFormat df = new SimpleDateFormat(CommonConstant.DATE_FORMAT_DEFAULT);
        String requestTime = df.format(new Date());
        String title = URLEncoder.encode(paramJson.getString(MovieInformationConstant.TITLE), "UTF-8");
        Integer searchFlag = paramJson.getInteger(MovieInformationConstant.SEARCH_FLAG) == null ? 1
                : paramJson.getInteger(MovieInformationConstant.SEARCH_FLAG);
        Integer pageSize = paramJson.getInteger(MovieInformationConstant.PAGE_SIZE) == null ? 20
                : paramJson.getInteger(MovieInformationConstant.PAGE_SIZE);
        String conditionMessage = String.format("title=%s,searchFlag=%s,pageSize=%s", title, searchFlag, pageSize);
        try {
            return getData(title, searchFlag, pageSize, requestTime);
        } catch (Exception e) {
            LogUtil.logError(requestTime, conditionMessage, "请求聚合影讯集合接口失败", e);
            return new Result(DataErrorCode.FAIL);
        }
    }

    private Result getData(String title, int searchFlag, Integer pageSize, String requestTime) throws IOException {
        JSONObject queryString = new JSONObject();
        queryString.put(MovieInformationConstant.TITLE, title);
        queryString.put(MovieInformationConstant.SMODE, searchFlag);
        queryString.put(MovieInformationConstant.MOVIE_PAGESIZE, pageSize);
        queryString.put(MovieInformationConstant.KEY, key);
        JSONObject keyString = new JSONObject();
        keyString.put(MovieInformationConstant.TITLE, title);
        keyString.put(MovieInformationConstant.SMODE, searchFlag);
        DispatchRequest dispatchRequest = DispatchRequest.builder()
                .timeOut(timeOut)
                .isEnable(isEnable)
                .okHttpClient(new OkHttpClient())
                .callId(ServiceIdEnum.D004.getId())
                .requestParam(queryString)
                .request(getRequest(queryString))
                .key(KeyUtil.structureKey(keyString, ServiceIdEnum.D004.getId()))
                .build();
        Result result = dispatchService.dispatch(dispatchRequest, true);
        String code = result.getJsonResult().getString(MovieInformationConstant.ERROR_CODE);
        if (DataFromEnum.DATA_FROM_DATASOURCE == result.getDataFrom() && !MovieInformationConstant.SUCCESS.equals(code)) {
            LogUtil.logWarn(requestTime, code, JuheErrorCodeEnum.getDesc(code));
            return new Result(DataErrorCode.DATASOURCE_ERROR);
        }
        return dealQueryResult(result, dispatchRequest);
    }

    private Request getRequest(JSONObject jsonObject) {
        String datasourceUrl = AppendUrlUtil.getURL(jsonObject, url);
        Request request = new Request.Builder().url(datasourceUrl).build();
        return request;
    }

    private Result dealQueryResult(Result result, DispatchRequest dispatchRequest) {
        if (DataFromEnum.DATA_FROM_DATASOURCE == result.getDataFrom()) {
            JSONObject jsonResult = result.getJsonResult();
            jsonResult.remove(MovieInformationConstant.REASON);
            jsonResult.remove(MovieInformationConstant.RESULT_CODE);
            jsonResult.remove(MovieInformationConstant.ERROR_CODE);
            jsonResult.put(MovieInformationConstant.MOVIE_COUNT,
                    jsonResult.getJSONArray(MovieInformationConstant.MOVIE_RESULT).size());
            result.setJsonResult(jsonResult);
            redisService.asyncSet(dispatchRequest.getKey(), jsonResult.toJSONString(), 30 * 24 * 3600);
        }
        return result;
    }
}
