<%@page language="java" pageEncoding="UTF-8"%>
<%@taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<fmt:setBundle basename="templates.default" />
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	response.setHeader("Cache-Control","no-store");
	response.setHeader("Pragrma","no-cache");
	response.setDateHeader("Expires",0);
%>

<script type="text/javascript" src="${contextPath}/jsp/aone/js/fileuploader.js?v=${aoneVersion}"></script>
<script type="text/javascript" src="${contextPath}/scripts/jquery/jquery.ui.autocomplete.js"></script>
<link href="${contextPath}/scripts/jquery/jquery.ui.theme.css" rel="stylesheet" type="text/css">	
<link href="${contextPath}/jsp/aone/css/fileuploader.css" rel="stylesheet" type="text/css">	
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/tokenInput.css" type="text/css" />
<link href="${contextPath}/scripts/bootstrap/css/bootstrap.min.css" rel="stylesheet" media="screen"/>
<link href="${contextPath}/scripts/bootstrap/css/todc-bootstrap.css" rel="stylesheet"/>
<meta HTTP-EQUIV="pragma" CONTENT="no-cache">
<meta HTTP-EQUIV="Cache-Control" CONTENT="no-cache, must-revalidate">
<meta HTTP-EQUIV="expires" CONTENT="0">
<style>

</style>
<script type="text/javascript" src="${contextPath}/jsp/aone/js/toker-jQuery-forTag.js"></script>
<script type="text/javascript">
var EditorModal = {
		imageModal : null,
		resourceModal : null,
		getResourceModal : function(){
				var that = this;
				if(that.resourceModal == null){
					that.resourceModal = ResourceModal.getInstance("选择要插入的文件");
					that.resourceModal.clickOk=function(rids){
						var span = document.createElement("span");
						$.each(rids,function(i,n){
							var a = document.createElement("a");
							a.setAttribute("href", "<vwb:Link context='f' format='url' absolute='true' />"+n);
							a.setAttribute("target", "_blank");
							a.setAttribute("rid", n);
							a.innerHTML = that.resourceModal.getRecordById(n).fileName + " ";
							span.appendChild(a);
						});
						E2APIinsertNode(span);
						that.resourceModal.close();
					};
					that.resourceModal.hideCallback = function(){
						FCKeditorAPI.GetInstance('htmlPageText').Selection.Release();
					};
				}
				return that.resourceModal;
		},
		getImageModal : function(){
			var that = this;
			if(that.imageModal == null){
				that.imageModal = ImageModal.getInstance();
				that.imageModal.clickOk=function(rids){
					var span = document.createElement("span");
					$.each(rids,function(i,n){
						var a = document.createElement("a");
						a.setAttribute("href", "<vwb:Link context='f' format='url' absolute='true' />"+n);
						a.setAttribute('target', '_blank');
						a.setAttribute('rid', n);
						var img = document.createElement("img");
						img.setAttribute("src", "<vwb:Link context='download' format='url' />"+n);
						img.setAttribute("title", "");
						a.appendChild(img);
						var p = document.createElement("p");
						p.appendChild(a);
						span.appendChild(p);
					});
					E2APIinsertNode(span);
					that.imageModal.close();
				};
				that.imageModal.hideCallback = function(){
					FCKeditorAPI.GetInstance('htmlPageText').Selection.Release();
				};
			}
			return that.imageModal;
		}
	};


function insertHTML(){
	var oEditor = FCKeditorAPI.GetInstance('htmlPageText') ;
	oEditor.InsertHtml( ' ' ) ;
}

var colsePageFlage={
	flage : false,
	standardClose:function(){
		colsePageFlage.flage=true;
	}
};

 function addToRightList(vwbClbId,docId,infoURL,title,previewURL,type){
     	var obj = new Object();
     	obj.fid = vwbClbId;
     	obj.clbId = docId; // CLB ID
     	obj.infoURL = infoURL; //http://www.aone.com/dct/cerc/file/12 （12为vwb_clb自增id）
     	obj.title = title; //文件名称
     	obj.previewURL = previewURL; // 文件下载URL
     	obj.type = type; //IMAGE or FILE
    	if(obj.type=='IMAGE'){
	     	 $("#aone-image-row-template").tmpl(obj).appendTo("#edit-attach-list");
   	 	 }else{
   	 	 	$("#aone-file-row-template").tmpl(obj).appendTo("#edit-attach-list");
   	 	 }
     };


