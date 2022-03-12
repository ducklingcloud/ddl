<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%
	String path = request.getContextPath();

	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
			
%>
<c:set var='pageurl'><vwb:Link jsp='managePages' context='team' format='url'/></c:set>
<div style="height: 458px;">
	<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
		codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0"
		width="100%" height="100%">
		<param name="movie" value="<%=basePath%>jsp/pagemanage.swf">
		<param name="quality" value="high">
		<param name="flashVars"
			value="localeChain=<%=request.getLocale()%>&contextpath=<%=basePath%>&actionUrl=${pageurl}" />
		<embed src="<%=path%>/jsp/pagemanage.swf" quality="high"
			pluginspage="http://www.macromedia.com/go/getflashplayer"
			type="application/x-shockwave-flash"
			flashVars="localeChain=<%=request.getLocale()%>&contextpath=<%=basePath%>&actionUrl=${pageurl}"
			width="100%" height="100%">
		</embed>
	</object>
</div>
