                    <%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/uiLib.css" type="text/css" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/aone.css" type="text/css" />
<script type="text/javascript" src="${contextPath}/jsp/aone/js/uiLib-jQuery.js"></script>
<script type="text/javascript" src="${contextPath}/jsp/aone/js/global.js"></script>
<style>
	.ui-wrap h3.unlink {margin:5% 10% 2%; padding:0; }
	.ui-wrap p.hint {margin:2% 10%; color:#666;font-size:1.1em;}
	.ui-wrap p.others {margin:2% 10% 10%;font-size:1.1em;}
	.ui-wrap p.others a.btn {color:#fff; text-decoration:none; margin-left:1em;}
</style>
<div class="ui-wrap">
	<h3 class="unlink">对不起，该邀请已经失效 </h3>
	<p class="hint">可能原因：邀请链接已过期或者邀请人取消了邀请</p>
	<p class="others">别灰心，您可以访问已加入的团队~<a class="btn btn-success" href="<vwb:Link context='switchTeam' format='url'/>">访问我的团队</a></p>
</div>

