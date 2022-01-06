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
package net.duckling.ddl.exception;

public class NoEnoughSpaceException extends Exception {
    private int tid;
    /**
     *
     */
    private static final long serialVersionUID = -8065839820566847250L;

    public NoEnoughSpaceException(String msg){
        super(msg);
    }

    public NoEnoughSpaceException(){
        super();
    }
    public NoEnoughSpaceException(String msg,Throwable e){
        super(msg, e);
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }


}
