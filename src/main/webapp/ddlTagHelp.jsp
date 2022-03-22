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
<link href="${contextPath}/jsp/aone/css/index-nov2013.css" rel="stylesheet" type="text/css"/>
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
<title>团队文档库-标签使用帮助</title>
</head>
<body class="texure">
	<div class="ui-wrap">
		<jsp:include page="/ddlHeader.jsp"></jsp:include> 
		<div id="content">
			<div id="ddl-intro" class="ddl-feature">
				<h1>标签使用帮助</h1>
			</div>
			
			<div id="ddl-tagHelp">
				<p>标签帮助您<font class="light">按类别整理文档，</font>可以包括“通知公告”、“项目文档”、“学习资料”等任意类别。
				它可以实现文件夹的所有用途，而且还另有妙用：您可以<font class="light">为一个文档添加多个标签</font>。</p>
				<p>标签的管理共分为两级，第一级为“标签集”，它可以将标签分类，作用对象为标签；第二级为“标签”，作用对象为文档。</p>
				<p><font class="stress">标签是全团队共享信息，由全体队员共同管理维护。</font></p>
				<p><img width="800" src="${contextPath}/jsp/aone/images/TH_tags_new.png"/></p>
				<p>如上图所示，左侧为某团队的标签导航区。细致的分类，保证了团队成员能快速找到目标文档，不用担心文档数目太多而不好管理。</p>
				<p>下面分四部分进行详细介绍：创建标签、删除标签、管理标签以及使用技巧。</p>
				
				<h2>创建标签</h2>
				<p>创建标签有两种方法：</p>
				<p>1、<font class="light">创建可以添加到任何文档的标签。</font>点击左下图所示位置，进入“标签管理”界面，在右下图所示位置可添加标签。您可以在此界面中批量创建新标签，还可以新建标签集，将标签分类管理。</p>
				<p style="text-align:center;"><img src="${contextPath}/jsp/aone/images/TH-manage-new.png"/><img src="${contextPath}/jsp/aone/images/TH_add.png"/></p>
				<p>2、<font class="light">为特定文档创建新标签。</font>在文档展示区中，点击文档标题右方的加号，可以直接给文档打上标签。该标签将被保存为“常用标签”，供日后使用。</p>
				<p><img width="800"  src="${contextPath}/jsp/aone/images/TH_add2_new.png"/></p>
				<p>添加标签时，可以新建标签，也可以从常用标签中选择。新建标签时，系统将自动识别该标签是否存在以避免重复。</p>
				<p><img src="${contextPath}/jsp/aone/images/TH_addPool.png"/></p>
				<p>还可以同时为多个文档打标签。</p>
				<p><img width="800" src="${contextPath}/jsp/aone/images/TH_addTogether_new.png"/></p>
				
				<h2>删除标签</h2>
				<p>点击文档标题，进入内容展示页面。在标题下方，可以看到该文档具有的标签。可以在此处将您认为不需要的标签删除。</p>
				<p><img src="${contextPath}/jsp/aone/images/TH_delete.png"/></p>
				
				<h2>管理标签</h2>
				<p>点击图1-1所示的钳子按钮，进入标签管理界面。在建立标签及使用标签集对其归类整理后，可以达到如下效果。每个标签只能属于一个标签集。</p>
				<p><img src="${contextPath}/jsp/aone/images/TH_tagShow.png"/></p>
				
				<h2>使用技巧</h2>
				<p>除了“归类整理”的作用，标签还可以帮助您快速检索文档。选中一个标签（如下图中“论文”），将显示所有贴有该标签的文档。点击其它标签右侧的加号，进行多选。</p>
				<p><img src="${contextPath}/jsp/aone/images/TH_tagTrick.png"/></p>
				<!-- <p>通过标签组合，可以更准确的定位文档，效果如下图。您还可以点击右上角的按钮，将该标签组合保存到快捷导航栏中，作为您私人的常用检索条件。</p>
				     <p><img src="${contextPath}/jsp/aone/images/TH_tagTrick2.png"/></p> -->
			</div>
		
		</div>
		
		<%-- <jsp:include page="/help/footer.jsp"></jsp:include> --%>
		<div id="footer">
			<jsp:include page="/Version.jsp"></jsp:include>
		</div> 
	</div>

</body>
</html>
