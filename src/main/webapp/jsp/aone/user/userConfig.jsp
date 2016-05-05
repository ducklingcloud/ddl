<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<link href="${contextPath}/jsp/aone/css/error.css" rel="stylesheet" type="text/css"/>
<link href="${contextPath}/scripts/bootstrap/css/bootstrap.min.css" rel="stylesheet" media="screen"/>
<link href="${contextPath}/scripts/bootstrap/css/bootstrap-responsive.min.css" rel="stylesheet"/>
<link href="${contextPath}/scripts/bootstrap/css/todc-bootstrap.css" rel="stylesheet"/>	
<fmt:setBundle basename="templates.default" />
<div class="ui-wrap">
	<div>
		<table class="table">
			<tr>
				<td>uid</td>
				<td>最大团队数</td>
				<td>已创建</td>
				<td>配置人</td>
				<td>配置时间</td>
				<td>描述</td>
				<td>操作</td>
			</tr>
			<c:forEach var="item" items="${configs }">
				<tr>
					<td>${item.uid }</td>
					<td>${item.maxCreateTeam }</td>
					<td>${size[item.uid]}</td>
					<td>${item.configUid }</td>
					<td><fmt:formatDate value="${item.configDate }" pattern="yyyy-MM-dd HH:mm:ss"/>:</td>
					<td>${item.description }</td>
					<td><input type="button" value="更新" class="updateUserConfig">
						<input type="hidden" class="userUid" value="${item.uid }">
						<input type="hidden" class="userId" value="${item.id }">
						<input type="button" value="删除" class="deleteUserConfig"></td>
				</tr>
			</c:forEach>	
		</table>
	</div>
	
	<div>
		<button class="btn btn-primary btn-lg" data-toggle="modal" data-target="#userShow">查询用户</button>
	</div>
	<div id="userShow" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="title modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	       			 <h4 class="modal-title" id="myModalLabel">用户查询</h4>
				</div>
				<div class="modal-body" >
					<div id="teamFilter">
						uid :<input id="queryWord" type="text" >
						<input type="button" id="queryUser" value="查询">
					</div>
					<table class="table">
						<tr>
							<td>uid</td>
							<td>最大团队数</td>
							<td>已创建</td>
							<td>配置人</td>
							<td>配置时间</td>
							<td>描述</td>
							<td>操作</td>
						</tr>
						<tbody id="userConfig">
							
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
	<div id="userUpdate"  class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	
		<div class="title modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	       			 <h4 class="modal-title" id="myModalLabel">配置用户团队大小</h4>
				</div>
				<div class="modal-body" >
					<input type="hidden" id="userUid">
					uid:<font class="uid" id="queryUid"></font><br/>
					size:<input type="text" size="8" id="maxSize"><br/>
					description :<textarea rows="" cols="" id="description"></textarea><br/>		
					<input type="button" value="提交" id="configUser">
				</div>
			</div>
		</div>
	</div>
</div>
<script type="text/html" id="userConfigTmp">
	<tr>
		<td>{{= uid}}</td>
		<td>{{= maxSize}}</td>
		<td>{{= teamSize}}</td>
		<td>{{= configUid}}</td>
		<td>{{= configTime}}</td>
		<td>{{= description}}</td>
		<td><input type="button" value="更新" class="updateUserConfig"><input type="hidden" class="userUid" value="{{= uid}}"></td>
	</tr>	
</script>
<script type="text/javascript" src="${contextPath}/scripts/jquery.tmpl.min.js"></script>
<script type="text/javascript" src="${contextPath}/scripts/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript">
	$(document).ready(function(){
		$("#queryUser").live('click',function(){
			var keyWord = $("#queryWord").val();
			if(!keyWord){
				alert("uid不能为空!");
				return;
			}
			var o = new Object();
			o.keyWord = keyWord;
			o.func = "searchUser";
			$.ajax({
				url : "${contextPath}/system/userConfig",
				data:o,
				type:'post',
				dataType:'json',
				success : function(data){
					if(data.result==true){
						$("#userConfig").html("");
						$("#userConfigTmp").tmpl(data.configs).appendTo($("#userConfig"));
					}else{
						alert(data.message);
					}
				}
			});
		});
		$(".updateUserConfig").live('click',function(){
			var v = $(this).parent().find(".userUid").val();	
			var o ={"uid":v,"func":"queryUser"};
			$.ajax({
				url : "${contextPath}/system/userConfig",
				data:o,
				type:'post',
				dataType:'json',
				success : function(data){
					if(data.result==true){
						
						$("#userUid").val(v);
						$("#queryUid").html(v);
						$("#maxSize").val(data.maxSize);
						$("#description").val(data.description);
						$('#userUpdate').modal('toggle');
					}else{
						alert(data.message);
					}
				}
			});
		});
	
		$("#configUser").live('click',function(){
			var uid = $("#userUid").val();
			var size = $("#maxSize").val();
			var desc = $("#description").val();
			var o = new Object();
			o.uid = uid;
			o.size = size;
			o.description = desc;
			o.func = "updateUserConfig";
			$.ajax({
				url : "${contextPath}/system/userConfig",
				data:o,
				type:'post',
				dataType:'json',
				success : function(data){
					if(data.result==true){
						alert("设置成功");
						window.location.reload();
					}else{
						alert(data.message);
					}
				}
			});
		});
	
		$(".deleteUserConfig").live('click',function(){
			var v = $(this).parent().find(".userId").val();
			var element = $(this).closest("tr");
			var o = new Object();
			o.id = v;
			o.func = "deleteUserConfig";
			$.ajax({
				url : "${contextPath}/system/userConfig",
				data:o,
				type:'post',
				dataType:'json',
				success : function(data){
					if(data.result==true){
						alert("删除成功");
						element.remove();
					}else{
						alert(data.message);
					}
				}
			});
			
		});
	
	
	});

</script>

