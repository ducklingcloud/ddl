<%@page language="java" pageEncoding="UTF-8"%>
<%@taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<script type="text/javascript"
	src="${contextPath}/scripts/jquery/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript"
	src="${contextPath}/jsp/aone/js/fileuploader.js"></script>
<link href="${contextPath}/jsp/aone/css/fileuploader.css"
	rel="stylesheet" type="text/css">
<fmt:setBundle basename="templates.default" />
<html>
<head>
<script>
function submitDEForm(obj) {
	if(obj=='exit'){
		var action="<vwb:Link context='task' format='url'/>?listTypecreateByMe";
		$("#editform").attr("action",action);
		$("#editform").submit();
		return;
	}
	var params="&undoIds=";
	$("#undo tr").each(function(trindex,tritem){
		var id=$(this).attr("id");
		if(id!=undefined&&id!=""){
			params+=id+",";
		}
	});
	params+="&doingIds=";
	$("#doing tr").each(function(trindex,tritem){
		var id=$(this).attr("id");
		if(id!=undefined&&id!=""){
			params+=id+",";
		}
	});
	params+="&finishIds=";
	$("#finish tr").each(function(trindex,tritem){
		var id=$(this).attr("id");
		if(id!=undefined&&id!=""){
			params+=id+",";
		}
	});
	$("#editform").attr("action",$("#editform").attr("action")+params);
	alert($("#editform").attr("action"));
	$("#editform").submit();
}
function toMove(obj,flag){
	var target=$(obj).parent().attr(flag+"Target");
	if(target==""){
		alert("到头了！");
		return ;
	}
	var rownum=$("#"+target+" tr").length-1;
	$(obj).parent().attr("downTarget",getNextDownTarget(target));
	$(obj).parent().attr("upTarget",getNextUpTarget(target));
	var itemId=$(obj).parent().parent().attr("id");
	$("<tr id='"+itemId+"'>"+$(obj).parent().parent().html()+"</tr>")
	.insertAfter($("#"+target+" tr:eq("+rownum+")"));
	$(obj).parent().parent().remove();
	
}
function getNextDownTarget(target){
	if(target=="undo"){
		return "doing";
	}else if(target=="doing"){
		return "finish";
	}else if(target=="finish"){
		return "";
	}
}
function getNextUpTarget(target){
	if(target=="finish"){
		return "doing";
	}else if(target=="doing"){
		return "undo";
	}else if(target=="undo"){
		return "";
	}
}

</script>
</head>
<body>
	<div id="pagecontent">
		<div id="tab21" class="DCT_tabmenu toolHolder ui-wrap wrapperFull">
			<div style="float: left" id="submitbuttons">
				<input name='saveexit' type='button' id='okbutton'
					value='<fmt:message key="editor.plain.save.submit"/>'
					onclick='javascript:submitDEForm(this);' /> <input name='cancel'
					type='button' id='cancelbutton'
					value='<fmt:message key="editor.plain.cancel.submit"/>'
					onclick='javascript:submitDEForm("exit");' /> <input name="action"
					type="hidden" id="action" />
			</div>
		</div>
		<b>${task.title }</b><br>
		<form id="editform" name="editform" method="post" action="<vwb:Link context='task'  format='url'/>/independent?func=dealSubmit">
			<input type="hidden" name="taskId" value="${task.taskId }"/>
			<table id="undo">
			
			<tr>
				<td colspan="3" ><b>未接受</b></td>
			</tr>
			<c:forEach items="${items }" var="item">
				<c:if test="${item.status=='undo' }">
					<tr id="${item.rid }">
						<td>${item.content }</td>
						<td downTarget="doing" upTarget="">
							<a onclick="toMove(this,'up')"  ><img src="${contextPath}/images/common/maximize.png" alt="hander" title="上移"/></a>
							<a onclick="toMove(this,'down')"  ><img src="${contextPath}/images/common/minimize.png" alt="hander" title="下移"/></a>
						</td>
						<td>已完成：${item.process }</td>
					</tr>
				</c:if>
			</c:forEach>
			
			</table>
			<table id="doing">
			<tr>
				<td colspan="3" ><b>执行中</b></td>
			</tr>
			<c:forEach items="${items }" var="item">
				<c:if test="${item.status=='doing' }">
					<tr id="${item.rid }">
						<td>${item.content }</td>
						<td downTarget="finish" upTarget="undo">
							<a onclick="toMove(this,'up')"  ><img src="${contextPath}/images/common/maximize.png" alt="hander" title="上移"/></a>
							<a onclick="toMove(this,'down')"  ><img src="${contextPath}/images/common/minimize.png" alt="hander" title="下移"/></a>
						</td>
						<td>已完成：${item.process }</td>
					</tr>
				</c:if>
			</c:forEach>
			</table>
			<table id="finish">
			<tr>
				<td colspan="3" ><b>已完成</b></td>
			</tr>
			<c:forEach items="${items }" var="item">
				<c:if test="${item.status=='finish' }">
					<tr id="${item.rid }">
						<td>${item.content }</td>
						<td downTarget="" upTarget="doing">
							<a onclick="toMove(this,'up')"  ><img src="${contextPath}/images/common/maximize.png" alt="hander" title="上移"/></a>
							<a onclick="toMove(this,'down')"  ><img src="${contextPath}/images/common/minimize.png" alt="hander" title="下移"/></a>
						</td>
						<td>已完成：${item.process }</td>
					</tr>
				</c:if>
			</c:forEach>
			</table>
			
		</form>
	</div>
</body>

</html>