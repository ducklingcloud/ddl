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
package net.duckling.ddl.service.team.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import net.duckling.ddl.constant.KeyConstants;
import net.duckling.ddl.service.authority.AuthorityService;
import net.duckling.ddl.service.authority.TeamAcl;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamMemberService;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.user.UserExt;
import net.duckling.falcon.api.mq.DFMQFactory;
import net.duckling.falcon.api.mq.DFMQMode;
import net.duckling.falcon.api.mq.IDFMessageHandler;
import net.duckling.falcon.api.mq.IDFSubscriber;
import net.duckling.falcon.api.mq.NotFoundHandlerException;
import net.duckling.vmt.api.domain.VmtGroup;
import net.duckling.vmt.api.domain.VmtUser;
import net.duckling.vmt.api.domain.message.MQBaseMessage;
import net.duckling.vmt.api.domain.message.MQCreateGroupMessage;
import net.duckling.vmt.api.domain.message.MQDeleteGroupMessage;
import net.duckling.vmt.api.domain.message.MQLinkUserMessage;
import net.duckling.vmt.api.domain.message.MQRefreshGroupMessage;
import net.duckling.vmt.api.domain.message.MQUnlinkUserMessage;
import net.duckling.vmt.api.domain.message.MQUpdateGroupMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
@Service
public class VMTMessageHandlerImpl {
    private class ReceiveThread implements Runnable {
        IDFSubscriber subscriber;

        public ReceiveThread(IDFSubscriber subscriber) {
            this.subscriber = subscriber;
        }

        @Override
        public void run() {
            try {
                subscriber.receive();
            } catch (NotFoundHandlerException e) {
                LOG.error("", e);
            }
        }

    }
    abstract class VmtMessageHandle implements IDFMessageHandler {

        /**
         * 处理消息
         *
         * @param message
         */
        public abstract void dealMessage(MQBaseMessage message);

        @Override
        public void handle(Object message, String routingKey) {
            try {
                if (message instanceof MQBaseMessage) {
                    dealMessage((MQBaseMessage) message);
                    LOG.info(((MQBaseMessage) message).toJsonString());
                } else {
                    LOG.error("MESSAGE type error :" + message);
                }
            } catch (Exception e) {
                LOG.error("", e);
            }
        }
    }
    class VmtTeamMessageHandle extends VmtMessageHandle {

        @Override
        public void dealMessage(MQBaseMessage message) {
            if (message instanceof MQCreateGroupMessage) {
                MQCreateGroupMessage createMessage = (MQCreateGroupMessage) message;
                createMessage.getPublishTime();
                VmtGroup group = createMessage.getGroup();
                Team team = teamService.getTeamByName(group.getSymbol());
                if (team != null) {
                    LOG.info(team.getDisplayName()
                             + " already exists; team info:" + team);
                    return;
                }
                VmtUser u = vmtTeamManager.getUidByUmtId(group.getCreator());
                if (u == null) {
                    LOG.error("vmt mq create team" + group.getSymbol()
                              + " and  the user '" + group.getCreator()
                              + "' query from vmt is null");
                } else {
                    int tid = createTeam(u.getCstnetId(), group.getSymbol(),
                                         group.getName(), group.getDn(),
                                         createMessage.getPublishTime());
                    addTeamAdmin(tid, u.getCstnetId(), u.getName());
                    LOG.info("vmt mq create team ;teamcode="
                             + group.getSymbol() + ";name=" + group.getName()
                             + ";creator=" + u.getCstnetId() + ";creatorName="
                             + u.getName());
                }
            } else if (message instanceof MQRefreshGroupMessage) {
                LOG.info("DDL now don't deal refresh message ;message bodys:"
                         + message.toJsonString());
            } else if (message instanceof MQUpdateGroupMessage) {
                doUpdateGroup((MQUpdateGroupMessage) message);
            } else if (message instanceof MQDeleteGroupMessage) {
                MQDeleteGroupMessage deleteMessage = (MQDeleteGroupMessage) message;
                deleteTeam(deleteMessage.getGroup().getSymbol());
            } else {
                LOG.info("the type " + message.getType() + message.getClass()
                         + "don't deal; message json body:"
                         + (message).toJsonString());
            }
        }

