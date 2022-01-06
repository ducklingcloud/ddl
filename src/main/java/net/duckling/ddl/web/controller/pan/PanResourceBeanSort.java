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

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import net.duckling.ddl.constant.LynxConstants;

public final class PanResourceBeanSort {
    public static final String TYPE_TIME_DESC = "timeDesc";


    public static void sort(List<PanResourceBean> list,String type){
        if(list==null||list.isEmpty()){
            return;
        }
        Comparator<PanResourceBean> com = factory(type);
        Collections.sort(list, com);
    }

    private static  Comparator<PanResourceBean> factory(String type){
        if(type==null){
            type = "";
        }
        switch (type) {
            case "":
            case "timeDesc":
                return new OrderByTime(-1);
            case "time":
                return new OrderByTime(1);
            case "titleDesc":
                return new OrderByTitle(-1);
            case "title":
                return new OrderByTitle(1);
            default:
                return null;
        }
    }


    private static class  OrderByTime implements Comparator<PanResourceBean>{
        private int desc;
        OrderByTime(int desc){
            this.desc = desc;
        }
        @Override
        public int compare(PanResourceBean o1, PanResourceBean o2) {
            if(o1.getItemType().equals(o2.getItemType())){
                return (int)(transfer(o1.getModifyTime().getTime()-o2.getModifyTime().getTime()))*desc;
            }else{
                if(LynxConstants.TYPE_FOLDER.equals(o1.getItemType())){
                    return -1;
                }else if(LynxConstants.TYPE_FOLDER.equals(o2.getItemType())){
                    return 1;
                }else{
                    return 0;
                }
            }
        }

    }
    private static int transfer(long i){
        if(i<0){
            return -1;
        }else if(i>0){
            return 1;
        }else{
            return 0;
        }
    }

    private static class OrderByTitle implements Comparator<PanResourceBean>{
        private int desc;
        OrderByTitle(int desc){
            this.desc = desc;
        }
        @Override
        public int compare(PanResourceBean o1, PanResourceBean o2) {
            if(o1.getItemType().equals(o2.getItemType())){
                //              return o1.getTitle().toLowerCase().compareTo(o2.getTitle().toLowerCase())*desc;
                return china.compare(o1.getTitle().toLowerCase(), o2.getTitle().toLowerCase())*desc;
            }else{
                if(LynxConstants.TYPE_FOLDER.equals(o1.getItemType())){
                    return -1;
                }else if(LynxConstants.TYPE_FOLDER.equals(o2.getItemType())){
                    return 1;
                }else{
                    return 0;
                }
            }
        }

    }

    private static Comparator<Object> china = Collator.getInstance(Locale.CHINA);
}
