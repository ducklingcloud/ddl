<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/uiLib.css" type="text/css" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/aone.css" type="text/css" />
<script type="text/javascript" src="${contextPath}/jsp/aone/js/uiLib-jQuery.js"></script>
<script type="text/javascript" src="${contextPath}/jsp/aone/js/global.js"></script>
<link href="${contextPath}/jsp/aone/css/exportColl.css" rel="stylesheet" type="text/css">
<link href="${contextPath}/jsp/aone/css/tag-z.css" rel="stylesheet" type="text/css">	

<div class="config-float">
	<form id="exportForm">
		<h3>导出文件</h3>
		<p>将选中标签内的文件导出：
			<input type="button" onclick="checkSubmit(this)" class="btn-primary" value="导出为.zip" id="zipbtn"/>
			<input type="button" onclick="checkSubmit(this)" class="btn-success" value="导出为ePub" id="epubbtn"/>
		</p>
		<div class="tagByGroup">
		<c:choose>
			<c:when test="${taggroup.size()<=0 }">
				<span>本团队无任何标签</span>
			</c:when>
			<c:otherwise>
				<c:forEach items="${taggroup}" var="taggroup">
				<div class="tagGroups">
					<h4>${taggroup.group.title}</h4>
					<input type="checkbox" class="checkall">全选
					<ul>
					<c:forEach items="${taggroup.tags}" var="tag">
						<li>
						<input type="checkbox" name="tag" value="${tag.id }">
						<span>${tag.title }</span>
						</li>
					</c:forEach>
					</ul>
				</div>
				</c:forEach>
			</c:otherwise>
		</c:choose>
		<div class="ui-clear"></div>
		</div>
	</form>
</div>

<script type="text/javascript">

$(document).ready(function(){
	$('input.checkall').change(function(){
		var checked = $(this).attr('checked');
		var result;
		if(checked == 'checked' || checked == true)
			result = true;
		else
			result = false;
		$(this).siblings('ul').find('input[name=tag]').attr('checked',result);
	});
	
	$('input[name=tag]').change(function(){
		var checked = $(this).attr('checked');
		if(checked == 'checked' || checked == true){
			var isAll = true;
			$(this).parents('ul').children().each(function(){
				var isChecked = $(this).children('input[name=tag]').attr('checked');
				if(!(isChecked == 'checked' || isChecked == true)){
					isAll = false;
					return;
				}
			});
			$(this).parents('ul').siblings('input').attr('checked',isAll);
		}else{
			$(this).parents('ul').siblings('input').attr('checked',false);
		}
	});
});

function checkSubmit(btn) {
	var cks = $("input[name=tag]");
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
			form.action = "<vwb:Link context='configTeam' jsp='${teamName}' format='url' />&func=download&format=zip";
		}
		else {
			form.action = "<vwb:Link context='configTeam' jsp='${teamName}' format='url' />&func=download&format=epub";
		}
		//alert(form.action);
		form.method = "POST";
		form.submit();
	}
	else {
		alert("请选择至少一个标签!");
	}
}

</script>
