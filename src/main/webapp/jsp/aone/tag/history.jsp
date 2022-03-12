<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />

<div id="historyTool" class="toolHolder light">
	<ul class="filter">
		<li><a filter="all">全部</a></li>
	</ul>
	<ul class="filter leftBorder">
		<li><a filter="create">创建</a></li>
		<li><a filter="upload">上传</a></li>
		<li><a filter="modify">编辑</a></li>
		<li><a filter="comment">评论</a></li>
		<li><a filter="recommend">分享</a></li>
		<li><a filter="delete">删除</a></li>
	</ul>
</div>

<div class="innerWrapper">
	<div class="innerWrapperMargin">
		<div id="singleFeed-history">
			<div>
			<c:choose>
				<c:when test="${empty historyNoticeList }">
					<p class="NA large">最近您没有编辑、上传文档或参与讨论。</p>
					<div class="NA">
						<p>这里按时间顺序记录您参与编辑、上传的文档</p>
					</div>
				</c:when>
				<c:otherwise>
					<c:set var="noticeQueue" value="SingleTeamQueue" scope="request" />
					<c:set var="noticeTab" value="HistoryTab" scope="request" />
					<c:forEach items="${historyNoticeList}" var="c">
						<h3>${c.date}</h3>
						<div class="teamFeedList">
							<c:set var="noticeList" value="${c.records}" scope="request" />
							<jsp:include page="/jsp/aone/team/notice/simpleNoticeDisplay.jsp"></jsp:include>
						</div>
					</c:forEach>
				</c:otherwise>
			</c:choose>
			<div class="ui-clear bedrock"></div>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
$(document).ready(function(){
	
});
</script>
