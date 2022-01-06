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
package net.duckling.ddl.service.resource;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.springframework.util.StringUtils;

import net.duckling.ddl.constant.LynxConstants;


public final class PageHelper {
    private PageHelper(){}
    public static Resource createNewPage(int tid, String title, String creator){
        Date curDate = new Date();
        Resource page = new Resource();
        page.setTid(tid);
        page.setStatus(LynxConstants.STATUS_AVAILABLE);
        page.setTitle(title);
        page.setCreateTime(curDate);
        page.setCreator(creator);
        page.setLastEditor(creator);
        page.setLastEditTime(curDate);
        page.setLastVersion(LynxConstants.INITIAL_VERSION);
        page.setItemType(LynxConstants.TYPE_PAGE);
        return page;
    }

    public static PageVersion createPageVersion(Resource page, String title, String editor, String editorName, String content){
        int version = page.getLastVersion()+1;
        Date editTime = (version==1)?(page.getCreateTime()):(new Date());
        PageVersion pageVersion = new PageVersion();
        pageVersion.setTid(page.getTid());
        pageVersion.setRid(page.getRid());
        pageVersion.setVersion(version);
        pageVersion.setTitle(title);
        pageVersion.setEditor(editor);
        pageVersion.setEditorName(editorName);
        pageVersion.setEditTime(editTime);
        pageVersion.setContent(content);
        return pageVersion;
    }

    public static void updatePage(Resource page, PageVersion pageVersion){
        page.setTitle(pageVersion.getTitle());
        page.setLastEditor(pageVersion.getEditor());
        page.setLastEditorName(pageVersion.getEditorName());
        page.setLastEditTime(pageVersion.getEditTime());
        page.setLastVersion(pageVersion.getVersion());
    }

    public static PageVersion createPageVersion(Resource meta, String content) {
        int version = meta.getLastVersion()+1;
        Date editTime = (version==1)?(meta.getCreateTime()):(new Date());
        PageVersion pageVersion = new PageVersion();
        pageVersion.setTid(meta.getTid());
        pageVersion.setRid(meta.getRid());
        pageVersion.setVersion(version);
        pageVersion.setTitle(meta.getTitle());
        pageVersion.setEditor(meta.getLastEditor());
        pageVersion.setEditorName(meta.getLastEditorName());
        pageVersion.setEditTime(editTime);
        pageVersion.setContent(content);
        pageVersion.setSize(getContentSize(content));
        return pageVersion;
    }

    public static long getContentSize(String content){
        if(StringUtils.isEmpty(content)){
            return 0;
        }else{
            try {
                return (long)content.getBytes("utf-8").length;
            } catch (UnsupportedEncodingException e) {
                return content.length();
            }
        }
    }

}
