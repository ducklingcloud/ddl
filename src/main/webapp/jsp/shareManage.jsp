<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<link href="${contextPath}/scripts/bootstrap/css/bootstrap.min.css" rel="stylesheet" media="screen"/>
<link href="${contextPath}/scripts/bootstrap/css/bootstrap-responsive.min.css" rel="stylesheet"/>
<link href="${contextPath}/scripts/bootstrap/css/todc-bootstrap.css" rel="stylesheet"/>	
<link href="${contextPath}/jsp/aone/css/fileuploader.css" rel="stylesheet" type="text/css">	
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/tokenInput.css" type="text/css" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/lynx.css?v=${aoneVersion}" type="text/css" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/lion.css?v=${aoneVersion}" type="text/css" />
<style type="text/css">
	#resourceList.asTight li.files-item.element-data.dropFolder,
		#resourceList.asTight li.files-item.element-data.dropFolder:hover,
		#resourceList.asTable li.files-item.element-data.dropFolder,
		#resourceList.asTable li.files-item.element-data.dropFolder:hover  {background: #cfc}
</style>

<div class="lionContent" style="width:1158px;">
	<div class="content-menu readyHighLight0" >
		<ul class="myNavList">
		    <li class="filterDrop">
              <a id="showAllFiles" class="" href="${list }">所有文件<span class="caretSpan"><b class="caret"></b></span></a>
              <ul class="filteFile" style="display:none">
                <li><a tabindex="-1" id="showTeamPages" queryType="showTeamPages" href="${list }#queryType=showFileByType&type=DPage">协作文档</a></li>
				<li><a tabindex="-1" id="showTeamFiles" queryType="showTeamFiles" href="${list }#queryType=showFileByType&type=DFile">文件</a></li>
				<li><a tabindex="-1" id="showTeamPicture" queryType="showTeamPicture" href="${list }#queryType=showFileByType&type=Picture">图片</a></li>
              </ul>
              <div class="clear"></div>
            </li>
		    <li><a id="showTeamRecentChang"  queryType="teamRecentChangeFiles" href="${list}#rid=0&queryType=myCreate">最近更新</a></li>
		    <li><a id="showMyRecentFiles"  queryType="myRecentFiles" href="${list }#rid=0&queryType=myRecentFiles">我常用的</a></li>
		    <li><a id="showMyCreateFiles"  queryType="myCreate" href="${list}#rid=0&queryType=myCreate">我创建的</a></li>			
			<li><a id="showMyStarFiles" queryType="myStarFiles"  href="${list }#rid=0&queryType=myStarFiles">已加星标</a></li>
		</ul>
		<ul class="myNavList myShare">
			<li class="current"><a>分享历史</a></li>
		</ul>
	</div>
	<div class="content-menu-body lionContent">
	<div class="content-right-board">
		<div id="navBarDiv" class="navBar">
			<div class="currentResource">
				<h2 class="currentResourceTitle"></h2>
			</div>
			<ol class="breadcrumb" id="navBarOl"></ol>
			<div class="newBtnGroup">
			</div>	
			</div>
		<div class="fixMenuBar">
			<div id="opareteFileMessage" class="alert alert-success" style="display: none;z-index:9999"></div>
			<div class="toolBar">
			 	<input type='checkbox' name="checkAllFiles" id="checkAllFiles" class="showSelectedOperate" value=""/>
				<span class="selectedOperGroup" style="display:none">
					<span id="checkedMessage">&nbsp;已选中<span class="checkedNum"></span>项</span>	
					<a class="btn btn-small btn-primary selectedOper" id="CancelShare" title="取消分享" ><i class="icon-move icon-white"></i> 取消分享</a>
				</span>
				<div class="filterBoard">
					<a class="refresh" onclick="reflesh();return false" title="刷新"><i class="icon-refresh icon-color"></i></a>
				</div>
			</div>
			<div class="tableHeader">
				<div class="title-col col share">  
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;分享文件  
				</div>   
				<div class="time-col col">    
					<span>提取码</span>   
				</div>        
				<div class="creator-col col">
					<span>分享者</span>
				</div>
				<div class="time-col col">
					<span>分享时间</span>
				</div>   
				<div class="version-col col">    
					<span style="color:#666">大小</span>   
				</div>        
				<div class="version-col col">
					<span style="color:#666">下载次数</span>
				</div>
			</div>
		</div>
		
		<div id="fileItemDisplay" >
			<ul id="resourceList" class="readyHighLight1 asTight"> 
				<c:forEach items="${res }" var="re">
		   		<c:set value="${srs[re.rid] }" var="share"></c:set>
				<li class="files-item element-data">
					<div class="title-col col share">
					<div class='oper' style="width:35px; width:50px\0;">
						<input type='checkbox' class="showSelectedOperate" />
						<c:if test="${!empty share.password }">
							<div class="iconLynxTag icon-codeLock"></div>
						</c:if>
						<input type='hidden' class='rid' value='${re.rid }'/>
					</div> 
				    <span class="file-commands" style="display: none;">
				           	<a class="cancle-share cancelShare" title="取消分享" style="float:left"></a>
					</span>
						<h2>
							<a class='fileName' title='${re.title }' href="${shareUrl[re.rid] }" target="_blank">
								<span class='${re.itemType} headImg ${re.fileType }'><input type='hidden' class='itemType' value='${re.itemType}'></span>
								<c:choose>
									<c:when test="${re.status =='delete' }">
										<span class="fileNameSpan" style="color: #999">${re.title}</span>
										<span style="color: red">(已失效)</span>
									</c:when>
									<c:otherwise>
										<span class="fileNameSpan">${re.title}</span>
									</c:otherwise>
								</c:choose>
				            </a>
						</h2>
				     
				   </div>
				   		<c:set value="${srs[re.rid].shareUid }" var="uid"></c:set>
						<div class='time-col col'>
							<c:choose>
								<c:when test="${empty share.password }">
									<span style="display:none;" id="codeText-${re.rid }">${shareUrl[re.rid] }</span>
									<span class="fetchCode"> &nbsp; (无)</span>
								</c:when>
								<c:otherwise>
									<span style="display:none;" id="codeText-${re.rid }">${shareUrl[re.rid] } 提取码：${share.password}</span>
									<span class="fetchCode">${share.password}</span>
								</c:otherwise>
							</c:choose>
							<span id="copyCode-${re.rid }" class="copyFetchCode" data-clipboard-target="codeText-${re.rid }">复制</span>
						</div>
				       <div class='creator-col col'>
							<span>${users[uid] }</span>
						</div>
				       <div class='time-col col'><span><fmt:formatDate value="${srs[re.rid].createTime }" pattern="yyyy-MM-dd HH:mm"/></span></div>
					   <div class='version-col col'>
							<span>${reSize[re.rid]}</span>
					   </div>
						<div class='version-col col'>
							<span>${srs[re.rid].downloadCount }</span>
						</div>
					<div class="clear"></div>
					
				</li>
				</c:forEach>
			
			
			</ul>
			<div id="no-result-helper" class="NA" style="display:none;">
				<!-- <p>您可以：</p> -->
				<ul class="no-result-guide">
					<li><a id="createNewDDoc" href="javascript:void(0)">
							<span class="iconLynx icon-page hover"></span> 新建协作文档
					</a></li>
					
				</ul>
			</div>
			<div class="clear"></div>
		</div>
	</div>
	</div>
