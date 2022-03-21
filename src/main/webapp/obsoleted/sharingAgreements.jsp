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
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="${contextPath}/jsp/aone/css/index.css" rel="stylesheet"
	type="text/css" />
<link href="${contextPath}/jsp/aone/css/index-nov2013.css"
	rel="stylesheet" type="text/css" />
<link href="${contextPath}/jsp/aone/css/index-aug2014.css?v=${aoneVersion}"	
	rel="stylesheet" type="text/css" />
<link href="${contextPath}/images/favicon.ico" rel="shortcut icon"
	type="image/x-icon" />
<script type="text/javascript"
	src="${contextPath}/scripts/jquery/jquery-1.8.3.min.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		$("#escienceMenu").mouseenter(function() {
			$("#es-pullDownMenu").show();
		});
		$("#escienceMenu").mouseleave(function() {
			$("#es-pullDownMenu").hide();
		});
		$(".active").removeClass("active");
	});
</script>
<title>分享协议-科研在线团队文档库</title>
</head>
<body class="texure">
	<jsp:include page="/ddlHeader.jsp"></jsp:include>
	<div id="ddl-intro" class="help">
		<div class="ui-wrap">
			<div class="left-intro" style="width:100%">
				<h2>
					<a href="#" class="current-nav" style="margin-right: 0px;">分享服务协议</a>
				</h2>
			</div>
		</div>
	</div>
	<div class="newsList news-1">
		<div class="ui-wrap">
			<h2>团队文档库分享服务法律声明</h2>
			<p class="newsHint">中国科学文献情报中心科技信息政策研究咨询与服务中心推荐版本 </p>
			<p>1.团队文档库提醒您遵守国家相关法律法规和中国科学院计算机网络信息中心政策、尊重他人知识产权、尊重个人隐私、合法合理使用本分享服务。</p>
			<p>2.分享内容的合法性、真实性、准确性由分享人负责，中国科学院计算机网络信息中心对因使用（或不能使用）本分享服务导致的任何直接或间接损失不承担责任。</p>
			<p>3.如您的分享行为违背本声明，中国科学院计算机网络信息中心有权停止对您提供的服务，并对此造成的任何直接或间接损失不承担责任。</p>
			<p>4. 团队文档库提供网络服务，有权对您公开分享的内容进行审核，如您认为您的权利受到侵害，请及时通知。团队文档库一旦发现有涉及违法信息，将一律删除，并终止所涉文档的分享服务。 </p>
			<p>为妥善解决您的投诉，请您提供您的姓名、单位、住所地、联系方式、权利凭证、侵权事实，中国科学院计算机网络信息中心将依据您的资料进行处理。</p>
			<p>通信地址：北京市海淀区中关村南四街四号中科院软件园2号楼</p>
			<p>联系邮箱：vlab@cnic.cn</p>
			<p>本声明的修改及解释权归中国科学院计算机网络信息中心。</p>
			<p>相关争议适用中华人民共和国法律，由中国科学院计算机网络信息中心所在地法院管辖。</p> 
		</div>
	</div>
	
	
	<div id="footer">
		<jsp:include page="/Version.jsp"></jsp:include>
	</div>
</body>
</html>