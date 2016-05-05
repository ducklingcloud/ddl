<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/uiLib.css" type="text/css" />
<script type="text/javascript" src="${contextPath}/jsp/aone/js/uiLib-jQuery.js"></script>


<script type="text/javascript">
$(document).ready(function(){
	
});
</script>

<div id="content-title">
	<h1>已成功发送邀请</h1>
</div>
<div class="content-through">
	
	<c:if test="${not empty invalidInvitees}">
		<table class="ui-table-form numCount">
			<tr><td colspan="2">以下用户已是该团队的成员，无法重复邀请</td></tr>
			<c:forEach items="${invalidInvitees}" var="item">
				<tr><th style="width:2em;"><span class="num"></span></th>
					<td>${item}</td>
				</tr>
			</c:forEach>
		</table>
	</c:if>
	<div class="procedureHolder holderCenter">
		<a href="<vwb:Link context='configTeam' format='url' jsp="${team.name}"/>&func=adminInvitations" class="largeButton">返回</a>
	</div>
</div>
<div class="clear"></div>
