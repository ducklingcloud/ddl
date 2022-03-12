<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/uiLib.css" type="text/css" />
<script type="text/javascript">
$(document).ready(function(){
	$('#submit').click(function(){
		$("#add-contact-form").validate({
			rules: {name: {required: true}},
			messages:{name:{required:"请输入联系人名称"}}
		});
		var requestURL="<vwb:Link context='contacts' format='url'/>?func=addContactSubmit";
		ajaxRequest(requestURL, $("#add-contact-form").serialize(), showResult);
	});
	$('#reset').click(reset);
});
function showResult(data) {
	if(data.result == "success") {
		alert("保存成功");
		reset();
	}
	else {
		alert("操作失败");
	}
}
function reset() {
	$('#contact-info input').attr("value", "");
}
</script>
<div id="content-title">
	<h1>添加联系人</h1>
	<div style="float:right"><a href="<vwb:Link context='contacts' format='url'/>">返回</a></div>
</div>
<div>
	<form id="add-contact-form" method="post">
		<table id="contact-info">
			<tr><td>姓名：</td><td><input id="name" name="name" type="text" /></td></tr>
			<tr><td>性别：</td><td><input id="sex" name="sex" type="text" /></td></tr>
			<tr><td>邮箱：</td><td><input id="mainEmail" name="mainEmail" type="text" /></td></tr>
			<tr><td>第二邮箱：</td><td><input id="optionEmail" name="optionEmail" type="text" /></td></tr>
			<tr><td>手机：</td><td><input id="mobile" name="mobile" type="text" /></td></tr>
			<tr><td>电话：</td><td><input id="telephone" name="telephone" type="text" /></td></tr>
			<tr><td>组织：</td><td><input id="orgnization" name="orgnization" type="text" /></td></tr>
			<tr><td>部门：</td><td><input id="department" name="department" type="text" /></td></tr>
			<tr><td>QQ：</td><td><input id="qq" name="qq" type="text" /></td></tr>
			<tr><td>微博：</td><td><input id="weibo" name="weibo" type="text" /></td></tr>
			<tr><td>地址：</td><td><input id="address" name="address" type="text" /></td></tr>
			<tr><td>拼音：</td><td><input id="pinyin" name="pinyin" type="text" /></td></tr>
		</table>
	</form>
	<button id="submit">提交</button>
	<button id="reset">重置</button>
</div>
