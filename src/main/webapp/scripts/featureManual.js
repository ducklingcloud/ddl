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
								/*$(this).parent(".filterBoard").css({"width":"98px"});*/
							}else{
								$("#sortDivId").show();
								/*$(this).parent(".filterBoard").css({"width":"383px"});*/
							}
							
							if(!d.showSearch){
								$("#resourceList-search").hide();
								/*$("#resourceList-search").parent(".filterBoard").css({"width":"98px"});*/
							}else{
								$("#resourceList-search").show();
								/*$("#resourceList-search").parent(".filterBoard").css({"width":"383px"});*/
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
							$(v).html("<li class='allFiles'><a>所有文件</a> / 图片</li>");
						}else if(type=='DFile'){
							$(v).html("<li class='allFiles'><a>所有文件</a> / 文件</li>");
						}else if(type=='DPage'){
							$(v).html("<li class='allFiles'><a>所有文件</a> / 协作文档</li>");
						}
						$("#showAllFiles").parent().addClass("current");
					}
				}else{
					$(v).html("<li class='allFiles'><a>所有文件</a></li>");
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
			 $(".selectedOperGroup #addTags").addClass("disableBtn");
		 }else{
			 $(".selectedOperGroup #addTags").removeClass("disableBtn");
		 }
		 
		 if(!canShowCopyBtn()){
			 $(".selectedOperGroup #copySelected").addClass("disableBtn");
		 }else{
			 $(".selectedOperGroup #copySelected").removeClass("disableBtn");
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

	return containFloder();
}

function containFloder(){
	var result=true;
	$(".showSelectedOperate:checked:not(#checkAllFiles)").each(function(i,item){
		if($(item).parents("li.files-item:first").find("input.itemType").val()=='Folder'){
			result=false;
			return;
		}
	});
	return result;
}

function canShowCopyBtn(){
	
	return containFloder();
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




