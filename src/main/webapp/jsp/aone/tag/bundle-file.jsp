<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>


<div id="content-title-right">
	<h1>${bundle.title }</h1>
</div>
	<div id="fileInfo">
	<c:forEach items="${resList }" var="file" varStatus="status">
		<div resource_id="${resList[status.index].rid }">
			<table class="fileContainer">
				<tbody>
					<tr class="fileDetail">
						<th >
							<div class="fileIcon ${filePageInfo[status.index].fileType}"></div>
						</th>
						<td>
							<h3 class="fileName">${file.title }</h3>
							<p class="fileNote">
								${file.lastEditorName } | 修改于
								<fmt:formatDate value="${file.lastEditTime}" type="both"
									dateStyle="medium" />
								| <span>当前版本：${file.lastVersion}</span>
								<c:if test="${file.lastVersion >1 }">
								 | <span>历史版本：</span>
									<c:forEach var="index" begin="1" end="${file.lastVersion-1 }" step="1">
										<a href='<vwb:Link context="download" page="${file.itemId }" format="url"/>?type=doc&version=${index}'>
										${index }
										</a>
									</c:forEach>
								</c:if>
							</p>
							<div class="largeButtonHolder">
								<c:choose>
									<c:when test="${filePageInfo[status.index].pdfstatus == 'converting'}">
										<p>该文档正在进行转换，请稍等几分钟后刷新页面即可浏览！</p>
									</c:when>
									<c:when test="${filePageInfo[status.index].pdfstatus== 'success' or filePageInfo[status.index] == 'original_pdf'}">
										<a
											href="<vwb:Link page="${file.itemId}" context='file' format='url'/>?func=onlineViewer"
											class="largeButton extra onlineViewerClass" id="onlineViewer" target="_blank">在线预览</a>
									</c:when>
									<c:when test="${filePageInfo[status.index].pdfstatus== 'fail'}">
										<p>该文档在PDF转换过程中转换失败！无法进行在线浏览！</p>
									</c:when>
									<c:when test="${filePageInfo[status.index].pdfstatus== 'source_not_found'}">
										<p>PDF转换时未找到原文档，无法预览！</p>
									</c:when>
									<c:when test="${filePageInfo[status.index].fileType eq 'img'}">
										<!-- 剔除图片的不支持转换信息 -->
									</c:when>
									<c:otherwise>
										<c:choose>
											<c:when test="${filePageInfo[status.index].supported}">
												<vwb:CLBCanUse>
												<a class="largeButton extra pdfTransform" 
													transform="<vwb:Link page="${file.itemId}" context='file' format='url'/>?func=pdfTransform">格式转换</a>
												</vwb:CLBCanUse>
											</c:when>
											<c:otherwise>
												<span>暂不支持该文件类型的在线显示</span>
											</c:otherwise>
										</c:choose>
									</c:otherwise>
								</c:choose>
								<a href="${filePageInfo[status.index].downloadUrl}"
									class="largeButton extra">下载<span class="ui-text-note">(${filePageInfo[status.index].shortFileSize
										})</span></a>
								<a class="bundle-oper" href="<vwb:Link context='file' page='${file.itemId}' format='url'/>?func=shareExistFile">分享</a>
								<a class="update-file-button bundle-oper" fid="${file.itemId }">更新</a>
								<a class="deleteBundleFile bundle-oper" fid="${file.itemId }">删除</a>
								<c:if test="${filePageInfo[status.index].fileType eq 'img'}">
										<!-- 剔除图片的不支持转换信息 -->
										<a class="preview bundle-oper" fid="${file.itemId }">预览</a>
										
									</c:if>
								<c:if
									test="${filePageInfo[status.index].supported or filePageInfo[status.index].fileType eq 'pdf'}">
									<p class="ui-text-note">在线预览支持Firefox10+,Chrome,IE9+,Safari5+等浏览器</p>
								</c:if>
							</div>
							<p></p>
							<%-- <c:set var="tagExist" value="${filePageInfo[status.index].tagExist }" scope="request"/> --%>
							<c:set var="tagMap" value="${tagMapList[status.index] }" scope="request"/>
							<c:set var="rid" value="${file.rid }" scope="request"/>
							<c:set var="starmark" value="${filePageInfo[status.index].starmark }" scope="request"/>
							<jsp:include page="bundle-add-tag.jsp"/>
						</td>
					</tr>
				</tbody>
			</table>
		<div class="ui-clear"></div>
		</div>
	</c:forEach>
</div>

