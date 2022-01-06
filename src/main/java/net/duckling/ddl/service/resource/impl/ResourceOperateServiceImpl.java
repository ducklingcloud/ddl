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
package net.duckling.ddl.service.resource.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.constant.ParamConstants;
import net.duckling.ddl.exception.HasDeletedException;
import net.duckling.ddl.exception.NoEnoughSpaceException;
import net.duckling.ddl.exception.ResourceExistedException;
import net.duckling.ddl.service.authority.AuthorityService;
import net.duckling.ddl.service.devent.EventDispatcher;
import net.duckling.ddl.service.draft.Draft;
import net.duckling.ddl.service.draft.IDraftService;
import net.duckling.ddl.service.file.AttSaver;
import net.duckling.ddl.service.file.DFileSaver;
import net.duckling.ddl.service.file.FileStorage;
import net.duckling.ddl.service.file.FileVersion;
import net.duckling.ddl.service.file.FileVersionService;
import net.duckling.ddl.service.param.IParamService;
import net.duckling.ddl.service.param.Param;
import net.duckling.ddl.service.resource.FolderPathService;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.IStarmarkService;
import net.duckling.ddl.service.resource.ITagService;
import net.duckling.ddl.service.resource.PageHelper;
import net.duckling.ddl.service.resource.PageRender;
import net.duckling.ddl.service.resource.PageVersion;
import net.duckling.ddl.service.resource.PageVersionService;
import net.duckling.ddl.service.resource.PathName;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.ResourceBuilder;
import net.duckling.ddl.service.resource.ResourceDirectoryTrash;
import net.duckling.ddl.service.resource.ResourceDirectoryTree;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.service.resource.Tag;
import net.duckling.ddl.service.resource.TeamSpaceSizeService;
import net.duckling.ddl.service.sync.IJounalService;
import net.duckling.ddl.service.sync.Jounal;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.web.bean.ClbUrlTypeBean;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import cn.vlabs.clb.api.document.ChunkResponse;
import cn.vlabs.clb.api.document.DocMetaInfo;
import cn.vlabs.clb.api.document.MetaInfo;
@Service
@Transactional(propagation=Propagation.REQUIRED, isolation=Isolation.READ_COMMITTED,rollbackFor=Exception.class)
public class ResourceOperateServiceImpl implements ResourceOperateService {
    /**
     * 资源关系树
     * @author zhonghui
     *
     */
    static class ResourceFolder{
        private List<ResourceFolder> children;
        private Resource resource;
        private int rid;
        private String type;
        ResourceFolder(Resource r){
            resource = r;
            rid = r.getRid();
            type = r.getItemType();
            children = new ArrayList<ResourceFolder>();
        }
        public void addChildren(ResourceFolder child) {
            children.add(child);
        }
        public List<ResourceFolder> getChildren() {
            return children;
        }
        public Resource getResource() {
            return resource;
        }
        public int getRid() {
            return rid;
        }
        public String getType() {
            return type;
        }
    }
    private static final Logger LOG = Logger.getLogger(ResourceOperateServiceImpl.class);
    @Autowired
    private AuthorityService authorityService;
    @Autowired
    private IDraftService draftService;
    @Autowired
    private EventDispatcher eventDispatcher;
    @Autowired
    private FileVersionService fileVersionService;
    @Autowired
    private FolderPathService folderPathService;
    @Autowired
    private PageVersionService pageVersionService;
    @Autowired
    private IResourceService resourceService;
    @Autowired
    private IStarmarkService starmarkService;
    @Autowired
    private FileStorage storage;
    @Autowired
    private AoneUserService aoneUserService;
    @Autowired
    private IParamService paramService;
    @Autowired
    private ITagService tagService;
    @Autowired
    private TeamSpaceSizeService teamSpaceSizeService;
    @Autowired
    private ResourceDirectoryTrashService resourceDirectoryTrashService;
    @Autowired
    private IJounalService jounalService;

    @Value("${duckling.file.proxy.gateway}")
    private String transpondFlag;

    public Object[] prepareChunkUpload(String uid, int tid, int parentRid,String fileName, String md5, long size) throws NoEnoughSpaceException {
        teamSpaceSizeService.validateTeamSizes(tid,size);
        ChunkResponse res = storage.prepareChunkUpload(fileName, md5, size);
        String status = LynxConstants.STATUS_PREPARE;
        //文件已存在直接设置资源为可用
        if(ChunkResponse.FOUND_THE_SAME_CONTENT == res.getStatusCode()){
            status = LynxConstants.STATUS_AVAILABLE;
        }
        FileVersion fv = createFileAndFileVersion(tid,parentRid, uid, res.getDocid(),1, fileName, size, status, null, null);
        teamSpaceSizeService.resetTeamResSize(tid);
        LOG.info("upload chunk, phase="+ res.getPhase() +";filename="+fileName+";size="+size+ "uid:"+uid+";status("+ res.getStatusCode() +"): "+ res.getStatusMessage() );
        return new Object[]{res, fv.getRid()};
    }

    public ChunkResponse executeChunkUpload(int rid, int tid, int chunkedIndex, byte[] buf, int numOfBytes) {
        long begin = System.currentTimeMillis();
        FileVersion fv = fileVersionService.getLatestFileVersion(rid, tid);
        ChunkResponse res = storage.executeChunkUpload(fv.getClbId(), chunkedIndex, buf, numOfBytes);

        LOG.info("upload chunk, phase="+ res.getPhase() +";rid="+rid+";chunkIndex="+res.getChunkIndex()+";chunkSize="+res.getChunkSize()+
                 ";status("+ res.getStatusCode() +"): "+ res.getStatusMessage()+";spend time:"+(System.currentTimeMillis()-begin)+"ms");
        return res;
    }

    public ChunkResponse finishChunkUpload(int rid, int tid){
        FileVersion fv = fileVersionService.getLatestFileVersion(rid, tid);
        ChunkResponse res = storage.finishChunkUpload(fv.getClbId());
        //更新资源状态可用
        List<Integer> rids = new ArrayList<Integer>();
        rids.add(rid);
        resourceService.updateResourceStatus(rids, LynxConstants.STATUS_AVAILABLE, tid);

        LOG.info("upload chunk, phase="+ res.getPhase() +";rid="+rid+";chunkIndex="+res.getChunkIndex()+";chunkSize="+res.getChunkSize()+
                 ";status("+ res.getStatusCode() +"): "+ res.getStatusMessage());

        //记录jounal
        jounalService.add(tid,fv.getRid(),fv.getVersion(), fv.getDevice(),
                          Jounal.OPERATION_ADD,false, jounalService.getPathString(fv.getRid()));
        return res;
    }


