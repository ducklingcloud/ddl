<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />

<script type="text/javascript">
$(document).ready(function(){
	var switchTeamUrl = "<vwb:Link context='switchTeam' format='url'/>";
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
		$("#team-template").tmpl(object).prependTo("#teamDock");
		$("#team-quit-template").tmpl(object).appendTo("#existTeamDock");
		updateTeamMessageCount();
	};
	
	function updateTeamMessageCount(){
		replaceTopMessageCount(1);
		replaceLeftMessageCount(1);
		$("#top-focus-state").html($("#top-focus-state").html()-1);
	};
	
	$(".ignoreButton").live("click",function(){
		var url = site.getURL("invite",$(this).attr("inviteURL"))+"?func=ignore";
		$(this).parent().parent().remove();
		ajaxRequest(url,null,afterIgnoreInvitation);
	});
	
	function afterIgnoreInvitation(data){
		updateTeamMessageCount();
	};
	
	function replaceTopMessageCount(count){
		count=count?count:0;
		var newCount = $("#noticeCount").html() - count;
		if(newCount == 0)
			$("#userbox-right > ul.quickOp > li.notification").removeClass().addClass("icon notification msgCount0");
		$("#noticeCount").html(newCount);
	};
	
	function replaceLeftMessageCount(count){
		var newCount  = $("#top-focus-state").html() - 1;
		if(newCount>0)
			$("ul.ui-navList > li.current > a > span").html(newCount);
		else
			$("ul.ui-navList > li.current > a > span").remove();
	};
	
	function avoidSetBack() {
		$('#teamDock > li:nth-child(3n+1), #existTeamDock > li:nth-child(3n+1)').css('clear', 'both'); //to avoid set back by longer blocks
		$('#teamDock > li:nth-child(3n) > div.teamFeed').addClass('adjPos'); // to adjust pos of teamFeed float box
		$(".ui-dialog-cover").hide();
	}
	avoidSetBack();
	
	var floatBox;
	var floatBoxTimer;
	$('#teamDock > li').mouseenter(function(){
		var entity = $(this);
		floatBox = [$(this).attr('id'), true];
		floatBoxTimer = setTimeout(function(){
			if (floatBox[0]!='' && floatBox[1]==true) {
				entity.find('div.teamFeed').fadeIn();
			}
		}, 300);
	});
	$('#teamDock > li').mouseleave(function(){
		floatBox = ['', false];
		clearTimeout(floatBoxTimer);
		$(this).find('div.teamFeed').hide();
	});
	
	updateOneNotice = function(v){
		var li = $(v).parent().parent();
		var params = "func=updateOneNotice&teamId="+$(li).attr("tid")+"&messageType=Team"+"&eventId="+$(li).attr("eventid")+"&targetId="+$(li).attr("targetid");
		ajaxRequest(switchTeamUrl,params,function(){
			var a = $(v).attr("href");
			if(a==null||a==''||a=="undefined"){
				return ;
			}else{
				window.location.href=$(v).attr("href");
			}
		})
		return true;
	}
	
	$('#noTeamBox').delay(500).slideDown();
	
	$("#quit-team-button").click(function(){
		$('#feedSelector a, .content-menu-body').toggle();
	});
	$('#leave-quite-team').click(function(){
		$('#feedSelector a, .content-menu-body').toggle();
	});
	
	$(".select-team-to-quit").live("click",function(){
		$("input[name='teamName']").attr("value",$(this).attr("name"));
		$("#dialog-team-name").html($(this).parent().prev().html());
		ui_showDialog("quit-team-dialog", '', {
			pos:'belowAction',
			anchor: $(this)
		});
	});
	
	$("#quit-team-submit").click(function(){
		var url = "<vwb:Link format='url' context='quitteam'/>";
		$.ajax({
			type : 'post',
			data :$("#quit-team-form").serialize(),
			url : url+"?func=quitTeamValidate",
			dataType:"json",
			success :function(data){
				if(data.type=='onlyOneUser'){
					var t = window.confirm("团队只有您一位成员，退出团队后会自动删除团队，是否继续?");
					if(t){
						var params = $("#quit-team-form").serialize();
						ajaxRequest(url,params,afterQuitHandler);
					}
				}else if(data.type=='onlyOneAdmin'){
					alert("团队只有您一位管理员，请移交管理员后再退出团队。");
				}else if(data.type=='conferenceTeam'){
					alert("该团队为系统自动生成，如想退出，请到国际会议服务平台进行操作。");
				}else if(data.type=='success'){
					var params = $("#quit-team-form").serialize();
					ajaxRequest(url,params,afterQuitHandler);
				}
			}
		});
	});
	
	$(".ui-dialog-close.ui-text-small").click(function(){
		$(".ui-dialog-cover").hide();
	});
	$("span.ui-dialog-x").click(function(){
		$(".ui-dialog-cover").hide();
	});
	function afterQuitHandler(data){
		if(data.status=='success'){
			ui_hideDialog("quit-team-dialog");
			$('li[id='+ $('input[name="teamName"]').val()+']').fadeOut().remove();
			$('li[id="team_'+ data.tid +'"]').fadeOut().remove();
			$('#teamDock li, #existTeamDock li').css('clear', '');
			avoidSetBack();
		}
	};
	
	/** --------------------团队申请----------------------**/
	$("a.quitApplicantTeam").click(function(){
		var url = site.getURL("joinPublicTeam", null);
		var tid = $(this).attr("data-tid");
		$.ajax({
			url : url,
			type : 'POST',
			data:'func=cancelApply&teamId='+tid,
			success : function(data){
				data = JSON.parse(data);
				if(typeof(data.status) != 'undefined' && data.status){
					var $target = $('a.quitApplicantTeam[data-tid='+data.tid+"]");
					$target.closest("li").remove();
					alert("撤销申请成功！");
				}else{
					alert("撤销申请失败！");
				}
			},
			error:function(){
				alert("撤销申请失败！");
			},
			statusCode:{
				450:function(){alert('会话已过期,请重新登录');},
				403:function(){alert('您没有权限进行该操作');}
			}
		});
	});
	
	$("a.reApply").click(function(){
		var url = site.getURL("joinPublicTeam", null);
		var tid = $(this).attr("data-tid");
		window.location.href= url+"?func=join&teamId="+tid;
	});
	
	$("#applicantMessage").click(function(){
		$.ajax({
			url : site.getURL("joinPublicTeam", null),
			type : 'POST',
			data:{"func":"iknow"},
			success : function(data){
				data = JSON.parse(data);
				if(typeof(data.status) != 'undefined' && data.status){
					$("#applicantMessageDiv").remove();
				}else{
					alert("处理失败！");
				}
			},
			error:function(){
				alert("处理失败！");
			},
			statusCode:{
				450:function(){alert('会话已过期,请重新登录');},
				403:function(){alert('您没有权限进行该操作');}
			}
		});
	});
	/**--------------------------------------------------**/
	
});
</script>

