<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<link href="${contextPath}/scripts/bootstrap/css/bootstrap.min.css" rel="stylesheet" media="screen"/>
<link href="${contextPath}/scripts/bootstrap/css/bootstrap-responsive.min.css" rel="stylesheet"/>
<link href="${contextPath}/scripts/bootstrap/css/todc-bootstrap.css" rel="stylesheet"/>
<fmt:setBundle basename="templates.default" />
<script type="text/javascript" src="${contextPath}/scripts/syntaxhighlighter/scripts/shCore.js"></script>
<script type="text/javascript" src="${contextPath}/scripts/syntaxhighlighter/scripts/shAutoloader.js"></script>
<link href="${contextPath}/scripts/syntaxhighlighter/styles/shCore.css" rel="stylesheet" type="text/css"/>
<link id="coreCss" href="${contextPath}/scripts/syntaxhighlighter/styles/shCoreEclipse.css" rel="stylesheet" type="text/css"/>
<link id="themeCss" href="${contextPath}/scripts/syntaxhighlighter/styles/shThemeEclipse.css" rel="stylesheet" type="text/css"/>
<link href="${contextPath}/jsp/aone/css/fileuploader.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/tokenInput.css" type="text/css" />
<script type="text/javascript" src="${contextPath}/jsp/aone/js/toker-jQuery-forTag.js"></script>
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
	$('.uidToolTip').tooltip();
	/* var menu = $('.sideBlock');
	var pos = menu.offset().top;
	var top = 100;
	menu.css('top', top);
	$(window).scroll(function(){
		if ($(document).scrollTop()-pos > -top) {
			menu.css('position', 'fixed');
		}
		else {
			menu.css('position', '');
		}
	}); */

	$('input.tagPoolAutoShow').tokenInput("<vwb:Link context='tag' format='url'/>?func=loadTeamTags&type=nogroup", {
		theme:"facebook",
		hintText: "输入标签名称，以逗号或回车结束",
		searchingText: "正在搜索……",
		noResultsText: "没有该标签，输入逗号或回车添加",
		preventDuplicates: true,
	});
	$(".recoverPageVersion").live("click",function(){
		var version = $(this).find("input[name='version']").val();
		var tid = $(this).find("input[name='tid']").val();
		var aa= window.confirm("您确定要恢复此版本的文本文件吗？");
		if (aa) {
			window.location.href="<vwb:Link page='${resource.rid}' context='file' format='url'/>"+"?func=recoverFileVersion&tid="+tid+"&version="+version+"&rid="+${resource.rid};
		}
	});
//-- ----------------------------- SyntaxHighlighter Config Start  -----------------------------------
	var fileType = "${strFileType}";
	function initPreTag(){
		$('#directShowFile').addClass('brush: '+fileType+';');
	}
	initPreTag();

	function path(){
	  var args = arguments,
	      result = [];

	  for(var i = 0; i < args.length; i++)
	      result.push(args[i].replace('@', '${contextPath}/scripts/syntaxhighlighter/scripts/'));

	  return result;
	};

	SyntaxHighlighter.autoloader.apply(null, path(
		  'xml xhtml xslt html    @shBrushXml.js'

	));
	SyntaxHighlighter.all();
	SyntaxHighlighter.defaults['toolbar']=false;


// ----------------------------- SyntaxHighlighter Config End  -----------------------------------

