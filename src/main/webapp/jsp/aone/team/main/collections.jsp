<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
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
			$(this).attr('folded', 'true').text('展开快捷');
			$('#collection-listMode table.dataTable ul.shortcutList').fadeOut();
		}
		else {
			$(this).attr('folded', 'false').text('隐藏快捷');
			$('#collection-listMode table.dataTable ul.shortcutList').fadeIn();
		}
	});
	
/* SEARCH MATCHED ITEMS WITHIN PAGE */
	var collectionSearch = new SearchBox('collectionSearch', '过滤集合和快捷方式', false, true, true);
	collectionSearch.doSearch = function(QUERY){
		$('#collection-gridMode table#collection-container td').each(function(){
			collectionSearch.findMatches(QUERY, $(this), 'a');
		});
		$('#collection-listMode table.dataTable tbody > tr').each(function(){
			collectionSearch.findMatches(QUERY, $(this), 'a');
		});
	};
	collectionSearch.isMatch = function(OBJ) { OBJ.fadeIn(); };
	collectionSearch.notMatch = function(OBJ) { OBJ.fadeOut(); };
	collectionSearch.resetSearch = function() {
		$('#collection-gridMode table#collection-container td').fadeIn();
		$('#collection-listMode table.dataTable tbody > tr').fadeIn();
	};
	
});
</script>

<div id="collectionModeSelector" class="filterHolder">
		<ul class="switch" style="float:left">
			<li class="chosen"><a name="gridMode">网格模式</a></li>
			<li><a name="listMode">列表模式</a></li>
		</ul>
		<div id="collectionSearch" class="ui-RTCorner"></div>
	</div>
	

<div id="collection-gridMode" class="content-menu-body" style="display:none">
	<div class="subHolder">
		<a name="editMode" href="<vwb:Link context='teamHome' format='url'/>/grid?func=editGrids" class="iconLink config ui-RTCorner">编辑网格</a>
		<div class="ui-clear"></div>
	</div>
	<table id="collection-container">
	<tbody>
	<c:forEach items="${covList}" var="covItem" varStatus="status">
		<c:if test="${(status.index mod 3) eq 0}"><tr></c:if>
		<td>
			<h4>
				<a href="<vwb:Link context='viewCollection' page='${covItem.collection.resourceId}' format='url'/>">${covItem.collection.title}</a>
			</h4>
		<c:choose>
			<c:when test="${fn:length(covItem.shortcutList) eq 0}">	</c:when>
			<c:otherwise>
				<ul class="shortcutList">
					<c:forEach items="${covItem.shortcutList}" var="scItem" begin="0" end="10" step="1">
						<c:choose>
							<c:when test="${scItem.resourceType eq 'DPage'}">
								<li><a href="<vwb:Link page='${scItem.resourceId}' context='view' format='url'/>">${scItem.title}</a></li>
							</c:when>
							<c:otherwise>
								<li><a href="<vwb:Link page='${scItem.resourceId}' context='file' format='url'/>">${scItem.title}</a></li>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</ul>
			</c:otherwise>
		</c:choose>
		</td>
		<c:if test="${(status.index mod 3) eq 2}"></tr></c:if>
	</c:forEach>	
	</tbody>
	</table>
</div>

<div id="collection-listMode" class="content-menu-body" style="display:none">
	<!-- Collection and Shortcut -->
	<table class="dataTable merge">
	<thead>
		<tr>
			<td class="dtStd">集合</td>
			<td>快捷方式
				<a id="foldShortcut" class="iconLink expandY" folded="false">隐藏快捷</a>
			</td>
		</tr>
	</thead>
	<tbody>
	<c:forEach items="${covList}" var="covItem">
		<tr>
			<th>
				<a href="<vwb:Link context='viewCollection' page='${covItem.collection.resourceId}' format='url'/>">${covItem.collection.title}</a>
			</th>
			<td>
		<c:choose>
			<c:when test="${fn:length(covItem.shortcutList) eq 0}">
				<p class="NA">没有快捷方式</p>
			</c:when>
			<c:otherwise>
				<ul class="shortcutList">
					<c:forEach items="${covItem.shortcutList}" var="scItem">
						<c:choose>
							<c:when test="${scItem.resourceType == 'DPage'}">
								<li><a href="<vwb:Link page='${scItem.resourceId}' context='view' format='url'/>">${scItem.title}</a></li>
							</c:when>
							<c:otherwise>
								<li><a href="<vwb:Link page='${scItem.resourceId}' context='file' format='url'/>">${scItem.title}</a></li>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</ul>
			</c:otherwise>
		</c:choose>
			</td>
		</tr>
	</c:forEach>
	</tbody>
	</table>
</div>
