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

import java.util.LinkedList;
import java.util.List;

import net.duckling.ddl.service.resource.FolderPath;

import org.junit.Test;


public class DirectoryServiceImplTest extends BaseTest {
    
    private int nid1 = 1 ;

    private int nid2 = 2 ;
    
    private int nid3 = 3 ;
    
    private int nid4 = 4 ;
    
    private int nid5 = 5 ;
    
    private int nid6 = 6 ;
    
    private int nid7 = 7 ;
    
    private int nid8 = 8 ;
    
    private int nid9 = 9 ;
    

//    @Test
    public void testInsert() {
        prepareTreeCase1();
        
        assertThat(getAncester(folderPathService.getPath(0,nid9)),contains(0,nid1,nid2,nid8,nid9));
        assertThat(getAncester(folderPathService.getPath(0,nid8)),contains(0,nid1,nid2,nid8));
        assertThat(getAncester(folderPathService.getPath(0,nid7)),contains(0,nid1,nid2,nid4,nid7));
        assertThat(getAncester(folderPathService.getPath(0,nid6)),contains(0,nid1,nid2,nid4,nid6));
        assertThat(getAncester(folderPathService.getPath(0,nid5)),contains(0,nid1,nid2,nid5));
        assertThat(getAncester(folderPathService.getPath(0,nid4)),contains(0,nid1,nid2,nid4));
        assertThat(getAncester(folderPathService.getPath(0,nid3)),contains(0,nid1,nid3));
        assertThat(getAncester(folderPathService.getPath(0,nid2)),contains(0,nid1,nid2));
        assertThat(getAncester(folderPathService.getPath(0,nid1)),contains(0,nid1));
        
        folderPathService.delete(0,nid1);
        assertThat(folderPathService.getPath(0,nid6).size(),is(0));
    }
    private List<Integer> getRid(List<FolderPath> ps){
    	List<Integer> rs = new LinkedList<Integer>();
    	for(FolderPath p : ps){
    		rs.add(p.getRid());
    	}
    	return rs;
    }
    private List<Integer> getAncester(List<FolderPath> ps){
    	List<Integer> rs = new LinkedList<Integer>();
    	for(FolderPath p : ps){
    		rs.add(p.getAncestorRid());
    	}
    	return rs;
    }
    /* Test Case 1:
     * 1
     * ├── 2
     * │   ├── 4
     * │   │   ├── 6
     * │   │   └── 7
     * │   ├── 5
     * │   └── 8
     * │       └── 9
     * └── 3
     */
    private void prepareTreeCase1() {
    	cleanTable("ddl_folder_path");
        folderPathService.create(0, nid1, 1);
        folderPathService.create( nid1,nid2, 1);
        folderPathService.create( nid1,nid3, 1);
        folderPathService.create( nid2,nid4, 1);
        folderPathService.create( nid2,nid5, 1);
        folderPathService.create( nid4,nid6, 1);
        folderPathService.create( nid4,nid7, 1);
        folderPathService.create( nid2,nid8, 1);
        folderPathService.create( nid8,nid9, 1);
    }
    

//    @Test
    public void testMoveTo() {
        prepareTreeCase1();
        
        folderPathService.move(0,nid4, nid8);
        
        /* Test Case 2:
         * 1
         * ├── 2
         * │   ├── 5
         * │   └── 8
         * │       ├── 4
         * │       │   ├── 6
         * │       │   └── 7
         * │       └── 9
         * └── 3
         */
        assertThat(getAncester(folderPathService.getPath(0,nid9)),contains(0,nid1,nid2,nid8,nid9));
        assertThat(getAncester(folderPathService.getPath(0,nid8)),contains(0,nid1,nid2,nid8));
        assertThat(getAncester(folderPathService.getPath(0,nid7)),contains(0,nid1,nid2,nid8,nid4,nid7));
        assertThat(getAncester(folderPathService.getPath(0,nid6)),contains(0,nid1,nid2,nid8,nid4,nid6));
        assertThat(getAncester(folderPathService.getPath(0,nid5)),contains(0,nid1,nid2,nid5));
        assertThat(getAncester(folderPathService.getPath(0,nid4)),contains(0,nid1,nid2,nid8,nid4));
        assertThat(getAncester(folderPathService.getPath(0,nid3)),contains(0,nid1,nid3));
        assertThat(getAncester(folderPathService.getPath(0,nid2)),contains(0,nid1,nid2));
        assertThat(getAncester(folderPathService.getPath(0,nid1)),contains(0,nid1));
        
        folderPathService.delete(0,nid1);
        assertThat(folderPathService.getPath(0,nid6).size(),is(0));
    }


//    @Test
    public void testQuery() {

        prepareTreeCase1();
        
        assertThat(getRid(folderPathService.query(nid2, 1)),contains(nid4,nid5,nid8));
        assertThat(getRid(folderPathService.query(nid2, 2)),contains(nid6,nid7,nid9));
        assertThat(getRid(folderPathService.query(nid2, 0)),contains(nid2));
        
//        assertThat(getRid(folderPathService.query(nid2, -1)),is(emptyCollectionOf(Integer.class)));
        
        folderPathService.delete(0,nid1);
    }

//    @Test
    public void testQueryChildren() {
        prepareTreeCase1();
        assertThat(getRid(folderPathService.getChildrenPath(0,nid2)),contains(nid4,nid5,nid8));
        assertThat(getRid(folderPathService.getChildrenPath(0,nid4)),contains(nid6,nid7));
//       assertThat(getRid(folderPathService.getChildren(nid9)),is(emptyCollectionOf(Integer.class)));
        folderPathService.delete(0,nid1);
    }

//    @Test
    public void testGetParent() {
        prepareTreeCase1();
        assertThat(nid1,is(folderPathService.getParent(nid2).getAncestorRid()));
        assertThat(nid2,is(folderPathService.getParent(nid4).getAncestorRid()));
        assertThat(0,is(folderPathService.getParent(nid1).getAncestorRid()));
//        assertThat(0,is(folderPathService.getParent(10).getAncestorRid()));
        folderPathService.delete(0,nid1);
    }

//    @Test
    public void testDepthFirstTraversal() {
        /* Test Case 1:
         *  .
            └── 1
                ├── 2
                │   ├── 5
                │   └── 8
                │       ├── 4
                │       │   ├── 6
                │       │   └── 7
                │       └── 9
                └── 3
         */
        prepareTreeCase1();
        
        /*
         * 1 
         * |-- 3 
         * |-- 2 
         * |   |-- 8
         * |   |-- 5 
         * |       |-- 9
         * |       |-- 4 
         * |           |-- 7
         * |           |-- 6
         */
//        folderPathService.depthFirstTraversal(nid1);
        
        /* Test Case 2:
         *  .
            └── 1
                ├── 2
                │   ├── 4
                │   │   ├── 6
                │   │   └── 7
                │   ├── 5
                │   └── 8
                │       └── 9
                └── 3
         */
        folderPathService.delete(0,nid1);
    }


//    @Test
    public void testBreadthFirstTraversal() {
//        fail("Not yet implemented");
    }

}
