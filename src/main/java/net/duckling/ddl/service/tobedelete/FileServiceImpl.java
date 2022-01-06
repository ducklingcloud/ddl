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
package net.duckling.ddl.service.tobedelete;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.bundle.IBundleService;
import net.duckling.ddl.service.devent.EventDispatcher;
import net.duckling.ddl.service.file.AttSaver;
import net.duckling.ddl.service.file.DFileRef;
import net.duckling.ddl.service.file.DFileSaver;
import net.duckling.ddl.service.file.FileStorage;
import net.duckling.ddl.service.file.FileVersion;
import net.duckling.ddl.service.file.IPictureService;
import net.duckling.ddl.service.file.impl.FileVersionDAO;
import net.duckling.ddl.service.mail.EmailAttachment;
import net.duckling.ddl.service.mail.impl.EmailAttachmentDAO;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.util.ImageUtils;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.vlabs.clb.api.document.MetaInfo;

@Service
@Deprecated
public class FileServiceImpl {

    private static final Logger LOG = Logger.getLogger(FileServiceImpl.class);

    @Autowired
    private IPictureService pictureService;
    @Autowired
    private EventDispatcher dispatcher;
    @Autowired
    private AoneUserService aoneUserService;
    @Autowired
    private IResourceService resourceService;
    @Autowired
    private FileDAO fileDAO;
    @Autowired
    private FileVersionDAO fileVersionDAO;
    @Autowired
    private EmailAttachmentDAO emailAttachmentDAO;

    @Value("${duckling.file.proxy.gateway}")
    private String transpondFlag;

    @Autowired
    private FileStorage storage;

    public int getClbVersion(int fid, int tid, int version) {
        return fileVersionDAO.getFileVersion(fid, tid, version).getClbVersion();
    }

    public void setFileDAO(FileDAO fileDAO) {
        this.fileDAO = fileDAO;
    }

    public void setFileVersionDAO(FileVersionDAO fileVersionDAO) {
        this.fileVersionDAO = fileVersionDAO;
    }

    public void setEmailAttachmentDAO(EmailAttachmentDAO emailAttachmentDAO) {
        this.emailAttachmentDAO = emailAttachmentDAO;
    }

    public int delete(int fid, int tid, IBundleService bundleService) {
        fileVersionDAO.deleteAllFileVersion(fid, tid);
        Resource resource = resourceService.getResource(fid, tid);
        // 若文件属于某个Bundle则删除a1_bundle_item中的关联关系
        if (resource.getBid() != 0) {
            bundleService.removeBundleItems(resource.getBid(), tid,
                                            new int[] { resource.getRid() });
        }
        resourceService.delete(fid, tid, LynxConstants.TYPE_FILE);
        return fileDAO.delete(fid, tid);
    }

    public int recoverFile(int fid, int tid) {
        Resource resource = resourceService.getResource(fid, tid);
        resource.setStatus(LynxConstants.STATUS_AVAILABLE);
        resourceService.update(resource);
        fileVersionDAO.recoverFileVersion(fid, tid);
        File file = fileDAO.getFile(fid, tid);
        file.setStatus(LynxConstants.STATUS_AVAILABLE);
        return update(file);
    }

    public int batchDelete(int tid, List<Integer> fids) {
        if (null == fids || fids.isEmpty()) {
            return 0;
        }
        return fileDAO.batchDelete(tid, fids);
    }

    public File getFile(int fid, int tid) {
        return fileDAO.getFile(fid, tid);
    }


    private static FileVersion createFileVersion(File file,long size){
        int version = file.getLastVersion()+1;
        boolean isInitVer = file.getLastVersion() == LynxConstants.INITIAL_VERSION;
        Date date = isInitVer?file.getCreateTime():(new Date());
        FileVersion fileVersion = new FileVersion();
        fileVersion.setClbVersion(version);
        fileVersion.setTid(file.getTid());
        fileVersion.setVersion(version);
        fileVersion.setClbId(file.getClbId());
        fileVersion.setSize(size);
        fileVersion.setTitle(file.getTitle());
        fileVersion.setEditor(file.getLastEditor());
        fileVersion.setEditTime(date);
        return fileVersion;
    }
    private FileVersion createNewFileVersion(File f, long size) {
        FileVersion v = createFileVersion(f, size);
        int fvid = fileVersionDAO.create(v);
        v.setId(fvid);
        return v;
    }
    private static File createNewFile(int tid, int clbId, String creator, String title){
        java.sql.Date curDate = new java.sql.Date(new Date().getTime());
        File file = new File();
        file.setClbId(clbId);
        file.setStatus(LynxConstants.STATUS_AVAILABLE);
        file.setTid(tid);
        file.setTitle(title);
        file.setCreateTime(curDate);
        file.setCreator(creator);
        file.setLastEditor(creator);
        file.setLastEditTime(curDate);
        file.setLastVersion(LynxConstants.INITIAL_VERSION);
        file.setClbVersion(LynxConstants.INITIAL_VERSION);
        return file;
    }
    private File createNewFile(int tid, String creator, int clbId, String title) {
        File file = createNewFile(tid, clbId, creator, title);
        int fid = fileDAO.create(file);
        file.setId(fid);
        return file;
    }

