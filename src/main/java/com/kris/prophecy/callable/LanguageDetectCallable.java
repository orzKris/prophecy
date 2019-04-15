package com.kris.prophecy.callable;

import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.constant.*;
import com.kris.prophecy.enums.*;
import com.kris.prophecy.framework.MongoService;
import com.kris.prophecy.model.DataCenter;
import com.kris.prophecy.model.Result;
import com.kris.prophecy.framework.ConcurrentCallable;
import com.kris.prophecy.utils.CommonCheckUtil;
import com.kris.prophecy.utils.HttpClientUtil;
import com.kris.prophecy.utils.KeyUtil;
import com.kris.prophecy.utils.LogUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * @author by Kris on 2018/12/21.
 */
@Component(ServiceCode.LANGUAGE_DETECT)
@Scope("prototype")
@ConfigurationProperties(prefix = "language")
@Data
public class LanguageDetectCallable implements ConcurrentCallable {

    @Autowired
    MongoService mongoService;

    private JSONObject paramJson;

    private String url;

    @Override
    public void init(JSONObject paramJson) {
        this.paramJson = paramJson;
    }

    @Override
    public Result checkParam(JSONObject paramJson) {
        if (!CommonCheckUtil.validateString(paramJson.getString(LanguageDetectConstant.TEXT))) {
            return Result.fail("text invalid !");
        }
        return Result.success();
    }

    @Override
    public Result call() {
        DateFormat df = new SimpleDateFormat(RequestConstant.DATE_FORMAT_DEFAULT);
        String requestTime = df.format(new Date());
        String text = paramJson.getString(LanguageDetectConstant.TEXT);
        Map<String, String> map = new HashMap<>(1);
        map.put(LanguageDetectConstant.QUERY, text);
        String uid = paramJson.getString(RequestConstant.UID);
        long start = System.currentTimeMillis();

        Result result = new Result(LocalErrorCode.SUCCESS);
        try {
            JSONObject jsonResult = HttpClientUtil.get(url, map);
            String lang = dictionaryConversion(jsonResult);
            jsonResult.put(LanguageDetectConstant.LANGUAGE_TYPE, lang);
            result.setJsonResult(jsonResult);
            LogUtil.logInfo3rd(uid, result.getJsonResult().toString(), start, text);
            result.setDataFrom(DataFromEnum.DATA_FROM_DATASOURCE);
            return result;
        } catch (Exception e) {
            LogUtil.logError(uid, requestTime, text, "请求百度语言识别接口失败", e);
            return new Result(LocalErrorCode.FAIL);
        } finally {
            paramJson.remove(RequestConstant.UID);
            String id = KeyUtil.structureKey(paramJson, ServiceIdEnum.D001.getId());
            mongoService.asyncInsert(new DataCenter(id, result.getJsonResult().toJSONString()));
        }
    }

    private String dictionaryConversion(JSONObject jsonObject) {
        switch (jsonObject.getString(LanguageDetectConstant.LANGUAGE_TYPE)) {
            case "en":
                return "英语";
            case "zh":
                return "汉语";
            case "jp":
                return "日语";
            case "fra":
                return "法语";
            case "ru":
                return "俄语";
            case "spa":
                return "西班牙语";
            case "kor":
                return "韩语";
            case "ara":
                return "阿拉伯语";
            default:
                return "无法识别";
        }
    }
}
