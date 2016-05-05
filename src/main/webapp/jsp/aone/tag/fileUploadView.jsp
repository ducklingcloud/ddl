<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<script type="text/javascript" src="${contextPath}/jsp/aone/js/fileuploader.js?v=${aoneVersion}"></script>
<script type="text/javascript" src="${contextPath}/scripts/jquery/jquery.validate.js"></script>
<link href="${contextPath}/jsp/aone/css/fileuploader.css" rel="stylesheet" type="text/css">	
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/tag.css" type="text/css" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/tag-z.css" type="text/css" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/tokenInput.css" type="text/css" />
<script type="text/javascript" src="${contextPath}/jsp/aone/js/toker-jQuery-forTag.js"></script>
<div id="content-title">
	<h1>快速上传</h1>
</div>
<div class="content-through" id="quickUpload">
<form id="quick-upload-form" method="POST" action="<vwb:Link context='quick' format='url'/>?func=afterUpload&bid=${bid}">
	<input name="tagIds" type="hidden" value="${tagIds }">
	<div id="step1" class="content-major" style="border-right:1px solid #ccc;">
		<p class="ui-text-note" style="padding-left:1em; margin-top:2em;">可以多次上传附件。使用Ctrl或Shift键，一次上传多个附件。</p>
		<div id="file-uploader-demo1" >
			<div class="qq-uploader" >
				<div class="qq-upload-button" >
					<input type="file" multiple="multiple" name="files" >
				</div>
				<ul class="qq-upload-list fileList"></ul>
			</div>
		</div>
		<div id="files-data-area">
		</div>
	</div>
	<div id="step2" style="margin-left:60%;" >
	<c:choose>
		<c:when test="${bid==0 }">
			<p><label><input type="checkbox" value="true" name="groupCheck"/>保存为组合</label></p>
			<p><label>组合名称：<input type="text" name="groupName"  disabled="disabled"/></label><div class="errorContainer" width="200"></div></p>
			<p><label><textarea name="groupDesc" id="groupDescId" disabled="disabled">组合描述：</textarea></label></p>
			<input type="hidden" name="tagCollection" value=""/>
		</c:when>
		<c:otherwise>
			<p><label>添加到组合</label></p>
			<hr/>
			<h3>${bundle.title}</h3>
			<input type="hidden" name="existBundle" value="${bundle.bid}"/>
		</c:otherwise>
	</c:choose>
	</div>
	<div id="step3" style="margin-left:60%;" >
		<p>添加标签：</p>
		<ul class="tagList">
			<li class="newTag"><a>+</a></li>
		</ul>
	</div>
	<div id="step4" class="content-through toolHolder holderMerge" style="#display:block;#margin-top:0;">
		<div class="holderCenter" style="margin-top:0.5em;">
			<input type="button" value="保存文件" id="save-file-button" class="largeButton" disabled="true" />
			<a class="largeButton dim" href="javascript:window.history.back();">取消</a>
		</div>
	</div>
</form>
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

<script type="text/javascript">
$(document).ready(function() {
	var afterUpdata = "<vwb:Link context='quick' format='url'/>";
	$('#step4 input[type="button"]').click(function() {
		var check = $('#step2 input[type="checkbox"]').attr('checked');
		if (check) {
			var avatar = $('#step2 input[name="groupName"]');
			$.ajax({
				url : afterUpdata+ '?func=checkBundleName',
				data : {'groupName' : avatar.val()},
				dataType : "json",
				type : 'POST',
				success : function(data) {
					if (data["success"] == 'true') {
						$('#quick-upload-form').submit();
						return true;
					} else {
						$('.errorContainer').html(data["error"]);
						avatar.focus();
						return false;
					}
				},
				statusCode:{
					450:function(){alert('会话已过期,请重新登录');},
					403:function(){alert('您没有权限进行该操作');}
				}
			});
		} else {
			$('#quick-upload-form').submit();
			return true;
		}
	});

	$('#step2 input[type="checkbox"]').live('click',function() {
		if ($('#step2 input[type="checkbox"]').attr('checked')) {
			$('#step2 input[name="groupName"]').removeAttr("disabled");
			$('#groupDescId').removeAttr('disabled');
		} else {
			$('#step2 input[name="groupName"]').attr('disabled', 'disabled');
			$('#groupDescId').attr('disabled','disabled');
		}
	});
});
</script>

