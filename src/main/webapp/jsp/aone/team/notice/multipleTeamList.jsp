<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
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

	$(".acceptButton").live("click",function(){
		var url = site.getURL("invite",$(this).attr("inviteURL"))+"?func=accept";
		$(this).parent().parent().remove();
		ajaxRequest(url,null,afterAcceptInvitation);
	});
	
	function afterAcceptInvitation(data){
		var object = new Object();
		object["teamName"] = data.teamName;
		object["teamDisplayName"] = data.teamDisplayName;
		object["inviterName"] = data.inviterName;
		$("#noTeamBox").remove();
		$("#accepted-invite-template").tmpl(object).appendTo("#new-accepted-invites");
		$("#team-template").tmpl(object).prependTo("#my-teams");
	};
	
	$(".ignoreButton").live("click",function(){
		var url = site.getURL("invite",$(this).attr("inviteURL"))+"?func=ignore";
		$(this).parent().parent().remove();
		ajaxRequest(url,null,afterIgnoreInvitation);
	});
	
	function afterIgnoreInvitation(data){
		//TODO 
	};
	
	$('#howto').click(function(){ $('#msg-howto').toggle(); });
	$('#whyteam').click(function(){ $('#msg-whyteam').toggle(); });
	$('.msgFloat .msg-close').click(function(){ $(this).parent().hide(); });
	$('#my-teams li:nth-child(4n+1)').css('clear', 'both'); //to avoid set back by longer blocks
	$('#noTeamBox').delay(500).slideDown();
	
});
</script>

<div id="feedSelector" class="filterHolder">
	<ul class="filter">
		<li><a href="<vwb:Link context='teamHome' format='url'/>?func=teamNotice">团队更新</a></li>
		<li class="chosen"><a href="#">团队列表</a></li>
	</ul>
</div>

<div id="newsfeed-time" class="content-menu-body" >
	<!-- 当前用户收到的邀请 -->
	<c:if test="${fn:length(invites)>0}">
		<c:forEach items="${invites}" var="item">
		<div class="msgBox">
			<h3>新邀请</h3>
			<p><strong>${item.inviterName}</strong>邀请您加入团队<strong><c:out value="${item.teamDisplayName}"></c:out></strong>（${item.teamName}）。</p>
			<c:if test="${not empty item.message}">
				<p>[${item.message}]</p>
			</c:if>
			<div class="control">
				<input inviteURL="${item.displayURL}" type="button" class="largeButton acceptButton" value="接受邀请"/>
				<input inviteURL="${item.displayURL}" type="button" class="largeButton dim ignoreButton" value="忽略" />
			</div>
		</div>
		</c:forEach>
	</c:if>
	<!-- 当前用户已接受的邀请 -->
	<div id="new-accepted-invites" >
	</div>
	
	<!-- 当前用户参加的团队列表 -->
	<c:if test="${empty myTeams}">
		<div class="msgBox" id="noTeamBox" style="display:none">
			<p>您当前没有加入任何团队。</p>
			<a class="ui-RTCorner ui-iconButton help" id="whyteam">为什么要创建或加入团队？</a>
			<p>请<strong>创建自己的团队</strong>，并邀请朋友和同事加入。</p>
			<div class="pointDown"></div>
		</div>
		<div id="msg-whyteam" class="msgFloat" style="left:30%; width:30em; text-indent:2em; ">
			<p>科研在线是一个支持科研团队协作的系统，通过实现内容发布、组织、分享和沟通而展现价值。</p>
			<p>因此首先，您需要一个<strong>团队</strong>。</p>
			<a class="ui-RTCorner msg-close">关闭</a>
		</div>
	</c:if>
	<ul id="my-teams" class="teamDock">
	<c:forEach items="${myTeams}" var="item">
		<li teamid="${item.id}" class="teamRecord">
			<a href="<vwb:Link format='url' context='switchTeam' page=""/>?func=jump&team=${item.name}">
				<div class="teamIcon"></div>
				<span class="displayName"><c:out value="${item.displayName}"/></span><br/>
				<span class="teamName">${item.name}</span>
			</a>
		</li>
	</c:forEach>
		<li class="createTeam">
			<a href="<vwb:Link format='url' context='createTeam'/>">
				<div class="createTeamIcon">+</div>
				<span class="displayName">创建团队</span>
			</a>
		</li>
	</ul>
	<div class="bedrock"></div>
</div>

<div class="ui-clear"></div>

<div id="rookieGuide" style="display:none">
	<div class="guideTitle">新手帮助</div>
	<div class="guideBody">
		你需要1234
	</div>
</div>

<style>
#footer { margin-bottom:230px; }
</style>

<script id="accepted-invite-template" type="text/html">
	<p class="msgBox">已接受{{= inviterName}}的邀请,并成功加入到{{= teamDisplayName}}({{= teamName}})中。</p>
</script>

<script id="team-template" type="text/html">
	<li teamid="{{= teamName}}" class="teamRecord">
		<a href="<vwb:Link format='url' context='switchTeam' />?func=jump&team={{= teamName}}">
			<div class="teamIcon"></div>
			<span class="displayName">{{= teamDisplayName}}</span><br/>
			<span class="teamName">{{= teamName}}</span>
		</a>
	</li>
</script>

