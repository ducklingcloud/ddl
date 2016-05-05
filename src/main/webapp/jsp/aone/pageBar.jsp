<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<script type="text/javascript">
	$(document).ready(function(){
	    
		$(".interest-box").click(function(){
			prepareSubscription("<vwb:Link context='feed' format='url'/>?func=preparePageFeed&pid=${pid}");
		});
		
		$(".recommend-box").click(function(){
			prepareRecommend("<vwb:Link context='recommend' format='url'/>?func=prepareRecommend&pid=${pid}",'${pid}',"${pageMeta.title}");
		});
		
		$(".remove-interest-box").click(function(){
			prepareRemoveSubscription("<vwb:Link context='feed' format='url'/>?func=removePageFeed&pid=${pid}");
		});
		
    });
    
</script>

<%-- <div class="ui-RTCorner" id="pageBarWrap">
	<vwb:UserCheck status="authenticated">
		<ul class="ui-groupButton ui-RTCorner" id="pageBar">
			<li class="leftSide">
				<input type="hidden" name="subscriptionStatus" value="" />
				<vwb:IsSubscribed flagName="flag" itemsName="existInterest" />
				<c:choose>
					<c:when test="${!flag}">
						<a class="interest-box" attr="${flag}"><span>关注</span></a>
					</c:when>
					<c:otherwise>
						<a class="remove-interest-box" attr="${flag}"><span>已关注</span></a>
					</c:otherwise>
				</c:choose> 
			</li>
			<li>
				<a class="recommend-box"><span>分享</span> </a>
			</li>
			<li class="rightSide">
				<a onclick="a1_showHideout('visitor', this)"><span>热度：<vwb:VisitCount /></span> </a>
			</li>
		</ul>
	</vwb:UserCheck>
	<vwb:UserCheck status="notAuthenticated">
		<a class="ui-linkButton ui-RTCorner" onclick="a1_showHideout('visitor', this)">
			<span>热度：<vwb:VisitCount /></span> </a>
	</vwb:UserCheck>
</div> --%>
<!-- for dialog background -->
<div class="ui-dialog-cover"></div>
<jsp:include page="/jsp/aone/subscription/addSub.jsp"></jsp:include>
<jsp:include page="/jsp/aone/subscription/deleteSub.jsp"></jsp:include>
<jsp:include page="/jsp/aone/recommend/addRec.jsp"></jsp:include>
<jsp:include page="/jsp/aone/browse/pageReadLog.jsp"></jsp:include>