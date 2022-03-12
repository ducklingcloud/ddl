<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
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
$(document).ready(function(){
	var menu = $('#content-menu');
	var pos = menu.offset().top;
	var top = 100;
	menu.css('top', top);
	$(window).scroll(function(){
		if ($(document).scrollTop()-pos > -top) {
			menu.css('position', 'fixed');
		}
		else {
			menu.css('position', '');
		}
	});
});
</script>
	<!-- <div id="content-title">
		<h1>${team.displayName}动态</h1>
	</div> -->
	<div id="content-menu">
		<ul class="myNavList">
			<li <c:if test="${currTab eq 'teamNotice'}">class="current"</c:if>>
				<a name="contacts" href="<vwb:Link context='notice' format='url'/>?func=teamNotice">团队动态
				<c:if test="${singleTeamCount > 0 }"><span class="feedCount cold"><span class="feedBorder coldBlue">${singleTeamCount}</span></span></c:if>
				</a>
			</li>
			<li <c:if test="${currTab eq 'personNotice'}">class="current"</c:if>>
				<a name="contacts" href="<vwb:Link context='notice' format='url'/>?func=personNotice">我的消息
				<c:if test="${singlePersonCount > 0 }"><span class="feedCount"><span class="feedBorder">${singlePersonCount}</span></span></c:if>
				</a>
			</li>
			<li <c:if test="${currTab eq 'historyNotice'}">class="current"</c:if>>
				<a name="contacts" href="<vwb:Link context='notice' format='url'/>?func=historyNotice">我的足迹
				<c:if test="${singleMonitorCount > 0 }"><span class="feedCount"><span class="feedBorder">${singleMonitorCount}</span></span></c:if>
				</a>
			</li>
			<li <c:if test="${currTab eq 'monitorNotice'}">class="current"</c:if>>
				<a name="contacts" href="<vwb:Link context='notice' format='url'/>?func=monitorNotice">我的关注
				<c:if test="${singleMonitorCount > 0 }"><span class="feedCount"><span class="feedBorder">${singleMonitorCount}</span></span></c:if>
				</a>
			</li>
			<!--  
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
			-->
		</ul>
	</div>
	<div id="content-menu-body-container" class="lionContainer">
		<c:choose>
			<c:when test="${currTab eq 'collections'}">
				<jsp:include page="/jsp/aone/team/main/collections.jsp"/>
			</c:when>
			<c:when test="${(currTab == 'contacts') && (teamId != 1)}">
				<jsp:include page="/jsp/aone/team/main/contacts.jsp"/>
			</c:when>
			<c:when test="${currTab eq 'teamNotice'}">
				<jsp:include page="/jsp/aone/team/notice/singleTeamNotice.jsp"/>
			</c:when>
			<c:when test="${currTab eq 'personNotice'}">
				<jsp:include page="/jsp/aone/team/notice/singlePersonNotice.jsp"/>
			</c:when>
			<c:when test="${currTab eq 'monitorNotice'}">
				<jsp:include page="/jsp/aone/team/notice/singleMonitorNotice.jsp"/>
			</c:when>
			<c:when test="${currTab eq 'myUpdate'}">
				<jsp:include page="/jsp/aone/team/main/myUpdate.jsp"/>
			</c:when>
			<c:when test="${currTab eq 'historyNotice'}">
				<jsp:include page="/jsp/aone/team/notice/historyNotice.jsp"/>
			</c:when>
		</c:choose>
		
	</div>
	<div class="clear"></div>
