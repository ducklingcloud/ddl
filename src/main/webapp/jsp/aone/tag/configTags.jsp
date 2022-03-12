<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/tag-z.css" type="text/css" />

	<div id="configTagsJSP-free" class="content-menu" style="position:relative; top:-1px;" value="0">
		<P class="ui-navList-title">未分类标签</P>
		<div id="newTag">
			<input type="text" name="newTag" />
			<button id="saveUnknowTag" class="largeButton small">添加</button>
		</div>
		<div class="ui-clear"></div>
		<ul id="freeTags" class="tag-list <c:if test="${empty tags}">empty</c:if>">
			<c:forEach items="${tags}" var="item">
				<li>
					<a class="lightDel delFreeTag" id="tag-for-${item.id}" title="删除"></a>
					<label  id="tag-for-${item.id}" class="tag-option multiple" key="tag" value="${item.id}">${item.title}</label>
					<span class="tagNumCount">${item.count}</span>
					<div class="ui-clear"></div>
				</li>
			</c:forEach>
		</ul>
	</div>
	
	<div id="configTagsJSP-grouped" class="content-menu-body">
		<div id="tagGroupSelector" class="toolHolder">
			<label class="ui-text-note">所有标签集：</label><label class="ui-text-note">（拖拽以调整标签集的排序）</label>
			<a class="ui-RTCorner" href="#" onclick="self.location=document.referrer;">返回</a>
			<ul class="tagGroupShow" id="switch">
			<c:forEach items="${tagGroups}" var="gitem">
				<li group_id="${gitem.group.id}"><a id="topGroupTilte_${gitem.group.id}">${gitem.group.title}</a></li>
			</c:forEach>
			</ul>
			<button id="createTagGroup" class="largeButton small">+ 添加标签集</button>
		</div>
		
		<div id="groupedTags">
			<c:if test="${empty tagGroups}">
				<p class="NA large">团队还没有创建任何标签集</p>
				<div class="NA">
					<p>将标签组成标签集，可以方便文档的管理和检索。例如，您可以创建三个标签集：“项目”、“研究方向”和“文档类型”，分别包含若干个标签。</p>
					<ul>
						<li><strong>项目：</strong>项目1，项目2，项目3...</li>
						<li><strong>研究方向：</strong>团队协作，社会化网络，信息传播...</li>
						<li><strong>文档类型：</strong>论文笔记，方案，任务书...</li>
					</ul>
					<p>鼓励成员按照标签集有规律地创建标签和添加标签。</p>
					<div class="bedrock"></div>
					<hr/>
				</div>
			</c:if>
			<p id='information' class="NA">拖拽标签以调整标签分组</p>
			<div id="tagGroupsContainer">
			<c:forEach items="${tagGroups}" var="gitem">
				<div class="gTag-block" value="${gitem.group.id}">
					<h4><span class="groupTitleName">${gitem.group.title}</span><a class="lightDel delGroup"></a></h4>
					<input type="text" class="saveNewKnowText"/><button class="saveNewKnowTag largeButton small">添加</button>
					<ul id="tag-list-${gitem.group.id}" class="tag-list <c:if test="${empty gitem.tags}">empty</c:if>">
						<c:forEach items="${gitem.tags}" var="item">
							<li>
								<a class="lightDel delFreeTag" id="tag-for-${item.id}" title="删除"></a>
								<label id="tag-for-${item.id}" class="tag-option multiple" key="tag" value="${item.id}">${item.title}</label>
								<span class="tagNumCount">${item.count}</span>	
								<div class="ui-clear"></div>
							</li>
						</c:forEach>
					</ul>
				</div>
			</c:forEach>
				<div class="ui-clear"></div>
			</div>
		</div>
	</div>
	
	<div class="ui-clear"></div> 

	<div id="addTagGroupDialog"	class="std stdRounded lynxDialog">
		<div class="inner">
			<h2>添加标签集</h2>
			<form id="create-tagGroup-form">
				<input type="text" name="newTagGroupTitle" /> 
				<input type="hidden" name="isNewTag" value="" /> 
				<input type="hidden" name="existTagId" value="" /> 
				<input type="hidden" name="item_id" value="" /> 
				<input type="hidden" name="item_type" value="" />
			</form>
		</div>
		<div class="control">
			<button id="saveTagGroup" class="largeButton small">确定</button>
			<button class="closeThis largeButton small">取消</button>
		</div>
	</div>
	
	<div id="removeTagGroupDialog" class="std stdRounded lynxDialog">
		<div class="inner">
			<h2>删除标签集</h2>
			<label><input type="radio" name="removeTagGroup" value="0" />删除标签组和其中所有的标签</label>
			<br/>
			<label><input type="radio" name="removeTagGroup" value="1" />只删除组，保留所有标签</label>
			<input type="hidden" id="removeTagGroupId"/>
		</div>
		<div class="control">
			<button id="removeTagGroup" class="largeButton small">确定</button>
			<button class="closeThis largeButton small">取消</button>
		</div>
	</div>
