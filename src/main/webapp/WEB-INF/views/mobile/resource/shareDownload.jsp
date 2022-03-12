<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/lion.css?v=${aoneVersion}" type="text/css" />
<fmt:setBundle basename="templates.default" />
<div id="opareteFileMessage"  class="alert alert-success" style="margin:25px 8px 8px;;display: none;"> </div>
<div id="macroNav" class="simpleMobileNav">
	<a id="logo" title="<spring:message code='ddl.site.name' />"></a>
</div>
<div id="content-major" class="mobileContent">
	<div id="content-title">
		<input type="hidden" value="${uid }" id="currentUid"/>
		<h1 class="fileName" style="font-size:1.65em; margin-bottom:0px;">
			<div class="title-left" style="margin-right:0px">
				<c:if test="${resource.folder == true}">
					<div class="${resource.itemType} headImg40 ${resource.fileType}"></div>
				</c:if>
				<span id="pageTitle" rid="${resource.rid}" parentId="${resource.bid}"><c:out value="${resource.title}"/></span>
			</div>
			<div id="version">
				<spring:message code="ddl.sharing.sharer" />：<span>${shareUserName}</span> &nbsp;|&nbsp;
				<spring:message code="ddl.sharing.time" />： <fmt:formatDate value="${shareResource.createTime}" type="both" dateStyle="medium" />
			</div>
		</h1>
	</div>
	<c:choose>
		<c:when test="${resource.folder == true}">
			<jsp:include page="./shareFolder.jsp" />
		</c:when>
		<c:otherwise>
			<vwb:FileShow rid="${resource.rid}" theme="mobile" version="${version}" ridCode="${ridCode}"/>
		</c:otherwise>
	</c:choose>
</div>

<div class="bedrock"></div>

<c:set var="fileBarItemId" value="${resource.rid}" scope="request"></c:set>
<c:set var='deleteItemURL' scope='request' value=''></c:set>
<c:set var="fileBarBid" value="0" scope="request"></c:set>


<div id="fileBrowserModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="fileBrowserModalLabel" aria-hidden="true">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3 id="fileBrowserModalLabel"><spring:message code="ddl.saveTo" /></h3>
	</div>
	<div class="modal-body" style="height: 345px;">
		<div id="teamSelWrapper" style="display:none;">
			<select id="teamSel">
				<option value="${myTeamCode}" id="teamSel_${myTeamId }" <c:if test="${teamType eq 'myspace'}">selected="selected"</c:if>><spring:message code="ddl.personalSpace" /></option>
				<vwb:TeamPreferences/>
				<c:forEach items="${myTeamList}" var="item">
					<c:if test="${teamAclMap[fn:trim(item.id)] ne 'view'}">   
						<option value="${item.name }" id="teamSel_${item.id }" <c:if test="${teamType eq item.name}">selected="selected"</c:if>><c:out value="${item.displayName}"/></option>
					</c:if>
				</c:forEach>
			</select>
			<span class="ui-text-note" style="color:#999;"><spring:message code="ddl.tip.t2" /></span>
		</div>
		<div id="file_browser"></div>
	</div>
	<div class="modal-footer">
		<button id="newNodeBtn" class="btn text-left pull-left" ><i class="icon-folder-open"></i> <spring:message code="ddl.newFolder" /></button>
		<button class="btn btn-primary" id="moveToBtn"><spring:message code="ddl.confirm" /></button>
		<button class="btn" data-dismiss="modal" aria-hidden="true"><spring:message code="ddl.cancel" /></button>
	</div>
</div>
<div class="coverPage">
	<div class="cover">
	</div>
	<div class="guide">
		<img src="${contextPath}/images/ddlMobileTri.png" style="position:absolute; right:0; top:0; height:60px;">
		<p>无法自动下载？请执行以下操作:<br />
		1.点击右上角的<img src="${contextPath}/images/ddlIOSMore.png" class="more-js" style="width:40px; height:40px; margin:0 5px">按钮<br />
		2.选择“在浏览器中打开”</p>
	</div>
</div>
<script type="text/javascript">

<%--
/**
    显示消息
 * @param type success-成功,block-警告,error-错误
 */
 --%>
function showMsg(msg, type){
	type = type || "success";
	$("#opareteFileMessage").removeClass().addClass("alert alert-" + type).html(msg).show(150);
}
function hideMsg(timeout){
	timeout = timeout || 2000;
	window.setTimeout(function(){$("#opareteFileMessage").hide(150);}, timeout);
}
function showMsgAndAutoHide(msg, type,time){
	time=time||2000;
	showMsg(msg,type);
	hideMsg(time);
}



<%--------- file move/copy/delete start ----------%>
var original_rid = -1;
var file_operation = 'none';

$("#fileCopy").live("click", function(){
	$('#fileBrowserModalLabel').empty();
	$('#fileBrowserModalLabel').append("<spring:message code='ddl.saveTo' />");
	$("#fileBrowserModal").modal();
	original_rid = "${resource.rid}";
	file_operation = 'copy';
	
	$("#teamSelWrapper").show();
	$("#teamSel").val("${teamCode}");
});

$("#fileBrowserModal").on("show", function(){
	loadBrowserTree("${teamCode}");
});

$("#fileBrowserModal").on("hide", function(){
	$("#teamSelWrapper").hide();
});

//团队ID
function getSelectedTid(){
	return $("#teamSel").find("option:selected").attr("id").replace("teamSel_","");
}

$(function(){
	var downloadBtn = $("#fileInfo").find(".largeButton");
	var cover = $(".coverPage");
	downloadBtn.click(function(){
		if(browser.versions.weixin){
			if(browser.versions.android){
				cover.find(".more-js").attr("src","${contextPath}/images/ddlAndroidMore.png");
			}
			cover.show();
			return false;
		};
	});
	cover.click(function(){
		$(this).hide();
	});
});

$(document).ready(function(){
	var mobileHeight = $(window).height();
	$(".cover").css("height",mobileHeight);
})

</script>
<script src="${contextPath}/scripts/bootstrap/js/bootstrap.min.js"></script>
<style type="text/css">
	.popover-content {padding:0;cursor:pointer;}
	.coverPage {display:none;}
	.cover {width:100%; height:100%; background:#000; opacity:0.7; position:absolute; top:0; left:0;z-index:9}
	.guide {
		position:absolute; z-index:10; width:100%; top:0; left:0;
	}
	.guide p {
		font-size:1.5em; left:10%; top:65px; position:relative;width:80%;
		font-family:Arial,"微软雅黑";font-weight:bold; color:#fff; line-height:2em;
	}
</style>
