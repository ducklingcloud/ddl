<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%
	request.setAttribute("contextPath", request.getContextPath());
	request.setAttribute("siteURL", request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath());
%>
<%-- 是否是可以预览的文件 --%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<tiles:insertAttribute name="commonheader"/>
<meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/base.css?v=6.0.0" type="text/css" />
<link href="${contextPath}/scripts/bootstrap/css/bootstrap.min.css" rel="stylesheet" media="screen"/>
<link href="${contextPath}/scripts/bootstrap/css/todc-bootstrap.css" rel="stylesheet"/>
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/lynx.css?v=${aoneVersion}" type="text/css" />

<style>
body {background:#fff}
div.ui-wrap {width: 100%;}
#content, #footer {margin:0;width:99.9%;}
#fileInfo {margin: 0; margin-top:0px;}
#fileInfo table.fileContainer{width:30%; margin:8em auto}
#fileInfo .fileContainer td{margin:0;padding:0;}
.ui-wrap div.bedrock {height: 1px;}
#footer{padding:0px;}
#content.portalContent {margin-top:0;}
div.ui-wrap a:hover {text-decoration: none;}
div.ui-wrap a.downSave {color:#000; background:#eee; border-radius:1px; margin-right:7px; padding:1px 6px; display:inline-block;border:none;}
div.ui-wrap a.downSave:hover {background:#f5f5f5;}
span.fileInfo {color:#bbb; display:inline-block; margin-left:2em; margin-top:8px;}
span.fileInfo span {display:inline-block;}
span.fileInfo span.title {font-weight:bold;color:#fff; font-size:16px;height:1em; height:1.15em\0; line-height:1.15em; max-width:600px;overflow-y:hidden;}
span.fileInfo span.size {display:inline-block; margin:0 7px 0 7px;color:#bbb}
span.headImgContainer { margin-bottom:-3px;display:inline-block;}
span.headImgContainer span.headImg {display:block;float:left;}
#photoInfo {margin-top: 50px;}
#codeMode {margin-top: 50px;width: 1150px;margin-left: auto;margin-right: auto;background-color: white;}
</style>

<fmt:setBundle basename="templates.default" />
<title>
<spring:message code="ddl.sharing" /> - ${resource.title == null ? "<spring:message code='ddl.sharing.extract' />" : resource.title}
</title>
</head>
	<body>
		<div id="content" class="std portalContent ui-wrap" style="background:transparent;border:none; box-shadow:none">
			<vwb:render content="${content}"/>
		</div>
	</body>
	
</html>