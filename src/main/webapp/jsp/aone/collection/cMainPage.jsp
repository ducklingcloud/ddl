<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<script type="text/javascript"  src="${contextPath}/scripts/jquery/jquery.hashchange-1.0.0.js"></script>
<script type="text/javascript">
$(document).ready(function(){
	//appearance
	if ($('#content-side').height() > $('#content-major').height()) {
		//incase side is longer than main
		$('#content-major').css('min-height', $('#content-side').height());
	}

	$('#editTool a.editTool').click(function(){});
	
	$(".edit-link").live("click",function(){
		$(this).attr("href",site.getEditURL($(this).attr("rid")));
	});
	
	$(".download-link").live("click",function(){
		$(this).attr("href",site.getURL("download",$(this).attr("rid")));
	});
	
	
	
	$(".share-link").live("click",function(){
		$(this).attr("href",site.getURL("file",$(this).attr("rid"))+"?func=shareExistFile");
	});
	
	function notEnoughAuth(){
		alert("操作权限不够");
	};
	
	var requestURL = "<vwb:Link context='configCollection' format='url'/>";
	
	$(".add-shortcut-link").live('click',function(){
		$("input[name='rid']").attr("value",$(this).attr("rid"));
		$("input[name='resourceType']").attr("value",$(this).attr("type"));
		$("input[name='shortcutName']").attr("value",$(this).attr("name"));
		ajaxRequestWithErrorHandler(requestURL,$("#addShortcutForm").serialize(),
			afterAddShortcut,notEnoughAuth);
	});
	
	function afterAddShortcut(data){
		if(data.status=='success'){
			$("#shortcut p").remove();
			$("#shortcut-template").tmpl(data).appendTo("#shortcut");
		}else{
			alert("不能重复添加");
		}
	};
	
	
	$(".delete-shortcut").live('click',function(){
		var queryParam = "func=deleteShortcut&cid="+${collection.resourceId}+"&sid="+$(this).attr("sid");
		ajaxRequestWithErrorHandler(requestURL,queryParam,afterDeleteShortcut,notEnoughAuth);
	});
	
	function afterDeleteShortcut(data){
		$("ul li[sid='"+data.sid+"']").slideUp('fast', function(){$(this).remove();});
	};
	
	var currentPageIndex=0;
	var count = $("#result_count").html();
	
	function refreshTotalCount(){
		var requestURL = "<vwb:Link context='viewCollection' page='${collection.resourceId}' format='url'/>";
		ajaxRequest(requestURL,$("#searchParameter").serialize()+"&func=getTotalCount",afterRefreshTotalCount);
	}
	
	function afterRefreshTotalCount(data){
		$("#mycontainer").html("");
		$("#result_count").html(data.total);
		count = data.total;
		if(data.total==0){
			$("#mycontainer").html("<li>没有匹配结果</li>");
			$("#pagination").html("");
		}else{
			repaintDataList();
		}
	}
	
	function repaintDataList(){
		var size=$("input[name='pagesize']").val();
		$("#pagination").pagination(count,{
			current_page:currentPageIndex,
			num_edge_entries: 2,
			items_per_page:size,
			callback: function(pageIndex, container){
				var offset = pageIndex*size;
				$("input[name='offset']").attr("value",offset);
				var requestURL = "<vwb:Link context='viewCollection' page='${collection.resourceId}' format='url'/>";
				ajaxRequest(requestURL,$("#searchParameter").serialize()+"&func=getPaginationData",renderPageDataList);
			}//end callback function
		});//end parameters
	};//end repaint function
		
	function renderPageDataList(data){
		$("#mycontainer").html("");
			for(var i=0;i<data.length;i++){
				data[i].extendType = "";
				if(data[i].type=='DPage')
					$("#page-group-template").tmpl(data[i].parent).appendTo("#mycontainer");
				else{
					data[i].parent.extendType = getFileExtend(data[i].parent.title);
					$("#file-group-template").tmpl(data[i].parent).appendTo("#mycontainer");
				}
				if($("input[name='filter']").val()!='all'){
					for(var j=0;j<data[i].children.length;j++){
						data[i].children[j].extendType = getFileExtend(data[i].children[j].title);
					}
					$("#page-child-template").tmpl(data[i].children).appendTo("#parent_"+data[i].parent.id);
				}
			}
		$('#mycontainer-listMode').fadeOut();
		$('#mycontainer').fadeIn();
		return false;
	};
	
	function getFileExtend(t){
		return t.substring(t.lastIndexOf(".")+1).toLowerCase();
	}
	
	refreshTotalCount();
	
	function refreshDataByCondition(selector,value){
		currentPageIndex = 0;
		$("input[name='"+selector+"']").attr("value",value);
		refreshTotalCount();
	};
	
	function generateConditionKey(){
		var hashKey = $("input[name='pagesize']").val()+"-";
		hashKey += $("input[name='sortBy']").val()+"-";
		hashKey += $("input[name='descOrAsc']").val()+"-";
		var queryword = $("input[name='keyword']").val()
		if(queryword.length!=0)
			hashKey += $("input[name='keyword']").val();
		else
			hashKey += "null";
		return hashKey;
	}
	
	function resetSearchSetting(pagesize,offset,sortby,descorasc,keyword){
		currentPageIndex = 0;
		$("input[name='pagesize']").attr("value",pagesize);
		$("input[name='offset']").attr("value",offset);
		$("input[name='sortBy']").attr("value",sortby);
		$("input[name='descOrAsc']").attr("value",descorasc);
		$("input[name='keyword']").attr("value",keyword);
		$("input[name='filter']").attr("value","all");
	};
	
	function switchGroupTab(selector,current,key,value){
		$(selector).removeClass('chosen');
		$(current).parent().addClass('chosen');
		refreshDataByCondition(key,value);
	}
	
	$("#sortByTime").live("click",function(){
		switchGroupTab('#sortFilter > li',$(this),"sortBy","time");
	});
	
	$("#sortByAuthor").live("click",function(){
		switchGroupTab('#sortFilter > li',$(this),"sortBy","author");
	});
	
	$("#sortByTitle").live("click",function(){
		switchGroupTab('#sortFilter > li',$(this),"sortBy","title");
	});
	
	$("#filterByPage").live("click",function(){
		switchGroupTab('#typeFilter > li',$(this),"filter","page");
	});
	
	$("#filterByFile").live("click",function(){
		switchGroupTab('#typeFilter > li',$(this),"filter","file");
	});
	
	$("#filterByBoth").live("click",function(){
		switchGroupTab('#typeFilter > li',$(this),"filter","all");
	});
	
	$("a[name=asc]").live("click",function(){
		switchGroupTab('#orderFilter > li',$(this),"descOrAsc","asc");
	});
	
	$("a[name=desc]").live("click",function(){
		switchGroupTab('#orderFilter > li',$(this),"descOrAsc","desc");
	});
	
	$('a[name=listMode]').click(function(){
		mode = 'listMode';
		refreshDataByCondition('', '');
	});
	$('a[name=compactMode]').click(function(){
		mode = '';
		refreshDataByCondition('', '');
	});
	
	var searchInCollection = new SearchBox('cMainSearch', '搜索内容和文件', '搜索', false, true);
	searchInCollection.doSearch = function(QUERY) {
		$('#typeFilter > li').removeClass('chosen');
		$("#filterByBoth").addClass('chosen');
		refreshDataByCondition("filter","all");
		refreshDataByCondition("keyword", QUERY);
	};
	searchInCollection.resetSearch = function() {
		refreshDataByCondition("keyword",'');
	};
	
	$("a[name=move-page-link]").live("click",function(){
		
	});
	
	$("#delete-current-collection").click(function(){
		var url = "<vwb:Link context='configCollection' format='url'/>?cid=${collection.resourceId}&func=isEmptyCollection";
		ajaxRequest(url,null,afterDeleteCollection);
	});
	
	function afterDeleteCollection(data){
		if(data.status=='error')
			alert(data.result);
		else{
			if(!data.isEmpty)
				alert("不能删除非空集合，请移动页面后再删除");
			else
				window.location = "<vwb:Link context='configCollection' format='url'/>?cid=${collection.resourceId}&func=deleteCollection";
		}
	};
	
	$( "#shortcut" ).sortable({change: function(event, ui) {$("#save-button").show();}});
	
	$( "#shortcut" ).disableSelection();
	
	$("#save-shortcut-sequence").click(function(){adjustPlace();});
	
	$("#save-shortcut-place-form").bind("submit",function(){adjustPlace();return false;});
	
	function adjustPlace(){
		var parameter = $("#save-shortcut-place-form").serialize()+"&cid=${collection.resourceId}&func=updateShortcutSequence";
		ajaxRequestWithErrorHandler(requestURL,parameter,afterSavePlace,notEnoughAuth);
	};
	
	function afterSavePlace(data){
		ui_spotLight('saveChanges-spotLight', 'success', '保存成功', 'fade');
		setTimeout(function() {$("#save-button").fadeOut();}, 800);
	};
	

	
});
</script>

