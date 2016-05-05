<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:if test="${resourePath != null}">
	<div class="resourcePath" >
		<ol class="breadcrumb">
			<li class='pathAllFiles' ><a href="javascript:void(0)">所有文件</a>&nbsp;&gt;</li>
			<c:forEach items="${resourePath}" var="item">
				<li class="pathActive">
					<a class="filePath" href="javascript:void(0)">${fn:escapeXml(item.title)}</a>&nbsp;&gt;<input class="pathRid" type="hidden" value="${item.rid }">
				</li>	
			</c:forEach>
			<c:if test="${resource!=null }">
				<li><span class="subHint">${fn:escapeXml(resource.title) }<c:if test="${resource.itemType=='DPage' }">.ddoc</c:if></span></li>
			</c:if>
		</ol>
	</div>
</c:if>
<script type="text/javascript">
$(document).ready(function(){
	var path = "${teamUrl}";
	$("ol li.pathAllFiles a").die().live('click',function(){
		location.href=path;
	});
	$("ol li.pathActive a.filePath").die().live('click',function(){
		var prev = $(this).parents(".pathActive:first").prevAll(".pathActive");
		var p = '';
		$.each(prev,function(index,item){
			var rid =$(item).children(".pathRid").val();
			p = "/"+rid+p;
		});
		p =p+ "/"+$(this).parent().find(".pathRid").val();
		var s = encodeURIComponent(p);
		location.href = path+"#path="+s;
	});
});
</script>