<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<style type="text/css">
<!--
.pagination{margin:0 10px;}
.pagination ul li a, .pagination ul li span {
float: left;
padding: 4px 10px;
line-height: 18px;
margin:0px;
text-decoration: none;
background-color: #fff;
border: 1px solid #ddd;
}
#imageModal hr{margin:10px 0;}
#imageList{ list-style:none;}
#imageList li.span2 { width:100px; margin:10px 15px;}
#imageList li.span2 a{height: 94px;width:94px;display: table-cell;vertical-align:middle;}
#imageList li.span2 a img,#uploadImage li.span2 img{vertical-align:middle; max-height:94px;max-width:94px;}
#imageList li.span2 a.selected{ border-width:3px; border-color:#4C8FFD }
#imageList li.span2 p { text-align:center;overflow:hidden;text-overflow: ellipsis;height:18px; white-space:nowrap; }
#imageList li.span2 a.folder { }
#imageList hr{ margin:0px; }

#imageSelectedList li.span2 { width:70px;margin:10px;}
#imageSelectedList li.span2 a.thumbnail{height: 61px;width:61px;border:none;display: table-cell;vertical-align:middle;}
#imageSelectedList li.span2 a.thumbnail img{vertical-align:middle; max-height:61px;max-width:61px;}
#imageSelectedList li.span2 a.lightDel{margin:0px;position:relative;float:right; top:0; left:3px; }
.scollHeight{height:100px}
.qq-upload-list {text-align: left;}
.qq-upload-list li.alert{margin-bottom:2px;}
.alert-error .qq-upload-failed-text {display: inline;}
#uploadImage li.span2 { width:100px; margin:10px;}
#uploadImage li.span2 a{height: 94px;width:94px;display: table-cell;vertical-align:middle;cursor:default;}
#uploadImage a.thumbnail:hover, a.thumbnail:focus{border-color:#fff;}

#folderBrowser{margin:0;padding:0 15px;border:1px #ddd solid;background-color:#F5F5F5;display:none; position: absolute;z-index:99999;}
-->
</style>

<div id="imageModal" style="width:720px;top:3%;margin-left:-360px;" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="imageModalLabel" aria-hidden="true">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
    <h3><span id="imageModalLabel">插入图片</span> &nbsp; &nbsp; <span id="imageModalMsg" style="display:none;padding:auto 0;font-size:12px;font-weight:normal;"></span></h3>
  </div>
  <div class="modal-body">
	<div class="tabbable">
		<ul class="nav nav-tabs">
    		<li class="active"><a href="#tab1" data-toggle="tab">文档库图片</a></li>
    		<vwb:CLBCanUse><li><a href="#tab2" data-toggle="tab">本地上传</a></li> </vwb:CLBCanUse>
  		</ul>
  		<div style="clear:both;"> </div>
	  	<div class="tab-content">
	    	<div class="tab-pane active" id="tab1">
	    	
	    		<div><input id="imageModalKeyword" style="width:400px;margin:10px 3px;" type="text" placeholder="搜索图片" /> </div>
	    		<ul id="navBarImage" style="background:none;padding:0 0 5px 2px;" class="breadcrumb"></ul>
	      		<ul id="imageList" class="thumbnails"></ul>
				<div class="pagination"><ul id="pageBarImage"></ul></div>
				<hr/>
				
				<div style="overflow-x:hidden;overflow-y:auto;">
					<ul id="imageSelectedList" class="thumbnails"></ul>
				</div>
	    	</div>
 
	    	<div class="tab-pane" id="tab2">
                
                <div style="margin-top:15px;border:1px #ddd solid;">
                	<div style="padding:15px;background-color:#F5F5F5; overflow-x:hidden;"> 
                		<div  style="float:left;">
			     			选择保存目录：
				            <button id="folderSel" class="btn" value="0"> 全部文件 </button><button  id="folderDropdown" class="btn" ><span class="caret"></span></button>
	                     </div>
	                     <div style="float:right;margin-right:20px; width:8em">
	                     	
	                     	<div id="img-file-uploader">
	                     	 </div>
	                     </div>
	                     <div style="clear:both;"></div>
                	</div>
                	<div id="uploadImage" style="margin:10px;">
                		<div id="img-file-uploader-list">
                			<ul class="qq-upload-list" id="qq-upload-list" style="margin-top: 10px; text-align: center;"></ul>
		               		<ul class="thumbnails" style="width:100%; min-height:100px;max-height:410px;overflow-y: auto;">
		               		 </ul>
	               		 </div>
                	</div>
	               	
                </div>
	    	</div>

		</div>
	</div>
	
  </div>
  <div class="modal-footer">
  	<button id="imageModalOk" class="btn btn-primary">确定</button>
    <button class="btn" data-dismiss="modal" aria-hidden="true">关闭</button>
  </div>

