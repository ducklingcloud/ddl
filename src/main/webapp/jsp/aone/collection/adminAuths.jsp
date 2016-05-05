<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />

<script type="text/javascript">
$(document).ready(function() {
	function dataTableStripe() {
		$('table.dataTable tr:nth-child(even)').addClass('striped');
		$('table.dataTable tr:nth-child(odd)').removeClass('striped');
	}
		
	var cid = ${collection.resourceId};
	var url="<vwb:Link context='configCollection' format='url'/>";
	var params = null;	
	var addAuthority = $('input[name="addAuthority"]');
	var selectAuthority = $('select[name="defaultAuth"]');
	/*-------------------开始添加动作-----------------------*/
	function loadCandidates(){
		params = "func=loadTeamMembers&cid="+cid;
		ajaxRequest(url,params,afterLoadSearchResult);
	}
	
	function afterLoadSearchResult(data){
		$("#candidates-table tbody").children().remove();
		if(data.length!=0){
			addAuthority.removeAttr("disabled");
			selectAuthority.removeAttr('disabled');
			$("#candidate-row-template").tmpl(data).appendTo("#candidates-table tbody");
		}else{
			addAuthority.attr("disabled","true");
			selectAuthority.attr('disabled', 'true');
			$("#candidates-table tbody").append('<tr id="noCandidate-row"><td colspan="3"><p class="NA">没有候选用户</p></td></tr>');
		}
		dataTableStripe();
	};
	/*---------------------------------------------------*/
	
	/*-------------------将候选列表中成员加入团队-----------------------*/
	addAuthority.click(function(){
		params = $("#candidates-form").serialize();
		ajaxRequest(url,params,afterAddAuthority);
	});
	
	function afterAddAuthority(data){
		$("#noMemberRow").remove();
		$("#exist-member-row-template").tmpl(data).appendTo("#exist-member-table tbody");
		for(var i=0;i<data.length;i++){
			$("select[uid='"+data[i].uid+"']").val(data[i].auth);
		}
		loadCandidates();
		dataTableStripe();
	};
	/*---------------------------------------------------*/
	
	/*-------------------移除候选列表-----------------------*/
	$("input[name='remove-candidate-button']").live("click",function(){
		$(this).parent().parent().remove();
		if($(".candidate-row").length==0)
			$("#add-members-button").attr("disabled","true");
	});
	/*---------------------------------------------------*/
	
	/*-------------------移除当前团队成员-----------------------*/
	
	$("select[name='auth']").live("change",function(){
		$("a[uid='"+$(this).attr("uid")+"']").attr("auth",$(this).attr("value"));
	});
	
	$("a[name='remove-member-button']").live("click",function(){
		var adminCount = $("select[name='auth'] option:selected[value='admin']").length;
		var currentValue = $(this).attr("auth");
		if((adminCount==0)||(currentValue=='admin'&&adminCount==1)){
			alert("至少需要一个集合管理员");
			return false;
		}
		params = "func=removeAuthority&cid="+cid+"&uid="+$(this).attr("uid");
		ajaxRequest(url,params,afterRemoveMember);	
	});
	
	function afterRemoveMember(data){
		if(data.status){
			$(".member-row[uid='"+data.uid+"']").remove();
			loadCandidates();
			dataTableStripe();
		}else{
			ui_spotLight('updateAll-spotLight', 'fail', '保存失败');
		}
	};
	/*---------------------------------------------------*/
	
	/*-------------------保存全部团队成员权限-----------------------*/
	$("#update-all-button").live("click",function(){
		var adminCount = $("select[name='auth'] option:selected[value='admin']").length;
		if(adminCount>0){
			params = $("#member-authority-form").serialize();
			ajaxRequest(url,params,afterUpdateAll);
		}else{
			alert("至少需要一个集合管理员");
		}
	});
	
	function afterUpdateAll(data){
		ui_spotLight('updateAll-spotLight', 'success', '更改已保存', 'fade');
	}
	/*---------------------------------------------------*/	

	$('a#authType').click(function(){
		$('#msg-authType').toggle();
	});
	
	$('input[name="candidateAll"]').change(function(){
		$('#candidates-table input[name="userInfo"]').attr('checked', $(this).attr("checked"));
		//if ($(this).attr('checked')=='checked' || $(this).attr('checked')==true) {
		//	$('#candidates-table input[name="userInfo"]').attr('checked', 'checked');
		//}
	});
	$('#candidates-table input[name="userInfo"]').change(function(){
		$('input[name="candidateAll"]').attr('checked', '');
	});
	
	var searchCdd = new SearchBox('searchCandidate', '搜索姓名或邮箱（账号）', false, true, true);
	searchCdd.doSearch = function(QUERY) {
		if ($('#noCandidate-row').length<1) {
			$('#candidates-table tbody tr').each(function(){
				searchCdd.findMatches(QUERY, $(this), 'td');
			});
		}
	}
	searchCdd.isMatch = function(OBJ) { OBJ.show(); }
	searchCdd.notMatch = function(OBJ) { OBJ.hide(); }
	searchCdd.resetSearch = function() {
		$('#candidates-table tbody tr').show();
	}
	
	loadCandidates();
	dataTableStripe();
});
</script>
<form id="member-authority-form">
<input type="hidden" name="func" value="updateAuthority"/>
<input type="hidden" name="cid" value="${collection.resourceId}" />

