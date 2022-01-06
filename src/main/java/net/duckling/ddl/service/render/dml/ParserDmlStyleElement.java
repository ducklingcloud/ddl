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

import org.jdom.Element;
/**
 * Introduction Here.
 * @date 2010-3-8
 * @author 狄
 */
public class ParserDmlStyleElement extends AbstractParseDmlElement {


    public void printAttribute(Element e,Dml2HtmlEngine dml2htmlengine) {}

    public void printElement(Element element, Dml2HtmlEngine dml2htmlengine) {
        dml2htmlengine.getMout().print("<"+element.getName().toLowerCase());
        if(dml2htmlengine.getPreType()>0){
            dml2htmlengine.getMout().print("/>");
        }else{
            dml2htmlengine.getMout().println("/>");
        }
    }
}
