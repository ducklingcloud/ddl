<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="net.duckling.ddl.constant.Constant"%>
<%
pageContext.setAttribute("contextPath", request.getContextPath());
String basePath = getServletContext().getRealPath("/");
Object scheme = request.getAttribute("requestScheme") == null ? request.getScheme() : request.getAttribute("requestScheme");
/* request.setAttribute("escienceDomain",scheme + "://www.escience.cn"); */
%>

<%-- Powered by
<a href="<%=Constant.DUCKLING_WEB%>"
	target="_blank"><%=Constant.DUCKLING_NAME%>&nbsp;<%=Constant.DUCKLING_VER%></a>&nbsp;(<%=Constant.getVersion(basePath)%>) (京ICP备09112257号-1) 

 --%>	<div style="display:none;">
	<!--script src="http://s15.cnzz.com/stat.php?id=3651388&web_id=3651388" language="JavaScript"></script-->
	<!-- script type="text/javascript" src="http://159.226.11.99/tongji/tr"></script-->
	</div>
	
<link href="${contextPath}/dface/css/dface.simple.footer.css" rel="stylesheet" type="text/css"/>
<script src="${contextPath}/dface/js/dface.simple.footer.js" type="text/javascript" ></script>
<script type="text/javascript">
$(document).ready(function(){
	$(".dface.footer p span#app-version").html("(<%=Constant.getVersion(basePath)%>)");
});
</script>
