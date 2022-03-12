<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />

<script type="text/javascript">
$(document).ready(function(){
     
});
</script>

<div id="content-title">
	<h2><a href="<vwb:Link context='viewCollection' page='${collection.resourceId}' format='url'/>">${collection.title}</a></h2>
	<h1><span>图片：</span>${lastVersion.title}</h1>
</div>
<div id="content-major">
	<div id="download" class="ui-RTCorner">
		<a id="open-upload-form-button">更新</a>
		<a href="${downloadURL}" class="ui-iconButton download">下载</a>
	</div>
	<div id="version">
		${lastVersion.changeBy} 上传于 ${lastVersion.changeTime} |
		<a href="${lastVersion.version}">版本：${lastVersion.version}</a>
	</div>
	<div class="clear"></div>
	<div id="photoInfo">
		<div class="photoContainer">
			<img src="${downloadURL}"/>
		</div>
	</div>
</div>

<div id="content-side" class="light">
	<div id="info" class="sideBlock">
		<h4>图片信息</h4>
		<table class="ui-table-form-2col">
		<tr><th>图片类型：</th>
			<td></td>
		</tr>
<!--		<tr><th>原图尺寸：</th>-->
<!--			<td>?? &times; ?? 像素</td>-->
<!--		</tr>	-->
		<tr>
			<th>文件大小：</th>
			<td>${lastVersion.size/1000} KB</td>
		</tr>
<!--		<tr><th></th>-->
<!--			<td><a id="EXIF">EXIF信息</a></td>-->
<!--		</tr>-->
		</table>
	</div>
	<div id="linkIn" class="sideBlock">
		<h4>引用该图片的页面</h4>
<!--		<p class="NA">暂无引用该图片的页面</p>-->
		<ul id="abc" class="fileList">
			<c:forEach items="${refView}" var="item">
				<li>
					<a href="<vwb:Link format='url' context='view' page='${item.dfileRef.pid}'/>">${item.pageName}</a>
				</li>
			</c:forEach>
		</ul>
	</div>
</div>
<div class="ui-clear"></div>
