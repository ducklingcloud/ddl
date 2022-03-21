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
		$("#ddlNav-feature").parent("li").addClass("active");
	});
</script>
<title>功能特性-团队文档库</title>
</head>
<body class="texure">
	<jsp:include page="/ddlHeader.jsp"></jsp:include>
	<div id="ddl-intro" class="help">
		<div class="ui-wrap">
			<div class="left-intro" style="width:100%" >
				<h2>
					<a href="#" class="current-nav" style="margin-right: 0px;">功能特性</a>
				</h2>
			</div>
		</div>
	</div>
	<div class="featureDetail" id="featureDetail-1">
		<div class="ui-wrap">
			<div class="feature-left">
				<h2>文档资料云存储，轻松管理共享文档</h2>
				<p>提供安全可靠的云存储服务。个人、团队的文档都可以安全的放置在云端，方便共享，不怕丢失。<br />
				提供目录+标签+搜索的方式，实现资料的有序管理。</p>
			</div>
			<div class="feature-right">
				<p><img src="${contextPath}/jsp/aone/images/fea-folder.png"></img></p>
			</div>
		</div>
	</div>
	
	<div class="featureDetail dark" id="featureDetail-2">
		<div class="ui-wrap">
			<div class="feature-right">
				<p><img src="${contextPath}/jsp/aone/images/ddlNewer-2-team.png"></img></p>
			</div>
			<div class="feature-left">
				<h2>团队专属空间</h2>
				<p>团队专属的协作空间，可以添加任意数量的成员，并设置成员权限。成员可以在这里共享文档和开展讨论，方便团队沟通协作。<br />
				个人独享空间，更加私密，可以备份个人资料，并方便与同事、朋友共享文档。</p>
			</div>
		</div>
	</div>
	
	<div class="featureDetail" id="featureDetail-3">
		<div class="ui-wrap">
			<div class="feature-left">
				<h2>注重文档协作</h2>
				<p>在线协作编辑文档，记录帮助团队记录，积累沉淀形成团队知识库。<br/>
				所有文件修改均有历史版本记录，可以对任何一个版本进行恢复。<br/>
				文档完美在线预览。 PPT还支持在线动画播放。</p>
			</div>
			<div class="feature-right">
				<p><img src="${contextPath}/jsp/aone/images/fea-together.png" style="border:none;"></img></p>
			</div>
		</div>
	</div>
	
	<div class="featureDetail dark" id="featureDetail-4">
		<div class="ui-wrap">
			<div class="feature-right">
				<p><img src="${contextPath}/jsp/aone/images/fea-commend.png"></img></p>
			</div>
			<div class="feature-left">
				<h2>高效交流</h2>
				<p>围绕文档展开交流，成员可以对文档展开评论或@成员，及时的通知、分享，减少邮件的流转和堆积，沟通畅通无阻。</p>
			</div>
		</div>
	</div>
	
	<div class="featureDetail" id="featureDetail-5">
		<div class="ui-wrap">
			<div class="feature-left">
				<h2>移动工作</h2>
				<p>提供Android和iPhone客户端，贴合手机使用习惯和交互模式 ，全面支持文档浏览，团队更新，文档分享、评论等，让您随时随地进行移动工作。<br />
				更支持拍照上传和录音上传，进行照片和资料备份，释放存储卡空间 。</p>
			</div>
			<div class="feature-right">
				<p><img src="${contextPath}/jsp/aone/images/ddl-and.png" style="border:none; width:300px; float:right"></img></p>
			</div>
		</div>
	</div>
	
	<div class="featureDetail dark" id="featureDetail-6">
		<div class="ui-wrap">
			<div class="feature-right">
				<p><img src="${contextPath}/jsp/aone/images/fea-windows.png" style="border:none"></img></p>
			</div>
			<div class="feature-left">
				<h2>多客户端同步</h2>
				<p>提供Windows，Mac，Linux的电脑同步客户端，时刻保持单位和家里文件同步，从此告别U盘。</p>
			</div>
		</div>
	</div>
	
	<div id="footer">
		<jsp:include page="/Version.jsp"></jsp:include>
	</div>
	<!-- <div class="consult">
		<h4>咨询方式</h4> 
		<div>
			<span>邮箱:</span> 
			<a href="mailto:vlab@cnic.cn">vlab@cnic.cn</a> 
		</div>
		<div>
			<span class="size">QQ:</span> <a href="###" title="点击这里联系DDL" class="size">1072762843</a>
			<div>
				<a style="font:normal 16px '微软雅黑';" href="http://iask.cstnet.cn/?/home/explore/category-11" title="意见反馈" class="size" target="_blank">意见反馈</a>
			</div>
		</div>
	</div> -->
</body>
</html>
