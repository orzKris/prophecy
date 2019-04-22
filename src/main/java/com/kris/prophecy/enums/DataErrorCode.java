package com.kris.prophecy.enums;


import com.kris.prophecy.model.common.exception.ErrorCode;

/**
 * @author Kris
 * @date 2019/4/15
 */
public enum DataErrorCode implements ErrorCode {

    SUCCESS("00", "成功"),
    FAIL("30000001", "失败"),
    NO_CONTENT("30000008", "没有记录"),
    PARAM_ERROR("30000002", "参数错误"),
    NO_CONFIGURED_SERVICE("30000003", "没有匹配的服务"),
    DISPATCH_TIMEOUT("30000004", "调度服务超时"),
    DISPATCH_ERROR("30000005", "调度服务处理异常"),
    DATASOURCE_ERROR("30000006", "数据源错误"),
    INTERFACE_FORBIDDEN("30000007", "接口已禁用");

    private String code;

    private String errorMsg;

    private DataErrorCode(String code, String errorMsg) {
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
