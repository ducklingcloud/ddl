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

package net.duckling.ddl.service.devent.impl;

import java.util.Date;
import java.util.List;

import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.devent.DAction;
import net.duckling.ddl.service.devent.DEntity;
import net.duckling.ddl.service.devent.DEvent;
import net.duckling.ddl.service.devent.DEventBody;
import net.duckling.ddl.service.devent.EventDispatcher;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.task.Task;
import net.duckling.ddl.service.task.TaskTaker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DEventDispatcherImpl implements EventDispatcher {
    private static final String FILE_EVENT=DEventListener.FILE_EVENT;
    private static final String PAGE_EVENT=DEventListener.PAGE_EVENT;
    private static final String TASK_EVENT=DEventListener.TASK_EVENT;
    private static final String FOLDER_EVENT=DEventListener.FOLDER_EVENT;
    @Autowired
    private DEventRunner eventRunner;

    private DEventBody getDefaultPageEventBody(int tid,Resource meta,String type){
        return initEventBody(tid, meta.getLastEditor(),meta.getRid(),DEntity.DPAGE,meta.getTitle(),meta.getLastVersion(),type,null,null);
    }
    private DEventBody getDefaultFolderEventBody(int tid,Resource meta,String type){
        return initEventBody(tid, meta.getLastEditor(),meta.getRid(),DEntity.DFOLDER,meta.getTitle(),meta.getLastVersion(),type,null,null);
    }
    private DEventBody getDefaultFileEventBody(int tid,Resource meta,String type){
        return initEventBody(tid, meta.getLastEditor(),meta.getRid(),DEntity.DFILE,meta.getTitle(),meta.getLastVersion(),type,null,null);
    }
    private DEventBody initEventBody(int tid, String author, int targetId,
                                     String targetType,String targetName, int version, String operation, String message,
                                     String recipients) {
        DEventBody b = new DEventBody();
        b.setActor(author);
        DEntity d = new DEntity();
        d.setId(targetId + "");
        d.setType(targetType);
        d.setName(targetName);
        b.setTarget(d);
        b.setOperation(operation);
        b.setMessage(message);
        b.setOccurTime(new Date());
        b.setTargetVersion(version);
        b.setTid(tid);
        b.setRecipients(recipients);
        return b;
    }

    private void sendEvent(int tid, DEventBody body,String type) {
        DEvent event = new DEvent();
        event.setTid(tid);
        event.setEventType(type);
        event.setEventBody(body);
        eventRunner.raise(event);
    }

    private void sendFileCommentEvent(int tid,int pid,String title,String actor,int version,String message){
        DEventBody body = initEventBody(tid,actor,pid,DEntity.DFILE,title,version,DAction.COMMENT,message,null);
        sendEvent(tid,body,FILE_EVENT);
    }

    private void sendFileReplyEvent(int tid,int pid,String title,String author,int version,String message,String recipients){
        DEventBody body = initEventBody(tid,author,pid,DEntity.DFILE,title,version,DAction.REPLY,message,recipients);
        sendEvent(tid,body,PAGE_EVENT);
    }

    private void sendPageCommentEvent(int tid,int pid,String title,String actor,int version,String message){
        DEventBody body = initEventBody(tid,actor,pid,DEntity.DPAGE,title,version,DAction.COMMENT,message,null);
        sendEvent(tid,body,PAGE_EVENT);
    }

    private void sendPageEvent(int tid,DEventBody body){
        sendEvent(tid,body,PAGE_EVENT);
    }
    private void sendFolderEvent(int tid,DEventBody body){
        sendEvent(tid,body,FOLDER_EVENT);
    }
    private void sendPageReplyEvent(int tid,int pid,String title,String author,int version,String message,String recipients){
        DEventBody body = initEventBody(tid,author,pid,DEntity.DPAGE,title,version,DAction.REPLY,message,recipients);
        sendEvent(tid,body,PAGE_EVENT);
    }

    public void sendFileDeleteEvent(int tid,Resource file,String actor){
        DEventBody body = initEventBody(tid, actor, file.getRid(), DEntity.DFILE, file.getTitle(), file.getLastVersion(), DAction.DELETE, null, null);
        sendEvent(tid, body, FILE_EVENT);
    }
    public void sendFileModifyEvent(String title, int fid, String author,int version,int tid) {
        DEventBody body = initEventBody(tid,author,fid,DEntity.DFILE,title,version,DAction.MODIFY,null,null);
        sendEvent(tid, body,FILE_EVENT);
    }
    public void sendFileRecommendEvent(int tid, int fid,String title,String author,int version,String message,String recipients,String emailSendType){
        DEventBody body = initEventBody(tid,author,fid,DEntity.DFILE,title,version,DAction.RECOMMEND,message,recipients);
        body.setEmailSendType(emailSendType);
        sendEvent(tid,body,FILE_EVENT);
    }

    public void sendFileRecoverEvent(int tid,Resource file,String actor){
        DEventBody body = initEventBody(tid, actor, file.getRid(), DEntity.DFILE, file.getTitle(), file.getLastVersion(), DAction.RECOVER, null, null);
        sendEvent(tid, body, FILE_EVENT);
    }

    public void sendFileUploadEvent(String title, int fid, String author,int tid) {
        DEventBody body = initEventBody(tid,author,fid,DEntity.DFILE,title,1,DAction.UPLOAD,null,null);
        sendEvent(tid, body,FILE_EVENT);
    }

    public void sendPageCreateEvent(int tid, Resource resource){
        DEventBody body = getDefaultPageEventBody(tid,resource,DAction.CREATE);
        sendPageEvent(tid,body);
    }

    /**
     * 页面删除事件
     * @param site
     * @param page
     */
    public void sendPageDeleteEvent(int tid,Resource resource,String actor){
        DEventBody body = getDefaultPageEventBody(tid, resource, DAction.DELETE);
        body.setActor(actor);
        sendPageEvent(tid,body);
    }

    public void sendPageModifyEvent(int tid, Resource resource){
        DEventBody body = getDefaultPageEventBody(tid,resource,DAction.MODIFY);
        sendPageEvent(tid,body);
    }

    public void sendPageRecommendEvent(int tid,int pid,String title,String author,int version,String message,String recipients,String emailSendType){
        DEventBody body = initEventBody(tid,author,pid,DEntity.DPAGE,title,version,DAction.RECOMMEND,message,recipients);
        body.setEmailSendType(emailSendType);
        sendEvent(tid,body,PAGE_EVENT);
    }

    /**
     * 页面恢复事件
     * @param site
     * @param page
     * @param actor
     */
    public void sendPageRecoverEvent(int tid,Resource resource,String actor){
        DEventBody body = getDefaultPageEventBody(tid, resource, DAction.RECOVER);
        body.setActor(actor);
        sendPageEvent(tid,body);
    }

    public void sendPageRenameEvent(int tid, Resource meta, String oldName,int version,String uid) {
        DEventBody body = getDefaultPageEventBody(tid,meta,DAction.RENAME);
        body.setActor(uid);
        body.setTargetVersion(version);
        body.setMessage(oldName);
        sendPageEvent(tid,body);
    }

    public void sendResourceCommentEvent(int tid,int rid,String itemType,String title,String creator,int version,String message){
        if(LynxConstants.TYPE_PAGE.equals(itemType)){
            sendPageCommentEvent(tid,rid,title,creator,version,message);
        }else if(LynxConstants.TYPE_FILE.equals(itemType)){
            sendFileCommentEvent(tid,rid,title,creator,version,message);
        }
    }
    /**
     * 发送评论提及事件
     */
    public void sendResourceMentionEvent(int tid,int rid,String itemType,String title,int version,String comment,String creator,String recipients){
        DEventBody body = initEventBody(tid, creator, rid, itemType, title, version, DAction.MENTION, comment, recipients);
        if(LynxConstants.TYPE_PAGE.equals(itemType)){
            sendEvent(tid,body,PAGE_EVENT);
        }else if(LynxConstants.TYPE_FILE.equals(itemType)){
            sendEvent(tid,body,FILE_EVENT);
        }
    }

    public void sendResourceReplyEvent(int tid,int rid,String itemType,String title,String creator,int version,String message,
                                       String recipients){
        if(LynxConstants.TYPE_PAGE.equals(itemType)){
            sendPageReplyEvent(tid,rid,title,creator,version,message,recipients);
        }else if(LynxConstants.TYPE_FILE.equals(itemType)){
            sendFileReplyEvent(tid,rid,title,creator,version,message,recipients);
        }
    }

    /*add by lvly@2012-06-13*/
    /**在任务发布的时候发送通知
     * @param site 获取到的Site实例
     * @param taker 接受者实例
     * @param action 执行动作
     * @param noticeType 任务类型
     * */
    public void sendTaskActionEvent(int tid,List<TaskTaker> takers,Task task,String action,String actor, String noticeType){
        StringBuffer recipients=new StringBuffer();
        for(TaskTaker taker:takers){
            String uId=taker.getUserIDStr();
            if(recipients.indexOf(uId)>0){
                continue;
            }
            recipients.append(uId).append(",");
        }
        DEventBody body=initEventBody(tid,actor,task.getTaskId(),noticeType,task.getTitle(),0,action,null,recipients.toString());
        sendEvent(tid,body,TASK_EVENT);
    }

    @Override
    public void sendFolderRecommendEvent(int tid, int fid,String title,String author,int version,String message,String recipients,String emailSendType) {
        DEventBody body = initEventBody(tid,author,fid,DEntity.DFOLDER,title,version,DAction.RECOMMEND,message,recipients);
        body.setEmailSendType(emailSendType);
        sendEvent(tid,body,FOLDER_EVENT);

    }

    @Override
    public void sendFolderCreateEvent(int tid, Resource resource) {
        DEventBody body = getDefaultFolderEventBody(tid,resource,DAction.CREATE);
        sendFolderEvent(tid,body);
    }
    @Override
    public void sendFolderRenameEvent(int tid, Resource meta, String oldName,String uid) {
        DEventBody body = getDefaultFolderEventBody(tid,meta,DAction.RENAME);
        body.setActor(uid);
        body.setTargetVersion(0);
        body.setMessage(oldName);
        sendFolderEvent(tid,body);

    }
    @Override
    public void sendResourceRenameEvent(int tid, Resource meta, String oldName,String uid) {
        if(!meta.getTitle().equals(oldName)){
            if(meta.isFile()){
                sendFileRenameEvent(tid,meta,oldName,meta.getLastVersion(),uid);
            }else if(meta.isPage()){
                sendPageRenameEvent(tid, meta, oldName, meta.getLastVersion(),uid);
            }else if(meta.isFolder()){
                sendFolderRenameEvent(tid, meta, oldName,uid);
            }
        }

    }
    public void sendFileRenameEvent(int tid, Resource meta, String oldName, int lastVersion,String uid) {
        DEventBody body = getDefaultFileEventBody(tid,meta,DAction.RENAME);
        body.setActor(uid);
        body.setTargetVersion(lastVersion);
        body.setMessage(oldName);
        sendPageEvent(tid,body);

    }
    @Override
    public void sendResourceCopyEvent(int tid, Resource meta, int destRid) {
        if(meta.isPage()){
            sendPageCreateEvent(tid, meta);
        }else if(meta.isFile()){
            sendFileUploadEvent(meta.getTitle(), meta.getRid(), meta.getCreator(), tid);
        }else if(meta.isFolder()){
            sendFolderCreateEvent(tid, meta);
        }
    }

    private String getResourceType(Resource r){
        if(r.isFile()){
            return DEntity.DFILE;
        }else if(r.isFolder()){
            return DEntity.DFOLDER;
        }else if(r.isPage()){
            return DEntity.DPAGE;
        }
        return null;
    }

    @Override
    public void sendResourceMoveEvent(int tid, Resource meta, int destRid,String actor) {
        DEventBody body =initEventBody(tid, meta.getLastEditor(),meta.getRid(),getResourceType(meta),meta.getTitle(),meta.getLastVersion(),DAction.TEAM_MOVE,null,null);
        body.setActor(actor);
        body.setTargetVersion(meta.getLastVersion());
        String eventName = null;
        if(meta.isPage()){
            eventName = PAGE_EVENT;
        }else if(meta.isFile()){
            eventName = FILE_EVENT;
        }else if(meta.isFolder()){
            eventName = FOLDER_EVENT;
        }
        sendEvent(tid,body,eventName);
    }
    @Override
    public void sendFolderDeleteEvent(int tid, Resource r, String actor) {
        DEventBody body = initEventBody(tid, actor, r.getRid(), DEntity.DFOLDER, r.getTitle(), r.getLastVersion(), DAction.DELETE, null, null);
        sendEvent(tid, body, FOLDER_EVENT);

    }

    @Override
    public void sendFolderRecoverEvent(int tid, Resource r, String currentUID) {
        DEventBody body = getDefaultFolderEventBody(tid, r, DAction.RECOVER);
        body.setActor(currentUID);
        sendPageEvent(tid,body);
    }


}
