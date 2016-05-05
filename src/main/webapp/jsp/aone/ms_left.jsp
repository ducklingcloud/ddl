<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<fmt:setBundle basename="templates.default" />
<script type="text/javascript">
	
	$("#recommend-history").live('click',function(){
		$(this).attr("href","<vwb:Link jsp='myspace' format='url'/>?func=recommendHistory");
	});
	
	$("#latest-message-link").live('click',function(){
	});
	
	$("#feed-history").live('click',function(){
		$(this).attr("href","<vwb:Link jsp='myspace' format='url'/>?func=feedHistory");
	});
	
	$("#my-page-link").live('click',function(){
		$(this).attr("href","<vwb:Link jsp='myspace' format='url'/>?func=myPageNavigator");
	});
	
	$("#team-page-link").live('click',function(){
		$(this).attr("href","<vwb:Link jsp='myspace' format='url'/>?func=teamPageNavigator");
	});
	
	$("#feed-manage-link").live('click',function(){
		$(this).attr("href","<vwb:Link jsp='myspace' format='url'/>?func=feedManager");
	});

/* Keeps left menu steady in the viewport */
	$(document).ready(function(){
		var obj = $('#a1-sort');	
		ori = obj.offset().top;

	});
	
</script>
<div id="a1-sort">
	<ul class="ui-navList">
		<li id="allTab" <c:if test="${tab=='all'}">class="current"</c:if>>
			<a id="latest-message-link" href="<vwb:Link jsp='myspace' format='url'/>">动态</a>
		</li>
		<li id="recommendTab" class="ui-sub <c:if test="${tab=='recommend'}">current</c:if>">
			<c:if test="${tagMessageSize!=0}">
				<span class="a1-feedCount ui-RTCorner">${tagMessageSize}</span>
			</c:if>
			<a id="recommend-history" href="">给我的分享</a>
		</li>
		<li id="feedTab" class="ui-sub <c:if test="${tab=='feed'}">current</c:if>">
			<a id="feed-history" href="">我的关注</a>
		</li>
		<li style="display:none"><a href="">全部动态</a></li>
		<li style="display:none"><a href="">大家关注的</a></li>
		<li class="<c:if test="${tab=='pageNav'}">current</c:if>"><span><a id="my-page-link" href="#">我的导航</a></span></li>
		<!-- <li class="ui-sub"><span><a id="team-page-link" href="#">团队导航</a></span></li>  -->
		<li class="ui-sub <c:if test="${tab=='adminFeed'}">current</c:if>"><span><a id="feed-manage-link" href="#">关注列表</a></span></li>
	</ul>
	<p class="a1-top10-title"><span class="ui-RTCorner ui-text-small">热度</span>本周热门</p>
	<vwb:HotPages days="7" pages="10" boxStyle="a1-top10 ui-navList"/>
</div>
