package com.kris.prophecy.controller;

import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.config.ApplicationContextRegister;
import com.kris.prophecy.enums.RequestConstant;
import com.kris.prophecy.enums.ResponseConstant;
import com.kris.prophecy.model.Result;
import com.kris.prophecy.model.CallMap;
import com.kris.prophecy.framework.ConcurrentCallable;
import com.kris.prophecy.service.InterfaceUsageService;
import com.kris.prophecy.utils.LogUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;

/**
 * @author by Kris on 2018/12/19.
 * 请求统一管理接口
 */
@RequestMapping(value = "/concurrent")
@RestController
@Scope("request")
public class ConcurrentController {

    @Autowired
    private CallMap callMap;

    @Autowired
    ApplicationContextRegister applicationContextRegister;

    @Autowired
    InterfaceUsageService interfaceUsageService;

    /**
     * 手动创建线程池
     */
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = new ScheduledThreadPoolExecutor(10,
            new BasicThreadFactory.Builder().namingPattern("Schedule-thread-pool-%d").daemon(true).build());

    @PostMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JSONObject main(HttpServletRequest request, @PathVariable("id") String id) {
        long start = System.currentTimeMillis();
        DateFormat df = new SimpleDateFormat(RequestConstant.DATE_FORMAT_DEFAULT);
        String requestTime = df.format(new Date());
        String param = request.getParameter(RequestConstant.PARAM);
        String uid = request.getHeader(RequestConstant.UID);

        Result paramResult = checkParam(param, uid, requestTime);
        if (!paramResult.getStatus().equals(ResponseConstant.SUCCESS)) {
            return paramResult.toJson();
        }
        JSONObject paramJson = paramResult.getJsonResult();
        if (paramJson.containsKey(RequestConstant.FILE_UPLOAD)) {
            InputStream inputStream = obtainFile(request, uid, requestTime);
            paramJson.put(RequestConstant.FILE, inputStream);
        }
        String serviceName = callMap.getMap().get(id);
        if (serviceName == null) {
            return new Result(RequestConstant.NO_CONFIGURED_SERVICE).toJson();
        }
        paramJson.put(RequestConstant.UID, uid);
        LogUtil.logInfo(uid, "调用的服务有：" + serviceName);
        //执行配置的服务
        CompletionService<Result> executorCompletionService = new ExecutorCompletionService<>(SCHEDULED_EXECUTOR_SERVICE);
        ConcurrentCallable concurrentCallable = (ConcurrentCallable) applicationContextRegister.getApplicationContext().getBean(serviceName);
        concurrentCallable.init(paramJson);
        Result checkResult = concurrentCallable.checkParam(paramJson);
        if (ResponseConstant.PARAM_ERROR.equals(checkResult.getStatus())) {
            checkResult.setName(serviceName);
            return checkResult.toJson();
        }
        Future<Result> future = executorCompletionService.submit(concurrentCallable);
        Result result = new Result();
        try {
            result = future.get(20000, TimeUnit.MILLISECONDS);
            result.setName(serviceName);
            LogUtil.logInfo(uid, result.getJsonResult().toJSONString());
            return result.toJson();
        } catch (TimeoutException e) {
            return new Result(serviceName, ResponseConstant.DISPATCH_TIMEOUT).toJson();
        } catch (Exception e) {
            result = new Result(serviceName, ResponseConstant.FAIL);
            JSONObject jsonResult = new JSONObject();
            jsonResult.put(ResponseConstant.RESPONSE, ResponseConstant.DISPATCH_ERROR);
            result.setJsonResult(jsonResult);
            LogUtil.logError(uid, requestTime, param, RequestConstant.SERVICE_PROCESS_ERROR, e);
            return result.toJson();
        } finally {
            interfaceUsageService.insertInterfaceUsage(start, request.getRequestURI(), param, uid, result.getStatus());
        }
    }

    private Result checkParam(String param, String uid, String requestTime) {
        JSONObject paramJson = new JSONObject();
        Result result = new Result(ResponseConstant.SUCCESS);
        if (StringUtils.isBlank(param)) {
            paramJson.put(ResponseConstant.RESPONSE, RequestConstant.PARAM_LESS);
            return new Result(ResponseConstant.PARAM_ERROR, paramJson);
        }
        param = param.replaceAll(" ", "");
        try {
            paramJson = JSONObject.parseObject(param);
            result.setJsonResult(paramJson);
            return result;
        } catch (Exception e) {
            LogUtil.logError(uid, requestTime, param, RequestConstant.PARAM_MUST_BE_JSON, e);
            paramJson.put(ResponseConstant.RESPONSE, RequestConstant.PARAM_MUST_BE_JSON);
            return new Result(ResponseConstant.PARAM_ERROR, paramJson);
        }
    }

    private InputStream obtainFile(HttpServletRequest request, String uid, String requestTime) {
        InputStream inputStream = null;
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile multipartFile = multipartRequest.getFile("file");
            inputStream = multipartFile.getInputStream();
        } catch (Exception e) {
            LogUtil.logError(uid, requestTime, request.toString(), RequestConstant.FILE_UPLOAD_FAIL, e);
        }
        return inputStream;
    }
}
