package com.kris.prophecy.callable;

import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.constant.*;
import com.kris.prophecy.model.Result;
import com.kris.prophecy.enums.*;
import com.kris.prophecy.model.DispatchRequest;
import com.kris.prophecy.framework.ConcurrentCallable;
import com.kris.prophecy.framework.DispatchService;
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
 * @date 2019/2/1
 */
@Component(ServiceCode.ADDRESS_RESOLUTION)
@Scope("prototype")
@ConfigurationProperties(prefix = "address")
@Data
public class AddressResolutionCallable implements ConcurrentCallable {

    private JSONObject paramJson;

    private String url;

    private String key;

    private boolean isEnable = true;

    @Autowired
    DispatchService dispatchService;

    @Override
    public void init(JSONObject paramJson) {
        this.paramJson = paramJson;
    }

    @Override
    public Result checkParam(JSONObject paramJson) {
        if (!CommonCheckUtil.validateDouble(paramJson.getString(AddressResolutionConstant.LONGITUDE))) {
            return Result.fail("longitude invalid !");
        }
        if (!CommonCheckUtil.validateDouble(paramJson.getString(AddressResolutionConstant.LATITUDE))) {
            return Result.fail("latitude invalid !");
        }
        return Result.success();
    }

    @Override
    public Result call() {
        DateFormat df = new SimpleDateFormat(RequestConstant.DATE_FORMAT_DEFAULT);
        String requestTime = df.format(new Date());
        String longitude = paramJson.getString(AddressResolutionConstant.LONGITUDE);
        String latitude = paramJson.getString(AddressResolutionConstant.LATITUDE);
        String conditionMessage = String.format("longitude=%s,latitude=%s", longitude, latitude);
        try {
            return getData(longitude, latitude, requestTime);
        } catch (Exception e) {
            LogUtil.logError(paramJson.getString(RequestConstant.UID), requestTime, conditionMessage, "请求聚合经纬地址解析接口失败", e);
            return new Result(DataErrorCode.FAIL);
        }
    }

    private Result getData(String longitude, String latitude, String requestTime) throws IOException {
        JSONObject queryString = new JSONObject();
        queryString.put(AddressResolutionConstant.SOURCE_LATITUDE, latitude);
        queryString.put(AddressResolutionConstant.SOURCE_LONGITUDE, longitude);
        queryString.put(AddressResolutionConstant.SOURCE_KEY, key);
        JSONObject keyString = new JSONObject();
        keyString.put(AddressResolutionConstant.SOURCE_LATITUDE, latitude);
        keyString.put(AddressResolutionConstant.SOURCE_LONGITUDE, longitude);
        DispatchRequest dispatchRequest = DispatchRequest.builder()
                .key(KeyUtil.structureKey(keyString, ServiceIdEnum.D002.getId()))
                .request(getRequest(queryString))
                .requestParam(queryString)
                .dsId(ServiceIdEnum.D002.getId())
                .okHttpClient(new OkHttpClient())
                .isEnable(isEnable)
                .build();
        Result result = dispatchService.dispatchDatasource(dispatchRequest, true);
        String code = result.getJsonResult().getString(AddressResolutionConstant.ERROR_CODE);
        if (!code.equals(AddressResolutionConstant.SUCCESS)) {
            LogUtil.logWarn(paramJson.getString(RequestConstant.UID), requestTime, code, JuheErrorCodeEnum.getDesc(code));
            return new Result(DataErrorCode.DATASOURCE_ERROR);
        }
        JSONObject jsonResult = result.getJsonResult().getJSONObject(AddressResolutionConstant.RESULT);
        String address = jsonResult.getString(AddressResolutionConstant.SOURCE_ADDRESS);
        if (StringUtils.isBlank(address)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AddressResolutionConstant.RESPONSE, AddressResolutionConstant.UNKNOWN_AREA);
            result.setJsonResult(jsonObject);
            return result;
        }
        jsonResult.remove(AddressResolutionConstant.SOURCE_LONGITUDE);
        jsonResult.remove(AddressResolutionConstant.SOURCE_LATITUDE);
        result.setJsonResult(jsonResult);
        return result;
    }

    private Request getRequest(JSONObject queryString) {
        String datasourceUrl = AppendUrlUtil.getURL(queryString, url);
        Request request = new Request.Builder().url(datasourceUrl).build();
        return request;
    }
}
