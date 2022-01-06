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

package net.duckling.ddl.service.mail.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.duckling.ddl.common.DucklingProperties;
import net.duckling.ddl.constant.KeyConstants;
import net.duckling.ddl.constant.ParamConstants;
import net.duckling.ddl.service.devent.DEvent;
import net.duckling.ddl.service.invitation.Invitation;
import net.duckling.ddl.service.invitation.InvitationService;
import net.duckling.ddl.service.mail.AoneMailService;
import net.duckling.ddl.service.mail.Mail;
import net.duckling.ddl.service.mail.MailService;
import net.duckling.ddl.service.mail.NoticeMailHelper;
import net.duckling.ddl.service.mail.compile.RenderUtils;
import net.duckling.ddl.service.mail.thread.EmailSendThread;
import net.duckling.ddl.service.param.IParamService;
import net.duckling.ddl.service.param.Param;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.url.ContainerURLConstructor;
import net.duckling.ddl.service.user.Activation;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.SimpleUser;
import net.duckling.ddl.service.user.UserExt;
import net.duckling.ddl.util.CommonUtils;
import net.duckling.ddl.util.DateUtil;
import net.duckling.ddl.util.EncodeUtil;
import net.duckling.ddl.util.JSONMap;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @date 2011-6-20
 * @author Clive Lee
 */
@Service
public class AoneMailServiceImpl implements AoneMailService {

    static class FileInfo {
        private String fileName;
        private String fileUrl;

        public FileInfo(String fileName, String fileUrl) {
            this.fileName = fileName;
            this.fileUrl = fileUrl;
        }

        public String getFileName() {
            return fileName;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }

    }

    private static final String ACCOUNT = "account";
    private static final String ACTIVATION_URL = "activationURL";
    private static final String BR = "<br/>";
    private static final String INVITE_MESSAGE = "message";
    private static final String INVITE_URL = "inviteURL";
    private static final String INVITEE = "invitee";
    private static final String INVITER = "inviter";
    private static final String P = "：</p>";
    private static final String P_USER = "<p>尊敬的用户";
    private static final String PASSWORD = "password";
    private static final String TEAM = "team";
    private static final String TEXT_HTML = "text/html";
    private static final String USER_NAME = "userName";
    private static final String BASE_URL = "baseUrl";
    private static final String CONTAINER_BEGIN = "<table style=\"width:700px; background:#fff; border:1px solid #ccc; margin:0 auto; font-size:14px;border-collapse:collapse;\">";
    private static final String CONTAINER_END = "</table>";
    private static final String CONTENT_BEGIN = "<tr><td colSpan=\"3\"><div style=\"margin:10px; line-height:2em;background:#fff;\">";
    private static final String CONTENT_END = "</div></td></tr>";
    private static final String BANNER = "<tr><td style=\"background:url("+ BASE_URL
            +"/jsp/aone/images/banner_bg.png) repeat 0 0 #0096CE; height: 60px; width:700px; vertical-align:middle;\">"
            + "<table><tr><td style=\"width:50px;\"><img title=\"科研在线·团队文档库\" src=\""+ BASE_URL
            + "/jsp/aone/images/ddlMailLogo.png\"/></td> "
            + "<td style=\"width:500px;\"><span style=\"font-size:24px; color:#fff; font-family:'微软雅黑';\">团队文档库</span></td>"
            + "<td style=\"width:100px;text-align:right;\"><a href=\""+ BASE_URL +"\" target=\"_blank\" style=\"font-size:16px; color:#fff;text-decoration:none; padding-right:10px;\">进入主页</a></td>"
            + "</tr></table></td></tr>";
    private static final String INVITE_TEMPLATE = CONTAINER_BEGIN + BANNER + CONTENT_BEGIN + P_USER + INVITEE + "，您好" + P
            + "用户" + INVITER + " 邀请您加入科研在线团队文档库上的团队\"" + TEAM
            + "\"，您可以点击下面的链接接受邀请：<br/><a href='" + INVITE_URL + "'>"
            + INVITE_URL + "</a> （该邀请链接将于七天后过期）<br/>" + INVITE_MESSAGE
            + "<br/><br/>科研在线·团队文档库<BR><DIV>&nbsp;</DIV>"
            +"<DIV style=\"FONT-SIZE: 10pt; COLOR: #969696;text-align:right\">本邮件由系统发出，请勿直接回复。如有疑问，可致信"
            +"<A style=\"FONT-SIZE: 10pt; COLOR: #969696\" href=\"mailto:vlab@cnic.cn\">vlab@cnic.cn</A></DIV>"
            +"<DIV style=\"FONT-SIZE: 10pt; COLOR: #969696;text-align:right\">Copyright @2014 中国科学院计算机网络信息中心 中国科技网</DIV><BR>"
            + CONTENT_END+CONTAINER_END;

