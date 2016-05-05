<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />

<style>
#candidates { border-collapse:none; }
#candidates tr td:first-child { border-left:5px solid transparent; }
#candidates td.authOption { color:#999; }
#candidates tr.inner-chosen td { background:#f9f8ff; border-bottom:1px solid #69c; color:#000 !important; }
#candidates tr.inner-chosen td:first-child { border-left:5px solid #69c; }
</style>

<script type="text/javascript">

</script>
	<div id="content-title">
		<h1>成员</h1>
	</div>
<div id="content-menu">
		<!-- 
		<ul class="ui-navList">
			<c:if test="${teamId != 1}">
			<li <c:if test="${currTab == 'contacts'}">class="current"</c:if>>
					<a name="contacts" href="<vwb:Link context='notice' format='url'/>?func=contacts" style="float:left; width:70%">通讯录</a>
					<c:if test="${teamType ne 'myspace'}">
					<c:if test="${teamAcl == 'admin'}">
					<a href="<vwb:Link context='configTeam' page='${teamCode}' format='url'/>&func=adminInvitations" style="float:right; width:auto; margin-right:5px;">邀请</a>
					</c:if>
					</c:if>
					<div class="ui-clear"></div>
			</li>
			</c:if>
		</ul>
		 -->
	</div>
<div id="content-menu-body-container" style="margin-left:0; border-left:none">
	<jsp:include page="/jsp/aone/team/main/contacts.jsp"/>
</div>
<div class="clear"></div>