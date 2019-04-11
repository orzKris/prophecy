package com.kris.prophecy;

import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.utils.AppendUrlUtil;
import okhttp3.*;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Kris
 * @date 2019/4/7
 */
public class DataOutputScript {

    private final static String HOME_PATH = "xxx";

    private final static String COMPANY_PATH = "xxx";

    public static Request getRequest(JSONObject param) {
        return new Request.Builder()
                .url(AppendUrlUtil.getURL(param, "http://127.0.0.1:8888/concurrent/D005"))
                .post(new FormBody.Builder()
                        .add("param", param.toJSONString())
                        .build())
                .header("uid", "0be1e377-50c4-4dc6-9100-9c3c7a734ca8")
                .build();
    }

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(new FileInputStream(COMPANY_PATH + "手机号100.xls"));
        HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            HSSFRow row = sheet.getRow(i);
            String mobile = row.getCell(0).getStringCellValue();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("mobile", mobile);
            JSONObject param = new JSONObject();
            param.put("param", jsonObject.toJSONString());
            Request request = getRequest(param);
            Call call = new OkHttpClient().newCall(request);
            Response response = call.execute();
            String responseBody = response.body().string();
            System.out.println(mobile + ": " + responseBody);
            row.createCell(1).setCellValue(responseBody);
        }
        FileOutputStream fileOutputStream = new FileOutputStream(COMPANY_PATH + "手机号归属地.xls");
        hssfWorkbook.write(fileOutputStream);
        fileOutputStream.flush();
        System.out.println("cost: " + (System.currentTimeMillis() - start) + "ms");
    }
}