<div id="feedSelector" class="filterHolder ">
<!--	<ul class="filter">
		<li class="chosen"><a view="byPerson" href="#">团队更新</a></li>
 	<li><a href="<vwb:Link context='teamHome' format='url'/>?func=switchTeam">团队列表</a></li>
	</ul>
-->
	<!-- <a class="ui-RTCorner largeButton dim" href="#" id="quit-team-button">- 退出团队</a> -->
	<a class="ui-RTCorner largeButton" href="#" id="leave-quite-team" style="display:none">返回</a>
	<a class="ui-RTCorner but-red" href="<vwb:Link format='url' context='joinPublicTeam'/>" id="join-team-button">+ 加入团队</a>
	<a class="ui-RTCorner  but-green " href="<vwb:Link format='url' context='createTeam'/>" id="create-team-button">+ 创建团队</a>
</div>

<div id="teams" class="content-menu-body" >
	<!-- 当前用户收到的邀请 -->
	<c:if test="${fn:length(invites)>0}">
		<c:forEach items="${invites}" var="item">
		<div class="msgBox">
			<h3>新邀请<span class="newTeam">&nbsp;</span></h3>
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
	<div id="new-accepted-invites" ></div>
	
	<!-- 当前用户的团队申请记录消息 -->
	<c:if test="${applicantMessage}">
		<div id="applicantMessageDiv">
			<c:if test="${rejectApplicants!=null && rejectApplicants!='' }">
				<span>您申请加入团队 “ ${rejectApplicants } ” 的请求已被拒绝！</span>
			</c:if>
			<c:if test="${waitingApplicants!=null && waitingApplicants!='' }">
				<span>您申请加入团队 “ ${waitingApplicants } ” 的请求已发送给管理员！</span>
			</c:if>
			<a id="applicantMessage" class="clear-one-team largeButton dim small">我知道了</a>
		</div>
	</c:if>
	
	<ul id="teamDock">
	<c:forEach items="${TeamMap}" var="item">
		<li id="${item.value.name}" class="exist-team">
			<c:choose>
				<c:when test="${TeamCountMap[item.key] != 0}">
					<a class="wholeLink" href="<vwb:Link format='url' context='switchTeam' page=""/>?func=forward&team=${item.value.name}&tab=teamNotice">
				</c:when>
				<c:otherwise>
					<a class="wholeLink" href="<vwb:Link format='url' context='switchTeam' page=""/>?func=jump&team=${item.value.name}">
				</c:otherwise>
			</c:choose>
			<div class="teamIcon"></div>
			<div class="teamInfo">
				<h3><c:out value="${item.value.displayName}"/>
				</h3>
				<%-- <c:choose>
					<c:when test="${!empty teamCreatorInfos[item.value.id]}">
						<p class="creatorInfo">${teamCreatorInfos[item.value.id].name}创建于${item.value.createTime}</p>
					</c:when>
				</c:choose> --%>
				<c:if test="${TeamCountMap[item.key] != 0}">
					<div class="teamUpdate">最近更新：<span class="feedCount cold"><span class="feedBorder coldBlue">${TeamCountMap[item.key]}</span></span></div>
				</c:if>
				
				<c:if test="${!empty userAdminTeam[item.value.id]}">
					<p class="manager">管理员</p>
				</c:if>
				
			</div>
			<c:if test="${!empty userAdminTeam[item.value.id]}">
				<span class="manager" href="<vwb:Link format='url' context='configTeam' page='' jsp='${item.value.name}'/>"></span>
			</c:if>
			</a>
			<c:if test="${TeamCountMap[item.key] != 0 }">
				<c:set var="noticeList" value="${TeamNoticeMap[item.key]}" scope="request" />
				<c:set var="resourceMap" value="${resourceMapMap[item.key]}" scope="request" />
				<c:if test="${not empty noticeList }">
				<div class="teamFeed teamFeedList">
					<div class="teamFeed_pointer"></div>
					<jsp:include page="/jsp/aone/team/notice/simpleNoticeDisplay.jsp"></jsp:include>
					<p class="moreFeed ui-text-note"><a href="<vwb:Link format='url' context='switchTeam' page=''/>?func=forward&team=${item.value.name}&tab=teamNotice">查看全部更新>></a></p>
				</div>
				</c:if>
			</c:if>
			
			
		</li>
	</c:forEach>
	<c:forEach items="${applicantTeams }" var="team">
		<li class="applicant-team">
			<div class="teamIcon"></div>
			<div class="teamInfo">
				<h3><c:out value="${team.displayName}"/></h3>
			</div>
			<c:if test="${team.status == 'waiting' }">
			<div class="appInfo">
				<span>审核中</span>
				<a class="quitApplicantTeam" data-tid="${team.id}">撤销申请</a>
			</div>
			</c:if>
			<c:if test="${team.status == 'reject' }">
			<div class="appInfo">
				<span>申请被拒绝</span>
				<a class="reApply" data-tid="${team.id}">重新申请</a>
				<br><a class="quitApplicantTeam" data-tid="${team.id}">撤销申请</a>
			</div>
			</c:if>
		</li>
	</c:forEach>
		<li class="createTeam isHighLight">
			<a href="<vwb:Link format='url' context='createTeam'/>">
				<div class="createTeamIcon">+</div>
				<div class="teamInfo"><h3>创建团队</h3></div>
			</a>
		</li>
		<li class="createTeam isHighLight">
			<a href="<vwb:Link format='url' context='joinPublicTeam'/>">
				<div class="createTeamIcon">+</div>
				<div class="teamInfo"><h3>加入公开团队</h3></div>
			</a>
		</li>
		<div class="clear"></div>
		<a href="#" id="quit-team-button" class="quit-team-link">点此退出团队</a>
	</ul>
	<div class="bedrock"></div>