</div>


<div id="alertDeleteModel" class="modal hide fade" data-backdrop="static">
  <div class="modal-header">
    <button type="button" class="close closeUpload initShow" data-dismiss="modal" aria-hidden="true">&times;</button>
    <h3>提示</h3>
  </div>
  <div class="modal-body">
  <div class='initShow'>
	<p class="alertContent">确定取消所选文件的分享吗？</p>
	<input type="hidden" name="rid" class="deleteRid" value=''/>
  </div>
   <div class="deleteShow" style="display:none">
	<p class="alertContent">正在取消分享...</p>
  </div>
  </div>
  <div class="modal-footer">
  	<button class="btn btn-primary initShow" id="okAlertContent">确认</button>
    <button class="btn closeAlertModel initShow"  data-dismiss="modal" aria-hidden="true">取消</button>
  </div>
</div>

<script type="text/javascript" src="${contextPath}/jsp/aone/js/toker-jQuery-forTag.js"></script>
<script type="text/javascript" src="${contextPath}/scripts/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${contextPath}/scripts/jquery.tmpl.min.js"></script>
<script type="text/javascript" src="${contextPath}/scripts/jquery_tree/jquery.jstree.js"></script>
<script type="text/javascript" src="${contextPath}/scripts/jquery_tree/jquery-ui-1.10.3.custom.min.drag-drop.js"></script>
<script type="text/javascript" src="${contextPath}/jsp/aone/js/toker-jQuery.js?v=${aoneVersion}"></script>
<script type="text/javascript" src="${contextPath}/scripts/zeroclipboard/ZeroClipboard-1.3.5.js"></script>

