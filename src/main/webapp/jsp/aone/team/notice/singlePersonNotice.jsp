<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<script type="text/javascript">
$(document).ready(function(){
});
</script>

<div id="singleFeed-person" class="content-menu-body" >
	<div class="innerWrapper">
		<div class="innerWrapperMargin">
			<c:set var="noticeQueue" value="SingleTeamQueue" scope="request" />
			<c:choose>
				<c:when test="${empty personNoticeList }">
					<p class="a1-feed-none">最近没有发生更新</p>
				</c:when>
				<c:otherwise>
				<c:forEach items="${personNoticeList}" var="c">
					<h3>${c.date}</h3>
					<div class="teamFeedList">
						<c:set var="noticeList" value="${c.records}" scope="request" />
						<jsp:include page="/jsp/aone/team/notice/simpleNoticeDisplay.jsp"></jsp:include>
					</div>
				</c:forEach>
				</c:otherwise>
			</c:choose>
			<div class="ui-clear"></div>
		</div>
	</div>
</div>

<script type="text/html">
<li>
		<div class="notice-leadImg ${n.operation.name}"></div>
				<span class="time"><fmt:formatDate value="${n.occurTime}" type="time" /></span>
		<c:choose>
			
			<c:when test='${n.operation.name eq "comment"}'>
				<!-- 句式1: 某人A 评论 了 页面/文件 B TA说：Blalaala	 -->
				<span class="notice-actor"><a href="${n.actor.url}">${n.actor.name}</a></span>
				<span class="notice-target-operation-${n.target.type}">${n.operation.displayName}了</span>
				<span class="notice-target-type">${n.target.type}</span>
				<span class="notice-target-name"><a href="${n.target.url}">${n.target.name}</a></span>
				<span class="notice-target-version">版本${n.targetVersion}</span>
				<p class="notice-comment-message">${n.message}</p>
			</c:when>
			<c:when test='${n.operation.name eq "rename"}'>
				<!-- 句式2: 某人A 将 页面/文件/集合/团队oldB 重命名成了  newB -->
				<span class="notice-actor"><a href="${n.actor.url}">${n.actor.name}</a></span>
				<span>将</span>
				<span class="notice-target-type">${n.target.typeDisplay}</span>
				<span class="notice-target-old-name"><a href="${n.target.url}">${n.message}</a></span>
				<span class="notice-target-operation-${n.target.type}">${n.operation.displayName}为了</span>
				<span class="notice-target-new-name"><a href="${n.target.url}">${n.target.name}</a></span>
				<c:if test="${(n.target.type eq 'DPage') or (n.target.type eq 'DFile') }">
					<span class="notice-target-version">版本${n.targetVersion}</span>
				</c:if>
			</c:when>
			<c:otherwise>
				<!-- 句式3: 某人A 修改/上传/创建 了 页面/文件/集合/团队 B -->
				<span class="notice-actor"><a href="${n.actor.url}">${n.actor.name}</a></span>
				<span class="notice-target-operation-${n.operation.name}">${n.operation.displayName}了</span>
				<span class="notice-target-type">${n.target.typeDisplay}</span>
				<span class="notice-target-name"><a href="${n.target.url}">${n.target.name}</a></span>
				<c:if test="${(n.target.type eq 'DPage') or (n.target.type eq 'DFile') }">
					<span class="notice-target-version">版本${n.targetVersion}</span>
				</c:if>
			</c:otherwise>
		</c:choose>
	</li>
</script>
<script id="recommend-notice-template" type="text/html">
	<span class="notice-actor"><a href="{{= actorURL}}">{{= actorName}}</a></span>
	<span>把</span>
	<span class="notice-target-type-{{= targetType}}">{{= targetTypeDisplay}}</span>
	<span class="notice-target-name"><a href="{{= targetURL}">{{= targetName}}</a></span>
	<span class="notice-target-version">版本{{= targetVersion}}</span>
	<span class="notice-target-operation-recommend">{{= operationDisplayName}}给了</span>
	<span class="notice-recommend-recipients">{{= additionDisplay}}</span>
	<p class="notice-recommend-message">{{= message}}</p>
</script>
<script id="comment-notice-template" type="text/html">
	<span class="notice-actor"><a href="{{= actorURL}}">{{= actorName}}</a></span>
	<span class="notice-target-operation-{{= operation}}">{{= operationDisplay}}了</span>
	<span class="notice-target-type-{{= targetType}}">{{= targetTypeDisplay}}</span>
	<span class="notice-target-name"><a href="{{= targetURL}">{{= targetName}}</a></span>
	<span class="notice-target-version">版本{{= targetVersion}}</span>
	<span class="notice-target-operation-recommend">{{= operationDisplayName}}给了</span>
	<span class="notice-recommend-recipients">{{= additionDisplay}}</span>
	<p class="notice-comment-message">${n.message}</p>
</script>

