<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<fmt:setBundle basename="templates.default" />
<script type="text/javascript"  src="${contextPath}/scripts/jquery/jquery.tmpl.js"></script>
<script type="text/javascript" src="${contextPath}/jsp/aone/js/fileuploader.js"></script>
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/tag.css" type="text/css" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/tag-z.css" type="text/css" />
<script type="text/javascript">
$(document).ready(function(){
	var url = '<vwb:Link context="reclogging" format="url"/>';
	var cur_url ;
	
	$(".related-click-log").live("click",function(){
		var json = new Object();
		json['func'] = "cl";
		json['pid'] = $(this).attr("pid");
		json['type'] = 'click';
		json['tid'] = $(this).attr("tid");
		
		cur_url = $(this).attr("cur_url");
		ajaxRequest(url,json,afterRecordLog);
	});
	
	function afterRecordLog(data){
		//do nothing
		//window.location.href = cur_url;
	};
});
</script>	
		<div id="tagSelector" class="content-menu bundleView">
				<p class="ui-navList-title">
					<%-- <span id="bundle-title">${bundle.title }</span> --%>
					<a class="iconLink config ui-RTCorner editDes" id="config-bundle-title"></a>
					<label id="bundle-title">${bundle.title}</label>
					
				</p>
				<ul class="ui-navList" id="bundle-navList">
					<c:choose>
						<c:when test="${fn:length(resTitleList) >0 }">
							<c:forEach items="${resTitleList }" var="title" varStatus="status">
								<li <c:if test="${resource.rid eq items[status.index].rid}">class="active"</c:if> index="${status.index+1 }" resource_id="${items[status.index].rid }">
									<input name="bitem" type="hidden" value="${items[status.index].rid }">
									<a href="${urlList[status.index] }">
									<c:choose>
										<c:when test="${items[status.index].itemType eq 'Bundle'}">
									<span class="headImg ${items[status.index].itemType }"></span>
										</c:when>
										<c:when test="${items[status.index].itemType eq 'DPage'}">
									<span class="headImg ${items[status.index].itemType }"></span>
										</c:when>
										<c:otherwise>
									<span class="headImg ${items[status.index].itemType} ext ${items[status.index].fileType.toLowerCase()}"></span>
										</c:otherwise>
									</c:choose>
									${title }
									</a>
									<a class="lightDel removeFromBundle" title="从组中移出"></a>
								</li>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<li><p>资源列表为空</p></li>
						</c:otherwise>
					</c:choose>
				</ul>
				<!-- <div class="openall">展开</div> -->
				<div id="save-button" class="subHolder isolate holderCenter" style="display:none">
	    			<input type="button" id="save-bundle-sequence" class="largeButton" value="保存顺序"/>
					<input type="button" id="cancle-bundle-sequence" class="largeButton" value="恢复顺序"/>
	    			<span id="saveChanges-spotLight" class="ui-spotLight"></span>
	    		</div>
	    		
	    		<p class="ui-navList-title">描述<a class="iconLink config ui-RTCorner editDes" id="bundle-desc-manage"></a></p>
	    		<div class="description">
	    			<div id="${bundle.bid}"><p id="group_desc_class">${bundleDesc}</p></div>
	    		</div>
	    		<vwb:UserAuthCheck auth="edit">
		    		<p class="ui-navList-title">添加资源</p>
		    		<div class="new_res">
		    			<a href="<vwb:Link format='url' context='quick'/>?func=createPage&bid=${bundle.itemId}" class="largeButton small">新建页面</a>
						<vwb:CLBCanUse>
						<a href="<vwb:Link format='url' context='quick'/>?func=uploadFiles&bid=${bundle.itemId}" class="largeButton small">上传文件</a>
		    			</vwb:CLBCanUse>
		    			<a id="search-unbundle" class="largeButton small">搜索资源</a>
		    		</div>
	    		
	    		
		    		<%-- <p class="ui-navList-title"></a>标签</p>
					<table>
					<tr class="resStarAndTag">
						<td colSpan="2">
							<div class="content-resTag">
								<ul class="tagList">
									<c:if test="${bundle.tagMap.size() >0 }">
										<c:forEach items="${bundle.tagMap }" var="tag" varStatus="statusTag">
											<li tag_id="${tag.key }">
												<a href="<vwb:Link context='tag' format='url'/>#&tag=${tag.key }">${tag.value }</a>
												<a class="delete-tag-link lightDel" tag_id="${tag.key }" rid="${bundle.rid }"></a>
											</li>
										</c:forEach>
									</c:if>
									<li resource_id="${bundle.rid}"  class="newTag"><a>+</a></li>
								</ul>
							</div>
						</td>
					</tr>
					</table> --%>
					<hr />
					<p class="removeBuddle">
						<c:if test="${fn:length(resTitleList) > 0 }">
							<input id="disbandBundle" type="button" value="解散组合" class='largeButton'/>
						</c:if>
						<input id="deleteBundle" type="button" value="删除组合" class='largeButton'/>
					</p>
					<div class="ui-clear"></div>
				</vwb:UserAuthCheck>
				<p class="ui-navList-title">为您推荐可能感兴趣的页面：</p>
	    			<div id="relevance" class="sideBlock" >
		          <c:choose>
			        <c:when test="${empty relatedGrids.gridItemList }">
				       <p class="NA">暂无相关内容</p>
			        </c:when>
			      <c:otherwise>
				    <ul class="fileList">
					  <c:forEach items="${relatedGrids.gridItemList}" var="gridItem">
					    <li>	
						 <c:choose>
							<c:when test="${gridItem.item.resourceType eq 'DPage'}">
							<a class="related-click-log" target="_blank" href="<vwb:Link context='view' page='${gridItem.item.resourceId}' format='url'/>" cur_url="<vwb:Link context='view' page='${gridItem.item.resourceId}' format='url'/>" pid='${gridItem.item.resourceId}' tid='${gridItem.item.tid}'>${gridItem.title}</a>
						<!-- 		<a href="<vwb:Link context='view' page='${gridItem.item.resourceId}' format='url'/>">${gridItem.title}</a> -->
							</c:when>
							<c:otherwise>
								<a href="<vwb:Link context='file' page='${gridItem.item.resourceId}' format='url'/>">${gridItem.title}</a>
							</c:otherwise>
						 </c:choose>
					   </li>
					</c:forEach>
				    </ul>
			      </c:otherwise>
		      </c:choose>
	        </div>
			</div>
			<div id="main_data_area">
			<div class="content-menu-body bundleContent">
				<c:choose>
					<c:when test="${fn:length(resTitleList) >0 }">
						<c:choose>
							<%-- <c:when test = "${type eq 'allfile' }">
								<jsp:include page="bundle-file.jsp"></jsp:include>
							</c:when>--%>
							<c:when test="${type eq 'allpic' }">
								<jsp:include page="bundle-picture.jsp"></jsp:include>
							</c:when> 
							<c:when test="${type eq 'DFile' or type eq 'allfile'}">
								<script type="text/javascript">
									$(document).ready(function(){
										var navurl = new Array();
										<c:forEach items="${urlList}" var = "url" varStatus="status">
											navurl[${status.index}] = "${url}";
										</c:forEach>
										position();
										function position(){
											var exist = false;
											var cururl = window.location.pathname+window.location.search;
											if(navurl.length==1){
												$("#pagenext").attr('disabled', 'disabled');
												$("#pageprev").attr('disabled', 'disabled');
											}else if(cururl == navurl[0]||cururl==navurl[0].substring(0,navurl[0].indexOf("?"))){
												$("#pageprev").attr('disabled', 'disabled');
												$("#pagenext").removeAttr('disabled');
											}else if(cururl == navurl[navurl.length-1]){
												$("#pagenext").attr('disabled', 'disabled');
												$("#pageprev").removeAttr('disabled');
											}
											for(var i=0;i<navurl.length;i++){
												if(cururl == navurl[i]){
													$("#pageInfo").html("<p> "+(i+1)+" / "+navurl.length+" </p>");
													exist = true;
													return;
												}
											}
											if(!exist)
												$("#pageInfo").html("<p> 1 / "+navurl.length+" </p>");
										};
										$("#pageprev").click(function(){
											var cururl = window.location.pathname+window.location.search;
											for(var i = 0; i < navurl.length; i++)
												if(cururl == navurl[i]){
													if(i!=0)
														window.location.href = navurl[i-1];
												}
										});
										$("#pagenext").click(function(){
											var next = navurl[1];
											var cururl = window.location.pathname+window.location.search;
											for(var i = 0; i < navurl.length; i++)
												if(cururl == navurl[i]){
													if(i==(navurl.length-1))
														next = cururl;
													else
														next = navurl[i+1];
												}
											if(next!=cururl)
												window.location.href = next;
										});
										
									});
								</script>
								<div class="prevNext">
									<input type="button" id="pageprev" class="prevOne" value=""/>
									<div id="pageInfo"></div>
									<input type="button" id="pagenext" class="nextOne" value=""/>
									<div class="ui-clear"></div>
								</div>
								<jsp:include page="bundle-mix-file.jsp"></jsp:include>
							</c:when>
							<c:otherwise>
								<script type="text/javascript">
									$(document).ready(function(){
										var navurl = new Array();
										<c:forEach items="${urlList}" var = "url" varStatus="status">
											navurl[${status.index}] = "${url}";
										</c:forEach>
										
										position();
										
										function position(){
											var exist = false;
											var cururl = window.location.pathname+window.location.search;
											if(navurl.length==1){
												$("#pagenext").attr('disabled', 'disabled');
												$("#pageprev").attr('disabled', 'disabled');
											}else if(cururl == navurl[0]||cururl==navurl[0].substring(0,navurl[0].indexOf("?"))){
												$("#pageprev").attr('disabled', 'disabled');
												$("#pagenext").removeAttr('disabled');
											}else if(cururl == navurl[navurl.length-1]){
												$("#pagenext").attr('disabled', 'disabled');
												$("#pageprev").removeAttr('disabled');
											}
											for(var i=0;i<navurl.length;i++){
												if(cururl == navurl[i]){
													$("#pageInfo").html("<p> "+(i+1)+" / "+navurl.length+" </p>");
													exist = true;
													return;
												}
											}
											if(!exist)
												$("#pageInfo").html("<p> 1 / "+navurl.length+" </p>");
											
												
										};
										
										$("#pageprev").click(function(){
											var cururl = window.location.pathname+window.location.search;
											for(var i = 0; i < navurl.length; i++)
												if(cururl == navurl[i]){
													if(i!=0)
														window.location.href = navurl[i-1];
												}
										});
										$("#pagenext").click(function(){
											var next = navurl[1];
											var cururl = window.location.pathname+window.location.search;
											for(var i = 0; i < navurl.length; i++)
												if(cururl == navurl[i]){
													if(i==(navurl.length-1))
														next = cururl;
													else
														next = navurl[i+1];
												}
											if(next!=cururl)
												window.location.href = next;
										});
										
										
									});
								</script>
								<div class="prevNext">
									<input type="button" id="pageprev" class="prevOne" value=""/>
									<div id="pageInfo"></div>
									<input type="button" id="pagenext" class="nextOne" value=""/>
									<div class="ui-clear"></div>
								</div>
								<jsp:include page="bundle-mix-page.jsp"></jsp:include>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<div><h1>组合为空</h1></div>
					</c:otherwise>
				</c:choose>
			</div>
			<div class="ui-clear"></div>
			</div>
			
			<div id="addSingleTagDialog" class="lynxDialog">
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
					<input type="button" class="saveThis" value="保存">
					<input type="button" class="closeThis" value="取消">
				</div>
			</div>

