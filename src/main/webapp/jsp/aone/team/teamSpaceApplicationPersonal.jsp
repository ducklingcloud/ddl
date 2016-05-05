<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

<link href="${contextPath}/jsp/aone/css/error.css" rel="stylesheet" type="text/css"/>
<link href="${contextPath}/scripts/bootstrap/css/bootstrap.min.css" rel="stylesheet" media="screen"/>
<link href="${contextPath}/scripts/bootstrap/css/bootstrap-responsive.min.css" rel="stylesheet"/>
<link href="${contextPath}/scripts/bootstrap/css/todc-bootstrap.css" rel="stylesheet"/>	
	<div class= "manageSpaceMain">
			<div id="opareteFileMessage" class="alert alert-success" style="display: none;z-index:9999"></div>
			<h3 style="margin-bottom:0">${team.displayName }</h3>		
		<p>当前总空间为<span id="totalSize">${size.totalDisplay}</span>，已使用<span id="usedSize">${size.usedDisplay }</span></p>
		<div class="enlargeSpaceTool">
		<c:choose>
			<c:when test="${canApply }">
				<button  id="application" class="btn btn-primary" style="cursor:pointer;">立即扩容</button>
			</c:when>
			<c:otherwise>
				<button  id="application" class="btn btn-primary disabled" disabled="disabled" style="cursor:pointer;">立即扩容</button><span>可使用空间未少于5GB</span>
			</c:otherwise>
		</c:choose>
		</div>
		<p>个人空间初始化10GB，空间不足5GB时，可手动扩容。</p>
		<p>个人空间扩容完全免费，每次扩容，增加10GB。</p>
		<div class="enlargeSpaceHistory">扩容记录</div>
		<p>以下是您免费扩容的记录 合计：<span id="totalAddSize">${totalSize }</span></p>
		<div class="historyList">
		<table class="table dataTable">
			<thead>
				<tr>
					<td>类别</td>
					<td>大小</td>
					<td>日期</td>
				</tr>
			</thead>
			<tbody id="resordList">
				<c:forEach items="${records}" var="re">
					<tr>
						<td>${re['type']} </td>
						<td>${re['size'] }</td>
						<td>${re['time'] }</td>
					</tr>
				</c:forEach>
			</tbody>		
		
		</table>
		</div>
	</div>
<script type="text/html" id="recordTmpl">
	<tr>
		<td>{{= type}}</td>
		<td>{{= size}}</td>
		<td>{{= time}}</td>
	</tr>
</script>
<script type="text/javascript" src="${contextPath}/scripts/jquery.tmpl.min.js"></script>
<script type="text/javascript" src="${contextPath}/scripts/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript">
	$(document).ready(function(){
		$("#application").live("click",function(){
			$.ajax({
				url:"",
				data:{"func":"manualApply"},
				dataType:"json",
				success : function (data){
					if(data.status==true){
						$("#totalSize").html(data.totalSize);
						$("#totalAddSize").html(data.totalAddSize);
						$("#resordList").html("");
						$("#recordTmpl").tmpl(data.records).appendTo($("#resordList"));
						if(!data.canApp){
							$("#application").attr('disabled','disabled');
							$("#application").parent().append("使用空间未超过总空间50%");
						}
						$(".progressBar p").html("<strong>"+data.totalShow.used+"</strong> / "+data.totalShow.total);
						$(".progressBar .progress .bar").css("width",data.totalShow.percent);
						showMsg("扩容成功");
						hideMsg();
					}else{
						showMsg(data.message,"error");
						hideMsg();
					}
				}
			});
		});
		function showMsg(msg, type){
			type = type || "success";
			$("#opareteFileMessage").removeClass().addClass("alert alert-" + type).html(msg).show(150);
		}
		function hideMsg(timeout){
			timeout = timeout || 2000;
			window.setTimeout(function(){$("#opareteFileMessage").hide(150);}, timeout);
		}
	});
</script>