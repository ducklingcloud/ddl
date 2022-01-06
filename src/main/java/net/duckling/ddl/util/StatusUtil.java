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

package net.duckling.ddl.util;

/**
 * @date 2011-6-27
 * @author Clive Lee
 */
public final class StatusUtil {
    private StatusUtil(){}
    public static final String IGNORE = "IGNORED";
    public static final String ACCEPT = "ACCEPT";
    public static final String INVALID = "INVALID";
    public static final String WAITING = "WAITING";

    public static boolean isWaiting(String status) {
        return StatusUtil.WAITING.equals(status);
    }

    public static boolean isAccept(String status) {
        return StatusUtil.ACCEPT.equals(status);
    }

    public static boolean isIgnore(String status) {
        return StatusUtil.IGNORE.equals(status);
    }

    public static boolean isInvalid(String status) {
        return StatusUtil.INVALID.equals(status);
    }

}
