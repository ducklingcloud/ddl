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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

public final class PictureCheckCodeUtil {

    /**
     * 验证request中保存的验证码是否正确，如果验证错误移除验证码，重新生成
     * @param request
     * @param code
     * @param type
     * @param reflesh true 去掉type类型session验证码，false不移除
     * @return
     */
    public static boolean checkCode(HttpServletRequest request, String code,String type,boolean reflesh){
        if(StringUtils.isEmpty(type)||StringUtils.isEmpty(code)){
            return false ;
        }
        String c = (String)request.getSession().getAttribute(type);
        if(!StringUtils.isEmpty(c)&&c.equalsIgnoreCase(code)){
            if(reflesh){
                request.getSession().removeAttribute(type);
            }
            return true;
        }else{
            request.getSession().removeAttribute(type);
            return false ;
        }

    }
}
