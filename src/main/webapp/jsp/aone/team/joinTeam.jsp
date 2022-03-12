<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/uiLib.css"
	type="text/css" />
<script type="text/javascript"
	src="${contextPath}/jsp/aone/js/uiLib-jQuery.js"></script>

<style>
#candidates { border-collapse:none; }
#candidates tr td:first-child { border-left:5px solid transparent; }
#candidates td.authOption { color:#999; }
#candidates tr.inner-chosen td { background:#f9f8ff; border-bottom:1px solid #69c; color:#000 !important; }
#candidates tr.inner-chosen td:first-child { border-left:5px solid #69c; }
</style>

<script type="text/javascript">
$(document).ready(function(){
	
});
</script>

<div>
	<h2>创建团队</h2>
	恭喜你，你可以创建一个新团队了
	<form id="createTeamForm" action="<vwb:Link context='createTeam' format='url'/>">
		<input type="hidden" value="create" name="func"/>
		<label>团队名称</label>
		<input type="text" name="teamName" value=""/><br/>
		<label>团队网址</label>
		<input type="text" name="teamId" value=""/>
		<label>（唯一标示）</label>
		<label>用于URL，便于访问，如：http://cerc.aone.com</label><br/>
		<label>团队简介</label>
		<textarea name="teamDescription"></textarea><br/>
		<input type="submit" value="提交"/>
		<a href="#">取消</a>
	</form>
</div>
<div class="clear"></div>