    private Resource copyFile(int destTid,int parentRid, Resource src, String uid) {
        Resource desc = ResourceBuilder.getNewFile(destTid, parentRid, uid, src.getTitle(), src.getFileType(),src.getSize());
        FileVersion v = fileVersionService.getLatestFileVersion(src.getRid(), src.getTid());
        if(v == null){
            return null;
        }
        FileVersion f  = createFileVersion(desc, v.getClbId(), v.getClbVersion(),v.getSize(), null, null);
        f.setTid(destTid);
        createFile(desc, f);

        //记录jounal
        jounalService.add(f.getTid(),f.getRid(),f.getVersion(), f.getDevice(),
                          Jounal.OPERATION_ADD, false, jounalService.getPathString(f.getRid()));
        return desc;
    }

    private Resource copyFolder(int destTid,int parentRid,Resource r,String uid){
        Resource dest = ResourceBuilder.getNewFolder(destTid, parentRid, uid, r.getTitle(),r.getSize());
        createFolder(dest);

        //记录jounal
        jounalService.add(dest.getTid(),dest.getRid(),dest.getLastVersion(), null,
                          Jounal.OPERATION_ADD, true,jounalService.getPathString(dest.getRid()));
        return dest;
    }

    private Resource copyPage(int destTid,int parentRid, Resource src, String uid) {
        Resource dest = ResourceBuilder.getNewPage(destTid, parentRid, uid, src.getTitle(),src.getSize());
        PageVersion srcPage = pageVersionService.getLatestPageVersion(src.getRid());
        if(srcPage==null){
            return null;
        }
        PageVersion p = PageHelper.createPageVersion(dest, srcPage.getContent());
        p.setTid(destTid);
        createPage(dest, p);

        //记录jounal
        jounalService.add(dest.getTid(),dest.getRid(),dest.getLastVersion(), null,
                          Jounal.OPERATION_ADD, false,jounalService.getPathString(dest.getRid())+"."+Resource.DDOC);
        return dest;
    }

    private Resource coypResourceFodler(int destTid, int parentRid, ResourceFolder f, String uid) {
        Resource desc = null;
        if(f!=null){
            Resource src = f.getResource();
            if(src.isFolder()){
                desc = copyFolder(destTid,parentRid, src, uid);
            }else if(src.isFile()){
                desc = copyFile(destTid,parentRid, src, uid);
            }else if(src.isPage()){
                desc = copyPage(destTid,parentRid,src,uid);
            }
            if(desc != null){
                for(ResourceFolder rf : f.getChildren()){
                    coypResourceFodler(destTid, desc.getRid(), rf, uid);
                }
                if(!src.getTitle().equals(desc.getTitle())){
                    renameResourceVersion(desc);
                }
            }
        }
        return desc;
    }

    private FileVersion createFileAndFileVersion(int tid,int parentRid, String uid,
                                                 int clbId,int clbVersion, String filename, long size) {
        return createFileAndFileVersion(tid, parentRid, uid, clbId, clbVersion, filename, size, LynxConstants.STATUS_AVAILABLE, null, null);
    }

    private FileVersion createFileAndFileVersion(int tid,int parentRid, String uid,
                                                 int clbId,int clbVersion, String filename, long size, String status, String checksum, String device) {
        Resource f = createNewFile(tid, parentRid, uid,clbId, filename,size, status);
        FileVersion fv = createNewFileVersion(f,clbId,clbVersion, size, checksum, device);
        updateFile(f, fv);
        resourceService.update(f);
        folderPathService.create(parentRid, f.getRid(), tid);
        updateResourceTime(parentRid, uid);
        return fv;
    }

    private FileVersion createFileVersion(Resource file,int clbId,int clbVersion,long size,
                                          String checksum, String device){
        int version = file.getLastVersion()+1;
        if(checksum==null){
            DocMetaInfo docMetaInfo = storage.getDocMeta(clbId);
            checksum = docMetaInfo.md5;
        }
        FileVersion fileVersion = new FileVersion();
        fileVersion.setClbVersion(clbVersion);
        fileVersion.setRid(file.getRid());
        fileVersion.setTid(file.getTid());
        fileVersion.setVersion(version);
        fileVersion.setClbId(clbId);
        fileVersion.setSize(size);
        fileVersion.setTitle(file.getTitle());
        fileVersion.setEditor(file.getLastEditor());
        fileVersion.setEditTime(file.getCreateTime());
        fileVersion.setChecksum(checksum);
        fileVersion.setDevice(device);
        return fileVersion;
    }


    private FileVersion createFileVersion(Resource file, int clbId, int clbVersion, long size,
                                          String title, String editor, boolean isInitVer){
        Date date = isInitVer?file.getCreateTime():(new Date());
        file.setLastEditor(editor);
        file.setTitle(title);
        file.setCreateTime(date);
        return createFileVersion(file, clbId, clbVersion, size, null, null);
    }


    private Resource createNewFile(int tid,int parentRid, String creator, int clbId, String title,long size, String status) {
        Date curDate = new Date();
        Resource file = new Resource();
        file.setStatus(status);
        file.setTid(tid);
        file.setBid(parentRid);
        file.setTitle(title);
        file.setCreateTime(curDate);
        file.setCreator(creator);
        file.setLastEditor(creator);
        file.setLastEditorName(aoneUserService.getUserNameByID(creator));
        file.setLastEditTime(curDate);
        file.setLastVersion(LynxConstants.INITIAL_VERSION);
        file.setItemType(LynxConstants.TYPE_FILE);
        file.setOrderType(Resource.NO_FOLDER_ORDER_TYPE);
        file.setFileType(title.substring(title.lastIndexOf('.')+1, title.length()));
        file.setSize(size);
        createResource(file);
        return file;
    }

    private FileVersion createNewFileVersion(Resource f,int clbId,int clbVersion ,long size, String checksum, String device) {
        FileVersion v = createFileVersion(f,clbId,clbVersion, size,null,null);
        int fvid = fileVersionService.create(v);
        v.setId(fvid);
        return v;
    }


    private void deleteSingleResource(Resource resource,int tid){
        if(resource!=null&&tid==resource.getTid()){
            if(resource.isPage()){
                deleteDDoc(resource.getRid());
            }else if(resource.isFile()){
                deleteFile(tid,resource.getRid());
            }else if(resource.isFolder()){
                deleteSingleFolder(tid, resource.getRid());
            }
        }
    }