</div>

<script type="text/javascript">
<!--

var ImageModal = {
	getInstance : function(){
		var _t = {};
		
		var pager = null;
		var path_rid = 0; //目录rid
		var uploader = null;
		var isLoadFolder = false;
		var keyword = "";
		
		_t.open = function(){
			$("#imageModal").modal();
			$("#imageModal .modal-body").css("max-height", $(window).height()*0.72 + "px");
			if(pager==null){
				init();
			}
			this.load();
		};
		
		_t.load = function(pageCurrent){
			loading();
			pageCurrent = pageCurrent || 1;
			var begin = pager == null ? 0 : pager.getStart(pageCurrent);
			
			var param = {"begin" : begin, "maxPageSize": 10, "queryType":"showFileByType", "type":"Picture"};
			if(path_rid != 0){
				$.extend(param,{"path":path_rid});
			}
			if(keyword){
				$.extend(param,{"keyWord":keyword});
			}
			
			//窗口关闭提示
			if(window.colsePageFlage){
				window.colsePageFlage.standardClose(); 
			}
			$.ajax({
				   type: "POST",
				   url: "<vwb:Link context='files' format='url' />?func=query",
				   data: param,
				   dataType: "json",
				   timeout:5000,
				   success: function(resp){
					   $("#imageList").empty();
					   
					   if(keyword){
						  buildSearchNav(keyword);
					   }else{
						   buildNav(resp.path);
					   }
					   
					   if(resp.children.length==0){
						   loadedNoRecord();
						   return;
					   }
					   renderRow(resp.children);
					   pager = Pager.getInstance($("#pageBarImage"), goPage, resp.total, 10);
					   pager.change(pageCurrent);
					   
					   imageSelected();
					   
					   loadedSuccess();
				   },
				   error:function(){
					   loadedError();
				   },
				   complete:function(){
					   if(window.colsePageFlage){
						   window.colsePageFlage.flage = false;
					   }
				   }
			});
			
		};
		
		_t.close = function(){
			$('#imageModal').modal('hide');
		};
		
		_t.getRecordById = function(id){
			var res = {};
			res.rid = id;
			return res;
		};
		_t.clickOk = function(ids){};
		_t.showMsgAuto = function(msg, type, timeout){
			type = type || "success", 
			timeout = timeout || 2000;
			var obj = $("#imageModalMsg");
			obj.removeClass().addClass("alert alert-" + type).html(msg).show(150);
			window.setTimeout(function(){obj.hide(150);}, timeout);
		}
		
		function init(){
			$("#imageModalOk").die("click").live("click",function(){
				var ids = new Array();
				//选择图片库
				$("#imageSelectedList li").each(function(){
					var rid = $(this).attr("rid");
					if(rid){
						ids.push(rid);
					}
				});
				
				//上传图片
				$("#uploadImage li").each(function(){
					var rid = $(this).attr("rid");
					if(rid){
						ids.push(rid);
					}
				});
				_t.clickOk(ids);
			});
			
			//搜索
			$('#imageModalKeyword').off().on('input propertychange',function(event) {
					keyword = $.trim($("#imageModalKeyword").val());
					_t.load();
	         });
			
			$("#imageList li a.folder, #navBarImage li a").die().live("click",function(){
				path_rid = $(this).prev("input").val();
				$("#imageModalKeyword").val("");
				keyword = "";
				_t.load();
			});
			
			$("#imageList a.thumbnail").die().live("click",function(){
				if($(this).hasClass("selected")){
					$(this).remove("selected");
					removeImage($(this).prev().val());
				}else{
					$(this).addClass("selected");
					addImage($(this).prev().val(), $(this).children("img").attr("src"));
				}
			});
			
			$("#imageSelectedList a.lightDel").die().live("click",function(){ removeImage($(this).parent("li").attr("rid"));});
			
			if(uploader==null){
				uploader = new qq.FileUploader({
			          element: document.getElementById('img-file-uploader'),
			          action: "<vwb:Link context='upload' format='url'/>?func=uploadFiles",
			          statisticAction: site.getTeamURL("statistics/upload"),
			          params: {parentId:0},
			          template: '<div class="qq-uploader span7">' +
			          			  '<pre class="qq-upload-drop-area span7" style="display:none"><span>Drop files here to upload</span></pre>' +
				                  '<div id="uploadImgBtn" class="qq-upload-button btn btn-primary" style="width: auto;"><i class="icon-upload icon-white"></i> 上传图片</div>' +
				                  '<span class="qq-drop-processing"><span> </span><span class="qq-drop-processing-spinner"></span></span>' +
					                '</div>',
					  listElement: document.getElementById("qq-upload-list"),
				      allowedExtensions: ["jpg","jpeg","png","gif","bmp"],
			          onComplete:function(id, fileName, data){
			        	  uploadSuccess(data);
			          },
			          messages: {
			              typeError: "只能上传  {extensions} 类型的图片."
			          },
			          classes: {
			              // used to get elements from templates
			              button: 'qq-upload-button',
			              drop: 'qq-upload-drop-area',
			              dropActive: 'qq-upload-drop-area-active',
			              list: 'qq-upload-list',
			                          
			              file: 'qq-upload-file',
			              spinner: 'qq-upload-spinner',
			              size: 'qq-upload-size',
			              cancel: 'qq-upload-cancel',
	
			              success: 'alert alert-success',
			              fail: 'alert alert-error'
			          },
			          showMessage: function(message){
				        	 _t.showMsgAuto(message,'error',5000);
				      },
				      debug: false
	
			        });
			}
			
			$("input[type=file]").attr("title","上传文件");
			$("#uploadBtn").die("click").live("click",function(){
				$("div#uploadImgBtn [type=file]").trigger("click");
			});
			
			function uploadSuccess(data){
				if(data.rid){
					data.timestamp = new Date().getTime();
					$("#img-file-uploader-list .qq-upload-list li.alert-success").fadeOut(2000);
					$("#uploadImage .thumbnails").append($("#imageUploadList-template").tmpl(data));
				}else{
					$("#img-file-uploader-list .qq-upload-list li.alert-error").fadeOut(5000);
				}
			}
			
			$("body").append("<div id=\"folderBrowser\"></div>");
			$("#folderDropdown").bind("click",function(){
				if(!isLoadFolder){
					$("#folderBrowser").jstree({
						"json_data" : {
							"ajax" : {
								"url" : "<vwb:Link context='teamHome' format='url' />/fileManager",
								"data" : function(n) {
									return {
										"rid" : (n.attr ? n.attr("rid").replace("node_", "") : 0),
										"func" : "list",
										"originalRid" : "${rid}",
									};
								},
							}
						},
						"plugins" : [ "themes", "json_data", "ui" ],
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
							var forlderRid = data.rslt.obj.attr("rid").replace("node_", "");
							var txt = data.rslt.obj.children("a").text();
							$("#folderSel").html(" " + txt + " ");
							
							uploader.setParams({parentRid:forlderRid});
							hideMenu();
					});
				}
				
				var os = $("#folderSel").offset();
				$("#folderBrowser").css({left:os.left + "px", top:os.top + $("#folderSel").outerHeight() + "px"}).slideDown("fast");
				$("body").bind("mousedown", onBodyDown);
				
				function onBodyDown(event) {
					if (!(event.target.id == "folderBrowser" || $(event.target).parents("#folderBrowser").length>0)) {
						hideMenu();
					}
				}
				function hideMenu() {
					$("#folderBrowser").fadeOut("fast");
					$("body").unbind("mousedown", onBodyDown);
				}
				isLoadFolder = true;
			});
			
			$("#imageModal").on('hide', function () {
				clear();
				if(typeof(_t.hideCallback) == "function"){
					_t.hideCallback();
				}
			});
		};
		
		
		function addImage(rid, src){
			var data = {"rid":rid,"src":src,};
			$("#imageSelectedList").append($("#imageSelectedList-template").tmpl(data));
			
			$("#imageSelectedList").parent("div").addClass("scollHeight");
		}
		function removeImage(rid){
			$("#imageList input[value='"+ rid +"']").next().removeClass("selected");
			$("#imageSelectedList li[rid='" + rid + "']").remove();
		}
		
		function imageSelected(){
			$("#imageSelectedList li").each(function(){
				var rid = $(this).attr("rid");
				$("#imageList input[value='"+ rid +"']").next().addClass("selected");
			});
		};
		
		function goPage(event){ _t.load(event.data.page); };
		//渲染行
		function renderRow(rows){
			var newRow = $("#imageItem-template").tmpl(rows);
			$("#imageList").append(newRow);
		}
		
		//构建导航菜单
		function buildNav(pathData){
			$("#navBarImage").empty();
			if(pathData){
				$("#navBarImage").append($("<li><input type=\"hidden\" value=\"0\"><a href=\"javascript:void(0);\">所有图片</a> <span class=\"divider\">/</span></li>"));
				var p = $("#navBarImage-template").tmpl(pathData);
				$("#navBarImage").append(p);
			}else{
				$("#navBarImage").html("<li>所有图片</li>");
			}
			$("#navBarImage span:last").remove(".divider");
			$("#navBarImage span:last").unwrap();
			$("#navBarImage li:last").addClass("active");
		}
		//构建查询导航菜单
		function buildSearchNav(keyword){
			$("#navBarImage").empty();
			$("#navBarImage").append($("<li><input type=\"hidden\" value=\"0\"><a href=\"javascript:void(0);\">所有图片</a> <span class=\"divider\">/</span></li>"));
			$("#navBarImage").append($("<li style=\"color:red;\" class=\"active\">'" + keyword + "'</li>"));
		}
		
		//加载提示
		function loading(){
			$("#imageList").empty();
			$("#imageList").html("<li style=\"width:90%;text-align:center;\"><span class=\"label\">正在加载中...</span></li>").show();
		}
		function loadedSuccess(){ $("#imageList li span.label").parent("li").remove(); }
		function loadedError(){ $("#imageList").html("<li style=\"width:90%;text-align:center;\"><span class=\"label\">加载失败，请稍候再试.</span></li>").show(); }
		function loadedNoRecord(){ 
			$("#imageList").html("<li style=\"width:90%;text-align:center;\"><span class=\"label\">没有显示项.</span></li>").show();
			$("#pageBarImage").empty();
		}
		function clear(){
			$("#imageSelectedList").empty();
			$("#imageList a.thumbnail").removeClass("selected");
			$("#imageSelectedList").parent("div").removeClass("scollHeight");
			
			$("#uploadImage ul.thumbnails").empty();
			$("#folderSel").html(" 全部文件  ");
			$("#folderBrowser").empty();
			$("#imageModalKeyword").val("");
			pager = null;
			path_rid = 0; 
			//uploader = null;
			isLoadFolder = false;
			keyword = "";
		}
				
		return _t;
	}
};

