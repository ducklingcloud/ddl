<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<script type="text/javascript" src="${contextPath}/jsp/aone/js/fileuploader.js?v=${aoneVersion}"></script>
<link href="${contextPath}/jsp/aone/css/fileuploader.css" rel="stylesheet" type="text/css">	
<script type="text/javascript">
$(document).ready(function(){
     
});
</script>
<div id="content-title">
	<h1>文件分享</h1>
</div>
<div class="content-through">
	<div class="msgBox light" style="width:50%">

		<h3>${fileVersion.title}</h3>
		<p class="ui-text-note">分享者：${fileOwnerName}</p>
		<p class="ui-text-note">分享时间：${shareCreateTime}</p>
		<c:choose>
			<c:when test="${notExist }">
				<p><font color="red">该文件已不存在！</font></p><br/>
			</c:when>
			<c:otherwise>
				<p class="ui-text-note">文件大小：${fileSize}</p>
				<p><font color="red">该文件已过期！</font></p>
				<br/>
			</c:otherwise>		
		</c:choose>
		
	</div>
	<div class="procedureHolder largeButtonHolder holderCenter">
		<p>
			<c:if test="${not isLogin }">
				<a class="largeButton" href="https://ddl.escience.cn/system/regist">注册</a>
			</c:if>
			<a class="largeButton dim" href="http://support.ddl.escience.cn/">了解科研在线</a>
		</p>
		<h3>加入科研在线，享受更多便捷服务！</h3>
	</div>
</div>