<div class="ui-dialog" id="delete-bundle-dialog" style="width:400px;">
	<p class="ui-dialog-title">删除组合</p>
	<p>您真的要删除此组合吗？</p>
	<p style="color:red">提示：该操作将会使组合所有页面和文件被删除。请谨慎操作！</p>
	<div class="ui-dialog-control">
		<form id="deleteBundleForm" action='<vwb:Link format='url' context='bundle' page='${bundle.itemId}'/>?func=deleteBundle' method="POST">
			<input type="submit" value="删除"/>
			<a id="delete-cancel" name="cancel">取消</a>
		</form>
	</div>
</div>

<div class="ui-dialog" id="bundle-search-dialog" style="width: 500px;">
	<p class="ui-dialog-title">添加资源到组合</p>
	<div class="ui-widget">
		<input type="text" id="search-keyword" value="输入关键字检索已有资源"/>
		<input type="button" id="bundle-search" value="搜索" class='largeButton small'/>
	</div>
	
	<div id="bundle-search-div">
		<ul class='ui-navList' id="bundle-search-result"></ul>
		<input id='addBundleItem' type='submit' value='保存到组合'/>
		<a name="cancel">取消</a>
		<div class="ui-clear"></div>
	</div>
</div>

<div class="ui-dialog" id="delete-bundleerror-dialog"
	style="width: 500px; position: fixed; left: 30%;">
	<p class="ui-dialog-title">删除错误</p>
	<p style="color:red;line-height:70px;">组合本身或组合内的资源中包含其他成员创建的资源，您无权删除该组合！</p>
