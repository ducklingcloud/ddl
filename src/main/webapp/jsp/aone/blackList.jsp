<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<link rel="stylesheet" type="text/css"
	href='${contextPath}/scripts/DUI/autosearch/jquery.autocomplete.css' />
<fmt:setBundle basename="templates.default" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/uiLib.css"
	type="text/css" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/aone.css"
	type="text/css" />
<script type="text/javascript" src="${contextPath}/scripts/DUI/autosearch/jquery.autocomplete.js"></script>
<script type="text/javascript"
	src="${contextPath}/jsp/aone/js/uiLib-jQuery.js"></script>
<script type="text/javascript"
	src="${contextPath}/jsp/aone/js/global.js"></script>
<script type="text/javascript">
	$(document).ready(function(){
		$("#addButton").attr("disabled", true);
		$("#txtPageInput").autocomplete('<vwb:Link format='url' jsp='blackList'/>?func=allPages',
			{
				parse: function(data) { 
				return $.map(data, function(row) { 
					return {data: row, value: row.title,m_value:row.id}
				}); 
			}, 
			formatItem: function(row, i, max) {//显示效果|必选
				return "<td>" + row.title+ "</td><td>" + row.id +"</td>";
			},
			
			extraParams: {query:function(){return $('#txtPageInput').val();},datatype:"json"},
			dataType: "json",
			onSelected:function(row){
				$("#txtPageId").val(row.m_value);
				$("#addButton").attr("disabled", false);
			},
			autoFill: true,//自动填充
			scroll:true,//是否加滚动条
			scrollHeight:100,//有滚动条时候下拉框长度
			delay:10,//延迟10秒  
			max:100//下拉列表显示的数据长度
		});
		
		$(document).scroll(function(){
			if ($(window).height()<$('#toTop').offset().top)
				$('#toTop').show();
			else
				$('#toTop').hide();
		});
		
	});
	function removeBlackList(pageId){
		var form = document.removeForm;
		form.pageId.value=pageId;
		form.submit();
	}
</script>
<style>
#txtPageInput { font-size:11pt; padding:0.4em 1em; width:28em; }
#addButton { font-size:11pt; padding:0.2em 1em; }
#search { margin-bottom:3em; }
.a1-blacklist tr td:first-child { text-align:right; }
</style>
<div class="ui-wrap" id="blackListManagement" style="width:90%; margin:0px auto;">
<form name="removeForm" action="<vwb:Link jsp="blackList" format='url'/>" method="post">
	<input type="hidden" name="pageId" value=""/>
	<input type="hidden" name="func" value="removeFromList"/>
</form>

	<div id="search">
	<form action="<vwb:Link jsp="blackList" format='url'/>" method="post">
		<p>将页面添加到黑名单<span class="ui-text-note">（不在热点页面中显示）</span>：</p>
		<p align="center"><input type="text" id="txtPageInput" name="page" />
		<input type="submit" id="addButton" value="添加到黑名单" />
		<input type="hidden" id="txtPageId" name="pageId" />
		<input type="hidden" name="func" value="addToList"/>
		</p>
	</form>
	</div>
	
	<c:choose>
		<c:when test="${not empty blackList}">
			<table class="a1-blacklist ui-table-horizon" width="90%">
				<tr>
					<th width="50">
						页面ID
					</th>
					<th>
						页面名称
					</th>
					<th width="100">
						创建者
					</th>
					<th width="80">
						创建时间
					</th>
					<th width="20">&nbsp;</th>
				</tr>
				<c:forEach var="page" items="${blackList}">
					<tr>
						<td>
							${page.id}
						</td>
						<td>
							<a href='<vwb:Link page="${page.id}" format='url'/>' target="_blank">${page.title}</a>
						</td>
						<td>
							${page.creator}
						</td>
						<td>
							<fmt:formatDate type='date' value='${page.createTime}' />
						</td>
						<td><a class="ui-linkButton-del" href="#" onclick="removeBlackList('${page.id}')" title="移除"></a></td>
					</tr>
				</c:forEach>
			</table>
		</c:when>
		<c:otherwise>
			<p class="a1-feed-none">目前系统中没有被加入黑名单的页面。</p>
		</c:otherwise>
	</c:choose>
	
	<div id="toTop" style="text-align:right; display:none"><a href="#">返回顶部</a></div>
</div>
