<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<fmt:setBundle basename="templates.default" />
<div id="userbox-right" class="ui-RTCorner">
		<vwb:UserCheck status="authenticated">
		<ul class="quickOp">
			<li class="divider"></li>
			
		 	<vwb:MessageCount/>
		 	<li class="icon notification msgCount${totalCount}">
				<a href="javascript:void(0)"><span id="noticeCount" class="msgCount${totalCount}">${totalCount}</span>通知</a>
		 	</li>
			<li class="icon team">
		 		<a href="javascript:void(0)">团队</a>
		 	</li>
		 	<li class="icon home"><a href="<vwb:Link format='url' context='switchTeam' page=''/>">我的空间</a></li>
		 	<li class="icon userMe">
		 		<a href="javascript:void(0)"><vwb:UserName /></a>
			</li>
		</ul>
		<div id="teamMenu" class="pulldownMenu">
			<ul>
			<vwb:TeamPreferences/>
	 		<c:forEach items="${myTeamList}" var="item">
	 			<li><a href='<vwb:Link context="switchTeam" page="" format="url"/>?func=jump&team=${item.name}'>${item.displayName}</a></li>
	 		</c:forEach>
		 	</ul>
		</div>
		<div id="noticeMenu" class="pulldownMenu" style="width:120px;">
			<ul>
				<li><a href="<vwb:Link context='switchTeam' format='url'/>?func=home&tab=teamNotice">团队邀请<span id="top-focus-state" class="msgCount${teamInvites} count">${teamInvites}</span></a></li>
		 		<li><a href="<vwb:Link context='switchTeam' format='url'/>?func=home&tab=personNotice">我的消息<span id="top-recommend-count" class="msgCount${personCount} count">${personCount}</span></a></li>
		 		<li><a href="<vwb:Link context='switchTeam' format='url'/>?func=home&tab=monitorNotice">我的关注<span id="top-focus-state" class="msgCount${monitorCount} count">${monitorCount}</span></a></li>
		 	</ul>
		</div>
		<div id="userMenu" class="pulldownMenu">
			<ul>
				<li><a href="<vwb:Link context="switchTeam" format='url'/>?func=home&tab=profile">个人资料</a></li>
				<li><a href="<vwb:Link context="logout" format='url'/>"
					title="<fmt:message key='actions.logout.title'/>"><fmt:message
						key="actions.logout" /></a>
				</li>
			</ul>
		</div>
		
		
		</vwb:UserCheck>
		<vwb:UserCheck status="anonymous">
		<ul>
			<li>
				<fmt:message key="fav.greet.anonymous" />
			</li>
			<li>
				<a href="<vwb:Link context="login" format='url'/>"
					class="action login"
					title="<fmt:message key='actions.login.title'/>"><fmt:message
						key="actions.login" /> </a>
			</li>
			<li>
				<a href="<vwb:Link context='regist' format='url' absolute='true'/>" target="_blank"><fmt:message
						key="actions.register" /> </a>
			</li>
		</ul>
		</vwb:UserCheck>
</div>
