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
<script type="text/javascript">
$(document).ready(function(){
	var team = "${fn:escapeXml(currTeam.displayName)}";
	var DMauth = "${currTeam.defaultMemberAuth}";
	init();
	
	function init(){
		selectChange();
		$("input[type=radio][name=accessType]").live('change',selectChange);
	}
	
	function selectChange(){
		var accessType;
		$("input[type=radio][name=accessType]").each(function(){
			if($(this).attr('checked')){
				accessType = $(this).val();
				return;
			}
		});
		if(accessType == 'public'){
			$("#accessType-protected-info").attr('style','display:none');
			$("#accessType-public-info").removeAttr('style');
			$("#accessType-private-info").attr('style','display:none');
			$("#accessType-public-info input[name=auth]").attr('checked',false);
			$("#accessType-public-info input[name=auth][value="+DMauth+"]").attr('checked',true);
			$("input[name=defaultMemberAuth]").val(DMauth);
		}else if(accessType == 'protected'){
			$("#accessType-protected-info").removeAttr('style');
			$("#accessType-public-info").attr('style','display:none');
			$("#accessType-private-info").attr('style','display:none');
			$("#accessType-protected-info input[name=auth]").attr('checked',false);
			$("#accessType-protected-info input[name=auth][value="+DMauth+"]").attr('checked',true);
			$("input[name=defaultMemberAuth]").val(DMauth);
		}else{
			$("#accessType-protected-info").attr('style','display:none');
			$("#accessType-public-info").attr('style','display:none');
			$("#accessType-private-info").removeAttr('style');
			$("input[name=defaultMemberAuth]").val('edit');
		}
	}
	
	$('#saveBasicButton').click(function(){
		var title = $('form[name=team-info] input[name=title]').val().trim();
		if(title==""){
			ui_spotLight('teamTitle-spotLight', 'success', '团队名不能为空', 'fade');
			$('form[name=team-info] input[name=title]').val(team);
			return;
		}
		if(validateName(title)){
			ui_spotLight('teamTitle-spotLight', 'success', '团队名不能包含: ?\\ /*<>|\"', 'fade');
			$('form[name=team-info] input[name=title]').val(team);
			return;
		}
		var descript = $('textarea[name=description]').val().trim();
		if(descript&&validateName(descript)){
			ui_spotLight('teamDesc-spotLight', 'success', '团队描述不能包含:?\\ /*<>|\"', 'fade');
			return;
		}
		var url = site.getURL('configTeam','${currTeam.name}');
		var params = $('form[name=team-info]').serialize();
		ajaxRequest(url,params,function(data){
			if(data&&data.success!=undefined&&data.success==false){
				alert(data.message);				
			}else{
				ui_spotLight('updateAll-spotLight', 'success', '当前设置已保存', 'fade');
			}
			
		});
		$("input[name=defaultMemberAuth]").each(function(){
			if($(this).attr('checked')){
				DMauth = $(this).val();
				return;
			}
		});
	});
	function validateName(name){
		var reg = /[:\\\/<>*?|"]/;
		return name.match(reg);
	}
	
	function isLtGt(value){
		var ltGt = /^[<>]+$/;
		return (ltGt.test(value));
	}
	
	$('input[type=radio][name=auth]').change(function(){
		$('input[name=defaultMemberAuth]').val($(this).val());
	});
});
</script>
<div class="config-float">

	<form name="team-info" action="#">
		<input type="hidden" name="func" value="updateBasicInfo"/>
		<input type="hidden" value="edit" name="defaultMemberAuth">
		<table class="ui-table-form">
		<tbody>
			<tr><th style="padding-top: 0">团队名称：</th>
				<td>
					<c:choose>
						<c:when test="${'personal' eq currTeam.type }">
							${fn:escapeXml(currTeam.displayName)}
							<input type="hidden" name="title" value="${fn:escapeXml(currTeam.displayName)}"/>
						</c:when>
						<c:otherwise>
							<input type="text" name="title" value="${fn:escapeXml(currTeam.displayName)}"/>
							<span class="ui-spotLight" id="teamTitle-spotLight"></span>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
			<tr><th style="padding-top: 0">团队网址：</th>
				<td>${teamUrl}</td>
			</tr>
			<tr><th style="padding-top: 0">空间使用：</th>
				<td>已使用${teamSize.usedDisplay }(总共${teamSize.totalDisplay })</td>
			</tr>
			<c:if test="${teamType != 'myspace' }">
			<tr><th>团队权限：</th>
				<td>
					<ul style="list-style:none; padding:0; margin:0;">
						<li>
							<input type="radio" name="accessType" value="private" 
								<c:if test="${currTeam.accessType eq 'private' }"> checked="checked"</c:if>/>完全保密
							<input type="radio" name="accessType" value="protected" 
								<c:if test="${currTeam.accessType eq 'protected' }"> checked="checked"</c:if>/>公开需审核
							<input type="radio" name="accessType" value="public" 
								<c:if test="${currTeam.accessType eq 'public' }"> checked="checked"</c:if>/>完全公开
						</li>
						<li>
							<div id="accessType-public-info">
								<span class="ui-text-note">用户无需管理员审核即可通过以下三种方式加入：</span>
								<br><span class="ui-text-note">1) 通过推广链接注册加入 ( <b>推广链接：${currTeamUrl} </b>)</span>
								<br><span class="ui-text-note">2) 从首页（Dashboard）的“加入公开团队”功能加入</span>
								<br><span class="ui-text-note">3) 接受团队邀请加入</span>
								<br><span class="ui-text-note">团队新成员加入团队后的默认权限为：</span>
								<br><input type="radio" name="auth" value="view" 
									<c:if test="${currTeam.defaultMemberAuth eq 'view'}"> checked="checked" </c:if>>可查看
								<input type="radio" name="auth" value="edit"
									<c:if test="${currTeam.defaultMemberAuth eq 'edit'}"> checked="checked" </c:if>>可编辑
							</div>
							<div id="accessType-protected-info">
							    <span class="ui-text-note">用户需要管理员审核才能加入团队，可通过以下三种方式加入：</span>
								<br><span class="ui-text-note">1) 通过推广链接注册加入 ( <b>推广链接：${currTeamUrl} </b>)</span>
								<br><span class="ui-text-note">2) 从首页（Dashboard）的“加入公开团队”功能加入</span>
								<br><span class="ui-text-note">3) 接受团队邀请加入</span>
								<br><span class="ui-text-note">团队新成员加入团队后的默认权限为：</span>
								<br><input type="radio" name="auth" value="view" 
									<c:if test="${currTeam.defaultMemberAuth eq 'view'}"> checked="checked" </c:if>>可查看
								<input type="radio" name="auth" value="edit"
									<c:if test="${currTeam.defaultMemberAuth eq 'edit'}"> checked="checked" </c:if>>可编辑
							</div>
							
							<div id="accessType-private-info">
								<span class="ui-text-note">团队成员仅能由管理员邀请加入。</span>
							</div>
						</li>
					</ul>
				</td>
			</tr>
			</c:if>
			<tr><th>默认首页： </th>
				<td><input type="radio" name="teamDefaultView" value="list" 
						<c:if test="${currTeam.teamDefaultView != 'notice' }"> checked="checked" </c:if>>文件
					<input type="radio" name="teamDefaultView" value="notice" 
						<c:if test="${currTeam.teamDefaultView eq 'notice'}"> checked="checked" </c:if>>动态
						<br/>
						<span class="ui-text-note">设置团队默认进入的首页</span>
				</td>
			</tr>
			<tr><th>团队描述：</th>
				<td><textarea name="description">${currTeam.description}</textarea><span class="ui-spotLight" id="teamDesc-spotLight"></span></td>
			</tr>
		</tbody>
		<tfoot>
			<tr><th></th>
				<td class="largeButtonHolder">
				<input type="button" id="saveBasicButton" class="btn-success" value="保存更改"/>
				<span class="ui-spotLight" id="updateAll-spotLight"></span>
				</td>
			</tr>
		</tfoot>
		</table>
	</form>

</div>