<script type="text/javascript" src="${contextPath}/scripts/editable/jquery.editable-1.3.3.js"></script>
<script type="text/javascript">
$(document).ready(function(){
	switchToLightNav();
	
	//双击修改事件
	$('.tag-option').editable({editBy : "dblclick",onSubmit:onSub});
	
	function onSub(content) { 
		var t=this;
		var data = content['current'];
		var preData=content['previous'];
		if(data==preData){
			return;
		}
		if(data==''||data==null){
			alert("修改时数据不能为空");
			$(this).trigger("dblclick");
			return;
		}
		var tagId = $(this).attr("value");
		var groupId = $(this).parent().parent().parent().attr("value");
		
		ajaxRequest(tagConfigURL,"func=updateTag&existGroupId="+groupId+"&existTagId="+tagId+"&tagTitle="+content['current'],function(data){
			if(data!=null&&data["error"]!=null){
				alert(data["error"]);			
			}	
		});
	  }
	
	
	$('.groupTitleName').editable({editBy : "dblclick",onSubmit:onGroupTitleSub});
	
	function onGroupTitleSub(content){
		var t=this;
		var data = content["current"];
		var groupTile = data;
		if(data==''){
			alert("修改时数据不能为空");
			$(this).trigger("dblclick");
			return;
		}
		var groupId = $(this).parent().parent().attr("value");
		ajaxRequest(tagConfigURL,"func=updateTagGroup&existGroupId="+groupId+"&groupTile="+data,function(data){
			if(data!=null&&data["error"]!=null){
				alert(data["error"]);			
			}else{
				$('#topGroupTilte_'+groupId).html(groupTile);
			}
		});
	}
	
	var pitfallPara = { column: 4, 'margin-bottom': 50 };
	$('#tagGroupsContainer').pitfall(pitfallPara);
	
	/*catch the exactly tagContainer---vera*/
	var tagGroupOrderURL = site.getURL("configTag",null);
	$('.tagGroupShow').sortable({
		stop: function (event, ui) {
			var params = {'func':'sortTagGroups','tagGroupIds[]':[]};
			
			$('.tagGroupShow li').each(function(){
				params['tagGroupIds[]'].push($(this).attr('group_id'));
			});
			
			ajaxRequest(tagGroupOrderURL, params,function(){
				updateInfo("更新标签集顺序成功！");
			});
		}
	});
	function updateInfo(str){
		var text=$('#information').html();
		$('#information').html('<font color="red">'+str+'</font>');
		setTimeout(function(){
			$('#information').html(text);
		},2000);
	}
	
	$(".tagGroupShow li").live("click",function(){
		var  thisTitle = $(this).text();
		
		$(".gTag-block").each(function(){
			if(thisTitle == $(this).children("h4").text()){
				$(".selectedTagContainer").removeClass("selectedTagContainer");
				$(this).addClass("selectedTagContainer").viewFocus();
			}
		})
	});
	
	
	/*drag and drop tags---vera*/
	//$( "ul.tag-list").sortable({connectWith: "ul", dropOnEmpty: true}).disableSelection();
	/*
	$( "#freeTags" ).sortable({change: function(event, ui) {
		}});
	*/
	//托拽事件定义
	$("ul.tag-list").sortable({
		connectWith : 'ul.tag-list',
		
		stop : function(event,ui){
			//拖拽动作停止
			var thisItem = ui.item;
			var groupId = thisItem.parent().parent().attr("value");
			var thisValue = thisItem.children("label").attr("value");
			//add by lvly@2012-07-20{
			
			var newSort='';
			//如果移动到无组标签
			if(groupId==0){
				$('#freeTags li').each(function(i,n){
					newSort+=$(n).children("label").attr("value")+",";
				})
				if(newSort.indexOf(',')>-1){
					newSort=newSort.substring(0,newSort.lastIndexOf(','));
				}
			//有组标签
			}else{
				$('#tag-list-'+groupId+' li').each(function(i,n){
					newSort+=$(n).children("label").attr("value")+",";
				})
				if(newSort.indexOf(',')>-1){
					newSort=newSort.substring(0,newSort.lastIndexOf(','));
				}
			}
			//}
			ajaxRequest(tagConfigURL,"func=changeTagFromGroup&groupId="+groupId+"&tagId="+thisValue+"&sortIds="+newSort,function(data){
				//回调
				if(data!=null&&data["error"]!=null){
					alert(data["error"]);			
				}else{
					updateInfo('更新标签顺序成功！');
				}	
			});
			thisItem.parent().removeClass('empty');
			$('#tagGroupsContainer').pitfall(pitfallPara);
		},
		out : function(event, ui) {
			//拖拽出原有的框
			if (ui.sender.children('li').length==0) {
				ui.sender.addClass('empty');
			}
		}
	});
	
	var tagConfigURL = "<vwb:Link context='configTag' format='url'/>";
	$("#cancle-tag-sequence").live("click",function(){
		window.location.reload();
	});
	
	//添加未分类tag
	$("#saveUnknowTag").click(function(){
		var vale=$("input[name='newTag']").val();
		if(vale==''||vale==null){
			alert("请输入值后，再保存！");
			return;
		}
		ajaxRequest(tagConfigURL,"func=addTag&isNewTag=true&newTagTitle="+vale,function(data){
			if(data!=null&&data["error"]!=null){
				alert(data["error"]);			
			}else{ 
				$("#add-tagun-template").tmpl(data["currTag"]).appendTo("#freeTags");
				$('#freeTags').removeClass('empty');
				$('label[value='+data["currTag"].id+']').editable({editBy : "dblclick",onSubmit:onSub});
			}
		});
		
		$("input[name='newTag']").val(null);
	});
	//未分类输入框Entry事件
	$("input[name='newTag']").keydown(function(event){  
		  if(event.keyCode==13){  
			  $("#saveUnknowTag").trigger("click");
			  return false;
		  }  
	});
	
	$(".saveNewKnowText").live('keydown',function(event){  
		  if(event.keyCode==13){  
			  $(this).next().trigger("click");
		  }  
	});
	
	$('#create-tagGroup-form').submit(function(event){ event.preventDefault(); });
	
	$("#create-tagGroup-form input[name='newTagGroupTitle']").keydown(function(event){
		if(event.keyCode==13){
			$('#saveTagGroup').trigger('click');
			
		}
	})
	
	$('#createTagGroup').click(function(){
		$("input[name='newTagGroupTitle']").val('');
		$('#addTagGroupDialog').show();
		$("input[name='newTagGroupTitle']").focus();
	});
	
	$('.closeThis').click(function(){
		$(this).parent().parent().fadeOut().find('input:not([type="radio"])').val('');
	});
	//添加group
	$("#saveTagGroup").live('click',function(){
		var value = $("input[name='newTagGroupTitle']").val();
		if(value==''||value==null){
			alert("输入参数不能为空！");
			$("input[name='newTagGroupTitle']").focus();
			return;
		}else{
			ajaxRequest(tagConfigURL,"func=addTagGroup&"+$("#create-tagGroup-form").serialize(),function(data){
				if(data!=null&&data["error"]!=null){
					alert(data["error"]);
				}else{
					var newGroup = $("#add-tagGroup-template").tmpl(data).appendTo("#tagGroupsContainer");
					$("#add-tagGroupToHead-template").tmpl(data).appendTo($("#switch"));
					$('#addTagGroupDialog').hide();
					//绑定双击修改title事件
					$(".gTag-block[value="+data.id+"] h4 span").editable({editBy : "dblclick",onSubmit:onGroupTitleSub});
					$('#tagGroupsContainer').pitfall(pitfallPara);
					$('ul.tag-list').sortable({
						connectWith : 'ul.tag-list',
						
						stop : function(event,ui){
							var thisItem = ui.item;
							var groupId = thisItem.parent().parent().attr("value");
							var thisValue = thisItem.children("label").attr("value");
							var newSort='';
							//如果移动到无组标签
							if(groupId==0){
								$('#freeTags li').each(function(i,n){
									newSort+=$(n).children("label").attr("value")+",";
								})
								if(newSort.indexOf(',')>-1){
									newSort=newSort.substring(0,newSort.lastIndexOf(','));
								}
							//有组标签
							}else{
								$('#tag-list-'+groupId+' li').each(function(i,n){
									newSort+=$(n).children("label").attr("value")+",";
								})
								if(newSort.indexOf(',')>-1){
									newSort=newSort.substring(0,newSort.lastIndexOf(','));
								}
							}
							ajaxRequest(tagConfigURL,"func=changeTagFromGroup&groupId="+groupId+"&tagId="+thisValue+"&sortIds="+newSort,function(data){
								if(data!=null&&data["error"]!=null){
									alert(data["error"]);			
								}	
							});
							thisItem.parent().removeClass('empty');
							$('#tagGroupsContainer').pitfall(pitfallPara);
						},
						
						out : function(event, ui) {
							if (ui.sender.children('li').length==0) {
								ui.sender.addClass('empty');
							}
						}
					});
				}		
			});
		}
	});
	
	$("#saveTag").click(function(){
		ajaxRequest(tagURL,"func=add&"+$("#create-tag-form").serialize(),function(data){
			//1. append to left menu 
			$("#new-tag-template").tmpl(data["currTag"]).appendTo("#ungrouped-tag-list");
			//2. append to item row 
			$("#page-tag-template").tmpl(data["currTag"]).appendTo("#tag-item-"+data["item_key"]);
			$('#addTag').hide();
		});
	});
	
	//获取groupId并设置在tag中
	$("li.exist-tagGroup-li").live("click",function(){
		$("input[name='newTagGroupId']").attr("value",$(this).attr("tagGroup_id"));
	});
	
	
	$('.delFreeTag').live('click',function(){
		//var cfm = confirm('确定删除标签' + $(this).next().text() + "吗？");
		var cfm = confirm('确定删除标签吗？');
		var prev = $(this).next();
		var a = $(this).parent();
		if (cfm) {
			var id=$(this).next().attr('value');
			ajaxRequest(tagConfigURL,"func=deleteTag&existTagId="+id,function(data){
				if(data!=null&&data["error"]!=null){
					alert(data["error"]);			
				}else{
					if (a.parent().children('li').length == 1) {
						a.parent().addClass('empty');
					}
					a.remove();
				}
			});
		}
		
	});
	
	//删除标签集
	$('#removeTagGroup').live('click',function(){
		var v = $("input[name='removeTagGroup']:checked").val();
		if(v==null){
			alert("请选择后，再删除！");
			return ;
		}
		var id=$("#removeTagGroupId").val();
		ajaxRequest(tagConfigURL,"func=deleteTagGroup&existTagGroupId="+id+"&deleteAll="+v,function(data){
			if(data!=null&&data["error"]!=null){
				alert(data["error"]);			
			}else{
				window.location.href = site.getURL("configTag","url");
			}
		});
	});
	//
	$('button.saveNewKnowTag').live('click',function(){
		var test=$(this).prev();
		var s = test.val();
		if(s==''||s==null){
			alert("请输入值后，再保存！");
			return;	
		}	
		var id = test.parent().attr('value');
		ajaxRequest(tagConfigURL,"func=addTag&isNewTag=true&groupId="+id+"&newTagTitle="+test.val(),function(data){
			if(data!=null&&data["error"]!=null){
				alert(data["error"]);			
			}else{
				$("#add-tagknow-template").tmpl(data["currTag"]).appendTo("#tag-list-"+id);
				$('#tag-list-' + id).removeClass('empty');
				$('#tagGroupsContainer').pitfall(pitfallPara);
				//绑定双击修改事件
				$('label[value='+data["currTag"].id+']').editable({editBy : "dblclick",onSubmit:onSub});
			}
		});
		test.val(null);
	});
	
	$('.delGroup').live('click',function(){
		$('#removeTagGroupId').val(null);
		var groupId=$(this).parent().parent().attr("value");
		$('#removeTagGroupId').val(groupId);
		$('#removeTagGroupDialog').show();
	});
});
</script>

