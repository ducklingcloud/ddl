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
	$("#ddl-nav").remove();
});
</script>
<title>团队文档库-用户邀请使用帮助</title>
</head>
<body class="texure">
	<div class="ui-wrap">
		<jsp:include page="/ddlHeader.jsp"></jsp:include> 
		<div id="content">
			<div id="ddl-intro" class="ddl-feature">
				<h1>用户邀请使用帮助</h1>
			</div>
			
			<div id="ddl-tagHelp">
				<p>创建团队后，管理员可以通过点击团队名称右侧的管理按钮（如下图），邀请他人加入自己的团队。文档库将向受到邀请的人发送带有邀请链接的电子邮件，他们通过该链接注册文档库并加入团队。如果被邀请人已经是文档库用户，他们将在系统中收到通知消息。</p>
				<p><img src="${contextPath}/jsp/aone/images/IH-1.png"/></p>
				<p>只要知道用户的邮箱地址，就可以完成邀请。邮箱导入有两种方式，一种是手动输入，如下图，这种方式适合加入的成员数目较少时。在输入邮箱过程中，系统会自动帮助您检索文档库已有用户。如果您要邀请的成员已经是文档库用户，他的邮箱将会随着您的内容键入提示给您。</p>
				<p><img src="${contextPath}/jsp/aone/images/IH-2.png"/></p>
				<p>另一种，是通过文件导入，文档库支持csv和vCard格式的地址簿文件。如下图左，点击“导入地址簿”按钮，并导入文件。平台为用户提供了从常见邮件客户端导出地址簿的方法。</p>
				<p><img src="${contextPath}/jsp/aone/images/IH-3.png"/></p>
				<p>将文件中的所有联系人展示出来后，管理员通过进一步勾选，确定邀请的人员名单，附上留言后发送邮件。</p>
				<p><img src="${contextPath}/jsp/aone/images/IH-4.png"/></p>
				<p>被邀请人的邮箱将收到如下邮件，若同意加入团队，点击该邮件中的链接即可。</p>
				<p><img src="${contextPath}/jsp/aone/images/IH-5.png"/></p>
			</div>
		
		</div>
		
		<jsp:include page="/help/footer.jsp"></jsp:include>
		<div id="footer">
			<jsp:include page="/Version.jsp"></jsp:include>
		</div> 
	</div>

</body>
</html>
