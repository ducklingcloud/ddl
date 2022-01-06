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
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.duckling.ddl.util.FileUtil;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 * Introduction Here.
 * @date 2010-3-8
 * @author 狄 diyanliang@cnic.cn
 */

public class ParseHtmlImg extends AbstractParseHtmlElement {

    private static final Logger LOG = Logger.getLogger(ParseHtmlImg.class);

    @Override
    public void printAttribute(Element e,Html2DmlEngine html2dmlengine) {
        Map<String, String> map = new ForgetNullValuesLinkedHashMap<String,String>();
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


        if(isNotSpan2Plugin(e)){
            printPlugin(e, html2dmlengine);
        } else{

            //因为附件后面会加上一个图片 所以在此做判断
            boolean  attachtype=false;
            if(e.getAttributeValue( "alt" )!=null && e.getAttributeValue( "alt" ).equals("(info)")){
                attachtype=true;
            }
            if(!attachtype){
                attachtype=false;
                html2dmlengine.getMout().print("<img");
                printAttribute(e, html2dmlengine);

                if(html2dmlengine.getPreType()>0){
                    html2dmlengine.getMout().print("/>");
                }else{
                    html2dmlengine.getMout().println("/>");
                }
                try {
                    h2d.getChildren(e,html2dmlengine);
                } catch (IOException e1) {
                    LOG.error(e1);
                } catch (JDOMException e1) {
                    LOG.error(e1);
                }
            }
        }
    }

    // 将 BASE64 编码的字符串 s 进行解码
    public static String getFromBASE64(String s)
    {
        if (s == null){
            return null;
        }
        try
        {
            byte[] b = Base64.decodeBase64(s.getBytes());
            return new String(b);
        }
        catch (Exception e)
        {
            LOG.error(e);
            return null;
        }
    }


    /*
     * 判断是否需要将img转换成puglin
     *
     */
    public boolean isNotSpan2Plugin(Element e){
        boolean reb=false;
        String strattibute="";
        org.jdom.Attribute attibute=e.getAttribute("class");
        if(attibute!=null){
            strattibute=attibute.getValue().toString();
            if(strattibute.equals("plugin")){
                reb=true;
            }
        }

        return reb;

    }

    public static final String PARAM_CMDLINE   = "_cmdline";
    public static final String PARAM_BODY      = "_body";
    public Map parseArgs( String argstring )
            throws IOException
    {
        HashMap<String, String>         arglist = new HashMap<String, String>();

        //
        //  Protection against funny users.
        //
        if( argstring == null ){
            return arglist;
        }



        StringReader    in      = new StringReader(argstring);
        StreamTokenizer tok     = new StreamTokenizer(in);
        int             type;


        String param = null;
        String value = null;

        tok.eolIsSignificant( true );

        boolean potentialEmptyLine = false;
        boolean quit               = false;

        while( !quit )
        {
            String s;

            type = tok.nextToken();

            switch( type )
            {
                case StreamTokenizer.TT_EOF:
                    quit = true;
                    s = null;
                    break;

                case StreamTokenizer.TT_WORD:
                    s = tok.sval;
                    potentialEmptyLine = false;
                    break;

                case StreamTokenizer.TT_EOL:
                    quit = potentialEmptyLine;
                    potentialEmptyLine = true;
                    s = null;
                    break;

                case StreamTokenizer.TT_NUMBER:
                    s = Integer.toString( new Double(tok.nval).intValue() );
                    potentialEmptyLine = false;
                    break;

                case '\'':
                    s = tok.sval;
                    break;

                default:
                    s = null;
            }

            //
            //  Assume that alternate words on the line are
            //  parameter and value, respectively.
            //
            if( s != null ){
                if( param == null ){
                    param = s;
                }else{
                    value = s;
                    arglist.put( param, value );
                    param = null;
                }
            }
        }

        //
        //  Now, we'll check the body.
        //

        if( potentialEmptyLine )
        {
            StringWriter out = new StringWriter();
            FileUtil.copyContents( in, out );

            String bodyContent = out.toString();

            if( bodyContent != null )
            {
                arglist.put( PARAM_BODY, bodyContent );
            }
        }

        return arglist;
    }

    /*
     * 将转换后的puglin插件按照DML形势输出
     * plugin在html中表现形势
     * <span class="plugin">
     *   <span class="parameter" style="display:none">
     *     key1=value1;
     *     key2=value2;
     *     ...
     *   </span>
     * </span>
     *
     */
    public void printPlugin(Element element,Html2DmlEngine html2dmlengine){
        html2dmlengine.getMout().print("<D_plugin" );

        org.jdom.Attribute srcattibute=element.getAttribute("src");
        String src=srcattibute.getValue();


        org.jdom.Attribute style=element.getAttribute("style");
        if(style!=null){
            String strstyle=style.getValue().toLowerCase();
            String[] strstyles=strstyle.split(";");
            for(String str:strstyles){
                String[] strs=str.trim().split(":");
                if(strs.length==2){
                    if("height".equals(strs[0])){
                        String height=strs[1].trim().replace("px", "");
                        element.setAttribute("height",height);

                    }else if("width".equals(strs[0])){
                        String width=strs[1].trim().replace("px", "");
                        element.setAttribute("width",width);
                    }
                }
            }
        }


        org.jdom.Attribute height=element.getAttribute("height");
        if(height!=null){
            html2dmlengine.getMout().println(" height=\""+height.getValue()+"\" " );
        }

        org.jdom.Attribute width=element.getAttribute("width");
        if(width!=null){
            html2dmlengine.getMout().println(" width=\""+width.getValue()+"\" " );
        }

        int beginIndex=src.indexOf("script");
        src="$baseurl"+src.substring(beginIndex);
        html2dmlengine.getMout().println(" src=\""+src+"\" >" );

        Map map = new HashMap();
        org.jdom.Attribute attibute=element.getAttribute("title");
        String strParameter=attibute.getValue();
        try{
            map=parseArgs(strParameter);
        }catch (IOException e){
            LOG.error(e);
        }

        Iterator   it   =   map.entrySet().iterator()   ;
        while(it.hasNext()){
            Map.Entry   entry   =   (Map.Entry)   it.next()   ;
            Object   key   =   entry.getKey()   ;
            Object   value   =   entry.getValue()   ;
            html2dmlengine.getMout().println( "<D_parameter " + ((String)key).trim().replaceAll( "[\\r\\n\\f\\u0085\\u2028\\u2029\\ufeff]", "" ) + "=\"" + ((String)value) + "\"/>" );

        }
        html2dmlengine.getMout().println("</D_plugin>" );

    }


}
