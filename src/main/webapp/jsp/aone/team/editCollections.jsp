<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/aone.css" type="text/css" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/uiLib.css" type="text/css" />

<script type="text/javascript" src="${contextPath}/jsp/aone/js/uiLib-jQuery.js"></script>
<script type="text/javascript" src="${contextPath}/jsp/aone/js/global.js"></script>
<script type="text/javascript">
$(document).ready(function(){
	var posX = 0;
	var posY = 0;
	var drag = $('#drag');
	var ori, target;
	var stepCtrl = 5; //to avoid too much actions while mouse moving. see drag.mousemove(..)
	var area = ''; //record the origin of a drag, avoid drog to different area
	
	function dragPos(item, mouse) {
		if (typeof item != 'undefined') {
			var top = item.offset().top;
			var bottom = item.offset().top + item.outerHeight();
			var range = item.outerWidth()/2;
			var center = item.offset().left + range;
			if (mouse.pageY > top && mouse.pageY < bottom) {
				if (mouse.pageX >= center && mouse.pageX < (center+range)) { //right
					return 1;
				}
				else if (mouse.pageX < center && mouse.pageX > (center-range)) {//left
					return -1;
				}
				else {//out of range
					return 0;
				}
			}
			else {//out of range
				return 0;
			}
		}
	}

	function setDropZone(mouse) {
		function removeTarget() {
			$('.targetHolder').each(function(){
				if ($(this).hasClass('oriHolder')) {
					$(this).removeClass('targetHolder');
				}
				else {
				//	$(this).animate({width:0}, 100).remove();
					$(this).remove();
				}
			});
		}
		function growTarget() {
		//	$('.targetHolder').animate({width:drag.width()}, 100);
			if ($('.targetHolder').hasClass('oriHolder')) {
			//	$('.oriHolder').css('width', '');
			}
			else {
			//	$('.oriHolder').animate({width:0}, 100);
			}
		}
		
		var landing;
		var pos;
		//find the landing item
		area.children('li').each(function(){
			pos = dragPos($(this), mouse);
			if (pos!=0) {
				landing = $(this);
			}
		});
		if (typeof landing == 'undefined') {
			//no matches: see if it's at the end of the list
			var last = $('.item li:last');
			if (mouse.pageX > last.offset().left
					&& mouse.pageY > last.offset().top && mouse.pageY < last.offset().top+last.outerHeight()) {
				//end of list
				landing = last;
				pos = 1;
			}
			else {
				if (typeof $('.targetHolder') != 'undefined')
					landing = $('.targetHolder');
				else
					landing = ori;
				pos = 0;
			}
		}
		else {
			pos = dragPos(landing, mouse);
		}
		
		//develope drop zone
		if (landing.hasClass('oriHolder')) {
			//landing on origin position
			removeTarget();
			landing.addClass('targetHolder');
			growTarget();
		}
		else if (!landing.hasClass('targetHolder')){
			//landing on other position
			
			switch (pos) {
			case -1: //left
				if (landing.prev().hasClass('targetHolder')) {
					//left is already target
				}
				else if (landing.prev().hasClass('oriHolder')) {
					//left is original
					removeTarget();
					ori.addClass('targetHolder');
				}
				else {
					removeTarget();
					landing.before('<li class="targetHolder">&nbsp;</li>');
					growTarget();
				}
				break;
			case 1: //right
				if (landing.next().hasClass('targetHolder')) {}
				else if (landing.next().hasClass('oriHolder')) {
					removeTarget();
					ori.addClass('targetHolder');
				}
				else {
					removeTarget();
					landing.after('<li class="targetHolder">&nbsp;</li>');
					growTarget();
				}
			}//end switch
		}
	} 
	
	$('.item li').live('mousedown', function(e){
		//initiate
		var step = stepCtrl;
		ori = $(this);
		ori.addClass('oriHolder');
		posX = ori.offset().left - e.pageX;
		posY = ori.offset().top - e.pageY;
		area = ori.parent();

		drag.html(ori.html());
		drag.css('left', ori.offset().left).css('top', ori.offset().top).show();

		//drag the box
		drag.mousemove(function(e){
			var mouse = e;
			//move box
			drag.css('left', e.pageX+posX).css('top', e.pageY+posY);

			if (step==0) {
				setDropZone(mouse);
				step = stepCtrl;
			}
			else {
				step--;
			}
		});
		
	});

	function removeActions(){
		$('#drag').unbind();
	}

	function release(){
		var target = $('.targetHolder');
		var landing = '';
		if (target.length!=0 && !target.hasClass('oriHolder')) {
			//has set target && is not origin
			landing = target;
			//ori.animate({ width:0, padding:0 }, 100, function(){ $(this).remove(); });
			ori.remove();
			drag.animate({
				top: target.offset().top,
				left: target.offset().left
			}, 300, function(){
				target.removeClass('targetHolder').attr('id', ori.attr('id')).html(ori.html());
				drag.hide();
			});
		}
		else {
			//unsuccessful drag || landing on origin
			landing = ori;
			drag.animate({
				top: ori.offset().top,
				left: ori.offset().left
			}, 300, function(){ drag.hide(); ori.removeClass('oriHolder').removeClass('targetHolder'); });
		}
		drag.html('');
		$('.item li').css('width', '');
		//set a notification to show where it lands
		landing.addClass('landing');
		setTimeout(function(){ landing.removeClass('landing'); }, 1000); 
		
	}
	
	$('body').mouseup(function(){
		if (typeof(ori)!='undefined') {
			release();
			removeActions();
		}	
	});

	
	$("#show-create-dialog").live("click",function(){
		ui_showDialog("add-collection-dialog");
	});
	$('#createCollection a').removeAttr('href').click(function(){
		ui_showDialog('add-collection-dialog');
	});
	
	/* initiate */
	var hash = (window.location.hash)?window.location.hash:'';
	if (hash.substring(1)=='create') {
		ui_showDialog('add-collection-dialog');
	}
	
	$('#saveChange').click(function(){
		ui_spotLight('saveChange-spotLight', 'success', '更改已保存', 'fade');
	});
	
	$('#col').click(function(){ $('#msg-col').toggle(); });
	$('#pubCol').click(function(){ $('#msg-pubCol').toggle(); });
	
	var currentValidator = $("#create-collection-form").validate({
		rules: {name: {required: true}},
		messages:{name:{required:"请输入集合名称"}}
	});
});

