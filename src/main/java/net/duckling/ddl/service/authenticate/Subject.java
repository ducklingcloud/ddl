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

package net.duckling.ddl.service.authenticate;

import java.security.Principal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;



/**
 * Container of Principals
 * @date Mar 6, 2010
 * @author xiejj@cnic.cn
 */
public class Subject {
    private HashSet<Principal> m_principals;
    private UserPrincipal m_user;
    public Subject(UserPrincipal user){
        m_principals = new HashSet<Principal>();
        m_user = user;
    }
    public Subject(UserPrincipal user, Collection<Principal> prins){
        m_principals = new HashSet<Principal>();
        m_principals.addAll(prins);
        m_user = user;
    }

    public UserPrincipal getCurrentUser(){
        return m_user;
    }

    public Set<Principal> getPrincipals(){
        return m_principals;
    }

    /**
     * @param guest
     */
    public void setCurrentUser(UserPrincipal user) {
        this.m_user=user;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(m_user.toString());
        sb.append(m_principals);
        return sb.toString();
    }
}
