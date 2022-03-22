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
	<title>科研在线是什么?</title>
	<link href="${contextPath}/jsp/aone/css/index.css" rel="stylesheet" type="text/css">
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
				<li class="current"><a href="${contextPath}/help/introduction.jsp">科研在线是什么？</a></li>
				<li><a href="${contextPath}/help/tutorial.jsp">使用指南</a></li>
				<li><a href="${contextPath}/help/history.jsp">更新记录</a></li>
				<li><a href="<vwb:Link context='shareFile' format='url'/>">快速分享</a></li>
			</ul>
		</div>
		
		<div id="content">
			<div class="content-title">
				<h1>科研在线是什么？</h1>
				<h2>科研在线帮助您与您的同伴在云端同步分享观点、文件和图片，积累知识、资料和数据
					<br/>通过协同编辑文档、追踪历史记录、在线讨论，实现协同工作
					<br/>让团队的知识协作与共享变得简单</h2>
			</div>
			
			<div class="content-through">
				<div id="archIntro">
					<dl>
						<dt>科研在线是面向团队的应用</dt>
						<dd>您需要创建或加入一个<strong>团队</strong>，邀请您的同伴加入团队，与同伴一起使用科研在线。</dd>
						
						<dt>科研在线主要用来分享信息和知识</dt>
						<dd>信息和知识的载体是<strong>页面</strong>，相当于一篇文章。</dd>
						<dd>将想法写成页面，或在页面中上传任意类型的文件作为附件，就可以与团队分享。</dd>
						
						<dt>科研在线需要将内容整理分类</dt>
						<dd>页面需要放到<strong>集合</strong>中，每个集合就像一个文件夹。</dd>
						
						<dt>科研在线提供了社会化的沟通模式</dt>
						<dd>您可以对页面<strong>留言</strong>评论，或回复其他人的留言。</dd>
						<dd>您可以将页面<strong>分享</strong>给特定的同伴，就像发邮件。</dd>
						<dd>当添加了新页面，或是有更新时，您都可以从首页看到。</dd>
						
						<dt>科研在线支持协同编辑</dt>
						<dd>可以由多个用户编辑同一个页面，共同完成一份文档。</dd>
					</dl>
				</div>
				<div id="archFigure"></div>
				<div class="ui-clear"></div>
				<hr/>
			</div>
			<div id="steps" class="content-through">
				<h3>怎样开始？</h3>
				<ul>
					<li><h4>注册</h4>
						<p>使用有效邮箱，注册一个Duckling帐号</p>
					</li>
					<li><h4>创建团队</h4>
						<p>建议您以实际的工作或学习团队为基础</p>
					</li>
					<li><h4>邀请用户</h4>
						<p>用邮件邀请用户加入团队</p>
					</li>
					<li><h4>创建集合</h4>
						<p>创建集合以存放内容</p>
					</li>
					<li><h4>创建页面</h4>
						<p>编写内容成为页面，上传附件</p>
					</li>
				</ul>
				<div class="ui-clear"></div>
				<hr/>
			</div>
			<div id="whatToDo" class="content-through">
				<h3>可以做什么？</h3>		
				<dl>
					<dt>分享观点、思想</dt>
					<dd>将内容写成页面，上传附件，所有成员都可以看到</dd>
					<dd>可以针对内容开展讨论</dd>
										
					<dt>同步共享文件、资料和数据</dt>
					<dd>上传文件，与团队共享</dd>
					<dd>在一个页面上传多个文件，配以必要的文字说明，资料再多也能井井有条</dd>
					<dd>对文件资料的更新，所有成员都可以第一时间得到同步的资料</dd>
					
					<dt>随时了解最新的消息</dt>
					<dd>团队中内容的任何添加或变更，都通过首页的动态向您呈现</dd>
					<dd>关注特定的成员或内容页面，不错过重要的变更</dd>
				</dl>
				<dl>
					<dt>建立文档库、资料库</dt>
					<dd>团队成员共同编写和整理内容，高效而互不干扰</dd>
					<dd>利用集合将文件和资料进行初步分类</dd>
					<dd>在页面中嵌入相关链接，构建如Wiki一般的知识库，易于浏览</dd>
					<dd>完整的内容列表和高效的搜索引擎，帮助快速找到想要的文档资料</dd>
					
					<dt>项目协作</dt>
					<dd>将项目的目标、规划、日程安排等公布给团队</dd>
					<dd>时时更新进展，所有成员充分了解现况，有效协调配合，保障项目进程</dd>
					<dd>建立分任务页面，分别推送，实现任务分配</dd>
					<dd>快捷方便地编写工作日志，编写反馈，实时了解项目进度</dd>
					
				</dl>
				<dl>
					<dt>协同编写文档</dt>
					<dd>支持多人共同编写页面，避免文档版本不同的合并困难</dd>
					<dd>共同编写文档，互相修正纰漏</dd>
					<dd>建立点子收集或问题汇报页面，所有人都可以将信息记录在一起，便于查阅整理</dd>
					<dd>强大的版本记录和差异追踪，方便编写过程中错误的修正</dd>
					
					<dt>云端服务</dt>
					<dd>数据和服务都来自云端，您只需要关注真正有价值的内容，而非系统维护</dd>
					<dd>高同步性和异地访问特性，即使团队分散（合作项目）或出差在外，都能与团队紧密联系</dd>
				</dl>
				<div class="ui-clear"></div>
			</div>
		</div>
		
		<jsp:include page="/help/footer.jsp"></jsp:include>
		<div id="footer">
			<jsp:include page="/Version.jsp"></jsp:include>
		</div>
	</div>
</body>
</html>
