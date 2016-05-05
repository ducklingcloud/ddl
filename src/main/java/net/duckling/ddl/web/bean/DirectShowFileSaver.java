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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import net.duckling.ddl.service.file.DFileSaver;

import org.apache.log4j.Logger;
import org.mozilla.universalchardet.UniversalDetector;


public class DirectShowFileSaver implements DFileSaver {

	private static final Logger LOG = Logger.getLogger(DirectShowFileSaver.class);
	private static final int READ_SIZE = 4096;
	private static final String GBK = "GBK";
	private ByteArrayOutputStream baos;
	private long fileLen = 0;
	private String encode = null;
	
	public DirectShowFileSaver(long fileLen){
		baos = new ByteArrayOutputStream();
		this.fileLen = fileLen;
	}
	
	@Override
	public void save(String filename, InputStream in) {
		try{
			BufferedInputStream bis = new BufferedInputStream(in);
	        byte[] buf = new byte[READ_SIZE];
		    UniversalDetector detector = new UniversalDetector(null);

		    int nread;
		    while ((nread = bis.read(buf)) > 0 ){
		      if(!detector.isDone()){
		    	  	detector.handleData(buf, 0, nread);
		      }
		      baos.write(buf, 0, nread);
		    }
		    
		    if(detector.isDone()){
		    	encode = detector.getDetectedCharset();
		    }
		    detector.dataEnd();
	        detector.reset();
		}catch(IOException e){
			LOG.error("dowload error", e);
		}finally{
			if(null == encode){
				encode = GBK;
			}
			try {
				in.close();
			} catch (IOException e) {
				LOG.error(e);
			}
		}
	}
	
	public String getFileContent(){
		try {
			String temp = baos.toString(encode);
			baos.close();
			return temp;
		} catch (UnsupportedEncodingException e) {
			LOG.error("Charset is not supported",e);
		} catch (IOException e) {
			LOG.error("close byteArrayOutputStream failed!",e);
		}
		return null;
	}

	@Override
	public void setLength(long length) {
		// TODO Auto-generated method stub
		
	}

}
