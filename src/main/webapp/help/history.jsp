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
	<link href="${contextPath}/jsp/aone/css/index.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="${contextPath}/scripts/jquery/jquery-1.8.3.min.js"></script>
	<script type="text/javascript" src="${contextPath}/jsp/aone/js/backToTop-jQuery.js"></script>
	<title>更新记录 - 科研在线</title>
</head>

<body>
	<div class="ui-wrap">
		<div id="aoneBanner" class="std">
			<a id="ROL" href="${contextPath}/index.jsp"><span>科研在线</span></a>
			<div class="ui-RTCorner" id="userCtrl">
				<a class="largeButton green" href="<vwb:Link context='regist' absolute='true' format='url'/>">注册</a>
				<a class="largeButton" href="<vwb:Link context='switchTeam' absolute='true' format='url'/>">登录</a>
			</div>
			<ul id="nav">
				<li><a href="${contextPath}/index.jsp">概述</a></li>
				<li><a href="${contextPath}/help/introduction.jsp">科研在线是什么？</a></li>
				<li><a href="${contextPath}/help/tutorial.jsp">使用指南</a></li>
				<li class="current"><a href="${contextPath}/help/history.jsp">更新记录</a></li>
				<li><a href="<vwb:Link context='shareFile' format='url'/>">快速分享</a></li>
			</ul>
		</div>
		
		<div class="content" id="history">
			<h1>更新记录</h1>
			<h4>2012年4月30日前（陆续更新发布）</h4>
			<ul>
				<li>改进的内容组织与交互模式（其中支持标签模式）</li>
				<li>支持word、ppt的在线浏览</li>
				<li>发布网页内容采集工具</li>
				<li>支持新浪微博、QQ账号登录</li>
			</ul>
			<h4>2012年3月31日</h4>
			<ul>
				<li>发布版本DCT 6.1.0</li>
				<li>发布iPhone手机客户端（<a id="iphone" href="http://itunes.apple.com/cn/app/ke-yan-zai-xian/id495109931" target="_blank" title="连接到App Store安装应用"><span>获取</span></a>）</li>
				<li>支持PDF文件的在线预览（需要最新的浏览器版本）</li>
				<li>支持通讯录的批量导入导出（支持Outlook, Outlook Express, Thunderbird, foxmail）</li>
				<li>支持团队成员退出</li>
				<li>页面创建入口统一</li>
				<li>支持页面浏览模式下的文件上传与下载</li>
			</ul>
			<h4>2011年11月28日</h4>
			<ul>
				<li>发布最新版本：科研在线2011版，版本号 DCT 6.0.0</li>
				<li>支持个人空间和多团队协作，整合消息模块</li>
				<li>增加个人通讯录与团队通讯录的整合</li>
				<li>改善编辑器功能，具备清除格式和使用HTML源代码编写能力</li>
				<li>优化集合首页信息呈现方式，允许对页面、文件分别筛选查看</li>
				<li>整合快捷工具栏、消息通知和个人账户控制工具栏</li>
			</ul>
			
			<h4>2011年9月22日</h4>
			<ul>
				<li>发布Android手机客户端，支持团队更新和集合内容的查看与搜索</li>
				<li>集合首页增加网格模式，支持对有关联的内容进行整理和呈现</li>
				<li>新增快速上传、快速创建页面功能和快捷工具栏</li>
				<li>实现文件与页面混排功能，支持文件的版本更新</li>
				<li>改进编辑页面时的锁定、过期解锁、自动保存和恢复机制</li>
				<li>改进“关注”功能，自动对自己创建的页面进行关注</li>
			</ul>
			
			<h4>2011年7月8日</h4>
			<ul>
				<li>发布重要版本 DCT 5.1.49a</li>
				<li>增加全局内容的搜索功能，支持页面顶部快速搜索和详细搜索</li>
				<li>重构分享和关注功能</li>
				<li>改进编辑冲突处理机制，引入用户信息</li>
				<li>增加邀请加入团队机制和相应管理、配置功能</li>
				<li>新增用户注册激活机制</li>
				<li>新增创建多个团队功能及相应的管理和切换功能</li>
				<li>新增图片和附件的区分和描述页，改进E2编辑器对文件和图片上传、嵌入的机制</li>
				<li>改进团队首页设计，支持快速查看团队内容和团队通讯录</li>
				<li>更新系统框架，采用新页面组织结构：“团队-集合-页面”三层结构组织内容；对界面框架进行相应调整</li>
				<li>升级编辑器，面向内容语义简化编辑工具，调整显示模式以适应长内容的编辑需要</li>
			</ul>
			
			<h4>2011年3月17日</h4>
			<ul>
				<li>发布A1第一版原型（即当前版本科研在线的内部原型），在内容管理体系中融入社会化元素</li>
				<li>增加对内容的关注机制、内容分享机制、评论和回复机制、更新推送和通知中心</li>
			</ul>
			
		</div>
		
		<jsp:include page="/help/footer.jsp"></jsp:include>
		<div id="footer">
			<jsp:include page="/Version.jsp"></jsp:include>
		</div>
	</div>
</body>
</html>
