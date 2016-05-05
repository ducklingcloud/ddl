<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<div style="clear:both; padding:0 23px;">

<div id="navBarDiv" class="navBar" style="overflow:hidden;">
	<div class="currentResource">
		<h2 class="currentResourceTitle"></h2>
	</div>
	<div id="backUpDir" style="display: none; float:left; font-family:Arial,'微软雅黑'";><a>返回上一级</a>&nbsp;|&nbsp;</div>
	<ol class="breadcrumb rnav" id="navBarOl" style="max-width:630px;margin:0; padding:0;"></ol>
</div>


<div class="fixMenuBar">
	<div id="opareteFileMessage" class="alert alert-success" style="display: none;z-index:9999"></div>
	<div class="toolBar">
	 	<input type='checkbox' name="checkAllFiles" id="checkAllFiles" class="showSelectedOperate" value=""/>
		<span class="selectedOperGroup" style="display:none">
			<span id="checkedMessage">&nbsp;已选中<span class="checkedNum"></span>项</span>	
			<a class="btn btn-small btn-primary selectedOper" id="downLoadSelected" title="下载" ><i class="icon-download-alt icon-white"></i>下载</a>
		</span>
	 	<div class="filterBoard">
	 		<ul id="viewSwitch" class="switch">
				<li id="showAsTable" class="chosen"><a title="列表显示"><span class="iconLynxTag icon-listView"></span></a></li>
				<li id="showAsGrid"><a title="缩略图显示"><span class="iconLynxTag icon-gridView"></span></a></li>
			</ul>
			<div class="btn-group" style="float:right;margin-right:10px;" id="sortDivId">
	 			<a class="btn filter dropdown-toggle" data-toggle="dropdown" role="button" id="sort">
	 				<span class="sortTitle">
	 				<c:choose>
	 					<c:when test="${sortType eq 'time' }">时间正序</c:when>
	 					<c:when test="${sortType eq 'title' }">标题A-Z</c:when>
	 					<c:when test="${sortType eq 'titleDesc' }">标题Z-A</c:when>
	 					<c:otherwise>时间倒序</c:otherwise>
	 				</c:choose>
	 				</span>
	 			<span class="caret"></span></a>
				<ul class="dropdown-menu " id="sortMenu">
				     <li><a tabindex="-1" href="javascript:void(0)" class="sortFiles" sortType="timeDesc"><span class="menu-item-icon"><b class="ico-radio <c:if test="${sortType eq 'timeDesc' || sortType eq '' }"> ico-radio-checked</c:if> "></b></span><span class="sortName">时间倒序</span></a></li>
				     <li><a tabindex="-1" href="javascript:void(0)" class="sortFiles" sortType="time"><span class="menu-item-icon"><b class="ico-radio <c:if test="${sortType eq 'time'}"> ico-radio-checked</c:if>"></b></span><span class="sortName">时间正序</span></a></li>
					 <li><a tabindex="-1" href="javascript:void(0)" class="sortFiles" sortType="title"><span class="menu-item-icon"><b class="ico-radio <c:if test="${sortType eq 'title'}"> ico-radio-checked</c:if>"></b></span><span class="sortName">标题A-Z</span></a></li>
					 <li><a tabindex="-1" href="javascript:void(0)" class="sortFiles" sortType="titleDesc"><span class="menu-item-icon"><b class="ico-radio <c:if test="${sortType eq 'titleDesc'}"> ico-radio-checked</c:if>"></b></span><span class="sortName">标题Z-A</span></a></li>
				</ul>
		    </div>
		    
	 	</div>
	</div>
	<div class="rlist-header tableHeader"  style="display:none">
		<div class="title-col col">  
			标题       
		</div>   
		<div class="creator-col col">    
			<span>修改人</span>   
		</div>        
		<div class="time-col col">
			<span>修改时间</span>
		</div>   
		<div class="version-col col">    
			<span>大小</span>   
		</div>        
	</div>
</div>

<div id="fileItemDisplay" >
	<ul id="resourceList" class="rlist-list readyHighLight1 asTight"> </ul>
	<p id="notice" class="NA large  readyHighLight1 loading rlist-msg-common"  style="float:left">正在载入...</p>

	<a id="load-more-items" class="rlist-msg-more largeButton dim" begin="0" style="float:left;display:none">更多结果</a>
	<div class="clear"></div>
