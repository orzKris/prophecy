package com.kris.prophecy.enums;

import java.util.Arrays;

/**
 * 聚合数据数据源错误码
 *
 * @author Kris
 * @date 2019/3/1
 */
@SuppressWarnings("all")
public enum JuheErrorCodeEnum {

    E228000("网络超时"),
    E220801("服务商网络异常,请重试"),
    E220802("查询失败"),
    E220803("库中无此纪录"),
    E220807("参数错误"),
    E201501("参数错误"),
    E10001("错误的请求KEY"),
    E10002("KEY无请求权限"),
    E10003("KEY过期"),
    E10004("错误的OPENID"),
    E10005("应用未审核超时,请提交认证"),
    E10007("未知的请求源"),
    E10008("被禁止的IP"),
    E10009("被禁止的KEY"),
    E10011("当前IP请求超过限制"),
    E10012("请求超过次数限制"),
    E10013("测试KEY超过请求限制"),
    E10014("系统内部异常(调用充值类业务时,请务必联系客服或通过订单查询接口检测订单,避免造成损失)"),
    E10020("接口维护"),
    E10021("接口停用"),
    UNKNOWN("数据源返回的错误码未定义");

    String desc;

    private JuheErrorCodeEnum(String desc) {
        this.desc = desc;
    }

    public static String getDesc(String code) {
        return Arrays.stream(JuheErrorCodeEnum.values()).filter(juheErrorCodeEnum -> juheErrorCodeEnum.name().endsWith(code)).findFirst().orElse(JuheErrorCodeEnum.UNKNOWN).desc;
    }
}