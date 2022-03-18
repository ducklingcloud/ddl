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
		<link rel="stylesheet" href="${contextPath}/jsp/aone/css/fileuploader.css" type="text/css"/>	
		<link rel="stylesheet" href="${contextPath}/jsp/aone/css/uiLib-0.9.2.css?v=${aoneVersion}" type="text/css" />
		<link rel="stylesheet" href="${contextPath}/jsp/aone/css/lynx.css?v=${aoneVersion}" type="text/css" />
		<link rel="stylesheet" href="${contextPath}/jsp/aone/css/lion.css?v=${aoneVersion}" type="text/css" />

		<!--[if lte IE 7]>
				<link rel="stylesheet" href="${contextPath}/jsp/aone/css/lynx.iehack.css?v=${aoneVersion}" type="text/css" />
		<![endif]-->
		<style>
			.breadcrumb {
			  background-color: #f3f3f3;
			  background-image: -moz-linear-gradient(top, #f5f5f5, #f1f1f1);
			  background-image: -webkit-gradient(linear, 0 0, 0 100%, from(#f5f5f5), to(#f1f1f1));
			  background-image: -webkit-linear-gradient(top, #f5f5f5, #f1f1f1);
			  background-image: -o-linear-gradient(top, #f5f5f5, #f1f1f1);
			  background-image: linear-gradient(to bottom, #f5f5f5, #f1f1f1);
			  background-repeat: repeat-x;
			  filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#fff5f5f5', endColorstr='#fff1f1f1', GradientType=0);
			  -webkit-border-radius: 2px;
			  -moz-border-radius: 2px;
			  border-radius: 2px;
			}
			#navBarOl.breadcrumb li {
				border:1px solid transparent\0;
				display:inline-block;
			}
		</style>
		<script type="text/javascript" src="${contextPath}/jsp/aone/js/fileuploader.js?v=${aoneVersion}"></script>
		<script type="text/javascript" src="${contextPath}/jsp/aone/js/search-jQuery.js?v=${aoneVersion}"></script>
		<script type="text/javascript" src="${contextPath}/jsp/aone/js/uiLib-0.9.2-jquery.js?v=${aoneVersion}"></script>
		<script type="text/javascript" src="${contextPath}/jsp/aone/js/lynx-jquery.js?v=${aoneVersion}"></script>
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
	<body id="bodyElement" class='<c:choose><c:when test="${spaceType eq 'personal' }">personal-space</c:when><c:otherwise>common-space</c:otherwise></c:choose>'>
		
		<tiles:insertAttribute name="header"/>
		
		<div id="body" class="ui-wrap wrapper1280">
			<jsp:include page="/jsp/aone/resourcepath/rsourcePath.jsp"></jsp:include>
			<div id="content" class="std stdRounded">
				<tiles:insertAttribute name="content"/>
			</div>
			<div class="clear"></div>
		</div>
		
		<div id="footer">
			<tiles:insertAttribute name="footer"/>
		</div>
		
		<a id="getFeedback" class="ui-sideTouch" href="mailto:kai.nan@icloud.com" target="_blank">意见反馈</a>

		<%-- <jsp:include page="/jsp/aone/QRCode.jsp" /> --%>
		
	<script type="text/javascript">
		$(document).ready(function(){
		
			//----------------------------------------------------//
			
			browserAlert();
			
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
	</body>
	
	
</html>
