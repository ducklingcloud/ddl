<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page errorPage="/Error.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/vwb.tld" prefix="vwb"%>


<div style="min-height:300px; text-align:center; padding-top:100px">
	<c:choose>
		<c:when test="${not empty pageLocks}">
			<c:forEach items="${pageLocks }" var="page">
				<h4>页面${page.page.title}正在被${page.pageLock.uid}编辑,请等待Ta完成编辑！</h4>
			</c:forEach>
			<a href="<vwb:Link context='f' page='${pageLocks[0].pageLock.rid}' format='url'/>">返回</a>
		</c:when>
		<c:otherwise>
			<h4>当前页面正在被${lock.uid}编辑,请等待Ta完成编辑！</h4>
			<a href="<vwb:Link context='f' page='${lock.rid}' format='url'/>">返回</a>
		</c:otherwise>
	</c:choose>
</div>