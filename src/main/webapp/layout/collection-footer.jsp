<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<ul id="subNav">
	<li><a id="logo" href="${contextPath}/index.jsp"><span class="ROL-small-black">&nbsp;</span></a></li>
	<li><a href="${contextPath }/help/introduction.jsp">什么是科研在线？</a></li>
	<li><a href="${contextPath}/help/tutorial.jsp">使用指南&nbsp;&bull;&nbsp;帮助</a></li>
	<li><a href="${contextPath}/help/history.jsp">更新记录</a></li>
	<li class="divider"><a href="<vwb:Link context='shareFile' format='url'/>">快速分享</a></li>
	<li><a href="${contextPath}/index.jsp#mobile">移动客户端</a></li>
</ul>
<p><jsp:include page="/Version.jsp" /></p>