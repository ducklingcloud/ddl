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

import org.apache.commons.lang.StringUtils;

/**
 *  Includes body, if the request context matches.
 *
 *  @author Yong Ke
 */
public class CheckRequestContextTag
        extends VWBBaseTag
{
    private static final long serialVersionUID = 0L;

    private String m_context;
    private String[] m_contextList = {};


    public void initTag()
    {
        super.initTag();
        m_context = null;
        m_contextList = new String[0];
    }

    public String getContext()
    {
        return m_context;
    }

    public void setContext( String arg )
    {
        m_context = arg;

        m_contextList = StringUtils.split(arg,'|');
    }

    public final int doVWBStart()
            throws IOException,
            ProviderException
    {
        for(int i = 0; i < m_contextList.length; i++ )
        {
            String ctx = vwbcontext.getURLPattern();

            String checkedCtx = m_contextList[i];

            if( checkedCtx.length() > 0 )
            {
                if( checkedCtx.charAt(0) == '!' )
                {
                    if( !ctx.equalsIgnoreCase(checkedCtx.substring(1) ) )
                    {
                        return EVAL_BODY_INCLUDE;
                    }
                }
                else if( ctx.equalsIgnoreCase(m_contextList[i]) )
                {
                    return EVAL_BODY_INCLUDE;
                }
            }
        }

        return SKIP_BODY;
    }
}