<script type="text/html" id="exist-tag-template">
<li class="exist-tag-li" tag_id="{{= id}}" tag_title="{{= title}}"><a>{{= title}}({{= count}})</a></li>
</script>

<script type="text/html" id="exist-tagGroup-template">
<li class="exist-tagGroup-li" tagGroup_id="{{= id}}" tagGroup_title="{{= title}}"><a>{{= title}}</a></li>
</script>
<script type="text/html" id="add-tagun-template">
<li>
	<a class="lightDel delFreeTag" id="tag-for-{{= id}}" title="删除"></a>	
	<label id="tag-for-{{= id}}" class="tag-option multiple" key="tag" value="{{= id}}">{{= title}}</label>
	<span class="tagNumCount">{{= count}}</span>	
	<div class="ui-clear"></div>
</li>
</script>
<script type="text/html" id="add-tagknow-template">
	<li>
		<a class="lightDel delFreeTag" id="tag-for-{{= id}}" title="删除"></a>		
		<label id="tag-for-{{= id}}" class="tag-option multiple" key="tag" value="{{= id}}">{{= title}}</label>
		<span class="tagNumCount">{{= count}}</span>	
		<div class="ui-clear"></div>
	</li>	
</script>
<script type="text/html" id="add-tagGroup-template">
	<div class="gTag-block" value="{{= id}}">
		<h4><span class="groupTitleName">{{= title}}</span><a class="lightDel delGroup"></a></h4>
		<input type="text" class="saveNewKnowText"/><button class="saveNewKnowTag largeButton small">添加</button>
		<ul id="tag-list-{{= id}}" class="tag-list empty">
		</ul>
	</div>
</script>
<script type="text/html" id="add-tagGroupToHead-template">
	<li group_id="{{= id}}"><a id="topGroupTilte_{{= id}}">{{= title}}</a></li>
</script>
