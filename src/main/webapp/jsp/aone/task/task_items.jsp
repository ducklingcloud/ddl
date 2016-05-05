<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<script type="text/javascript" src="${contextPath}/scripts/jquery/jquery-ui-1.8.16.custom.min.js"></script>
<link rel="stylesheet" href="${contextPath}/scripts/jquery_ui/css/jquery.ui.all.css">
<script src="${contextPath}/scripts/jquery_ui/jquery.ui.core.js"></script>
<script src="${contextPath}/scripts/jquery_ui/jquery.ui.widget.js"></script>
<script src="${contextPath}/scripts/jquery_ui/jquery.ui.progressbar.js"></script>
<link href="${contextPath}/jsp/aone/css/task.css"	rel="stylesheet" type="text/css">
<script type="text/javascript">
$(document).ready(function(){
	$("#typeSelector li[key='${taskTypeCon}']").each(function(index,item){
		$(item).attr("class","chosen")
	});
	$("#timeSelector li[key='${dateCon}']").each(function(index,item){
		$(item).attr("class","chosen");
	});
	$(".ui-navList li[key='${preOper}']").each(function(index,item){
		$(item).attr("class","current");
	});
});
var baseUrl="<vwb:Link context='task'  format='url'/>";
function dealTask(taskId,taskType){
	if($("#task_detail_all_"+taskId+" ul.task_detail_process").html().length>0){
		$("#task_detail_all_"+taskId).slideToggle("normal");
		$("#task_detail_all_"+taskId + " ul").html("");
		return;
	};
	
	//因进度需要后显示，放到getiems回调函数里面
	//getProcess(taskId,taskType);
	getItems(taskId,taskType);
}
function getProcess(taskId,taskType){
		$.ajax({
			   type: "get",
			   url: baseUrl+"/"+taskType,
			   data: "func=process&taskId="+taskId,
			   success: function(items){
				   var obj=$.parseJSON(items);
				   $("#task-"+taskType+"-process-template").tmpl(obj).appendTo("#task_detail_p_"+taskId);
				   $("#task_detail_p_"+taskId+" div[key='progressbar']").each(function(index,item){
					   $(item).progressbar({
							value: parseInt($(item).attr("value"))
						});
				   });
				   $("#task_detail_all_"+taskId).slideToggle("normal");
			   },
				statusCode:{
					450:function(){alert('会话已过期,请重新登录');},
					403:function(){alert('您没有权限进行该操作');}
				}
		});
}
function getItems(taskId,taskType){	
	$.ajax({
		   type: "get",
		   url: baseUrl+"/"+taskType,
		   data: "func=readyDeal&taskId="+taskId,
			statusCode:{
				450:function(){alert('会话已过期,请重新登录');},
				403:function(){alert('您没有权限进行该操作');}
			},
		   success: function(items){
			 var obj=$.parseJSON(items);
			 //如果是已完成的任务，展现，不操作
			 if(${preOper=='allHistory'||preOper=='createByMe'}){
				 $("#task-over-detail-template").tmpl(obj).appendTo("#task_detail_"+taskId);
				 getProcess(taskId,taskType);
				 return;
			 }
			 $(obj).each(function(index,item){
				  item.taskId=taskId;
				  if(taskType=='share'){
					  var visible="";
					  var buttonValue="";
					  var tCheck="";
					  var msg="";
					  if(!item.canEdit){
						  item.taskId=-1;
						  
						  msg="<span>已在"+item.editTime+"被<font style='color:#00f;'>"+item.userNameStr+"</font>锁定</span>";
						  visible="hidden";
						  tCheck="";
					  }else{
						  if(item.status=='doing'){
							  tCheck="task_status unchecked"
							  buttonValue="释放"
						  }else if(item.status=='finish'){
							  tCheck="task_status checked"
							  buttonValue="释放";
						  }else if(item.status=='undo'){
							  tCheck="";
							  buttonValue="接受";
						  }
						  msg="";
						  visible="visible"
					  }
					  item.msg=msg;
					  item.visible=visible;
					  item.buttonValue=buttonValue;
					  item.tCheck=tCheck;
					 
				  }else if(taskType='independent'){
					  if(item.status=='doing'||item.status=='undo'){
						   item.tClass="task_status unchecked";
					  }else if(item.status=='finish'){
						   item.tClass="task_status checked";
					  }
				  }
				  
			  });
			 $("#task-"+taskType+"-detail-template").tmpl(obj).appendTo("#task_detail_"+taskId);
			 getProcess(taskId,taskType);
		   }
	});
}

