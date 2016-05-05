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
import java.io.Writer;
import java.util.regex.Pattern;

/**
 * Introduction Here.
 * @date 2010-3-8
 * @author 狄 diyanliang@cnic.cn
 */
public class WhitespaceTrimWriter extends Writer{

    private StringBuffer mResult = new StringBuffer();

    private StringBuffer mBuffer = new StringBuffer();

    private boolean mTrimMode = true;

    private static final Pattern ONLINE_PATTERN = Pattern.compile( ".*?\\n\\s*?", Pattern.MULTILINE );

    private boolean mCurrentlyOnLineBegin = true;

    public void flush()
    {
        if( mBuffer.length() > 0 )
        {
            String s = mBuffer.toString();
            s = s.replaceAll( "\r\n", "\n" );
            mResult.append( s );
            mBuffer = new StringBuffer();
        }
    }

    public boolean isWhitespaceTrimMode()
    {
        return mTrimMode;
    }

    public void setWhitespaceTrimMode( boolean trimMode )
    {
        if( mTrimMode != trimMode )
        {
            flush();
            mTrimMode = trimMode;
        }
    }

    public void write( char[] arg0, int arg1, int arg2 ) throws IOException
    {
        mBuffer.append( arg0, arg1, arg2 );
        mCurrentlyOnLineBegin = ONLINE_PATTERN.matcher( mBuffer ).matches();
    }

    public void close() throws IOException
    {}

    public String toString()
    {
        flush();
        return mResult.toString();
    }

    public boolean isCurrentlyOnLineBegin()
    {
        return mCurrentlyOnLineBegin;
    }
}
