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
package net.duckling.ddl.web.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import net.duckling.ddl.util.JsonUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.exception.NoEnoughSpaceException;
import net.duckling.ddl.service.authenticate.UserPrincipal;
import net.duckling.ddl.service.file.FileVersion;
import net.duckling.ddl.service.invitation.ClientInviteService;
import net.duckling.ddl.service.mail.MailService;
import net.duckling.ddl.service.resource.FolderPathService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/api/share")
@RequirePermission(authenticated = true)
public class APIShareSecureFile {
    private static final String RECEIVE_FOLDER = "我收到的分享";
    private static final int ROOT_RID = 0;
    @Autowired
    private AoneUserService aoneUserService;
    @Autowired
    private FolderPathService folderPathService;
    @Autowired
    private ResourceOperateService resourceOperateService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private MailService mailService;
    @Autowired
    private ClientInviteService inviteService;

    private enum APIStatusCode {
        SUCCESS(200), NOT_FOUND(404), NO_ENOUGH_SPACE(501), ERROR(500);
        private int code;

        private APIStatusCode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public String toString() {
            switch (this) {
                case SUCCESS:
                    return "success";
                case NO_ENOUGH_SPACE:
                    return "no enough space";
                case ERROR:
                    return "internal error";
                case NOT_FOUND:
                    return "not found";
                default:
                    return "unknown error";
            }
        }
    }

    private Resource findShareFolder(String userName, int tid) {
        Resource shareFolder;
        List<Resource> folders = folderPathService.getResourceByName(tid,
                                                                     ROOT_RID, LynxConstants.TYPE_FOLDER, RECEIVE_FOLDER);
        if (folders != null && folders.size() == 1) {
            shareFolder = folders.get(0);
        } else {
            shareFolder = new Resource();
            shareFolder.setTid(tid);
            shareFolder.setBid(ROOT_RID);
            shareFolder.setCreateTime(new Date());
            shareFolder.setCreator(userName);
            shareFolder.setItemType(LynxConstants.TYPE_FOLDER);
            shareFolder.setTitle(RECEIVE_FOLDER);
            shareFolder.setLastEditor(userName);
            shareFolder.setLastEditorName(aoneUserService
                                          .getUserNameByID(userName));
            shareFolder.setLastEditTime(new Date());
            shareFolder.setStatus(LynxConstants.STATUS_AVAILABLE);
            resourceOperateService.createFolder(shareFolder);
        }
        return shareFolder;
    }

    @SuppressWarnings("unchecked")
    private void printJSON(HttpServletResponse response, APIStatusCode status,
                           Object message) throws IOException {
        JsonObject obj = new JsonObject();
        obj.addProperty("code", status.getCode());
        obj.addProperty("message", status.toString());
        if (status == APIStatusCode.SUCCESS) {
            obj.add("result", JsonUtil.getJSONObject(message));
        }
        response.setContentType("text/javascript");
        response.setCharacterEncoding("utf8");
        PrintWriter out = response.getWriter();
        out.write(obj.toString());
        out.flush();
        out.close();
    }

