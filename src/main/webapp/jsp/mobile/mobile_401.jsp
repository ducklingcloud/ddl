<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="CoreResources" />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
<title>${title}</title>
<style type="text/css">
	.mobile .ui-clear {
		clear:both;
	}
</style>
</head>
<body class="mobile">
	<div id="content-title">
		<h1><fmt:message key="security.error.token.timeout.title"/></h1>
	</div>
	<div id="content-major">
		<div id="DCT_viewcontent">
			<fmt:message key="security.error.token.timeout"/>
			<div class="ui-clear"></div>
		</div>
	</div>
</body>
</html>