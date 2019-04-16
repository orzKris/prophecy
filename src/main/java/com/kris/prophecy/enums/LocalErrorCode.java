package com.kris.prophecy.enums;


import com.kris.prophecy.model.common.exception.ErrorCode;

/**
 * @author Kris
 * @date 2019/4/15
 */
public enum LocalErrorCode implements ErrorCode {

    SUCCESS("00", "成功"),
    FAIL("10000001", "失败"),
    PARAM_ERROR("10000002", "参数错误"),
    NO_CONFIGURED_SERVICE("10000003", "没有匹配的服务"),
    DISPATCH_TIMEOUT("10000004", "调度服务超时"),
    DISPATCH_ERROR("10000005", "调度服务处理异常"),
    DATASOURCE_ERROR("10000006", "数据源错误"),
    INTERFACE_FORBIDDEN("10000007", "接口已禁用")
    ;

    private String code;

    private String errorMsg;

    private LocalErrorCode(String code, String errorMsg) {
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
