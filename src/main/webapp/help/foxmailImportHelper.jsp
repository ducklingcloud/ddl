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
	<title>常见邮件客户端软件地址簿文件的导出方法 - Foxmail邮件客户端 - 科研在线</title>
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
						<ul class="titleDivide addressbook"  id="mailHelper">
							<li id="outlookTab" ><a href="outlookImportHelper.jsp">Outlook</a></li>
							<li id="foxmailTab"  class="current"><a>Foxmail</a></li>
							<li id="expressTab" ><a href="expressImportHelper.jsp">Outlook Express</a></li>
							<li id="thunderbirdTab" ><a href="thunderbirdImportHelper.jsp">Thunderbird</a></li>
						</ul>
			</div>
			<div id="foxmail" class="content-through sub">
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
							<div class="text">首先打开Foxmail 7.0软件，点击“工具”->“地址簿”；</div>
							<div class="img"><img src="${contextPath}/images/mailhelper/foxmail/csv/step1.png"/></div>
							<div class="ui-clear"></div> 
						</li>
						<li>
							<div class="text">选中需要导出的文件夹或者文件夹内的部分联系人，点击“工具”->“导出”->“Foxmail地址簿目录(*.csv)”；</div>
							<div class="img"><img src="${contextPath}/images/mailhelper/foxmail/csv/step2.png"/></div>
							<div class="ui-clear"></div> 
						</li>
						<li>
							<div class="text">选择您要保存*.csv文件的路径，然后点击“下一步”；</div>
							<div class="img"><img src="${contextPath}/images/mailhelper/foxmail/csv/step3.png"/></div>
							<div class="ui-clear"></div>
						</li>
						<li>
							<div class="text">最后，选择您需要导出的联系人字段信息，一般默认只导出“姓名”、“电子邮件地址”和“手机”信息。接着点击“完成”按钮即可完成导出操作。</div>
							<div class="img"><img src="${contextPath}/images/mailhelper/foxmail/csv/step4.png"/></div>
							<div class="ui-clear"></div>
						</li>
						<li>
							<div class="text fullview">经过上述步骤后，您就可以在步骤3中选择的文件保存路径中找到保存的.csv文件。
								<br/>然后，就可以将此文件导入到您在科研在线的通讯录了。</div>
						</li>
					</ol>
				</div>
				<div class="vcf" style="display:none;">
					<ol>
						<li>
							<div class="text">首先打开Foxmail 7.0软件，点击“工具”->“地址簿”；</div>
							<div class="img"><img src="${contextPath}/images/mailhelper/foxmail/vcf/step1.png"/></div>
							<div class="ui-clear"></div>
						</li>
						<li>
							<div class="text">选中需要导出的文件夹或者文件夹内的部分联系人，点击“工具”->“导出”->“名片（vCard）”；</div>
							<div class="img"><img src="${contextPath}/images/mailhelper/foxmail/vcf/step2.png"/></div>
							<div class="ui-clear"></div>
						</li>
						<li>
							<div class="text">选择您要保存*.vcf文件的路径，点击“确定”即可完成导出操作；</div>
							<div class="img"><img src="${contextPath}/images/mailhelper/foxmail/vcf/step3.png"/></div>
							<div class="ui-clear"></div>
						</li>
						<li>
							<div class="text fullview">经过上述步骤后，您就可以在步骤3中选择的文件保存路径中找到保存的.vcf文件。
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
