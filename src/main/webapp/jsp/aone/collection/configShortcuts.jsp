<%@ page language="java" pageEncoding="utf-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<link rel="stylesheet" href="${contextPath}/scripts/colorPicker/colorPicker.css" type="text/css" />
<a class="ui-RTCorner back" href="#" id ="backReferrer" onclick="self.location=document.referrer;">返回</a>
<span class="plschoose">选择以下文件添加到推荐阅读</span>
<a class="shortcut-sort-info" id="information" style="margin-left:33%;"></a>
<div class="clear"></div>

<div class="shortcut" style="background:#f5f5f5; border:1px solid #ccc">
	<div id="nav" class="nav" style="border:none; margin-bottom:0;">
		<ul class="nav-list">
			<li class="current" style="padding:5px 10px">
				<a href="#" id="reloadShortcutPan"><p>${tagName}</p></a>
			</li>
			<li flag="false" style="width:200px; padding:7px;">
				<input type="text" id="shortcutKeyword" style="width:120px; font-size:12px; float:left;" placeholder="在团队文档中搜索">
				<input type="button" id="shortcutSearch" class="largeButton small" value="搜索"/>
			</li>
		</ul>
	</div>
	<div id="configShortCut-resouce" style="border-top:1px solid #ccc">
		<div class="clear"></div>
		<input type="hidden" id="tid" value="${teamId }"/>
		<input type="hidden" id="tgid" value="${tgid }"/>
			<div id="groupResource">
				<div id="resoureContainer">
					<div class="gTag-block" id="resouresPan">
						<ul class="shortcutlist" id="shortcutlist">
							<c:forEach items="${resources}" var="item">
								<li value="${item.rid}">
									<span class="${item.resourceType} headImg ${item.resourceFileType}"></span>
									<label  id="shortcut-for-${item.sid}" class="shortcut-option multiple" key="shoutcut" value="${item.rid}"><a href="${item.resourceURL }" target="_blank">${item.resourceTitle}</a></label>
									<span class="shortcutStatus">
										<c:choose>
											<c:when test="${item.choice}">
												已添加
											</c:when>
											<c:otherwise>
											<a class="addShortcut" title="添加到推荐阅读">添加</a>
											</c:otherwise>
										</c:choose>
									</span>
									<div class="ui-clear"></div>
								</li>
							</c:forEach>
						</ul>
					</div>
					<div class="ui-clear"></div>
				</div>
			</div>
	</div>
</div>
<div class="addshortcutGoto"></div>
<div id="configShortCut-sort" class="addedshortcut">
		<p class="ui-navList-title" style="padding:12px;">已添加的推荐阅读</p>
		<div class="ui-clear"></div>
		<ul id="choiceShortCut" class="tag-list shortcutlist">
			<c:forEach items="${shortcuts}" var="item">
				<c:choose>
					<c:when test="${empty item.color }">
						<c:set value="#ff6600" var="color"></c:set>
					</c:when>
					<c:otherwise>
						<c:set value="${item.color }" var="color"></c:set>
					</c:otherwise>
				</c:choose>
				<li>
					<span class="${item.resourceType} headImg ${item.resourceFileType}"></span>
					<label  id="shortcut-for-${item.sid}" class="shortcut-option multiple" key="shoutcut" value="${item.sid}" rid="${item.rid }"><a target="_blank" href="${item.resourceURL }" style="color:${color};">${item.resourceTitle}</a></label>
					<a class="lightDel delChoiceShortcut" id="shortcut-for-${item.sid}" title="删除"></a>
					<div>
						<input class="shortcutColor" type="text" name="${ item.sid}" value="${color }" />
					</div>
					<div class="ui-clear"></div>
				</li>
			</c:forEach>
		</ul>
