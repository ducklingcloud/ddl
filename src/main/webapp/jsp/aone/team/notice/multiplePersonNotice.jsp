<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<script type="text/javascript">
$(document).ready(function(){
	
	var url = "<vwb:Link context='switchTeam' format='url'/>";
	$("#know-all").click(function(){
		var params = "func=updateAllNotice&messageType="+$("#messageType").attr("value");
		ajaxRequest(url,params,updateAll);
	});
	
	function updateAll(data){
		replaceTopMessageCount($("#noticeCount").html());
		$("#top-recommend-count").html("0");
		$("ul.myNavList > li.current > a > span").remove();
		$("td.teamBlock > div").remove();
		$("td.feedList").html("<p class='a1-feed-none'>最近没有新的记录</p>");
		$("input.clear-one-team").remove();
	};
	
	$(".clear-one-team").live('click',function(){
		var params = "func=updateNotice&teamId="+$(this).attr("tid")+"&messageType=Person";
		$(this).remove();
		ajaxRequest(url,params,updateOneTeam);
	});
	
	updateOneNotice = function(v){
		var li = $(v).parent().parent();
		var params = "func=updateOneNotice&teamId="+$(li).attr("tid")+"&messageType=Person"+"&eventId="+$(li).attr("eventid")+"&targetId="+$(li).attr("targetid");
		ajaxRequest(url,params,function(){
			var a = $(v).attr("href");
			if(a==null||a==''||a=="undefined"){
				return ;
			}else{
				window.location.href=$(v).attr("href");
			}
		})
		return false;
	}
	
	function updateOneTeam(data){
		var count = $("#team-"+data.tid).find("td > div  >span.feedBorder").html();
		replaceTopMessageCount(count);
		replaceLeftMessageCount(count);
		$("#team-"+data.tid).find("td > div").remove();
		$("#team-"+data.tid).find("td.feedList").html("<p class='a1-feed-none'>最近没有新的记录</p>");
	};
	
	function replaceTopMessageCount(count){
		count = count?count:0;
		var newCount = $("#noticeCount").html() - count;
		if(newCount == 0)
			$("#userbox-right > ul.quickOp > li.notification").removeClass().addClass("icon notification msgCount0");
		$("#noticeCount").html(newCount);
	};
	
	function replaceLeftMessageCount(count){
		var newCount  = $("#top-recommend-count").html() - count;
		$("#top-recommend-count").html(newCount);
		if(newCount>0)
			$("ul.myNavList > li.current > a > span").html(newCount);
		else
			$("ul.myNavList > li.current > a > span").remove();
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
<div id="newsfeed-time" class="content-menu-body" >
	<input type="hidden" id="messageType" value="person"/>
	<table class="teamDivision dataTable merge">
	<thead>
		<tr>
			<td colspan="3" class="dtRight"><a  id="know-all"  class="but-color" >全知道了</a></td>
		</tr>
	</thead>
	<tbody>
	<c:forEach items="${TeamMap}" var="item">
	<tr id="team-${item.value.id}">
		<td class="teamBlock">
			
			<h3>
			<a href="<vwb:Link format='url' context='switchTeam' page=""/>?func=forward&team=${item.value.name}&tab=${currTab}" tid="${item.value.id}">
				<c:out value="${item.value.displayName}"/>
			</a>
			</h3>
			<c:if test="${PersonCountMap[item.key] != 0}">
				<div class="feedCount"><span class="feedBorder">${PersonCountMap[item.key]}</span></div>
			</c:if>
			<p class="ui-text-note">(${item.value.name})</p>
		</td>
		<td class="feedList">
			<c:set var="noticeList" value="${PersonNoticeMap[item.key]}" scope="request" />
			<c:set var="resourceMap" value="${resourceMapMap[item.key]}" scope="request" />
			<jsp:include page="/jsp/aone/team/notice/simpleNoticeDisplay.jsp"></jsp:include>
			<p class="moreFeed"><a href="<vwb:Link format='url' context='switchTeam' page=''/>?func=forward&team=${item.value.name}&tab=personNotice">更多消息&gt;&gt;</a></p>
		</td>
		<td class="dtRight" style="padding-top:10px;with:65px;white-space: nowrap;">
			<c:if test="${fn:length(noticeList) != 0}">
				<a class="but-color clear-one-team" tid="${item.value.id}">知道了 </a>
			</c:if>
		</td>
	</tr>
	</c:forEach>
	</tbody>
	</table>
	
</div>