var twinkle = { //
	interval:	500,
	base:	document.title,
	msg:	'【编辑超时】',
	flash:	'【　　　　】',
	count:	0,
	timer: null,
	isOn:	false,
	timeout :0,
	beginTime :0,
	
	change: function() {
		if (twinkle.count %2 == 0) {
			document.title = twinkle.msg + twinkle.base;
		}
		else {
			document.title = twinkle.flash + twinkle.base;
			$('#locker #countdown').text(twinkle.getTimeout());
			closeEditePage();
		}
		twinkle.count++;
		motionDetector.editorTrigger();
	},
	getTimeout : function(){
		var now = new Date().getTime();
		var result = twinkle.timeout- Math.floor((now -twinkle.beginTime)/1000);
		return result;
		
	},
	start: function() {
		if (!twinkle.isOn) {
			twinkle.timer = setInterval(twinkle.change, twinkle.interval);
			twinkle.beginTime= new Date().getTime();
			twinkle.timeout = motionDetector.leftTime;
			twinkle.isOn = true;
		}
	},
	
	stop: function() {
		if(twinkle.timer!=null){
			clearInterval(twinkle.timer);
			twinkle.timer = null;
		}
		document.title = twinkle.base;
		twinkle.isOn = false;
	}
};


//查询页面锁是否过期 <vwb:Link context='edit' page='${pid}' format='url'/>&func=isLockTimeOut
//更新页面锁时间 <vwb:Link context='edit' page='${pid}' format='url'/>&func=updateLockTime
var autosaveInterval = 0.25;
var motionDetector = {
	listener:	false,	//keeps motion swicth
	activeTime:	new Date(),	//keeps motion time, for precise detect
	leftTime:   0,
	warning:	false,	//marks if it's warning
	timeout:	null,
	
	trigger:
		function() {
			if (!motionDetector.warning) {
				motionDetector.activeTime = new Date();
				motionDetector.listener = true;
				motionDetector.hideAlert();
			}
			else {
				motionDetector.refreshLock();
				motionDetector.hideAlert();
			}
		},
	editorTrigger:
		function() {
			var editorTime = getE2UserActiveTime();
			if (editorTime!=null) {
				if (motionDetector.activeTime.getTime() < editorTime.getTime()) {
					// editor action is later
					motionDetector.activeTime = editorTime;
					motionDetector.listener = true;
					motionDetector.hideAlert();
				}
			}
			else {
			}
		},

	isTimeOut:
		function() {
			var result = 'notset';
			$.ajax({
				url: "<vwb:Link context='edit' page='${rid}' format='url'/>&func=isLockTimeOut",
				dataType: "JSON",
				async : false,
				success: function(json) {
					motionDetector.timeout = json.flag;
					if (!motionDetector.timeout){
						motionDetector.leftTime = json.myLeftTime;
					}
					result = json.flag;
				},
				error: function(data) {
					result = 'timeout ajax fail';
				},
				statusCode:{
					450:function(){alert('会话已过期,请重新登录');},
					403:function(){alert('您没有权限进行该操作');}
				}
			});
			return result;
		},
		
	refreshLock:
		function() {
			$.ajax({
				url: "<vwb:Link context='edit' page='${rid}' format='url'/>&func=updateLockTime",
				cache:false
			});
		},
		
	alertMotion:
		function() {
			motionDetector.warning = true;
			twinkle.start();
			$('#locker').slideDown().html('<p>您已经较长时间没有编辑过内容了！在您编辑时，其他用户无法同时编辑此页面！如果您不希望继续编辑本页面，建议保存并退出，以免影响他人编辑。'
				+ '<br/>您现在是要继续<input type="button" id="continueBtn" value="编辑" >？还是<input type="button"  id="messageSaveBtn" value="保存并退出"><br/>'
				+'否则，页面将在 <span id="countdown">'+ twinkle.getTimeout()+'</span> 秒后自动保存并关闭。</p>');
			//alert('您已经很长时间没有编辑过内容了！如果不希望继续编辑，建议保存并退出，以免影响他人编辑。');
		},
	hideAlert:
		function() {
			twinkle.stop();
			if(motionDetector.warning){
				$('#locker').hide();
			}
			motionDetector.warning = false;
		}
	
};

