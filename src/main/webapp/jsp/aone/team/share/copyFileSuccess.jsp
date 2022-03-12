<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<script type="text/javascript">
$(document).ready(function(){
	
});
</script>
<div id="content-title">
	<h1>快速分享文件</h1>
</div>
<div class="content-through">
	<div class="msgBox light" style="width:60%;height:280px;font-size:14px;">
		<h2>文件转存成功！</h2>
		文件<a href="${fileURL}">${fileName}</a>已经转存到您的个人空间下的<a href="${tagURL}">文档</a>下。
		<div style="margin:10px 0 20px 0"><a href="${teamURL}">访问我的团队</a></div>
		
	</div>
</div>
