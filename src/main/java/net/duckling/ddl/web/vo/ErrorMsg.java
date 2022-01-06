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
package net.duckling.ddl.web.vo;

/**
 * 错误对象
 * @author Brett
 *
 */
public class ErrorMsg {

    /**
     * common
     */
    public static ErrorMsg URI_NOT_FOUND = new ErrorMsg(1000, "uri_not_found"); //404
    public static ErrorMsg UNKNOW_ERROR = new ErrorMsg(1001, "unknow_error"); //500
    public static ErrorMsg NEED_PERMISSION = new ErrorMsg(1002, "need_permission"); //403
    public static ErrorMsg MISSING_PARAMETER = new ErrorMsg(1003, "missing_parameter"); //400
    public static ErrorMsg TYPE_MISMATCH = new ErrorMsg(1004, "type_mismatch"); //400


    /**
     * resource
     */
    public static ErrorMsg NO_ENOUGH_SPACE = new ErrorMsg(2001, "no_enough_space"); //400
    public static ErrorMsg NOT_FOUND = new ErrorMsg(2002, "not_found"); //400
    public static ErrorMsg EXISTED = new ErrorMsg(2003, "existed"); //400

    /**
     * team
     */
    public static ErrorMsg TEAM_CODE_WRONG = new ErrorMsg(3001, "team_code_wrong"); //400
    public static ErrorMsg TEAM_CODE_ALREADY_EXIST = new ErrorMsg(3002, "team_code_already_exist"); //400
    public static ErrorMsg DISPLAY_NAME_WRONG = new ErrorMsg(3003, "display_name_wrong"); //400
    public static ErrorMsg ACCESS_TYPE_WRONG = new ErrorMsg(3004, "access_type_wrong"); //400
    public static ErrorMsg TEAM_AUTH_WRONG = new ErrorMsg(3005, "team_auth_wrong"); //400

    public ErrorMsg(int errno, String name){
        this(errno, name, "");
    }

    public ErrorMsg(int errno, String name, String msg){
        this.errno = errno;
        this.name = name;
        this.msg = msg;
    }

    private int errno;
    private String name;
    private String msg;

    public int getErrno() {
        return errno;
    }
    public void setErrno(int errno) {
        this.errno = errno;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
}
