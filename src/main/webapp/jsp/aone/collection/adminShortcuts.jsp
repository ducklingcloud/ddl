<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<script type="text/javascript">
$(document).ready(function(){
	function dataTableStripe() {
		$('ul.dataTable li:nth-child(even)').addClass('striped');
		$('ul.dataTable li:nth-child(odd)').removeClass('striped');
	}
	dataTableStripe();
	
	var currentURL = "<vwb:Link context='configCollection' format='url'/>?cid=${collection.resourceId}";
	var shortcutAdded = '', shortcutRemoved = '';
	$('a.addShortcut').live('click', function(){
		$('input[name="resourceType"]').attr('value',$(this).attr("type"));
		$('input[name="shortcutName"]').attr('value', $(this).attr("title"));
		$('input[name="rid"]').attr('value', $(this).attr("rid"));
		shortcutAdded = $(this).attr("rid");
		ajaxRequest(currentURL, $("#addShortcutForm").serialize(), afterAddShortcut); 
	});
	
	function afterAddShortcut(data){
		if (data.status=='success') {
			$('#shortcutTable #noShortcut-row').hide();
			$("#shortcut-item-template").tmpl(data).appendTo("#shortcutTable");
			$('#alterTable tr[rid="' + shortcutAdded + '"]').addClass("isShortCut");
			dataTableStripe();
		}
		else {
			alert('不能重复添加快捷');
		}			
	};
	
	$('a.removeShortcut').live('click', function(){
		var queryParam = "func=deleteShortcut&sid="+$(this).attr("sid");
		ajaxRequest(currentURL,queryParam,afterRemoveShortcut);
	});
	
	function afterRemoveShortcut(data) {
		$("#shortcutTable li[sid='"+data.sid+"']").slideUp('fast', function(){
			$(this).remove();});
		dataTableStripe();
	}
	
	var searchPage = new SearchBox('searchPage', '搜索页面或文件', false, true, true);
	searchPage.doSearch = function(QUERY) {
		$('#alterTable tbody tr').each(function(){
			searchPage.findMatches(QUERY, $(this), 'td');
		});
	};
	searchPage.isMatch = function(OBJ) { OBJ.show(); };
	searchPage.notMatch = function(OBJ) { OBJ.hide(); };
	searchPage.resetSearch = function() {
		$('#alterTable tbody tr').show();
	};
	
	$('a#aboutShortcut').click(function(){
		$('#msg-shortcut').toggle();
	});
	
	$( "#shortcutTable" ).sortable({
		items:"li:not(.ui-not-sortable)"
	});
	$( "#shortcutTable" ).disableSelection();
	
	var requestURL = "<vwb:Link context='configCollection' format='url'/>?cid=${collection.resourceId}";
	$("#save-sequence-button").click(function(){
		ajaxRequest(currentURL,$("#shortcut-form").serialize(),afterSavePlace);
	});
	
	function afterSavePlace(data){
		ui_spotLight('spot-sequence', 'success', '顺序已保存', 'fade');
	};
	
	$("input[type='text']").each(function(){
		$(this).keypress(function(e) {
			var key = window.event ? e.keyCode : e.which;
			if(key.toString() == "13"){return false;}
	    });
	});
	
});
</script>

<div class="toolHolder" id="configCol-shortcut">
	<div id="searchPage" style="float:left"></div>
	<div id="infoHelp" class="ui-RTCorner">
		<a class="ui-iconButton help" id="aboutShortcut">关于快捷方式</a>
		<a class="ui-iconButton help" style="display:none">热度、搜索率、快捷率</a>
		<div id="msg-shortcut" class="msgFloat" style="left:60%; width:300px;">
			<p>快捷方式出现在每个集合页面的右侧，目的是便于快速找到经常使用的、或者重要的内容。</p>
			<p>建议不定期地整理快捷方式，增加近期常用的，移除不那么有用的内容。</p>
		</div>
	</div>
