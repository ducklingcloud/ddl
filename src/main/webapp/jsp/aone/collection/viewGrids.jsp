<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script type="text/javascript">
$(document).ready(function(){
	
	var baseURL = "<vwb:Link context='viewCollection' page="${cid}" format='url'/>/grid";
	
});
</script>
<style>
	#gridContainer { list-style-type: none; margin: 0; padding: 0; }
	#gridContainer li { margin: 3px 3px 3px 0; padding: 1px;display:block;float: left;text-align: center; }
</style>
<div id="content-title">
	<input type="hidden" name="collectionId" value="${collection.resourceId}"/>
	<div id="editTool">
		<a title="编辑工具" class="editTool"><span></span></a>
		<ul id="toolGroup">
			<li><a href="<vwb:Link context='createPage' page="${collection.resourceId}" format='url'/>" class="toolNewPage"><span>+新建页面</span></a></li>
        	<li><a href="<vwb:Link context='quick' format='url'/>?func=uploadFiles&cid=${collection.resourceId}">上传文件</a></li>
        	<li><a href="<vwb:Link context='configCollection' format='url'/>?cid=${collection.resourceId}#pageTab">移动页面</a></li>
        	<li><a href="<vwb:Link context='viewCollection' page="${collection.resourceId}" format='url'/>?func=export">导出集合</a></li>
        	<c:if test="${collection.resourceId != 1}">
        		<li><a id="delete-current-collection">删除本集合</a></li>
        	</c:if>
        	<li><a href="<vwb:Link context='configCollection' format='url'/>?cid=${collection.resourceId}" class="toolConfig"><span>设置</span></a></li>
		</ul>
		<div class="decoLeft"></div>
	</div>
	<h1>${collection.title}</h1>
</div>
   
<div id="cmainConsole" class="toolHolder">
	<div class="ui-RTCorner">
		<a class="iconLink config" href="<vwb:Link context='viewCollection' page="${cid}" format='url'/>/grid?func=editGrids">编辑格子</a>
	</div>
	<ul class="switch" style="float:left">
		<li><a href="<vwb:Link context='viewCollection' page="${collection.resourceId}" format='url'/>?func=viewList">列表模式</a></li>
		<li class="chosen"><a>格子模式</a></li>
	</ul>
	<div class="ui-clear"></div>
</div>

<div class="content-through">
	<div id="viewGridContainer">
	<c:choose>
		<c:when test="${fn:length(gridList) eq 0 }">
			<p class="a1-feed-none">还没有创建过格子。</p>
			<p style="font-size:1.2em; text-align:center"><a class="iconLink config" href="<vwb:Link context='viewCollection' page="${cid}" format='url'/>/grid?func=editGrids">创建格子</a></p>
		</c:when>
		<c:otherwise>
		<c:forEach items="${gridList}" var="item" varStatus="status">
			<c:if test="${(status.index mod collection.gridColumn) eq 0}">
				<div class="gridRowBg col${collection.gridColumn}">
				<ul class="gridRow">
			</c:if>
			<li>
				<h4>${item.grid.title}
					<span class="ui-RTCorner">
						<a class="iconLink config" href="<vwb:Link context='viewCollection' page="${cid}" format='url'/>/grid?func=editGridItems&gid=${item.grid.id}" title="编辑"></a>
					</span>
				</h4>
				<c:forEach items="${item.gridItemList}" var="gridItem">
					<p>
					<c:choose>
						<c:when test="${gridItem.item.resourceType eq 'DPage'}">
							<a href="<vwb:Link context='view' page='${gridItem.item.resourceId}' format='url'/>">${gridItem.title}</a>
						</c:when>
						<c:otherwise>
							<a href="<vwb:Link context='file' page='${gridItem.item.resourceId}' format='url'/>">${gridItem.title}</a>
						</c:otherwise>
					</c:choose>
					</p>
				</c:forEach>
			</li>
			<c:choose>
				<c:when test="${(status.index) eq fn:length(gridList)-1}">
					</ul></div>
				</c:when>
				<c:otherwise>
					<c:if test="${(status.index mod collection.gridColumn) eq (collection.gridColumn-1)}">
						</ul></div>
					</c:if>
				</c:otherwise>
			</c:choose>
		</c:forEach>
		</c:otherwise>
	</c:choose>
	</div>
	<div class="bedrock"></div>
</div>
