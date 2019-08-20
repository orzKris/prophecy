package com.kris.prophecy.script;

import com.alibaba.fastjson.JSONObject;
import com.kris.prophecy.utils.AppendUrlUtil;
import okhttp3.*;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author Kris
 * @date 2019/4/7
 */
public class DataOutputScript {

    private final static String PATH = "/home/excel/";

    private static ExecutorService threadPool = Executors.newFixedThreadPool(200);

    public static Request getRequest(JSONObject param) throws UnsupportedEncodingException {
        return new Request.Builder()
                .url("http://localhost:8888/concurrent/D005?param=" + URLEncoder.encode(param.toJSONString(), "UTF-8"))
                .post(new FormBody.Builder().build())
                .build();
    }

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        List<Future> futureList = new ArrayList<>();
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(new FileInputStream(PATH + "手机号1000.xls"));
        HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            HSSFRow row = sheet.getRow(i);
            String mobile = row.getCell(0).getStringCellValue();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("mobile", mobile);
            MyCallable callable = new MyCallable(jsonObject);
            futureList.add(threadPool.submit(callable));
        }
        threadPool.shutdown();

        int i = 1;
        for (Future future : futureList) {
            String result = null;
            try {
                result = (String) future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            sheet.getRow(i).getCell(1, Row.CREATE_NULL_AS_BLANK).setCellValue(result);
            System.out.println(sheet.getRow(i).getCell(0).getStringCellValue() + " : " + result);
            i = i + 1;
        }
        FileOutputStream fileOutputStream = new FileOutputStream(PATH + "手机号归属地.xls");
        hssfWorkbook.write(fileOutputStream);
        fileOutputStream.flush();
        System.out.println("cost: " + (System.currentTimeMillis() - start) + "ms");
    }

    static class MyCallable implements Callable<String> {

        private JSONObject param;

        MyCallable(JSONObject param) {
            this.param = param;
        }

        @Override
        public String call() throws IOException {
            Request request = getRequest(param);
            Call call = new OkHttpClient.Builder().callTimeout(10000, TimeUnit.MILLISECONDS).build().newCall(request);
            Response response = call.execute();
            return response.body().string();
        }
    }
}
