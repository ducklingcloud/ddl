<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />


<script type="text/javascript">
$(document).ready(function(){
	function dataTableStripe() {
		$('table.dataTable tr:nth-child(even)').addClass('striped');
		$('table.dataTable tr:nth-child(odd)').removeClass('striped');
	}
	dataTableStripe(); 
	
	function showMessage($parentDiv){
		ui_spotLight($parentDiv.find("span.ui_spotLight"), 'success', '当前设置已保存', 'fade');
	};
	
	$('a.showMsgAuthority').click(function(){
		$(this).parent().find('div.msgAuthority').toggle();
	});
	
	$("ul.nav>li").click(function(){
		$(this).siblings("li").removeClass("current");
		$(this).addClass("current");
		var type = $(this).attr("data-type");
		$("#waiting").hide();
		$("#reject").hide();
		$("#"+type).show();
	});
	
	function myAjaxRequest(url, params, success, fail){
		$.ajax({
			url: url,
			data : params,
			type: 'POST',
			success : success,
			error : fail,
			statusCode:{
				450:function(){alert('会话已过期,请重新登录');},
				403:function(){alert('您没有权限进行该操作');}
			}
		});
	}
	
	/**---------------用户申请审核------------------ **/
	var url="<vwb:Link context='configTeam' jsp='${currTeam.name}' format='url'/>&func=auditApplicant";
	var params = null;
	
	function afterSubmit(data){
		data = JSON.parse(data);
		if(typeof(data.status) != 'undefined'){
			var uids = data.uids.split(",");
			var $curDiv = getCurrentDiv();
			showMessage($curDiv);
			prependToTargetTable(uids, $curDiv, data.status);
			refreshCurrentTable(uids, $curDiv);
		}else{
			alert("审核失败！");
		}
	}
	
	function prependToTargetTable(uids, $curDiv, status){
		$.each(uids, function(index, element){
			if(status == 'reject'){
				var $targetTbody = $("#"+status).find("tbody");
				$curDiv.find("tr[uid='"+element+"']").remove().prependTo($targetTbody);
				var $targetTr = $targetTbody.children("tr");
				if($targetTr.length>0){
					$targetTr.find("input[name=single]").removeAttr("checked");
					var $parentDiv = $targetTbody.closest("div");
					$parentDiv.show();
					$parentDiv.prev("p.NA").hide();
					$parentDiv.find("span.count-span").html($targetTr.length);
					appendAuditLink($targetTr);
				}
			}else{
				$curDiv.find("tr[uid='"+element+"']").remove();
			}
			
		});
	}
	
	function appendAuditLink($targetTr){
		$targetTr.each(function(index, element){
			$a = $(element).find("a[uid]");
			$a.eq(1).remove();
			$a.eq(0).addClass("accept-single");
			$a.eq(0).html("接受");
		});
	}
	
	function refreshCurrentTable(uids, $curDiv){
		var $countSpan = $curDiv.find("span.count-span");
		var count = parseInt($countSpan.html())-uids.length;
		if(count>0){
			$countSpan.html(count);
		}else{
			$curDiv.children("p.NA").show();
			$curDiv.children("div").hide();
		}
	}
	
	function error(xhr, ajaxOptions, error){
		alert(error);
	}
	
	function getCurrentDiv(){
		if($("#waiting").has(":visible").length>0){
			return $("#waiting");
		}else if($("#reject").has(":visible").length>0){
			return $("#reject");
		}else{
			console.log("GetCurrentDiv: error div is showing！");
			return [];
		}
	}
	
	$("a.accept-single").live("click", function(){
		var uid = $(this).attr("uid");
		var $tr = $(this).closest("tr[uid]");
		var uname = $.trim($tr.find("td.username").html());
		var auth = $tr.find("select[name=auth]").val();
		params = {"uids[]":uid, "unames[]":uname, "auths[]":auth,"status":"accept"};
		myAjaxRequest(url,params,afterSubmit,error);
	});
	
	$("a.reject-single").live("click", function(){
		var uid = $(this).attr("uid");
		var $tr = $(this).closest("tr[uid]");
		var uname = $.trim($tr.find("td.username").html());
		var auth = $tr.find("select[name=auth]").val();
		var ismember  = $(this).attr("member");
		if(typeof(ismember)=="undefined" || confirm("【"+uname+"】已经是团队成员，拒绝他的申请会把他从团队中移除！是否确认移除？")){
			params = {"uids[]":uid, "unames[]":uname, "auths[]":auth, "status":"reject"};
			myAjaxRequest(url,params,afterSubmit);
		}
	});
	
	function getSelectParams(){
		var $curDiv = getCurrentDiv();
		var uids = new Array();
		var unames = new Array();
		var member = new Array();
		var auths = new Array();
		$curDiv.find("input[name=single]").each(function(index, element){
			if($(element).attr("checked")){
				var $tr = $(element).parents("tr");
				var uid = $tr.attr("uid");
				var uname = $.trim($tr.find("td.username").html());
				var memberFlag = $tr.find("a[member]").length;
				var auth = $tr.find("select[name=auth]").val();
				uids.push(uid);
				unames.push(uname);
				if(memberFlag>0){
					member.push(uname);
				}
				auths.push(auth);
			}
		});
		return {"uids":uids, "unames":unames, "member":member, "auths":auths};
	}
	
	$("button.accept-all").click(function(){
		var result = getSelectParams();
		params = {"uids[]":result.uids, "unames[]":result.unames, "auths[]":result.auths,"status":"accept"};
		myAjaxRequest(url,params,afterSubmit);
	});
	
	$("button.reject-all").click(function(){
		var result = getSelectParams();
		var hasMember = result.member.length>0
		if(!hasMember){
			params = {"uids[]":result.uids, "unames[]":result.unames, "auths[]":result.auths, "status":"reject"};
			myAjaxRequest(url,params,afterSubmit);
		}else{
			var message = "";
			$.each(result.member, function(index, element){
				message += "【"+element+"】,";
			});
			message = message.substring(0,message.length-1);
			message += "已经是团队成员，拒绝他们的申请会把他们从团队中移除！是否确认移除？";
			if(confirm(message)){
				params = {"uids[]":result.uids, "unames[]":result.unames, "auths[]":result.auths, "status":"reject"};
				myAjaxRequest(url,params,afterSubmit);
			}
		}
	});
	/** ------------------------------------------**/
	
	/** ------------全选按钮事件-----------------**/
	$("input[name=all]").click(function(){
		var checked = $(this).attr("checked");
		$(this).parents("table").find("input[name=single]").attr("checked",checked);
	});
	
	$("input[name=single]").click(function(){
		var allchecked = true;
		$(this).parents("tbody").find("input[name=single]").each(function(index, element){
			if(!$(element).attr("checked")){
				allchecked = false;
				return;
			}
		});
		if(allchecked){
			$(this).parents("table").find("input[name=all]").attr("checked", "checked");
		}else{
			$(this).parents("table").find("input[name=all]").removeAttr("checked");
		}
	});
	/** ---------------------------------------------**/
	
});
</script>
<div class="config-float">
	<div id="nav" class="nav">
		<ul class="nav nav-pills nav-list">
			<li class="current" data-type="waiting"><p>待审核</p></li>
			<li data-type="reject"><p>已拒绝</p></li>
		</ul>
	</div>
	<!-- <ul class="nav nav-pills">
		<li class="active" data-type="waiting"><a href="#">待审核</a></li>
		<li data-type="reject"><a href="#">已拒绝</a></li>
	</ul> -->
	
	<div id="waiting" class="content">
		<p class="NA" <c:if test="${waiting.size()>0 }">style="display:none;"</c:if>>当前没有任何申请需要审核</p>
		<div <c:if test="${waiting.size()<=0 }">style="display:none;"</c:if>>
			<p>当前共有<span class="count-span">${waiting.size()}</span>人申请加入团队。</p>
			<table id="exist-member-table" class="dataTable" style="margin-top:1em; margin-bottom:2em;">
			<thead>
				<tr>
					<td class="dt3char"><input type="checkbox" name="all">全选</td>
					<td class="dtName">姓名</td>
					<td class="dtMail">邮箱（帐号）</td>
					<td class="dtDepartment dtNums" style="width:40px">单位</td>
					<td class="dtApplyTime">申请时间</td>
					<td>申请原因</td>
					<td class="dtStd" style="width:40px">权限
						<a class="showMsgAuthority ui-iconButton help"></a>
						<div class="msgAuthority msgFloat" style="display:none">可查看：可查看团队文档和信息
							<br/>可编辑：可查看、编辑和上传文档，但不可管理团队
							<br/>可管理：可查看、编辑和上传文档，可管理团队信息
						</div>	
					</td>
					<td class="dtName">审核操作</td>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${waiting}" var="item" varStatus="status">
				<tr class="member-row" uid="${item.teamApplicant.uid}">
					<td><input type="checkbox" name="single"></td>
					<td class="username">${item.userName}</td>
					<td>${item.teamApplicant.uid}</td>
					<td>${item.department}</td>
					<td><fmt:formatDate pattern='yyyy-MM-dd HH:mm:ss' value='${item.teamApplicant.applyTime }'/></td>
					<td>${item.teamApplicant.reason}</td>
					<td><select name="auth">
							<option value="view" <c:if test="${currTeam.defaultMemberAuth eq 'view'}">selected</c:if>>可查看</option>
							<option value="edit" <c:if test="${currTeam.defaultMemberAuth eq 'edit'}">selected</c:if>>可编辑</option>
							<option value="admin" <c:if test="${currTeam.defaultMemberAuth eq 'admin'}">selected</c:if>>可管理</option>
						</select>
					</td>
					<td>
						<a class="accept-single" uid="${item.teamApplicant.uid}">接受</a>
						<a class="reject-single" uid="${item.teamApplicant.uid}" <c:if test="${isMembers[status.index]}">member="member"</c:if>>拒绝</a>
					</td>
				</tr>
				</c:forEach>
			</tbody>
			</table>
			<div class="btn-groups">
				<button class="accept-all largeButton">接受</button>
				<button class="reject-all largeButton">拒绝</button>
				<span class="ui-spotLight"></span>
			</div>
		</div>
	</div>
	
	<div id="reject" class="content" style="display:none;">
		<p class="NA" <c:if test="${reject.size()>0 }">style="display:none;"</c:if>>当前没有任何申请被拒绝</p>
		<div <c:if test="${reject.size()<=0 }">style="display:none;"</c:if>>
			<p>当前共有<span class="count-span">${reject.size()}</span>人的申请被拒绝。</p>
			<table id="exist-member-table" class="dataTable" style="margin-top:1em; margin-bottom:2em;">
			<thead>
				<tr>
					<td class="dt3char"><input type="checkbox" name="all">全选</td>
					<td class="dtName">姓名</td>
					<td class="dtMail">邮箱（帐号）</td>
					<td class="dtDepartment dtNums">单位</td>
					<td class="dtApplyTime">申请时间</td>
					<td class="dtStd">权限
						<a class="showMsgAuthority ui-iconButton help"></a>
						<div class="msgAuthority msgFloat" style="display:none">可查看：可查看团队文档和信息
							<br/>可编辑：可查看、编辑和上传文档，但不可管理团队
							<br/>可管理：可查看、编辑和上传文档，可管理团队信息
						</div>	
					</td>
					<td class="dtName">审核操作</td>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${reject}" var="item" varStatus="status">
				<tr class="member-row" uid="${item.teamApplicant.uid}">
					<td><input type="checkbox" name="single"></td>
					<td class="username">${item.userName}</td>
					<td>${item.teamApplicant.uid}</td>
					<td>${item.department}</td>
					<td><fmt:formatDate pattern='yyyy-MM-dd HH:mm:ss' value='${item.teamApplicant.applyTime }'/></td>
					<td><select name="auth">
							<option value="view" <c:if test="${currTeam.defaultMemberAuth eq 'view'}">selected</c:if>>可查看</option>
							<option value="edit" <c:if test="${currTeam.defaultMemberAuth eq 'edit'}">selected</c:if>>可编辑</option>
							<option value="admin" <c:if test="${currTeam.defaultMemberAuth eq 'admin'}">selected</c:if>>可管理</option>
						</select>
					</td>
					<td>
						<a class="accept-single" uid="${item.teamApplicant.uid}">接受</a>
					</td>
				</tr>
				</c:forEach>
			</tbody>
			</table>
			<div class="btn-groups">
				<button class="accept-all largeButton">接受</button>
				<span class="ui-spotLight"></span>
			</div>
		</div>
	</div>
</div>
