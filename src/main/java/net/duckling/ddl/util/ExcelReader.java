/*
 * Copyright (c) 2008-2016 Computer Network Information Center (CNIC), Chinese Academy of Sciences.
 * 
 * This file is part of Duckling project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 *
 */
package net.duckling.ddl.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
/**
 * 读取Excel
 */
public class ExcelReader {
    private Logger logger = LogManager.getLogger(ExcelReader.class);
    private Workbook wb;
    private Sheet sheet;
    private Row row;
 
    public ExcelReader(InputStream is, String ext) throws IOException {
    	initWorkbook(is, ext);
    }
    
    public ExcelReader(String filepath) throws IOException {
        if(filepath==null){
            return;
        }
        String ext = filepath.substring(filepath.lastIndexOf(".")+1);
        InputStream is = null;
        try {
            is = new FileInputStream(filepath);
        } catch (FileNotFoundException e) {
            logger.error("FileNotFoundException", e);
        } 
        initWorkbook(is, ext);
    }
    
    private void initWorkbook(InputStream is, String ext) throws IOException{
    	if("XLS".equals(ext.toUpperCase())){
            wb = new HSSFWorkbook(is);
        }else if("XLSX".equals(ext.toUpperCase())){
            wb = new XSSFWorkbook(is);
        }
    }
     
    /**
     * 读取Excel表格表头的内容
     * 
     * @param InputStream
     * @return String 表头内容的数组
     */
    public String[] readExcelTitle() throws Exception{
        if(wb==null){
            throw new Exception("initialized Workbook error.");
        }
        sheet = wb.getSheetAt(0);
        row = sheet.getRow(0);
        // 标题总列数
        int colNum = row.getPhysicalNumberOfCells();
        System.out.println("colNum:" + colNum);
        String[] title = new String[colNum];
        for (int i = 0; i < colNum; i++) {
            // title[i] = getStringCellValue(row.getCell((short) i));
            title[i] = row.getCell(i).getCellFormula();
        }
        return title;
    }
 
    /**
     * 读取Excel数据内容
     * 
     * @param InputStream
     * @return Map 包含单元格数据内容的Map对象
     */
    public List<Map<Integer,Object>> readExcelContent() throws Exception{
        if(wb==null){
            throw new Exception("Workbook对象为空！");
        }
        List<Map<Integer,Object>> content = new ArrayList<Map<Integer,Object>>();
         
        sheet = wb.getSheetAt(0);
        // 得到总行数
        int rowNum = sheet.getLastRowNum();
        row = sheet.getRow(0);
        int colNum = row.getPhysicalNumberOfCells();
        for (int i = 0; i <= rowNum; i++) {
            row = sheet.getRow(i);
            int j = 0;
            Map<Integer,Object> cellValue = new LinkedHashMap<Integer, Object>();
            while (j < colNum) {
                Object obj = getCellFormatValue(row.getCell(j));
                cellValue.put(j, obj);
                j++;
            }
            content.add(cellValue);
        }
        return content;
    }
 
    /**
     * 
     * 根据Cell类型设置数据
     * 
     * @param cell
     * @return
     */
    private Object getCellFormatValue(Cell cell) {
        Object cellvalue = "";
        if (cell != null) {
            // 判断当前Cell的Type
            switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC:// 如果当前Cell的Type为NUMERIC
            case Cell.CELL_TYPE_FORMULA: {
                // 判断当前的cell是否为Date
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    cellvalue = date;
                } else {// 如果是纯数字
                    // 取得当前Cell的数值
                    cellvalue = String.valueOf(cell.getNumericCellValue());
                }
                break;
            }
            case Cell.CELL_TYPE_STRING:// 如果当前Cell的Type为STRING
                // 取得当前的Cell字符串
                cellvalue = cell.getRichStringCellValue().getString();
                break;
            default:// 默认的Cell值
                cellvalue = "";
            }
        } else {
            cellvalue = "";
        }
        return cellvalue;
    }
 
    public static void main(String[] args) throws Exception {
        String filepath = "D:/test.xlsx";
        ExcelReader excelReader = new ExcelReader(filepath);
        // 对读取Excel表格标题
        //String[] title = excelReader.readExcelTitle();
         
        // 对读取Excel表格内容测试
        List<Map<Integer,Object>> list = excelReader.readExcelContent();
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).get(0));
        }
    }
}