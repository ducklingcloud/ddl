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

import net.duckling.ddl.common.DDLFacade;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.browselog.BrowseLogService;
import net.duckling.ddl.service.file.FileVersion;
import net.duckling.ddl.service.file.FileVersionService;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.web.bean.DirectShowFileSaver;
import cn.vlabs.clb.api.AccessForbidden;
import cn.vlabs.clb.api.document.MetaInfo;

public class DirectShowFileTag extends VWBBaseTag {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private int rid;
    private int version;

    /**
     * @return the fid
     */
    public int getRid() {
        return rid;
    }

    /**
     * @param fid the fid to set
     */
    public void setRid(int rid) {
        this.rid = rid;
    }

    /**
     * @return the version
     */
    public int getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public int doVWBStart() throws Exception {
        FileVersionService ifs = DDLFacade.getBean(FileVersionService.class);
        FileVersion fv = ifs.getFileVersion(rid, VWBContext.getCurrentTid(), version);
        ResourceOperateService ros = DDLFacade.getBean(ResourceOperateService.class);
        if(null != fv){
            try{
                MetaInfo mi = ros.getMetaInfo(fv.getClbId(), fv.getClbVersion()+"");
                DirectShowFileSaver fs = new DirectShowFileSaver(mi.getSize());
                ros.getContent(fv.getClbId(), fv.getClbVersion()+"", fs);
                String content = formatString(fs.getFileContent());
                pageContext.getOut().print(content);
                //add by lvly@2012-07-23  记下载次数
                DDLFacade.getBean(BrowseLogService.class).resourceVisited(VWBContext.getCurrentTid(), rid, vwbcontext.getCurrentUID(), vwbcontext.getCurrentUserName(), LynxConstants.TYPE_FILE);;
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
