<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<fmt:setBundle basename="templates.default" />
<script type="text/javascript"  src="${contextPath}/scripts/jquery/jquery.tmpl.js"></script>

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

<div id="content-title">
	<div class="ui-RTCorner">
		<a id="howto" class="ui-iconButton help">如何使用科研在线</a>
		<div id="msg-howto" class="msgFloat" style="left:30%; width:40em; text-indent:2em; ">
			<p>科研在线是一个支持科研团队中，内容发布、组织、分享和沟通的系统。因此首先，您需要一个<strong>团队</strong>。</p>
			<p>您可以创建团队：创建团队后，您可以通过系统向同事或朋友发送邀请，请他们加入您的团队。您也可以等待同事给您发送邀请，并加入他们的团队。在团队中，您将可以和团队成员共同分享内容。</p>
			<p>进入团队看到的第一个页面“首页”，是一个信息中心，您可以从这里看到团队每天的变化、其他成员给您的信息；您也可以通过自己最近创建或修改过的页面、团队中所有的集合和快捷方式等，快速找到需要的内容；同时，您也可以在这里查看团队通讯录，毕竟有时候电话比网络更高效。</p>
			<hr/>
			<p>在一个新的团队中，通常您需要创建一些<strong>集合</strong>，它们相当于文件夹，可以分门别类地存放文档、文件。</p>
			<p>科研在线强调开放、共享和协作，因此默认情况下，所有人都可以在集合中浏览、添加和修改内容。但我们也为团队运作的需要而设计了<strong>权限</strong>，您可以使一个集合只有指定的成员才能编辑内容，而其他所有人都可以查看，例如实验室的规章制度；也可以设置一些只有指定成员才能查看和编辑的集合，用于存放一些较为敏感的内容。</p>
			<hr/>
			<p>拥有集合之后，您就可以向集合里添加内容了。在集合中编写内容，就像用Word写一篇文章，或是写一篇博客。编写过程中，您可以上传图片和文件，也可以根据需要用超链接的方式引用其它的内容或文章，用作解释说明、或者额外的补充。例如，在编写了一个项目的总体规划之后，您可以添加几个描述单独子任务的页面。</p>
			<p>当团队中有了成员和内容，您可以通过“关注”、“分享”、“留言”等方式与团队成员们进行分享和沟通，感受新型的团队协作。</p>
			<hr/>
			<p>这里只是对科研在线核心功能的一个简单说明。更多的功能、使用方法（例如建立知识网络、编制设备预约和使用记录、考勤等等），正等着您带着智慧的眼睛去体验和发现。</p>
			<p class="ui-alignCenter"><a href="/dct/help/tutorial.jsp" target="_blank">&gt;&gt;&nbsp;更多应用场景</a></p>
			<a class="ui-RTCorner msg-close">关闭</a>
		</div>
	</div>
	<h1>我的团队</h1>
</div>

<div id="content-major">
	<!-- 当前用户收到的邀请 -->
	<c:if test="${fn:length(invites)>0}">
		<c:forEach items="${invites}" var="item">
		<div class="msgBox">
			<h3>新邀请</h3>
			<p><strong>${item.inviterName}</strong>邀请您加入团队<strong>${item.teamDisplayName}</strong>（${item.teamName}）。</p>
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
				<span class="displayName">${item.displayName}</span><br/>
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
<div id="content-side">
	<div class="sideBlock">
		<h4>个人信息</h4>
		<ul class="fileList">
			<li>
				<a href="<vwb:Link context='user' format='url' page="${userExtInfo.id}"/>">我的资料</a>
				（<a href="<vwb:Link context='user' page='${userExtInfo.id}' format='url'/>?func=editProfile"><span>编辑</span></a>）
			</li>
			<li><a href="${changePasswordURL}">修改密码</a></li>
		</ul>
	</div>
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

