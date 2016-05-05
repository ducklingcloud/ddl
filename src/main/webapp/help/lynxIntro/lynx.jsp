<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="net.duckling.ddl.common.*"%>
<%@taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%pageContext.setAttribute("contextPath", request.getContextPath()); %>
	<%
		pageContext.setAttribute("contextPath", request.getContextPath());
		VWBContext.createContext(request,"error");
	%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="${contextPath}/help/lynxIntro/lynxIntro.css" rel="stylesheet"  type="text/css" />
<link href="${contextPath}/jsp/aone/css/index.css" rel="stylesheet" type="text/css"/>
<title>新版科研在线</title>
</head>
<body>

<div id="main-content"  class="ui-wrap">
		<div id="aoneBanner" class="std">
			<a id="ROL" href="${contextPath}/index.jsp"><span>科研在线</span></a>
			<div class="ui-RTCorner" id="userCtrl">
				<a class="largeButton green" href="<vwb:Link context='regist' absolute='true' format='url'/>">注册</a>
				<a class="largeButton" href="<vwb:Link context='switchTeam' absolute='true' format='url'/>">登录</a>
			</div>
			<ul id="nav">
				<li class="current"><a href="${contextPath}/index.jsp">概述</a></li>
				<li id="step_1"><a href="${contextPath}/help/introduction.jsp">科研在线是什么？</a></li>
				<li><a href="${contextPath}/help/tutorial.jsp">使用指南</a></li>
				<li><a href="${contextPath}/help/history.jsp">更新记录</a></li>
				<li><a href="<vwb:Link context='shareFile' format='url'/>">快速分享</a></li>
			</ul>
		</div>
		
		<div id="banner"><img src="lynx.png"></div>
		<div id="feature">
			<div class="lynx-feature">
				<h3 class="feature-title"><span class="introIcon ui"></span>舒适的界面设计</h3>
				<p>清新、简约的界面风格，为您的团队工作助力。</p>
			</div>
			
			<div class="lynx-feature">
				<h3 class="feature-title"><span class="introIcon tag"></span>自由的标签模式</h3>
				<p>采用多标签模式，对资料进行多维度、灵活的组织与管理。</p>
			</div>
			
			<div class="lynx-feature">
				<h3 class="feature-title"><span class="introIcon sub"></span>便捷的专题功能</h3>
				<p>可将关联性很强的资料组合成一个专题。平台为专题提供了易用的浏览视图，如为纯图片专题提供相册浏览功能。</p>
			</div>
			
			<div class="clear"></div>
			
			<div class="lynx-feature">
				<h3 class="feature-title"><span class="introIcon star"></span>醒目的资料星标</h3>
				<p>通过点击资料标题前方的<span class="uncheckedStar">标记为您感兴趣或认为重要的资料打星标。星标资料访问更便捷。</p>
			</div>
			
			<div class="lynx-feature">
				<h3 class="feature-title"><span class="introIcon search"></span>快捷的检索导航</h3>
				<p>用户可将常用的标签检索条件保存为快捷导航，减少多次输入检索条件。</p>
			</div>
			
			<div class="lynx-feature">
				<h3 class="feature-title"><span class="introIcon note"></span>体贴的工作记录</h3>
				<p>用户常用的资料及工作历史自动记录至“常用”和“历史记录”中，用户可方便的回到上次工作状态。</p>
			</div>
			<div class="clear"></div>
			
		</div>
		<jsp:include page="/help/footer.jsp"></jsp:include>
		<div id="footer">
			<jsp:include page="/Version.jsp"></jsp:include>
		</div>
	</div>
</body>
</html>