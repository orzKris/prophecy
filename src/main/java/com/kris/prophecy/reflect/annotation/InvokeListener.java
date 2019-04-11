package com.kris.prophecy.reflect.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author by Kris on 2018/12/9.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface InvokeListener {
}