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
package net.duckling.ddl.web.bean;

import java.util.ArrayList;
import java.util.List;

import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.bundle.IBundleService;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.util.ArrayAndListConverter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


/**
 * 添加组合元素冲突的处理类<br/>
 * 添加组合资源时，由于并发操作的原因，会产生想要添加的资源被添加到另外到bundle。
 * 因为以下代码有几个类共享，所以抽取出一个类
 * @author Yangxp
 * @since 2012-12-18
 */
public class ConflictBundleItemHelper {

    /**
     * 获取当前新添加进Bundle内的资源rid集合.<br/>
     * @param bs BundleService
     * @param bundle 当前想要添加资源的Bundle
     * @param wantToAddRids 预计要添加到bundle的资源rid集合
     * @return bundle内的资源rid集合
     */
    public static int[] getNewAddBundleItems(IBundleService bs, Resource bundle, List<Integer> wantToAddRids){
        List<Integer> curItems = bs.getRidsOfBundleAndItems(bundle.getRid(), bundle.getTid());
        curItems.remove(Integer.valueOf(bundle.getRid()));
        List<Integer> confilctRids = new ArrayList<Integer>(wantToAddRids);
        List<Integer> newAddRids = new ArrayList<Integer>(wantToAddRids);
        confilctRids.removeAll(curItems);//移除已经添加进bundle的资源
        newAddRids.removeAll(confilctRids);//从预计要添加的资源集合中移除冲突元素
        int size = newAddRids.size();
        int[] curRids = new int[size];
        for(int i=0; i<size; i++){
            curRids[i] = newAddRids.get(i);
        }
        return curRids;
    }

    /**
     * 将没有添加到bundle内的资源信息取出来，并生成与前台相关的信息返回。
     * @param wantToAddRids 预计要添加到bundle内的资源rid集合
     * @param actualAddRids 实际添加进去的资源rid集合
     * @return
     */
    public static JsonArray getJSONArrayOfConflictItems(IResourceService resourceService,
                                                        URLGenerator urlGenerator,int[] wantToAddRids, int[] actualAddRids){
        JsonArray array = new JsonArray();
        if(null != wantToAddRids && wantToAddRids.length>0){
            List<Integer> want = ArrayAndListConverter.convertInt2Integer(wantToAddRids);
            List<Integer> actual = ArrayAndListConverter.convertInt2Integer(actualAddRids);
            want.removeAll(actual);
            IResourceService rs = resourceService;
            List<Long> conflictRids = ArrayAndListConverter.convertInteger2Long(want);
            List<Resource> conflictItems = rs.getResourcesBySphinxID(conflictRids);
            for(Resource res : conflictItems){
                Resource bundle = rs.getResource(res.getBid(), res.getTid());
                if(bundle!=null){
                    JsonObject bundleObj = new JsonObject();
                    bundleObj.addProperty("title", bundle.getTitle());
                    bundleObj.addProperty("url", urlGenerator.getURL(res.getTid(),UrlPatterns.T_BUNDLE, res.getBid()+"",null));
                    JsonObject obj = getJSONResourceForBundleItem(urlGenerator, res);
                    obj.add("bundle", bundleObj);
                    array.add(obj);
                }
            }
        }
        return array;
    }

    /**
     * 为组合视图生成单个资源的JSON对象
     * @param resource 组合内资源对象
     * @return JSON对象 , 形如{"rid":"1","itemType":"DFile","fileType":"pdf",
     * "url":"/ddl/cnic/bundle/2?rid=1","title":"haha"}
     */
    public static JsonObject getJSONResourceForBundleItem(URLGenerator urlGenerator,  Resource resource){
        JsonObject obj = new JsonObject();
        obj.addProperty("rid", resource.getRid());
        obj.addProperty("itemType", resource.getItemType());
        obj.addProperty("fileType", (null!=resource.getFileType())?resource.getFileType().toLowerCase():"");
        obj.addProperty("url", urlGenerator.getURL(resource.getTid(), UrlPatterns.T_BUNDLE, resource.getBid()+"","rid="+resource.getRid()));
        obj.addProperty("title", resource.getTitle());
        return obj;
    }

}
