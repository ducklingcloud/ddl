<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />


<div id="collection">
	<!-- 动态加载出集合内容 -->
	<ul>
		<c:forEach items="${collectionList}" var="item">
			<li><a href="${item.href}">${item.name}</a></li>
		</c:forEach>
	</ul>
</div>

