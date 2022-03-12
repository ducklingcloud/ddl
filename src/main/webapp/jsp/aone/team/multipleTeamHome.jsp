<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<%
	request.setAttribute("contextPath", request.getContextPath());
%>

<script type="text/javascript">
$(document).ready(function(){
	$('td.feedList ul:not(ul.a1-subs-list) li:nth-child(even)').addClass('striped');
	$("#mask_home_1").css({
		"width":$(document.body).outerWidth(),
		"top":0 - $("#body.ui-wrap.wrapper1280").offset().top,
		"left":0 - $("#body.ui-wrap.wrapper1280").offset().left -12,
	});
	// intro steps begin
	var coverStyle = setInterval(function(){
		if($(document.body).outerHeight() > $(window).height()){ 
			$("#mask_home_1").css({
				"height":$(document.body).outerHeight(),
			});
		}
		else{
			$("#mask_home_1").css({
				"height":window.innerHeight,
			});
		}
	},20);
	
	$("#macro-innerWrapper").css({"z-index":"51"});
	var step;
	var totalStep = 1;
	$.ajax({
		url:site.getURL('userguide',null),
		type:'POST',
		data:"func=get&module=dashboard",
		success:function(data){
			data = eval("("+data+")");
			step = data.step;
			if(step < totalStep) {
				showTheVeryStep(step);
			}
		},
		error:function(){
			step = 0;
		},
		statusCode:{
			450:function(){alert('会话已过期,请重新登录');},
			403:function(){alert('您没有权限进行该操作');}
		}
	});
	
	var count = 1;
	function showTheVeryStep(step){
		$("#mask_home_1,.closeGuide").show();
		if(step==0){
			$("#intro_home_" + count).show();
		}
		else {
			$("#intro_home_" + (step+1)).show();
		}
		
		/* $(".isHighLight").removeClass("isHighLight");
		$(".readyHighLight" + count).addClass("isHighLight"); */
	}
	
	$(".closeGuide,.Iknow").click(function(){
		$("#mask_home_1,#intro_home_1").hide();
		$(".isHighLight").removeClass("isHighLight");
		/* step = totalStep; */
		postStep(1);
	});
	
	function postStep(step){
		$.ajax({
			//url:site.getURL('tag',null),
			url:site.getURL('userguide',null),
			type:'POST',
			data:"func=update&module=dashboard&step="+step,
			success:function(data){},
			error:function(){},
			statusCode:{
				450:function(){alert('会话已过期,请重新登录');},
				403:function(){alert('您没有权限进行该操作');}
			}
		});
	}
	
	// intro steps end
});
</script>
	<div id="content-menu">
		<p class="personal-space-title userName">${currentUserName}</p>
		<ul class="myNavList maR0">
			<li <c:if test="${(currTab eq 'teamNotice') or (currTab eq 'switchTeam')}">class="current"</c:if>>
				<a  href="<vwb:Link context='dashboard' format='url'/>?func=teamNotice">我的团队
				<c:if test="${teamCount != 0 }"><span class="feedCount cold"><span class="feedBorder coldBlue">${teamCount}</span></span></c:if>
				<c:if test="${teamInvites > 0 }"><span class="feedCount newTeam" title="${teamInvites}个新邀请"><span class="feedBorder">${teamInvites}</span></span></c:if></a>
			</li>
			<li <c:if test="${currTab eq 'personNotice'}">class="current"</c:if>>
				<a  href="<vwb:Link context='dashboard' format='url'/>?func=personNotice">消息
				<c:if test="${personCount != 0 }"><span class="feedCount"><span class="feedBorder">${personCount}</span></span></c:if></a>
			</li>
			<li <c:if test="${currTab eq 'monitorNotice'}">class="current"</c:if>>
				<a  href="<vwb:Link context='dashboard' format='url'/>?func=monitorNotice">关注
				<c:if test="${monitorCount != 0 }"><span class="feedCount"><span class="feedBorder">${monitorCount}</span></span></c:if></a>
			</li>
			
		</ul>
	<form> <fieldset><legend >我的应用</legend></fieldset></form>
		<ul class="myNavList maR0">
			<li <c:if test="${currTab eq 'capture'}">class="current"</c:if>>
				<a name="webpageCapture" href="<vwb:Link context='dashboard' format='url'/>?func=capture" title="新功能">网页收藏</a>
				<div class="newFunc" title="新功能"></div>
			</li>
		</ul>
		<form> <fieldset><legend >设置</legend></fieldset></form>
		<ul class="myNavList maR0">
			<li <c:if test="${currTab eq 'profile'}">class="current"</c:if>>
				<a  href="<vwb:Link context='dashboard' format='url'/>?func=profile">个人资料</a>
			</li>
			<li <c:if test="${currTab eq 'preferences'}">class="current"</c:if>>
				<a  href="<vwb:Link context='dashboard' format='url'/>?func=preferences">个人偏好</a>
			</li>
			<li <c:if test="${currTab eq 'noticeEmail'}">class="current"</c:if>>
				<a  href="<vwb:Link context='dashboard' format='url'/>?func=noticeEmail">邮件通知</a>
			</li>
		</ul>
	</div>

	<div id="content-menu-body-container">
		<c:choose>
			<c:when test="${currTab eq 'contacts'}">
				<jsp:include page="/jsp/aone/user/personContacts.jsp"/>
			</c:when>
			<c:when test="${currTab eq 'teamNotice'}">
				<jsp:include page="/jsp/aone/team/notice/multipleTeamNotice.jsp"/>
			</c:when>
			<c:when test="${currTab eq 'personNotice'}">
				<jsp:include page="/jsp/aone/team/notice/multiplePersonNotice.jsp"/>
			</c:when>
			<c:when test="${currTab eq 'monitorNotice'}">
				<jsp:include page="/jsp/aone/team/notice/multipleMonitorNotice.jsp"/>
			</c:when>
			<c:when test="${currTab eq 'historyNotice'}">
				<jsp:include page="/jsp/aone/team/notice/multipleHistoryNotice.jsp"/>
			</c:when>
			<c:when test="${currTab eq 'switchTeam'}">
				<jsp:include page="/jsp/aone/team/notice/multipleTeamList.jsp"/>
			</c:when>
			<c:when test="${currTab eq 'profile'}">
				<jsp:include page="/jsp/aone/team/profile/viewProfile.jsp"/>
			</c:when>
			<c:when test="${currTab eq 'capture'}">
				<jsp:include page="/dataCollect/dataCollectIntro.jsp"/>
			</c:when>
			<c:when test="${currTab eq 'preferences'}">
				<jsp:include page="/jsp/aone/team/preferences.jsp"/>
			</c:when>
			<c:when test="${currTab eq 'noticeEmail'&&((allChecked||allUnChecked)&&empty showDetailNoticeEmail)}">
				<jsp:include page="/jsp/aone/team/noticeEmailAll.jsp"/>
			</c:when>
			<c:when test="${currTab eq 'noticeEmail'&&((!allChecked&&!allUnChecked)||!empty showDetailNoticeEmail)}">
				<jsp:include page="/jsp/aone/team/noticeEmailDetail.jsp"/>
			</c:when>
		</c:choose>
		
		<div id="intro_home_1" class="intro_step">
			<a class="Iknow" id="Iknow_home_1">我知道了</a>
			<a class="closeGuide"></a>
		</div>
			
	</div>
	<div class="clear"></div>
	<div id="mask_home_1" class="intro_mask"></div>
