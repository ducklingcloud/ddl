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

import cn.vlabs.clb.api.ResourceNotFound;
import cn.vlabs.clb.api.document.ChunkResponse;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.exception.NoEnoughSpaceException;
import net.duckling.ddl.exception.ResourceExistedException;
import net.duckling.ddl.service.file.AttSaver;
import net.duckling.ddl.service.file.FileStorage;
import net.duckling.ddl.service.file.FileVersion;
import net.duckling.ddl.service.file.FileVersionService;
import net.duckling.ddl.service.resource.*;
import net.duckling.ddl.service.sync.*;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.web.bean.NginxAgent;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
@RequirePermission(target = "team", operation = "edit")
@RequestMapping("/v1/file")
public class FileContentController {
	private static final Logger LOG = Logger.getLogger(FileContentController.class);
	private static final String DDL_API_RESULT_HEADER = "DDL-API-Result";
	private static final long _4GB = 1L << 32;
	private static final int LOCK_LEASE_TIME = 20;
	private static final int LOCK_WAIT_TIME = 0;
	private static final byte[] DDOC_FILE_CONTENT = "DDLDDOC001".getBytes(StandardCharsets.US_ASCII);
	private static final String EMPTY_DDOC_CONTENT = "<p>\n \n</p>\n";

	/*
	 * 下载文件 
	 * 1.如果文件元数据不存在，则在DDL-API-Result头中返回文件不存在的json结果，结束处理。
	 * 2.如果以tomcat方式下载文件不成功，返回http状态码404。
	 * 3.如果以nginx方式下载文件不成功，由后端clb返回状态码（可能CLB前面也有nginx，由clb的nginx返回状态码）。
	 * 4.如果请求下载ddoc文件，如果存在，由ddl生成文件内容返回客户端。
	 * 
	 * 元数据存在时，没有写DDL-API-Result头的原因是：Nginx代理之后，DDL写的头会传给CLB，但是CLB并不转发该头。
	 */
	@RequirePermission(target = "team", operation = "view")
	@ResponseBody
	@RequestMapping(params = "func=download")
	public void download(HttpServletRequest request, HttpServletResponse response, @RequestParam("fid") Long rid,
						 @RequestParam(value = "fver", required = false) Long fver) {
		Context ctx = ContextUtil.retrieveContext(request);
		int tid = ctx.getTid();

		Resource resource = resourceService.getResource(rid.intValue(), tid);
		if (resource == null || LynxConstants.STATUS_DELETE.equals(resource.getStatus())) {
			Result<Object> result = new Result<Object>(Result.CODE_FILE_NOT_FOUND, Result.MESSAGE_FILE_NOT_FOUND);
			response.setHeader(DDL_API_RESULT_HEADER, result.toString());
			return;
		}

		if (resource.getItemType().equals(LynxConstants.TYPE_PAGE)) {
			OutputStream out = null;
			try {
				out = response.getOutputStream();
				out.write(DDOC_FILE_CONTENT);
				if (rid == 254974) {
					throw new IOException("a test");
				}
				return;
			} catch (IOException e) {
				LOG.error(String.format("Fail to download rid: %d", rid), e);
				return;
			} finally {
				if (null != out) {
					try {
						out.close();
					} catch (IOException ignored) {
					}
				}
			}
		}

		FileVersion file = null;
		file = fver == null ? fileVersionService.getLatestFileVersion(rid.intValue(), tid) : fileVersionService
				.getFileVersion(rid.intValue(), tid, fver.intValue());
		if (file == null || LynxConstants.STATUS_DELETE.equals(file.getStatus())) {
			Result<Object> result = new Result<Object>(Result.CODE_FILE_NOT_FOUND, Result.MESSAGE_FILE_NOT_FOUND);
			response.setHeader(DDL_API_RESULT_HEADER, result.toString());
			LOG.error(String.format("Resource of rid:%d exists, but there is no valid file version.", rid));
			return;
		}

		if (NginxAgent.isNginxMode()) {
			String url = resourceOperateService.getDirectURL(file.getClbId(), String.valueOf(file.getClbVersion()), false);
			NginxAgent.setRedirectUrl(request, response, file.getTitle(), file.getSize(), url);
		} else {
			try {
				AttSaver fs = new AttSaver(response, request, file.getTitle());
				resourceOperateService.getContent(file.getClbId(), String.valueOf(file.getClbVersion()), fs);
			} catch (ResourceNotFound resourceNotFound) {
				try {
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
				} catch (IOException ignored) {
				}
				LOG.error(String.format("Fail to get file %d from CLB.", file.getRid()), resourceNotFound);
			} catch (Exception e) {
				JsonResponse.error(response);
				LOG.error(String.format("Fail to get file %d from CLB.", file.getRid()), e);
			}
		}
	}