    private static final String REGIST_TEMPLATE = CONTAINER_BEGIN + BANNER + CONTENT_BEGIN + P_USER + USER_NAME + P
            + "您在科研在线团队文档库上注册了一个新用户，" + "账号为：" + ACCOUNT + BR
            + "请点下面链接以激活您的账号：" + BR + "<h3><a href='" + ACTIVATION_URL + "'>"
            + ACTIVATION_URL + "</a></h3><br/><br/>科研在线·团队文档库<BR><DIV>&nbsp;</DIV>"
            +"<DIV style=\"FONT-SIZE: 10pt; COLOR: #969696;text-align:right\">本邮件由系统发出，请勿直接回复。如有疑问，可致信"
            +"<A style=\"FONT-SIZE: 10pt; COLOR: #969696\" href=\"mailto:vlab@cnic.cn\">vlab@cnic.cn</A></DIV>"
            +"<DIV style=\"FONT-SIZE: 10pt; COLOR: #969696;text-align:right\">Copyright @2014 中国科学院计算机网络信息中心 中国科技网</DIV><BR>"
            + CONTENT_END+CONTAINER_END;


    private static Map<String, Object> getSendParams(DEvent e, SimpleUser simpleUser, String baseUrl, Team team,
                                                     Resource res) {
        Map<String, Object> params = new JSONMap();
        RenderTeam rTeam = new RenderTeam();
        rTeam.setTeam(team);
        rTeam.setAccessUrl(team.getName(), res);
        params.put("renderTeam", rTeam);
        params.put("shareFrom", simpleUser);
        params.put("resource", res);
        params.put("remark", e.getEventBody().getMessage());
        params.put("itemId", e.getEventBody().getTarget().getId());
        params.put("baseUrl", baseUrl);
        params.put("targetId", e.getEventBody().getTarget().getId());
        params.put("currentTime", DateUtil.getCurrentTime());
        params.put("eventId", e.getEventBody().getId());
        params.put("fromEmail", simpleUser.getEmail());
        return params;
    }

    private static String[] getUserEmail(String[] uids, AoneUserService us) {
        if (CommonUtils.isNull(uids)) {
            return null;
        }
        String[] r = new String[uids.length];
        for (int i = 0; i < uids.length; i++) {
            UserExt u = us.getUserExtInfo(uids[i]);
            r[i] = u.getEmail();
        }
        return r;
    }

    @Autowired
    private AoneUserService aoneUserService;

    @Autowired
    private DucklingProperties config;

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private MailService mailService;

    @Autowired
    private IParamService paramService;

    @Autowired
    private  IResourceService resourceService ;

    @Autowired
    private TeamService teamService;

    @Autowired
    private ContainerURLConstructor urlConstructor;

    private Mail buildActivationMail(Activation instance, String activationURL) {
        Mail mail = new Mail();
        mail.setRecipient(instance.getEmail());
        mail.setSubject("账号激活");
        mail.setContentType(TEXT_HTML);
        mail.setTemplate(REGIST_TEMPLATE);
        mail.putParam(USER_NAME, instance.getName());
        mail.putParam(ACCOUNT, instance.getEmail());
        mail.putParam(PASSWORD, instance.getPassword());
        mail.putParam(ACTIVATION_URL, activationURL);
        String baseUrl = config.getProperty(KeyConstants.SITE_BASEURL_KEY);
        mail.putParam("baseUrl", baseUrl);
        return mail;
    }

