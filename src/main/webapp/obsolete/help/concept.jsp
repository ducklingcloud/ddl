<%@ page language="java" pageEncoding="utf-8"%>
<%@ page import="net.duckling.ddl.common.*"%>
<%@taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">	
<html>
<head>
	<%pageContext.setAttribute("contextPath", request.getContextPath()); %>
	<%
		pageContext.setAttribute("contextPath", request.getContextPath());
		VWBContext.createContext(request,"error");
	%>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="keywords" content="云计算服务,科研管理,协同办公,移动互联网,科研在线" />
	<title>科研在线</title>
	<link href="${contextPath}/jsp/aone/css/index.css" rel="stylesheet" type="text/css">
</head>

<body>
	<div class="ui-wrap">
		<div id="aoneBanner" class="std">
			<a id="ROL" href="${contextPath}/index.jsp"><span>科研在线</span></a>
			<ul id="nav">
				<li><a href="${contextPath}/index.jsp">概述</a></li>
				<li><a href="<vwb:Link context='shareFile' format='url'/>">快速分享</a></li>
				<li><a href="${contextPath}/help/introduction.jsp">科研在线是什么？</a></li>
				<li><a href="${contextPath}/help/tutorial.jsp">使用指南</a></li>
			</ul>
		</div>
		
		<div class="largeButtonHolder" style="text-align:right">
			<a class="largeButton green" href="<vwb:Link context='regist' absolute='true' format='url'/>">注册</a>
			<a class="largeButton" href="<vwb:Link context='switchTeam' absolute='true' format='url'/>">登录</a>
		</div>
		
		<div id="content">
			<div class="content-title tutorial">
				<ul class="titleDivide">
					<li id="scenarioTab"><a href="tutorial.jsp">应用场景</a></li>
					<li id="operationTab"><a href="manual.jsp">基本操作</a></li>
					<li id="conceptTab" class="current"><a href="#concept">概念和名词</a></li>
				</ul>
			</div>
			<div id="concept" class="content-through sub">
				<div class="tutorialBlock">
				</div>
			</div>
			<div class="content-through">
				<ul class="titleDivide bottom">
					<li id="scenarioTab"><a href="tutorial.jsp">应用场景</a></li>
					<li id="operationTab"><a href="manual.jsp">基本操作</a></li>
					<li id="conceptTab" class="current"><a href="#concept">概念和名词</a></li>
				</ul>
			</div>
			<div class="ui-clear"></div>
		</div>
		
		<ul id="subNav">
			<li><a href="${contextPath}/index.jsp">首页/概述</a></li>
			<li class="current"><a href="${contextPath}/help/introduction.jsp">科研在线是什么？</a></li>
			<li><a href="${contextPath}/help/tutorial.jsp">使用指南</a></li>
		</ul>
		
		<div id="footer">
			<jsp:include page="/Version.jsp"></jsp:include>
		</div>
		<a id="getFeedback" href="http://iask.cstnet.cn/?/home/explore/category-11" target="_blank">意见反馈</a>
	</div>
</body>
</html>