</div>
<div id="shortcutAlternative" class="content-major">
	<form id="addShortcutForm">
		<input type="hidden" name="func" value="addShortcuts" />
		<input type="hidden" name="shortcutName" value="" />
		<input type="hidden" name="rid" value="" />
		<input type="hidden" name="resourceType" value=""/>
	</form>
	<table id="alterTable" class="dataTable merge">
	<thead>
		<tr>
			<td>类型</td>
			<td>名称</td>
			<td class="dtName"></td>
		</tr>
	</thead>
	<tbody>
	<c:if test="${not empty elements}">
		<c:forEach var="item" items="${elements}">
			<tr rid="${item.resourceId}">
				<td>
					<c:choose>
						<c:when test="${item.type eq 'DPage'}">
							页面
						</c:when>
						<c:otherwise>
							文件
						</c:otherwise>
					</c:choose>
				</td>
				<td>
					<span class="ui-RTCorner">
					<c:choose>
						<c:when test="${item.type eq 'DPage'}">
							<a href="<vwb:Link page='${item.resourceId}' context='view' format='url'/>" target="_blank">查看</a>
						</c:when>
						<c:otherwise>
							<a href="<vwb:Link page='${item.resourceId}' context='file' format='url'/>" target="_blank">查看</a>
						</c:otherwise>
					</c:choose>
					</span>
					<c:choose>
						<c:when test="${not empty item.title}">
								<span>${item.title}</span>
						</c:when>
						<c:otherwise>
							<span>未命名页面</span>
						</c:otherwise>
					</c:choose>
				</td>
				<td class="dtCenter"><a class="addShortcut" rid="${item.resourceId}" type="${item.type}" title="${item.title}">设为快捷</a></td>
			</tr>
		</c:forEach>
	</c:if>
	<c:if test="${empty elements}">
		<tr id="noPage-row">
			<td colspan="4">
				<p class="NA">当前集合中没有可移动对象</p>
			</td>
		</tr>
	</c:if>
	</tbody>
	</table>
</div>

<div id="shortcutList" class="content-side">
	<h4>当前快捷方式</h4>
	<form id="shortcut-form" method="POST">
	<input type="hidden" name="func" value="updateShortcutSequence"/>
	<ul id="shortcutTable" class="ui-sortable">
	<c:if test="${not empty cShortcuts}">
		<c:forEach items="${cShortcuts}" var="item">
			<li sid="${item.id}" class="ui-state-default">
			<input type="hidden" name="shortcutId" value="${item.id}"/>
			<span class="ui-RTCorner">
				<a class="removeShortcut lightDel" sid="${item.id}"></a>
			</span>
			<span class="ui-RTCorner">
				<c:choose>
					<c:when test="${item.resourceType eq 'DPage'}">
						<a href="<vwb:Link page='${item.resourceId}' context='view' format='url'/>">查看</a>
					</c:when>
					<c:otherwise>
						<a href="<vwb:Link page='${item.resourceId}' context='file' format='url'/>">查看</a>
					</c:otherwise>
				</c:choose>
			</span>
			<c:choose>
				<c:when test="${not empty item.title}">
					<span class="title">${item.title}</span>
				</c:when>
				<c:otherwise>
					<span class="title">未命名页面</span>
				</c:otherwise>
			</c:choose>
			</li>
		</c:forEach>
	</c:if>
	<c:if test="${empty cShortcuts}">
		<li id="noShortcut-row">
			<span >
				<p class="NA">当前集合中没有创建快捷方式</p>
			</span>
		</li>
	</c:if>
	</ul>
	<div class="subHolder isolate holderCenter">
		<input type="button" id="save-sequence-button" value="保存顺序" class="largeButton"/>
		<span class="ui-spotLight" id="spot-sequence"></span>
	</div>
	</form>
</div>
<div class="ui-clear"></div>

<script id="shortcut-item-template" type="text/html">
<li sid="{{= sid}}" class="ui-state-default newlyAdded">
	<input type="hidden" name="shortcutId" value="{{= sid}}"/>
	<span class="ui-RTCorner"><a class="removeShortcut lightDel" sid="{{= sid}}"></a></span>
	<span class="ui-RTCorner"><a href="{{= url}}" target="_blank">查看</a></span>
	<span class="title">{{= title}}</span>
</li>
</script>
