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

import java.io.IOException;
import java.security.ProviderException;

import javax.servlet.jsp.JspException;
import javax.servlet.ServletException;

import net.duckling.ddl.util.TextUtil;


/**
 *  Includes an another JSP page, making sure that we actually pass
 *  the WikiContext correctly.
 *
 *  @author Yong Ke
 */
public class IncludeTag
        extends VWBBaseTag
{
    private static final long serialVersionUID = 0L;

    protected String m_page;

    public void initTag()
    {
        super.initTag();
        m_page = null;
    }

    public void setPage( String page )
    {
        m_page = page;
    }

    public String getPage()
    {
        return m_page;
    }

    public final int doVWBStart()
            throws IOException,
            ProviderException
    {
        // WikiEngine engine = m_wikiContext.getEngine();

        return SKIP_BODY;
    }

    public final int doEndTag()
            throws JspException
    {
        try
        {
            String page = m_page;

            if( page == null )
            {
                pageContext.getOut().println("No template file called '"+TextUtil.replaceEntities(m_page)+"'");
            }
            else
            {
                pageContext.include( page );
            }
        }
        catch( ServletException e )
        {
            LOG.warn( "Including failed, got a servlet exception from sub-page. "+
                      "Rethrowing the exception to the JSP engine.", e );
            throw new JspException( e.getMessage() );
        }
        catch( IOException e )
        {
            LOG.warn( "I/O exception - probably the connection was broken. "+
                      "Rethrowing the exception to the JSP engine.", e );
            throw new JspException( e.getMessage() );
        }

        return EVAL_PAGE;
    }
}
