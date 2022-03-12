<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<script type="text/javascript"
	src="${contextPath}/jsp/aone/js/fileuploader.js?v=${aoneVersion}"></script>
<link href="${contextPath}/jsp/aone/css/fileuploader.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/tokenInput.css" type="text/css" />
<script type="text/javascript" src="${contextPath}/jsp/aone/js/toker-jQuery.js?v=${aoneVersion}"></script>

<script type="text/javascript">
$(document).ready(function(){
	var upload_url = "<vwb:Link context='shareFile' format='url'/>?func=uploadTempFiles";
	
	function createUploader(){  
         var uploader = new qq.FileUploader({
             element: document.getElementById('file-uploader-demo1'),
             action: upload_url,
             onComplete:function(id, fileName, data){
             	$("#files-hidden-area").append("<input type='hidden' name='fid' value='"+data.fid+"'/>");
             	$("#files-hidden-area").append("<input type='hidden' name='clbId' value='"+data.clbId+"'/>");
             	$("#files-hidden-area").append("<input type='hidden' name='fileName' value='"+data.fileName+"'/>");
             	$("#files-hidden-area").append("<input type='hidden' name='size' value='"+data.size+"'/>");
             	$("#share-file-submit-button").attr("disabled",false);
             },
             debug: true
         });           
     };
     
     createUploader();
     	
     jQuery.validator.addMethod("multiemail", function(value, element) {
         if (this.optional(element))
             return true;
         var emails = value.split( new RegExp( "\\s*,\\s*", "gi" ) );
         valid = true;
         for(var i in emails) {
             value = emails[i];
             valid=valid && jQuery.validator.methods.email.call(this, value,element);
         }
         return valid;
     }, "请输入有效的邮箱地址,并用英文逗号分隔");
	
	$("input[name='newRegister']").blur(function(){
		var url = "<vwb:Link context='shareFile' format='url'/>?func=isExistRegister";
		var params = "newRegister="+$(this).val();
		ajaxRequest(url,params,afterValidateUser);
	});
	
	function afterValidateUser(data){
		if(data.result){
			$("input[name='isFirst']").attr("value",false);
			$("#password-input-row").show();
			$("input[name='password']").focus();
		}else{
			$("input[name='isFirst']").attr("value",true);
			$("#password-input-row").hide();
		}
	};
	
	$("input[name='password']").change(function(){
		var url = "<vwb:Link context='shareFile' format='url'/>?func=isPasswordCorrect";
		var params = "newRegister="+$("input[name='newRegister']").val()+"&password="+$(this).val();
		ajaxRequest(url,params,afterValidatePassword);
	});
	
	function afterValidatePassword(data){
		if(data.result){
			$("input[name='password']").parent().next().html("");
			$("input[name='name']").attr("value",data.message);
			$("#share-file-submit-button").attr("disabled",false);
		}else{
			$("input[name='password']").parent().next().html("密码填写错误！");
			$("input[name='name']").attr("value","");
			$("#share-file-submit-button").attr("disabled",true);
		}
	};
     
    var loginURL = "<vwb:Link context='shareFile' format='url'/>?func=shareAgain";
    var currentValidator = $("#share-files-form").validate({
		rules: {
			newRegister: {required: true,email: true},
			targetEmails: {required: true,multiemail: true},
			name:{required:true}
		},
		messages:{
			newRegister:{required:"请输入邮箱",email:"请输入有效的邮箱地址"},
			targetEmails:{required:"请输入邮箱",multiemail:"请输入有效的邮箱地址,并用英文逗号分隔"},
			name:{required:"请输入您的姓名"}
		},
		errorPlacement: function(error, element){
			error.appendTo(element.parent().next(".errorContainer"));
		}
	});
     
	$('input[name="targetEmails"]').tokenInput2("<vwb:Link context='contacts' format='url'/>?func=searchUser", {
		theme:"facebook",
		hintText: "请输入邮箱地址，以逗号 分号 空格或回车确认",
		searchingText: "",
		noResultsText: "没有该用户信息，输入逗号 分号 空格或回车添加",
		preventDuplicates: true
	});
});
</script>

<div class="toolHolder control">
	<h1>快速分享文件 &bull; 绿色通道</h1>
</div>
<vwb:CLBCanUse hasMessage="true">
<div class="content-through">
	<form id="share-files-form" method="POST" action="<vwb:Link context='shareFile' format='url'/>">
	<div class="shareHolder" style="margin-bottom:50px">
		<h2>无需注册，简单三步轻松完成文件分享</h2>
		<div id="files-hidden-area">
			<input type="hidden" name="func" value="submitShare" />
			<input type="hidden" name="isFirst" value="true" />
		</div>
		<hr/>
		<h3>第一步 <span class=”small”>选择上传文件</span></h3>
		<div id="files" >
			<div id="file-uploader-demo1" style="margin-left:0">
				<div class="qq-uploader">
					<div class="qq-upload-button">
						上传文件 <input type="file" multiple="multiple" name="files">
					</div>
					<ul class="qq-upload-list fileList"></ul>
				</div>
			</div>
		</div>
		<label style="font-size:14px;">有效时间：</label>
		<select name="validOfDays">
			<option value="30">30天</option>
			<option value="15">15天</option>
			<option value="7">7天</option>
			<option value="3">3天</option>
		</select>
		<hr/>
		<h3>第二步 <span class=”small”>填写您想分享的好友信息</span></h3>
		<table style="font-size:14px;">
			<tr>
				<td style=" vertical-align:top">好友邮箱：</td>
				<td>
					<input type="text" name="targetEmails" class="ui-textInput-xLong" />
					<span class="ui-text-note">多个邮箱可以用逗号 分号 空格或回车分割</span>
				</td>
				<td class="errorContainer"></td>
			</tr>
		</table>
		<hr/>		
		<h3>第三步 <span class=”small”>填写您的信息</span> </h3>
		<table class="shareFileFrom">
			<tr>
				<td style="vertical-align:top">您的邮箱:</td>
				<td>
					<input type="text" name="newRegister" id="first-input-email" value="" /><br/>
					<span class="ui-text-note">输入您的邮箱和姓名，便于您的朋友们认出您来</span>
				</td>
				<td class="errorContainer"></td>
			</tr>
			<tr id="password-input-row" style="display:none">
				<td>您的密码:</td>
				<td>
					<input type="password" name="password"  value=""/><br/>
					<span class="ui-text-note">您输入的邮箱已经注册过,请输入密码</span>
				</td>
				<td></td>
			</tr>
			<tr>
				<td>您的姓名：</td>
				<td><input type="text" name="name" value="" /></td>
				<td class="errorContainer"></td>
			</tr>
			<tr>
				<td>您的留言:</td>
				<td><textarea name="message"></textarea></td>
				<td class="errorContainer"></td>
			</tr>
			<tr>
				<td></td>
				<td class="">
					<input type="submit" class="btn btn-success" name="submitShare" id="share-file-submit-button" value="分享文件" /> 
					<a href="/index.jsp" class="btn btn-link">取消</a>
				</td>
				<td></td>
			</tr>
		</table>
		</div>
		<!-- <div class="procedureHolder holderMerge"></div> -->
	</form>
</div>
</vwb:CLBCanUse>
