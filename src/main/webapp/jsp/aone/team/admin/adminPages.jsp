<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
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
	
	
	var searchPages = new SearchBox('searchPages', '搜索页面或集合', false);
	
	
});
</script>

<p class="a1-feed-none">To be ReDesigned.</p>

<div class="content-major" style="display:none">
	<div style="width:70%; float:left;">
		<div class="toolHolder">
			<p>选择页面移动到当前集合：${collection.title}</p>
			<div id="searchPages" class="search"></div>
		</div>
		<table class="dataTable merge">
		<thead>
			<tr><td class="dtStd">集合</td>
				<td class="dtLong">页面</td>
				<td></td>
			</tr>
		</thead>
		<tbody>
		</tbody>
		</table>
		
	</div>	
	
	<div style="width:29%; float:right;">
	
	</div>
	
	<div class="ui-clear"></div>
		
		<ul>
			<li style="display:inline"><a class="collectionClass" cid="0">未分类</a></li>
			<c:forEach items="${collectionList}" var="item">
				<li style="display:inline"> | <a class="collectionClass" cid="${item.resourceId}">${item.title}</a></li>
			</c:forEach>
		</ul>
		<table id="chooseTable">
			<tr>
				<td>
					<select name="pageCandidates" id="candidates-box"  multiple="true">
						<c:forEach items="${candidates}" var="pageItem">
							<option id="option${pageItem.pid}" value="${pageItem.pid}" 
							url="<vwb:Link page='${pageItem.pid}' format='url'/>">${pageItem.title}</option>
						</c:forEach>
					</select>
				</td>
				<td>
					<input type="button" id="chooseButton" value="&gt;&gt;"/><br/>
					<input type="button" id="disChooseButton" value="&lt;&lt;"/>
				</td>
				<td>
					<form id="addPagesForm">
						<input type="hidden" name="cid" value="${collection.resourceId}"/>
						<input type="hidden" name="func" value="addPages"/>
						<select name="pageChoose" id="select-box"  multiple="true">
						</select>
					</form>
				</td>
			</tr>
		</table>
		<div class="clear"></div>
		<input type="button" id="submitSelectionButton" value="提交"/>
</div>

<div id="content-side" style="display:none">
	<div class="sideBlock">
		<h4>${collection.title} 已有页面</h4>
		<c:if test="${not empty cPages}">
			<c:forEach items="${cPages}" var="item">
				<ul class="fileList">
				<c:choose>
					<c:when test="${not empty item.title}">
						<li><a href="<vwb:Link page='${item.pid}' format='url'/>">${item.title}</a></li>
					</c:when>
					<c:otherwise>
						<li><a href="<vwb:Link page='${item.pid}' format='url'/>">Undefined</a></li>	
					</c:otherwise>
				</c:choose>
				</ul>
			</c:forEach>
		</c:if>
		<c:if test="${empty cPages}">
			<p class="NA">当前集合中没有添加任何页面</p>
		</c:if>
	</div>
</div>

<div class="ui-clear"></div>