</div>
<div class="ui-dialog" id="conflict-bundle-item-dialog" style="width: 400px;">
	<p class="ui-dialog-title">操作冲突</p>
	<p class="ui-dialog-content">您添加进组合的资源与其他人的操作冲突，这些资源已经属于某个组合了，他们将不会被添加进当前组合！</p>
	<p class="ui-dialog-content">冲突资源的详细内容如下：</p>
	<div style="max-height:300px; overflow:auto;"><ul id="conflict-bundle-item-list"></ul></div>
	<div class="ui-dialog-control">
		<input type="button" value="我知道了" id="conflict-bundle-item-cancel"/>
	</div>
</div>

<script type="text/javascript" src="${contextPath}/scripts/editable/jquery.editable-1.3.3.js"></script>
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
	/*remove the banner image---vera*/
	$(window).unbind('.localNav');
	$("#masthead").css({"height":"60px"});
	$("#navigation").addClass("fixed");
	$("#banner").hide();
	$('#footer').hide();
	$('body').append('<div class="bedrock" style="height:30px;"></div>');
	
	$("ul#bundle-navList li .removeFromBundle").live("click",function(e){
		e.stopPropagation();
		var rid = $(this).parents("li[resource_id]").attr("resource_id");
		var requestURL = site.getURL("bundle","${bundle.itemId}")+"?func=removeBundleItem";
		ajaxRequestWithErrorHandler(requestURL,"rid="+rid,function(data){
			if(data!="undefined"&&data.lockStatus=="error"){
				alert("当前文件正在被编辑！");
				return;
			}
			window.location.href = site.getURL("bundle","${bundle.itemId}");
		},notEnoughAuth);
	});
	
	function notEnoughAuth(){
		alert("您无权进行此操作！");
	};
	$("#delete-cancel").live("click",function(){
		$("#delete-bundle-dialog").hide();
	});
	$("#bundle-navList").sortable({change: function(event, ui) {$("#save-button").show();}}); 
	
	var fixOrNot = setInterval(function (){
		if($("#tagSelector.bundleView").outerHeight() > ($(window).height()*0.8)){
			$("#tagSelector.bundleView").css({"position":"absolute"});
		}
		else{
			$("#tagSelector.bundleView").css({"position":"fixed"});
		}
	},20);
	
	//window.location.href = site.getURL('tag',null)+"#&tag="+tagid;
	//<vwb:Link context="tag" format="url"/>
	$("#cancle-bundle-sequence").live("click",function(){
		window.location.reload();
	});
	
	$("#save-bundle-sequence").live("click",function(){
		var datas = "";
		$("ul#bundle-navList li").each(function(index){
			datas = datas + '"' + $(this).children("input").attr("value") + '":"' + (index + 1) + '",';
		})
		if(datas!=""){
			datas = datas.substring(0,datas.lastIndexOf(","));
			datas = "{" + datas +"}";
		}
		$("#save-button").hide();
		var requestURL = site.getURL("bundle","${bundle.itemId}");
		ajaxRequestWithErrorHandler(requestURL,"func=reorder&order=" + datas,function(){},notEnoughAuth);
	});
	
	$('input#disbandBundle').click(function(){
		window.location.href=site.getURL("bundle","${bundle.itemId}")+"?func=disbandBundle";
	});
	
	$('input#deleteBundle').click(function(){
		$('#delete-bundle-dialog').attr('style','width:400px; position:fixed; top:30%; left:30%;').fadeIn();
	});

