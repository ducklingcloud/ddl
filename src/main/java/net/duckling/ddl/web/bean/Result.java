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
package net.duckling.ddl.web.bean;

import net.duckling.ddl.util.JsonUtil;


public class Result<T> {
    public static final String CODE_SUCCESS = "200";
    public static final String CODE_WARN = "201";
    public static final String CODE_NO_ENOUGH_SPACE = "210";
    public static final String CODE_FILE_LOCKED = "211";
    public static final String CODE_FILE_VERSION_CONFLICT = "212";
    public static final String CODE_FILE_EXISTED = "213";
    public static final String CODE_FILE_NEWEST = "214";
    public static final String CODE_FILE_NAME_CONFLICT = "215";

    public static final String CODE_UNAUTHORIZED = "401";
    public static final String CODE_NO_PERMISSION = "403";
    public static final String CODE_FILE_NOT_FOUND = "404";
    public static final String CODE_PARAM_ERROR = "410";
    public static final String CODE_ERROR = "500";

    public static final String MESSAGE_SUCCESS = "ok";
    public static final String MESSAGE_PARAM_ERROR = "parameters error.";
    public static final String MESSAGE_UNAUTHORIZED = "unauthorized.";
    public static final String MESSAGE_NO_PERMISSION = "access forbidden.";
    public static final String MESSAGE_FILE_NOT_FOUND = "file not found.";
    public static final String MESSAGE_FILE_LOCKED = "file locked.";
    public static final String MESSAGE_FILE_VERSION_CONFLICT = "file version conflict.";
    public static final String MESSAGE_FILE_EXISTED = "file existed.";
    public static final String MESSAGE_FILE_NAME_CONFLICT = "file name conflict.";
    public static final String MESSAGE_NO_ENOUGH_SPACE = "no enough space.";
    public static final String MESSAGE_ERROR = "internal server error.";

    private String code;
    private String message;
    private T result;

    public Result(){

    }

    public Result(T obj){
        this.code = Result.CODE_SUCCESS;
        this.message = Result.MESSAGE_SUCCESS;
        this.result = obj;
    };

    public Result(String code, String message){
        this.code = code;
        this.message = message;
    };

    public Result(String code, String message, T obj){
        this.code = code;
        this.message = message;
        this.result = obj;
    };

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public T getResult() {
        return result;
    }
    public void setResult(T result) {
        this.result = result;
    }

    public String toString(){
        return JsonUtil.getJSONString(this);
    }
}
