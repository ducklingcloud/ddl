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

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
/**
 * Introduction Here.
 * @date 2010-3-8
 * @author 狄
 */
public class PaserDmlHeadingElement extends AbstractParseDmlElement {

    private static final Logger LOG = Logger.getLogger(PaserDmlHeadingElement.class);

    @Override
    public void printAttribute(Element e, Dml2HtmlEngine dml2htmlengine) {
        List attList=e.getAttributes();
        for(int i=0;i<attList.size();i++){
            String strAttName=((Attribute)attList.get(i)).getName();
            String strAttValue=((Attribute)attList.get(i)).getValue();
            dml2htmlengine.getMout().print( " " + strAttName+ "=\"" + strAttValue + "\"" );


        }
        if("h1".equals(e.getName().toLowerCase())){
            dml2htmlengine.setH1index(dml2htmlengine.getH1index()+1);
            dml2htmlengine.getMout().print( " id=\"" +e.getName().toLowerCase()+"_"+ dml2htmlengine.getH1index() + "\"" );
        }else if("h2".equals(e.getName().toLowerCase())){
            dml2htmlengine.setH2index(dml2htmlengine.getH2index()+1);
            dml2htmlengine.getMout().print( " id=\"" +e.getName().toLowerCase()+"_"+ dml2htmlengine.getH2index() + "\"" );
        }else if("h3".equals(e.getName().toLowerCase())){
            dml2htmlengine.setH3index(dml2htmlengine.getH3index()+1);
            dml2htmlengine.getMout().print( " id=\"" +e.getName().toLowerCase()+"_"+ dml2htmlengine.getH3index() + "\"" );
        }else if("h4".equals(e.getName().toLowerCase())){
            dml2htmlengine.setH4index(dml2htmlengine.getH4index()+1);
            dml2htmlengine.getMout().print( " id=\"" +e.getName().toLowerCase()+"_"+ dml2htmlengine.getH4index() + "\"" );
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

        if(dml2htmlengine.getPreType()>0){
            dml2htmlengine.getMout().print("</"+element.getName().toLowerCase()+">");
        }else{
            dml2htmlengine.getMout().println("</"+element.getName().toLowerCase()+">");
        }
    }
}