</div>

<div id="quitTeam" class="content-menu-body" style="display:none;">
	<ul id="existTeamDock">
		<c:forEach items="${TeamMap}" var="item">
			<li id="${item.value.name}">
				<div class="teamIcon"></div>
				<div class="teamInfo">
					<h3><c:out value="${item.value.displayName}"/></h3>
					<p><a href="javascript:void(0)" class="largeButton alert select-team-to-quit" name="${item.value.name}">退出</a></p>
				</div>
			</li>
		</c:forEach>
	</ul>
	<div class="bedrock"></div>
</div>

<div class="ui-dialog-cover"></div>
<div class="ui-dialog" id="quit-team-dialog" style="width:400px;">
	<span class="ui-dialog-x"></span>
	<p class="ui-dialog-title">退出团队</p>
	<form id="quit-team-form" action="<vwb:Link context='quitTeam' format='url'/>" method="POST">
		<div class="ui-dialog-body">
			<input type="hidden" name="teamName" value=""/>
			<p class="ui-text-large">您确定要退出“<span id="dialog-team-name" class="ui-text-strong"></span>”团队吗？</p>
			<p class="ui-text-note">退出后，您将无法使用团队中的资源。</p>
			<p class="ui-text-note">如需重新加入团队，您需要联系团队管理员给您发送新的邀请。</p>
		</div>
		<div class="ui-dialog-control">
			<input type="button" id="quit-team-submit" value="确定退出"/>
			<a class="ui-dialog-close ui-text-small">取消</a>
		</div>
	</form>
