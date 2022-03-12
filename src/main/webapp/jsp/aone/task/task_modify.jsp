<%@page language="java" pageEncoding="UTF-8"%>
<%@taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<link href="${contextPath}/jsp/aone/css/fileuploader.css"	rel="stylesheet" type="text/css">
<link href="${contextPath}/jsp/aone/css/task.css" rel="stylesheet"	type="text/css">
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/tokenInput.css" type="text/css" />
<link rel="stylesheet" href="${contextPath}/scripts/jquery_chosen/chosen.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="${contextPath}/scripts/jquery_ui/slidernav.css" media="screen, projection" />
<script type="text/javascript" src="${contextPath}/scripts/jquery/jquery-1.8.3.min.js"></script>
<script type="text/javascript"	src="${contextPath}/scripts/jquery/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript"	src="${contextPath}/jsp/aone/js/fileuploader.js"></script>
<script type="text/javascript" src="${contextPath}/scripts/jquery_ui/slidernav.js"></script>
<script type="text/javascript" src="${contextPath}/jsp/aone/js/toker-jQuery.js"></script>
<script type="text/javascript" src="${contextPath}/scripts/jquery_chosen/chosen.jquery.js"></script>
<fmt:setBundle basename="templates.default" />
<html>
<head>
<script type="text/javascript">
	showPersonList();
	$(document).ready(function(){
		/*remove the banner image---vera*/
		$(window).unbind('.localNav');
		$("#masthead").css({"height":"60px"});
		$("#navigation").addClass("fixed");
		$("#banner").hide();
		$('#footer').hide();
		$('body').append('<div class="bedrock" style="height:30px;"></div>');
		$('#slider').sliderNav();
		//------------------------------------------------automatical memberList begin copiede from adminInvitations.jsp
		$('input#invitees').tokenInput2("<vwb:Link context='task' format='url'/>?func=getmembers&type=map", {
		//$('input#invitees').tokenInput("<vwb:Link context='contacts' format='url'/>?func=searchUser", {
			theme:"facebook",
			searchingText: "正在搜索……",
			noResultsText: "",
			preventDuplicates: true
		});
		//$('input#invitees').chosen("<vwb:Link context='contacts' format='url'/>?func=searchUser");
		
		
		//$("#memberListShow").click(function(){
			//$("#memberListShow").chosen();
		//})
		

	  //------------------------------------------------automatical memberList end
	});
	function validateForm(){
		if($('#taskTitle').attr("value")==''){
			alert('请输入标题');
			return false;
		}
		var rs="";
		$("[name='newTaskContent']").each(function(index,item){
			if($(item).val()==''){
				msg="第"+(index+1)+"个任务内容为空！";
				rs+=msg+"\n";
			};
		})
		if(rs!=''){
			alert(rs);
			return false
		}
		return true;
	}
	function submitDEForm(obj) {
		
		if(obj=='exit'){
			var action="<vwb:Link context='task' format='url'/>?CreateByMe=true";
			$("#editform").attr("action",action);
			$("#editform").submit();
			return;
		}else{
			if(!validateForm())return;
			var taskType="";
			if(${!empty task}){
			 	taskType=$('#taskType').attr("value")
			}else{
				taskType=$("input[name='taskType']:checked").val();
			}
			var action="<vwb:Link context='task'  format='url'/>/"+ taskType+"?func=modifySubmit"
			
			
			$("#editform").attr("action",action);
		}
		var strction=obj.name;
		if($(obj).attr("name")=="cancel"){
			$("#pageTitle").rules("remove","required");
		}
		validateUpdateContent();
		$("#editform").submit();
	}
	/**验证内容更改*/
	function validateUpdateContent(){
		var needUpdateIds=$("#needUpdateItemsId").attr("value");
		$("[name='taskContent']").each(function(){
			var itemId=$(this).attr("itemId");
			if($("#hiddenContent_"+itemId).attr("value")!=$(this).val()){
				$(this).attr("name","modifyTaskContent")
				needUpdateIds+=itemId+","
			}
		});
		$("#needUpdateItemsId").attr("value",needUpdateIds);
	}
	var rowNum=0;
	function addTask(){
		rowNum++;
		var rownum=$("#taskTable tr").length-1;
		$("<tr id='taskContent_"+rowNum+"'><th>任务内容：</th><td><textarea name='newTaskContent'></textarea>"
				+"<input type='button' class='largeButton small' name='deleteTaskItems' value='删除' onclick='javascript:deleteRow("+rowNum+")'>"
				+"</td></tr>")
		.insertAfter($("#taskTable tr:eq("+rownum+")"));
	}
	function deleteRow(rowId)
	{
	     $("#taskContent_"+rowId).remove();
	}
	function deleteExistRow(trId){
		$("#TR_"+trId).remove();
		var val=$('#needDeleteItemsId').attr("value");
		val+=trId+",";
		$('#needDeleteItemsId').attr("value",val);
		
	}

	function showPersonList(){
		var url = site.getURL('task',null)+"?func=getmembers";
		//ajaxRequest(url,"",renderData);
		$.ajax({
			url:url,
			type:'POST',
			success:function(data){
				var datajson = JSON.parse(data);
				renderData(datajson.users);
				//renderDataChosen(datajson.users);
			},
			error:function(data){
				//alert('I am sorry!');
			},
			statusCode:{
				450:function(){alert('会话已过期,请重新登录');},
				403:function(){alert('您没有权限进行该操作');}
			}
		})
		$("#slider").show();
	}
	
	function renderData(data){
		//console.log(data);
		//alert($("ul.taskPersonList").text());
		if($("ul.taskPersonList").children().length <= 0){
			$.each(data,function(index,element){
				var alphabet = element.id;
				var users = element.value;
				$("ul.taskPersonList").append("<li id = '" + alphabet + "'><a class='title'>" + alphabet + "</a><ul></ul></li>");
				$.each(users, function(index2,element2){
					var uid = element2.id;
					var name = element2.name;
					//console.log("("+uid+","+name+")");
					$("ul.taskPersonList li[id = '" + alphabet + "'] ul").append("<li><a uid='"+uid+"'>" + name + "</a></li>");
				});
			});
		}
	}
	
	function renderDataChosen(data){
		//console.log(data);
		//alert($("ul.taskPersonList").text());
		//if($("ul.taskPersonList").children().length <= 0){
			
			$.each(data,function(index,element){
				var alphabet = element.id;
				var users = element.value;
				//console.log(alphabet);
				$("#memberListShow").append("<optgroup label = '" + alphabet + "'></optgroup>");
				$.each(users, function(index2,element2){
					var uid = element2.id;
					var name = element2.name;
					//console.log("("+uid+","+name+")");
					$("#memberListShow optgroup[label = '" + alphabet + "']").append("<option>" + name + "</option>");
				});
			});
		//}
	}
	
	/* $("table#taskTable ul#personList .deleteName").live("click",function(){
		$(this).parent().remove();
	}); */
	
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
					onclick='javascript:submitDEForm("exit");' />
			</div>
		</div>
		<form id="editform" name="editform" method="post">
			<input
				type="hidden" name="taskId" value="${task.taskId }" /> <input
				type="hidden" name="needDeleteItemsId" id="needDeleteItemsId" /> <input
				type="hidden" name="needUpdateItemsId" id="needUpdateItemsId">

			<table id="taskTable">
				<tr>
					<th>分派任务：</th>
					<td>
						<!-- <ul id="personList" contentEditable='true'></ul> <a id="memberList" onclick="showPersonList()">联系人列表</a> -->
						<input type="text" id="invitees" name="takerList" value=""/>
						<input type="hidden" id="invitees_pop" name="takerList_pop" value=""/>		<a id="memberList" onclick="showPersonList()">联系人列表</a>
							<br/><span class="ui-text-note">可以输入多个邮箱</span>
						
						 <!-- <select data-placeholder="请输入联系人" style="width:600px;" id="memberListShow" class="chzn-select" multiple tabindex="6">
					          <option value=""></option>
					     </select> -->
					</td>
					<%-- <td><c:forEach items="${members}" var="member"
							varStatus="index">
							<ul class="taskPersonList">
								<li><input
									<c:if test="${fn:contains(task.takersUIDStr, member.uid)}">checked="checked"</c:if>
									type="checkbox" value="${member.name }%${member.uid}"
									name="taker" id="taker">${member.name}</li>
							</ul>
						</c:forEach></td> --%>
				<tr>
				<tr>
					<th>任务名称：</th>
					<td><input name="taskTitle" id="taskTitle"
						value="${task.title }" /></td>
					<c:if test="${empty task }">
						<tr>
							<th>任务类型：</th>
							<td><input type="radio" name="taskType" value="independent"
								checked="checked">独立任务 <input type="radio"
								name="taskType" value="share">共享任务</td>
						</tr>
						<tr>
							<th>任务内容：</th>
							<td><textarea name="newTaskContent" id="newTaskContent"></textarea>
								<input type="button" class="largeButton small"
								name="addTaskItems" value="增加任务内容" onclick="addTask();">
							</td>

						</tr>
					</c:if>
					<c:if test="${!empty task }">
						<tr>
							<th>任务类型：</th>
							<td><input type="hidden" id="taskType" name="taskType"
								value="${task.taskType}"> ${task.taskTypeCN }</td>
						</tr>
					</c:if>
					<c:forEach items="${task.items}" var="item" varStatus="status">
						<tr id="TR_${item.rid }">
							<th>任务内容：</th>
							<td><textarea name="taskContent" itemId="${item.rid }">${item.content }</textarea>
								<input type="hidden" id="hiddenContent_${item.rid}"
								value="${item.content}" /> <c:if test="${!status.first }">
									<input type="button" class="largeButton small"
										name="deleteTaskItems" value="删除"
										onclick="javascript:deleteExistRow('${item.rid}')">
								</c:if> <c:if test="${status.first }">
									<input type="button" class="largeButton small"
										name="addTaskItems" value="增加任务内容" onclick="addTask();">
								</c:if></td>
						</tr>
					</c:forEach>
			</table>

		</form>
	</div>
	
	<div id="slider" class="lynxDialog" style="display:none;">
		<div class="slider-content">
			<ul class="taskPersonList">
			</ul>
		</div>
	</div>
</body>

</html>
