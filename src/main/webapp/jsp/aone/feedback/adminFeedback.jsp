<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
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
<div class="toolHolder control">
	<h1>意见反馈</h1>
</div>

<div class="content-through">
	<table>
		<tr>
			<th width="20">ID</th>
			<th width="150">Email</th>
			<th width="300">Message</th>
			<th width="100">Status</th>
		</tr>
		<c:forEach items="${feedbacks}" var="item">
			<tr>
				<td width="10">${item.id}</td>
				<td width="150">${item.email}</td>
				<td width="300">${item.message}</td>
				<td width="100">${item.status}</td>			
			</tr>
		</c:forEach>
	</table>
</div>
