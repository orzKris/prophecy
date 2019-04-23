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

    public static void logInfo(String conditionMessage) {
        log.info("[TRACE_INFO]: {}", conditionMessage);
    }

    public static void logWarn(String requestTime, String conditionMessage, String errorMessage) {
        log.error("[TRACE_WARN]: request_time={},{},{} ", requestTime, conditionMessage, errorMessage);
    }

    public static void logError(String requestTime, String conditionMessage, String errorMessage, Exception e) {
        log.error("[TRACE_ERROR]: request_time={},{},{},{} ", requestTime, conditionMessage, errorMessage, e);
    }

    public static void logInfoElasticSearch(long start, String conditionMessage) {
        long end = System.currentTimeMillis();
        log.info("[TRACE_ES]: request_time={},spend {} ms,{}", start, end - start, conditionMessage);
    }

    public static void logInfo3rd(String responseBody, long start, String conditionMessage) {
        long end = System.currentTimeMillis();
        log.info("[TRACE_3RD]: response_body={},request_time={},response_time={},spend {} ms,{}",
                responseBody, start, end, end - start, conditionMessage);
    }

    public static void logInfo3rdFallBack(String conditionMessage) {
        log.info("[TRACE_3RD_FALLBACK]: {}", conditionMessage);
    }

    public static void logInfoRedis(String responseBody, long start, String conditionMessage) {
        long end = System.currentTimeMillis();
        log.info("[TRACE_REDIS]: response_body={},request_time={},response_time={},spend {} ms,{}",
                responseBody, start, end, end - start, conditionMessage);
    }

    public static void logInfoRedisFallBack(String conditionMessage) {
        log.info("[TRACE_REDIS_FALLBACK]:{}", conditionMessage);
    }

    public static void logInfoMongo(String responseBody, long start, String conditionMessage) {
        long end = System.currentTimeMillis();
        log.info("[TRACE_MONGO]: response_body={},request_time={},spend {} ms,{}", responseBody, start, end - start, conditionMessage);
    }
}
