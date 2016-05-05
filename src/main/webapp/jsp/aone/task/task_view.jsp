<%@page language="java" pageEncoding="UTF-8"%>
<%@taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<link href="${contextPath}/jsp/aone/css/task.css" rel="stylesheet"
	type="text/css">

	<script type="text/javascript"
	src="${contextPath}/scripts/jquery/jquery-ui-1.8.16.custom.min.js"></script>
	<link rel="stylesheet" href="${contextPath}/scripts/jquery_ui/css/jquery.ui.all.css">
	<script src="${contextPath}/scripts/jquery_ui/jquery.ui.core.js"></script>
	<script src="${contextPath}/scripts/jquery_ui/jquery.ui.widget.js"></script>
	<script src="${contextPath}/scripts/jquery_ui/jquery.ui.progressbar.js"></script>



<fmt:setBundle basename="templates.default" />
<html>
<head>
<script>
	function submitDEForm(obj) {
		if (obj == 'exit') {
			var action = "<vwb:Link context='task' format='url'/>?CreateByMe=true";
			$("#editform").attr("action", action);
			$("#editform").submit();
			return;
		}
		var strction = obj.name;
		if ($(obj).attr("name") == "cancel") {
			$("#pageTitle").rules("remove", "required");
		}
		var result = "";
		$("[name='taker']").each(function() {
			if ($(this).attr("checked")) {
				result += this.value + ",";
			}
		});
		$("#takerListStr").attr("value", result);
		validateUpdateContent();
		$("#editform").submit();
	}
	/**验证内容更改*/
	function validateUpdateContent() {
		var needUpdateIds = $("#needUpdateItemsId").attr("value");
		$("[name='taskContent']").each(function() {
			var itemId = $(this).attr("itemId");
			if ($("#hiddenContent_" + itemId).attr("value") != $(this).val()) {
				$(this).attr("name", "modifyTaskContent")
				needUpdateIds += itemId + ","
			}
		});
		$("#needUpdateItemsId").attr("value", needUpdateIds);
	}
	var rowNum = 0;
	function addTask() {
		rowNum++;
		var rownum = $("#taskTable tr").length - 1;
		$(
				"<tr id='taskContent_"+rowNum+"'><td>任务内容：</td><td><textarea name='newTaskContent'></textarea>"
						+ "<a href='javascript:deleteRow("
						+ rowNum
						+ ")'><img src='${contextPath}/images/delete.gif' alt='delete' title='删除'/>"
						+ "</td></tr>").insertAfter(
				$("#taskTable tr:eq(" + rownum + ")"));
	}
	function deleteRow(rowId) {
		$("#taskContent_" + rowId).remove();
	}
	function deleteExistRow(trId) {
		$("#TR_" + trId).remove();
		var val = $('#needDeleteItemsId').attr("value");
		val += trId + ",";
		$('#needDeleteItemsId').attr("value", val);

	}
	$(document).ready(function() {
		<c:forEach items="${userProcess}" var="pro" varStatus="status">
			$( "#progressbar${status.index}" ).progressbar({
				value: ${pro.userProcess}
			});
		 </c:forEach>
	});
</script>
</head>
<body>
	<div id="pagecontent">
		<form id="editform" name="editform" method="post"
			action="<vwb:Link context='task'  format='url'/>?func=modifySubmit">
			<h1>${task.title }</h1>
			<table id="taskTable">
				<tr>
					<th>任务类型：</th>
					<td>${task.taskTypeCN }</td>
				</tr>
				<tr>
					<th>完成进度：</th>
					<td>
					<c:forEach items="${userProcess}" var="pro" varStatus="status">
						<div class="executor">${pro.userNameStr } (${pro.userProcessStr }) </div>
						<div id="progressbar${status.index }"></div>
						<div class="ui-clear"></div>
					</c:forEach>
					</td>
				</tr>
				<tr>
					<th>接受者：</th>
					<td>${task.takersNameStr }</td>
				<tr>
					<c:forEach items="${task.items }" var="item">
						<tr>
							<th>任务内容：</th>
							<td>${item.content } <c:if
									test="${item.valid eq 'invalid' }">[已作废]</c:if> 

							</td>

						</tr>
					</c:forEach>
				<tr>
					<th></th>
					<td><input name='cancel' type='button' id='cancelbutton'
						value='<fmt:message key="editor.plain.cancel.submit"/>'
						onclick='javascript:submitDEForm("exit");'
						class="largeButton small" /></td>
				</tr>
			</table>

		</form>
	</div>
</body>

</html>