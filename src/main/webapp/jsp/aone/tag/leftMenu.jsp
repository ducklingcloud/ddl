<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />

	<div id="tagSelector"  class="content-menu readyHighLight0">
		<p class="ui-navList-title">
			<a class="iconLink config ui-RTCorner" href='<vwb:Link context='configTag' format='url'/>' title="管理标签"></a>
			<a href="${contextPath}/ddlTagHelp.jsp" target="_blank" class="ui-iconButton help tagHelp"></a>
		文件导航</p>
		<ul class="ui-navList">
			<li><a class="filter-option single" key="filter" value="all" ><span class="tagTitle">所有文件</span></a></li>
			<li><a class="filter-option single" key="filter" value="untaged" ><span class="tagTitle">无标签文件</span></a></li>
		</ul>
		<c:forEach items="${tagGroups}" var="gitem">
			<p class="ui-navList-title tagGroupTitle subNavList">
				<%-- <a class="iconLink config ui-RTCorner" href='<vwb:Link context='configTag' format='url'/>' title="管理标签"></a> --%>
				<a class="iconFoldable" id="tag1Control" title="展开/折叠"></a>
				${gitem.group.title}
			</p>
			<ul class="ui-navList">
				<c:if test="${empty gitem.tags}">
					<li class="NA">无标签</li>
				</c:if>
				<c:forEach items="${gitem.tags}" var="tagItem">
					<li><a id="tag-for-${tagItem.id}" class="tag-option multiple" key="tag" value="${tagItem.id}">
						<span class="tagTitle">${tagItem.title}</span><span class="tagResCount">${tagItem.count}</span>
						</a>
						<a class="addToQuery"><span>+</span></a>
					</li>
				</c:forEach>
			</ul>
		</c:forEach>
	<c:if test="${not empty tags}">
		<p class="ui-navList-title tagGroupTitle subNavList">
			<%-- <a class="iconLink config ui-RTCorner"  href="<vwb:Link context='configTag' format='url'/>" title="管理标签"></a> --%>
			<a class="iconFoldable" id="tag1Control" title="展开/折叠"></a>
			未分类标签
		</p>
		<ul class="ui-navList" id="ungrouped-tag-list">
			<c:forEach items="${tags}" var="item">
				<li><a id="tag-for-${item.id}" class="tag-option multiple" key="tag" value="${item.id}">
					<span class="tagTitle">${item.title}</span><span class="tagResCount">${item.count}</span>
					</a>
					<a class="addToQuery" title="同时选中该标签"><span>+</span></a>
				</li>
			</c:forEach>
		</ul>
	</c:if>
	</div>
	
			<div id="intro_team_1" class="intro_step">
				<div class="title">标签，可以帮助您将文档按类别<br><br>整理得井井有条。</div>
				<a class="Iknow" id="Iknow_team_1">下一功能</a>
				<a class="closeMe">跳过</a>
			</div>
			<div id="intro_team_2" class="intro_step">
				<div class="title">可以使用列表、缩略图等形式<br><br>展示文档。</div>
				<a class="Iknow" id="Iknow_team_2">下一功能</a>
				<a class="closeMe">跳过</a>
			</div>
			<div id="intro_team_3" class="intro_step">
				<div class="title">您可以通过修改时间、文档类型或<br><br>多标签组合对文档快速定位。</div>
				<a class="Iknow" id="Iknow_team_3">下一功能</a>
				<a class="closeMe">跳过</a>
			</div>
			<div id="intro_team_4" class="intro_step">
				<div class="title">推荐阅读，让你更快寻找到团队的重要文档。</div>
				<a class="Iknow" id="Iknow_team_4">完成</a>
			</div>
			<div id="mask_team_1" class="intro_mask"></div>