function updateIndependentStatus(tId,itemId){
	
	var paramName="";
	var check="";
	var className=$("#item_"+itemId).attr("class");
	if(className=='task_status checked'){paramName="doingIds";check="unchecked"};
	if(className=='task_status unchecked'){paramName="finishIds";check="checked"};
	$.ajax({
		   type: "get",
		   url: baseUrl+"/independent",
		   data: "func=dealSubmit&taskId="+tId+"&"+paramName+"="+itemId,
		   success: function(flag){
			   if(flag=='true'){
			   	   $("#item_"+itemId).attr("class","task_status "+check);
			   		//add by lvly 2012-08-04 判断是否需要划掉任务 BEGIN
			   		var taskDiv=$("#task_"+tId);
			   		var taskItemDiv=$("#task_detail_"+tId+" li");
			  	 	var flag=true;
			  	 	taskItemDiv.each(function(index,item){
			   			flag&=$(item).hasClass('checked');
			   		})
			   		//如果是全勾上了
			  	 	if(flag){
			  	 		taskDiv.addClass("checked");
			  	 		$("#dustbin_ref_"+tId).show();
			  	 	}else{
			  	 		taskDiv.removeClass("checked");
			  	 		$("#dustbin_ref_"+tId).hide();
			  	 	}
			  	 	//END
			   }else{
				   alert("任务已经结束，请刷新页面！");
			   }
		   },
			statusCode:{
				450:function(){alert('会话已过期,请重新登录');},
				403:function(){alert('您没有权限进行该操作');}
			}
	});
}
function updateLock(tId,itemId){

	if(tId==-1){
		alert("被别人锁了，挑选别的任务吧");
		return;
	}
	var paramName="";
	var value=$("#item_lock_"+itemId).attr("value");
	if(value=='释放'){
		paramName="undoIds";
		value="接受";
		$("#item_check_"+itemId).attr("class","");
	}
	else if(value=='接受'){
		paramName="doingIds";
		value="释放";
		$("#item_check_"+itemId).attr("class","task_status unchecked");
	}
	$("#item_lock_"+itemId).attr("value",value);
	$.ajax({
		   type: "get",
		   url: baseUrl+"/share",
		   data: "func=dealSubmit&taskId="+tId+"&"+paramName+"="+itemId,
		   success: function(flag){
			  		 if(flag=='true'){
				   
				   }else{
					   alert('任务状态以被别人编辑！请刷新页面！');
				   }
		   },
			statusCode:{
				450:function(){alert('会话已过期,请重新登录');},
				403:function(){alert('您没有权限进行该操作');}
			}
	});
}
function updateCheck(tId,itemId){
	if(tId==-1){
		alert("被别人锁了，挑选别的任务吧");
		return;
	}
	var paramName="";
	var checkClassName=$("#item_check_"+itemId).attr("class");

	if(checkClassName=='task_status checked'){paramName="doingIds";check="unchecked"};
	if(checkClassName=='task_status unchecked'){paramName="finishIds";check="checked"};
	$.ajax({
		   type: "get",
		   url: baseUrl+"/share",
		   data: "func=dealSubmit&taskId="+tId+"&"+paramName+"="+itemId,
		   success: function(flag){
			   if(flag=='true'){
				   	$("#item_check_"+itemId).attr("class","task_status "+check);
				  	//add by lvly 2012-08-04 判断是否需要划掉任务 BEGIN
			   		var taskDiv=$("#task_"+tId);
			   		var taskItemDiv=$("#task_detail_"+tId+" li .task_status");
			  	 	var flag=true;
			  	 	taskItemDiv.each(function(index,item){
			   			flag&=$(item).hasClass('checked');
			   		})
			   		//如果是全勾上了
			  	 	if(flag){
			  	 		taskDiv.addClass("checked");
			  	 		$("#dustbin_ref_"+tId).show();
			  	 	}else{
			  	 		taskDiv.removeClass("checked");
			  	 		$("#dustbin_ref_"+tId).hide();
			  	 	}
			  	 	//END
			   }else{
				   alert('任务状态已经变更！请刷新页面！');
			   }
		   },
			statusCode:{
				450:function(){alert('会话已过期,请重新登录');},
				403:function(){alert('您没有权限进行该操作');}
			}
	});
}
function stopBubble(){
	alert(event);
	if ( event && event.stopPropagation )
	event.stopPropagation(); 
	else
	window.event.cancelBubble = true;
	return false;

}
function select(){
	param="";
	$("#typeSelector li").each(function(index,item){
		if($(item).attr("class")=='chosen'){
			param+="&taskType="+$(item).attr("key");
		}
	})
	$("#timeSelector li").each(function(index,item){
		if($(item).attr("class")=='chosen'){
			param+="&date="+$(item).attr("key");
		}
	})
	window.location.href=baseUrl+"?listType=${preOper}"+param;
}
function hideChecked(){
	$(".task_left").each(function(index,item){
		if($(item).hasClass('checked')){
			$(item).parent().parent().slideToggle('normal');
		}
	});
}
function toDustbin(url){
	var flag=confirm("确定归档吗？ 归档后会挪到垃圾箱，不可恢复");
	if(flag){
		window.location.href=url;
	}
}

