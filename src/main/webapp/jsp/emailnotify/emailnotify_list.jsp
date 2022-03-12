<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="/WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<%
	pageContext.setAttribute("locale", request.getLocale());
%>
<fmt:setBundle basename="templates.default" />
<c:if test="${errorInfo!=null}">
	<font color="red" size="3"> <c:out value='${errorInfo}' /> </font>
</c:if>

<form name="esubManage" action="<vwb:Link jsp='emailManage' format='url'/>" method="post">
	<input type="hidden" name="deleteESubId" id="deleteESubid" value="" />
	<input type="hidden" name="func" value="delete"/>
</form>
	<h3>
	<fmt:message key="emailnotifier.info" />
</h3>
<table class="DCT_wikitable">
	<tr>
		
		<th>
			<fmt:message key="emailnotifier.email" />
		</th>
		<th>
			<fmt:message key="emailnotifier.page" />
		</th>

		<th>
			<fmt:message key="emailnotifier.hour" />
		</th>
		<th><fmt:message key="emailnorifier.unsubscriber" /></th>
	</tr>
	
	<c:forEach var="item" items='${requestScope.eMailSubscribers}'
		varStatus="status">
		<tr>
			
			<td>

				<c:out value='${item.receiver}' />
			</td>
			<td>
				<c:choose>
					<c:when test="${ '*' == item.resourceId }">
						<fmt:message key="emailnotifier.allpages" />
					</c:when>
					<c:otherwise>
						<c:out value='${item.pageTitle}' />
					</c:otherwise>
				</c:choose>

			</td>

			<td>
				<c:out value='${item.rec_time}' />
			</td>
			<td>
			<BUTTON  onclick="delsubmit('${item.id}');"><img src='<%=request.getContextPath() %>/images/common/unsubscribe.png'/></button>
			</td>
		</tr>
	</c:forEach>
</table>
<script type="text/javascript" type="text/javascript">
<!--
function delsubmit(itemId){
	$("#deleteESubid").attr("value", itemId);
	document.esubManage.submit();
}
//-->
</script>
