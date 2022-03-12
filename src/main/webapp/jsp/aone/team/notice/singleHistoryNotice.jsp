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
	
<div id="singleFeed-history" class="content-menu-body">
	<div class="innerWrapper">
	<c:choose>
		<c:when test="${empty historyNoticeList }">
			<p class="a1-feed-none">最近没有发生更新</p>
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
	<div class="ui-clear"></div>
	</div>
</div>