$("#messageSaveBtn").live('click',function(){
	$('input#okbutton').click();
});
$("#continueBtn").live('click',function(){
	motionDetector.trigger();
});

function fLocker(){
	var lockType=document.getElementById("lockType").value;
	if(lockType=='locked'){
		document.getElementById("locker").style.display="block";
	}
}

$(document).ready(function(){
	function isFirstLoad(){
		var firstLoad = $("#firstLoadSign").val();
		if(firstLoad=='true'){
			window.location.reload();
		}else{
			$("#firstLoadSign").val('true');
		}
	}
	isFirstLoad();
	$("#okbutton").live("click",function(){
		submitDEForm(this);
	});
	$('input.tagPoolAutoShow').tokenInput("<vwb:Link context='tag' format='url'/>?func=loadTeamTags&type=nogroup", {
		theme:"facebook",
		hintText: "输入标签名称，以逗号或回车结束",
		searchingText: "正在搜索……",
		noResultsText: "没有该标签，输入逗号或回车添加",
		preventDuplicates: true,
	});
	switchToLightNav('noLocalNav');
	
	var upload_base_url = "<vwb:Link context='upload' format='url'/>";
	var upload_url = "<vwb:Link context='upload' format='url'/>?func=uploadFiles";
	var search_file_url = "<vwb:Link context='upload' format='url'/>?func=searchReferableFiles";
	
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
     
     var editformValidator = $("#editform").validate({
  		rules: {
  			newCollection: {required:"#put-new-collection:checked"},
  			pageTitle: {required:true,DoubleFileName:true}
  		},
  		messages:{
  			newCollection: { required: '请输入集合名称' },
  			pageTitle: {required: '请输入页面标题',DoubleFileName:"当前文件夹下存在重名文件" }
  		},
  		errorPlacement: function(error, element) {  //验证消息放置的地方  
            error.appendTo( element.parent());  
        },
  	 });
     
     
  // 校验重名
     jQuery.validator.addMethod("DoubleFileName", function(value, element) { 
    	 var url='<vwb:Link context="edit" page="${rid}" format="url"/>';
    	 var result=true;
    	 $.ajax({
				url: url,
				dataType: "json",
				type: "POST",
				async:false,
				data: {
					func:"validateFileName",
					fileName:value
				},
				success: function(data){
					if(!data.result){
						result= false;
					}
				},
				statusCode:{
					450:function(){alert('会话已过期,请重新登录');},
					403:function(){alert('您没有权限进行该操作');}
				}
		});
    	 return result;
     }, "当前文件夹下存在重名文件"); 
     
    
	//bind actions
	$('#pagecontent').keydown(motionDetector.trigger);
	
	$('#pagecontent').mousedown(motionDetector.trigger);
	//$('#locker').show().text('monitor');
	
	function split( val ) {
		return val.split( /,\s*/ );
	};
	
	function extractLast( term ) {
		return split( term ).pop();
	};
	
	 $("input[name='useNewCollection']").live("change",function(){
	     	if($(this).attr("checked")){
	     		$("#new-collection-input").show();
	     		$("#old-collection-select").hide();
	     		$("#collectionName").hide();
	     		$("#inputCollectionName").show();
	     		$('#new-collection-input input').focus();
	     	}else{
	     		$("#new-collection-input").hide();
	     		$("#old-collection-select").show();
	     		$("#collectionName").show();
	     		$("#inputCollectionName").hide();
	     	}
	     });
	 
	$("select[name='selectCollection']").val("${cid}");
	
//************************标签管理 ****************************************
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
//关闭浏览器事件
	var UnloadConfirm = {};
	UnloadConfirm.MSG_UNLOAD = "数据尚未保存，离开后可能会导致数据丢失\n";
	UnloadConfirm.set = function(a) {
	    window.onbeforeunload = function(b) {
	        b = b || window.event;
	        if(! colsePageFlage.flage){
		        b.returnValue = a;
		        return a;
	        }
	    };
	};
	UnloadConfirm.clear = function() {
	    fckDraft.delDraftById();
	    window.onbeforeunload = function() {}
	};
	UnloadConfirm.set(UnloadConfirm.MSG_UNLOAD);
	

//自动生成未命名标题
	
	function addKeyUpEvent(){
		var oFCKeditor = FCKeditorAPI.GetInstance('htmlPageText');
		oFCKeditor.Events.AttachEvent("keydown", editor_keyup);
	};
	editor_keyup = function(){
		if(isNewPage()&&notEditTitle()){
			setPageTitle();
		}
	};
	function notEditTitle(){
		return haveEditePage;
	}
	var haveEditePage = false;
	$("#pageTitle").live("keyup",function(){
		if(isNewPage()&&!haveEditePage){
			var pageTitle = $('#pageTitle').val();
			if(pageTitle!=null){
				haveEditePage = true;	
			}
		}		
	});
	function isNewPage(){
		var lastVersion = $("#lastVersion").val();
		if(lastVersion==0){
			return true;
		}else{
			return false;
		}
	}
	function getPageContext(){
		var context = FCKeditorAPI.GetInstance('htmlPageText').GetXHTML('FixDom');
		return context;
	}
	
	function setPageTitle(){
		var oldPageTitle = $('#pageTitle').val();
		var newPageTitle = getTitleFromContext();
		if(oldPageTitle!=newPageTitle){
			$('#pageTitle').val(newPageTitle);
		}
	}
	
	function getTitleFromContext(){
		var context = FCKeditorAPI.GetInstance('htmlPageText').GetXHTML('FixDom');
	}
	
//自动生成标题结束
});//end of document.ready

