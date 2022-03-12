<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
    request.setAttribute("decorator", "none");
    response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>

<div id="content-title">
	<h1 id="pageTitle">${resource.title }</h1>
	<%-- <c:choose>
		<c:when test="${resource.tagMap.length eq 0 }">
			<c:set var="tagExist" value="${false}"  scope="request"/>
		</c:when>
		<c:otherwise>
			<c:set var="tagExist" value="${true}"  scope="request"/>
		</c:otherwise>
	</c:choose> --%>
	<c:set var="tagMap" value="${resource.tagMap }" scope="request"/>
	<c:set var="rid" value="${resource.rid }" scope="request"/>
	<c:set var="starmark" value="${starmark }" scope="request"/>
	<jsp:include page="bundle-add-tag.jsp"/>
</div>
<div id="content-major" >
	<%-- <div id="version">
		${resource.lastEditorName} | 修改于
		<fmt:formatDate value="${resource.lastEditTime}" type="both" dateStyle="medium" />
		|<a href="<vwb:Link context='info' format='url' page='${resource.itemId}'/>">版本：${resource.lastVersion}</a>
	</div> --%>
	<div id="version">
		<c:choose>
			<c:when test="${pageVersion != null }">
				${pageVersion.editorName} | 修改于
				<fmt:formatDate value="${pageVersion.editTime}" type="both" dateStyle="medium" />
				|<a href="<vwb:Link context='info' format='url' page='${pageVersion.pid}'/>">版本：${pageVersion.version}</a>
				<c:if test="${pageLatestVersion != pageCurVersion }">
					<span class="recoverPageVersion">
						<input type="hidden" name="pid" value="${pid }">
						<input type="hidden" name="version" value="${pageCurVersion }">
						<input type="hidden" name="tid" value="${pageMeta.tid }">
						| <a>恢复版本</a>
					</span>
				</c:if>
			</c:when>
			<c:otherwise>
				${resource.lastEditorName} | 修改于
				<fmt:formatDate value="${resource.lastEditTime}" type="both" dateStyle="medium" />
				|<a href="<vwb:Link context='info' format='url' page='${resource.itemId}'/>">版本：${resource.lastVersion}</a>
			</c:otherwise>
		</c:choose>
		<c:if test="${!empty copyLog}">
			<br>
			${ copyLog.userName} 从 团队 [${copyLog.fromTeamName}]复制了页面 [${copyLog.rTitle }] 版本：${copyLog.fromVersion}
		</c:if>
	</div>
	<div class="ui-clear"></div>
	<div id="DCT_viewcontent">
		<c:choose>
			<c:when test="${pageVersion != null }">
				<div><vwb:PageDisplay pid="${pageVersion.pid}" version="${pageVersion.version}"/></div>
			</c:when>
			<c:otherwise>
				<div><vwb:PageDisplay pid="${resource.itemId}" version="0"/></div>
			</c:otherwise>
		</c:choose>
		<div class="ui-clear"></div>
	</div>
	<div id="overflowShade"></div>
	<hr />
	
	<div id="attachment" class="sideBlock">
		<h4>附件列表</h4>
		<ul id="exist-attach-list" class="fileList">
		<c:forEach items="${attachments}" var="item">
			<c:choose>
				<c:when test="${item.type eq 'IMAGE'}">
					<li>
						<a class='image <vwb:FileExtend fileName="${item.title}"/>' href='<vwb:Link context="file" page="${item.fid}" format="url"/>?func=viewImage'>
							<span class="fileIcon <vwb:FileExtend fileName='${item.title}'/>"></span>${item.title}</a>
						<a class='file ui-RTCorner ui-iconButton download' href='<vwb:Link context="download" page="${item.fid}" format="url"/>' title="下载">&nbsp;</a>
					</li>
				</c:when>
				<c:otherwise>
					<li>
						<a class='file <vwb:FileExtend fileName="${item.title}"/>' href='<vwb:Link context="file" page="${item.fid}" format="url"/>'>
							<span class="fileIcon <vwb:FileExtend fileName='${item.title}'/>"></span>${item.title}</a>
						<a class='file ui-RTCorner ui-iconButton download' href='<vwb:Link context="download" page="${item.fid}" format="url"/>' title="下载">&nbsp;</a>
					</li>
				</c:otherwise>
			</c:choose>
		</c:forEach>
		</ul>
	</div>
	<div id="comment">
		<c:set var="itemId" value="${resource.itemId}" scope="request"/>
		<c:set var="itemType" value="DPage" scope="request"/>
		<jsp:include page="/jsp/aone/comment/displayComment.jsp"></jsp:include>
	</div>
