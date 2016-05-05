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
package net.duckling.ddl.web.tag;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import net.duckling.ddl.common.DDLFacade;
import net.duckling.ddl.service.pan.PanAclUtil;
import net.duckling.ddl.web.bean.DirectShowFileSaver;
import net.duckling.meepo.api.IPanService;
import net.duckling.meepo.api.PanAcl;
import cn.vlabs.clb.api.AccessForbidden;

import com.meepotech.sdk.MeePoMeta;

public class ShowPanFileTag extends VWBBaseTag {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String path;
	private int version;
	
	public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public int doVWBStart() throws Exception {
		IPanService ps = DDLFacade.getBean(IPanService.class);
		PanAcl acl = PanAclUtil.getInstance((HttpServletRequest)pageContext.getRequest());
		String decodePath = URLDecoder.decode(path,"UTF-8");
		MeePoMeta meta = ps.ls(acl, decodePath, true);
		if(null != meta){
			try{
				ByteArrayOutputStream ous = new ByteArrayOutputStream(4096);
				ps.download(acl, decodePath, version, ous);
				DirectShowFileSaver fs = new DirectShowFileSaver(meta.size);
				InputStream in = new ByteArrayInputStream(ous.toByteArray());
				fs.save(meta.name, in);
				String content = formatString(fs.getFileContent());
				pageContext.getOut().print(content);
			}catch(AccessForbidden e){
				LOG.error("文件读取错误！已被删除或无权访问！", e);
				pageContext.getOut().print("文件读取错误！有可能该文件已被删除或者您无权访问！");
			}
		}
		return SKIP_BODY;
	}
	
	private String formatString(String sourceStr){
		String source = sourceStr;
		source = source.replaceAll("&", "&amp;");
		source = source.replaceAll("<", "&lt;");
		source = source.replaceAll(">", "&gt;");
		source = source.replaceAll("\"", "&quot;");
		return source;
	}
}