    private Mail buildInvitationMail(Invitation instance, String inviteURL,
                                     String teamDispalyName) {
        Mail mail = new Mail();
        mail.setRecipient(instance.getInvitee());
        String inviter = instance.getInviter();
        if (StringUtils.isNotEmpty(instance.getInviterName())) {
            inviter = instance.getInviterName() + "( " + inviter + " )";
        }
        mail.setSubject("用户" + inviter + "邀请您加入团队 " + teamDispalyName);
        mail.setContentType(TEXT_HTML);
        mail.setTemplate(INVITE_TEMPLATE);
        if (!StringUtils.isEmpty(instance.getMessage())) {
            mail.putParam(INVITE_MESSAGE, "<h3 style=\"background:#eef; border:1px solid #ccd; padding:10px; border-radius:5px; color:#666; font-size:12px;\">邀请留言:"
                          + instance.getMessage()
                          + "</h3>");
        } else {
            mail.putParam(INVITE_MESSAGE, "");
        }
        mail.putParam(INVITER, inviter);
        mail.putParam(INVITEE, instance.getInvitee());
        mail.putParam(TEAM, teamDispalyName);
        mail.putParam(INVITE_URL, inviteURL);
        String baseUrl = config.getProperty(KeyConstants.SITE_BASEURL_KEY);
        mail.putParam(BASE_URL, baseUrl);
        return mail;
    }

    private Mail buildShareSuccessMail(Activation instance,
                                       String activationURL, String[] fileURL, String[] fileNames) {
        Mail mail = new Mail();
        mail.setRecipient(instance.getEmail());
        mail.setSubject("分享文件成功");
        mail.setContentType(TEXT_HTML);
        mail.putParam(USER_NAME, instance.getName());
        mail.putParam(ACCOUNT, instance.getEmail());
        mail.putParam(PASSWORD, instance.getPassword());
        mail.putParam(ACTIVATION_URL, activationURL);
        String baseUrl = config.getProperty(KeyConstants.SITE_BASEURL_KEY);
        mail.putParam(BASE_URL, baseUrl);
        StringBuilder sb = new StringBuilder();
        sb.append(CONTAINER_BEGIN + BANNER + CONTENT_BEGIN + P_USER + USER_NAME + P);
        sb.append("您的文件已分享成功，为了您更好的使用科研在线，系统为您自动创建了帐号：" + BR);
        sb.append("账号为：" + ACCOUNT + BR);
        sb.append("默认密码为：" + PASSWORD + "(请尽快修改此密码)" + BR + "请点下面链接以激活您的账号"
                  + BR);
        sb.append("<h2><a href='" + ACTIVATION_URL + "'>" + ACTIVATION_URL
                  + "</a></h2>" + BR);
        sb.append("文档下载链接如下:" + BR);
        for (int i = 0; i < fileURL.length; i++) {
            sb.append("<a href='" + fileURL[i] + "'>" + fileNames[i] + "</a>"
                      + BR);
        }
        sb.append("您上传的文件通过邮件发送给您想要分享的朋友，感谢您的使用！" + BR);
        sb.append(CONTENT_END + CONTAINER_END);
        mail.setTemplate(sb.toString());
        return mail;
    }

