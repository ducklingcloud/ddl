<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="net.duckling.ddl.common.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
<%pageContext.setAttribute("contextPath", request.getContextPath());
%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="${contextPath}/jsp/aone/css/index.css" rel="stylesheet"	type="text/css" />
<link href="${contextPath}/jsp/aone/css/index-nov2013.css?v=${aoneVersion}"	rel="stylesheet" type="text/css" />
<link href="${contextPath}/jsp/aone/css/index-aug2014.css?v=${aoneVersion}"	rel="stylesheet" type="text/css" />
<link href="${contextPath}/images/favicon.ico" rel="shortcut icon"	type="image/x-icon" />
<link href="${contextPath}/scripts/bootstrap/css/bootstrap.min.css" rel="stylesheet" media="screen"/>
<link href="${contextPath}/scripts/bootstrap/css/todc-bootstrap.css" rel="stylesheet"/>	
<link href="${contextPath}/jsp/aone/css/lion.css?v=${aoneVersion}"	rel="stylesheet" type="text/css" />
<link href="${contextPath}/jsp/aone/css/activity-cover-bootstrap.css?v=${aoneVersion}"	rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${contextPath}/scripts/jquery/jquery-1.8.3.min.js"></script>
<script type="text/javascript" src="${contextPath}/scripts/bootstrap/js/bootstrap.min.js"></script>
<title>100G永久空间免费领取</title>
<script type="text/javascript">
	$(document).ready(function(){
		$('#value').popover('hide');
		$('#team').popover('hide');
	})
