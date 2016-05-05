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

import java.util.Date;
import java.util.List;

import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.bundle.Bundle;
import net.duckling.ddl.service.bundle.impl.BundleDAO;
import net.duckling.ddl.service.draft.Draft;
import net.duckling.ddl.service.draft.IDraftService;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.PageLock;
import net.duckling.ddl.service.resource.PageRender;
import net.duckling.ddl.service.resource.PageVersion;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.SimpleResource;
import net.duckling.ddl.service.resource.impl.PageLockProvider;
import net.duckling.ddl.service.resource.impl.PageVersionDAO;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.SimpleUser;
import net.duckling.ddl.util.HTMLConvertUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Deprecated
@Service
public class PageServiceImpl {

	private static final Logger LOG = Logger.getLogger(PageServiceImpl.class);
	
	@Autowired
	private PageDAO pageDAO;
	@Autowired
	private PageVersionDAO pageVersionDAO;
	@Autowired
	private BundleDAO bundleDAO;
	
    @Autowired
    private AoneUserService aoneUserService;
    @Autowired
    private IResourceService resourceService;
    @Autowired
    private IDraftService draftService;
	@Autowired
    private PageLockProvider lockProvider;
    
    
	public void setPageDAO(PageDAO pageDAO) {
		this.pageDAO = pageDAO;
	}

	public void setPageVersionDAO(PageVersionDAO pageVersionDAO) {
		this.pageVersionDAO = pageVersionDAO;
	}


	public void updatePage(Page p){
		if (p==null){
			throw new IllegalArgumentException("ViewPort can't be null while update it.");
		}
		Page old = pageDAO.getPage(p.getPid(), p.getTid());
		if (old!=null){
			p.setTitle(HTMLConvertUtil.replaceLtGt(p.getTitle()));
			pageDAO.update(p);
		}
		else{
			LOG.error("Can not find page meta "+p.getId());
		}
	}
	
	public PageVersion createPageVersion(Page meta, String content) {
		PageVersion v = null;//PageHelper.createPageVersion(meta, content);
		pageVersionDAO.create(v);
		meta.setLastVersion(v.getVersion());
		updatePage(meta);
		createResourceRecord(meta);
//		eventDispatcher.sendPageCreateEvent(v.getTid(), meta);
		return v;
	}
	
	
	private void createResourceRecord(Page vp) {
		if(vp!=null) {
			SimpleResource s = resourceService.getSimpleResource(vp.getPid(),LynxConstants.TYPE_PAGE,vp.getTid());
			if(s==null){
				Resource res = null;//ResourceBuilder.build(vp);
				res.setCreatorName(aoneUserService.getUserNameByID(res.getCreator()));
				res.setLastEditorName(aoneUserService.getUserNameByID(res.getLastEditor()));
				resourceService.create(res);
			}else{
				//添加此条件分支是因为创建页面时，如果上传附件或者引用附件会产生Bundle，从而创建一个
				//版本号为0的页面Resource对象，因而缓存中存在，s!=null
				updateResourceRecord(vp);
			}
		}
	}
	
	public void updatePageVersion(Page meta,String content){
//		PageVersion oldEdition = pageVersionDAO.getLatestPageVersion(meta.getPid(), meta.getTid());
//		PageVersion newEdition = PageHelper.createPageVersion(meta, content);
//		if(isTitleChanged(newEdition,oldEdition)){
//		    eventDispatcher.sendPageRenameEvent(newEdition.getTid(),meta,oldEdition.getTitle(),oldEdition.getVersion());
//		}
//		if (isContentChanged(newEdition, oldEdition)) {
//			pageVersionDAO.create(newEdition);
//			meta.setLastVersion(newEdition.getVersion());
//			eventDispatcher.sendPageModifyEvent(newEdition.getTid(), meta);
//		}
//		updatePage(meta);
//		updateResourceRecord(meta);
	}
	
