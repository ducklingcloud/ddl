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


/**
 * @date 2011-3-15
 * @author Clive Lee
 */
public class FileExtendTag extends VWBBaseTag {
    private static final long serialVersionUID = 1L;
    private String fileName;


    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public int doVWBStart() throws Exception {
        int index = fileName.lastIndexOf('.');
        if(index<0){
            pageContext.getOut().write("");
        }else{
            String extend = fileName.substring(index+1);
            pageContext.getOut().write(extend.toLowerCase());
        }
        return EVAL_PAGE;
    }

}
