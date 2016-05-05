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
	<title>常见邮件客户端软件地址簿文件的导出方法 - Outlook邮件客户端 - 科研在线</title>
	<link href="${contextPath}/images/favicon.ico" rel="shortcut icon" type="image/x-icon" />
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
					<li id="outlookTab" class="current"><a>Outlook</a></li>
					<li id="foxmailTab" ><a href="foxmailImportHelper.jsp">Foxmail</a></li>
					<li id="expressTab" ><a href="expressImportHelper.jsp">Outlook Express</a></li>
					<li id="thunderbirdTab" ><a href="thunderbirdImportHelper.jsp">Thunderbird</a></li>
				</ul>
			</div>
			<div id="outlook" class="content-through sub">
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
						<div class="text">首先打开Outlook 2007软件，点击“文件”->“导入和导出”；</div>
						<div class="img"><img src="${contextPath}/images/mailhelper/outlook/csv/step1.png"/></div>
						<div class="ui-clear"></div>
					</li>
					<li>
						<div class="text">选择“导出到文件”，然后点击“下一步”；</div>
						<div class="img"><img src="${contextPath}/images/mailhelper/outlook/csv/step2.png"/></div>
						<div class="ui-clear"></div>
					</li>
					<li>
						<div class="text">选择“逗号分割的值（Windows）”项，然后点击“下一步”；</div>
						<div class="img"><img src="${contextPath}/images/mailhelper/outlook/csv/step3.png"/></div>
						<div class="ui-clear"></div>
					</li>
					<li>
						<div class="text">选择导出文件夹的位置为“联系人”，然后点击“下一步”；</div>
						<div class="img"><img src="${contextPath}/images/mailhelper/outlook/csv/step4.png"/></div>
						<div class="ui-clear"></div>
					</li>
					<li>
						<div class="text">选择导出文件夹的选择导出文件的保存路径和文件名，点击“下一步”；</div>
						<div class="img"><img src="${contextPath}/images/mailhelper/outlook/csv/step4.png"/></div>
						<div class="ui-clear"></div>
					</li>
					<li>
						<div class="text">最后，点击“完成”按钮即可完成导出。若联系人中有若干个文件夹，那么可以选择需要导出的文件夹，然后单击“完成”即可。</div>
						<div class="img"><img src="${contextPath}/images/mailhelper/outlook/csv/step4.png"/></div>
						<div class="ui-clear"></div>
					</li>
					<li>
						<div class="text fullview">经过上述步骤后，您就可以在步骤5中选择的文件保存路径中找到保存的.csv文件。
							<br/>然后，就可以将此文件导入到您在科研在线的通讯录了。</div>					
					</li>
				</ol>
				</div>
				
				<div class="vcf" style="display:none;">
				<ol>
					<li>
						<div class="text">首先打开Outlook 2007软件，点击软件左下角的“联系人”；</div>
						<div class="img"><img src="${contextPath}/images/mailhelper/outlook/vcf/step1.png"/></div>
						<div class="ui-clear"></div>
					</li>
					<li>
						<div class="text">选择需要导出的联系人(摁住Shift键多选)，然后单击右键选择“作为名片发送”；</div>
						<div class="img"><img src="${contextPath}/images/mailhelper/outlook/vcf/step2.png"/></div>
						<div class="ui-clear"></div>
					</li>
					<li>
						<div class="text">在附件栏中选择所有联系人的*.vcf标签，点击右键复制或者按Ctrl+C；</div>
						<div class="img"><img src="${contextPath}/images/mailhelper/outlook/vcf/step3.png"/></div>
						<div class="ui-clear"></div>
					</li>
					<li>
						<div class="text">将这些复制的*.vcf选择您需要保存的目标文件夹，右键点击“粘贴”或者按Ctrl+V即可完成导出联系人的*.vcf文件操作。</div>
						<div class="img"><img src="${contextPath}/images/mailhelper/outlook/vcf/step4.png"/></div>
						<div class="ui-clear"></div>
					</li>
					<li>
						<div class="text fullview">经过上述步骤后，您就可以在步骤4中选择的文件保存路径中找到保存的.vcf文件。
							<br/>然后，就可以将此文件导入到您在科研在线的通讯录了。</div>
					</li>
				</ol>
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
