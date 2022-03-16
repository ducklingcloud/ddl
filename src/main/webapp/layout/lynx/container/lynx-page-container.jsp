<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<link href="${contextPath}/scripts/bootstrap/css/bootstrap.min.css" rel="stylesheet" media="screen"/>
<link href="${contextPath}/scripts/bootstrap/css/bootstrap-responsive.min.css" rel="stylesheet"/>
<link href="${contextPath}/scripts/bootstrap/css/todc-bootstrap.css" rel="stylesheet"/>
<fmt:setBundle basename="templates.default" />

<script type="text/javascript" src="${contextPath}/jsp/aone/js/fileuploader.js?v=${aoneVersion}"></script>

<link href="${contextPath}/jsp/aone/css/fileuploader.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/tokenInput.css" type="text/css" />
<script type="text/javascript" src="${contextPath}/jsp/aone/js/toker-jQuery-forTag.js"></script>
<script type="text/javascript" src="${contextPath}/jsp/aone/js/global.js"></script>
<script type="text/javascript" src="${contextPath}/jsp/aone/js/pv-timing.js"></script>

<link rel="stylesheet" href="${contextPath}/jsp/aone/css/lynx.css?v=${aoneVersion}" type="text/css" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/lion.css?v=${aoneVersion}" type="text/css" />
<style type="text/css">
<!--
ul.fileList li{list-style:none;border-bottom:none;vertical-align:bottom;padding:0;margin:5px;}
ul.fileList span{vertical-align:middle; margin-right:4px;}
#exist-attach-list .ellipsis{ overflow:hidden;text-overflow: ellipsis; white-space:nowrap;display:inline-block; width:182px;}
-->
</style>

