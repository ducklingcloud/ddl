<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<link href="${contextPath}/jsp/aone/css/error.css" rel="stylesheet" type="text/css"/>
<fmt:setBundle basename="templates.default" />
<div class="ui-wrap">
	<div class="error-center">
		<h3>新建协作文档失败，您的空间已满。如需扩容，请联系管理员vlab@cnic.cn！</h3>
				<hr/>
				<a href="<vwb:Link context='teamHome' format='url'/>">跳转到团队首页</a>
	</div>
</div>

