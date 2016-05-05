var parentRidArr={};
$.template("navBarTemp","<li class='active'>&nbsp;>&nbsp;<a class='filePath'>{{= fileName}}</a><input type='hidden' class='rid' value='{{= rid}}'></li>");
$.template('ss',"<div class='files-item'><div class='oper'><input type='checkbox'><div class='iconLynxTag icon-checkStar ' title='标记为星标文件' rid='{{= rid}}'></div></div><input type='hidden' class='rid' value='{{= rid}}'><input class='parentRid' value='{{= parentRid}}'type='hidden' > </div>");
window.pageTokenKey= createKeyCode();
window.featureManual={
	init:function(){
		initLeftMenu();
		function initLeftMenu(){
			var queryType=getQueryType();			
			if(queryType!=""&&queryType!=null){
				$(".myNavList .current").removeClass("current");
				$(".myNavList a[queryType='"+queryType+"']").parent("li").addClass("current");
			}else{
				$("#showAllFiles").parent().addClass("current");
				$("#addFolder").show();
			}
		}
		$('#'+option.addFolder).die().live('click',function(){
			if(existedEditor()){ return; }
			
			var o = new Object();
			o.fileName="新建文件夹";
			o.rid = 0;
			o.parentRid=featureManual.currentDirRid;
			o.itemType="Folder";
			o.modofyTime="-";
			o.creator="-";
			var v = $("#"+option.display);
			if($(v).find("li.files-item").length==0){
				featureManual.notice_handler.hide();
			}
			var vs = $.tmpl('fileItemTemp', o).prependTo(v);
			$(vs).addClass("newFolderLi");
			v.show();
			$(".tableHeader").show();
			featureManual.editorFolderName(vs,"createFolder");
		});
		//banding文件名修改事件
		$('#resourceList .editFileName .updateFolder').die().live('click',function(){
			var span = $(this).parents('span.editFileName');
			var type = $(span).find("input.opType").val();
			var fileName = $(span).find('input.fileNameInput').val();
			if(!fileName||fileName==''){
				alert("文件名不能为空");
				return;
			}
			var d = new Object();
			if(featureManual.renderData(span).itemType=='DFile'){
				var f = featureManual.renderData(span).fileName;
				fileName = fileName +f.substring(f.lastIndexOf("."));
			}
			d.fileName=fileName;
			d.rid=featureManual.renderData(span).rid;
			d.parentRid=featureManual.renderData(span).parentRid;
			d.func=type;
			$.ajax({
				url:option.oprateUrl,
				data : d,
				type : "post",
				dataType:"json",
				success :function(data){
					if(!data.result){
						showMsg(data.message,"error");
						hideMsg(3000);
						featureManual.getFileItem(span).find('.editFileName .cancelFolder').trigger('click');
						return;
					}
					var d = $.tmpl('fileItemTemp', data.resource);
					var file = featureManual.getFileItem(span);
					$(file).replaceWith(d);
				},
				statusCode:{
					450:function(){alert('会话已过期,请重新登录');},
					403:function(){alert('您没有权限进行该操作');}
				}
			});
		});
		//文件名输入回车事件
		$("#resourceList input.fileNameInput").die().live('keypress',function(e){
			if(e.keyCode==13){
				$('.editFileName .updateFolder').trigger('click');
			}
		});
		//下载
		$("a.down-file").die().live('click',function(){
			var item = featureManual.renderData($(this));
			if(item.itemType=='DFile'){
				location.href = featureManual.option.teamHome+"/downloadResource/"+item.rid;
			}
		});
		//创建新页面
		$('a#createNewDDoc').die().live('click',function(){
			var url = featureManual.option.teamHome+"/quick";
			url =url+"?func=createPage&parentRid="+featureManual.currentDirRid;
			window.open(url);
		});
		//重命名
		$("li.rename_item").die().live('click',function(){
			if(existedEditor()){ return; }
			featureManual.editorFolderName(featureManual.getFileItem($(this)),"editFileName");
		});
		//重命名和新建文件夹取消
		$("#resourceList a.cancelFolder").die().live('click',function(){
			var i = featureManual.getFileItem($(this));
			var type = $(i).find("input.opType").val();
			if(type=='editFileName'){
				var item = featureManual.renderData($(this));
				var d = $.tmpl('fileItemTemp', item);
				$(i).replaceWith(d);
			}else if(type=='createFolder'){
				i.remove();
				if($("#resourceList").find("li.files-item").length==0){
					$('#notice').show();
					$("#resourceList").hide();
					$(".tableHeader").hide();
				}
			}
		});
		
		//是否已有编辑框
		function existedEditor(){
			var e = $("#"+option.display).find("span.editFileName");
			if(e.length>0){
				e.find('.fileNameInput').select();
				return true;
			}
			return false;
		}
	},
	editorFolderName :function(folder,type){
		var icon = "";
		if(type=="createFolder"){
			folder.find("input.showSelectedOperate").remove();
			folder.find("div.iconLynxTag").css("width","55px").removeClass("iconLynxTag icon-checkStar");
			icon = "headImg Folder";
		}else{
			icon = folder.find("span.headImg").attr("class");
		}
		
		var fileName = folder.data('tmplItem').data.fileName;
		if(folder.data('tmplItem').data.itemType=='DFile'){
			fileName=fileName.substring(0,fileName.lastIndexOf("."));
		}
		var editor = "<span class='editFileName'><span class='" + icon + "'></span>"  +
					 " 	<input class='fileNameInput' type='text' value='"+fileName+"'>" +
					 "	<a class='btn btn-mini btn-primary updateFolder'><i class='icon-ok icon-white'></i></a>" +
					 "	<a class='btn btn-mini cancelFolder'><i class='icon-remove'></i></a>"+
					 "	<input type='hidden' class='foldOriganlName' value='"+fileName+"'>" +
					 "	<input type='hidden' class='opType' value='"+type+"'>" +
					 "</span>";
		$(folder).find('h2 a.fileName').hide();
		$(folder).find('h2').append(editor);
		$(folder).find('.fileNameInput').select();
	},
	notice_handler:{
  			loading: function() {
  				$('#load-more-items').hide();
  				$('#no-result-helper').attr('style','display:none');
  				$("#notice").addClass("loading");
  				$('#notice').addClass('large').text('正在载入...').show();
  			},
  			noMatch: function() {
  				$('#notice').removeClass('loading');
  				var keyWord=getKeyWord();
  				if(keyWord){
  					$('#load-more-items').hide();
  					$('#notice').addClass('large').text('未搜索到相关文档').show();
  					return;
  				}
  				var queryType=getQueryType();	
  				$('#load-more-items').hide();
  				if(queryType=='myStarFiles'){
  					$('#notice').addClass('large').text('当前没有星标文档').show();
  				}else if(queryType=='myCreate'){
  					$('#notice').addClass('large').text('您还没有创建文档或上传文件，您可以').show();
  					$('#no-result-helper').removeAttr('style');
  				}else if(queryType=='myRecentFiles'){
  					$('#notice').addClass('large').text('当前没有常用文档').show();
  				}else if(queryType=='tagQuery'){
  					$('#notice').addClass('large').text('当前标签下没有文档').show();
  				}else{
  					if(featureManual.currentDirRid>0){
  						$('#notice').addClass('large').text('当前文件夹下没有文档').show();
  					}else{
  						$('#notice').addClass('large').text('该团队还未创建文档或上传文件，您可以').show();
  						$('#no-result-helper').removeAttr('style');
  					}
  				}
  			},
  			noMore: function() {
  				$('#notice').removeClass('loading');
  				$('#load-more-items').hide();
  				$('#no-result-helper').attr('style','display:none');
  				setTimeout(function(){
  					$('#notice').removeClass('large').text('没有更多文档了').show();
  				}, 500);
  			},
  			readyToLoad: function() {
  				$('#notice').removeClass('loading');
  				$('#load-more-items').show();
  				$('#no-result-helper').attr('style','display:none');
  				$('#notice').hide();
  			},
  			error : function(){
  				$('#notice').removeClass('loading');
  				$('#load-more-items').hide();
  				$('#no-result-helper').attr('style','display:none');
  				$('#notice').removeClass('large').text('请求失败！可能由于以下原因导致此问题：未登录，会话过期或权限不够！').fadeIn();
  			},
  			clean:function(){
  				$('#load-more-items').show();
  				$('#no-result-helper').attr('style','display:none');
  				$('#notice').hide();
  			},
  			hide:function(){
  				//$('#load-more-items').hide();
  				$('#no-result-helper').attr('style','display:none');
  				$('#notice').hide();
  			}
  	},
	currentDirRid:0,
	option :{},
	result :{},
	getInstance : function(opt){
		option=opt;
		$.extend(featureManual.option,opt);
		featureManual.init();
		var result={
			loadNewFiles:function(data,optype){
				featureManual.result.renderPrePendFile(data,optype);
			},
			loadFiles:function(parentId,type,param){
				backToTop();
				var data = new Object();
				data.rid=parentId;
				if(param){
					$.extend(data,param);
				}
				var hashData = location.hash.replace(/^#/,'');
				if(opt){
					$.extend(hashData,param);
				}
				if(hashData==null||hashData==''){
					hashData="tokenKey="+window.pageTokenKey;
				}else{
					hashData+="&tokenKey="+window.pageTokenKey;
				}
				//加载新数据前清空当前列表
				with(result){
					cleanFile();
				}
				
				
				//提前将根路径改变改变，获得数据后重新画路径
				featureManual.result.renderNavBar();
				featureManual.notice_handler.loading();
				$.ajax({
					url : option.queryUrl,
					data : hashData,
					type : "post",
					dataType:"json",
					async:true,
					success : function(d){
						if(d.tokenKey!=window.pageTokenKey){
							return;
						}
						with(result){
							$("#fileItemDisplay #resourceList").hide();
							$(".tableHeader").hide();
							featureManual.notice_handler.readyToLoad();
							renderFile(d.children);
							refreshNextBeginNum(d.nextBeginNum);
							renderNavBar(d.path);
							if(d.currentResource){
								featureManual.currentDirRid=d.currentResource.rid;
							}else{
								featureManual.currentDirRid=0;
							}
							
							parentRidArr.parentRid=window.featureManual.currentDirRid;
							
							if (d.total == 0) {
								featureManual.notice_handler.noMatch();
				    	  	}else if(d.loadedNum==0){
								featureManual.notice_handler.noMore();
							}else{
								if(d.loadedNum<d.size||d.total<=d.size){
									$('#load-more-items').hide();
									$('#notice').addClass('large').hide();
								}else{
									$('#load-more-items').show();
									$('#notice').addClass('large').hide();
								}
							}
							if(!d.showSort){
								$("#sortDivId").hide();
								$(this).parent(".filterBoard").css({"width":"98px"});
							}else{
								$("#sortDivId").show();
								$(this).parent(".filterBoard").css({"width":"383px"});
							}
							
							if(!d.showSearch){
								$("#resourceList-search").hide();
								$("#resourceList-search").parent(".filterBoard").css({"width":"98px"});
							}else{
								$("#resourceList-search").show();
								$("#resourceList-search").parent(".filterBoard").css({"width":"383px"});
							}
							
						}
					},
					statusCode:{
						450:function(){alert('会话已过期,请重新登录');},
						403:function(){alert('您没有权限进行该操作');}
					}
				});
			},
			loadAppendFiles:function(beginNum){
				var arr=getHashArray();
	        	replaceArrayParam(arr,"begin",beginNum);
	        	var hashData=convertArray2Hash(arr);
	        	if(hashData==null||hashData==''){
					hashData=window.pageTokenKey;
				}else{
					hashData+="&tokenKey="+window.pageTokenKey;
				}
	        	featureManual.notice_handler.loading();
				$.ajax({
					url : option.queryUrl,
					data : hashData,
					type : "post",
					dataType:"json",
					async:true,
					success : function(d){
						if(d.tokenKey!=window.pageTokenKey){
							return;
						}
						with(result){
							featureManual.notice_handler.readyToLoad();
							renderAppendFile(d.children);
							refreshNextBeginNum(d.nextBeginNum);
							renderNavBar(d.path);
							if(d.currentResource){
								featureManual.currentDirRid=d.currentResource.rid;
							}else{
								featureManual.currentDirRid=0;
							}
							
							if (d.total == 0) {
								featureManual.notice_handler.noMatch();
				    	  	}
							else if(d.loadedNum==0){
								featureManual.notice_handler.noMore();
							}else{
								if(d.loadedNum<d.size||d.total<=d.size){
									$('#load-more-items').hide();
									$('#notice').addClass('large').hide();
								}else{
									$('#load-more-items').show();
									$('#notice').addClass('large').hide();
								}
							}
						}
					},
					statusCode:{
						450:function(){alert('会话已过期,请重新登录');},
						403:function(){alert('您没有权限进行该操作');}
					}
				});
			},
			renderFile : function(data){
				var v = $("#"+option.display);
				$(v).html("");
				if(data.length==0){
					$("#fileItemDisplay #resourceList").hide();
					$(".tableHeader").hide();
				}else{
					$("#fileItemDisplay #resourceList").show();
					$(".tableHeader").show();
				}
//				$.tmpl('fileItemTemp', data).appendTo(v);
				$("#fileItemTemp").tmpl(data).appendTo(v);
				validateSelectedOperate();
			},
			cleanFile:function(){
				var v = $("#"+option.display);
				$(v).html("");
			},
			renderAppendFile : function(data){
				var v = $("#"+option.display);
				$.tmpl('fileItemTemp', data).appendTo(v);
				validateSelectedOperate();
			},
			renderPrePendFile: function(data,optype){
				var queryType=getQueryType();
				if(featureManual.result.canPrePendFile(data,optype)){
					$("#fileItemDisplay #resourceList").show();
					$(".tableHeader").show();
					featureManual.notice_handler.hide();
					$("#"+option.display +" input[type=hidden][value="+data.rid+"]").parents("li:first").remove();
					 $("#"+option.display).show();
					var insert=$.tmpl('fileItemTemp', data);
					if($("#"+option.display+" li.newFolderLi").size()>0){
						$(insert).insertAfter($("#"+option.display+" li.newFolderLi:last"));
					}else{
						$(insert).prependTo($("#"+option.display));
					}
					//var obj=$("#"+option.display+" li:first");
					insert.addClass("newFileLiAdd");
					//$("#"+option.display+" li:first").delay(5000).removeClass("newFileLiAdd");
					setTimeout(function(){ 
						insert.removeClass("newFileLiAdd");
					},2000);
				}
			},
			canPrePendFile:function(data,optype){
				if(!optype){
					return false;
				}
				var queryType=getQueryType();
				switch(optype){
					case 'newFloder':{
						return data.parentRid==featureManual.currentDirRid&&(queryType==''||queryType==null);
					}
					case 'uploadFile':{
						return data.parentRid==featureManual.currentDirRid&&(queryType==''||queryType==null||queryType=='teamRecentChange'||queryType=='myCreate');
					}
					case 'moveFile':{
						if(queryType==''||queryType==null){
							return data.parentRid==featureManual.currentDirRid&&queryType!='myRecentFiles'&&data.star;
						}
						
						return false;
					}
					case 'copyFile':{
						if(queryType==''||queryType==null){
							return data.parentRid==featureManual.currentDirRid;
						}else if((queryType=='myCreate'||queryType=='teamRecentChange')&&data.itemType!='Floder'){
							return true;
						}else if(queryType=='myStarFiles'){
							return data.start;
						}if(queryType=='myRecentFiles'){
							return false;
						}else if(queryType=="showFileByType"){
							var fileType=getFileType();
							if(fileType=='Picture'){
								return data.itemType=='DFile'&&(data.fileType=='jpg'||data.fileType=='png'||data.fileType=='jpeg'||data.fileType=='gif'||data.fileType=='bmp'||data.fileType=='tiff');
							}/*else if(fileType=='DFile'){
								return fileType==data.itemType&&data.fileType!='jpg'&&data.fileType!='png'&&data.fileType!='jpeg'&&data.fileType!='gif'&&data.fileType!='bmp'&&data.fileType!='tiff';
							}*/
							return fileType==data.itemType;
						}
						return false;
					}
				}
				
				
				
			},
			refreshNextBeginNum:function(nextBeginNum){
				$("#load-more-items").attr("begin",nextBeginNum);
			},
			moveFile:function(data){
				for (var i=0;i<data.length;i++){
					$("div.iconLynxTag.icon-checkStar[rid=" + data[i] + "]").parent().parent().parent().remove(); 
				}
				var begin=$("#load-more-items").attr("begin");
				begin=begin-data.length;
				if(begin<=0){
					with(result){
						loadFiles();
					}
				}else{
					$("#load-more-items").attr("begin",begin);
				}
			},
			renderNavBar:function(data){
				var v = $("#"+option.navBar);
				$(v).html("");
				var array = getHashArray();
				$("ul.ui-navList li.chosen").removeClass("chosen");
				var queryType = getArrayParam(array,'queryType');
				if(queryType){
					if(queryType=='myCreate'){
						$(v).html("<li><a>我创建的</a></li>");
					}else if(queryType=='myStarFiles'){
						$(v).html("<li><a>已加星标</a></li>");
					}else if(queryType=='myRecentFiles'){
						$(v).html("<li><a>我常用的</a></li>");
					}else if(queryType=='teamRecentChange'){
						$(v).html("<li><a>最近更新</a></li>");
					}else if(queryType=='tagQuery'){
						var tagId = getHashParam("tagId");
						var tagName = $("a#tag-for-"+tagId).find(".tagTitle").html();
						$("a#tag-for-"+tagId).parent().addClass("chosen");
						$(v).html("<li><a>"+tagName+"</a></li>");
					}else if(queryType=='showFileByType'){
						var type = getArrayParam(array,'type');
						if(type=='Picture'){
							$(v).html("<li class='allFiles'><a>所有文档</a> / 图片</li>");
						}else if(type=='DFile'){
							$(v).html("<li class='allFiles'><a>所有文档</a> / 文件</li>");
						}else if(type=='DPage'){
							$(v).html("<li class='allFiles'><a>所有文档</a> / 协作文档</li>");
						}
						$("#showAllFiles").parent().addClass("current");
					}
				}else{
					$(v).html("<li class='allFiles'><a>所有文档</a></li>");
					if(!data){
						//根目录
					}else{
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
							var tempLi=$.tmpl("navBarTemp", item);
							tempLi.insertAfter($(v).find("li:first"));
							var totalWidth=result.getNavWidth();
							//此处要多判断当前目录下级目录变换为“...”后长度是否超长 如果超长直接将当前目录缩为“...”28 为“...”的长度
							if(totalWidth>maxWidth||(index<data.length-1&&28+totalWidth>maxWidth)){
								tempLi.find("a").text("...");
								return false;
							}
						});
					}
				}
			},
			getNavWidth:function(){
				var totalLiWidth=0;
				$("#navBarOl li").each(function(index,item){
					totalLiWidth+=$(item).width();
				});
				return totalLiWidth;
			},
			loadRoot:function(){
				this.loadFiles('');
			},
			loadByHash:function(){
				var hash = getHashArray();
				var param = new Object();
				for(var i=0;i<hash.length;i++){
					var l = hash[i].split('=');
					param[l[0]]=l[1];
				}
				this.loadFiles(0,'all',param);
			},
			clean:function(){
				featureManual.result.refreshNextBeginNum(0);
				featureManual.notice_handler.clean();
			}
		};
		featureManual.result = result;
		return result;
	},
	renderData :function(item){
		var fileItem = featureManual.getFileItem(item);
		return $(fileItem).data('tmplItem').data;
	},
	getFileItem : function(item){
		var result = $(item).parents("li.files-item");
		return result;
	},
	renderNavData :function(item){
		var result = $(item).parents('li');
		return $(result).data('tmplItem').data;
	},
	setLoadPathHash:function(type,curDirRid,allPath){
		var array =getHashArray();
		var queryType = getArrayParam(array,'queryType');
		//
		var path = getArrayParam(array,"path");
		if(queryType&&!path){
			$.getJSON(window.featureManual.option.oprateUrl+"?func=getPath",{rid:curDirRid},function(data){
				var path = data.ridPath;
				var a = new Array();
				a.push(buildKeyValueObject("path",path));
				buildHash(a);
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
					replaceArrayParam(array,"path",tmp);
					buildHash(array);
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
					replaceArrayParam(array,"path",tmp);
					buildHash(array);
					return;
				}
			}	
		}else if(type=='add'){
			path=path+"/"+curDirRid;
			replaceArrayParam(array,"path",path);
			buildHash(array);
			return;
		}else if(type=='all'){
			if(allPath){
				var tmp ='';
				for(var p in allPath){
					tmp=tmp+'/'+p.rid;
				}
				replaceArrayParam(array,"path",tmp);
				buildHash(array);
				return;
			}
		}
	}
	
};

