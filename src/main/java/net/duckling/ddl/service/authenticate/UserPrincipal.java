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
import java.util.Arrays;
import java.util.Comparator;

/**
 * A lightweight, immutable Principal class. WikiPrincipals can be created with
 * and optional "type" to denote what type of user profile Principal it
 * represents (FULL_NAME, WIKI_NAME, LOGIN_NAME). Types are used to determine
 * suitable user and login Principals in classes like WikiSession. However, the
 * type property of a WikiPrincipal does not affect a WikiPrincipal's logical
 * equality or hash code; two WikiPrincipals with the same name but different
 * types are still considered equal.
 *
 * @date Feb 3, 2010
 * @author zzb
 */
public final class UserPrincipal implements Principal {

    /**
     * Represents an anonymous user. WikiPrincipals may be created with an
     * optional type designator: LOGIN_NAME, WIKI_NAME, FULL_NAME or
     * UNSPECIFIED.
     */
    public static final UserPrincipal GUEST = new UserPrincipal("Guest", "GUEST");

    /** WikiPrincipal type denoting a user's full name. */
    public static final String FULL_NAME = "fullName";

    /** WikiPrincipal type denoting a user's login name. */
    public static final String LOGIN_NAME = "loginName";

    /** WikiPrincipal type denoting a user's wiki name. */
    public static final String WIKI_NAME = "wikiName";

    /** Generic WikiPrincipal of unspecified type. */
    public static final String UNSPECIFIED = "unspecified";

    /** Static instance of Comparator that allows Principals to be sorted. */
    public static final Comparator COMPARATOR = new PrincipalComparator();

    private static final String[] VALID_TYPES;

    static {
        VALID_TYPES = new String[] { FULL_NAME, LOGIN_NAME, WIKI_NAME, UNSPECIFIED };
        Arrays.sort(VALID_TYPES);
    }

    private final String m_name;
    private final String m_fullname;
    private final String m_type;

    public UserPrincipal(String name) {
        m_name = name;
        m_fullname = name;
        m_type = UNSPECIFIED;
    }

    public UserPrincipal(String name, String fullname) {
        m_name = name;
        m_fullname = fullname;
        m_type = UNSPECIFIED;
    }

    /**
     * Constructs a new WikiPrincipal with a given name and optional type
     * designator. If the supplied <code>type</code> parameter is not
     * {@link #LOGIN_NAME}, {@link #FULL_NAME}, {@link #WIKI_NAME} or
     * {@link #WIKI_NAME}, this method throws an
     * {@link IllegalArgumentException}.
     *
     * @param name
     *            the name of the Principal
     * @param type
     *            the type for this principal, which may be {@link #LOGIN_NAME},
     *            {@link #FULL_NAME}, {@link #WIKI_NAME} or {@link #WIKI_NAME}.
     */
    public UserPrincipal(String name, String fullname, String type) {
        m_name = name;
        m_fullname = fullname;
        m_type = type;
    }

    /**
     * Returns the wiki name of the Principal.
     *
     * @return the name
     */
    public final String getName() {
        return m_name;
    }

    public final String getFullName() {
        return m_fullname;
    }

    /**
     * Two <code>WikiPrincipal</code>s are considered equal if their names are
     * equal (case-sensitive).
     *
     * @param obj
     *            the object to compare
     * @return the result of the equality test
     */
    public final boolean equals(Object obj) {
        if (obj == null || !(obj instanceof UserPrincipal)) {
            return false;
        }
        return m_name.equals(((UserPrincipal) obj).getName());
    }

    /**
     * The hashCode() returned for the WikiPrincipal is the same as for its
     * name.
     *
     * @return the hash code
     */
    public final int hashCode() {
        return m_name.hashCode();
    }

    /**
     * Returns the Principal "type": {@link #LOGIN_NAME}, {@link #FULL_NAME},
     * {@link #WIKI_NAME} or {@link #WIKI_NAME}
     *
     * @return the type
     */
    public final String getType() {
        return m_type;
    }

    /**
     * Returns a human-readable representation of the object.
     *
     * @return the string representation
     */
    public final String toString() {
        return "[WikiPrincipal (" + m_type + "): " + getName() + "]";
    }

}
