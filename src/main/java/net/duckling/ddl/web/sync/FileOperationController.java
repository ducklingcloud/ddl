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
package net.duckling.ddl.web.sync;

import net.duckling.common.util.JSONHelper;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.authority.AuthorityService;
import net.duckling.ddl.service.resource.*;
import net.duckling.ddl.service.sync.Context;
import net.duckling.ddl.service.sync.FileMeta;
import net.duckling.ddl.service.sync.IFileMetaService;
import net.duckling.ddl.service.sync.IRedisLockService;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.web.bean.Result;
import net.duckling.ddl.web.interceptor.access.RequirePermission;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.redisson.core.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/v1/fileops")
@RequirePermission(target = "team", operation = "view")
public class FileOperationController {

	private static final Logger LOG = Logger.getLogger(FileOperationController.class);
	private static final int LOCK_LEASE_TIME = 20;
	private static final int LOCK_WAIT_TIME = 0;
	public static final String DDOC_SUFFIX = ".ddoc";

	/*
	 * 文件列表 读文件信息时需要对父目录加锁
	 */
	@RequestMapping(params = "func=ls", method = RequestMethod.POST)
	@ResponseBody
	public void ls(HttpServletRequest request, HttpServletResponse response, @RequestParam("path") String path) {
		Context context = ContextUtil.retrieveContext(request);
		PathName pathName = new PathName(path);

		List<RLock> lockList = new ArrayList<RLock>();
		StringBuilder pathAppender = new StringBuilder();
		for (int i = 0; i < pathName.getLength(); i++) {
			String name = pathName.getNames().get(i);
			pathAppender.append(PathName.DELIMITER).append(name);
			RLock lock = lockService.getReadWriteLock(context.getTid(), pathAppender.toString()).readLock();
			try {
				if (lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS)) {
					lockList.add(lock);
				} else {
					JsonResponse.locked(response);
					break;
				}
			} catch (InterruptedException e) {
				JsonResponse.error(response);
				break;
			}
		}
		try {
			if (lockList.size() == pathName.getLength()) {
				// 所有路径成功加上读锁
				doLs(response, path, context);
			} else {
				JsonResponse.locked(response);
			}
		} catch (Exception e) {
			LOG.error(String.format("error to ls %s", pathName.getPath()), e);
			JsonResponse.error(response);
		} finally {
			for (int index = lockList.size() - 1; index >= 0; index--) {
				try {
					RLock lock = lockList.get(index);
					lock.unlock();
				} catch (IllegalMonitorStateException ignore) {
					// 在持有的锁超时的时候，试图解锁抛出该异常
				}
			}
		}
	}

	private void doLs(HttpServletResponse response, String path, Context context) {
		Resource res = folderPathService.getResourceByPath(context.getTid(), path);
		if (res == null) {
			JSONHelper.writeJSONObject(response, new Result<String>(Result.CODE_FILE_NOT_FOUND,
					Result.MESSAGE_FILE_NOT_FOUND + " path=" + path));
			return;
		}
		List<FileMeta> list = fileMetaService.list(context.getTid(), res);

		JSONHelper.writeJSONObject(response, new Result<List<FileMeta>>(list));
	}

	/**
	 * 返回所有子孙列表 读文件信息时需要对父目录加锁
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(params = "func=descendants")
	public void descendants(@RequestParam("path") String path, HttpServletRequest request, HttpServletResponse response) {
		Context context = ContextUtil.retrieveContext(request);
		PathName pathName = new PathName(path);

		List<RLock> lockList = new ArrayList<RLock>();
		StringBuilder pathAppender = new StringBuilder();
		for (int i = 0; i < pathName.getLength(); i++) {
			String name = pathName.getNames().get(i);
			pathAppender.append(PathName.DELIMITER).append(name);
			RLock lock = lockService.getReadWriteLock(context.getTid(), pathAppender.toString()).readLock();
			try {
				if (lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS)) {
					lockList.add(lock);
				} else {
					JsonResponse.locked(response);
					break;
				}
			} catch (InterruptedException e) {
				JsonResponse.error(response);
				break;
			}
		}
		try {
			if (lockList.size() == pathName.getLength()) {
				// 所有路径成功加上读锁
				doDescendants(path, response, context);
			} else {
				JsonResponse.locked(response);
			}
		} catch (Exception e) {
			LOG.error(String.format("error to ls %s", pathName.getPath()), e);
			JsonResponse.error(response);
		} finally {
			for (int index = lockList.size() - 1; index >= 0; index--) {
				try {
					RLock lock = lockList.get(index);
					lock.unlock();
				} catch (IllegalMonitorStateException ignore) {
					// 在持有的锁超时的时候，试图解锁抛出该异常
				}
			}
		}
	}

	private void doDescendants(String path, HttpServletResponse response, Context context) {
		Resource parent = folderPathService.getResourceByPath(context.getTid(), path);
		List<FileMeta> list = fileMetaService.getDescendants(context.getTid(), parent.getRid());
		JSONHelper.writeJSONObject(response, new Result<List<FileMeta>>(list));
	}

	/*
	 * 文件详细 读文件信息时需要对父目录加锁
	 */
	@ResponseBody
	@RequestMapping(params = "func=stat", method = RequestMethod.GET)
	public void stat(HttpServletRequest request, HttpServletResponse response, @RequestParam("path") String path,
			@RequestParam(value = "fver", required = false) Long fver) {

		Context context = ContextUtil.retrieveContext(request);
		PathName pathName = new PathName(path);

		List<RLock> lockList = new ArrayList<RLock>();
		StringBuilder pathAppender = new StringBuilder();
		for (int i = 0; i < pathName.getLength(); i++) {
			String name = pathName.getNames().get(i);
			pathAppender.append(PathName.DELIMITER).append(name);
			RLock lock = lockService.getReadWriteLock(context.getTid(), pathAppender.toString()).readLock();
			try {
				if (lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS)) {
					lockList.add(lock);
				} else {
					JsonResponse.locked(response);
					break;
				}
			} catch (InterruptedException e) {
				JsonResponse.error(response);
				break;
			}
		}
		try {
			if (lockList.size() == pathName.getLength()) {
				// 所有路径成功加上读锁
				doStat(response, path, fver, context);
			} else {
				JsonResponse.locked(response);
			}
		} catch (Exception e) {
			LOG.error(String.format("error to ls %s", pathName.getPath()), e);
			JsonResponse.error(response);
		} finally {
			for (int index = lockList.size() - 1; index >= 0; index--) {
				try {
					RLock lock = lockList.get(index);
					lock.unlock();
				} catch (IllegalMonitorStateException ignore) {
					// 在持有的锁超时的时候，试图解锁抛出该异常
				}
			}
		}
	}

	private void doStat(HttpServletResponse response, String path, Long fver, Context context) {
		if (path.endsWith(DDOC_SUFFIX)) {
			path = path.substring(0, path.length() - DDOC_SUFFIX.length());
		}
		Resource res = folderPathService.getResourceByPath(context.getTid(), path);
		if (res == null) {
			JSONHelper.writeJSONObject(response, new Result<String>(Result.CODE_FILE_NOT_FOUND,
					Result.MESSAGE_FILE_NOT_FOUND + " path=" + path));
			return;
		}
		FileMeta meta = fver == null || res.isFolder() ? fileMetaService.get(res) : fileMetaService.get(res, fver);
		JSONHelper.writeJSONObject(response, new Result<FileMeta>(meta));
	}

	/**
	 * @param request
	 * @param response
	 * @param path
	 * @throws InterruptedException
	 */
	@RequirePermission(target = "team", operation = "edit")
	@ResponseBody
	@RequestMapping(params = "func=rm", method = RequestMethod.POST)
	public void rm(HttpServletRequest request, HttpServletResponse response, @RequestParam("path") String path)
			throws InterruptedException {
		Context context = ContextUtil.retrieveContext(request);

		// ddoc文件在resource表里没有存后缀，所以在这里把后缀去掉
		if (path.endsWith(DDOC_SUFFIX)) {
			path = path.substring(0, path.length() - ".ddoc".length());
		}

		PathName pathName = new PathName(path);

		List<RLock> lockList = new ArrayList<RLock>();
		StringBuilder pathAppender = new StringBuilder();
		for (int i = 0; i < pathName.getLength(); i++) {
			String name = pathName.getNames().get(i);
			pathAppender.append(PathName.DELIMITER).append(name);
			try {
				RLock lock = null;
				if (i == pathName.getNames().size() - 1) {
					lock = lockService.getReadWriteLock(context.getTid(), pathAppender.toString()).writeLock();
					if (lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS)) {
						lockList.add(lock);
					} else {
						JsonResponse.locked(response);
						break;
					}
				} else {
					lock = lockService.getReadWriteLock(context.getTid(), pathAppender.toString()).readLock();
					if (lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS)) {
						lockList.add(lock);
					} else {
						JsonResponse.locked(response);
						break;
					}
				}

			} catch (InterruptedException e) {
				JsonResponse.error(response);
				break;
			}
		}
		try {
			if (lockList.size() == pathName.getLength()) {
				doRm(request, response, context, pathName);
			} else {
				JsonResponse.locked(response);
			}
		} catch (Exception e) {
			LOG.error(String.format("error to ls %s", pathName.getPath()), e);
			JsonResponse.error(response);
		} finally {
			for (int index = lockList.size() - 1; index >= 0; index--) {
				try {
					RLock lock = lockList.get(index);
					lock.unlock();
				} catch (IllegalMonitorStateException ignore) {
					// 在持有的锁超时的时候，试图解锁抛出该异常
				}
			}
		}
	}

	/*
	 * 创建文件夹 对每级父目录加读锁，对要创建的文件夹加写锁
	 */
	@RequirePermission(target = "team", operation = "edit")
	@ResponseBody
	@RequestMapping(params = "func=mkdir", method = RequestMethod.POST)
	public void mkdir(HttpServletRequest request, HttpServletResponse response, @RequestParam("path") String path) {
		Context context = ContextUtil.retrieveContext(request);
		PathName pathName = new PathName(path);

		List<RLock> lockList = new ArrayList<RLock>();
		StringBuilder pathAppender = new StringBuilder();
		for (int i = 0; i < pathName.getLength(); i++) {
			String name = pathName.getNames().get(i);
			pathAppender.append(PathName.DELIMITER).append(name);
			try {
				RLock lock = null;
				if (i == pathName.getNames().size() - 1) {
					lock = lockService.getReadWriteLock(context.getTid(), pathAppender.toString()).writeLock();
					if (lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS)) {
						lockList.add(lock);
					} else {
						break;
					}
				} else {
					lock = lockService.getReadWriteLock(context.getTid(), pathAppender.toString()).readLock();
					if (lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS)) {
						lockList.add(lock);
					} else {
						break;
					}
				}

			} catch (InterruptedException e) {
				LOG.error(
						String.format("InterruptedException while aquiring lock fo %d:%s", context.getTid(),
								pathName.getPath()), e);
				break;
			}
		}
		try {
			if (lockList.size() == pathName.getLength()) {
				long startTime = System.currentTimeMillis();
				doMkdir(response, context, pathName);
				long completeTime = System.currentTimeMillis();
				LOG.info(String.format("mkdir takes %dms", completeTime - startTime));
			} else {
				JsonResponse.locked(response);
				LOG.error(String.format("Fail to aquire lock for %d:%s ", context.getTid(), pathName.getPath()));
			}
		} catch (Exception e) {
			LOG.error(String.format("error to ls %s", pathName.getPath()), e);
			JsonResponse.error(response);
		} finally {
			for (int index = lockList.size() - 1; index >= 0; index--) {
				try {
					RLock lock = lockList.get(index);
					lock.unlock();
				} catch (IllegalMonitorStateException ignore) {
					// 在持有的锁超时的时候，试图解锁抛出该异常
				}

			}
		}
	}

	private void doMkdir(HttpServletResponse response, Context context, PathName pn) {
		int parentRid = 0; // 默认根目录
		if (pn.getLength() > 1) {
			Resource parent = folderPathService.getResourceByPath(context.getTid(), pn.getContextPath());
			if (parent == null) {
				// 父目录不存在
				JSONHelper.writeJSONObject(response, new Result<String>(Result.CODE_FILE_NOT_FOUND,
						Result.MESSAGE_FILE_NOT_FOUND + " path=" + pn.getContextPath()));
				return;
			}
			parentRid = parent.getRid();
		}

		Resource r = null;
		List<Resource> rs = folderPathService.getResourceByName(context.getTid(), parentRid, LynxConstants.TYPE_FOLDER,
				pn.getName());
		if (rs.size() == 1) {
			JSONHelper.writeJSONObject(response, new Result<String>(Result.CODE_FILE_EXISTED,
					Result.MESSAGE_FILE_EXISTED + " path=" + pn.getPath()));
			return;
		} else if (rs.size() > 1) {
			JSONHelper.writeJSONObject(response, new Result<String>(Result.CODE_FILE_EXISTED,
					Result.MESSAGE_FILE_EXISTED + " path=" + pn.getPath()));
			LOG.warn(String.format("There is more than one %s under %s", pn.getName(), pn.getContextPath()));
			return;
		} else {
			Date createDate = new Date();
			r = new Resource();
			r.setTid(context.getTid());
			r.setBid(parentRid);
			r.setCreateTime(createDate);
			r.setCreator(context.getUid());
			r.setItemType(LynxConstants.TYPE_FOLDER);
			r.setTitle(pn.getName());
			r.setLastEditor(context.getUid());
			r.setLastEditorName(aoneUserService.getUserNameByID(context.getUid()));
			r.setLastEditTime(createDate);
			r.setStatus(LynxConstants.STATUS_AVAILABLE);

			int rid = resourceOperateService.createFolder(r, context.getDevice());
			r.setRid(rid);

			FileMeta meta = fileMetaService.get(r);
			meta.setUploadDevice(context.getDevice());
			JsonResponse.fileMeta(response, meta);
		}
	}

	private void doRm(HttpServletRequest request, HttpServletResponse response, Context context, PathName pn) {
		Resource resource = folderPathService.getResourceByPath(context.getTid(), pn.getPath());

		if (resource == null) {
			JsonResponse.notFound(response);
			return;
		}

		if (!validateDeletePermission(request, resource)) {
			JsonResponse.forbidden(response);
			return;
		}

		resourceOperateService.deleteResource(context.getTid(), resource.getRid(), context.getUid(),
				context.getDevice());
		Map<String, Object> wrap = new HashMap<String, Object>();
		JSONHelper.writeJSONObject(response, new Result<Map<String, Object>>(wrap));
	}

	/**
	 * 仅有重名名功能
	 * 
	 * @param request
	 * @param response
	 * @param fromPath
	 * @param toPath
	 */
	@RequirePermission(target = "team", operation = "edit")
	@ResponseBody
	@RequestMapping(params = "func=mv", method = RequestMethod.POST)
	public void mv(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("from_path") String fromPath, @RequestParam("to_path") String toPath) {
		Context context = ContextUtil.retrieveContext(request);
		PathName fromPathName = new PathName(fromPath);
		PathName toPathName = new PathName(toPath);

		List<RLock> lockList = new ArrayList<RLock>();
		StringBuilder pathAppender = new StringBuilder();
		for (int i = 0; i < fromPathName.getLength(); i++) {
			String name = fromPathName.getNames().get(i);
			pathAppender.append(PathName.DELIMITER).append(name);
			try {
				RLock lock = null;
				if (i == fromPathName.getNames().size() - 1) {
					lock = lockService.getReadWriteLock(context.getTid(), pathAppender.toString()).writeLock();
					if (lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS)) {
						lockList.add(lock);
					} else {
						JsonResponse.locked(response);
						break;
					}
				} else {
					lock = lockService.getReadWriteLock(context.getTid(), pathAppender.toString()).readLock();
					if (lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS)) {
						lockList.add(lock);
					} else {
						JsonResponse.locked(response);
						break;
					}
				}

			} catch (InterruptedException e) {
				JsonResponse.error(response);
				break;
			}
		}

		for (int i = 0; i < toPathName.getLength(); i++) {
			String name = toPathName.getNames().get(i);
			pathAppender.append(PathName.DELIMITER).append(name);
			try {
				RLock lock = null;
				if (i == toPathName.getNames().size() - 1) {
					lock = lockService.getReadWriteLock(context.getTid(), pathAppender.toString()).writeLock();
					if (lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS)) {
						lockList.add(lock);
					} else {
						JsonResponse.locked(response);
						break;
					}
				} else {
					lock = lockService.getReadWriteLock(context.getTid(), pathAppender.toString()).readLock();
					if (lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS)) {
						lockList.add(lock);
					} else {
						JsonResponse.locked(response);
						break;
					}
				}

			} catch (InterruptedException e) {
				JsonResponse.error(response);
				break;
			}
		}

		try {
			if (lockList.size() == fromPathName.getLength() + toPathName.getLength()) {
				doMv(request, response, context, fromPathName, toPathName);
			} else {
				JsonResponse.locked(response);
			}
		} catch (Exception e) {
			LOG.error(String.format("error to mv %s", fromPathName.getPath()), e);
			JsonResponse.error(response);
		} finally {
			for (int index = lockList.size() - 1; index >= 0; index--) {
				try {
					RLock lock = lockList.get(index);
					lock.unlock();
				} catch (IllegalMonitorStateException ignore) {
					// 在持有的锁超时的时候，试图解锁抛出该异常
				}
			}
		}
	}

	private void doMv(HttpServletRequest request, HttpServletResponse response, Context context, final PathName fromPath,
					  final PathName toPath) {
		if (!fromPath.getContextPath().equals(toPath.getContextPath()) || fromPath.getLength() == 0
				|| fromPath.getPath().equals(toPath.getPath())) {
			JsonResponse.paraError(response);
			return;
		}

		Resource source;
		if (fromPath.getName().endsWith(DDOC_SUFFIX)) {
			PathName ddocPath = new PathName(fromPath.getPath().substring(0, fromPath.getPath().length() - DDOC_SUFFIX.length()));
			source = folderPathService.getResourceByPath(context.getTid(), ddocPath);
		} else {
			source = folderPathService.getResourceByPath(context.getTid(), fromPath);
		}

		if (source == null) {
			JsonResponse.notFound(response);
			return;
		}

		String uid = VWBSession.getCurrentUid(request);
		String newTitle;
		if (source.isPage()) {
			if (fromPath.getName().endsWith(DDOC_SUFFIX) && toPath.getName().endsWith(DDOC_SUFFIX)) {
				newTitle = toPath.getName().substring(0, toPath.getName().length() - DDOC_SUFFIX.length());
			} else {
				JsonResponse.paraError(response);
				return;
			}
		} else {
			// 非ddoc文件重命名为.ddoc后缀的文件，创建新的空白ddoc，原来的文件保留。
			if (toPath.getName().endsWith(DDOC_SUFFIX)) {
				Context ctx = ContextUtil.retrieveContext(request);
				String device = ctx.getDevice();
				int tid = ctx.getTid();

				Resource parentResource = folderPathService.getResourceByPath(tid, toPath.getContextPath());
				if (parentResource == null) {
					JsonResponse.notFound(response);
					return;
				}
				String title = toPath.getName().substring(0, toPath.getName().length() - DDOC_SUFFIX.length());
				List<Resource> resources = resourceService.getResourceByTitle(tid, parentResource.getRid(),
						LynxConstants.TYPE_PAGE, title);
				Resource resource = CollectionUtils.isEmpty(resources) ? null : resources.get(0);
				if (resource != null) {
					JsonResponse.sameFileExisted(response);
					return;
				}
				int rid = resourceOperateService.createNewPage(parentResource.getRid(), title, tid, uid);
				FileMeta meta = fileMetaService.get(tid, Long.valueOf(rid));
				meta.setUploadDevice(device);
				JsonResponse.fileMeta(response, meta);
				return;
			}
			newTitle = toPath.getName();
		}

		boolean renameSuccess;
		renameSuccess = resourceOperateService.renameResource(context.getTid(), source.getRid(), uid, newTitle, context.getDevice());
		if (!renameSuccess) {
			JSONHelper.writeJSONObject(response,
					new Result<String>(Result.CODE_ERROR, "rename error. path=" + fromPath));
			return;
		}
		source.setTitle(newTitle);
		FileMeta meta = fileMetaService.get(source);
		meta.setUploadDevice(context.getDevice());
		Map<String, Object> wrap = new HashMap<String, Object>();
		wrap.put("fileMeta", meta);
		JSONHelper.writeJSONObject(response, new Result<Map<String, Object>>(wrap));
	}

	private boolean validateDeletePermission(HttpServletRequest request, Resource resource) {
		VWBContext vwbContext = VWBContext.createContext(request, UrlPatterns.DELETE);
		String u = vwbContext.getCurrentUID();
		if (authorityService.teamAccessability(VWBContext.getCurrentTid(),
				VWBSession.findSession(vwbContext.getHttpRequest()), AuthorityService.ADMIN)) {
			return true;
		} else {
			if (resource != null && u.equals(resource.getCreator())) {
				return true;
			}
		}
		return false;
	}

	/*
	 * 测试加锁的时间消耗
	 */
	@RequirePermission(target = "team", operation = "edit")
	@ResponseBody
	@RequestMapping(params = "func=testLockCost", method = RequestMethod.POST)
	public void testLockCost(HttpServletRequest request, HttpServletResponse response, @RequestParam("path") String path) {
		Context context = ContextUtil.retrieveContext(request);
		PathName pathName = new PathName(path);

		long startTime = System.currentTimeMillis();

		List<RLock> lockList = new ArrayList<RLock>();
		StringBuilder pathAppender = new StringBuilder();
		for (int i = 0; i < pathName.getLength(); i++) {
			String name = pathName.getNames().get(i);
			pathAppender.append(PathName.DELIMITER).append(name);
			try {
				RLock lock = null;
				if (i == pathName.getNames().size() - 1) {
					lock = lockService.getReadWriteLock(context.getTid(), pathAppender.toString()).writeLock();
					if (lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS)) {
						lockList.add(lock);
					} else {
						JsonResponse.locked(response);
						break;
					}
				} else {
					lock = lockService.getReadWriteLock(context.getTid(), pathAppender.toString()).readLock();
					if (lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS)) {
						lockList.add(lock);
					} else {
						JsonResponse.locked(response);
						break;
					}
				}

			} catch (InterruptedException e) {
				JsonResponse.error(response);
				break;
			}
		}
		try {
			if (lockList.size() == pathName.getLength()) {
				// Do nothing...
			} else {
				JsonResponse.locked(response);
			}
		} catch (Exception e) {
			LOG.error(String.format("error to ls %s", pathName.getPath()), e);
			JsonResponse.error(response);
		} finally {
			for (int index = lockList.size() - 1; index >= 0; index--) {
				try {
					RLock lock = lockList.get(index);
					lock.unlock();
				} catch (IllegalMonitorStateException ignore) {
					// 在持有的锁超时的时候，试图解锁抛出该异常
				}

			}
		}

		long completeTime = System.currentTimeMillis();
		LOG.info(String.format("%dms to lock and unlock %s", completeTime - startTime, path));
	}

	@Autowired
	private IRedisLockService lockService;
	@Autowired
	private IFileMetaService fileMetaService;
	@Autowired
	private IResourceService resourceService;
	@Autowired
	private ResourceOperateService resourceOperateService;
	@Autowired
	private FolderPathService folderPathService;
	@Autowired
	private AoneUserService aoneUserService;
	@Autowired
	private AuthorityService authorityService;

	@PreDestroy
	public void destory() {
	}

}
