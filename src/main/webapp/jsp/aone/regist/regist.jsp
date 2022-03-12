<%@page import="net.duckling.ddl.common.DDLFacade"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import="net.duckling.ddl.common.DDLFacade"%>
<fmt:setBundle basename="templates.default" />
<link rel="stylesheet" href="${contextPath}/scripts/bootstrap/css/todc-bootstrap.css" type="text/css" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/uiLib.css" type="text/css" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/aone.css" type="text/css" />
<style>
	.ui-wrap input[type="text"], .ui-wrap input[type="password"] {height:24px;}
	#content #registForm table.ui-table-form th, #content #registForm table.ui-table-form td {padding-bottom:0.8em}
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
							$("input[name=checkCode]").parent().next('.errorContainer').html("<label class='error' generated='true'>验证码错误,请重新输入!</label>");
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
				error.appendTo(element.parent().next('.errorContainer'));
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
				error.appendTo(element.parent().next('.errorContainer'));
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

<div id="content-title">
	<h1 style="font-size:27px">
	<c:choose>
			<c:when test="${not empty joinGroupName}">
				注册账号并加入${joinTeamName }团队
			</c:when>
			<c:otherwise>
				新用户注册
			</c:otherwise>
	</c:choose>
	</h1>
	<p class="ui-text-note" style="margin-left:3em">注册为<span class="">中国科技网通行证</span></p>
	<%-- <div class="ui-RTCorner" style="margin-top:-1em; margin-right:10px">
		已经注册过，或拥有中国科技网通行证？请
		<vwb:Link context='login' >登录</vwb:Link>
	</div> --%>
</div>

<div id="mask_config_1" class="intro_mask"></div>
<div class="popuplogin" style="display:none;">
	<p class="popuplogin-title">登录 <span class="ui-dialog-x"></span></p>
	<form action="${contextPath }/system/regist" id="existedUser" method="post">
	<input name="func" type="hidden" value="registByExistedUser"/>
	<input name="existInviteURL" type="hidden" id="existInviteURL"/>
	<table style="margin-top:3em" class="ui-table-form">
		<tbody>
			<tr>
				<th width="200">用户邮箱</th>
				<td width="10" class="ui-text-alert">*</td>
				<td>
					<input type="text" name="existedUser">
				</td>
				<td width="300" class="errorContainer"><label class="error">
				</label></td>
			</tr>
			<tr>
				<th>密码</th>
				<td class="ui-text-alert">*</td>
				<td><input type="password" value="" name="existedPassword" id="existedPassword">
				</td>
				<td class="errorContainer"><label class="error"></label> </td>
			</tr>
			<tr>
				<th></th>
				<td class="ui-text-alert"></td>
				<td class="largeButtonHolder">
					<input type="submit" value="登录">
					<a class="largeButton dim">取消</a> 
				</td>
			</tr>
		</tbody>
	</table>
	</form>
</div>