</div>

<script id="accepted-invite-template" type="text/html">
	<p class="msgBox">已接受{{= inviterName}}的邀请,并成功加入到{{= teamDisplayName}}({{= teamName}})中。</p>
</script>

<script id="team-template" type="text/html">
		<li id="{{= teamName}}" class="teamRecord">
			<a href="<vwb:Link format='url' context='switchTeam' page=""/>?func=forward&team={{= teamName}}&tab=${currTab}">
			<div class="teamIcon"></div>
			<div class="teamInfo">
				<h3>{{= teamDisplayName}}</h3>
				<p class="ui-text-note">({{= teamName}})</p>
			</div>
			</a>
		</li>
</script>

<script id="team-quit-template" type="text/html">
	<li id="{{= teamName}}">
			<div class="teamIcon"></div>
			<div class="teamInfo">
				<h3>{{= teamDisplayName}}</h3>
				<p><a href="javascript:void(0)" class="largeButton alert select-team-to-quit" name="{{= teamName}}">退出</a></p>
			</div>
	</li>
</script>
<script type="text/javascript">
	$(document).ready(function(){
		$("ul#teamDock > li > a.wholeLink").mouseenter(function(){
			$(this).children("span.manager").show();
		})
		$("ul#teamDock > li > a.wholeLink").mouseleave(function(){
			$(this).children("span.manager").hide();
		})
		$("ul#teamDock > li > a > span.manager").click(function(e){
			e.stopPropagation();
			e.preventDefault();  
			window.location.href=$(this).attr('href');
		});
		
	})
</script>
