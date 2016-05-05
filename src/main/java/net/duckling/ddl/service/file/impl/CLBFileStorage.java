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

package net.duckling.ddl.service.file.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.duckling.ddl.common.DDLFacade;
import net.duckling.ddl.common.DucklingProperties;
import net.duckling.ddl.common.VWBContainerImpl;
import net.duckling.ddl.service.file.DFileSaver;
import net.duckling.ddl.service.file.FileStorage;
import net.duckling.ddl.util.FileTypeUtils;
import net.duckling.ddl.util.MimeType;
import net.duckling.ddl.util.PdfStatus;
import net.duckling.ddl.web.bean.ClbUrlTypeBean;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import cn.vlabs.clb.api.AccessForbidden;
import cn.vlabs.clb.api.CLBConnection;
import cn.vlabs.clb.api.CLBPasswdInfo;
import cn.vlabs.clb.api.CLBServiceFactory;
import cn.vlabs.clb.api.CLBStatus;
import cn.vlabs.clb.api.ResourceNotFound;
import cn.vlabs.clb.api.SupportedFileFormatForOnLineViewer;
import cn.vlabs.clb.api.document.ChunkResponse;
import cn.vlabs.clb.api.document.CreateDocInfo;
import cn.vlabs.clb.api.document.DocMetaInfo;
import cn.vlabs.clb.api.document.DocumentService;
import cn.vlabs.clb.api.document.MetaInfo;
import cn.vlabs.clb.api.document.UpdateInfo;
import cn.vlabs.clb.api.image.IResizeImageService;
import cn.vlabs.clb.api.image.ImageMeta;
import cn.vlabs.clb.api.image.ImageQuery;
import cn.vlabs.clb.api.image.ResizeImage;
import cn.vlabs.clb.api.image.ResizeParam;
import cn.vlabs.clb.api.pdf.IPdfService;
import cn.vlabs.clb.api.pdf.PdfStatusCode;
import cn.vlabs.rest.ServiceContext;
import cn.vlabs.rest.stream.StreamInfo;

/**
 * 这个类负责和CLB通信。
 * 
 * @date 2011-8-10
 * @author xiejj@cnic.cn
 */
public class CLBFileStorage implements FileStorage {
	
	/**
	 * 图片大小类型
	 */
	public static final String IMAGE_TYPE_ORIGINAL = "original";
	public static final String IMAGE_TYPE_LARGE = "large";
	public static final String IMAGE_TYPE_MEDIUM = "medium";
	public static final String IMAGE_TYPE_SMALL = "small";
	//是否强制小图，如果没有小图也不返回原图
	public static final String IMAGE_TYPE_FIXSMALL = "fixSmall"; 
	

	private static final Logger LOG = Logger.getLogger(CLBFileStorage.class);

	private CLBConnection conn;
	private boolean enableDConvert;
	private String transpondFlag;

	public CLBFileStorage(String serverUrl, String clbUserName,
			String clbPassword, String clbVersion, boolean enableDConvert) {
		ServiceContext.setMaxConnection(20, 20);
		CLBPasswdInfo pwd = new CLBPasswdInfo();
		pwd.setUsername(clbUserName);
		pwd.setPassword(clbPassword);
		conn = CLBServiceFactory.getClbConnection(serverUrl, pwd, true,
				clbVersion);
		this.enableDConvert = enableDConvert;
	}
	
	public ChunkResponse prepareChunkUpload(String filename, String md5, long size) {
		DocumentService dService = CLBServiceFactory.getDocumentService(conn);
		return dService.prepareChunkUpload(filename, md5, size);
	}
	
	public ChunkResponse executeChunkUpload(int docid, int chunkedIndex, byte[] buf, int numOfBytes){
		DocumentService dService = CLBServiceFactory.getDocumentService(conn);
		return dService.executeChunkUpload(docid, chunkedIndex, buf, numOfBytes);
	}
	
	public ChunkResponse finishChunkUpload(int docid){
		DocumentService dService = CLBServiceFactory.getDocumentService(conn);
		return dService.finishChunkUpload(docid);
	}

