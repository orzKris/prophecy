package com.kris.prophecy.utils;

import com.alibaba.fastjson.JSONObject;

import java.util.stream.Collectors;

/**
 * @author Kris
 * Date: 2018/12/19
 */
public class KeyUtil {

    private KeyUtil() {
    }

    private static final String PREFIX = "hy:";

    /**
     * 获取并MD5加密key
     */
    public static String structureKey(JSONObject params, String interfaceId) {
        return PREFIX + interfaceId + ":" + Md5Util.MD5(
                params.keySet()
                        .stream()
                        .map(String::toString)
                        .sorted()
                        .map(params::getString)
                        .collect(Collectors.joining("_"))
        );
    }

    /**
     * 直接MD5加密字符串
     */
    public static String structureKeyString(String params, String interfaceId) {
        return PREFIX + interfaceId + ":" + Md5Util.MD5(params);
    }
}
