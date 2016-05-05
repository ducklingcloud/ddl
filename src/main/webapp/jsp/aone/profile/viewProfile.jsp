<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<script type="text/javascript">
	
	

	
</script>

<div id="content-title">
	<c:if test="${not empty isMyself}">
		<div id="editTool">
			<a title="编辑工具" class="editTool"><span></span></a>
			<ul id="toolGroup">
				<li><a href="<vwb:Link context='dashboard' format='url'/>?func=profile#update"><span>编辑资料</span></a></li>
				<li><a href="${changePasswordURL}">修改密码</a></li>
			</ul>
		<div class="decoLeft"></div>
		</div>
	</c:if>
		
	<h1>个人资料：${user.name}</h1>
</div>

<div id="userInfo" class="content-major">
	<input type="hidden" name="uid" value="${user.uid}"/>
	<table id="profileTable" class="ui-table-form">
		<tr class="titleRow">
			<th colspan="4" width="520">基本信息</th>
		</tr>
		<tr>
			<th width="60">单位：</th>
			<td width="200">${user.orgnization}</td>
			<th width="60">部门：</th>
			<td>${user.department}</td>
		</tr>
		<tr>
			<th>地址：</th>
			<td colspan="3">${user.address}</td>
		</tr>
		<tr class="titleRow">
			<th colspan="4">联系方式</th>
		</tr>
		<tr><th>邮箱：</th>
			<td colspan="3">${user.email}</td>
		</tr>
		<tr>
			<th>电话：</th>
			<td>${user.telephone}</td>
			<th>手机：</th>
			<td>${user.mobile}</td>
		</tr>
		<tr>
			<th>QQ：</th>
			<td>${user.qq}</td>
		</tr>
		<tr>
			<th>微博：</th>
			<td>${user.weibo}</td>
			<th></th>
			<td></td>
		</tr>
	</table>
</div>

<div id="content-side">
	<div class="sideBlock">
		
		<h4>团队</h4>
		<c:if test="${not empty isMyself}">
			<ul class="fileList">
				<li><a href="<vwb:Link format='url' context='switchTeam'/>">我的团队</a></li>
			</ul>
			<p style="text-align:center"><a class="largeButton" href="<vwb:Link format='url' context='createTeam'/>">创建团队</a></p>
		</c:if>
		<c:if test="${empty isMyself}">

			<p class="NA">暂时不能查看${user.name}参加的团队</p>
		</c:if>
	</div>
</div>	

<div class="ui-clear"></div>