	@Override
	public int createFile(String filename, long length, InputStream in) {
        CreateDocInfo   info = new CreateDocInfo ();
        info.title = filename;
        DocumentService dService = CLBServiceFactory.getDocumentService(conn);
		StreamInfo stream = new StreamInfo();
        stream.setFilename(info.title);
		stream.setLength(length);
		stream.setInputStream(in);
		int docid = dService.createDocument(info, stream).docid;
		if (SupportedFileFormatForOnLineViewer.isSupported(MimeType
                .getSuffix(info.title).toLowerCase())) {
			sendPdfTransformEvent(docid, "1");
		}
        if(FileTypeUtils.isClbDealImage(info.title)){
			resizeImage(docid,"1");
		}
		return docid;
    }

	public int updateFile(int docid, String filename, long length,
			InputStream in) {
		DocumentService dService = CLBServiceFactory.getDocumentService(conn);
		String comment = "";
		StreamInfo stream = new StreamInfo();
		stream.setFilename(filename);
		stream.setLength(length);
		stream.setInputStream(in);

		UpdateInfo ui = dService.update(docid, comment, stream);
		if (SupportedFileFormatForOnLineViewer.isSupported(MimeType
				.getSuffix(filename))) {
			sendPdfTransformEvent(docid, ui.version);
		}
		return Integer.parseInt(ui.version);
	}

	public void getContent(int docid, int version, DFileSaver fs) {
		DocumentService dService = CLBServiceFactory.getDocumentService(conn);
		FileSaverBridge bridge = new FileSaverBridge(fs);
		if (version > 0) {
			dService.getContent(docid, Integer.toString(version), bridge);
		} else {
			dService.getContent(docid, bridge);
		}
	}

	public void getPdfContent(int docid, int version, DFileSaver fs) {
		IPdfService pdfService = CLBServiceFactory.getPdfServie(conn);
		FileSaverBridge bridge = new FileSaverBridge(fs);
		pdfService.getPdfContent(docid, Integer.toString(version), bridge);
	}
	
	@Override
	public MetaInfo getMeta(int docid) {
        DocumentService dService = CLBServiceFactory.getDocumentService(conn);
        return dService.getMeta(docid);
    }

	@Override
    public MetaInfo getMeta(int docid, String version) {
        DocumentService ds = CLBServiceFactory.getDocumentService(conn);
        if(StringUtils.isEmpty(version)){
            return ds.getMeta(docid);
        }else{
            return ds.getMeta(docid, version);
        }
    }

	@Override
    public DocMetaInfo getDocMeta(int docid) {
        DocumentService dService = CLBServiceFactory.getDocumentService(conn);
        return dService.getDocMeta(docid);
    }

    @Override
    public DocMetaInfo getDocMeta(int docid, String version) {
        DocumentService dService = CLBServiceFactory.getDocumentService(conn);
        if(StringUtils.isEmpty(version)){
            return dService.getDocMeta(docid);
        }else{
            return dService.getDocMeta(docid, version);
        }
    }

    @Override
	public String queryPdfStatus(int docid, String version) {
		IPdfService dService = CLBServiceFactory.getPdfServie(conn);
		try {
			int status = dService.queryPdfStatus(docid, version);
			switch (status) {
			case PdfStatusCode.NOT_FOUND_SOURCE_FILE:
				return PdfStatus.SOURCE_NOT_FOUND.toString();
			case PdfStatusCode.CONVERT_ONGOING:
				return PdfStatus.CONVERTING.toString();
			case PdfStatusCode.CONVERT_SUCCESS:
				return PdfStatus.SUCCESS.toString();
			case PdfStatusCode.CONVERT_FAILED:
				return PdfStatus.FAIL.toString();
			case PdfStatusCode.UNCONVERT_SOURCE_FILE:
				return PdfStatus.NEED_CONVERT.toString();
			case PdfStatusCode.ENCRYPTED_SOURCE_FILE:
				return PdfStatus.ENCRYPTED_SOURCE_FILE.toString();
			case PdfStatusCode.CORRUPT_SOURCE_FILE:
				return PdfStatus.CORRUPT_SOURCE_FILE.toString();
			case PdfStatusCode.CONVERT_SUCCESS_AND_HAS_MORE:
				return PdfStatus.CONVERT_SUCCESS_AND_HAS_MORE.toString();
			default:
				return PdfStatus.ERROR.toString();
			}
		} catch (ResourceNotFound e) {
			LOG.error("Resource not found while querying pdf status with ("
					+ docid + "," + version + ")");
			return PdfStatus.SOURCE_NOT_FOUND.toString();
		} catch (AccessForbidden e) {
			LOG.error("Access forbidden while querying pdf status with ("
					+ docid + "," + version + ")");
			return PdfStatus.ERROR.toString();
		}
	}