    private List<Resource> getFileResourceByTitile(int tid,int parentRid,String fileName){
        return resourceService.getFileByTitle(tid, parentRid, fileName);
    }

    private Resource getResource(int tid,int rid){
        Resource r =resourceService.getResource(rid);
        if(r!=null&&tid==r.getTid()&&!r.isDelete()){
            return r;
        }
        return null;
    }

    private boolean isContentChanged(PageVersion newVersion,PageVersion oldVersion){
        if(oldVersion==null){
            return false;
        }
        return oldVersion.getContent().hashCode() != newVersion.getContent().hashCode();
    }


    private boolean isTitleChanged(PageVersion newVersion,PageVersion oldVersion){
        if(oldVersion==null){
            return false;
        }
        return oldVersion.getTitle().hashCode() != newVersion.getTitle().hashCode();
    }


    private ResourceFolder parseFolderTree(List<Resource> rs,int rootRid){
        ResourceFolder result = null;
        if(rs!=null){
            Map<Integer,ResourceFolder> map = new HashMap<Integer,ResourceFolder>();
            //从最深的子孙开始添加，最后一个一定是树顶。
            Collections.reverse(rs);
            for(Resource r : rs){
                ResourceFolder rf = new ResourceFolder(r);
                map.put(r.getRid(), rf);
                ResourceFolder parent = map.get(r.getBid());
                if(parent!=null){
                    parent.addChildren(rf);
                }
                if(rootRid==r.getRid()){
                    result = rf;
                }
            }
        }
        return result;
    }


    private void updateFile(Resource file, FileVersion fileVersion){
        file.setTitle(fileVersion.getTitle());
        file.setLastEditor(fileVersion.getEditor());
        file.setLastEditorName(aoneUserService.getUserNameByID(fileVersion.getEditor()));
        file.setLastEditTime(fileVersion.getEditTime());
        file.setLastVersion(fileVersion.getVersion());
        file.setSize(fileVersion.getSize());
    }


    @Override
    public void addResource(Resource r) {
        createResource(r);
        folderPathService.create(r.getBid(), r.getRid(), r.getTid());
        if(!r.isPage()){
            updateResourceTime(r.getBid(), r.getCreator());
        }
    }

    @Override
    public Resource copyResource(int destTid, int destRid,int srcTid, int srcRid,String uid) {
        List<Resource> descendants = folderPathService.getDescendants(srcTid, srcRid);
        ResourceFolder f = parseFolderTree(descendants,srcRid);
        Resource dest = coypResourceFodler(destTid,destRid,f,uid);
        eventDispatcher.sendResourceCopyEvent(destTid, dest, destRid);
        teamSpaceSizeService.resetTeamResSize(destTid);
        updateResourceTime(destRid, uid);
        return dest;
    }
    @Override
    public List<Resource> copyResource(int destTid, int destRid,int srcTid, List<Integer> srcRids, String uid) {
        List<Resource> resourceList=new ArrayList<Resource>();
        if(srcRids!=null){
            for(int srcRid : srcRids){
                resourceList.add(copyResource(destTid, destRid,srcTid, srcRid, uid));
            }
        }
        return resourceList;
    }
    public void createFile(Resource r,FileVersion fileVersion){
        r.setLastVersion(fileVersion.getVersion());
        createResource(r);
        fileVersion.setRid(r.getRid());
        folderPathService.create(r.getBid(), r.getRid(), r.getTid());
        fileVersionService.create(fileVersion);
    }

    @Override
    public int createFolder(Resource r){
        return createFolder(r,null);
    }
    @Override
    public int createFolder(Resource r, String device){
        int rid = createResource(r);
        folderPathService.create(r.getBid(), r.getRid(), r.getTid());
        updateResourceTime(r.getBid(), r.getLastEditor());

        LOG.info("用户"+r.getCreator()+"创建resournce"+r);
        eventDispatcher.sendFolderCreateEvent(r.getTid(), r);

        jounalService.add(r.getTid(),r.getRid(),0, device,
                          Jounal.OPERATION_ADD,true, jounalService.getPathString(r.getRid()));
        return rid;
    }

    /**
     * 创建resource，并保证名称唯一
     * @param r
     */
    private int createResource(Resource r){
        String title = folderPathService.getResourceName(r.getTid(), r.getBid(), r.getItemType(), r.getTitle());
        r.setTitle(title);
        int rid = resourceService.create(r);
        teamSpaceSizeService.resetTeamResSize(r.getTid());
        return rid;
    }

    public void createPage(Resource r ,PageVersion pageVersion){
        r.setLastVersion(pageVersion.getVersion());
        createResource(r);
        pageVersion.setRid(r.getRid());
        pageVersionService.create(pageVersion);
        folderPathService.create(r.getBid(), r.getRid(), r.getTid());
    }

    @Override
    public int createNewPage(int parentRid, String title, int tid, String uid){
        Resource res = ResourceBuilder.getNewPage(tid, parentRid, uid, title, 0);
        PageVersion pv = PageHelper.createPageVersion(res, "");
        pv.setTid(tid);
        createPage(res, pv);
        return res.getRid();
    }

    @Override
    public PageVersion createPageVersion(Resource resource, String content) {
        PageVersion v = PageHelper.createPageVersion(resource, content);
        pageVersionService.create(v);
        resource.setLastVersion(v.getVersion());
        resource.setSize(v.getSize());
        resourceService.update(resource);
        tagService.updateTagCountByRid(resource.getTid(), resource.getRid());
        eventDispatcher.sendPageCreateEvent(v.getTid(), resource);
        updateParentTime(resource.getRid(), resource.getLastEditor());

        jounalService.add(resource.getTid(),resource.getRid(),resource.getLastVersion(), null,
                          Jounal.OPERATION_ADD, false, jounalService.getPathString(resource.getRid())+"."+Resource.DDOC);
        return v;
    }

