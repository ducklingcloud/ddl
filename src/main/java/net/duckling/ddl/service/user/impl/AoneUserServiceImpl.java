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

package net.duckling.ddl.service.user.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.duckling.ddl.service.space.SpaceGained;
import net.duckling.ddl.service.space.dao.SpaceGainedDao;
import net.duckling.ddl.service.user.Activation;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.SimpleUser;
import net.duckling.ddl.service.user.UserExt;
import net.duckling.ddl.service.user.UserGuide;
import net.duckling.ddl.service.user.UserPreferences;
import net.duckling.falcon.api.cache.ICacheService;

import org.apache.log4j.Logger;

import cn.vlabs.commons.principal.UserPrincipal;
import cn.vlabs.duckling.api.umt.rmi.exception.UserExistException;
import cn.vlabs.duckling.api.umt.rmi.exception.UserNotFoundException;
import cn.vlabs.duckling.api.umt.rmi.user.UMTUser;
import cn.vlabs.duckling.api.umt.rmi.user.UserService;

import com.mysql.jdbc.StringUtils;

/**
 * @date 2011-5-26
 * @author Clive Lee
 */
public class AoneUserServiceImpl implements AoneUserService {
	private static final String USER = "simple-user";
	private static final String USER_DETAIL = "user-detail";
	private static final Logger LOG = Logger.getLogger(AoneUserServiceImpl.class);
    
    private UserExt getDetailUserFromCache(int uxid){
        return (UserExt) memcachedService.get(USER_DETAIL+"." + uxid);
    }
    

    private SimpleUser getSimpleUserFromCache(String uid) {
        return (SimpleUser) memcachedService.get(USER + "." + uid);
    }

    /*-------------Domain Method Begin---------------------*/


    private void setDetailUserToCache(int uxid,UserExt ext){
        memcachedService.set(USER_DETAIL+"." + uxid, ext);
    }
    
    private void setSimpleUserToCache(String uid,SimpleUser u){
        memcachedService.set(USER + "." + uid,u);
    }

    /**
     * 如果用户不存在，将用户批量加入到userExt中
     * 
     * @param users
     * @param usernames
     */
    public void addToBatchUsers(String[] users, String[] usernames) {
        for (int i = 0; i < users.length; i++) {
            registInAONE(users[i], usernames[i]);
        }
    }
    
    public boolean createAccountInUMT(String email, String name, String password) {
        UserService us = new UserService(serviceUrl);
        if (!us.isExist(email)) {
            UMTUser user = new UMTUser(email, name, email, password);
            try {
                us.createUser(user);
                return true;
            } catch (UserExistException e) {
                LOG.error(e);
                return false;
            }
        }
        return false;
    }
    
    public int createUserGuide(UserGuide userGuide) {
        return userGuideDAO.create(userGuide);
    }

    public int createUserPreferences(UserPreferences userPre) {
        return userPreferencesDAO.create(userPre);
    }

    public Activation getActivationByKeyAndID(String encode, String id) {
        return activationDao.getActivationByIdAndEncode(Integer.parseInt(id), encode);
    }

    public List<SimpleUser> getAllSimpleUser() {
        return userExtDao.getAllSimpleUser();
    }

    public SimpleUser getSimpleUserByUid(String uid) {
        SimpleUser s = getSimpleUserFromCache(uid);
        if (s == null) {
            s = userExtDao.getSimpleUser(uid);
            if (s == null) {
                return null;
            }
            setSimpleUserToCache(uid, s);
        }
        return s;
    }

    public int getTotalAoneUserNumber() {
        return userExtDao.getTotalUserNumber();
    }

    public UserPrincipal getUMTUser(String uid, String password) {
        UserService us = new UserService(serviceUrl);
        return us.login(uid, password);
    }

    // with cache
    public UserExt getUserExtByAutoID(int uxid) {
        UserExt result = getDetailUserFromCache(uxid);
        if (result == null) {
            result = userExtDao.getUserExtByAutoID(uxid);
            if (result == null) {
                return null;
            }
            setDetailUserToCache(uxid, result);
        }
        return result;
    }

    public List<UserExt> getUserExtByIds(Set<Integer> userId) {
		return userExtDao.getUserExtByIds(userId);
	}
    public List<UserExt> getUserExtByUids(Set<String> userId) {
		return userExtDao.getUserExtByUids(userId);
	}
    /*---------------Finish Update Cache-----------*/

    public UserExt getUserExtInfo(String uid) {
        SimpleUser s = getSimpleUserByUid(uid);
        if (s == null) {
            return null;
        }
        return getUserExtByAutoID(s.getId());
    }

    public Map<String, SimpleUser> getUserExtMap(String[] array) {
        Map<String, SimpleUser> userMap = new HashMap<String, SimpleUser>();
        for (String uid : array) {
            userMap.put(uid, getSimpleUserFromCache(uid));
        }
        return userMap;
    }

    public int getUserGuideStep(String uid, String module) {
        return userGuideDAO.getStep(uid, module);
    }

    public String getUserNameByID(String uid) {
        SimpleUser s = getSimpleUserByUid(uid);
        return (s != null) ? s.getName() : null;
    }

    public UserPreferences getUserPreferences(String uid) {
        return userPreferencesDAO.getUserPreferences(uid);
    }