<p class="ui-text-note">集合默认权限是集合的基本权限，对未特殊指定的所有用户生效。
	<a id="authType" class="ui-iconButton help">权限类型</a>
</p>
	<div id="msg-authType" class="msgFloat" style="left:40%">
		<p><strong>管理：</strong>配置集合、管理页面和快捷方式，设置用户权限等。同时拥有编辑权限。
		<br/><strong>编辑：</strong>可对页面内容进行编辑。同时拥有查看权限。
		<br/><strong>查看：</strong>可以查看集合内容和阅读页面。
		<br/><strong>私有：</strong>除设定的成员外不可查看集合内容或阅读页面。
		</p>
	</div>
<p class="ui-text-note">当为某些用户设置特定的权限后，将按照其特定权限执行。</p>

<h3>集合默认权限</h3>
<p style="padding-left:2em; margin-top:1em;">
	<label><input type="radio" name="defaultAuth" value="public" <c:if test='${collection.defaultAuth eq "public"}'>checked</c:if>/>公开</label>
	<label><input type="radio" name="defaultAuth" value="private" <c:if test='${collection.defaultAuth eq "private"}'>checked</c:if>/>私有</label>
</p>

<h3>集合个人权限</h3>
<table id="exist-member-table" class="dataTable" style="margin-top:1em; margin-bottom:2em;">
	<thead>
		<tr><td class="dtName">姓名</td>
			<td class="dtMail">邮箱（账号）</td>
			<td class="dtStd">权限</td>
			<td></td>
		</tr>
	</thead>
	<tbody>
	<c:if test="${empty existAuthority}">
		<tr id="noMemberRow">
			<td colspan="4"><p class="NA">没有设置任何权限</p></td>
		</tr>
	</c:if>
	<c:forEach items="${existAuthority}" var="item" varStatus="status">
	<tr class="member-row" uid="${item.uid}">
		<td>${item.userName}</td>
		<td><input type="hidden" name="uid" value="${item.uid}"/>${item.uid}</td>
		<td>
			<select name="auth" uid="${item.uid}" >
				<option value="view" <c:if test="${item.auth eq 'view'}">selected</c:if>>可查看</option>
				<option value="edit" <c:if test="${item.auth eq 'edit'}">selected</c:if>>可编辑</option>
				<option value="admin" <c:if test="${item.auth eq 'admin'}">selected</c:if>>可管理</option>
			</select>
		</td>
		<td class="dtCenter">
			<a name="remove-member-button" uid="${item.uid}" auth="${item.auth}">删除权限<span class="lightDel"></span></a>
		</td>
	</tr>
	</c:forEach>
	</tbody>
</table>

<p style="margin:0.5em 0;">	
	<input type="button" id="update-all-button" class="largeButton" value="保存更改" />
	<span id="updateAll-spotLight" class="ui-spotLight"></span>
</p>

</form>

<hr/>

<h3>候选用户</h3>
<div id="searchCandidate" class="ui-RTCorner"></div>
<form id="candidates-form">
	<input type="hidden" name="func" value="addAuthority" />
	<input type="hidden" name="cid" value="${collection.resourceId}" />
	<div style="float:left">
		将选中候选用户的权限设置为：
		<select name="defaultAuth" >
			<option value="view">可查看</option>
			<option value="edit">可编辑</option>
			<option value="admin">可管理</option>
		</select>
		<input type="button" name="addAuthority" value="授予权限" class="largeButton newUser" />
	</div>
	<div class="ui-clear"></div>
	<table class="dataTable" id="candidates-table" style="margin-top:1em; margin-bottom:2em;">
		<thead>
			<tr><td width="85">
					<label><input name="candidateAll" type="checkbox" />全选</label>
				</td>
				<td class="dtName">姓名</td>
				<td>邮箱（账号）</td>
				<td>团队权限</td>
			</tr>
		</thead>
		<tbody>
		<tr class="candidate-row">
			<td class="dtCenter"><input type="checkbox" name="userInfo" value="{{= uid}};{{= name}}" id="check-{{= uid}}"/></td>
			<td><label for="check-{{= uid}}">{{= name}}</label></td>
			<td>{{= uid}}</td>
		</tr>
		</tbody>
	</table>
</form>


<script id="candidate-row-template" type="text/html">
<tr class="candidate-row">
	<td class="dtCenter"><input type="checkbox" name="userInfo" value="{{= uid}};{{= name}}" id="check-{{= uid}}"/></td>
	<td><label for="check-{{= uid}}">{{= name}}</label></td>
	<td>{{= uid}}</td>
	<td>{{= team_auth}}</td>
</tr>
</script>

<script id="exist-member-row-template" type="text/html">
<tr class="member-row" uid="{{= uid}}">
	<td>{{= name}}</td>
	<td><input type="hidden" name="uid" value="{{= uid}}"/>{{= uid}}</td>
	<td>
		<select name="auth" uid="{{= uid}}" value="{{= auth}}">
			<option value="view">可查看</option>
			<option value="edit">可编辑</option>
			<option value="admin">可管理</option>
		</select>
	</td>
	<td class="dtCenter">
		<a name="remove-member-button" uid="{{= uid}}" auth="{{}= auth}">删除权限<span class="lightDel"></span></a>
	</td>
</tr>
</script>