</div>
<div class="clear"></div>
<script type="text/javascript" src="${contextPath}/scripts/editable/jquery.editable-1.3.3.js"></script>
<script type="text/javascript" src="${contextPath}/scripts/colorPicker/jquery.colorPicker.js"></script>
<script type="text/javascript">
$(document).ready(function(){
	
	var url="<vwb:Link context='configShortCut' format='url' />";
	
	$('.shortcutColor').colorPicker({pickerDefault: "ff6600", colors: ["000000", "ff9900", "0088aa", "33cc33","ff6600"], transparency: true}); 
	
	$(".addShortcut").live("click",function(){
		var tid = $("#tid").val();
		var tgid = $("#tgid").val();
		var rid = $(this).parent().parent().attr("value");
		var resourceTitle = $(this).parent().prev().children().html();
		var a = $(this).parent().prev().children();
		var li = $(this);
		var param={"tgid":tgid,"rid":rid,"color":"#ff6600"};
		ajaxRequest(url+"?func=addShortcut",param,function(data){
			
			var sid = data.sid;
			$(li).parent().html("已添加");
			var s = new Object();
			s.sid=sid;
			s.rid=rid;
			s.resourceType=data.resourceType;
			s.resourceTitle=resourceTitle;
			s.resourceFileType=data.resourceFileType;
			s.resourceUrl=a.attr("href");
			var ss = $("#add-choiceShortcut-template").tmpl(s);
			$(ss).appendTo($("#choiceShortCut"));
			$(ss).find("input.shortcutColor").colorPicker({pickerDefault: "ff6600", colors: ["000000", "ff9900", "0088aa", "33cc33","ff6600"], transparency: true}); 
		});
		
	});
	
	$("#backReferrer").live("click",function(){
		var url = site.getURL("tag",null);
		var tgid  = $("#tgid").val();
		if(tgid==0){
			url=url+"#&filter=all";	
		}else{
			url = url+"#&tag="+$("#tgid").val();
		}
		window.location.href =url;
	});
	
	$(".shortcutColor").live("change",function(){
		var color = $(this).attr("value");
		var label = $(this).parent().prev().prev();
		var sid = $(label).attr("value");
		ajaxRequest(url+"?func=changeColor","sid="+sid+"&color="+color,function(data){
			if(data.result){
				$(label).children().css({"color":color});
			}
		});
	});
	
	
	
	$(".delChoiceShortcut").live("click",function(){
		var sid = $(this).prev().attr("value");
		var rid =  $(this).prev().attr("rid");
		var li = $(this).parent();
		ajaxRequest(url,"func=delShortcut&sid="+sid,function(data){
			if(data.result){
				li.remove();
				$("li[value="+rid+"] span.shortcutStatus").html("<span class='addShortcut'title='添加到推荐阅读'>添加</span>");
			}
		});
		
	});
	
	
	$("ul.tag-list").sortable({
		connectWith : 'ul.tag-list',
		
		update : function(event,ui){
			//拖拽动作停止
			
			var newSort='';
			
			$('#choiceShortCut li').each(function(i,n){
				newSort+=$(n).children("label").attr("value")+",";
			})
			if(newSort.indexOf(',')>-1){
				newSort=newSort.substring(0,newSort.lastIndexOf(','));
			}
			ajaxRequest(url,"func=sortShortcut&sortIds="+newSort,function(data){
				//回调
				if(data!=null&&data["error"]!=null){
					alert(data["error"]);			
				}else{
					updateInfo("顺序已保存");
				}	
			});
		}
	});
	
	function updateInfo(str){
		$('#information').html('<font color="red">'+str+'</font>');
		setTimeout(function(){
			$('#information').html("");
		},2000);
	}
	
	$("#reloadShortcutPan").live("click",function(){
		var teamId = $("#tid").val();
		var tgid = $("#tgid").val();
		$(this).parent().addClass("current");
		$(this).parent().next().removeClass("current");
		ajaxRequest(url,"func=reloadShortcutPan&teamId="+tid+"&tgid="+tgid,function(data){
			addSortcutToPan(data);
		});
		
	});
	
	$("#shortcutKeyword").keyup(function(event){
		var val = event.which;
		if(val==13){
			$("#shortcutSearch").trigger('click');
		}
	});
	
	$("#shortcutSearch").live("click",function(){
		var keyword = $("#shortcutKeyword").val();
		if(keyword==""||keyword==null){
			alert("请输入搜索内容！");
			return;
		}
		$(this).parent().prev().removeClass("current");
		$(this).parent().addClass("current");
		var tgid = $("#tgid").val();
		ajaxRequest(url,"func=searchResult&keyword="+keyword+"&tgid="+tgid,function(data){
			addSortcutToPan(data);
		});
		
	});
	
	
	addSortcutToPan = function(data){
		$("#shortcutlist").children().remove();
		if(data!=null&&data["results"]!=null){
			$.each(data.results,function(index,item){
				if(item.choice){
					$("#add-shortcutPan-choice").tmpl(item).appendTo($("#shortcutlist"))
				}else{
					$("#add-shortcutPan-notchoice").tmpl(item).appendTo($("#shortcutlist"))
					
				}
			});
		}
	};
	
});
</script>


<script type="text/html" id="add-choiceShortcut-template">
				<li>
					<span class="{{= resourceType }}  headImg {{= resourceFileType}}"></span>
					<label  id="shortcut-for-{{= sid}}" class="shortcut-option multiple" key="shoutcut" value="{{= sid}}" rid="{{= rid}}" ><a style="color:#ff6600;" target="_blank" href="{{= resourceUrl}}">{{= resourceTitle}}</a></label>
					<a class="lightDel delChoiceShortcut" id="shortcut-for-{{= sid}}" title="删除"></a>
					<div><input class="shortcutColor" type="text" name="{{= sid}}" value="#ff6600" /></div>
					<div class="ui-clear"></div>
				</li>
</script>
<script type="text/html" id="add-shortcutPan-choice">
<li value="{{= rid}}">
	<span class="{{= resourceType}} headImg {{= resourceFileType}}"></span>
	<label  id="shortcut-for-{{= sid}}" class="shortcut-option multiple" key="shoutcut" value="{{= rid}}"><a href="{{= resourceUrl}}" target="_blank">{{= resourceTitle}}</a></label>
		<span class="shortcutStatus">已添加</span>
	<div class="ui-clear"></div>
</li>
</script>

<script type="text/html" id="add-shortcutPan-notchoice">
<li value="{{= rid}}">
	<span class="{{= resourceType}} headImg {{= resourceFileType}}"></span>
	<label  id="shortcut-for-{{= sid}}" class="shortcut-option multiple" key="shoutcut" value="{{= rid}}"><a href="{{= resourceUrl}}" target="_blank">{{= resourceTitle}}</a></label>
		<span class="shortcutStatus"><span class="addShortcut" title="添加到推荐阅读">添加</span></span>
	<div class="ui-clear"></div>
</li>
</script>

