<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />

<jsp:include page="tagMenu.jsp"/>
<style>
	#shortcutShow {margin:0px 0 0px 0; border:none}
</style>
<div class="content-menu-body">
	<div class="toolHolder">
		<ul id="spaceSwitch" class="switch">
			<li class="chosen"><a href="#common" id="switchCommon">常用</a></li>
			<li><a href="#trace" id="switchTrace">历史记录</a></li>
		</ul>
	</div>
	<div id="commonlyUse" style="display:none">
		<div style="margin:0 20px; border-bottom:1px dotted #ccc;" >
			<jsp:include page="/jsp/aone/collection/shortCut.jsp"/>
		</div>
		<c:if test="${empty reslist}">
			<!-- <p class="NA large">这里显示您经常浏览、编辑的文档。</p> -->
			<p class="NA large">您还没有浏览、编辑过任何文档，您可以：</p>
			<div class="NA" style="font-size:1.1em;line-height:2em;">
				<!-- <p>您还没有浏览、编辑过任何文档，您可以：</p> -->
				<ul>
					<li><a href="<vwb:Link format='url' context='quick'/>?func=createPage">
						<span class="iconLynx icon-page hover"></span>
						新建页面</a>
					</li>
					<li><a href="<vwb:Link format='url' context='quick'/>?func=uploadFiles&bid=0">
						<span class="iconLynx icon-upload hover"></span>
						上传文件和图片</a>
					</li>
					<li><a href="<vwb:Link context='tag' format='url'/>">查看其它团队成员贡献的文档</a></li>
				</ul>
			</div>
			
		</c:if>
		<ul id="grid9">
			<c:forEach var="item" items="${reslist}" varStatus="status">
				<li class="page-link" value="${item.rid}" itemtype="${item.itemType}"  level="${status.index}">
					<a class="wrapper">
						<h2>
						<c:choose>
							<c:when test="${item.itemType eq 'Bundle'}">
								<span class="headImg ${item.itemType }"></span>
							</c:when>
							<c:when test="${item.itemType eq 'DPage'}">
								<span class="headImg ${item.itemType }"></span>
							</c:when>
							<c:otherwise>
								<span
									class="headImg ${item.itemType } ext ${item.fileType}"></span>
							</c:otherwise>
						</c:choose>
						${item.title }</h2>
						<div class="resChangeLog">
							<p>${item.lastEditorName} 修改于<fmt:formatDate value="${ item.lastEditTime}" pattern="yyyy-MM-dd HH:mm:ss"/></p>
							<c:if test="${item.itemType ne 'Bundle'}">
								<p>版本 ：${item.lastVersion}</p>
							</c:if>
						</div>
					</a>
					<a class="pin" title="标记为永久">
						<span class="iconLynxTag icon-pin <c:if test="${gridItems[status.index].fixed}">pinned</c:if>"></span>
					</a>
					<a class="largeButton dim kickout" itemid="${item.rid}" itemtype="${item.itemType}">移除</a>
				</li>
			</c:forEach>
			<li class="ui-clear" style="border:none;"></li>
		</ul>
	</div>
	
	<div id="myTrace" style="display:none">
		<jsp:include page="history.jsp"></jsp:include>
	</div>
			
			<div id="intro_common_1" class="intro_step">
				<div class="title">管理员在此设置团队权限、邀请成员。</div>
				<a class="Iknow" id="Iknow_common_1">下一功能</a>
				<a class="closeMe">跳过</a>
			</div>
			<div id="intro_common_2" class="intro_step">
				<div class="title">团队中的更新信息以及协作消息，<br><br>都可以在这里看到~</div>
				<a class="Iknow" id="Iknow_common_2">下一功能</a>
				<a class="closeMe">跳过</a>
			</div>
			<div id="intro_common_3" class="intro_step">
				<div class="title">自动为您最常用的页面创建<br><br>九宫格。</div>
				<a class="Iknow" id="Iknow_common_3">下一功能</a>
				<a class="closeMe">跳过</a>
			</div>
			<div id="intro_common_4" class="intro_step">
				<div class="title">团队的全部文档，您的常用页面<br><br>以及工作记录都在这里！<b>去看看~</b></div>
				<a class="Iknow" id="Iknow_common_4">完成</a>
			</div>
			<div id="mask_common_1" class="intro_mask"></div>
<div class="ui-clear"></div>

