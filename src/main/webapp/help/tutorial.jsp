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
	<title>使用指南 - 科研在线</title>
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
				<li class="current"><a href="${contextPath}/help/tutorial.jsp">使用指南</a></li>
				<li><a href="${contextPath}/help/history.jsp">更新记录</a></li>
				<li><a href="<vwb:Link context='shareFile' format='url'/>">快速分享</a></li>
			</ul>
		</div>
		
		<div id="content">
			<div class="content-title tutorial">
				<ul class="titleDivide">
					<li id="scenarioTab" class="current"><a>应用场景</a></li>
					<li id="operationTab"><a href="manual.jsp">基本操作</a></li>
					<li id="conceptTab"><a href="concept.jsp">概念和名词</a></li>
				</ul>
			</div>
			<div id="scenario" class="content-through sub">
				<div class="tutorialBlock">
					<p class="img"><span class="ROL-text" title="科研在线">&nbsp;</span>通过简单的应用模式，可以满足团队中多种多样的协作需要。</p>
				</div>
				<div class="tutorialBlock">
					<h3>分享信息和思想</h3>
					<p>当工作中有一些想法，看论文作了笔记，希望与同伴们分享。给所有人群发邮件？立即找身边的同事讨论？似乎都不是最好的方式。</p>
					<p>我们给您的建议是：<a class="help" href="manual.jsp#createPage"><em>将信息或想法写下来</em></a>。当同事们来到科研在线时，就能在<a class="help" href="manual.jsp#teamUpdate">团队更新</a>中看到你分享的信息和想法。</p>
					<h4>除了文字，还可以上传文件</h4>
					<p>除了用文字描述，您还以上传各种格式的文件作为<a class="help" href="manual.jsp#uploadFile">附件</a>与大家分享。您可以一次上传多个文件，这些文件会被放在一起，您还可以给每个文件加上说明。不用再费心打包文件，每个文件的作用和关注点都可以写得一清二楚。</p>
				</div>
				<div class="tutorialBlock">
					<h3>同步分享项目信息</h3>
					<p>项目中众多的规划文档和文献，各个阶段产生的数据文件、资料、笔记和总结，以及最后生成的报告，常常分散在四处，叫人无从查找和整理。更不用提在邮件里传来传去的各种版本，总是弄不清究竟哪个“latest”是最新的，不是误删了新版本、就是错用了旧版本。</p>
					<p>将这些资料都存放在科研在线的团队空间吧！文档类的内容直接在<a class="help" href="manual.jsp#createPage">页面</a>中编写，各种格式的文件都作为附件上传。这样大家都能方便地找到这些资料了。</p>
					<h4>总能看到最新的版本</h4>
					<p>更重要的是，无论谁修改了文档，或更新了附件，所有人都会知道。并且任何时候，你都能在这里看到最新的版本。</p>
					<h4>随时追溯历史版本</h4>
					<p>强大的版本控制技术还保存了所有的<a class="help" href="manual.jsp#version">历史版本</a>，可以轻松查看过去的版本情况，在几个版本间进行<a class="help" href="manual.jsp#compare">对比</a>。</p>
				</div>
				<div class="tutorialBlock">
					<h3>组建知识库</h3>
					<p>每个科研团队都积累了大量专业知识和素材。将这些资料整理成知识库，能够为后续的工作带来助益，也能够帮助新加入的研究人员和学生迅速融入团队工作中。</p>
					<p>科研在线通过<a class="help" href="manual.jsp#createCollection">集合</a>组织资料和文件，可以将资料分门别类存放。同时，每个集合可以自由地<a class="help" href="manual.jsp#collectionGrid">按照不同主题存放</a>资料和文件。最后，在资料间使用链接，将相关的资料进行关联。三种方法结合在一起，就能够架构出一个简单的知识库，帮助知识的整理和查找。</p>
				</div>
				<div class="tutorialBlock">
					<h3>“你应该看看这个”：分享</h3>
					<p>在团队空间里发现一篇文字，希望张三也来看看？写了一份实验方案草稿，需要李四帮忙修改？给他们<a class="help" href="manual.jsp#recommend">分享</a>吧！</p>
					<p>选定几个团队成员，将一个页面或文件分享给他们，还可以附上几句留言。他们将收到特别的<a class="help" href="manual.jsp#msgAlert">通知</a>，从而不会错过您分享的重要信息。</p>
					<p class="img"><img src="figure/t-recommend.jpg"/></p>
				</div>
				<div class="tutorialBlock">
					<h3>意见簿/登记表/项目进度</h3>
					<p>借助协同编辑功能（即多人编辑同一个页面），您可以要求团队成员将关于项目的意见写到您创建的页面中。这样您创建的页面就成了意见簿：</p>
					<ul>
						<li>您可以先创建页面，写下希望收集哪类信息，给出一个信息模板（例如表格，列出“内容”、“提交者”等项目）；</li>
						<li>然后用<a class="help" href="manual.jsp#recommend">分享</a>功能告知团队成员来填写内容；</li>
						<li>建议您<a class="help" href="manual.jsp#follow">关注</a>这个页面，这样每当有人填写了内容，您就能够很方便地看到<a class="help" href="manual.jsp#msgAlert">通知提示</a>，而不用每天手动去检查页面的更新情况。</li>
					</ul>
					<p>用类似的方法，您还可建立签到表、登记表、信息统计表等等。</p>
					<h4>项目进度追踪</h4>
					<p>如果您愿意将内容写地更完善，用这样的方法还可以实现项目进度追踪。</p>
					<ul>
						<li>首先，您可以创建一个页面，用来明确每个成员的任务、提交结果的方式和时间节点；</li>
						<li>其次，您需要创建一个页面，做成空白的进度表的样子，要求填写已做的工作、实际的进度、遇到的困难等等，并上传必要的产出物；</li>
						<li>最后，您只需要<a class="help" href="manual.jsp#followMember">关注每个成员</a>，就可以轻松地通过<a class="help" href="manual.jsp#msgAlert">消息通知</a>来查看项目的实际进度了。</li>
					</ul>
				</div>
			</div>
			<div class="content-through">
				<ul class="titleDivide bottom">
					<li id="scenarioTab" class="current"><a href="#scenario">应用场景</a></li>
					<li id="operationTab"><a href="manual.jsp">基本操作</a></li>
					<li id="conceptTab"><a href="concept.jsp">概念和名词</a></li>
				</ul>
			</div>
			<div class="ui-clear"></div>
		</div>
		
		<jsp:include page="/help/footer.jsp"></jsp:include>
		<div id="footer">
			<jsp:include page="/Version.jsp"></jsp:include>
		</div>
	</div>
</body>
</html>