<!-- 该页面是Collection集合的首页面 -->

	<div id="content-title">
		<input type="hidden" name="collectionId" value="${collection.resourceId}"/>
		<div id="editTool">
			<a title="编辑工具" class="editTool"><span></span></a>
			<ul id="toolGroup">
				<li><a href="<vwb:Link context='createPage' page="${collection.resourceId}" format='url'/>" class="toolNewPage"><span>+新建页面</span></a></li>
	        	<li><a href="<vwb:Link context='quick' format='url'/>?func=uploadFiles&cid=${collection.resourceId}">上传文件</a></li>
	        	<li><a href="<vwb:Link context='configCollection' format='url'/>?cid=${collection.resourceId}#pageTab">移动</a></li>
	        	<li><a href="<vwb:Link context='viewCollection' page="${collection.resourceId}" format='url'/>?func=export">导出集合</a></li>
	        	<c:if test="${collection.resourceId != 1}">
		        	<li><a id="delete-current-collection">删除本集合</a></li>
	        	</c:if>
	        	<li><a href="<vwb:Link context='configCollection' format='url'/>?cid=${collection.resourceId}" class="toolConfig"><span>设置</span></a></li>
			</ul>
			<div class="decoLeft"></div>
		</div>
		<h1>${collection.title}</h1>
    </div>
    <div id="content-major">
    	<form id="searchParameter">
    		<input type="hidden" name="cid" value="${collection.resourceId}"/>
    		<input type="hidden" name="pagesize" value="10" />
    		<input type="hidden" name="offset" value="0"/>
    		<input type="hidden" name="sortBy" value="time"/>
    		<input type="hidden" name="descOrAsc" value="desc"/>
    		<input type="hidden" name="keyword" value=""/>
    		<input type="hidden" name="filter" value="all"/>
    	</form>
    	<div id="cmainConsole" class="toolHolder">
    		<div id="cMainSearch" class="ui-RTCorner"></div>
    		<ul  class="switch" style="float:left">
    			<li class="chosen"><a >列表模式</a></li>
    			<li><a href="<vwb:Link context='viewCollection' page="${collection.resourceId}" format='url'/>?func=viewGrid">格子模式</a></li>
    		</ul>
    		<div class="ui-clear"></div>
    	</div>
    	<div class="subHolder">
    		<ul class="filter" id="typeFilter">
    			<li class='chosen'><a id="filterByBoth" href="#byBoth">全部</a></li>
        		<li ><a id="filterByPage" href="#byPage">页面</a></li>
    			<li ><a id="filterByFile" href="#byFile">文件</a></li>
        	</ul>
    		<ul class="filter dividerPrev" id="sortFilter">
    			<li <c:if test="${option eq 1}">class='chosen'</c:if>><a id="sortByTime" href="#byTime">按更新时间</a></li>
        		<li <c:if test="${option eq 3}">class='chosen'</c:if>><a id="sortByTitle" href="#byTitle">按标题</a></li>
    			<li <c:if test="${option eq 2}">class='chosen'</c:if>><a id="sortByAuthor" href="#byAuthor">按作者</a></li>
        	</ul>
        	<ul id="orderFilter" class="switch ui-RTCorner" >
       			<li title="升序"><a name="asc">升序</a></li>
       			<li class="chosen" title="降序"><a name="desc">降序</a></li>
       		</ul>
       		<div class="ui-clear"></div>
    	</div>
    	
        <div id="paging-top">
        	共<span id="result_count">${pageCount}</span>个搜索结果(<span id="page_size">${pageSize}</span>条每页)
        	<ul class="paging" style="display:none">
        		<li class="prev">&nbsp;</li>
        		<li>1/2</li>
        		<li class="next"><a href="#">下一页</a></li>
        	</ul>
        </div>
        <div id="item">
        	<ul class="list" id="mycontainer"></ul>
        	<form id="addShortcutForm">
	        	<input type="hidden" name="func" value="addShortcuts"/>
	        	<input type="hidden" name="cid" value="${collection.resourceId}"/>
	        	<input type="hidden" name="shortcutName" value=""/>
	        	<input type="hidden" name="rid" value=""/>
	        	<input type="hidden" name="resourceType" value=""/>
	        </form>
        </div>
        
        <div id="paging-bottom">
        	<ul class="paging" style="display:none">
        		<li class="prev disabled">上一页</li>
        		<li class="current">1</li>
        		<li><a href="">2</a></li>
        		<li class="next"><a href="#">下一页</a></li>
        	</ul>
        </div>
        <div class="collection-search" style="display:none">
        	<input name="search" type="text" value="仅该Collection下" /><input name="submit" type="button" value="搜" />
        </div>
        <div id="pagination"></div>
    </div>
    <div id="content-side">
    	<div class="sideBlock">
    		<h4>快捷方式</h4>
    		<form id="save-shortcut-place-form">
	       	<ul id="shortcut" class="fileList">
		        <c:choose>
		        	<c:when test="${not empty cShortcuts}">
		        		<c:forEach items="${cShortcuts}" var="item">
							<li sid="${item.id}">
								<input type="hidden" name="shortcutId" value="${item.id}"/>
								<a class="delete-shortcut lightDel ui-RTCorner" sid="${item.id}" title="移除快捷方式"></a>
								<c:choose>
									<c:when test="${item.resourceType eq 'DPage'}">
										<a href="<vwb:Link page='${item.resourceId}' context='view' format='url'/>">${item.title}</a>
									</c:when>
									<c:otherwise>
										<a href="<vwb:Link page='${item.resourceId}' context='file' format='url'/>">${item.title}</a>
									</c:otherwise>
								</c:choose>
							</li>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<p class="NA" style="margin:1em 0;">还未添加快捷方式</p>
					</c:otherwise>
				</c:choose>
			</ul>
			<div id="save-button" class="subHolder isolate holderCenter" style="display:none">
    			<input type="button" id="save-shortcut-sequence" class="largeButton" value="保存顺序"/><br/>
    			<span id="saveChanges-spotLight" class="ui-spotLight"></span>
    		</div>
    		</form>
		</div>
    </div>
    
