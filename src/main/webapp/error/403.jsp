<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<fmt:setBundle basename="CoreResources" />
<meta http-equiv="refresh" content="3;url=${returnPage}" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/error.css" />
<div class="content-through">
	<div class="error-center">
		<h3>
			<fmt:message key="security.error.noaccess.logged" />
		</h3>
		<hr />
		<p>
			<c:if test="${hasRefer!=null}">
				<a href="${returnPage}"> <fmt:message
						key="security.error.noaccess.back" /> </a>
			</c:if>
			<c:if test="${hasRefer==null}">
				<c:if test="${!hasTeamAccess}">
					<a href="${frontPage}"> <fmt:message
							key="security.error.noaccess.team" /> </a>
				</c:if>
				<c:if test="${hasTeamAccess}">
					<a href="${returnPage}"> <fmt:message
							key="security.error.noaccess.home" /> </a>
				</c:if>
			</c:if>
		</p>
		<div class="ui-RTCorner">
			<vwb:Link context="logout">
				<fmt:message key="security.error.noaccess.logout" />
			</vwb:Link>
		</div>
		<div class="ui-clear"></div>
	</div>
</div>
