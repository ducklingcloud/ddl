<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<fmt:setBundle basename="templates.default" />
<script type="text/javascript" src="${contextPath}/jsp/aone/js/uiLib-jQuery.js"></script>
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/uiLib.css" type="text/css" />


<!-- jquery.validate and jquery.pagination has conflict -->


<style>
#candidates { border-collapse:none; }
#candidates tr td:first-child { border-left:5px solid transparent; }
#candidates td.authOption { color:#999; }
#candidates tr.inner-chosen td { background:#f9f8ff; border-bottom:1px solid #69c; color:#000 !important; }
#candidates tr.inner-chosen td:first-child { border-left:5px solid #69c; }

</style>


<div id="content-title">
	<h1>创建团队</h1>
</div>
<div class="content-through">
	
	<h3>您创建的团队数已达到最大！如想再创建团队，请发邮件联系vlab@cnic.cn</h3>
</div>
<div class="clear"></div>
