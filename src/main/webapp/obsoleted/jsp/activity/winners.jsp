<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="net.duckling.ddl.common.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib  uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%> 
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
<link href="${contextPath}/scripts/bootstrap/css/bootstrap.min.css" rel="stylesheet" media="screen"/>
<script type="text/javascript" src="${contextPath}/scripts/jquery/jquery-1.8.3.min.js"></script>
<link href="${contextPath}/jsp/aone/css/activity-cover-bootstrap.css?v=${aoneVersion}"	rel="stylesheet" type="text/css" />

<title>幸运抽奖大转盘</title>
</head>
<body class="texure">
	<jsp:include page="/ddlHeader.jsp"></jsp:include>
	<div id="ddl-intro" class="help lottery">
		<div class="ui-wrap"></div>
	</div>
	<div class="ui-wrap" style="padding-top:20px;">
		<div class="winners">
			<h1>幸运抽奖大转盘获奖名单</h1>
			
			<h2>${giftname1 }</h2>
			<ul>
				<c:forEach var="item" items="${level1 }">  
					<li class="omg">${item.user }</li>
				</c:forEach>
			</ul>
			<h2>${giftname2 }</h2>
			<ul>
				<c:forEach var="item" items="${level2 }">  
					<li>${item.user }</li>
				</c:forEach>
			</ul>
			<h2>${giftname3 }</h2>
			<ul>
				<c:forEach var="item" items="${level3 }">  
					<li>${item.user }</li>
				</c:forEach>
			</ul>
			<h2>${giftname4 }</h2>
			<ul>
				<c:forEach var="item" items="${level4 }">  
					<li>${item.user }</li>
				</c:forEach>
			</ul>
			<h2>${giftname5 }</h2>
			<ul>
				<c:forEach var="item" items="${level5 }">  
					<li>${item.user }</li>
				</c:forEach>
			</ul>
		</div>
	</div>
	
	<div id="footer">
		<jsp:include page="/Version.jsp"></jsp:include>
	</div>

<script type="text/javascript" src="${contextPath}/scripts/bootstrap/js/bootstrap.min.js"></script>
</body>
</html>