    private List<FileInfo> convert2FileInfo(String[] fileNames,
                                            String[] fileUrls) {
        List<FileInfo> list = new ArrayList<FileInfo>();
        int index = 0;
        for (String fileName : fileNames) {
            FileInfo info = new FileInfo(fileName, fileUrls[index++]);
            list.add(info);
        }
        return list;

    }
    @Override
    public void sendAccessFileMail(String[] fileNames, String[] fileURLs,
                                   String fileOwner, String receiver, String message) {
        Map<String, Object> map = new JSONMap();
        map.put("userName", fileOwner);
        map.put("message", message);
        map.put("fileInfos", convert2FileInfo(fileNames, fileURLs));
        String baseUrl = config.getProperty(KeyConstants.SITE_BASEURL_KEY);
        map.put("baseUrl", baseUrl);
        String content = RenderUtils.render(map, RenderUtils.QUICK_SHARE_TO);
        //      mailService.sendSimpleMail(new String[] { receiver }, "您的朋友 "
        //              + fileOwner + "给您分享了文件", content);
        SimpleEmail se = new SimpleEmail(new String[] { receiver },"您的朋友 "+ fileOwner + "给您分享了文件",content,null);
        EmailSendThread.addEmail(se);

    }
    @Override
    public void sendActivationMail(Activation instance, String activationURL) {
        Mail mail = buildActivationMail(instance, activationURL);
        mailService.sendMail(mail);
    }
    @Override
    public void sendApplyResultToUser(String uid, String teamName,
                                      String teamUrl, boolean flag) {
        StringBuilder sb = new StringBuilder();
        sb.append("<body>"+ CONTAINER_BEGIN + getBannerString() + CONTENT_BEGIN + "尊敬的用户您好：").append("<br/>");
        sb.append("您加入<a href='" + teamUrl + "'>").append(teamName)
                .append("</a>团队的申请，").append("经管理员审核");
        if (flag) {
            sb.append("通过。<br>");
            sb.append("您可以访问团队").append("<a href='" + teamUrl + "'>")
                    .append(teamName).append("</a>");
        } else {
            sb.append("被拒绝！");
        }
        sb.append("<br/><br/>科研在线·团队文档库<BR><DIV>&nbsp;</DIV>"
                  +"<DIV style=\"FONT-SIZE: 10pt; COLOR: #969696;text-align:right\">本邮件由系统发出，请勿直接回复。如有疑问，可致信"
                  +"<A style=\"FONT-SIZE: 10pt; COLOR: #969696\" href=\"mailto:vlab@cnic.cn\">vlab@cnic.cn</A></DIV>"
                  +"<DIV style=\"FONT-SIZE: 10pt; COLOR: #969696;text-align:right\">Copyright @2014 中国科学院计算机网络信息中心 中国科技网</DIV><BR>"
                  + CONTENT_END + CONTAINER_END + "</body>");
        mailService.sendSimpleMail(new String[] { uid }, "您加入团队" + teamName
                                   + "的申请审核结果通知", sb.toString());
    }

