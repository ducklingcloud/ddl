<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<script type="text/javascript">
$(document).ready(function(){
	function switchView(VIEW) {
		$('#feedSelector ul.filter > li').removeClass('chosen');
		$('.content-menu-body').fadeOut();
		var view = VIEW;
		
		switch (VIEW) {
			case 'follow':
				$('#singleFeed-monitor-follow').fadeIn();
				break;
			case 'update':
			default:
				view = 'update';
				$('#singleFeed-monitor-update').fadeIn();
		}
		$('#feedSelector ul.filter a[view="' + view + '"]').parent().addClass('chosen');
	};
	
	var hash = (window.location.hash)?window.location.hash:'';
	if (hash!='') { switchView(hash.substring(1)); }
	else { switchView(); }
	
	
	$('#feedSelector ul.filter a').click(function(){
		switchView($(this).attr('view'));
	});
	
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
});
</script>

<div id="feedSelector" class="filterHolder">
	<ul class="filter">
		<li class="chosen"><a view="update" href="#update">关注更新</a></li>
		<li><a view="follow" href="#follow">关注列表</a></li>
	</ul>
</div>
<div id="singleFeed-monitor-update" class="content-menu-body" style="display:none">
	<div class="innerWrapper">
		<div class="innerWrapperMargin">
			<c:choose>
				<c:when test="${empty monitorNoticeList }">
					<p class="a1-feed-none">最近没有发生更新</p>
				</c:when>
				<c:otherwise>
					<c:set var="noticeQueue" value="SingleTeamQueue" scope="request" />
					<c:forEach items="${monitorNoticeList}" var="c">
						<h3>${c.date}</h3>
						<div class="teamFeedList">
							<c:set var="noticeList" value="${c.records}" scope="request" />
							<jsp:include page="/jsp/aone/team/notice/simpleNoticeDisplay.jsp"></jsp:include>
						</div>
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</div>

<div id="singleFeed-monitor-follow" class="content-menu-body" style="display:none">
	<div class="innerWrapper">
	<jsp:include page="/jsp/aone/team/admin/adminSubs.jsp"></jsp:include>
	<div class="ui-clear"></div>
	</div>
</div>


