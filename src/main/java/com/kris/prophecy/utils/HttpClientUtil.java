package com.kris.prophecy.utils;


import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author by Kris on 2018/12/25.
 */
public class HttpClientUtil {

    public static JSONObject get(String url, Map<String, String> params) throws Exception {
        String requestUrl = urlConcatenation(url, params);
        String response = HttpHandler.get(requestUrl);
        if (response != null) {
            return JSONObject.parseObject(response);
        }
        return new JSONObject();
    }

    public static JSONObject post(String url, Map<String, String> params) throws Exception {
        String requestUrl = urlConcatenation(url, params);
        String response = HttpHandler.post(requestUrl, params);
        if (response != null) {
            return JSONObject.parseObject(response);
        }
        return new JSONObject();
    }

    /**
     * get请求url拼接
     *
     * @param url    URL
     * @param params 参数
     * @return 拼接结果
     */
    public static String urlConcatenation(String url, Map<String, String> params) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(url);
        if (url == null || url.equals("")) {
            return "";
        }
        if (params == null || params.isEmpty()) {
            return url;
        }
        stringBuilder.append("?");
        int length = params.size();
        int count = 1;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            stringBuilder.append(entry.getKey());
            stringBuilder.append("=");
            stringBuilder.append(entry.getValue());
            if (count < length) {
                stringBuilder.append("&");
                count++;
            }
        }
        return stringBuilder.toString();
    }

    /**
     * url拼接
     *
     * @param param 参数
     * @return 拼接结果
     */
    public static Map<String, String> urlParamtoMap(String param) {

        if (param == null || param.equals("") || !param.contains("=")) {
            return null;
        }

        Map<String, String> map = new HashMap<>();
        String[] paramS = param.split("&");
        for (String str : paramS) {
            String[] params = str.split("=");
            map.put(params[0], params[1]);
        }
        return map;
    }
}
