package com.kris.prophecy.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Kris
 * @date 2019/5/10
 */
@SuppressWarnings("all")
public enum BusinessTypeEnum {

    A01("01", "直销银行"),
    A02("02", "消费金融"),
    A03("03", "银行二三类账户开户"),
    A04("04", "征信"),
    A05("05", "保险"),
    A06("06", "基金"),
    A07("07", "证券"),
    A08("08", "租赁"),
    A09("09", "海关申报"),
    A10("99", "其他"),
    ;

    private String businessType;
    private String businessTypeDesc;

    BusinessTypeEnum(String businessType, String businessTypeDesc) {
        this.businessType = businessType;
        this.businessTypeDesc = businessTypeDesc;
    }

    public static boolean hasType(String businessType) {
        if (StringUtils.isBlank(businessType)) {
            return false;
        }

        for (BusinessTypeEnum value : BusinessTypeEnum.values()) {
            if (value.getBusinessType().equalsIgnoreCase(businessType.trim())) {
                return true;
            }
        }
        return false;
    }

    public String getBusinessType() {
        return businessType;
    }

    public String getBusinessTypeDesc() {
        return businessTypeDesc;
    }
}
