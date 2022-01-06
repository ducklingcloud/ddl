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

package net.duckling.ddl.service.render.dml;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;


import net.duckling.ddl.common.VWBContext;

import org.apache.commons.codec.binary.Base64;

/**
 * Introduction Here.
 * @date 2010-3-8
 * @author 狄
 */
public class Html2DmlEngine {
    private Writer mOutTimmer = new WhitespaceTrimWriter();
    private PrintWriter mout = new PrintWriter( mOutTimmer );
    private int preType=0;//所有父级元素中是否存在pre或者per样式的span >0是存在
    private String baseURL="";
    private boolean dmlinstall=false;
    private String formid="";//把表单id作为全局变量贯穿这个解析过程
    private boolean bFormTable=false;
    private String strCreateFormTable="";
    private List listCreateFormTable=new ArrayList();
    private VWBContext vwbcontext;
    private DmlContext dmlcontext;

    /**
     * @return the dmlcontext
     */
    public DmlContext getDmlcontext() {
        return dmlcontext;
    }
    /**
     * @param dmlcontext the dmlcontext to set
     */
    public void setDmlcontext(DmlContext dmlcontext) {
        this.dmlcontext = dmlcontext;
    }
    public VWBContext getVwbcontext() {
        return vwbcontext;
    }
    public void setVwbcontext(VWBContext vwbcontext) {
        this.vwbcontext = vwbcontext;
    }

    public String getStrCreateFormTable()
    {
        return strCreateFormTable;
    }
    public void setStrCreateFormTable(String strCreateFormTable)
    {
        this.strCreateFormTable = strCreateFormTable;
    }
    public boolean isDmlinstall()
    {
        return dmlinstall;
    }
    public void setDmlinstall(boolean dmlinstall)
    {
        this.dmlinstall = dmlinstall;
    }
    public void setMout(PrintWriter mout)
    {
        this.mout = mout;
    }
    public int getPreType() {
        return preType;
    }
    public void setPreType(int preType) {
        this.preType = preType;
    }
    public Writer getMoutTimmer() {
        return mOutTimmer;
    }
    public void setMoutTimmer(Writer timmer) {
        mOutTimmer = timmer;
    }
    public PrintWriter getMout() {
        return mout;
    }
    public void setMout(Html2DmlEngine html2dmlengine) {
        this.mout = html2dmlengine.getMout();
    }

    public String getBaseURL()
    {
        return baseURL;
    }
    public void setBaseURL(String baseURL)
    {
        this.baseURL = baseURL;
    }
    public String getFormid()
    {
        return formid;
    }
    public void setFormid(String formid)
    {
        this.formid = formid;
    }
    public boolean isBFormTable()
    {
        return bFormTable;
    }
    public void setBFormTable(boolean formTable)
    {
        bFormTable = formTable;
    }
    public List getListCreateFormTable()
    {
        return listCreateFormTable;
    }
    public void setListCreateFormTable(List listCreateFormTable)
    {
        this.listCreateFormTable = listCreateFormTable;
    }
    public String findAttachment( String link,Html2DmlEngine html2dmlengine){
        String baseurl=html2dmlengine.getBaseURL();
        if(link!=null && link.startsWith(baseurl)){
            int index = link.lastIndexOf('/');
            link = link.substring(index + 1);
            String strhash = getFromBASE64(link);
            if (strhash.indexOf("clb") != -1) {
                return link;
            }
        }
        return null;
    }
    // 将 BASE64 编码的字符串 s 进行解码
    public static String getFromBASE64(String s)
    {
        if (s == null)
            return null;
        try
        {
            byte[] b = Base64.decodeBase64(s.getBytes());
            return new String(b);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public String getDDataSiteTableName(String tablename){
        String sitename="site"+vwbcontext.getSite().getId()+"_";
        return sitename+tablename;
    }

    public String removeDDataSiteTableName(String tablename){
        String sitename="site"+vwbcontext.getSite().getId()+"_";
        if(tablename.startsWith(sitename)){
            tablename=tablename.substring(sitename.length());
        }
        return tablename;

    }

}
