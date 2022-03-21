<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%
	request.setAttribute("contextPath", request.getContextPath());
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<tiles:insertAttribute name="commonheader"/>
		<meta name="keywords" content="云计算服务,科研管理,协同办公,移动互联网,科研在线" />
	<!-- 
		<link rel="stylesheet" href="${contextPath}/jsp/aone/css/aone.css?v=6.0.0" type="text/css" />
		<link rel="stylesheet" href="${contextPath}/jsp/aone/css/base.css?v=6.0.0" type="text/css" />
		<link rel="stylesheet" href="${contextPath}/jsp/aone/css/uiLib.css?v=6.0.0" type="text/css" />
	-->
		<link rel="stylesheet" href="${contextPath}/jsp/aone/css/uiLib-0.9.2.css" type="text/css" />
		<link rel="stylesheet" href="${contextPath}/jsp/aone/css/a1.css" type="text/css" />
		<link rel="stylesheet" href="${contextPath}/jsp/aone/css/tag.css" type="text/css" />
	
		<link rel="stylesheet" href="${contextPath}/jsp/aone/css/fileuploader.css?v=6.0.0" type="text/css"/>	
		<script type="text/javascript" src="${contextPath}/jsp/aone/js/fileuploader.js?v=6.0.0"></script>
		<script type="text/javascript" src="${contextPath}/jsp/aone/js/search-jQuery.js?v=6.0.0"></script>
		<script type="text/javascript" src="${contextPath}/jsp/aone/js/uiLib-0.9.2-jquery.js"></script>
		
		<fmt:setBundle basename="templates.default" />
		<title>
			<fmt:message key="view.title.view">
				<fmt:param>
					<vwb:viewportTitle />
				</fmt:param>
				<fmt:param>
					<vwb:applicationName />
				</fmt:param>
			</fmt:message>
		</title>
	</head>
	<vwb:SpaceType/>
	<body id="bodyElement" class='<c:choose><c:when test="${spaceType eq 'personal' }">personal-space</c:when><c:otherwise>common-space</c:otherwise></c:choose>'>
		<div class="ui-wrap fullWidth std" id="fixedTop">
			<div id="browserAlert" class="fullWidth">
				<p>系统不完全支持您所使用的浏览器：内容显示和部分功能可能无法正常运行。建议您使用以下更好的浏览器：</p>
				<p class="browsers">
					<a class="firefox" href="http://firefox.com.cn/" target="_blank">Firefox</a>
					<a class="msie" href="http://windows.microsoft.com/zh-CN/internet-explorer/products/ie/home" target="_blank">IE9</a>
					<a class="safari" href="http://www.apple.com.cn/safari/" target="_blank">Safari</a>
					<a class="chrome" href="http://www.google.com/chrome" target="_blank">Chrome</a>
					<a class="opera" href="http://www.opera.com/" target="_blank">Opera</a>
				</p>
			</div>
			<div id="header" class="wrapper1280">
				<tiles:insertAttribute name="userbox"/>
				<tiles:insertAttribute name="header"/>
			</div>
			<div id="navigation" class="fullWidth"><div class="wrapper1024">
				<jsp:include page="/layout/collection-topmenu.jsp"/>
			</div></div>
		</div>
		<div class="ui-wrap wrapper1280">
			<div id="content" class="std stdRounded">
				<tiles:insertAttribute name="content"/>
			</div>
			<div class="clear"></div>
			<div id="footer">
				<tiles:insertAttribute name="footer"/>
			</div>
		</div>
		<a id="getFeedback" class="ui-sideTouch" href="http://iask.cstnet.cn/?/home/explore/category-11" target="_blank">意见反馈</a>
	</body>
	
	<script language="javaScript">
		$(document).ready(function(){
		
			//----------------------------------------------------//
			var browserAlert = $('#browserAlert');
			if ($.browser.msie) {
				if (parseInt($.browser.version, 10) < 8) {
					browserAlert.show();
					$('body').addClass('browserAlert');
				}
			}
			
			$('.msgFloat').mouseout(function(){
				var obj = $(this);
				var msgTimeout = setTimeout(function(){ obj.hide(); }, 1000);
				obj.attr('msgTimeoutId', msgTimeout);
			});
			$('.msgFloat').mouseover(function(){
				window.clearTimeout($(this).attr('msgTimeoutId'));
			});
			
			function placeDock() {
				if ($(window).width() < 1024) {
					$('body').addClass('narrowConfigure');
				}
			}
			
			$(window).resize(placeDock);
			placeDock();
			
			var backBox = new BackToTop('回顶部');	
		});
	</script>
	
</html>