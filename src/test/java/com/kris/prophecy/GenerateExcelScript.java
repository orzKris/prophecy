package com.kris.prophecy;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Kris
 * @date 2019/4/3
 */
public class GenerateExcelScript {

    private final static int EXCEL_SIZE = 1000;

    private final static String HOME_PATH = "xxx";

    private final static String COMPANY_PATH = "xxx";

    public static void main(String[] args) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("手机号归属地");
        HSSFRow beginRow = sheet.createRow(0);
        HSSFCell beginCell0 = beginRow.createCell(0);
        HSSFCell beginCell1 = beginRow.createCell(1);
        beginCell0.setCellValue("手机号");
        beginCell1.setCellValue("归属地");
        int index = 1;
        for (int i = 0; i <= EXCEL_SIZE; i++) {
            //填写Excel第一列的Collection名
            HSSFRow row = sheet.createRow(index);
            HSSFCell cell = row.createCell(0);
            cell.setCellValue((long) (Math.random() * 10000000000L) + 10000000000L + "");
            index = index + 1;
        }
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(COMPANY_PATH + "手机号1000.xls");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        workbook.write(output);
        output.flush();
    }
}
