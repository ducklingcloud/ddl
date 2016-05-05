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
	<title>常见邮件客户端软件地址簿文件的导出方法 - Thunderbird邮件客户端- 科研在线</title>
	<link href="${contextPath}/jsp/aone/css/index.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="${contextPath}/scripts/jquery/jquery-1.8.3.min.js"></script>
	<script type="text/javascript" src="${contextPath}/jsp/aone/js/search-jQuery.js"></script>
	<script type="text/javascript">
	$(document).ready(function(){
		$('.csv-vcf li').click(function() {
			var v=$(this).attr("id");
			var flag=$(this).attr("flag");
			$("div.csv").hide();
			if(v=='csv'){
				$(".csv").show();
				$(".vcf").hide();
				$("#csv").addClass("current");
				$("#vcf").removeClass("current");
			}
			else{	
				$(".vcf").show();
				$(".csv").hide();
				$("#vcf").addClass("current");
				$("#csv").removeClass("current");
			}
		});
		var backBox = new BackToTop('回顶部');
	})
	</script>
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
				<li class="current"><a href="${contextPath}/help/introduction.jsp">科研在线是什么？</a></li>
				<li><a href="${contextPath}/help/tutorial.jsp">使用指南</a></li>
				<li><a href="${contextPath}/help/history.jsp">更新记录</a></li>
				<li><a href="<vwb:Link context='shareFile' format='url'/>">快速分享</a></li>
			</ul>
		</div>
		
		<div id="content">
			<div class="content-title tutorial">
				<ul class="titleDivide addressbook" id="mailHelper">
					<li id="outlookTab" ><a href="outlookImportHelper.jsp">Outlook</a></li>
					<li id="foxmailTab" ><a href="foxmailImportHelper.jsp">Foxmail</a></li>
					<li id="expressTab" ><a href="expressImportHelper.jsp">Outlook Express</a></li>
					<li id="thunderbirdTab"  class="current"><a>Thunderbird</a></li>
				</ul>
			</div>
			<div id="hunderbird" class="content-through sub">
			<div class="csv-vcf-container">
				<ul class="csv-vcf">
					<li id="csv" class="left current">导出*.csv文件</li>
					<li id="vcf" class="right" flag="false">导出*.vcf文件</li>
				</ul>
			</div>
			<div class="ui-clear"></div>
			<div class="csv">
				<ol>
					<li>
						<div class="text">首先打开Mozilla Thunderbird软件，点击工具栏中的“通讯录”；</div>
						<div class="img"><img src="${contextPath}/images/mailhelper/thunderbird/step1.png"/></div>
						<div class="ui-clear"></div> 
					</li>
					<li>
						<div class="text">在左侧窗口中选择需要导出的通讯录，然后点击“工具”->“导出”；</div>
						<div class="img"><img src="${contextPath}/images/mailhelper/thunderbird/step2.png"/></div>
						<div class="ui-clear"></div> 
					</li>
					<li>
						<div class="text">选择您要保存*.csv文件的路径，并在保存类型上选择“逗号分隔”，然后点击“保存”即可完成导出操作；</div>
						<div class="img"><img src="${contextPath}/images/mailhelper/thunderbird/step3.png"/></div>
						<div class="ui-clear"></div>
					</li>
					<li>
						<div class="text fullview">经过上述步骤后，您就可以在步骤3中选择的文件保存路径中找到保存的.csv文件。
							<br/>然后，就可以将此文件导入到您在科研在线的通讯录了。</div>
					</li>
				</ol>
			</div>
			<div class="vcf" style="display:none;">
				<p class="novcf">目前Mozilla Thunderbird不支持*.vcf文件的导出功能。</p>
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
