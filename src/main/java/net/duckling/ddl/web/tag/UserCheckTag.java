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

package net.duckling.ddl.web.tag;

import java.io.IOException;

import net.duckling.ddl.common.VWBSession;
import cn.vlabs.commons.principal.UserPrincipal;

/**
 * Includes the content if an user check validates. This has been considerably
 * enhanced for 2.2. The possibilities for the "status"-argument are:
 * 
 * <ul>
 * <li>"anonymous" - the body of the tag is included if the user is completely
 * unknown (no cookie, no password)</li>
 * <li>"asserted" - the body of the tag is included if the user has either been
 * named by a cookie, but not been authenticated.</li>
 * <li>"authenticated" - the body of the tag is included if the user is
 * validated either through the container, or by our own authentication.</li>
 * <li>"assertionsAllowed" - the body of the tag is included if wiki allows
 * identities to be asserted using cookies.</li>
 * <li>"assertionsNotAllowed" - the body of the tag is included if wiki does
 * <i>not</i> allow identities to be asserted using cookies.</li>
 * <li>"containerAuth" - the body of the tag is included if the user is
 * validated through the container.</li>
 * <li>"customAuth" - the body of the tag is included if the user is validated
 * through our own authentication.</li>
 * <li>"known" - if the user is not anonymous</li>
 * <li>"notAuthenticated" - the body of the tag is included if the user is not
 * yet authenticated.</li>
 * </ul>
 * 
 * If the old "exists" -argument is used, it corresponds as follows:
 * <p>
 * <tt>exists="true" ==> status="known"<br>
 *  <tt>exists="false" ==> status="unknown"<br>
 * 
 *  It is NOT a good idea to use BOTH of the arguments.
 * 
 * @author Yong Ke
 */
public class UserCheckTag extends VWBBaseTag {
	private static final long serialVersionUID = 3256438110127863858L;
	private static final String ASSERTED = "asserted";
	private static final String AUTHENTICATED = "authenticated";
	private static final String ANONYMOUS = "anonymous";
	private static final String KNOWN = "known";
	private static final String NOT_AUTHENTICATED = "notauthenticated";

	private String m_status;

	public void initTag() {
		super.initTag();
		m_status = null;
	}

	public String getStatus() {
		return (m_status);
	}

	public void setStatus(String arg) {
		m_status = arg.toLowerCase();
	}

	/**
	 * @see com.ecyrd.jspwiki.tags.WikiTagBase#doWikiStartTag()
	 */
	public final int doVWBStart() throws IOException {
		VWBSession session = vwbcontext.getVWBSession();

		String status = session.getStatus();
		if (m_status != null) {
			if (ANONYMOUS.equals(m_status)) {
				if (status.equals(VWBSession.ANONYMOUS)) {
					return EVAL_BODY_INCLUDE;
				}
			} else if (AUTHENTICATED.equals(m_status)) {
				if (status.equals(VWBSession.AUTHENTICATED)) {
					return EVAL_BODY_INCLUDE;
				}
			} else if (ASSERTED.equals(m_status)) {
				if (status.equals(VWBSession.ASSERTED)) {
					return EVAL_BODY_INCLUDE;
				}
			} else if (KNOWN.equals(m_status)) {
				if (session.getCurrentUser() instanceof UserPrincipal) {
					return EVAL_BODY_INCLUDE;
				}
				return SKIP_BODY;
			} else if (NOT_AUTHENTICATED.equals(m_status)
					&& !status.equals(VWBSession.AUTHENTICATED)) {
				return EVAL_BODY_INCLUDE;
			}
		}

		return SKIP_BODY;
	}

}
