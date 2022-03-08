<%@ page language="java" pageEncoding="utf-8" %>
<%@ taglib uri="/WEB-INF/tld/vwb.tld" prefix="vwb" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%
request.setAttribute("contextPath", request.getContextPath());
request.setAttribute("siteURL", request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath());
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
	<tiles:insertAttribute name="commonheader"/>
	<link rel="stylesheet" href="${contextPath}/jsp/aone/css/aone.css?v=${aoneVersion}" type="text/css" />
	<link rel="stylesheet" href="${contextPath}/jsp/aone/css/base.css?v=${aoneVersion}" type="text/css" />
	<link rel="stylesheet" href="${contextPath}/jsp/aone/css/uiLib.css?v=${aoneVersion}" type="text/css" />
	<script type="text/javascript" src="${contextPath}/jsp/aone/js/uiLib-0.9.2-jquery.js"></script>
	<script type="text/javascript" src="${contextPath}/jsp/aone/js/search-jQuery.js?v=${aoneVersion}"></script>
	
	<fmt:setBundle basename="templates.default" />
	<title>
	    <fmt:message key="view.title.view">
		<fmt:param>
		    <vwb:viewportTitle />
		</fmt:param>
		<fmt:param>
		    <vwb:applicationName />
		</fmt:param>
	    </fmt:message>
	</title>
    </head>
    <body style="background:none;">
	<div class="ui-wrap container">
	    <div id="content" class="std portalContent" style="border:none; box-shadow:none;">
		<tiles:insertAttribute name="content"/>
	    </div>
	    <div class="clear"></div>
	</div>
    </body>
</html>
