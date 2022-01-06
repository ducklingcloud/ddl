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
import java.util.List;
import java.util.Map;

import net.duckling.ddl.util.FileUtil;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 * Introduction Here.
 * @date 2010-3-8
 * @author 狄 diyanliang@cnic.cn
 */
public class ParseHtmlSpan extends AbstractParseHtmlElement {

    private static final Logger LOG = Logger.getLogger(ParseHtmlSpan.class);

    @Override
    public void printAttribute(Element e,Html2DmlEngine html2dmlengine) {
        Map<String, String> map = new ForgetNullValuesLinkedHashMap<String, String>();
        map.put( "style", e.getAttributeValue( "style" ) );
        map.put( "class", e.getAttributeValue( "class" ) );
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

        // 设pre标志位
        if (isNPreSpan(e)) {
            html2dmlengine.setPreType(html2dmlengine.getPreType() + 1);
        }

        html2dmlengine.getMout().print("<span");
        printAttribute(e, html2dmlengine);
        if (html2dmlengine.getPreType() > 0) {
            html2dmlengine.getMout().print(">");
        } else {
            html2dmlengine.getMout().println(">");
        }
        try {
            h2d.getChildren(e, html2dmlengine);
        } catch (IOException e1) {
            LOG.error(e1);
        } catch (JDOMException e1) {
            LOG.error(e1);
        }

        // 设pre标志位
        if (isNPreSpan(e)) {
            html2dmlengine.setPreType(html2dmlengine.getPreType() - 1);
        }
        if (html2dmlengine.getPreType() > 0) {
            html2dmlengine.getMout().print("</span>");
        } else {
            html2dmlengine.getMout().println("</span>");
        }

    }

    public boolean isNPreSpan(Element e){
        boolean reb=false;
        String classstr=e.getAttributeValue( "style" );
        if(classstr!=null){
            int startnum=classstr.indexOf("white-space");
            if(startnum!=-1){
                if(classstr.length()>startnum+11){
                    String startstr=classstr.substring(startnum+11);
                    int endnum=startstr.indexOf(';');
                    if(endnum!=-1){
                        String prestr=startstr.substring(0,endnum);
                        if(prestr.indexOf("pre")!=-1){
                            reb=true;
                        }
                    }
                }

            }
        }
        return reb;


    }


    /*
     * 判断是否需要将span转换成puglin
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
        HashMap<String, String> arglist = new HashMap<String, String>();

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
        html2dmlengine.getMout().println("<D_plugin>" );
        Map map = new HashMap();
        List parameterList= element.getChildren();
        Element childelement = (Element)parameterList.get(0);
        String strParameter=childelement.getValue();

        try
        {
            map=parseArgs(strParameter);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        Iterator   it   =   map.entrySet().iterator()   ;
        while   (it.hasNext())
        {
            Map.Entry   entry   =   (Map.Entry)   it.next()   ;
            Object   key   =   entry.getKey()   ;
            Object   value   =   entry.getValue()   ;
            html2dmlengine.getMout().println( "<D_parameter " + ((String)key).trim().replaceAll( "[\\r\\n\\f\\u0085\\u2028\\u2029\\ufeff]", "" ) + "=\"" + ((String)value) + "\"/>" );

        }

        html2dmlengine.getMout().println("</D_plugin>" );


    }

}
