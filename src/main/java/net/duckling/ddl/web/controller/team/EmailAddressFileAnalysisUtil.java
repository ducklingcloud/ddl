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
package net.duckling.ddl.web.controller.team;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.duckling.ddl.util.ExcelReader;
import net.duckling.ddl.util.StringUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * @date 2012-2-14
 * @author Yangxp
 * @Description 解析由Foxmail和Outlook导出电子邮件地址簿的*.csv/*。vcf文件
 */
public final class EmailAddressFileAnalysisUtil {
    private EmailAddressFileAnalysisUtil() {
    }

    private static String ming;
    private static String mingZi;
    private static String xing;
    private static String xingShi;
    private static String xianShiMing;
    private static String email;

    private static final String MING_EN = "first+name";// utf-8将空格编码成+
    private static final String XING_EN = "last+name";
    private static final String DISPLAY_NAME_EN = "display+name";
    private static final String EMAIL_EN1 = "e-mail";
    private static final String EMAIL_EN2 = "email";

    private static final String UTF8_BOM = "%EF%BB%BF";

    private static final Logger LOG = Logger.getLogger(EmailAddressFileAnalysisUtil.class);

    static {
        try {
            ming = java.net.URLEncoder.encode("名", "UTF-8");
            mingZi = java.net.URLEncoder.encode("名字", "UTF-8");
            xing = java.net.URLEncoder.encode("姓", "UTF-8");
            xingShi = java.net.URLEncoder.encode("姓氏", "UTF-8");
            xianShiMing = java.net.URLEncoder.encode("显示名称", "UTF-8");
            email = java.net.URLEncoder.encode("电子邮件", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.info(
                    "Unsupported Encoding character in static initialization of cn.vlabs.duckling.aone.infrastructure.util.EmailAddressFileAnalysisUtil",
                    e);
        }
    }

    public static List<Map<String, String>> getContactsFromStream(InputStream in, String fileFormat) {
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        Map<String, String> contacts = new HashMap<String, String>();
        if (fileFormat.toUpperCase().equals("CSV")) {
            contacts = readCSVFile(in);
        } else if (fileFormat.toUpperCase().equals("VCF")) {
            contacts = readVCFFile(in);
        } else if(fileFormat.toUpperCase().equals("XLS")||fileFormat.toUpperCase().equals("XLSX")){
        	contacts = readExcelFile(in,fileFormat);
        }else{
        	LOG.error("Unsupported File Format!");
            return null;
        }
        if (null == contacts) {
            return null;
        }
        for (Entry<String, String> entry:contacts.entrySet()){
        	Map<String, String> temp = new HashMap<String, String>();
        	temp.put("name", entry.getValue());
        	temp.put("email", entry.getKey());
        	result.add(temp);
        }
        return result;
    }
    
    /**
     * @param in
     * @param ext excel扩展名xls或xlsx
     * @return
     * @throws IOException 
     */
    public static Map<String, String> readExcelFile(InputStream in, String ext) {
    	Map<String, String> result = new LinkedHashMap<String, String>();
    	ExcelReader er = null;
		try {
			er = new ExcelReader(in, ext);
		} catch (IOException err) {
			LOG.error("", err);
			return null;
		}
		
		try {
			List<Map<Integer, Object>> list = er.readExcelContent();
			for (int i = 0; i < list.size(); i++) {
	        	Map<Integer,Object> row = list.get(i);
	        	String tureName = String.valueOf(row.get(0)).trim();
	        	String email = String.valueOf(row.get(1)).trim();
	        	if(!StringUtil.isValidEmail(email) || StringUtils.isEmpty(tureName)){
	        		return null;
	        	}
	            result.put(email, tureName);
	        }
		} catch (Exception e) {
			 LOG.error("Invalid Excel File: " + e.getMessage());
			 return null;
		}
        
        return result;
    }

    public static Map<String, String> readCSVFile(InputStream in) {
        Map<String, String> result = new HashMap<String, String>();
        BufferedReader br;
        try {
            br = new BufferedReader(new UnicodeReader(in, "GBK"));// 此处的GBK是传默认编码
            String line = "", former = "";
            boolean firstLine = true;
            int ming_index = -1, xing_index = -1, xingming_index = -1, displayName_index = -1;// email_index=0,
            ArrayList<Integer> emailCols = new ArrayList<Integer>();// 保存检索出的所有Email列索引位置值
            int colNum = 0; // 用于保存可供解析的最长字段数
            while ((line = br.readLine()) != null || (null != former && !"".equals(former))) {
                int tempLen = (line + former).split(",").length;
                String temp = "";
                String[] strArray; // 存放解析后的字符串数组
                while (tempLen < colNum) { // former中不够一个联系人的信息
                    line = former + line;
                    temp = br.readLine();
                    strArray = (line + temp).split(",");
                    if (strArray.length > colNum || null == temp) { // 超过最长字段数或解析文件结束
                        former = temp;
                        break;
                    } else {
                        line += temp;
                    }
                }
                if (tempLen == colNum) {
                    line = former + line;
                    former = "";
                }
                if (colNum > 0 && tempLen > colNum) {// former中已包含一个联系人信息，line中为另一个联系人信息
                    temp = former;
                    former = line;
                    line = temp;
                }
                // Outlook导出的地址簿每列都包含了""，而Foxmail没有。去除每列中的""
                strArray = line.split(",");
                if (firstLine) { // 第一列时，需找出姓名列的位置索引
                    for (int i = 0; i < strArray.length; i++) {
                        String colStr = java.net.URLEncoder.encode(strArray[i].replace("\"", "").trim(), "UTF-8");
                        if (colStr.startsWith(UTF8_BOM)) {// 去掉UTF-8的BOM字节
                            colStr = colStr.substring(UTF8_BOM.length(), colStr.length());
                        }
                        if (ming.equals(colStr) || mingZi.equals(colStr) || MING_EN.equals(colStr.toLowerCase())) {
                            ming_index = i;
                        }
                        if (xing.equals(colStr) || xingShi.equals(colStr) || XING_EN.equals(colStr.toLowerCase())) {
                            xing_index = i;
                        }
                        if ((xing + ming).equals(colStr) || "name".equals(colStr.toLowerCase())) {
                            xingming_index = i;
                            // break;
                        }
                        if (xianShiMing.equals(colStr) || DISPLAY_NAME_EN.equals(colStr.toLowerCase())) {
                            displayName_index = i;
                        }
                        if (colStr.contains(email) || colStr.toLowerCase().contains(EMAIL_EN1)
                                || colStr.toLowerCase().contains(EMAIL_EN2)) {
                            emailCols.add(i);
                        }// break;
                    }
                    if (xing_index < 0 && ming_index < 0 && xingming_index < 0 && displayName_index < 0) {
                        LOG.error("ERROR:Invalid CSV File! Unable to find Column of name");
                        return null;
                    }
                    colNum = strArray.length;
                    firstLine = false;
                } else { // 从第二列开始提取姓名和Email
                    String name;
                    if (xingming_index < 0) {
                        name = strArray[xing_index] + strArray[ming_index];
                    } else {
                        name = strArray[xingming_index];
                    }
                    if ("".equals(name) && displayName_index > 0) {
                        name = strArray[displayName_index];
                    }
                    name = name.replace("\"", "");
                    Pattern pattern = Pattern.compile(StringUtil.EMAIL_REGEX);
                    Matcher matcher = pattern.matcher(line);
                    List<String> emails = new ArrayList<String>();
                    while (matcher.find()) {
                        String email = matcher.group();
                        if (isRightEmail(strArray, emailCols, line, email)) {// 如果是合法的email则加入
                            emails.add(email);
                        }
                    }
                    for (String email : emails) {
                        if ("".equals(name))// 姓名为空则用email中@字符之前的字符串作为姓名
                        {
                            name = email.substring(0, email.indexOf('@'));
                        }
                        result.put(email, name);
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            LOG.error("", e);
        } catch (FileNotFoundException e) {
            LOG.error("", e);
        } catch (IOException e) {
            LOG.error("", e);
        }
        return result;
    }

    public static Map<String, String> readVCFFile(InputStream in) {
        Map<String, String> result = new HashMap<String, String>();
        String name = "", email = "";
        BufferedReader br;
        try {
            br = new BufferedReader(new UnicodeReader(in, "GBK"));
            String line = br.readLine();
            if (!line.startsWith("BEGIN:VCARD")) {
                LOG.error("Invalid VCF File!");
                return null;
            }
            while (!"END:VCARD".equals((line = br.readLine())) && line != null) {
                if (line.contains("FN")) {
                    int index = line.lastIndexOf(':');
                    name = line.substring(index + 1, line.length());
                }
                if (line.contains("EMAIL")) {
                    int index = line.lastIndexOf(':');
                    email = line.substring(index + 1, line.length());
                }
                if (!("".equals(name)) && !("".equals(email)))
                    result.put(email, name);
            }
            if (null == line && result.size() <= 0) {
                LOG.error("Invalid VCF File!");
            }
        } catch (FileNotFoundException e) {
            LOG.error("", e);
        } catch (IOException e) {
            LOG.error("", e);
        }
        return result;
    }

    // 判断email是否为line中的合法email
    static boolean isRightEmail(String[] strArray, List<Integer> emailCols, String line, String email) {
        int arrLen = strArray.length;
        for (Integer i : emailCols) {
            if (i < arrLen && strArray[i].equals(email)) {
                return true;
            }
            if (i >= arrLen) {
                break;
            }
        }
        int cur = line.indexOf(email);
        if (cur >= 0) {
            String temp = line.substring(0, cur);
            int start = temp.lastIndexOf(",\"") + 2;
            temp = line.substring(cur, line.length());
            int end = temp.indexOf("\",") + cur;
            if (start < 2 || end < cur) {// email不被""包裹，不是合法的email
                return false;
            }
            String emails = line.substring(start, end);
            if (emails.contains(email)) {
                return true;
            }
        }
        return false;
    }

}
