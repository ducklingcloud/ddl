<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/tld/vwb.tld" prefix="vwb"%>
<fmt:setBundle basename="templates.default" />
<c:choose>
	<c:when test="${isAdmin!=null && isAdmin=='false'}">
		<table class="DCT_wikitable">
			<tr>
				<td>
					<fmt:message key="emailnotifier.priviledgeError" />
				</td>
			</tr>
		</table>
	</c:when>
	<c:otherwise>
		<c:if test="${errorInfo!=null}">
			<font color="red" size="3"> <c:out value='${errorInfo}' /> </font>
		</c:if>
		<form id="esubManage"
			action="<vwb:Link jsp='subscribeAdmin' context='team' format='url'/>"
			method="post" name="pageInfoForm">
			<input type="hidden" name="deleteESubId" id="deleteESubid" value="" />
			<h3>
				<fmt:message key="emailnotifier.emailList" />
			</h3>

			<table class="" boder="0">
				<tr>
					<td></td>
					<td>
						<fmt:message key="emailnotifier.email" />
						:
						<br />
						<input type="text" name="emails" />
					</td>
					<td>

					</td>
					<td>
						<fmt:message key="emailnotifier.page" />
						:
						<br />
						<input type="text" name="pageName" />
					</td>
					<td align="left">

					</td>
					<td>
						<fmt:message key="emailnotifier.hour" />
						:
						<br />
						<input type="text" name="hour" />
					</td>
					<td>
						<br />
						<input type="submit"
							value="<fmt:message key="emailnotifier.search" />" />
					</td>
				</tr>
			</table>
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
					<th>
						<fmt:message key="emailnorifier.unsubscriber" />
					</th>
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
							<BUTTON onclick="delsubmit('${item.id}');">
								<img
									src='<%=request.getContextPath()%>/images/common/unsubscribe.png' />
							</button>
						</td>
					</tr>
				</c:forEach>
			</table>

		</form>
		<script type="text/javascript" type="text/javascript">
<!--
function delsubmit(itemId)
{

	 var delid = document.getElementById("deleteESubid");
	 delid.value=itemId;
	 var form = document.getElementById("esubManage");
	
	 form.submit();
}
//-->
</script>
	</c:otherwise>
</c:choose>
