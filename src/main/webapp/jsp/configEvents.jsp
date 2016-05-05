<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page errorPage="/Error.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/vwb.tld" prefix="vwb"%>

<fmt:setBundle basename="templates.default" />
<script type="text/javascript">
	function changeEvent(obj) {
		$('cmd').value = 'query';
		$('eventid').value = obj.value;
		document.configEventsForm.submit();
	}
	
	function saveConfig() {
		var obj = $('score');
		var v = obj.value;
		if (!(/[^0-9]/.test(v))) {
			alert('configevents.numeric'.localize());
			obj.focus();
			return;
		}
		
		if (v.length>3) {
			alert('configevents.big'.localize());
			obj.focus();
			return;	
		}
		
		$('cmd').value = 'save';
		document.configEventsForm.submit();
	}
	
	function validScore(obj) {
		var v = obj.value;
		
		if(v.length == 0) {
			obj.value = '0';
			return;
		}
			
		var valid  = !/[^0-9]/.test(v);
		
		if (!valid) {
			alert('configevents.numeric'.localize());
			obj.focus();
		}
		else {
			if (v.length>3) {
				alert('configevents.big'.localize());
				obj.focus();
			}
		}
	}
</script>

<h3>
	<fmt:message key='configevents.title' />
</h3>

<form enctype="multipart/form-data"
	action="<vwb:Link page='5009' format='url'/>" method="post"
	name="configEventsForm">

	<input type="hidden" id="cmd" name="cmd" />
	<input type="hidden" id="eventid" name="eventid" />
	<input type="hidden" id="install" name="install"
		value="<%=request.getParameter("install")%>" />

	<table width="98%" border="0" cellspacing="0" cellpadding="0"
		class="DCT_wikitable">
		<c:if test="${eventCell!=null}">
			<tr>
				<td>
					<fmt:message key='configevents.eventid' />
				</td>
				<td>
					<select name="eventid" id="eventid" onChange="changeEvent(this)">
						<c:if test="${allid!=null}">
							<c:forEach var="event" items="${allid}">
								<c:choose>
									<c:when test="${event.eventID==eventCell.eventID}">
										<option value="${evnet.eventID}" selected>
											${event.eventID}
										</option>
									</c:when>
									<c:otherwise>
										<option value="${event.eventID}">
											${event.eventID}
										</option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</c:if>
					</select>
				</td>
			</tr>
			<tr>
				<td>
					<fmt:message key='configevents.eventname' />
				</td>
				<td>
					<input type="text" name="eventname" id="eventname"
						value="${eventCell.eventIDName}" />
				</td>
			</tr>
			<tr>
				<td>
					<fmt:message key='configevents.type' />
				</td>
				<td>
					<input type="text" name="type" id="type"
						value="${eventCell.eventType}" />
				</td>
			</tr>
			<tr>
				<td>
					<fmt:message key='configevents.typename' />
				</td>
				<td>
					<input type="text" name="typename" id="typename"
						value="${eventCell.eventTypeName}" />
				</td>
			</tr>
			<tr>
				<td>
					<fmt:message key='configevents.enable' />
				</td>
				<td>
					<input type="radio" name="eventcell" value="true">
					<fmt:message key='configevents.yes' />
					<input type="radio" name="eventcell" value="false">
					<fmt:message key='configevents.no' />
				</td>
			</tr>
			<tr>
				<td>
					<fmt:message key='configevents.score' />
				</td>
				<td>
					<input type="text" name="score" id="score"
						value="${eventcell.score}"
						onchange="validScore(this)" />
				</td>
			</tr>
			</c:if>
		<tr>
			<td colspan="2">
				<input type="button" name="Save"
					value="<fmt:message key='configevents.save'/>"
					onclick="saveConfig()" />
			</td>
		</tr>
	</table>
</form>