        public void doUpdateGroup(MQUpdateGroupMessage updateMessage) {
            VmtGroup group = updateMessage.getGroup();
            updateTeamName(group.getSymbol(), group.getName(),
                           updateMessage.getPublishTime());
            String[] admin = group.getAdmins();
            if (admin != null && admin.length > 0) {
                List<VmtUser> users = updateMessage.getAdmins();
                String[] uids = new String[users.size()];
                for(int i=0;i<users.size();i++){
                    uids[i] = users.get(i).getCstnetId();
                }
                updateTeamAdmins(group.getSymbol(), uids);
            }
            LOG.info("vmtMQ update " + group.getSymbol() + ";message json body"
                     + updateMessage.toJsonString());
        }

    }
    class VmtUserMessageHandle extends VmtMessageHandle {

        @Override
        public void dealMessage(MQBaseMessage message) {
            if (message instanceof MQLinkUserMessage) {
                MQLinkUserMessage linkMessage = (MQLinkUserMessage) message;
                if (linkMessage.isGroup()) {
                    List<VmtUser> users = linkMessage.getUsers();
                    addUserToTeam(linkMessage.getGroup().getSymbol(), users);
                }
            } else if (message instanceof MQUnlinkUserMessage) {
                MQUnlinkUserMessage unLink = (MQUnlinkUserMessage) message;
                if (unLink.isGroup()) {
                    List<VmtUser> users = unLink.getUsers();
                    removeUserFromTeam(unLink.getGroup().getSymbol(), users);
                }
            } else {
                LOG.info("The VMT MQ type " + message.getType()
                         + message.getClass() + " do nothing ; message body"
                         + (message).toJsonString());
            }
        }
    }
    private static final Logger LOG = Logger
            .getLogger(VMTMessageHandlerImpl.class);
    private static IDFSubscriber subscriber;
    @Autowired
    private AuthorityService authorityService;
    @Value("${duckling.mq.exchange}")
    private String mqExchange;
    @Value("${duckling.mq.host}")
    private String mqHost;
    @Value("${duckling.mq.password}")
    private String password;
    @Value("${duckling.mq.queuename}")
    private String queueName;
    @Autowired
    private TeamMemberService teamMemberService;

    @Autowired
    private TeamService teamService;
    @Value("${duckling.mq.username}")
    private String username;

    @Autowired
    private VMTTeamManager vmtTeamManager;

    private void addTeamAdmin(int tid, String uid, String name) {
        teamService.addTeamMembers(tid, new String[] { uid },
                                   new String[] { name },new String[]{Team.AUTH_ADMIN},false);
    }

    private void addUserToTeam(String teamCode, List<VmtUser> vUser) {
        Team team = teamService.getTeamByName(teamCode);
        List<String> users = new ArrayList<String>();
        List<String> usernames = new ArrayList<String>();
        List<String> auths = new ArrayList<String>();
        for (int i = 0; i < vUser.size(); i++) {
            VmtUser u = vUser.get(i);
            if(teamMemberService.isUserInTeam(team.getId(), u.getCstnetId())){
                continue;
            }
            users.add(u.getCstnetId());
            usernames.add(u.getName());
            auths.add(team.getDefaultMemberAuth());
        }
        String [] userArr = users.toArray(new String[0]);
        if(userArr.length>0){
            teamService.addTeamMembers(team.getId(), userArr, usernames.toArray(new String[0]),
                                       auths.toArray(new String[0]),false);
            LOG.info("vmt MQ sync add user " + arraryToString(userArr)
                     + ";team name :" + team.getDisplayName() + ";team info:" + team);
        }
    }

    private static String arraryToString(String[] ss) {
        if (ss != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (String s : ss) {
                sb.append(s).append(",");
            }
            if (sb.length() > 1) {
                sb.deleteCharAt(sb.length() - 1);
            }
            sb.append("]");
            return sb.toString();
        } else {
            return "";
        }
    }

