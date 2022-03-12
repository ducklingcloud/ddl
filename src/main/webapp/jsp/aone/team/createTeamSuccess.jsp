<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/uiLib.css"
	type="text/css" />
	<link rel="stylesheet" href="${contextPath}/jsp/aone/css/lynx.css"
	type="text/css" />
<script type="text/javascript"
	src="${contextPath}/jsp/aone/js/uiLib-jQuery.js"></script>

<style>
#candidates { border-collapse:none; }
#candidates tr td:first-child { border-left:5px solid transparent; }
#candidates td.authOption { color:#999; }
#candidates tr.inner-chosen td { background:#f9f8ff; border-bottom:1px solid #69c; color:#000 !important; }
#candidates tr.inner-chosen td:first-child { border-left:5px solid #69c; }
.msgBox {border:none;}
.msgBox h3 {color:#0a0;}
div.ui-wrap a.btn {color:#fff;}
div.ui-wrap a.btn:hover {text-decoration:none;}
</style>

<script type="text/javascript">
$(document).ready(function(){
	
});
</script>

<div id="content-title">
	<h1>创建团队</h1>
</div>

<div class="content-through">
	<input type="hidden" name="tid" value="${team.id}"/>
	<div class="msgBox" style="width:60%;">
		<h3>团队创建成功！</h3>
		<p>以下是新团队的基本信息：</p>
	</div>
	<table class="ui-table-form" style="font-size:14px;">
	<tbody>
		<tr><th>团队名称：</th>
			<td width="320">${team.displayName}</td>
		</tr>
		<tr><th>团队网址：</th>
			<td>${teamUrl}</td>
		</tr>
		<tr><th>团队权限：</th>
			<c:choose>
				<c:when test="${team.accessType eq 'public' }">
					<td>完全公开</td>
				</c:when>
				<c:when test="${team.accessType eq 'protected' }">
					<td>受限公开</td>
				</c:when>
				<c:otherwise>
					<td>完全保密</td>
				</c:otherwise>
			</c:choose>
		</tr>
		<tr><th>成员权限：</th>
			<c:choose>
				<c:when test="${team.defaultMemberAuth eq 'admin'}">
					<td>可管理</td>
				</c:when>
				<c:when test="${team.defaultMemberAuth eq 'edit'}">
					<td>可编辑</td>
				</c:when>
				<c:otherwise>
					<td>可查看</td>
				</c:otherwise>
			</c:choose>
		</tr>
		
		<tr><th>创建者：</th>
			<td>${team.creator}</td>
		</tr>
		<tr><th>团队简介：</th>
			<td>${team.description}</td>
		</tr>
	</tbody>
	</table>
	
	<div class="procedureHolder largeButtonHolder holderCenter">
		<h3>您可以邀请成员，进入团队，立即开始协作吧！</h3>
		<p>
			<a href="<vwb:Link context='configTeam' format='url' jsp="${team.name}"/>&func=adminInvitations" class="btn btn-info btn-large">邀请成员</a>
			<a href="${contextPath}/ddlInviteHelp.jsp" target="_blank" class="ui-iconButton help inviteHelp"></a>
			<%-- <a href="<vwb:Link format='url' context='configTeam' jsp='${team.name}'/>&func=adminUsers" class="btn btn-info btn-large">修改权限</a> --%>
			&nbsp;<a href="<vwb:Link format='url' context='switchTeam' />?func=jump&team=${team.name}" class="btn btn-warning btn-large">访问团队</a>
		</p>
	</div>
</div>
<div class="clear"></div>