    @Override
    public void sendApplyToTeamAdmin(List<String> admin, SimpleUser user,
                                     String teamName, String url) {
        StringBuilder sb = new StringBuilder();
        sb.append("<body>" + CONTAINER_BEGIN + getBannerString()  + CONTENT_BEGIN + "尊敬的管理员您好：").append("<br/>");
        String name = user.getName() + "(" + user.getUid() + ")";
        sb.append(" 用户 ").append(name);
        sb.append(" 申请加入 ")
                .append(teamName)
                .append(" 团队。")
                .append("请管理员审核！<br/><br/><a href='" + url
                        + "'>点击该链接立即审核</a><br/><br/>科研在线·团队文档库<BR><DIV>&nbsp;</DIV>"
                        +"<DIV style=\"FONT-SIZE: 10pt; COLOR: #969696;text-align:right\">本邮件由系统发出，请勿直接回复。如有疑问，可致信"
                        +"<A style=\"FONT-SIZE: 10pt; COLOR: #969696\" href=\"mailto:vlab@cnic.cn\">vlab@cnic.cn</A></DIV>"
                        +"<DIV style=\"FONT-SIZE: 10pt; COLOR: #969696;text-align:right\">Copyright @2014 中国科学院计算机网络信息中心 中国科技网</DIV><BR>"
                        + CONTENT_END + CONTAINER_END + "</body>");
        mailService.sendSimpleMail(admin.toArray(new String[admin.size()]),
                                   "用户" + name + "申请加入团队" + teamName, sb.toString());
    }
    /**
     *
     * 发送分享邮件
     *
     * @param e
     * @param userIds
     */
    public void sendEmail(DEvent e, String[] userIds) {
        int teamId = e.getTid();

        String baseUrl = config.getProperty(KeyConstants.SITE_BASEURL_KEY);
        Team team = teamService.getTeamByID(teamId);
        Resource res = resourceService.getResource(Integer.parseInt(e.getEventBody().getTarget().getId()));
        SimpleUser sr = aoneUserService.getSimpleUserByUid(e.getEventBody().getActor());
        if (!CommonUtils.isNull(userIds)) {
            if ("group".equals(e.getEventBody().getEmailSendType())) {
                NoticeMailHelper.sendRecommandMail(userIds, getSendParams(e, sr, baseUrl, team, res));
            } else {
                for (String userId : userIds) {
                    Param param = paramService.get(ParamConstants.NoticeEmailShareType.TYPE, teamId + "", userId);
                    if (param != null && ParamConstants.NoticeEmailShareType.VALUE_UN_CHECKED.equals(param.getValue())) {
                        continue;
                    }
                    NoticeMailHelper.sendRecommandMail(new String[] { userId },
                                                       getSendParams(e, sr,baseUrl ,team, res));
                }
            }
        }
    }
    @Override
    public void sendInvitationChangeUser(List<String> admin,
                                         Invitation instance, String curUser, String teamName,
                                         String configURL) {
        StringBuilder sb = new StringBuilder();
        sb.append("<body>" + CONTAINER_BEGIN + getBannerString()  + CONTENT_BEGIN + "尊敬的用户您好：").append("<br/>");
        sb.append("  团队" + teamName + "的管理员 ").append(instance.getInviter())
                .append(" 邀请的用户 ").append(instance.getInvitee());
        sb.append(" 更改成 ")
                .append(curUser)
                .append(" 来接受邀请。")
                .append("请管理员审核！<br/><br/>" + configURL
                        + "<br/><br/>科研在线·团队文档库<BR><DIV>&nbsp;</DIV>"
                        +"<DIV style=\"FONT-SIZE: 10pt; COLOR: #969696;text-align:right\">本邮件由系统发出，请勿直接回复。如有疑问，可致信"
                        +"<A style=\"FONT-SIZE: 10pt; COLOR: #969696\" href=\"mailto:vlab@cnic.cn\">vlab@cnic.cn</A></DIV>"
                        +"<DIV style=\"FONT-SIZE: 10pt; COLOR: #969696;text-align:right\">Copyright @2014 中国科学院计算机网络信息中心 中国科技网</DIV><BR>"
                        + CONTENT_END + CONTAINER_END + "</body>");
        mailService.sendSimpleMail(admin.toArray(new String[admin.size()]),
                                   "用户" + instance.getInvitee() + "审核通知", sb.toString());
    }
    @Override
    public void sendInvitationExisterUser(List<String> admin,
                                          Invitation instance, String curUser, String teamName,
                                          String configURL) {
        StringBuilder sb = new StringBuilder();
        sb.append("<body>" + CONTAINER_BEGIN + getBannerString() + CONTENT_BEGIN + "尊敬的用户您好：").append("<br/>");
        sb.append("  团队" + teamName + "的管理员 ").append(instance.getInviter())
                .append(" 邀请的用户 ").append(instance.getInvitee());
        sb.append(" 使用了系统中存在的用户 ")
                .append(curUser)
                .append(" 接受邀请。")
                .append("请管理员审核！<br/><br/>" + configURL
                        + "<br/><br/>科研在线·团队文档库<BR><DIV>&nbsp;</DIV>"
                        +"<DIV style=\"FONT-SIZE: 10pt; COLOR: #969696;text-align:right\">本邮件由系统发出，请勿直接回复。如有疑问，可致信"
                        +"<A style=\"FONT-SIZE: 10pt; COLOR: #969696\" href=\"mailto:vlab@cnic.cn\">vlab@cnic.cn</A></DIV>"
                        +"<DIV style=\"FONT-SIZE: 10pt; COLOR: #969696;text-align:right\">Copyright @2014 中国科学院计算机网络信息中心 中国科技网</DIV><BR>"
                        + CONTENT_END + CONTAINER_END + "</body>");
        mailService.sendSimpleMail(admin.toArray(new String[admin.size()]),
                                   "用户" + instance.getInvitee() + "审核通知", sb.toString());
    }
    @Override
    public void sendInvitationMail(String teamName, int tid, String inviter,
                                   List<String> invitees, String message, String teamDispalyName) {
        if (invitees != null) {
            SimpleUser user = aoneUserService.getSimpleUserByUid(inviter);
            for (String uid : invitees) {
                Invitation instance = Invitation.getInstance(inviter, uid,
                                                             teamName, tid);
                instance.setMessage(message);
                if (user != null) {
                    instance.setInviterName(user.getName());
                }
                int id = invitationService.saveInvitation(instance);
                instance.setId(id);
                String displayURL = urlConstructor.makeURL("invite",
                                                           EncodeUtil.getDisplayURL(instance), null, true);
                if (displayURL.contains(":80/")) {
                    int index = displayURL.indexOf(":80/");
                    displayURL = displayURL.substring(0, index)
                            + displayURL.substring(index + 3);
                }
                Mail mail = buildInvitationMail(instance, displayURL,
                                                teamDispalyName);
                mailService.sendMail(mail);
            }
        }
    }

