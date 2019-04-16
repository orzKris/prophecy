package com.kris.prophecy.enums;

import com.kris.prophecy.model.common.exception.ErrorCode;

/**
 * @author Kris
 * @date 2019/4/15
 */
public enum UserErrorCode implements ErrorCode {

    SUCCESS("00", "成功"),
    FAIL("20000001", "失败"),
    PARAM_ERROR("20000002", "参数错误"),
    PARAM_FLAG_ERROR("20000002", "参数flag错误"),
    MESSAGE_NOT_EXIST("20000004", "该通知不存在"),
    CURRENCY_ERROR("20000006", "最大充值金额为1000000");

    private String code;

    private String errorMsg;

    private UserErrorCode(String code, String errorMsg) {
        this.code = code;
        this.errorMsg = errorMsg;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getErrorMsg() {
        return errorMsg;
    }
}
