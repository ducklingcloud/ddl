<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />

<script type="text/javascript" src="${contextPath}/jsp/aone/js/fileuploader.js?v=${aoneVersion}"></script>
<link href="${contextPath}/jsp/aone/css/fileuploader.css" rel="stylesheet" type="text/css">	

<script>
$(document).ready(function(){

	var url = '<vwb:Link context="reclogging" format="url"/>';
	var cur_url ;
	
	$(".related-click-log").live("click",function(){
		var json = new Object();
		json['func'] = "cl";
		json['pid'] = $(this).attr("pid");
		json['type'] = 'click';
		json['tid'] = $(this).attr("tid");
		
		cur_url = $(this).attr("cur_url");
		ajaxRequest(url,json,afterRecordLog);
	});
	
	function afterRecordLog(data){
		//do nothing
		window.location.href = cur_url;
	};
	
	
});
$(document).ready(function(){
	
	function placeEdge() {
		var cont = $('#DCT_viewcontent');
		var major = $('#content-major');
		$('#fullScreenEdge').css('height', cont.height())
			.css('top', cont.offset().top)
			.css('left', major.offset().left + major.outerWidth()-11);
	}
	$(window).resize(function(){ placeEdge(); });
	placeEdge();

	var DOMobj = document.getElementById('DCT_viewcontent');
	var jQobj = $('#DCT_viewcontent');
	var contentWidth = DOMobj.scrollWidth + jQobj.outerWidth(true) - jQobj.width();
	var w = DOMobj.scrollWidth;
	var m = jQobj.outerWidth(true) - jQobj.width();
	
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
	
	var hash = (window.location.hash)?window.location.hash:'';
	if (hash.substring(1)=='fullscreen') {
		fullscreen(true);
	}
	
	$("a[name='movePage']").live("click",function(){
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
	
	var upload_base_url = "<vwb:Link context='upload' format='url'/>";
	var upload_url = "<vwb:Link context='upload' format='url'/>?func=uploadFiles";
	
	var uploadedFiles = [];
	var index = 0;
	
	function createUploader(){  
         var uploader = new qq.FileUploader({
             element: document.getElementById('file-uploader-demo1'),
             action: upload_url,
             params:{pid:"${pid}"},
             onComplete:function(id, fileName, data){
             	uploadedFiles[index] = data;
             	index ++;
             },
             debug: true
         });           
     };
     
     createUploader();
	
    $("#attachFile-button").live("click",function(){
   	 ui_showDialog("upload-attach-dialog");
    });
    
    $("a[name='cancel']").live("click",function(){
    	ui_hideDialog("upload-attach-dialog");
    });
    
    $("#attach-to-this-page").live("click",function(){
	   	 for(var i=0;i<uploadedFiles.length;i++){
	     	 $("#aone-image-row-template").tmpl(uploadedFiles[i]).appendTo("#exist-attach-list");
	   	 }
	   	 $(".qq-upload-list").html("");
	   	 uploadedFiles = new Array();
	   	 index = 0;
	   	 ui_hideDialog("upload-attach-dialog");
    });
});
</script>


<div id="content-title">
	<vwb:Permission permission="edit">
	<div id="editTool">
		<a href="javascript:void(0)" title="编辑工具" class="editTool"><span></span></a>
		<ul id="toolGroup">
			<li><a href="<vwb:Link context='createPage' page="${collection.resourceId}" format='url'/>" class="toolNewPage"><span>+新建页面</span></a></li>
			<li><a name="movePage">移动页面</a></li>
			
			<li>
				<a class="toolDelete" href="#" onClick="$('#deleteForm').submit()"><span>删除</span></a>
			</li>
			<c:choose>
				<c:when test="${version eq latestVersion}">
					<li><a href="<vwb:EditLink format='url'/>" class="toolEdit"><span>编辑</span></a></li>
				</c:when>
				<c:otherwise>
					<li><a href="<vwb:EditLink format='url'/>&version=${version}" class="toolEdit"><span>编辑</span></a></li>
				</c:otherwise>
			</c:choose>
			<form
				action="<vwb:Link format='url' context='view'><vwb:Param name='func' value='del'></vwb:Param></vwb:Link>"
				class="viewPageForm" id="deleteForm" method="post"
				accept-charset="<vwb:ContentEncoding />"
				onsubmit="return( confirm('<fmt:message key="info.confirmdelete"/>') && Wiki.submitOnce(this) );">
			</form>
		</ul>
		<div class="decoLeft"></div>
	</div>
	</vwb:Permission>
	<h2><a href="<vwb:Link context='viewCollection' page='${collection.resourceId}' format='url'/>">${collection.title}</a></h2>
	<h1>${pageMeta.title }</h1>
</div>
<div id="content-major">
	<div id="operation" class="ui-RTCorner" style="text-align:right">
		<a id="fullScreen" isFull="false" title="Alt+Enter键全屏">全屏阅读</a>
		<div id="fullScreenEdge" isFull="false" title="全屏阅读"></div>
	</div>
	<div id="version">
		${editor} | 修改于
		<fmt:formatDate value="${pageMeta.lastEditTime}" type="both" dateStyle="medium"/> |
		<a href="<vwb:Link context='info' format='url' page='${pid}'/>">版本：${pageMeta.lastVersion}</a>
	</div>
	<div class="ui-clear"></div>
	<div id="DCT_viewcontent">
		<vwb:render content="${content}"/>
		<div class="ui-clear"></div>
	</div>
	<div id="overflowShade"></div>
	
	<div id="readTool">
		<jsp:include page="/jsp/aone/pageBar.jsp"></jsp:include>
	</div>
	<hr/>
	
	<div class="share" style="display:none;">
		<vwb:UserCheck status="authenticated">
			<ul class="ui-groupButton" style="margin:0px auto; ">
				<li class="leftSide">
					<input type="hidden" name="subscriptionStatus" value="" />
					<vwb:IsSubscribed flagName="flag" itemsName="existInterest" />
					<c:choose>
						<c:when test="${!flag}">
							<a class="interest-box" attr="${flag}"><span>关注</span></a>
						</c:when>
						<c:otherwise>
							<a class="remove-interest-box" attr="${flag}"><span>已关注</span></a>
						</c:otherwise>
					</c:choose> 
				</li>
				<li class="rightSide">
					<a class="recommend-box"><span>分享</span> </a>
				</li>
			</ul>
		</vwb:UserCheck>
	</div>
	<div id="comment">
		<jsp:include page="/jsp/aone/comment/displayComment.jsp"></jsp:include>
	</div>
</div>

<div id="content-side">
	<div id="relevance" class="sideBlock" >
		<h4>相关内容</h4>
		<c:choose>
			<c:when test="${fn:length(relatedGrids) eq 0 }">
				<p class="NA">暂无相关内容</p>
			</c:when>
			<c:otherwise>
			<c:forEach items="${relatedGrids}" var="item" varStatus="status">
				<dl class="fileList">
					<dt>${item.grid.title}
						<c:choose>
							<c:when test="${item.grid.type eq 'rec'}">							
							</c:when>
							<c:otherwise>
								<span>
									<a class="iconLink config ui-RTCorner" href="<vwb:Link context='viewCollection' page="${collection.resourceId}" format='url'/>/grid?func=editGridItems&gid=${item.grid.id}" title="编辑格子">&nbsp;</a>
								</span>
							</c:otherwise>
						</c:choose>
					</dt>
					<c:forEach items="${item.gridItemList}" var="gridItem">
					<dd>	
						<c:choose>
							<c:when test="${gridItem.item.resourceType eq 'DPage'}">
							<a class="related-click-log" cur_url="<vwb:Link context='view' page='${gridItem.item.resourceId}' format='url'/>" pid='${gridItem.item.resourceId}' tid='${gridItem.item.tid}'>${gridItem.title}</a>
						<!-- 		<a href="<vwb:Link context='view' page='${gridItem.item.resourceId}' format='url'/>">${gridItem.title}</a> -->
							</c:when>
							<c:otherwise>
								<a href="<vwb:Link context='file' page='${gridItem.item.resourceId}' format='url'/>">${gridItem.title}</a>
							</c:otherwise>
						</c:choose>
					</dd>
					</c:forEach>
				</dl>
			</c:forEach>
			</c:otherwise>
		</c:choose>
	</div>
	<div id="attachment" class="sideBlock">
		<h4>附件列表 <input type="button" id="attachFile-button" value="上传附件"/></h4>
		<ul id="exist-attach-list" class="fileList">
		<c:forEach items="${attachments}" var="item">
			<c:choose>
				<c:when test="${item.type eq 'IMAGE'}">
					<li>
						<a class='image <vwb:FileExtend fileName="${item.title}"/>' href='<vwb:Link context="file" page="${item.fid}" format="url"/>?func=viewImage&pid=${pid}'>
							<span class="fileIcon <vwb:FileExtend fileName='${item.title}'/>"></span>${item.title}</a>
						<a class='file ui-RTCorner ui-iconButton download' href='<vwb:Link context="download" page="${item.fid}" format="url"/>' title="下载">&nbsp;</a>
					</li>
				</c:when>
				<c:otherwise>
					<li>
						<a class='file <vwb:FileExtend fileName="${item.title}"/>' href='<vwb:Link context="file" page="${item.fid}" format="url"/>?pid=${pid}'>
							<span class="fileIcon <vwb:FileExtend fileName='${item.title}'/>"></span>${item.title}</a>
						<a class='file ui-RTCorner ui-iconButton download' href='<vwb:Link context="download" page="${item.fid}" format="url"/>' title="下载">&nbsp;</a>
					</li>
				</c:otherwise>
			</c:choose>
		</c:forEach>
		</ul>
	</div>
</div>

<div class="ui-dialog" id="move-page-dialog" style="width:400px;">
	<span class="ui-dialog-x"></span>
	<p class="ui-dialog-title">
		移动页面
	</p>
	<form  action="<vwb:Link context='configCollection' format='url'/>?func=moveElement" method="POST">
		<div class="ui-dialog-body">
			<input type="hidden" name="rid" value="${pid}"/>
			<input type="hidden" name="type" value="DPage"/>
			<table class="ui-table-form" width="320px;">
				<tr><th style="text-align:left;">将页面移动到：</th></tr>
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

<div class="ui-dialog" id="upload-attach-dialog" style="width:400px;">
		<p class="ui-dialog-title">上传附件</p>
	
		<div id="file-uploader-demo1">
			<div class="qq-uploader">
				<div class="qq-upload-button">上传附件
					<input type="file" multiple="multiple" name="files">
				</div>
				<ul class="qq-upload-list fileList"></ul>
			</div>
		</div>
		
		<div class="ui-dialog-control">
			<input type="button" id="attach-to-this-page" value="完成"/>
			<a name="cancel">取消</a>
		</div>
</div>

<script id="single-collection-template" type="text/html">
	<li><label><input type="radio" name="cid" value="{{= cid}}"/>{{= title}}</label></li>
</script>

<script id="aone-image-row-template" type="html/text">
<li>
	<a class="image {{= fileExtend}}" href="{{= infoURL}}"><span class="fileIcon {{= fileExtend}}"></span>{{= title}}</a>
	<a class="file ui-RTCorner ui-iconButton download" href="{{= previewURL}}" title="下载">&nbsp;</a>
</li>
</script>

<div class="ui-clear"></div>
