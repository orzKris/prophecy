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
    PARAM_FLAG_ERROR("20000003", "参数flag错误"),
    MESSAGE_NOT_EXIST("20000004", "该通知不存在"),
    TITLE_SIZE_ERROR("20000005", "标题字数不能超过15字"),
    CURRENCY_ERROR("20000006", "充值失败，最大充值金额为1000000"),
    POST_NOT_EXIST("20000007", "该帖子不存在"),
    POST_NOT_MATCH("20000008", "帖子与回复不匹配"),
    USER_ALREADY_EXIST("20000009", "该用户已经注册"),
    LOGIN_ERROR("20000010", "用户名或密码错误"),
    USER_NOT_EXIST("20000011", "该用户不存在");

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
