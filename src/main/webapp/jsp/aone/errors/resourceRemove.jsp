<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link href="${contextPath}/jsp/aone/css/error.css" rel="stylesheet" type="text/css"/>
<link href="${contextPath}/scripts/bootstrap/css/bootstrap.min.css" rel="stylesheet" media="screen"/>
<link href="${contextPath}/scripts/bootstrap/css/bootstrap-responsive.min.css" rel="stylesheet"/>
<link href="${contextPath}/scripts/bootstrap/css/todc-bootstrap.css" rel="stylesheet"/>
<title>${resource.title}已被删除</title>
</head>
<body>

<div class="ui-wrap">
	<input type='hidden' id='tid' value='${resource.tid }'/>
	<div class="error-center">
		<c:choose>
			<c:when test="${resource.itemType eq 'DFile' }">
				<c:set var="resourceName" value="文件"></c:set>
			</c:when>
			<c:when test="${exception.resource.itemType eq 'DPage' }">
				<c:set var="resourceName" value="协作文档"></c:set>
			</c:when>
			<c:when test="${exception.resource.itemType eq 'Folder' }">
				<c:set var="resourceName" value="文件夹"></c:set>
			</c:when>
			<c:otherwise>
				<c:set var="resourceName" value="资源"></c:set>
			</c:otherwise>
		</c:choose>
	
		<h3>对不起，您请求的${resourceName}已被删除！</h3>
		<hr/>
		<c:choose>
			<c:when test="${recoverFlag}">
				<p style="line-height: 1.7em;font-size: 13px;color: #333">我们为您做了备份，<a class="recoverFile">
					<input type="hidden" id="recoverRid" name="rid" value="${resource.rid }"/><input id="recoverType" type="hidden" name="itemType" value="${resource.itemType }"/>点此恢复该${resourceName }。
						</a></p>
				<p style="line-height: 1.7em;font-size: 13px;color: #333">已删除的页面不会出现在团队空间里。恢复后即可立即浏览。</p>
			</c:when>
			<c:otherwise>
				<a href="<vwb:Link context='teamHome' format='url'/>">跳转到团队首页</a>
			</c:otherwise>
		</c:choose>
	</div>
	
	<div id="fileBrowserModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="fileBrowserModalLabel" aria-hidden="true">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3 id="fileBrowserModalLabel">恢复${resourceName}</h3>
		</div>
		<div class="modal-body" style="height: 345px;">
			<div id="teamSelWrapper" style="display:none;">
			</div>
			<div id="file_browser"></div>
		</div>
		<div class="modal-footer">
			<button id="newNodeBtn" class="btn text-left pull-left" ><i class="icon-folder-open"></i> 新建文件夹</button>
			<button class="btn btn-primary" id="moveToBtn">确定</button>
			<button class="btn" data-dismiss="modal" aria-hidden="true">取消</button>
		</div>
	</div>
</div>
<script type="text/javascript" src="${contextPath}/scripts/jquery_tree/jquery.jstree.js"></script>
<script src="${contextPath}/scripts/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript">
$(document).ready(function(){
	$(".recoverFile").live('click',function(){
		var aa= window.confirm("您确定要恢复此${resourceName }吗？\n恢复后，团队的其他成员可以看到该${resourceName }");
		if(aa){
			var rid = $(this).find("input[name='rid']").val();
			var url = site.getURL("teamHome");
			var query = {"func":"validateParent","rid":rid};
			$.ajax({
				url:url,
				data:query,
				type:'post',
				dataType:'json',
				success:function(data){
					if(data.status){
						if(data.haveParent){
							recoverResource(data.parentRid);					
						}else{
							showTree();
						}
					}else{
						alert(data.message);
					}
				}
			});
		}
	});
	function recoverResource(parentRid){
		var rid = $("#recoverRid").val();
		var url = site.getURL("teamHome");
		var itemType = $("#recoverType").val();
		var params ={"func":"recoverResource","rid":rid,"itemType":itemType,"parentRid":parentRid};
		$.ajax({
			url:url,
			type:'post',
			data:params,
			dataType:'json',
			success:function(data){
				if(data.status){
					window.location.href=data.redirectURL;
				}else{
					alert(data.message);				
				}
			}
		});
	}
	
	function showTree(){
		$("#fileBrowserModal").modal();
		loadBrowserTree('${teamCode}');
	}
	
	//------------------------------目录树-----------------------------
	var original_rid = -1;
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
	function getSelectedTid(){
		return $("#tid").val();
	}
	$("#moveToBtn").live('click', function(){
		$('#fileBrowserModal').modal('hide');
		if( target_rid==-1 ){
			return ;
		}
		recoverResource(target_rid);
});
	
});

</script>
</body>
</html>