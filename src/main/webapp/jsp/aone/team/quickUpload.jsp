<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<script type="text/javascript" src="${contextPath}/jsp/aone/js/fileuploader.js"></script>
<link href="${contextPath}/jsp/aone/css/fileuploader.css" rel="stylesheet" type="text/css">	
<script type="text/javascript">
$(document).ready(function(){
	
	var upload_url = "<vwb:Link context='upload' format='url'/>?func=uploadCollectionFiles";
	
	var uploadedFiles = [];
	var index = 0;
	
	function createUploader(){  
         var uploader = new qq.FileUploader({
             element: document.getElementById('file-uploader-demo1'),
             action: upload_url,
             params:{cid:"1"},
             onComplete:function(id, fileName, data){
             	uploadedFiles[index] = data;
             	$("#files-data-area").append('<input type="hidden" name="fids" value="'+data.fid+'"/>');
             	$("#files-data-area").append('<input type="hidden" name="fileNames" value="'+data.title+'"/>');
             	//alert($("#files-data-area").html());
             	$("#save-file-button").attr("disabled",false);
             	if(index==0)
             		$("input[name='fileSetName']").attr("value","页面:"+data.title);
             	index ++;
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
     
});
</script>
<div id="content-title">
	<h1>快速上传</h1>
</div>
<div class="content-through" id="quickUpload">
<form id="quick-upload-form" method="POST" action="<vwb:Link context='quick' format='url'/>?func=afterUpload">
	<div id="step1" class="content-major">
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
	<div id="step2" class="content-side">
		<table class="ui-table-form" style="margin-top:2em">
		<tbody>
			<tr>
				<th>文件位置：</th>
				<td style="height:50px">
					<span id="old-collection-select">
						<c:if test="${not empty collections}">
							<select name="selectCollection"  value="${selectCollection}">
								<c:forEach items="${collections}" var="item">
									<option value="${item.resourceId}" <c:if test="${item.resourceId eq selectCollection}">selected</c:if>>${item.title}</option>
								</c:forEach>
							</select>
						</c:if>
					</span>
					<span id="new-collection-input" <c:if test="${not empty collections}">style="display:none"</c:if>>
						<input type="text" name="newCollection" value=""/>
					</span>
					<br/>
					<c:choose>
						<c:when test="${empty collections}">请输入新集合名称</c:when>
						<c:otherwise><label><input type="checkbox" id="use-new-collection-option" name="useNewCollection" value="true"/>新建集合</label></c:otherwise>
					</c:choose>
				</td>
			</tr>
			<tr>
				<th>附加页面：</th>
				<td>
					<input type="checkbox" id="use-page-option" name="isUsePage"/>
					<label for="use-page-option">为刚上传的多个文件生成一个页面</label>
				</td>
			</tr>
			<tr class="page-row disabled">
				<th>页面标题：</th>
				<td><input type="text" name="fileSetName" value="" disabled /></td>
			</tr>
			<tr class="page-row disabled">
				<th>页面描述：</th>
				<td><textarea name="fileSetDescription" disabled ></textarea></td>
			</tr>
			
		</tbody>
		</table>
	</div>
	<div id="step3" class="content-through toolHolder holderMerge" style="#display:block;#margin-top:0;">
		<div class="holderCenter" style="margin-top:0.5em;">
			<input type="submit" value="保存文件" id="save-file-button" class="largeButton" disabled="true" />
			<a class="largeButton dim" href="javascript:window.history.back();">取消</a>
		</div>
	</div>
</form>
</div>

