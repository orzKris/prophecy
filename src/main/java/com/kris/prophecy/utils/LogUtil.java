package com.kris.prophecy.utils;

import lombok.extern.log4j.Log4j2;

import java.util.Optional;

/**
 * 日志规范util
 *
 * @author by Kris on 2018/12/7.
 */
@Log4j2
public class LogUtil {

    public static void logInfo(String uid, String conditionMessage) {
        log.info("[TRACE_INFO]: uid={},{}", uid, conditionMessage);
    }

    public static void logWarn(String uid, String requestTime, String conditionMessage, String errorMessage) {
        log.error("[TRACE_WARN]: uid={},request_time={},{},{} ",
                uid, requestTime, conditionMessage, errorMessage);
    }

    public static void logError(String uid, String requestTime, String conditionMessage, String errorMessage, Exception e) {
        log.error("[TRACE_ERROR]: uid={},request_time={},{},{},{} ",
                uid, requestTime, conditionMessage, errorMessage, Optional.ofNullable(e).orElse(new RuntimeException("查询结果数据异常")));
    }

    public static void logInfoElasticSearch(String uid, long start, String conditionMessage) {
        long end = System.currentTimeMillis();
        log.info("[TRACE_ES]: uid={},request_time={},response_time={},spend {} ms,{}",
                uid, start, end, end - start, conditionMessage);
    }

    public static void logInfo3rd(String uid, String responseBody, long start, String conditionMessage) {
        long end = System.currentTimeMillis();
        log.info("[TRACE_3RD]: uid={},response_body={},request_time={},response_time={},spend {} ms,{}",
                uid, responseBody, start, end, end - start, conditionMessage);
    }

    public static void logInfoRedis(String uid, String responseBody, long start, String conditionMessage) {
        long end = System.currentTimeMillis();
        log.info("[TRACE_REDIS]: uid={},response_body={},request_time={},response_time={},spend {} ms,{}",
                uid, responseBody, start, end, end - start, conditionMessage);
    }

    public static void logInfoMongo(String uid, String responseBody, long start, String conditionMessage) {
        long end = System.currentTimeMillis();
        log.info("[TRACE_MONGO]: uid={},response_body={},request_time={},response_time={},spend {} ms,{}",
                uid, responseBody, start, end, end - start, conditionMessage);
    }
}
