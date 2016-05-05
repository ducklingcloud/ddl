<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/lion.css?v=${aoneVersion}" type="text/css" />
<fmt:setBundle basename="templates.default" />
<script type="text/javascript" src="${contextPath}/scripts/syntaxhighlighter/scripts/shCore.js"></script>
<script type="text/javascript" src="${contextPath}/scripts/syntaxhighlighter/scripts/shAutoloader.js"></script>
<script type="text/javascript">
$(document).ready(function(){
	
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

});

</script>
<c:set var="PDF_MAX_SIZE"  value="52428800" scope="request"/> 
<div id="opareteFileMessage"  class="alert alert-success" style="margin:8px;display: none;"> </div>
	<c:choose>
		<c:when test="${fileNotExist =='true' }">
			<div style="margin-top:80px; text-align:center;"><h3 style="font-size:24px; color:#999">文件不存在或已经被删除！</h3></div>
		</c:when>
		<c:when test="${fileExtend eq 'FILE'}">
			<div id="fileInfo">
			<c:choose>
				
			    <c:when test="${isPreview }">
					<div class="largeButtonHolder">
						<div id="viewerWrapper" style="z-index:100;width:100%; height:750px;"></div>
					</div>
			    </c:when>
			    <c:otherwise>
			    	<div class="container" style="background:#fff">
				        <table class="fileContainer" style="border:none;">
					 		<tr>
				        		<th><div class="fileIcon <vwb:FileExtend  fileName='${filename}'/>"></div></th>
				        		<td>
									<div class="largeButtonHolder">
										<p class="fileName"><c:out value="${filename}"/></p>
									<c:choose>
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
										<c:when test="${curVersion.size > PDF_MAX_SIZE && isPreview}">
											<p>Office系列文件只能预览小于 50M 的文件，请下载查看。</p>
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
														<p>暂不支持该文件类型的在线预览</p>
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
			    </c:otherwise>
			</c:choose>
			</div>
		</c:when>
		<c:when test="${fileExtend eq 'TEXT'}">
			<div id="codeMode">
				<pre id="directShowFile"><vwb:ShowPanFile path="${remotePath}" version="${version }"/></pre>
			</div>
		</c:when>
		<c:otherwise>
			<div id="photoInfo">
				<div class="photoContainer">
					<img src="${downloadURL}" />
				</div>
			</div>
		</c:otherwise>
	</c:choose>

	<div class="bedrock"></div>
<div class="ui-clear"></div>

<c:out value=""></c:out>
<c:set var="fileBarItemId" value="${resource.rid}" scope="request"></c:set>
<c:set var='deleteItemURL' scope='request' value=''></c:set>
<c:set var="fileBarBid" value="0" scope="request"></c:set>


<div id="fileBrowserModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="fileBrowserModalLabel" aria-hidden="true">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3 id="fileBrowserModalLabel">另存为</h3>
	</div>
	<div class="modal-body" style="height: 345px;">
		<div id="teamSelWrapper" style="display:none;">
			<select id="teamSel">
				<option value="${myTeamCode}" id="teamSel_${myTeamId }" <c:if test="${teamType eq 'myspace'}">selected="selected"</c:if>>个人空间</option>
				<vwb:TeamPreferences/>
				<c:forEach items="${myTeamList}" var="item">
					<option value="${item.name }" id="teamSel_${item.id }" <c:if test="${teamType eq item.name}">selected="selected"</c:if>><c:out value="${item.displayName}"/></option>
				</c:forEach>
			</select>
			<span class="ui-text-note" style="color:#999;">选择空间</span>
		</div>
		<div id="file_browser"></div>
	</div>
	<div class="modal-footer">
		<button id="newNodeBtn" class="btn text-left pull-left" ><i class="icon-folder-open"></i> 新建文件夹</button>
		<button class="btn btn-primary" id="moveToBtn">确定</button>
		<button class="btn" data-dismiss="modal" aria-hidden="true">取消</button>
	</div>
</div>

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
function showMsgAndAutoHide(msg, type,time){
	time=time||2000;
	showMsg(msg,type);
	hideMsg(time);
}



<%--------- file move/copy/delete start ----------%>
var original_rid = -1;
var file_operation = 'none';

$("#fileCopy").live("click", function(){
	$('#fileBrowserModalLabel').empty();
	$('#fileBrowserModalLabel').append("另存为");
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
			};
});

<%--------- file move/copy/delete end ----------%>

</script>
<script type="text/javascript" src="${contextPath}/scripts/jquery_tree/jquery.jstree.js"></script>

<script type="text/javascript">
<!--
function checkHtml5(){   
	return (typeof(Worker) !== "undefined") ? true : false;
}
var isIE9=function(){
	return navigator.userAgent.indexOf("MSIE 9.0")>0;
}

$(function(){
	<c:if test="${isPreview == 'true'}">
		if(checkHtml5()||isIE9()){
			<c:choose>
				 <c:when test="${pdfstatus == 'original_pdf'}">
				 	var url = "${contextPath}/pan/pdfpreview?path=${remotePath}&version=${version}";
			     </c:when>
			    <c:otherwise>
			    	var url = "${clbPreviewUrl}";
			    </c:otherwise>
		    </c:choose>
			$("#viewerWrapper").append("<iframe src=\"" + url +"\" height=\"100%\" width=\"100%\" scrolling=\"no\" ></iframe>");
		}else{
			$("#fileInfo").empty();
			$("#fileInfo").append("<table class=\"fileContainer\" style=\"border:none;\"><tbody><tr>" +
				"<th><div class=\"fileIcon <vwb:FileExtend  fileName='${filename}'/>\"></div></th>" +
				"<td><p class=\"fileNote\"></p><div class=\"largeButtonHolder\"><p class=\"fileName\">${filename}</p><p>团队文档库在线预览服务所支持的浏览器包括Chrome、IE9+、Firefox和Safari。请确认您使用了合适的浏览器并且没有使用IE兼容模式。</p>" +
					 "<a href=\"${downloadURL}\" class=\"largeButton extra\">下载<span class=\"ui-text-note\">(${sizeShort})</span></a></div></td>" +
			    "</tr></tbody></table>"
			);
		}
	</c:if>
	
	var wh=$(window).height();
	$("#viewerWrapper").css("height", wh - 10 + "px");
});


//-->
</script>


<script src="${contextPath}/scripts/bootstrap/js/bootstrap.min.js"></script>
