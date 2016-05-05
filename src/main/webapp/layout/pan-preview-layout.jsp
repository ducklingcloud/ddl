<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%
	request.setAttribute("contextPath", request.getContextPath());
	request.setAttribute("siteURL", request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath());
%>
<%-- 是否是可以预览的文件 --%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<tiles:insertAttribute name="commonheader"/>
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/aone.css?v=6.0.0" type="text/css" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/base.css?v=6.0.0" type="text/css" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/uiLib.css?v=6.0.0" type="text/css" />
<link href="${contextPath}/scripts/bootstrap/css/bootstrap.min.css" rel="stylesheet" media="screen"/>
<link href="${contextPath}/scripts/bootstrap/css/bootstrap-responsive.min.css" rel="stylesheet"/>
<link href="${contextPath}/scripts/bootstrap/css/todc-bootstrap.css" rel="stylesheet"/>
<link href="${contextPath}/scripts/syntaxhighlighter/styles/shCore.css" rel="stylesheet" type="text/css"/>
<link id="coreCss" href="${contextPath}/scripts/syntaxhighlighter/styles/shCoreEclipse.css" rel="stylesheet" type="text/css"/>
<link id="themeCss" href="${contextPath}/scripts/syntaxhighlighter/styles/shThemeEclipse.css" rel="stylesheet" type="text/css"/>
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/lynx.css?v=${aoneVersion}" type="text/css" />

<style>
<c:if test="${isPreview }">
	body {overflow-y: hidden;}
	#content{border:0;padding:0;}
</c:if>
<c:if test="${pdfstatus == 'original_pdf'}">
	#viewerWrapper{margin-top:30px;}
</c:if>
div.ui-wrap {width: 100%;}
#content, #footer {margin:0;width:99.7%;}
#fileInfo {margin: 0; padding-top:6px;}
#fileInfo table.fileContainer{width:30%; margin:8em auto}
#fileInfo .fileContainer td{margin:0;padding:0;}
.ui-wrap div.bedrock {height: 1px;}
#footer{padding:0px;}
#content.portalContent {margin-top:0;}
div.ui-wrap a {color:#000;}
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
#macroNav #userBox li a.btn-warning.btn-mini {padding:1px 0.5em;color:#fff;font-size:10pt; box-shadow:none; font-weight:normal;background:#FB7433; line-height:20px; margin-top:9px; border-radius:1px;border:none;text-shadow:none}
#macroNav #userBox li a.btn-warning.btn-mini:hover {border-top:none;background-color:#FB8333;}



</style>
<script type="text/javascript" src="${contextPath}/jsp/aone/js/uiLib-0.9.2-jquery.js"></script>
<script type="text/javascript" src="${contextPath}/jsp/aone/js/search-jQuery.js?v=6.0.0"></script>

<fmt:setBundle basename="templates.default" />
<title>
预览 - ${filename }
</title>
</head>
	<body>
		<div id="macroNav" class="ui-wrap wrapperFull">
	
			<a title="科研在线-团队文档库" id="logo" href="${siteURL}"></a>
			<div class="container">
				<span class="fileInfo">
					<span class="headImgContainer">
						<span class="${resource.itemType} headImg  ${resource.fileType}"></span>
					</span>
					<c:if test="${fileNotExist !='true' }">
						<span class="title" title="${filename }">${filename }</span>
						<span class="size">(${sizeShort})</span>
					</c:if>
					<c:if test="${itemType == 'DFile'}">
						<a class="downSave" href="${contextPath}/pan/download?path=${remotePath}">下载</a>
					</c:if>
				</span>
				<ul id="userBox">
					<vwb:UserCheck status="authenticated">
						<c:if test="${param.from !='web' }">
					 	<li>
							<a class="btn btn-warning btn-mini" target="_blank" href="<vwb:Link format='url' context='switchTeam' page=''/>?func=person">访问文档库</a>
						</li>
						</c:if>
						<li>
							<a style="margin-top:2px" target="_blank" href="http://iask.cstnet.cn/?/home/explore/category-11">意见反馈</a>
						</li>
					 	<li class="userMe">
					 		<a style="margin-top:2px"><vwb:UserName /></a>
						</li>
					</vwb:UserCheck>
					<vwb:UserCheck status="notauthenticated">
						<li><a href="<vwb:Link context='switchTeam' absolute='true' format='url'/>">登录</a></li>
						<li><a href="<vwb:Link context='regist' absolute='true' format='url'/>">注册</a></li>
					</vwb:UserCheck>
				</ul>
				
				<vwb:UserCheck status="authenticated">
					<div id="msgMenu" class="pulldownMenu" style="width:90px; position:static;float:right; margin:38px -215px -1px -16px">
						<ul>
							<li><a href="http://support.ddl.escience.cn/" target="_blank">帮助</a></li>
							<li>
								<a href="<vwb:Link context="logout" format='url'/>"
									class="action logout"
									title="<fmt:message key='actions.logout.title'/>"><fmt:message
										key="actions.logout" /></a>
							</li>
					 	</ul>
					</div>
				</vwb:UserCheck>
			</div>
		</div>
		<div id="content" class="std portalContent ui-wrap" style="background:transparent;border:none; box-shadow:none">
			<vwb:render content="${content}"/>
		</div>
	</body>
	
	<script type="text/javascript">

		var backBox = new BackToTop('回顶部');
		$('li.userMe>a').pulldownMenu({ 'menu': $('#msgMenu') });
		
	</script>
	
</html>