    public boolean isCorrectUserInfo(String uid, String password) {
        UserService us = new UserService(serviceUrl);
        UserPrincipal usp = us.login(uid, password);
        if (usp == null) {
            return false;
        }
        return true;
    }

    public boolean isExistAoneRegister(String email) {
        // 加入null和空判断，解决后台日志在此处抛出NullPointerException。 yangxiaopeng@cnic.cn
        return !StringUtils.isNullOrEmpty(email) && getSimpleUserFromCache(email) != null;
    }

    public boolean isExistRegister(String uid) {
        return userExtDao.isExistRegister(uid);
    }

    public boolean isExistUMTRegister(String email) {
        UserService us = new UserService(serviceUrl);
        return us.isExist(email);
    }

    public void modifyUserProfile(UserExt user) {
    	UserExt older = getUserExtByAutoID(user.getId());
        userExtDao.updateUserExt(user);
        setDetailUserToCache(user.getId(), user);
        setSimpleUserToCache(user.getUid(), SimpleUser.transfer(user));
        if(isChange(older,user)){
        	UserService us = new UserService(serviceUrl);
        	UMTUser umt = new UMTUser(user.getUid(), user.getName(), user.getUid(), null);
        	try {
        		us.updateUserWithoutPwd(umt);
        	} catch (UserNotFoundException e) {
        		LOG.error(e);
        	}
        }
    }

    private boolean isChange(UserExt older, UserExt user) {
    	if(!older.getUid().equals(user.getUid())){
    		return true;
    	}
    	if(!older.getName().equals(user.getName())){
    		return true;
    	}
		return false;
	}

	/**
     * 向usrExt中加入用户
     * 
     * @param uid
     * @param name
     */
    public void registInAONE(String uid, String name) {
        if (!userExtDao.isExistRegister(uid)) {
            LOG.info("向userext中加入用户uid=" + uid + ";name=" + name);
            int uxid = userExtDao.createUserExt(uid, name);
            SimpleUser n = new SimpleUser();
            n.setId(uxid);
            n.setUid(uid);
            n.setEmail(uid);
            n.setName(name);
            setSimpleUserToCache(uid, n);
        }
    }

    public int saveActivation(Activation instance) {
        return activationDao.insertRecord(instance);
    }

    public List<UMTUser> search(String keyword, int offset, int pagesize) {
        UserService us = new UserService(serviceUrl);
        return us.search(keyword, offset, pagesize, UMTUser.FIELD_USER_NAME, true);
    }

    /*-------------Domain Method End---------------------*/

    public List<UserExt> searchUserByMail(String mail) {
        return userExtDao.searchUserByMail(mail);
    }

    public List<UserExt> searchUserByName(String name) {
        return userExtDao.searchUserByName(name);
    }

    public List<UserExt> searchUserByPinyin(String pinyin) {
        return userExtDao.searchUserByPinyin(pinyin);
    }

    public List<UserExt> searchUserByUserPinyinAndName(String uid, String name) {
        return userExtDao.searchUserByUserPinyinAndName(uid, name);
    }

    public List<UserExt> searchUserByUserTeamAndName(String uid, String name) {
        return userExtDao.searchUserByUserTeamAndName(uid, name);
    }
    
    @Override
    public List<UserExt> searchByUnallocatedSpace() {
        return userExtDao.searchByUnallocatedSpace();
    }

    public void updateActivationStatus(Activation instance) {
        activationDao.updateStatus(instance);
    }

    public int updateRefreshTeamMode(String uid, String mode) {
        return userPreferencesDAO.updateRefreshTeamMode(uid, mode);
    }

    public void updateUserGuideStep(String uid, String module, int step) {
        userGuideDAO.updateStep(uid, module, step);
    }

	public int updateUserPreferences(UserPreferences userPre) {
        return userPreferencesDAO.update(userPre);
    }

	@Override
	public void updateSpaceIncrease(String uid, long size, String remark, int objId) {
		UserExt older = getUserExtInfo(uid);
		older.setUnallocatedSpace(older.getUnallocatedSpace() + size);
		modifyUserProfile(older);
		
		SpaceGained obj = new SpaceGained();
		obj.setUid(uid);
		obj.setObjId(objId);
		obj.setSize(size);
		obj.setRemark(remark);
		obj.setCreateTime(new Date());
		spaceGainedDao.insert(obj);
	}

	public ActivationDAO getActivationDao() {
		return activationDao;
	}
	public void setActivationDao(ActivationDAO activationDao) {
		this.activationDao = activationDao;
	}
	public void setMemcachedService(ICacheService memcachedService) {
		this.memcachedService = memcachedService;
	}
	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}
	public void setUserExtDao(UserExtDAO userExtDao) {
		this.userExtDao = userExtDao;
	}
	public void setUserGuideDAO(UserGuideDAO userGuideDAO) {
		this.userGuideDAO = userGuideDAO;
	}
	public void setUserPreferencesDAO(UserPreferencesDAO userPreferencesDAO) {
		this.userPreferencesDAO = userPreferencesDAO;
	}
    public void setSpaceGainedDao(SpaceGainedDao spaceGainedDao) {
		this.spaceGainedDao = spaceGainedDao;
	}

	private ActivationDAO activationDao;
    private ICacheService memcachedService;
    private String serviceUrl;
    private UserExtDAO userExtDao;
    private UserGuideDAO userGuideDAO;
    private UserPreferencesDAO userPreferencesDAO;
	private SpaceGainedDao spaceGainedDao;
}
