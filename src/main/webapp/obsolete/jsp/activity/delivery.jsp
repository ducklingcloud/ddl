<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="net.duckling.ddl.common.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%pageContext.setAttribute("contextPath", request.getContextPath());%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="${contextPath}/images/favicon.ico" rel="shortcut icon" type="image/x-icon" />
<link href="${contextPath}/jsp/aone/css/index.css" rel="stylesheet" type="text/css" />
<link href="${contextPath}/jsp/aone/css/index-nov2013.css?v=${aoneVersion}" rel="stylesheet" type="text/css" />
<link href="${contextPath}/jsp/aone/css/index-aug2014.css?v=${aoneVersion}"	rel="stylesheet" type="text/css" />
<link href="${contextPath}/scripts/bootstrap/css/todc-bootstrap.css" rel="stylesheet"/>	
<link href="${contextPath}/jsp/aone/css/lion.css?v=${aoneVersion}"	rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${contextPath}/scripts/jquery/jquery-1.8.3.min.js"></script>
<style type="text/css">
.navbar.navbar-fixed-top .navbar-inner .container ul.nav li {margin:6px 0 0 0;}
.navbar .brand {padding:0; margin-left:0;}
</style>
<title>提交收货地址</title>
</head>
<body class="texure">
	<jsp:include page="/ddlHeader.jsp"></jsp:include>
	<div id="ddl-intro" class="help lottery">
		<div class="ui-wrap"></div>
	</div>
	<div class="ui-wrap myPrize delivery">
		<h2>我的收货地址 <span style="font-size:14px;font-weight:normal;">（请务必保证您所填写的信息真实有效）</span></h2>
		<form id="form1" action="${contextPath }/activity/lottery?func=deliverySave" method="post">
			<p><label>真实姓名：</label> <input type="text" id="realName" name="realName" value="${delivery.realName }" /></p>
			<p><label>联系电话：</label> <input type="text" id="phoneNumber" name="phoneNumber" value="${delivery.phoneNumber }" /></p>
			<p><label>收货地址：</label> <input type="text" id="userAddress" size="300" name="userAddress" value="${delivery.userAddress }" /></p>
			
			<p><label>&nbsp;</label><input type="submit" value=" 提交 " class="btn btn-primary" />  <c:if test="${!empty act }"><span style="color:#0a0; font-size:14px; display:inline-block; margin:0 10px;">提交成功!</span></c:if></p>
			<input type="hidden" name="func" value="deliverySave" />
		</form>
	</div>
	
	<div id="footer">
		<jsp:include page="/Version.jsp"></jsp:include>
	</div>
<script type="text/javascript">
$(function(){
	$("#form1").submit( function () {
		if($("#realName").val().trim() == ""){
			alert("请输入真实姓名.");
			$("#realName").focus();
			return false;
		}
		if($("#phoneNumber").val().trim() == ""){
			alert("请输入联系电话.");
			$("#phoneNumber").focus();
			return false;
		}
		if($("#userAddress").val().trim() == ""){
			alert("请输入收货地址.");
			$("#userAddress").focus();
			return false;
		}
		return true;
	} );
});

</script>
</body>
</html>