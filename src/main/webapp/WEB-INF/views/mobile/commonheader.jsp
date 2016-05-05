<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="net.duckling.ddl.constant.Constant"%>
<meta http-equiv="Content-Type" content="text/html; charset=<vwb:ContentEncoding />" />
<%
pageContext.setAttribute("contextPath", request.getContextPath());
String basePath = getServletContext().getRealPath("/");
String aoneVersion = Constant.getVersion(basePath);
request.setAttribute("aoneVersion", aoneVersion);
%>
<fmt:setBundle basename="templates.default" />

<%-- CSS stylesheet --%>
<link rel="stylesheet" media="screen, projection, print" type="text/css" href="${contextPath}/jsp/aone/css/css.css?v=<%=aoneVersion%>"/>
<link rel="stylesheet" type="text/css" media="print" href="${contextPath}/jsp/aone/css/css.css?v=<%=aoneVersion%>" /> 
<link href="${contextPath}/images/favicon.ico" rel="shortcut icon" type="image/x-icon" />
<vwb:IncludeResources type="stylesheet"/>
<vwb:IncludeResources type="inlinecss" />
<script type="text/javascript">
	/* Localized javascript strings: LocalizedStrings[] */
	<vwb:IncludeResources type="jslocalizedstrings"/>
	<vwb:IncludeResources type="jsfunction"/>
</script>

<%-- JAVASCRIPT --%>
<script type="text/javascript"  src="${contextPath}/scripts/jquery/jquery-1.8.3.min.js"></script>
<script type="text/javascript"  src="${contextPath}/scripts/jquery.tmpl.min.js"></script>
<script type="text/javascript"  src="${contextPath}/scripts/jquery/jquery.pagination.js"></script>
<script type="text/javascript" src="${contextPath}/scripts/site.js?v=${aoneVersion}"></script>
<script type="text/javascript" src="${contextPath}/scripts/DucklingCommon.js?v=${aoneVersion}"></script>
<script type="text/javascript">
	site.init(<vwb:Patterns/>);
</script>
<c:if test="${teamFounded !=null}">
	<script type="text/javascript">
		var Wiki = {
			'BaseUrl': '<vwb:BaseURL />',
			'PageUrl': '<vwb:Link page="%23%24%25" format="url"/>',
			'TemplateDir': '${contextPath}/',
			'PageName': '<vwb:Variable key="pagename" />',
			'UserName': '<vwb:UserName />', 
			'JsonUrl' : '<vwb:Link jsp="JSON-RPC" format="url" context="plain"/>'
			}
	</script>
	<%-- SKINS : extra stylesheets, extra javascript --%>
</c:if>
<vwb:Variable key="duckling.robots" var="robots"/>
<c:if test="${'true' eq robots}"><META NAME="ROBOTS" CONTENT="all"></c:if>
<c:if test="${'true' ne robots}"><META NAME="ROBOTS" CONTENT="none"></c:if>

<vwb:Variable key="duckling.keyword" var="keywords"/>
<c:if test="${keywords!=null}"><META NAME="KEYWORDS" CONTENT="${keywords}"></c:if>

<vwb:Variable key="duckling.descriptions" var="descriptions"/>
<c:if test="${descriptions!=null}"><META NAME="DESCRIPTIONS" CONTENT="${descriptions}>"></c:if>

<vwb:Variable key="frontPage" var="frontpage"/>
<c:if test="${frontpage!=null}"><link rel="start"  href="${frontpage}"  title="Front page" /></c:if>

<link rel="shortcut icon" type="image/x-icon" href="${contextPath}/images/favicon.ico" />