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
package net.duckling.ddl.web.bean;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.service.contact.ContactExt;
import net.duckling.ddl.util.Browser;

import org.apache.log4j.Logger;


public final class ContactsExportUtil {
    private static final Logger LOGGER=Logger.getLogger(ContactsExportUtil.class);
    private ContactsExportUtil(){}
    public static String convert2CSVString(List<ContactExt> contacts){
        StringBuilder sb=new StringBuilder();
        sb.append("姓名,账号,电子邮件,辅助电子邮件,单位,部门,性别,固定电话,手机,QQ,MSN,地址,生日,微博"+"\r\n");
        Iterator<ContactExt> ceItr=contacts.iterator();
        while(ceItr.hasNext()){
            ContactExt ce=ceItr.next();
            sb.append(ce.getName()+",");
            sb.append(ce.getUid()+",");
            sb.append(ce.getMainEmail()+",");
            sb.append((ce.getOptionEmail()==null?"":ce.getOptionEmail())+",");
            sb.append((ce.getOrgnization()==null?"":ce.getOrgnization())+",");
            sb.append((ce.getDepartment()==null?"":ce.getDepartment())+",");
            sb.append((ce.getSex()==null?"":ce.getSex())+",");
            sb.append((ce.getTelephone()==null?"":ce.getTelephone())+",");
            sb.append((ce.getMobile()==null?"":ce.getMobile())+",");
            sb.append((ce.getQq()==null?"":ce.getQq())+",");
            sb.append((ce.getMsn()==null?"":ce.getMsn())+",");
            sb.append((ce.getAddress()==null?"":ce.getAddress())+",");
            sb.append((ce.getBirthday()==null?"":ce.getBirthday())+",");
            sb.append((ce.getWeibo()==null?"":ce.getWeibo()));
            sb.append("\r\n");
        }
        return sb.toString();
    }

    public static void download(HttpServletRequest request, HttpServletResponse response,String text,String fileName){
        //sresponse.reset();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/x-msdownload");//APPLICATION/OCTET-STREAM
        try {
            fileName = java.net.URLDecoder.decode(fileName, "UTF-8");//解决中文 文件名问题
            String agent=request.getHeader("USER-AGENT");
            response.setHeader("Content-Disposition", Browser.encodeFileName(agent, fileName));

        } catch (UnsupportedEncodingException e1) {
            LOGGER.error(e1);
        }

        byte[] b=new byte[100];
        java.io.OutputStream os=null;
        java.io.InputStream is=null;
        try{
            is=new java.io.ByteArrayInputStream(text.getBytes("UTF-8"));
            os=response.getOutputStream();
            int len=0;
            //下面两行是给csv文件添加BOM头字节，避免Excel打开时中文乱码
            byte[] fileHeader={(byte)0xEF, (byte)0xBB, (byte)0xBF};
            os.write(fileHeader);
            while((len=is.read(b))>0){
                os.write(b,0,len);
            }
            response.setStatus( HttpServletResponse.SC_OK );
            os.flush();
            os.close();
            is.close();

        }catch(IOException e){
            response.reset();
            LOGGER.error("",e);
        }
    }
}
