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

<div class="ui-wrap">
	<h1>
		<c:choose>
			<c:when test="${not empty message }">
				${message}
			</c:when>
			<c:otherwise>
				注册失败，该用户已存在
			</c:otherwise>	
		</c:choose>
	</h1>
</div>

