<%@ page language="java" pageEncoding="utf-8"%>
<%@ page import="org.json.*" %>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/tokenInput.css" type="text/css" />
<link href="${contextPath}/scripts/bootstrap/css/bootstrap.min.css" rel="stylesheet" media="screen"/>
<link href="${contextPath}/scripts/bootstrap/css/bootstrap-responsive.min.css" rel="stylesheet"/>
<link href="${contextPath}/scripts/bootstrap/css/todc-bootstrap.css" rel="stylesheet"/>
<script type="text/javascript" src="${contextPath}/jsp/aone/js/toker-jQuery-forTag.js"></script> 
<script type="text/javascript">
$(document).ready(function(){
	/**         remove team banner                **/
	switchToLightNav('noLocalNav');
	
/* Placement of search tool box */
	var tool = $('#searchTool');
	var toolOri = tool.offset().top;
	tool.css('width', tool.width());
	
	$(window).scroll(function(){
		var scrDiff = $(window).scrollTop() - toolOri;
		if (scrDiff>0)
			tool.css('position', 'fixed').css('top', '0');
		else
			tool.css('position', '');
	});

/* switch results facet*/
	function switchView(VIEW) {
		$('#searchTool ul.filter > li').removeClass('chosen');
		$('#resultList > div:not(.bedrock)').hide();
		
		var view = VIEW;
		
		switch (VIEW) {
			case 'resultPages':
				$('#pageResultContainer').fadeIn();
				break;
			case 'resultFiles':
				$('#fileResultContainer').fadeIn();
				break;
			case 'resultFolders':
				$('#floderResultContainer').fadeIn();
				break;
			case 'resultAll':
			default:
				view = 'resultAll';
				$('#resultList > div').fadeIn();
		}
		$('#searchTool ul.filter a[view="' + view + '"]').parent().addClass('chosen');
	}
	
	$('#searchTool ul.filter a').click(function(){
		switchView($(this).attr('view'));
		$(window).scrollTop(toolOri);
	});
	/* initiate */
	var hash = (window.location.hash)?window.location.hash:'';
	if (hash!='') {
		switchView(hash.substring(1));
	}
	else {
		switchView();
	}	
	
	
	var currPageCount = 0;
	var currFileCount = 0;
	var currFloderCount = 0;
	var currClickedLoadMoreButton = "";//当前点击的更多结果按钮类型
	var isLoading = false;
	
	$("#searchButton").live("click",function(){
		if (!keywordIsEmpty()){
			currClickedLoadMoreButton = "";
			searchData();
		}else{
			alert("关键词不能为空！");
		}
	});
	
	function searchData(){
		if(!isLoading){
			isLoading = true;
			initResultList();
			standbyResultList(true);
			var params = $("#search-form").serialize();
			params = params + "&flag=search";
			ajaxRequestWithErrorHandler("<vwb:Link format='url' context='globalSearch'/>?func=research",
					params,afterLoadSearchResult, errorHandler);
		}
	};
	
	function keywordIsEmpty(){
		var keyword = $.trim($("input[name=keyword]").val());
		return keyword=='';
	}
	
	function errorHandler(){
		standbyResultList(false);
		addSessionExpiredLink(currClickedLoadMoreButton);
		isLoading = false;
	}
	
	var lock = false;
	$('input[name="keyword"]').keyup(function(key){
		switch (key.which) {
			case 13://enter
				if (!keywordIsEmpty()){
					searchData();
				}else{
					if(lock){
						return;
					}
					alert("关键词不能为空！");
					lock = true;
					setTimeout(function(){
						lock = false;
					},200);
				}
				break;
			case 27://escape
				$(this).val('');
				break; 
		}
	});
	$('input[name="keyword"]').focus(function(){
		$(this).select();
	});
	
	function afterLoadSearchResult(data){
		initResultList();
		standbyResultList(false);
		$('#numTotal').html(data.count);
		$('#numPageContents').html(data.pageCount);
		$('#numFiles').html(data.fileCount);
		$('#numFolders').html(data.floderCount);
		
		if(data.pageCount!=0){
			currPageCount = data.pageResult.length;
			$("#page-header-template").tmpl(buildResourceHeaderDataModel(data)).appendTo("#pageResultContainer h3");
			for(var i=0; i<currPageCount; i++){
				$("#page-record-template").tmpl(data.pageResult[i]).appendTo("#pageResult");
				$("#page-tag-template").tmpl(JSON.parse(data.pageResult[i].tagMap)).appendTo("#tag-item-"+data.pageResult[i].rid);
				var ridStr = {"rid":data.pageResult[i].rid};
				$("#page-tag-newTag-template").tmpl(ridStr).appendTo("#tag-item-"+data.pageResult[i].rid);
				if(data.pageResult[i].starmark){
					$('div.icon-checkStar[rid='+data.pageResult[i].rid+']').addClass('checked');
				}else{
					$('div.icon-checkStar[rid='+data.pageResult[i].rid+']').addClass('unchecked');
				}
			}
			$("#footer-template").tmpl(buildResourceFooterDataModel("DPage")).appendTo("#pageResult");
			hideLoadMoreButton(data.pageResult.length,$("input[name='size']").attr("value"),"DPage");
			$('#pageResultContainer p.NA').hide();
		}
		else {
			$('#pageResultContainer p.NA').show();
		}
		
		if(data.fileCount!=0){
			currFileCount = data.fileResult.length;
			$("#file-header-template").tmpl(buildResourceHeaderDataModel(data)).appendTo('#fileResultContainer h3');
			for(var i=0; i<currFileCount; i++){
				$("#file-record-template").tmpl(data.fileResult[i]).appendTo("#fileResult");
				$("#page-tag-template").tmpl(JSON.parse(data.fileResult[i].tagMap)).appendTo("#tag-item-"+data.fileResult[i].rid);
				var ridStr = {"rid":data.fileResult[i].rid};
				$("#page-tag-newTag-template").tmpl(ridStr).appendTo("#tag-item-"+data.fileResult[i].rid);
				if(data.fileResult[i].starmark){
					$('div.icon-checkStar[rid='+data.fileResult[i].rid+']').addClass('checked');
				}else{
					$('div.icon-checkStar[rid='+data.fileResult[i].rid+']').addClass('unchecked');
				}
			}
			$("#footer-template").tmpl(buildResourceFooterDataModel("DFile")).appendTo("#fileResult");
			hideLoadMoreButton(data.fileResult.length,$("input[name='size']").attr("value"),"DFile");
			$('#fileResultContainer p.NA').hide();
		}
		else {
			$('#fileResultContainer p.NA').show();
		}
		
		if (data.floderCount!=0) {
			currFloderCount = data.floderResult.length;
			$('#floder-header-template').tmpl(buildResourceHeaderDataModel(data)).appendTo('#floderResultContainer h3');
			for(var i=0; i<currFloderCount; i++){
				$('#floder-record-template').tmpl(data.floderResult[i]).appendTo('#floderResult');
				$("#page-tag-template").tmpl(JSON.parse(data.floderResult[i].tagMap)).appendTo("#tag-item-"+data.floderResult[i].rid);
				var ridStr = {"rid":data.floderResult[i].rid};
				$("#page-tag-newTag-template").tmpl(ridStr).appendTo("#tag-item-"+data.floderResult[i].rid);
				if(data.floderResult[i].starmark){
					$('div.icon-checkStar[rid='+data.floderResult[i].rid+']').addClass('checked');
				}else{
					$('div.icon-checkStar[rid='+data.floderResult[i].rid+']').addClass('unchecked');
				}
			}
			$("#footer-template").tmpl(buildResourceFooterDataModel("Bundle")).appendTo("#floderResult");
			hideLoadMoreButton(data.floderResult.length,$("input[name='size']").attr("value"),"Bundle");
			$('#floderResultContainer p.NA').hide();
		}
		else {
			$('#floderResultContainer p.NA').show();
		}
		
		if(data.pageCount+data.fileCount+data.floderCount == 0){
			addGoToSearchAllTeamLink();
		}else{
			removeEmptyNotice();
		}
		isLoading = false;
		//initTagContext();
	};
	
	function renderTagMap(tagmap, rid){
		for(var prop in tagmap){
			var obj = new Object();
			obj.id = prop;
			obj.title = tagmap[prop];
			$("#page-tag-template").tmpl(obj).appendTo("#tag-item-"+rid);
		}
	}
	
	function initResultList(){
		$("#totalResult").html("");
		$("#pageResult").html("");
		$("#fileResult").html("");
		$("#floderResult").html("");
		$('#resultList h3 span').remove();
		$("input[name=offset]").attr("value",0);
		currPageCount = 0;
	};
	
	function standbyResultList(TRIGGER) {
		if (TRIGGER) {
			$('#standby').show();
		}
		else {
			$('#standby').hide();
		}
	};
	
	function addGoToSearchAllTeamLink(){
		var $form = $("#search-form");
		var keyword = $form.find("input[name=keyword]").val();
		var teamName = $form.find("select[name=teamName]").val();
		removeEmptyNotice();
		if(teamName!=""){
			var teamDisplayName = $form.find("option[value="+teamName+"]").text();
			var url = "<vwb:Link context='globalSearch' format='url'/>?func=searchResult&keyword="+encodeURIComponent($.trim(keyword));
			var html = "<div id=\"emptyResultNotice\" style='margin:3em 5em'><span>\""+teamDisplayName+"\"中没有符合条件的结果，您可以在</span><a href=\""+url+"\">全部团队</a><span>中搜索。</span></div>";
			$("#pageResultContainer").before(html);
		}
	}
	
	function addSessionExpiredLink(type){
		var $form = $("#search-form");
		var keyword = $form.find("input[name=keyword]").val();
		var teamName = $form.find("select[name=teamName]").val();
		removeEmptyNotice();
		var url = "<vwb:Link context='globalSearch' format='url'/>?func=searchResult&keyword="+encodeURIComponent($.trim(keyword));
		if(teamName!=""){
			url += "&teamName="+teamName;
		}
		var html = "<div id=\"emptyResultNotice\" style='margin:3em 5em'><span>搜索失败！您可能没有登录或者会话已过期，请</span><a href=\""+url+"\">刷新</a><span>页面。</span></div>";
		if(type==""){
			$("#pageResultContainer").before(html);
		}else{
			$("a.load-more."+type).before(html);
		}
	}
	
	function removeEmptyNotice(){
		var $emptyNotice = $("#emptyResultNotice");
		if($emptyNotice.length>0){
			$emptyNotice.remove();
		}
	}
	
	function buildTotalHeaderDataModel(data){
		var obj = new Object();
		obj["count"] = data.count;
		obj["pageCount"] = data.pageCount;
		obj["fileCount"] = data.fileCount;
		obj["floderCount"] = data.floderCount;
		return obj;
	};
	
	function buildResourceHeaderDataModel(data){
		var obj = new Object();
		obj["pageCount"] = data.pageCount;
		obj["currPageCount"] = currPageCount;
		obj["fileCount"] = data.fileCount;
		obj["currFileCount"] = currFileCount;
		obj["floderCount"] = data.floderCount;
		obj["currFloderCount"] = currFloderCount;
		return obj;
	};
	
	function buildResourceFooterDataModel(type){
		var obj = new Object();
		obj["type"] = type;
		return obj;
	};
	
	$(".load-more").live('click',function(){
		if(!isLoading){
			isLoading = true;
			var type = $(this).parent().attr('class');
			var params = {};
			if(type=="DFile"){
				$("input[name=offset]").attr("value",currFileCount);
				params = $("#search-form").serialize();
				params = params + "&type=DFile";
			}else if(type=="Bundle"){
				$("input[name=offset]").attr("value",currFloderCount);
				params = $("#search-form").serialize();
				params = params + "&type=Bundle";
			}else{
				$("input[name=offset]").attr("value",currPageCount);
				params = $("#search-form").serialize();
				params = params + "&type=DPage";
			}
			
			currClickedLoadMoreButton = type;
			params = params +"&keyword="+ $('input[name="keyword"]').val();
			params = params + "&oper_name=loadmore";
			ajaxRequestWithErrorHandler("<vwb:Link format='url' context='globalSearch'/>?func=loadmore&area=page",
					params,afterLoadMoreSearchResult, errorHandler);
		}
	});
	
	function hideLoadMoreButton(actural,expected,type){
		var total = 0;
		var curCount = 0;
		if(type == 'DPage'){
			total = parseInt($.trim($("#numPageContents").html()));
			curCount = currPageCount;
		}else if(type == 'DFile'){
			total = parseInt($.trim($("#numFiles").html()));
			curCount = currFileCount;
		}else{
			total = parseInt($.trim($("#numFolders").html()));
			curCount = currFloderCount;
		}
		
		if(actural!=expected && curCount>=total && actural >0){
			$("a.load-more."+type).hide();
		}
	}
	
	function afterLoadMoreSearchResult(data){
		//Page
		if(data.pageResult!=null){
			currPageCount += data.pageResult.length;
			hideLoadMoreButton(data.pageResult.length,$("input[name='size']").attr("value"),"DPage");
		}
		if(data.pageCount!=0){
			for(var i=0; i<data.pageResult.length; i++){
				$(".page-record:last").addClass('section').after($("#page-record-template").tmpl(data.pageResult[i]));
				$("#page-tag-template").tmpl(JSON.parse(data.pageResult[i].tagMap)).appendTo("#tag-item-"+data.pageResult[i].rid);
				var ridStr = {"rid":data.pageResult[i].rid};
				$("#page-tag-newTag-template").tmpl(ridStr).appendTo("#tag-item-"+data.pageResult[i].rid);
				if(data.pageResult[i].starmark){
					$('div.icon-checkStar[rid='+data.pageResult[i].rid+']').addClass('checked');
				}else{
					$('div.icon-checkStar[rid='+data.pageResult[i].rid+']').addClass('unchecked');
				}
			}
		}
		$("span.curr-display.DPage").html(currPageCount);
		
		//File
		if(data.fileResult!=null){
			currFileCount += data.fileResult.length;
			hideLoadMoreButton(data.fileResult.length,$("input[name='size']").attr("value"),"DFile");
		}
		if(data.fileCount!=0){
			for(var i=0; i<data.fileResult.length; i++){
				$(".file-record:last").addClass('section').after($("#file-record-template").tmpl(data.fileResult[i]));
				$("#page-tag-template").tmpl(JSON.parse(data.fileResult[i].tagMap)).appendTo("#tag-item-"+data.fileResult[i].rid);
				var ridStr = {"rid":data.fileResult[i].rid};
				$("#page-tag-newTag-template").tmpl(ridStr).appendTo("#tag-item-"+data.fileResult[i].rid);
				if(data.fileResult[i].starmark){
					$('div.icon-checkStar[rid='+data.fileResult[i].rid+']').addClass('checked');
				}else{
					$('div.icon-checkStar[rid='+data.fileResult[i].rid+']').addClass('unchecked');
				}
			}
		}
		$("span.curr-display.DFile").html(currFileCount);
		
		//Bundle
		if(data.floderResult!=null){
			currFloderCount += data.floderResult.length;
			hideLoadMoreButton(data.floderResult.length,$("input[name='size']").attr("value"),"Bundle");
		}
		if(data.floderCount!=0){
			for(var i=0; i<data.floderResult.length; i++){
				$(".floder-record:last").addClass('section').after($("#floder-record-template").tmpl(data.floderResult[i]));
				$("#page-tag-template").tmpl(JSON.parse(data.floderResult[i].tagMap)).appendTo("#tag-item-"+data.floderResult[i].rid);
				var ridStr = {"rid":data.floderResult[i].rid};
				$("#page-tag-newTag-template").tmpl(ridStr).appendTo("#tag-item-"+data.floderResult[i].rid);
				if(data.floderResult[i].starmark){
					$('div.icon-checkStar[rid='+data.floderResult[i].rid+']').addClass('checked');
				}else{
					$('div.icon-checkStar[rid='+data.floderResult[i].rid+']').addClass('unchecked');
				}
			}
		}
		$("span.curr-display.Bundle").html(currFloderCount);
		isLoading = false;
	};

	/*----------- add tag dialog begin ---------------*/
	var curBaseTagURL;
	
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
		var i = 0;
		$(".existTags ul.tagList").children("li").each(function(){
			results[i] = $(this).attr("tag_id");
			i++;
		});
		return results;
	}
  	var _tagCache = null;
  	var addTag_handler = {
  			addTagForSingleRecord:function(){
  				//var tagURL = "<vwb:Link context='tag' format='url'/>";
  				//var params = {"func":"batchAdd","newTags[]":aTBox.log.create,"existTags[]":aTBox.log.add,"rids[]":aTBox.log.rid};
  				var params = {"func":"batchAdd","newTags[]":getNewTags(),"existTags[]":getExistTags(),"rids[]":aTBox.log.rid};
  				ajaxRequestWithErrorHandler(curBaseTagURL,params,function(data){
  					$.each(data, function(index, element){
  						element.tagurl = curBaseTagURL;
  					});
  					$('li.newTag[rid="' + aTBox.log.rid[0] + '"]').before($("#page-tag-template").tmpl(data));
  					addSingleTagDialog.hide();
  				},notEnoughAuth);
  			},
  			loadAllTeamTags:function(){
  				//var url = site.getURL("tag",null);
  				ajaxRequest(curBaseTagURL,"func=loadTeamTags",function(data){
  					_tagCache = data;
  					tPool.refresh(_tagCache);
  					aTBox.refresh();
  				});
  			}
		};
	
	
	
	var tPool = new tagPool({
		pool: $('.tagGroupHorizon'),
		scroller: $('.tG-scroll'),
		blockClass: 'tG-block'
	});
	
	var aTBox = new addTagBox({
		input: $('input[name="typeTag"]'),
		tagList: $('.existTags ul.tagList'),
		tagPool: tPool,
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
	
	resSelector = function(){
		//this.rid = new Array();
		var _rid = new Array();
		this.getRidArr=function(){
			return this._rid;
		};
		this.setRidArr=function(v){
			var vs = new Array();
			vs.push(v);
			this._rid = vs;
		};
		this.setRid=function(da){
			this._rid=da;
		};
		this.removeItem=function(){
			this._rid = new Array();
		};
	};

	$('li.newTag').live('click',function(){
		//加载标签
		curBaseTagURL = $(this).closest("ul").attr("data-tagurl");
		addTag_handler.loadAllTeamTags();
		aTBox.prepare({ ul: $(this).parent(), ridArr: [$(this).parent().attr('rid')] });
		addSingleTagDialog.show();
		selector.setRidArr(item.rid);
		$("#token-input-").focus();
	});
	
	$("#addSingleTagDialog .saveThis").click(function(){
		var feedback = addTag_handler.addTagForSingleRecord();
	});
	
	$('#resAction-tag a').click(function(){
		aTBox.prepare({ ridArr: selector.getItem() });
		addSingleTagDialog.$dialog.addClass('multiple');
		addSingleTagDialog.show();
	});
	
	$('li.newTag')
	.bind('click.lynx.callAddSingleTagDialog', function(event){
		event.stopPropagation();
		aTBox.prepare({ ul: $(this).parent() });
		addSingleTagDialog.$dialog.removeClass('multiple');
		addSingleTagDialog.show();
	});
  
/*---------------add tag dialog end---------------*/
 
	$('input.tagPoolAutoShow').tokenInput(getTagURL, {
		theme:"facebook",
		hintText: "输入标签名称，以逗号或回车结束",
		searchingText: "正在搜索……",
		noResultsText: "没有该标签，输入逗号或回车添加",
		preventDuplicates: true
	}); 
	
	function getTagURL(){
		return curBaseTagURL+"?func=loadTeamTags&type=nogroup";
	}
	
/* Delete Tag start */
 	$("a.delete-tag-link").live('click',function(){
 		var rids = new Array();
 		rids[0] = $(this).parents("ul").attr("rid");
    	var params = {"func":"remove","rid[]":rids,"tagId":$(this).attr("tag_id")};
    	//var url = site.getURL('tag',null);
    	var $a = $(this);
    	curBaseTagURL = $.trim($(this).closest("ul").attr("data-tagurl"));
		ajaxRequestWithErrorHandler(curBaseTagURL,params,function(data){
    		$a.parent().remove();
    	},notEnoughAuth);
    });
 /* Delete Tag end */
 
 	function notEnoughAuth(){
		alert("您无权进行此操作！");
	};
	
	searchData();
	
	$('.icon-checkStar').live('click', function(){
		//var tempURL = site.getURL("starmark",null);
		var tempURL = $.trim($(this).attr("data-starmarkurl"));
		if ($(this).hasClass('checked')) 
			tempURL += "?func=remove&rid="+$(this).attr("rid");
		else
			tempURL += "?func=add&rid="+$(this).attr("rid");
		ajaxRequest(tempURL, "", function(data){
			var rid = data.rid;
			if(data.status == 'success'){
				var $obj = $('div.icon-checkStar[rid='+rid+']');
				if($obj.hasClass('checked')){
					$obj.removeClass('checked');
					$obj.addClass('unchecked');
				}else{
					$obj.removeClass('unchecked');
					$obj.addClass('checked');
				}
			}else{
				alert('标记失败！');
			}
		});
	});
});

$("a.page-link").live('click',function(event){
	var resultURL = $(this).attr("href");
	var resultURLs= new Array();
	resultURLs = resultURL.split("/");
	var tid =  $(this).attr("tid");
	var pid = resultURLs[resultURLs.length-1];
	if(pid.lastIndexOf("rid=")!=(-1)){
		pid = pid.substr(pid.lastIndexOf("="));
		var type = "rid";
	}
	else{
		var type = resultURLs[resultURLs.length-2];
	} 
	var params = {"keyword":$('input[name="keyword"]').val(),"rank":$(this).attr("count"),"type":type,"pid":pid,"tid":tid};
	//此处删除了回掉函数处理，原先的回掉函数也没做什么特殊的处理
	ajaxRequest("<vwb:Link format='url' context='globalSearch'/>?func=searchlog",params,function(data){});
});
	
	function initTagContext(){
		try{
			if($("li.page-record").length>0){
				$("li.page-record").find("li.newTag:eq(0)").click();
			}else if($("li.file-record").length>0){
				$("li.file-record").find("li.newTag:eq(0)").click();
			}else if($("li.floder-record").length>0){
				$("li.floder-record").find("li.newTag:eq(0)").click();
			}
		}catch(e){
			//对该异常不做处理
		}
	}
</script>
<div class="content-through" id="searchToolSpacer" style="height:60px;">
	<div class="toolHolder" id="searchTool">
		<form id="search-form" action="javascript:void(0)" style="margin-bottom:0">
			<input type="hidden" name="offset" value="0"/>
			<input type="hidden" name="size" value="20"/>
			<input type="hidden" name="sortBy" value="time"/>
			<select name="teamName" class="teamSearch" style="float:left; margin-top:5px;">
				<option value="" <c:if test="${currentTeam == null || currentTeam == '' }">selected="selected"</c:if>>全部团队</option>
				<c:forEach items="${teams}" var="team">
					<option title="${team.displayName }" value="${team.name}" <c:if test="${team.name==currentTeam }">selected="selected"</c:if>><c:out value="${team.displayName }"/></option>
				</c:forEach>
			</select>
			<input type="text" name="keyword" value="${keyword}" style="float:left; margin-top:4px;"/>
			<input type="button" id="searchButton" class="btn btn-success" value="搜索" style="float:left; margin:4px 0 0 -11px;padding:0px 23px;line-heigth:33px; height:33px;overflow:hidden;"/>
		    <div class="clear"></div>
		</form>
		<ul class="filter">
			<li class="fixed">搜索到<span id="numTotal">-</span>条结果：</li>
			<li><a view="resultAll" href="#resultAll">所有结果</a></li>
			<li><a view="resultPages" href="#resultPages">
				<span id="numPageContents">-</span>个协作文档</a></li>
			<li><a view="resultFiles" href="#resultFiles">
				<span id="numFiles">-</span>个文件</a></li>
			<li><a view="resultFolders" href="#resultFolders">
				<span id="numFolders">-</span>个文件夹</a></li>
		</ul>
	</div>
</div>

<div class="content-through" id="standby">
	<div class="bedrock"></div>
	<p class="NA large"><label class="checking" style="display:inline; padding-left:20px;"></label>正在搜索</p>
	<div class="bedrock"></div>
</div>

<div class="content-through" id="resultList">
	<div id="pageResultContainer">
		<h3>协作文档</h3>
		<ul id="pageResult"></ul>
		<p class="NA large">没有符合条件的协作文档</p>
	</div>
	<div id="fileResultContainer">
		<h3 id="fileResult-title">文件</h3>
		<ul id="fileResult"></ul>
		<p class="NA large">没有符合条件的文件</p>
	</div>
	<div id="floderResultContainer">
		<h3 id="floderResult-title">文件夹</h3>
		<ul id="floderResult"></ul>
		<p class="NA large">没有符合条件的文件夹</p>
	</div>
	<div class="bedrock"></div>
</div>

<div id="addSingleTagDialog" class="lynxDialog" style="top: 10%">
	<div class="toolHolder light">
		<h3>添加标签</h3>
	</div>
	<div class="inner">
		<p class="tokerInput-p">
			<input type="text" name="typeTag" class="tagPoolAutoShow" />
		</p>
		<div class="existTags">
			<div class="tagShow singleFile">
				<p>已有标签：</p>
				<ul class="tagTogether"></ul>
				<ul class="hideMe" style="display:none;"></ul>
			</div>
			<div class="tagShow singleFile">
				<p>新增标签：</p>
				<ul class="tagCreate"></ul>
				<ul class="tagList"></ul>
			</div>
		</div>
		<div class="tagGroupHorizon">
			<div class="tG-scroll"></div>
		</div>
	</div>
	<div class="control largeButtonHolder">
		<a href="javascript:void(0)" class="saveThis btn btn-primary" >保存</a>
		<a href="javascript:void(0)" class="closeThis btn" >取消</a>
	</div>
</div>

<script id="page-header-template" type="text/html">
	<span class="ui-text-std">共 {{= pageCount}} 条</span>
	<span class="ui-text-std">当前显示<span class="curr-display DPage"> {{= currPageCount}} </span>条</span>
</script>

<script id="footer-template" type="text/html">
	<li class="{{= type}}"><a class="load-more {{= type}} largeButton">更多结果</a></li>
</script>

<script id="page-record-template" type="text/html">
<li class="page-record">
	<p><span class="count"></span>
		<a target="_blank" class="page-link title" tid="{{= tid}}" count="{{= id}}" href="{{= url}}">{{html title}}.ddoc</a>
	</p>
	<div class="iconLynxTag icon-checkStar withTag" rid="{{= rid}}" data-starmarkurl="{{= starmarkurl}}">&nbsp;</div>
	<div class="content-resTag">
		<ul class="tagList" id="tag-item-{{= rid}}" rid="{{= rid}}" style="float:left;" data-tagurl="{{= tagurl}}"></ul>
	</div>
	<div class="ui-clear"></div>
	<p>
		<span class="author">创建者：{{= author}}</span>
		<span class="updateTime">创建时间：{{= modifyTime}}</span>
		<span class="teamName">来源：{{= teamName}}团队</span>
	</p>
	<p class="digest">{{html digest}}</p>
</li>
</script>

<script id="file-header-template" type="text/html">
	<span class="ui-text-std">共 {{= fileCount}} 条</span>
	<span class="ui-text-std">当前显示<span class="curr-display DFile"> {{= currFileCount}} </span>条</span>
</script>
<script id="file-record-template" type="text/html">
<li class="file-record">
	<p><span class="count"></span>
		<a target="_blank" class="page-link title" tid="{{= tid}}" " count="{{= sn}}" href="{{= url}}">{{html title}}</a>
	<a class="downloadListFile" href="{{= downloadUrl}}"></a>
	</p>
	<div class="iconLynxTag icon-checkStar withTag" rid="{{= rid}}" data-starmarkurl="{{= starmarkurl}}">&nbsp;</div>
	<div class="content-resTag">
		<ul class="tagList" id="tag-item-{{= rid}}" rid="{{= rid}}" style="float:left;" data-tagurl="{{= tagurl}}"></ul>
	</div>
	<div class="ui-clear"></div>
	<p>
		<span class="author">创建者：{{= creator}}</span>
		<span class="updateTime">创建时间:{{= createTime}}</span>
		<span class="teamName">来源：{{= teamName}}团队</span>
	</p>
</li>
</script>

<script id="floder-header-template" type="text/html">
	<span class="ui-text-std">共 {{= floderCount}} 条</span>
	<span class="ui-text-std">当前显示<span class="curr-display Bundle"> {{= currFloderCount}} </span>条</span>
</script>
<script id="floder-record-template" type="text/html">
<li class="floder-record">
	<p><span class="count"></span>
		<a target="_blank" class="page-link title" tid="{{= tid}}" count="{{= sn}}" href="{{= url}}" >{{html title}}</a>
	</p>
	<div class="iconLynxTag icon-checkStar withTag" rid="{{= rid}}" data-starmarkurl="{{= starmarkurl}}">&nbsp;</div>
	<div class="content-resTag">
		<ul class="tagList" id="tag-item-{{= rid}}" rid="{{= rid}}" style="float:left;" data-tagurl="{{= tagurl}}"></ul>
	</div>
<div class="ui-clear"></div>
	<p>
		<span class="author">创建者：{{= creator}}</span>
		<span class="updateTime">创建时间:{{= createTime}}</span>
		<span class="teamName">来源：{{= teamName}}团队</span>
	</p>
</li>
</script>

<script type="text/html" id="page-tag-template">
	<li tag_id="{{= id}}">
		<a target="_blank" href="{{= tagurl}}#tagId={{= id}}&queryType=tagQuery">{{= title}}</a>
		<a class="delete-tag-link lightDel" tag_id="{{= id}}"></a>
	</li>
</script>

<script type="text/html" id="page-tag-newTag-template">
	<li class="newTag"  rid="{{= rid}}"><a>+</a></li>
</script>
