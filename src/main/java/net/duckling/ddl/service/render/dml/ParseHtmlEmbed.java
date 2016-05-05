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

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 * Introduction Here.
 * @date 2010-3-8
 * @author 狄
 */

public class ParseHtmlEmbed extends AbstractParseHtmlElement {
    
	private static final Logger LOG = Logger.getLogger(ParseHtmlEmbed.class);
	
	@Override
    public void printAttribute(Element e,Html2DmlEngine html2dmlengine) {
         Map<String,String> map = new ForgetNullValuesLinkedHashMap<String, String>();
         map.put( "class", e.getAttributeValue( "class" ) ); 
         map.put( "style", e.getAttributeValue( "style" ) ); 
         String dosrc=html2dmlengine.findAttachment( e.getAttributeValue( "src" ),html2dmlengine);
         if(dosrc!=null){
                 map.put( "dmlsrc", dosrc); 
         }else{
                 map.put( "src", e.getAttributeValue( "src" ) );    
         }
         map.put( "alt", e.getAttributeValue( "alt" ) );
         map.put( "height", e.getAttributeValue( "height" ) );
         map.put( "width", e.getAttributeValue( "width" ) );
         map.put("type", e.getAttributeValue( "type" ));
         map.put("pluginspage", e.getAttributeValue( "pluginspage" ));
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
        
        html2dmlengine.getMout().print("<embed ");
        printAttribute(e, html2dmlengine);
        if(html2dmlengine.getPreType()>0){
            html2dmlengine.getMout().print(">");
        }else{
            html2dmlengine.getMout().println(">");
        }
        try {
            h2d.getChildren(e, html2dmlengine);
        } catch (IOException e1) {
        	LOG.error(e1);
        } catch (JDOMException e1) {
        	LOG.error(e1);
        }
        if(html2dmlengine.getPreType()>0){
            html2dmlengine.getMout().print("</embed >");
        }else{
            html2dmlengine.getMout().println("</embed >");
        }
    }
    
    public static String getFromBASE64(String s)
    {
        if (s == null){
        	return null;
        }
        try{
            byte[] b = Base64.decodeBase64(s.getBytes());
            return new String(b);
        }
        catch (Exception e)
        {
            return null;
        }
    }
    
    
}
