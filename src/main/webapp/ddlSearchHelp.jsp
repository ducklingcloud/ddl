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
<title>科研在线文档库-文档检索使用帮助</title>
</head>
<body class="texure">
	<div class="ui-wrap">
		<jsp:include page="/ddlHeader.jsp"></jsp:include> 
		<div id="content">
			<div id="ddl-intro" class="ddl-feature">
				<h1>文档检索使用帮助</h1>
			</div>
			
			<div id="ddl-tagHelp">
				<p>在顶部导航栏中的搜索框中输入关键字并按回车键，即可搜索到当前团队空间中所有包含该关键字的页面（标题或正文中包含）及文件（标题中包含）。</p>
				<p><img src="${contextPath}/jsp/aone/images/SH-1.png"/></p>
				<p>另外，用户还可以通过修改时间、文档类型及多标签组合快速定位文档。如下图所示，文档展示区中的文档可按照时间、类型排序，下图中显示的为所有具有“论文”标签的文档中，属于“上月”创建的“页面”。</p>
				<p><img src="${contextPath}/jsp/aone/images/SH-2.png"/></p>
				<p>用户还可以借助多标签组合的方式快速定位文档。选中一个标签（如下图中“论文”），将显示所有贴有该标签的文档。点击其它标签右侧的加号，进行多选。</p>
				<p><img src="${contextPath}/jsp/aone/images/SH-3.png"/></p>
				<p>通过标签组合，可以更准确的定位文档，效果如下图。用户还可以点击右上角的按钮，将该标签组合保存到快捷导航栏中，作为您私人的常用检索条件。</p>
				<p><img src="${contextPath}/jsp/aone/images/SH-4.png"/></p>
			</div>
		
		</div>
		
		<jsp:include page="/help/footer.jsp"></jsp:include>
		<div id="footer">
			<jsp:include page="/Version.jsp"></jsp:include>
		</div> 
	</div>

</body>
</html>