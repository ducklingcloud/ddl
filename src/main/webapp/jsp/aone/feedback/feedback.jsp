<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:setBundle basename="templates.default" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/uiLib.css" type="text/css" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/aone.css" type="text/css" />
<script type="text/javascript" src="${contextPath}/jsp/aone/js/uiLib-jQuery.js"></script>
<script type="text/javascript" src="${contextPath}/jsp/aone/js/global.js"></script>
<script type="text/javascript">
/*
$(document).ready(function(){
	
	var contact = $('#send-feed-back input[name="email"]');
	var standbyText = '电子邮箱或其他';
	contact.val(standbyText);
	contact.focus(function(){
		if ($.trim(contact.val())==standbyText || contact.hasClass('standby')) {
			contact.removeClass('standby').val('');
		}
	})
	.blur(function(){
		if ($.trim(contact.val())=='') {
			contact.addClass('standby').val(standbyText);
		}
	});


	var txt = $('textarea[name="message"]');
	$('#send').click(function(){
		if ($.trim(txt.val())!='') {
			if ($.trim(contact.val())=='' || $.trim(contact.val())==standbyText) {
				contact.val('');
			}
			$('#send-feed-back').submit();
		}
		else {
			ui_spotLight('alert-spotlight', 'fail', '请填写意见后再提交');
			txt.val($.trim(txt.val())).focus();
			
			var color = 200;
			txt.css('background-color', 'rgb(255,' + color + ',' + color + ')');
			var colorFade = setInterval(function(){
				color ++;
				txt.css('background-color', 'rgb(255,' + color + ',' + color + ')');
				if (color>=255) {
					clearInterval(colorFade);
				}
			}, 15);
		}
	});
	txt.keydown(function(){ $('#alert-spotlight').fadeOut(); });
	
	var shortcutkey = function(KEY){
		if (KEY.which=='13' && KEY.ctrlKey) {
			$('#send').click();
		}
	}
	contact.keyup(function(KEY){ shortcutkey(KEY); });
	txt.keyup(function(KEY){ shortcutkey(KEY); });
	
});*/
</script>
<style>
#getFeedback { display:none; }
#send-feed-back input[name="email"] {
	width:400px; font-size:14pt;
	padding:0.5em;
	line-height:2em;
	font-family: helvetica, arial, '微软雅黑', sans-serif;
}
#send-feed-back input.standby { color:#999; }
#send-feed-back textarea { width:400px; height:200px; font-size:12pt; padding:0.5em; }
.service-phone {
	margin-right:1em; font-size:16px; float:right; margin-top:5px; font-weight:200;
	font-family:Arial,"微软雅黑"; text-shadow:2px 2px 2px #fff;
}

</style>
<div class="toolHolder control">
	<h1>意见反馈 <span class="service-phone">服务热线：010-58812312</span></h1>
</div>

<div class="content-through" style="font-size:12pt;">
	<form id="send-feed-back" method="POST" action="<vwb:Link context='feedback' format='url'/>">
		<input type="hidden" name="func" value="submit"/>
		<div class="shareHolder">
		<table class="ui-table-form">
		<tbody>
			<!--  <tr><th>意见：<span class="ui-text-alert">*</span>&nbsp;</th>
				<td><textarea name="message"></textarea></td>
			</tr>
			<tr><th>联系方式：</th>
				<td><input type="text" name="email" value="" class="standby" /></td>
			</tr>
			<tr><th></th>
				<td><input type="button" value="发送意见" class="btn btn-success" id="send" />
					<span class="ui-spotLight" id="alert-spotlight"></span>
				</td>
			</tr>
			-->
			<tr><th></th>
				<td>
				<p>您还可以通过以下方式联系我们</p>
				<p>邮件咨询：vlab@cnic.cn</p> 
				<p>客服热线：010-58812312</p>
				<p>新浪微博：http://e.weibo.com/dcloud</p>
                <p>腾讯微博：http://t.qq.com/keyanzaixian</p>
                <p>QQ咨询：1072762843</p>
				</td>
			</tr>
		</tbody>
		</table>
		
		</div>
	</form>
</div>