    @SuppressWarnings("unused")
    private String getFileType(String title) {
        String fileType;
        if (null == title || "".equals(title) || !title.contains(".")) {
            fileType = null;
        } else {
            fileType = title.substring(title.lastIndexOf('.') + 1,
                                       title.length());
        }
        return fileType;
    }

    public FileVersion upload(String uid, Integer tid, String filename,
                              long size, InputStream in) {
        boolean isPicture = ImageUtils.isPicture(filename);
        int clbId = -1;
        FileVersion fv = null;
        if (isPicture) {
            String tmpFilePath = ImageUtils.saveAsFile(in);
            try {
                clbId = storage.createFile(filename, size, new FileInputStream(
                    new java.io.File(tmpFilePath)));
                fv = createFileAndFileVersion(tid, uid, clbId, filename, size);
                pictureService.addPictrue(fv.getClbId(), fv.getClbVersion(),
                                          tmpFilePath);
            } catch (FileNotFoundException e) {
                LOG.error(e.getMessage(), e);
            }
        } else {
            clbId = storage.createFile(filename, size, in);
            fv = createFileAndFileVersion(tid, uid, clbId, filename, size);
        }
        dispatcher.sendFileUploadEvent(filename, fv.getRid(), uid, tid);
        return fv;
    }
    private static FileVersion createFileVersion(File file, int clbId, long size, String title, String editor,
                                                 int version, boolean isInitVer){
        Date date = isInitVer?file.getCreateTime():(new Date());
        FileVersion fileVersion = new FileVersion();
        fileVersion.setTid(file.getTid());
        fileVersion.setClbVersion(version);
        fileVersion.setVersion(file.getLastVersion()+1);
        fileVersion.setClbId(clbId);
        fileVersion.setSize(size);
        fileVersion.setTitle(title);
        fileVersion.setEditor(editor);
        fileVersion.setEditTime(date);
        return fileVersion;
    }
    private static void updateFile(File file, FileVersion fileVersion){
        file.setClbId(fileVersion.getClbId());
        file.setTitle(fileVersion.getTitle());
        file.setLastEditor(fileVersion.getEditor());
        file.setLastEditTime(fileVersion.getEditTime());
        file.setLastVersion(fileVersion.getVersion());
        file.setClbVersion(fileVersion.getClbVersion());
    }
    private Resource build(File f) {
        String title = f.getTitle();
        Resource res = new Resource();
        res.setCreateTime(f.getCreateTime());
        res.setCreator(f.getCreator());
        res.setLastEditor(f.getLastEditor());
        res.setLastEditTime(f.getLastEditTime());
        res.setLastVersion(f.getLastVersion());
        res.setTitle(f.getTitle());
        res.setTid(f.getTid());
        res.setItemType(LynxConstants.TYPE_FILE);
        res.setFileType(title.substring(title.lastIndexOf('.')+1, title.length()));
        res.setMarkedUserSet(new HashSet<String>());
        res.setBid(LynxConstants.DEFAULT_BID);
        res.setOrderTitle(title);
        res.setOrderDate(f.getLastEditTime());
        return res;
    }
    private Resource build(File f, Resource res){
        Resource resource = build(f);
        resource.setMarkedUserSet(res.getMarkedUserSet());
        resource.setTagMap(res.getTagMap());
        resource.setBid(res.getBid());
        return resource;
    }
    public FileVersion updateCLBFile(int fid, int tid, String uid,
                                     String filename, long size, InputStream in) {
        File file = fileDAO.getFile(fid, tid);
        int docid = file.getClbId();
        int version = -1;
        if (ImageUtils.isPicture(filename)) {
            String tmpFilePath = ImageUtils.saveAsFile(in);
            try {
                version = storage.updateFile(docid, filename, size,
                                             new FileInputStream(tmpFilePath));
            } catch (FileNotFoundException e) {
                LOG.error(e.getMessage(), e);
            }
            pictureService.addPictrue(docid, version, tmpFilePath);
        } else {
            version = storage.updateFile(docid, filename, size, in);
        }
        FileVersion fileVersion = createFileVersion(file, docid,
                                                    size, filename, uid, version, false);
        updateFile(file, fileVersion);
        fileDAO.update(fid, tid, file);
        fileVersionDAO.create(fileVersion);
        Resource original = resourceService.getResource(fid, tid);
        Resource res = build(file, original);
        res.setLastEditorName(aoneUserService.getUserNameByID(res
                                                              .getLastEditor()));
        res.setFileType(filename.substring(filename.lastIndexOf('.') + 1,
                                           filename.length()));
        resourceService.update(res);
        fileVersion.setRid(original.getRid());
        dispatcher.sendFileModifyEvent(filename, file.getFid(), uid, version,
                                       tid);
        return fileVersion;
    }