</div>
		
		
</div>
<script type="text/javascript" src="${contextPath}/scripts/jquery/jquery.hashchange-1.3.js"></script>

<script type="text/x-jquery-tmpl" id="tmpl-listItem">
<li class='files-item element-data' >
    <div class="title-col col">
	<div class='oper' style='width:25px; width:40px\0;'>
		{{if $data.status!='unpublish'}}
		<input type='checkbox' class="showSelectedOperate" />
		{{else}}
		<div style="width:55px" rid='{{= rid}}'></div>
		{{/if}}
		<input type='hidden' class='rid' value='{{= rid}}'/>
		<input class='parentRid' value='{{= parentRid}}'type='hidden' />
	</div> 
	
       <span class="file-commands">
           	<a class="down-file" title="下载" style="float:left"></a>
		</span>

		<h2>
			<a class='fileName rlist-item' title='{{= fileName}}{{if $data.itemType=='DPage'}}.ddoc{{/if}}'>
				<span class='headImg {{= itemType}} {{= fileType}}'><input type='hidden' class='itemType' value='{{= itemType}}'></span><span class="fileNameSpan">{{= fileName}}{{if $data.itemType=='DPage'}}.ddoc{{/if}}</span>
			{{if $data.status=='unpublish'}}
            &nbsp;<font style="font-size:10px;color:#999">草稿</font>
            {{/if}}
            </a>
		</h2>
     
   </div>
		<div class='creator-col col'>
			<span>{{if $data.lastEditor }}{{= lastEditor}}{{else}}-{{/if}}</span>
			
		</div>
       <div class='time-col col'><span>{{= modofyTime}}</span></div>
		<div class='version-col col'>
			{{if $data.itemType != 'Folder'}}<span>{{= size}}</span>{{else}}<font>-</font>{{/if}}
		</div>
	<div class="clear"></div>
</li>
</script>

<script type="text/javascript">

$.template("tmpl-navBar","<li class='active'>&nbsp;>&nbsp;<a class='filePath'>{{= fileName}}</a><input type='hidden' class='rid' value='{{= rid}}'></li>");
$.template("tmpl-navBarUp","<li class='disabled'><a class='filePath'>返回上一级</a><input type='hidden' class='rid' value='{{= rid}}'>&nbsp;|&nbsp;</li>");

function PathUtils(){}
PathUtils.getArrayParam = function (array,param){
	for(var i=0;i<array.length;i++){
		if(array[i].key==param){
			return array[i].value;
		}
	}
	return null;
};
PathUtils.setLoadPathHash = function(type,curDirRid,allPath){
	var self = this;
	var array = self.getHashArray();
	var queryType = self.getArrayParam(array,'queryType');
	//
	var path = self.getArrayParam(array,"path");
	if(queryType&&!path){
		$.getJSON("${teamUrl}?func=getPath",{rid:curDirRid},function(data){
			var path = data.ridPath;
			self.changePath(path);
		});
		$(".tagDiv .chosen").removeClass("chosen");
		$(".myNavList .current").removeClass("current");
		$("#showAllFiles").parent("li").addClass("current");
		$("#addFolder").show();
		return;
	}
	if(path){
		var dir = path.split("/");
		for(var i =0;i<dir.length;i++){
			if(dir[i]==curDirRid){
				var tmp = '';
				for(var t=0;t<=i;t++){
					if(dir[t]!=''&&dir[t]){
						tmp=tmp+"/"+dir[t];
					}
				}
				self.changePath(tmp);
				return;
			}
		}
	}
	path = path?path:'';
	if(type=='in'){
		var dir = path.split("/");
		for(var i =0;i<dir.length;i++){
			if(dir[i]==curDirRid){
				var tmp = '';
				for(var t=0;t<=i;t++){
					if(dir[t]!=''&&dir[t]){
						tmp=tmp+"/"+dir[t];
					}
				}
				self.changePath(path);
				return;
			}
		}	
	}else if(type=='add'){
		path=path+"/"+curDirRid;
		self.changePath(path);
		return;
	}else if(type=='all'){
		if(allPath){
			var tmp ='';
			for(var p in allPath){
				tmp=tmp+'/'+p.rid;
			}
			self.changePath(tmp);
			return;
		}
	}
};

