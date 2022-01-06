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
package net.duckling.ddl.web.controller.pan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.ELayout;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.pan.PanAclUtil;
import net.duckling.ddl.service.share.PanShareResource;
import net.duckling.ddl.service.share.PanShareResourceService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.util.ShareRidCodeUtil;
import net.duckling.ddl.web.controller.BaseController;
import net.duckling.ddl.web.interceptor.access.RequirePermission;
import net.duckling.meepo.api.IPanService;
import net.duckling.meepo.api.PanAcl;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.meepotech.sdk.MeePoException;
import com.meepotech.sdk.MeePoMeta;

@Controller
@RequirePermission(authenticated=true)
@RequestMapping("pan/shareManage")
public class PanShareManageController extends BaseController {
    @Autowired
    private PanShareResourceService panShareResourceService;
    @Autowired
    private URLGenerator urlGenerator;
    @Autowired
    private AoneUserService aoneUserService;
    @Autowired
    private IPanService service;

    @RequestMapping
    public ModelAndView display(HttpServletRequest request){
        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
        ModelAndView m = layout(ELayout.LYNX_MAIN, context,"/jsp/pan/pan_share_manage.jsp");
        String uid = VWBSession.getCurrentUid(request);
        String userName = VWBSession.getCurrentUidName(request);
        List<PanShareResource> srs = panShareResourceService.getByUid(uid);
        Map<Integer,PanResourceBean> resource = getPanResource(srs,PanAclUtil.getInstance(request));
        m.addObject("res", resource);
        m.addObject("user", userName);
        m.addObject("srs", srs);
        m.addObject("shareUrl", getShareUrl(srs));

        String baseUrl = urlGenerator.getBaseUrl() + "/pan";
        m.addObject("teamUrl", baseUrl + "/list");
        m.addObject("teamHome", baseUrl);
        m.addObject("pageType", "list");
        m.addObject("teamType", "pan");
        m.addObject(LynxConstants.TEAM_TITLE, "个人空间（同步版Beta）");
        return m;
    }

    private Map<Integer, PanResourceBean> getPanResource(List<PanShareResource> srs,PanAcl acl) {
        Map<Integer,PanResourceBean> result = new HashMap<Integer,PanResourceBean>();
        if(srs!=null){
            for(PanShareResource p : srs){
                try {
                    MeePoMeta me = service.ls(acl, p.getSharePath(), false);
                    if(me==null){
                        PanResourceBean bean =getDeleteBean(p);
                        result.put(p.getId(), bean);
                    }else{
                        PanResourceBean bean = MeePoMetaToPanBeanUtil.transfer(me,null);
                        result.put(p.getId(), bean);
                    }
                } catch (MeePoException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private PanResourceBean getDeleteBean(PanShareResource p){
        PanResourceBean bean = new PanResourceBean();
        bean.setTitle(getTitleFromPath(p.getSharePath()));
        bean.setPath(null);
        bean.setItemType(LynxConstants.TYPE_FILE);
        int index = p.getSharePath().lastIndexOf(".");
        if(index>=0){
            String pix = p.getSharePath().substring(index+1);
            bean.setFileType(pix.toLowerCase());
        }
        return bean;
    }

    private String getTitleFromPath(String sharePath) {
        int index = sharePath.lastIndexOf("/");
        if(index>=0){
            sharePath = sharePath.substring(index+1);
        }
        return sharePath;
    }

    private Map<Integer,String> getShareUrl(List<PanShareResource> srs){
        Map<Integer,String> re = new HashMap<Integer,String>();
        for(PanShareResource sr: srs){
            re.put(sr.getId(), generateShareUrl(sr.getId()));
        }
        return re;
    }



    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(params="func=delete")
    public JSONObject deleteShareResource(HttpServletRequest request){
        String[] rs = request.getParameterValues("rids[]");
        int[] ids = new int[rs.length];
        for(int i = 0;i<rs.length;i++){
            ids[i] = Integer.parseInt(rs[i]);
        }
        for(int id:ids){
            panShareResourceService.delete(id);
        }
        JSONObject obj = new JSONObject();
        obj.put("success", true);
        return obj;
    }


    /**
     * 生成shareUrl
     * @param urlGenerator
     * @return
     */
    private String generateShareUrl(int id) {
        return urlGenerator.getAbsoluteURL(UrlPatterns.PAN_SHARE, null, null)+"/"+ShareRidCodeUtil.encode(id);
    }

    //  private Map<Integer,String> getShareTitle(List<PanShareResource> prs){
    //  Map<Integer,String> result = new HashMap<Integer,String>();
    //  if(prs!=null){
    //      for(PanShareResource p : prs){
    //          String title = p.getSharePath();
    //          int index = title.lastIndexOf("/");
    //          if(index>=0){
    //              title = title.substring(index);
    //          }
    //          result.put(p.getId(), title);
    //      }
    //  }
    //  return result;
    //}

}