</script>

	
<script>
//<![CDATA[
	restoreType='${restoreType}';
	TempPageData='${TempPageData}';
//]]>


diyanliang='${restoreType}';

var ajaxRequestURL = '<vwb:Link context="edit" page="${rid}" format="url"/>';
function submitDEForm(obj) {
		var strction=obj.name;
		var domstr=FCKeditorAPI.GetInstance('htmlPageText').GetXHTML('FixDom');
		document.getElementById('fixDomStr').value=domstr;	
		document.editform.func.value=strction;
		if($(obj).attr("name")=="cancel"){
			$("#pageTitle").rules("remove","required DoubleFileName");
		}
		colsePageFlage.standardClose();
		$("#editform").submit();
}


function saveDEeditor(){
	if(!$("#editform").validate().valid()){
		return;
	}
	$.ajax({
		  type: 'POST',
		  url: ajaxRequestURL+'&func=save',
		  data: { 
					htmlPageText: FCKeditorAPI.GetInstance('htmlPageText').GetXHTML('FixDom'),
					ResourceId:  $('#ResourceId').val(),
					useNewCollection:$("#put-new-collection").attr("checked"),
					newCollection:$("input[name='newCollection']").val(),
					selectCollection:$("select[name='selectCollection']").val(),
					pageTitle:$('#pageTitle').val(),
					lockVersion:$('#lockVersion').val()	
			},							
			success:function(response, option){
					var resp=response.split("|");
					document.getElementById('lockVersion').value=resp[1];
					alert(resp[0]);
			} ,
			statusCode:{
				450:function(){alert('会话已过期,请重新登录');},
				403:function(){alert('您没有权限进行该操作');}
			}
	});
}

var restoreType=0;
var later;
var TempPageData="";
function runAutoSavePage(){
	var AutoSaver = window.setInterval("autoSaveDEeditor()", autosaveInterval*60*1000);
	if(restoreType==1){
		later=window.setInterval("doRestore()",500)
	}
}
function doRestore() {
	if(document.readyState=="complete"){
	      window.clearInterval(later);
	}else return
	var com=confirm('plain.overwrite.changed'.localize(TempPageData))
	if(com){
		strTempPage=document.getElementById('inTempPage').value;
		FCKeditorAPI.GetInstance('htmlPageText').SetHTML(strTempPage);
	}else{
	}
}

function closeEditePage(){
	if(twinkle.getTimeout()<=0){
		colsePageFlage.standardClose();
		window.location.href = "<vwb:Link context='f' page='${rid}' format='url'/>"+"?autoClosepageFlage=true";
		//$('input#okbutton').click();
	}
}