</script>
</head>
<body class="texure">
	<jsp:include page="/ddlHeader.jsp"></jsp:include>
	<div id="ddl-intro" class="help one100g">
		<div class="ui-wrap">
			<div class="left-intro" style="width:100%">
				<h2>100G永久空间免费领取</h2>
			</div>
		</div>
	</div>
	<div class="ui-wrap" style="padding-top:20px;">
		<c:if test="${authenticated == true}">
			<div class="userSpaceBox">
				<span class="space-user">
					${userName }，恭喜您已获得
					<span class="num">${gainedSizePan }</span>
					<a class="beta" title="查看扩容记录" href="${contextPath}/pan/applicationSpace">个人同步版空间</a> 和 
					<span class="num">${gainedSizeTeam }</span>
					<span class="preparePopover inline" data-html='true' data-animation='false' data-trigger="hover" 
						data-placement="bottom" data-content="<font class='pop'>付费扩容团队空间的标准价格为每50G每月1000元</font>" id="value">团队空间</span>。
				</span>
				<div class="tooltip">dasfdsaf</div>
				<c:if test="${gainedSizeTeam!='0 B' }">
					<a class="space-manage" href="${contextPath}/system/space">分配团队空间</a>
				</c:if>
			</div>
		</c:if>
		
		<div class="task" id="task1">
			<span class="supNum">任务 1</span>
			<div class="leftTask">
				<h2>登录团队文档库网页版，送<span class="num">10G</span><span class="beta">个人同步版空间。</span></h2>
			</div>
			<div class="rightHint">
				<c:choose> 
				  <c:when test="${task1 == true}">
				  	 <a class="taskOk done" style="cursor:default;" href="javascript:;">已完成</a> 
				  </c:when> 
				  <c:otherwise>   
				   <a class="taskOk done" href="javascript:;">活动已结束</a>
				  </c:otherwise> 
				</c:choose> 
			</div>
			<div class="clear"></div>
		</div>
		
		<div class="task" id="task2">
			<span class="supNum">任务 2</span>
			<div class="leftTask">
				<h2>下载并登录桌面客户端（Windows / Mac / Linux），送<span class="num">30G</span><span class="beta">个人同步版空间。</span></h2>
				<ul class="pcList">
					<li><a class="pc windows" href="https://update.escience.cn/download/ddl_1.1.1_Beta_win32_setup.exe"><span class="model"></span><br />下载Windows客户端</a></li>
					<li><a class="pc mac" href="https://update.escience.cn/download/ddl-drive-macosx-1.1.1.beta.dmg"><span class="model"></span><br />下载Mac客户端</a></li>
					<li><a class="pc linux" href="http://update.escience.cn/download/ddl-drive-linux-1.1.1.beta.tar.gz"><span class="model"></span><br />下载Linux客户端</a></li>
				</ul>
			</div>
			<div class="rightHint">
				<c:choose> 
				  <c:when test="${authenticated == true && task2 == true}">
				  	<a class="taskOk done" style="cursor:default;" href="javascript:;">已完成</a>
				  </c:when> 
				  <c:otherwise>   
				     <a class="taskOk done" href="javascript:;">活动已结束</a>
				  </c:otherwise> 
				</c:choose> <br /><br />
				<p class="subHint">1. 下载任何一款桌面客户端</p>
				<p class="subHint">2. 安装并登录，即可获得30G空间</p>
			</div>
			<div class="clear"></div>
		</div>
		
		<div class="task" id="task3">
			<span class="supNum">任务 3</span>
			<div class="leftTask">
				<h2>下载并登录iPhone / Android客户端，送<span class="num">30G</span><span class="beta">个人同步版空间。</span></h2>
				<ul class="mobileList">
					<li><a class="mobile ios" href="http://itunes.apple.com/cn/app/ke-yan-zai-xian/id495109931"><span class="model"></span><br />下载iPhone客户端</a></li>
					<li><a class="mobile android" href="http://www.escience.cn/apks/ddl-latest.apk"><span class="model"></span><br />下载Android客户端</a></li>
					<li><span style="display:inline-block; line-height:50px; float:left; margin:25px 20px 25px 0; font-size:14px; font-weight:bold;">或者</span><a class="" href="http://update.escience.cn/download/ddl.html"><span class="model"><img class="mobileCode" src="${contextPath}/images/mobileRcode3.png"></span><br />扫描二维码</a></li>
				</ul>
			</div>
			<div class="rightHint">
				<c:choose> 
				  <c:when test="${authenticated == true && task3 == true}">   
				  	<a class="taskOk done" style="cursor:default;" href="javascript:;">已完成</a>
				  </c:when> 
				  <c:otherwise>   
				   	<a class="taskOk done" href="javascript:;">活动已结束</a>
				  </c:otherwise> 
				</c:choose><br /><br />
				<p class="subHint">1. 下载任何一款手机客户端</p>
				<p class="subHint">2. 安装并登录，即可获得30G空间</p>
			</div>
			<div class="clear"></div>
		</div>
		
		<div class="task" id="task4">
			<span class="supNum">任务 4</span>
			<div class="leftTask">
				<h2>分享一次文件，送<span class="num">20G</span><span class="beta">个人同步版空间。</span></h2>
				<img src="${contextPath}/jsp/aone/images/shareMe.png" style="width:500px; border-radius:5px;"></img>
			</div>
			<div class="rightHint">
				<c:choose> 
				  <c:when test="${authenticated == true && task4 == true}">   
				  	<a class="taskOk done" style="cursor:default;" href="javascript:;">已完成</a>
				  </c:when> 
				  <c:otherwise>
				  	<a class="taskOk done" href="javascript:;">活动已结束</a>
				  </c:otherwise> 
				</c:choose><br /><br />
				<p class="subHint">1. 登录个人空间或团队空间</p>
				<p class="subHint">2. 点击分享中“公开链接”即可</p>
			</div>
			<div class="clear"></div>
		</div>
		
		<div class="task" id="task5">
			<span class="supNum">任务 5</span>
			<div class="leftTask">
				<h2>创建团队并成功邀请一个成员，送<span class="num">10G</span><span class="preparePopover team" data-html='true' data-animation='false' data-trigger="hover" 
						data-placement="right" data-content="<font class='pop'>如何获取团队空间？<br />1.用户可以免费创建10个10G容量的团队空间；<br />2.付费扩容团队空间，标准价格为每50G每月1000元；<br />3.参加官方举办的活动来获取团队空间奖励；</font>" id="team">团队空间</span>。</h2>
			</div>
			<div class="rightHint">
				<c:choose> 
				  <c:when test="${authenticated == true && task5 == true}">   
				   	<a class="taskOk done" style="cursor:default;" href="javascript:;">已完成</a>
				  </c:when> 
				  <c:otherwise>
				  	<a class="taskOk done" href="javascript:;">活动已结束</a>
				  </c:otherwise> 
				</c:choose>	
			</div>
			<div class="clear"></div>
		</div>
	</div>
	
	<div id="footer">
		<jsp:include page="/Version.jsp"></jsp:include>
	</div>
<%--
<c:if test="${authenticated == false}">
<script type="text/javascript">
$(function(){
	$.getScript('${passportUrl}/js/isLogin.do', function(){
		 if(data.result){
			window.location.href="${loginUrl}";
		 }
	 });
});
</script>
</c:if>
 --%>
</body>
</html>