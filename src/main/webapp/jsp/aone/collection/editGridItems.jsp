<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script type="text/javascript">
$(document).ready(function(){
	
	function stripeDataTable(ID){
		$('#'+ID+'>li:nth-child(even)').addClass('striped');
		$('#'+ID+'>li:nth-child(odd)').removeClass('striped');
	}
	stripeDataTable('candidate-list');
	
	var baseURL = "<vwb:Link context='viewCollection' page="${cid}" format='url'/>/grid";
	$("#addGridItemButton").click(function(){
		var parameters = $("#add-grid-items-form").serialize();
		if($("input[name='selectedItems']:checked").length!=0)
			ajaxRequest(baseURL,parameters,afterAddGridItems);
		else
			alert("您没有选择任何条目");
	});
	function afterAddGridItems(data){
		var liNode = $("input[name='selectedItems']:checked").parent().parent();
		$(liNode).appendTo("#sortable-list");
		$(liNode).addClass("ui-state-default");
		$("input[name='selectedItems']:checked").remove();
		for(var i=0;i<data.length;i++) {
			$("#"+data[i].trv+'>span:first-child').before($("#item-operation-template").tmpl(data[i]));
		}
		stripeDataTable('candidate-list');
	};
	$(".delete-item-button").live('click',function(){
		var gtid = $(this).attr("gtid");
		ajaxRequest(baseURL+"?func=deleteGridItem&gtid="+gtid,null,afterDeleteGridItem);
	});
	function afterDeleteGridItem(data){
		var liNode = $("a[gtid='"+data.gtid+"']").parent().parent();
		var title = liNode.find('span.title');
		var tempHtml = '<label><input type="checkbox" name="selectedItems" value="'+$(liNode).attr("id")+'"/>'
			+ '<span class="title">' + liNode.find('span.title').text() + '</span></label>';
		title.remove();
		$(tempHtml).appendTo($(liNode));
		$(liNode).find(".lightDel").parent().remove();
		$(liNode).appendTo("#candidate-list");
		stripeDataTable('candidate-list');
	};
	
	$( "#sortable-list" ).sortable();
	$( "#sortable-list" ).disableSelection();
	
	var altSearch = new SearchBox('alternativeSearch', '搜索页面和文件', false, true, true);
	altSearch.doSearch = function(QUERY) {
		$('ul#candidate-list > li').each(function(){
			altSearch.findMatches(QUERY, $(this), 'span.title');
		});
	};
	
	$("#save-title-button").click(function(){
		var gid = $("input[name='gid']").val();
		var text = $("input[name='title']").val(); //发送ajax请求的时候不要将中文参数编入URL中
		var params = "func=updateTitle&gid="+gid+"&title="+text;
		ajaxRequest(baseURL,params,afterUpdateTitle);
	});
	$('#update-grid-form input[name="title"]').keydown(function(KEY){
		if (KEY.ctrlKey && KEY.which=='13') {
			$('#save-title-button').click();
		}
	});
	
	
	function afterUpdateTitle(data){
		ui_spotLight('spot-gridTitle', 'success', '保存成功', 'fade');};
	
	$("#save-sequence-button").click(function(){
		ajaxRequest(baseURL,$("#update-grid-form").serialize(),afterUpdateSequence);
	});
	
	function afterUpdateSequence(data){
		ui_spotLight('spot-gridItemSeq', 'success', '保存成功', 'fade');
	};
	
	$("input[type='text']").each(function(){
		$(this).keypress(function(e) {
			var key = window.event ? e.keyCode : e.which;
	        if(key.toString() == "13"){return false;}
	    });
	});
});
</script>

<div class="toolHolder control">
	<a class="largeButton" href="<vwb:Link context='viewCollection' page="${cid}" format='url'/>?func=viewGrid">返回</a>
	<h1>${collection.title}</h1>
</div>
<div class="content-through" id="editGridItem">
	<form id="update-grid-form" action="<vwb:Link context='viewCollection' page="${cid}" format='url'/>/grid" method="POST">
	<input type="hidden" name="func" value="updateItemSequence"/>
	<input type="hidden" name="gid" value="${grid.id}"/>
	<div class="subHolder space">
		<h4>
			<label>格子标题：</label>
			<input type="text" name="title" value="${grid.title}"/>
			<input type="button" class="largeButton" id="save-title-button" value="保存标题" title="保存标题[Ctrl+回车]"/>
			<span class="ui-spotLight" id="spot-gridTitle"></span>
		</h4>
	</div>
	<div class="content-major">
		<div class="holder space">
			<h4>当前格子内容</h4>
			<p><input type="button" class="largeButton" id="save-sequence-button" value="保存顺序"/>
				<span class="ui-spotLight" id="spot-gridItemSeq"></span>
			</p>
			<ul id="sortable-list">
				<c:forEach items="${gridItemList}" var="gridItem">
				<li class="ui-state-default" id="${gridItem.item.resourceId}-${gridItem.item.resourceType}">
					<span class="ui-RTCorner">
						<a class="delete-item-button lightDel" gtid="${gridItem.item.id}"></a>
					</span>
					<span class="ui-RTCorner view">
						<c:choose>
							<c:when test="${gridItem.item.resourceType eq 'DPage'}">
								<a href="<vwb:Link context='view' page='${gridItem.item.resourceId}' format='url'/>" target="_blank">查看</a>
							</c:when>
							<c:otherwise>
								<a href="<vwb:Link context='file' page='${gridItem.item.resourceId}' format='url'/>" target="_blank">查看</a>
							</c:otherwise>
						</c:choose>
					</span>
					<span class="title">${gridItem.title}</span>
					<input type="hidden" name="sequence" value="${gridItem.item.sequence}"/>
					<input type="hidden" name="gtid" value="${gridItem.item.id}"/>
				</li>
				</c:forEach>
			</ul>
		</div>
	</div>
	</form>
	
	<div class="content-side"> 
	<div class="holder space">
		<h4>候选内容</h4>
	</div>
	<form id="add-grid-items-form">
	
	<div class="subHolder isolate">
		<div id="alternativeSearch" class="ui-RTCorner"></div>
		<input type="button" id="addGridItemButton" class="largeButton" value="添加选中的条目"/>
	</div>
	
	<input type="hidden" name="func" value="addGridItems"/>
	<input type="hidden" name="gid" value="${grid.id}"/>
	
	<ul id="candidate-list" class="dataTable merge">
		<c:forEach items="${candidateList}" var="item">
			<li id="${item.resourceId}-${item.type}">
				<span class="ui-RTCorner">
					<c:choose>
						<c:when test="${item.type eq 'DPage'}">
							<a href="<vwb:Link context='view' page='${item.resourceId}' format='url'/>" target="_blank">查看</a>
						</c:when>
						<c:otherwise>
							<a href="<vwb:Link context='file' page='${item.resourceId}' format='url'/>" target="_blank">查看</a>
						</c:otherwise>
					</c:choose>
				</span>
				<label>
					<input type="checkbox" name="selectedItems" value="${item.resourceId}-${item.type}"/>
					<span class="title">${item.title}</span>
				</label>
			</li>
		</c:forEach>
	</ul>
	</form>
	</div>
	<div class="ui-clear"></div>
</div>

<script id="item-operation-template" type="html/text">
	<span class="ui-RTCorner">
		<a class="delete-item-button lightDel" gtid="{{= gtid}}"></a>
	</span>
	<input type="hidden" name="sequence" value="65536"/>
	<input type="hidden" name="gtid" value="{{= gtid}}"/>
</script>

