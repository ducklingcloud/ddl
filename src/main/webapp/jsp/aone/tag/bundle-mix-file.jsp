<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<script type="text/javascript" src="${contextPath}/scripts/syntaxhighlighter/scripts/shCore.js"></script>
<script type="text/javascript" src="${contextPath}/scripts/syntaxhighlighter/scripts/shAutoloader.js"></script>
<link href="${contextPath}/scripts/syntaxhighlighter/styles/shCore.css" rel="stylesheet" type="text/css"/>
<link id="coreCss" href="${contextPath}/scripts/syntaxhighlighter/styles/shCoreEclipse.css" rel="stylesheet" type="text/css"/>
<link id="themeCss" href="${contextPath}/scripts/syntaxhighlighter/styles/shThemeEclipse.css" rel="stylesheet" type="text/css"/>

<div id="content-title">
	<c:choose>
		<c:when test="${pageInfo.fileExtend eq 'FILE' or pageInfo.fileExtend eq 'TEXT'}">
			<h1><span>文件：</span>${resource.title}</h1>
		</c:when>
		<c:otherwise>
			<h1><span>图片：</span>${resource.title}</h1>
		</c:otherwise>
	</c:choose>
	<c:set var="tagMap" value="${resource.tagMap}" scope="request"/>
	<c:set var="rid" value="${resource.rid}" scope="request"/>
	<c:set var="starmark" value="${pageInfo.starmark }" scope="request"/>
	<jsp:include page="bundle-add-tag.jsp"/>
