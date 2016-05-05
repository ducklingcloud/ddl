<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<script type="text/javascript" src="${contextPath}/jsp/aone/js/fileuploader.js?v=${aoneVersion}"></script>
<link href="${contextPath}/jsp/aone/css/fileuploader.css" rel="stylesheet" type="text/css">	
<script type="text/javascript">
$(document).ready(function(){
    var upload_url = "<vwb:Link context='upload' format='url'/>?func=updateCollectionFile";
	
	var uploadedFiles = [];
	var index = 0;
	
	function createUploader(){  
         var uploader = new qq.FileUploader({
             element: document.getElementById('file-uploader-demo1'),
             action: upload_url,
             params:{cid:"${cid}",fid:"${element.fid}",version:"${element.version}"},
             multiple:false,
             onComplete:function(id, fileName, data){
             	uploadedFiles[index] = data;
             	index ++;
             },
             debug: true
         });           
     };
     
     createUploader();
     
      $("#open-upload-form-button").live("click",function(){
    	 ui_showDialog("upload-attach-dialog");
     });
     
     $("a[name='cancel']").live("click",function(){
     	ui_hideDialog("upload-attach-dialog");
     });
     
     $("#attach-to-page").click(function(){
 		window.location.reload();
     });
     
     $("#delete-submit").click(function(){
    	 
     });
     
     $("#delete-cancel").click(function(){
    	 ui_hideDialog("delete-attach-dialog");
     });
     
     $("#open-delete-form-button").click(function(){
    	 ui_showDialog("delete-attach-dialog");
     });
     
 	$("#open-move-form-button").live("click",function(){
		var url = "<vwb:Link context='configCollection' format='url'/>?func=loadCollectionList&cid=${collection.resourceId}";
		ajaxRequest(url,null,afterLoadCollections);
	});
	
	function afterLoadCollections(data){
		$("#candidats-container ul").html("");
		if(data!=null){
			if(data.length==0)
				alert("没有可移动集合");
			else{
				for(var i=0;i<data.length;i++){
					$("#single-collection-template").tmpl(data[i]).appendTo("#candidats-container ul");
				}
				ui_showDialog("move-page-dialog");
			}
		}
	};
	
	var fulScrA = $('a#fullScreen');
	var fulScrD = $('div#fullScreenEdge');
	function fullscreen(FULL) {
		var full;
		if (typeof(FULL)!='undefined') {
			full = FULL;
		}
		else {
			full = (fulScrA.attr('isFull')=='true')? false : true;
		}
		
		if (full) {
			$('body').addClass('fullScreen');
			fulScrA.attr('isFull', 'true').html('退出全屏').attr('title', 'ESC键退出全屏');
			fulScrD.attr('isFull', 'true').hide();
			window.location.hash = '#fullscreen';
			/* for FF4 */
		}
		else {
			$('body').removeClass('fullScreen');
			fulScrA.attr('isFull', 'false').html('全屏阅读').attr('title', 'Alt+Enter键全屏');
			fulScrD.attr('isFull', 'false').show();
			window.location.hash = '';
		}
	}
	$(window).keyup(function(KEY){
		if (KEY.which=='27' && KEY.target.toString().toLowerCase().indexOf('textarea')<0) {
			fullscreen(false);
		}
		if (KEY.which==13 && KEY.altKey) {
			fullscreen();
		}
	});
	$('a#fullScreen, div#fullScreenEdge').click(function(){
		fullscreen($(this).attr('isFull')=='false')
		/* TO ADD: */
		/*	Support for FF4
		 *	Resize when browser size changes */
	});
	
	
	if ($.browser.msie && parseInt($.browser.version, 10)<9) {
		$('#onlineViewer').addClass('disabled')
		.attr({
			'disabled':'disabled',
			'title':'您的浏览器不能使用在线预览功能，请使用 Firefox, Chrome, Safari, IE9+ 或 Opera 浏览器'
		})
		.click(function(event){
			event.preventDefault();
		});
	}
	

	var transformUrl="<vwb:Link page='${fid}' context='file' format='url'/>?func=pdfTransform";
	$('#pdfTransform').click(function(){
		ajaxRequest(transformUrl,"", function(){
			window.alert("文档已在后台转换，稍等几分钟后刷新即可进行在线浏览！");
		});
	});
});
</script>

<div id="content-title">
	<div id="editTool">
		<a href="javascript:void(0)" title="编辑工具" class="editTool"><span></span></a>
		<ul id="toolGroup">
			<li><a href="${downloadURL}" class="ui-iconButton download">下载</a></li>
			<li><a href="<vwb:Link context='file' page='${element.fid}' format='url'/>?func=shareExistFile" >分享</a></li>
			<li><a id="open-delete-form-button">删除</a></li>
			<li><a id="open-move-form-button">移动</a></li>
			<li><a id="open-upload-form-button">更新</a></li>
		</ul>
		<div class="decoLeft"></div>
	</div>
	
	<h2><a href="<vwb:Link context='viewCollection' page='${collection.resourceId}' format='url'/>">${collection.title}</a></h2>
	<c:choose>
		<c:when test="${fileExtend eq 'FILE'}">
			<h1><span>文件：</span>${element.title}</h1>
		</c:when>
		<c:otherwise>
			<h1><span>图片：</span>${element.title}</h1>
		</c:otherwise>
	</c:choose>