/*--------------	search unbundle resource start ------------------- */

	$('#search-unbundle').click(function(){
		ui_showDialog("bundle-search-dialog");
	});
	
	$('#search-keyword').live("focus",function(){
		$(this).attr('value','');
	});
	
	$('#search-keyword').live("blur",function(){
		var text = $(this).attr('value');
		if(text == "")
			$(this).attr('value','输入关键字检索已有资源');
	});

	var offset = 0;
	var size = 15;
	var curTitle = "";
	
	function searchUnbundle(){
		var title = $("#search-keyword").val();
		if(title == "输入关键字检索已有资源"){
			$('#search-keyword').attr('value',"");
			title="";
		}
		if(title != curTitle){
			$("#bundle-search-result").html("");
			curTitle = title;
			offset=0;
		}
		var requestURL = site.getURL("bundle","${bundle.itemId}");
		var params = {"func":"getUnBundle", "title":title, "offset":offset, "size":size};
		ajaxRequestWithErrorHandler(requestURL,params,renderData,notEnoughAuth);
	}
	
	$("a#loadMoreUnbundle").live('click',searchUnbundle);
	$("#bundle-search").live("click",searchUnbundle);
	
	function renderData(data){
		if(typeof(data)== 'undefined'|| data.length == 0){
			$("#bundle-search-result").html("");
			$("#bundle-search-result").append("<p>没有搜索到匹配的资源</p>");
			return;
		}
		var $obj = $("a#loadMoreUnbundle");
		if(typeof($obj) != 'undefined'){
			$obj.remove();
		}
		data = eval(data);
		$("#unbundle-resource-template").tmpl(data).appendTo("#bundle-search-result");
		offset += data.length;
		if(data.length == size){
			$("#bundle-search-result").append("<li class='loadMore'><a id='loadMoreUnbundle'>更多</a></li>");
		}
	}
	
	$('#addBundleItem').click(function(){
		ui_hideDialog("bundle-search-dialog");
		var requestUrl = site.getURL("bundle",${bundle.itemId});
		var rids = new Array();
		var i=0;
		$('input[type=checkbox][name=selectItem]').each(function(){
			if($(this).attr('checked')){
				rids[i]=$(this).attr("value");
				i++;
			}
		});
		var params = {"func":"saveBundleItem","selectItem[]":rids};
		ajaxRequestWithErrorHandler(requestUrl,params,function(data){
			if(typeof(data.newItems)!="undefined" && data.newItems.length>0){
				$('#bundle-item-template').tmpl(data.newItems).prependTo('#bundle-navList');
			}
			if(typeof(data.conflictItems)!="undefined" && data.conflictItems.length>0){
				$("#conflict-bundle-item-list").html("");
				$("#conflict-bundle-item-template").tmpl(data.conflictItems).prependTo("#conflict-bundle-item-list");
				ui_showDialog("conflict-bundle-item-dialog");
			}
		},notEnoughAuth);
	})
	
	$("#conflict-bundle-item-cancel").click(function(){
		ui_hideDialog("conflict-bundle-item-dialog");
	});
	
	$('a[name=cancel]').click(function(){
		ui_hideDialog("bundle-search-dialog");
		$("#bundle-search-result").html("");
		curTitle = "";
		offset=0;
	});
