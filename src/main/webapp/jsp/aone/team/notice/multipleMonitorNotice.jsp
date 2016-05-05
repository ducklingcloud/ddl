<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<script type="text/javascript">
$(document).ready(function(){
	$('.delete-feed-button').live('click', function(){
		$('span#pageName').text($(this).next().text());
		$('input#confirmCancel').attr('requestURL', $(this).attr("url"));
		ui_showDialog('cancelSubscription');
	});
	
	$("input#confirmCancel").live('click',function(){
		ajaxRequest($(this).attr('requestURL'),null,afterRemoveFeedRecord);
	});
	
	function afterRemoveFeedRecord(data){
		ui_hideDialog('cancelSubscription');
		$('input#confirmCancel').attr('requestURL', '');
		$('input#confirmCancel').attr('name', '');
		
		var ul = $("li[feedId='"+data.feedId+"']").parent();
		$("li[feedId='"+data.feedId+"']").remove();
		 
		if (ul.find('li').size()==0) {
			if (ul.attr('id')=='pageFeedList') {
				ul.after('<p class="a1-feed-none">没有关注的页面。</p>');
			}
			else {
				ul.after('<p class="a1-feed-none">没有关注的成员。</p>');
			}
			ul.remove();
		}
	};
	
	/* switch */
	function switchView(VIEW) {
		$('#feedSelector ul.filter > li').removeClass('chosen');
		$('.content-menu-body').fadeOut();
		var view = VIEW;
		
		switch (VIEW) {
			case 'follow':
				$('#newsfeed-follow').fadeIn();
				break;
			case 'update':
			default:
				view = 'update';
				$('#newsfeed-update').fadeIn();
		}
		$('#feedSelector ul.filter a[view="' + view + '"]').parent().addClass('chosen');
	};
	
	$('#feedSelector ul.filter a').click(function(){
		switchView($(this).attr('view'));
	});
	/* initiate */
	var hash = (window.location.hash)?window.location.hash:'';
	if (hash!='') {
		switchView(hash.substring(1));
	}
	else {
		switchView();
	};
	
	var url = "<vwb:Link context='switchTeam' format='url'/>";
	$("#know-all").click(function(){
		var params = "func=updateAllNotice&messageType="+$("#messageType").attr("value");
		ajaxRequest(url,params,updateAll);
	});
	
	function updateAll(data){
		replaceTopMessageCount($("ul.ui-navList > li.current > a > span").html());
		$("#top-focus-state").html("0");
		$("ul.ui-navList > li.current > a > span").remove();
		$("td.teamBlock > div").remove();
		$("td.feedList").html("<p class='a1-feed-none'>没有相关记录</p>");
		$("input.clear-one-team").remove();
	};
	
	updateOneNotice = function(v){
		var li = $(v).parent().parent();
		var params = "func=updateOneNotice&teamId="+$(li).attr("tid")+"&messageType=Monitor"+"&eventId="+$(li).attr("eventid")+"&targetId="+$(li).attr("targetid");
		ajaxRequest(url,params,function(){
			var a = $(v).attr("href");
			if(a==null||a==''||a=="undefined"){
				return ;
			}else{
				window.location.href=$(v).attr("href");
			}
		})
		return true;
	}
	
	$(".clear-one-team").live('click',function(){
		var params = "func=updateNotice&teamId="+$(this).attr("tid")+"&messageType=Monitor";
		$(this).remove();
		ajaxRequest(url,params,updateOneTeam);
	});
	
	function updateOneTeam(data){
		var count = $("#team-"+data.tid).find("td > div >span.feedBorder").html();
		replaceTopMessageCount(count);
		replaceLeftMessageCount(count);
		$("#team-"+data.tid).find("td > div").remove();
		$("#team-"+data.tid).find("td.feedList").html("<p class='a1-feed-none'>没有相关记录</p>");
	};
	
	function replaceTopMessageCount(count){
		count=count?count:0;
		var newCount = $("#noticeCount").html() - count;
		if(newCount == 0)
			$("#userbox-right > ul.quickOp > li.notification").removeClass().addClass("icon notification msgCount0");
		$("#noticeCount").html(newCount);
	};
	
	function replaceLeftMessageCount(count){
		var newCount  = $("#top-focus-state").html() - count;
		$("#top-focus-state").html(newCount);
		if(newCount>0)
			$("ul.ui-navList > li.current > a > span").html(newCount);
		else
			$("ul.ui-navList > li.current > a > span").remove();
	};
	function onView(){
		if($("div.feedCount").length>0){
			var v = $("div.feedCount").first().parent();
			var height = $(v).offset().top;
			console.log(height);
			if(height>200){
				height = height-200;
			}
			window.scrollTo(0,height);
		}
	}
	onView();
});
</script>

