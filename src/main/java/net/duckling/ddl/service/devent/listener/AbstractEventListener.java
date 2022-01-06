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

package net.duckling.ddl.service.devent.listener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.devent.DEvent;
import net.duckling.ddl.service.devent.IDEventService;
import net.duckling.ddl.service.devent.INoticeService;
import net.duckling.ddl.service.devent.Notice;
import net.duckling.ddl.service.devent.NoticeRule;
import net.duckling.ddl.service.devent.impl.DEventListener;
import net.duckling.ddl.service.devent.impl.DEventRunner;
import net.duckling.ddl.service.mail.AoneMailService;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.subscribe.Subscription;
import net.duckling.ddl.service.subscribe.impl.SubscriptionServiceImpl;
import net.duckling.ddl.service.team.TeamMemberService;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.SimpleUser;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @date 2011-11-2
 * @author clive
 */
public abstract class AbstractEventListener implements DEventListener {

    private static final  Logger LOG = Logger.getLogger(AbstractEventListener.class);
    @Autowired
    private AoneMailService aoneMailService;
    @Autowired
    private AoneUserService aoneUserService;
    @Autowired
    private DEventRunner eventRunner;
    @Autowired
    private IDEventService eventService;
    @Autowired
    private INoticeService noticeService;
    @Autowired
    private IResourceService resourceService;
    @Autowired
    private SubscriptionServiceImpl subscription;
    @Autowired
    private TeamMemberService teamMemberSerivce;

    protected List<Notice> noticeList;

    private void appendNoticeList(DEvent e,String[] recipient,String reason){
        if(recipient==null || recipient.length==0) {
            return;
        }
        Notice[] results = new Notice[recipient.length];
        for(int i=0;i<recipient.length;i++){
            results[i] = noticeService.convertToNotice(e.getEventBody(), recipient[i], reason);
        }
        if(this.noticeList == null){
            noticeList = new ArrayList<Notice>();
        }
        for (int i = 0; i < results.length; i++) {
            noticeList.add(results[i]);
        }

    }
    private String[] convertSubcriptions(List<Subscription> subs){
        if (subs == null){
            return new String[]{};
        }
        String[] results = new String[subs.size()];
        int index = 0;
        for (Subscription sub : subs){
            results[index++] = sub.getUserId();
        }
        return results;
    }

    private String[] getActorFollowers(DEvent e){
        int tid = e.getTid();
        List<Subscription> subs = subscription.getPersonSubscribers(tid, e.getEventBody().getActor());
        return convertSubcriptions(subs);
    }

    private String[] getDirectRecipients(DEvent e){
        String src = e.getEventBody().getRecipients();
        if(StringUtils.isEmpty(src)){
            return new String[1];
        }
        return src.split(",");
    }

    private String[] getTargetFollowers(DEvent e){
        int tid = e.getTid();
        List<Subscription> subs = subscription.getPageSubscribers(tid, Integer.parseInt(e.getEventBody().getTarget().getId()));
        return convertSubcriptions(subs);
    }

    private String[] getTargetOwners(DEvent e){
        int rid = Integer.parseInt(e.getEventBody().getTarget().getId());
        String type = e.getEventBody().getTarget().getType();
        if(type.equals(LynxConstants.TYPE_PAGE)){
            Resource page = resourceService.getResource(rid);
            return (page!=null)?new String[]{page.getCreator()}:new String[]{};
        }else if(type.equals(LynxConstants.TYPE_FILE)){
            Resource file = resourceService.getResource(rid);
            return (file!=null)?new String[]{file.getCreator()}:new String[]{};
        }else
        {
            return null;
        }
    }

    private String[] getTeamMembers(int tid) {
        List<SimpleUser> teamMembers = teamMemberSerivce.getTeamMembersOrderByName(tid);
        if(teamMembers==null || teamMembers.size()==0){
            return new String[]{};
        }
        String[] array = new String[teamMembers.size()];
        for(int i=0;i<array.length;i++){
            array[i] = teamMembers.get(i).getUid();
        }
        return array;
    }

    private void saveDEvent(DEvent e){
        int eventId = eventService.writeEvent(e.getEventBody());
        e.getEventBody().setId(eventId);
    }

    protected abstract void addDifferenceNoticeToHandle(DEvent e);

