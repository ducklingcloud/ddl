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

package net.duckling.ddl.service.mail.notice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.duckling.ddl.service.devent.Notice;


/**
 * @date 2011-11-9
 * @author clive
 */
public abstract class AbstractNoticeHelper {

    protected abstract String getKey(Notice notice);

    protected abstract GroupNotice wrapNotice(List<Notice> notices,String key);

    public GroupNotice[] getCNoticeArray(Collection<Notice> source){
        if(source==null||source.size()==0){
            return new GroupNotice[]{};
        }
        Map<String,Integer> indexMap = new HashMap<String,Integer>();
        Map<String,List<Notice>> tempMap = new HashMap<String,List<Notice>>();
        int index = 0;
        for(Notice notice:source){
            String key = getKey(notice);
            if(!tempMap.containsKey(key)) {
                List<Notice> records = new ArrayList<Notice>();
                records.add(notice);
                tempMap.put(key, records);
                indexMap.put(key, index++);
            }else {
                List<Notice> records = tempMap.get(key);
                records.add(notice);
            }
        }
        GroupNotice[] results = new GroupNotice[indexMap.keySet().size()];
        for(Map.Entry<String , List<Notice>> entry : tempMap.entrySet()){
            results[indexMap.get(entry.getKey())] = wrapNotice(entry.getValue(), entry.getKey());
        }
        indexMap.clear();
        tempMap.clear();
        indexMap = null;
        tempMap = null;
        return results;
    }

}