    @Override
    public boolean deleteAuthValidate(int tid,int rid, String currentUid) {
        Resource r = getResource(tid,rid);
        if(r==null){
            return false;
        }
        List<Resource> descendants = folderPathService.getDescendants(tid, rid);
        //      if(haveLockResouce(descendants)){
        //          return false;
        //      }
        if(Team.AUTH_ADMIN.equals(authorityService.getTeamAuthority(tid, currentUid))){
            return true;
        }
        if(!authorityService.haveTeamEditeAuth(tid, currentUid)){
            return false;
        }
        if(r.isFile()||r.isPage()){
            return currentUid.equals(r.getCreator());
        }

        for(Resource descendant : descendants){
            if(!currentUid.equals(descendant.getCreator())){
                return false;
            }
        }
        return true;
    }
    /**
     * 删除page，并删除其中的星标和标签，切星标和标签永久删除不能恢复
     * @param rid
     */
    public void deleteDDoc(int rid) {
        Resource r = resourceService.getResource(rid);
        if(r==null||!r.isPage()||r.isDelete()){
            return ;
        }
        starmarkService.removeResourceStarmark(r);
        tagService.removeAllTagItemsOfRid(r.getTid(), r.getRid());
        fileVersionService.deleteRefer(rid, r.getTid());
        pageVersionService.deleteAllPageVersion(rid,r.getTid());
        resourceService.delete(rid, r.getTid(), r.getItemType());
    }
    /**
     *  删除file，并删除其中的星标和标签，切星标和标签永久删除不能恢复
     * @param tid
     * @param rid
     */
    public void deleteFile(int tid,int rid) {
        Resource r = resourceService.getResource(rid);
        if(r==null||tid!=r.getTid()||!r.isFile()||r.isDelete()){
            return ;
        }
        starmarkService.removeResourceStarmark(r);
        tagService.removeAllTagItemsOfRid(r.getTid(), r.getRid());
        fileVersionService.deleteRefer(rid, tid);
        fileVersionService.deleteAllFileVersion(rid, tid);
        resourceService.delete(rid, r.getTid(), r.getItemType());
    }

    @Override
    public void deleteResource(int tid, List<Integer> rids,String uid) {
        if(rids!=null){
            for(int rid : rids){
                deleteResource(tid, rid,uid);
            }
        }
    }

    @Override
    public void deleteResource(int tid, int rid,String uid) {
        deleteResource(tid, rid, uid, null);
    }

    @Override
    public void deleteResource(int tid, int rid,String uid, String device) {
        if(rid==0){
            return ;
        }
        Resource r = getResource(tid, rid);
        if(r==null){
            return ;
        }
        String path = jounalService.getPathString(r.getRid());

        List<Resource> descendants = folderPathService.getDescendants(tid, rid);
        if(descendants==null||descendants.isEmpty()){
            deleteSingleResource(r, tid);
        }
        for(Resource resource : descendants){
            deleteSingleResource(resource, tid);
        }
        if(r.isUnpublish()){
            deleteSingleResource(r, tid);
        }
        deleteResourcePath(tid, descendants,r);
        teamSpaceSizeService.resetTeamResSize(tid);
        //草稿删除的时候不更新父目录时间
        if(r.getLastVersion()!=0){
            updateParentTime(rid, uid);
        }

        if(r.isAvailable()){
            sendDeleteEvent(tid, r, uid);
            path = path + getDdocExt(r.getItemType());
            jounalService.add(r.getTid(),r.getRid(),r.getLastVersion(), device,
                              Jounal.OPERATION_DELETE, r.isFolder(), path);
            LOG.info("用户"+uid+"删除resournce"+r);
        }

    }

    /**
     * 删除所有resource的path
     * @param tid
     * @param rs
     */
    private void deleteResourcePath(int tid,List<Resource> rs,Resource resource){
        List<Integer> rids = new ArrayList<Integer>();
        if(resource.isUnpublish()){
            rids.add(resource.getRid());
        }
        for(Resource r : rs){
            rids.add(r.getRid());
        }
        folderPathService.delete(tid, rids);
    }

    public void deleteSingleFolder(int tid,int rid){
        Resource r = getResource(tid, rid);
        if(r!=null){
            starmarkService.removeResourceStarmark(r);
            tagService.removeAllTagItemsOfRid(tid, rid);
            resourceService.delete(rid,r.getTid(), r.getItemType());
        }
    }

    @Override
    public void getContent(int docid, AttSaver fs) {
        storage.getContent(docid, -1, fs);
    }
    @Override
    public void getContent(int docid, String version, DFileSaver fs) {
        if(StringUtils.isEmpty(version)){
            version = "-1";
        }
        storage.getContent(docid, Integer.parseInt(version), fs);
    }
    @Override
    public List<Resource> getDDoc(int tid, List<Integer> rids) {
        return resourceService.getDDoc(tid, rids);
    }
    @Override
    public String getDirectURL(int clbId, String version, boolean isPDF) {
        String url = storage.getDirectURL(clbId, version, isPDF);
        return url.replace("http://", "/" + transpondFlag + "/");
    }
    @Override
    public void getImageContent(int docid, int version, String type,
                                DFileSaver fs) {
        storage.getImageContent(docid, version, type, fs);
    }
    @Override
    public ClbUrlTypeBean getImageDirevtURL(int clbId, String version, String type) {
        return  storage.getImageDirevtURL(clbId, version, type);
    }
    @Override
    public MetaInfo getMetaInfo(int docid) {
        return storage.getMeta(docid);
    }
    @Override
    public MetaInfo getMetaInfo(int docid, String version) {
        return storage.getMeta(docid, version);
    }
    @Override
    public PageRender getPageRender(int tid,int rid) {
        Resource r = resourceService.getResource(rid);
        if(tid!=r.getTid()||r.isDelete()){
            r = null;
        }
        PageVersion detail = null;
        if(r!=null&&r.getLastVersion()!=LynxConstants.INITIAL_VERSION){
            detail = pageVersionService.getLatestPageVersion(rid);
        }
        if(detail==null){
            detail = new PageVersion();
        }

        //如果是新建未发布的文档，则显示草稿内容 added by lishanbo.20140730
        if(r.getLastVersion() == 0){
            Draft draft = draftService.getLastestDraft(r.getTid(), rid, r.getCreator());
            if(draft!=null){
                detail.setContent(draft.getContent());
            }
        }

        return new PageRender(r,detail);
    }


    @Override
    public PageRender getPageRender(int tid,int rid, int version) {
        Resource r = resourceService.getResource(rid);
        if(r==null||tid!=r.getTid()||r.isDelete()){
            r=null;
        }
        PageVersion detail = null;
        if(r!=null&&r.getLastVersion()!=LynxConstants.INITIAL_VERSION){
            detail = pageVersionService.getPageVersion(rid, version);
        }
        if(detail==null){
            detail = new PageVersion();
        }
        return new PageRender(r,detail);
    }
    @Override
    public void getPdfContent(int docid, String version, DFileSaver fs) {
        storage.getPdfContent(docid, Integer.parseInt(version), fs);
    }