    private int createTeam(String uid, String teamCode, String name, String dn,
                           Date createTime) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(KeyConstants.SITE_DISPLAY_NAME, name);
        params.put(KeyConstants.SITE_NAME_KEY, teamCode);
        params.put(KeyConstants.SITE_CREATOR, uid);
        params.put(KeyConstants.TEAM_TYPE, Team.COMMON_TEAM);
        params.put(KeyConstants.TEAM_ACCESS_TYPE, Team.ACCESS_PRIVATE);
        params.put(KeyConstants.SITE_DESCRIPTION, "");
        params.put(KeyConstants.TEAM_DEFAULT_MEMBER_AUTH, Team.AUTH_EDIT);
        return teamService.createAndStartTeam(uid, params, createTime, true);
    }

    private void deleteTeam(String teamCode) {
        Team team = teamService.getTeamByName(teamCode);
        List<UserExt> us = teamMemberService.getUserExtContacts(team.getId());
        String[] uids = new String[us.size()];
        for (int i = 0; i < us.size(); i++) {
            UserExt u = us.get(i);
            uids[i] = u.getUid();
        }
        teamService.removeMembers(team.getId(), uids,false);
        LOG.info("VMT sycn delete team: " + team.getDisplayName()
                 + "；remove user:" + arraryToString(uids) + ";team info :"
                 + team);
    }

    private void removeUserFromTeam(String teamCode, List<VmtUser> vusers) {
        Team team = teamService.getTeamByName(teamCode);
        String[] users = new String[vusers.size()];
        for (int i = 0; i < vusers.size(); i++) {
            VmtUser u = vusers.get(i);
            users[i] = u.getCstnetId();
        }
        teamService.removeMembers(team.getId(), users, false);
        LOG.info("VMT MQ remove user " + arraryToString(users) + " from team:"
                 + team.getDisplayName() + "; team info " + team);
    }

    private void updateTeamAdmins(String teamCode, String[] uid) {
        Team team = teamService.getTeamByName(teamCode);
        List<TeamAcl> acls = authorityService.getTeamAdminByTid(team.getId());
        Set<String> uSet = new HashSet<String>();
        for (String u : uid) {
            uSet.add(u);
        }
        List<String> removeAdmin = new ArrayList<String>();
        for (TeamAcl a : acls) {
            if (uSet.contains(a.getUid())) {
                uSet.remove(a.getUid());
            } else {
                removeAdmin.add(a.getUid());
            }
        }
        updateTeamAuth(team.getId(), uSet, Team.AUTH_ADMIN);
        updateTeamAuth(team.getId(), removeAdmin, team.getDefaultMemberAuth());

    }

    private void updateTeamAuth(int tid, Collection<String> uids, String auths) {
        if (!uids.isEmpty()) {
            String[] auth = new String[uids.size()];
            for (int i = 0; i < uids.size(); i++) {
                auth[i] = auths;
            }
            teamService.updateMembersAuthority(tid,
                                               uids.toArray(new String[uids.size()]), auth, true);
            if (Team.AUTH_ADMIN.equals(auths)) {
                LOG.info("VMT MQ add admin" + uids + " to team " + tid);
            } else {
                LOG.info("VMT MQ remove admin" + uids + " from team " + tid);

            }
        }
    }

    private void updateTeamName(String teamCode, String name, Date date) {
        teamService.updateTeamTitle(teamCode, name, null);
    }

    @PreDestroy
    public void destroy() {
        if (subscriber != null) {
            subscriber.close();
        }
    }

    @PostConstruct
    public void init() {
        if (subscriber == null) {
            subscriber = DFMQFactory.buildSubscriber(username, password,
                                                     mqHost, mqExchange, queueName + "-group", DFMQMode.TOPIC);
            subscriber.registHandler("#.group", new VmtTeamMessageHandle());
            subscriber.registHandler("#.user", new VmtUserMessageHandle());
            Runnable groupThream = new ReceiveThread(subscriber);
            Thread t = new Thread(groupThream);
            t.setName("groupThream");
            t.start();
        }
    }

}
