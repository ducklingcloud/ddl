<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
    <%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<div id="tagSelector"  class="content-menu readyHighLight0">
		<p class="leftMenu-title">
			<a class="iconLink config ui-RTCorner" href='<vwb:Link context='configTag' format='url'/>' title="管理标签"></a>
			<a href="${contextPath}/ddlTagHelp.jsp" target="_blank" class="ui-iconButton help tagHelp"></a>
		文件标签</p>
		<div class="tagGroupsDiv">
		<c:forEach items="${tagGroups}" var="gitem">
			<p class="ui-navList-title tagGroupTitle subNavList leftMenu-subTitle">
				<%-- <a class="iconLink config ui-RTCorner" href='<vwb:Link context='configTag' format='url'/>' title="管理标签"></a> --%>
				<a class="iconFoldable" id="tag1Control" title="展开/折叠"></a>
				${gitem.group.title}
			</p>
			<ul class="ui-navList leftMenuUl">
				<c:if test="${empty gitem.tags}">
					<li class="NA">无标签</li>
				</c:if>
				<c:forEach items="${gitem.tags}" var="tagItem">
					<li><a id="tag-for-${tagItem.id}" class="tag-option multiple" key="tag" value="${tagItem.id}">
						<span class="tagTitle">${tagItem.title}</span><span class="tagResCount">${tagItem.count}</span>
						</a>
						<a class="addToQuery"><span>+</span></a>
					</li>
				</c:forEach>
			</ul>
		</c:forEach>
		</div>
	<div class="noGroupTagTitle" <c:if test="${empty tags}"> style="display:none"</c:if>>
		<p class="ui-navList-title tagGroupTitle subNavList leftMenu-subTitle noGroupTagTitle">
			<a class="iconFoldable" id="tag1Control" title="展开/折叠"></a>
			未分类标签
		</p>
		<ul class="ui-navList leftMenuUl" id="ungrouped-tag-list">
			<c:forEach items="${tags}" var="item">
				<li><a id="tag-for-${item.id}" class="tag-option multiple" key="tag" value="${item.id}">
					<span class="tagTitle">${item.title}</span><span class="tagResCount">${item.count}</span>
					</a>
					<a class="addToQuery" title="同时选中该标签"><span>+</span></a>
				</li>
			</c:forEach>
		</ul>
		</div>
</div>