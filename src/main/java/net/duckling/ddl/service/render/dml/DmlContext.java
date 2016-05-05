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

import java.util.Map;

import net.duckling.ddl.service.tobedelete.Page;

/**
 * Introduction Here.
 * 
 * @date 2010-3-8
 * @author 狄
 */
public interface DmlContext {
    String getBaseUrl();

    String getViewURL(int id);

    String getEditURL(int id);

    String getURL(String context, String page, String params);// type key

    String getDdataSiteTableName(String tablename);

    String removeDdataSiteTableName(String tablename);

    Page getViewPort(int resourceId);

    boolean isInternalURL(String url);

    Map<String, String> resolve(String url);
}
