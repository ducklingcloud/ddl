<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<link href="${contextPath}/jsp/aone/css/error.css" rel="stylesheet" type="text/css"/>
<link href="${contextPath}/scripts/bootstrap/css/bootstrap.min.css" rel="stylesheet" media="screen"/>
<link href="${contextPath}/scripts/bootstrap/css/bootstrap-responsive.min.css" rel="stylesheet"/>
<link href="${contextPath}/scripts/bootstrap/css/todc-bootstrap.css" rel="stylesheet"/>	
<fmt:setBundle basename="templates.default" />
<div class="ui-wrap">
<div>
	<ul>
		<li><a href="${contextPath }/system/mobileversion">手机客户端版本配置</a></li>
		<li><a href="${contextPath }/system/teamSpaceConfig">团队空间配置</a></li>
		<li><a href="${contextPath }/system/userConfig">用户团队个数配置</a></li>
	
	</ul>
</div>
</div>