	@Override
	public void sendPdfTransformEvent(int docid, String version) {
		if (enableDConvert) {
			IPdfService dService = CLBServiceFactory.getPdfServie(conn);
			dService.sendPdfConvertEvent(docid, version);
		}
	}

	@Override
	public String getDirectURL(int clbId, String version, boolean isPDF) {
		if (StringUtils.isEmpty(version)) {
			version = "latest";
		}
		if (isPDF) {
			IPdfService a = CLBServiceFactory.getPdfServie(conn);
			return a.getPDFURL(clbId, version);
		} else {
			DocumentService dService = CLBServiceFactory
					.getDocumentService(conn);
			return dService.getContentURL(clbId, version);
		}
	}
	
	@Override
	public ClbUrlTypeBean getImageDirevtURL(int clbId, String version, String type) {
		if (StringUtils.isEmpty(version)) {
			version = "latest";
		}
		ClbUrlTypeBean re = new ClbUrlTypeBean();
		re.setType(type);
		re.setStatus(true);
		IResizeImageService ris = CLBServiceFactory.getResizeImageService(conn);
		ResizeImage rs = ris.getResizeImage(clbId, version);
		String result = null;
		List<ImageMeta> ims = rs.getMetaList();
		//resizeType 的值为small, large, medium
		Map<String,ImageMeta> map = new HashMap<String,ImageMeta>();
		for(ImageMeta i : ims){
			map.put(i.getResizeType(), i);
		}
		if (IMAGE_TYPE_ORIGINAL.equals(type)) {
			result = rs.getOriginalURL();
		} else if (IMAGE_TYPE_LARGE.equals(type)) {
			if(validateImage(map, IMAGE_TYPE_LARGE)){
				result = rs.getLargeURL();
			}
		} else if (IMAGE_TYPE_SMALL.equals(type)) {
			if(validateImage(map, IMAGE_TYPE_SMALL)){
				result = rs.getSmallURL();
			}
		} else if(IMAGE_TYPE_FIXSMALL.equals(type)){
			//不管是否存在小图也返回url；
			if(!validateImage(map, IMAGE_TYPE_SMALL)){
				LOG.warn("IMAGE("+clbId+","+version+") not have small type!");
				getImageStatus(clbId, version, IMAGE_TYPE_SMALL);
				return null;
			}
			re.setUrl(transferUrl(rs.getSmallURL()));
			return re;
		}else {
			if(validateImage(map, IMAGE_TYPE_MEDIUM)){
				result = rs.getMediumURL();
			}
		}
		if (StringUtils.isEmpty(result)) {
			result = rs.getOriginalURL();
			re.setStatus(false);
		}
		re.setUrl(transferUrl(result));
		return re;
	}
	
	private String transferUrl(String url){
		if(StringUtils.isNotEmpty(url)){
			return url.replace("http://", "/" + getTranspondFlag() + "/");
		}
		return null;
	}
	
	private String getTranspondFlag(){
		if(StringUtils.isEmpty(transpondFlag)){
			initTranspondFlag();
		}
		return transpondFlag;
	}
	
	private synchronized void initTranspondFlag() {
		if(StringUtils.isEmpty(transpondFlag)){
			transpondFlag = DDLFacade.getBean(DucklingProperties.class).getProperty("duckling.file.proxy.gateway");
		}
	}
	
	@Override
	public String getImageStatus(int clbId, String version, String type){
		if (StringUtils.isEmpty(version)) {
			version = "latest";
		}
		IResizeImageService ris = CLBServiceFactory.getResizeImageService(conn);
		Map<String, ImageMeta> map = ris.queryResizeStatus(clbId, version);
		ImageMeta im = null;
		if(map==null||(im=map.get(type))==null){
			resizeImage(clbId, version);
			LOG.info("CLBID="+clbId+",VERSION="+version+" not have resize Image and send resize event");
			return "not_ready";
		}
		if(CLBStatus.ZOMBIE==im.getStatus()){
			resizeImage(clbId, version);
			LOG.info("The "+type+" is zombie and send clbId="+clbId+",version="+version+" resize event");
			return "not_ready";
		}
		return im.getStatus().toString();
	}

