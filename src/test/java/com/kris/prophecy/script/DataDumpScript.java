package com.kris.prophecy.script;


import com.alibaba.fastjson.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * @author Kris
 * @date 2019/4/3
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DataDumpScript {

    @Autowired
    private TestRestTemplate template;

    private HttpEntity<String> requestEntity;

    private final static String PATH = "xxx";

    @Before
    public void init() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("uid", "0be1e377-50c4-4dc6-9100-9c3c7a734ca8");
        requestEntity = new HttpEntity<>(headers);
    }

    @Test
    public void dumpData() throws IOException {
        long start = System.currentTimeMillis();
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(new FileInputStream(PATH + "手机号1000.xls"));
        HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            HSSFRow row = sheet.getRow(i);
            String mobile = row.getCell(0).getStringCellValue();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("mobile", mobile);
            String param = jsonObject.toJSONString();
            String requestUrl = "/concurrent/D005?param={param}";
            ResponseEntity<JSONObject> response = template.exchange(
                    requestUrl, HttpMethod.POST, requestEntity, JSONObject.class, param);
            JSONObject responseBody = response.getBody();
            System.out.println(mobile + ": " + responseBody.toJSONString());
            row.createCell(1).setCellValue(responseBody.toJSONString());
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(PATH + "手机号归属地.xls");
            hssfWorkbook.write(fileOutputStream);
            fileOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("cost: " + (System.currentTimeMillis() - start) + "ms");
    }
}
