<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<fmt:setBundle basename="templates.default" />

<%
	pageContext.setAttribute("basepath", request.getContextPath());
%>

<script type="text/javascript">
	function delsubmit(itemId)
	{
		var delid = document.getElementById("templateName");
	 	delid.value=itemId;
	 	var form = document.getElementById("delTemplateManage");
		
	 	form.submit();
	 	
	}
</script>

<div>
	<h3>
		<fmt:message key="site.template.manage.title" />
	</h3>
	<div>
		<table class="DCT_wikitable">
			<tr>
				<th>
					<fmt:message key="site.template.manage.name" />
				</th>
				<th>
					<fmt:message key="site.template.manage.operation" />
				</th>
			</tr>

			<c:forEach var="item" items='${requestScope.templates}'
				varStatus="status">

				<tr>
					<td>
						<c:out value="${item}"></c:out>
					</td>
					<td>
					    <c:if test="${item!='default'&&item!='admin'}">
						<button onclick="delsubmit('${item}');">
							<img src='${basepath}/images/common/unsubscribe.png' />
						</button>
						<fmt:message key="site.template.manage.cancel" />
						</c:if>
					</td>
				</tr>
			</c:forEach>
		</table>
	</div>
</div>


<form id="delTemplateManage" action="<vwb:Link jsp="siteTemplate" context='plain' format="url"/>?func=delete" method="post"
	name="TemplateManageForm">
	<input type="hidden" name="templateName" id="templateName" value="" />
</form>