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
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<link href="${contextPath}/jsp/aone/css/index.css" rel="stylesheet" type="text/css"/>
<link href="${contextPath}/jsp/aone/css/index-may2012.css" rel="stylesheet" type="text/css"/>
<link href="${contextPath}/images/favicon.ico" rel="shortcut icon" type="image/x-icon" />
<script type="text/javascript" src="${contextPath}/scripts/jquery/jquery-1.8.3.min.js"></script>
<script type="text/javascript">
$(document).ready(function(){
	$("#escienceMenu").mouseenter(function(){
		$("#es-pullDownMenu").show();
	});
	$("#escienceMenu").mouseleave(function(){
		$("#es-pullDownMenu").hide();
	});
	$(".active").removeClass("active");
	$("#ddlNav-newer").parent("li").addClass("active");
});
</script>
<title>新手指南-科研在线团队文档库</title>
</head>
<body class="texure">
	<div class="ui-wrap">
		<jsp:include page="/ddlHeader.jsp"></jsp:include>
		
		<div id="content">
			<div id="ddl-intro" class="ddl-feature">
				<h1>新手指南<a class="ui-iconButton help smallText" href="${contextPath}/help/lynxIntro/introSteps.jsp" target="_blank">查看演示</a></h1>
			</div>
			
			<div id="ddl-newer">
				<div id="steps" class="content-through newer">
					<h3>怎样开始？</h3>
					<ul>
						<li><h4>注册</h4>
							<p>使用有效邮箱注册账号，邮箱会收到一封邮件，点击其中的激活链接后，账号注册成功。</p>
						</li>
						<li><h4>创建团队</h4>
							<p>可为您的团队设置名称以及网址。</p>
						</li>
						<li><h4>邀请用户</h4>
							<p>用邮件邀请用户加入团队，支持邮箱批量导入。</p>
						</li>
						<li><h4>开始使用</h4>
							<p>新建协作文档或上传文件，建立共享文档库。支持成员之间社会化的沟通协作。</p>
						</li>
					</ul>
					<div class="ui-clear"></div>
				</div>
				
				<div class="content-through newer">
					<h3>首页</h3>
					<p>这里汇集了您所有的团队信息。您可以在这里查看您的所有消息以及设置个人资料。</p>
					<p><img src="${contextPath}/jsp/aone/images/ddlNewer-1-index.png" class="newImg"></p>
				</div>
				
				<div class="content-through newer">
					<h3>团队空间</h3>
					<p>团队空间是您团队的云端工作室，包含团队所需的全部文档资料，并支持团队成员间的在线协作。用户可以建立多个团队，团队空间之间相互独立。</p>
					<p><img src="${contextPath}/jsp/aone/images/ddlNewer-2-team.png" class="newImg"></p>
				</div>
				
				<div class="content-through newer">
					<h3>个人空间</h3>
					<p>您的私人空间。帮助您随时随地上传资料，随时随地下载、浏览。空间中还记录了您完整的工作历史。</p>
				</div>
			</div>
		
		</div>
		
		<jsp:include page="/help/footer.jsp"></jsp:include>
		<div id="footer">
			<jsp:include page="/Version.jsp"></jsp:include>
		</div>
	</div>

</body>
</html>