	private boolean validateImage(Map<String,ImageMeta> map,String type){
		ImageMeta i = map.get(type);
		//status 值为 not_ready, ready, failed,zombie;
		if(i==null||CLBStatus.NOT_READY==i.getStatus()){
			return false;
		}else if(CLBStatus.FAILED==i.getStatus()){
			return false;
		}else{
			return validateWidth(i);
		}
		
	}
	private boolean validateWidth(ImageMeta im){
		if(IMAGE_TYPE_SMALL.equalsIgnoreCase(im.getResizeType())){
			if(im.getWidth()>130){
				resizeImage(im.getDocid(), im.getVersion()+"");
				LOG.info("image("+im.getDocid()+","+im.getVersion()+") size need width=130 now with="+im.getWidth()+" and send resize event");
				return false;
			}
			if(im.getSize()==0){
				resizeImage(im.getDocid(), im.getVersion()+"");
				LOG.info("image("+im.getDocid()+","+im.getVersion()+") size=0 and send resize event");
				return false;
			}
		}else if(IMAGE_TYPE_MEDIUM.equalsIgnoreCase(im.getResizeType())){
			if(im.getWidth()>1024){
				resizeImage(im.getDocid(), im.getVersion()+"");
				LOG.info("image("+im.getDocid()+","+im.getVersion()+") size need width=1024 now with="+im.getWidth()+" and send resize event");
				return false;
			}
		}
		return true;
	}
	public void resizeImage(int clbId, String version) {
		if (StringUtils.isEmpty(version)) {
			version = "latest";
		}
		if(!noImageCompress()){
			IResizeImageService ris = CLBServiceFactory.getResizeImageService(conn);
			ris.resize(clbId, version, getImageResiseParam());
		}
	}

	public void getImageContent(int docid, int version, String type,
			DFileSaver fs) {
		FileSaverBridge bridge = new FileSaverBridge(fs);
		String v = version + "";
		if (version <= 0) {
			v = "latest";
		}
		if (StringUtils.isEmpty(type)) {
			type = IMAGE_TYPE_MEDIUM;
		}
		if (IMAGE_TYPE_ORIGINAL.equals(type)||noImageCompress()) {
			DocumentService dService = CLBServiceFactory.getDocumentService(conn);
			dService.getContent(docid, v, bridge);
			return;
		}
		boolean needOrignal = true;
		//是否强制小图，如果没有小图也不返回原图
		if(IMAGE_TYPE_FIXSMALL.equals(type)){
			type=IMAGE_TYPE_SMALL;
			needOrignal=false;
		}
		ImageQuery q = new ImageQuery();
		q.setDocid(docid);
		q.setVersion(v);
		q.setResizeType(type);
		q.setResizeParam(getImageResiseParam());
		q.setNeedOrignal(needOrignal);
		IResizeImageService ris = CLBServiceFactory.getResizeImageService(conn);
		Map<String,ImageMeta> map = ris.queryResizeStatus(docid, v);
		ImageMeta im = null;
		if(map!=null&&(im=map.get(type))!=null&&CLBStatus.READY==im.getStatus()){
			fs.setLength(im.getSize());
		}
		ris.getContent(q, bridge);
	}
	private String imageCompress = null;
	private boolean noImage;
	/**
	 * 判断clb是否提供压缩功能
	 * @return
	 */
	private boolean noImageCompress(){
		if(imageCompress==null){
			initImageCompress();
		}
		return noImage;
	}


	private ResizeParam getImageResiseParam() {
		ResizeParam param = new ResizeParam();
		param.setSmallPoint(130);
		param.setMediumPoint(1024);
		param.setLargePoint(1500);
		param.setUseWidthOrHeight(ResizeParam.USE_WIDTH);
		return param;
	}

	private synchronized void initImageCompress() {
		if(imageCompress==null){
			imageCompress = VWBContainerImpl.findContainer().getProperty("duckling.clb.haveCompress");
			if(StringUtils.isEmpty(imageCompress)){
				imageCompress = "";
				noImage = false;
			}else{
				noImage = true;
			}
		}
	}
	
}
