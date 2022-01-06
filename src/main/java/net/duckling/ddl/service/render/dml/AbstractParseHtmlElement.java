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

import java.io.PrintWriter;
import java.io.Writer;

import org.jdom.Element;
/**
 * Introduction Here.
 * @date 2010-3-8
 * @author 狄
 */
public abstract class AbstractParseHtmlElement{
    private Writer moutTimmer = new WhitespaceTrimWriter();
    private PrintWriter mout = new PrintWriter( moutTimmer );

    Html2Dml h2d=new Html2Dml();

    public abstract  void printElement(Element e,Html2DmlEngine html2dmlengine);

    public abstract  void printAttribute(Element e,Html2DmlEngine html2dmlengine);

    public Writer getMoutTimmer()
    {
        return moutTimmer;
    }

    public void setMoutTimmer(Writer timmer)
    {
        moutTimmer = timmer;
    }

    public PrintWriter getMout()
    {
        return mout;
    }

    public void setMout(PrintWriter mout)
    {
        this.mout = mout;
    }
}