    protected void appendCommentNotice(DEvent e) {
        String[] followers = getTargetFollowers(e);
        appendNoticeList(e, followers, NoticeRule.REASON_CONCERN);
        noticeService.increaseNoticeCount(followers, e.getEventBody().getTid(), NoticeRule.MONITOR_NOTICE,e.getEventBody().getId());
        String[] actors = getActorFollowers(e);
        appendNoticeList(e, actors, NoticeRule.REASON_FOLLOW);
        noticeService.increaseNoticeCount(actors, e.getEventBody().getTid(), NoticeRule.MONITOR_NOTICE,e.getEventBody().getId());
    }
    protected void appendConcernNotice(DEvent e){
        String[] concerns = getTargetFollowers(e);
        appendNoticeList(e,getTargetFollowers(e),NoticeRule.REASON_CONCERN);
        noticeService.increaseNoticeCount(concerns, e.getEventBody().getTid(), NoticeRule.MONITOR_NOTICE,e.getEventBody().getId());
    }

    protected void appendFollowNotice(DEvent e){
        String[] followers = getActorFollowers(e);
        appendNoticeList(e,followers,NoticeRule.REASON_FOLLOW);
        noticeService.increaseNoticeCount(followers, e.getEventBody().getTid(), NoticeRule.MONITOR_NOTICE,e.getEventBody().getId());
    }

    protected void appendHistoryNotice(DEvent e){
        appendNoticeList(e,new String[]{e.getEventBody().getActor()},NoticeRule.REASON_HISTORY);
    }

    protected void appendMentionNotice(DEvent e){
        String[] recipients = e.getEventBody().getRecipients().split(",");
        appendNoticeList(e,recipients,NoticeRule.PERSON_NOTICE);
        noticeService.increaseNoticeCount(recipients, e.getEventBody().getTid(), NoticeRule.PERSON_NOTICE,e.getEventBody().getId());
        aoneMailService.sendMentionEmail(e,recipients);
    }

    protected void appendOwnerNotice(DEvent e){
        String[] owner = getTargetOwners(e);
        if(owner!=null && !e.getEventBody().getActor().equals(owner[0])){
            appendNoticeList(e,getTargetOwners(e),NoticeRule.REASON_OWNER);
            noticeService.increaseNoticeCount(owner, e.getEventBody().getTid(), NoticeRule.PERSON_NOTICE,e.getEventBody().getId());
        }
    }

    protected void appendRecommendNotice(DEvent e){
        String[] recipients = getDirectRecipients(e);
        appendNoticeList(e,recipients,NoticeRule.REASON_RECOMMEND);
        noticeService.increaseNoticeCount(recipients, e.getEventBody().getTid(), NoticeRule.PERSON_NOTICE,e.getEventBody().getId());
        Set<String> email = new HashSet<String>();
        if(recipients!=null){
            for(String r :recipients){
                SimpleUser user = aoneUserService.getSimpleUserByUid(r);
                if(user!=null){
                    email.add(user.getEmail());
                }
            }
        }
        String [] emails = email.toArray(new String[email.size()]);
        aoneMailService.sendEmail(e,emails);
    }

    protected void appendReplyNotice(DEvent e){
        String[] recipients = getDirectRecipients(e);
        appendNoticeList(e,recipients,NoticeRule.REASON_REPLY);
        noticeService.increaseNoticeCount(recipients, e.getEventBody().getTid(), NoticeRule.PERSON_NOTICE,e.getEventBody().getId());
    }

    //add by lvly@2012-06-13
    protected void appendTaskTakersNotice(DEvent e){
        if(StringUtils.isEmpty(e.getEventBody().getRecipients())){
            return;
        }
        appendNoticeList(e,getDirectRecipients(e),NoticeRule.REASON_TASK);
        noticeService.increaseNoticeCount(getDirectRecipients(e), e.getEventBody().getTid(), NoticeRule.PERSON_NOTICE,e.getEventBody().getId());
    }

    protected void appendTeamNotice(DEvent e){
        appendNoticeList(e,new String[]{e.getEventBody().getTid()+""},NoticeRule.REASON_TEAM);
        int tid = e.getEventBody().getTid();
        String[] teamMembers = getTeamMembers(tid);
        noticeService.increaseNoticeCount(teamMembers,tid,NoticeRule.TEAM_NOTICE,e.getEventBody().getId());
    }

    protected void handleNoticeList(DEvent e){
        noticeService.writeNotification(noticeList);
    }

    protected void registQueue(String queueName){
        eventRunner.registListener(queueName, this);
    }

    public void handle(DEvent e){
        try{
            saveDEvent(e);
            addDifferenceNoticeToHandle(e);
            handleNoticeList(e);
        }catch(Exception ex){
            LOG.error("Write database contains some mistakes.");
            LOG.error("Message:"+ex.getMessage(),ex);
        }finally{
            if(noticeList!=null){
                noticeList.clear();
            }
        }
    }

}
