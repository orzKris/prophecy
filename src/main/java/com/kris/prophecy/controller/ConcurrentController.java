package com.kris.prophecy.controller;

import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.config.ApplicationContextRegister;
import com.kris.prophecy.enums.DataErrorCode;
import com.kris.prophecy.enums.CommonConstant;
import com.kris.prophecy.model.Result;
import com.kris.prophecy.model.CallMap;
import com.kris.prophecy.framework.ConcurrentCallable;
import com.kris.prophecy.model.common.util.Response;
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
 *
 * CompletionService与ExecutorService最主要的区别在于submit的task不一定是按照加入时的顺序完成的。
 * CompletionService对ExecutorService进行了包装，内部维护一个保存Future对象的BlockingQueue。
 * 只有当这个Future对象状态是结束的时候，才会加入到这个Queue中，take()方法其实就是Producer-Consumer中的Consumer。
 * 它会从Queue中取出Future对象，如果Queue是空的，就会阻塞在那里，直到有完成的Future对象加入到Queue中。
 * 所以，先完成的必定先被取出。这样就减少了不必要的等待时间。
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
    public Response main(HttpServletRequest request, @PathVariable("id") String id, @RequestParam("param") String param) {
        long start = System.currentTimeMillis();
        DateFormat df = new SimpleDateFormat(CommonConstant.DATE_FORMAT_DEFAULT);
        String requestTime = df.format(new Date());
        String uid = request.getHeader(CommonConstant.UID);

        Response paramResult = checkParam(param, requestTime);
        if (!paramResult.getResponseCode().equals(DataErrorCode.SUCCESS.getCode())) {
            return paramResult;
        }
        JSONObject paramJson = (JSONObject) paramResult.getResult();
        if (paramJson.containsKey(CommonConstant.FILE_UPLOAD)) {
            InputStream inputStream = obtainFile(request, requestTime);
            paramJson.put(CommonConstant.FILE, inputStream);
        }
        String serviceName = callMap.getMap().get(id);
        if (serviceName == null) {
            return Response.error(DataErrorCode.NO_CONFIGURED_SERVICE);
        }
        paramJson.put(CommonConstant.UID, uid);
        LogUtil.logInfo("调用的服务有：" + serviceName);
        //执行配置的服务
        CompletionService<Result> executorCompletionService = new ExecutorCompletionService<>(SCHEDULED_EXECUTOR_SERVICE);
        ConcurrentCallable concurrentCallable = (ConcurrentCallable) applicationContextRegister.getApplicationContext().getBean(serviceName);
        concurrentCallable.init(paramJson);
        Result checkResult = concurrentCallable.checkParam(paramJson);
        if (DataErrorCode.PARAM_ERROR.equals(checkResult.getStatus())) {
            checkResult.setName(serviceName);
            return new Response<>(checkResult.getStatus().getCode(), checkResult.getStatus().getErrorMsg(), checkResult.toJson());
        }
        Future<Result> future = executorCompletionService.submit(concurrentCallable);
        Result result = new Result();
        try {
            result = future.get(20000, TimeUnit.MILLISECONDS);
            result.setName(serviceName);
            return new Response<>(result.getStatus().getCode(), result.getStatus().getErrorMsg(), result.toJson());

        } catch (TimeoutException e) {
            return Response.error(DataErrorCode.DISPATCH_TIMEOUT);
        } catch (Exception e) {
            return Response.error(DataErrorCode.DISPATCH_ERROR);
        } finally {
            interfaceUsageService.insertInterfaceUsage(start, request.getRequestURI(), param, uid, result.getStatus().getCode());
        }
    }

    private Response checkParam(String param, String requestTime) {
        if (StringUtils.isBlank(param)) {
            return Response.error(DataErrorCode.PARAM_ERROR);
        }
        param = param.replaceAll(" ", "");

        try {
            return Response.ok(JSONObject.parseObject(param));
        } catch (Exception e) {
            LogUtil.logError(requestTime, param, CommonConstant.PARAM_MUST_BE_JSON, e);
            return Response.error(DataErrorCode.PARAM_ERROR);
        }
    }

    private InputStream obtainFile(HttpServletRequest request, String requestTime) {
        InputStream inputStream = null;
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile multipartFile = multipartRequest.getFile("file");
            if (multipartFile != null) {
                inputStream = multipartFile.getInputStream();
            }
        } catch (Exception e) {
            LogUtil.logError(requestTime, request.toString(), CommonConstant.FILE_UPLOAD_FAIL, e);
        }
        return inputStream;
    }
}
