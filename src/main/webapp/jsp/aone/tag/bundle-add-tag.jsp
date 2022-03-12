<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<link href="${contextPath}/jsp/aone/css/fileuploader.css" rel="stylesheet" type="text/css">	
<c:choose>
	<c:when test="${starmark }">
		<div class="iconLynxTag icon-checkStar checked withTag" rid=${rid }>&nbsp;</div>
	</c:when>
	<c:otherwise>
		<div class="iconLynxTag icon-checkStar withTag" rid=${rid }>&nbsp;</div>
	</c:otherwise>
</c:choose>
<div class="content-resTag">
	<ul class="tagList">
		<c:forEach items="${tagMap}" var="tagItem">
			<li tag_id="${tagItem.key }">
				<a target="_blank" href="<vwb:Link context='tag' format='url'/>#&tag=${tagItem.key }">${tagItem.value}</a>
				<a class="delete-tag-link lightDel" tag_id="${tagItem.key }" rid="${rid }"></a>
			</li>
		</c:forEach>
		<li resource_id="${rid}"  class="newTag"><a>+</a></li>
	</ul>
</div>