</div>
<div id="content-major">
	<div id="operation" class="ui-RTCorner" style="text-align:right">
		<a id="fullScreen" isFull="false" title="Alt+Enter键全屏">全屏阅读</a>
		<div id="fullScreenEdge" isFull="false" title="全屏阅读"></div>
	</div>
	<div id="version">
		${element.changeBy} 上传于 ${element.changeTime} |
		<a href="#">版本：${element.version}</a>
	</div>
	<div class="clear"></div>
	<c:choose>
		<c:when test="${fileExtend eq 'FILE'}">
			<div id="fileInfo">
				<table class="fileContainer">
				<tr>
					<th><div class="fileIcon <vwb:FileExtend  fileName='${element.title}'/>"></div></th>
					<td>
						<p class="fileName">${element.title}</p>
						<p class="fileNote"></p>
					</td>
				</tr>
				</table>
			</div>
		</c:when>
		<c:otherwise>
			<div id="photoInfo">
				<div class="photoContainer">
					<img src="${downloadURL}"/>
				</div>
			</div>
		</c:otherwise>
	</c:choose>
	<div class="largeButtonHolder holderCenter">
	<c:choose>
		<c:when test="${pdfstatus==1}">
		<p>该文档正在进行转换，请稍等几分钟后刷新页面即可浏览！</p>
		</c:when>
		<c:when test="${pdfstatus==2}">
		<a target="_blank" href="<vwb:Link page="${fid}" context='file' format='url'/>?func=onlineViewer" class="largeButton extra" id="onlineViewer">在线预览</a>
		</c:when>
		<c:when test="${pdfstatus==3}">
		<p>该文档在PDF转换过程中转换失败！无法进行在线浏览！</p>
		</c:when>
		<c:when test="${strFileType eq 'img'}">
			<!-- 剔除图片的不支持转换信息 --><p>这是图片</p>
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test="${supported}">
				<vwb:CLBCanUse>
					<a class="largeButton extra" id="pdfTransform">格式转换</a>
				</vwb:CLBCanUse>
				</c:when>
				<c:otherwise>
				<p>${supported },,${strFileType }</p>
				<p>暂不支持该文件类型的在线显示</p>
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>
		<a href="${downloadURL}" class="largeButton extra">下载</a>
		<c:if test="${supported or strFileType eq 'pdf'}">
			<p class="ui-text-note">在线预览支持Firefox10+,Chrome,IE9+,Safari5+等浏览器</p>
		</c:if>
	</div>
	<div class="bedrock"></div>
</div>

<div id="content-side">
	<div id="relevance" class="sideBlock">
		<h4>文件信息</h4>
		<table class="ui-table-form-2col">
		<tr>
			<th>大小：</th>
			<td>${sizeShort}</td>
		</tr>
		</table>
	</div>
	<div class="sideBlock">
		<h4>版本信息</h4>
		<table class="ui-table-form-2col">
		<c:forEach items="${versionList}" var="item">
		<tr>
			<th>版本:</th>
			<td><a href="<vwb:Link format='url' context='file' page='${item.fid}'/>?func=viewFile&version=${item.version}">${item.version}</a></td>
		</tr>
		</c:forEach>
		</table>
	</div>
	<div id="linkIn" class="sideBlock">
		<h4>引用页面</h4>
<!--		<p class="NA">暂无引用该文件的页面</p>-->
		<ul id="abc" class="fileList">
			<c:forEach items="${refView}" var="item">
				<li>
					<a href="<vwb:Link format='url' context='view' page='${item.dfileRef.pid}'/>">${item.pageName}</a>
				</li>
			</c:forEach>
		</ul>
	</div>
</div>
<div class="ui-clear"></div>


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
			<input type="button" id="attach-to-page" value="完成"/>
			<a name="cancel">取消</a>
		</div>
</div>

<div class="ui-dialog" id="delete-attach-dialog" style="width:400px;">
	<p class="ui-dialog-title">删除文件</p>
	<p>您真的要删除此文件吗？</p>
	<p style="color:red">提示：该操作将会使所有关于此文件的下载链接失效。</p>
	<div class="ui-dialog-control">
		<form action='<vwb:Link format='url' context='file' page='${element.fid}'/>' method="POST">
			<input type="hidden" name="func" value="moveToTrash"/>
			<input type="submit" value="删除"/>
			<a id="delete-cancel">取消</a>
		</form>
	</div>
</div>

<div class="ui-dialog" id="move-page-dialog" style="width:400px;">
	<span class="ui-dialog-x"></span>
	<p class="ui-dialog-title">
		移动文件
	</p>
	<form  action="<vwb:Link context='configCollection' format='url'/>?func=moveElement" method="POST">
		<div class="ui-dialog-body">
			<input type="hidden" name="rid" value="${element.fid}"/>
			<input type="hidden" name="type" value="DFile"/>
			<table class="ui-table-form" width="320px;">
				<tr><th style="text-align:left;">将文件移动到：</th></tr>
				<tr><td id="candidats-container">
					<ul class="fileList"></ul>
					</td>
				</tr>
			</table>
		</div>
		<div class="ui-dialog-control">
			<input type="submit" value="保存"/>
			<a class="ui-dialog-close ui-text-small">取消</a>
		</div>
	</form>
</div>

<script id="single-collection-template" type="text/html">
	<li><label><input type="radio" name="cid" value="{{= cid}}"/>{{= title}}</label></li>
</script>