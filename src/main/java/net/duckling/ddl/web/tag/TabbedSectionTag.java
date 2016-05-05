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
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * Introduction Here.
 * @date Mar 5, 2010
 * @author xiejj@cnic.cn
 */
public class TabbedSectionTag extends BodyTagSupport {
	   private static final long serialVersionUID = 1702437933960026481L;
	    private String       defaultTabId;
	    private String       firstTabId;
	    private boolean      defaultTabFound = false;

	    private StringBuffer buffer = new StringBuffer(BUFFER_SIZE);

	    private static final int FIND_DEFAULT_TAB = 0;
	    private static final int GENERATE_TABMENU = 1;
	    private static final int GENERATE_TABBODY = 2;

	    private static final int BUFFER_SIZE      = 1024;

	    private              int state            = FIND_DEFAULT_TAB;

	    public void release()
	    {
	        super.release();
	        defaultTabId = firstTabId = null;
	        defaultTabFound = false;
	        buffer = new StringBuffer();
	        state = FIND_DEFAULT_TAB;
	    }

	    public void setDefaultTab(String anDefaultTabId)
	    {
	        defaultTabId = anDefaultTabId;
	    }

	    public boolean validateDefaultTab( String aTabId )
	    {
	        if( firstTabId == null ){
	        	firstTabId = aTabId;
	        }
	        if( aTabId.equals( defaultTabId ) ){
	        	defaultTabFound = true;
	        }

	        return aTabId.equals( defaultTabId );
	    }

	    public int doStartTag() throws JspTagException
	    {
	        return EVAL_BODY_BUFFERED; /* always look inside */
	    }

	    public boolean isStateFindDefaultTab()
	    {
	        return state == FIND_DEFAULT_TAB;
	    }

	    public boolean isStateGenerateTabMenu()
	    {
	        return state == GENERATE_TABMENU;
	    }

	    public boolean isStateGenerateTabBody()
	    {
	        return state == GENERATE_TABBODY;
	    }


	    /* The tabbed section iterates 3 time through the underlying Tab tags
	     * - first it identifies the default tab (displayed by default)
	     * - second it generates the tabmenu markup (displays all tab-titles)
	     * - finally it generates the content of each tab.
	     */
	    public int doAfterBody() throws JspTagException
	    {
	        if( isStateFindDefaultTab() )
	        {
	            if( !defaultTabFound )
	            {
	                defaultTabId = firstTabId;
	            }
	            state = GENERATE_TABMENU;
	            return EVAL_BODY_BUFFERED;
	        }
	        else if( isStateGenerateTabMenu() )
	        {
	            if( bodyContent != null )
	            {
	                buffer.append( "<div class=\"DCT_tabmenu\">" );
	                buffer.append( bodyContent.getString() );
	                bodyContent.clearBody();
	                buffer.append( "</div>\n" );
	            }
	            state = GENERATE_TABBODY;
	            return EVAL_BODY_BUFFERED;
	        }
	        else if( isStateGenerateTabBody() )
	        {
	            if( bodyContent != null )
	            {
	                buffer.append( "<div class=\"tabs\">" );
	                buffer.append( bodyContent.getString() );
	                bodyContent.clearBody();
	                buffer.append( "<div style=\"clear:both;\" ></div>\n</div>\n" );
	            }
	            return SKIP_BODY;
	        }
	        return SKIP_BODY;
	    }

	    public int doEndTag() throws JspTagException
	    {
	        try
	        {
	            if( buffer.length() > 0 )
	            {
	                getPreviousOut().write( buffer.toString() );
	            }
	        }
	        catch(java.io.IOException e)
	        {
	            throw new JspTagException( "IO Error: " + e.getMessage() );
	        }

	        //now reset some stuff for the next run -- ugh.
	        buffer    = new StringBuffer(BUFFER_SIZE);
	        state = FIND_DEFAULT_TAB;
	        defaultTabId    = null;
	        firstTabId      = null;
	        defaultTabFound = false;
	        return EVAL_PAGE;
	    }
}
