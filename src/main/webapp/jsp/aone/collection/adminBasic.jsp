<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<script type="text/javascript">
$(document).ready(function(){
	$('#saveBasicButton').click(function(){
		ui_spotLight('saveChanges-spotLight', 'success', '更改已保存', 'fade');
	});
	
	var currentValidator = $("#basicCollectionForm").validate({
			rules: {title: {required: true}},
			messages:{title:{required:"集合名称不能为空"}},
			errorPlacement: function(error, element){
				error.appendTo(element.parent().next());
			}
		});
});
</script>
<form id="basicCollectionForm" action="<vwb:Link context='configCollection' format='url'/>" method="POST">
	<input type="hidden" name="func" value="updateBasicInfo"/>
	<input type="hidden" name="cid" value="${collection.resourceId}"/>
	
	<table class="ui-table-form">
		<tr><th>集合名称：</th>
			<td><input type="text" name="title" value="${collection.title}"/></td>
			<td></td>
		</tr>
		<tr><th>集合描述：</th>
			<td><textarea name="description">${collection.description}</textarea></td>
			<td></td>
		</tr>
		<tr>
			<th>集合显示模式:</th>
			<td>
				<select name="homeMode" value="${collection.homeMode}">
					<option value="grid" <c:if test="${collection.homeMode eq 'grid'}">selected</c:if>>网格模式</option>
					<option value="list" <c:if test="${collection.homeMode eq 'list'}">selected</c:if>>列表模式</option>
				</select>
			</td>
			<td></td>
		</tr>
		<tr><td></td>
			<td class="largeButtonHolder">
				<input type="submit" id="saveBasicButton" value="保存更改"/>
				<span id="saveChanges-spotLight" class="ui-spotLight"></span>
			</td>
			<td></td>
		</tr>
	</table>
</form>
	

