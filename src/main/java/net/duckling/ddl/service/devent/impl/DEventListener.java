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
package net.duckling.ddl.service.devent.impl;

import net.duckling.ddl.service.devent.DEvent;

public interface DEventListener {
    public static final String FILE_EVENT="FileEvent";
    public static final String PAGE_EVENT="PageEvent";
    public static final String TASK_EVENT="TaskEvent";
    public static final String FOLDER_EVENT = "FolderEvent";
    void handle(DEvent e);
}
