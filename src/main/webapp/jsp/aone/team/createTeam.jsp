<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import="net.duckling.ddl.service.url.URLGenerator"%>
<%@ page import="net.duckling.ddl.common.DDLFacade"%>
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
p.subHint {margin:0.5em 0; font-size:0.9em; color:#666;}
</style>

<script type="text/javascript">
$(document).ready(function(){

		jQuery.validator.addMethod( "isTeamId" ,  function (value, element)  {       
		     var  teamId  =   /^[a-z0-9\-]+$/;     
     		 return   this .optional(element)  ||  (teamId.test(value));       
 		} ,  " 请正确填写团队名称 " ); 
		jQuery.validator.addMethod("isLtGt",function(value,element){
			var ltGt = /[:\\\/<>*?|"]/;;
			return !(ltGt.test(value));
		},"请不要输入非法字符:?\\ /*<>|\":");
		
		var currentValidator = $("#createTeamForm").validate({
			submitHandler:function(form){
				form.submit();
				/*
				var value = $("input[name=checkCode]").val();
				var dd = "checkCode="+value+"&type=teamType"
				$.ajax({
					url:"<vwb:Link context='pictrueCheckCode' format='url'/>?func=checkCode",
					type:"POST",
					dataType:"json", 
					data:dd, 
					error:function(){},
					success:function(data) {
						if(data.status=="success"){
							form.submit();
						}else{
							$("input[name=checkCode]").addClass("error");
							$("input[name=checkCode]").parent().next('.errorContainer').html("<label class='error' generated='true'>验证码错误,请重新输入!</label>");
							$("#checkCodePiceture").attr("src","<vwb:Link context='pictrueCheckCode' format='url'/>?func=getImage&type=teamType"+"&&rand="+Math.random());
							return ;
						}
					},
					statusCode:{
						450:function(){alert('会话已过期,请重新登录');},
						403:function(){alert('您没有权限进行该操作');}
					}
				});
				*/
			},
			rules: {
				teamId: {	
							required: true,isTeamId:true,
							isLtGt:true,
							minlength:2,
							remote:"<vwb:Link context='createTeam' format='url'/>?func=validateTeamId"
						},
				teamName:{required: true,isLtGt:true,maxlength : 100},
				teamDescription:{isLtGt:true}
			},
			messages:{
				teamId: {
					required: '请输入团队网址',
					minlength:'输入的团队网址不能少于2个字符',
					isTeamId:"团队网址只能包括小写英文字母、数字和中划线",
					remote: '该地址已被使用。请尝试其他地址。'
				},
				teamName: {
					required: '请输入团队名称',
					maxlength : '团队名称不能超过100个字符'
				}
			},
			errorPlacement: function(error, element) {
				error.appendTo(element.parent().next('.errorContainer'));
			}
		});
		
		$("input[name='cancel']").live("click",function(){
			window.location = "<vwb:Link context='switchTeam' format='url'/>";
		});
		$("#teamName").live("blur",function(){
			var value = $("#teamName").val();
			var r = "";
			for(var i=0;i<value.length;i++){
				var c = value.charCodeAt(i); 
		        if ((c >= 48 && c <= 57) || (65<=c && c<=90)|| (97<=c && c<=122)||c==45){ 
		             r=r+value[i];
		        } 
			}
			var teamId = $("#teamId").val();
			if(!teamId){
				$("#teamId").val(r.toLowerCase());
			}
		});
		
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
				$("#accessType-public-info input[name=auth][value=view]").attr('checked',true);
				$("input[name=defaultMemberAuth]").val('view');
			}else if(accessType == 'protected'){
				$("#accessType-protected-info").removeAttr('style');
				$("#accessType-public-info").attr('style','display:none');
				$("#accessType-private-info").attr('style','display:none');
				$("#accessType-protected-info input[name=auth]").attr('checked',false);
				$("#accessType-protected-info input[name=auth][value=edit]").attr('checked',true);
				$("input[name=defaultMemberAuth]").val('edit');
			}else{
				$("#accessType-protected-info").attr('style','display:none');
				$("#accessType-public-info").attr('style','display:none');
				$("#accessType-private-info").removeAttr('style');
				$("input[name=defaultMemberAuth]").val('edit');
			}
		}
		
		refreshCode = function(){
			$("#checkCodePiceture").attr("src","<vwb:Link context='pictrueCheckCode' format='url'/>?func=getImage&type=teamType"+"&&rand="+Math.random());
			return false;
		};
		
		$("input[type=radio][name=accessType]").live('change',selectChange);
		selectChange();
		
		$('input[type=radio][name=auth]').change(function(){
			$('input[name=defaultMemberAuth]').val($(this).val());
		});
});
</script>

<div id="content-title">
	<h1>创建团队</h1>