</script>
<div class="content-through">
	<form id="collectionForm" action="<vwb:Link context='editCollections' format='url'/>" method="POST">
		<input type="hidden" name="func" value="adjustPlace"/>
		<div class="dragDropText">
			<div class="ui-RTCorner">
				<a id="col" class="ui-iconButton help">集合</a>
				<br/>
				<a id="pubCol" class="ui-iconButton help">公开集合与授权集合</a>
				<div id="msg-col" class="msgFloat" style="width:32em; left:50%;">
					<p><strong>集合</strong>是团队中的一个子空间，用于分类存放相应的内容、文件和图片。</p>
					<p>您可以根据需要，为一个课题小组、一个项目创建集合，以便沟通管理；也可以为所有的规章制度文档创建一个集合，以集中存放、方便查找。</p>
					<p>总之，集合是为某个主题而开创的小空间。每个集合还可以单独设置阅读、编辑权限。</p>
				</div>
				<div id="msg-pubCol" class="msgFloat" style="width:32em; left:55%;">
					<p><strong>公开集合</strong>，是那些可供所有团队成员查看的集合，拥有“公开管理”、“公开编辑”和“公开查看”权限的集合都是公开集合。</p>
					<p>公开集合会出现在页面上方导航栏的首行，所有人都能看到；您对其顺序的调整也是所有人都可以看到的。</p>
					<p><strong>授权集合</strong>，是只供团队中个别成员查看的集合。它们出现在导航栏下部，需要靠右侧“更多”按钮才能展开看到。</p>
					<p>您将只能看到对您开放权限的那些集合，对他们的排序也只是针对您个人的。</p>
				</div>
			</div>
			<h3>管理集合</h3>
			<p><input type="button" id="show-create-dialog" value="创建集合" class="largeButton" /></p>
		</div>
		<div class="dragDropText">
			<hr/>
			<p>拖动集合名字，以调整集合在导航栏中的显示顺序。</p>
			<h4>公开集合</h4>
		</div>
		<div id="publicCollections" class="dragDropWrap" onselectstart="return false">
			<ul class="item">
				<c:forEach items="${publicCollections}" var="item" varStatus="status">
					<li>
						<input type="hidden" name="publicCollection" value="${item.resourceId}"/>
						${item.title}
					</li>
				</c:forEach>
			</ul>
		</div>
		
		<div class="dragDropText">
			<h4>授权集合</h4>
		</div>
		<div id="authorizedCollections" class="dragDropWrap" onselectstart="return false">
			<ul class="item">
				<c:forEach items="${protectedCollections}" var="item" varStatus="status">
					<li>
						<input type="hidden" name="protectedCollection" value="${item.resourceId}"/>
						${item.title}
					</li>
				</c:forEach>
			</ul>
			<div id="drag"></div>
		</div>
		
		<div class="dragDropText largeButtonHolder">
			<input type="submit" value="保存更改" id="saveChange" />
			<span class="ui-spotLight" id="saveChange-spotLight"></span>
		</div>
		
	</form>
	
	<div class="bedrock"></div>
</div>
	
	<div class="ui-dialog-cover"></div>
	<div class="ui-dialog" id="add-collection-dialog" style="width:400px;">
		<span class="ui-dialog-x"></span>
		<p class="ui-dialog-title">
			新建集合
		</p>
		<form id="create-collection-form" action="<vwb:Link context='editCollections' format='url'/>" method="POST">
			<div class="ui-dialog-body">
				<input type="hidden" name="func" value="create"/>
				<input type="hidden" name="sequence" value="65536"/>
				<input type="hidden" name="defaultAuth" value="public"/>
				<table class="ui-table-form" >
					<tr><th id="page-option">集合名称：</th>
						<td><input type="text" name="name" style="width:180px;" /></td>
					</tr>
					<tr><th id="column-option">集合描述：</th>
						<td><textarea type="text" name="description" style="width:180px;"></textarea></td>
					</tr>
				</table>
			</div>
			<div class="ui-dialog-control">
				<input type="submit" id="create-collection-button" value="保存"/>
				<a class="ui-dialog-close ui-text-small">取消</a>
			</div>
 		</form>
	</div>