    private void updateParentTime(int rid,String uid){
        if(rid>0){
            Resource r = resourceService.getResource(rid);
            if(r!=null){
                updateResourceTime(r.getBid(),uid);
            }
        }
    }
    private void updateResourceTime(int rid,String uid){
        if(rid>0){
            Resource r = resourceService.getResource(rid);
            updateResourceTime(r, uid);
        }
    }
    private void updateResourceTime(Resource r,String uid){
        if(r!=null){
            r.setLastEditTime(new Date());
            r.setLastEditor(uid);
            r.setLastEditorName(aoneUserService.getUserNameByID(uid));
            resourceService.update(r);
        }
    }
    @Override
    public void moveResource(int tid, int destRid, int srcRid,String uid) {
        Resource r = resourceService.getResource(srcRid);
        String srcPath = jounalService.getPathString(r.getRid());

        String title = folderPathService.getResourceName(tid, destRid, r.getItemType(), r.getTitle());
        if(!title.equals(r.getTitle())){
            r.setTitle(title);
        }
        r.setBid(destRid);
        resourceService.update(r);
        folderPathService.move(tid, srcRid, destRid);
        eventDispatcher.sendResourceMoveEvent(tid, resourceService.getResource(srcRid), destRid,uid);
        updateResourceTime(destRid, uid);

        String ext = getDdocExt(r.getItemType());
        jounalService.add(tid,r.getRid(),r.getLastVersion(), null,
                          Jounal.OPERATION_MOVE, r.isFolder(), srcPath + ext, jounalService.getPathString(r.getRid())+ext);
    }

    //  @Override
    //  public void moveResource(int tid, int destRid, List<Integer> srcRids,String uid) {
    //      if(srcRids!=null){
    //          for(int srcRid : srcRids){
    //              moveResource(tid, destRid, srcRid,uid);
    //          }
    //      }
    //  }

    @Override
    public void moveResource(int tid, int destRid, List<Integer> srcRids,String uid) {
        if(srcRids==null||srcRids.isEmpty()){
            return;
        }
        if(srcRids.size()==1){
            moveResource(tid, destRid, srcRids.get(0), uid);
        }else{
            List<Resource> rs = resourceService.getResource(srcRids);
            validateResource(rs, srcRids, tid,destRid);
            if(rs.isEmpty()){
                return;
            }
            List<Jounal> jounalList = assembleJounalList(rs, destRid, tid);

            folderPathService.updateResourceName(tid,destRid,rs);
            folderPathService.move(tid, rs, destRid);
            for(Resource r : rs){
                r.setBid(destRid);
            }
            resourceService.update(rs);
            for(Resource r : rs){
                eventDispatcher.sendResourceMoveEvent(tid, r, destRid,uid);
            }
            jounalService.addBatch(jounalList);
        }
        updateResourceTime(destRid, uid);
    }

    private List<Jounal> assembleJounalList(List<Resource> rs, int destRid, int tid){
        List<Jounal> jounalList = new ArrayList<Jounal>();
        String srcParentPath = (new PathName(jounalService.getPathString(rs.get(0).getRid()))).getContextPath();
        String destPath = jounalService.getPathString(destRid);

        for(Resource r : rs){
            String ext = getDdocExt(r.getItemType());
            jounalList.add(jounalService.assmebleJounal(tid,r.getRid(),r.getLastVersion(),null,Jounal.OPERATION_MOVE,r.isFolder(),
                                                        PathName.appendDelimiter(srcParentPath)+r.getTitle() + ext,PathName.appendDelimiter(destPath)+r.getTitle()+ext));
        }
        return jounalList;
    }

    void validateResource(List<Resource> rs,List<Integer> rids,int tid,int destRid){
        Iterator<Resource> it = rs.iterator();
        while(it.hasNext()){
            Resource r = it.next();
            if(r.getTid()!=tid||r.getBid()==destRid){
                it.remove();
                rids.remove(r.getRid());
            }
        }
    }
    @Override
    public void publishManualSaveDraft(int version, Draft d) {
        Resource meta = resourceService.getResource(d.getRid());

        //状态为新建的文档，不发布草稿  changed by lishanbo. 20140730
        if (version == 0) {
            //meta.setTitle(d.getTitle());
            //meta.setLastEditor(d.getUid());
            //meta.setLastEditorName(aoneUserService.getUserNameByID(d.getUid()));
            //meta.setLastEditTime(d.getModifyTime());
            //meta.setStatus(LynxConstants.STATUS_AVAILABLE);
            //createPageVersion(meta, d.getContent());
            return;
        } else {
            updatePageVersion(meta, d.getContent());
        }
        draftService.clearAutoSaveDraft(d.getTid(), d.getRid(), d.getUid());
        draftService.clearManualSaveDraft(d.getTid(), d.getRid(), d.getUid());
        updateParentTime(meta.getBid(), meta.getLastEditor());
    }
    @Override
    public String queryPdfStatus(int clbId, String version) {
        return storage.queryPdfStatus(clbId, version);
    }

