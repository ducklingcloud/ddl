<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<script type="text/javascript" src="${contextPath}/jsp/aone/js/fileuploader.js?v=${aoneVersion}"></script>
<link href="${contextPath}/jsp/aone/css/fileuploader.css" rel="stylesheet" type="text/css">	
<script language="javascript">
$(document).ready(function(){
	
	var upload_url = "<vwb:Link context='upload' format='url'/>?func=uploadCollectionFiles";
	
	var uploadedFiles = [];
	var index = 0;
	
	function createUploader(){  
         var uploader = new qq.FileUploader({
             element: document.getElementById('file-uploader-demo1'),
             action: upload_url,
             params:{cid:"${cid}"},
             onComplete:function(id, fileName, data){
             	uploadedFiles[index] = data;
             	index ++;
             },
             debug: true
         });           
     };
     
     createUploader();
});
</script>
<div id="collection">
	<h2>集合文件列表</h2>
	<div id="file-uploader-demo1" >
		<div class="qq-uploader" >
			<div class="qq-upload-button" >
				上传附件
				<input type="file" multiple="multiple" name="files" >
			</div>
			<ul class="qq-upload-list fileList"></ul>
		</div>
	</div>
	<br/>
</div>

