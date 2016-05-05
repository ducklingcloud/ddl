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
package net.duckling.ddl.model.folder;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.duckling.ddl.service.resource.FolderPath;

import org.junit.Test;


public class FileRelationDAOTest extends BaseTest {
    
//    @Test
    public void testGetAncestors() {
        int pid = 2;
        List<FolderPath> relations = prepareDataByDesendant(pid);
        
        List<FolderPath> remoteRelations = folderPathDao.getPath(0,pid);
        assertThat(remoteRelations.size(),is(relations.size()));
        
        removeByDescendant(pid);
    }
    
//    @Test
    public void testGetDescendants() {
        int pid = 3;
        
        List<FolderPath> relations = preparDataByAncestor(pid);
        
        List<FolderPath> remoteRelations = folderPathDao.getPath(0,pid);
        assertThat(remoteRelations.size(),is(relations.size()));
        
        removeByAncestor(pid);
    }
    
//    @Test
    public void testGetPath() {
        int pid = 4;
        prepareDataByDesendant(pid);
        
        List<FolderPath> pathList = folderPathDao.getPath(0,pid);
        List<Integer> i = new ArrayList<Integer>();
        for(FolderPath p : pathList){
        	i.add(p.getRid());
        }
        assertThat(i,contains(1,2,3,4,5));
        
        removeByDescendant(pid);
    }

    private List<FolderPath> prepareDataByDesendant(int pid) {
        List<FolderPath> relations = new LinkedList<FolderPath>();
        for(int i=0;i<5;i++){
            relations.add(new FolderPath(pid,11,i+1,i));
        }
        folderPathDao.insertBatch(relations);
        return relations;
    }

    private void removeByDescendant(int pid) {
        folderPathDao.deleteByRid(pid);
        List<FolderPath> emptyList = folderPathDao.getChildren(0,pid);
        assertThat(emptyList.size(),is(0));
    }
    
    private List<FolderPath> preparDataByAncestor(int pid) {
        List<FolderPath> relations = new LinkedList<FolderPath>();
        for(int i=0;i<4;i++){
            relations.add(new FolderPath(pid,11,i+1,i));
        }
        folderPathDao.insertBatch(relations);
        return relations;
    }


    private void removeByAncestor(int pid) {
        folderPathDao.deleteByRid(pid);
        List<FolderPath> emptyList = folderPathDao.getPath(0,pid);
        assertThat(emptyList.size(),is(0));
    }

//    @Test
    public void testGetChildren() {
        int nid = 10;
        preparDataByAncestor(nid);
        
        List<FolderPath> children = folderPathDao.getChildren(0,nid);
        assertThat(getRid(children),contains(2));
        
        removeByAncestor(nid);
    }
    private List<Integer> getRid(List<FolderPath> ps){
    	List<Integer> rs = new ArrayList<Integer>();
    	for(FolderPath p : ps){
    		rs.add(p.getTid());
    	}
    	return rs;
    }
    
    
//    @Test
    public void testGetParent() {
        int nid = 10;
        prepareDataByDesendant(nid);
        
        FolderPath parent = folderPathDao.getParent(nid);
        assertThat(parent.getRid(),is(2));
        
        removeByDescendant(nid);
    }

}
