package com.kris.prophecy.utils;

import com.alibaba.fastjson.JSONObject;

import java.util.Iterator;

/**
 * @author Kris
 * @date 2019/1/7
 */
public class AppendUrlUtil {

    /**
     * 组装GET方法url
     *
     * @param queryStringJson
     * @return
     */
    public static String getURL(JSONObject queryStringJson, String datasourceURL) {
        StringBuilder queryString = new StringBuilder();
        if (queryStringJson != null && queryStringJson.size() > 0) {
            Iterator var3 = queryStringJson.keySet().iterator();

            while (var3.hasNext()) {
                String key = (String) var3.next();
                queryString.append(String.format("%s=%s&", key, queryStringJson.getString(key)));
            }
        }
        String url = String.format("%s?%s", datasourceURL, queryString.toString());
        return url.substring(0, url.length() - 1);
    }
}
