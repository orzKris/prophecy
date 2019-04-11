package com.kris.prophecy.reflect.exception;

/**
 * @author by Kris on 2018/12/11.
 */
public class RejectedException extends RuntimeException {

    public RejectedException() {
        super("访问频率过高!");
    }
}
