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
package net.duckling.ddl.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.service.bundle.IBundleService;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.PageLock;
import net.duckling.ddl.service.resource.PageLockService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.util.ArrayAndListConverter;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.bean.PageLockDisplay;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


/**
 * 资源中page锁信息查询工具类
 * @author zhonghui
 *
 */
public final class PageLockValidateUtils {
    private PageLockValidateUtils(){}
    /**
     * 将页面锁和页面信息使用json加入response中
     * @param locks
     * @param response
     * @param context
     */
    public static void pageLockMessage(List<PageLock> locks,HttpServletResponse response,ResourceOperateService resourceOperateService){
        List<Integer> pids = new ArrayList<Integer>();
        Map<Integer,PageLock> map = new HashMap<Integer,PageLock>();
        for(PageLock lock : locks){
            map.put(lock.getRid(), lock);
            pids.add(lock.getRid());
        }
        List<Resource> pages = resourceOperateService.getDDoc(locks.get(0).getTid(),pids);

        JsonArray arrays = new JsonArray();
        for(Resource p : pages){
            PageLock lock = map.get(p.getRid());
            if(lock!=null){
                JsonObject o = new JsonObject();
                o.addProperty("pageTitle", p.getTitle());
                o.addProperty("editor", lock.getUid());
                arrays.add(o);
            }
        }
        JsonObject obj = new JsonObject();
        obj.addProperty("lockStatus", "error");
        obj.add("lockError", arrays);
        JsonUtil.write(response, obj);
    }
    /**
     * 将页面锁转换成为页面显示用的pagelock并加入page信息
     * @param locks
     * @param context
     * @return
     */
    public static List<PageLockDisplay>  getPageLockMessage(List<PageLock> locks,ResourceOperateService resourceOperateService){
        List<PageLockDisplay> pagesd = new ArrayList<PageLockDisplay>();
        List<Integer> pids = new ArrayList<Integer>();
        Map<Integer,PageLock> map = new HashMap<Integer,PageLock>();
        for(PageLock lock : locks){
            map.put(lock.getRid(), lock);
            pids.add(lock.getRid());
        }
        List<Resource> pages = resourceOperateService.getDDoc(locks.get(0).getTid(),pids );
        for(Resource p : pages){
            PageLock lock = map.get(p.getRid());
            if(lock!=null){
                PageLockDisplay pd = new PageLockDisplay();
                pd.setPage(p);
                pd.setPageLock(lock);
                pagesd.add(pd);
            }
        }
        return pagesd;
    }

    /**
     * 获取该资源下的所有pageLock，
     * 如果是bundle遍历下面资源获取page
     * @param rids
     * @param context
     * @return
     */
    public static List<PageLock> getPageLockFromResource(int[] rids,IResourceService resourceService,
                                                         PageLockService pageService,IBundleService bundleService){
        List<PageLock> result = new ArrayList<PageLock>();
        if(rids==null||rids.length==0){
            return result;
        }
        List<Resource> pages = getPageFromRid(rids, resourceService,bundleService);
        for(Resource r : pages){
            PageLock lock = pageService.getCurrentLock(r.getTid(), r.getRid());
            if(lock!=null){
                result.add(lock);
            }
        }
        return result;
    }

    /**
     * 从rid中获取所有的page的resource
     * @param rids
     * @param context
     * @return
     */
    private static List<Resource> getPageFromRid(int[] rids,IResourceService resourceService,IBundleService bundleService){
        List<Long> ids = new ArrayList<Long>();
        for(int i : rids){
            ids.add(new Long(i));
        }
        List<Resource> rs= resourceService.getResourcesBySphinxID(ids);
        List<Resource> result = new ArrayList<Resource>();

        for(Resource r : rs){
            if(r.isPage()){
                result.add(r);
            }else if(r.isBundle()){
                List<Integer> br = bundleService.getRidsOfBundleAndItems(r.getRid(), r.getTid());
                br.remove(Integer.valueOf(r.getRid()));
                List<Resource> list = resourceService.getResourcesBySphinxID(ArrayAndListConverter.convertInteger2Long(br));
                for(Resource rr : list){
                    if(rr.isPage()){
                        result.add(rr);
                    }
                }
            }
        }
        return result;
    }
}