	private void updateResourceRecord(Page vp) {
		if(vp!=null) {
			Resource oldRes = resourceService.getResource(vp.getPid(), vp.getTid());
			Resource res = null;//ResourceBuilder.build(vp,oldRes);
			res.setLastEditorName(aoneUserService.getUserNameByID(res.getLastEditor()));
			resourceService.update(res);
			updateBundleHasThisResource(oldRes.getRid());
		}
	}
	
    private void updateBundleHasThisResource(int rid) {
        Resource item = resourceService.getResource(rid);
        if (null != item && item.getBid() != 0) {
            int bid = item.getBid();
            int tid = item.getTid();
            // 更新Bundle信息
            Bundle bundle = bundleDAO.getBundle(bid, tid);
            bundle.setLastEditor(item.getLastEditor());
            bundle.setLastEditTime(item.getLastEditTime());
            bundleDAO.update(bid, tid, bundle);
            // 更新Resource信息
            Resource bundleRes = resourceService.getResource(bid, tid);
            bundleRes.setLastEditor(item.getLastEditor());
            bundleRes.setLastEditorName(item.getLastEditorName());
            bundleRes.setLastEditTime(item.getLastEditTime());
            resourceService.update(bundleRes);
            resourceService.updateBundleFileType(bid, tid);
        }
    }
	
	private boolean isTitleChanged(PageVersion newVersion,PageVersion oldVersion){
		return oldVersion.getTitle().hashCode() != newVersion.getTitle().hashCode();
	}
	
	private boolean isContentChanged(PageVersion newVersion,PageVersion oldVersion){
	    return oldVersion.getContent().hashCode() != newVersion.getContent().hashCode();
	}

	
	public int delete(int pid, int tid){
		pageVersionDAO.deleteAllPageVersion(pid, tid);
		resourceService.delete(pid, tid, LynxConstants.TYPE_PAGE);
		return pageDAO.delete(pid, tid);
	}
	
	public int recoverPage(int pid, int tid) {
		Resource resource = resourceService.getResource(pid, tid);
		resource.setStatus(LynxConstants.STATUS_AVAILABLE);
		resourceService.update(resource);
		pageVersionDAO.recoverPafeVersion(pid, tid);
		Page page = pageDAO.getPage(pid, tid);
		page.setStatus(LynxConstants.STATUS_AVAILABLE);
		return pageDAO.update(page);
	}

	public int batchDelete(int tid, List<Integer> pids){
		if(null == pids || pids.isEmpty()){
			return 0;
		}
		return pageDAO.batchDelete(tid, pids);
	}
	
	public Page getPage(int pid, int tid){
		return pageDAO.getPage(pid, tid);
	}
	public List<Page> getPage(List<Integer> pids, int tid){
		return pageDAO.getPage(pids, tid);
	}
	public PageVersion getPageVersion(int pid, int tid, int version) {
		if(version==LynxConstants.INITIAL_VERSION){
			return null;
		}
		return pageVersionDAO.getPageVersion(pid, version);
	}
	
	public PageVersion getLatestPageVersion(int pid, int tid){
//		return pageVersionDAO.getLatestPageVersion(pid, tid);
		return null;
	}

	public List<PageVersion> getAllPageVersion(int pid, int tid) {
//		return pageVersionDAO.getAllPageVersionByTIDPID(tid, pid);
		return null;
	}

	public Page createPage(int tid, String title, String uid) {
		String creatorName = aoneUserService.getUserNameByID(uid);
		Page newPage = null;//PageHelper.createNewPage(tid, title, uid, creatorName);
		int pid = pageDAO.create(newPage);
		newPage.setPid(pid);
		createResourceRecord(newPage);
		return newPage;
	}
	
	public Page createPageAndVersion(int tid, String uid, String title, String content){
		Page page = createPage(tid, title, uid);
		createPageVersion(page, content);
		return page;
	}
	

