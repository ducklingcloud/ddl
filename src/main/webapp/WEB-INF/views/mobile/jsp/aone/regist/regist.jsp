<%@page import="net.duckling.ddl.common.DDLFacade"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import="net.duckling.ddl.service.url.URLGenerator"%>
<%@ page import="net.duckling.ddl.common.DDLFacade"%>
<fmt:setBundle basename="templates.default" />
<meta http-equiv="pragma" content="no-cache" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<link href="${contextPath}/scripts/bootstrap/css/bootstrap.min.css" rel="stylesheet" media="screen"/>
<link href="${contextPath}/scripts/bootstrap/css/bootstrap-responsive.min.css" rel="stylesheet"/>
<link rel="stylesheet" href="${contextPath}/scripts/bootstrap/css/todc-bootstrap.css" type="text/css" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/uiLib.css" type="text/css" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/aone.css" type="text/css" />
<style>
	div.ui-wrap,#content,.ui-wrap input[type=text], .ui-wrap input[type=password],.dface.footer,#footer {width:auto;width:auto !important;}
	.ui-wrap input[type="text"], .ui-wrap input[type="password"] {height:24px;}
	#content #registForm table.ui-table-form th, #content #registForm table.ui-table-form td {padding-bottom:0.8em}
	.btn.btn-primary.btn-large {width:180px}
	.dface.footer.container ul.simple-footer-nav {margin:0}
	.errorContainer,.errorContainer label.error {display:inline-block;}
	.red {color:#a00; font-weight:bold;}
	.form-horizontal .control-label {font-weight:bold;}
</style>
<script type="text/javascript" src="${contextPath}/jsp/aone/js/uiLib-jQuery.js"></script>
<script type="text/javascript" src="${contextPath}/jsp/aone/js/global.js"></script>
<script type="text/javascript">
$(document).ready(function(){
		var srcMessage = "该邮箱已经注册过或不能使用"
		var remoteMessage=srcMessage;
		var remoteMessageFun = function(){
			return remoteMessage;
		}
		var currentValidator = $("#registForm").validate({
			submitHandler:function(form){
				var value = $("input[name=checkCode]").val();
				var dd = "checkCode="+value+"&type=registType"
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
							$("input[name=checkCode]").parent().find('.errorContainer').html("<label class='error' generated='true'>验证码错误,请重新输入!</label>");
							$("#checkCodePiceture").attr("src","<vwb:Link context='pictrueCheckCode' format='url'/>?func=getImage&type=registType"+"&&rand="+Math.random());
							return ;
						}
					},
					statusCode:{
						450:function(){alert('会话已过期,请重新登录');},
						403:function(){alert('您没有权限进行该操作');}
					}
				});
			},
			rules: {
				uid: {required: true ,email: true, remote:"<vwb:Link context='regist' format='url'/>?func=validate"},
				name:{required: true},
				password:{ required:true, minlength:6 },
				passwordAgain:{ required:true, minlength:6, equalTo: "[name=password]" }
			},
			messages:{
				uid:{required:"请输入邮箱",email:"请输入有效的邮箱地址",remote : remoteMessageFun},
				name:{required:"请输入姓名"},
				password:{required:"请输入密码",minlength:"密码不能少于6位"},
				passwordAgain:{required:"请再次输入密码",minlength:"密码不能少于6位",equalTo:"两次输入的密码不一致"}
			},
			errorPlacement: function(error, element){
				error.appendTo(element.parent().find('.errorContainer'));
			}
		});
		refreshCode = function(){
			$("#checkCodePiceture").attr("src","<vwb:Link context='pictrueCheckCode' format='url'/>?func=getImage&type=registType"+"&&rand="+Math.random());
			return false;
		};
		
		
		$("input[name=uid]").keyup(function(event){
			var val = event.which;
			if(val<=90&&val>=65){
				$(this).val($(this).val().toLocaleLowerCase());
			}else if(val==32){
				$(this).val($.trim($(this).val()));
			}
			event.preventDefault();
		});
		
		//popupLogin
		$("#mask_config_1").css({
			"width":$(document.body).outerWidth(),
			"top":"0","left":"0"
		});
		var coverStyle = setInterval(function(){
			if($(document.body).outerHeight() > $(window).height()){ 
				$("#mask_config_1").css({
					"height":$(document.body).outerHeight()
				});
			}
			else{
				$("#mask_config_1").css({
					"height":window.innerHeight
				});
			}
		},20); 
		
		//登录
		$("a#popupLogin").live('click',function(){
			var email = $("input[name='uid']").val();
			$("input[name=existedUser]").val(email);
			$("#mask_config_1").show();
			$(".popuplogin").show();
			var v = $("input[name='inviteURL']").val();
			$("#existInviteURL").val(v);
		});
		//取消
		$("p.popuplogin-title span.ui-dialog-x,a.dim").click(function(){
			$("#mask_config_1").hide();
			$(".popuplogin").hide();
		})
		var getRemoteUrl = function (){
			return "<vwb:Link context='regist' format='url'/>?func=validateEmail&existedPassword="+$("#existedPassword").val();
		}
		$("#existedUser").validate({
			submitHandler : function(form){
				$.ajax({
					url : "<vwb:Link context='regist' format='url'/>?func=validateEmail",
					type : "post",
					data : $("#existedUser").serialize(),
					dataType:"json",
					success:function(dd){
						if(dd.status==true||dd.status=='true'){
							form.submit();
						}else{
							$("input[name=existedUser]").parent().next().html("<label class='error' for='existedUser' generated='true' style='display: block;''>邮箱或密码不正确</label>");
						}
					},
					statusCode:{
						450:function(){alert('会话已过期,请重新登录');},
						403:function(){alert('您没有权限进行该操作');}
					}

				});
			},
			rules: {
				existedUser: {required: true,email: true},
				existedPassword:{ required:true, minlength:6 }
			},
			messages:{
				existedUser:{required:"请输入邮箱",email:"请输入有效的邮箱地址"},
				existedPassword:{required:"请输入密码",minlength:"密码不能少于6位"}
			},
			errorPlacement: function(error, element){
				error.appendTo(element.parent().find('.errorContainer'));
			}
		});
		
		
		$("#userOtherEmal").live("click",function(){
			changeRegisterHref();
		});
		
		var changeRegisterHref = function (){
			var d = $("input[name='uid']").attr("readonly");
			if(d=='readonly'||d){
				uidInputChangeType();
				$("#submitWithInviteId").val("registWithInviteAndOtherEmail");
				remoteMessage = "您可以使用此邮箱<a id='popupLogin'>登录</a>";
			}else{
				initUid();
			}
		}
		var uidInputChangeType = function(){
			$("input[name='uid']").removeAttr("readonly");
			$("input[name='uid']").css("background","none");
			
		}
		
		var initUid = function(){
			$("input[name='uid']").attr("readonly","true");
			$("input[name='uid']").css("background","#cccccc");
			$("input[name='uid']").removeClass("error");
			$("input[name='uid']").val($("#orgUid").val());
			$("#submitWithInviteId").val("submitWithInvite");
			$("input[name='uid']").parent().parent().find(".errorContainer").html("");
			remoteMessage = srcMessage;
		}
		
});
</script>

