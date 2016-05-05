<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<style type="text/css">
<!--
.pagination{margin:0 10px;}
.pagination ul li a, .pagination ul li span {
padding: 4px 10px;
line-height: 18px;
margin:0px;
text-decoration: none;
background-color: #fff;
border: 1px solid #ddd;
}
#resList{margin-bottom:3px;}
#resList tr th i{ margin-top:3px;}
#resList td{padding:2px 4px;}
#resList td.msg{text-align:center;height:270px;vertical-align:middle;}
#resList td.msg:hover{background-color: #fff;}
#resList th{padding:5px 3px;}
#resourceModal hr{margin:10px 0;}
#resourceModal ul.tagList {max-height:53px;}
.ellipsis{ overflow:hidden;text-overflow: ellipsis; white-space:nowrap;
	display:inline-block; width:350px;
	vertical-align: bottom;
	margin-bottom: 3px;
}
-->
</style>
<div id="resourceModal" style="width:700px;top:4%;margin-left:-350px;" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="resourceModalLabel" aria-hidden="true">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
    <h3><span id="resourceModalLabel"></span> &nbsp; &nbsp; <span id="resourceModalMsg" style="display:none;font-size:12px;font-weight:normal;"></span></h3>  
  </div>
  <div class="modal-body" >
    <div>
    	<div style="float:left;padding-top:4px;">
    		<input id="resourceModalKeyword" style="width:350px;margin-bottom:5px;" type="text" placeholder="搜索要引用的文件" />
    	</div>
    	<div style="float:left;margin:0 15px;" class="btn-toolbar">
		  <div class="btn-group">
		  	<a id="res-modal-folder" class="btn  btn-primary" href="#" title="文件夹浏览" ><i class="icon-folder-open icon-white"></i></a>
		    <a id="res-modal-file" class="btn" title="文件浏览" href="#"><i class="icon-file"></i></a>
		  </div>
		</div>
		<vwb:CLBCanUse />
    	<div id="file-uploader-resourceModal" class="newBtnGroup" style="float:right; margin-right:0;<c:if test='${!clbCanUse }'>display:none;</c:if>">
			<a class="btn btn-large btn-primary upLoadFile" style=" margin-left:0" name="uploadFile" href="#uploadModal" data-toggle="modal" id="upLoadFile"><i class="icon-file icon-white"></i> 上传文件</a>
		</div>
    </div>
    <div style="clear:both;">
	    <ul id="navBar" style="background:none;padding:0 0 5px 2px;filter:none;" class="breadcrumb"></ul>
	</div>
    <div>
   		<table id="resList" class="table table-hover" >
             <thead>
               <tr class="toolBar">
                 <th style="width:30px"><input style="vertical-align:top;" type="checkbox" id="ridAll" /></th>
                 <th style="width:390px"><a id="titleSort" href="#">标题 </a></th>
                 <th style="width:90px">修改人</th>
                 <th style="width:150px"><a id="timeSort" href="#">时间 <i class="icon-arrow-down"></i></a></th>
               </tr>
             </thead>
             <tbody>
				<tr><td class="msg" colspan="4"></td></tr>
             </tbody>
           </table>
           <div class="pagination">
              <ul id="pageBar"></ul>
            </div>
     </div>
     <hr/>
     <div>
     	<ul id="resTagList" class="tagList"></ul>
     </div>
  </div>
  <div class="modal-footer">
  	<button id="resourceModalOk" class="btn btn-primary">确定</button>
    <button class="btn" data-dismiss="modal" aria-hidden="true">关闭</button>
  </div>
</div>

<div id="popupUploadRm" class="popupUpload" style="display:none;z-index:2000;">
	<div class="popupTitle">
		<p class="uploadTitle">上传文件</p>
		<span><i class="fillUploadPagCal icon-remove icon-white"></i></span>
		<span><i class="icon-minus icon-white"></i></span>
	</div>
	<div id="fileListDivRm" class="popupContent">
		<ul id="upload-listRm" style="list-style:none"></ul>
	</div>
