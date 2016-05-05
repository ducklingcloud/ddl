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
<title>科研在线文档库-组合使用与管理帮助</title>
</head>
<body class="texure">
	<div class="ui-wrap">
		<jsp:include page="/ddlHeader.jsp"></jsp:include> 
		<div id="content">
			<div id="ddl-intro" class="ddl-feature">
				<h1>组合使用与管理帮助</h1>
			</div>
			
			<div id="ddl-tagHelp">
				<p>文档库为关联性较强的文档提供了“组合”的方式，用户可以将若干文档存放在一个组合里，便于查找。创建组合的方式非常简单，只需在文档展示区中勾选需要的文档，点击下方编辑条中的“组合”按钮即可。</p>
				<p><img src="${contextPath}/jsp/aone/images/BH-1.png"/></p>
				<p>进入组合后，用户可以方便的浏览组合中所包含的文档，也可以随时在组合中添加其他资源，以及解散组合。平台为组合提供了易用的浏览视图，如为纯图片专题提供相册浏览功能。</p>
				<p><img src="${contextPath}/jsp/aone/images/BH-2.png"/></p>
				<p>当给整个组合打标签时，该标签将被分别添加的组合内的所有文档上，用户不用担心需要解散组合时原有的标签信息会丢失。</p>
			</div>
		
		</div>
		
		<jsp:include page="/help/footer.jsp"></jsp:include>
		<div id="footer">
			<jsp:include page="/Version.jsp"></jsp:include>
		</div> 
	</div>

</body>
</html>