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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TryCatchFinally;

import net.duckling.ddl.common.VWBContext;

import org.apache.log4j.Logger;


/**
 *  This is a class that provides the same services as the WikiTagBase, but this time it
 *   works for the BodyTagSupport base class.
 * 
 *  @author Yong Ke
 *
 */
public abstract class VWBBodyTag extends BodyTagSupport
    implements TryCatchFinally
{
    protected VWBContext vwbcontext;
    static final   Logger    LOGGER = Logger.getLogger( VWBBodyTag.class );

    public int doStartTag() throws JspException
    {
    	 try
         {
         	vwbcontext = VWBContext.getContext((HttpServletRequest)pageContext.getRequest());

             if( vwbcontext == null )
             {
                 throw new JspException("VWBContext may not be NULL - serious internal problem!");
             }

             return doVWBStart();
         }
         catch( Exception e )
         {
             LOGGER.error( "Tag failed", e );
             throw new JspException( "Tag failed, check logs: "+e.getMessage() );
         }
         catch (Throwable e)
         {
             throw new JspException("Tag failed, check logs: " +e.getMessage());
         }
    }

    /**
     * A local stub for doing tags.  This is just called after the local variables
     * have been set.
     * @return As doStartTag()
     * @throws JspException
     * @throws IOException
     */
    public abstract int doVWBStart() throws JspException, IOException;

    public void doCatch(Throwable arg0) throws Throwable
    {
    }

    public void doFinally()
    {
    	vwbcontext = null;
    }  
    
    
}
