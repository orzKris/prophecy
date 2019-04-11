package com.kris.prophecy.reflect.annotation;

import java.lang.annotation.*;

/**
 * @author by Kris on 2018/12/9.
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceDataMapping {
    String value();
}
