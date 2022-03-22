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
	$("#ddlNav-qa").parent("li").addClass("active");
});
</script>
<title>常见问题-科研在线团队文档库</title>
</head>
<body class="texure">
	<div class="ui-wrap">
	
		<jsp:include page="/ddlHeader.jsp"></jsp:include>
		
		<div id="content">
			<div id="ddl-intro" class="ddl-feature">
				<h1>常见问题</h1>
			</div>
			
			<div id="ddl-qa">
					<ol>
						<li>
							<p class="question">团队文档库是免费使用的吗？</p>
							<p>团队文档库目前提供的所有功能都是免费的。</p>
						</li>
						<li>
							<p class="question"> 团队空间有没有人数限制？</p>
							<p>目前没有人数限制，可以任意邀请用户加入团队。</p>
						</li>
						<li>
							<p class="question">团队空间和个人空间有什么区别？</p>
							<p> 云端文件可存储在团队空间或个人空间中。团队空间是由团队成员组成的共享空间，可多人协作。个人空间是私人空间，在该空间中存放的文档只有自己可见，可以作为用户便捷的云端硬盘。</p>
						</li>
						<li>
							<p class="question">用户能不能设定进入团队后首先进入的页面 </p>
							<p>可以。管理员在团队设置中的“默认首页”一栏，可以定制默认首页。</p>
						</li>
						<li>
							<p class="question">用户能不能自己设定登录文档库后首先进入哪个团队？ </p>
							<p>可以。在首页设置中的“个人偏好”一栏，可以定制默认首页。</p>
						</li>
				
						<li>
							<p class="question">一个人可以属于多个团队吗？ </p>
							<p>可以。用户可以创建多个团队，也可以加入其他人创建的团队。团队之间相互独立。</p>
						</li>				
						<li>
							<p class="question">团队文档库中，有哪些内容是全团队共享的，哪些内容是个性化的？ </p>
							<p>所有的文档、文档的标签信息以及团队动态消息都是全团队共享的。星标文档、登录后进入的默认首页是每位用户根据自己的使用情况设定的。</p>
						</li>
						<li>
							<p class="question">能大批量上传文件吗？</p>
							<p>可以。一次可上传多个文件。</p>
						</li>
						<li>
							<p class="question">如何下载科研在线客户端？</p>
							<p> 1) iOS版，在App Store中搜索“科研在线”进行查询，在结果列表中点选“科研在线”，免费下载安装。<br/>
							    2) Android版，<a href="http://www.escience.cn/apks/ddl-latest.apk">点击此处下载</a>
							</p>
						</li>
						<li>
							<p class="question">我有问题该如何反馈或求助？</p>
							<p>您可以通过首页最右侧的-“<a href="http://iask.cstnet.cn/?/home/explore/category-11">意见反馈</a>”直接发送您的问题给我们，我们收到后将及时反馈。此外，为了方便用户，我们还开通了<a href="http://e.weibo.com/dcloud" target="_blank">新浪微博</a>、<a href="http://t.qq.com/keyanzaixian" target="_blank">腾讯微博</a>官方账号，您可以通过上述任一渠道联系我们，提出意见或建议。</p>
						</li>
						
						
					</ol>
			</div>
		
		</div>
		
		<jsp:include page="/help/footer.jsp"></jsp:include>
		<div id="footer">
			<jsp:include page="/Version.jsp"></jsp:include>
		</div>
	</div>

</body>
</html>