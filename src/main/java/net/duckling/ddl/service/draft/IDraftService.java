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

package net.duckling.ddl.service.draft;

import net.duckling.ddl.service.resource.PageRender;

/**
 * @date 2011-7-15
 * @author Clive Lee
 */
public interface IDraftService {
    void updateAutoSaveDraft(int tid, PageRender page,String uid);
    void updateManualSaveDraft(int tid,PageRender page,String uid);
    void clearAutoSaveDraft(int tid,int pid,String uid);
    void clearManualSaveDraft(int tid,int pid,String uid);
    Draft getAutoSaveDraft(int tid,int pid,String uid);
    Draft getManualSaveDraft(int tid,int pid,String uid);
    Draft getLastestDraft(int teamId, Integer pid, String currentUid);
}
