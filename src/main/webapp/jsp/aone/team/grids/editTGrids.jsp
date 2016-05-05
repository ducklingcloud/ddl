<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/uiLib.css" type="text/css" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/aone.css" type="text/css" />
<script type="text/javascript" src="${contextPath}/jsp/aone/js/uiLib-jQuery.js"></script>
<script type="text/javascript" src="${contextPath}/jsp/aone/js/global.js"></script>
<script type="text/javascript" src="${contextPath}/scripts/jquery/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript">
$(document).ready(function(){
	$('table.dataTable tr:nth-child(even)').addClass('striped');
	
	/* switch */
	$('#collectionModeSelector ul.switch a').click(function(){
		$('#collectionModeSelector ul.switch li').removeClass('chosen');
		$(this).parent().addClass('chosen');
		$('.content-menu-body').fadeOut();
		$('#collection-'+$(this).attr('name')+'.content-menu-body').fadeIn();		
	});
	/* initiate */
	$('#collectionModeSelector ul.switch li.chosen a').click();
	
/*
	$('#collection-gridMode ul.collectionList > li > h4 > a,' + 
		'#collection-listMode table.dataTable > tbody > tr > th > a').each(function(){
		$(this).attr('target', getTargetName($(this), ''));
	});
*/

/* HIDE SHORTCUTS - IN ORDER TO VIEW COLLECTIONS ONLY */
	$('#collection-listMode a#foldShortcut').click(function(){
		if ($(this).attr('folded')=='false') {
			$(this).attr('folded', 'true');
			$('#collection-listMode table.dataTable ul.shortcutList').fadeOut();
		}
		else {
			$(this).attr('folded', 'false');
			$('#collection-listMode table.dataTable ul.shortcutList').fadeIn();
		}
	});
	
/* SEARCH MATCHED ITEMS WITHIN PAGE */
	var collectionSearch = new SearchBox('collectionSearch', '搜索集合和快捷方式', false, true, true);
	collectionSearch.doSearch = function(QUERY){
		$('#collection-gridMode ul.collectionList>li').each(function(){
			collectionSearch.findMatches(QUERY, $(this), 'a');
		});
		$('#collection-listMode table.dataTable tbody > tr').each(function(){
			collectionSearch.findMatches(QUERY, $(this), 'a');
		});
	};
	collectionSearch.isMatch = function(OBJ) { OBJ.fadeIn(); };
	collectionSearch.notMatch = function(OBJ) { OBJ.fadeOut(); };
	collectionSearch.resetSearch = function() {
		$('#collection-gridMode ul.collectionList > li').fadeIn();
		$('#collection-listMode table.dataTable tbody > tr').fadeIn();
	};
	
	$( "ul#teamGridContainer" ).sortable();
	$( "ul#teamGridContainer" ).disableSelection();
	
});
</script>

<div class="toolHolder control">
	<a class="largeButton" name="viewMode" href="<vwb:Link context='teamHome' format='url'/>">返回</a>
	<h1>集合与快捷</h1>
</div>

<div id="collection-gridModeEdit" class="content-through" >
<form id="collection-grid-form" action="<vwb:Link context='teamHome' format='url'/>/grid" method="POST">
	<div class="holder space">
		<h4>集合顺序：<input type="submit" class="largeButton" value="保存修改"/></h4>
	</div>
	<ul id="teamGridContainer" class="collectionList" >
	<c:forEach items="${covList}" var="covItem">
		<li class="ui-state-default">
			<input type="hidden" name="func" value="updateCollectionSequence"/>
			<input type="hidden" name="cid" value="${covItem.collection.resourceId}"/>
			<h4>
				<span class="ui-RTCorner">
					<a href="<vwb:Link context='viewCollection' page='${covItem.collection.resourceId}' format='url'/>">查看</a>
				</span>
				<div class="title">${covItem.collection.title}</div>
				<div class="ui-clear"></div>		
			</h4>
			<div class="gridContent">
				<div class="editGridItem">
					<a class="iconLink config" href="<vwb:Link context='configCollection' format='url'/>?cid=${covItem.collection.resourceId}#shortcutTab">编辑</a>
				</div>
			<c:choose>
				<c:when test="${fn:length(covItem.shortcutList) eq 0}">
					<p class="NA">没有快捷方式</p>
				</c:when>
				<c:otherwise>
					<c:forEach items="${covItem.shortcutList}" var="scItem" begin="0" end="1" step="1">
						<p>${scItem.title}</p>
					</c:forEach>
				</c:otherwise>
			</c:choose>
			</div>
			<h5>共有${fn:length(covItem.shortcutList)}条快捷</h5>
	</c:forEach>
	</ul>
</form>
	<div class="bedrock"></div>
</div>