//***************



      $("#open-upload-form-button").live("click",function(){
    	 ui_showDialog("upload-attach-dialog");
     });

     $("a[name='cancel']").live("click",function(){
     	ui_hideDialog("upload-attach-dialog");
     });

     $("#attach-to-bundle").click(function(){
 		window.location.reload();
     });

     $("#delete-submit").click(function(){

     });

     $("#delete-cancel").click(function(){
    	 ui_hideDialog("delete-attach-dialog");
     });

     $("#open-delete-form-button").click(function(){
    	 ui_showDialog("delete-attach-dialog");
     });

 	$("#open-move-form-button").live("click",function(){
		var url = "<vwb:Link context='configCollection' format='url'/>?func=loadCollectionList";
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

	$('#photoInfo img').bind('contextmenu', function(event){ event.preventDefault(); });

	var fulScrA = $('a#fullScreen');
	var fulScrD = $('div#fullScreenEdge');
	function fullscreen(FULL) {
		var full;
		if (typeof(FULL)!='undefined') {
			full = FULL;
		}
		else {
			full = (fulScrA.attr('isFull')=='true')? false : true;
		}

		if (full) {
			$('body').addClass('fullScreen');
			fulScrA.attr('isFull', 'true').html('退出全屏').attr('title', 'ESC键退出全屏');
			fulScrD.attr('isFull', 'true').hide();
			window.location.hash = '#fullscreen';
			/* for FF4 */
		}
		else {
			$('body').removeClass('fullScreen');
			fulScrA.attr('isFull', 'false').html('全屏阅读').attr('title', 'Alt+Enter键全屏');
			fulScrD.attr('isFull', 'false').show();
			window.location.hash = '';
		}
	}
	$(window).keyup(function(KEY){
		if (KEY.which=='27' && KEY.target.toString().toLowerCase().indexOf('textarea')<0) {
			fullscreen(false);
		}
		if (KEY.which==13 && KEY.altKey) {
			fullscreen();
		}
	});
	$('a#fullScreen, div#fullScreenEdge').click(function(){
		fullscreen($(this).attr('isFull')=='false')
		/* TO ADD: */
		/*	Support for FF4
		 *	Resize when browser size changes */
	});

	var transformUrl="<vwb:Link page='${resource.rid}' context='file' format='url'/>?func=pdfTransform&version=${curVersion.version}";
	$('.pdfTransform').click(function(){
		ajaxRequest(transformUrl,"", function(){
			window.alert("文件已在后台转换，稍等几分钟后刷新即可进行在线浏览！");
		});
	});

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


  	var _tagCache = null;
  	var tag_handler = {
  			addTagForSingleRecord:function(){
  				var tagURL = "<vwb:Link context='tag' format='url'/>";
  				//var params = {"func":"batchAdd","newTags[]":aTBox.log.create,"existTags[]":aTBox.log.add,"rids[]":aTBox.log.rid};
  				function getExistTags(){
  					var results = [];
  					$(".existTags ul.tagList").children("li").each(function(){
  						results.push($(this).attr("tag_id"));
  					});
  					return results;
  				}
  				var params = {"func":"batchAdd","newTags[]":getNewTags(),"existTags[]":getExistTags(),"rids[]":aTBox.log.rid};
  				ajaxRequestWithErrorHandler(tagURL,params,function(data){
  					$("li.newTag").before($("#page-tag-template").tmpl(data));
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

	$('li.newTag').click(function(){
		aTBox.prepare({ ul: $(this).parent(), ridArr: [$(this).attr('rid')] });
		addSingleTagDialog.show();
		$("#token-input-").focus();
	});
	$("#addSingleTagDialog .saveThis").click(function(){
		var feedback = tag_handler.addTagForSingleRecord();
	});

	$('#resAction-tag a').click(function(){
		aTBox.prepare({ ridArr: selector.getItem() });
		addSingleTagDialog.$dialog.addClass('multiple');
		addSingleTagDialog.show();
	});


	$('li.newTag')
	.unbind('.lynx.callAddSingleTagDialog')
	.bind('click.lynx.callAddSingleTagDialog', function(event){
		event.stopPropagation();
		aTBox.prepare({ ul: $(this).parent().parent() });
		addSingleTagDialog.$dialog.removeClass('multiple');
		addSingleTagDialog.show();
	});


/*---------------add tag dialog end---------------*/

	$('.icon-checkStar').each(function(){
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

});

//重命名
$("#fileRename").live('click',function(){
	if(!$(this).hasClass("clicked")){
		$(this).addClass("clicked");
		editorFolderName("pageTitle","editFileName","DFile");
	}
});

function editorFolderName(id,opType,fileType){
	var fileName =$("#"+id).text();
	var orFileName =$("#"+id).text();
	var divFloder=$("#"+id).parent();
	if(fileType=='DFile'){
		var fix =  fileName.substring(fileName.lastIndexOf(".")+1);
		if(fix.toLowerCase()=='dsf'){
			var f =fileName.substring(0,fileName.lastIndexOf("."));
			if(f.lastIndexOf(".")>-1){
				fileName = f.substring(0,f.lastIndexOf("."));
			}else{
				fileName=fileName.substring(0,fileName.lastIndexOf("."));
			}
		}else{
			fileName=fileName.substring(0,fileName.lastIndexOf("."));
		}
	}
	var rid=$("#"+id).attr("rid");
	var parentId=$("#"+id).attr("parentId");
	var editor = "<span class='editFileName' id='editorDiv'>" +
				 " 	<input class='fileNameInput' type='text' value=\""+fileName+"\" style='margin-bottom:0' length='250'>" +
				 "	<a class='btn btn-small btn-primary updateFolder'><i class='icon-ok icon-white'></i></a>" +
				 "	<a class='btn btn-small cancelFolder'><i class='icon-remove'></i></a>"+
				 "	<input type='hidden' class='foldOriganlName' value=\""+orFileName+"\">" +
				 "	<input type='hidden' class='rid' value='"+rid+"'>" +
				 "	<input type='hidden' class='parentId' value='"+parentId+"'>" +
				 "	<input type='hidden' class='opType' value='"+opType+"'>" +
				 "	<input type='hidden' class='fileType' value='"+fileType+"'>" +
				 "</span>";
	$("#"+id).hide();
	$("#"+id).addClass("origanName");
	$("#"+id).after(editor);
	$("#"+id).parent().find('.fileNameInput').select();
}

$('#editorDiv .updateFolder').die().live('click',function(){
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
		var sub = foldOriganlName.substring(foldOriganlName.lastIndexOf(".")+1);
		if(sub.toLowerCase()=='dsf'){
			var last = foldOriganlName.substring(0,foldOriganlName.lastIndexOf(".")).lastIndexOf(".");
			if(last>-1){
				fileName = fileName+foldOriganlName.substring(last);
			}else{
				fileName +=".dsf";
			}
		}else{
			fileName = fileName +foldOriganlName.substring(foldOriganlName.lastIndexOf("."));
		}
	}
	if(fileName.length>200){
		showMsg("文件名称不能超过200个字符","warning");
		hideMsg(3000);
		return false;
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
				$("#fileInfo .fileName").text(d.fileName);
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
$("#editorDiv input.fileNameInput").die().live('keypress',function(e){
	if(e.keyCode==13){
		$('.editFileName .updateFolder').trigger('click');
	}
});

$("#editorDiv a.cancelFolder").die().live('click',function(){
	$("#fileRename").removeClass("clicked");
	var span = $(this).parents('span.editFileName');
	$(span).parent().find(".origanName").show();
	$(span).remove();
});

$("#showHotCode").live('click',function(){
	$("#visitor").toggle();
});


</script>

<div id="opareteFileMessage"  class="alert alert-success" style="margin:8px;display: none;"> </div>
<div id="content-major">
	<div id="content-title">
		<input type="hidden" value="${uid }" id="currentUid"/>
		<c:choose>
			<c:when test="${fileExtend eq 'FILE' or fileExtend eq 'TEXT'}">
				<h1 class="fileName">
					<div class="title-right" style="width:190px">
						<a id="showHotCode" class="hotCode smallBtn" rel="popover" data-placement="top" >热度  <span class="count"><vwb:VisitCount rid="${rid }" /></span> <span id="hotCode"></span></a>
						<a class="versionCode smallBtn" href="<vwb:Link context='infoFile' format='url' page='${rid}'/>">版本  <span class="count">${curVersion.version}</span></a>
					</div>
					<div class="title-left" style="margin-right:190px">
						<span id="pageTitle" rid="${rid }" parentId="${resource.bid}">${resource.title}</span>
					</div>
				</h1>
			</c:when>
			<c:otherwise>
				<h1 class="fileName">
					<div class="title-right" style="width:190px">
						<a id="showHotCode" class="hotCode smallBtn" rel="popover" data-placement="top" >热度  <span class="count"><vwb:VisitCount rid="${rid }" /></span> <span id="hotCode"></span></a>
						<a class="versionCode smallBtn" href="<vwb:Link context='infoFile' format='url' page='${rid}'/>">版本  <span class="count">${curVersion.version}</span></a>
					</div>
					<div class="title-left" style="margin-right:190px">
						<span id="pageTitle" rid="${rid }" parentId="${resource.bid}"><c:out value="${resource.title}"/></span>
					</div>
				</h1>
			</c:otherwise>
		</c:choose>
		<jsp:include page="/jsp/aone/browse/pageReadLog.jsp"></jsp:include>
		<div class="clear"></div>
		<c:choose>
			<c:when test="${starmark}">
		<div class="iconLynxTag icon-checkStar withTag checked" style="margin-top:-5px" rid="${resource.rid }" alt="星标" title="星标">&nbsp;</div>
			</c:when>
			<c:otherwise>
		<div class="iconLynxTag icon-checkStar withTag" style="margin-top:-5px" rid="${resource.rid }" alt="星标" title="星标">&nbsp;</div>
			</c:otherwise>
		</c:choose>
		<div class="content-resTag" style="margin-top:-5px; float:left; width:85%">
			<ul class="tagList">
				<c:forEach items="${resource.tagMap}" var="tagItem">
					<li tag_id='${tagItem.key}'><a href="<vwb:URLGenerator pattern='files'/>#tagId=${tagItem.key}&queryType=tagQuery">${tagItem.value}</a>
						<a class="delete-tag-link lightDel" tag_id='${tagItem.key}' rid="${resource.rid}"></a>
					</li>
				</c:forEach>
				<li class="newTag" rid="${resource.rid}"><a>+</a></li>
			</ul>
		</div>
		<div class="ui-clear"></div>

		<%-- 是否是可以预览的文件 --%>
		<c:set var="isPreview"  value="${pdfstatus == 'success' || pdfstatus == 'original_pdf'||pdfstatus=='convert_success_and_has_more'}" />

		<div id="version">
		<a href="<vwb:Link context='userguide'  format='url'/>?func=redirect&uid=${curVersion.editor}" class="uidToolTip" rel="tooltip" data-placement="bottom" data-original-title="${curVersion.editor}" target="_blank">${editorName}</a> |上传于 <fmt:formatDate value="${curVersion.editTime}" type="both" dateStyle="medium" />
		|&nbsp;<vwb:fileSize size="${curVersion.size }"/>
		<c:if test="${latestVersion!=curVersion.version }">
			<span class="recoverPageVersion">
				<input type="hidden" name="pid" value="${pid }">
				<input type="hidden" name="version" value="${curVersion.version }">
				<input type="hidden" name="tid" value="${curVersion.tid }">
				| <a>恢复版本</a>
			</span>
		</c:if>
		<c:if test="${fileExtend != 'TEXT' && fileExtend != 'FILE'}">
			|&nbsp;<a target="_blank" href="<vwb:Link context='originalImage' page='${resource.rid }' format='url'/>?version=${curVersion.version}">查看原图</a>
		</c:if>
		<c:if test="${!empty copyLog}">
			<br>
			${ copyLog.userName} 从 团队 [${copyLog.fromTeamName}]复制了页面 [${copyLog.rTitle }] 版本：${copyLog.fromVersion}
		</c:if>
                <!-- TODO: disabled temporarily -->
		<!-- <c:if test="${officeSupported == 'true' && pdfstatus != 'original_pdf'}">
		     |&nbsp; <a class="btn btn-mini btn-primary" style="color:#fff;border:none;" target="_blank" href="<vwb:Link context='teamHome'  format='url'/>/preview/${curVersion.rid}?version=${curVersion.version }&redirect=redirect&from=web">Office 预览</a>

		     </c:if> -->
	</div>
	</div>

	<div class="ui-clear"></div>
	<c:choose>
		<c:when test="${fileExtend eq 'FILE'}">
			<div id="fileInfo">
				<table class="fileContainer" style="border:none;">
					<tr>
						<c:if test="${!isPreview }">
							<th><div class="fileIcon <vwb:FileExtend  fileName='${curVersion.title}'/>"></div></th>
						</c:if>
						<td>

							<!-- <p class="fileNote"></p> -->
							<div class="largeButtonHolder">
								<c:if test="${!isPreview }">
									<p class="fileName"><c:out value="${curVersion.title}"/></p>
								</c:if>
								<c:choose>
									<c:when test="${pdfstatus == 'converting' && enableDConvert}">
										<div class="progress progress-striped active" style="margin:15px 0 0;width:350px; height:20px; border:none">
	 										<div class="bar" style="width:100%;"></div>
										</div>
										<p>请稍候，文件正在进行转换...</p>
									</c:when>
									<c:when test="${isPreview }">
										<c:if test="${pdfstatus == 'convert_success_and_has_more'}">
											<p style="margin:-5px 0 5px">Office系列文件只能预览100页，该文件超过100页，请<a href="${downloadURL}">下载查看全文</a></p>
										</c:if>
										<div id="viewerWrapper" style="z-index:100;width:800px; height:600px;">

										</div>
									</c:when>
									<c:when test="${pdfstatus == 'fail' && enableDConvert}">
										<vwb:CLBCanUse>
										<p>该文件在上次PDF转换过程中转换失败！如仍需要在线浏览请进行格式转换！</p>
										<a class="largeButton extra pdfTransform">格式转换</a>
										</vwb:CLBCanUse>
									</c:when>
									<c:when test="${pdfstatus == 'source_not_found' && enableDConvert}">
										<p>PDF转换时未找到原文件，无法预览！</p>
									</c:when>
									<c:when test="${pdfstatus == 'encrypted_source_file'}">
										<p>文件被加密无法预览，请下载后查看！</p>
										<a href="${downloadURL}" class="largeButton extra">下载<span class="ui-text-note">(${sizeShort})</span></a>
									</c:when>
									<c:when test="${pdfstatus == 'corrupt_source_file'}">
										<p>文件已经损坏，请下载后查看！</p>
										<a href="${downloadURL}" class="largeButton extra">下载<span class="ui-text-note">(${sizeShort})</span></a>
									</c:when>
									<c:when test="${strFileType eq 'img'}">
										<!-- 剔除图片的不支持转换信息 -->
									</c:when>
									<c:otherwise>
										<c:if test="${enableDConvert }">
										<c:choose>
											<c:when test="${supported}">
												<a class="largeButton extra pdfTransform">格式转换</a>
											</c:when>
											<c:otherwise>
												<p>暂不支持该文件类型的在线显示</p>
												<a href="${downloadURL}" class="largeButton extra">下载<span class="ui-text-note">(${sizeShort})</span></a>
											</c:otherwise>
										</c:choose>
										</c:if>
									</c:otherwise>
								</c:choose>
							</div>
						</td>
					</tr>
				</table>
			</div>
		</c:when>
		<c:when test="${fileExtend eq 'TEXT'}">
			<div id="codeMode">
				<pre id="directShowFile"><vwb:DirectShowFile rid="${resource.rid }" version="${curVersion.version }"/></pre>
			</div>
		</c:when>
		<c:otherwise>
			<div id="photoInfo">
				<div class="photoContainer">
					<a target="_blank" href="<vwb:Link context='originalImage' page='${resource.rid }' format='url'/>?version=${curVersion.version}">
						<img src="${downloadURL}" />
					</a>
				</div>
			</div>
		</c:otherwise>
	</c:choose>

	<hr />
	<div id="comment">
		<c:set var="itemId" value="${resource.rid}" scope="request"/>
		<c:set var="itemType" value="DFile" scope="request"/>
		<jsp:include page="/jsp/aone/comment/displayComment.jsp"></jsp:include>
	</div>
	<div class="bedrock"></div>
</div>

<div id="content-side">
	<div class="sideBlock">
	    <div class="sideCenter">
    		<a class="btn btn-large btn-success" href="${downloadURL}${fileExtend == 'IMAGE' ? '&imageType=original' : ''}"><i class="icon-download-alt icon-white"></i> 下载 <span style="font-size:12px;">(${sizeShort})</span></a>
    	    </div>

        <!-- Disabled <2022-03-15 Tue> -->
    	<!-- <div class="sideCenter">
	     <a id="shareFile" href="javascript:void(0);" class="btn btn-large"><i class="icon-share"></i> 团队内分享</a>
	     </div>
    	     <div class="sideCenter">
	     <a id="shareResourceCode" href="javascript:void(0);" class="btn btn-large"><i class="icon-share"></i> 公开链接</a>
	     </div> -->

    	<div class="sideCenter">
    		<vwb:CLBCanUse />
    		<div id="file-uploader-file" <c:if test='${!clbCanUse }'>style="display:none;"</c:if>>
				<div class="qq-uploader">
					<a class="btn btn-large upLoadFile"  name="uploadFile" href="javascript:void(0);" data-toggle="modal"  ><i class="icon-refresh"></i> 上传新版本</a>
				</div>
			</div>
			<!-- <a class="btn btn-large upLoadFile" id="upLoadFile" name="uploadFile" href="javascript:void(0);" data-toggle="modal"  ><i class="icon-refresh"></i> 上传新版本</a> -->
		</div>
		<h4 class="sideTitle"><span>文件操作</span></h4>
    	<div class="sideOper"><a id="fileMove" href="javascript:void(0);"><i class="icon-move"></i> 移动</a></div>
    	<div class="sideOper"><a id="fileCopy" href="javascript:void(0);"><i class="icon-copy"></i> 复制</a></div>
    	<div class="sideOper"><a id="fileRename" href="javascript:void(0);"><i class="icon-edit"></i> 重命名</a></div>
    	<div class="sideOper"><a id="fileDelete" href="javascript:void(0);"><i class="icon-trash"></i> 删除</a></div>
		<h4 class="sideTitle"><span>相关文件</span></h4>
		<ul id="exist-attach-list" class="fileList">
			<c:forEach items="${refView}" var="item">
				<c:set var="fileFullname" value="${item.pageName}${item.dfileRef.itemType == 'DPage'? '.ddoc' : ''}" />
				<li>
					<span class="${item.dfileRef.itemType} headImg  ${item.dfileRef.fileType}"></span><a class="file ellipsis" rid="${item.dfileRef.fileRid}" href="${teamHome}/r/${item.dfileRef.fileRid}" title='${fileFullname }'><c:out value="${fileFullname }"/></a>
					<c:if test="${item.dfileRef.itemType == 'DFile'}">
						<a style="float:left;margin-left:5px;" href="<vwb:Link context='download' format='url'/>${item.dfileRef.fileRid}?type=doc&imageType=original"> <i class="icon-download-alt"></i></a>
					</c:if>
					<a class="lightDel" rid="${item.dfileRef.fileRid}"> </a>
				</li>
			</c:forEach>
		</ul>
		<div class="sideOper">
				<a id="attachFile-button"><i class="icon-plus"></i> 添加相关文件</a>
		</div>
	</div>
</div>
<div class="ui-clear"></div>

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

<div id="pipeShowTable" class="popupUpload" style="display:none">
	<div class="popupTitle">
		<p class="uploadTitle">文件复制</p>
		<span><i class="fillUploadPagCal icon-remove icon-white"></i></span>
		<span><i class="icon-minus icon-white"></i></span>
	</div>
	<div class="popupContent">
	</div>
</div>

<c:out value=""></c:out>
<c:set var="fileBarItemId" value="${resource.rid}" scope="request"></c:set>
<c:set var='deleteItemURL' scope='request' value=''></c:set>
<c:set var="fileBarBid" value="0" scope="request"></c:set>
<%-- <jsp:include page="/jsp/aone/tag/lynxFileBar.jsp"></jsp:include> --%>

<script id="single-collection-template" type="text/html">
	<li><label><input type="radio" name="cid" value="{{= cid}}"/>{{= title}}</label></li>
</script>

<script type="text/html" id="page-tag-template">
	<li tag_id="{{= id}}"><a href="<vwb:Link context='tag' format='url'/>#&tag={{= id}}">
						{{= title}} </a>
					<a class="delete-tag-link lightDel" tag_id="{{= id}}" rid="{{= item_key}}"></a>
				</li>
</script>

<div id="popupUpload" class="popupUpload" style="display:none">
		<div class="popupTitle">上传文件
			<span><i class="icon-remove icon-white"></i></span>
			<span><i class="icon-minus icon-white"></i></span>
		</div>
		<div id="fileListDiv" class="popupContent">
			<ul id="upload-list" style="list-style:none"></ul>
		</div>
</div>
<div id="alertModel" class="modal hide fade">
  <div class="modal-header">
    <button type="button" class="close closeUpload" data-dismiss="modal" aria-hidden="true">&times;</button>
    <h3>提示</h3>
  </div>
  <div class="modal-body">
	<p class="alertContent">列表中有未上传完成的文件，确定要放弃上传吗？</p>
  </div>
  <div class="modal-footer">
  	<a href="#" class="btn btn-primary" id="okAlertContent">确认</a>
    <a href="#" class="btn closeAlertModel"  data-dismiss="modal" aria-hidden="true">取消</a>
  </div>
</div>


<div id="fileBrowserModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="fileBrowserModalLabel" aria-hidden="true">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3 id="fileBrowserModalLabel">移动到</h3>
	</div>
	<div class="modal-body" style="height: 345px;">
		<div id="teamSelWrapper" style="display:none;">
			<select id="teamSel">
			    <!-- <option value="pan" id="teamSel_pan" >个人空间同步版</option> -->
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
				"title":"",
				"bid": "${resource.bid}"
		};
		$.ajax({
		   type: "POST",
		   url: "${teamHome}/upload?func=referFiles",
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
		   error:function(xhr){
			   if(xhr.status==403){
				   showMsg("对不起，您没有权限执行此操作！","error");
				   hideMsg(5000);
			   }
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
		   error:function(xhr){
			   if(xhr.status==403){
				   showMsg("对不起，您没有权限执行此操作！","error");
				   hideMsg(5000);
			   }
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

	if('${pdfstatus}' == 'converting'){
		window.setInterval(function(){
			$.ajax({
				   type: "POST",
				   url: "${teamHome}/r/${rid}",
				   cache:false,
				   data: {
						'func' : 'getPdfStatus'
					},
					dataType:"json",
				   	success: function(data){
					   if(data.pdfstatus != 'converting'){
						   window.location.reload();
					   }
				   }
				});
		},3000);
	}
});
</script>

<script type="text/javascript">


<%--------- file upload begin ----------%>
$(function(){
	var upload_url = "<vwb:Link context='upload' format='url'/>?func=updateFile";
	var uploadedFiles = [];
	var index = 0;

	function createUploader(){
		qq.extend(qq.FileUploader.prototype,{
			_addToList: function(id, fileName){
		        var item = qq.toElement(this._options.fileTemplate);
		        item.qqFileId = id;

		        var fileElement = this._find(item, 'file');
		        qq.setText(fileElement, this._formatFileName(fileName));
		        this._find(item, 'size').style.display = 'none';

		        this._listElement.appendChild(item);
		        $("#popupUpload").show();
	        	//$("#fileListDiv").slideUp(5000);
		        $("#fileListDiv").show();
	        	//$("#fileListDiv").fadeIn(1000);
	        	//$("#fileListDiv").fadeOut(5000);
		    },
		    _onComplete: function(id, fileName, result){
		        qq.FileUploaderBasic.prototype._onComplete.apply(this, arguments);

		        // mark completed
		        var item = this._getItemByFileId(id);
		        qq.remove(this._find(item, 'cancel'));
		        qq.remove(this._find(item, 'spinner'));

		        if (result.success){
		            qq.addClass(item, this._classes.success);

		            showMsg("上传新版本成功！");
		            window.setTimeout(function(){
						window.location.reload();
					}, 1500);

		        } else {
		            qq.addClass(item, this._classes.fail);
		        }
		        if(this._filesInProgress==0){
		      	  hideFilesList();
		        }
		    },
		    _CancelAll: function(){
		        var self = this,
		        list = this._listElement;
		        $(list).find("a.qq-upload-cancel").each(function(){
		        		   var item = this.parentNode;
			                self._handler.cancel(item.qqFileId);
			                qq.remove(item);
		        } );
		     /*    qq.attach(list, 'click', function(e){
		            e = e || window.event;
		            var target = e.target || e.srcElement;
		            alert("ssssdddd");
		            if (qq.hasClass(target, self._classes.cancel)){
		                qq.preventDefault(e);

		                var item = target.parentNode;
		                self._handler.cancel(item.qqFileId);
		                qq.remove(item);
		            }
		        });  */
		    }
		});
	     var uploader = new qq.FileUploader({
	         element: document.getElementById('file-uploader-file'),
	         template: '<div class="qq-uploader">' +
	         '<div class="qq-upload-drop-area"><span>Drop files here to upload</span></div>' +
	         '<div class="qq-upload-button"><i class="icon-refresh"></i> 上传新版本</div><br/>'+ '</div>',
	         listElement: document.getElementById("upload-list"),
	         fileTemplate: '<li>' +
	         '<span class="qq-upload-file"></span>' +
	         '<span class="qq-upload-spinner"></span>' +
	         '<span class="qq-upload-size"></span>' +
	         '<a class="qq-upload-cancel" href="#">取消</a>' +
	         '<span class="qq-upload-failed-text">失败,您没有上传权限</span>' +
	    	 '</li>',
	         action: upload_url,
	         statisticAction: site.getTeamURL("statistics/upload"),
	         params:{cid:"${cid}",rid:"${curVersion.rid}",version:"${curVersion.version}"},
	         onComplete:function(id, fileName, data){
	         	uploadedFiles[index] = data;
	         	index ++;
	         },
	         showMessage: function(message){
	        	 showMsgAndAutoHide(message,'error',5000);
	         },
	         debug: true,
	         multiple: false
	     });

	    return uploader;
	 };
	var uploader=createUploader();
	$("input[type=file]").attr("title","上传文件");

	$(".upLoadFile").click("click",function(){
		$("div.qq-upload-button [type=file]").trigger("click");
	 });


	function hideFilesList(){
		 $("#fileListDiv").slideUp(2000);
	 	 var obj=$("i.icon-minus");
	 	 obj.removeClass("icon-minus");
	 	 obj.addClass(" icon-resize-full");
	}

	function showFilesList(){
		 $("#fileListDiv").show();
	 	 var obj=$("i.icon-resize-full");
	 	 obj.removeClass("icon-resize-full");
	 	 obj.addClass("icon-minus");
	}

	$("i.icon-resize-full").live("click",function(){
		 showFilesList();
	});

	$("i.icon-minus").live("click",function(){
		 hideFilesList();
	});

	$("i.icon-remove").live("click",function(){
		  if(uploader._filesInProgress>0){
			  $("#alertModel").modal("show");
			  $("#okAlertContent").addClass("cancleAllOk");
	     	  return;
	       }
		  uploader._filesInProgress=0;
	     $("#upload-list").html("");
	     $("#popupUpload").hide();
	});

	$(".closeUpload").live("click",function(){
		  $("#alertModel").modal("hide");
	});

	$("#okAlertContent").live("click",function(){
		  	$("#alertModel").modal("hide");
	});

	$(".cancleAllOk").live("click",function(){
		 uploader._CancelAll();
		  uploader._filesInProgress=0;
	     $("#upload-list").html("");
	     $("#popupUpload").hide();
	});

});
<%--------- file upload end----------%>

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
function showMsgAndAutoHide(msg, type,time){
	time=time||2000;
	showMsg(msg,type);
	hideMsg(time);
}



<%--------- file move/copy/delete start ----------%>
var original_rid = -1;
var file_operation = 'none';
$("#fileMove").live("click",function(){
	$('#fileBrowserModalLabel').empty();
	$('#fileBrowserModalLabel').append("移动到");
	$("#fileBrowserModal").modal();
	original_rid = "${resource.rid}";
	file_operation = 'move';

	$("#teamSel_pan").hide();
});
$("#fileCopy").live("click", function(){
	$('#fileBrowserModalLabel').empty();
	$('#fileBrowserModalLabel').append("复制到");
	$("#fileBrowserModal").modal();
	original_rid = "${resource.rid}";
	file_operation = 'copy';

	$("#teamSelWrapper").show();
	$("#teamSel").val("${teamCode}");

	$("#teamSel_pan").show();
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
			alert("文件夹名不能为空.");
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

		if(file_operation!='move'){
			var teamCode = $("#teamSel").val();
			if(teamCode=='pan'){
				opUrl = "${contextPath}/" + teamCode + "/list";
				alert(opUrl);
			};
		}
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

				   		if(data.type=='meepoCopy'){
						   $("#opareteFileMessage").hide();
						   if(!data.taskId){
							   showMsgAndAutoHide("复制错误");
							   return;
						   }
						   var d = new PipeCopy({"showTable":"pipeShowTable","taskId":data.taskId,"url":file_manager_url,
							   "closeCallback":function(finished){
								   if(!finished){showMsgAndAutoHide('系统将会自动帮您完成复制.','block',5000); }
								},
						   });
						   d.showCopyStatus(data);
						   d.getCoyeStatus();
						   return;
					   }

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
			};
});

//删除
$("#fileDelete").live('click',function(){
	var fileName =$("#pageTitle").text();
	if(confirm("确定要将“"+fileName+"”删除吗？")){
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
<%--------- file move/copy/delete end ----------%>

</script>
<script type="text/javascript" src="${contextPath}/scripts/jquery_tree/jquery.jstree.js"></script>


<jsp:include page="/jsp/aone/recommend/shareResourceToTeam.jsp"></jsp:include>
<script type="text/javascript">
<!--
function checkHtml5(){
	return (typeof(Worker) !== "undefined") ? true : false;
}
var isIE9=function(){
	return navigator.userAgent.indexOf("MSIE 9.0")>0;
}

$(function(){

	<%--------- file share start ----------%>
	$("#shareFile").click(function(){
		prepareRecommend("<vwb:Link context='recommend' format='url'/>?func=prepareRecommend&itemType=${resource.itemType}&rid=${resource.rid }","${resource.rid }","${resource.title }","${resource.itemType }");
	});
	<%--------- file share end ----------%>
	//share resouce code start
	$("#shareResourceCode").click(function(){


		prepareShareResource("<vwb:Link context='team' format='url'/>/shareResource","${resource.rid}","${resource.title}","opareteFileMessage");
	});


	//share resource code end


	if("${isPreview}" == "true"){
		if(checkHtml5()||isIE9()){
			$("#viewerWrapper").append("<iframe src=\"<vwb:Link page='${resource.rid}' context='f' format='url'/>?func=onlineViewer&version=${curVersion.version}\"" +
											" height=\"100%\" width=\"100%\" scrolling=\"no\" ></iframe>");
		}else{
			$("#fileInfo").empty();
			$("#fileInfo").append("<table class=\"fileContainer\" style=\"border:none;\"><tbody><tr>" +
				"<th><div class=\"fileIcon <vwb:FileExtend  fileName='${curVersion.title}'/>\"></div></th>" +
				"<td><p class=\"fileNote\"></p><div class=\"largeButtonHolder\"><p class=\"fileName\">${curVersion.title}</p><p>浏览器不支持该文件类型的在线显示</p>" +
					 "<a href=\"${downloadURL}\" class=\"largeButton extra\">下载<span class=\"ui-text-note\">(${sizeShort})</span></a></div></td>" +
			    "</tr></tbody></table>"
			);
		}
	}

});


//-->
</script>
<script src="${contextPath}/scripts/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${contextPath}/scripts/app-ui/pipe_copy.js?v=${aoneVersion}"></script>