PathUtils.getHashArray = function(){
	var hash = location.hash;
	var array = new Array();
	if(hash){
		if(hash[0]=='#'){
			hash = hash.substring(1);
		}
		var hashs = hash.split(/&/);
		for(var i=0 ;i< hashs.length;i++){
			if(hashs[i]){
				var h = hashs[i].split("=");
				var o = new Object();
				o.key=h[0];
				o.value=decodeURIComponent(h[1]);
				array.push(o);
			}
		}
	}
	return array;
};
PathUtils.buildHash = function(array){
	var tmp = '';
	if(array.length>0){
		for(var i=0;i<array.length;i++){
			if(array[i]){
				if(i==0){
					tmp=array[i].key+"="+encodeURIComponent(array[i].value);
				}else{
					tmp=tmp+"&"+array[i].key+"="+encodeURIComponent(array[i].value);
				}
			}
		}
	}
	window.location.hash=tmp;
};
PathUtils.changePath=function(path){
	var self = this;
	var a = new Array();
	a.push(buildKeyValueObject("path",path));
	self.buildHash(a);
};


PathUtils.replaceArrayParam = function(array,keys,value){
	var f = false;
	for(var i=0;i<array.length;i++){
		if(array[i]){
			if(array[i].key==keys){
				array[i].value=value;
				f=true;
			}
		}
	}
	if(!f){
		var o = new Object();
		o.key = keys;
		o.value = value;
		array.push(o);
	}
};

/*base*/
function buildKeyValueObject(key,value){
	var param=new Object();
	param.key=key;
	param.value=value;
	return param;
}

var Rnav = {

	createNew: function(opt){
		//容器
		var _container = $(".rnav");
		var _t = {};
		
		_t.load = function(data, total){
			
			var upLi = "";
			
			if(!data){
				_container.html("<li class='allFiles'>所有文件</li>");
				return; //根目录
			}else{
				upLi = $("<li class='allFiles'><a>返回上一级</a>&nbsp;|&nbsp;</li>");
				_container.html("<li class='allFiles'><a>所有文件</a></li>");
			}
			
			var maxWidth=510;
			var maxFloderNameLength=16;
			var getLength=function(str,length){
				var cArr = str.match(/[^\x00-\xff]/ig);
				var cArrLenth=cArr == null ? 0 : cArr.length;
				if((str.length+cArrLenth)>length){
					var newstr=str.substr(0,str.length-4)+"..";
					cArr = newstr.match(/[^\x00-\xff]/ig);
					cArrLenth=cArr == null ? 0 : cArr.length;
					if((newstr.length+cArrLenth)>length){
						return getLength(newstr,length);
					}else{
						return newstr;
					}
				}
				return str;
			};
			$.each(data,function(index,item){
				var cArr = item.fileName.match(/[^\x00-\xff]/ig);
				var cArrLenth=cArr == null ? 0 : cArr.length;
				if((item.fileName.length+cArrLenth)>maxFloderNameLength){
					item.fileName=getLength(item.fileName,maxFloderNameLength);
				}
			});
			
			data.reverse();
			
			$.each(data,function(index,item){
				var tempLi=$.tmpl("tmpl-navBar", item);
				if(index == 1){
					upLi = $.tmpl("tmpl-navBarUp", item);
				}
				tempLi.insertAfter(_container.find("li:first"));
				var totalWidth=getNavWidth();
				//此处要多判断当前目录下级目录变换为“...”后长度是否超长 如果超长直接将当前目录缩为“...”28 为“...”的长度
				if(totalWidth>maxWidth||(index<data.length-1&&28+totalWidth>maxWidth)){
					tempLi.find("a").text("...");
					return false;
				}
			});
			
			_container.prepend(upLi);
			
			function getNavWidth(){
				var totalLiWidth=0;
				_container.find("li").each(function(index,item){
					totalLiWidth+=$(item).width();
				});
				return totalLiWidth;
			};
			_container.append("<li id='totalSize' size='"+total+"' ><span style='color:#999' title='"+total+"个文件/文件夹'>&nbsp;("+total+")</span></li>");
	
		};
		return _t;
	}

};


