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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.JDOMException;
/**
 * Introduction Here.
 * @date 2010-3-8
 * @author 狄
 */
public class ParseHtmlA extends AbstractParseHtmlElement {

	private static final Logger LOG = Logger.getLogger(ParseHtmlA.class);
	private static final String ERR_STRING="scripts/fckeditor/editor/"; 

	public void printAttribute(Element e,Html2DmlEngine html2dmlengine) {
		  Map<String,String> map = new ForgetNullValuesLinkedHashMap<String,String>();
		  String strherf=null;
		  if(e.getAttributeValue( "href" )!=null){
		      strherf=e.getAttributeValue( "href" );
		      strherf=fixPageUrl(strherf);
		  }
		
		  if(html2dmlengine.getDmlcontext().isInternalURL(strherf)){
			  Map<String,String> params = html2dmlengine.getDmlcontext().resolve(strherf);
			  if(params!=null){
				  String herftype = params.get("type");
				  map.put( "linktype", herftype); 
				  String herfkey  = params.get("key");
				  map.put( "href", herfkey); 
			  } else{
				  map.put( "href", strherf); 
			  }

		  }else{
			  map.put( "href", strherf);     
		  }
          
          if(!"wikipage".equals(e.getAttributeValue( "class" ))&&!"createpage".equals(e.getAttributeValue( "class" )))
          {
        	  map.put( "class", e.getAttributeValue( "class" ) );
          }
          map.put( "target", e.getAttributeValue( "target" ) );
          map.put( "title", e.getAttributeValue( "title" ) );
          map.put( "id", e.getAttributeValue( "id" ) );
          map.put( "rid", e.getAttributeValue( "rid" ) ); //保留自定义ID
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

	public void printElement(Element e,Html2DmlEngine html2dmlengine){
		html2dmlengine.getMout().print("<D_Link");
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
			html2dmlengine.getMout().print("</D_Link>");
		}else{
			html2dmlengine.getMout().println("</D_Link>");
		}
	}

	
	
	public  String fixPageUrl(String str){
     	//@fixstart@此方法是防止在ie编辑器下将链接变化的一个临时办法例：http://localhost:8080/dct/scripts/fckeditor/editor/1001
     	int iindex=str.indexOf(ERR_STRING);
        if(iindex!=-1){
        	str=str.substring(iindex+ERR_STRING.length());
        }
        return str;
         //@fixend@
	}


    /**
     * Turns a WikiName into something that can be called through using an URL.
     * 
     * @since 1.4.1
     * @param pagename
     *            A name. Can be actually any string.
     * @return A properly encoded name.
     * @see #decodeName(String)
     */
    public static String decodeName(String pagename)
    {
        try
        {
            return URLDecoder.decode (pagename,"UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            LOG.error(e);
            
            return ("ISO-8859-1 not a supported encoding!?!  Your platform is borked.");
        }
    }
}