<script id="collection-page-template" type="text/html">
<li>
	<div class="headImg duk {{= type}}"></div>
	<div class="body">
		<h4><a href="{{= realURL}}" >{{= title}}</a></h4>
		<p class="author">创建者:{{= creatorName}} | 最后修改时间: {{= modifyTime}} .</p>						
    </div>

</li>
</script>

<script id="collection-file-template" type="text/html">
<li>
	<div class="headImg fil {{= type}}"></div>
	<div class="body">
		<h4><a href="{{= realURL}}" >{{= title}}</a></h4>
		<p class="author"> 最后  | {{= version}}.</p>						
    </div>
	<div class="op">
	</div>
</li>
</script>
  
<script id="page-child-template" type="text/html">
	<span class="attachment {{= extendType}}"><a href="{{= realURL}}">{{= title}}</a></span>
</script>

<script id="page-group-template" type="text/html">
<li>
	<div class="headImg {{= type}}"></div>
	<div class="body" id="parent_{{= id}}">
		<h4><a href="{{= realURL}}" >{{= title}}</a></h4>
		<p class="author">创建者:{{= creatorName}}&nbsp;|&nbsp;修改时间:{{= modifyTime}}&nbsp;|&nbsp;版本:{{= version}}</p>
	</div>
	<div class="op">
		<a class="add-shortcut-link" name="{{= title}}" rid="{{= resourceId}}" type="DPage">添加快捷</a><br/>
		<a class="edit-link" rid="{{= resourceId}}">编辑页面</a>
	</div>
</li>
</script>

<script id="file-group-template" type="text/html">
<li>
	<div class="headImg {{= type}} {{= extendType}}"></div>
	<div class="body" id="parent_{{= id}}">
		<h4><a href="{{= realURL}}" >{{= title}}</a></h4>
		<p class="author">创建者:{{= creatorName}} | 修改时间: {{= modifyTime}} | 版本:{{= version}}</p>
	</div>
	<div class="op">
		<a class="add-shortcut-link" name="{{= title}}" rid="{{= resourceId}}" type="DFile">添加快捷</a><br/>
		<a class="download-link" rid="{{= resourceId}}">下载文件</a><br/>
		<a class="share-link" rid="{{= resourceId}}" >分享文件</a>
	</div>
</li>
</script>

<script id="shortcut-template" type="text/html">
<li sid="{{= sid}}">
	<a class="delete-shortcut lightDel ui-RTCorner" sid="{{= sid}}" title="移除快捷方式"></a>
	<a href="{{= url}}">{{= title}}</a>
</li>
</script>

<div class="ui-clear"></div>
