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

/**
 * Introduction Here.
 * @date 2010-3-8
 * @author 狄
 */
public class Dml2HtmlEngine {
    private Writer moutTimmer = new WhitespaceTrimWriter();
    private PrintWriter mout = new PrintWriter( moutTimmer );

    private int preType=0;//所有父级元素中是否存在pre或者per样式的span >0是存在
    private String PageName;//页面名
    private String parentNameOfSubPage;//当前页面的下级页面的父页面；一般情况(非导航和左菜单页面的时候)PageName=parentNameOfSubPage
    private int sectionid=0;//节编辑编号
    private String viewMode="0";//在浏览页面下判断是浏览模式还是编辑模式0是浏览1编辑
    private DmlContext dmlcontext;
    private boolean mwysiwygEditorMode;//在编辑状态查看该区域是属于fck内部还是外部
    private int h1index;
    private int h2index;
    private int h3index;
    private int h4index;
    
    public String getDdataSiteTableName(String tablename){
    	return dmlcontext.getDdataSiteTableName(tablename);
    }
    public String removeDdataSiteTableName(String tablename){
    	return dmlcontext.removeDdataSiteTableName(tablename);
    }
    public String getBaseurl()
    {
        return dmlcontext.getBaseUrl();
    }
    public int getH1index()
    {
        return h1index;
    }
    public void setH1index(int h1index)
    {
        this.h1index = h1index;
    }
    public int getH2index()
    {
        return h2index;
    }
    public void setH2index(int h2index)
    {
        this.h2index = h2index;
    }
    public int getH3index()
    {
        return h3index;
    }
    public void setH3index(int h3index)
    {
        this.h3index = h3index;
    }
    public String getPageName() {
        return PageName;
    }
    public void setPageName(String pageName) {
        PageName = pageName;
    }
    public int getPreType() {
        return preType;
    }
    public void setPreType(int preType) {
        this.preType = preType;
    }
    public Writer getMoutTimmer() {
        return moutTimmer;
    }
    public void setMoutTimmer(Writer timmer) {
        moutTimmer = timmer;
    }
    public PrintWriter getMout() {
        return mout;
    }
    public void setMout(PrintWriter mout) {
        this.mout = mout;
    }
    public int getSectionid() {
        return sectionid;
    }
    public void setSectionid(int sectionid) {
        this.sectionid = sectionid;
    }
    public String getViewMode()
    {
        return viewMode;
    }
    public void setViewMode(String viewMode)
    {
        this.viewMode = viewMode;
    }
    public boolean isMwysiwygEditorMode()
    {
        return mwysiwygEditorMode;
    }
    public void setMwysiwygEditorMode(boolean editorMode)
    {
        mwysiwygEditorMode = editorMode;
    }
    public DmlContext getDmlcontext()
    {
        return dmlcontext;
    }
    public void setDmlcontext(DmlContext dmlcontext)
    {
        this.dmlcontext = dmlcontext;
    }
    public int getH4index()
    {
        return h4index;
    }
    public void setH4index(int h4index)
    {
        this.h4index = h4index;
    }
    public String getParentNameOfSubPage()
    {
        return parentNameOfSubPage;
    }
    public void setParentNameOfSubPage(String parentNameOfSubPage)
    {
        this.parentNameOfSubPage = parentNameOfSubPage;
    }
}