    @Override
    public void sendMentionEmail(DEvent e, String[] userIds) {
        int teamId = e.getTid();
        String baseUrl = config.getProperty(KeyConstants.SITE_BASEURL_KEY);

        Team team = teamService.getTeamByID(teamId);
        String type = e.getEventBody().getTarget().getType();
        Resource res = resourceService.getResource(Integer.parseInt(e.getEventBody().getTarget().getId()), teamId);
        String[] emails = getUserEmail(userIds, aoneUserService);
        SimpleUser sr = aoneUserService.getSimpleUserByUid(e.getEventBody().getActor());
        if (!CommonUtils.isNull(emails)) {
            if ("group".equals(e.getEventBody().getEmailSendType())) {
                NoticeMailHelper.sendMentionMail(emails, getSendParams(e, sr,baseUrl, team, res));
            } else {
                for (String userId : emails) {
                    Param param = paramService.get(ParamConstants.NoticeEmailShareType.TYPE, teamId + "", userId);
                    if (param != null && ParamConstants.NoticeEmailShareType.VALUE_UN_CHECKED.equals(param.getValue())) {
                        continue;
                    }
                    NoticeMailHelper.sendMentionMail(new String[] { userId },
                                                     getSendParams(e, sr, baseUrl, team, res));
                }
            }
        }
    }
    @Override
    public void sendShareSuccessMail(Activation instance, String activationURL,
                                     String[] fileURL, String[] fileNames) {
        Mail mail = buildShareSuccessMail(instance, activationURL, fileURL,
                                          fileNames);
        mailService.sendMail(mail);
    }

    @Override
    public void sendShareSuccessMailWithoutActivation(String email,
                                                      String userName, String[] fileURL, String[] fileNames,String[]shareUser) {
        Map<String, Object> map = new JSONMap();
        map.put("shareUser", getShareUser(shareUser));
        map.put("userName", userName);
        map.put("fileInfos", convert2FileInfo(fileNames, fileURL));
        String baseUrl = config.getProperty(KeyConstants.SITE_BASEURL_KEY);
        map.put("baseUrl", baseUrl);
        String content = RenderUtils.render(map, RenderUtils.QUICK_SHARE_FROM);
        //      mailService.sendSimpleMail(new String[] { email }, "分享成功", content);
        SimpleEmail se = new SimpleEmail(new String[] { email },"分享成功",content,null);
        EmailSendThread.addEmail(se);
    }

    private String getShareUser(String[] shareUser){
        if(shareUser==null||shareUser.length==0){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for(String s : shareUser){
            SimpleUser su = aoneUserService.getSimpleUserByUid(s);
            String name =s;
            if(su!=null){
                name = s+"("+su.getName()+")";
            }
            if(sb.length()==0){
                sb.append(name);
            }else{
                sb.append(",").append(name);
            }
        }
        return sb.toString();
    }

    /**
     * 获取带网址的banner
     * @return
     */
    private String getBannerString(){
        String baseUrl = config.getProperty(KeyConstants.SITE_BASEURL_KEY);
        return BANNER.replaceAll(BASE_URL, baseUrl);
    }
}
