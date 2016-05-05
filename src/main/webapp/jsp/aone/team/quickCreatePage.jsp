<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<script type="text/javascript" src="${contextPath}/jsp/aone/js/fileuploader.js"></script>
<link href="${contextPath}/jsp/aone/css/fileuploader.css" rel="stylesheet" type="text/css">	
<script language="javascript">
$(document).ready(function(){
     $("input[name='useNewCollection']").live("change",function(){
     	if($(this).attr("checked")){
     		$("#new-collection-input").show();
     		$("#old-collection-select").hide();
     	}else{
     		$("#new-collection-input").hide();
     		$("#old-collection-select").show();
     	}
     });
     
     var currentValidator = $("#quickCreatePageForm").validate({
		rules: {
			pageName:{required:true},
			newCollection: {required: "#put-new-collection:checked"}
		},
		messages:{
			pageName:{required:"请输入页面名称"},
			newCollection:{required:"请输入集合名称"}
		}
	});
	
});
</script>
<div id="content-title">
	<h1>快速创建页面</h1>
</div>
<div class="content-through" id="quickCreatePage">
	<form id="quickCreatePageForm" method="POST" action="<vwb:Link context='quick' format='url'/>?func=submitCreatePage">
	<div id="step1">
		<table class="ui-table-form" style="margin-top:3em;">
			<tbody>
			<tr>
				<th>标题：</th>
				<td><input type="text" name="pageName" value="" /></td>
			</tr>
			<tr>
				<th>页面位置：</th>
				<td style="height:50px">
					<span id="old-collection-select">
						<c:if test="${not empty collections}">
							<select name="selectCollection" value="1">
								<c:forEach items="${collections}" var="item">
									<option value="${item.resourceId}" <c:if test="${item.resourceId eq 1}">selected</c:if> >${item.title}</option>
								</c:forEach>
							</select>
						</c:if>
					</span>
					<span id="new-collection-input" <c:if test="${not empty collections}">style="display:none"</c:if>>
						<input type="text" name="newCollection" value=""/>
					</span>
					
					<br/>
					
					<c:choose>
						<c:when test="${empty collections}">请输入新集合名称</c:when>
						<c:otherwise><label><input type="checkbox" id="put-new-collection" name="useNewCollection" value="true"/>新建集合</label></c:otherwise>
					</c:choose>
				</td>
			</tr>
			</tbody>
			<tfoot>
			<tr><th></th>
				<td class="largeButtonHolder" style="padding-top:1.5em;">
					<input id="toSubmit" type="submit" value="创建页面"/>
					<a class="largeButton dim" href="javascript:window.history.back();">取消</a>
				</td>
			</tr>
			</tfoot>
		</table>
	</div>
	</form>
	<div class="toolHolder holderMerge"></div>
</div>