</div>
<div id="alertModelRm" class="modal hide fade">
  <div class="modal-header">
    <button type="button" class="close closeUpload" data-dismiss="modal" aria-hidden="true">&times;</button>
    <h3>提示</h3>
  </div>
  <div class="modal-body">
	<p class="alertContent">列表中有未上传完成的文件，确定要放弃上传吗？</p>
  </div>
  <div class="modal-footer">
  	<a href="#" class="btn btn-primary" id="okAlertContentRm">确认</a>
    <a href="#" class="btn closeAlertModel"  data-dismiss="modal" aria-hidden="true">取消</a>
  </div>
</div>
<script type="text/javascript">
<!--
//分页封装
var Pager = {
	getInstance: function (container,pageFunction, recordTotal, pageSize, pageCurrent){
		var _t = {};
		_t.container = container;
		_t.pageCurrent = pageCurrent || 1;
		_t.recordTotal = recordTotal;
		_t.pageSize = pageSize || 10;
		_t.pageTotal = parseInt(_t.recordTotal%_t.pageSize == 0 ? _t.recordTotal/_t.pageSize : _t.recordTotal/_t.pageSize + 1);
		
		var getLink = function(page, style){
			if(style=="active"){
				return $("<li class=\"active\"><a href=\"javascript:void(0);\">"+page+"</a></li>");
			}else{
				return $("<li></li>").append($("<a href=\"javascript:void(0);\">"+page+"</a>").bind("click",{"page":page},pageFunction));
			}
		};
		var getOperateLink = function(text, page){
				return $("<li></li>").append($("<a href=\"javascript:void(0);\">"+text+"</a>").bind("click",{"page":page},pageFunction));
		};
		
		_t.getStart = function(pageCurrent){
			return (pageCurrent - 1) * _t.pageSize;
		};
		
		_t.change = function(pageCurrent){
			_t.pageCurrent = pageCurrent;
			_t.start = _t.getStart(pageCurrent);
			_t.end = _t.start + _t.pageSize - 1;
			if(_t.pageCurrent > _t.pageTotal){_t.pageCurrent = _t.pageTotal;}
			if(_t.pageCurrent == parseInt(_t.pageTotal && _t.recordTotal%_t.pageSize) != 0){
				_t.end = _t.recordTotal;
			}
			
			_t.container.empty();
			if(_t.recordTotal < _t.pageSize){
				return;
			}
			
			if(pageCurrent > 1){
				_t.container.append(getOperateLink("«",pageCurrent-1));
			}
			
			var pageStart = 1;
			var pageEnd = _t.pageTotal > 10 ? 10  : _t.pageTotal;
			if(pageCurrent>6){
				pageStart = pageCurrent-5;
				
				if( pageCurrent+4 <_t.pageTotal){
					pageEnd = pageCurrent+4;
				}else{
					pageEnd = _t.pageTotal;
					pageStart -= 4-(_t.pageTotal - pageCurrent);
					pageStart = pageStart < 0 ? 1 : pageStart;
				}
			}
			for(var i=pageStart; i<= pageEnd; i++){
				if(i==pageCurrent){
					_t.container.append(getLink(i,"active"));
				}else{
					_t.container.append(getLink(i));
				}
			}
			if(pageCurrent < _t.pageTotal){
				_t.container.append(getOperateLink("»",pageCurrent+1));
			}
		};
		return _t;
	},
	
};

