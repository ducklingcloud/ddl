<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<script type="text/javascript"  src="${contextPath}/scripts/jquery/jquery.hashchange-1.0.0.js"></script>
<link href="${contextPath}/jsp/aone/css/exportColl.css" rel="stylesheet" type="text/css">	
<script type="text/javascript">
function selectAll() {
	$("input[name='check']").attr("checked","checked");
}
function deselectAll() {
	$("input[name='check']").removeAttr("checked");
}
function checkSubmit(btn) {
	var cks = $("input[name='check']");
	var hasChecked = false;
	for(var i=0;i<cks.length;i++) {
		if(cks[i].checked) {
			hasChecked = true;
			break;
		}
	}
	if(hasChecked) {
		var form = document.getElementById("exportForm");
		if(btn.id == "zipbtn") {
			form.action = "<vwb:Link context='viewCollection' page='${collection.resourceId}' format='url' />?func=download&format=zip";
		}
		else {
			form.action = "<vwb:Link context='viewCollection' page='${collection.resourceId}' format='url' />?func=download&format=epub";
		}
		//alert(form.action);
		form.method = "POST";
		form.submit();
	}
	else {
		alert("请选择至少一个页面或文件!");
	}
	
}

$(document).ready(function(){
	$('table.dataTable tr:nth-child(even)').addClass('striped');
	$('#selectAll').change(function(){
		if ($(this).attr('checked')=='checked' || $(this).attr('checked')==true) {
			selectAll();
		}
		else {
			deselectAll();
		}
	});
	
});

</script>
<div id="content-title">
	<input type="hidden" name="collectionId"
		value="${collection.resourceId}" />
	<div id="editTool">
		<a title="编辑工具" class="editTool"><span></span>
		</a>
		<ul id="toolGroup">
			<li><a
				href="<vwb:Link context='createPage' page="${collection.resourceId}" format='url'/>"
				class="toolNewPage"><span>+新建页面</span>
			</a>
			</li>
			<li><a
				href="<vwb:Link context='quick' format='url'/>?func=uploadFiles&cid=${collection.resourceId}">上传文件</a>
			</li>
			<li><a
				href="<vwb:Link context='configCollection' format='url'/>?cid=${collection.resourceId}#pageTab">移动页面</a>
			</li>
			<li><a
				href="<vwb:Link context='viewCollection' page="${collection.resourceId}" format='url'/>?func=export">导出集合</a>
			</li>
			<c:if test="${collection.resourceId != 1}">
				<li><a id="delete-current-collection">删除本集合</a>
				</li>
			</c:if>
			<li><a
				href="<vwb:Link context='configCollection' format='url'/>?cid=${collection.resourceId}"
				class="toolConfig"><span>设置</span>
			</a>
			</li>
		</ul>
		<div class="decoLeft"></div>
	</div>
	<h1>${collection.title}</h1>
</div>
<div id="content-major">
	<form id="exportForm">
		<h3 style="padding-left:10px;">导出文件</h3>
		<p style="padding-left:10px;">将选中集合内的文件导出：
			<input type="button" onclick="checkSubmit(this)" class="selbtn largeButton" value="导出为.zip" id="zipbtn"/>
			<input type="button" onclick="checkSubmit(this)" class="selbtn largeButton" value="导出为ePub" id="epubbtn"/>
		</p>
		<table class="dataTable merge" style="border-top:1px solid #ccc;">
		<thead>
			<tr><td width="85" class="dtCenter"><label><input class="selbtn" type="checkbox" id="selectAll">全选</label></td>
				<td class="dtLong">文章</td>
				<td></td>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="item" items="${elements}">
				<tr>
				<c:choose>
					<c:when test="${item.type == 'DPage'}">
					<td class="dtCenter"><input type="checkbox" name="check" value="DPage;${item.resourceId}" id="check${item.resourceId}" /></td>
					<td><a class="ui-RTCorner" href="<vwb:Link context="view" page="${item.resourceId}" format='url' />" target="_blank">查看</a>
						<label for="check${item.resourceId}">${item.title}</label>
					</td>
					<td></td>
					</c:when>
					<c:otherwise>
					<td class="dtCenter"><input type="checkbox" name="check" value="DFile;${item.resourceId}" id="check${item.resourceId}" /></td>
					<td><a class="ui-RTCorner" href="<vwb:Link context="file" page="${item.resourceId}" format='url' />" target="_blank">查看</a>
						<label for="check${item.resourceId}">${item.title}</label>
					</td>
					<td></td>
					</c:otherwise>
				</c:choose>
				</tr>
			</c:forEach>
		</tbody>
		</table>
	</form>
</div>
<div id="content-side">
	<div class="sideBlock">
		<h4>快捷方式</h4>
		<form id="save-shortcut-place-form">
			<ul id="shortcut" class="fileList">
				<c:choose>
					<c:when test="${not empty cShortcuts}">
						<c:forEach items="${cShortcuts}" var="item">
							<li sid="${item.id}">
								<input type="hidden" name="shortcutId" value="${item.id}" /> 
								<a class="delete-shortcut lightDel ui-RTCorner" sid="${item.id}" title="移除快捷方式"></a> 
								
								<c:choose>
									<c:when test="${item.resourceType eq 'DPage'}">
										<a href="<vwb:Link page='${item.resourceId}' context='view' format='url'/>">${item.title}</a>
									</c:when>
									<c:otherwise>
										<a href="<vwb:Link page='${item.resourceId}' context='file' format='url'/>">${item.title}</a>
									</c:otherwise>
								</c:choose>
							</li>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<p class="NA" style="margin: 1em 0;">还未添加快捷方式</p>
					</c:otherwise>
				</c:choose>
			</ul>
			<div id="save-button" class="subHolder isolate holderCenter"
				style="display: none">
				<input type="button" id="save-shortcut-sequence" class="largeButton" value="保存顺序" />
			</div>
		</form>
	</div>
</div>
<div class="ui-clear"></div>
