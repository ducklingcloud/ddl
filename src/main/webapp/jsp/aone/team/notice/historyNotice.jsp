<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<div id="myTrace">
<div id="historyTool" class="toolHolder light">
	<ul class="filter">
		<li class="chosen"><a filter="all">全部</a></li>
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
	<div style="margin:0 10px 0 20px;">
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
</div>
<script type="text/javascript">
$(document).ready(function(){
	$('#switchTrace').click(function(){traceState($(this));});
/* 	function commonState(obj){
		$('.chosen').removeClass('chosen');
		$(obj).parent().addClass('chosen');
		$('#myTrace').hide();
		$('#commonlyUse').show();
	}; */
	
	function traceState(obj){
		$('.chosen').removeClass('chosen');
		$(obj).parent().addClass('chosen');
		$('#commonlyUse').hide();
		$('#myTrace').show();
		
		filterTrace(location.hash.substring(7));
		// hash = '#trace-stateRef'
	}
	
	function filterTrace(ref) {
		var traceState = ['create', 'upload', 'modify', 'comment', 'recommend','delete'];
		var allClass = '';
		for (var i=0; i<traceState.length; i++) {
			allClass += 'show-' + traceState[i] + ' ';
		}
		
		
		if (arrIndexOf(traceState, ref)>-1) {
			$('#myTrace').removeClass(allClass).addClass('show-'+ref);
			$('#historyTool li.chosen').removeClass('chosen');
			$('#historyTool a[filter="' + ref +'"]').parent().addClass('chosen');
			window.location.hash = '#trace-' + ref;
		}
		else {
			$('#myTrace').removeClass(allClass);
			$('#historyTool li.chosen').removeClass('chosen');
			$('#historyTool a[filter="all"]').parent().addClass('chosen');
			window.location.hash = '#trace';
		}
	}
	$('#historyTool li a').click(function(){
		if ($(this).parent().hasClass('chosen')) {
			filterTrace();
		}
		else {
			filterTrace($(this).attr('filter'));
		}
	});
});
</script>
