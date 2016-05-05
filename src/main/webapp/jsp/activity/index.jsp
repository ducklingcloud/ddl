<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib  uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%> 
<%@taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="${contextPath}/jsp/aone/css/index.css" rel="stylesheet" type="text/css" />
<link href="${contextPath}/jsp/aone/css/index-nov2013.css?v=${aoneVersion}" rel="stylesheet" type="text/css" />
<link href="${contextPath}/jsp/aone/css/index-aug2014.css?v=${aoneVersion}"	rel="stylesheet" type="text/css" />
<link href="${contextPath}/scripts/bootstrap/css/todc-bootstrap.css" rel="stylesheet"/>	
<link href="${contextPath}/jsp/aone/css/lion.css?v=${aoneVersion}"	rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${contextPath}/scripts/jquery/jquery-1.8.3.min.js"></script>
<link href="${contextPath}/images/favicon.ico" rel="shortcut icon" type="image/x-icon" />
<title>金秋十月 好礼相送</title>
<style>
	.navbar.navbar-fixed-top .navbar-inner .container ul.nav li {margin:6px 0 0 0;}
	.navbar .brand {padding:0;}
</style>
</head>
<body class="texure">
	<jsp:include page="/ddlHeader.jsp"></jsp:include>
	<div id="ddl-intro" class="help activity">
		<div class="ui-wrap"></div>
	</div>
	<div class="ui-wrap" style="padding-top:20px;">
		<div class="active-left large">
			<div class="active" id="active1">
				<a href="${contextPath}/activity/task-win-space" target="_blank"><img src="${contextPath}/jsp/aone/images/activity-banner1.png" /></a>
			</div>
			
			<div class="active" id="active2">
				<a href="${contextPath}/activity/lottery" target="_blank"><img src="${contextPath}/jsp/aone/images/activity-banner2.png" /></a>
			</div>
		</div>
		<div class="active-right small">
			<div class="lucky">
				<h3>中奖公示</h3>
				<%-- 活动结束 
				<c:choose>
					<c:when test="${fn:length(drawList)>0}">
						<div id="sd1" class="scrollDiv">
						<ul>
						<c:forEach var="item" items="${drawList }">  
							<li><span class="mail">${item.user }</span><span class="present">${item.giftName }</span></li>
						</c:forEach>
						</ul>
						</div>
					</c:when>
					<c:otherwise>
						<p class="noWin">暂无人中奖.</p>
					</c:otherwise>
				</c:choose>
				--%>
				<p class="over">抽奖活动已结束。</p>
				<p class="over"><a class="btn btn-primary" href="${contextPath }/activity/lottery/winners" target="_blank">查看中奖名单</a></p>
			</div>
			
		</div>
		<div class="clear"></div>
	</div>
	
	<div id="footer">
		<jsp:include page="/Version.jsp"></jsp:include>
	</div>
<c:if test="${authenticated == false}">
<script type="text/javascript">
$(function(){
	$.getScript('${passportUrl}/js/isLogin.do', function(){
		 if(data.result){
			window.location.href="${loginUrl}";
		 }
	 });
});
</script>
</c:if>
<%-- 活动结束 
<c:if test="${fn:length(drawList)>12}">
	<style  type="text/css">
	.scrollDiv{height:20px;line-height:20px;overflow:hidden;}
	.scrollDiv li{height:20px;}
	#sd1{height:380px;}
	</style>
	<script type="text/javascript">
	$(function(){
		$("#sd1").Scroll({line:5,speed:500,timer:4000});
	});
	</script>
	<script type="text/javascript" src="${contextPath}/scripts/jquery/jquery.scroll.js"></script>
</c:if>
--%>
</body>
</html>