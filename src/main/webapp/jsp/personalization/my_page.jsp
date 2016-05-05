<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<link rel="stylesheet" type="text/css" href="${contextPath}/jsp/personalization/pop.css">
	<div style="margin-top:20px;margin-left:30px;">
		<div class="pop_chbox pop_bw" id="m_culture">
			<div class="pop_t1"><fmt:message key="personalization.recentcreate" /></div>
			<div>
			<table>
				<tr><td>
						<ul class="pop_ul">
						<c:forEach var="item" items='${requestScope.createdPage}' varStatus="status">
						<li>
							<a href="<vwb:Link page="${item.resourceId}" format="url"/>" class="txt">${item.title}</a>
						</li>
						</c:forEach>
						</ul>
				</td></tr>
			</table>
			</div>
		</div>
	
		<div class="pop_chbox pop_bw" id="m_science">
			<div class="pop_t1"><fmt:message key="personalization.recentedit" /></div>
			<div>
			<table>
				<tr><td>
						<ul class="pop_ul">
						<c:forEach var="item" items='${requestScope.editedPage}' varStatus="status">
							<li>
							<a href="<vwb:Link page="${item.resourceId}" format="url"/>" class="txt">${item.title}</a>
							</li>
						</c:forEach>
						</ul>
				</td></tr>
			</table>
			</div>
		</div>
	</div>