    @Override
    public int recoverDDoc(int tid, int rid,int parentRid,String uid) throws NoEnoughSpaceException {
        Resource resource = resourceService.getResource(rid, tid);
        if(resource==null||resource.isAvailable()||tid!=resource.getTid()){
            return 0;
        }
        Resource pr = resourceService.getResource(parentRid);
        if(pr==null||pr.isDelete()){
            parentRid=0;
        }
        String title =folderPathService.getResourceName(tid, parentRid, LynxConstants.TYPE_PAGE, resource.getTitle());
        resource.setTitle(title);
        resource.setBid(parentRid);
        resource.setStatus(LynxConstants.STATUS_AVAILABLE);
        teamSpaceSizeService.validateTeamSizes(tid, resource.getSize());
        resourceService.update(resource);
        pageVersionService.recoverPageVersion(rid, tid);
        folderPathService.create(parentRid, rid, tid);
        teamSpaceSizeService.resetTeamResSize(tid);
        updateResourceTime(parentRid, uid);

        jounalService.add(tid,resource.getRid(),resource.getLastVersion(), null,
                          Jounal.OPERATION_ADD, resource.isFolder(), jounalService.getPathString(rid)+"."+Resource.DDOC);
        return 0;
    }
    @Override
    public int recoverFile(int tid, int rid,int parentRid,String uid) throws NoEnoughSpaceException {
        Resource resource = resourceService.getResource(rid, tid);
        if(resource==null||resource.isAvailable()||tid!=resource.getTid()){
            return 0;
        }
        Resource pr = resourceService.getResource(parentRid);
        if(pr==null||pr.isDelete()){
            parentRid=0;
        }
        teamSpaceSizeService.validateTeamSizes(tid, resource.getSize());
        String title =folderPathService.getResourceName(tid, parentRid, LynxConstants.TYPE_FILE, resource.getTitle());
        resource.setTitle(title);
        resource.setBid(parentRid);
        resource.setStatus(LynxConstants.STATUS_AVAILABLE);
        resourceService.update(resource);
        fileVersionService.recoverFileVersion(rid, tid);
        folderPathService.create(parentRid, rid, tid);
        teamSpaceSizeService.resetTeamResSize(tid);
        updateResourceTime(parentRid, uid);

        jounalService.add(tid,resource.getRid(),resource.getLastVersion(), null,
                          Jounal.OPERATION_ADD, resource.isFolder(), jounalService.getPathString(rid));
        return 0;
    }
    @Override
    public void recoverFileVersion(int tid, int rid, int version,String uid) {
        FileVersion fileVersion = fileVersionService.getFileVersion(rid, tid, version);
        Resource file = getResource(tid,rid);
        if (fileVersion == null || file == null) {

        } else {
            FileVersion fileVersionNew = createFileVersion(file, fileVersion.getClbId(), fileVersion.getClbVersion(), fileVersion.getSize(),
                                                           fileVersion.getTitle(), fileVersion.getEditor(), false);
            file.setLastVersion(fileVersionNew.getVersion());
            file.setTitle(fileVersionNew.getTitle());
            file.setLastEditTime(new Date());
            file.setLastEditor(fileVersionNew.getEditor());
            file.setLastEditorName(aoneUserService.getUserNameByID(fileVersionNew.getEditor()));
            resourceService.update(file);
            fileVersionService.create(fileVersionNew);
            updateParentTime(rid, uid);
        }
    }

    @Override
    public void recoverPageVersion(int tid, int rid, int recoverVersion) {
        PageVersion  p = pageVersionService.getPageVersion(rid, recoverVersion);
        if(p!=null&&tid==p.getTid()){
            Resource r = resourceService.getResource(rid);
            updatePageVersion(r, p.getContent());
        }
    }
    @Override
    public FileVersion referExistFileByClbId(int tid,int parentRid, String uid, int clbId,int clbVersion,
                                             String filename, long size) {
        filename = folderPathService.getResourceName(tid, parentRid, LynxConstants.TYPE_FILE, filename);
        FileVersion fv = createFileAndFileVersion(tid,parentRid, uid, clbId,clbVersion ,filename, size);
        jounalService.add(tid, fv.getRid(), fv.getVersion(), "", Jounal.OPERATION_ADD, false, jounalService.getPathString(fv.getRid()));
        return fv;
    }

    @Override
    public boolean renameResource(int tid, int rid, String uid, String fileName) {
        return renameResource(tid, rid, uid, fileName, null);
    }
    @Override
    public boolean renameResource(int tid, int rid, String uid, String fileName, String device) {
        Resource r = getResource(tid, rid);
        if(r==null){
            return false;
        }
        if(r.getTitle().equals(fileName)){
            return true;
        }
        String srcPath = jounalService.getPathString(r.getRid());

        String oldName = r.getTitle();
        fileName = folderPathService.getResourceName(tid, r.getBid(), r.getItemType(), fileName);
        r.setTitle(fileName);
        renameResourceVersion(r);
        resourceService.update(r);
        eventDispatcher.sendResourceRenameEvent(tid, r, oldName,uid);

        String ext = getDdocExt(r.getItemType());
        jounalService.add(tid,r.getRid(),r.getLastVersion(), device,
                          Jounal.OPERATION_MOVE, r.isFolder(), srcPath+ext, jounalService.getPathString(r.getRid())+ext);
        return true;
    }

    private void renameResourceVersion(Resource r){
        if(r.isFile()){
            FileVersion fv = fileVersionService.getLatestFileVersion(r.getRid(), r.getTid());
            fv.setTitle(r.getTitle());
            fileVersionService.update(fv.getId(), fv);
        }else if(r.isPage()){
            PageVersion pv = pageVersionService.getLatestPageVersion(r.getRid());
            pv.setTitle(r.getTitle());
            pageVersionService.update(pv.getId(), pv);
        }
    }

    @Override
    public void sendPdfTransformEvent(int clbId, String version) {
        storage.sendPdfTransformEvent(clbId, version);
    }

    public void setFolderPathService(FolderPathService folderPathService) {
        this.folderPathService = folderPathService;
    }


    public void setResourceService(IResourceService resourceService) {
        this.resourceService = resourceService;
    }


    @Override
    public FileVersion updateCLBFile(int rid, int tid, String uid,
                                     String filename, long size, InputStream in, String device) throws NoEnoughSpaceException {
        Resource file = resourceService.getResource(rid);
        if (file.isDelete()){
            throw new HasDeletedException(rid);
        }
        int clbId = -1;
        teamSpaceSizeService.validateTeamSizes(tid, size-file.getSize());
        long begin = System.currentTimeMillis();
        clbId = storage.createFile(filename, size, in);
        FileVersion fileVersion = createFileVersion(file, clbId, 1, size, filename, uid, false);
        updateFile(file, fileVersion);
        fileVersionService.create(fileVersion);
        ResourceBuilder.updateResource(file, fileVersion);
        resourceService.update(file);
        teamSpaceSizeService.resetTeamResSize(tid);
        LOG.info("uid:"+uid+",update rid="+rid+";filename="+filename+";size="+size+";spend time:"+(System.currentTimeMillis()-begin)+"ms");
        eventDispatcher.sendFileModifyEvent(filename, file.getRid(), uid, file.getLastVersion(), tid);
        updateParentTime(rid, uid);

        jounalService.add(tid, fileVersion.getRid(), fileVersion.getVersion(), device, Jounal.OPERATION_ADD, false, jounalService.getPathString(fileVersion.getRid()));

        return fileVersion;
    }
    @Override
    public FileVersion updateFileFromClb(int rid, int tid, String uid,
                                         String fileName, int clbId, String device) throws NoEnoughSpaceException {

        Resource file = resourceService.getResource(rid);
        if (file.isDelete()){
            throw new HasDeletedException(rid);
        }

        DocMetaInfo docMetaInfo = storage.getDocMeta(clbId);
        long size = docMetaInfo.size;
        String checksum = docMetaInfo.md5;

        teamSpaceSizeService.validateTeamSizes(tid, size-file.getSize());
        long begin = System.currentTimeMillis();
        // 添加文件版本
        FileVersion fileVersion = createFileVersion(file, clbId, 1, size, checksum, device);
        updateFile(file, fileVersion);
        int id = fileVersionService.create(fileVersion);
        fileVersion.setId(id);
        ResourceBuilder.updateResource(file, fileVersion);
        resourceService.update(file);
        teamSpaceSizeService.resetTeamResSize(tid);
        eventDispatcher.sendFileModifyEvent(fileName, file.getRid(), uid, file.getLastVersion(), tid);
        updateParentTime(rid, uid);

        jounalService.add(tid, fileVersion.getRid(), fileVersion.getVersion(), device, Jounal.OPERATION_ADD, false, jounalService.getPathString(fileVersion.getRid()));

        LOG.info("update file from clb successfully. {clbId:"+ clbId +", uid:"+uid
                 +", fileName="+fileName+",size="+size+",spendTime:"+(System.currentTimeMillis()-begin)+"ms }");
        return fileVersion;
    }


