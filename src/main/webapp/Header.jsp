<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<fmt:setBundle basename="templates.default" />
<jsp:include flush="true" page="popupmenu.jsp"></jsp:include>

<c:if test="${RenderContext.banner!=null}">
	<c:if test="${RenderContext.showUserbox==true}">
		<jsp:include page="UserBox.jsp" />
	</c:if>
	<div id="header" class="DCT_banner">
		<vwb:render content="${RenderContext.banner}" />
	</div>
	<c:if test="${RenderContext.showSearchbox==true}">
		<div id="box">
			<jsp:include page="/SearchBox.jsp" />
		</div>
	</c:if>
	
</c:if>