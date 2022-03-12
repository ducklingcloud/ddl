<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<script type="text/javascript" src="${contextPath}/scripts/jquery/jquery.validate.js"></script>
<script type="text/javascript">
$(document).ready(function(){
	var hash = (window.location.hash)?window.location.hash:'';
	if (hash!='') {
		switchView(hash.substring(1));
	}
	else {
		switchView();
	}
	
	function switchView(VIEW) {
		$('#feedSelector ul.filter > li').removeClass('chosen');
		$('.content-menu-body').fadeOut();
		var view = VIEW;
		
		switch (VIEW) {
			case 'update':
				$('#edit-user-info').fadeIn();
				break;
			case 'view':
				$('#view-user-info').fadeIn();
			default:
				view = 'view';
				$('#'+view+'-user-info').fadeIn();
		}
		$('#feedSelector ul.filter a[view="' + view + '"]').parent().addClass('chosen');
	};
	
	$('#feedSelector ul.filter a').click(function(){
		switchView($(this).attr('view'));
	});
	
	$("#cancelProfileChange").click(function(){
		window.location.hash = '#view';
		window.location.reload();
	});
	
	
	$.validator.addMethod("isValidateCode", function(value, element) {   
		var reg = /[:\\\/<>*?|"]/;
		return !reg.test(value);
	}, "输入中不能出现：:?\\ /*<>|\"");
	$("#userExtForm").validate({
		rules :{
			name:{required:true,isValidateCode:true},
			orgnization:{isValidateCode:true},
			department:{isValidateCode:true},
			address:{isValidateCode:true},
			telephone:{isValidateCode:true},
			email:{isValidateCode:true,required:true,email:true},
			mobile:{isValidateCode:true},
			qq:{isValidateCode:true},
			weibo:{isValidateCode:true}
		},
		messages:{
			name:{required:"姓名不能为空"},
			email:{required:"邮箱不能为空",email:"邮箱格式不正确"}
		},
		errorPlacement:function(error,element){
			$(element).addClass('error');
			$(element).parent().parent().find('.errorContainer').html("<label for='name' class='error'>"+error.html()+"</label>");
		},
		success:function(element){
			$(element).parent().parent().find('.errorContainer').html("");
		},
		submitHandler:function(form){
			var url = "<vwb:Link context='user'  page='${user.id}' format='url'/>";
			var params = $("#userExtForm").serialize();
			ajaxRequest(url,params,afterSaveProfileHandler);
		}
		
	});
	
	
	
	$('input[name="name"]').blur(function(){
		if ($(this).val().trim()=='') {
			$(this).addClass('error');
			$('label[for="name"]').show();
		}
		else {
			$(this).removeClass('error');
			$('label[for="name"]').hide();
		}
	});
	$('input[name="email"]').blur(function(){
		if ($(this).val().trim()=='') {
			$(this).addClass('error');
			$('label[for="email"]').show();
		}
		else {
			$(this).removeClass('error');
			$('label[for="email"]').hide();
		}
		if ($(this).val().trim()!=''&&!validateEmail($('input[name="email"]').val().trim())) {
				$('input[name="email"]').addClass('error').focus();
				$('label[for="notEmail"]').show();
			}
			else{			
				$('label[for="notEmail"]').hide();
				$(this).removeClass('error');
		}	
		
	});
	
	function afterSaveProfileHandler(data){
		if(data&&data.success!=undefined&&data.success==false){
			alert(data.message);
			return;
		}
		ui_spotLight('saveProfile-spotLight', 'success', '保存成功', 'fade');
		setTimeout(function(){
			window.location.hash = '#view';
			window.location.reload();
		}, 1500);
	};
	function validateEmail(value) {
		// contributed by Scott Gonzalez: http://projects.scottsplayground.com/email_address_validation/
		return /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i.test(value);
	};

});
</script>
<div id="feedSelector" class="filterHolder">
	<ul class="filter">
		<li class="chosen"><a view="view" href="#view">查看资料</a></li>
		<li><a view="update" href="#update">编辑资料</a></li>
		<li><a href="${changePasswordURL}" target="_blank">修改密码</a></li>
	</ul>
</div>

<div id="view-user-info" class="content-menu-body" style="display:none">
	<input type="hidden" name="uid" value="${user.uid}"/>
	<div class="profile-page">
			<h2 class="name">${user.name}
				<span class="head${user.sex}"></span>
				<p class="ui-text-note">${user.uid}</p>		
			</h2>
				
		</div>
	<table id="profileTable" class="ui-table-form">

		<tr class="titleRow">
			<th colspan="4" width="520">基本信息</th>
		</tr>
		<tr>
			<th width="42">单位：</th>
			<td colspan="3">${user.orgnization}</td>
		</tr>
		<tr>
			<th >部门：</th>
			<td colspan="3">${user.department}</td>
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
			<td width="200">${user.telephone}</td>
			<th width="60">手机：</th>
			<td>${user.mobile}</td>
		</tr>
		<tr class="titleRow"><td colspan="4"></td></tr>
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
	<div class="bedrock"></div>
</div>

<div id="edit-user-info" class="content-menu-body" style="display:none;">
	<form id="userExtForm" method="POST" action="<vwb:Link context='user'  page='${user.id}' format='url'/>">
		<input type="hidden" name="func" value="updateProfile"/>
		<input type="hidden" name="uid" value="${user.uid}"/>
		
		<table class="ui-table-form" id="editProfileTable">
			<tr class="titleRow">
				<th colspan="4">基本资料</th>
			</tr>
			<tr>
				<th>姓名：</th>
				<td width="10" class="ui-text-alert">*</td>
				<td><input type="text" name="name" id="f_name" value="${user.name}"/>
					<label for="name" class="error" style="display:none">姓名不能为空</label>
				</td>
				<td width="180" class="errorContainer"></td>
			</tr>
			<tr><th>账号：</th>
				<td></td>
				<td>${user.uid}</td>
				<td></td>
			</tr>
			<tr>
				<th>单位：</th>
				<td></td>
				<td><input type="text" name="orgnization" id="f_orgnization" value="${user.orgnization}" class="ui-textInput-long" /></td>
				<td width="180" class="errorContainer"></td>
			</tr>
			<tr>
				<th>部门：</th>
				<td></td>
				<td><input type="text" name="department" id="f_department" value="${user.department}"/></td>
				<td  class="errorContainer"></td>
			</tr>
			<tr>
				<th>地址：</th>
				<td></td>
				<td><textarea name="address" id="f_address">${user.address}</textarea></td>
				<td  class="errorContainer"></td>
			</tr>
			
			<tr class="titleRow">
				<th colspan="4">联系方式</th>
			</tr>
			<tr>
				<th>邮箱：</th>
				<td width="10" class="ui-text-alert">*</td>
				<td><input type="text" name="email" id="f_email" maxlength="45" value="${user.email}"/>
				<font class="ui-text-note">用于接收分享@消息和每日动态邮件</font>
				<label for="email" class="error" style="display:none">邮箱不能为空</label>
				<label for="notEmail" class="error" style="display:none">请输入有效的邮箱地址</label>
				</td>
				<td class="errorContainer"></td>
			</tr>
			<tr>
				<th>电话：</th>
				<td></td>
				<td><input type="text" name="telephone" id="f_telephone" value="${user.telephone}"/></td>
				<td class="errorContainer"></td>
			</tr>
			<tr>
				<th>手机：</th>
				<td></td>
				<td><input type="text" name="mobile" id="f_mobile" value="${user.mobile}"/></td>
				<td class="errorContainer"></td>
			</tr>
			<tr class="titleRow"><td colspan="4"></td></tr>
			
			<tr>
				<th>QQ：</th>
				<td></td>
				<td><input type="text" name="qq" id="f_qq" value="${user.qq}"/></td>
				<td class="errorContainer"></td>
			</tr>
			<tr>
				<th>微博：</th>
				<td></td>
				<td><input type="text" name="weibo" value="${user.weibo}"/></td>
				<td class="errorContainer"></td>
			</tr>
			<tr class="titleRow"><td colspan="4"></td></tr>
			<tr><th></th>
				<td></td>
				<td class="largeButtonHolder">
					<input type="submit" id="saveProfileButton" class="largeButton small but-color-commom" value="保存"/>
					<a id="cancelProfileChange" class="largeButton small but-color-cannel">取消</a>
<%-- 					<a href="<vwb:Link context='user' page='${user.id}' format='url'/>" class="largeButton dim">取消</a> --%>
					<span class="ui-spotLight" id="saveProfile-spotLight"></span>
				</td>
				<td></td>
			</tr>
		</table>
	</form>
	<div class="bedrock"></div>
</div>
<div class="ui-clear"></div>