	public PageRender getPageRender(int tid, int pid) {
		Page meta = pageDAO.getPage(pid, tid);
		PageVersion detail = new PageVersion();
		if(meta.getLastVersion()!=LynxConstants.INITIAL_VERSION){
			detail = pageVersionDAO.getLatestPageVersion(pid);
		}
//		return new PageRender(meta,detail);
		return null;
	}
	
	public PageRender getPageRender(int tid, int pid, int version) {
		Page meta = pageDAO.getPage(pid, tid);
		PageVersion detail = new PageVersion();
		if(meta.getLastVersion()!=LynxConstants.INITIAL_VERSION){
			detail = pageVersionDAO.getPageVersion(pid,version);
			meta.setLastEditor(detail.getEditor());
			meta.setLastEditTime(detail.getEditTime());
			meta.setTitle(detail.getTitle());
			meta.setLastVersion(version);
		}
//		return new PageRender(meta,detail);
		return null;
	}
	
	
	/*-----------Page Lock Service-----------------*/
	public PageLock getCurrentLock(int tid, int pageid){
		return lockProvider.getCurrentLock(tid,pageid);
	}

	public void unlockPage(int tid,int pid,String uid){
		lockProvider.unlockPage(tid,pid,uid);
	}

	public void updateLockTime(int tid,int pageid) {
		lockProvider.updateLockTime(tid,pageid);
	}

	public long getLeftTimeOfPageLock(int tid,int pid) {
		return lockProvider.getLeftTimeOfPageLock(tid,pid);
	}

	public boolean isLockTimeOut(int tid,int pid) {
		PageLock lock = lockProvider.getCurrentLock(tid,pid);
		if(lock!=null){
			return lockProvider.isTimeOut(lock.getLastAccess(), new Date());
		}
		return false;
	}

	public PageLock lockPage(int tid,int pid,String uid,int version){
		return lockProvider.lockPage(tid, pid, uid,version);
	}
	
	/*---------Page Lock Service---------*/

	public void publishManualSaveDraft(int version, Draft d) {
		SimpleUser su = aoneUserService.getSimpleUserByUid(d.getUid());
		Page meta = null;// PageHelper.buildPageMeta(d.getPid(), d.getTid(), getTitle(d.getTitle()), d.getUid(), su.getName(), version);
		
        if (version == 0) {
            createPageVersion(meta, d.getContent());
        } else {
            updatePageVersion(meta, d.getContent());
        }
		
        draftService.clearAutoSaveDraft(d.getTid(), d.getRid(), d.getUid());
        draftService.clearManualSaveDraft(d.getTid(), d.getRid(), d.getUid());
	}
	
	private String getTitle(String title){
		if(StringUtils.isEmpty(title)){
			return "未命名页面";
		}else{
			return title;
		}
	}
	public List<PageContentRender> fetchPageContentListByPageIncrementId(List<Long> pageIds) {
		return pageDAO.fetchDPageContentByIncrementId(pageIds);
	}

	public List<Page> fetchDPageBasicListByPageIncrementId(List<Long> sphinxIds) {
		return pageDAO.fetchDPageBasicListByPageIncrementId(sphinxIds);
	}

	public List<PageVersion> getVersions(int pid, int tid, int offset, int size) {
		return pageVersionDAO.getVersions(pid,tid,offset,size);
	}

	public List<Page> getRecentCreatePages(int tid,String uid, int offset, int size) {
		return pageDAO.getRecentUserCreatePages(tid,uid,offset,size);
	}

	public List<Page> getRecentEditPages(int tid, String uid, int offset, int size) {
		return pageDAO.getRecentUserEditPages(tid, uid, offset, size);
	}

	public List<Page> searchResourceByTitle(int tid, String title) {
		return pageDAO.searchResourceByTitle(tid,title);
	}

	public void recoverPageVersion(int tid, int pid,int recoverVersion) {
		PageVersion pv = getPageVersion(pid, tid, recoverVersion);
		Page page = getPage(pid, tid);
		if(pv==null){
			
		}else{
			updatePageVersion(page, pv.getContent());
		}
	}

	
	
}
