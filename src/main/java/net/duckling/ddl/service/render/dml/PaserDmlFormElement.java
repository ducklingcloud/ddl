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

import java.io.IOException;
import java.util.List;

import net.duckling.ddl.service.url.UrlPatterns;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 * Introduction Here.
 * @date 2010-3-8
 * @author 狄
 */
public class PaserDmlFormElement extends AbstractParseDmlElement {

    private String strFormid="";
    private static final Logger LOG = Logger.getLogger(PaserDmlFormElement.class);

    @Override
    public void printAttribute(Element e, Dml2HtmlEngine dml2htmlengine) {
        if(e.getAttribute("id")!=null){
            strFormid=e.getAttribute("id").getValue();
        }
        List attList=e.getAttributes();
        for(int i=0;i<attList.size();i++){
            String strAttName=((Attribute)attList.get(i)).getName();
            String strAttValue=((Attribute)attList.get(i)).getValue();
            dml2htmlengine.getMout().print( " " + strAttName+ "=\"" + strAttValue + "\"" );
        }
        if(!dml2htmlengine.isMwysiwygEditorMode()){//判断是fck内部页面还是fck外部浏览页面
            String url =dml2htmlengine.getDmlcontext().getURL(UrlPatterns.T_TEAM, "dmlData",null);
            dml2htmlengine.getMout().print("action='"+url+"'");
        }
    }

    @Override
    public void printElement(Element element,Dml2HtmlEngine dml2htmlengine) {
        dml2htmlengine.getMout().print("<"+element.getName().toLowerCase());
        printAttribute(element,  dml2htmlengine);
        if(dml2htmlengine.getPreType()>0){
            dml2htmlengine.getMout().print(">");
        }else{
            dml2htmlengine.getMout().println(">");
        }
        try {
            d2h.getChildren(element, dml2htmlengine);
        } catch (IOException e1) {
            LOG.error(e1);
        } catch (JDOMException e1) {
            LOG.error(e1);
        }

        if(!dml2htmlengine.isMwysiwygEditorMode()){//判断是fck内部页面还是fck外部浏览页面
            if(!"".equals(strFormid)){
                dml2htmlengine.getMout().print("<input type='hidden' name='dmlformid' value='"+dml2htmlengine.getDdataSiteTableName(strFormid)+"'/>");
            }
            String strsql1="", strsql2="",strsql3="",changepage="",resourceid="";
            if(element.getAttribute("sqlsrc1")!=null){
                strsql1=element.getAttribute("sqlsrc1").getValue();
                dml2htmlengine.getMout().print("<input type='hidden' name='sqlsrc1' value=\""+strsql1+"\"/>");
            }

            if(element.getAttribute("sqlsrc2")!=null){
                strsql2=element.getAttribute("sqlsrc2").getValue();
                dml2htmlengine.getMout().print("<input type='hidden' name='sqlsrc2' value=\""+strsql2+"\"/>");
            }
            if(element.getAttribute("sqlsrc3")!=null){
                strsql3=element.getAttribute("sqlsrc3").getValue();
                dml2htmlengine.getMout().print("<input type='hidden' name='sqlsrc3' value=\""+strsql3+"\"/>");
            }
            if(element.getAttribute("changepage")!=null){
                changepage=element.getAttribute("changepage").getValue();
                dml2htmlengine.getMout().print("<input type='hidden' name='changepage' value=\""+changepage+"\"/>");
            }

            if(element.getAttribute("resourceid")!=null){
                resourceid=element.getAttribute("resourceid").getValue();
                dml2htmlengine.getMout().print("<input type='hidden' name='resourceid' value=\""+resourceid+"\"/>");
            }

        }


        strFormid="";

        if(dml2htmlengine.getPreType()>0){
            dml2htmlengine.getMout().print("</"+element.getName().toLowerCase()+">");
        }else{
            dml2htmlengine.getMout().println("</"+element.getName().toLowerCase()+">");
        }
    }
}