<script type="text/html" id="grid-template">
<li>
	<a href="#somepage">
		<img src="/dct/dataCollect/images/tutor-select.jpg" />
		<h3>{{= title}}</h3>
		<p>{{= lastEditorName}}修改于{{= lastEditTime}}</p>
	</a>
	<div class="grid9Pin"></div>
</li>
</script>

<script type="text/javascript"  src="${contextPath}/scripts/jquery/jquery.hashchange-1.3.js"></script>
<script type="text/javascript">
$(document).ready(function(){
	
// intro steps begin
	$("#mask_common_1").css({
		"width":$(document.body).outerWidth(), //window.innerWidth
		"top":0 - $("#body.ui-wrap.wrapper1280").offset().top,
		"left":0 - $("#body.ui-wrap.wrapper1280").offset().left -11,
	});
	
	function clear(){
		$("#addShortcutButton").remove();
		if($(".shortcutItem").length>0){
			$("#shortcutShow").show();
		}else{
			$("#shortcutShow").hide();
		}
	};
	$(function(){
			showOpen();
	});
	clear();
	
	var coverStyle = setInterval(function(){
		if($(document.body).outerHeight() > $(window).height()){ 
			$("#mask_common_1").css({
				"height":$(document.body).outerHeight(),
			});
		}
		else{
			$("#mask_common_1").css({
				"height":window.innerHeight,
			});
		}
	},20);
	
	
	$("#macro-innerWrapper").css({"z-index":"51"});
	var step;
	if ($("#teamConfig").length > 0) {
		totalStep = 4;
	}
	else {
		totalStep = 3;
	}
	
	$.ajax({
		//url:'http://localhost:8080/dct/system/userguide',
		url:site.getURL('userguide',null),
		type:'POST',
		data:"func=get&module=common",
		success:function(data){
			data = eval("("+data+")");
			step = data.step;
			if(step < totalStep) {
				//showTheVeryStep(step);
				if ($("#teamConfig").length > 0) {
					$("#mask_common_1").show();
					$("#teamConfig").addClass("isHighLight");
					$(".intro_step#intro_common_1").css({
						"top":$("#banner-innerWrapper #teamConfig").offset().top - 167,
						"left":$("#banner-innerWrapper #teamConfig").position().left - 40,
					});
					$("#intro_common_1").show();
					$("#intro_common_2").hide();
				}
				else {
					$("#mask_common_1").show();
					$("#intro_common_2").show();
				}
				// view the unmanaged team before managed team.vera
				if (step == 3) {
					$("#intro_common_1").show();
					$("#intro_common_2").hide();
					$("#intro_common_1 #Iknow_common_1").html("完成");
					$("#intro_common_1 a.closeMe").remove();
					
					$("#Iknow_common_1").click(function(){
						$(this).parent().hide();
						$(this).parent().next().hide();
						$("#mask_common_1").hide();
						$(".isHighLight").removeClass("isHighLight");
						step = totalStep;
						postStep(step);
					});
				}
			}
		},
		error:function(){
			step = 0;
		},
		statusCode:{
			450:function(){alert('会话已过期,请重新登录');},
			403:function(){alert('您没有权限进行该操作');}
		}
	});
	
	
	/*if 0 < step < totalStep , this function is very useful*/
	var count = 1;
	function showTheVeryStep(step){
		$("#mask_common_1").show();
		//$("#intro_common_" + (count +1)).show();
		$(".isHighLight").removeClass("isHighLight");
		$(".readyHighLight" + count).addClass("isHighLight");
	} 
	
	$(".Iknow").click(function(){
		$("#mask_common_1").show();
		count++;
		$(this).parent().hide();
		$(this).parent().next().show();
		$(".isHighLight").removeClass("isHighLight");
		$(".readyHighLight" + count).addClass("isHighLight");
	});
	
	$("#Iknow_common_4").click(function(){
		$(this).parent().hide();
		$("#mask_common_1").hide();
		$(".isHighLight").removeClass("isHighLight");
		step = totalStep;
		postStep(step);
	});
	
	$(".closeMe").click(function(){
		$(this).parent().hide();
		$("#mask_common_1").hide();
		$(".isHighLight").removeClass("isHighLight");
		step = totalStep;
		postStep(step);
	})
	
	function postStep(step){
		$.ajax({
			//url:site.getURL('tag',null),
			url:site.getURL('userguide',null),
			type:'POST',
			data:"func=update&module=common&step="+step,
			success:function(data){},
			error:function(){},
			statusCode:{
				450:function(){alert('会话已过期,请重新登录');},
				403:function(){alert('您没有权限进行该操作');}
			}
		});
	}
	
	// intro steps end
	
	$('#grid9 a.pin').each(function(){
		$(this).checkItem({
			checkClass: 'pinned',
			getCheckObject: function($i) { return $i.children('.icon-pin'); },
			makeUrl: function(curr){
  				var tempURL = site.getURL("teamHome",null);
  				var obj = curr.parent();
  				if (curr.children('.icon-pin').hasClass('pinned')) 
  					return tempURL+'?func=unpin&rid='+ $(obj).attr('value')+'&itemType='+$(obj).attr('itemtype')+"&level="+$(obj).attr('level');
  				else
  					return tempURL+'?func=pin&rid='+ $(obj).attr('value')+'&itemType='+$(obj).attr('itemtype')+"&level="+$(obj).attr('level');
  			},
			whenSuccess: function(data, obj) {
				obj.checkThis();
			}
		});
	});
	
	$('#switchCommon').click(function(){commonState($(this));});
	
	$('#switchTrace').click(function(){traceState($(this));});
	
	function commonState(obj){
		$('.chosen').removeClass('chosen');
		$(obj).parent().addClass('chosen');
		$('#myTrace').hide();
		$('#commonlyUse').show();
	};
	
	function traceState(obj){
		$('.chosen').removeClass('chosen');
		$(obj).parent().addClass('chosen');
		$('#commonlyUse').hide();
		$('#myTrace').show();
		
		filterTrace(location.hash.substring(7));
		// hash = '#trace-stateRef'
	}
	
	function filterTrace(ref) {
		var traceState = ['create', 'upload', 'modify', 'comment', 'recommend','delete'];
		var allClass = '';
		for (var i=0; i<traceState.length; i++) {
			allClass += 'show-' + traceState[i] + ' ';
		}
		
		
		if (arrIndexOf(traceState, ref)>-1) {
			$('#myTrace').removeClass(allClass).addClass('show-'+ref);
			$('#historyTool li.chosen').removeClass('chosen');
			$('#historyTool a[filter="' + ref +'"]').parent().addClass('chosen');
			window.location.hash = '#trace-' + ref;
		}
		else {
			$('#myTrace').removeClass(allClass);
			$('#historyTool li.chosen').removeClass('chosen');
			$('#historyTool a[filter="all"]').parent().addClass('chosen');
			window.location.hash = '#trace';
		}
	}
	$('#historyTool li a').click(function(){
		if ($(this).parent().hasClass('chosen')) {
			filterTrace();
		}
		else {
			filterTrace($(this).attr('filter'));
		}
	});
	
	$("a.kickout").live('click',function(event){
		event.stopPropagation();
		var $a = $(this);
		var url = site.getURL("teamHome");
		var params = {"func":"kickout","rid":$(this).attr("itemid"),"itemType":$(this).attr("itemtype")};
		ajaxRequest(url,params,function(data){
			if(data.status)
				$a.parent().remove();
		});
	});
	
	$("li.page-link").live('click',function(){
		  if($(this).attr("itemtype")=='DPage')
			  	window.location.href = site.getViewURL($(this).attr("value"));
		   else if($(this).attr("itemtype")=="DFile")
				window.location.href = site.getURL('file',$(this).attr("value"));
		   else
				window.location.href = site.getURL('bundle',$(this).attr("value"));
	});
	$(".recoverResource").live('click',function(){
		var targetId = $(this).find("input[name='targetId']").val();
		var targetType = $(this).find("input[name='targetType']").val();
		var url = site.getURL("teamHome");
		var params ={"func":"recoverResource","rid":targetId,"itemType":targetType};
		ajaxRequest(url,params,function(data){
			if(data.status){
				window.location.href=data.redirectURL;
			}else{
				alert(data.message);				
			}
		});
	});
	
  	$(window).hashchange(function(){
		var hash = location.hash;
    	if(hash.substring(0,6)=='#trace'){
    		traceState($("#switchTrace"));
    	}else{
			commonState($("#switchCommon"));
    	}
  	});
  	
  	$(window).hashchange();
});
</script>

</html>