var ResourceModal = {
	getInstance : function(title){
		var _t = {};
		
		_t.title = title || "选择相关文件";
		var pager = null;
		var path_rid = 0; //目录rid
		var keyword = "";
		var sortType = "timeDesc";
		var showType = "folder"; // or file
		
		_t.open = function(){
			$("#resourceModal").modal();
			$("#resourceModal .modal-body").css("max-height", $(window).height()*0.72 + "px");
			clear();
			this.load();
		};
		
		_t.load = function(pageCurrent){
			resourceLoading();
			pageCurrent = pageCurrent || 1;
			var begin = pager == null ? 0 : pager.getStart(pageCurrent);
			var param = {"begin" : begin, "maxPageSize": 10, "sortType": sortType};
			if(path_rid != 0){
				$.extend(param,{"path":path_rid});
			}
			if(keyword){
				$.extend(param,{"keyWord":keyword});
			}
			if(showType=="file"){
				$.extend(param,{queryType:"ExceptFolder"});
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
				   $("#resList tr:gt(1)").remove();
				   
				   if(showType==="file"){
					  buildSearchNav(keyword);
				   }else{
					  buildNav(resp.path, keyword);
				   }
				   
				   if(resp.children.length==0){
					   resourceNoRecord();
					   $("#ridAll").hide();
					   return;
				   }
				   renderRow(resp.children);
				   pager = Pager.getInstance($("#pageBar"), goPage, resp.total, 10);
				   pager.change(pageCurrent);
				   
				   tagListChecked();
				   
				   resourceLoadedSuccess();
			   },
			   error:function(){
				   resourceLoadedError();
			   },
			   complete:function(){
				   if(window.colsePageFlage){
					   window.colsePageFlage.flage = false;
				   }
			   }
			});
		};
		
		_t.close = function(){
			$('#resourceModal').modal('hide');
		};
		
		_t.getRecordById = function(id){
			var res = {};
			res.rid = id;
			var a = $("#resTagList li a[rid='"+id+"']");
			res.fileName = $.trim(a.parent("li").text());
			res.itemType = a.attr("itemType");
			res.fileType = a.attr("fileType");
			return res;
		};
		
		_t.clickOk = function(ids){};
		_t.showMsgAuto = function(msg, type, timeout){
			type = type || "success", 
			timeout = timeout || 2000;
			var obj = $("#resourceModalMsg");
			obj.removeClass().addClass("alert alert-" + type).html(msg).show(150);
			window.setTimeout(function(){obj.hide(150);}, timeout);
		}
		
		function init(){
			$("#resourceModalLabel").html(_t.title);
			
			$("#resourceModalOk").die().live("click", function(){
				var ids = new Array();
				$("#resTagList li a").each(function(){
					var rid = $(this).attr("rid");
					if(rid){
						ids.push(rid);
					}
				});
				_t.clickOk(ids);
			});
			
			//选择框
			$("#resList input[name='rid']").die().live("click",function(){
				if($(this).attr("checked")== "checked"){
					tagListAdd(wrapResource($(this).val(), $(this)));
					autoCheckboxAll();
				}else{
					tagListRemove($(this).val());
					autoCheckboxAll(true);
				}
				
			});
			//全选框
			$("#ridAll").bind("click",function(){
				if($(this).attr("checked")== "checked"){
					$("#resList input[name='rid']").each(function(){
						tagListAdd(wrapResource($(this).val(), $(this)));
						$(this).attr("checked", "checked");
					});
				}else{
					$("#resList input[name='rid']").each(function(){
						tagListRemove($(this).val());
					});
					$("#resList input[name='rid']").removeAttr("checked");
				}
			});
			//删除标签
			$("#resTagList a.lightDel").die().live("click",function(){
				var rid = $(this).attr("rid");
				tagListRemove(rid);
				$("#resList input[value='"+ rid +"']").removeAttr("checked");
			});
			
			//搜索
			$('#resourceModalKeyword').off().on('input propertychange',function(event) {
					keyword = $.trim($("#resourceModalKeyword").val());
					_t.load();
	         });
			
			//显示方式
			showTypeBind("folder");
			showTypeBind("file");
			function showTypeBind(type){
				$('#res-modal-' + type).die().live('click',function(event) {
					$(this).addClass("btn-primary"), 
					$(this).find("i").addClass("icon-white");
					var typeOther;
					if(type==="folder"){
						typeOther = "file"; <vwb:CLBCanUse>$("#file-uploader-resourceModal").show();</vwb:CLBCanUse>
					}else{
						typeOther = "folder";
						$("#file-uploader-resourceModal").hide();
					}
					$('#res-modal-' + typeOther).removeClass("btn-primary"),
					$('#res-modal-' + typeOther).find("i").removeClass("icon-white");
					showType=type;
					keyword = $.trim($("#resourceModalKeyword").val());
					_t.load();
					return false;
		         });
			}
			
			//排序
			sortBind("title");
			sortBind("time");
			function sortBind(key){
				$("#"+key+"Sort").die().live('click',function() {
					$("#resList th a i").remove();
		        	if(sortType==(key+"Desc")){
		        		sortType=key;
		        		$(this).append("<i class=\"icon-arrow-up\"></i>");
		        	}else{
		        		sortType=key + "Desc";
		        		$(this).append("<i class=\"icon-arrow-down\"></i>");
		        	}
		        	_t.load();
		         });
			}
			
			$("#resourceModal").on('hide', function () {
				clear();
				if(typeof(_t.hideCallback) == "function"){
					_t.hideCallback();
				}
			});
			
			
			<%---------------- upload begin ----------------------%>
			var upload_url = "<vwb:Link context='upload' format='url'/>?func=uploadFiles";
			var uploadedFiles = [];
			var index = 0;
			
		     var topUploader = new qq.FileUploader({
		         element: document.getElementById('file-uploader-resourceModal'),
		         template: '<div class="qq-uploader">' + 
		         '<div class="qq-upload-drop-area" style="display:none"><span>ree</span></div>' +
		         '<div id="uploader-resourceModalbtn" class="qq-upload-button"><i class="icon-file icon-white"></i> 上传文件</div><br/>'+ '</div>',
		         listElement: document.getElementById("upload-listRm"),
		         fileTemplate: '<li>' +
		         '<span class="qq-upload-file"></span>' +
		         '<span class="qq-upload-spinner"></span>' +
		         '<span class="qq-upload-size"></span>' +
		         '<a class="qq-upload-cancel" href="#">取消</a>' +
		         '<span class="qq-upload-failed-text">失败,您没有上传权限</span>' +
		    	 '</li>', 
		         action: upload_url,
		         statisticAction: site.getTeamURL("statistics/upload"),
		         button: document.getElementById("uploader-resourceModalbtn"),
		         params:{"parentRid":path_rid},
		         onComplete:function(id, fileName, data){
		         	uploadedFiles[index] = data;
		         	index ++;
		         	
		         	resourceLoadedSuccess();
		         },
		         showMessage: function(message){
		        	 _t.showMsgAuto(message,'error',5000);
		         },
		         debug: true,
		     });
		     
		     qq.extend(topUploader,{
					_addToList: function(id, fileName){
				        var item = qq.toElement(this._options.fileTemplate);                
				        item.qqFileId = id;

				        var fileElement = this._find(item, 'file');        
				        qq.setText(fileElement, this._formatFileName(fileName));
				        this._find(item, 'size').style.display = 'none';        

				        this._listElement.appendChild(item);
				        $("#popupUploadRm").show();
				        $("#fileListDivRm").show();
				    }, 
				    _onComplete: function(id, fileName, result){
				        qq.FileUploaderBasic.prototype._onComplete.apply(this, arguments);

				        // mark completed
				        var item = this._getItemByFileId(id);     
				        qq.remove(this._find(item, 'cancel'));
				        qq.remove(this._find(item, 'spinner'));
				        
				        if (result.success){
				            qq.addClass(item, this._classes.success);
				        	$(item).find("span.qq-upload-file").wrap("<a href='javascript:void(0)' class='viewFile' rid='"+result.resource.rid+"'></a>");
				        	
				        	$("#resList input[type=hidden][value="+result.resource.rid+"]").parent("td").parent("tr").remove();
				        	insertRow(result.resource);
				        } else {
				            qq.addClass(item, this._classes.fail);
				        }         
				        if(this._filesInProgress==0){
				      	  hideFilesList();
				        }
				    },
				    _CancelAll: function(){
				        var self = this,
				        list = this._listElement;
				        $(list).find("a.qq-upload-cancel").each(function(){
				        		   var item = this.parentNode;
					                self._handler.cancel(item.qqFileId);
					                qq.remove(item);
				        } );
				    }   ,
				    _setupDragDrop: function(){
				    	var self = this,
			            dropArea = this._find(this._element, 'drop');     
				    	dropArea.style.display = 'none';
				    }
				});
		     
		   	//文件夹点击
			$("#resList tr td a, #navBar li a").die().live("click",function(){
				path_rid = $(this).prev("input").val();
				$("#resourceModalKeyword").val("");
				topUploader.setParams({"parentRid":path_rid});
				keyword = "";
				_t.load();
			});
		     
		     function hideFilesList(){
		    	 $("#fileListDivRm").slideUp(2000);
		      	 var obj=$("i.icon-minus");
		      	 obj.removeClass("icon-minus");
		      	 obj.addClass(" icon-max");
		     }
		     
		     function showFilesList(){
		    	 $("#fileListDivRm").show();
		      	 var obj=$("i.icon-max");
		      	 obj.removeClass("icon-max");
		      	 obj.addClass("icon-minus");
		     }
		     function toggleFilesList(){
		    	 var bool = $("#fileListDivRm").is(":hidden");
		    	 console.log(bool+"----vera");
		    	 if(bool){
		    		 console.log("helloooo");
		    		 showFilesList();
		    	 }
		    	 else{
		    		 console.log("oooo");
		    		 
		    		 hideFilesList();
		    	 }
		     }
		     
		     $("#popupUploadRm i.icon-max").die().live("click",function(){
		    	 showFilesList();
		     });
		     
		     $("#popupUploadRm i.icon-minus").die().live("click",function(){
		    	 hideFilesList();
		     });
		     $("#popupUploadRm p.uploadTitle").die().live("click",function(){
		    	 toggleFilesList();
		     });
		     
		     $("#popupUploadRm i.fillUploadPagCal").die().live("click",function(){
		    	  if(topUploader._filesInProgress>0){
		    		  $("#alertModelRm").modal("show");
		    		  $("#okAlertContentRm").addClass("cancleAllOk");
			      	  return;
			        }
		    	  topUploader._filesInProgress=0;
			      $("#upload-listRm").html("");
			      $("#popupUploadRm").hide();
		     });
		     
		     $("#popupUploadRm .closeUpload").die().live("click",function(){
		    	  $("#alertModelRm").modal("hide");
		     });
		     
		     $("#okAlertContentRm").die().live("click",function(){
		   	  	$("#alertModelRm").modal("hide");
		    });
		     
		     $("#popupUploadRm .cancleAllOk").die().live("click",function(){
		    	 if(topUploader._filesInProgress>0){
			    	topUploader._CancelAll();
			    	 topUploader._filesInProgress=0; 
		    	 }
		    	 $("#upload-listRm").html("");
			      $("#popupUploadRm").hide();
			    
		     });
		     <%---------------- upload end ----------------------%>
			
		};
		
		function autoCheckboxAll(isRemove){
			if(isRemove){ $("#ridAll").removeAttr("checked","checked");return;}
			var l = $("#resList td input:checkbox").length;
			if(l==0){$("#ridAll").hide();return;}
			
			if(l == $("#resList td input:checkbox[checked='checked']").length){
				$("#ridAll").attr("checked","checked");
			}else{
				$("#ridAll").removeAttr("checked","checked");
			}
			
			$("#ridAll").show();
		}
		function tagListChecked(){
			$("#resTagList li a").each(function(){
				var rid = $(this).attr("rid");
				$("#resList input[value='"+ rid +"']").attr("checked","checked");
			});
			
			autoCheckboxAll();
		};
		function tagListAdd(res){
			if(!$("#resTagList li a[rid="+ res.rid + "]").attr("rid")){
				$("#resTagList").append("<li>"+ res.fileName + 
						"<a class=\"lightDel\" rid=\"" +  res.rid + "\" itemType=\"" +  
						res.itemType + "\" fileType=\"" +  res.fileType + "\" ></a> </li>");
			}
		};
		function tagListRemove(rid){
			$("#resTagList li a[rid="+ rid + "]").parent("li").remove();
		};
		function goPage(event){ _t.load(event.data.page); };
		function wrapResource(rid,checkedBox){
			var td = checkedBox.parent("td").next("td");
			var r = {};
			r.rid = rid;
			r.fileName = td.text();
			var cls = td.children("span.headImg").attr("class").split(" ");
			r.itemType = cls[1];
			r.fileType = cls.length==3 ? cls[2] : "";
			return r;
		}
		
		//渲染所有行
		function renderRow(rows){
			var newRow = $("#resource-item-template").tmpl(rows);
			$("#resList tr:last").after(newRow);
		}
		//在前面插入一行
		function insertRow(row){
			var newRow = $("#resource-item-template").tmpl(row);
			$("#resList tr:gt(0):first").after(newRow);
		}
		//构建导航菜单
		function buildNav(pathData, keword){
			$("#navBar").empty();
			if(pathData){
				$("#navBar").append($("<li><input type=\"hidden\" value=\"0\"><a href=\"javascript:void(0);\">所有文件</a> <span class=\"divider\">/</span></li>"));
				var p = $("#nav-bar-template").tmpl(pathData);
				$("#navBar").append(p);
			}else{
				var root = keyword ? "<li><input type=\"hidden\" value=\"0\"><a href=\"javascript:void(0);\">所有文件</a> <span class=\"divider\">/</span></li>" 
								   : "<li>所有文件</li>";
				$("#navBar").html(root);
			}
			if(keyword){
				$("#navBar").append($("<li style=\"color:red;\" class=\"active\">'" + keyword + "'</li>"));
			}else{
				$("#navBar span:last").remove(".divider");
				$("#navBar span:last").unwrap();
				$("#navBar li:last").addClass("active");
			}
		}
		//构建查询导航菜单
		function buildSearchNav(keyword){
			$("#navBar").empty();
			$("#navBar").append($("<li><input type=\"hidden\" value=\"0\"><a href=\"javascript:void(0);\">所有文件</a> <span class=\"divider\">/</span></li>"));
			if(keyword){
				$("#navBar").html("<li><input type=\"hidden\" value=\"0\"><a href=\"javascript:void(0);\">所有文件</a> <span class=\"divider\">/</span></li>");
				$("#navBar").append($("<li style=\"color:red;\" class=\"active\">'" + keyword + "'</li>"));
			}else{
				$("#navBar").html("<li class=\"active\">所有文件</li>");
			}
		}
		//加载提示
		function resourceLoading(){
			$("#resList tr:gt(1)").remove();
			$("#resList tr:gt(0):first td").html("<span class=\"label\">正在加载中...</span>").show();
			$("#resList tr:gt(0):first").show();
		}
		function resourceLoadedSuccess(){ $("#resList tr:gt(0):first").hide(); }
		function resourceLoadedError(){ 
			$("#resList tr:gt(0):first td").html("<span class=\"label\">加载失败，请稍候再试.</span>");
			$("#resList tr:gt(0):first").show();
		}
		function resourceNoRecord(){ 
			$("#resList tr:gt(0):first td").html("<span class=\"label\">没有显示项.</span>");
			$("#resList tr:gt(0):first").show();
			$("#pageBar").empty();
		}

		function clear(){
			$("#resTagList").empty();
			$("#resList input").removeAttr("checked");
			$("#resourceModalKeyword").val("");
			pager = null;
			path_rid = 0;
			keyword = "";
			sortType = "timeDesc";
			
			$("#upload-listRm").empty();
			$("#popupUploadRm").hide();
		}
		
		init();
		return _t;
	}
};

