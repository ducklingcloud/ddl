<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<fmt:setBundle scope="page" basename="CoreResources" />
<title><fmt:message key="security.error.title" /></title>
	<meta http-equiv="refresh" content="3;url=${returnPage}" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/error.css"/>
<div class="content-through">
	<div class="error-center">
		<h3>对不起，您刚才请求的页面未找到。<br/></h3>
		<hr/>
		<p>
			<c:if test="${hasRefer!=null}">
				<a href="${returnPage}">
					<fmt:message key="security.error.noaccess.back"/>
				</a>
			</c:if>
			<c:if test="${hasRefer==null}">
				<c:if test="${teamFounded}">
					<a href="${returnPage}">
						<fmt:message key="security.error.noaccess.team"/>
					</a>
				</c:if>
				<c:if test="${!teamFounded}">
					<a href="${returnPage}">
						<fmt:message key="security.error.noaccess.home"/>
					</a>
				</c:if>
			</c:if>
		</p>
	</div>
</div>