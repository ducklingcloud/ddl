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
package net.duckling.ddl.service.user;

import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.vlabs.commons.principal.UserPrincipal;
import cn.vlabs.duckling.api.umt.rmi.user.UMTUser;

public interface AoneUserService {
	   /**
     * 如果用户不存在，将用户批量加入到userExt中
     * 
     * @param users
     * @param usernames
     */
    void addToBatchUsers(String[] users, String[] usernames) ;
    
    boolean createAccountInUMT(String email, String name, String password);
    
    int createUserGuide(UserGuide userGuide) ;

    int createUserPreferences(UserPreferences userPre);

    Activation getActivationByKeyAndID(String encode, String id) ;

    List<SimpleUser> getAllSimpleUser() ;

    SimpleUser getSimpleUserByUid(String uid);
    int getTotalAoneUserNumber();
    UserPrincipal getUMTUser(String uid, String password);

    // with cache
    UserExt getUserExtByAutoID(int uxid) ;
    List<UserExt> getUserExtByIds(Set<Integer> userId);


    UserExt getUserExtInfo(String uid) ;

    Map<String, SimpleUser> getUserExtMap(String[] array);

    int getUserGuideStep(String uid, String module);
    String getUserNameByID(String uid);

    UserPreferences getUserPreferences(String uid);


    boolean isCorrectUserInfo(String uid, String password) ;

    boolean isExistAoneRegister(String email);

    boolean isExistRegister(String uid) ;

    boolean isExistUMTRegister(String email);

    void modifyUserProfile(UserExt user) ;
    /**
     * 向usrExt中加入用户
     * 
     * @param uid
     * @param name
     */
    void registInAONE(String uid, String name);
    int saveActivation(Activation instance) ;
    List<UMTUser> search(String keyword, int offset, int pagesize);
    /*-------------Domain Method End---------------------*/

    List<UserExt> searchUserByMail(String mail);
    List<UserExt> searchUserByName(String name) ;

    List<UserExt> searchUserByPinyin(String pinyin);
    List<UserExt> searchUserByUserPinyinAndName(String uid, String name);

    List<UserExt> searchUserByUserTeamAndName(String uid, String name) ;
    
    List<UserExt> searchByUnallocatedSpace() ;
    
    void updateActivationStatus(Activation instance);

    int updateRefreshTeamMode(String uid, String mode);

    void updateUserGuideStep(String uid, String module, int step);

	int updateUserPreferences(UserPreferences userPre);

	List<UserExt> getUserExtByUids(Set<String> uids);
	
	/**
	 * 用户未分配空间增加
	 * @param uid
	 * @param size
	 * @param remark
	 * @param objId
	 */
	void updateSpaceIncrease(String uid, long size, String remark, int objId);
}