</div>
<div id="content-major">
	<div id="version">
		${resource.lastEditorName} 上传于
		 <fmt:formatDate value="${resource.lastEditTime}" type="both" dateStyle="medium" /> |
		<a href="#"> 当前版本：${resource.lastVersion}</a>
		<c:if test="${resource.lastVersion >1 }">
			 | <span>历史版本：</span>
			<c:forEach var="index" begin="1" end="${resource.lastVersion-1 }"
				step="1">
				<a title="xx" class="versionHistory"
					href='<vwb:Link context="download" page="${resource.itemId }" format="url"/>?type=doc&version=${index}'>
					${index } </a>
			</c:forEach>
		</c:if>
		<c:if test="${pageInfo.fileExtend != 'TEXT' && pageInfo.fileExtend != 'FILE'}">
			|&nbsp;<a target="_blank" href="<vwb:Link context='originalImage' page='${resource.itemId }' format='url'/>?version=${resource.lastVersion}">查看原图</a>
		</c:if>
		<c:if test="${!empty copyLog}">
			<br>
			${ copyLog.userName} 从 团队 [${copyLog.fromTeamName}]复制了页面 [${copyLog.rTitle }] 版本：${copyLog.fromVersion}
		</c:if>
	</div>
	<div class="clear"></div>
	<c:choose>
		<c:when test="${pageInfo.fileExtend eq 'FILE'}">
			<div id="fileInfo">
				<table class="fileContainer">
					<tr>
						<th><div
								class="fileIcon <vwb:FileExtend  fileName='${resource.title}'/>"></div></th>
						<td>
							<h3 class="fileName">${resource.title}</h3>
							<p class="fileNote"></p>
							<div class="largeButtonHolder">
								<c:if test="${pageInfo.fileExtend eq 'FILE'}">
									<c:choose>
										<c:when test="${pageInfo.pdfstatus== 'converting' && enableDConvert}">
											<p>该文档正在进行转换，请稍等几分钟后刷新页面即可浏览！</p>
										</c:when>
										<c:when test="${pageInfo.pdfstatus== 'success' || pageInfo.pdfstatus == 'original_pdf'}">
											<a
												href="<vwb:Link page="${resource.itemId}" context='file' format='url'/>?func=onlineViewer"
												class="largeButton extra" id="onlineViewer" target="_blank">在线预览</a>
										</c:when>
										<c:when test="${pageInfo.pdfstatus== 'fail' && enableDConvert}">
											<vwb:CLBCanUse>
												<p>该文档在上次PDF转换过程中转换失败！如仍需要在线浏览请进行格式转换！</p>
												<a class="largeButton extra pdfTransform">格式转换</a>
											</vwb:CLBCanUse>
										</c:when>
										<c:when test="${pageInfo.pdfstatus== 'source_not_found' && enableDConvert}">
											<p>PDF转换时未找到原文档，无法预览！</p>
										</c:when>
										<c:when test="${pageInfo.fileType eq 'img'}">
											<!-- 剔除图片的不支持转换信息 -->
										</c:when>
										<c:otherwise>
											<c:if test="${enableDConvert}">
											<c:choose>
												<c:when test="${pageInfo.supported}">
													<a class="largeButton extra pdfTransform">格式转换</a>
												</c:when>
												<c:otherwise>
													<p>暂不支持该文件类型的在线显示</p>
												</c:otherwise>
											</c:choose>
											</c:if>
										</c:otherwise>
									</c:choose>
								</c:if>
								<a href="${pageInfo.downloadUrl}" class="largeButton extra">下载<span
									class="ui-text-note">(${pageInfo.shortFileSize})</span></a>
								<c:if test="${enableDConvert and (pageInfo.supported or pageInfo.fileType eq 'pdf')}">
									<p class="ui-text-note">在线预览支持Firefox10+,Chrome,IE9+,Safari5+等浏览器</p>
								</c:if>
							</div>
						</td>
					</tr>
				</table>
			</div>
		</c:when>
		<c:when test="${pageInfo.fileExtend eq 'TEXT' }">
			<ul id="highlighter-mode">
				<li><a mode="Default">Default</a></li>
				<li><a mode="Django">Django</a></li>
				<li><a mode="Eclipse">Eclipse</a></li>
				<li><a mode="Emacs">Emacs</a></li>
				<li><a mode="FadeToGrey">FadeToGrey</a></li>
				<li><a mode="Midnight">Midnight</a></li>
				<li><a mode="RDark">RDark</a></li>
				<li><a mode="MDUltra">MDUltra</a></li>
			</ul>
			<div id="codeMode">
				<pre id="directShowFile"><vwb:DirectShowFile rid="${resource.rid }" version="${resource.lastVersion}"/></pre>
			</div>
			<div class="downloadFile">
				<a href="${pageInfo.downloadUrl}" class="largeButton extra">下载
						<span class="ui-text-note">(${pageInfo.shortFileSize})</span>
				</a>
			</div>	
		</c:when>
		<c:otherwise>
			<div id="photoInfo">
				<div class="photoContainer">
					<a target="_blank" href="<vwb:Link context='originalImage' page='${resource.rid }' format='url'/>?version=${resource.lastVersion}">
						<img src="${pageInfo.downloadUrl}" />
					</a>
					<a href="${pageInfo.downloadUrl}&imageType=original" class="largeButton extra">下载
					<span class="ui-text-note">(${pageInfo.shortFileSize})</span></a>
				</div>
			</div>
		</c:otherwise>
	</c:choose>
	<hr />
	<div id="comment">
		<c:set var="itemId" value="${resource.itemId}" scope="request"/>
		<c:set var="itemType" value="DFile" scope="request"/>
		<jsp:include page="/jsp/aone/comment/displayComment.jsp"></jsp:include>
	</div>
	<div class="bedrock"></div>
</div>


