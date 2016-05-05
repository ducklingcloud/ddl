<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%
	request.setAttribute("contextPath", request.getContextPath());
	request.setAttribute("siteURL", request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath());
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<tiles:insertAttribute name="commonheader"/>
		<link rel="stylesheet" href="${contextPath}/jsp/aone/css/aone.css?v=${aoneVersion}" type="text/css" />
		<link rel="stylesheet" href="${contextPath}/jsp/aone/css/base.css?v=${aoneVersion}" type="text/css" />
		<link rel="stylesheet" href="${contextPath}/jsp/aone/css/uiLib.css?v=${aoneVersion}" type="text/css" />
		
		<script type="text/javascript" src="${contextPath}/jsp/aone/js/uiLib-0.9.2-jquery.js"></script>
		<script type="text/javascript"
			src="${contextPath}/jsp/aone/js/search-jQuery.js?v=${aoneVersion}"></script>

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
	<body>
		<div class="ui-wrap">
			<div id="aoneBanner" class="std">
				<div id="browserAlert">
					<p>系统不完全支持您所使用的浏览器：内容显示和部分功能可能无法正常运行。建议您使用以下更好的浏览器：</p>
					<p class="browsers">
						<a class="firefox" href="http://firefox.com.cn/" target="_blank">Firefox</a>
						<a class="msie" href="http://windows.microsoft.com/zh-CN/internet-explorer/products/ie/home" target="_blank">IE8</a>
						<a class="safari" href="http://www.apple.com.cn/safari/" target="_blank">Safari</a>
						<a class="chrome" href="http://www.google.com/chrome" target="_blank">Chrome</a>
						<a class="opera" href="http://www.opera.com/" target="_blank">Opera</a>
					</p>
				</div>
				
				<a id="ROL" href="${siteURL}"></a>
				<a class="ui-iconButton help" style="float:left" href="http://support.ddl.escience.cn/">新手指南</a>
				<div id="aoneUserbox">
					<ul>
					<vwb:UserCheck status="authenticated">
					 	<li class="userMe">
					 		<a><vwb:UserName /></a>
						</li>
						<li>
					 		<a href="<vwb:Link format='url' context='switchTeam'/>">首页</a>
					 	</li>
						<li>
							<a href="<vwb:Link context="logout" format='url'/>"
								class="action logout"
								title="<fmt:message key='actions.logout.title'/>"><fmt:message
									key="actions.logout" /></a>
						</li>
					</vwb:UserCheck>
					<vwb:UserCheck status="notauthenticated">
						<li><a href="<vwb:Link context='switchTeam' absolute='true' format='url'/>">登录</a></li>
						<li><a href="<vwb:Link context='regist' absolute='true' format='url'/>">注册</a></li>
					</vwb:UserCheck>
					</ul>
				</div>
				<div id="userMeMenu" class="pulldownMenu">
					<ul>
						<li><a href="<vwb:Link context="dashboard" format='url'/>?func=profile">个人资料</a></li>
						<li><a href="<vwb:Link context="dashboard" format='url'/>?func=preferences">个人偏好</a></li>
					</ul>
				</div>
				<div class="ui-clear"></div>
			</div>
			<div id="content" class="std portalContent">
				<tiles:insertAttribute name="content"/>
			</div>
			<div class="clear"></div>
			<div id="footer">
				<tiles:insertAttribute name="footer"/>
			</div>
		</div>
		<a id="getFeedback" href="http://iask.cstnet.cn/?/home/explore/category-11" target="_blank">意见反馈</a>
	</body>
	
	<script language="javascript">
		var browserAlert = $('#browserAlert');
		if ($.browser.msie) {
			if (parseInt($.browser.version, 10) < 7) {
				browserAlert.show();
			}
		}
		
		var backBox = new BackToTop('回顶部');
		$('li.userMe>a').pulldownMenu({ 'menu': $('#userMeMenu') });
		
		function init(){
			var url = window.location.href;
			if(url.indexOf("regist")>0 && url.indexOf("code")>0){
				$("#aoneUserbox").remove();
			}
		}
		
		init();
	</script>
	
</html>