$(window).bind('hashchange',function(){
	if(typeof window.featureManual.result.loadFiles =='function'){
		window.featureManual.result.clean();
		window.pageTokenKey=createKeyCode();
		window.featureManual.result.loadFiles();
	}
});

$(window).hashchange();

function getArrayParam(array,param){
	for(var i=0;i<array.length;i++){
		if(array[i].key==param){
			return array[i].value;
		}
	}
	return null;
}
function replaceArrayParam(array,keys,value){
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
}

function removeArrayParam(array,keys){
	var deleteIndex= -1;
	for(var i=0;i<array.length;i++){
		if(array[i]){
			if(array[i].key==keys){
				deleteIndex=i;
			}
		}
	}
	if(deleteIndex>=0){
		array.splice(deleteIndex,1);
	}
}

function getHashArray(){
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
}


function buildHash(array){
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
}

function convertArray2Hash(array){
	var tmp = '';
	for(var i=0;i<array.length;i++){
		if(array[i]){
			if(i==0){
				tmp=array[i].key+"="+encodeURIComponent(array[i].value);
			}else{
				tmp=tmp+"&"+array[i].key+"="+encodeURIComponent(array[i].value);
			}
		}
	}
	return tmp;
}

function buildKeyValueObject(key,value){
	var param=new Object();
	param.key=key;
	param.value=value;
	return param;
}

