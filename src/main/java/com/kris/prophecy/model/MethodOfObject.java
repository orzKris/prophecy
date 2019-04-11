package com.kris.prophecy.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author by Kris on 2018/12/9.
 */
public class MethodOfObject {

    private Object object;
    private Method method;

    public MethodOfObject(Object object, Method method) {
        this.object = object;
        this.method = method;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object invoke(Object... args) throws InvocationTargetException, IllegalAccessException {
        return method.invoke(object, args);
    }
}
