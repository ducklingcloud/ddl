<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/lion.css?v=${aoneVersion}" type="text/css" />
<fmt:setBundle basename="templates.default" />

<div id="opareteFileMessage"  class="alert alert-success" style="margin:25px 8px 8px;;display: none;"> </div>

<div id="content-major" style="width:100%;margin-top:35px;padding-bottom:20px;">
	<div id="content-title">
		<input type="hidden" value="${uid }" id="currentUid"/>

		<h1 class="fileName" style="font-size:22px; margin-bottom:0px;">
			<div class="title-right">
				<a class="btn btn-success js-downloadBtn" 
					<c:choose>
						<c:when test="${resource.folder == true}">
							href="javascript:void(0);"
						</c:when>
						<c:otherwise>
							href="${downloadURL}"
						</c:otherwise>
					</c:choose>
				style="color:#fff;" >
					<i class="icon-download-alt icon-white"></i> <spring:message code="ddl.download" />  <c:if test="${resource.folder == false}"><span style="font-size:12px;">(${sizeShort})</span></c:if>
				</a>
				<c:if test="${resource.folder == false}">
					<vwb:UserCheck status="authenticated">
						<a class="btn btn-info" id="fileCopy" style="color:#fff;"><spring:message code="ddl.sharing.save" /></a> &nbsp;
					</vwb:UserCheck>
				</c:if>
				<div id="qrContainer" style="display:inline;"><a id="js-qrCode" class="btn" data-placement="bottom"><i class="icon-qrcode"></i> </a>
				</div>
			</div>
			<div class="title-left" style="margin-right:90px">
				<div class="${resource.itemType} headImg40 ${resource.fileType}"></div>
				<span id="pageTitle" rid="${resource.rid}" parentId="${resource.bid}"><c:out value="${resource.title}"/></span>
				<div id="version">
					<spring:message code="ddl.sharing.sharer" />：<span>${shareUserName}</span> &nbsp;|&nbsp;
					<spring:message code="ddl.sharing.time" />： <fmt:formatDate value="${shareResource.createTime}" type="both" dateStyle="medium" />
					<c:if test="${! empty sizeShort}">&nbsp; |&nbsp; <spring:message code="ddl.sharing.size" />： ${sizeShort}</c:if>
				</div>
			</div>
		</h1>
	</div>
	<c:choose>
		<c:when test="${resource.folder == true}">
			<jsp:include page="shareFolder.jsp" />
		</c:when>
		<c:otherwise>
			<vwb:FileShow rid="${resource.rid}" version="${version}" ridCode="${ridCode}"/>
		</c:otherwise>
	</c:choose>
</div>

<div class="bedrock"></div>

<c:set var="fileBarItemId" value="${resource.rid}" scope="request"></c:set>
<c:set var='deleteItemURL' scope='request' value=''></c:set>
<c:set var="fileBarBid" value="0" scope="request"></c:set>


<div id="fileBrowserModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="fileBrowserModalLabel" aria-hidden="true">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3 id="fileBrowserModalLabel"><spring:message code="ddl.saveTo" /></h3>
	</div>
	<div class="modal-body" style="height: 345px;">
		<div id="teamSelWrapper" style="display:none;">
			<select id="teamSel">
				<option value="${myTeamCode}" id="teamSel_${myTeamId }" <c:if test="${teamType eq 'myspace'}">selected="selected"</c:if>><spring:message code="ddl.personalSpace" /></option>
				<vwb:TeamPreferences/>
				<c:forEach items="${myTeamList}" var="item">
					<c:if test="${teamAclMap[fn:trim(item.id)] ne 'view'}">   
						<option value="${item.name }" id="teamSel_${item.id }" <c:if test="${teamType eq item.name}">selected="selected"</c:if>><c:out value="${item.displayName}"/></option>
					</c:if>
				</c:forEach>
			</select>
			<span class="ui-text-note" style="color:#999;"><spring:message code="ddl.tip.t2" /></span>
		</div>
		<div id="file_browser"></div>
	</div>
	<div class="modal-footer">
		<button id="newNodeBtn" class="btn text-left pull-left" ><i class="icon-folder-open"></i> <spring:message code="ddl.newFolder" /></button>
		<button class="btn btn-primary" id="moveToBtn"><spring:message code="ddl.confirm" /></button>
		<button class="btn" data-dismiss="modal" aria-hidden="true"><spring:message code="ddl.cancel" /></button>
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
	$('#fileBrowserModalLabel').append('<spring:message code="ddl.saveTo" />');
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
		var fileName = '<spring:message code="ddl.newFolder" />';
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
			alert("<spring:message code='ddl.tip.t1' />");
			return;
		}
		var d = new Object();
		var opType = span.find('input.opType').val();

		d.fileName=fileName;
		d.rid=span.find('input.rid').val();
		d.parentRid=span.find('input.parentId').val();
		d.func=opType;
		d.tid = getSelectedTid();
		var opUrl="${contentPath}/${teamCode}/list";
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
				450:function(){alert('<spring:message code="ddl.tip.t3" />');},
				403:function(){alert("<spring:message code='ddl.tip.t4' />");}
			},
			error: function(){
				alert("<spring:message code='ddl.tip.t5' />");
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
					$("#opareteFileMessage").html("<spring:message code='ddl.tip.t6' />");
				}else if(file_operation=="copy"){
					$("#opareteFileMessage").html("<spring:message code='ddl.tip.t7' />");
				}
				$("#opareteFileMessage").show();
				window.setTimeout(function(){
					$("#opareteFileMessage").hide(150);
				}, 1500);
				return;
			}
			
			if(file_operation == 'move'){
				$("#opareteFileMessage").removeClass();
				$("#opareteFileMessage").addClass("alert alert-block");
				$("#opareteFileMessage").html('<spring:message code="ddl.tip.t8" />');
				$("#opareteFileMessage").show();
				$.ajax({
				   type: "POST",
				   url: "${teamHome}/fileManager",
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
				$("#opareteFileMessage").html('<spring:message code="ddl.tip.t9" />');
				$("#opareteFileMessage").show();
				$.ajax({
				   type: "POST",
				   url: "${contextPath}/f/${ridCode}",
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
$(function(){
	// popover demo
	var imgUrl = "${contextPath}/f/qrcode?text=" + encodeURIComponent("${shareUrl}");
    var qrCode = $("#js-qrCode").popover({
    	html:true,
    	trigger:"",
    	content:" <img title=\"<spring:message code='ddl.qrcode' />\" style=\"width:120px;height:120px;\" src=\"" + imgUrl 
    		+"\" /><div style=\"font-size:12px;font-weight:normal;color:#999;margin-left:22px;line-height:18px;\"><spring:message code='ddl.sharing.scan' /></div>"
      });
    qrCode.click(function(e) { e.preventDefault(); });
	
	$("#qrContainer").hover(function(){qrCode.popover("show");},function(){ qrCode.popover("hide"); });
});
</script>
<script type="text/javascript" src="${contextPath}/scripts/jquery_tree/jquery.jstree.js"></script>
<script src="${contextPath}/scripts/bootstrap/js/bootstrap.min.js"></script>
<style type="text/css">
.popover-content {padding:0;cursor:pointer;}
#content-title h1.fileName div.title-right{ width:330px;  margin-right:65px;}
</style>