//-->
</script>
<script type="text/x-jquery-tmpl" id="nav-bar-template">
<li><input type="hidden" value="{{= rid}}" /><a href="javascript:void(0);"><span>{{= fileName}}</span></a> <span class="divider">/</span></li>
</script>
<script type="text/x-jquery-tmpl" id="resource-item-template">
	<tr>
		<td>{{if $data.itemType!='Folder' }}<input type="checkbox" name="rid" value="{{= rid}}" />{{/if}}</td>
		<td title="{{= fileName}}{{if $data.itemType=='DPage'}}.ddoc{{/if}}">
			<input type="hidden" value="{{= rid}}" />
			{{if $data.itemType=='Folder'}}
				<a style="text-decoration:none;" href="javascript:void(0)"><span style="margin-top:2px;" class="headImg {{= itemType}} {{= fileType}}"></span> <span class="ellipsis">{{= fileName}}</span><a>
			{{else}}
				<span style="margin-top:2px;" class="headImg {{= itemType}} {{= fileType}}"></span> <span class="ellipsis">{{= fileName}}{{if $data.itemType=='DPage'}}.ddoc{{/if}}</span>
			{{/if}}
		</td>
		<td>{{if $data.lastEditor }}{{= lastEditor}}{{else}}-{{/if}}</td>
		<td>{{= modofyTime}}</td>
	</tr>
</script>