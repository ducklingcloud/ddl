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

package net.duckling.ddl.service.invitation;

import java.util.List;



/**
 * @date 2012-2-17
 * @author clive
 */
public interface InvitationService {

    Invitation getInvitationInstance(String encode, String id);

    List<Invitation> getInvitationListByTeam(int tid);

    List<Invitation> getInvitationListByUser(String user);

    int saveInvitation(Invitation instance);

    void saveInvites(List<Invitation> array);

    boolean updateInviteStatus(String encode, String id, String status);

    /**
     * 建状态为waiting置为接受
     * @param tid
     * @param uid
     * @return
     */
    boolean updateWaiteToAccept(int tid,String uid);

    int getInvitationCount(String user);

    Invitation getExistValidInvitation(String user, int tid);
}
