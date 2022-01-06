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
/**
 *
 */
package net.duckling.ddl.service.mail.impl;

/**
 *
 * @author lvly
 * @since 2012-11-27
 */
public class SimpleEmail {
    private String[] email;
    private String title;
    private String content;
    private String from;


    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String[] getEmail() {
        return email;
    }

    public void setEmail(String email) {
        setEmail(new String[]{email});
    }

    public void setEmail(String[] email) {
        this.email = email;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public SimpleEmail(String email,String title,String content){
        setEmail(email);
        this.title=title;
        this.content=content;
    }

    public SimpleEmail(String email,String title,String content,String from){
        setEmail(email);
        this.title=title;
        this.content=content;
        this.from = from;
    }
    public SimpleEmail(String[] email,String title,String content,String from){
        setEmail(email);
        this.title=title;
        this.content=content;
        this.from = from;
    }

}
