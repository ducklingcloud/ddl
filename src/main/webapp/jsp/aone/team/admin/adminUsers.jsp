<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<script type="text/javascript"  src="${contextPath}/scripts/jquery.tmpl.min.js"></script>


<script type="text/javascript">
$(document).ready(function(){
/* 	function dataTableStripe() {
		$('table.dataTable tr:nth-chfiild(even)').addClass('striped');
		$('table.dataTable tr:nth-child(odd)').removeClass('striped');
	}
	dataTableStripe();  */
	
	var url="<vwb:Link context='configTeam' jsp='${currTeam.name}' format='url'/>";
	var params = null;
	var memberCount = ${memberCount};
	
	/*-------------------移除当前团队成员-----------------------*/
	$("a[name='remove-member']").live("click",function(){
		var adminCount = $("select[name='auth'] option:selected[value='admin']").length;
		var uid = $(this).attr("uid");
		var currentValue = $("select[uid='"+uid+"'] option:selected").val();
		if(adminCount==0 || (currentValue=='admin' && adminCount==1)){
			alert("至少需要一个团队管理员");
			return false;
		}
		params = "func=removeMember&uid="+$(this).parent().parent().attr("uid");
		ajaxRequest(url,params,afterRemoveMember);
	});
	
	function afterRemoveMember(data){
		if(data.status){
			$(".member-row[uid='"+data.uid+"']").remove();
			afterUpdateAll();
		}else{
			alert("error");
		}
		memberCount--;
		$("#count-span").html(memberCount);
		//dataTableStripe();
	};
	/*---------------------------------------------------*/
	
	<%--
	/*-------------------保存全部团队成员权限-----------------------*/
	$("#update-all-button").live("click",function(){
		var adminCount = $("select[name='auth'] option:selected[value='admin']").length;
		if(adminCount>0){
			params = $("#member-authority-form").serialize();
			ajaxRequest(url,params,afterUpdateAll);
		}else{
			alert("至少需要一个团队管理员");
		}
	}); --%>
	
	$(".changeUserAuth").on("change",function(){
		var uid = $(this).attr("uid");
		var currentUser = '${currentUser}';
		if(uid==currentUser){
			alert("您不能修改自己的管理员权限！");
			$(this).val("admin");
			return;
		}
		if(uid){
			var value = $(this).val();
			if(haveAdmin()){
				var params = new Object();
				params.uid=uid;
				params.auth=value;
				params.ctoken = "${csrfToken}";
				params.func="updateOneAuthority";
				ajaxRequest(url,params,afterUpdateAll);				
			}else{
				alert("至少需要一个团队管理员");
				$(this).val("admin");
			}
		}
	});
	
	function haveAdmin(){
		var adminCount = $("select[name='auth'] option:selected[value='admin']").length;
		if(adminCount>0){
			return true;
		}else{
			return false;
		}
	}
	
	function afterUpdateAll(data){
		ui_spotLight('updateAll-spotLight', 'success', '当前设置已保存', 'fade');
	};
	/*---------------------------------------------------*/
	
	$('#showMsgAuthority').click(function(){
		$('#msgAuthority').toggle();
	});
	
});
</script>

<div class="config-float">
	<p>当前团队成员<span id="count-span">${memberCount}</span>人。</p>
	<form id="member-authority-form">
	<input type="hidden" name="func" value="updateAllAuthority"/>
	<c:if test="${empty existMembers}">
		<p class="NA">还未添加任何成员</p>
	</c:if>
	<table id="exist-member-table" class="dataTable" style="margin-top:1em; margin-bottom:2em;">
	<thead>
		<tr>
			<td class="dtName">姓名</td>
			<td class="dtMail">帐号</td>
			<td class="dtStd">权限
				<a id="showMsgAuthority" class="ui-iconButton help"></a>
				<div id="msgAuthority" class="msgFloat" style="display:none">可查看：可查看团队文档和信息
							<br/>可编辑：可查看、编辑和上传文档，但不可管理团队
							<br/>可管理：可查看、编辑和上传文档，可管理团队信息
				</div>	
			</td>
			<td></td>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${existMembers}" var="item" varStatus="status">
		<tr class="member-row" uid="${item.uid}">
			<td>${item.userName}</td>
			<td>${item.uid}
				<input type="hidden" name="uid" value="${item.uid}"/></td>
			<td><select name="auth" uid="${item.uid}" class="changeUserAuth">
					<option value="view" <c:if test="${item.auth eq 'view'}">selected</c:if>>可查看</option>
					<option value="edit" <c:if test="${item.auth eq 'edit'}">selected</c:if>>可编辑</option>
					<option value="admin" <c:if test="${item.auth eq 'admin'}">selected</c:if>>可管理</option>
				</select>
			</td>
			<td class="dtCenter"><a name="remove-member" uid="${item.uid}">移出团队<span class="lightDel"></span></a></td>
		</tr>
		</c:forEach>
	</tbody>
	</table>
	<div class="largeButtonHolder" style="margin-bottom:1.5em;position:fixed; top:60%;left:45%; height:3em; line-height:3em;">
		<%--
		<input type="button" id="update-all-button" value="保存更改1" />
		  --%>
		<span class="ui-spotLight" id="updateAll-spotLight" ></span>
	</div>
	</form>
	
</div>

<script id="exist-member-row-template" type="text/html">
<tr class="member-row new" uid="{{= uid}}">
	<td>{{= name}}</td>
	<td>{{= uid}}
		<input type="hidden" name="uid" value="{{= uid}}"/>
	</td>
	<td>
		<select name="auth" uid="{{= uid}}" value="{{= auth}}">
			<option value="view">可查看</option>
			<option value="edit">可编辑</option>
			<option value="admin">可管理</option>
		</select>
	</td>
	<td class="dtCenter"><a name="remove-member">移出团队<span class="lightDel"></span></a></td>
</tr>
</script>

