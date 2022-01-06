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

public class ErrorMessage {
    private String message;
    private String title;
    private String tip;
    private String redirectURL;
    private String redirectMesaage;
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getTip() {
        return tip;
    }
    public void setTip(String tip) {
        this.tip = tip;
    }
    public String getRedirectURL() {
        return redirectURL;
    }
    public void setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
    }
    public String getRedirectMesaage() {
        return redirectMesaage;
    }
    public void setRedirectMesaage(String redirectMesaage) {
        this.redirectMesaage = redirectMesaage;
    }


}
