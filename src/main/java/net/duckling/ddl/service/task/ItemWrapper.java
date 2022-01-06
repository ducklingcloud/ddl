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
package net.duckling.ddl.service.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import org.apache.commons.lang.StringUtils;

/**
 * 拥有显示的共享任务包装类
 * @author lvly
 * @since 2012-6-21
 * */
public final class ItemWrapper {
    private ItemWrapper (){}
    /**更新一下是否能编辑,并根据能否编辑排序，能编辑的放前面
     * @param shares 共享任务项
     * @param uid 当前用户ID
     * */
    public static List<TaskItemShare> getView( List<TaskItemShare> shares,String uid){
        if(shares==null){
            return shares;
        }
        List<TaskItemShare> canEditList=new ArrayList<TaskItemShare>();
        List<TaskItemShare> canNotEditList=new ArrayList<TaskItemShare>();
        for(TaskItemShare share:shares){
            share.setCanEdit(StringUtils.isEmpty(share.getUserId())||uid.equals(share.getUserIDStr()));
            if(share.isCanEdit()){
                canEditList.add(share);
            }else{
                canNotEditList.add(share);
            }
        }
        canEditList.addAll(canNotEditList);
        return canEditList;
    }
    /**更新进度，用于独立任务项描述
     * @param items 独立任务项
     * @param pro 进度【1/2】
     * */
    public static List<TaskItem> getProcessFromView(List<TaskItem> items,Map<Integer,String> pro){
        if(items==null||pro==null){
            return items;
        }
        for(TaskItem item:items){
            item.setProcess(pro.get(item.getItemId()));
        }
        return items;
    }
}
