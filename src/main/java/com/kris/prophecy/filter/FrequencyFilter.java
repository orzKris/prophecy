package com.kris.prophecy.filter;

import com.alibaba.fastjson.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.TimeUnit;

/**
 * @author by Kris on 8/10/2018.
 */
public class FrequencyFilter implements Filter {

    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void init(FilterConfig filterConfig) {
        ServletContext context = filterConfig.getServletContext();
        ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(context);
        redisTemplate = (RedisTemplate<String, String>) applicationContext.getBean("redisTemplate");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequestWrapper request = new HttpServletRequestWrapper((HttpServletRequest) servletRequest);
        HttpServletResponseWrapper response = new HttpServletResponseWrapper((HttpServletResponse) servletResponse);

        String tokenId = request.getHeader("uid");
        String url = request.getRequestURI();
        Object str = redisTemplate.opsForValue().get(tokenId + url);
        if (null != str) {
            response.setContentType("application/json;charset=UTF-8");
            Writer writer = response.getWriter();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("response_code", "20000012");
            jsonObject.put("message", "每分钟最多能发帖一次");
            writer.write(jsonObject.toJSONString());
            writer.flush();
        } else {
            filterChain.doFilter(request, response);
            redisTemplate.opsForValue().set(tokenId + url, System.currentTimeMillis() + "", 1, TimeUnit.MINUTES);
        }
    }

    @Override
    public void destroy() {
    }

}
