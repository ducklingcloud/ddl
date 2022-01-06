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
public class PaserDmlPreElement extends AbstractParseDmlElement {

    private static final Logger LOG = Logger.getLogger(PaserDmlPreElement.class);

    @Override
    public void printAttribute(Element e, Dml2HtmlEngine dml2htmlengine) {
        List attList=e.getAttributes();
        for(int i=0;i<attList.size();i++){
            String strAttName=((Attribute)attList.get(i)).getName();
            String strAttValue=((Attribute)attList.get(i)).getValue();
            dml2htmlengine.getMout().print( " " + strAttName+ "=\"" + strAttValue + "\"" );
        }

    }

    @Override
    public void printElement(Element element,Dml2HtmlEngine dml2htmlengine) {
        //设pre标志位
        dml2htmlengine.setPreType(dml2htmlengine.getPreType()+1);

        dml2htmlengine.getMout().print("<pre");
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
        //设pre标志位
        dml2htmlengine.setPreType(dml2htmlengine.getPreType()-1);
        if(dml2htmlengine.getPreType()>0){
            dml2htmlengine.getMout().print("</pre>");
        }else{
            dml2htmlengine.getMout().println("</pre>");
        }
    }

}
