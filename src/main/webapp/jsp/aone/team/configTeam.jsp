<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<script type="text/javascript">
</script>
<style>
#content #content-title{background:#f5f5f5;border-bottom:0;padding-bottom:0}
#content #content-title h1{font-size:20px;line-height:1.8em;}
</style>
	<div id="content-title">
		<div id="editTool">
			<ul id="toolGroup">
				<li><a class="toolReturn" href="<vwb:Link context='teamHome' format='url'/>"><span>返回</span></a></li>
			</ul>
			<div class="decoLeft"></div>
		</div>
		<h1>管理团队：<c:out value="${currTeam.displayName}" /></h1>
	</div>
	<div id="config-tab">
		<ul class="tab-list">
			<li <c:if test="${currTab eq 'basic'}">class="current" </c:if>>
				<a href="<vwb:Link context='configTeam' format='url' jsp="${currTeam.name}"/>&func=adminBasic">基本设置</a>
			</li>
			<li <c:if test="${currTab eq 'users'}">class="current" </c:if>>
				<a href="<vwb:Link context='configTeam' format='url' jsp="${currTeam.name}"/>&func=adminUsers">成员列表</a>
			</li>
			<li <c:if test="${currTab eq 'applicant'}">class="current" </c:if>>
				<a href="<vwb:Link context='configTeam' format='url' jsp="${currTeam.name}"/>&func=adminApplicant">管理申请</a>
			</li>
			<vwb:SpaceType/>
			<c:if test="${spaceType != 'personal'  }">
			<li <c:if test="${currTab eq 'invitations'}">class="current" </c:if>>
				<a href="<vwb:Link context='configTeam' format='url' jsp="${currTeam.name}"/>&func=adminInvitations">邀请成员</a>
			</li>
			</c:if>
			<li <c:if test="${currTab eq 'export'}">class="current" </c:if>>
				<a href="<vwb:Link context='configTeam' format='url' jsp="${currTeam.name}"/>&func=exportDocs">导出文件</a>
			</li>
		</ul>
	</div>
	<div id="config-content">
		<c:choose>
			<c:when test="${currTab eq 'basic'}">
				<jsp:include page="/jsp/aone/team/admin/adminBasic.jsp"/>
			</c:when>
			<c:when test="${currTab eq 'users'}">
				<jsp:include page="/jsp/aone/team/admin/adminUsers.jsp"/>
			</c:when>
			<c:when test="${currTab eq 'applicant'}">
				<jsp:include page="/jsp/aone/team/admin/adminApplicant.jsp"/>
			</c:when>
			<c:when test="${currTab eq 'invitations' and spaceType != 'personal'}">
				<jsp:include page="/jsp/aone/team/admin/adminInvitations.jsp"/>
			</c:when>
			<c:when test="${currTab eq 'export'}">
				<jsp:include page="/jsp/aone/team/admin/exportDocs.jsp"/>
			</c:when>
		</c:choose>
	</div>

