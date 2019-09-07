package com.kris.prophecy.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kris
 * @date 2019/1/31
 */
public class CommonCheckUtil {

    private static final Pattern NUMBER_PATTERN = Pattern.compile("-?[0-9]+.*[0-9]*");

    public static boolean validateString(String text) {
        return StringUtils.isNotBlank(text) && !StringUtils.isNumeric(text);
    }

    public static boolean validateDouble(String s) {
        if (s == null) {
            return false;
        } else {
            Matcher isNum = NUMBER_PATTERN.matcher(s);
            return isNum.matches();
        }
    }

    public static boolean validateNumber(String number) {
        return StringUtils.isNumeric(number);
    }

    public static boolean validateYear(String number) {
        return StringUtils.isNumeric(number) && 1949 < Integer.parseInt(number) && Integer.parseInt(number) < 2018;
    }

    /**
     * 校验手机号是否合法
     *
     * @param mobile 身份证号
     * @return 机号是否合法
     */
    public static Boolean validateMobile(String mobile) {
        return StringUtils.isNotBlank(mobile) && (mobile.length() == 11);
    }

}