<c:set var="fileBarItemId" value="${resource.itemId}" scope="request"></c:set>
<c:set var="fileBarBid" value="${bundle.itemId}" scope="request"></c:set>
<jsp:include page="/jsp/aone/tag/lynxFileBar.jsp"></jsp:include>
<script type="text/javascript">
$(document).ready(function(){
	(function scrollNav(){
		var lis=$("#bundle-navList li");
		var scrollValue=0;
		for(var i=0;i<lis.length;i++){
			if($(lis[i]).attr("class")=="active"){
				break;
			}
			scrollValue+=$(lis[i]).height();
		}
		$('#bundle-navList').scrollTop(scrollValue);
	})();
	
<!-- ----------------------------- SyntaxHighlighter Config Start  -----------------------------------  -->
	var fileType = "${pageInfo.fileType}";
	function initPreTag(){
		$('#directShowFile').addClass('brush: '+fileType+';');
	}
	initPreTag();
	
	function path()
	{
	  var args = arguments,
	      result = []
	      ;
	       
	  for(var i = 0; i < args.length; i++)
	      result.push(args[i].replace('@', '${contextPath}/scripts/syntaxhighlighter/scripts/'));
	       
	  return result
	};
	 
	SyntaxHighlighter.autoloader.apply(null, path(
	  'applescript            @shBrushAppleScript.js',
	  'actionscript3 as3      @shBrushAS3.js',
	  'bash shell             @shBrushBash.js',
	  'coldfusion cf          @shBrushColdFusion.js',
	  'cpp c                  @shBrushCpp.js',
	  'c# c-sharp csharp      @shBrushCSharp.js',
	  'css                    @shBrushCss.js',
	  'delphi pascal          @shBrushDelphi.js',
	  'diff patch pas         @shBrushDiff.js',
	  'erl erlang             @shBrushErlang.js',
	  'groovy                 @shBrushGroovy.js',
	  'java                   @shBrushJava.js',
	  'jfx javafx             @shBrushJavaFX.js',
	  'js jscript javascript  @shBrushJScript.js',
	  'perl pl                @shBrushPerl.js',
	  'php                    @shBrushPhp.js',
	  'text plain             @shBrushPlain.js',
	  'py python              @shBrushPython.js',
	  'ruby rails ror rb      @shBrushRuby.js',
	  'sass scss              @shBrushSass.js',
	  'scala                  @shBrushScala.js',
	  'sql                    @shBrushSql.js',
	  'vb vbnet               @shBrushVb.js',
	  'xml xhtml xslt html    @shBrushXml.js'
	));
	SyntaxHighlighter.all();
	SyntaxHighlighter.defaults['toolbar']=false;
	
	$('#highlighter-mode a').click(function(){
		var mode = $(this).attr('mode');
		var coreCssUrl = '${contextPath}'+'/scripts/syntaxhighlighter/styles/shCore'+mode+'.css';
		var themeCssUrl = '${contextPath}'+'/scripts/syntaxhighlighter/styles/shTheme'+mode+'.css';
		$('#coreCss').attr('href',coreCssUrl);
		$('#themeCss').attr('href',themeCssUrl);
	});
<!-- ----------------------------- SyntaxHighlighter Config End  -----------------------------------  -->
	
	var upload_url = "<vwb:Link context='upload' format='url'/>?func=updateFile";
	
	var uploadedFiles = [];
	var index = 0;
	function createUploader(){  
         var uploader = new qq.FileUploader({
             element: document.getElementById('file-uploader-demo1'),
             action: upload_url,
             params:{cid:"${cid}",rid:"${resource.rid}",version:"${resource.lastVersion}"},
             multiple:false,
             onComplete:function(id, fileName, data){
             	uploadedFiles[index] = data;
             	index ++;
             },
             debug: true
         });           
     };
     
     createUploader();
     
     $('#attach-to-bundle').click(function(){
 		window.location.reload();
 	});
	
	var transformUrl="<vwb:Link page='${resource.itemId}' context='file' format='url'/>?func=pdfTransform";
	$('.pdfTransform').click(function(){
		ajaxRequest(transformUrl,"", function(){
			window.alert("文档已在后台转换，稍等几分钟后刷新即可进行在线浏览！");
		});
	});
	
	$('a[name=cancel]').click(function(){
		$(this).parents('div.ui-dialog').fadeOut();
	});
	
	if (($.browser.msie && parseInt($.browser.version, 10)<9)) {
		$('#onlineViewer').addClass('disabled')
		.attr({
			'disabled':'disabled',
			'title':'您的浏览器不能使用在线预览功能，请使用Firefox10+,IE9+,Safari5+,Chrome20+等浏览器'
		})
		.click(function(event){
			event.preventDefault();
		});
	}
})
</script>
