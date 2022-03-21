<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="net.duckling.ddl.common.*"%>
<%@taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
<%
	pageContext.setAttribute("contextPath", request.getContextPath());
	VWBContext context = VWBContext.createContext(request, "error");
	String baseURL = null;
	if ((request.getServerPort() == 80)
			|| (request.getServerPort() == 443))
		baseURL = request.getScheme() + "://" + request.getServerName()
				+ request.getContextPath();
	else
		baseURL = request.getScheme() + "://" + request.getServerName()
				+ ":" + request.getServerPort()
				+ request.getContextPath();
%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="${contextPath}/jsp/aone/css/index.css" rel="stylesheet"
	type="text/css" />
<link href="${contextPath}/jsp/aone/css/index-nov2013.css"
	rel="stylesheet" type="text/css" />
<link href="${contextPath}/jsp/aone/css/index-aug2014.css?v=${aoneVersion}"	
	rel="stylesheet" type="text/css" />
<link href="${contextPath}/images/favicon.ico" rel="shortcut icon"
	type="image/x-icon" />
<script type="text/javascript"
	src="${contextPath}/scripts/jquery/jquery-1.8.3.min.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		$("#escienceMenu").mouseenter(function() {
			$("#es-pullDownMenu").show();
		});
		$("#escienceMenu").mouseleave(function() {
			$("#es-pullDownMenu").hide();
		});
		$(".active").removeClass("active");
		$("#ddlNav-news").parent("li").addClass("active");
	});
</script>
<title>动态-科研在线团队文档库</title>
</head>
<body class="texure">
	<jsp:include page="/ddlHeader.jsp"></jsp:include>
	<div id="ddl-intro" class="help">
		<div class="ui-wrap">
			<div class="left-intro" style="width:100%">
				<h2>
					<a href="#" class="current-nav" style="margin-right: 0px;">动态</a>
				</h2>
			</div>
		</div>
	</div>
	<div class="newsList news-1">
		<div class="ui-wrap">
			<h2>支持链接分享</h2>
			<p class="newsHint">2014-09-16 11:56 | 科研在线 </p>
			<p>还在为将文件分享给团队外的好友好头疼吗？快试一下团队文档库新推出的一键快速分享吧！我们收到好多老师的需求，需要将文件分享给相关人员，而他又不是当前团队的成员，现在您可以使用我们最新推出的公开分享链接功能！</p>
			<p class="img"><img src="${contextPath}/jsp/aone/images/news1-share-1.png"></img></p>
			<p>只需要点击文件上的分享按钮下的【公开分享】，就会创建一个分享链接。您可以将分享链接通过QQ、微信、email等分享给相关人员。对方无需在团队文档库上注册账号或加入团队，打开链接就能浏览文件! 当然如果您对文件有私密性要求，可以通过添加提取码的方式，对文件加一层保护。共享结束后，您可以在分享历史中选择随时关闭分享链接。</p>
			<p class="img"><img src="${contextPath}/jsp/aone/images/news1-share-2.png"></img></p>
		</div>
	</div>
	
	<div class="newsList news-2">
		<div class="ui-wrap">
			<h2>个人空间同步版和其它空间的相互复制</h2>
			<p class="newsHint">2014-09-17 10:40 | 科研在线 </p>
			<p>团队文档库推出了同步客户端后，收到了很多老师的好评：中科院终于有自己的dropbox了。目前文件同步到云端后，是放在个人空间同步版中。本次更新支持个人空间同步版的文件复制到其他空间了。如果您要将文件分享给团队成员，可以在网页端操作，在个人空间同步版里，选择文件，复制到目标团队中。</p>
			<p class="img"><img src="${contextPath}/jsp/aone/images/news1-copy.png"></img></p>
		</div>
	</div>
	
	<div class="newsList news-2">
		<div class="ui-wrap">
			<h2>千呼万唤始出来，令人激动的桌面客户端</h2>
			<p class="newsHint">2014-09-17 10:40 | 科研在线 </p>
			<p>团队文档库在网页版、移动版之后推出了具有同步功能的桌面客户端，而且一口气推出了Windows，Mac，Linux三个版本！ 您可以按需选择！</p>
			<p>桌面客户端安装简单，操作方便，在安装时设置一个同步文件夹，您可以拖拽文件到这个文件夹内，客户端将对这个文件夹里的文件和服务器上的文件进行自动同步。您不必在每次更新文件后都再把文件上传一次，只要开着客户端，这些操作都会被客户端自动完成，省时又省心。所有的文件都会同步到个人空间（同步版Beta）中。</p>
			<p>文件存储云端，可通过桌面客户端、网站随时获取，不必担心丢失。</p>
			<p>安装多台电脑，<strong>时刻保持单位和家里电脑的文件同步</strong>。还可以使用手机客户端，随时随地的获取文件。</p>
			<p>依托于中国科技网的网络环境，科学院用户可以享受到比其它同类网盘产品<strong>更快速</strong>的上传下载体验！空间无上限！还等什么，快去下载吧：<a href="http://ddl.escience.cn/download.jsp" target="_blank">http://ddl.escience.cn/download.jsp</a></p>
		</div>
	</div>
	
	<div id="footer">
		<jsp:include page="/Version.jsp"></jsp:include>
	</div>
</body>
</html>