/* tag Selector */	
$("#typeSelector li").live("click",function(){
	$("#typeSelector li").removeClass("chosen");
	$("#allSelector li").removeClass("chosen");
	$(this).addClass("chosen");
	select();
});
$("#timeSelector li").live("click",function(){
	$("#timeSelector li").removeClass("chosen");
	$("#allSelector li").removeClass("chosen");
	$(this).addClass("chosen");
	select();
});
$("#allSelector li").live("click",function(){
	$("#typeSelector li").removeClass("chosen");
	$("#timeSelector li").removeClass("chosen");
	$(this).addClass("chosen");
	select();
});
$("#showSelector li").live("click",function(){
	$(this).toggleClass("chosen");
	hideChecked();
});

</script>
<fmt:setBundle basename="templates.default" />

	<div id="tagSelector" class="content-menu">
		<ul class="ui-navList" style="margin-top:-2px;">
			<li key='imTaker'><a class="filter-option single" href="<vwb:Link context='task' format='url'/>?listType=imTaker"><span class="tagTitle">收任务</span></a></li>
			<li key='createByMe'><a class="filter-option single" href="<vwb:Link context='task' format='url'/>?listType=createByMe"><span class="tagTitle">发任务</span></a></li>
			<li key='allHistory'><a class="filter-option single" href="<vwb:Link context='task' format='url'/>?listType=allHistory"><span class="tagTitle">垃圾箱</span></a></li>
		</ul>
	</div>
	<div id="tagItemsJSP" class="content-menu-body">
		<div class="innerWrapper">
			<div class="toolHolder light readyHighLight2" >
				<ul id="allSelector" class="filter" style="float:left;">
					<li ><a id="remove-single-option">全部</a></li>
				</ul>
				<ul id="timeSelector" class="filter">
					<li key="today"><a  class="date-option single">今天</a></li>
					<li key="yesterday"><a  class="date-option single" >昨天</a></li>
					<li key="thisweek"><a  class="date-option single" >本周</a></li>
					<li key="lastweek"><a  class="date-option single">上周</a></li>
					<li key="thismonth"><a  class="date-option single" >本月</a></li>
					<li key="lastmonth"><a  class="date-option single" >上月</a></li>
				</ul>
				<ul id="typeSelector" class="filter">
					<li key="independent"><a  class="type-option single">独立任务</a></li>
					<li key="share"><a  class="type-option single" >共享任务</a></li>
				</ul>
				<ul id="showSelector" class="filter">
					<li key="hiddenFinish"><a  class="type-option single">隐藏已完成任务</a></li>
				</ul>
			</div>
			<ul class="toolHolder taskList">
				<c:forEach items="${tasks }" var="task">
					<div>
					<li class="taskListShow" onclick="dealTask(${task.taskId},'${task.taskType }')">
						<div id="task_${task.taskId }" class="task_main task_left ${task.userOver=='true'?'checked':''}">
							<div class="task_title">
								<div class="task_type">
									<c:if test="${task.taskType=='independent' }">
										<span class="type independent"></span>
									</c:if>
									<c:if test="${task.taskType=='share' }">
										<span class="type share"></span>
									</c:if>
								</div>
								<h2>${task.title }<span class="type text"> | ${task.taskTypeCN }</span></h2>
								
							</div>
							<div class="task_owner">${task.creator } 创建于 ${task.createTime }</div>
						</div>
						<div class="task_main task_mid">
							<div class="task_executor">${task.takersNameStr }</div>

						</div>

						<div class="task_main task_right">
							<c:if test="${task.newTask=='true' }">
								<div title="新功能" class="newFunc"></div>
							</c:if>
							<div class="task_oper">
								<!-- 修改 -->
									<c:if test="${preOper=='createByMe' }"><a class="task_edit" href="<vwb:Link context='task'  format='url'/>/${task.taskType }?func=readyModify&taskId=${task.taskId}" alt="修改"  title="修改"></a></c:if>
								<!-- 删除 -->
									<c:if test="${preOper=='createByMe' }"><a class="task_delete" href="<vwb:Link context='task'  format='url'/>/${task.taskType }?func=delete&taskId=${task.taskId}&listType=${preOper}" alt="删除" title="删除"></a></c:if>
								<!-- 归档 -->
								<c:if test="${preOper=='imTaker' }"><a id="dustbin_ref_${task.taskId }" class="task_delete" style="display:${task.userOver=='true'?'':'none'}" href="javascript:toDustbin('<vwb:Link context='task'  format='url'/>/${task.taskType }?func=dustbin&taskId=${task.taskId}&listType=${preOper}')" alt="归档" title="归档"></a></c:if>
							</div>
						</div>
						<div class='ui-clear'></div>
					</li>
					<li class='task_detail' style="display: none;" id="task_detail_all_${task.taskId }">
						<ul class="task_detail_content" id="task_detail_${task.taskId }"></ul>
						<ul class="task_detail_process" id="task_detail_p_${task.taskId }"></ul>
						<div class="ui-clear"></div>
					</li>
					</div>
				</c:forEach>
			</ul>
			
		</div>
		<div class="ui-clear"></div>
	</div>