var Rlist = {

createNew: function(opt){
	var _t = {}; 
	_t.option = {
			viewMode:Rlist.TABLE,
			clickItem: function(item){
				
			},
			getThumbUrl:function(rid){
				//to implement
			},
			getThumbStatusUrl:function(rid){
				//to implement
			}
	};
	$.extend(_t.option,opt);
	
	//容器
	var _container = $(".rlist"), _header = $(".rlist-header"),_list = $(".rlist-list");
	//消息提示
	_msgMore=$(".rlist-msg-more"), _msgCommon=$(".rlist-msg-common");
	//模板
	var _tmplListItem = $("#tmpl-listItem");
	//记录起始号
	var beginNum = 0;
	
	var _viewMode = _t.option.viewMode;
	
	var _notice = {
			loading: function() {
				_msgMore.hide();
				_msgEmpty.hide();
				_msgCommon.addClass("loading large").text("正在载入...").show();
  			},
  			loadCompleted: function() {
  				_msgCommon.removeClass('loading').hide();
  				_msgMore.show();
  			},
  			more:function(){
  				_msgMore.show();
  				_msgCommon.addClass('large').hide();
  			},
  			noMore: function() {
  				_msgCommon.removeClass('loading');
  				_msgMore.hide();
  				setTimeout(function(){ _msgCommon.removeClass('large').text('该文件夹下没有文件').show();}, 500);
  			},
  			hideAll : function(){
  				_msgMore.hide();
  				_msgCommon.addClass('large').hide();
  			},
  			error : function(){
  				_msgCommon.removeClass('large loading').text('请求失败！可能由于以下原因导致此问题：未登录，会话过期或权限不够！').fadeIn();
  				_msgMore.hide();
  			}
	};
	
	_t.load = function(url, params, callback){
		var tokenKey = _createTokenKey();
		if(params==null||params==''){
			params = "tokenKey="+tokenKey;
		}else{
			params += "&tokenKey="+tokenKey;
		}
		params += '&fileType=NoPage&maxPageSize=1000';
		$.ajax({
			url : url, data : params, type : "post",dataType:"JSON",async:true,
			success : function(resp){
				if(resp.tokenKey!=tokenKey){
					return;
				}
				
				_notice.loadCompleted();
				_showList(resp.children);
				_t.markBeginNum(resp.nextBeginNum);

				if (resp.total == 0 || resp.loadedNum==0) {
					_notice.noMore();
	    	  	}else{
					if(resp.loadedNum<resp.size||resp.total<=resp.size){
						_notice.hideAll();
					}else{
						_notice.more();
					}
				}
				$("#checkAllFiles").attr("checked",false);
				
				callback(resp);
			},
			statusCode:{
				450:function(){
					alert('会话已过期,请重新登录');
				},
				403:function(){alert('您没有权限进行该操作');}
			},
			complete: function(){ }
		});
	};
	
	_t.switchView = function(viewMode){
		if(viewMode==Rlist.GRID){
			_viewMode = Rlist.GRID;
			_gridViewInit();
			$(".headImg.DFile").filter(":not(.imageTemp)").each(function(index,item){
				 if($(item).hasClass("bmp")||$(item).hasClass("jpg")||$(item).hasClass("png")||$(item).hasClass("gif")){
					 _gridViewThumb($(item));
				 }else{
					 $(item).addClass("thumb");
				 }
			 });
			$("li.files-item").each(function(index,item){
				_cutFileName($(item));
			 });
		}else{
			_viewMode = Rlist.TABLE;
			_tableViewInit();
			$(".headImg.DFile").each(function(index,item){
				 _backUpHeadImg($(item));
		 	});
			$("li.files-item").each(function(inxex,item){
				 $(item).find(".fileNameSpan").text( $(item).find("a.fileName").attr("title"));
			});
		};
	};
	
	_t.getViewMode = function(){
		return _viewMode;
	};

	_t.append = function(data){
		//todo
	};
	
	_t.markBeginNum = function(num){
		_msgMore.attr("begin",num);
	};
	
	function _showList(data){
		_list.html("");
		if(data.length==0){
			_header.hide();
			_list.hide();
		}else{
			_list.show();
		}
		_renderItem(data).appendTo(_list);
	}
	
	function _renderItem(data){
		var items=_tmplListItem.tmpl(data);
		if(_t.getViewMode()==Rlist.GRID){
			_gridView(items);
		}else{
			_tableViewInit();
		}
		return items;
	}
	
	function _gridView(items){
		viewMode = Rlist.GRID;
		_gridViewInit();
		items.filter("li.files-item").each(function(index,item){
			if($(item).find('.bmp,.jpg,.png,.gif').length>0){
				_gridViewThumb($(item).find('.bmp,.jpg,.png,.gif'));
			}else{
				$(item).find(".headImg").addClass("thumb");
			}
			_cutFileName(item);
		});
	}
	
	function _gridViewThumb(item){
		var dataTemp =$(item).parents("li.files-item").data('tmplItem').data;
		var thumbUrl = _t.option.getThumbUrl(dataTemp.rid);

		 var imgObj=$("<img src='"+ thumbUrl +"'>");
		 imgObj.error(function(){
			 _gridViewThumbReload(item);
		 });
		 
		 $(item).css("background","");
		 $(item).removeClass("loading");
		 $(item).find("img").remove();
		 $(item).addClass("imageTemp");
		 $(item).append(imgObj);
	}
	
	//图片加载错误时，重新加载
	function _gridViewThumbReload(item){
		var dataTemp =$(item).parents("li.files-item").data('tmplItem').data;
		var statusUrl=_t.option.getThumbStatusUrl(dataTemp.rid);
		var thumbUrl=_t.option.getThumbUrl(dataTemp.rid);
		 $(item).find("img").remove();
		 $.ajax({
			 url:statusUrl, data : {}, type : "post", dataType:"json",
			 success :function(data){
				 if(data.status=='ready'){
					 $(item).css("background","");
					 $(item).removeClass("loading");
					 $(item).append("<img src='"+thumbUrl+"'>");
					 $(item).addClass("imageTemp");
				 }else if(data.status=='not_ready'){
					 $(item).attr('style','');
					 $(item).addClass("loading");
					 $(item).find("img").remove();
					 $(item).addClass("imageTemp");
					 window.setTimeout(function(){
						 _gridViewThumbReload(item);
					 },2000);
				 }else{
					 $(item).removeClass("loading");
					 $(item).attr('style','');
					 $(item).addClass("thumb");
				 }
			 },
			 error:function(){
				 $(item).addClass("thumb");
			 },
			 statusCode:{
				 403:function(){alert('您没有权限进行该操作');}
			 }
		 });
	}
	
	function _cutFileName(li){
		var obj=$(li).find(".fileNameSpan");
		var fileTitleObj=$(li).find(".headImg");
		var fileName=obj.text();
		if(fileTitleObj.hasClass("Folder")){
			fileName=spliceStr(fileName,Rlist.FILE_NAME_MAX_LENGTH);
		}else{
			fileName=spliceFileName(fileName,Rlist.FILE_NAME_MAX_LENGTH);
		}
		obj.text(fileName);
	}
	
	function _gridViewInit(){
		_list.removeClass("asTight");
		_list.addClass("asTable");
		_header.hide();
	}
	
	function _tableViewInit(){
		_list.removeClass("asTable");
		_list.addClass("asTight");
		_header.show();
	}
	
	function _backUpHeadImg(item){
		 $(item).removeClass("imageTemp");
		 $(item).removeClass("thumb");
		 $(item).attr('style','');
		 $(item).find("img").remove();
	}

	function _createTokenKey(){ return new Date().getTime().toString();}
	
	function _getItemData(item){
		item = _getItem(item);
		return $(item).data('tmplItem').data;
	}
	function _getItem(item){
		return $(item).parents("li.files-item");
	}
	
	function spliceStr(str,length){
		if(getStrLength(str)>length){
			//减3是为“...”预留位置
			length=length-3;
			while(getStrLength(str)>length){
				str=str.substr(0,str.length-1);
			}
			str=str+"...";
		}
		return str;
	}
	function getStrLength(str){
		var cArr = str.match(/[^\x00-\xff]/ig);
		var cArrLenth=cArr == null ? 0 : cArr.length;
		return str.length+cArrLenth;
	};
	function spliceFileName(str,length){
		var fileName="";
		var fileSuffix="";
		if(str.lastIndexOf('.')>0){
			fileName=str.substring(0,str.lastIndexOf('.'));
			fileSuffix=str.substring(str.lastIndexOf('.'),str.length);
			length=length-fileSuffix.length;
			fileName=spliceStr(fileName,length);
			return fileName+fileSuffix;
		}else{
			return spliceStr(fileName,length);
		}
	}
	
	//列表项单击事件
	$(".rlist-item").die().live('click',function(){
		var item = _getItemData($(this));
		_t.option.clickItem(item);
	});
	
	return _t;
},

GRID:"grid",
TABLE:"table",

FILE_NAME_MAX_LENGTH:18
};