<script type="text/javascript">

	function reflesh(){
		window.location.reload();
	}
	$(document).ready(function(){
		
		function showMsgAndAutoHide(msg, type,time){
			time=time||2000;
			showMsg(msg,type);
			hideMsg(time);
		}
		function showMsg(msg, type){
			type = type || "success";
			$("#opareteFileMessage").removeClass().addClass("alert alert-" + type).html(msg).show(150);
		}
		function hideMsg(timeout){
			timeout = timeout || 2000;
			window.setTimeout(function(){$("#opareteFileMessage").hide(150);}, timeout);
		}
		$("#CancelShare").live('click',function(){
			var checks = $(".showSelectedOperate:checked:not(#checkAllFiles)");
			var rids = new Array();
			for(var i=0;i<checks.length;i++){
				rids.push($(checks[i]).parents(".files-item.element-data").find("input.rid").val());
			}
			deleteShare.data = rids;
			showAlertDeleteModel();
		});
		
		$(".cancelShare").live('click',function(){
			var rids = new Array();
			rids.push($(this).parents(".files-item.element-data").find("input.rid").val());
			deleteShare.data = rids; 
			showAlertDeleteModel();
		});
		
		function showAlertDeleteModel(){
			$("#alertDeleteModel .initShow").show();
 			$("#alertDeleteModel .deleteShow").hide();
 			$("#alertDeleteModel").modal();
		}
		
		
		$("#okAlertContent").live('click',function(){
			$("#alertDeleteModel .initShow").hide();
 			$("#alertDeleteModel .deleteShow").show();
			deleteShare.deleteShareResource();
			var ss = $(".showSelectedOperate:checked");
			for(var i=0;i<ss.length;i++){
				$(ss[i]).attr("checked",false);
			}
			$(".selectedOperGroup").hide();
		});
		
		var deleteShare = {
				data:[],
				deleteShareResource : function(){
					var d = new Object();
					d.rids = this.data;
					d.func = "delete";
					var url = window.location.href;
					$.ajax({
						url:url,
						data:d,
						type:'post',
						dataType:'json',
						success:function(data){
							for(var i=0;i<deleteShare.data.length;i++){
								var rid = deleteShare.data[i];
								$("input.rid[value="+rid+"]").parents(".files-item.element-data").remove();
							}
							$("#alertDeleteModel").modal("hide");
							showMsgAndAutoHide("已经成功取消分享", "success",2000);			
						}
					});
				}
		};
		
	
		
		$(".showSelectedOperate").live('click',function(){
			validateSelectedOperate();
		});
		
		
		
		function validateSelectedOperate(){
			 if($(".showSelectedOperate:checked:not(#checkAllFiles)").size()>0){
				 $("#checkedMessage .checkedNum").html($(".showSelectedOperate:checked:not(#checkAllFiles)").size());
				 $(".selectedOperGroup").show();
			 }else{
				 $(".selectedOperGroup").hide();
			 }
			 if($(".showSelectedOperate:checked:not(#checkAllFiles)").size()==$(".showSelectedOperate:not(#checkAllFiles)").size()&&$(".showSelectedOperate:checked:not(#checkAllFiles)").size()>0){
				 $("#checkAllFiles").attr("checked",true);
			 }else{
				 $("#checkAllFiles").attr("checked",false);
			 }
			 $(".showSelectedOperate:not(#checkAllFiles)").parents("li.files-item").removeClass("chosen");
			 $(".showSelectedOperate:checked:not(#checkAllFiles)").parents("li.files-item").addClass("chosen");
		};
		
		$("#checkAllFiles").click(function(){
	    	 if($(this).attr("checked")==true||$(this).attr("checked")=="checked"){
	    	 	$("#fileItemDisplay input:checkbox").attr("checked",true);
	    	 }else{
	    		 $("#fileItemDisplay input:checkbox").attr("checked",false);
	    	 }
	     });
		
		$(".ui-wrap ul.myNavList li.filterDrop span.caretSpan").click(function(e){
			$("ul.filteFile").show();
			e.stopPropagation();
			e.preventDefault();
		});
		$(".ui-wrap ul.myNavList li.filterDrop").mouseleave(function(e){
			$("ul.filteFile").hide();
		});
		
		function initCopy(){
			var vs = $(".copyFetchCode");	
			for(var i=0;i<vs.length;i++){
				var v = vs[i];
				var id = $(v).attr("id");
				var client = new ZeroClipboard( document.getElementById(id), {
					  moviePath: "${contextPath}/scripts/zeroclipboard/ZeroClipboard-1.3.5.swf"
					} );

					client.on( "load", function(client) {

					  client.on( "complete", function(client, args) {
						  showMsgAndAutoHide("分享链接已复制到剪贴板", "success",2000);
					    
					  } );
					} );
					client.on("noFlash", function (client) {
						vs.hide();
					});
			}
		};
		initCopy();
		$(".files-item").live("mouseover",function(){
			$("span.file-commands").hide();
			$(this).find("span.file-commands").show();
		}).live("mouseout",function(){
			$(this).find("span.file-commands").hide();
		});
		
	});


</script>