/*------------------   search unbundle resource end  -------------------- */

	$('#resAction .fullScreen').toggle(
		function(){
			$("body").addClass("fullScreenView");
			$("#macroNav,#masthead,#tagSelector,#footer").hide();
			$(this).find("a").html('<span class="resAction-fullScreen"></span>退出全屏');	
		}, 
		function(){
			$("body").removeClass("fullScreenView");
			$("#macroNav,#masthead,#tagSelector,#footer").show();
			$(this).find("a").html('<span class="resAction-fullScreen"></span>全屏阅读');	
		}
	);

	/*-------------delete---------------*/
	$("a.delete-tag-link").live('click',function(){
		var rids = new Array();
 		rids[0] = $(this).attr("rid");
    	var params = {"func":"remove","rid[]":rids,"tagId":$(this).attr("tag_id")};
    	var url = site.getURL('tag',null);
    	var $a = $(this);
		ajaxRequestWithErrorHandler(url,params,function(data){
    		$a.parent().remove();
    	},notEnoughAuth);
    });
	
	/*----------- add tag dialog begin ---------------*/
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
  				var tagURL = "<vwb:Link context='tag' format='url'/>";
  				//var params = {"func":"batchAdd","newTags[]":aTBox.log.create,"existTags[]":aTBox.log.add,"rids[]":aTBox.log.rid};
  				var params = {"func":"batchAdd","newTags[]":getNewTags(),"existTags[]":getExistTags(),"rids[]":aTBox.log.rid};
  				ajaxRequestWithErrorHandler(tagURL,params,function(data){
  					$('li.newTag[resource_id="' + aTBox.log.rid[0] + '"]').before($("#page-tag-template").tmpl(data));
  					addSingleTagDialog.hide();
  				},notEnoughAuth);
  			},
  			loadAllTeamTags:function(){
  				var url = site.getURL("tag",null);
  				ajaxRequest(url,"func=loadTeamTags",function(data){
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
	addTag_handler.loadAllTeamTags();
	
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

	$('li.newTag').each(function(){
		$(this).click(function(){
			aTBox.prepare({ ul: $(this).parent(), ridArr: [$(this).attr('resource_id')] });
			addSingleTagDialog.show();
			$("#token-input-").focus();
		});
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
		aTBox.prepare({ ul: $(this).parent().parent() });
		addSingleTagDialog.$dialog.removeClass('multiple');
		addSingleTagDialog.show();
	});
  
    
/*---------------add tag dialog end---------------*/
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
 				}else{
 					alert("save failed");
 				}
 			}
	  	});
 	});
 	
 	
