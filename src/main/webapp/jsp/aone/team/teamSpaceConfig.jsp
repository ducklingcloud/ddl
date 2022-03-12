<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
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
		<td>tid</td>
		<td>teamCode</td>
		<td>teamName</td>
		<td>size</td>
		<td>creator</td>
		<td>upate time</td>
		<td>descripton</td>
		<td>操作</td>
	</tr>
	<c:forEach var="item" items="${configs }">
		<tr>
			<td>${item.tid }</td>
			<c:set value="${teamMap[item.tid] }" var="team"/>
			<td>${team.name }</td>
			<td>${team.displayName }</td>
			<td>${item.sizeDisplay }</td>
			<td>${item.updateUid }</td>
			<td>${item.updateTime }</td>
			<td>${item.description }</td>
			<td><input type="button" value="更新" class="updateTeam"><input type="hidden" value="${item.tid }" class="tid"/><input type="button" value="删除"/ class="deleteConfig"><input type="hidden" value="${item.id }" class="configId"/></td>
		</tr>
	</c:forEach>
</table>
</div>
<div><button class="btn btn-primary btn-lg" data-toggle="modal" data-target="#teamShow">
 查询团队
</button></div>
<div id="teamShow" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="title modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
       			 <h4 class="modal-title" id="myModalLabel">团队查询</h4>
			</div>
			<div class="modal-body" >
				<div id="teamFilter">
					类型：<select id="queryType" style="width: 3em">
						<option value="teamCode" selected="selected" >teamCode</option>
						<option value="teamName">teamName</option>
					</select>
					关键字 :<input id="queryWord" type="text" >
					<input type="button" id="queryTeam" value="查询">
				</div>
				<table class="table">
					<tr>
						<td>tid</td>
						<td>teamCode</td>
						<td>teamName</td>
						<td>大小</td>
						<td>类型</td>
						<td>操作</td>
					</tr>
					<tbody id="teamConfig">
					
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>
<div id="teamUpdate"  class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	
	<div class="title modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
       			 <h4 class="modal-title" id="myModalLabel">配置团队大小</h4>
			</div>
			<div class="modal-body" >
				<input type="hidden" id="configTid">
				teamCode:<font class="teamCode"></font><br/>
				teamName:<font class="teamName"></font><br/>
				size:<input type="text" size="8" id="configTeamSize">G<br/>
				description :<textarea rows="" cols="" id="description"></textarea><br/>		
				<input type="button" value="提交" id="configTeam">
			</div>
		</div>
	</div>
</div>

</div>
<script type="text/html" id="teamConfigTmp">
	<tr>
		<td>{{= tid}}</td>
		<td>{{= teamCode}}</td>
		<td>{{= teamName}}</td>
		<td>{{= size}}</td>
		<td>{{= type}}</td>
		<td><input type="button" class="updateTeam" value="更新"><input type="hidden" value="{{= tid}}" class="tid"/></td>
	</tr>
</script>
<script type="text/javascript" src="${contextPath}/scripts/jquery.tmpl.min.js"></script>
<script type="text/javascript" src="${contextPath}/scripts/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript">
	$(document).ready(function(){
		$("#showTeam").live('click',function(){
			$("#teamShow").toggle();
		});
		$("#queryWord").live("keypress",function(e){
			if(e.keyCode==13){
				  $('#queryTeam').trigger('click');
				 }

		});
		$(".deleteConfig").live('click',function(){
			var id = $(this).parent().find(".configId").val();
			var o = new Object();
			var element = $(this).parents("tr");
			o.id = id;
			o.func="deleteConfig";
			$.ajax({
				url:"${contextPath}"+"/system/teamSpaceConfig",
				data:o,
				type:'post',
				dataType:"json",
				success : function (data){
					if(data.result==true){
						alert("删除成功");
						element.remove();
					}else{
						alert(data.message);
					}
				}
			});
		});
		$(".updateTeam").live('click',function(){
			var tid = $(this).parent().find(".tid").val();
			var o = new Object();
			o.tid=tid;
			o.func="queryTeamConfig"
			$.ajax({
				url:"${contextPath}"+"/system/teamSpaceConfig",
				data:o,
				type:'post',
				dataType:"json",
				success : function (data){
					if(data.result==true){
						$("#teamUpdate").find(".teamCode").html(data.teamCode);
						$("#teamUpdate").find(".teamName").html(data.teamName);
						$("#configTid").val(data.tid);
						$("#description").val(data.description);
						$("#configTeamSize").val(data.size);
						$('#teamUpdate').modal('toggle')
					}else{
						alert(data.message);
					}
				}
			})
		});
		
		
		$("#queryTeam").live('click',function(){
			var query = $("#queryWord").val();
			if(!query){
				alert("查询词不能为空");
				return;
			}
			var o = new Object();
			o.queryType = $("#queryType").val();
			o.queryWord = query;
			o.func = "queryTeam";
			$.ajax({
				url:"${contextPath}"+"/system/teamSpaceConfig",
				data:o,
				type:'post',
				dataType:"json",
				success : function (data){
					if(data.result==true){
						$("#teamConfig").html("");
						$("#teamConfigTmp").tmpl(data.config).appendTo($("#teamConfig"));
					}else{
						alert(data.message);	
					}
				}
			});
		});
		
		$("#configTeam").live('click',function(){
			var size = $("#configTeamSize").val();
			if(!size){
				alert("团队大小值未填");
				return;
			}
			var o = new Object();
			o.func= "configTeamSize";
			o.tid = $("#configTid").val();
			o.size = size;
			o.description = $("#description").val();
			$.ajax({
				url :"${contextPath}"+"/system/teamSpaceConfig",
				data:o,
				type:'post',
				dataType:'json',
				success : function(data){
					if(data.result==true){
						window.location.reload();
					}else{
						alert(data.message);
					}
				}
			});
		});
	});
</script>