function autoSaveDEeditor(){
	if (motionDetector.warning) {
		closeEditePage();
	}else {
		var myDate = new Date();
		motionDetector.editorTrigger();
		if ((autosaveInterval*60*1000 + motionDetector.activeTime.getTime())
				>= myDate.getTime() && motionDetector.listener == true) {
			motionDetector.refreshLock();
		}else {//autosave range has no motion
			timeout = motionDetector.isTimeOut();
			if ( 60 >=motionDetector.leftTime && $("#myspace").val()!='true') {
				motionDetector.alertMotion();
			}
			
		}
		motionDetector.listener = false;
		
		//save content
		$.ajax({
			  type: 'POST',
			  url: ajaxRequestURL+'&func=autosave',
			  dataType:'json',
			  data: { 
						htmlPageText: FCKeditorAPI.GetInstance('htmlPageText').GetXHTML('FixDom'),
						rid:  $('#ResourceId').val(),
						useNewCollection:$("#put-new-collection").attr("checked"),
						newCollection:$("input[name='newCollection']").val(),
						selectCollection:$("select[name='selectCollection']").val(),
						pageTitle:$('#pageTitle').val()
				},							
				success:function(data, option){
					var myDate = new Date();
					var mytime=myDate.toLocaleTimeString();
					document.getElementById("autosaveautosaveinfoarea").innerHTML=data.message+mytime;
					if(!data.pageLock && $("#myspace").val()!='true'){
						$('#unlocker').slideDown().html("<p>会话已结束请<a href='#' id='reloadCurrentPage' style='color: white;text-decoration:underline'>刷新</a>当前页面后再编辑！</p>");
					}
				},
				statusCode:{
					450:function(){alert('会话已过期,请重新登录');},
					403:function(){alert('您没有权限进行该操作');}
				}   
		});
	}
}
$("#reloadCurrentPage").live('click',function(){
	colsePageFlage.standardClose();
	window.location.reload();
});
</script>

<div id="locker">
	<p>
	<fmt:message key="edit.locked">
	    <fmt:param>${locker}</fmt:param>
	    <fmt:param>${lastAccessTime}</fmt:param>
	</fmt:message>
	</p>
</div>
<div id="unlocker">
</div>

<textarea cols="80" rows="4" name="inTempPage" id="inTempPage" readonly="readonly" style="display: none;">
${strTempPage}
</textarea>
<div id="pagecontent">
<input type="hidden" id="firstLoadSign" value="false">
<form id="editform" name="editform" method="post" action="<vwb:Link context='edit' page='${rid}' format='url'/>">
<input type="hidden" name="parentRid" id="bid-field" value="${parentRid }"/>
<input type="hidden" id="lastVersion" name="lastVersion" value="${resource.lastVersion }">
<input type="hidden" name="version" id="version" value="${version}"/>
<input type="hidden" name="lockType" id="lockType" value='${lockType}'>
<input type="hidden" name="lockVersion" id="lockVersion" value='${lockVersion}'>
<input type="hidden" id="myspace" value="${myspace }"/>
	<div id="tab21" class="DCT_tabmenu toolHolder ui-wrap wrapperFull">
		<div id="submitbuttons">
			<input name='saveexit' type='button' id='okbutton'
				value='<fmt:message key="editor.plain.save.submit"/>' />
				
 			<input name='save' type='button' id="savebutton"
				value='<fmt:message key="editor.plain.saveedit.submit"/>'
				onclick="javascript:saveDEeditor();" />

 			<input name='preview' type='button' id="previewbutton"
				value='<fmt:message key="editor.plain.preview.submit"/>'
				onclick="javascript:submitDEForm(this);" />

 			<input name='cancel' type='button' id='cancelbutton'
				value='放弃编辑' title="放弃未保存的内容，并退出"		
				onclick='javascript:submitDEForm(this);' />
			<input name="func" type="hidden" />
		</div>
	</div>
	
	<div id="setTitle" class="content-through">
		<div id="pageTitleDiv">
		<p>
			<fmt:message key="editor.fck.page.title" />
			<c:if test="${not empty version}">
				<span style="color:green">（您当前编辑的版本<b>${version}</b>,最新版本<b>${latestVersion}</b>）</span>
			</c:if>
		</p>
			<input name="pageTitle"  type="text" id="pageTitle"  value="${editDpage.meta.title}" /><span id="autosaveautosaveinfoarea" ></span><span id="autoSaveNothaveLock"></span>
		</div>
		<div id="tags">
			
			<div class="content-resTag">
				
				<ul class="tagList">
				<font style="float:left; margin-right:15px;">标签:</font>
					<c:forEach items="${tagMap }" var="tag" varStatus="statusTag">
						<li tag_id='${tag.key}'><a
							href="<vwb:URLGenerator pattern='files'/>#tagId=${tag.key}&queryType=tagQuery">${tag.value
								}</a> <a class="delete-tag-link lightDel" tag_id='${tag.key}'
							rid="${rid}"></a></li>
					</c:forEach>
					<li class="newTag" rid="${rid}"><a>+</a></li>
				</ul>
			</div>
		</div>
	</div>
	
		<jsp:include page="/jsp/editor.jsp"/>