/*------------------update bundle description------------------*/
 var oo=$('#group_desc_class').editable({editBy : "dblclick",type : "textarea" ,submit : '提交',cancel:'取消',onSubmit:onSub});

	function onSub(content) { 
		var t=this;
		var descData = content['current'];
		var preData=content['previous'];
		if(descData==preData){
			return;
		}
		var bid = $(this).parent().attr("id");
		var tagConfigURL = site.getURL("bundle",bid);
		ajaxRequestWithErrorHandler(tagConfigURL,"func=updateBundleDesc&bid="+bid+"&description="+descData,
				function(data){},function(){
					notEnoughAuth();
					$("#group_desc_class").html(preData);
				});
	  }
	var updateBundleDesError = function(preData){
		notEnoughAuth();
		$("#group_desc_class").html(preData);
	}
	var check = true;
	$('#bundle-desc-manage').click(function(){
		if(check){
		 $('#group_desc_class').trigger("dblclick");
			check=false;			
		}else{
			 $('.cancel').mouseup();
			check=true;
		}
	});

/*-----------------------config bundle title--------------------------*/

 	$('#bundle-title').editable({editBy : "dblclick",onSubmit:onSubTitle});
 	
 	var cancelChecked = true;
	$('#config-bundle-title').live('click',function(){
		if(cancelChecked){
			$('#bundle-title').trigger('dblclick');
			cancelChecked = false;		
		}else{
			cancelChecked = true;
		}
	});
	$("#bundle-title>input").live("keyup",function(event) {
		var key = event.which;
		if(key==13||event.which == 10){
			$('#bundle-title').blur();
		}
	});


	
	function onSubTitle(content) { 
		var t=this;
		var data = content['current'];
		var preData=content['previous'];
		if(data==preData){
			return;
		}
		if(data==''||data==null){
			alert("组合标题为空");
			$(this).trigger("dblclick");
			return;
		}
		
		var requestURL = site.getURL('bundle',${bundle.itemId});
		ajaxRequestWithErrorHandler(requestURL,"func=rename&title="+content['current'],
				function(data){},notEnoughAuth);
	 };
	 
	 $("#deleteBundleForm").validate({
		 submitHandler:function(form){
			 ui_hideDialog("delete-bundle-dialog");
			 $.ajax({
				 url : "<vwb:Link format='url' context='bundle' page='${bundle.itemId}'/>?func=deleteBundleValidate",
				dataType :"json",
				type : "post",
				 success : function(data){
					 if(!data.status){
						 $('#delete-bundleerror-dialog').attr('style','width:500px; height:120px; position:fixed; top:30%; left:30%;').fadeIn();
							window.setTimeout(function(){
								$('#delete-bundleerror-dialog').fadeOut(500);
							},4000);
					 }else{
						 form.submit();
					 }
				 },
					statusCode:{
						450:function(){alert('会话已过期,请重新登录');},
						403:function(){alert('您没有权限进行该操作');}
					}
			 });
		 }
	 });
	 
	 
