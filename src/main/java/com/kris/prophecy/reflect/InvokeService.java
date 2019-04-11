package com.kris.prophecy.reflect;

import javax.servlet.http.HttpServletRequest;

/**
 * @author by Kris on 2018/12/7.
 */
public interface InvokeService {

    /**
     * 请求统一映射管理
     *
     * @param request
     * @param tag
     * @return
     */
    Object serviceInvoke(HttpServletRequest request, String tag, String rid, Object... args) throws Throwable;
}
