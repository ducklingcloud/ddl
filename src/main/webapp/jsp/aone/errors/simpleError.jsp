<%@ page language="java"  import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<html>
<head><title><c:choose >
				<c:when test="${ not empty exception.title}">
				${exception.title }
				</c:when>
				<c:otherwise>
					error page
				</c:otherwise>
			</c:choose></title></head>
			
<body>
<H2>${exception.message}</H2>
<br/>
<c:if test="${not empty exception.tip }">
	${exception.tip}
</c:if>
<br/>

	<c:if test="${ not empty exception.redirectURL }">
		三秒后跳转
		<a href="${exception.redirectURL}">
		<c:if test="${ not empty exception.redirectMesaage }">
			${exception.redirectMesaage}
		</c:if>
		</a>
	</c:if>

<P/>
</body>
</html>