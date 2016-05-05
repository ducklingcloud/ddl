<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<script type="text/javascript">
$(document).ready(function(){
	var tagURL = site.getURL('tag',null);
	$("a.tag-link").live('click',function(){
		window.location.href = tagURL+"#&tag="+$(this).attr("value");
	});
	
	$("a.filter-link").live('click',function(){
		window.location.href = tagURL+"#&filter="+$(this).attr("value");
	});
  
	var tagMenu = new foldableMenu({ controller: 'p.tagGroupTitle', focus: false });
	
	$("#tagSelector").children(".ui-navList").each(function(){
		var childrenNum = $(this).find("li").length;
		var i = 0;
		if(childrenNum > 5) {
			$(this).find("li").each(function(){
				i++;
				if(i>5){
					$(this).hide();
				}
			})
		}
	})
	
	$("#tagSelector").children(".ui-navList").hover(function(){
		$(this).children().show("normal");
	})
	$("#tagSelector").children(".ui-navList").mouseleave(function(){
		var j = 0;
		$(this).children().each(function(){
			j++;
			if(j>5){
				$(this).hide();
			}
		})
	})
	
	$("li.moreTags").live("click",function(){
		$(this).parent().children().show();
		$(this).remove();
	})
	
	
});
</script>
<div id="tagSelector" class="content-menu">
	<ul class="team-shortcut">
		<li class="left-common"><a href="<vwb:Link format='url' context='teamHome' />">常用</a></li>
		<li><a onclick="refreshAnchor('<vwb:Link format='url' context='teamHome' />#trace')">历史记录</a></li>
		<div class="clear"></div>
	</ul>
	<p class="ui-navList-title">
		<a class="iconLink config ui-RTCorner" href='<vwb:Link context='configTag' format='url'/>' title="管理标签"></a>
		<a href="${contextPath}/ddlTagHelp.jsp" target="_blank" class="ui-iconButton help tagHelp"></a>
	文档导航</p>
	<ul class="ui-navList">
		<li><a class="filter-link single" key="filter" value="all" ><span class="tagTitle">所有文件</span></a></li>
		<li><a class="filter-link single" key="filter" value="untaged" ><span class="tagTitle">无标签文件</span></a></li>
	</ul>
	<c:forEach items="${tagGroups}" var="gitem">
		<p class="ui-navList-title tagGroupTitle subNavList">
			<%-- <a class="iconLink config ui-RTCorner" href='<vwb:Link context='configTag' format='url'/>' title="管理标签"></a> --%>
			<a class="iconFoldable" title="展开/折叠"></a>
			${gitem.group.title}
		</p>
		<ul class="ui-navList">
			<c:if test="${empty gitem.tags}">
				<li class="NA">无标签</li>
			</c:if>
			<c:forEach items="${gitem.tags}" var="tagItem">
				<li><a id="tag-for-${tagItem.id}" class="tag-link multiple" key="tag" value="${tagItem.id}">
					<span class="tagTitle">${tagItem.title}</span><span class="tagResCount">${tagItem.count}</span></a>
				</li>
			</c:forEach>
		</ul>
	</c:forEach>
<c:if test="${not empty tags}">
	<p class="ui-navList-title tagGroupTitle subNavList">
		<%-- <a class="iconLink config ui-RTCorner" href='<vwb:Link context='configTag' format='url'/>' title="管理标签"></a> --%>
		<a class="iconFoldable" id="tag1Control" title="展开/折叠"></a>
		未分类标签
	</p>
	<ul class="ui-navList" id="ungrouped-tag-list">
		<c:forEach items="${tags}" var="item">
			<li><a id="tag-for-${item.id}" class="tag-link multiple" key="tag" value="${item.id}">
				<span class="tagTitle">${item.title}</span><span class="tagResCount">${item.count}</span></a>
			</li>
		</c:forEach>
	</ul>
</c:if>
</div>
