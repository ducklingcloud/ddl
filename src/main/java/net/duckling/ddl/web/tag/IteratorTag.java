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
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TryCatchFinally;

import net.duckling.ddl.common.VWBContext;

import org.apache.log4j.Logger;



/**
 *  Iterates through tags.
 *
 *  <P><B>Attributes</B></P>
 *  <UL>
 *    <LI>list - a collection.
 *  </UL>
 *
 *  @author Yong Ke
 */
public abstract class IteratorTag
    extends BodyTagSupport
    implements TryCatchFinally
{

    protected String      m_pageName;
    protected Iterator    m_iterator;
    protected VWBContext  vwbcontext;

    private static final Logger LOG = Logger.getLogger( IteratorTag.class );

    /**
     *  Sets the collection that is used to form the iteration.
     *  
     *  @param arg A Collection which will be iterated.
     */
    public void setList( Collection arg )
    {
        if( arg != null ){ 
        	m_iterator = arg.iterator();
        
        }
    }

    /**
     *  Sets the collection list, but using an array.
     *  @param arg An array of objects which will be iterated.
     */
    public void setList( Object[] arg )
    {
        if( arg != null ){
            m_iterator = Arrays.asList(arg).iterator();
        }
    }

    /**
     *  Clears the iterator away.  After calling this method doStartTag()
     *  will always return SKIP_BODY
     */
    public void clearList()
    {
        m_iterator = null;
    }
    
    /**
     *  Override this method to reset your own iterator.
     */
    public void resetIterator()
    {
        // No operation here
    }
    
    /**
     *  {@inheritDoc}
     */
    public int doStartTag()
    {
        vwbcontext = VWBContext.getContext((HttpServletRequest)pageContext.getRequest());
        
        resetIterator();
        
        if( m_iterator == null ) return SKIP_BODY;

        if( m_iterator.hasNext() )
        {
            buildContext();
        }

        return EVAL_BODY_BUFFERED;
    }

    /**
     *  Arg, I hate globals.
     */
    private void buildContext()
    {
        
    	VWBContext context = null;
        Object o = m_iterator.next();
       /*
        *  Push it to the iterator stack, and set the id.
        */
        pageContext.setAttribute( VWBBaseTag.ATTR_CONTEXT,
                                  context,
                                  PageContext.REQUEST_SCOPE );
        pageContext.setAttribute( getId(),
                                  o );
    }

    /**
     *  {@inheritDoc}
     */
    public int doEndTag()
    {
        // Return back to the original.
        pageContext.setAttribute( VWBBaseTag.ATTR_CONTEXT,
                                  vwbcontext,
                                  PageContext.REQUEST_SCOPE );

        return EVAL_PAGE;
    }

    /**
     *  {@inheritDoc}
     */
    public int doAfterBody()
    {
        if( bodyContent != null )
        {
            try
            {
                JspWriter out = getPreviousOut();
                out.print(bodyContent.getString());
                bodyContent.clearBody();
            }
            catch( IOException e )
            {
                LOG.error("Unable to get inner tag text", e);
                // FIXME: throw something?
            }
        }

        if( m_iterator != null && m_iterator.hasNext() )
        {
            buildContext();
            return EVAL_BODY_BUFFERED;
        }

        return SKIP_BODY;
    }
    
    /**
     *  In case your tag throws an exception at any point, you can
     *  override this method and implement a custom exception handler.
     *  <p>
     *  By default, this handler does nothing.
     *  
     *  @param arg0 The Throwable that the tag threw
     *  
     *  @throws Throwable I have no idea why this would throw anything
     */
    public void doCatch(Throwable arg0) throws Throwable
    {
    }

    /**
     *  Executed after the tag has been finished.  This is a great place
     *  to put any cleanup code.  However you <b>must</b> call super.doFinally()
     *  if you override this method, or else some of the things may not
     *  work as expected.
     */
    public void doFinally()
    {
        resetIterator();
        m_iterator = null;
        m_pageName = null;
        vwbcontext = null;        
    }

}