    @Override
    public void updatePageVersion(Resource meta, String content) {
        PageVersion oldEdition = pageVersionService.getLatestPageVersion(meta.getRid());
        PageVersion newEdition = PageHelper.createPageVersion(meta, content);
        if(isTitleChanged(newEdition,oldEdition)){
            eventDispatcher.sendPageRenameEvent(newEdition.getTid(),meta,oldEdition.getTitle(),oldEdition.getVersion(),meta.getLastEditor());
        }
        if (isContentChanged(newEdition, oldEdition)) {
            pageVersionService.create(newEdition);
            meta.setLastVersion(newEdition.getVersion());
            meta.setSize(newEdition.getSize());
            eventDispatcher.sendPageModifyEvent(newEdition.getTid(), meta);
        }else{
            //更新version title
            renameResource(meta.getTid(), meta.getRid(), meta.getLastEditor(), meta.getTitle());
        }
        resourceService.update(meta);
        teamSpaceSizeService.resetTeamResSize(meta.getTid());
        updateResourceTime(meta.getBid(), meta.getLastEditor());

        jounalService.add(meta.getTid(),meta.getRid(),meta.getLastVersion(), null,
                          Jounal.OPERATION_ADD, false, jounalService.getPathString(meta.getRid())+"."+Resource.DDOC);
    }

    @Override
    public FileVersion upload(String uid, Integer tid, Integer parentRid,String fileName,
                              long size, InputStream in)throws NoEnoughSpaceException {
        return upload(uid,tid,parentRid,fileName,size,in,true);
    }

    @Override
    public FileVersion upload(String uid, Integer tid, Integer parentRid,String fileName,
                              long size, InputStream in,boolean addDefaultTag) throws NoEnoughSpaceException {

        return upload(uid, tid, parentRid, fileName, size, in, addDefaultTag, true, false, null);
    }

    @Override
    public FileVersion upload(String uid, Integer tid, Integer parentRid,String fileName,
                              long size, InputStream in,boolean addDefaultTag,boolean sendEvent) throws NoEnoughSpaceException {
        return upload(uid, tid, parentRid, fileName, size, in, addDefaultTag, sendEvent, false, null);
    }

    @Override
    public FileVersion upload(String uid, Integer tid, Integer parentRid,String fileName,
                              long size, InputStream in,boolean addDefaultTag,boolean sendEvent,
                              boolean fileNameSerial, String folderName) throws NoEnoughSpaceException {
        return upload(uid, tid, parentRid, fileName, size, in, addDefaultTag, sendEvent,  fileNameSerial, folderName, null);
    }

    @Override
    public FileVersion upload(String uid, Integer tid, Integer parentRid,String fileName,
                              long size, InputStream in,boolean addDefaultTag,boolean sendEvent,
                              boolean fileNameSerial, String folderName, String device) throws NoEnoughSpaceException {
        Integer pid = (folderName==null) ? parentRid : getFolderAtRoot(folderName, tid, uid).getRid();

        List<Resource> name = getFileResourceByTitile(tid, pid, fileName);
        //文件是否存在重名
        if(name.size()>0){
            //是否序列自增文件名
            if(fileNameSerial){
                fileName = getSerialFileName(tid, pid, fileName);
            }else{
                //文件更新版本
                return updateCLBFile(name.get(0).getRid(), tid, uid, fileName, size, in, device);
            }
        }
        teamSpaceSizeService.validateTeamSizes(tid,size);
        long begin = System.currentTimeMillis();
        int clbId = storage.createFile(fileName, size, in);
        FileVersion fv = createFileAndFileVersion(tid,pid, uid, clbId,1, fileName, size);
        if(addDefaultTag){
            addDefaultTag(fv.getRid());
        }
        teamSpaceSizeService.resetTeamResSize(tid);
        if(sendEvent){
            eventDispatcher.sendFileUploadEvent(fileName, fv.getRid(), uid, tid);
        }

        jounalService.add(tid, fv.getRid(), fv.getVersion(), device, Jounal.OPERATION_ADD, false, jounalService.getPathString(fv.getRid()));

        LOG.info("uid:"+uid+",upload filename="+fileName+";size="+size+";spend time:"+(System.currentTimeMillis()-begin)+"ms");
        return fv;
    }

    @Override
    public FileVersion createFileFromClb(String uid, Integer tid, Integer parentRid,String fileName, int clbId,boolean sendEvent, String device)
            throws NoEnoughSpaceException, ResourceExistedException {
        List<Resource> nameList = getFileResourceByTitile(tid, parentRid, fileName);
        //文件是否存在重名
        if(nameList.size()>0){
            throw new ResourceExistedException();
        }
        long begin = System.currentTimeMillis();
        // 创建资源和文件版本
        DocMetaInfo docMetaInfo = storage.getDocMeta(clbId);
        long size = docMetaInfo.getSize();
        String checksum = docMetaInfo.md5;
        teamSpaceSizeService.validateTeamSizes(tid,size);

        FileVersion fv = createFileAndFileVersion(tid,parentRid, uid, clbId,1, fileName, size, LynxConstants.STATUS_AVAILABLE, checksum, device);

        teamSpaceSizeService.resetTeamResSize(tid);
        if(sendEvent){
            eventDispatcher.sendFileUploadEvent(fileName, fv.getRid(), uid, tid);
        }

        jounalService.add(tid, fv.getRid(), fv.getVersion(), device, Jounal.OPERATION_ADD, false, jounalService.getPathString(fv.getRid()));

        LOG.info("create file from clb successfully. {clbId:"+ clbId +", uid:"+uid
                 +", fileName="+fileName+",size="+size+",spendTime:"+(System.currentTimeMillis()-begin)+"ms }");
        return fv;
    }