    public String queryPdfStatus(int clbId, String version) {
        return storage.queryPdfStatus(clbId, version);
    }

    public void getPdfContent(int docid, String version, DFileSaver fs) {
        storage.getPdfContent(docid, Integer.parseInt(version), fs);
    }

    public void sendPdfTransformEvent(int clbId, String version) {
        storage.sendPdfTransformEvent(clbId, version);
    }

    public void getContent(int docid, String version, DFileSaver fs) {
        storage.getContent(docid, Integer.parseInt(version), fs);
    }

    public void getContent(int docid, AttSaver fs) {
        storage.getContent(docid, -1, fs);
    }

    public List<DFileRef> getDFileReferences(int fid, int tid) {
        return fileDAO.getReferenceOfDFile(fid, tid);
    }

    public void deleteDFileRef(int fid, int tid) {
        fileDAO.deleteDFileReference(fid, tid);
    }

    public void deleteFileAndPageRef(int fid, int pid, int tid) {
        fileDAO.deleteFileAndPageReference(fid, pid, tid);
    }

    public void removePageRefers(int pid, int tid) {
        fileDAO.removePageRefers(pid, tid);
    }

    public List<FileVersion> getFilesOfPage(int pid, int tid) {
        return fileVersionDAO.getDFilesOfPage(pid, tid);
    }



    public FileVersion referExistFileByClbId(int tid, String uid, int clbId,
                                             String filename, long size) {
        return createFileAndFileVersion(tid, uid, clbId, filename, size);
    }

    private FileVersion createFileAndFileVersion(int tid, String uid,
                                                 int clbId, String filename, long size) {
        File f = createNewFile(tid, uid, clbId, filename);
        FileVersion fv = createNewFileVersion(f, size);
        updateFile(f, fv);
        fileDAO.update(f.getFid(), tid, f);
        Resource res = build(f);
        res.setCreatorName(aoneUserService.getUserNameByID(res.getCreator()));
        res.setLastEditorName(aoneUserService.getUserNameByID(res
                                                              .getLastEditor()));
        int rid = resourceService.create(res);
        fv.setRid(rid);
        return fv;
    }

    public List<FileVersion> getLatestFileVersions(int[] fids, int tid) {
        return fileVersionDAO.getLatestFileVersions(fids, tid);
    }

    public int createEmailAttach(EmailAttachment attachment) {
        return emailAttachmentDAO.create(attachment);
    }

    public List<EmailAttachment> getEmailAttachByUidAndTid(String uid, int tid) {
        return emailAttachmentDAO.findByUserAndTid(uid, tid);
    }

    public List<EmailAttachment> getEmailAttachByMid(String mid) {
        return emailAttachmentDAO.findByMid(mid);
    }

    public boolean updateEmailAttach(EmailAttachment attachment) {
        return emailAttachmentDAO.update(attachment);
    }

    public boolean deleteEmailAttach(int id) {
        return emailAttachmentDAO.delete(id);
    }

    public List<File> getFileByEmailMidAndUid(String mid, int tid, String uid) {
        return fileDAO.getFileByEmailMidAndUid(mid, tid, uid);
    }

    public List<File> getFileByTid(int[] tids, int offset, int rows) {
        return fileDAO.getFileByTid(tids, offset, rows);
    }

    public int getTeamFileCount(int[] tids) {
        return fileDAO.getTeamFileCount(tids);
    }

    public List<File> getFileByTidAndTitle(String title, int tid) {
        return fileDAO.getFileByTidAndTitle(title, tid);
    }

    public String getDirectURL(int clbId, String version, boolean isPDF) {
        String url = storage.getDirectURL(clbId, version, isPDF);
        return url.replace("http://", "/" + transpondFlag + "/");
    }


    public int update(File file) {
        return fileDAO.update(file);
    }

    public FileVersion getFileVersionByDocId(int clbId, String clbVersion) {
        return fileVersionDAO.getFileVersionByDocId(clbId, clbVersion);
    }

    public String getImageDirevtURL(int clbId, String version, String type) {
        //      String url = storage.getImageDirevtURL(clbId, version, type);
        //      return url.replace("http://", "/" + transpondFlag + "/");
        return "";
    }

    public void resizeImage(int clbId, String version) {
        storage.resizeImage(clbId, version);
    }

    public void getImageContent(int docid, int version, String type,
                                DFileSaver fs) {
        storage.getImageContent(docid, version, type, fs);
    }
}
