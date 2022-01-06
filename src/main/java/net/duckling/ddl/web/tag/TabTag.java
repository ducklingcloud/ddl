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

package net.duckling.ddl.web.tag;

import javax.servlet.jsp.JspTagException;

import net.duckling.ddl.util.TextUtil;


/**
 * Introduction Here.
 * @date Mar 5, 2010
 * @author xiejj@cnic.cn
 */
public class TabTag extends VWBBaseTag {
    private static final long serialVersionUID = -8534125226484616489L;
    private String m_accesskey;
    private String m_tabTitle;
    private String m_url;
    private String m_click; //kevin added

    /**
     * {@inheritDoc}
     */
    public void doFinally()
    {
        super.doFinally();

        m_accesskey = null;
        m_tabTitle  = null;
        m_url       = null;
    }

    /**
     * Sets the tab title.
     * @param aTabTitle the tab title
     */
    public void setTitle(String aTabTitle)
    {
        m_tabTitle = TextUtil.replaceEntities( aTabTitle );
    }

    /**
     * Sets the tab access key.
     * @param anAccesskey the access key
     */
    public void setAccesskey(String anAccesskey)
    {
        m_accesskey = TextUtil.replaceEntities( anAccesskey ); //take only the first char
    }

    /**
     * Sets the tab URL.
     * @param url the URL
     */
    public void setUrl( String url )
    {
        m_url = TextUtil.replaceEntities( url );
    }

    //kevin added at 20080122
    public void setClick(String action) {
        m_click = action;
    }

    // insert <u> ..accesskey.. </u> in title
    private boolean handleAccesskey()
    {
        if( (m_tabTitle == null) || (m_accesskey == null) ){
            return false;
        }

        int pos = m_tabTitle.toLowerCase().indexOf( m_accesskey.toLowerCase() );
        if( pos > -1 )
        {
            m_tabTitle = m_tabTitle.substring( 0, pos ) + "<span class='accesskey'>"
                    + m_tabTitle.charAt( pos ) + "</span>" + m_tabTitle.substring( pos+1 );
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public int doVWBStart() throws JspTagException
    {
        TabbedSectionTag parent=(TabbedSectionTag)findAncestorWithClass( this, TabbedSectionTag.class );

        //
        //  Sanity checks
        //
        if( getId() == null )
        {
            throw new JspTagException("Tab Tag without \"id\" attribute");
        }
        if( m_tabTitle == null )
        {
            throw new JspTagException("Tab Tag without \"tabTitle\" attribute");
        }
        if( parent == null )
        {
            throw new JspTagException("Tab Tag without parent \"TabbedSection\" Tag");
        }

        if( !parent.isStateGenerateTabBody() ){
            return SKIP_BODY;
        }

        StringBuffer sb = new StringBuffer(32);

        sb.append( "<div id=\""+ getId() + "\"" );

        if( !parent.validateDefaultTab( getId()) )
        {
            sb.append( " class=\"DCT_hidetab\"" );
        }
        sb.append( " >\n" );

        try
        {
            pageContext.getOut().write( sb.toString() );
        }
        catch( java.io.IOException e )
        {
            throw new JspTagException( "IO Error: " + e.getMessage() );
        }

        return EVAL_BODY_INCLUDE;
    }

    /**
     * {@inheritDoc}
     */
    public int doEndTag() throws javax.servlet.jsp.JspTagException
    {
        TabbedSectionTag parent=(TabbedSectionTag)findAncestorWithClass( this, TabbedSectionTag.class );

        StringBuffer sb = new StringBuffer();

        if( parent.isStateFindDefaultTab() )
        {
            //inform the parent of each tab
            parent.validateDefaultTab( getId() );
        }
        else if( parent.isStateGenerateTabBody() )
        {
            sb.append( "</div>\n" );
        }
        else if( parent.isStateGenerateTabMenu() )
        {
            sb.append( "<a" );

            if( parent.validateDefaultTab( getId() ) )
            {
                sb.append( " class=\"activetab\"" );
            }

            sb.append( " id=\"menu-" + getId() + "\"" );

            if( m_url != null )
            {
                sb.append( " href='"+m_url+"'" );
            }

            if( handleAccesskey() )
            {
                sb.append( " accesskey=\"" + m_accesskey + "\"" );
            }

            //kevin added at 20080122
            if (m_click !=null) {
                sb.append( " onClick=\"" + m_click + "\"");
            }

            sb.append( " >" );
            sb.append( m_tabTitle );
            sb.append( "</a>" );
        }

        try
        {
            pageContext.getOut().write( sb.toString() );
        }
        catch( java.io.IOException e )
        {
            throw new JspTagException( "IO Error: " + e.getMessage() );
        }

        return EVAL_PAGE;
    }
}