<script type="text/javascript">
$(document).ready(function(){
	$('input.tagPoolAutoShow').tokenInput("<vwb:Link context='tag' format='url'/>?func=loadTeamTags&type=nogroup", {
		theme:"facebook",
		hintText: "输入标签名称，以逗号或回车结束",
		searchingText: "正在搜索……",
		noResultsText: "没有该标签，输入逗号或回车添加",
		preventDuplicates: true,
	}); 
	var upload_url = "<vwb:Link context='upload' format='url'/>?func=uploadFiles";
	
	var uploadedFiles = [];
	var index = 0;
	var status = false;
	function createUploader(){  
         var uploader = new qq.FileUploader({
             element: document.getElementById('file-uploader-demo1'),
             action: upload_url,
             params:{cid:"1"},
             onComplete:function(id, fileName, data){
             	uploadedFiles[index] = data;
             	$("#files-data-area").append('<input type="hidden" name="rids" value="'+data.rid+'"/>');
             	$("#files-data-area").append('<input type="hidden" name="fids" value="'+data.fid+'"/>');
             	$("#files-data-area").append('<input type="hidden" name="fileNames" value="'+data.title+'"/>');
             	//alert($("#files-data-area").html());
             	$("#save-file-button").attr("disabled",false);
             	if(index==0)
             		$("input[name='fileSetName']").attr("value","页面:"+data.title);
             	index ++;
             	if(index>1){
             		if(!status){
             			$('#step2 input[type="checkbox"]').attr('checked','true');
             			$('#step2 input[name="groupName"]').removeAttr("disabled");
             			var fileName = $(".qq-upload-success span.qq-upload-file").first().html();
             			var i = fileName.lastIndexOf('.');
             			if(i>0){
             				fileName = fileName.substring(0,i);
             			}
             			$('#step2 input[name="groupName"]').val(fileName);
             			$('#step2 input[name="groupName"]').focus();
             			$('#step2 input[name="groupName"]').select();
             			$('#groupDescId').removeAttr("disabled");
             			status = true;
             		}
             		
             	}
             },
             debug: true
         });           
     };
     
     createUploader();
     
     $("input[name='useNewCollection']").live("change",function(){
     	if($(this).attr("checked")){
     		$("#new-collection-input").show();
     		$("#old-collection-select").hide();
     	}else{
     		$("#new-collection-input").hide();
     		$("#old-collection-select").show();
     	}
     });
     
     var currentValidator = $("#quick-upload-form").validate({
		rules: {
			fileSetName: {required: "#use-page-option:checked"},
			fids:{required:true},
			newCollection: {required:"#use-new-collection-option:checked"},
			fileSetName: {required:true}
		},
		messages:{
			fileSetName:{required:"请输入文件页面名称"},
			fids:{required:"您没有上传任何附件"},
			newCollection: { required: '请输入集合名称' },
			fileSetName: { required: '请输入页面标题' }
		}
	});
     
     $("#use-page-option").change(function(){
    	if($(this).attr("checked")) {
    		$("tr.page-row").removeClass('disabled');
    		$('tr.page-row input, tr.page-row textarea').removeAttr('disabled');
    	}
    	else {
    		$("tr.page-row").addClass('disabled');
    		$('tr.page-row input, tr.page-row textarea').attr('disabled', 'disabled');
    	}
     });
     
/*-----------------------tag begin ---------------------*/
	function initTag(){
		var initTags = $("input[name=tagIds]").val();
		var tagIds = initTags.split(",");
		var getTagInfoUrl = site.getURL("quick", null);
		ajaxRequest(getTagInfoUrl, {"func":"getTag", "tagids[]":tagIds}, function(data){
			$('li.newTag').before($("#tag-template").tmpl(data));
			var tags = new Array();
			$.each(data,function(index,element){
				tags.push(element.id);
			});
			$("input[name=tagIds]").val(tags);
		});
	}
	
	initTag();
 
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
   					var ridLen = aTBox.log.rid.length;
  					var newData = new Array();
  					for(var i=0; i<data.length; i+=ridLen){
  						newData.push(data[i]);
  					}
   					$('li.newTag').before($("#tag-template").tmpl(newData));
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

 	$('li.newTag').click(function(){
 		var rids = new Array();
 		$("#files-data-area input[name=rids]").each(function(){
 			rids.push($(this).val());
 		});
 		if(rids.length<=0){
 			alert("您还没有上传文件，无法添加标签！");
 		}else{
	 		aTBox.prepare({ ul: $(this).parent(), ridArr: rids });
	 		addSingleTagDialog.show();
	 		$("#token-input-").focus();
 		}
 	});
 	
 	$("#addSingleTagDialog .saveThis").click(function(){
 		var feedback = addTag_handler.addTagForSingleRecord();
 	});
 	
 	$('#resAction-tag a').click(function(){
 		aTBox.prepare({ ridArr: selector.getItem() });
 		addSingleTagDialog.$dialog.addClass('multiple');
 		addSingleTagDialog.show();
 	});

	$("a.delete-tag-link").live('click',function(){
		var tagId = $(this).attr("tag_id");
		var tagIdStr = $("input[name=tagIds]").val();
		var tagIdFromDocPage = tagIdStr.split(",");
		var index = tagIdFromDocPage.indexOf(tagId);
		if( index >=0){
			tagIdFromDocPage.pop(index);
			$("input[name=tagIds]").val(tagIdFromDocPage.toString());
			$(this).parent().remove();
			return;
		}
		var rids = new Array();
 		$("#files-data-area input[name=rids]").each(function(){
 			rids.push($(this).val());
 		});
    	var params = {"func":"remove","rid[]":rids,"tagId":tagId};
    	var url = site.getURL('tag',null);
    	var $a = $(this);
		ajaxRequestWithErrorHandler(url,params,function(data){
    		$a.parent().remove();
    	},notEnoughAuth);
    });
	
	/*---------------tag end---------------*/
	
	function notEnoughAuth(){
		alert("您无权进行此操作！");
	};
});
</script>

<script type="text/html" id="tag-template">
	<li tag_id={{= id}}>
		<a target="_blank" href="<vwb:Link context='tag' format='url'/>#&tag={{= id}}">{{= title}} </a>
		<a class="delete-tag-link lightDel" tag_id="{{= id}}"></a>
	</li>
</script>
