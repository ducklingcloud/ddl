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

package net.duckling.ddl.service.share.impl;

import java.util.Calendar;
import java.util.Date;

import net.duckling.ddl.service.share.ShareFileAccess;
import net.duckling.ddl.service.share.ShareFileAccessService;
import net.duckling.ddl.util.AoneTimeUtils;
import net.duckling.ddl.util.EncodeUtil;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * @date 2011-10-10
 * @author clive
 */
@Service
public class ShareFileAccessServiceImpl implements ShareFileAccessService{
	
	
	private static final int UID_LEN = 4;
	private static final int PSW_LEN = 3;
	
	@Autowired
	private ShareFileAccessDAOImpl shareFileAccessDao;


	@Override
	public String getRestOfVaildDays(ShareFileAccess s){
		Calendar c = Calendar.getInstance();
		c.setTime(s.getCreateTime());
		c.add(Calendar.DATE, s.getValidOfDays());
		Date now = new Date();
		return AoneTimeUtils.getLastTime(c.getTimeInMillis() - now.getTime());
	}
	
	@Override
	public boolean isValidRequest(ShareFileAccess instance){
		Calendar c = Calendar.getInstance();
		c.setTime(instance.getCreateTime());
		c.add(Calendar.DATE, instance.getValidOfDays());
		return c.getTime().after(new Date());
	}
	
	@Override
	public String getPublicFileURL(int tid,int clbId,int fid,int validOfDays,String fileOwner) {
		ShareFileAccess s = new ShareFileAccess();
		s.setTid(tid);
		s.setClbId(clbId);
		s.setFid(fid);
		s.setRid(fid);
		s.setUid(fileOwner);
		s.setValidOfDays(validOfDays);
		s.setPassword(getRandomPassword());
		persistentShareFileAccess(s);
		return getEncodeURL(s);
	}
	
	@Override
	public String getPublicFileURL(ShareFileAccess s) {
		s.setPassword(getRandomPassword());
		persistentShareFileAccess(s);
		return getEncodeURL(s);
	}
	
	@Override
	public ShareFileAccess parseShareAccess(String encodeURL){
		byte[] bytes = Base64.decodeBase64(encodeURL.getBytes());
		StringBuilder rawStrBuilder = new StringBuilder();
		for(int i=0;i<bytes.length;i++) {
			rawStrBuilder.append((char)bytes[i]);
		}
		String[] strArray = rawStrBuilder.toString().split("#");
		ShareFileAccess current = wrapShareFileAccess(strArray);
		ShareFileAccess persistence = shareFileAccessDao.find(current);
		if(isOldPassword(current.getPassword()) && persistence==null){
			return persistentShareFileAccess(current);
		}
		return isValidPassword(current.getPassword(),persistence)?persistence:null;
	}
	
	private ShareFileAccess wrapShareFileAccess(String[] array){
		ShareFileAccess s = new ShareFileAccess();
		s.setTid(Integer.parseInt(array[0]));
		s.setClbId(Integer.parseInt(array[1]));
		s.setFid(Integer.parseInt(array[2]));
		s.setPassword(array[PSW_LEN]);
		s.setUid(array[UID_LEN]);
		return s;
	}
	
	private boolean isOldPassword(String password){
		return password.equals(OLD_VALID_PASSWORD);
	}
	
	private boolean isValidPassword(String password,ShareFileAccess record){
		return password.equals(OLD_VALID_PASSWORD) || password.equals(record.getPassword());
	}
	
	private ShareFileAccess persistentShareFileAccess(ShareFileAccess s){
		s.setCreateTime(new Date());
		int id = shareFileAccessDao.insertRecord(s);
		s.setId(id);
		return s;
	}
	
	private String getEncodeURL(ShareFileAccess s){
		String tempStr = s.getTid()+"#"+s.getClbId()+"#"+s.getRid()+"#"+s.getPassword()+"#"+s.getUid()+"#rid";
		byte[] tempBytes = Base64.encodeBase64(tempStr.getBytes());
		return new String(tempBytes);
	}
	
	private String getRandomPassword() {
		return EncodeUtil.generateEncode();
	}
}
