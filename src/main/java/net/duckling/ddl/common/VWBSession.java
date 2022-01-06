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
package net.duckling.ddl.common;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.duckling.ddl.service.authenticate.GroupPrincipal;
import net.duckling.ddl.service.authenticate.Role;
import net.duckling.ddl.service.authenticate.Subject;
import net.duckling.ddl.service.authenticate.UserPrincipal;



public class VWBSession {
    /** the generic list's key of messages */
    private static final String ALL = "*";

    /** An anonymous user's session status. */
    public static final String ANONYMOUS = "anonymous";

    /** An asserted user's session status. */
    public static final String ASSERTED = "asserted";

    /** An authenticated user's session status. */
    public static final String AUTHENTICATED = "authenticated";

    private static final int DOT = 46;

    private static final int NINE = 57;

    private static final int ONE = 48;
    private static final String SESSION_KEY = "vwbsession";

    private Principal[] standardPrincipal;
    private boolean changed=true;
    public Principal[] getStandardPrincipals(){
        return standardPrincipal;
    }

    public VWBSession(){
        m_subject= new Subject(UserPrincipal.GUEST);
        m_subject.getPrincipals().add(UserPrincipal.GUEST);
        m_subject.getPrincipals().add(Role.ALL);
        m_subject.getPrincipals().add(Role.ANONYMOUS);
        status=ANONYMOUS;
    }

    public static VWBSession findSession(PortletRequest request){
        PortletSession psession = request.getPortletSession();
        VWBSession vwbsession = (VWBSession) psession.getAttribute(SESSION_KEY, PortletSession.APPLICATION_SCOPE);
        return vwbsession;
    }

    public static VWBSession findSession(HttpServletRequest request) {
        if (request == null)
        {
            return null;
        }
        HttpSession session = request.getSession();
        VWBSession vwbsession = (VWBSession) session.getAttribute(SESSION_KEY);
        if (vwbsession == null) {
            vwbsession = new VWBSession();
            session.setAttribute(SESSION_KEY, vwbsession);
        }
        return vwbsession;
    }

    public static String getCurrentUid(HttpServletRequest request) {
        VWBSession vwbsession = findSession(request);
        if(vwbsession!=null){
            return vwbsession.getCurrentUser().getName();
        }else{
            return null;
        }
    }
    public static String getCurrentUidName(HttpServletRequest request){
        VWBSession vwbsession = findSession(request);
        if(vwbsession!=null){
            Principal p = vwbsession.getCurrentUser();
            if(p instanceof UserPrincipal){
                UserPrincipal u = (UserPrincipal)p;
                return u.getFullName();
            }else{
                return p.getName();
            }
        }else{
            return null;
        }
    }

    protected static final boolean isIPV4Address(String name) {
        if (name.charAt(0) == DOT || name.charAt(name.length() - 1) == DOT) {
            return false;
        }

        int[] addr = new int[] { 0, 0, 0, 0 };
        int currentOctet = 0;
        for (int i = 0; i < name.length(); i++) {
            int ch = name.charAt(i);
            boolean isDigit = ch >= ONE && ch <= NINE;
            boolean isDot = ch == DOT;
            if (!isDigit && !isDot) {
                return false;
            }
            if (isDigit) {
                addr[currentOctet] = 10 * addr[currentOctet] + (ch - ONE);
                if (addr[currentOctet] > 255) {
                    return false;
                }
            } else if (name.charAt(i - 1) == DOT) {
                return false;
            } else {
                currentOctet++;
            }
        }
        return currentOctet == 3;
    }

    private boolean fullmode;

    private Subject m_subject;

    private final Map<String, Set<String> > messages = new HashMap<String, Set<String> >();

    private String status = AUTHENTICATED;

    public boolean hasPrincipal(Principal prin){
        return m_subject.getPrincipals().contains(prin);
    }
    /**
     * Adds a message to the generic list of messages associated with the
     * session. These messages retain their order of insertion and remain until
     * the {@link #clearMessages()} method is called.
     * @param message the message to add; if <code>null</code> it is ignored.
     */
    public final void addMessage(String message) {
        addMessage(ALL, message);
    }

    /**
     * Adds a message to the specific set of messages associated with the
     * session. These messages retain their order of insertion and remain until
     * the {@link #clearMessages()} method is called.
     * @param topic the topic to associate the message to;
     * @param message the message to add
     */
    public final void addMessage(String topic, String message) {
        if (topic == null) {
            throw new IllegalArgumentException(
                "addMessage: topic cannot be null.");
        }
        if (message == null) {
            message = "";
        }
        Set<String> msgSet = messages.get(topic);
        if (msgSet == null) {
            msgSet = new LinkedHashSet<String>();
            messages.put(topic, msgSet);
        }
        msgSet.add(message);
        changed = true;
    }

    /**
     * Clears all messages associated with this session.
     */
    public final void clearMessages() {
        messages.clear();
    }

    /**
     * Clears all messages associated with a session topic.
     * @param topic the topic whose messages should be cleared.
     */
    @SuppressWarnings("rawtypes")
    public final void clearMessages(String topic) {
        Set msgSet = messages.get(topic);
        if (msgSet != null) {
            msgSet.clear();
        }
        changed = true;
    }

    public Principal getCurrentUser() {
        return m_subject.getCurrentUser();
    }