</div>
<c:set var="pid" value="${resource.itemId}" scope="request"></c:set>
<c:set var="bid" value="${bundle.bid}" scope="request"/>
<c:set var="version" value="${pageVersion.version }" scope="request"/>
<c:set var="latestVersion" value="${resource.lastVersion}" scope="request"/>
<jsp:include page="/jsp/aone/tag/lynxPageBar.jsp"></jsp:include>
<div class="ui-dialog bundle" id="delete-attach-dialog" style="width:400px;">
	<p class="ui-dialog-title">删除页面</p>
	<p>您真的要删除此页面吗？</p>
	<p style="color:red">提示：该操作将会使所有关于此页面的版本链接失效。并从当前组合中移除！</p>
	<div class="ui-dialog-control">
		<form id="deleteBundleItemForm" action='<vwb:Link format='url' context='bundle' page='${bundle.itemId}'/>?func=deleteBundleItem&rid=${resource.rid}' method="POST">
			<input type="hidden" name="func" value="moveToTrash"/>
			<input type="submit" value="删除"/>
			<a id="delete-cancel" name="cancel">取消</a>
		</form>
	</div>
</div>

<div class="ui-dialog" id="delete-bundleerror-dialog"
	style="width: 400px; position: fixed; left: 30%;">
	<p class="ui-dialog-title">删除错误</p>
	<p style="color: red;line-height:50px;">您无权删除该资源，只能由资源创建者或管理员进行删除！</p>
</div>

<script type="text/javascript">
$(document).ready(function(){
	(function scrollNav(){
		var lis=$("#bundle-navList li");
		var scrollValue=0;
		for(var i=0;i<lis.length;i++){
			if($(lis[i]).attr("class")=="active"){
				break;
			}
			scrollValue+=$(lis[i]).height();
		}
		$('#bundle-navList').scrollTop(scrollValue);
	})();
	
	$('.toolDelete').click(function(){
		$('#delete-attach-dialog').attr('style','width:400px; position:fixed; top:30%; left:30%;').fadeIn();
	});
	
	$('a[name=cancel]').click(function(){
		$(this).parents('div.ui-dialog').fadeOut();
	});
	$(".recoverPageVersion").live("click",function(){
		var version = $(this).find("input[name='version']").val();
		var tid = $(this).find("input[name='tid']").val();
		var aa= window.confirm("您确定要恢复此版本的文本文档吗？");
		if (aa) {
			window.location.href="<vwb:Link context='info' format='url' page='${pid}'/>"+"&func=recoverVersion&tid="+tid+"&version="+version;
		}
	});
	$("#deleteBundleItemForm").validate({
		submitHandler:function(form){
			ui_hideDialog("delete-attach-dialog");
			$.ajax({
				url :"<vwb:Link format='url' context='bundle' page='${bundle.itemId}'/>?func=deleteBundleItemValidate&rid=${resource.rid}",
				type : "post",
				dataType:"json",
				success : function(data){
					if(!data.status){
						$('#delete-bundleerror-dialog').attr('style','width:400px; height:120px; position:fixed; top:30%; left:30%;').fadeIn();
						window.setTimeout(function(){
							$('#delete-bundleerror-dialog').fadeOut(500);
						},4000);
					}else{
						form.submit();
					}
				},
				statusCode:{
					450:function(){alert('会话已过期,请重新登录');},
					403:function(){alert('您没有权限进行该操作');}
				}
			});
		}
	});
})
</script>