</div>
<div class="content-through">
	<%
	String url = DDLFacade.getBean(URLGenerator.class).getAbsoluteURL("createTeam", "", "");
	request.setAttribute("url", url);
	%>
	<form:form action="${url }" id="createTeamForm" modelAttribute="teamForm" commandName="teamForm">
	<!--  	<form id="createTeamForm" action="<vwb:Link context='createTeam' format='url'/>" method="POST">-->
		<input type="hidden" value="createTeam" name="func"/>
		<input type="hidden" value="edit" name="defaultMemberAuth">
		<table class="ui-table-form" style="font-size:14px; width:800px;">
		<tbody>
			<tr><th width="120">团队名称：</th>
				<td width="10" class="ui-text-alert">*</td>
				<td width="320"><form:input  path="teamName" value="" maxlength="100" style="width:270px;padding:3px;"/></td>
				<td width="150" class="errorContainer"><label class='error' generated='true'><form:errors path="teamName" cssClass="error" class="error" /></label></td>
			</tr>
			<tr><th>团队网址：</th>
				<td class="ui-text-alert" style="padding-top:7px;">*</td>
				<td style="vertical-align:middle;">${baseUrl}<form:input type="text" path="teamId" value="" maxlength="200" style="width:133px;padding:3px;"/>
					<p class="subHint"><span class="ui-text-note">一经生成不可修改，使用小写英文字母和数字</span></p>
				</td>
				<td class="errorContainer"><label class='error' generated='true'><form:errors path="teamId" cssClass="error" class="error" /></label></td>
			</tr>
			<tr><th>团队权限：</th>
				<td colspan="3">
					<ul style="list-style:none; padding:0 0 0 10px; margin:0;">
						<li>
							<form:radiobutton name="accessType" path="accessType" value="private" checked="checked"/>完全保密
							<form:radiobutton name="accessType" path="accessType" value="protected"/>公开需审核
							<form:radiobutton name="accessType" path="accessType" value="public"/>完全公开
						</li>
						<li>
							<div id="accessType-public-info">
								<p class="subHint"><span class="ui-text-note">用户无需管理员审核即可通过以下三种方式加入：</span></p>
								<p class="subHint"><span class="ui-text-note">1）通过推广链接注册加入（管理员可在团队“基本设置”里获取推广链接 ）</span></p>
								<p class="subHint"><span class="ui-text-note">2）从首页（Dashboard）的“加入公开团队”功能加入</span></p>
								<p class="subHint"><span class="ui-text-note">3）接受团队邀请加入</span></p>
								<p class="subHint">
									<span class="ui-text-note">您可以指定新成员进入团队后的默认权限：</span>
									<form:radiobutton path="auth" value="view" checked="checked"/>可查看
									<form:radiobutton path="auth" value="edit"/>可编辑
								</p>
							</div>
							<div id="accessType-protected-info">
								<p class="subHint"><span class="ui-text-note">用户需要管理员审核才能加入团队，可通过以下三种方式加入：</span></p>
								<p class="subHint"><span class="ui-text-note">1）通过推广链接注册加入（管理员可在团队“基本设置”里获取推广链接）</span></p>
								<p class="subHint"><span class="ui-text-note">2）从首页（Dashboard）的“加入公开团队”功能加入</span></p>
								<p class="subHint"><span class="ui-text-note">3）接受团队邀请加入</span></p>
								<p class="subHint">
									<span class="ui-text-note">您可以指定新成员进入团队后的默认权限：</span>
									<form:radiobutton path="auth" value="view" checked="checked"/>可查看
									<form:radiobutton path="auth" value="edit"/>可编辑
								</p>
							</div>
							<div id="accessType-private-info">
								<p class="subHint"><span class="ui-text-note">团队成员仅能由管理员邀请加入。</span></p>
							</div>
						</li>
					</ul>
				</td>
			</tr>
			<%--
			<tr>
				<th>团队用途：</th>
				<td colspan="3">
					<ul style="list-style:none; padding:0 0 0 10px; margin:0;">
						<li>
							<select name="teamInfo">
								<option value="docManager" selected="selected">文档管理</option>
								<option value="teamcommunication">团队沟通</option>
							</select>
						</li>
					</ul>
				</td>
			</tr>
			 --%>
			<input type="hidden" name="teamInfo" value="docManager" />
			<tr><th>团队简介：</th>
				<td></td>
				<td><form:textarea path="teamDescription" style="width:275px;"/></td>
				<td class="errorContainer">
					<c:if test="${not empty teamDescriptionError }">
						<label class='error'>${teamDescriptionError}</label>
					</c:if>
				</td>
			</tr>
			<!--
			<tr><th>验证码：</th>
				<td width="10" class="ui-text-alert">*</td>
				<td><input type="text" name="checkCode" style="width:220px;padding:3px;"></td>
				<c:choose>
					<c:when test="${not empty checkError }">
						<td class="errorContainer"><label class='error'>${checkError}</label></td>
					</c:when>
					<c:otherwise>
						<td class="errorContainer"></td>
					</c:otherwise>
				</c:choose>
			</tr>
			
			<tr><th></th>
				<td></td>
				<td><img id="checkCodePiceture" style="width:80px; height:40px;" src="<vwb:Link context='pictrueCheckCode' format='url'/>?func=getImage&type=teamType">
				<a onclick="refreshCode()">换一张</a></td>
			</tr>
			-->
		</tbody>
		<tfoot>
			<tr><th></th>
				<td></td>
				<td class="">
					<input type="submit" value="创建团队" class="btn btn-success btn-large"/>&nbsp;&nbsp;
					<input type="button" class="btn btn-large" name="cancel" value="取消" />
				</td>
				<td></td>
			</tr>
		</tfoot>
		</table>
		
		<!-- <div class="procedureHolder holderMerge"></div> -->
		
	<!--</form>-->
	
	</form:form>
</div>
<div class="clear"></div>