    /**
     * Returns all generic messages associated with this session.
     * The messages stored with the session persist throughout the
     * session unless they have been reset with {@link #clearMessages()}.
     * @return the current messsages.
     */
    public final String[] getMessages() {
        return getMessages(ALL);
    }

    /**
     * Returns all messages associated with a session topic.
     * The messages stored with the session persist throughout the
     * session unless they have been reset with {@link #clearMessages(String)}.
     * @return the current messsages.
     * @param topic The topic
     */
    public final String[] getMessages(String topic) {
        Set<String> msgSet = messages.get(topic);
        if (msgSet == null || msgSet.size() == 0) {
            return new String[0];
        }
        return msgSet.toArray(new String[messages.size()]);
    }

    public Principal[] getPrincipals() {
        return m_subject.getPrincipals().toArray(new Principal[0]);
    }

    public String getStatus() {
        return status;
    }

    public boolean isAnonymous() {
        Set<Principal> principals = m_subject.getPrincipals();
        return principals.contains(Role.ANONYMOUS)
                || principals.contains(UserPrincipal.GUEST)
                || isIPV4Address(m_subject.getCurrentUser().getName());
    }

    public final boolean isAsserted() {
        return m_subject.getPrincipals().contains(Role.ASSERTED);
    }
    public void invalidate(){
        m_subject.getPrincipals().clear();
        m_subject.setCurrentUser(UserPrincipal.GUEST);
        m_subject.getPrincipals().clear();
        Set<Principal> principals=m_subject.getPrincipals();

        principals.clear();
        principals.add(UserPrincipal.GUEST);
        principals.add(Role.ALL);
        principals.add(Role.ANONYMOUS);
        principals.add(UserPrincipal.GUEST);

        status=ANONYMOUS;

        attributes.clear();
        standardPrincipal=null;
    }


    public boolean isAuthenticated() {
        // If Role.AUTHENTICATED is in principals set, always return true.
        if (m_subject.getPrincipals().contains(Role.AUTHENTICATED)) {
            return true;
        }

        // With non-JSPWiki LoginModules, the role may not be there, so
        // we need to add it if the user really is authenticated.
        if (!isAnonymous() && !isAsserted()) {
            // Inject AUTHENTICATED role
            m_subject.getPrincipals().add(Role.AUTHENTICATED);
            return true;
        }

        return false;
    }

    public boolean isFullMode() {
        return fullmode;
    }

    public void setFullMode(boolean fullmode) {
        this.fullmode = fullmode;
        changed = true;
    }

    public void setStatus(String status) {
        this.status = status;
        changed = true;
    }
    public void setPrincipals(Collection<Principal> principals){
        Subject subject;
        try {
            subject = convertPrincipal(principals);
            setSubject(VWBSession.AUTHENTICATED, subject);
            standardPrincipal = principals.toArray(new Principal[0]);
        } catch (UnkownCredentialException e) {
            //Do Nothing
        }
    }
    private Subject convertPrincipal(
        Collection<java.security.Principal> prins)
            throws UnkownCredentialException {
        if (prins != null && prins.size() == 0) {
            return null;
        }

        ArrayList<Principal> result = new ArrayList<Principal>();
        Principal newprin = null;
        UserPrincipal user = null;
        for (java.security.Principal prin : prins) {
            newprin = null;
            if (prin instanceof cn.vlabs.commons.principal.UserPrincipal) {
                cn.vlabs.commons.principal.UserPrincipal oldu = (cn.vlabs.commons.principal.UserPrincipal) prin;
                user = new UserPrincipal(oldu.getName(), oldu
                                         .getDisplayName(), UserPrincipal.LOGIN_NAME);
                newprin=user;
            }
            if (prin instanceof cn.vlabs.commons.principal.GroupPrincipal) {
                cn.vlabs.commons.principal.GroupPrincipal oldg = (cn.vlabs.commons.principal.GroupPrincipal) prin;
                newprin = new GroupPrincipal(oldg.getName());
            }
            if (prin instanceof cn.vlabs.commons.principal.RolePrincipal) {
                cn.vlabs.commons.principal.RolePrincipal oldr = (cn.vlabs.commons.principal.RolePrincipal) prin;
                String roleName = oldr.getGroupName() + "."
                        + oldr.getShortName();
                newprin = new Role(roleName);
            }
            if (newprin != null)
            {
                result.add(newprin);
            }
        }
        result.add(Role.AUTHENTICATED);
        result.add(Role.ALL);
        if (user == null)
        {
            throw new UnkownCredentialException("User Principal not found");
        }
        return new Subject(user, result);
    }
    public void setSubject(String status, Subject subject) {
        if (subject == null)
        {
            throw new IllegalArgumentException("Subject can't be null");
        }
        this.m_subject = subject;
        this.status = status;
        this.attributes.clear();
        changed = true;
    }


    private final Map<String, Object> attributes = Collections.synchronizedMap(new HashMap<String, Object>());

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public void setAttribute(String key, Object value){
        attributes.put(key, value);
        changed = true;
    }
    public Object removeAttribute(String key){
        return attributes.remove(key);
    }

    private String s;
    @Override
    public String toString() {
        if(s==null||changed){
            StringBuilder sb = new StringBuilder();
            if(m_subject!=null){
                sb.append(m_subject.getCurrentUser().getName());
            }
            s= sb.toString();
            changed = false;
        }
        return s;
    }

}
