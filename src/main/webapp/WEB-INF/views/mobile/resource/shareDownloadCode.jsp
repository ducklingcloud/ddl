<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/lion.css?v=${aoneVersion}" type="text/css" />
<fmt:setBundle basename="templates.default" />

<div id="macroNav" class="simpleMobileNav">
	<a id="logo" title="科研在线-团队文档库"></a>
</div>

<div id="content-major" class="mobileContent" style="padding:1em">
	<c:choose>
		<c:when test="${type=='shareNull'or type=='resourceDelete' }">
				<h4>对不起，分享的文件已被取消~</h4><br/>
				<p>引起这样结果的原因可能是：</p>
				<p>1. 分享者已取消此分享，或删除了分享文件</p>
				<p>2. 该分享含违规内容，被群众举报或审核删除</p>
		
		</c:when>
		<c:when test="${type=='noAuth' }">
			<h3 style="margin:0; width:95%">${shareUserName}给你加密分享了文件</h3> 
			<form class="form-horizontal" id="codeForm">
			  <div class="js-code-msg">
			      <label style="color:#888; margin:20px 0">请输入提取码：</label>
			      <input type="text" class="input-large" name="code" id="code" style="padding:8px 0; width:80%; font-size:14px;">
			      <input type="button" id="codeBtn" class="btn btn-primary btn-large" style="width:80%;margin-top:1em;" value="提取文件" />
			      <p style="color:red;margin:10px 0;display:none;">提取码错误.</p>
			  </div>
			</form>
			<div id="opareteFileMessage"  class="alert alert-success" style="margin:8px 0; position:static; display:none;"></div>
		</c:when>
	</c:choose>
	

</div>

<div class="bedrock"></div>


<script type="text/javascript">

<%--
/**
    显示消息
 * @param type success-成功,block-警告,error-错误
 */
 --%>
function showMsg(msg, type){
	type = type || "success";
	$("#opareteFileMessage").removeClass().addClass("alert alert-" + type).html(msg).show(150);
}
function hideMsg(timeout){
	timeout = timeout || 2000;
	window.setTimeout(function(){$("#opareteFileMessage").hide(150);}, timeout);
}
function showMsgAndAutoHide(msg, type,time){
	time=time||2000;
	showMsg(msg,type);
	hideMsg(time);
}

function codeSubmit(){
	if($.trim($("#code").val()) == "") return;
	var params = {  "code": $("#code").val() };
	$.ajax({
	   url: "?func=checkcode",
	   data: params,type: "POST",dataType: "json", timeout:5000,
	   success: function(resp){
		  if(resp.result){
			  window.location.reload();
		  }else{
			  $(".js-code-msg p").show();
		  }
	   },
	   error:function(){
		   showMsg("操作失败,请稍候再试。","error");
		   hideMsg(5000);
	   }
	});
}
$(function(){
	$("#codeBtn").click(function(){
		codeSubmit();
	});
	$("#code").keypress(function(e){
		if (e.keyCode == 13) {  
            codeSubmit();
            e.preventDefault();
            return false;
        }  
	});
	$("#code").change(function(){$(".js-code-msg p").hide(); });
});



</script>
<script src="${contextPath}/scripts/bootstrap/js/bootstrap.min.js"></script>