<script type="text/javascript"  src="${contextPath}/scripts/jquery/jquery.hashchange-1.3.js"></script>
<script type="text/javascript"  src="${contextPath}/jsp/aone/js/stateToggle.js"></script>
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/tokenInput.css" type="text/css" />
<script type="text/javascript" src="${contextPath}/jsp/aone/js/toker-jQuery-forTag.js"></script>
<script type="text/javascript">
$(document).ready(function(){
	
	$('input.tagPoolAutoShow').tokenInput("<vwb:Link context='tag' format='url'/>?func=loadTeamTags&type=nogroup", {
		theme:"facebook",
		hintText: "输入标签名称，以逗号或回车结束",
		searchingText: "正在搜索……",
		noResultsText: "没有该标签，输入逗号或回车添加",
		preventDuplicates: true,
	}); 
	
	// leftMenu show firstFive begin
	$("#tagSelector").children(".ui-navList").each(function(){
		var childrenNum = $(this).find("li").length;
		var i = 0;
		if(childrenNum > 5) {
			$(this).find("li").each(function(){
				i++;
				if(i>5){
					$(this).hide();
				}
			})
		}
	})
	
	$("#tagSelector").children(".ui-navList").mouseenter(function(){
		$(this).children().show("normal");
	})
	$("#tagSelector").children(".ui-navList").mouseleave(function(){
		var j = 0;
		$(this).children().each(function(){
			j++;
			if(j>5){
				$(this).hide();
			}
		})
	})
	// leftMenu show firstFive end
	// intro steps begin
	
	$("#mask_team_1").css({
		"width":$(document.body).outerWidth(),
		"top":0 - $("#body.ui-wrap.wrapper1280").offset().top,
		"left":0 - $("#body.ui-wrap.wrapper1280").offset().left -11,
	});
	
	var coverStyle = setInterval(function(){
		if($(document.body).outerHeight() > $(window).height()){ 
			$("#mask_team_1").css({
				"height":$(document.body).outerHeight(),
			});
		}
		else{
			$("#mask_team_1").css({
				"height":window.innerHeight,
			});
		}
	},20);
	
	
	$("#macro-innerWrapper").css({"z-index":"51"});
	var step;
	var totalStep = 3;
	$.ajax({
		//url:'http://localhost:8080/dct/system/userguide',
		url:site.getURL('userguide',null),
		type:'POST',
		data:"func=get&module=team",
		success:function(data){
			data = eval("("+data+")");
			step = data.step;
			if(step < totalStep) {
				showTheVeryStep(step);
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
		$("#mask_team_1").show();
		if(step == 0 ){
			$("#intro_team_" + (count + 1)).show();
			$(".isHighLight").removeClass("isHighLight");
			$(".readyHighLight" + count).addClass("isHighLight");
		}
		else{
			$("#intro_team_" + (step +2)).show();
			$(".isHighLight").removeClass("isHighLight");
			$(".readyHighLight" + (step+1)).addClass("isHighLight");
		}
		
	}
	
	$("#shortcutShow").addClass("readyHighLight3");
	$(".Iknow").click(function(){
		$("#mask_team_1").show();
		count++;
		$(this).parent().hide();
		$(this).parent().next().show();
		$(".isHighLight").removeClass("isHighLight");
		$(".readyHighLight" + count).addClass("isHighLight");
	});
	
	$("#Iknow_team_4").click(function(){
		$(this).parent().hide();
		$("#mask_team_1").hide();
		$(".isHighLight").removeClass("isHighLight");
		step = totalStep;
		postStep(step);
	});
	
	$(".closeMe").click(function(){
		$(this).parent().hide();
		$("#mask_team_1").hide();
		$(".isHighLight").removeClass("isHighLight");
		step = totalStep;
		postStep(step);
	})
	
	function postStep(step){
		$.ajax({
			url:site.getURL('userguide',null),
			type:'POST',
			data:"func=update&module=team&step="+step,
			success:function(data){},
			error:function(){},
			statusCode:{
				450:function(){alert('会话已过期,请重新登录');},
				403:function(){alert('您没有权限进行该操作');}
			}
		});
	}
	
	// intro steps end
	
/* view mode: table, tight or grid */
	var viewMode = $.cookie('tagItems-viewMode') || 'Tight';
	
	$('#showAsTable a').click(function(){
		viewMode = 'Table';
		$('#resourceList').addClass('asTable')
			.removeClass('grid9').removeClass('asTight')
			.pitfall('clean');
		
		$(this).parent().parent().find('.chosen').removeClass('chosen');
		$(this).parent().addClass('chosen');
		$.cookie('tagItems-viewMode', 'Table');
	});
	$('#showAsGrid a').click(function(){
		viewMode = 'Grid';
		$('#resourceList').addClass('grid9')
			.removeClass('asTable').removeClass('asTight')
			.pitfall();
	
		$(this).parent().parent().find('.chosen').removeClass('chosen');
		$(this).parent().addClass('chosen');
		$.cookie('tagItems-viewMode', 'Grid');
	});
	$('#showAsTight a').click(function(){
		viewMode = 'Tight';
		$('#resourceList').addClass('asTight')
			.removeClass('grid9').removeClass('asTable')
			.pitfall('clean');
		
		$(this).parent().parent().find('.chosen').removeClass('chosen');
		$(this).parent().addClass('chosen');
		$.cookie('tagItems-viewMode', 'Tight');
	})
	$('#showAs' + viewMode + ' a').click();
/* END view mode */

/* Resource Selector */
	var selectConsole = new shadeConsole({
		'console': $('#resourceAction'),
		'anchor': $('#tagItemsJSP.content-menu-body')
	});
	
	var selector = new resSelector(selectConsole);
	
/* END resource selector */

/*  Export to Zip or EPub start*/
	$('#export-zip').click(function(){
		var rids = selector.getRidArr();
		var url = site.getURL("tag",null)+"?func=download&format=zip&rids[]="+rids;
		window.location.href = url;
	});
 
	$('#export-epub').click(function(){
		var rids = selector.getRidArr();
		var url = site.getURL("tag",null)+"?func=download&format=epub&rids[]="+rids;
		window.location.href = url;
	});
 /* Export to Zip or EPub end */
 /* Delete Tag start */
 	$("a.delete-tag-link").live('click',function(){
 		var rids = new Array();
 		rids[0] = $(this).parents("li.element-data").attr("item_id");
 		if(typeof(rids[0])=='undefined'){
 	 		rids = selector.getRidArr();
 		}
    	var params = {"func":"remove","rid[]":rids,"tagId":$(this).attr("tag_id")};
    	var url = site.getURL('tag',null);
    	var $a = $(this);
		ajaxRequestWithErrorHandler(url,params,function(data){
    		$a.parent().remove();
    		$.each(data.rids, function(index, element){
    			$("ul#tag-item-"+element.rid+" li[tag_id="+data.tagId+"]").remove();
    		});
    		var $tagCount = $("a#tag-for-"+data.tagId+" span.tagResCount");
    		var count = parseInt($tagCount.text());
    		count = (count-data.rids.length)>=0?(count-data.rids.length):0;
    		$tagCount.text(""+count);
    	},notEnoughAuth);
		$(".tagGroupHorizon ul").find('a[tag_id="'+ $(this).attr("tag_id") + '"]').parent().removeClass("chosen");
    });
 /* Delete Tag end */
 /* Delete Resource start*/
 	$("#toolDelete").click(function(){
 		var rids = selector.getRidArr();
 		$('#delete-resource-list').html("");
 		var html="";
 		$.each(rids, function(index, element){
 			var $parent = $('li.element-data[item_id='+element+']');
 			var $bundleChildren = $parent.children('div.showBundleChild').children('ul.bundleChildren');
 			if($bundleChildren.children('li').length>0){
 				html += "<li>"+$parent.children('div.resBody').children('h2').html();
 				html += "<ul class='bundleChildren'>"+$bundleChildren.html()+"</ul></li>";
 			}else{
 				html += "<li>"+$parent.children('div.resBody').children('h2').html()+"</li>";
 			}
 		});
 		$('#delete-resource-list').append(html);
 		$('#delete-resource-list a').attr("target","_blank");
 		ui_showDialog("delete-resource-dialog");
 	});
 
 	$("#delete-cancel").click(function(){
    	 ui_hideDialog("delete-resource-dialog");
     });
 
 	$("#delete-resource-submit").click(function(){
 		ui_hideDialog("delete-resource-dialog");
 		var rids = selector.getRidArr();
 		var url = site.getURL("tag",null)+"?func=deleteResource&rids[]="+rids+"&t="+Math.random();
 		ajaxRequest(url,"",afterDeleteResource);
 	});
 	
 	function afterDeleteResource(data){
 		if(data.lockStatus=='error'){
 			showPageLockErrorMessage(data);
 			return;
 		}
		if(!data.authValidate){
			showDeleltResource(data);
			return;
		}
 		if(!data.status){
 			alert("删除失败！");
 			return;
 		}
 		var rids = selector.getRidArr();
 		$.each(rids,function(index, element){
 			$("li.element-data[item_id="+element+"]").remove();
 		});
 		
 		$.each(data.tagCount,function(index, element){
 			var item = $('#tag-for-' + element.id + ' .tagResCount');
 			item.text(element.value);
 		});
		
 		selector.removeItem('all', true);
 		selector.refresh();
 		$("#deleteMsg").html("");
 		var html="<p style='text-align:left; padding-left:1em;'>资源："
 		if(data.auth.length==1){
		 	html ="<p style='text-align:centre; padding-left:1em;'>删除成功！"
 		}else{
			$.each(data.auth,function(index,d){
				if(index==0){
					html=html+d.title;
				}else{
					html=html+"，"+d.title;
				}
			});
	 		html=html+" 您已经删除！<p/>";
 		}
 		$("#deleteMsg").html(html);
		ui_showDialog("delete-error-msg-dialog",3500);
 	}
 	
 	function showDeleltResource(data){
 		$("#deleteMsg").html("");
		var html='';
 		if(data.noAuth.length > 0 && data.auth.length > 0){
 			html="<p style='color: red; text-align:left; padding-left:1em;'>资源："
 			$.each(data.noAuth,function(index,d){
 				if(index==0){
 					html=html+d.title;
 				}else{
	 				html=html+"，"+d.title;
 				}
 			});
 			html=html+" 您无权删除！<p/><p style='text-align:left; padding-left:1em;'>资源："
 			$.each(data.auth,function(index,d){
 				if(index==0){
 					html=html+d.title;
 				}else{
	 				html=html+"，"+d.title;
 				}
 				$("li.element-data[item_id="+d.rid+"]").remove();
 			});
 			$.each(data.tagCount,function(index, element){
 	 			var item = $('#tag-for-' + element.id + ' .tagResCount');
 	 			item.text(element.value);
 	 		});
 			
 	 		selector.removeItem('all', true);
 	 		selector.refresh();
 			html=html+" 您已经删除！<p/>";
 		}else if(data.noAuth.length>1){
 			html="<p style='color: red;text-align:left; padding-left:1em;'>资源：";
 			$.each(data.noAuth,function(index,d){
 				if(index==0){
 					html=html+d.title;
 				}else{
	 				html=html+"，"+d.title;
 				}
 			});
 			html=html+" 您无权删除！<p/>"
 		}else{
 			html = "<p style='color: red'>资源："+data.noAuth[0].title+" 您无权删除！<p/>";
 		}
		$("#deleteMsg").html(html);
		ui_showDialog("delete-error-msg-dialog",3500);
 	}
 /* Delete Resource end*/

/* add tag dialog */
	var tPool = new tagPool({
		pool: $('.tagGroupHorizon'),
		scroller: $('.tG-scroll'),
		blockClass: 'tG-block'
	});
	
	var aTBox = new addTagBox({
		input: $('input[name="typeTag"]'),
		tagList: $('.existTags ul.tagList'),
		tagTogether: $('.existTags ul.tagTogether'),
		tagSelf: $('.existTags ul.tagSelf'),
		tagCreate: $('.existTags ul.tagCreate'),
		tagPool: tPool
	});
	
	var addSingleTagDialog = new lynxDialog({
		'instanceName': 'addSnglTag',
		'dialog': $('#addSingleTagDialog'),
		'close': $('#addSingleTagDialog .closeThis'),
		'beforeShow': function() {
			tPool.refreshAppearance();
		},
		'afterHide': function() {
			aTBox.clean();
		}
	});
	
	var showPageLockErrorMessage = function(data){
		$("#pageLockErrorMessageDialogContent").html("");
		$("#pageLockErrorMessageTemplate").tmpl(data.lockError).appendTo("#pageLockErrorMessageDialogContent");
		ui_showDialog("pageLockErrorMessageDialog",3500);
	};
	
	$('#resAction-tag a').click(function(){
		aTBox.prepare({ ridArr: selector.getRidArr() });
		var ridArr = selector.getRidArr();
		$.each(ridArr, function(n, value){
			aTBox.prepare({ul: $('#resourceList li[item_id='+value+'] ul.tagList'), source:"toolBar"});
		});
		var chosenNum = $("ul#resourceList").find("li.chosen").length; 
		hideOrDisplayDiv(chosenNum);
		addSingleTagDialog.show();
		$("#token-input-").focus();
	});
	$('#resourceList .tagList li.newTag a').live('click', function(event){
		event.stopPropagation();
		aTBox.prepare({ ul: $(this).parent().parent(), source: "single" });
		hideOrDisplayDiv(1);
		addSingleTagDialog.show();
		$("#token-input-").focus();
	});
	
	function hideOrDisplayDiv(chosenNum){
		if (chosenNum == 1) {
			$("#addSingleTagDialog .self").hide();
			$("#addSingleTagDialog .change").html("已有标签：");
			$("#addSingleTagDialog .change2").html("新增标签：");
			$("#addSingleTagDialog .tagShow").css({"width":"47%"});
			$("#addSingleTagDialog .tagShow:last").css({"margin-left":"48%"});
			$("#addSingleTagDialog .tagShow:last").find("ul.tagList").appendTo($("#addSingleTagDialog .tagShow:last").prev());
		}
		else{
			$("#addSingleTagDialog .self").show();
			$("#addSingleTagDialog .change").html("全部公有标签：");
			$("#addSingleTagDialog .change2").html("本次新增标签：");
			$("#addSingleTagDialog .tagShow").css({"width":"23%"});
			$("#addSingleTagDialog .tagShow:last").css({"margin-left":"0"});
			$("#addSingleTagDialog .tagShow:last").prev().find("ul.tagList").appendTo($("#addSingleTagDialog .tagShow:last"));
		}
	}
	
	$("#addSingleTagDialog .saveThis").click(function(){
		var feedback = current_data_loader.addTagForSingleRecord();
		addSingleTagDialog.hide();
	});
	
/* END add tag dialog */
	
/* make bundle dialog */
	var makeBundleDialog = new lynxDialog({
		'instanceName': 'makeBundle',
		'dialog': $('#makeBundleDialog'),
		'trigger': $('#resAction-bundle a'),
		'close': $('#makeBundleDialog .closeThis')
	});
	$("#makeBundleDialog .saveThis").click(function(){
		var title = $("input[name='m-bundle-title']").val();
		if(!title){
			alert("组合名不能为空");
			return;
		}
		var params = {"itemKeys[]":selector.getRidArr(),"func":"makeBundle","title":$("input[name='m-bundle-title']").val()};
		current_data_loader.addBundle(params);
		makeBundleDialog.hide();
	});
	$("input[name='m-bundle-title']").keypress(function(event){
		var key = event.which;
		if(key==13){
			$("#makeBundleDialog .saveThis").trigger('click');
		}	
	});
/* END make bundle dialog */

	var offset = 0;
	var size = 9;
	var count = 0;
	var autoLoadMore;
	
	function getResourcePageURL(_offset,_size){ return "<vwb:Link context='resource' format='url'/>?func=query&offset="+_offset+"&size="+_size; };
	
	$(window).scroll(function(){
		if ($(window).scrollTop() + $(window).height() > $('#load-more-items').offset().top
			&& $('#load-more-items').is(':visible')		
		) {
			clearTimeout(autoLoadMore);
			autoLoadMore = setTimeout(function(){
				$('#load-more-items:visible').click();
			}, 700);
		}
	});
	$("#load-more-items").click(function(){current_data_loader.loadMoreRecords();});
	
	var notice_handler = {
			loading: function() {
				$('#load-more-items').hide();
				$('#no-result-helper').attr('style','display:none');
				$('#notice').addClass('large').text('正在载入...').fadeIn();
			},
			noMatch: function() {
				$('#load-more-items').hide();
				$('#notice').addClass('large').text('没有找到匹配的文档，您可以').show();
				$('#no-result-helper').removeAttr('style');
			},
			noMore: function() {
				$('#load-more-items').hide();
				$('#no-result-helper').attr('style','display:none');
				setTimeout(function(){
					$('#notice').removeClass('large').text('没有更多文档了').show();
				}, 500);
			},
			readyToLoad: function() {
				$('#load-more-items').show();
				$('#no-result-helper').attr('style','display:none');
				$('#notice').hide();
			},
			error : function(){
				$('#load-more-items').hide();
				$('#no-result-helper').attr('style','display:none');
				$('#notice').removeClass('large').text('请求失败！可能由于以下原因导致此问题：未登录，会话过期或权限不够！').fadeIn();
			}
	};
	
	function getNewTags(){
		var results = [];
		var i = 0;
		$(".existTags ul.tagCreate").children("li").each(function(){
			results[i] = $(this).text();
			i++;
		});
		
		return results;
	}
	
	function getExistTags(){
		var results = [];
		$(".existTags ul.tagList").children("li").each(function(){
			results.push( $(this).attr("tag_id"));
		});
		return results;
	}
	
	var _tagCache = null;
	var current_data_loader = ({
		reloadRecords:function(){
			offset = 0;
			notice_handler.loading();
			$("#resourceList .element-data").remove();
    	  	if (viewMode=='Grid') {
    	  		$('#resourceList').pitfall('clean');
    	  	}
    	  	ajaxRequestWithErrorHandler(getResourcePageURL(offset,size),location.hash.replace(/^#&/,''),function(data){
	    	  	renderData(data.array);
	    	  	renderShortcut(data.shortResouce);
	    	  	offset = data.count;
	    	  	if (data.count == 0) {
	    	  		notice_handler.noMatch();
	    	  	}
	    	  	else if (data.count < size) {
	    	  		notice_handler.noMore();
	    	  	}
	    	  	else {
	    	  		notice_handler.readyToLoad();
	    	  	}
	    	},notEnoughAuth);
		},
		loadMoreRecords:function(){
			notice_handler.loading();
			ajaxRequestWithErrorHandler(getResourcePageURL(offset,size),location.hash.replace(/^#&/,''),function(data){
	    	  	renderData(data.array);
	    	  	renderShortcut(data.shortResouce);
	    	  	if (data.count < size) {
	    	  		notice_handler.noMore();
	    	  	}
	    	  	else {
	    	  		notice_handler.readyToLoad();
	    	  	}
	    		offset = offset+data.count;
	    	},notEnoughAuth);
		},
		addTagForSingleRecord:function(){
			var tagURL = "<vwb:Link context='tag' format='url'/>";
			//var params = {"func":"batchAdd","newTags[]":aTBox.log.create,"existTags[]":aTBox.log.add,"rids[]":aTBox.log.rid};
			var params = {"func":"batchAdd","newTags[]":getNewTags(),"existTags[]":getExistTags(),"rids[]":aTBox.log.rid};
			ajaxRequestWithErrorHandler(tagURL,params,function(data){
				var newTagIndex = [];
				for (var i=0; i<data.length; i++) {
					$('#page-tag-template').tmpl(data[i]).prependTo('#tag-item-'+data[i].item_key);
					
					if (data[i].isNewTag && arrIndexOf(newTagIndex, data[i].id)==-1) {
						newTagIndex.push(data[i].id);
						$('new-tag-template').tmpl(data[i]).prependTo('#ungrouped-tag-list');
					}else {
						var item = $('#tag-for-' + data[i].id + ' .tagResCount');
						item.text(parseInt(item.text())+1);
					}
				}
				
				addSingleTagDialog.hide();
				selector.removeItem('all', true);
				if (viewMode=='Grid')
					$('#resourceList').pitfall();
				loadAllTeamTagsNow();
			},notEnoughAuth);
		},
		loadAllTeamTags:function(){
			var url = site.getURL("tag",null);
			ajaxRequest(url,"func=loadTeamTags",function(data){
				_tagCache = data;
				tPool.refresh(_tagCache);
				aTBox.refresh();
			});
		},
		addBundle:function(params){
			var url = site.getURL("quick",null);
			ajaxRequestWithErrorHandler(url,params,function(data){
				if(data.lockStatus=="error"){
					showPageLockErrorMessage(data);
					return ;
				}
				var itemKeys = selector.getRidArr();
				for(var i=0;i<itemKeys.length;i++){
					$("li[item_id='"+itemKeys[i]+"']").remove();
				}
			  	if(data.isChecked=='checked'){
			  		data.starTitle="取消标记";
				}else{
					data.starTitle="标记为星标文件";
				}
				$("#resource-template").tmpl(data).prependTo("#resourceList");
				renderResourceRecord(data);
				selector.removeItem('all', true);
				refreshState();
				$.each(data.tagCount, function(index, element){
					var tagid = element.id;
					var count = (element.count>=0)?element.count:0;
					$("a#tag-for-"+tagid+" span.tagResCount").text(""+count);
				});
				showConflictAddBundleItems(data.conflictItems);
			},notEnoughAuth);
		},
		addNavbarItem:function(){
			var url = site.getURL("navbar",null);
			var params = {"func":"addItem","title":$("#navbar-title").val(),"url":site.getURL("tag",null)+location.hash};
			ajaxRequest(url,params,function(data){
				$("#nav-bar-item-template").tmpl(data).appendTo("#customNav");
				customNavAdjust('refresh');
			});
		}
	});
	
	current_data_loader.loadAllTeamTags();

	function notEnoughAuth(){
		notice_handler.error();
	};
	
	/*******      显示组合冲突的资源信息        *********/
	function showConflictAddBundleItems(conflictItems){
		if(typeof(conflictItems)!="undefined" && conflictItems.length>0){
			$("#conflict-bundle-item-list").html("");
			$("#conflict-bundle-item-template").tmpl(conflictItems).appendTo("#conflict-bundle-item-list");
			ui_showDialog("conflict-bundle-item-dialog");
		}
	}
	
	$("#conflict-bundle-item-cancel").click(function(){
		ui_hideDialog("conflict-bundle-item-dialog");
	});
	
	function loadAllTeamTagsNow(){
		var url = site.getURL("tag",null);
		ajaxRequest(url,"func=loadTeamTags",function(data){
			_tagCache = data;
			tPool.refresh(_tagCache);
			aTBox.refresh();
		});
	}
	
/* tag Selector */	
  	$(window).hashchange(function(){
		var hash = location.hash;
    	var array = hash.split("&");
		
    	if (array.length>1) {
    		$('.addToQuery').fadeIn();
    	}
    	else {
    		$('.addToQuery').fadeOut();
    	}
    	
    	$("#tagSelector li.chosen, #timeSelector li.chosen, #typeSelector li.chosen").removeClass("chosen");
    	for(var i=0;i<array.length;i++){
    		if(i!=0){
	    		var kvpr = array[i].split("=");
    			if(kvpr.length!=2){
    			}else{
    				restoreState(kvpr[0],kvpr[1]);
    			}
    		}
    	}
    	current_data_loader.reloadRecords();
    	$("li.tQ-tag").remove();
    	$("li.chosen a.filter-option, li.chosen a.tag-option").each(function(){
    		var node = new Object();
    		node["tagId"] = $(this).attr("value");
    		node["title"] = $(this).children("span.tagTitle").html();
    		$("#selected-tags-template").tmpl(node).appendTo("#tagQuery");
    		if(node["tagId"]=="all"){
    			$("a.lightDel").remove();
    		}
    		$("input[type=checkbox][name=checkAll]").removeAttr("checked");
    	});
    	
    	var keyword = getParameterValue(window.location.hash, 'keyword');
    	for (var i=0; i<keyword.length; i++) {
    		$('#selected-tags-template').tmpl({ title:decodeURI(keyword[i]), tagId:'keyword' })
    			.addClass('tQ-keyword')
    			.appendTo('#tagQuery');
    	}
    	
    	if ($('#tagQuery li').length==0) {
    		$('#selected-tags-template').tmpl({ title:'所有文档', tagId:'all' })
    			.addClass('tQ-all')
    			.appendTo('#tagQuery')
    			.find('.lightDel').remove();
    	}
   	  
  	});
  
  	$(window).hashchange();
  
  
  	function compositeStateToggle(selector,otherKey,func){
		var hash = location.hash;
		hash = removeAllParameters(hash, "keyword");
		hash = removeAllParameters(hash,otherKey);
		hash = func(hash,selector);
		window.location.hash = hash;
  	};
	
	var tagMenu = new foldableMenu({ controller: 'p.tagGroupTitle', focus: false });
  	
  	var sbox = new searchBox({
  		container : $('#resourceList-search'),
  		standbyText: '在结果中搜索'
  	});
	sbox.doSearch = function(QUERY) {
		location.hash = upsertQueryClause(location.hash, "keyword", encodeURI(QUERY));
	};
	sbox.resetSearch = function(){
		location.hash = removeAllParameters(location.hash, "keyword");
	};
  	
  $("a.tag-option").live('click', function(event){
	  	//$('#resourceList-header input[name="checkAll"]').attr('checked',false);
  		if (event.ctrlKey || event.metaKey) {
  			compositeStateToggle(this,"filter",multipleOptionsToggle);
  		}
  		else {
  			var hash = location.hash;
  			hash = removeQueryClause(hash, 'filter', 'untaged');
  			hash = removeQueryClause(hash, 'filter', 'all');
  			hash = removeAllParameters(hash, "keyword");
  			
  			if ($(this).parent().hasClass('chosen') && $('#tagSelector li.chosen').length==1) {
  				hash = removeQueryClause(hash, $(this).attr('key'), $(this).attr('value'));
  				$(this).parent().removeClass('chosen');
  			}
  			else {
	  			hash = removeAllParameters(hash, 'tag');
	  			hash = addQueryClause(hash,$(this).attr("key"), $(this).attr("value"));
	  			$('#tagSelector li.chosen').removeClass('chosen');
	  			$(this).parent().addClass('chosen');
  			}
	  		window.location.hash = hash;
  		}
  	});
  	$('#tagSelector .addToQuery').live('click', function(event){
  		event.stopPropagation();
  		compositeStateToggle($(this).prev(), 'filter', multipleOptionsToggle);
  	});
  	
  	// resource without tags
  	$("a.filter-option").click(function(){ 
  		compositeStateToggle(this,"tag",singleOptionToggle); 
  	});
  	
  	
  	$("a.date-option").click(function(){ singleStateToggle(this); });
  	$("a.type-option").click(function(){ singleStateToggle(this); });
  	$("#remove-single-option").click(function(){
		var hash = location.hash;
		hash = removeAllParameters(hash,"date");
		hash = removeAllParameters(hash,"type");
		location.hash = hash;
		$(this).parent().addClass('chosen');
		$('#timeSelector .chosen, #typeSelector .chosen').removeClass('chosen');
	});
  	
  	
	$("a.unselectTag").live('click',function(){
		multipleStateToggle($("#tag-for-"+$(this).attr("value")));
	});
	$("a.selected-tag").live('click',function(){
		switch($(this).attr('value')) {
		case 'untaged':
			window.location.hash = removeQueryClause(window.location.hash, 'filter', 'untaged');
			break;
		case 'keyword':
			window.location.hash = removeAllParameters(window.location.hash, "keyword");
			break;
		default:
			singleStateToggle($("#tag-for-"+$(this).attr("value")));
		}
	});
	
	$("#orderTitle").click(function(){
		$("#orderDate").removeClass("chosen");
		$(this).addClass("chosen");
		var param = "titledown";
		var $span = $(this).find("span.iconLynxTag")
		if($span.hasClass("icon-arrowup")){
			param = "titledown";
			$span.removeClass("icon-arrowup");
			$span.addClass("icon-arrowdown");
		}else{
			param = "titleup";
			$span.removeClass("icon-arrowdown");
			$span.addClass("icon-arrowup");
		}
		var hash = window.location.hash;
		hash = removeAllParameters(hash, "orderDate");
		hash = upsertQueryClause(hash, "orderTitle", param);
		window.location.hash = hash;
	});
	
	$("#orderDate").click(function(){
		$("#orderTitle").removeClass("chosen");
		$(this).addClass("chosen");
		var param = "datedown";
		var $span = $(this).find("span.iconLynxTag")
		if($span.hasClass("icon-arrowup")){
			param = "datedown";
			$span.removeClass("icon-arrowup");
			$span.addClass("icon-arrowdown");
		}else{
			param = "dateup";
			$span.removeClass("icon-arrowdown");
			$span.addClass("icon-arrowup");
		}
		var hash = window.location.hash;
		hash = removeAllParameters(hash, "orderTitle");
		hash = upsertQueryClause(hash, "orderDate", param);
		window.location.hash = hash;
	});

  	
  	function restoreState(key,value){
  	  var currnode = $("a.single[key='"+key+"'][value='"+value+"']");
	  if(currnode.length>0){
		  $(currnode).parent().addClass("chosen");
	  }else{
		  currnode = $("a.multiple[key='"+key+"'][value='"+value+"']");
		  $(currnode).parent().addClass("chosen");
	  }
  	};
  	
  
  function refreshState(){
	  	$('div.icon-checkStar').each(function(){
	  		$(this).checkItem({
	  			'makeUrl': function(obj){
	  				var tempURL = site.getURL("starmark",null);
	  				if (obj.hasClass('checked')) 
	  					return tempURL+"?func=remove&rid="+obj.attr("rid");
	  				else
	  					return tempURL+"?func=add&rid="+obj.attr("rid");
	  			},
	  			'whenSuccess': function(data, obj) {
	  				if(data.status=='success'){
		  				obj.checkThis();
		  				if($(obj).hasClass('checked')){
		  					$(obj).attr("title","取消标记");
		  				}else{
		  					$(obj).attr("title","标记为星标文件");
		  				}
	  				}else{
	  					alert("save failed");
	  				}
	  			}
	  		});
	  	});
	  	//跨标签选择时清除已选择资料
		//selector.removeItem('all', true);
		selector.refresh();
		if (viewMode == 'Grid') {
			$('#resourceList').pitfall();
		}
		
		addFileDownloadLink();
  };
  
  function renderResourceRecord(data){
		var tagMap = data["tagMap"];
		for(var prop in tagMap){
			var obj = new Object();
			obj.id = prop;
			obj.title = tagMap[prop];
			$("#page-tag-template").tmpl(obj).appendTo("#tag-item-"+data["rid"]);
		}
		$('#page-tag-newTag-template').tmpl(data).appendTo("#tag-item-"+data["rid"]);
  }
  
  function renderResourceChildren(rid, children){
	  	$("#resource-bundle-itemlist").tmpl(children).appendTo("#child-list-"+rid);
	  	$("span.titleStyle").css('font-weight','bold');
	  	if(children.length>=5){
	  		var bid = children[0].bid;
  			var url = site.getURL('bundle',bid);
	  		var html = "<div class='moreItems'><a href='"+url+"'>></a></div>";
	  		$("#child-list-"+rid).parent().append(html);
	  	}
  }
  
  function renderData(data){
	    //删除包含在已有Bundle中的元素
	  	var target = $("#resourceList .element-data");
	  	if(typeof(target) != undefined && target.length > 0){
		  	$("#resourceList .element-data").each(function(){
		  		var itemId = $(this).attr("item_id");
		  		var childLen = $("#child-list-"+itemId).children("li").length;
			  	data = $.grep(data, function(cur, index){
			  		if(cur.rid==itemId && childLen<5 && cur.children!= null){
			  			var children = removeDuplicate(cur.children);
			  			$("#resource-bundle-itemlist").tmpl(children).appendTo("#child-list-"+itemId);
			  			childLen++;
			  			if(childLen==5){
			  				var bid = cur.children[0].bid;
			  	  			var url = site.getURL('bundle',bid);
			  		  		var html = "<li class='moreItems'><a href='"+url+"'>更多</a></li>";
			  		  		$("#child-list-"+cur.rid).append(html);
			  			}
			  		}
			  		return cur.rid!= itemId;
			  	});
		  	});
  		}
	  	$.each(data,function(index,item){
		  	if(item.isChecked=='checked'){
		  		item.starTitle="取消标记";
			}else{
				item.starTitle="标记为星标文件";
			}
	  	});
		$("#resource-template").tmpl(data).appendTo("#resourceList");
	  	for(var i=0;i<data.length;i++){
	  		renderResourceRecord(data[i]);
	  		if(data[i].children!=null){
		  		renderResourceChildren(data[i].rid, data[i].children);
	  		}
	  	}
	  	
	  	
	  	refreshState();
	  	$('span.version.Bundle').remove();
		return false;
  }
  
  function renderShortcut(shorts){
	  $("#shortcutView").children().remove();
	  if(shorts!=null){
		  var count = 0;
		  $.each(shorts,function(index,item){
			  var param = new Object();
			  param.url=item.resourceUrl;
			  param.title=item.resourceTitle;
			  if(item.color==""||item.color==null){
				  param.color="#ff6600";
			  }else{
				  param.color=item.color;
			  }
			 $("#shortcut-template").tmpl(param).appendTo($("#shortcutView"));
			 count++;
		  });
		  
		  if(count==0){
				$("#shortcutShow").hide();
				$("#top-AddShortcut").show();
		  }else{
			  $("#shortcutShow").show();
				$("#top-AddShortcut").hide();
		  }
		  showOpen();
		  /* if(count>4){
			  $("#moreShortcuts").show();
		  }else{
			  $("#moreShortcuts").hide();
		  } */
	  }
	  $("#shortcutView").append("<div class='clear'></div>");
  }
  
  $("#top-AddShortcut").live("click",function(){
	  $("#addShortcutButton").trigger("click");
  });
  
  
  function removeDuplicate(data){
	  var newData = new Array();
	  $.each(data, function(index, element){
		  var liObj = $("li[rid="+element.rid+"][bid="+element.bid+"]");
		  if(typeof(liObj)=='undefined'){
			  newData.push(element);
		  }
	  });
	  return newData;
  }
  
  function addFileDownloadLink(){
	  $("li.element-data").each(function(index, element){
		  var h2 = $(element).find('h2');
		  var span = $(element).find('h2 a span');
		  if(span.hasClass('DFile') && h2.children().length<2){
			  var href = span.parent().attr('href');
			  var itemId = href.substring(href.lastIndexOf('/')+1,href.length);
			  var html = "<a class='downloadListFile' title='点击下载' href='"+site.getURL('download',itemId)+"?type=doc'></a>";
			  $(h2).append(html);
		  }
	  })
  }
  
  /* show the bundle children when hovered*/
  $("ul.asTable li.element-data,ul.asTight li.element-data").live("hover",function(){
	  $(this).find("div.showBundleChild").show();
  });
  
  $("ul.asTable li.element-data,ul.asTight li.element-data").live("mouseleave",function(){
	  $(this).find("div.showBundleChild").hide();
  });
  
  /* 点击Bundle的子元素的事件*/
  $("ul.bundleChildren a").live("click", function(){
	  var rid = $(this).parent("li").attr('rid');
	  var bid = $(this).parent("li").attr('bid');
	  window.open(site.getURL('bundle',bid)+"?rid="+rid);
  });
  
  $('#resAction-view a').click(function(){
	  for (var i=0; i<selector.log.length; i++) {
		  window.open($('li.element-data[item_id="' + selector.log[i] + '"] a').attr('href'));
	  }
  });
  
  $("#addShortcutButton").live("click",function(){
	  var tagid=null;
	  var i=0;
	  $("ul.ui-navList li.chosen").each(function(index,item){
		  if($(this).attr("id")!="orderDate"){
			  i++;
			  var v = $(item).children("a").attr("value");
			  if(v=='untaged'){
				  tagid=-1;
			  }else if(v=='all'){
				  tagid=0;
			  }else{
				  tagid=v;
			  }
		  }
	  });
	  if(i>1){
		  alert("只能选择一个标签，添加推荐阅读。");
		  return;
	  }else if(i==0){
		  tagid = 0;
	  }
	  if(tagid==-1){
		  alert("无标签文档不能添加推荐阅读！");
		  return ;
	  }
	  window.location.href=(site.getURL("configShortCut",null)+"?tgid="+tagid);
  });
  

	var addShortcutDialog = new lynxDialog({
		'instanceName': 'addShortcut',
		'dialog': $('#addShortcutDialog'),
		'trigger': $('#dragTag'),
		'close': $('#addShortcutDialog .closeThis'),
		'beforeShow': function(){
			var label = '';
			$('#tagQuery .tQ-tag label, #timeSelector li.chosen a, #typeSelector li.chosen a')
				.each(function(){
					label += $(this).text() + '+';
				});
			$('#addShortcutDialog input').val(label.slice(0, -1));
			// slice -1 to remove the last "+"
		},
		'afterShow': function(){
			$('#addShortcutDialog input').focus().select();
		}
	});
	$("#addShortcutDialog .saveThis").click(function(){
		current_data_loader.addNavbarItem();
		addShortcutDialog.hide();
	});
	
	$('#resAction-export').pulldownMenu({
		'menu' : $('#resAction-exportMenu'),
		'anchor' : $('#resAction-export'),
		'direction' : 'up',
		'position' : 'fixed'
	});
	
});
</script>

<script type="text/javascript">
/* RESOURCE SELECTOR */

function resSelector(consoleObj) {
	//view state, decides which elements to respond to
	this._settings = {
			eventName : '.lynx.resSelector'
	}
	var avatar = this;
	
	this._selected = {
			$listCtrl: $('#resAction-list').pulldownMenu({
					eventName:	avatar._settings.eventName,
					menu:		$('#resAction-listMenu'),
					anchor:		$('#resAction-list'),
					direction:	'up',
					position: 'fixed',
					blockHideList:	$('#resourceList input[type="checkbox"]')
				}),
			$list: $('#resAction-listMenu ul'),
			$count: $('#resAction-list span.resCount')
	};
	this.log = [];
	this._console = consoleObj;
	
	this._candidate = {
			$ctrl: $('#resourceList-header input[name="checkAll"]'),
			$body: $('#resourceList'),
			$cbox: $('#resourceList li input[type="checkbox"]')
	}
	
	this._init();
}

resSelector.prototype = {
	_getItem: function(para) {
		if (typeof(para) == 'string') {
			
			return this._candidate.$body.children('li[item_id="' + para + '"]');
		}
		else {
			return para.parentsUntil('li', 'div').parent();
		}
	},
		
	_getItemData: function(para) {
		var item = this._getItem(para);
		
		return {
			$item: item,
			id: item.attr('item_id'),
			title: item.find('a.page-link').text(),
			$cbox: item.find('input[type="checkbox"]')
		};
	},
		
	_init: function() {
		var avatar = this;
		
		// select & deselect actions
		$('#resourceList li.element-data').live('click' + avatar._settings.eventName, function(event){
			if ($(this).hasClass('chosen')) {
				avatar.removeItem($(this).attr('item_id'), true);
			}
			else {
				avatar.addItem($(this).attr('item_id'), true);
			}
		});
		
		$('#resourceList-header input[name="checkAll"]').checkAll({
			slave:			'#resourceList li input[type="checkbox"]',
			eventName:		this._settings.eventName,
			whenCheckAll:	function() {		avatar.addItem('all'); },
			whenUncheckAll:	function() {		avatar.removeItem('all'); },
			whenCheckOne:	function(target) {	avatar.addItem(target); },
			whenUncheckOne:	function(target) {	avatar.removeItem(target); }
		});
		
		// inner control
		$('#clearSelection').bind('click' + avatar._settings.eventName, function(){
			avatar.removeItem('all', true);
			$('#resAction-listMenu').hide();
		});
		
		// create functions
		this._selected.clearList = function() {
			avatar.log.splice(0);
			this.$count.text(0);
			
			this.$list.html('');
			this.$listCtrl.resetPosition();
		};
		
		this._selected.appendItem = function(cbox) {
			var d = avatar._getItemData(cbox);
			
			if ($.inArray(d.id, avatar.log)==-1) {
				// not exist, append to list
				avatar.log.push(d.id);
				this.$count.text(avatar.log.length);
				
				this.$list.append('<li name="' + d.id + '">' + d.title + '<a class="lightDel" name="' + d.id + '"></a></li>');
				this.$listCtrl.resetPosition();
				
				this.$list.find('a.lightDel[name="' + d.id + '"]').bind('click' + avatar._settings.eventName, function(){
					avatar.removeItem(d.id, true);
					$(this).unbind('.resSelector');
				});
				
				return true;
			}
			else {
				return false;
			}
		};
		
		this._selected.removeItem = function(para) {
			var d = avatar._getItemData(para);
			
			var index = $.inArray(d.id, avatar.log);
			
			if (index > -1) {
				// exist, remove
				avatar.log.splice(index, 1);
				this.$count.text(avatar.log.length);
				
				this.$list.find('li[name="' + d.id + '"]').remove();
				this.$listCtrl.resetPosition();
				
				return true;
			}
			else {
				return false;
			}
		}
	},
	
	refresh : function() {
		var avatar = this;
		
		this._selected.$listCtrl.refresh({
			blockHideList:	$('#resourceList input[type="checkbox"]')
		});
		
		this._candidate.$body = $('#resourceList');
		this._candidate.$cbox = $('#resourceList li input[type="checkbox"]');
	},
	
	addItem : function(para, beacon) {
		var avatar = this;
		var change = false;
		
		if (para == 'all') {
			this._candidate.$cbox.each(function(){
				change = avatar._selected.appendItem($(this)) || change;
				avatar._getItem($(this)).addClass('chosen');
			});
		}
		else {
			change = this._selected.appendItem(para);
			this._getItem(para).addClass('chosen');
			if (change && beacon) {
				var d = this._getItemData(para);
				d.$cbox.attr('checked', 'checked');
			}
		}
		
		if (this.log.length>0 && this._console.is(':hidden')) {
			this._console.show();
			this._selected.$listCtrl.resetPosition();
		}
		
		return change;
	},
	
	removeItem : function(para, beacon) {
		var avatar = this;
		var change = false;
	
		if (para == 'all') {
			this._candidate.$cbox.each(function(){
				change = avatar._selected.removeItem($(this)) || change;
				avatar._getItem($(this)).removeClass('chosen');
			});
			if (beacon) {
				this._selected.removeItem('all');
				this._candidate.$cbox.removeAttr('checked');
				this._candidate.$ctrl.removeAttr('checked');
				this._candidate.$body.find('.chosen').removeClass('chosen');
			}
			change = true;
		}
		else {
			change = this._selected.removeItem(para);
			this._getItem(para).removeClass('chosen');
			if (change && beacon) {
				var d = this._getItemData(para);
				d.$cbox.removeAttr('checked');
				this._candidate.$ctrl.removeAttr('checked');
			}
		}
		
		if (change && this.log.length==0 && this._console.is(':visible')) {
			this._console.hide();
		}
		
		return change;
	},
	
	getItemCount : function() {
		return this.log.length;
	},
	getRidArr: function() {
		return this.log; 
	},
	
	showConsole : function() {
		this._console.show();
		return this;
	},
	hideConsole : function() {
		this._console.hide();
		return this;
	}
};// END OF 'RESOURCE SELECTOR'	
function addResource(url){
	try{
		var tagIds=''
		$('#tagQuery a').each(function(index,item){
			if(!isNaN($(item).attr("value"))){
				tagIds+=$(item).attr("value")+",";
			}
		});
		if(tagIds.indexOf(',')>-1){
			tagIds=tagIds.substring(0,tagIds.lastIndexOf(','));
		}
		var param=''
		if(tagIds!=''){
			param="&tagIds="+tagIds;
		}
		window.location.href=url+param;
	}catch(e){
		window.location.href=url;
	}
}
</script>


<script type="text/html" id="new-tag-template">
<li><a id="tag-for-{{= id}}" class="tag-option multiple" key="tag" value="{{= id}}">
	<span class="tagTitle">{{= title}}</span><span class="tagResCount">{{= count}}</span></a>
    <a class="addToQuery"><span>+</span></a>
</li>
</script>

<script type="text/html" id="selected-tags-template">
<li class="tQ-tag"><label>{{= title}}</label><a class="lightDel selected-tag" value="{{= tagId}}"></a></li>
</script>

<script type="text/html" id="resource-template">
<li class="element-data" item_id="{{= rid}}">
	<div class="oper">
		<input type="checkbox" />
		<div class="iconLynxTag icon-checkStar {{= isChecked}}" title="{{= starTitle}}"rid="{{= rid}}"></div>
	</div>
	<div class="resBody">
		<h2><a class="page-link" href="{{= url}}"><span class="headImg {{= itemType}} {{= fileType}}"></span>{{= title}}</a></h2>
		<div class="resChangeLog">
			<span>{{= lastEditorName}} 修改于 {{= lastEditTime}}</span>	
			<span class="version {{= itemType}}">版本：{{= lastVersion}}</span>
		</div>
	</div>
	<div class="showBundleChild" style="display:none">
		<ul class="bundleChildren" id="child-list-{{= rid}}"></ul>
	</div>
	<ul class="tagList" id="tag-item-{{= rid}}"></ul>
</li>
</script>

<script type="text/html" id="resource-bundle-itemlist">
	<li rid="{{= rid}}" bid="{{= bid}}">
		<a>
			<span class="headImg {{= itemType}} {{= fileType}}"></span>
			<span class="{{= titleStyle}}">{{= title}}</span>
		</a>
	</li>
</script>

<script type="text/html" id="page-tag-template">
	<li tag_id="{{= id}}">
		<a target="_blank" href="<vwb:Link context='tag' format='url'/>#&tag={{= id }}">{{= title}}</a>
		<a class="delete-tag-link lightDel" tag_id="{{= id }}"></a>
	</li>
</script>

<script type="text/html" id="page-tag-newTag-template">
	<li class="newTag"  rid="{{= rid}}" item_id="{{= itemId}}" item_type="{{= itemType}}"><a title="添加标签">+</a></li>
</script>
<script type="text/html" id="nav-bar-item-template">
<li id="navbar_{{= id}}" >
	<a href="{{= url}}">{{= title}}</a>
	<a class="delete-nvitem-link lightDel" value="{{= id}}" title="移除快捷导航"></a>
</li>
</script>
<script type="text/html" id="shortcut-template">
<li class="shortcutItem">
	<a title="{{= title}}" href="{{= url}}" style="color:{{= color}}" target="_blank">{{= title}}</a> 
</li>
</script>
<script type="text/html" id="conflict-bundle-item-template">
<li class="conflict-bundle-item">
	<span>
		<a target="_blank" href="{{= url}}">
			<span class="headImg {{= itemType}} ext {{= fileType}}"></span>
			{{= title}}
		</a>
	</span>
	<span>已经属于组合</span>
	<span>
		<a target="_blank" href="{{= bundle.url}}">{{= bundle.title}}</a>
	</span>
</li>
</script>

<script type="text/html" id="pageLockErrorMessageTemplate">
<p style="text-align:left;padding:0 1em;">页面“{{= pageTitle}}”正在被<span style="color:#08c">{{= editor}}</span>编辑,请等待Ta完成编辑！</p>
</script>
