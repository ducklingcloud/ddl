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

package net.duckling.ddl.service.contact;

/**
 * @date 2011-11-15
 * @author JohnX
 */
public final class ContactConstants {
    private ContactConstants(){}
    //item in personal contacts
    public static final int PERSON_CONTACT = 0;
    //item in team contacts
    public static final int TEAM_CONTACT = 4;
    //item has name collision with another or more
    public static final int NAME_COLLISION = 1;
    //item has Email collision with another or more
    public static final int EMAIL_COLLISION = 2;

    //personal contact name collision with another item, 0 | 1
    public static final int PERSON_NAME_COLLISION = 1;
    //personal contact email collision with another item, 0 | 2
    public static final int PERSON_EMAIL_COLLISION = 2;

    public static final int TEAM_NAME_COLLISION = 5;
    public static final int TEAM_EMAIL_COLLISION = 6;

    //personal contact name and email collision with another item, 0 | 1 | 2
    public static final int PERSON_COLLISION = 3;
    public static final int TEAM_COLLISION = 7;
}
