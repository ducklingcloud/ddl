<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/uiLib.css" type="text/css" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/aone.css" type="text/css" />

<script type="text/javascript">
$(document).ready(function(){
	$("#chooseButton").live("click",function(){
		$("#candidates-box option:checked").each(function(){
			$("#select-box").append($(this));
		});
	});
	
	$("#disChooseButton").live("click",function(){
		$("#select-box option:checked").each(function(){
			$("#candidates-box").append($(this));
		});
	});
	
	$("#submitSelectionButton").live("click",function(){
		var url = "<vwb:Link context='configCollection' format='url'/>";
		ajaxRequest(url,$("#addPagesForm").serialize(),afterPageSelection);
	});
	
	function afterPageSelection(data){
		$("#noPages").hide();
		for(var i=0;i<data.length;i++){
			var tempUrl = $("#option"+data[i]["id"]).attr("url");
			$("#exist-page-list").append("<li><a href="+tempUrl+">"+$("#option"+data[i]["id"]).html()+"</a></li>");
		}
		$("select[name='pageChoose'] option").remove();
	};
	
	$("a.collectionClass").live("click",function(){
		var url = "<vwb:Link context='configCollection' format='url'/>?func=loadPages";
		ajaxRequest(url,"cid="+$(this).attr("cid"),afterLoadPages);
	});
	
	function afterLoadPages(data){
		$("#candidates-box").html("");
		for(var i=0;i<data.length;i++){
			var str = "<option id='option"+data[i]["pid"]+"' value='"+data[i]["pid"]+"' url='"+data[i]["url"]+"'>"+data[i]["title"]+"</option>";
			if(data[i]["title"]==null)
				str = "<option id='option"+data[i]["pid"]+"' value='"+data[i]["pid"]+"' url='"+data[i]["url"]+"'>Undefined</option>";
			$("#candidates-box").append(str);
		}
	};
	
	
	var currentValidator = $("#movePagesForm").validate({
			rules: {
				selectedPages: {required: true},
				selectedCollection:{required:true}
			},
			messages:{
				selectedPages:{required:"请选择要移动的页面"},
				selectedCollection:{required:"请选择目标集合"}
			},
			errorPlacement: function(error, element) {
				var holderTable = element.parentsUntil('table.dataTable').parent();
				error.appendTo(holderTable.find('.errorContainer'));
			}
		});
	
	var spage = new SearchBox('searchPages', '搜索页面');
	spage.register('#pageTable tbody tr', 'label');
	
	var scoll = new SearchBox('searchCollection', '搜索集合');
	scoll.register('#collectionTable tbody tr', 'label');
	
	//registTabIndex
	spage.searchInput.attr('tabIndex', 1);
	$('input#movePage').attr('tabIndex', 2);
	scoll.searchInput.attr('tabIndex', 3);
	
	//select all
	$('input[name="selectAll"]').checkAll({
		slave: $('#pageTable input[name="selectedPages"]')
	});
});
</script>

<form id="movePagesForm" action="<vwb:Link context='configCollection' format='url'/>?cid=${collection.resourceId}&func=moveBatchPages" method="POST">
<input type="hidden" name="cid" value="${collection.resourceId}"/>

<p class="title">选择页面或者文件，移动到选定的集合：</p>
				
<div class="content-major" id="pageAlternative">
	<div class="toolHolder">
		<div id="searchPages" style="float:left"></div>
		<c:choose>
			<c:when test="${not empty elements}">
				<input id="movePage" type="submit" class="largeButton ui-RTCorner" value="移动"/>
			</c:when>
			<c:otherwise>
				<input id="movePage" type="submit" class="largeButton ui-RTCorner" value="移动" disabled />
			</c:otherwise>
		</c:choose>
	</div>
	<table id="pageTable" class="dataTable merge" >
	<thead>
		<tr>
			<td width="65" class="dtTight"><label><input type="checkbox" name="selectAll" />全选</label></td>
			<td width="45">类型</td>
			<td>标题
				<span class="errorContainer"></span>
			</td>
			<td class="dtName">创建者</td>
			<td class="dtTime">修改时间</td>
			<td class="dtShort">版本</td>
		</tr>
	</thead>
	<tbody>
	<c:choose>
		<c:when test="${not empty elements}">
			<c:forEach items="${elements}" var="item">
			<tr>
				<td class="dtCenter">
					<input type="checkbox" name="selectedPages" value='${item.resourceId};${item.type}' id="check-${item.resourceId}" />
				</td>
				<td>
					<c:choose>
						<c:when test="${item.type eq 'DPage'}">页面</c:when>
						<c:otherwise>文件</c:otherwise>
					</c:choose>
				</td>
				<td>
				<c:choose>
					<c:when test="${not empty item.title}">
						<label for="check-${item.resourceId}">${item.title}</label>
						（<c:choose>
							<c:when test="${item.type eq 'DPage'}">
								<a href="<vwb:Link page='${item.resourceId}' context='view' format='url'/>" target="_blank">查看</a>
							</c:when>
							<c:otherwise>
								<a href="<vwb:Link page='${item.resourceId}' context='file' format='url'/>" target="_blank">查看</a>
							</c:otherwise>
						</c:choose>）
					</c:when>
					<c:otherwise>
						<label for="selectedPages">未命名页面</label>
						（<a href="<vwb:Link page='${item.resourceId}' format='url'/>" target="_blank">查看</a>）
					</c:otherwise>
				</c:choose>
				</td>
				<td>${item.creator}</td>
				<td class="dtCenter"><fmt:formatDate value="${item.modifyTime }" type="date" dateStyle="medium"/></td>
				<td class="dtRight">${item.version }</td>
			</tr>
			</c:forEach>
		</c:when>
		<c:otherwise>
			<tr id="noPages"><td colspan="6">
				<p class="NA">当前集合中没有页面</p>
			</td>
			</tr>
		</c:otherwise>
	</c:choose>
	</tbody>
	</table>
</div>

<div class="content-side" id="collectionAlternative">
	<div class="toolHolder">
		<div id="searchCollection"></div>
	</div>
	<table class="dataTable merge" id="collectionTable">
	<thead>
		<tr><td class="dtTitle">集合
				<span class="errorContainer"></span>
			</td>
			<td></td>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${collectionList}" var="item">
		<tr>
			<td>
			<label><input type="radio" name="selectedCollection" value="${item.resourceId}"/>${item.title}</label>
				（<a href="<vwb:Link context='viewCollection' page='${item.resourceId}' format='url'/>" target="_blank">查看</a>）
			</td>
			<td></td>
		</tr>
		</c:forEach>
	</tbody>
	</table>
</div>

<div class="ui-clear"></div>

</form>
	
		