<div class="ui-dialog" id="upload-attach-dialog" style="width:400px;">
		<p class="ui-dialog-title">更新文件</p>
	
		<div id="file-uploader-demo1">
			<div class="qq-uploader">
				<div class="qq-upload-button">上传文件
					<input type="file" multiple="multiple" name="files">
				</div>
				<ul class="qq-upload-list fileList"></ul>
			</div>
		</div>
		
		<div class="ui-dialog-control">
			<input type="button" id="attach-to-bundle" value="完成"/>
			<a name="cancel">取消</a>
		</div>
</div>

<div class="lynxDialog imgPreview" style="display:none;">
	<table>
		<tr>
			<td>
				<span class="closePreview"></span>
				<img />
			</td>
		</tr>
	</table>
	
</div>

<div class="ui-dialog" id="delete-attach-dialog">
	<p class="ui-dialog-title">删除文件</p>
	<p>您真的要删除此文件吗？</p>
	<p style="color:red">提示：该操作将会使所有关于此文件的下载链接失效。</p>
	<div class="ui-dialog-control">
		<form action="" method="POST">
			<input type="hidden" name="func" value="moveToTrash"/>
			<input type="hidden" name="bid" value="${bundle.itemId}"/>
			<input type="submit" value="删除"/>
			<a name="cancel">取消</a>
		</form>
	</div>
</div>

<script type="text/javascript">
$(document).ready(function(){
	
	$("a.preview.bundle-oper").pulldownMenu({
		'menu': $(".lynxDialog.imgPreview"),
		'close': $('.lynxDialog.imgPreview .closePreview'),
		'direction': 'none',
		'beforeShow':function(){
			var imgsrc = $(this).parent().find("a.largeButton.extra").attr("href");
			$(".lynxDialog.imgPreview img").attr({"src":imgsrc});
		}
	});
	
	$("table.fileContainer .fileDetail .fileIcon.img").pulldownMenu({
		'menu': $(".lynxDialog.imgPreview"),
		'close': $('.lynxDialog.imgPreview .closePreview'),
		'direction': 'none',
		'beforeShow':function(){
			var imgsrc = $(this).parent().parent().find("a.largeButton.extra").attr("href");
			$(".lynxDialog.imgPreview img").attr({"src":imgsrc});
		}
	});
	
	var upload_url = "<vwb:Link context='upload' format='url'/>?func=updateFile";
	
	var uploadedFiles = [];
	var index = 0;
	var curr_params = {"fid":"1","version":"1"};
	function createUploader(){  
         var uploader = new qq.FileUploader({
             element: document.getElementById('file-uploader-demo1'),
             action: upload_url,
             //params:{cid:"${cid}",fid:"${element.fid}",version:"${element.version}"},
             multiple:false,
             onSubmit:function(id,  fileName)  {
            		this.params.fid = curr_params.fid;
            		this.params.version = curr_params.version;
             },
             onComplete:function(id, fileName, data){
             	uploadedFiles[index] = data;
             	index ++;
             },
             debug: true
         });           
     };
     
     createUploader();
     
     $('.update-file-button').click(function(){
 		curr_params = '{"fid":"'+$(this).attr('fid')+'", "version":"'+$(this).attr('version')+'", "bid":"'+${bundle.itemId}+'"}';
 		curr_params = eval("("+curr_params+")");
 		ui_showDialog("upload-attach-dialog");
 		//curr_params = "fid:"+$(this).attr('fid')+", version:"+$(this).attr('version')+", bid:"+${bundle.itemId};
 	});

	$("ul#bundle-navList li a").click(function(e){
		//$("div#res_454").viewFocus();
		e.preventDefault();
		$("div[resource_id=" + $(this).parent().attr("resource_id") + ']').viewFocus();
	});
	
	$('.pdfTransform').click(function(){
		var transformUrl = $(this).attr("transform");
		$.ajax({
			url:transformUrl,
			type:'GET',
			success:function(){},
			error:function(){
				alert("error");
			},
			statusCode:{
				450:function(){alert('会话已过期,请重新登录');},
				403:function(){alert('您没有权限进行该操作');}
			}
		});
	});
	
	$('.deleteBundleFile').click(function(){
		var fid = $(this).attr('fid');
		var url = site.getURL('file',fid);
		$('#delete-attach-dialog form').attr('action',url);
		$('#delete-attach-dialog').attr('style','width:400px; position:fixed; top:30%; left:30%;').fadeIn();
	});
	
	$('a[name=cancel]').click(function(){
		$(this).parents('div.ui-dialog').fadeOut();
	});
	
	$('#attach-to-bundle').click(function(){
		window.location.reload();
	});
	
	if ($.browser.msie && parseInt($.browser.version, 10)<9) {
		$('.onlineViewerClass').addClass('disabled')
		.attr({
			'disabled':'disabled',
			'title':'您的浏览器不能使用在线预览功能，请使用Firefox10+,IE9+,Safari5+等浏览器'
		})
		.click(function(event){
			event.preventDefault();
		});
	}

});
</script>