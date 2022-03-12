<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<script type="text/javascript">

</script>
	
<div id="newsfeed-time" class="content-menu-body" >
	<table class="teamDivision dataTable merge">
		<c:forEach items="${HistoryNoticeMap}" var="item">
		<tr>
			<td class="teamBlock">
			<h3>
			<a href="<vwb:Link format='url' context='switchTeam' page=""/>?func=forward&team=${TeamMap[item.key].name}&tab=${currTab}">
				${TeamMap[item.key].displayName}
			</a>
			</h3>
			<p class="ui-text-note">(${TeamMap[item.key].name})</p>
			</td>
			<td class="feedList">
				<c:set var="noticeList" value="${item.value}" scope="request" />
				<c:set var="noticeType" value="HistoryNotice" scope="request" />
				<c:set var="noticeTab" value="HistoryTab" scope="request" />
				<jsp:include page="/jsp/aone/team/notice/simpleNoticeDisplay.jsp"></jsp:include>
				<p class="moreFeed"><a href="<vwb:Link format='url' context='switchTeam' page=''/>?func=forward&team=${TeamMap[item.key].name}&tab=historyNotice">更多活动记录&gt;&gt;</a></p>
			</td>
		</tr>
		</c:forEach>
	</table>
</div>


