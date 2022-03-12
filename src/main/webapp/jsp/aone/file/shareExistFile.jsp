<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<script type="text/javascript"
	src="${contextPath}/jsp/aone/js/fileuploader.js?v=${aoneVersion}"></script>
<link href="${contextPath}/jsp/aone/css/fileuploader.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="${contextPath}/scripts/jquery/jquery-ui-1.8.16.custom.min.js"></script>
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/tokenInput.css" type="text/css" />
<script type="text/javascript" src="${contextPath}/jsp/aone/js/toker-jQuery.js?v=${aoneVersion}"></script>

<script type="text/javascript">
$(document).ready(function(){
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
    
    function split( val ) {
		return val.split( /,\s*/ );
	};
	
	function extractLast( term ) {
		return split( term ).pop();
	};
    $( "#targetEmails" ).autocomplete({
		minLength: 0,
		source: function( request, response ) {
			var search_user_url = "<vwb:Link context='contacts' format='url'/>?func=searchUser";
			if(request.term!=""){
				$.ajax({
						url: search_user_url,
						dataType: "json",
						type: "POST",
						data: {
							pinyin: extractLast( request.term )
						},
						success: response,
						statusCode:{
							450:function(){alert('会话已过期,请重新登录');},
							403:function(){alert('您没有权限进行该操作');}
						}
				});
			}
		},
		focus: function( event, ui ) {
			//$( "#project" ).val( ui.item.label );
			return false;
		},
		select: function( event, ui ) {
			var terms = split( this.value );
			// remove the current input
			terms.pop();
			// add the selected item
			terms.push( ui.item.name );
			// add placeholder to get the comma-and-space at the end
			terms.push( "" );
			this.value = terms.join( ", " );
			var email = $("#targetEmailValues").attr("value");
			if(email == "") {
				email = ui.item.email;
			}
			else {
				email = email + "," + ui.item.email;
			}
			$("#targetEmailValues").attr("value", email);
			return false;
		},
		delay:500
	})
	.data( "autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li></li>" )
			.data( "item.autocomplete", item )
			.append( "<a>" + item.name+"("+item.email+")" +"</a>" )
			.appendTo( ul );
	};
    
    $('input[name="targetEmails"]').tokenInput("<vwb:Link context='contacts' format='url'/>?func=searchUser", {
		theme:"facebook",
		searchingText: "",
		noResultsText: "",
		preventDuplicates: true
	});
     
});
</script>

<div class="toolHolder control">
	<h1>分享文件 </h1>
</div>

<div class="content-through">
	<form id="share-files-form" method="POST" action="<vwb:Link context='file' page='${fileVersion.fid}' format='url'/>">
	<div class="shareHolder">
		<div id="files-hidden-area">
			<input type="hidden" name="func" value="submitShareExistFile" />
			<input type="hidden" name="fid" value="${fileVersion.fid}"/>
			<input type="hidden" name="fileName" value="${fileVersion.title}"/>
			<input type="hidden" name="clbId" value="${file.clbId}"/>
		</div>
		<h3>第一步 <span class=”small”>选择要分享的时长</span></h3>
		${fileVersion.title}<br/>
		<label>有效时间</label>
		<select name="validOfDays">
			<option value="30">30天</option>
			<option value="15">15天</option>
			<option value="7">7天</option>
			<option value="3">3天</option>
		</select>
		<hr/>
		<h3>第二步 <span class=”small”>填写您想分享的好友信息</span></h3>
		<table>
			<tr>
				<td style="vertical-align:top">好友邮箱：</td>
				<td>
					<input type="text" id="targetEmails" name="targetEmails" class="ui-textInput-xLong" /><br/>
					<span class="ui-text-note">可以输入多个邮箱</span>
					<input type="hidden" id="targetEmailValues" name="targetEmailValues" value=""/>
				</td>
				<td class="errorContainer"></td>
			</tr>
		</table>
		<hr/>		
		<h3>第三步 <span class=”small”>填写您的信息</span> </h3>
		<table>
			<tr>
				<td  style="vertical-align:top">您的留言：</td>
				<td><textarea name="message"></textarea></td>
				<td class="errorContainer"></td>
			</tr>
			<tr>
				<td></td>
				<td class="largeButtonHolder">
					<input type="submit" name="submitShare" value="分享文件" /> 
					<a href="<vwb:Link context='file' format='url' page='${fileVersion.fid}'/>">取消</a>
					<br/>&nbsp;
				</td>
				<td></td>
			</tr>
		</table>
		</div>
		<div class="procedureHolder holderMerge"></div>
	</form>
</div>