    public void addDefaultTag(int rid){
        Resource resource=resourceService.getResource(rid);
        // 增加默认姓名标签
        Param param = paramService.get(ParamConstants.UserPreferenceType.TYPE,
                                       ParamConstants.UserPreferenceType.KEY_NAME_TAG, resource.getCreator());
        boolean flag = param != null && ParamConstants.UserPreferenceType.VALUE_NAME_TAG_TRUE.equals(param.getValue());
        if(!flag){
            return ;
        }
        if(LynxConstants.TYPE_PAGE.equals(resource.getItemType())){
            flag = resource.getLastVersion() == LynxConstants.INITIAL_VERSION;
        }else{
            flag = resource.getLastVersion() == 1;
        }
        if (flag) {
            Tag tag = tagService.getNameTag(resource.getTid(), resource.getCreator(), aoneUserService.getUserNameByID(resource.getCreator()));
            // 新建页面时
            List<Integer> tagIds = new ArrayList<Integer>();
            tagIds.add(tag.getId());
            tagService.addItems(resource.getTid(), tagIds, rid);
            resourceService.updateTagMap(rid, tag);
        }
    }

    public boolean canUseFileName(int tid,int parentRid,int rid,String itemType,String fileName){
        List<Resource> resourceList=folderPathService.getResourceByName(tid, parentRid, itemType, fileName);
        if(resourceList==null||resourceList.isEmpty()){
            return true;
        }
        boolean result=true;
        for(Resource resource:resourceList){
            if(resource.getRid()==rid){
                continue;
            }else{
                result=false;
                break;
            }
        }

        return result;
    }

    public boolean canUseFileName(int tid,int parentRid,String itemType,String fileName){
        List<Resource> resourceList=folderPathService.getResourceByName(tid, parentRid, itemType, fileName);
        if(resourceList==null||resourceList.isEmpty()){
            return true;
        }
        return false;
    }


    @Override
    public String getImageStatus(int clbId, String version, String type) {
        return storage.getImageStatus(clbId, version, type);
    }

    @Override
    public int recoverFolder(int tid, int rid, int parentRid,String uid) throws NoEnoughSpaceException {
        ResourceDirectoryTrash rd = resourceDirectoryTrashService.getResoourceTrash(rid);
        ResourceDirectoryTree tree = rd.getDescendants();
        Resource r = resourceService.getResource(tree.getRid());
        String title =folderPathService.getResourceName(tid, parentRid, LynxConstants.TYPE_FOLDER, r.getTitle());
        Set<Integer> rids = new HashSet<Integer>();
        recoverPath(rids, tree, parentRid, tid);
        resourceService.updateResourceStatus(rids,LynxConstants.STATUS_AVAILABLE, tid);
        Resource resource = resourceService.getResource(rid);
        resource.setBid(parentRid);
        resourceDirectoryTrashService.deleteResourceTrash(rid);
        if(!title.equals(resource.getTitle())){
            resource.setTitle(title);
        }
        resourceService.update(resource);
        updateResourceTime(parentRid, uid);
        return 0;
    }

    @Override
    public String getSerialFileName(Integer tid, Integer parentRid,String fileName){
        int i = 1;
        List<Resource> resList = null;
        int pointPos = fileName.lastIndexOf('.');
        String ext = "";
        String originalName = fileName;
        if(pointPos != -1){
            originalName = fileName.substring(0, pointPos);
            ext = fileName.substring(pointPos);
        }
        do{
            fileName = originalName + "(" + i + ")" + ext;
            i++;
            resList = getFileResourceByTitile(tid, parentRid, fileName);
        }while(resList.size()>0);

        return fileName;
    }

    /**
     * 文件夹在根目录下存在则返回，否则在根目录下创建
     * @param folderName
     * @param tid
     * @param uid
     * @return
     */
    private Resource getFolderAtRoot(String folderName, int tid, String uid){
        Resource r = null;
        List<Resource> rs = folderPathService.getResourceByName(tid, 0, LynxConstants.TYPE_FOLDER, folderName);
        if(rs.size()>0){
            r = rs.get(0);
        }else{
            r = new Resource();
            r.setTid(tid);
            r.setBid(0);
            r.setCreateTime(new Date());
            r.setCreator(uid);
            r.setItemType(LynxConstants.TYPE_FOLDER);
            r.setTitle(folderName);
            r.setLastEditor(uid);
            r.setLastEditorName(aoneUserService.getUserNameByID(uid));
            r.setLastEditTime(new Date());
            r.setStatus(LynxConstants.STATUS_AVAILABLE);
            this.createFolder(r);
        }
        return r;
    }

    private void recoverPath(Set<Integer> rids,ResourceDirectoryTree tree,int parentRid,int tid){
        if(tree!=null){
            folderPathService.create(parentRid, tree.getRid(), tid);
            List<ResourceDirectoryTree> cTree = tree.getChildren();
            rids.add(tree.getRid());
            if(cTree!=null){
                for(ResourceDirectoryTree t : cTree){
                    recoverPath(rids, t, tree.getRid(), tid);
                }
            }
        }
    }

    private void sendDeleteEvent(int tid,Resource r,String uid){
        if(r.isFile()){
            eventDispatcher.sendFileDeleteEvent(tid, r, uid);
        }else if(r.isPage()){
            eventDispatcher.sendPageDeleteEvent(tid, r, uid);
        }else if(r.isFolder()){
            eventDispatcher.sendFolderDeleteEvent(tid,r,uid);
        }
    }

    /**
     * 根据itemType获取DDOC扩展名.
     * @param itemType
     * @return
     */
    private String getDdocExt(String itemType){
        String ext = "";
        if(LynxConstants.TYPE_PAGE.equals(itemType)){
            ext = "."+Resource.DDOC;
        }
        return ext;
    }
}