var SHARE_URL = "${shareUrl}";
$(function(){
	var listUrl = SHARE_URL+"?func=query";
	var opt = {
		getThumbUrl: function(rid){return SHARE_URL+"?func=download&rid="+rid+"&imageType=fixSmall"; },
		getThumbStatusUrl: function(rid){return  SHARE_URL+"?func=getImageStatus&rid="+rid+"&type=small";},
		clickItem: function(data){
			if(data.itemType=="Folder"){
				PathUtils.setLoadPathHash('add',data.rid,null);
			}else{
				window.open(SHARE_URL+"?rid="+data.rid);
			}
		}
	};
	
	var rl = Rlist.createNew(opt);
	var hashData = location.hash.replace(/^#/,'');
	var param = "";
	if(hashData&&hashData!=''){
		param = hashData;
	}else{
		param ="first=first";
	}
	
	var rnav = Rnav.createNew();
	rl.load(listUrl,param,function(resp){
		rnav.load(resp.path, resp.total);
	});
	
	$("#showAsTable").click(function(){
		if(!$(this).hasClass("chosen")){
			rl.switchView(Rlist.TABLE);
			$(this).addClass("chosen");
			$("#showAsGrid").removeClass("chosen");
		}
	});
	
	$("#showAsGrid").click(function(){
		if(!$(this).hasClass("chosen")){
			rl.switchView(Rlist.GRID);
			$(this).addClass("chosen");
			$("#showAsTable").removeClass("chosen");
		}
	});
	
	function hashChangeF(){
		var param = location.hash.replace(/^#/,'');
		rl.load(listUrl, param,function(resp){
			rnav.load(resp.path, resp.total);
		});
	}
	$(window).bind('hashchange',hashChangeF);

	$(".sortFiles").live("click",function(){
    	var sortName= $(this).find("span.sortName").text();
    	$(".sortTitle").text(sortName);
    	var sortType=$(this).attr("sortType");
    	var arr=PathUtils.getHashArray();
    	PathUtils.replaceArrayParam(arr,"sortType",sortType);
    	PathUtils.buildHash(arr);
     });
	
});

$(document).ready(function(){
	$(".files-item").live("mouseover",function(){
		$("span.file-commands").hide();
		$(this).find("span.file-commands").show();
	}).live("mouseout",function(){
		$(this).find("span.file-commands").hide();
	});
	
	$('a.filePath').die().live('click',function(){
		var result = $(this).parents('li');
		var data = $(result).data('tmplItem').data;
		PathUtils.setLoadPathHash('in',data.rid,null);
	});
	
	$('li.allFiles').die().live('click',function(){
		location.hash='';
	});
	$('div#backUpDir').die().live('click',function(){
		var param = location.hash.replace(/^#/,'');
		var index = param.indexOf("path");
		param = param.substring(index);
		index = param.indexOf("&");
		if(index>0){
			param = param.substring(0, index);
		}
		
		var h = param.split("=");
		var value=decodeURIComponent(h[1]);
		index=value.lastIndexOf("/");
		if(index<=0){
			location.hash='';
		}else{
			var hash = "path="+encodeURIComponent(value.substring(0, index));
			location.hash=hash;
		}
	});
	
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
	
	$("a.down-file").die().live('click',function(){
		var dataTemp =$(this).parents("li.files-item").data('tmplItem').data;
		if(dataTemp.itemType=='DFile'){
			window.open(SHARE_URL+"?func=download&rid="+dataTemp.rid);
		}else if(dataTemp.itemType=='Folder'){
			window.open(SHARE_URL+"?func=downloads&rids="+dataTemp.rid);
		}
	});
	
	$("#downLoadSelected").die().live('click',function(){
		var checked = $(".showSelectedOperate:checked:not(#checkAllFiles)");
		downloadMuti(checked);
	});
	
	$(".js-downloadBtn").die().live('click',function(){
		var checked = $(".showSelectedOperate:checked:not(#checkAllFiles)");
		if(checked.length==0){
			showMsgAndAutoHide("先选择一下你要下载的文件.","block");
			return;
		}
		downloadMuti(checked);
	});
	
	function downloadMuti(checked){
		var rids = "";
		for(var i=0;i<checked.length;i++){
			var dataTemp =$(checked[i]).parents("li.files-item").data('tmplItem').data;
			rids+="&rids="+dataTemp.rid;
		}
		window.open(SHARE_URL+"?func=downloads"+rids);
	}
	
});

</script>
