<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<script type="text/javascript">
$(document).ready(function(){
	var url = "<vwb:Link format='url' context='myspace'/>?func=updateMessageStatus";
	var selectedLiNode = null;
	$("li.recommend-block").live('click',function(){
		selectedLiNode = $(this);
		if($(selectedLiNode).hasClass("a1-feed-unread")){
			ajaxRequest(url,"pid="+$(this).attr("pid")+"&messageType="+$(this).attr("messageType"),afterUpdateMessage);
		}
	});
	
	function afterUpdateMessage(data){
		$(selectedLiNode).removeClass('a1-feed-unread');
		var count = $("#top-recommend-count").html();
		count = count - 1;
		$("#top-recommend-count").html(count);
	};
	
	var redirectURL = null;
	
	$("a.recommend-link, a.user-link").live('click',function(event){
		redirectURL = $(this).attr("url");
		jumpToURL(event,$(this).parent().parent().parent());
	});
	
	
	function jumpToURL(event,target){
		event.stopPropagation();
		if($(target).hasClass("a1-feed-unread"))
			ajaxRequest(url,"pid="+$(target).attr("pid")+"&messageType="+$(target).attr("messageType"),afterRecommendLink);
		else
			window.location = redirectURL;
	};
	
	function afterRecommendLink(data){
		window.location = redirectURL;
	};
	
});
</script>

<c:if test="${empty recommendMessages }">
	<h3 class="feed-none-recommend">我的消息</h3>
	<p class="a1-feed-none feed-none-recommend">还没有收到过分享。</p>
</c:if>
<c:if test="${not empty recommendMessages}">
	<h3>我的消息</h3>
	<ul id="a1-recommendation" class="a1-feed">
		<c:forEach var="item" items="${recommendMessages}" varStatus="status">
			<c:choose>
				<c:when test="${item.type eq 'recommend'}">
					<li pid="${item.pageId}" messageType="recommend" <c:if test="${item.status==0 and isAllDisplay}">class="recommend-block a1-feed-unread"</c:if>>
						<div class="a1-feed-leadingImg -recommend"></div>
						<div class="a1-feed-body">
							<h4>
								<span class="ui-RTCorner a1-time">
									<fmt:formatDate type="both" dateStyle="default" timeStyle="default" value="${item.createTime}" />
								</span>
								<c:forEach var="sender" items="${item.senderMap}">
									<a class="a1-feed-author user-link" url="<vwb:Link context="user" page="${sender.value.id}" format='url'/>">${sender.value.name}</a>
								</c:forEach>
								分享了
								<a class="a1-feed-title recommend-link" url="<vwb:Link context='view' page='${item.pageId}' format='url'/>">
									${item.title}
								</a>
							</h4>
							<!-- Remarks -->
							<c:if test="${fn:length(item.remarkMap)!=0}">
								<div class="a1-feed-comment">
									<p>
									<c:forEach var="remark" items="${item.remarkMap}" varStatus="status">
										${remark}<br/>
									</c:forEach>
									</p>
								</div>
							</c:if>
							<p>${item.digest}</p>
						</div>
						<div class="a1-feed-op">
		<!--					<p><a class="a1-opLink" onclick="ui_showDialog('interest')"><span>关注</span></a></p>-->
<!--							<p><a class="a1-opLink recommend-box" pageTitle="${item.title}" recommendPage="${item.pageId}"><span>分享</span></a></p>-->
						</div>
						<div class="ui-clear"></div>
					</li>
				</c:when>
				<c:when test="${item.type eq 'my_page_comment'}">
					<li pid="${item.pageId}" messageType="my_page_comment"  <c:if test="${item.status==0 and isAllDisplay}">class="recommend-block a1-feed-unread"</c:if>>
						<div class="a1-feed-leadingImg -talk"></div>
						<div class="a1-feed-body">
							<span class="ui-RTCorner a1-time">
								<fmt:formatDate type="both" dateStyle="default" timeStyle="default" value="${item.createTime}" />
							</span>
							<h4>
							<c:forEach var="sender" items="${item.senderMap}">
								<a class="a1-feed-author user-link" url="<vwb:Link context="user" page="${sender.value.id}" format='url'/>">${sender.value.name}</a>
							</c:forEach>
								评论了你的页面
								<a class="a1-feed-title recommend-link" url="<vwb:Link context='view' page='${item.pageId}' format='url'/>" >${item.title}</a></h4>
								<div class="a1-feed-comment">
									<p>
									<c:forEach var="cItem" items="${item.contentList}" varStatus="status">
										${cItem}<br/>
									</c:forEach>
									</p>
								</div>
						</div>
						<div class="ui-clear"></div>
					</li>
				</c:when>
				<c:otherwise>
					<li pid="${item.pageId}" messageType="recommend_comment" <c:if test="${item.status==0 and isAllDisplay}">class="recommend-block a1-feed-unread"</c:if>>
						<div class="a1-feed-leadingImg -talk"></div>
						<div class="a1-feed-body">
							<span class="ui-RTCorner a1-time">
								<fmt:formatDate type="both" dateStyle="default" timeStyle="default" value="${item.createTime}" />
							</span>
							<h4>
							<c:forEach var="sender" items="${item.senderMap}">
								<a class="a1-feed-author user-link" url="<vwb:Link context="user" page="${sender.value.id}" format='url'/>">${sender.value.name}</a>
							</c:forEach>
								回复了你在
								<a class="a1-feed-title recommend-link" url="<vwb:Link context='view' page='${item.pageId}' format='url'/>" >
									${item.title}</a>的评论</h4>
								<div class="a1-feed-comment">
									<p>
									<c:forEach var="cItem" items="${item.contentList}" varStatus="status">
										${cItem}<br/>
									</c:forEach>
									</p>
								</div>
						</div>
						<div class="ui-clear"></div>
					</li>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</ul>
</c:if>
