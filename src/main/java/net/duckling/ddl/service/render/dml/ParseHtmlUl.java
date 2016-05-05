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
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.JDOMException;
/**
 * Introduction Here.
 * @date 2010-3-8
 * @author 狄 diyanliang@cnic.cn
 */
public class ParseHtmlUl extends AbstractParseHtmlElement {
	
	private static final Logger LOG = Logger.getLogger(ParseHtmlUl.class);
	
	@Override
	public void printAttribute(Element e,Html2DmlEngine html2dmlengine) {
		 Map<String,String> map = new ForgetNullValuesLinkedHashMap<String,String>();
		 map.put( "id", e.getAttributeValue( "id" ) ); 
         map.put( "style", e.getAttributeValue( "style" ) );                    
         map.put( "class", e.getAttributeValue( "class" ) );
         map.put( "type", e.getAttributeValue( "type" ) );
         if( map.size() > 0 )
         {
             for( Iterator ito = map.entrySet().iterator(); ito.hasNext(); )
             {
                 Map.Entry entry = (Map.Entry)ito.next();
                 if( !entry.getValue().equals( "" ) )
                 {
               	  html2dmlengine.getMout().print( " " + entry.getKey() + "=\"" + entry.getValue() + "\"" );
                 }
             }
         }
	}

	@Override
	public void printElement(Element e,Html2DmlEngine html2dmlengine ){
		html2dmlengine.getMout().print("<ul");
		printAttribute(e, html2dmlengine);
		if(html2dmlengine.getPreType()>0){
			html2dmlengine.getMout().print(">");
		}else{
			html2dmlengine.getMout().println(">");
		}
		try {
			h2d.getChildren(e,html2dmlengine);
		} catch (IOException e1) {
			LOG.error(e1);
		} catch (JDOMException e1) {
			LOG.error(e1);
		}
		if(html2dmlengine.getPreType()>0){
			html2dmlengine.getMout().print("</ul>");
		}else{
			html2dmlengine.getMout().println("</ul>");
		}
	}
}