<script id="task-share-process-template" type="text/html">
<li>
	<div class="executor">进度： ({{= shareProcessStr}}) </div>
	<div key="progressbar" value="{{= shareProcess}}"></div>
	<div class="ui-clear"></div>
</li>
</script>
<script id="task-independent-process-template" type="text/html">
<li>
	<div class="executor">{{= userNameStr}}：({{= userProcessStr}}) </div>
	<div key="progressbar" value="{{= userProcess}}"></div>
	<div class="ui-clear"></div>
</li>
</script>
<script id="task-over-detail-template" type="text/html">
<li>{{= content}}</li>
</script>
<script id="task-independent-detail-template" type="text/html">
	<li onclick=updateIndependentStatus({{= taskId}},{{= itemId}}) class="{{= tClass }}" id="item_{{= itemId }}">{{= content}}      {{= process}}</li>
</script>
<script id="task-share-detail-template" type="text/html">
	<li>
		<div class="acceptOrNot"><input class="largeButton small" type="button" style="visibility:{{= visible}}" onclick="updateLock({{= taskId}},{{= itemId}})" id=item_lock_{{= itemId}} value="{{= buttonValue}}"/>{{html msg}}</div>
		<div onclick="updateCheck({{= taskId}},{{= itemId}})"id=item_check_{{= itemId}} class="{{= tCheck}}">{{= content}}</div>
		<div class="ui-clear"></div>
	</li>
</script>
<c:if test="${!(empty taskId)&&!(empty taskType) }">
	<script>
		dealTask('${taskId}','${taskType}');
	</script>
</c:if>