    private static final String SHARE_MESSAGE = "<body>%s<br/>" + "您好！<br/>"
            + "%s(%s)想给您发送了加密文档<b>%s</b>。<br/>" + "%s<br/>" + "请注意查收。</body>";

    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.POST)
    public void share(@RequestParam("file") MultipartFile uploadFile,
                      @RequestParam("message") String msg, HttpServletRequest request,
                      HttpServletResponse response) throws IOException {
        String receivers = request.getParameter("receivers");
        msg = URLDecoder.decode(msg, "UTF-8");
        if (receivers == null || receivers.trim().length() == 0) {
            sayMissingParameter(response);
            return;
        }
        UserPrincipal currentUser = (UserPrincipal) VWBSession.findSession(
            request).getCurrentUser();
        String creator = currentUser.getName();

        LinkedList<String> notfound = new LinkedList<String>();
        LinkedList<String> nospace = new LinkedList<String>();
        String[] receiverArray = receivers.split(",");
        FileVersion item = null;
        for (String userName : receiverArray) {
            int tid = teamService.getPersonalTeamNoCreate(userName);
            if (tid == -1) {
                notfound.add(userName);
                continue;
            }
            Resource shareFolder = findShareFolder(userName, tid);
            int parentRid = shareFolder.getRid();
            if (item != null) {
                resourceOperateService.copyResource(tid, parentRid,
                                                    item.getTid(), item.getRid(), creator);
                if (msg == null) {
                    msg = "";
                }
                String message = String.format(SHARE_MESSAGE, userName,
                                               currentUser.getFullName(), currentUser.getName(),
                                               item.getTitle(), msg);
                mailService.sendSimpleMail(new String[] { userName }, "分享通知",
                                           message);
            } else {
                try {
                    String realname = decideName(tid, parentRid,
                                                 uploadFile.getOriginalFilename(), creator);
                    item = resourceOperateService.upload(creator, tid,
                                                         parentRid, realname, uploadFile.getSize(),
                                                         uploadFile.getInputStream());
                    String message = String.format(SHARE_MESSAGE, userName,
                                                   currentUser.getFullName(), currentUser.getName(),
                                                   item.getTitle(), msg);
                    mailService.sendSimpleMail(new String[] { userName },
                                               "分享通知", message);
                } catch (NoEnoughSpaceException e) {
                    nospace.add(userName);
                } catch (IOException e) {
                    printJSON(response, APIStatusCode.ERROR, e.getMessage());
                    return;
                }
            }
        }

        JsonObject obj = new JsonObject();
        obj.add("notfound", list2JsonArray(notfound));
        obj.add("nospace", list2JsonArray(nospace));

        printJSON(response, APIStatusCode.SUCCESS, obj);
    }

    private static class FileNameSplitter {
        private String left;
        private String right;

        public FileNameSplitter(String name) {
            right = ".dsf";
            name = name.substring(0, name.length() - ".dsf".length());
            int index = name.lastIndexOf('.');
            if (index != -1) {
                left = name.substring(0, index);
                right = name.substring(index) + right;
            } else {
                left = "";
                right = name + right;
            }
        }

        public String compose(String middle) {
            return left + middle + right;
        }

    }

    private String decideName(int tid, int parentRid, String originName,
                              String creator) {
        String result = originName;
        List<Resource> files = folderPathService.getResourceByName(tid,
                                                                   parentRid, LynxConstants.TYPE_FILE, result);
        if (files != null && files.size() > 0) {
            FileNameSplitter splitter = new FileNameSplitter(originName);
            result = splitter.compose("-" + creator);
            files = folderPathService.getResourceByName(tid, parentRid,
                                                        LynxConstants.TYPE_FILE, result);
            if (files != null && files.size() > 0) {
                SimpleDateFormat sdf = new SimpleDateFormat(
                    "yyyy.MM.dd.hh.mm.ss");
                result = splitter.compose("-" + creator + "-"
                                          + sdf.format(new Date()));
            }
        }
        return result;
    }

    @RequestMapping(params = "func=invite")
    public void invite(@RequestParam("username") String username,
                       HttpServletRequest request, HttpServletResponse response) {
        VWBSession vwbsession = VWBSession.findSession(request);
        UserPrincipal user = (UserPrincipal) vwbsession.getCurrentUser();
        inviteService.invite(user, username);
    }

    @RequestMapping(params = "func=accept")
    public void accept(HttpServletRequest request, HttpServletResponse response) {
        VWBSession vwbsession = VWBSession.findSession(request);
        UserPrincipal user = (UserPrincipal) vwbsession.getCurrentUser();
        inviteService.accept(user.getName());
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(params = "func=readmessage")
    public void readMessage(HttpServletRequest request,
                            HttpServletResponse response) throws IOException {
        VWBSession vwbsession = VWBSession.findSession(request);
        UserPrincipal user = (UserPrincipal) vwbsession.getCurrentUser();
        List<String> messages = inviteService.readMessage(user.getName());
        JsonArray array = new JsonArray();
        messages.forEach(s -> array.add(s));
        printJSON(response, APIStatusCode.SUCCESS, array);
    }

    @SuppressWarnings("unchecked")
    private JsonArray list2JsonArray(List<String> list) {
        JsonArray array = new JsonArray();
        for (String val : list) {
            array.add(val);
        }
        return array;
    }

    private void sayMissingParameter(HttpServletResponse response) {
        // TODO Auto-generated method stub

    }

}
