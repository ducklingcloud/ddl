<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script type="text/javascript">

</script>

		<h3>我关注的页面</h3>
		<c:choose>
			<c:when test="${empty pageFeedList }">
				<p class="a1-feed-none">没有关注的页面。</p>
			</c:when>
			<c:otherwise>
				<ul class="a1-subs-list" id="pageFeedList">
				<c:forEach items="${pageFeedList}" var="item">
					<li feedId="${item.id}">
						<a class="a1-subs-del delete-feed-button" url="<vwb:Link context='feed' format='url'/>?func=removeFeedByID&feedId=${item.id}" title="取消关注"></a>
						<%-- <c:set var="targetKey" value="${item.publisher.id }"></c:set>
						<c:set var="targetDe" value="${pageFeedDEntityMap[targetKey] }"></c:set> 
						<vwb:ResourceName resourceMap="${resourceMap }" target="${targetDe }"/> --%> 
						<a href="<vwb:Link context='viewFile' page="${item.publisher.id}" format='url'/>" >${item.publisher.name}</a>
					</li> 
				</c:forEach>
				</ul>
				<div class="ui-clear"></div>
			</c:otherwise>
		</c:choose>
		
		<h3>我关注的成员</h3>
		<c:choose>
			<c:when test="${empty personFeedList }">
				<p class="a1-feed-none">没有关注的成员。</p>
			</c:when>
			<c:otherwise>
				<ul class="a1-subs-list" id="personFeedList">
				<c:forEach items="${personFeedList}" var="item">
					<li feedId="${item.id}">
						<a class="a1-subs-del delete-feed-button" url="<vwb:Link context='feed' format='url'/>?func=removeFeedByID&feedId=${item.id}" title="取消关注"></a>
						<a href="<vwb:Link context='user' page='${item.publisher.id}' format='url'/>" >${item.publisher.name}</a>
					</li> 
				</c:forEach>
				</ul>
				<div class="ui-clear"></div>
			</c:otherwise>
		</c:choose>
	
	<!-- dialog -->
	<div class="ui-dialog-cover"></div>
	<div class="ui-dialog" id="cancelSubscription">
		<span class="ui-dialog-x"></span>
		<p class="ui-dialog-title">取消关注</p>
		<p>确定要取消对页面<span id="pageName"></span>的关注吗？</p>
		<div class="ui-dialog-control">
			<input type="button" id="confirmCancel" value="取消关注" />
			<a href="javascript:void(0)" class="ui-dialog-close">保持关注</a>
		</div>
	</div>