	/*
	 * 上传文件
	 */
	@RequestMapping(value = "/single_upload", method = RequestMethod.POST)
	@ResponseBody
	public void single_upload(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("path") String path, @RequestParam("file") MultipartFile file,
			@RequestParam("mode") String mode,
			@RequestParam(value = "fver", required = false, defaultValue = "0") Integer fver,
			@RequestParam("checksum") String checksum, @RequestParam("size") Long size) {
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
				doSingleUpload(request, response, file, pathName, mode, fver);
			} else {
				JsonResponse.locked(response);
			}
		} catch (Exception e) {
			LOG.error(String.format("error to upload %s", pathName.getPath()), e);
			JsonResponse.error(response);
		} finally {
			try {
				for (int index = lockList.size() - 1; index >= 0; index--) {
					RLock lock = lockList.get(index);
					lock.unlock();
				}
			} catch (IllegalMonitorStateException ignored) {
				// 这个异常是因为锁的超时时间到了，导致本线程不再拥有该锁，不再继续处理
			}

		}
	}

	private void doSingleUpload(HttpServletRequest request, HttpServletResponse response, MultipartFile file,
			PathName pathName, String mode, int fver) {
		Context ctx = ContextUtil.retrieveContext(request);
		String device = ctx.getDevice();
		String uid = ctx.getUid();
		int tid = ctx.getTid();
		long size = file.getSize();

		// 检查父文件夹是否存在
		Resource parentResource = folderPathService.getResourceByPath(tid, pathName.getContextPath());
		if (parentResource == null) {
			JsonResponse.notFound(response);
			return;
		}

		if (pathName.getName().endsWith(".ddoc")) {
			String title = pathName.getName().substring(0, pathName.getName().length() - ".ddoc".length());
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

		List<Resource> resources = resourceService.getResourceByTitle(tid, parentResource.getRid(),
				LynxConstants.TYPE_FILE, pathName.getName());
		Resource resource = CollectionUtils.isEmpty(resources) ? null : resources.get(0);
		if (mode.equals("add")) {
			if (resource != null) {
				FileMeta meta = fileMetaService.get(tid, Long.valueOf(resource.getRid()));
				JsonResponse.fileVersionConflict(response, meta);
				return;
			}
		} else if (mode.equals("update")) {
			if (resource != null) {
				int existedFileVersion = resource.getLastVersion();
				if (fver != existedFileVersion) {
					FileMeta meta = fileMetaService.get(tid, Long.valueOf(resource.getRid()));
					JsonResponse.fileVersionConflict(response, meta);
					return;
				}
			}
		} else if (mode.equals("overwrite")) {
			// Do nothing.
		}

		FileVersion fv = null;
		try {
			fv = resourceOperateService.upload(uid, tid, parentResource.getRid(), pathName.getName(), size,
					file.getInputStream(), true, true, false, null, device);
		} catch (IOException e) {
			JsonResponse.error(response);
			LOG.error(e.getMessage());
			return;
		} catch (NoEnoughSpaceException e) {
			JsonResponse.error(response);
		} finally {
			try {
				file.getInputStream().close();
			} catch (IOException ignored) {
			}
		}

		if (fv != null) {
			// 返回文件的元数据信息
			FileMeta meta = fileMetaService.get(tid, Long.valueOf(fv.getRid()));
			meta.setUploadDevice(device);
			JsonResponse.fileMeta(response, meta);
		} else {
			JsonResponse.error(response);
		}

	}

	/**
	 * 返回session_id
	 */
	@RequestMapping(value = "/upload_session/start", method = RequestMethod.POST)
	public void startUploadSession(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("checksum") String checksum, @RequestParam("size") Long size,
			@RequestParam(value = "filename", required = false, defaultValue = "ddl_drive_file") String filename) {
		Context ctx = ContextUtil.retrieveContext(request);
		int tid = ctx.getTid();

		if (size > _4GB) {
			JsonResponse.error(response);
			return;
		}

		// 剩余空间能够存放
		TeamSpaceSize existedSize = teamSpaceSizeService.getTeamSpaceSize(tid);
		long remain = existedSize.getTotal() - existedSize.getUsed();
		if (size > remain) {
			JsonResponse.noEnoughSapce(response);
			return;
		}

		ChunkResponse chunkResponse = fileStorage.prepareChunkUpload(filename, checksum, size);
		Long clbId = (long) chunkResponse.getDocid();
		if (chunkResponse.isExistSameFileContent()) {
			String sessionId = chunkUploadSessionService.create(clbId, ChunkUploadSession.FINISHED);
			JsonResponse.startSession(response, sessionId, chunkResponse.getEmptyChunkSet(),
					chunkResponse.getChunkSize());
			return;
		} else if (chunkResponse.isSccuessStatus()) {
			String sessionId = chunkUploadSessionService.create(clbId);
			JsonResponse.startSession(response, sessionId, chunkResponse.getEmptyChunkSet(),
					chunkResponse.getChunkSize());
			return;
		} else {
			JsonResponse.error(response);
			return;
		}
	}

	@RequestMapping(value = "/upload_session/append", method = RequestMethod.POST)
	public void appendUploadSession(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("session_id") String sessionId, @RequestParam("chunk_index") Integer chunkIndex,
			@RequestParam("chunk_data") MultipartFile chunkData) {
		ChunkUploadSession chunkUploadSession = chunkUploadSessionService.get(sessionId);
		if (chunkUploadSession == null) {
			JsonResponse.chunkUploadSessionNotFound(response, sessionId);
			return;
		}

		ChunkResponse chunkResponse = null;
		try {
			chunkResponse = fileStorage.executeChunkUpload(chunkUploadSession.getClbId().intValue(), chunkIndex,
					chunkData.getBytes(), (int) chunkData.getSize());
		} catch (IOException e) {
			JsonResponse.error(response);
			LOG.error(String.format("Fail to upload chunk %d of clbid %d.", chunkIndex, chunkUploadSession.getClbId()), e);
			return;
		}

		if (chunkResponse.isSccuessStatus()) {
			JsonResponse.ackChunk(response, sessionId, "ack", chunkResponse.getEmptyChunkSet());
		} else if (chunkResponse.isDuplicateChunk()) {
			JsonResponse.ackChunk(response, sessionId, "duplicated", chunkResponse.getEmptyChunkSet());
		} else if (chunkResponse.getStatusCode() == ChunkResponse.CHUNK_INDEX_INVALID) {
			JsonResponse.ackChunk(response, sessionId, "invalid_index", null);
		} else if (chunkResponse.getStatusCode() == ChunkResponse.EXCEED_MAX_CHUNK_SIZE){
			JsonResponse.error(response);
		} else {
			JsonResponse.error(response);
		}
	}

	@RequestMapping(value = "/upload_session/finish", method = RequestMethod.POST)
	public void finishUploadSession(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("session_id") String sessionId, @RequestParam("path") String path,
			@RequestParam("mode") String mode, @RequestParam(value = "fver", required = false) Integer fver) {

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
				doSessionUploadFinish(request, response, sessionId, pathName, mode, fver);
			} else {
				JsonResponse.locked(response);
				LOG.error(String.format("Fail to aquire lock for %d:%s ", context.getTid(), pathName.getPath()));
			}
		} catch (Exception e) {
			LOG.error(String.format("error to finish upload %d:%s", context.getTid(), pathName.getPath()), e);
			JsonResponse.error(response);
		} finally {
			try {
				for (int index = lockList.size() - 1; index >= 0; index--) {
					RLock lock = lockList.get(index);
					lock.unlock();
				}
			} catch (IllegalMonitorStateException ignored) {
				// 这个异常是因为锁的超时时间到了，导致本线程不再拥有该锁，不再继续处理
			}
		}
	}

	private void doSessionUploadFinish(HttpServletRequest request, HttpServletResponse response, String sessionId,
			PathName pathName, String mode, int fver) throws NoEnoughSpaceException {

		VWBContext vwbcontext = VWBContext.createContext(request, UrlPatterns.T_ATTACH);
		String uid = vwbcontext.getCurrentUID();
		Context ctx = ContextUtil.retrieveContext(request);
		int tid = ctx.getTid();
		String device = ctx.getDevice();

		ChunkUploadSession chunkUploadSession = chunkUploadSessionService.get(sessionId);
		if (chunkUploadSession == null) {
			JsonResponse.chunkUploadSessionNotFound(response, sessionId);
			return;
		}

		int clbId = chunkUploadSession.getClbId().intValue();
		if (!chunkUploadSession.getStatus().equals(ChunkUploadSession.FINISHED)) {
			ChunkResponse chunkResponse = fileStorage.finishChunkUpload(clbId);
			if (chunkResponse.getStatusCode() != ChunkResponse.SUCCESS) {
				JsonResponse.paraError(response);
				return;
			}
		}

		String fileName = pathName.getName();
		Resource parentResource = folderPathService.getResourceByPath(tid, pathName.getContextPath());
		if (parentResource == null) {
			JsonResponse.notFound(response);
			return;
		}

		List<Resource> resources = resourceService.getResourceByTitle(tid, parentResource.getRid(),
				LynxConstants.TYPE_FILE, pathName.getName());
		Resource resource = CollectionUtils.isEmpty(resources) ? null : resources.get(0);
		if (resource != null) {
			if (mode.equals("add")) {
				JsonResponse.fileNameConflict(response);
				return;
			} else if (mode.equals("update")) {
				int existedFileVersion = resource.getLastVersion();
				if (fver != existedFileVersion) {
					FileMeta meta = fileMetaService.get(tid, Long.valueOf(resource.getRid()));
					JsonResponse.fileVersionConflict(response, meta);
					return;
				}

				resourceOperateService.updateFileFromClb(resource.getRid(), tid, uid, fileName, clbId, device);

				FileMeta meta = fileMetaService.get(tid, (long) resource.getRid());
				meta.setUploadDevice(device);
				JsonResponse.fileMeta(response, meta);
				return;
			} else if (mode.equals("overwrite")) {
				// Do nothing.
			}
		} else {
			FileVersion fv;
			try {
				fv = resourceOperateService.createFileFromClb(uid, tid, parentResource.getRid(), fileName, clbId, true, device);
				FileMeta meta = fileMetaService.get(tid, (long) fv.getRid());
				meta.setUploadDevice(device);
				JsonResponse.fileMeta(response, meta);
			} catch (ResourceExistedException e) {
				JsonResponse.fileNameConflict(response);
			}
			
		}
	}

	@Autowired
	private IRedisLockService lockService;
	@Autowired
	private ResourceOperateService resourceOperateService;
	@Autowired
	private IResourceService resourceService;
	@Autowired
	private IFileMetaService fileMetaService;
	@Autowired
	private FileVersionService fileVersionService;
	@Autowired
	private FolderPathService folderPathService;
	@Autowired
	private FileStorage fileStorage;
	@Autowired
	private TeamSpaceSizeService teamSpaceSizeService;
	@Autowired
	private ChunkUploadSessionService chunkUploadSessionService;

}
