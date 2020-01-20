package com.kris.prophecy.utils;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.constant.TaoBaoBeautyConstant;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.TreeSet;

/**
 * 淘女郎签名工具类
 */
public class BeautySignUtil {

    /**
     * 给请求签名。
     *
     * @param params 所有字符型的请求参数
     * @param secret 签名密钥
     * @return 签名
     */
    public static String signRequest(JSONObject params, String secret) {
        //1.去除file字段 ,同时排序
        TreeSet<String> keys = new TreeSet<>(params.keySet());
        //2.把所有参数名和参数值串在一起
        StringBuilder query = new StringBuilder();
        for (String key : keys) {
            String value = params.get(key) + "";
            if (!StringUtils.isEmpty(key) && !StringUtils.isEmpty(value)) {
                query.append(key).append(value);
            }
        }
        //3.加入密钥和api段
        query.append(secret);
        String result;
        result = Md5Util.MD5(query.toString());
        return result;
    }

    public static String generateTimestamp() {
        DateFormat df = new SimpleDateFormat(TaoBaoBeautyConstant.DATE_TIME_FORMAT);
        df.setTimeZone(TimeZone.getTimeZone(TaoBaoBeautyConstant.DATE_TIMEZONE));
        return df.format(new Date());
    }
}