<%--<div id="content-title">
	<h1 style="font-size:27px">
	<c:choose>
			<c:when test="${not empty joinGroupName}">
				注册账号并加入${joinTeamName }团队
			</c:when>
			<c:otherwise>
				注册
			</c:otherwise>
	</c:choose>
	</h1>
	<!-- <p class="ui-text-note" style="margin-left:3em">注册为<span class="">Duckling通行证</span></p> -->
	 <div class="ui-RTCorner" style="margin-top:-1em; margin-right:10px">
		已经注册过，或拥有Duckling通行证？请
		<vwb:Link context='login' >登录</vwb:Link>
	</div> 
</div>--%>
<%
	String url = DDLFacade.getBean(URLGenerator.class).getAbsoluteURL("regist", "", "");
	request.setAttribute("url", url);
%>
<div id="mask_config_1" class="intro_mask"></div>

<div class="content-through">
	<div class="container" style="border:none;">
	<form:form id="registForm" class="form-horizontal" style="margin:10px;" modelAttribute="registerSubmit" commandName="registerForm" action="${url }">
	<!-- <form id="registForm" action="<vwb:Link context='regist' format='url'/>" method="POST"> -->
		<c:choose>
			<c:when test="${not empty invitation}">
				<input id="submitWithInviteId" type="hidden" name="func" value="submitWithInvite"/>
				<input type="hidden" name="inviteURL" value="${invitation.displayURL}"/>
				<div class="msgBox">
					<p>${invitation.inviterName}邀请您加入
					<c:choose>
						<c:when test="${not empty team }">
							${team.displayName }
						</c:when>
						<c:otherwise>
							${invitation.teamName}
						</c:otherwise>
					</c:choose>
					团队。</p>
					<p>在接受邀请前，请您先进行注册。</p>
				</div>
			</c:when>
			<c:otherwise>
				<input type="hidden" name="func" value="submit"/>
			</c:otherwise>
		</c:choose>
		
		<div class="control-group">
			<label class="control-label">
				<span class="red">*</span>用户邮箱
			</label>
			<div class="controls">
				<c:choose>
					<c:when test="${not empty invitation}">
						<input id="uid" name="uid" type="text" value="${invitation.invitee}" readonly="readonly" style="background-color:#cccccc" class="ui-textInput-long" />
						<div class="errorContainer">
							<label class='error'><form:errors path="uid" cssClass="error" class="error" /></label>
						</div>
						<span> &nbsp&nbsp<a id="userOtherEmal">使用其他邮箱</a></span>
						<input id="orgUid" type="hidden" value="${invitation.invitee}"/>
					</c:when>
					<c:otherwise>
						<form:input  path="uid" class="ui-textInput-long" />
						<div class="errorContainer">
							<label class='error'><form:errors path="uid" cssClass="error" class="error" /></label>
						</div>
						<br/><span id="uidHint" class="ui-text-note">请使用有效的邮箱注册</span>
						<!--<br/> <span class="ui-text-note">注册激活、系统通知、团队活动通知等都将发送到该邮箱</span> -->
						<%-- 屏蔽escience邮箱注册链接
						  &nbsp&nbsp<span><a id="userEscienceEmail" href="<vwb:Link context='escienceRegist' format='url'/>">使用escience邮箱</a></span> 
						--%>
					</c:otherwise>
				</c:choose>
				
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">
				<span class="red">*</span>真实姓名
			</label>
			<div class="controls">
				<form:input type="text" path="name" value=""/>
				<div class="errorContainer"><label class='error'>
					<form:errors path="name" cssClass="error" class="error" /></label>
				</div>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">
				<span class="red">*</span>密码
			</label>
			<div class="controls">
				<form:input type="password" path="password" value=""/>
				<div class="errorContainer"><label class='error'><form:errors path="password" cssClass="error" class="error" /></label> </div>
					<br/><span class="ui-text-note">为保证安全，请使用长度6位以上的密码</span>
				
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">
				<span class="red">*</span>确认密码
			</label>
			<div class="controls">
				<form:input type="password" path="passwordAgain" value=""/>
				<div class="errorContainer"></div>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">
				<span class="red">*</span>验证码
			</label>
			<div class="controls">
				<input type=text name="checkCode" value=""/>
				<c:choose>
					<c:when test="${not empty checkError }">
						<div class="errorContainer"><label class='error'>${checkError}</label></div>
					</c:when>
					<c:otherwise>
						<div class="errorContainer"></div>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">
				
			</label>
			<div class="controls">
				<img id="checkCodePiceture" style="width:80px; height:40px;" src="<vwb:Link context='pictrueCheckCode' format='url'/>?func=getImage&type=registType">
				<a href="#" onclick="refreshCode()">换一张</a>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">
				
			</label>
			<div class="controls">
				<input type="submit" value="注册" class="btn btn-primary btn-large"/>
			</div>
		</div>
		
		
		<c:choose>
			<c:when test="${not empty joinGroupName}">
				<input type="hidden" name="joinGroupName" value="${joinGroupName}"/>
			</c:when>
			<c:otherwise>
				<form:input path="joinGroupName" type="hidden"/>
			</c:otherwise>
		</c:choose>
	<!--</form>-->
	</form:form>
	<!-- <div class="toolHolder holderMerge"></div> -->
	</div>
	
	<div class="ui-clear"></div>
</div>
<script type="text/javascript" src="${contextPath}/scripts/jquery/jquery.validate.js"></script>
