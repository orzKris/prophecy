package com.kris.prophecy.reflect.impl;

import com.kris.prophecy.enums.DataErrorCode;
import com.kris.prophecy.reflect.annotation.InvokeListener;
import com.kris.prophecy.reflect.annotation.ServiceDataMapping;
import com.kris.prophecy.enums.RequestConstant;
import com.kris.prophecy.entity.InterfaceUsage;
import com.kris.prophecy.mapper.InterfaceUsageMapper;
import com.kris.prophecy.mapper.UserMapper;
import com.kris.prophecy.model.MethodOfObject;
import com.kris.prophecy.reflect.InvokeService;
import com.kris.prophecy.utils.LogUtil;
import com.kris.prophecy.reflect.exception.ParamErrorException;
import com.kris.prophecy.reflect.exception.ParamLessException;
import com.kris.prophecy.reflect.exception.RejectedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author by Kris on 2018/12/7.
 */
@Service
public class InvokeServiceImpl implements InvokeService, ApplicationContextAware {

    private Map<String, MethodOfObject> serviceMethodMap = new HashMap<>();

    @Autowired
    InterfaceUsageMapper interfaceUsageMapper;

    @Autowired
    UserMapper userMapper;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> map = applicationContext.getBeansWithAnnotation(InvokeListener.class);
        map.forEach((string, object) -> {
            Method[] methods = object.getClass().getMethods();
            for (Method method : methods) {
                ServiceDataMapping mapping = method.getAnnotation(ServiceDataMapping.class);
                if (mapping != null) {
                    serviceMethodMap.put(mapping.value(), new MethodOfObject(object, method));
                }
            }
        });
    }

    @Override
    public Object serviceInvoke(HttpServletRequest request, String tag, String rid, Object... args) throws Throwable {
        long start = System.currentTimeMillis();
        DateFormat df = new SimpleDateFormat(RequestConstant.DATE_FORMAT_DEFAULT);
        String requestTime = df.format(new Date());
        String uid = request.getHeader(RequestConstant.UID);
        String conditionMessage = String.format("tag=%s,rid=%s", tag, rid);
        InterfaceUsage interfaceUsage = new InterfaceUsage();
        interfaceUsage.setUid(uid);

        if (StringUtils.isBlank(uid)) {
            throw new ParamLessException("缺少用户ID");
        } else if (userMapper.selectByUid(uid) == null) {
            throw new ParamErrorException("该用户不存在!");
        }
        try {
            MethodOfObject mo = serviceMethodMap.get(tag);
            if (mo != null) {
                Object o = mo.invoke(args);
                return o;
            } else {
                throw new ParamErrorException("[tag] is error tag");
            }
        } catch (InvocationTargetException e) {
            return this.exceptionHandler(tag, uid, rid, requestTime, interfaceUsage, e);
        } catch (Exception e) {
            LogUtil.logError(requestTime, conditionMessage, e.getMessage(), e);
            interfaceUsage.setStatus(DataErrorCode.FAIL.getCode());
            interfaceUsage.setFailMessage(e.getMessage().replace("'", ""));
            throw e;
        } finally {
            this.addUsageLog(request, interfaceUsage, start, tag, rid);
        }

    }

    private String exceptionHandler(String tag, String uid, String rid, String start, InterfaceUsage interfaceUsage, InvocationTargetException e) throws Throwable {
        interfaceUsage.setStatus(DataErrorCode.FAIL.getCode());
        Throwable throwable = e.getTargetException();
        String conditionMessage = String.format("tag=%s,rid=%s", tag, rid);

        boolean cause = throwable.getCause() != null &&
                (throwable.getCause() instanceof RejectedException || throwable.getCause().getCause() instanceof RejectedException);
        if (throwable instanceof RejectedException) {
            LogUtil.logError(start, conditionMessage, "访问频率过高!", e);
        } else if (cause) {
            LogUtil.logError(start, conditionMessage, e.getCause().getMessage(), e);
            interfaceUsage.setFailMessage("访问频率过高!");
            throw new RejectedException();
        } else {
            LogUtil.logError(start, conditionMessage, "Service层调用异常", e);
        }
        interfaceUsage.setFailMessage(throwable.getMessage().replace("'", ""));
        throw throwable;
    }

    private void addUsageLog(HttpServletRequest request, InterfaceUsage interfaceUsage, long start, String tag, String rid) {
        String conditionMessage = String.format("tag=%s,rid=%s", tag, rid);
        LogUtil.logInfo(conditionMessage);
        String uri = request.getRequestURI();
        interfaceUsage.setArgs(conditionMessage);
        interfaceUsage.setUri(uri);
        interfaceUsage.setResponseTime(System.currentTimeMillis() - start);
        interfaceUsageMapper.insertSelective(interfaceUsage);
    }

}