/*show all the resources  by zhangshixiang-2012.11.16*/	
	/* $("#tagSelector .openall").live("click",function(){
		$("ul#bundle-navList").css({"max-height":"100000000px"});
		$(this).html("收起");
		$(this).removeClass("openall").addClass("closeall");
	});
	$("#tagSelector .closeall").live("click",function(){
		$("ul#bundle-navList").css({"max-height":"300px"});
		$(this).html("展开");
		$(this).removeClass("closeall").addClass("openall");
	}) */
});
</script>


<script type="text/html" id="page-tag-template">
	<li tag_id="{{= id}}">
		<a href="<vwb:Link context='tag' format='url'/>#&tag={{= id}}">{{= title}} </a>
		<a class="delete-tag-link lightDel" tag_id="{{= id}}" rid="{{= item_key}}"></a>
	</li>
</script>
<script type="text/html" id="unbundle-resource-template">
	<li>
		<input type="checkbox" value="{{= rid}}" name="selectItem"/>
		<span class="headImg {{= itemType}} {{= fileType}}"></span>
		<span>{{= title}}</span>
	</li>
</script>
<script type="text/html" id="bundle-item-template">
	<li resource_id="{{= rid}}">
		<input name="bitem" type="hidden" value="{{= rid}}">
		<a href="{{= url}}">
			<span class="headImg {{= itemType}} ext {{= fileType}}"></span>
			{{= title}}
		</a>
		<a class="lightDel removeFromBundle" title="从组中移出"></a>
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
</html>