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
	$(".active").removeClass("active");
	$("#ddlNav-scene").parent("li").addClass("active");
});
</script>
<title>应用场景-团队文档库</title>
</head>
<body class="texure">
<jsp:include page="/ddlHeader.jsp"></jsp:include>
    <div id="ddl-intro" class="help">
		<div class="ui-wrap">
			<div class="left-intro" style="width:100%">
				<h2>
					<a href="#" class="current-nav" style="margin-right: 0px;">应用场景</a>
				</h2>
			</div>
		</div>
	</div>

	<div class="ui-wrap help-content">			
			<div id="ddl-scene">
			
				<div class="content-through newer">
					<h3>团队的知识库组建</h3>
					<p>课题组、实验室、机关单位等机构均可使用团队文档库作为团队内部的管理系统。团队成员可在文档库中查看通知公告、共享资料、协同编辑、汇报工作、开展讨论等等，并按照规章制度、项目、资料等任意自定义分类对文档进行归类整理。</p>
					<p>机构的所有资料完整记录，给日后工作提供很大帮助；新加入的成员也可以借助文档库快速融入到集体中。</p>
				</div>
				
				<div class="content-through newer">
					<h3>项目组的文档管理</h3>
					<p>项目中众多的规划文档和文献，各个阶段产生的数据文件、资料、笔记和总结，以及最后生成的报告，常常分散在四处，让人无从查找和整理。更不用提在邮件甚至是聊天工具中传来传去的各种版本，总是弄不清究竟哪个“latest”是最新的。</p>
					<p>项目组可将这些资料都存放在文档库中！既可以在平台中直接编写文字生成文档，也可以从本地上传各种格式的文件。文档全团队同步共享，无论谁做出修改，所有人都会知道。在任何时候，项目组成员都能在这里看到最新的版本。强大的版本控制技术还保存了文档所有的历史版本，可以让人轻松了解文档从初稿到完善的整个过程。</p>
				</div>
				
				<div class="content-through newer">
					<h3>科研数据的分享</h3>
					<p>科研团队希望对公众或者同行分享科研数据，并希望与同行之间进行交流与互动，同时也想要了解数据的使用情况。文档库支持公开或特定范围内的科研数据分享，轻松几步即可完成。</p>
					<p>公开分享：</p>
						<p>1、在文档库创建团队，上传数据。把团队权限设置为“完全公开”；</p>
						<p>2、在网络上发布对应的“推广链接”。对数据感兴趣的人点击该链接，并用邮箱注册文档库账号后，就可以加入团队，共享其中的数据；而文档库的已有用户，可以自由加入平台中的所有公开团队。</p>
					<p>特定范围内的数据分享：</p>
						<p>1、创建团队并上传数据；</p>
						<p>2、通过邮件邀请同行加入团队，分享数据，并可以在团队内进行讨论与交流。</p>
					<p>团队管理员可以获得成员信息，及设置他们的权限。</p>
				</div>
				
				<div class="content-through newer">
					<h3>兴趣小组的资料共享</h3>
					<p>兴趣相投的人可能来自天南海北，文档库可以帮助他们建立资料共享的云端工作室。以书籍爱好者为例，在文档库平台建立“书籍分享”团队，邀请书友们加入。大家可以把自己收藏的电子书上传到团队空间中，每本电子书都可以被打上一个或多个标签，方便书籍的多维度管理。</p>
					<p>这之后，不论书友身在何处，只要是有网络的地方，他就可以从文档库中挑选书籍阅读。文档库成为团队便捷的移动书架。另外，成员还可以团队空间中尽情的交流读书体会、评价或分享书籍。</p>
					<p>校园社团？公益组织？免费的文档库可以为您的团队提供最优质的服务。</p>
				</div>
				
				<div class="content-through newer">
					<h3>师生间的指导与交流</h3>
					<p>老师可以在文档库中创建团队，并邀请自己的学生加入。老师可以上传课件，推荐论文和书籍。同学们在这里分享论文，提交作业和工作汇报，交流学习心得。老师可以看到学生的学术进展，及时及对他们的汇报进行点评。而这一过程，将会积累起丰富的知识财富，师弟师妹将不再需要苦苦找寻师兄师姐的论文，甚至是论文本身之外的思想历程。</p>
					<p>可能有同学在外地学习，老师需要出差时，文档库保证了他们之间的交流协作不受异地的困扰。</p>
				</div>
			</div>
		</div>
		
		<div id="footer">
			<jsp:include page="/Version.jsp"></jsp:include>
		</div>
	

</body>
</html>