<div id="feedSelector" class="filterHolder">
	<ul class="filter">
		<li class="chosen"><a view="update" href="#update">最近更新</a></li>
		<li><a view="follow" href="#follow">关注列表</a></li>
	</ul>
</div>
	

<div id="newsfeed-update" class="content-menu-body" style="display:none" >
	<input type="hidden" id="messageType" value="monitor"/>
	<table class="teamDivision dataTable merge">
	<thead>
		<tr>
			<td colspan="3" class="dtRight"><a id="know-all" class="but-color">全知道了</a></td>
		</tr>
	</thead>
	<c:forEach items="${TeamMap}" var="item">
	<tr id="team-${item.value.id}">
		<td class="teamBlock">
		    <h3>
			<a href="<vwb:Link format='url' context='switchTeam' page=""/>?func=forward&team=${item.value.name}&tab=${currTab}">
				<c:out value="${item.value.displayName}"/>
			</a>
			</h3>
			<c:if test="${MonitorCountMap[item.key] != 0}">
				<div class="feedCount"><span class="feedBorder">${MonitorCountMap[item.key]}</span></div>
			</c:if>
			
			<p class="ui-text-note">(${item.value.name})</p>
		</td>
		<td class="feedList">
			<c:set var="noticeList" value="${MonitorNoticeMap[item.key]}" scope="request" />
			<c:set var="resourceMap" value="${resourceMapMap[item.key]}" scope="request" />
			<jsp:include page="/jsp/aone/team/notice/simpleNoticeDisplay.jsp"></jsp:include>
			<p class="moreFeed"><a href="<vwb:Link format='url' context='switchTeam' page=''/>?func=forward&team=${item.value.name}&tab=monitorNotice">更多关注内容&gt;&gt;</a></p>
		</td>
		<td class="dtRight" style="padding-top:10px;with:65px;white-space: nowrap;">
			<c:if test="${fn:length(noticeList) != 0}">
				<a  class="but-color clear-one-team"  tid="${item.value.id}">知道了</a>
			</c:if>
		</td>
	</tr>
	</c:forEach>
	</table>
</div>

<div id="newsfeed-follow" class="content-menu-body" style="display:none" >
	<table class="teamDivision dataTable merge" >
	<c:forEach items="${TeamMap}" var="item">
	<tr>
		<td class="teamBlock">
			<h3>
			<a href="<vwb:Link format='url' context='switchTeam' page=""/>?func=jump&team=${item.value.name}">
				<c:out value="${item.value.displayName}"/>
			</a>
			</h3>
			<p class="ui-text-note">(${item.value.name})</p>
		</td>
		<td class="feedList light">
		<p class="ui-text-strong">页面</p>
		<c:choose>
			<c:when test="${empty pageFeedMap[item.value.id] }">
				<p class="a1-feed-none">没有关注的页面。</p>
			</c:when>
			<c:otherwise>
				<ul class="a1-subs-list" id="pageFeedList">
				<c:forEach items="${pageFeedMap[item.key]}" var="feedItem">
					<li feedId="${feedItem.id}">
<%-- 						<a class="a1-subs-del delete-feed-button" url="<vwb:Link context='feed' format='url'/>?func=removeFeedByID&feedId=${feedItem.id}" title="取消关注"></a> --%>
						<a href="${feedItem.publisher.url}" >${feedItem.publisher.name}</a>
					</li> 
				</c:forEach>
				</ul>
				<div class="ui-clear"></div>
			</c:otherwise>
		</c:choose>
		</td>
		
		<td class="feedList light">
		<p class="ui-text-strong">成员</p>
		<c:choose>
			<c:when test="${empty personFeedMap[item.key] }">
				<p class="a1-feed-none">没有关注的成员。</p>
			</c:when>
			<c:otherwise>
				<ul class="a1-subs-list" id="personFeedList">
				<c:forEach items="${personFeedMap[item.key]}" var="feedItem">
					<li feedId="${feedItem.id}">
<%-- 						<a class="a1-subs-del delete-feed-button" url="<vwb:Link context='feed' format='url'/>?func=removeFeedByID&feedId=${feedItem.id}" title="取消关注"></a> --%>
						<a href="<vwb:Link context='user' page='${feedItem.publisher.id}' format='url'/>" >${feedItem.publisher.name}</a>
					</li> 
				</c:forEach>
				</ul>
				<div class="ui-clear"></div>
			</c:otherwise>
		</c:choose>
		</td>
	</tr>
	</c:forEach>
	</table>
</div>