<script type="text/javascript">
$(document).ready(function(){
	var menu = $('.fixedMe');
	var pos = menu.offset().top;
	var top = 50;
	var newTop = $('a#attachFile-button').offset().top;
	menu.css('top', top);
	$(window).scroll(function(){
		if ($(document).scrollTop()-pos > newTop) {
			menu.css('display', 'block');
			menu.css('position', 'fixed');
			menu.css('width', '285px');
		}
		else {
			menu.css('display', 'none');
			menu.css('position', '');
		}
	});

	$('input.tagPoolAutoShow').tokenInput("<vwb:Link context='tag' format='url'/>?func=loadTeamTags&type=nogroup", {
		theme:"facebook",
		hintText: "输入标签名称，以逗号或回车结束",
		searchingText: "正在搜索……",
		noResultsText: "没有该标签，输入逗号或回车添加",
		preventDuplicates: true,
	});


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
$(document).ready(function(){

	function placeEdge() {
		var cont = $('#DCT_viewcontent');
		var major = $('#content-major');
		$('#fullScreenEdge').css('height', cont.height())
			.css('top', cont.offset().top)
			.css('left', major.offset().left + major.outerWidth()-11);
	}
	$(window).resize(function(){ placeEdge(); });
	placeEdge();

	var DOMobj = document.getElementById('DCT_viewcontent');
	var jQobj = $('#DCT_viewcontent');
	var contentWidth = DOMobj.scrollWidth + jQobj.outerWidth(true) - jQobj.width();
	var w = DOMobj.scrollWidth;
	var m = jQobj.outerWidth(true) - jQobj.width();

	$("a[name='movePage']").live("click",function(){
		var url = "<vwb:Link context='configCollection' format='url'/>?func=loadCollectionList&cid=${collection.resourceId}";
		ajaxRequest(url,null,afterLoadCollections);
	});

	function afterLoadCollections(data){
		$("#candidats-container ul").html("");
		if(data!=null){
			if(data.length==0)
				alert("没有可移动集合");
			else{
				for(var i=0;i<data.length;i++){
					$("#single-collection-template").tmpl(data[i]).appendTo("#candidats-container ul");
				}
				ui_showDialog("move-page-dialog");
			}
		}
	};

	var upload_base_url = "<vwb:Link context='upload' format='url'/>";
	var upload_url = "<vwb:Link context='upload' format='url'/>?func=uploadFiles";

	var uploadedFiles = [];
	var index = 0;

	function createUploader(){
         var uploader = new qq.FileUploader({
             element: document.getElementById('file-uploader-demo1'),
             action: upload_url,
             params:{rid:"${rid}"},
             onComplete:function(id, fileName, data){
             	uploadedFiles[index] = data;
             	index ++;
             },
             debug: true
         });
     };

     createUploader();

    $("a[name='cancel']").live("click",function(){
    	ui_hideDialog("upload-attach-dialog");
    });

    $("#attach-to-this-page").live("click",function(){
    	 var fids = [];
	   	 for(var i=0;i<uploadedFiles.length;i++){
	   		 fids[i]=uploadedFiles[i]["fid"];
	     	 $("#aone-image-row-template").tmpl(uploadedFiles[i]).appendTo("#exist-attach-list");
	   	 }
	   	 if(fids.length>0){
		   	 var params = {"func":"referFiles","rid":"${rid}","fids[]":fids,"title":$("#pageTitle").html(),"parentRid":$("#bid-field").val()};
		   	 ajaxRequest(upload_base_url,params,function(data){
		   		if(data.parentRid!=0){
			   		window.location.href = site.getURL("bundle",data.parentRid);
		   		}
		   	 });
	   	 }
	   	 $(".qq-upload-list").html("");
	   	 uploadedFiles = new Array();
	   	 index = 0;
	   	 ui_hideDialog("upload-attach-dialog");
    });

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

    function notEnoughAuth(){
		alert("您无权进行此操作！");
	};

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
  	var tag_handler = {
  			addTagForSingleRecord:function(){
  				var tagURL = "<vwb:Link context='tag' format='url'/>";
  				//var params = {"func":"batchAdd","newTags[]":aTBox.log.create,"existTags[]":aTBox.log.add,"rids[]":aTBox.log.rid};
  				var params = {"func":"batchAdd","newTags[]":getNewTags(),"existTags[]":getExistTags(),"rids[]":aTBox.log.rid};
  				ajaxRequestWithErrorHandler(tagURL,params,function(data){
  					$(".content-resTag ul.tagList li.newTag").before($("#page-tag-template").tmpl(data));
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
	tag_handler.loadAllTeamTags();

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

	$('.content-resTag ul.tagList li.newTag').click(function(){
		aTBox.prepare({ ul: $(this).parent() });
		addSingleTagDialog.show();
		$("#token-input-").focus();
	});
	$("#addSingleTagDialog .saveThis").click(function(){
		var feedback = tag_handler.addTagForSingleRecord();
	});

	$('ul.tagList li.newTag')
	.unbind('.lynx.callAddSingleTagDialog')
	.bind('click.lynx.callAddSingleTagDialog', function(event){
		event.stopPropagation();
		aTBox.prepare({ ul: $(this).parent() });
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


	function afterSubscription(data){
		var str = '您已关注了：';
		$("input[name='publisher']").each(function(){
			if($(this).attr('checked')=="checked")
				str += $(this).attr('attr')+" ";
		});
		  showMsg(str,"success");
		   hideMsg(3000);
		$(".icon-checkSub").addClass("checked");
	};

	function afterRemoveSubscription(data){
		var str='';
		if(data.status=='noExist'){
			$("input[name='subscriptionStatus']").attr('value','noExistSubscription');
			str='关注';
			window.setTimeout(function(){window.location.reload();}, 1500);
		}
		else{
			str='你已取消了关注';
			$(".icon-checkSub").removeClass("checked");
			showMsg(str,"success");
			hideMsg(3000);
		}
		$("input[name='existItemSize']").attr('value',data.size);
	};

	$(".icon-checkSub").live('click',function(){
		if(!$(this).hasClass("checked")){
			ajaxRequest("<vwb:Link context='feed' format='url'/>",{"func":"addPageFeed","pid":"${rid}"},afterSubscription);
		}else{
			ajaxRequest("<vwb:Link context='feed' format='url'/>?func=removePageFeed&pid=${rid}","",afterRemoveSubscription);
		}
	});
	$("#showHotCode").live('click',function(){
		$("#visitor").toggle();
	});

	$('#fullScreen').click(function(){
		if($("#content-major").hasClass("fullScreenView")){
			exitFullscreen();
		}else{
			enterFullscreen();
		}
	});
	function enterFullscreen(){
		$("#content-major").addClass("fullScreenView");
		$("#content-side").hide();
		$("#macroNav,#masthead,#tagSelector,#footer").hide();
		$('#fullScreen').html('<i class="icon-resize-small" ></i> 退出全屏');
	}
	function exitFullscreen(){
		$("#content-major").removeClass("fullScreenView");
		$("#content-side").show();
		$("#macroNav,#masthead,#tagSelector,#footer").show();
		$('#fullScreen').html('<i class="icon-fullscreen" ></i> 全屏阅读');
	}
	$(document).keyup(function(KEY){
		if (KEY.which=='27' && $("#content-major").hasClass("fullScreenView")) {
			exitFullscreen();
		}
	});
});
</script>


<div id="opareteFileMessage"  class="alert alert-success" style="margin:8px;display: none;"> </div>
<div>
<div id="content-major">
	<div id="content-title">
		<h1 class="fileName">
			<div class="title-right">
				<c:choose>
					<c:when test="${resource.status=='unpublish' }">
						<a class="versionCode smallBtn">草稿 </a>
					</c:when>
					<c:otherwise>
						<a class="versionCode smallBtn" href="<vwb:Link context='info' format='url' page='${rid}'/>">版本  <span class="count">${version}</span></a>
					</c:otherwise>
				</c:choose>
				<a id="showHotCode" class="hotCode smallBtn" rel="popover" data-placement="top" >热度  <span class="count"><vwb:VisitCount rid="${rid }" /></span> <span id="hotCode"></span></a>
				<a id ="fullScreen" class="smallBtn"><i class="icon-fullscreen" ></i> 全屏阅读</a>
			</div>
			<div class="title-left">
				<span id="pageTitle" rid="${rid }" parentId="${resource.bid}">${resource.title }</span><span id="suffixTitle">.ddoc</span>
			</div>
		</h1>
		<jsp:include page="/jsp/aone/browse/pageReadLog.jsp"></jsp:include>
		<div class="clear"></div>
		<c:if test="${'true' eq autoClosepageFlage}">
			<div style="color:#ff6600;text-align:right; margin:10px 50px;">该页面长时间没有编辑被自动关闭，您上次编辑的内容被临时保存，再次编辑时找回！</div>
		</c:if>
		<c:if test="${resource.status != 'unpublish' }">
		<c:choose>
			<c:when test="${starmark}">
				<div class="iconLynxTag icon-checkStar withTag checked" style="margin-top:-5px;" rid="${resource.rid }" alt="星标" title="星标">&nbsp;</div>
			</c:when>
			<c:otherwise>
				<div class="iconLynxTag icon-checkStar withTag" style="margin-top:-5px;" rid="${resource.rid }" alt="星标" title="星标">&nbsp;</div>
			</c:otherwise>
		</c:choose>
		<c:choose>
			<c:when test="${subFlag}">
				<div class="iconLionFocus icon-checkSub withTag checked" style="margin-top:-5px;" rid="${resource.rid }" alt="关注" title="关注">&nbsp;</div>
			</c:when>
			<c:otherwise>
				<div class="iconLionFocus icon-checkSub withTag" style="margin-top:-5px;" rid="${resource.rid }" alt="关注" title="关注">&nbsp;</div>
			</c:otherwise>
		</c:choose>
		<div class="content-resTag" style="float:left;width:85%; margin-top:-5px;">
			<ul class="tagList">
				<c:forEach items="${resource.tagMap}" var="tagItem">
					<li tag_id="${tagItem.key}"><a target="_blank" href="<vwb:URLGenerator pattern='files'/>#tagId=${tagItem.key}&queryType=tagQuery">${tagItem.value}</a>
						<a class="delete-tag-link lightDel" tag_id='${tagItem.key}' rid="${resource.rid}"></a>
					</li>
				</c:forEach>
				<li class="newTag" rid="${resource.rid}"><a>+</a></li>
			</ul>
		</div>
		</c:if>
		<div class="ui-clear"></div>
		<div id="version">
			<a href="<vwb:Link context='userguide'  format='url'/>?func=redirect&uid=${pageDetail.editor}" class="uidToolTip" rel="tooltip" data-placement="bottom" data-original-title="${pageDetail.editor}" target="_blank">${editor}</a> | 修改于
			<fmt:formatDate value="${resource.lastEditTime}" type="both" dateStyle="medium"/>
			|&nbsp;<vwb:fileSize size="${pageDetail.size }"/>
			<c:if test="${!empty copyLog}">
				<br>
				${ copyLog.userName} 从 团队 [${copyLog.fromTeamName}]复制了页面 [${copyLog.rTitle }] 版本：${copyLog.fromVersion}
			</c:if>
		</div>
	</div>
	<div class="ui-clear"></div>
	<div id="DCT_viewcontent">
		<vwb:render content="${content}"/>
		<div class="ui-clear"></div>
	</div>
	<div id="overflowShade"></div>

	<%-- <div id="readTool">
		<jsp:include page="/jsp/aone/pageBar.jsp"></jsp:include>
	</div> --%>
	<hr />
	<div id="comment">
		<c:set var="itemId" value="${rid}" scope="request"/>
		<c:set var="itemType" value="DPage" scope="request"/>
		<jsp:include page="/jsp/aone/comment/displayComment.jsp"></jsp:include>
	</div>
</div>
  </div>
<div id="content-side">
	<div id="attachment" class="sideBlock">
    	<div class="sideCenter">
    		<c:if test="${pageMeta.itemType eq 'DDoc' }">
	    		<a class="btn btn-large btn-success editPage"><i class="icon-edit icon-white" style="margin-left:25px"></i> &nbsp;编辑</a>
    		</c:if>
    		<c:if test="${pageMeta.itemType eq 'DPage' }">
	    		<a class="btn btn-large btn-success editPage"><i class="icon-edit icon-white" style="margin-left:25px"></i> &nbsp;编辑</a>
    		</c:if>
    		<c:if test="${pageMeta.itemType eq'DFile' }">
	    		<a class="btn btn-large btn-success editPage"><i class="icon-edit icon-white" style="margin-left:25px"></i> &nbsp;编辑</a>
    		</c:if>
    	</div>
        <!-- Disabled <2022-03-15 Tue> -->
    	<!-- <div class="sideCenter">
    	     <a id ="share" class="btn btn-large"><i class="icon-share" style="margin-left:25px"></i> 分享</a>
    	     </div> -->
    	<!-- <div class="sideCenter">
    	     <a class="btn btn-large" href="<vwb:Link context='f' page='${rid}' format='url'/>?func=exportPdf"><span class="icon-pdf" style="margin-left:25px"></span>&nbsp;&nbsp;导出</a>
    	     </div> -->
    	<h4 class="sideTitle"><span>文件操作</span></h4>
    	<c:if test="${resource.status!='unpublish' }">
	    	<div class="sideOper"><a id="fileMove" href="javascript:void(0);"><i class="icon-move"></i> 移动</a></div>
	    	<div class="sideOper"><a id="fileCopy" href="javascript:void(0);"><i class="icon-copy"></i> 复制</a></div>
	    	<div class="sideOper"><a id="fileRename" href="javascript:void(0);"><i class="icon-edit"></i> 重命名</a></div>
    	</c:if>
	    <div class="sideOper"><a id="fileDelete" href="javascript:void(0);"><i class="icon-trash"></i> 删除</a></div>
		<h4 class="sideTitle"><span>相关文件</span></h4>
		<ul id="exist-attach-list" class="fileList">
			<c:forEach items="${refView}" var="item">
				<li>
					<c:set var="fileFullname" value="${item.pageName}${item.dfileRef.itemType == 'DPage'? '.ddoc' : ''}" />
					<span class="${item.dfileRef.itemType} headImg ${item.dfileRef.fileType}"></span><a class="file ellipsis" rid="${item.dfileRef.fileRid}" href="${teamHome}/r/${item.dfileRef.fileRid}" title='${fileFullname }'>${fileFullname }</a>
					<c:if test="${item.dfileRef.itemType == 'DFile'}">
						<a style="float:left;margin-left:5px;" href="<vwb:Link context='download' format='url'/>${item.dfileRef.fileRid}?type=doc&imageType=original"> <i class="icon-download-alt"></i></a>
					</c:if>
					<a class="lightDel" rid="${item.dfileRef.fileRid}"> </a>
				</li>
			</c:forEach>
		</ul>
		<div class="sideOper">
				<a id="attachFile-button" ><i class="icon-plus"></i> 添加相关文件</a>
		</div>
		<div class="fixedMe" style="display:none">
	    	<div class="sideOper">
	    		<c:if test="${pageMeta.itemType eq 'DDoc' }">
		    		<a class="editPage"><i class="icon-edit"></i> 编辑</a>
	    		</c:if>
	    		<c:if test="${pageMeta.itemType eq 'DPage' }">
		    		<a class="editPage"><i class="icon-edit"></i> 编辑</a>
	    		</c:if>
	    		<c:if test="${pageMeta.itemType eq'DFile' }">
		    		<a class="editPage"><i class="icon-edit"></i> 编辑</a>
	    		</c:if>
	    	</div>
	    	<div class="sideOper">
	    		<a id ="shareSub" ><i class="icon-share"></i> 分享</a>
	    	</div>
	    	<div class="sideOper">
	    		<a href="<vwb:Link context='f' page='${rid}' format='url'/>?func=exportPdf"><span class="icon-pdf"></span> 导出</a>
	    	</div>
	    	<c:if test="${resource.status!='unpublish' }">
		    	<div class="sideOper"><a id="fileMoveSub" href="javascript:void(0);"><i class="icon-move"></i> 移动</a></div>
		    	<div class="sideOper"><a id="fileCopySub" href="javascript:void(0);"><i class="icon-copy"></i> 复制</a></div>
		    	<div class="sideOper"><a id="fileRenameSub" href="javascript:void(0);"><i class="icon-edit"></i> 重命名</a></div>
	    	</c:if>
		    	<div class="sideOper"><a id="fileDeleteSub" href="javascript:void(0);"><i class="icon-trash"></i> 删除</a></div>
	    </div>
	</div>


</div>

<div class="ui-dialog" id="move-page-dialog" style="width:400px;">
	<span class="ui-dialog-x"></span>
	<p class="ui-dialog-title">
		移动页面
	</p>
	<form  action="<vwb:Link context='configCollection' format='url'/>?func=moveElement" method="POST">
		<div class="ui-dialog-body">
			<input type="hidden" name="rid" value="${rid}"/>
			<input type="hidden" id="requestVersion" value="${requestVersion }">
			<input type="hidden" name="type" value="DPage"/>
			<table class="ui-table-form" width="320px;">
				<tr><th style="text-align:left;">将页面移动到：</th></tr>
				<tr><td id="candidats-container">
					<ul class="fileList"></ul>
					</td>
				</tr>
			</table>
		</div>
		<div class="ui-dialog-control">
			<input type="submit" value="保存"/>
			<a class="ui-dialog-close ui-text-small">取消</a>
		</div>
	</form>
</div>

<div class="ui-dialog" id="upload-attach-dialog" style="width:400px;">
		<p class="ui-dialog-title">上传附件</p>

		<div id="file-uploader-demo1">
			<div class="qq-uploader">
				<div class="qq-upload-button">上传附件
					<input type="file" multiple="multiple" name="files">
				</div>
				<ul class="qq-upload-list fileList"></ul>
			</div>
		</div>

		<div class="ui-dialog-control">
			<input type="button" id="attach-to-this-page" value="完成"/>
			<a name="cancel">取消</a>
		</div>
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
		<a href="javascript:void(0)" class="saveThis btn btn-primary" >保存</a>
		<a href="javascript:void(0)" class="closeThis btn" >取消</a>
	</div>
</div>

<%-- <jsp:include page="/jsp/aone/tag/lynxPageBar.jsp"></jsp:include> --%>
<!--  -------------------------------5.18 22:04 yangxiaopeng add  start----------------------------- -->
<div class="ui-dialog" id="delete-attach-dialog" style="width:400px;">
	<p class="ui-dialog-title">删除页面</p>
	<p>您真的要删除此页面吗？</p>
	<p style="color:red">提示：该操作将会使所有关于此页面的版本链接失效！</p>
	<div class="ui-dialog-control">
		<form action='<vwb:Link format='url' context='view' page='${resource.rid}'/>?parentRid=0' method="POST" id="delPageForm">
			<input type="hidden" name="func" value="del"/>
			<input id="delPageButton" type="button" value="删除"/>
			<a id="delete-cancel" name="cancel">取消</a>
		</form>
	</div>
</div>

<div class="ui-dialog" id="delete-error-dialog"	style="width: 400px; position: fixed; left: 30%;">
	<p class="ui-dialog-title">删除错误</p>
	<p style="color: red;line-height:50px;">您无权删除该页面，只能由页面创建者或管理员进行删除！</p>
</div>

<div id="fileBrowserModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="fileBrowserModalLabel" aria-hidden="true">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3 id="fileBrowserModalLabel">移动到</h3>
	</div>
	<div class="modal-body" style="height: 345px;">
		<div id="teamSelWrapper" style="display:none;">
			<select id="teamSel">
				<option value="${myTeamCode}" id="teamSel_${myTeamId }" <c:if test="${teamType eq 'myspace'}">selected="selected"</c:if>>个人空间</option>
				<c:forEach items="${myTeamList}" var="item">
					<option value="${item.name }" id="teamSel_${item.id }" <c:if test="${teamType eq item.name}">selected="selected"</c:if>><c:out value="${item.displayName}"/></option>
				</c:forEach>
			</select>
			<span class="ui-text-note" style="color:#999;">支持跨团队复制</span>
		</div>
		<div id="file_browser"></div>
	</div>
	<div class="modal-footer">
		<button id="newNodeBtn" class="btn text-left pull-left" ><i class="icon-folder-open"></i> 新建文件夹</button>
		<button class="btn btn-primary" id="moveToBtn">确定</button>
		<button class="btn" data-dismiss="modal" aria-hidden="true">取消</button>
	</div>
</div>

<jsp:include page="/jsp/aone/resource/resourceModal.jsp"></jsp:include>

<script type="text/javascript">

$(function(){
	var resourceModal = ResourceModal.getInstance();
	resourceModal.clickOk = function(ids){
		var ridArr = new Array();
		$.each(ids, function(i,n){
			if(!isExistedResTag(n)){
				ridArr.push(n);
			}
		});

		if(ridArr.length==0){
			this.close();
			return;
		}

		var params = { "fids[]": ridArr,
				"rid": "${resource.rid}",
				"title":$("#pageTitle").html(),
				"bid": "${resource.bid}"
		};
		$.ajax({
		   type: "POST",
		   url: "<vwb:Link format='url' context='upload'/>?func=referFiles",
		   data: params,
		   dataType: "json",
		   timeout:5000,
		   success: function(resp){
			   $.each(ids,function(i,n){
					if(!isExistedResTag(n)){
						var res = resourceModal.getRecordById(n);
						var downloadStr = res.itemType =='DFile' ? "<a style=\"float:left;margin-left:5px;\" href=\"<vwb:Link context='download' format='url'/>"+
								n +"?type=doc&imageType=original\"> <i class=\"icon-download-alt\"></i></a>" : "";
						$("#exist-attach-list").append("<li><span class=\"headImg " + res.itemType + " " + res.fileType +"\"></span><a class=\"file ellipsis\" rid=\""+ n
						           +"\" href=\"${teamHome}/r/"+ n +"\" title='"+ res.fileName +"'>"+ res.fileName +"</a>"
						           + downloadStr +
									" <a class=\"lightDel\" rid=\""+ n +"\"> </a></li>");
					}
				});
		   },
		   error:function(){
			   showMsg("操作失败,请稍候再试。","error");
			   hideMsg(5000);
		   },
		   complete:function(){
			   resourceModal.close();
		   }
		});
	};

	$("#exist-attach-list .lightDel").live("click",function(){
		$.ajax({
		   type: "POST",
		   url: "${teamHome}/rr/"+ $(this).attr("rid") +"?func=deleteFileRef",
		   data: "pid=${resource.rid}",
		   dataType: "json",
		   timeout:5000,
		   success: function(resp){
				$("#exist-attach-list a[rid='"+ resp.docid +"']").parent("li").remove();
		   },
		   error:function(){
			   showMsg("操作失败,请稍候再试。","error");
			   hideMsg(5000);
		   },
		});
	});

	function isExistedResTag(rid){
		var r = false;
		$("#exist-attach-list li a").each(function(){
			if(rid == $(this).attr("rid")){
				r = true;
				return;
			}
		});
		return r;
	}

	$("#attachFile-button").bind("click",function(){
		resourceModal.open();
	});
});

$(document).ready(function(){
	$('.uidToolTip').tooltip();
	$('.editPage').live('click',function(){
		var url = "<vwb:Link format='url' context='edit' page='${resource.rid}'/>";
		var version = $("#requestVersion").val();
		if(version!=-1){
			url+="&version="+version;
		}
		window.location.href = url;
	});
	$('.toolDelete').click(function(){
		$('#delete-attach-dialog').attr('style','width:400px; position:fixed; top:30%; left:30%;').fadeIn();
	});

	$("#delPageButton").click(function(){
		ui_hideDialog("delete-attach-dialog");
		$.ajax({
			url : "<vwb:Link format='url' context='view' page='${resource.rid}'/>?parentRid=0",
			data : $("#delPageForm").serialize(),
			dataType: "json",
			type :"post",
			async: false,
			success : function(data){
				if(data.lockStatus=="error"){
					alert("当前文件正在被"+data.lockError[0].editor+"编辑，请稍后再操作！");
					return;
				}
				if(!data.status){
					$('#delete-error-dialog').attr('style','width:400px; height:120px; position:fixed; top:30%; left:30%;').fadeIn();
					window.setTimeout(function(){
						$('#delete-error-dialog').fadeOut(500);
					},4000);
				}else{
					window.location.href=data.redirectUrl;
				}
			},
			statusCode:{
				450:function(){alert('会话已过期,请重新登录');},
				403:function(){alert('您没有权限进行该操作');}
			}
		});

	})

	$('a[name=cancel]').click(function(){
		$(this).parents('div.ui-dialog').fadeOut();
	});

});

</script>
<!--  -------------------------------5.18 22:04 yangxiaopeng add  end----------------------------- -->

<script type="text/javascript">

<%--
/**
    显示消息
 * @param type success-成功,block-警告,error-错误
 */
 --%>
function showMsg(msg, type){
	type = type || "success";
	$("#opareteFileMessage").removeClass().addClass("alert alert-" + type).html(msg).show(150);
}
function hideMsg(timeout){
	timeout = timeout || 2000;
	window.setTimeout(function(){$("#opareteFileMessage").hide(150);}, timeout);
}

<%--------- file move/copy/delete start ----------%>
var original_rid = -1;
var file_operation = 'none';
$("#fileMove,#fileMoveSub").live("click",function(){
	$('#fileBrowserModalLabel').empty();
	$('#fileBrowserModalLabel').append("移动到");
	$("#fileBrowserModal").modal();
	original_rid = "${resource.rid}";
	file_operation = 'move';
});
$("#fileCopy,#fileCopySub").live("click", function(){
	$('#fileBrowserModalLabel').empty();
	$('#fileBrowserModalLabel').append("复制到");
	$("#fileBrowserModal").modal();
	original_rid = "${resource.rid}";
	file_operation = 'copy';

	$("#teamSelWrapper").show();
	$("#teamSel").val("${teamCode}");
});

$("#fileBrowserModal").on("show", function(){
	loadBrowserTree("${teamCode}");
});

$("#fileBrowserModal").on("hide", function(){
	$("#teamSelWrapper").hide();
});

var target_rid = -1;
function loadBrowserTree(teamCode){
	var url = "${contextPath}/" + teamCode + "/fileManager";
	$("#file_browser").empty();
	$("#file_browser").jstree(
			{
				"json_data" : {
					"ajax" : {
						"url" : url,
						"data" : function(n) {
							return {
								"rid" : (n.attr ? n.attr("rid").replace("node_", "") : 0),
								"func" : "list",
								"originalRid" : original_rid,
							};
						},
						"success" : function(data){
							if(data && data.length>0){
								data[0].attr.id = data[0].attr.rid;
							}
						}
					}
				},
				"plugins" : [ "themes", "json_data", "ui" ],
				"ui" : {"initially_select" : [ "node_0" ]},
				"types" : {
					"max_depth" : -2,
					"max_children" : -2,
					"valid_children" : [ "drive" ],
					"types" : {
						"default" : {
							"valid_children" : "none",
							"icon" : {
								"image" : "/zk/img/file.png"
							}
						},
						"folder" : {
							"valid_children" : [ "default", "folder" ],
							"icon" : {
								"image" : "/zk/img/folder.png"
							}
						},
						"drive" : {
							"valid_children" : [ "default", "folder" ],
							"icon" : {
								"image" : "/zk/img/root.png"
							},
							"start_drag" : false,
							"move_node" : false,
							"delete_node" : false,
							"remove" : false
						}
					}
				},
			}).bind("select_node.jstree", function(event, data) {
		target_rid = data.rslt.obj.attr("rid").replace("node_", "");
	});
}

$("#newNodeBtn").click(function(){
	var fileBrowser = $.jstree._reference("#file_browser");
	var selectedNode = fileBrowser.get_selected();

	var editedNode = $("#file_browser").find("li[rid=-1]");
	if(editedNode.attr("rid")){
		editedNode.find('.fileNameInput').select();
		return;
	}

	fileBrowser.open_node(selectedNode,function(){
		var newNode = fileBrowser.create_node(selectedNode, "inside", { "attr" : { "rel" : "folder","rid":"-1"},"data":" "});
		newNode.find("a")[0].lastChild.nodeValue = "";
		var fileName = "新建文件夹";
		var parentId = selectedNode.attr("rid").replace("node_", "");
		var editor = "<span class='editFileName'>" +
		 " 	<input class='fileNameInput' type='text' value='"+fileName+"' style='margin-bottom:0' length='250'>" +
		 "	<a class='btn btn-mini btn-primary updateFolder'><i class='icon-ok icon-white'></i></a>" +
		 "	<a class='btn btn-mini cancelFolder'><i class='icon-remove'></i></a>"+
		 "	<input type='hidden' class='foldOriganlName' value='"+fileName+"'>" +
		 "	<input type='hidden' class='parentId' value='"+parentId+"'>" +
		 "	<input type='hidden' class='opType' value='createFolder'>" +
		 "</span>";
		 newNode.append(editor);
		 fileBrowser.open_node(selectedNode);

		 newNode.find('.updateFolder').bind("click",function(){
			 addNode(newNode);
		 });
		//文件名输入回车事件
		newNode.find('input.fileNameInput').bind("keyup",function(e){
			if(e.keyCode==13){
				addNode(newNode);
			}
		});
		newNode.find('a.cancelFolder').bind("click",function(){
			newNode.remove();
		});

		newNode.find('.fileNameInput').select();
	},true);


	function addNode(newNode){
		var span = newNode.children('span.editFileName');
		var fileName = $.trim(span.find('input.fileNameInput').val());
		if(!fileName){
			alert("文件夹名不能为空");
			return;
		}
		var d = new Object();
		var opType = span.find('input.opType').val();

		d.fileName=fileName;
		d.rid=span.find('input.rid').val();
		d.parentRid=span.find('input.parentId').val();
		d.func=opType;
		d.tid = getSelectedTid();
		var opUrl="<vwb:Link context='files' format='url'/>";
		$.ajax({
			url:opUrl,
			data : d,
			type : "post",
			dataType:"json",
			success :function(data){
				if(data.result){
					newNode.children("a").append(data.resource.fileName);
					newNode.attr("rid", data.resource.rid);
					$(span).remove();
					newNode.children("a").click();
				}else{
					alert(data.message);
					newNode.remove();
					fileBrowser.select_node(selectedNode);
				}
			},
			statusCode:{
				450:function(){alert('会话已过期,请重新登录');},
				403:function(){alert('您没有权限进行该操作');}
			},
			error: function(){
				alert("请求错误,请稍候再试.");
			},
		});
	}

});

//团队ID
function getSelectedTid(){
	return $("#teamSel").find("option:selected").attr("id").replace("teamSel_","");
}

$("#teamSel").change(function(){
	loadBrowserTree($(this).val());
});

$("#moveToBtn").live('click', function(){
			$('#fileBrowserModal').modal('hide');
			if(original_rid==-1 || target_rid==-1 || file_operation=='none'){
				return ;
			}
			if(original_rid==target_rid){
				$("#opareteFileMessage").removeClass();
				$("#opareteFileMessage").addClass("alert alert-block");
				if(file_operation=="move"){
					$("#opareteFileMessage").html("不能将文件夹移动到自身");
				}else if(file_operation=="copy"){
					$("#opareteFileMessage").html("不能将文件夹复制到自身");
				}
				$("#opareteFileMessage").show();
				window.setTimeout(function(){
					$("#opareteFileMessage").hide(150);
				}, 1500);
				return;
			}

			file_manager_url = "${teamHome}/fileManager";
			if(file_operation == 'move'){
				$("#opareteFileMessage").removeClass();
				$("#opareteFileMessage").addClass("alert alert-block");
				$("#opareteFileMessage").html("正在移动");
				$("#opareteFileMessage").show();
				$.ajax({
				   type: "POST",
				   url: file_manager_url,
				   cache:false,
				   data: {
						'func' : 'move',
						'originalRid' : original_rid,
						'targetRid' : target_rid,
					},
					dataType:"json",
				   	success: function(data){
				   		$("#opareteFileMessage").removeClass();
						if (data.state==0){
							$("#opareteFileMessage").addClass("alert alert-success");
						} else if (data.state==1){
							$("#opareteFileMessage").addClass("alert alert-block");
						} else if (data.state==2) {
							$("#opareteFileMessage").addClass("alert alert-error");
						}

						$("#opareteFileMessage").html(data.msg);
						$("#opareteFileMessage").show();
						window.setTimeout(function(){
							$("#opareteFileMessage").hide(150);
							location.reload();
						}, 1500);
				   }
				});
			} else if(file_operation == 'copy') {
				$("#opareteFileMessage").removeClass();
				$("#opareteFileMessage").addClass("alert alert-block");
				$("#opareteFileMessage").html("正在复制");
				$("#opareteFileMessage").show();
				$.ajax({
				   type: "POST",
				   url: file_manager_url,
				   cache:false,
				   data: {
						'func' : 'copy',
						'originalRid' : original_rid,
						'targetRid' : target_rid,
						'targetTid' : getSelectedTid()
					},
					dataType:"json",
				   	success: function(data){
					   $("#opareteFileMessage").removeClass();
						if (data.state == 0) {
							$("#opareteFileMessage").addClass("alert alert-success");
						} else if (data.state == 1) {
							$("#opareteFileMessage").addClass("alert alert-block");
						} else if (data.state == 2) {
							$("#opareteFileMessage").addClass("alert alert-error");
						}
						$("#opareteFileMessage").html(data.msg);
						$("#opareteFileMessage").show();
						window.setTimeout(function(){
							$("#opareteFileMessage").hide(150);
						}, 1500);
				   }
				});
			}
});

//删除
$("#fileDelete,#fileDeleteSub").live('click',function(){
	var fileName = $("#pageTitle").text()+".ddoc";
	if(confirm("确定要把“"+fileName+"”删除吗？")){
		$.getJSON("${teamHome}/list?func=deleteResource",{"rid":"${resource.rid}"},function(data){
			if(data.result){
				showMsg("文件已经删除！ ");
				window.setTimeout(function(){
					if("${resource.bid}" == "0"){
						window.location = "${teamHome}/list";
					}else{
						window.location = "${teamHome}/list#path=%2F${resource.bid}";
					}
				}, 1500);
			}else{
				showMsg(data.message,"block");
				window.setTimeout(function(){
					$("#opareteFileMessage").hide(150);
				}, 2000);
			}
		});
	}
});

//重命名
$("#fileRename").live('click',function(){
	if(!$(this).hasClass("clicked")){
		$(this).addClass("clicked");
		editorFolderName("pageTitle","editFileName","DPage");
	}else{
		$("#content-title .fileNameInput").select();
	}
});
//重命名
$("#fileRenameSub").live('click',function(){
	$("#backToTop").click();
	if(!$("#fileRename").hasClass("clicked")){
		$("#fileRename").addClass("clicked");
		editorFolderName("pageTitle","editFileName","DPage");
	}else{
		$("#content-title .fileNameInput").select();
	}
});

function editorFolderName(id,opType,fileType){
	var fileName =$("#"+id).text();
	var divFloder=$("#"+id).parent();
	if(fileType=='DFile'){
		fileName=fileName.substring(0,fileName.lastIndexOf("."));
	}
	var rid=$("#"+id).attr("rid");
	var parentId=$("#"+id).attr("parentId");
	var editor = "<span class='editFileName'>" +
				 " 	<input class='fileNameInput' type='text' value=\""+fileName+"\" style='margin-bottom:0' length='250'>" +
				 "	<a class='btn btn-small btn-primary updateFolder'><i class='icon-ok icon-white'></i></a>" +
				 "	<a class='btn btn-small cancelFolder'><i class='icon-remove'></i></a>"+
				 "	<input type='hidden' class='foldOriganlName' value=\""+fileName+"\">" +
				 "	<input type='hidden' class='rid' value='"+rid+"'>" +
				 "	<input type='hidden' class='parentId' value='"+parentId+"'>" +
				 "	<input type='hidden' class='opType' value='"+opType+"'>" +
				 "	<input type='hidden' class='fileType' value='"+fileType+"'>" +
				 "</span>";
	$("#"+id).hide();
	$("#suffixTitle").hide();
	$("#"+id).addClass("origanName");
	$("#"+id).parent().prepend(editor);
	$("#"+id).parent().find('.fileNameInput').select();
}

$('#content-title .updateFolder').die().live('click',function(){
	var span = $(this).parents('span.editFileName');
	var type = $(span).find("input.opType").val();
	var fileName = $.trim($(span).find('input.fileNameInput').val());
	if(!fileName||fileName==''){
		showMsg("文件名不能为空","warning");
		hideMsg(3000);
		return;
	}
	var d = new Object();
	var opType = $(span).find('input.opType').val();
	var fileType = $(span).find('input.fileType').val();
	var foldOriganlName =$(span).find('input.foldOriganlName').val();
	if(fileType=='DFile'){
		fileName = fileName +foldOriganlName.substring(f.lastIndexOf("."));
	}
	d.fileName=fileName;
	d.rid=$(span).find('input.rid').val();
	d.parentRid=$(span).find('input.parentId').val();
	d.func=opType;
	var opUrl="<vwb:Link context='files' format='url'/>";
	$.ajax({
		url:opUrl,
		data : d,
		type : "post",
		dataType:"json",
		success :function(data){
			if(data.result){
				$(span).parent().find(".origanName").text(d.fileName);
				$(".resourcePath").find(".subHint").text(d.fileName);
				$("#suffixTitle").show();
			}else{
				showMsg(data.message,"error");
				hideMsg(3000);
			}
			$("#fileRename").removeClass("clicked");
			$(span).parent().find(".origanName").show();
			$(span).remove();

		},
		statusCode:{
			450:function(){alert('会话已过期,请重新登录');},
			403:function(){alert('您没有权限进行该操作');}
		}
	});
});

//文件名输入回车事件
$("#content-title input.fileNameInput").die().live('keypress',function(e){
	if(e.keyCode==13){
		$('.editFileName .updateFolder').trigger('click');
	}
});

$("#content-title a.cancelFolder").die().live('click',function(){
	$("#fileRename").removeClass("clicked");
	var span = $(this).parents('span.editFileName');
	$(span).parent().find(".origanName").show();
	$(span).remove();
	$("#suffixTitle").show();
});


<%--------- file move/copy/delete end ----------%>
</script>
<script type="text/javascript" src="${contextPath}/scripts/jquery_tree/jquery.jstree.js"></script>

<script type="text/html" id="page-tag-template">
	<li><a href="<vwb:Link context='tag' format='url'/>#&tagId={{= id}}&queryType=tagQuery">{{= title}}</a>
		<a class="delete-tag-link lightDel" tag_id="{{= id}}" rid="{{= item_key}}"></a>
	</li>
</script>


<script id="single-collection-template" type="text/html">
	<li><label><input type="radio" name="cid" value="{{= cid}}"/>{{= title}}</label></li>
</script>

<script id="aone-image-row-template" type="html/text">
<li>
	<a class="image {{= fileExtend}}" href="{{= infoURL}}"><span class="fileIcon {{= fileExtend}}"></span>{{= title}}</a>
	<a class="file ui-RTCorner ui-iconButton download" href="{{= previewURL}}" title="下载">&nbsp;</a>
</li>
</script>


<%--------- file share start ----------%>
<jsp:include page="/jsp/aone/recommend/shareResourceToTeam.jsp"></jsp:include>
<script type="text/javascript">
<!--
$(function(){
	$("#share,#shareSub").click(function(){
		prepareRecommend("<vwb:Link context='recommend' format='url'/>?func=prepareRecommend&itemType=${resource.itemType}&rid=${resource.rid }","${resource.rid }","${resource.title }","${resource.itemType }");
	});
});
//-->
</script>
<%--------- file share end ----------%>

<jsp:include page="/jsp/aone/subscription/addSub.jsp"></jsp:include>
<jsp:include page="/jsp/aone/subscription/deleteSub.jsp"></jsp:include>
<div class="ui-clear"></div>

<script src="${contextPath}/scripts/bootstrap/js/bootstrap.min.js"></script>
