<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<script type="text/javascript">
$(document).ready(function(){
	
	$("#add-grid-button").click(function(){
		ui_showDialog('add-collection-dialog');
	});
	
	var baseURL = "<vwb:Link context='viewCollection' page="${cid}" format='url'/>/grid";
	
	function submitCreateGridForm(){
		var parameter = $("#create-grid-form").serialize();
		ajaxRequest(baseURL,parameter,afterCreateGrid);
	};
	
	function mouseSideDialog(OBJ, ORIGIN) {
		var wrap = $('body>.ui-wrap');
		var limit = wrap.offset().left + wrap.width();
		var dleft = ORIGIN.left;
		if (dleft + OBJ.width()>limit) {
			dleft = limit - OBJ.width();
		}
		OBJ.css('top', ORIGIN.top).css('left', dleft);
	}
	
	$('a.lightDel').live('click', function(){
		var link = $(this).attr('url');
		var pos = $(this).offset();
		$('#delete-grid-dialog #delete-grid-confirm').bind('click', function(){
			window.location.href = link;
		});
		ui_showDialog('delete-grid-dialog');
		mouseSideDialog($('#delete-grid-dialog'), pos);
	});
	
	$('#create-grid-form').bind('submit',function(){
		submitCreateGridForm();
     	return false;
    });
    
	$("#create-grid-button").click(function(){
		submitCreateGridForm();
	});
	
	function afterCreateGrid(data){
		$("#grid-template").tmpl(data).insertBefore("#last-grid");
		ui_hideDialog('add-collection-dialog');
	};
	
	$('select[name="gridColumn"]').change(function(){
		var selected = $('select[name="gridColumn"]').val(); 
		$('select[name="gridColumn"]>option').each(function(){
			if ($(this).val()==selected) {
				$('#gridContainer').addClass('col'+selected);
			}
			else {
				$('#gridContainer').removeClass('col'+$(this).val());
			}
		});
	});
	$("#save-pref-button").click(function(){
		var parameter = "func=changeColumnNumber&gridColumn="+$("select[name='gridColumn']").val();
		ajaxRequest(baseURL,parameter,afterChangeColumns);
	});
	
	function afterChangeColumns(data){
		ui_spotLight('spot-gridColumn', 'success', '格子列数已保存', 'fade');
	};
	
	$("#save-grid-sequence").click(function(){
		var parameter = $("#grid-form").serialize();
		ajaxRequest(baseURL,parameter,afterSaveGridSequence);
	});
	
	function afterSaveGridSequence(data){
		ui_spotLight('spot-gridSequence', 'success', '格子顺序已保存', 'fade');
	};

	$( "#gridContainer" ).sortable({items:"li:not(.ui-state-disabled)"});
	$( "#gridContainer" ).disableSelection();
	
});
</script>

<div class="toolHolder control"> 
	<a class="largeButton" href="<vwb:Link context='viewCollection' page="${cid}" format='url'/>?func=viewGrid">返回</a>
	<h1>${collection.title}</h1>
</div>
<div class="subHolder space">
	<h4>格子列数：
	<select name="gridColumn" value="${collection.gridColumn}">
		<option value="2" <c:if test="${collection.gridColumn eq 2}">selected</c:if>>2</option>
		<option value="3" <c:if test="${collection.gridColumn eq 3}">selected</c:if>>3</option>
		<option value="4" <c:if test="${collection.gridColumn eq 4}">selected</c:if>>4</option>
		<option value="5" <c:if test="${collection.gridColumn eq 5}">selected</c:if>>5</option>
	</select>
	<input type="button" class="largeButton" id="save-pref-button" value="保存设置"/>
	<span class="ui-spotLight" id="spot-gridColumn"></span>
	</h4>
</div>

<div id="editGridContainer" class="content-through">
	<div class="holder space">
		<h4>格子顺序：<input type="button" class="largeButton" id="save-grid-sequence" value="保存顺序"/>
			<span class="ui-spotLight" id="spot-gridSequence"></span>
		</h4>
	</div>
	<form id="grid-form">
	<input type="hidden" name="func" value="updateGridSequence"/>
	<ul id="gridContainer" class="col${collection.gridColumn}">
		<c:forEach items="${gridList}" var="item" varStatus="status">
			<li class="ui-state-default">
				<h4>
					<span class="ui-RTCorner"><a class="lightDel" url="?func=deleteGrid&gid=${item.grid.id}"></a></span>
					<input type="hidden" name="gid" value="${item.grid.id}"/>
					<div class="title">${item.grid.title}</div>
					<div class="ui-clear"></div>
				</h4>
				<div class="gridContent">
					<div class="editGridItem">
						<a class="iconLink config" href="?func=editGridItems&gid=${item.grid.id}" title="编辑格子标题和内容">编辑</a>
					</div>
				<c:forEach items="${item.gridItemList}" begin="0" end="1" step="1" var="gridItem">
					<p>${gridItem.title}</p>
				</c:forEach>
				</div>
				<h5>共有${fn:length(item.gridItemList)}条记录</h5>
			</li>
		</c:forEach>
		<li id="last-grid" class="ui-state-disabled"><input type="button" class="largeButton" value="+ 添加格子" id="add-grid-button"/></li>
	</ul>
	</form>
	<div class="bedrock"></div>
</div>


<div class="ui-dialog" id="add-collection-dialog" style="width:400px;">
	<span class="ui-dialog-x"></span>
	<p class="ui-dialog-title">
		新建网格
	</p>
	<form id="create-grid-form" >
		<input type="hidden" name="func" value="addGrid"/>
		<input type="hidden" name="type" value="${collection.gridColumn}"/>
		<div class="ui-dialog-body">
			<table class="ui-table-form" >
				<tr><th id="page-option">网格名称：</th>
					<td><input type="text" name="title" style="width:180px;" /></td>
				</tr>
			</table>
		</div>
		<div class="ui-dialog-control">
			<input type="button" id="create-grid-button" value="保存"/>
			<a class="ui-dialog-close ui-text-small">取消</a>
		</div>
	</form>
</div>

<div class="ui-dialog" id="delete-grid-dialog" style="width:400px;">
	<span class="ui-dialog-x"></span>
	<p class="ui-dialog-title">删除格子</p>
	<div class="ui-dialog-body">
		<p>即将删除格子和其中的链接，将不可恢复，确认删除？</p>
		<p class="ui-text-note">链接指向的内容不会被删除</p>
	</div>
	<div class="ui-dialog-control">
		<input type="button" id="delete-grid-confirm" value="删除格子"/>
		<a class="ui-dialog-close ui-text-small">取消</a>
	</div>
</div>


<script id="grid-template" type="text/html">
<li class="ui-state-default">
	<h4>
		<span class="ui-RTCorner">
			<a class="lightDel" url="?func=deleteGrid&gid={{= cgid}}"></a>
		</span>
		<input type="hidden" name="gid" value="{{= cgid}}"/>
		<div class="title">{{= title}}</div>
		<div class="ui-clear"></div>
	</h4>
	<div class="gridContent">
		<div class="editGridItem">
			<a class="iconLink config" href="?func=editGridItems&gid={{= cgid}}">编辑</a>
		</div>
	</div>
	<h5>共有0条记录</h5>
</li>
</script>

