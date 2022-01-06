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
package net.duckling.ddl.service.sync;

public class ChunkUploadSession {
    public static final String FINISHED = "finished";

    public static final String APPENDING = "appending";

    private Long clbId;

    private String status;

    public ChunkUploadSession() {
    }

    public ChunkUploadSession(Long clbId, String status) {
        this.clbId = clbId;
        this.status = status;
    }

    public Long getClbId() {
        return clbId;
    }

    public void setClbId(Long clbId) {
        this.clbId = clbId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String toString() {
        return String.format("{clbId:%d,status:%s}", clbId, status);
    }
}