function getQueryType(){
	return getArrayParam(getHashArray(),"queryType");
}

function getFileType(){
	return getArrayParam(getHashArray(),"type");
}

function getKeyWord(){
	return getArrayParam(getHashArray(),"keyWord");
}
function getHashParam(key){
	return getArrayParam(getHashArray(),key);
}

function validateSelectedOperate(){
	 if($(".showSelectedOperate:checked:not(#checkAllFiles)").size()>0){
		 if(!canShowAddTag()){
			 $(".selectedOperGroup #addTags").hide();
		 }else{
			 $(".selectedOperGroup #addTags").show();
		 }
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
}

function unCheckedAll(){
	$(".showSelectedOperate:checked").attr("checked",false);
	validateSelectedOperate();
}

function canShowAddTag(){
	var result=true;
	$(".showSelectedOperate:checked:not(#checkAllFiles)").each(function(i,item){
		if($(item).parents("li.files-item:first").find("input.itemType").val()=='Folder'){
			result=false;
			return;
		}
	});
	return result;
}

function backToTop() {
	window.scroll(0,0);
}


function showMsg(msg, type){
	type = type || "success";
	$("#opareteFileMessage").removeClass().addClass("alert alert-" + type).html(msg).show(150);
}
function hideMsg(timeout){
	timeout = timeout || 2000;
	window.setTimeout(function(){$("#opareteFileMessage").hide(150);}, timeout);
}

function createKeyCode(){
	return new Date().getTime().toString();
}


$(document).ready(function(){
	
    var actionURL = {
    	"team": site.getURL("files"),
    	"teamHome": site.getURL("teamHome"),
    	"recommend": site.getURL("recommend"),
    	"tag": site.getURL("tag"),
     	"files": site.getURL("files"),
     	"upload": site.getURL("upload"),
     	"starmark": site.getURL("starmark")
    };
    
    console.log(actionURL.recommend);
    
	/*vera added begin*/
	//上传完成弹出框的位置
	var rightWidth = ($(window).width() - $(".lionContent").width()) / 2 + 5;
	$(".popupUpload#popupUpload").css({"right":rightWidth});
	
	//所有文档-类型选择
	$(".ui-wrap ul.myNavList li.filterDrop span.caretSpan").click(function(e){
		$("ul.filteFile").show();
		e.stopPropagation();
	});
	$(".ui-wrap ul.myNavList li.filterDrop").mouseleave(function(e){
		$("ul.filteFile").hide();
	})
	
	//fixed住menu栏
	var menu = $('.fixMenuBar');
	var pop = $('.alert#opareteFileMessage')
	var pos = menu.offset().top;
	var top = 34;
	menu.css('top', top);
	var fixMenuBarWidth = $("#body").width() -233;
	$(window).scroll(function(){
		if ($(document).scrollTop()-pos > -top) {
			menu.css({'position': 'fixed','z-index':'10','width':fixMenuBarWidth});
			pop.css({'top':'2px','left':'30px'});
		}
		else {
			menu.css('position', '');
			pop.css({'top':'20px','left':'10px'});
		}
	});
	/*vera added end*/
	$("#fileItemTemp").template('fileItemTemp');
	var args = {
			display:"resourceList",
			navBar:"navBarOl",
			queryUrl: actionURL.team + "?func=query",
			oprateUrl:actionURL.team,
			teamHome : actionURL.teamHome,
			addFolder : "addFolder"
	};
	var featureManual = window.featureManual.getInstance(args);
	if(location.hash){
		featureManual.loadFiles();
	}else{
		featureManual.loadFiles();
	}
	$(".files-item").live("mouseenter ",function(){
		$(this).find("span.file-commands").show();
	}).live("mouseleave ",function(){
		$(this).find("span.file-commands").hide();
	});
	$("#sortMenu li").click(function(){ 
		   $("#sortMenu b.ico-radio-checked").removeClass("ico-radio-checked");  
		   $(this).find("a b.ico-radio").addClass("ico-radio-checked");    
		});
	
	var original_rid = -1;
	var target_rid = -1;
	var originalRids = [];
	var file_operation = 'none';
	$("li.move_item").live("click",function(){
		$('#fileBrowserModalLabel').empty();
		$('#fileBrowserModalLabel').append("移动到");
		$("#fileBrowserModal").modal();
		original_rid = $(this).attr("rid");
		file_operation = 'move';
	});
	$("li.copy_item").live("click", function(){
		$('#fileBrowserModalLabel').empty();
		$('#fileBrowserModalLabel').append("复制到");
		$("#fileBrowserModal").modal();
		original_rid = $(this).attr("rid");
		file_operation = 'copy';
	});
	$("#copySelected").live("click", function(){
		var items = $("div.oper .showSelectedOperate:checked");
		if(!items){
			return ;
		}
		$('#fileBrowserModalLabel').empty();
		$('#fileBrowserModalLabel').append("复制到");
		$("#fileBrowserModal").modal();
		originalRids = [];
		$.each(items,function(index,item){
			var i = $(item).parents("li.files-item").data('tmplItem').data;
			originalRids.push(i.rid);
		});
		file_operation = 'copySelected';
	});
	$("#moveSelected").live("click", function(){
		var items = $("div.oper .showSelectedOperate:checked");
		if(!items){
			return ;
		}
		$('#fileBrowserModalLabel').empty();
		$('#fileBrowserModalLabel').append("移动到");
		$("#fileBrowserModal").modal();
		originalRids = [];
		$.each(items,function(index,item){
			var i = $(item).parents("li.files-item").data('tmplItem').data;
			originalRids.push(i.rid);
		});
		file_operation = 'moveSelected';
	});
	$("#fileBrowserModal").on("show", function(){
		
		$("#file_browser").jstree(
				{
					"json_data" : {
						"ajax" : {
							"url" : "${teamHome}/fileManager",
							"cache":false,
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
								},
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
								"remove" : false,
							}
						}
					},
				}).bind("select_node.jstree", function(event, data) {
			target_rid = data.rslt.obj.attr("rid").replace("node_", "");
		});
		
	});
	
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
			 " 	<input style='margin:0' class='fileNameInput' type='text' value='"+fileName+"' length='250'>" +
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
				alert("文件夹名不能为空");
				return;
			}
			var d = new Object();
			var opType = span.find('input.opType').val();

			d.fileName=fileName;
			d.rid=span.find('input.rid').val();
			d.parentRid=span.find('input.parentId').val();
			d.func=opType;
			var opUrl=actionURL.files;
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
						featureManual.renderPrePendFile(data.resource,"newFloder");
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
	
	
	$("#moveToBtn").live('click', function(){
				$('#fileBrowserModal').modal('hide');
				//$("#file_browser").jstree('destroy');
				if(file_operation=='copy' || file_operation=='move'){
					if(original_rid==-1 || target_rid==-1){
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
						return ;
					}
					
					file_manager_url = "${teamHome}/fileManager";
					if(file_operation == 'move'){
						$("#opareteFileMessage").removeClass();
						$("#opareteFileMessage").addClass("alert alert-block");
						$("#opareteFileMessage").html("正在移动，请稍后...");
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
									current_nav = $(".myNavList .current a:first").attr('id');
									if (current_nav == "showAllFiles") {
										//$("div.iconLynxTag.icon-checkStar[rid=" + original_rid + "]").parent().parent().parent().remove();
										originalRids=[];
										originalRids.push(original_rid);
										featureManual.moveFile(originalRids);
										originalRids=[];
									}
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
								}, 3000);
						   }
						});
					} else if(file_operation == 'copy') {
						$("#opareteFileMessage").removeClass();
						$("#opareteFileMessage").addClass("alert alert-block");
						$("#opareteFileMessage").html("正在复制，请稍后...");
						$("#opareteFileMessage").show();
						$.ajax({
						   type: "POST",
						   url: file_manager_url,
						   cache:false,
						   data: {
								'func' : 'copy',
								'originalRid' : original_rid,
								'targetRid' : target_rid,
							},
							dataType:"json",
						   	success: function(data){
							   $("#opareteFileMessage").removeClass();
								if (data.state == 0) {
									$("#opareteFileMessage").addClass("alert alert-success");
									 featureManual.renderPrePendFile(data.resource,'copyFile');
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
					}
				} else if (file_operation=='copySelected' || file_operation=='moveSelected'){
					if(originalRids.length==0){
						return ;
					}
					file_manager_url = "${teamHome}/fileManager";
					if(file_operation == 'moveSelected'){
						$("#opareteFileMessage").removeClass();
						$("#opareteFileMessage").addClass("alert alert-block");
						$("#opareteFileMessage").html("正在移动，请稍后...");
						$("#opareteFileMessage").show();
						$.ajax({
						   type: "POST",
						   url: file_manager_url,
						   cache:false,
						   data: {
								'func' : 'moveSelected',
								'originalRids' : originalRids.toString(),
								'targetRid' : target_rid,
							},
							dataType:"json",
						   	success: function(data){
						   		$("#opareteFileMessage").removeClass();
								if (data.state==0){
									current_nav = $(".myNavList .current a:first").attr('id');
									if (current_nav == "showAllFiles") {
										featureManual.moveFile(originalRids);
									/* 	for (var i=0;i<originalRids.length;i++){
										} */
									}
									$("#opareteFileMessage").addClass("alert alert-success");
									unCheckedAll();
								} else if (data.state==1){
									$("#opareteFileMessage").addClass("alert alert-block");
								} else if (data.state==2) {
									$("#opareteFileMessage").addClass("alert alert-error");
								}
								
								$("#opareteFileMessage").html(data.msg);
								$("#opareteFileMessage").show();
								window.setTimeout(function(){
									$("#opareteFileMessage").hide(150);
								}, 3000);
								originalRids = [];
						   }
						});
					} else if(file_operation == 'copySelected') {
						$("#opareteFileMessage").removeClass();
						$("#opareteFileMessage").addClass("alert alert-block");
						$("#opareteFileMessage").html("正在复制，请稍后...");
						$("#opareteFileMessage").show();
						$.ajax({
						   type: "POST",
						   url: file_manager_url,
						   cache:false,
						   data: {
								'func' : 'copySelected',
								'originalRids' : originalRids.toString(),
								'targetRid' : target_rid,
							},
							dataType:"json",
						   	success: function(data){
						   		$("#opareteFileMessage").removeClass();
								if (data.state == 0) {
									$("#opareteFileMessage").addClass("alert alert-success");
									$.each(data.resourceList,function(index,item){
										 featureManual.renderPrePendFile(item,'copyFile');
									});
									unCheckedAll();
								} else if (data.state == 1) {
									$("#opareteFileMessage").addClass("alert alert-block");
								} else if (data.state == 2) {
									$("#opareteFileMessage").addClass("alert alert-error");
								}
								$("#opareteFileMessage").html(data.msg);
								$("#opareteFileMessage").show();
								window.setTimeout(function(){
									$("#opareteFileMessage").hide(150);
								}, 3000);
								originalRids = [];
						   }
						});
					}
				}
				
	});
	
	
	var _tagCache = null;
	var current_data_loader = ({
		reloadTagCount:function(){
			var url = actionURL.tag;
			ajaxRequest(url,"func=refreshTagCount",function(data){
				$.each(data,function(index,item){
					$('#tag-for-' + item.tag_id + ' .tagResCount').text(item.count);
				}); 
			});
		},
		addTagForSingleRecord:function(){
			var tagURL = actionURL.tag;
			var params = {"func":"batchAdd","newTags[]":getNewTags(),"existTags[]":getExistTags(),"rids[]":aTBox.log.rid};
			ajaxRequestWithErrorHandler(tagURL,params,function(data){
				var newTagIndex = [];
				for (var i=0; i<data.length; i++) {
					var needAdd=$('#tag-item-'+data[i].item_key+" li[tag_id="+data[i].id+"]").length==0;
					if(needAdd){
						$('#page-tag-template').tmpl(data[i]).prependTo('#tag-item-'+data[i].item_key);
					}
					if (data[i].isNewTag && arrIndexOf(newTagIndex, data[i].id)==-1) {
						newTagIndex.push(data[i].id);
						$('#new-tag-template').tmpl(data[i]).appendTo($('#ungrouped-tag-list'));
						$("div.noGroupTagTitle").show();
					}else if(!data[i].isNewTag){
						var item = $('#tag-for-' + data[i].id + ' .tagResCount');
						if(needAdd){
							item.text(parseInt(item.text())+1);
						}
					}
				}
				addSingleTagDialog.hide();
				selector.removeItem('all', true);
				unCheckedAll();
				loadAllTeamTagsNow();
			},notEnoughAuth);
		},
		loadAllTeamTags:function(){
			var url = site.getURL("tag",null);
			ajaxRequest(url,"func=loadTeamTags",function(data){
				_tagCache = data;
				tPool.refresh(_tagCache);
				aTBox.refresh();
			});
		},
		renderTag:function(){
			var url = site.getURL("tag",null);
			ajaxRequest(url,"func=loadTeamTags",function(data){
				$(".tagGroupsDiv").remove();
				$(".noGroupTagTitle").remove();
				$('#render-tag-all').tmpl(data).appendTo($('#tagSelector'));
				$("p.tagGroupTitle").addClass("foldable");
				_tagCache = data;
				tPool.refresh(_tagCache);
				aTBox.refresh();
			});
		}
	});
	
	
	$("a.viewFile").live("click",function(){
		var rid=$(this).attr("rid");
		var url = window.featureManual.option.teamHome+"/r/"+rid;
		window.open(url);
	});
	
	
	var upload_base_url = actionURL.upload;
	var upload_url = upload_base_url+"?func=uploadFiles";
	
	var uploadedFiles = [];
	var index = 0;
	
	
	qq.extend(qq.FileUploader.prototype,{
		_addToList: function(id, fileName){
	        var item = qq.toElement(this._options.fileTemplate);                
	        item.qqFileId = id;

	        var fileElement = this._find(item, 'file');        
	        qq.setText(fileElement, this._formatFileName(fileName));
	        this._find(item, 'size').style.display = 'none';        

	        this._listElement.appendChild(item);
	        $("#popupUpload").show();
        	//$("#fileListDiv").slideUp(5000);
	        $("#fileListDiv").show();
        	//$("#fileListDiv").fadeIn(1000);
        	//$("#fileListDiv").fadeOut(5000);
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
	            featureManual.renderPrePendFile(result.resource,'uploadFile');
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
	     /*    qq.attach(list, 'click', function(e){            
	            e = e || window.event;
	            var target = e.target || e.srcElement;
	            alert("ssssdddd");
	            if (qq.hasClass(target, self._classes.cancel)){                
	                qq.preventDefault(e);
	               
	                var item = target.parentNode;
	                self._handler.cancel(item.qqFileId);
	                qq.remove(item);
	            }
	        });  */
	    }   ,
	    _setupDragDrop: function(){
	    	var self = this,
            dropArea = this._find(this._element, 'drop');     
	    	dropArea.style.display = 'none';
	    }
	});
	
	
     
     var topUploader = new qq.FileUploader({
         element: document.getElementById('file-uploader-demo1'),
         template: '<div class="qq-uploader">' + 
         '<div class="qq-upload-drop-area" style="display:none"><span>ree</span></div>' +
         '<div class="qq-upload-button"><i class="icon-file icon-white"></i> 上传文件</div><br/>'+ '</div>',
         listElement: document.getElementById("upload-list"),
         fileTemplate: '<li>' +
         '<span class="qq-upload-file"></span>' +
         '<span class="qq-upload-spinner"></span>' +
         '<span class="qq-upload-size"></span>' +
         '<a class="qq-upload-cancel" href="#">Cancel</a>' +
         '<span class="qq-upload-failed-text">Failed</span>' +
    	 '</li>', 
         action: upload_url,
         params:parentRidArr,
         onComplete:function(id, fileName, data){
         	uploadedFiles[index] = data;
         	index ++;
         	current_data_loader.renderTag();
         },
         debug: true,
     });  
     
     
     var linkUploader = new qq.FileUploader({
         element: document.getElementById('file-uploader-link'),
         template: '<div class="qq-uploader">' + 
         '<div class="qq-upload-drop-area"><span>Drop files here to upload</span></div>' +
         '<div class="qq-upload-button">上传文件</div><br/>'+ '</div>',
         listElement: document.getElementById("upload-list"),
         fileTemplate: '<li>' +
         '<span class="qq-upload-file"></span>' +
         '<span class="qq-upload-spinner"></span>' +
         '<span class="qq-upload-size"></span>' +
         '<a class="qq-upload-cancel" href="#">Cancel</a>' +
         '<span class="qq-upload-failed-text">Failed</span>' +
    	 '</li>', 
         action: upload_url,
         params:parentRidArr, 
         onComplete:function(id, fileName, data){
         	uploadedFiles[index] = data;
         	index ++;
         	current_data_loader.renderTag();
         },
         debug: true,
     });  
     
     
     $("input[type=file]").attr("title","上传文件");
     
     
     /* $("#attach-to-this-page").live("click",function(){
	   	 $(".qq-upload-list").html("");
	   	$('#uploadModal').modal('hide');
	   	window.location.href=location.href;
    });
     
     $(".closeUpload").live("click",function(){
    	 $(".qq-upload-list").html("");
    	 $('#uploadModal').modal('hide');
    	 window.location.href=location.href;
     }); */
     
     $("#showAllFiles").live("click",function(){
    	 var arr=new Array();
    	 buildHash(arr);
    	 $(".myNavList .current").removeClass("current");
    	$(this).parent("li").addClass("current");
    	resetFilter();
    	$("#addFolder").show();
     });
     
     $("#showMyCreateFiles").live("click",function(){
    	 $("#addFolder").hide();
    	 resetFilter();
    	 var arr=new Array();
    	 arr.push(buildKeyValueObject("rid",0));
    	 arr.push(buildKeyValueObject("queryType","myCreate"));
    	 buildHash(arr);
    	 $(".myNavList .current").removeClass("current");
    	$(this).parent("li").addClass("current");
     });
     
     $("#showMyRecentFiles").live("click",function(){
    	 $("#addFolder").hide();
    	 var arr=new Array();
    	 arr.push(buildKeyValueObject("rid",0));
    	 arr.push(buildKeyValueObject("queryType","myRecentFiles"));
    	 buildHash(arr);
    	 $(".myNavList .current").removeClass("current");
    	$(this).parent("li").addClass("current");
     });
     
     $("#showTeamRecentChang").live("click",function(){
    	 $("#addFolder").hide();
    	 resetFilter();
    	 var arr=new Array();
    	 arr.push(buildKeyValueObject("rid",0));
    	 arr.push(buildKeyValueObject("queryType","teamRecentChange"));
    	 buildHash(arr);
    	 $(".myNavList .current").removeClass("current");
    	$(this).parent("li").addClass("current");
     });
     
     $("#showMyStarFiles").live("click",function(){
    	 $("#addFolder").hide();
    	 resetFilter();
    	 var arr=new Array();
    	 arr.push(buildKeyValueObject("rid",0));
    	 arr.push(buildKeyValueObject("queryType","myStarFiles"));
    	 buildHash(arr);
    	 $(".myNavList .current").removeClass("current");
    	 $(this).parent("li").addClass("current");
     });
     
     $("#showTeamPages").live("click",function(){
    	 console.log(1);
    	 $("#addFolder").hide();
    	 resetFilter();
    	 var arr=new Array();
    	 arr.push(buildKeyValueObject("queryType","showFileByType"));
    	 arr.push(buildKeyValueObject("type","DPage"));
    	 buildHash(arr);
    	 $(".myNavList .current").removeClass("current");
    	 $(this).parent("li").addClass("current");
     });
     $("#showTeamFiles").live("click",function(){
    	 $("#addFolder").hide();
    	 resetFilter();
    	 var arr=new Array();
    	 arr.push(buildKeyValueObject("queryType","showFileByType"));
    	 arr.push(buildKeyValueObject("type","DFile"));
    	 buildHash(arr);
    	 $(".myNavList .current").removeClass("current");
    	 $(this).parent("li").addClass("current");
     });
     $("#showTeamPicture").live("click",function(){
    	 $("#addFolder").hide();
    	 resetFilter();
    	 var arr=new Array();
    	 arr.push(buildKeyValueObject("queryType","showFileByType"));
    	 arr.push(buildKeyValueObject("type","Picture"));
    	 buildHash(arr);
    	 $(".myNavList .current").removeClass("current");
    	 $(this).parent("li").addClass("current");
     });
     
     $(".sortFiles").live("click",function(){
    	var sortName= $(this).find("span.sortName").text();
    	$(".sortTitle").text(sortName);
    	var sortType=$(this).attr("sortType");
    	var arr=getHashArray();
    	replaceArrayParam(arr,"sortType",sortType);
    	buildHash(arr);
     });
     
     $(".iconLynxTag").live("click",function(){
    	 if($(this).hasClass("checked")){
    		removeStar($(this));
    	 }else{
    		 addStar($(this));
    	 }
     });
     
     function addStar(object){
    	 var rid=$(object).attr("rid");
    	 $.ajax({
				url: actionURL.starmark,
				data : "rid="+rid,
				type : "post",
				dataType:"json",
				success :function(data){
					if(data.status=="success"){
						$(object).addClass("checked");
					}
				}
			});
     }
     
     function removeStar(object){
    	 var rid=$(object).attr("rid");
    	 $.ajax({
				url: actionURL.starmark + "?func=remove",
				data : "rid="+rid,
				type : "post",
				dataType:"json",
				success :function(data){
					if(data.status){
						$(object).removeClass("checked");
					}
				}
			});
     }
     
     
     $(".share-file").die().live("click",function(){
    	 var item = window.featureManual.renderData($(this));
    	 prepareRecommend(actionURL.recommend + "?func=prepareRecommend&itemType="+item.itemType+"&rid="+item.rid,item.rid,item.fileName,item.itemType);
     });
     
     $("#shareFiles").die().live("click",function(){
    	 var items=new Array();
    	 $(".showSelectedOperate:checked:not(#checkAllFiles)").each(function(index,item){
    		 items.push(window.featureManual.renderData($(item)));
    	 });
    	prepareRecommendRids(actionURL.recommend + "?func=prepareRecommend",items);
     });
     
    //--------------------------------tag start---------------------------------
    
    $('input.tagPoolAutoShow').tokenInput(actionURL.site + "?func=loadTeamTags&type=nogroup", {
		theme:"facebook",
		hintText: "输入标签名称，以逗号或回车结束",
		searchingText: "正在搜索……",
		noResultsText: "没有该标签，输入逗号或回车添加",
		preventDuplicates: true,
	}); 
	
	// leftMenu show firstFive begin
	$("#tagSelector").find("ul.ui-navList").each(function(){
		var childrenNum = $(this).find("li").length;
		var i = 0;
		if(childrenNum > 5) {
			$(this).addClass("moreThanFive");
			$(this).find("li").each(function(){
				i++;
				if(i>5){
					$(this).hide();
				}
			});
		}
	});
	
	$("#tagSelector").find("ul.ui-navList").mouseenter(function(){
		$(this).children().show("normal");
		$(this).removeClass("moreThanFive");
	});
	$("#tagSelector").find("ul.ui-navList").mouseleave(function(){
		var j = 0;
		$(this).children().each(function(){
			j++;
			if(j>5){
				$(this).hide();
				$(this).parent().addClass("moreThanFive");
			}
		});
	});
    
	$("a.delete-tag-link").live('click',function(){
 		var rids = new Array();
 		try{
	 		rids[0] = window.featureManual.renderData($(this)).rid;
 		}catch(e){
 			
 		}
 		if(typeof(rids[0])=='undefined'){
 	 		rids = selector.getRidArr();
 		}
    	var params = {"func":"remove","rid[]":rids,"tagId":$(this).attr("tag_id")};
    	var url = site.getURL('tag',null);
    	var $a = $(this);
		ajaxRequestWithErrorHandler(url,params,function(data){
    		$a.parent().remove();
    		$.each(data.rids, function(index, element){
    			$("ul#tag-item-"+element.rid+" li[tag_id="+data.tagId+"]").remove();
    		});
    		var $tagCount = $("a#tag-for-"+data.tagId+" span.tagResCount");
    		var count = parseInt($tagCount.text());
    		count = (count-data.rids.length)>=0?(count-data.rids.length):0;
    		$tagCount.text(""+count);
    	},notEnoughAuth);
		$(".tagGroupHorizon ul").find('a[tag_id="'+ $(this).attr("tag_id") + '"]').parent().removeClass("chosen");
    });
	
	var tPool = new tagPool({
		pool: $('.tagGroupHorizon'),
		scroller: $('.tG-scroll'),
		blockClass: 'tG-block'
	});
	
	var aTBox = new addTagBox({
		input: $('input[name="typeTag"]'),
		tagList: $('.existTags ul.tagList'),
		tagTogether: $('.existTags ul.tagTogether'),
		tagSelf: $('.existTags ul.tagSelf'),
		tagCreate: $('.existTags ul.tagCreate'),
		tagPool: tPool
	});
	
	var addSingleTagDialog = new lynxDialog({
		'instanceName': 'addSnglTag',
		'dialog': $('#addSingleTagDialog'),
		'close': $('#addSingleTagDialog .closeThisTagDialog'),
		'beforeShow': function() {
			tPool.refreshAppearance();
		},
		'afterHide': function() {
			aTBox.clean();
		}
	});
	
	var showPageLockErrorMessage = function(data){
		$("#pageLockErrorMessageDialogContent").html("");
		$("#pageLockErrorMessageTemplate").tmpl(data.lockError).appendTo("#pageLockErrorMessageDialogContent");
		ui_showDialog("pageLockErrorMessageDialog",3500);
	};
	
	/* $('#resAction-tag a').click(function(){
		aTBox.prepare({ ridArr: selector.getRidArr() });
		var ridArr = selector.getRidArr();
		$.each(ridArr, function(n, value){
			aTBox.prepare({ul: $('#resourceList li[item_id='+value+'] ul.tagList'), source:"toolBar"});
		});
		var chosenNum = $("ul#resourceList").find("li.chosen").length; 
		hideOrDisplayDiv(chosenNum);
		addSingleTagDialog.show();
		$("#token-input-").focus();
	}); */
	
	$('#resourceList .tagList li.newTag a').live('click', function(event){
		event.stopPropagation();
		aTBox.prepare({ ul: $(this).parent().parent(), source: "single" });
		hideOrDisplayDiv(1);
		var item = window.featureManual.renderData($(this));
		selector.setRidArr(item.rid);
		addSingleTagDialog.show();
		$("#token-input-").focus();
	});
	
	$("#addTags").live("click",function(){
		var temp=new Array();
		$(".showSelectedOperate:checked:not(#checkAllFiles)").each(function(i,item){
			temp.push($(item).parent().find("input.rid").val());
		});
		selector.setRid(temp); 
		aTBox.prepare({ ridArr: selector.getRidArr()});
		var ridArr = selector.getRidArr();
		$.each(ridArr, function(n, value){
			aTBox.prepare({ul: $('#resourceList  ul#tag-item-'+value), source:"toolBar"});
		});
		var chosenNum = $("ul#resourceList").find("li.chosen").length; 
		hideOrDisplayDiv(chosenNum);
		addSingleTagDialog.show();
		$("#token-input-").focus();
	});
	resSelector = function(){
		//this.rid = new Array();
		var _rid = new Array();
		this.getRidArr=function(){
			return this._rid;
		};
		this.setRidArr=function(v){
			var vs = new Array();
			vs.push(v);
			this._rid = vs;
		};
		this.setRid=function(da){
			this._rid=da;
		};
		this.removeItem=function(){
			this._rid = new Array();
		};
	};
	var selector = new resSelector();
	function hideOrDisplayDiv(chosenNum){
		if (chosenNum == 1) {
			$("#addSingleTagDialog .self").hide();
			$("#addSingleTagDialog .change").html("已有标签：");
			$("#addSingleTagDialog .change2").html("新增标签：");
			/* $("#addSingleTagDialog .tagShow").css({"width":"47%"});
			$("#addSingleTagDialog .tagShow:last").css({"margin-left":"48%"}); */
			$("#addSingleTagDialog .tagShow:last").find("ul.tagList").appendTo($("#addSingleTagDialog .tagShow:last").prev());
		}
		else{
			$("#addSingleTagDialog .self").show();
			$("#addSingleTagDialog .change").html("全部公有标签：");
			$("#addSingleTagDialog .change2").html("本次新增标签：");
			$("#addSingleTagDialog .tagShow").css({"width":"23%"});
			$("#addSingleTagDialog .tagShow:last").css({"margin-left":"0"});
			$("#addSingleTagDialog .tagShow:last").prev().find("ul.tagList").appendTo($("#addSingleTagDialog .tagShow:last"));
		}
	}
	
	$("#addSingleTagDialog .saveThisTagDialog").click(function(){
		var feedback = current_data_loader.addTagForSingleRecord();
		addSingleTagDialog.hide();
	});
	function getNewTags(){
		var results = [];
		var i = 0;
		$(".existTags ul.tagCreate").children("li").each(function(){
			results[i] = $(this).text();
			i++;
		});
		
		return results;
	}
	
	function getExistTags(){
		var results = [];
		$(".existTags ul.tagList").children("li").each(function(){
			results.push( $(this).attr("tag_id"));
		});
		return results;
	}
	

	function notEnoughAuth(){
		notice_handler.error();
	};
	current_data_loader.loadAllTeamTags();
	
	function loadAllTeamTagsNow(){
		var url = site.getURL("tag",null);
		ajaxRequest(url,"func=loadTeamTags",function(data){
			_tagCache = data;
			tPool.refresh(_tagCache);
			aTBox.refresh();
		});
	}
	$('#tagSelector .addToQuery').live('click', function(event){
  		event.stopPropagation();
  		compositeStateToggle($(this).prev(), 'filter', multipleOptionsToggle);
  	});
    
	$("a.tag-option").live('click', function(event){
		resetFilter();
		if($(this).parent().hasClass('chosen')){
			$(this).parent().removeClass('chosen');
			location.hash="";
			 $("#addFolder").show();
			 $("#showAllFiles").parent().addClass("current");
		}else{
			$(".myNavList .current").removeClass("current");
			var tagId = $(this).attr("value");
			var a = new Array();
			a.push({'key':"tagId","value":tagId});
			a.push({"key":'queryType',"value":'tagQuery'});
			$('#tagSelector li.chosen').removeClass('chosen');
			$(this).parent().addClass('chosen');
			buildHash(a);
			 $("#addFolder").hide();
		}
	  });
	
	//标签折叠
	var tagMenu = new foldableMenu({ controller: 'p.tagGroupTitle', focus: false });
    //--------------------------------tag   end---------------------------------
     function hideFilesList(){
    	 $("#fileListDiv").slideUp(2000);
      	 var obj=$("i.icon-minus");
      	 obj.removeClass("icon-minus");
      	 obj.addClass(" icon-max");
     }
     
     function showFilesList(){
    	 $("#fileListDiv").show();
      	 var obj=$("i.icon-max");
      	 obj.removeClass("icon-max");
      	 obj.addClass("icon-minus");
     }
     function toggleFilesList(){
    	/*  $("#fileListDiv").toggle(); */
    	 var bool = $("#fileListDiv").is(":hidden");
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
     
     $("i.icon-max").live("click",function(){
    	 showFilesList();
     });
     
     $("i.icon-minus").live("click",function(){
    	 hideFilesList();
     });
     $("p.uploadTitle").live("click",function(){
    	 toggleFilesList();
     });
     
     $("i.fillUploadPagCal").live("click",function(){
    	  if(linkUploader._filesInProgress>0||topUploader._filesInProgress>0){
    		  $("#alertModel").modal("show");
    		  $("#okAlertContent").addClass("cancleAllOk");
	      	  return;
	        }
    	  linkUploader._filesInProgress=0;
    	  topUploader._filesInProgress=0;
	      $("#upload-list").html("");
	      $("#popupUpload").hide();
     });
     
     $(".closeUpload").live("click",function(){
    	  $("#alertModel").modal("hide");
     });
     
     $("#okAlertContent").live("click",function(){
   	  	$("#alertModel").modal("hide");
    });
     
     $(".cancleAllOk").live("click",function(){
    	 if(linkUploader._filesInProgress>0){
	    	 linkUploader._CancelAll();
	    	 linkUploader._filesInProgress=0;
    	 } 
    	 if(topUploader._filesInProgress>0){
	    	topUploader._CancelAll();
	    	 topUploader._filesInProgress=0; 
    	 }
    	 $("#upload-list").html("");
	      $("#popupUpload").hide();
	    
     });
     
     $("#load-more-items").live("click",function(){
    		var begin=$(this).attr("begin");
    		window.featureManual.result.loadAppendFiles(begin);
     });
     
     $("#checkAllFiles").click(function(){
    	 if($(this).attr("checked")==true||$(this).attr("checked")=="checked"){
    	 	$("#fileItemDisplay input:checkbox").attr("checked",true);
    	 }else{
    		 $("#fileItemDisplay input:checkbox").attr("checked",false);
    	 }
     });
     
     $(".showSelectedOperate").live("click",function(){
    	 validateSelectedOperate();
     });
     
     var autoLoadMore;
     $(window).scroll(function(){
	 		if ($(window).scrollTop() + $(window).height() > $('#load-more-items').offset().top
	 			&& $('#load-more-items').is(':visible')		
	 		) {
	 			clearTimeout(autoLoadMore);
	 			autoLoadMore = setTimeout(function(){
	 				$('#load-more-items:visible').click();
	 			}, 700);
	 		}
 	});
     
     
     $("#fileTitleSearch").keydown(function(){ 
    	 if(event.keyCode==13){
    		var keyWord=$(this).val();
    		var arr=getHashArray();
        	replaceArrayParam(arr,"keyWord",keyWord);
        	buildHash(arr);
    	 }
    	 
     });
     
     var sbox = new searchBox({
   		container : $('#resourceList-search'),
   		standbyText: '过滤'
   	});
 	sbox.doSearch = function(QUERY) {
		var arr=getHashArray();
    	replaceArrayParam(arr,"keyWord",QUERY);
    	buildHash(arr);
 	};
 	
	sbox.resetSearch = function(){
		var keyWord=getKeyWord();
		if(keyWord){
			var arr=getHashArray();
	    	removeArrayParam(arr,"keyWord");
	    	buildHash(arr);
		}
 	};
 	
 	
 	var deleteAlertNotice={
 		data:new Array(),
 		clean: function(){
 			$("#alertDeleteModel .initShow").show();
 			$("#alertDeleteModel .deleteShow").hide();
 			$("#alertDeleteModel .deleteRid").val("");
 			data=new Array();
 		},
 		showDelete:function(rids){
 			with(deleteAlertNotice){
 				clean();
	 			$("#alertDeleteModel").modal(); 
	 			data=rids;
 			}
 		},
 		deleteing:function(){
 			$("#alertDeleteModel .initShow").hide();
 			$("#alertDeleteModel .deleteShow").show();
 			with(deleteAlertNotice){
	 			if(data.length<2){
	 				deleteOne(data[0]);
	 			}else{
	 				deleteSeleted(data);
	 			}
 			}
 		},
 		afterDelete:function(){
 			validateSelectedOperate();
 			deleteAlertNotice.clean();
 		},
 		success:function(){
 			with(deleteAlertNotice){
	 			$("#alertDeleteModel").modal("hide");
 			}
 			$("#opareteFileMessage").modal("hide");
 			$("#opareteFileMessage").html("文件已经删除！");
			$("#opareteFileMessage").show();
			window.setTimeout(function(){
				$("#opareteFileMessage").hide(150);
			}, 1500);
 		},
 		failed:function(){
 			with(deleteAlertNotice){
	 			$("#alertDeleteModel").modal("hide");
 			}
 		},
 		deleteOne:function(rid){
 			with(deleteAlertNotice){
	 			$.getJSON( window.featureManual.option.oprateUrl+"?func=deleteResource",{"rid":rid},function(data){
	 				if(data.result){
	 					success();
	 					var rids=new Array();
	 					rids.push(rid);
	 					featureManual.moveFile(rids);
	 					current_data_loader.reloadTagCount();
	 				}else{
	 					deleteAlertNotice.failed();
	 					alert(data.message);
	 				}
	 				afterDelete();
	 			});
 			}
 		},
 		deleteSeleted:function(rids){
 			var o = new Object();
			o.rids = rids;
			$.getJSON(window.featureManual.option.oprateUrl+"?func=deleteResources",o,function(data){
				if(data.result){
					deleteAlertNotice.success();
					window.featureManual.result.moveFile(o.rids);
					validateSelectedOperate();
				}else{
					deleteAlertNotice.failed();
					alert(data.message);
				}
				deleteAlertNotice.afterDelete();
			});
 		}
 		
 	};
 	
 	//删除
	$("li.delete_item").die().live('click',function(){
		var i = window.featureManual.getFileItem($(this));
		var item =  window.featureManual.renderData($(this));
		deleteAlertNotice.showDelete([item.rid]);
	});
 	
	$("#alertDeleteModel #okAlertContent").live("click",function(){
		deleteAlertNotice.deleteing();
	});
	
	
	//批量删除
	$("#deleteSelected").die().live('click',function(){
		var items = $("div.oper .showSelectedOperate:checked");
		if(!items||items.length<1){
			alert("请选择要删除的文件！");
			return ;
		}
		var rids = new Array();
		$.each(items,function(index,item){
			var i = window.featureManual.renderData($(item));
			rids.push(i.rid);
		});
		deleteAlertNotice.showDelete(rids);
	});
	
	
 	
 	
 	
 	
	$('a.filePath').die().live('click',function(){
		resetFilter();
		var data = window.featureManual.renderNavData($(this));
		window.featureManual.setLoadPathHash('in',data.rid,null);
	});
	$('li.allFiles').die().live('click',function(){
		resetFilter();
		location.hash='';
		$("#addFolder").show();
	});
 	
	//点击文件名进入
	$('#fileItemDisplay h2 a.fileName').die().live('click',function(event){
		var data = window.featureManual.renderData($(this));
		if(data.itemType=="Folder"){
			resetFilter();
			window.featureManual.setLoadPathHash('add',data.rid,null);
		}else{
			var url = window.featureManual.option.teamHome+"/r/"+data.rid;
			window.open(url);
		}
		 event.stopPropagation();
		
	});
	
	
	function resetSort(){
		$("#sortMenu b.ico-radio-checked").removeClass("ico-radio-checked");  
		   $("#sortMenu b.ico-radio:first").addClass("ico-radio-checked");    
		   $(".sortTitle").text( $("#sortMenu .sortName:first").text());
	}

	function resetFilter(){
		resetSort();
		$("div.filterBoard .search_reset").attr("disable",true);
		$("div.filterBoard input[name=search_input]").val("过滤");
		$("div.filterBoard input[name=search_input]").addClass("standby");
	}
 	
	// intro steps begin
		$("#mask_common_1").css({
			"width":$(document.body).outerWidth(), //window.innerWidth
			"top":0 - $("#body.ui-wrap.wrapper1280").offset().top,
			"left":0 - $("#body.ui-wrap.wrapper1280").offset().left -11,
		});
		
		
		var coverStyle = setInterval(function(){
			if($(document.body).outerHeight() > $(window).height()){ 
				$("#mask_common_1").css({
					"height":$(document.body).outerHeight(),
				});
			}
			else{
				$("#mask_common_1").css({
					"height":window.innerHeight,
				});
			}
		},20);
		
		
		$("#macro-innerWrapper").css({"z-index":"51"});
		var step;
		if ($("#teamConfig").length > 0) {
			totalStep = 4;
		}
		else {
			totalStep = 3;
		}
		
		$.ajax({
			//url:'http://localhost:8080/dct/system/userguide',
			url:site.getURL('userguide',null),
			type:'POST',
			data:"func=get&module=common",
			success:function(data){
				data = eval("("+data+")");
				step = data.step;
				if(step < totalStep) {
					//showTheVeryStep(step);
					if ($("#teamConfig").length > 0) {
						$("#mask_common_1").show();
						$("#teamConfig").addClass("isHighLight");
						$(".intro_step#intro_common_1").css({
							"top":$("#banner-innerWrapper #teamConfig").offset().top - 167,
							"left":$("#banner-innerWrapper #teamConfig").position().left - 40,
						});
						$("#intro_common_1").show();
						$("#intro_common_2").hide();
					}
					else {
						$("#mask_common_1").show();
						$("#intro_common_2").show();
					}
					// view the unmanaged team before managed team.vera
					if (step == 3) {
						$("#intro_common_1").show();
						$("#intro_common_2").hide();
						$("#intro_common_1 #Iknow_common_1").html("完成");
						$("#intro_common_1 a.closeMe").remove();
						
						$("#Iknow_common_1").click(function(){
							$(this).parent().hide();
							$(this).parent().next().hide();
							$("#mask_common_1").hide();
							$(".isHighLight").removeClass("isHighLight");
							step = totalStep;
							postStep(step);
						});
					}
				}
			},
			error:function(){
				step = 0;
			},
			statusCode:{
				450:function(){alert('会话已过期,请重新登录');},
				403:function(){alert('您没有权限进行该操作');}
			}
		});
		
		
		/*if 0 < step < totalStep , this function is very useful*/
		var count = 1;
		function showTheVeryStep(step){
			$("#mask_common_1").show();
			//$("#intro_common_" + (count +1)).show();
			$(".isHighLight").removeClass("isHighLight");
			$(".readyHighLight" + count).addClass("isHighLight");
		} 
		
		$(".Iknow").click(function(){
			$("#mask_common_1").show();
			count++;
			$(this).parent().hide();
			$(this).parent().next().show();
			$(".isHighLight").removeClass("isHighLight");
			$(".readyHighLight" + count).addClass("isHighLight");
		});
		
		$("#Iknow_common_5").click(function(){
			$(this).parent().hide();
			$("#mask_common_1").hide();
			$(".isHighLight").removeClass("isHighLight");
			step = totalStep;
			postStep(step);
		});
		
		$(".closeMe").click(function(){
			$(this).parent().hide();
			$("#mask_common_1").hide();
			$(".isHighLight").removeClass("isHighLight");
			step = totalStep;
			postStep(step);
		})
		
		function postStep(step){
			$.ajax({
				//url:site.getURL('tag',null),
				url:site.getURL('userguide',null),
				type:'POST',
				data:"func=update&module=common&step="+step,
				success:function(data){},
				error:function(){},
				statusCode:{
					450:function(){alert('会话已过期,请重新登录');},
					403:function(){alert('您没有权限进行该操作');}
				}
			});
		}
		
		// intro steps end
     
});
