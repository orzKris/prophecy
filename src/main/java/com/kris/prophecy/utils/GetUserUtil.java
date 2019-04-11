package com.kris.prophecy.utils;

import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
 * @author by Kris on 2018/12/12.
 */
public class GetUserUtil {

    public static final String GRADUATION_SCHOOL = "电子科技大学";

    public static final String MALE = "男";

    public static final String FEMALE = "女";

    /**
     * 较常见的百家姓
     */
    public static final String[] SUR_NAME = {"赵", "钱", "孙", "李", "周", "吴", "郑", "王", "冯", "陈", "褚", "卫", "蒋", "沈", "韩", "杨", "朱", "秦", "尤",
            "何", "吕", "施", "张", "孔", "曹", "严", "华", "金", "魏", "陶", "姜", "戚", "谢", "邹", "喻", "柏", "水", "窦", "章", "云", "苏", "潘", "葛",
            "鲁", "韦", "昌", "马", "苗", "凤", "花", "方", "俞", "任", "袁", "柳", "鲍", "史", "唐", "费", "廉", "岑", "薛", "雷", "贺", "倪", "汤", "殷",
            "罗", "毕", "郝", "安", "常", "乐", "于", "时", "傅", "皮", "卞", "齐", "康", "伍", "余", "元", "卜", "顾", "孟", "黄", "和", "彭", "许", "范",
            "穆", "萧", "尹", "姚", "邵", "汪", "祁", "毛", "禹", "狄", "米", "贝", "明", "臧", "计", "伏", "成", "戴", "宋", "茅", "庞", "熊", "纪", "舒",
            "屈", "项", "祝", "董", "梁", "杜", "阮", "蓝", "闵", "席", "季", "麻", "强", "贾", "路", "娄", "江", "童", "颜", "郭", "梅", "盛", "林", "钟",
            "徐", "邱", "骆", "高", "夏", "蔡", "田", "樊", "胡", "凌", "霍", "虞", "万", "支", "柯", "管", "卢", "莫", "经", "房", "裘", "解", "应"};

    public static final String[] SUBORDINATE_CLASS = {"信安一班", "信安二班", "信安三班", "信安四班", "信安五班", "信安六班", "信安七班", "信安八班", "信安九班", "计科一班",
            "计科二班", "计科三班", "计科四班", "计科五班", "计科六班", "计科七班", "计科八班", "计科九班", "通信一班", "通信二班", "通信三班", "通信四班", "通信五班"};

    public static String getChinese() {
        String str = null;
        int highPos, lowPos;
        Random random = new Random();
        /**
         * 区码，0xA0打头，从第16区开始，即0xB0=11*16=176,16~55一级汉字，56~87二级汉字
         */
        highPos = (176 + Math.abs(random.nextInt(71)));
        random = new Random();
        /**
         * 位码，0xA0打头，范围第1~94列
         */
        lowPos = 161 + Math.abs(random.nextInt(94));

        byte[] bArr = new byte[2];
        bArr[0] = (new Integer(highPos)).byteValue();
        bArr[1] = (new Integer(lowPos)).byteValue();
        try {
            /**
             * 区位码组合成汉字
             */
            str = new String(bArr, "GB2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String genRandomNum() {
        int maxNum = 36;
        int i;
        int count = 0;
        char[] str = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
                'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
                'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        StringBuffer pwd = new StringBuffer("");
        Random r = new Random();
        while (count < 8) {
            i = Math.abs(r.nextInt(maxNum));
            if (i >= 0 && i < str.length) {
                pwd.append(str[i]);
                count++;
            }
        }
        return pwd.toString();
    }
}