<!-- 	<div id="tab21" class="DCT_tabmenu toolHolder bottom">
		<div style="float: left" id="submitbuttons">
			<input name='saveexit' type='button'
				value='<fmt:message key="editor.plain.save.submit"/>'
				onclick='javascript:submitDEForm(this);' />

			<input name='save' type='button' 
				value='<fmt:message key="editor.plain.saveedit.submit"/>'
				onclick="javascript:saveDEeditor();" />

 			<input name='preview' type='button' 
				value='<fmt:message key="editor.plain.preview.submit"/>'
				onclick="javascript:submitDEForm(this);" />

			<input name='cancel' type='button' 
				value='<fmt:message key="editor.plain.cancel.submit"/>'
				onclick='javascript:submitDEForm(this);' />

			<input name='cancel' type='button'
				value='放弃编辑' title="放弃未保存的内容，并退出"		
				onclick='javascript:submitDEForm(this);' />			
		</div>
	</div>
 -->
</form>


<div class="ui-dialog" id="upload-attach-dialog" style="width:400px;">
		<p class="ui-dialog-title">上传附件</p>
	
		<div id="file-uploader-demo1">
			<div class="qq-uploader">
				<div class="qq-upload-button">上传附件
					<input type="file" multiple="multiple" name="files" syncmaster="SYNCMASTER"/>
				</div>
				<ul class="qq-upload-list fileList"></ul>
			</div>
		</div>
		
		<div class="ui-dialog-control">
			<input type="button" id="attach-to-page" value="完成"/>
			<a name="cancel">取消</a>
		</div>
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

<jsp:include page="/jsp/aone/resource/resourceModal.jsp"></jsp:include>
<jsp:include page="/jsp/aone/resource/imageModal.jsp"></jsp:include>
</div>


<script id="aone-image-row-template" type="html/text">
<li class="attach-link" id="attach_li_{{= fid}}">
	<a class="lightDel ui-RTCorner" fid="{{= fid}}" name="delete-button"></a>
	<a class="image" href="{{= infoURL}}?func=viewImage">{{= title}}</a>
	<p class="attachAction">
		<input type="button" name="add-photo-button" preview="{{= previewURL}}" fid="{{= clbId}}" value="插入图片"/>
		<input type="button" name="add-file-button" preview="{{= previewURL}}" fid="{{= clbId}}"value="插入链接">
	</p>
</li>
</script>

<script id="aone-file-row-template" type="html/text">
<li class="attach-link" id="attach_li_{{= fid}}">
	<a class="lightDel ui-RTCorner" fid="{{= fid}}" name="delete-button"></a>
	<a class="file" href="{{= infoURL}}">{{= title}}</a>
	<p class="attachAction">
		<input type="button" name="add-file-button"  preview="{{= previewURL}}" fid="{{= clbId}}" value="插入链接">
	</p>
</li>
</script>

<script type="text/html" id="page-tag-template">
	<li><a href="<vwb:Link context='tag' format='url'/>#&tag={{= id}}">
						{{= title}} </a>
					<a class="delete-tag-link lightDel" tag_id="{{= id}}" rid="{{= item_key}}"></a>
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

<script src="${contextPath}/scripts/bootstrap/js/bootstrap.min.js"></script>
