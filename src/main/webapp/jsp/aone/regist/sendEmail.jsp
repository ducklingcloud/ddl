<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/uiLib.css" type="text/css" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/aone.css" type="text/css" />
<script type="text/javascript" src="${contextPath}/jsp/aone/js/uiLib-jQuery.js"></script>
<script type="text/javascript" src="${contextPath}/jsp/aone/js/global.js"></script>
<script type="text/javascript">
$(document).ready(function(){
	
	
});
</script>

<div id="content-title">
	<h1>注册成功</h1>
</div>

<div class="content-through">
	<div class="msgBox light" style="width:60%">
		<h3>注册成功</h3>
		<p>已向您的邮箱发送激活链接。请到邮箱查收，用该链接将激活您的帐号</p>
		<a class="ui-iconButton help"></a>
	</div>
	<div class="procedureHolder holderCenter">
		<c:choose>
			<c:when test="${not empty umtLogin }">
				<a class="largeButton" href="${umtLogin }?theme=aone">完成</a>
			</c:when>
			<c:otherwise>
				<a class="largeButton" href="http://passport.escience.cn/login?theme=aone">完成</a>
			</c:otherwise>
		</c:choose>
	</div>
</div>

