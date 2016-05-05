<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<script type="text/javascript">
$(document).ready(function(){
	
});
</script>
<div id="content-title">
	<h1>快速分享文件</h1>
</div>
<div class="content-through">
<div class="msgBox light" style="width:60%;height:250px; padding:50px 20px; margin:20px auto; border:none; font-size:14px;">
	<h3>文件已经成功的分享，谢谢您的使用！</h3>
	<p>已经向您的好友发送电子邮件。他们通过邮件中的链接就可以下载文件。</p>
	<p>刚分享的文件已经保存到您的<a href="${collectionURL}">个人空间</a>中。</p>
	<p>&nbsp;</p>

<c:choose>
	<c:when test="${isFirst}">
		<div class="procedureHolder largeButtonHolder holderCenter">
			<h3>科研在线已经为您创建了帐号，并为您创建了属于自己的个人空间。</h3>
			<p>您可以通过邮箱激活帐号，并开始使用科研在线的完整功能。</p>
			<p>
				<a class="largeButton" href="<vwb:Link context='switchTeam' format='url'/>">查看我的团队</a>
				<a class="largeButton dim" href="http://support.ddl.escience.cn/">了解科研在线</a>
			</p>
		</div>
	</c:when>
</c:choose>
<p class="ui-text-note" style="font-size:1em">
	文件列表<br/>
	<c:forEach items="${fileNames}" varStatus="status">
		<a href="${fileURLs[status.index]}">${fileNames[status.index]}</a><br/>
	</c:forEach>
</p>
</div>
</div>