//-->
</script>

<script type="text/x-jquery-tmpl" id="imageUploadList-template">
<li class="span2"  rid="{{= rid}}">
	<a href="#" onclick="void(0);return false;" class="thumbnail" >
		<img src="<vwb:Link context='download' format='url'/>{{= rid}}?type=doc&imageType=small&{{= timestamp}}"  />
	</a>
</li>
</script>
<script type="text/x-jquery-tmpl" id="imageSelectedList-template">
<li class="span2" rid="{{= rid}}">
	<a class="lightDel"></a><a href="javascript:vodi(0);" class="thumbnail"><img src="{{= src}}" alt="" /></a>
</li>
</script>
<script type="text/x-jquery-tmpl" id="navBarImage-template">
<li><input type="hidden" value="{{= rid}}" /><a href="javascript:void(0);"><span>{{= fileName}}</span></a> <span class="divider">/</span></li>
</script>
<script type="text/x-jquery-tmpl" id="imageItem-template">
<li class="span2">
	<input type="hidden" value="{{= rid}}" /> 
	
		{{if $data.itemType=='Folder'}}
			<a href="#" onclick="void(0);return false;" class="folder" title="{{= fileName}}">
				<img src="${contextPath}/images/folder1.png" />
			</a>
        {{else}}
			<a href="#" onclick="void(0);return false;" class="thumbnail" title="{{= fileName}}">
				<img src="<vwb:Link context='download' format='url'/>{{= rid}}?type=doc&imageType=small" />
			</a>
		{{/if}}
	
	<p>{{= fileName}}</p>
</li>
</script>

<script type="text/javascript" src="${contextPath}/scripts/jquery_tree/jquery.jstree.js"></script>
