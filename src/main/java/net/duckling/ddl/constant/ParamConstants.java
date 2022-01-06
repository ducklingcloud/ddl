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
package net.duckling.ddl.constant;
/**期望记录所有param的，key和type
 * @author lvly
 * @sicne  2012-07-26
 * */
public class ParamConstants {
    /**用户个性 设置*/
    public static final class UserPreferenceType{
        /**TYPE*/
        public static final String TYPE="userPreference";
        /**默认使用名称*/
        public static final String KEY_NAME_TAG="userNameTag";
        /**可能的值*/
        public static final String VALUE_NAME_TAG_TRUE="true";
        public static final String VALUE_NAME_TAG_FALSE="false";
    }
    /**任务归档设置*/
    public static final class UserTaskType{
        /**TYPE*/
        public static final String TYPE="taskDustbin";

        /**默认KEY使用名称 缺省，应该用tid来充当*/

        /**默认value值缺省，应该使用Task.DUSTBIN*/

    }
    /**用户分享邮件提醒设置*/
    public static final class NoticeEmailShareType{
        /**TYPE */
        public static final String TYPE="noticeEmailSharedType";

        /**KEY,这里应该用tid，rid用uid，便于查询一个用户下的所有项目*/

        /**默认value是否打钩*/
        public static final String VALUE_CHECKED="checked";
        public static final String VALUE_UN_CHECKED="unchecked";
    }
    /***用户所有动态汇总邮件提醒*/
    public static final class NoticeEmailAllType{
        /** Type* */
        public static final String TYPE="noticeEmailAllType";

        /**KEY,同上类*/

        /**默认value是否打钩*/
        public static final String VALUE_CHECKED="checked";
        public static final String VALUE_UN_CHECKED="unchecked";

    }
}
