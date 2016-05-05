<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="net.duckling.ddl.common.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib  uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%> 
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
<title>我的中奖记录</title>
</head>
<body class="texure">
	<jsp:include page="/ddlHeader.jsp"></jsp:include>
	<div id="ddl-intro" class="help lottery">
		<div class="ui-wrap"></div>
	</div>
	<div class="ui-wrap myPrize">
		<h2>我的中奖记录</h2>
		<c:choose>
		<c:when test="${fn:length(drawList)>0}">
			<table class="prizeTable" cellspacing="0">
			<thead>
				<tr>
				<th>等级</th>
				<th>奖品名称</th>
				<th>中奖日期</th>
				</tr>
			</thead>
			<tbody>
			<c:forEach var="item" items="${drawList }" >  
			<tr>
				<td>
				${item.giftLevel } 等奖
				</td>
				<td>
				${item.giftName }  <c:if test="${item.giftLevel==6 }">&nbsp;<a target="_blank" href="${contextPath}/system/space" class="btn btn-mini btn-success" style="font-weight:normal; color:#fff; text-decoration:none">去分配</a></c:if>
				</td>
				<td>
				${item.date } 
				</td>
			</tr>
			</c:forEach>
			</tbody>
			</table>
		</c:when>
		<c:otherwise>
			<p class="noPrize">很遗憾，您还未中奖，继续加油哦~</p>
		</c:otherwise>
		</c:choose>
	</div>
	
	<div id="footer">
		<jsp:include page="/Version.jsp"></jsp:include>
	</div>
</body>
</html>