<div class="content-through">
	<div id="content-major" style="width:650px;">
	
	<form:form id="registForm" modelAttribute="registerSubmit" commandName="registerForm" action="${contextPath }/system/regist">
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
		
		<table class="ui-table-form" style="margin-top:3em;margin-right:0;">
		<tbody>
			<tr><th>用户邮箱</th>
				<td width="10" class="ui-text-alert">*</td>
				<td>
				<c:choose>
					<c:when test="${not empty invitation}">
						<input id="uid" name="uid" type="text" value="${invitation.invitee}" readonly="readonly" style="background-color:#cccccc" class="ui-textInput-long" />
						<span> &nbsp&nbsp<a id="userOtherEmal">使用其他邮箱</a></span>
						<input id="orgUid" type="hidden" value="${invitation.invitee}"/>
					</c:when>
					<c:otherwise>
						<form:input  path="uid" class="ui-textInput-long" />
						<br/><span id="uidHint" class="ui-text-note">请使用有效的邮箱注册</span>
						<!--<br/> <span class="ui-text-note">注册激活、系统通知、团队活动通知等都将发送到该邮箱</span> -->
						<%-- 屏蔽escience邮箱注册链接
						  &nbsp&nbsp<span><a id="userEscienceEmail" href="<vwb:Link context='escienceRegist' format='url'/>">使用escience邮箱</a></span> 
						--%>
					</c:otherwise>
				</c:choose>
				</td>
				<td class="errorContainer" width="120"><label class='error'>
				<form:errors path="uid" cssClass="error" class="error" /></label></td>
			</tr>
			<tr><th>真实姓名</th>
				<td class="ui-text-alert">*</td>
				<td><form:input type="text" path="name" value=""/></td>
				<td class="errorContainer"><label class='error'>
				<form:errors path="name" cssClass="error" class="error" /></label></td>
			</tr>
			<tr><th>密码</th>
				<td class="ui-text-alert">*</td>
				<td><form:input type="password" path="password" value=""/>
					<br/><span class="ui-text-note">为保证安全，请使用长度6位以上的密码</span>
				</td>
				<td class="errorContainer"><label class='error'><form:errors path="password" cssClass="error" class="error" /></label> </td>
			</tr>
			<tr><th>确认密码</th>
				<td class="ui-text-alert">*</td>
				<td><form:input type="password" path="passwordAgain" value=""/></td>
				<td class="errorContainer"></td>
			</tr>
			<tr><th>验证码</th>
				<td class="ui-text-alert">*</td>
				<td><input type=text name="checkCode" value=""/></td>
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
				<td><img id="checkCodePiceture" style="width:80px; height:40px;" src="<vwb:Link context='pictrueCheckCode' format='url'/>?func=getImage&type=registType">
				<a href="#" onclick="refreshCode()">换一张</a>
				</td>			
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td></td>
				<td></td>
				<td style="padding-top:30px;vertical-align:bottom;padding-bottom:0.8em;">
					<input type="submit" value="注册" class="btn btn-primary btn-large"/>
					<a class="dim btn btn-link" href="/">取消</a> 
				</td>
				<!-- <td style="padding-top:25px; vertical-align:bottom;">
					其它账号注册：
					<a href="http://passport.escience.cn/weiboLoginServlet"><img alt="用新浪微博注册" src="http://passport.escience.cn/images/login/sina.gif"></a>
				</td> -->
				
			</tr>
		</table>
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
	
	<div id="content-side" class="cst-rightside" style="width:347px">
		<p class="header">
			已有中国科技网通行证？
			<br></br>
			<c:choose>
				<c:when test="${not empty umtLoginURL }">
					<a href="${umtLoginURL }" style="text-decoration:none">
						 <span class="btn btn-success">
					 	 立即登录
						 </span>
					 </a>
				</c:when>
				<c:otherwise>
					<a href="${requestScheme}://passport.escience.cn/login">
						 <span class="btn btn-success">
					 	 立即登录
						 </span>
					 </a>
				</c:otherwise>
			</c:choose>
		</p>
		<p class="sub-header">
			第三方账号登录：
			<a href="${requestScheme}://passport.escience.cn/thirdParty/login?type=weibo&WebServerURL=http%3A%2F%2Fddl.escience.cn%2F"><img src="${requestScheme}://passport.escience.cn/images/login/weibo.png" alt="用新浪微博登录" /></a>
		</p>
		<h3 class="cst-define">什么是中国科技网通行证？</h3>
		<p class="sub-gray-text">中国科技网通行证是基于中国科技网的统一账号系统，可以用于登录各类科研应用服务，包括：
		<a href="${requestScheme}://www.escience.cn" target="_blank">科研在线</a>、
		<a href="${requestScheme}://ddl.escience.cn" target="_blank">文档库</a>、
		<a href="http://csp.escience.cn" target="_blank">国际会议服务平台</a>、
		<a href="${requestScheme}://www.escience.cn/people" target="_blank">科研主页</a>、
		<a href="${requestScheme}://mail.escience.cn" target="_blank">中科院邮件系统</a>等，以及今后将逐步扩展的更多应用服务。</p>
		<p><strong>原<span class="duckling-logo"></span>Duckling通行证升级为中国科技网通行证。</strong></p>
		<p><strong>中科院邮件系统账号可作为中国科技网通行证账号直接
			<c:choose>
				<c:when test="${not empty umtLoginURL }">
					<a href="${umtLoginURL }">登录</a> 
				</c:when>
				<c:otherwise>
					<a href="${requestScheme}://passport.escience.cn/login">登录</a> 
				</c:otherwise>
			</c:choose>
		 	。</strong></p>
	</div>
	
	<div class="ui-clear"></div>
</div>
<div class="ui-dialog" id="existerUser">
		<span class="ui-dialog-x"></span>
		<p class="ui-dialog-title">
			
		</p>
		<div class="ui-dialog-body">
			<p id="interest-tips">
				
			</p>
		</div>
		<div class="ui-dialog-control">
			<input style="display:none" type="button" value="确定" class="ui-dialog-close refresh-button" />
		</div>
	</div>
