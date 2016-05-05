<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<script type="text/javascript" src="${contextPath}/jsp/aone/js/fileuploader.js?v=${aoneVersion}"></script>
<link href="${contextPath}/jsp/aone/css/fileuploader.css" rel="stylesheet" type="text/css">	
<script type="text/javascript">
$(document).ready(function(){
	$(".validateCode").live("click",function(){
		var code = $("input[name='fetchFileCode']").val();
		if(code==null){
			
		}
		$("#validateCodeError").hide();
		var dd = new Object();
		dd.code=code;
		dd.url="${downloadURL}";
		$.ajax({
			url:"/direct/validateCode",
			data:dd,
			type:"POST",
			dataType:"json", 
			success:function(data){
				if(data.status){
					$("#downloadh").show();
					var downloadURL = $("#downloadURL").attr("href")+"?validateCode="+code;
					$("#downloadURL").attr("href",downloadURL);
					$("#showInputCode").hide();
				}else{
					$("#validateCodeError").html("提取码不正确");
					$("#validateCodeError").show();
				}
			}
		});
	});
	
	$("#fetchFileCodeId").live("keypress",function(even){
		var key = even.which;
		if(key==13){
			$(".validateCode").trigger("click");
		}
	});
     
});
</script>
<div id="content-title">
	<h1>文件分享</h1>
</div>
<div class="content-through">
	<div class="msgBox light" style="width:50%">

		<h3>${fileVersion.title}</h3>
		<p class="ui-text-note">分享者：${fileOwnerName}</p>
		<p class="ui-text-note">分享时间：${shareCreateTime}</p>
		<p class="ui-text-note">文件大小：${fileSize}</p>
		<c:choose>
			<c:when test="${status eq 'removed' }">
				<h2>对不起,该文件已经被删除!</h2>
			</c:when>
			<c:otherwise>
				<p class="ui-text-note">有效期：${validOfDays}天</p>
				<p class="ui-text-note">剩余时间：${restOfDays}</p>
				<c:choose>
					<c:when test="${!empty validateCode }">
						<div id="showInputCode" style="margin-top:1em;">
							<p>请输入提取码提取文件</p>
							<p><input type="text" name="fetchFileCode" id="fetchFileCodeId"> <a class="validateCode largeButton">提取</a></p>
							<p><label class="error" id="validateCodeError"></label></p>
						</div>
						<h1 style="display:none" id="downloadh">
							<a id="downloadURL" class="largeButton green" href="<vwb:Link context='direct' page='${downloadURL}' format='url'/>/download">下载</a>
							<c:if test="${not isMySelf}">
								<a class="largeButton green" href="<vwb:Link context='copyfile' page='${downloadURL}' format='url'/>">复制到我的空间</a>
							</c:if>
						</h1>
					</c:when>
					<c:otherwise>
						<h1>
							<a class="largeButton green" href="<vwb:Link context='direct' page='${downloadURL}' format='url'/>/download">下载</a>
							<c:if test="${not isMySelf}">
								<a class="largeButton green" href="<vwb:Link context='copyfile' page='${downloadURL}' format='url'/>">复制到我的空间</a>
							</c:if>
						</h1>
						
					</c:otherwise>
				</c:choose>
				<!-- 
				<p>该文件存放在${fileOwnerName}的空间下，想拥有自己的空间？马上注册吧！</p>
				 -->
			</c:otherwise>
		</c:choose>
	</div>
	<div class="procedureHolder largeButtonHolder holderCenter">
		<p>
			<c:if test="${not isLogin }">
				<a class="largeButton" href="https://ddl.escience.cn/system/regist/">注册</a>
			</c:if>
			<a class="largeButton dim" href="http://support.ddl.escience.cn/">了解科研在线</a>
		</p>
		<h3>加入科研在线，享受更多便捷服务！</h3>
	</div>
</div>