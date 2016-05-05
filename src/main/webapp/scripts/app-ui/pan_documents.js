var parentRidArr={};
//用于记录当前viewModel 'table' 'grid'
var currViewModel='table';
/*
 * featureMannual.js
 */

$.template("navBarTemp","<li class='active'>&nbsp;>&nbsp;<a class='filePath'>{{= fileName}}</a><input type='hidden' class='rid' value='{{= rid}}'></li>");
$.template('ss',"<div class='files-item'><div class='oper'><input type='checkbox'><div class='iconLynxTag icon-checkStar ' title='标记为星标文件' rid='{{= rid}}'></div></div><input type='hidden' class='rid' value='{{= rid}}'><input class='parentRid' value='{{= parentRid}}'type='hidden' > </div>");
var sbox = new searchBox({
	container : $('#resourceList-search'),
	standbyText: '搜索文件'
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
window.pageTokenKey= createKeyCode();
window.featureManual={
	init:function(){
		initLeftMenu();
		featureManual.initPageParam();
		function initLeftMenu(){
			var queryType=getQueryType();			
			if(queryType!=""&&queryType!=null){
				$(".myNavList .current").removeClass("current");
				if(queryType=='showFileByType'){
					var queryFileType=getQueryFileType();
					if(queryFileType=='DFile'){
						$('a#showTeamFiles').parent("li").addClass('current');
					}else if(queryFileType=='Picture'){
						$('a#showTeamPicture').parent("li").addClass('current');
					}else if(queryFileType=='DPage'){
						$('a#showTeamPages').parent("li").addClass('current');
					}
				}else{
					$(".myNavList a[queryType='"+queryType+"']").parent("li").addClass("current");
				}
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
			featureManual.editorFolderName(vs,"createFolder");
		});
		//banding文件名修改事件
		$('#resourceList .editFileName .updateFolder').die().live('click',function(){
			//解决双击重复提交问题
			var t  = $(this);
			if(t.attr("disable")=='disable'){
				return ;
			}
			t.attr("disable","disable");
			var span = $(this).parents('span.editFileName');
			var type = $(span).find("input.opType").val();
			var fileName = $.trim($(span).find('input.fileNameInput').val());
			if(!fileName||fileName==''){
				showMsg("文件名不能为空","warning");
				hideMsg(3000);
				t.removeAttr("disable");
				return;
			}
			if(validateFolderName(fileName)){
				showMsg("文件夹中不能出现：:?\\ /*<>|\"","warning");
				hideMsg(3000);
				t.removeAttr("disable");
				return;
			}
			var d = new Object();
			if(featureManual.renderData(span).itemType=='DFile'){
				var f = featureManual.renderData(span).fileName;
				if(f.lastIndexOf(".")>-1){
					fileName = fileName +f.substring(f.lastIndexOf("."));
				}
			}
			if(fileName.length>200){
				showMsg("文件名称不能超过200个字符","warning");
				hideMsg(3000);
				t.removeAttr("disable");
				return false;
			}
			
			d.fileName=fileName;
			d.rid=featureManual.renderData(span).rid;
			d.parentRid=featureManual.renderData(span).parentRid;
			if(location.hash.indexOf("keyWord")>-1){
				d.isSeachResult = "true";
			}
			d.func=type;
			$.ajax({
				url:option.oprateUrl,
				data : d,
				type : "post",
				dataType:"json",
				success :function(data){
					t.removeAttr("disable");
					if(!data.result){
						showMsg(data.message,"error");
						hideMsg(3000);
						if(data.errorCode&&data.errorCode=='errorName'){
							
						}else{
							featureManual.getFileItem(span).find('.editFileName .cancelFolder').trigger('click');
						}
						return;
					}
					var d = featureManual.result.renderLi(data.resource);
					var file = featureManual.getFileItem(span);
					$(file).replaceWith(d);
					if(type=='createFolder'){
						featureManual.updateTotalSize('addFolder','add',1);
					}
				},
				statusCode:{
					450:function(){
						alert('会话已过期,请重新登录');
						window.location.reload();
					},
					403:function(){
						t.removeAttr("disable");
						alert('您没有权限进行该操作');
					}
				}
			});
		});
		
		function validateFolderName(name){
			var reg = /[:\\\/<>*?|"]/;
			return name.match(reg);
		}
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
				location.href = site.getURL("panDownload",item.rid); 
			}
		});
		//查看历史信息
		$("li.preview_history").die().live('click',function(){
			var item = featureManual.renderData($(this));
			if(item.itemType=='DFile'){
				var url = featureManual.option.teamHome+"/history?path="+item.rid;
				window.open(url);
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
				var d = featureManual.result.renderLi(item);
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
			if(fileName.lastIndexOf(".")>-1){
				fileName=fileName.substring(0,fileName.lastIndexOf("."));
			}
		}
		var editor = "<span class='editFileName'><span class='" + icon + "'></span>"  +
					 " 	<input class='fileNameInput' type='text' value=\""+fileName+"\">" +
					 "	<a class='btn btn-mini btn-primary updateFolder'><i class='icon-ok icon-white'></i></a>" +
					 "	<a class='btn btn-mini cancelFolder'><i class='icon-remove'></i></a>"+
					 "	<input type='hidden' class='foldOriganlName' value=\""+fileName+"\">" +
					 "	<input type='hidden' class='opType' value=\""+type+"\">" +
					 "</span>";
		$(folder).find('h2 a.fileName').hide();
		$(folder).find('h2').append(editor);
		$(folder).find('.fileNameInput').select();
	},
	updateTotalSize:function(method,type,num){
		//更新操作产生当前目录个数变化
		var s = $('#totalSize').attr("size");
		if(type=='add'){
			s = num+parseInt(s);
			$('#totalSize').attr("size",s);
			$('#totalSize span').html(" ("+s+")");
			$('#totalSize span').attr('title',s+"文件/文件夹");
		}else if(type=='delete'){
			s = s-num;
			$('#totalSize').attr("size",s);
			$('#totalSize span').html(" ("+s+")");
			$('#totalSize span').attr('title',s+"文件/文件夹");
		}else if(type=='update'){
			s = num;
			$('#totalSize').attr("size",s);
			$('#totalSize span').html(" ("+s+")");
			$('#totalSize span').attr('title',s+"文件/文件夹");
		}
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
  					$('#notice').addClass('large').text('未搜索到相关文件').show();
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
  					if(featureManual.currentDirRid.length>1){
  						$('#notice').addClass('large').html('<p class="emptyFolder"></p>当前文件夹下没有文件<p class="dragHint">点击上传按钮或将文件拖放到此窗口即可上传</p>').show();
  					}else{
  						$('#notice').addClass('large').html('<p class="emptyFolder"></p>您的个人空间（同步版Beta）还没有文件<p class="dragHint">您可以点击上传按钮或将文件拖放到此窗口即可上传</p>').show();
  						/*$('#no-result-helper').removeAttr('style');*/
  					}
  				}
  			},
  			noMore: function() {
  				$('#notice').removeClass('loading');
  				$('#load-more-items').hide();
  				$('#no-result-helper').attr('style','display:none');
  				setTimeout(function(){
  					$('#notice').removeClass('large').text('没有更多文件了').show();
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
  	initPageParam:function(){
  		var keyWord=getKeyWord();
  		var sortType=getSortType();
  		if(keyWord){
  			$("div.filterBoard input[name=search_input]").val(keyWord);
  		}
  		if(sortType){
  			$("#sortMenu li a[sorttype='"+sortType+"']").trigger('click');
  			$("#sortMenu b.ico-radio-checked").removeClass("ico-radio-checked");  
  			$("#sortMenu li a[sorttype='"+sortType+"']").parent("li").find("a b.ico-radio").addClass("ico-radio-checked");    
  		}
  	},
	currentDirRid:0,
	currentOrder:'',
	option :{},
	result :{},
	isLoading : false,
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
				initViewModel();
				var data = new Object();
				data.rid=parentId;
				var hashArr=getHashArray();
				if(param){ $.extend(data,param); }
				$.extend(data,{"path":getArrayParam(hashArr,"path")});
				$.extend(data,{"sortType":getArrayParam(hashArr,"sortType")});
				$.extend(data,{"tokenKey":window.pageTokenKey});
				$.extend(data,{"keyWord":getKeyWord()});
				
				//加载新数据前清空当前列表
				with(result){
					cleanFile();
				}
				
				//提前将根路径改变改变，获得数据后重新画路径
				featureManual.result.renderNavBar();
				featureManual.notice_handler.loading();
				$.ajax({
					url : option.queryUrl,
					"data" : data,
					type : "post",
					dataType:"json",
					async:true,
					success : function(d){
						if(d.success=='false'){
							showMsg(d.message,"error");
							hideMsg(3000);
							$('#load-more-items').hide();
							$('#notice').addClass('large').hide();
							return;
						}
						if(d.tokenKey!=window.pageTokenKey){
							return;
						}
						with(result){
							$("#fileItemDisplay #resourceList").hide();
							$(".tableHeader").hide();
							featureManual.notice_handler.readyToLoad();
							renderFile(d.children);
							refreshNextBeginNum(d.nextBeginNum);
							renderNavBar(d);
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
//								$("#sortDivId").hide();
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
							if(!d.unshowSort){
								$("a#sort").css("visibility","visible");
								/*$("#resourceList-search").parent(".filterBoard").css({"width":"98px"});*/
							}else{
								$("a#sort").css("visibility","hidden");
								/*$("#resourceList-search").parent(".filterBoard").css({"width":"383px"});*/
							}
							if(d.isSearch){
								$("div.tableHeader").addClass("searchResult");
								$("div.search-col").show();
							}else{
								$("div.tableHeader").removeClass("searchResult");
								$("div.search-col").hide();
							}
							$(".uidTooltip").tooltip();
							renderOrder(d.order);
						}
					},
					statusCode:{
						450:function(){
							alert('会话已过期,请重新登录');
							window.location.reload();
						},
						403:function(){alert('您没有权限进行该操作');}
					},
					complete: function(){
						window.featureManual.isLoading= false;
					}
				});
			},
			loadAppendFiles:function(beginNum){
				var arr=getHashArray();
	        	replaceArrayParam(arr,"begin",beginNum);
	        	replaceArrayParam(arr,"sortType",currentOrder);
	        	var data = new Object();
	        	for(var i=0;i<arr.length;i++){
	        		$.extend(data, arr[i]);
	        	}
	        	$.extend(data, {"tokenKey":window.pageTokenKey});
	        	featureManual.notice_handler.loading();
				$.ajax({
					url : option.queryUrl,
					"data" : data,
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
							renderNavBar(d);
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
						$(".uidTooltip").tooltip();
						galleryAppend(d.children);
					},
					statusCode:{
						450:function(){
							alert('会话已过期,请重新登录');
							window.location.reload();
						},
						403:function(){alert('您没有权限进行该操作');}
					}
				});
			},
			renderOrder : function(order){
				if(order){
					var sortName= $("a.sortFiles[sorttype='"+order+"'] span.sortName").text();
					if(sortName){
						$("a.sortFiles b.ico-radio.ico-radio-checked").removeClass("ico-radio-checked");
						var v = $("a.sortFiles[sorttype='"+order+"']");
						$(v).find("b.ico-radio").addClass("ico-radio-checked");
						$(".sortTitle").text(sortName);
						currentOrder = order;
					}
				}
			},
			renderFile : function(data){
				var v = $("#"+option.display);
				$(v).html("");
				if(data.length==0){
					$("#fileItemDisplay #resourceList").hide();
					$(".tableHeader").hide();
				}else{
					$("#fileItemDisplay #resourceList").show();
					showTableHead();
				}
//				$.tmpl('fileItemTemp', data).appendTo(v);
				featureManual.result.renderLi(data).appendTo(v);
				validateSelectedOperate();
			},
			renderLi:function(data){
				var obj=$("#fileItemTemp").tmpl(data);
				if(getViewModel()=='grid'){
					$(obj).filter("li.files-item").each(function(index,item){
						if($(item).find('.bmp,.jpg,.png,.gif').length>0){
							replaceHeadImg($(item).find('.bmp,.jpg,.png,.gif'));
						}else{
							$(item).find(".headImg").addClass("thumb");
						}
						replaceFileName($(item));
					});
				}
				return obj;
			},
			cleanFile:function(){
				var v = $("#"+option.display);
				$(v).html("");
			},
			renderAppendFile : function(data){
				var v = $("#"+option.display);
				featureManual.result.renderLi(data).appendTo(v);
				validateSelectedOperate();
			},
			renderPrePendFile: function(data,optype){
				var queryType=getQueryType();
				if(featureManual.result.canPrePendFile(data,optype)){
					$("#fileItemDisplay #resourceList").show();
					showTableHead();
					featureManual.notice_handler.hide();
					var add = true;
					if($("#"+option.display +" input[type=hidden][value='"+data.rid+"']")[0]!=undefined){
						add = false;
					}
					$("#"+option.display +" input[type=hidden][value='"+data.rid+"']").parents("li:first").remove();
					 $("#"+option.display).show();
					var insert=featureManual.result.renderLi(data);
					if($("#"+option.display+" li.newFolderLi").size()>0){
						$(insert).insertAfter($("#"+option.display+" li.newFolderLi:last"));
					}else{
						$(insert).prependTo($("#"+option.display));
					}
					if(add){
						window.featureManual.updateTotalSize(optype,'add',1);
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
					featureManual.getFileItem($("div.oper input.rid[value='" + data[i] + "']")).remove(); 
				}
				var begin=$("#load-more-items").attr("begin");
				begin=begin-data.length;
				if(begin<=0){
					with(result){
						if(location.hash.indexOf("keyWord")==-1){
							loadFiles();
						}
					}
				}else{
					$("#load-more-items").attr("begin",begin);
				}
				updateTotalCount();
			},
			renderNavBar:function(d){
				var data ;
				var size="";
				if(d){
					data = d.path;
					size = d.total;
				}
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
						$(v).html("<li class='allFiles'><a>所有文件</a></li>");
						$(v).append("<li class='tagColor'>&nbsp;>&nbsp;标签过滤：</li>");
						var tagId = getHashParam("tagId");
						var tagIds=tagId.split('_');
						$.each(tagIds,function(index,item){
							$("a#tag-for-"+item).parent().addClass("chosen");
							var dataTemp=new Object();
							var tagName = $("a#tag-for-"+item).find(".tagTitle").html();
							dataTemp.id=item;
							dataTemp.title=tagName;
							$('#page-tag-nav').tmpl(dataTemp).appendTo($(v));
						});
					}else if(queryType=='showFileByType'){
						var type = getArrayParam(array,'type');
						if(type=='Picture'){
							$(v).html("<li class='allFiles'><a>所有文件</a> / 图片</li>");
						}else if(type=='DFile'){
							$(v).html("<li class='allFiles'><a>所有文件</a> / 文件</li>");
						}else if(type=='DPage'){
							$(v).html("<li class='allFiles'><a>所有文档</a> / 协作文档</li>");
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
							var tempLi=$.tmpl("navBarTemp", item);
							tempLi.insertAfter($(v).find("li:first"));
							var totalWidth=result.getNavWidth();
							//此处要多判断当前目录下级目录变换为“...”后长度是否超长 如果超长直接将当前目录缩为“...”23 为“...”的长度
							if(totalWidth>maxWidth||(index<data.length-1&&23+totalWidth>maxWidth)){
								tempLi.find("a").text("...");
								return false;
							}
						});
					}
				}
				if(d){
					$(v).append("<li id='totalSize' size='"+size+"' ><span style='color:#999' title='"+size+"文件/文件夹'>&nbsp;("+size+")</span></li>");
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
		var path = getArrayParam(array,"path");
		path = path?path:'';
		if(queryType&&!path){
			$.getJSON(window.featureManual.option.oprateUrl+"?func=getPath",{rid:curDirRid},function(data){
				var path = data.ridPath;
				changePath(path);
			});
			$(".tagDiv .chosen").removeClass("chosen");
			$(".myNavList .current").removeClass("current");
			$("#showAllFiles").parent("li").addClass("current");
			$("#addFolder").show();
			return;
		}
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
					changePath(path);
					return;
				}
			}	
		}else if(type=='add'){
			path=path+"/"+curDirRid;
			changePath(path);
			return;
		}else if(type=='all'){
			if(allPath){
				var tmp ='';
				for(var p in allPath){
					tmp=tmp+'/'+p.rid;
				}
				changePath(tmp);
				return;
			}
		}else if(type=='allin'){
			changePath(curDirRid);
			return;
		}else{
			var dir = path.split("/");
			for(var i =0;i<dir.length;i++){
				if(dir[i]==curDirRid){
					var tmp = '';
					for(var t=0;t<=i;t++){
						if(dir[t]!=''&&dir[t]){
							tmp=tmp+"/"+dir[t];
						}
					}
					changePath(tmp);
					return;
				}
			}
		}
	}	
};

function hashChangeF(){
	if(typeof window.featureManual.result.loadFiles =='function'){
		window.featureManual.result.clean();
		window.pageTokenKey=createKeyCode();
		window.featureManual.result.loadFiles();
	}
}
$(window).bind('hashchange',hashChangeF);


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
function updateTotalCount(){
	var count = $("li.files-item").length;
	window.featureManual.updateTotalSize('update','update',count);
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
				o.value= decodeURIComponent(h[1].replace(/\+/g,"%20"));
				array.push(o);
			}
		}
	}
	return array;
}

function buildHash(array){
	var tmp = convertArray2Hash(array);
	window.location.hash=tmp;
}

function convertArray2Hash(array){
	var tmp = '';
	if(array.length>0){
		for(var i=0;i<array.length;i++){
			if(array[i]){
				if(i==0){
					tmp=array[i].key+"="+array[i].value;
				}else{
					tmp=tmp+"&"+array[i].key+"="+array[i].value;
				}
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

function getQueryFileType(){
	return getArrayParam(getHashArray(),"type");
}

function getSortType(){
	return getArrayParam(getHashArray(),"sortType");
}

function getViewModel(){
	if($("#showAsTable").hasClass('chosen')){
		return 'table';
	}else {
		return 'grid';
	}
	return;
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

function changePath(path){
	var a = new Array();
	a.push(buildKeyValueObject("path",path));
	buildHash(a);
}

function getHash(location) {
	if(!location){
		location = window.location;
	}
    var match = location.href.match(/#(.*)$/);
    return match ? match[1] : '';
}
function validateSelectedOperate(){
	 if($(".showSelectedOperate:checked:not(#checkAllFiles)").size()>0){
		 if(!canShowAddTag()){
			 $(".selectedOperGroup #addTags").addClass("disableBtn");
		 }else{
			 $(".selectedOperGroup #addTags").removeClass("disableBtn");
		 }
		 
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

function initViewModel() {
	if(getQueryType()!='showFileByType'||getQueryFileType()!='Picture'){
		if(currViewModel=='grid'){
			$("#showAsGrid").trigger("click");
		}else {
			$("#showAsTable").trigger("click");
		}
	}else{
		showAsGrid();
	}
}


function showAsGrid(){
	 $("#showAsTable").removeClass("chosen");
	 $("#showAsGrid").addClass("chosen");
	 $("#resourceList").removeClass("asTight");
	 $("#resourceList").addClass("asTable");
	 $(".tableHeader").hide();
	showImageSmall();
	replaceAllFileName();
}
function showAsTable(){
	 $("#showAsGrid").removeClass("chosen");
	 $("#showAsTable").addClass("chosen");
	 $("#resourceList").removeClass("asTable");
	 $("#resourceList").addClass("asTight");
	 $(".tableHeader").show();
	 removeImageSmall();
	 backUpFileName();
}

function showTableHead(){
	if(getViewModel()=='grid'){
		$(".tableHeader").hide();
	}else{
		$(".tableHeader").show();
	}
}


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

function createKeyCode(){
	return new Date().getTime().toString();
}


function showImageSmall(){
	 $(".headImg.DFile").filter(":not(.imageTemp)").each(function(index,item){
		 if($(item).hasClass("bmp")||$(item).hasClass("jpg")||$(item).hasClass("png")||$(item).hasClass("gif")){
			 replaceHeadImg($(item));
		 }else{
			 $(item).addClass("thumb");
		 }
	 });
}

function replaceAllFileName(){
	 $("li.files-item").each(function(index,item){
		 replaceFileName($(item));
	 });
}

function replaceHeadImg(item){
	 var dataTemp =$(item).parents("li.files-item").data('tmplItem').data;
	 var getImageStatus=window.featureManual.option.teamHome+"/download/thumbnails?path="+dataTemp.rid+"&size=m";
	 var downloadUrl=window.featureManual.option.teamHome+"/download/thumbnails?path="+dataTemp.rid+"&size=m";
	 if(getViewModel()=='grid'){
		 var imgObj=$("<img src='"+downloadUrl+"'>");
		 imgObj.error(function(){
			 //alert('error');
			 replaceHeadImgByStatus(item);
		 });
		 
	    if(getViewModel()=='grid'){
			 $(item).css("background","");
			 $(item).removeClass("loading");
			 $(item).find("img").remove();
			 $(item).addClass("imageTemp");
			 $(item).append(imgObj);
		 }
	 }
}

function replaceHeadImgByStatus(item){
	var dataTemp =$(item).parents("li.files-item").data('tmplItem').data;
	 var getImageStatus=window.featureManual.option.teamHome+"/download/thumbnails?path="+dataTemp.rid+"&size=m";
	 var downloadUrl=window.featureManual.option.teamHome+"/download/thumbnails?path="+dataTemp.rid+"&size=m";
	 $(item).find("img").remove();
	 $.ajax({
		 url:getImageStatus,
		 data : {},
		 type : "post",
		 dataType:"json",
		 success :function(data){
			 if(getViewModel()=='grid'){
				 if(data.status=='ready'){
					 $(item).css("background","");
					 $(item).removeClass("loading");
					 $(item).append("<img src='"+downloadUrl+"'>");
					 $(item).addClass("imageTemp");
				 }else if(data.status=='not_ready'){
					 $(item).attr('style','');
					 $(item).addClass("loading");
					 $(item).find("img").remove();
					 $(item).addClass("imageTemp");
					 window.setTimeout(function(){
						 replaceHeadImgByStatus(item);
					 },2000);
				 }else{
					 $(item).removeClass("loading");
					 $(item).attr('style','');
					 $(item).addClass("thumb");
				 }
			 }
		 },
		 error:function(){
			 $(item).addClass("thumb");
		 },
		 statusCode:{
			 450:function(){
				 alert('会话已过期,请重新登录');
				 window.location.reload();
			 },
			 403:function(){alert('您没有权限进行该操作');}
		 }
	 });
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

function replaceFileName(li){
	var obj=$(li).find(".fileNameSpan");
	var fileTitleObj=$(li).find(".headImg");
	var fileName=obj.text();
	if(fileTitleObj.hasClass("Folder")){
		fileName=spliceStr(fileName,18);
	}else{
		fileName=spliceFileName(fileName,18);
	}
	obj.text(fileName);
}

function backUpHeadImg(item){
	 $(item).removeClass("imageTemp");
	 $(item).removeClass("thumb");
	 $(item).attr('style','');
	 $(item).find("img").remove();
}

function backUpFileName(item){
	 $("li.files-item").each(function(inxex,item){
		 $(item).find(".fileNameSpan").text( $(item).find("a.fileName").attr("title"));
	 });
}


function removeImageSmall(){
	 $(".headImg.DFile").each(function(index,item){
			 backUpHeadImg($(item));
	 });
}
var galleryData = null;
function galleryAppend(data){
	galleryData = galleryData || new Array();
	$.each(data,function(i,n){
		if(n.contentType.indexOf("image") == 0){
			galleryData.push(assembleImage(n));
		}
	});
};
function assembleImage(res){
	var img = {};
	img.image = site.getURL("panDownloadThumbnails",res.rid+"&size=xl");
	img.thumb = site.getURL("panDownloadThumbnails",res.rid+"&size=m");
	img.fileName = res.fileName;
	img.rid = res.rid;
	img.itemType = "DFile";
	return img;
}
function assembleResource(li){
	var res = {};
	res.rid = li.find("input.rid").val();
	res.fileName = li.find("a.fileName").attr("title");
	return res;
}


/*
 * featureTest.jsp 内嵌代码
 */

$(document).ready(function(){
	
    var actionURL = {
        	"team": $("#teamUrl").val(),
        	"teamHome": $("#teamHome").val(),
        	"recommend": site.getURL("recommend"),
        	"tag": site.getURL("tag"),
         	"files": $("#teamUrl").val(),
         	"upload": $("#teamHome").val()+"/upload",
         	"starmark": site.getURL("starmark")
        };
    
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
	var denotefixed = $('.denote');
	$(window).scroll(function(){
		if ($(document).scrollTop()-pos > -top) {
			denotefixed.css('position', 'absolute');
			denotefixed.css({'top':'35px','right':'3px'});
		}
		else {
			denotefixed.css('position', 'absolute');
			denotefixed.css({'top':'105px','right':'10px'});
		}
	});
	/*vera added end*/
	$("#fileItemTemp").template('fileItemTemp');
	var args = {
			display:"resourceList",
			navBar:"navBarOl",
			queryUrl:actionURL.team + "?func=query",
			oprateUrl:actionURL.team ,
			teamHome :actionURL.teamHome,
			addFolder : "addFolder"
	};
	var featureManual = window.featureManual.getInstance(args);
	if(location.hash){
		featureManual.loadFiles();
	}else{
		featureManual.loadFiles();
	}
	$(".files-item").live("mouseenter ",function(){
		$("span.file-commands").hide();
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
	function moveItem(rid){
		$('#fileBrowserModalLabel').empty();
		$('#fileBrowserModalLabel').append("移动到");
		file_operation = 'move';
		$("#fileBrowserModal").modal();
		original_rid = rid;
	}
	$("li.move_item").live("click",function(){
		moveItem($(this).attr("rid"));
	});
	$("li.copy_item").live("click", function(){
		$('#fileBrowserModalLabel').empty();
		$('#fileBrowserModalLabel').append("复制到");
		file_operation = 'copy';
		$("#fileBrowserModal").modal();
		original_rid = $(this).attr("rid");
		
		showTeamSelWrapper();
	});
	$("#copySelected:not(.disableBtn)").live("click", function(){
		var items = $("div.oper .showSelectedOperate:checked");
		if(!items){
			return ;
		}
		file_operation = 'copySelected';
		$('#fileBrowserModalLabel').empty();
		$('#fileBrowserModalLabel').append("复制到");
		$("#fileBrowserModal").modal();
		original_rid = window.featureManual.currentDirRid || -1;
		originalRids = [];
		$.each(items,function(index,item){
			var i = $(item).parents("li.files-item").data('tmplItem').data;
			originalRids.push(i.rid);
		});
		
		showTeamSelWrapper();
	});

	
	
//	var forder_list = $(".Folder");
//	$.each(forder_list, function(index, item) {
//		$(item).next().droppable({
//			drop: function(event, ui) { // Move the selected item to the folder.
//				// get the original rids.
//				var items = $("div.oper .showSelectedOperate:checked");
//				if(!items){
//					return ;
//				}
//				originalRids = [];
//				$.each(items,function(index,item){
//					var i = $(item).parents("li.files-item").data('tmplItem').data;
//					originalRids.push(i.rid);
//				});
//				file_operation = 'copySelected';
//				targetRid = $(this).attr('class');
//			},
//			create: function(event, ui) {
//			}
//		});
//	});
	
	$(".showSelectedOperate").live("click",function(){
		if (!$(this).attr("checked")) {
			return ;
		}
		// create draggable here.
		$(this).parent().next().next().draggable({
			helper: function() {
				var items_to_move = $("div.oper .showSelectedOperate:checked");
				return $("<p></p>").append(items_to_move.length + "个文件");
			}
		});
		
		// create droppable here.
		var forder_list = $(".Folder");
		$.each(forder_list, function(index, item) {
			$(item).parent().parent().droppable({
				drop: function(event, ui) { // Move the selected item to the folder.
					// get the original rids.
					var items = $("div.oper .showSelectedOperate:checked");
					if(!items){ // nothing to move.
						return ;
					}
					originalRids = [];
					$.each(items,function(index,item){
						var i = $(item).parents("li.files-item").data('tmplItem').data;
						originalRids.push(i.rid);
					});
					
					// get the target rid.
					target_rid = $(this).prev().prev().find("div").attr('rid');
					file_manager_url = actionURL.teamHome + "/fileMove";
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
							'targetRid' : target_rid
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
							target_rid = -1;
					   }
					});
				}
			});
		});
    });
	
	$("#moveSelected").live("click", function(){
		var items = $("div.oper .showSelectedOperate:checked");
		if(!items){
			return ;
		}
		file_operation = 'moveSelected';
		$('#fileBrowserModalLabel').empty();
		$('#fileBrowserModalLabel').append("移动到");
		$("#fileBrowserModal").modal();
		original_rid = window.featureManual.currentDirRid || -1;
		originalRids = [];
		$.each(items,function(index,item){
			var i = $(item).parents("li.files-item").data('tmplItem').data;
			originalRids.push(i.rid);
		});
	});
	
	$("#fileBrowserModal").on("show", function(){
		loadBrowserTree();
	});
	
	$("#fileBrowserModal").on("hide", function(){
		galleryOverlayDown();
		$("#teamSelWrapper").hide();
	});
	
	function showTeamSelWrapper(){
		$("#teamSelWrapper option.currentTeam").attr('selected',true);
		$("#teamSelWrapper").show();
	} 
	
	function loadBrowserTree(teamCode){
		var url = "";
		if(file_operation=='move'||file_operation=='moveSelected'){
			url =  actionURL.teamHome + "/fileMove";
		}else{
			if(!teamCode){
				teamCode = $("#teamSel").val();
			}
			url = (teamCode ? site.getJSPURL(teamCode) : actionURL.teamHome) + "/fileManager";
		}
		$("#file_browser").jstree(
				{
					"json_data" : {
						"ajax" : {
							"url" : url,
							"cache":false,
							"data" : function(n) {
								return {
									"rid" : (n.attr ? n.attr("rid").replace("node_", "") : "/"),
									"func" : "list",
									"originalRid" : original_rid,
									'targetTid' : getSelectedTid()
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
					}
				}).bind("select_node.jstree", function(event, data) {
			target_rid = data.rslt.obj.attr("rid").replace("node_", "");
		});
	};
	
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
			d.tid = getSelectedTid();
			var opUrl=actionURL.files;
			if(file_operation!='move'&&file_operation!='moveSelected'){
				var teamCode = $("#teamSel").val();
				opUrl = (teamCode ? site.getJSPURL(teamCode) : actionURL.teamHome) + "/list";
			}
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
						
						//是否在当前团队新建
						if(isSelectedCurrentTeam()){
							featureManual.renderPrePendFile(data.resource,"newFloder");
						}
						newNode.children("a").click();
					}else{
						alert(data.message);
						newNode.remove();
						fileBrowser.select_node(selectedNode);
					}
				},
				statusCode:{
					450:function(){
						alert('会话已过期,请重新登录');
						window.location.reload();
					},
					403:function(){alert('您没有权限进行该操作');}
				},
				error: function(){
					alert("请求错误,请稍候再试.");
				}
			});
		}
		
	});
	
	//是否选择当前团队
	function isSelectedCurrentTeam(){
		return $("#teamSel").val() === "pan";
	}
	
	//团队ID
	function getSelectedTid(){
		return $("#teamSel").find("option:selected").attr("id").replace("teamSel_","");
	}

	$("#teamSel").change(function(){
		loadBrowserTree($(this).val());
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
					
					file_manager_url = actionURL.teamHome + "/fileMove";
					if(file_operation == 'move'){
						file_manager_url = actionURL.teamHome + "/fileMove";
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
								'targetRid' : target_rid
							},
							dataType:"json",
						   	success: function(data){
						   		$("#opareteFileMessage").removeClass();
								if (data.state==0){
									current_nav = $(".myNavList .current a:first").attr('id');
									if (current_nav == "showAllFiles"&&$(".filteFile li.current").size()<=0) {
										//$("div.iconLynxTag.icon-checkStar[rid='" + original_rid + "']").parent().parent().parent().remove();
										originalRids=[];
										originalRids.push(original_rid);
										featureManual.moveFile(originalRids);
										originalRids=[];
									}
									$("#opareteFileMessage").addClass("alert alert-success");
									window.featureManual.updateTotalSize('deleteFolder','delete',1);
									//是否是画廊的移动操作
									if(isGalleryShow() && !getQueryType()){
										galleryRemove(Galleria.get(0),original_rid);
									}
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
						file_manager_url = actionURL.teamHome + "/fileManager";
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
								'targetTid' : getSelectedTid()
							},
							dataType:"json",
						   	success: function(data){
							   $("#opareteFileMessage").removeClass();
							   if(data.type=='meepoCopy'){
								   $("#opareteFileMessage").hide();
								   if(!data.taskId){
									   $("#opareteFileMessage").html("复制错误");
										$("#opareteFileMessage").show();
										window.setTimeout(function(){
											$("#opareteFileMessage").hide(150);
										}, 1500);
									   return;
								   }
								   var d = new PipeCopy({"showTable":"pipeShowTable","taskId":data.taskId,"url":file_manager_url,
									   "initCallback":function(){ unCheckedAll(); },
									   "closeCallback":function(finished){
										   if(!finished){showMsgAndAutoHide('系统将会自动帮您完成复制.','block',5000); }
										},
								   });
								   d.showCopyStatus(data);
								   d.getCoyeStatus();
								   return;
							   }
								if (data.state == 0) {
									$("#opareteFileMessage").addClass("alert alert-success");
									if(isSelectedCurrentTeam()){
										featureManual.renderPrePendFile(data.resource,'copyFile');
									}
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
					file_manager_url = actionURL.teamHome + "/fileMove";
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
								'targetRid' : target_rid
							},
							dataType:"json",
						   	success: function(data){
						   		$("#opareteFileMessage").removeClass();
								if (data.state==0){
									current_nav = $(".myNavList .current a:first").attr('id');
									if (current_nav == "showAllFiles"&&$(".filteFile li.current").size()<=0) {
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
						file_manager_url = actionURL.teamHome + "/fileManager";
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
								'targetTid' : getSelectedTid()
							},
							dataType:"json",
						   	success: function(data){
						   		$("#opareteFileMessage").removeClass();
						   		if(data.type=='meepoCopy'){
									   $("#opareteFileMessage").hide();
									   if(!data.taskId){
										   $("#opareteFileMessage").html("复制错误");
											$("#opareteFileMessage").show();
											window.setTimeout(function(){
												$("#opareteFileMessage").hide(150);
											}, 1500);
										   return;
									   }
									   var d = new PipeCopy({"showTable":"pipeShowTable","taskId":data.taskId,"url":file_manager_url,
										   "initCallback":function(){ unCheckedAll(); },
										   "closeCallback":function(finished){
											   if(!finished){showMsgAndAutoHide('系统将会自动帮您完成复制.','block',5000); }
											},
									   });
									   d.showCopyStatus(data);
									   d.getCoyeStatus();
									   return;
								   }
								if (data.state == 0) {
									$("#opareteFileMessage").addClass("alert alert-success");
									$.each(data.resourceList,function(index,item){
										if(isSelectedCurrentTeam()){
											featureManual.renderPrePendFile(item,'copyFile');
										}
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
//			var url = site.getURL("tag",null);
//			ajaxRequest(url,"func=refreshTagCount",function(data){
//				$.each(data,function(index,item){
//					$('#tag-for-' + item.tag_id + ' .tagResCount').text(item.count);
//				}); 
//			});
		},
		addTagForSingleRecord:function(){
			var tagURL = actionURL.tag;
			//var params = {"func":"batchAdd","newTags[]":aTBox.log.create,"existTags[]":aTBox.log.add,"rids[]":aTBox.log.rid};
//			var params = {"func":"batchAdd","newTags[]":getNewTags(),"existTags[]":getExistTags(),"rids[]":aTBox.log.rid};
//			ajaxRequestWithErrorHandler(tagURL,params,function(data){
//				var newTagIndex = [];
//				for (var i=0; i<data.length; i++) {
//					var needAdd=$('#tag-item-'+data[i].item_key+" li[tag_id="+data[i].id+"]").length==0;
//					if(needAdd){
//						$('#page-tag-template').tmpl(data[i]).prependTo('#tag-item-'+data[i].item_key);
//					}
//					if (data[i].isNewTag && arrIndexOf(newTagIndex, data[i].id)==-1) {
//						newTagIndex.push(data[i].id);
//						$('#new-tag-template').tmpl(data[i]).appendTo($('#ungrouped-tag-list'));
//						$("div.noGroupTagTitle").show();
//					}else if(!data[i].isNewTag){
//						var item = $('#tag-for-' + data[i].id + ' .tagResCount');
//						if(needAdd){
//							item.text(parseInt(item.text())+1);
//						}
//					}
//				}
//				addSingleTagDialog.hide();
//				selector.removeItem('all', true);
//				unCheckedAll();
//				loadAllTeamTagsNow();
//			},notEnoughAuth);
		},
		loadAllTeamTags:function(){
//			var url = site.getURL("tag",null);
//			ajaxRequest(url,"func=loadTeamTags",function(data){
//				_tagCache = data;
//				tPool.refresh(_tagCache);
//				aTBox.refresh();
//			});
		},
		renderTag:function(){
//			var url = site.getURL("tag",null);
//			ajaxRequest(url,"func=loadTeamTags",function(data){
//				$(".tagGroupsDiv").remove();
//				$(".noGroupTagTitle").remove();
//				$('#render-tag-all').tmpl(data).appendTo($('#tagSelector'));
//				$("p.tagGroupTitle").addClass("foldable");
//				_tagCache = data;
//				tPool.refresh(_tagCache);
//				aTBox.refresh();
//			});
		}
	});
	
	
	$("a.viewFile").live("click",function(){
		var rid=$(this).attr("rid");
		var url = window.featureManual.option.teamHome+"/r/"+rid;
		window.open(url);
	});
	
	
	var upload_base_url = actionURL.upload;
	var upload_url = upload_base_url + "?func=uploadFiles";
	
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
	    	var self = this;
	    	var dropArea = document.getElementById("bodyElement");
	    	 var dz = new qq.UploadDropZone({
	    		 element: dropArea,
	             onEnter: function(e){
	                 e.stopPropagation();
	                 if(isFolder(e.target)){
	                	 var folder = getFolderElement(e);
	                	 if(!$(folder).hasClass("dropFolder")){
	                		 $(".dropFolder").removeClass("dropFolder");
	                		 $("body").removeClass("drag");
	                		 $("body .dragCover").hide("fast");
	                		 $("body .dragCoverBox").slideUp("fast");
	                		 folder.addClass("dropFolder");
	                	 }
	                 }else{
	                	 $(".dropFolder").removeClass("dropFolder");
	                	 $("body").addClass("drag");
	                	 $("body.drag .dragCover").show("fast");
	                	 $("body .dragCoverBox").slideDown("fast");
	                 }
	             },
	             onLeave: function(e){
	                 e.stopPropagation();
	             },
	             onLeaveNotDescendants: function(e){
	            	 $("body").removeClass("drag");
	            	 $("body .dragCover").hide("fast");
	            	 $("body .dragCoverBox").slideUp("fast");
	            	 $(".dropFolder").removeClass("dropFolder");
	             },
	             onDrop: function(e){
	            	 e.stopPropagation();
	            	 $("body").removeClass("drag");
	            	 $("body .dragCover").hide("fast");
	            	 $("body .dragCoverBox").slideUp("fast");
	            	 $(".dropFolder").removeClass("dropFolder");
	            	 
	            	 var length = e.dataTransfer.files.length;
	            	 if(length==0){
	            		 //IE
	            		 showMsgAndAutoHide('暂不支持文件夹上传 ！','error',3000);
         				return;
	            	 }
	            	  for (var i = 0; i < length; i++) {
	            		//chrome
	            		if(e.dataTransfer.items){
	            			var entry = e.dataTransfer.items[i].webkitGetAsEntry();
	            			if (entry.isDirectory) {
	            				showMsgAndAutoHide('暂不支持文件夹上传 ！','error',3000);
	            				return;
	            			}
	            		}else{
	            			//firefox
	            			var f = e.dataTransfer.files[i];
	            			if (!f.type && f.size%4096 == 0 && f.size <= 102400) {
	        			        //file is a directory
	        			    	showMsgAndAutoHide('暂不支持文件夹上传 ！','error',3000);
	            				return;
	            			}
	            		}
	            	  }
	            	 if(isUploadToFolder(e)){
	            		 self._uploadFileList(e.dataTransfer.files,isUploadToFolder(e));   
	            	 }else{
	            		 self._uploadFileList(e.dataTransfer.files);    
	            	 }
	            	 
	             }
	    	 });
	    }
	});
	
	function isFolder(target){
		if($(target).hasClass("files-item")){
			return haveFolderData($(target));
		}else{
			var v = $(target).parents("li.files-item");
			return haveFolderData(v);
		}
	}
	
	function haveFolderData(p){
		if(!(p[0]===undefined)){
			var data = p.data('tmplItem').data;
			if(data.itemType=='Folder'){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	function getFolderElement(e){
		var target = e.target;
		if($(target).hasClass("files-item")){
			return $(target);
		}else{
			return $(target).parents("li.files-item");
		}
	}
	
	function isUploadToFolder(e){
		if(e.target){
			var p = $(e.target).parents("li.files-item");
			if(!(p[0]===undefined)){
				var data = p.data('tmplItem').data;
				if(data.itemType=='Folder'){
					return {'parentRid':data.rid};
				}else{
					return false;
				}
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
     
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
         '<a class="qq-upload-cancel" href="#">取消</a>' +
         '<span class="qq-upload-failed-text">失败</span>' +
    	 '</li>', 
         action: upload_url,
         maxConnections:1,
         statisticAction: '',//actionURL.teamHome + "/statistics/upload", // edit by zhoukang: 上传数据收集
         params:parentRidArr,
         onComplete:function(id, fileName, data){
         	uploadedFiles[index] = data;
         	index ++;
         	current_data_loader.renderTag();
         },
         debug: true,
         showMessage: function(message){
        	 showMsgAndAutoHide(message,'error',5000);
         }
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
         '<a class="qq-upload-cancel" href="#">取消</a>' +
         '<span class="qq-upload-failed-text">失败</span>' +
    	 '</li>', 
         action: upload_url,
         params:parentRidArr, 
         onComplete:function(id, fileName, data){
         	uploadedFiles[index] = data;
         	index ++;
         	current_data_loader.renderTag();
         },
         debug: true
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
    	 var hash = location.hash;
    	 if(hash){
    		 var arr=new Array();
    		 buildHash(arr);
    	 }else{
    		 hashChangeF();
    	 }
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
     
     $(".iconLynxTag.icon-checkStar").live("click",function(){
    	 if($(this).hasClass("checked")){
    		removeStar($(this));
    	 }else{
    		 addStar($(this));
    	 }
     });
     
     function addStar(object){
    	 var rid=$(object).attr("rid");
    	 $.ajax({
				url:actionURL.starmark + "?func=add",
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
				url:actionURL.starmark + "?func=remove",
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
     
     function shareFile(res){
    	 prepareRecommend(actionURL.recommend + "?func=prepareRecommend&itemType="+res.itemType+"&rid="+res.rid,res.rid,res.fileName,res.itemType);
     }
     
     $(".share-file").die().live("click",function(){
    	 var d = window.featureManual.renderData($(this));
    	 var team = actionURL.teamHome;
         prepareShareResource(team+"/shareResource",d.rid,d.fileName,"opareteFileMessage"); 
     });
     
     $(".share-file-none").die().live("hover",function(e){
    	 if(e.type=='mouseenter'){
    		 $(this).tooltip("show");
    	 }else if (e.type=='mouseleave'){
    		 $(this).tooltip("hide");
    	 }
     });
     
     $("#shareFiles").die().live("click",function(){
    	 var items=new Array();
    	 $(".showSelectedOperate:checked:not(#checkAllFiles)").each(function(index,item){
    		 items.push(window.featureManual.renderData($(item)));
    	 });
    	prepareRecommendRids(actionURL.recommend +"?func=prepareRecommend",items);
     });
     
    //--------------------------------tag start---------------------------------
    
    $('input.tagPoolAutoShow').tokenInput(actionURL.tag + "?func=loadTeamTags&type=nogroup", {
		theme:"facebook",
		hintText: "输入标签名称，以逗号或回车结束",
		searchingText: "正在搜索……",
		noResultsText: "没有该标签，输入逗号或回车添加",
		preventDuplicates: true
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
// 		var rids = new Array();
// 		try{
//	 		rids[0] = window.featureManual.renderData($(this)).rid;
// 		}catch(e){
// 			
// 		}
// 		if(typeof(rids[0])=='undefined'){
// 	 		rids = selector.getRidArr();
// 		}
//    	var params = {"func":"remove","rid[]":rids,"tagId":$(this).attr("tag_id")};
//    	var url = site.getURL('tag',null);
//    	var $a = $(this);
//		ajaxRequestWithErrorHandler(url,params,function(data){
//    		$a.parent().remove();
//    		$.each(data.rids, function(index, element){
//    			$("ul#tag-item-"+element.rid+" li[tag_id="+data.tagId+"]").remove();
//    		});
//    		var $tagCount = $("a#tag-for-"+data.tagId+" span.tagResCount");
//    		var count = parseInt($tagCount.text());
//    		count = (count-data.rids.length)>=0?(count-data.rids.length):0;
//    		$tagCount.text(""+count);
//    	},notEnoughAuth);
//		$(".tagGroupHorizon ul").find('a[tag_id="'+ $(this).attr("tag_id") + '"]').parent().removeClass("chosen");
    });
	
	
//	$('.delete-tag-nav').live('click',function(){
//		var tagId=$(this).attr('tag_id');
//		$("a#tag-for-"+tagId).parent().removeClass('chosen');
//		getTagData();
//	});
	
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
	
	$("#addTags:not(.disableBtn)").live("click",function(){
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
		showMsgAndAutoHide('请求失败！可能由于以下原因导致此问题：未登录，会话过期或权限不够！','error',3000);
	};
	//current_data_loader.loadAllTeamTags();
	
	function loadAllTeamTagsNow(){
//		var url = site.getURL("tag",null);
//		ajaxRequest(url,"func=loadTeamTags",function(data){
//			_tagCache = data;
//			tPool.refresh(_tagCache);
//			aTBox.refresh();
//		});
	}
	$('#tagSelector .addToQuery').live('click', function(event){
  		event.stopPropagation();
  		if($(this).parents('li:first').hasClass('chosen')){
			$(this).parents('li:first').removeClass('chosen');
		}else{
			$(".myNavList .current").removeClass("current");
			$(this).parents('li:first').addClass('chosen');
		}
  		getTagData();
  	});
    
	var ctrKeyDown=false;//用于记录ctrl是否按下
	/*临时去掉ctrl多选功能 2013年12月3日
	 $(window).keydown(function(e){
	    if(e.ctrlKey){
	    	ctrKeyDown=true;
	    }
	});
	$(window).keyup(function(){
		ctrKeyDown=false;
	});*/
	
	$("a.tag-option").live('click', function(event){
//		if($(this).parent().hasClass('chosen')){
//			if(!ctrKeyDown){
//				$('#tagSelector li.chosen').removeClass('chosen');
//				$(this).parent().addClass('chosen');
//			}else{
//				$(this).parent().removeClass('chosen');
//			}
//		}else{
//			if(!ctrKeyDown){
//				$('#tagSelector li.chosen').removeClass('chosen');
//			}
//			$(".myNavList .current").removeClass("current");
//			$(this).parent().addClass('chosen');
//		}
//		getTagData();
	  });
	
	function getTagData(){
//		var tagId="";
//		var array=new Array();
//		var tagIds=$('#tagSelector li.chosen a.tag-option').each(function(index,item){
//			array.push($(item).attr('value'));
//		});
//		
//		tagId=array.join('_');
//		if(tagId&&tagId!=''){
//			var a = new Array();
//			a.push({'key':"tagId","value":tagId});
//			a.push({"key":'queryType',"value":'tagQuery'});
//			buildHash(a);
//			 $("#addFolder").hide();
//		}else{
//			$(this).parent().removeClass('chosen');
//			location.hash="";
//			 $("#addFolder").show();
//			 $("#showAllFiles").parent().addClass("current");
//		}
//		resetFilter();
	}
	
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
    	 if(bool){
    		 showFilesList();
    	 }
    	 else{
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
 				if(rids.length==1){
 					var d = $("input.rid[value='"+rids[0]+"']");
 					var item =  window.featureManual.renderData(d);
 					$("#alertDeleteModel .alertContent").html("确定要将“"+item.fileName+"”删除吗？");
 				}else{
 					$("#alertDeleteModel .alertContent").html("确定要将这"+rids.length+"项删除吗？");
 				}
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
	 					successCallback(data.result);
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
			$.ajax({
				  type: "POST",
				  url: window.featureManual.option.oprateUrl+"?func=deleteResources",
				  data:o,
				  dataType: "json",
				  success: function(data){
					  if(data.result){
							deleteAlertNotice.success();
							window.featureManual.result.moveFile(o.rids);
							validateSelectedOperate();
						}else{
							deleteAlertNotice.failed();
							var message = "";
							var i=0;
							$.each(data.errorRids,function(index,item){
								var v = $("#"+option.display +" input[type=hidden][value='"+item+"']");
								var da = window.featureManual.renderData($(v));
								if(i==0){
									message+=da.fileName;
								}else{
									message+=","+da.fileName;
								}
							});
							message+="删除失败";
							window.featureManual.result.moveFile(data.sucRid);
							alert(message);
						}
						deleteAlertNotice.afterDelete();
				  }
			});
 		},
 		successCallback: function (result){} // to implement
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
		window.featureManual.setLoadPathHash('allin',data.rid,null);
	});
	$('a.seachResultA').die().live('click',function(){
		resetFilter();
		var data = $(this).attr("path");
		window.featureManual.setLoadPathHash('allin',data,null);
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
			window.featureManual.setLoadPathHash('allin',data.rid,null);
		}else if(data.contentType.indexOf('image') == 0){
			galleryShow(data);
		}else{
			var url = window.featureManual.option.teamHome+"/preview?path="+data.rid;
			window.open(url);
		}
		 event.stopPropagation();
		
	});
	
	$("#alertDeleteModel").on('hide', function () {
		galleryOverlayDown();
	});
	
	var galleryShowIndex = 0;
	Galleria.configure({initialTransition:"pulse",transition:"pulse",thumbnails:"lazy",
		thumbCrop:true,showCounter:false,showInfo:false,carousel:false,clicknext:true});
	
	Galleria.ready(function() {
		var that = this;
		var gb = $("#galleryBox");
		
		that.bind("loadstart", function(e) {
			var gd = e.galleriaData;
			var delBtn = gb.find("a.delete-file"), moveBtn = gb.find("a.move-file");
	        delBtn.css("cursor","default"), moveBtn.css("cursor","default");
	        delBtn.off("click"), moveBtn.off("click");
	        
	        if(getQueryType()!=="myRecentFiles"){
	        	galleryLoadMore();
	        }
	        
			if(e.index == (THUMBS_SIZE-1) && (galleryShowIndex + THUMBS_SIZE_MID) < galleryData.length ){
				galleryRun(gd.rid);
				return false;
			}
			if(e.index == 0 && (galleryShowIndex - THUMBS_SIZE_MID) > 0 ){
				galleryRun(gd.rid);
				return false;
			}
			
	        gb.find("h3").html(gd.fileName);
	        gb.find("a.see-detail").attr("href", window.featureManual.option.teamHome+"/r/"+gd.rid);
	        gb.find("a.magnifier").attr("href", site.getURL("panPreview", gd.rid));
	        gb.find("a.down-file").attr("href", site.getURL("panDownload", gd.rid));
	        gb.find("a.share-file").off("click").on("click",function(){
	        	shareFile(gd);
	        });
	        
	        deleteAlertNotice.successCallback = function(result){
	        	if(result){
	        		galleryRemove(that, gd.rid);
	        	}
			};
		});
		
		that.bind("image", function(e) {
			var gd = e.galleriaData;
			var img = e.imageTarget;
			//first
			if(galleryData[0].rid == gd.rid){
				that.$("image-nav-left").hide(0);
			}else{
				that.$("image-nav-left").show(0);
			}
			//last
			if(galleryData[galleryData.length-1].rid == gd.rid){
				that.$("image-nav-right").hide(0);
				$(e.imageTarget).css("cursor",'default').off("mouseup");
			}else{
				that.$("image-nav-right").show(0);
			}
			
			var delBtn = gb.find("a.delete-file"), moveBtn = gb.find("a.move-file"),
				rotateLeft = gb.find("a.rotate-left"),rotateRight = gb.find("a.rotate-right");
			delBtn.css("cursor","pointer"), moveBtn.css("cursor","pointer");
			delBtn.off("click").on("click",function(){
				galleryOverlayUp();
	        	deleteAlertNotice.showDelete([gd.rid]);
	        });
			moveBtn.off("click").on("click",function(){
				galleryOverlayUp();
	        	moveItem(gd.rid);
	        });
			rotateLeft.off("click").on("click",function(){
				rotator.left(img);
	        });
			rotateRight.off("click").on("click",function(){
				rotator.right(img);
	        });
			rotator.clear();
			
			COMMENT_LOADED = false;
//			if(SIDERBAR_OPEN){
//				loadComments(gd.rid);
//			}
	    });
		that.lazyLoadChunks(THUMBS_SIZE);
	});
	
	function galleryDataInit(activeObj){
		galleryData = new Array();
		$("#"+option.display +" .files-item").each(function(i,n){
			n = $(n);
			if(n.find("span.jpg,span.png,span.gif,span.bmp,span.jpeg").length>0){
				var res = assembleResource(n);
				galleryData.push(assembleImage(res));
			}
		});
	}
	function galleryThumbsData(currnetRid){
		var itemIndex = 0;
		for(var i = 0; i<galleryData.length; i++){
			var item = galleryData[i];
			if(item.rid == currnetRid){
				itemIndex = i;
				break;
			}
		}
		var start = itemIndex > THUMBS_SIZE_MID ? itemIndex - THUMBS_SIZE_MID : 0;
		var end = galleryData.length > THUMBS_SIZE ? start + THUMBS_SIZE : galleryData.length;
		if(end > galleryData.length){
			start = start - (end - galleryData.length);
			end = galleryData.length;
		}
		var thumbsData = galleryData.slice(start,end);
		var showIndex = 0;
		for(var i = 0; i<thumbsData.length; i++){
			var item = thumbsData[i];
			if(item.rid == currnetRid){
				showIndex = i;
				break;
			}
		}
		galleryShowIndex = itemIndex;
		return {"thumbsData" : thumbsData, "showIndex" : showIndex};
	}
	var loadBeginIndex = 30;
	function galleryShow(currentObj){
		galleryDataInit();
		galleryRun(currentObj.rid);
		$("#galleryBox").show();
		loadBeginIndex = parseInt($("#load-more-items").attr("begin"));
		$("body").append("<div id=\"galleryOverlay\" class=\"modal-backdrop\" style=\"z-index:1041;background:#0b0b0b;" +
				"opacity: .9;filter: alpha(opacity=90);\"></div>");
		
		$("body").addClass("hideScroll");
	}
	var NO_PHOTO = site.getJSPURL("scripts/galleria/themes/classic/nophoto.gif");
	function galleryRun(currentRid, showIndex){
		var data = galleryThumbsData(currentRid);
		if(typeof(showIndex) == "undefined"){
			showIndex = data.showIndex;
		}
		Galleria.run('#galleria',{dataSource: data.thumbsData,show:showIndex,dummy:NO_PHOTO});
	}
	function galleryLoadMore(){
		if(galleryData.length < (galleryShowIndex+30)){
			var begin=parseInt($("#load-more-items").attr("begin"));
			if(begin > loadBeginIndex){
				loadBeginIndex = begin;
				window.featureManual.result.loadAppendFiles(begin);
			}
		}
	}
	function galleryRemove(gallery,rid){
		index = gallery.getIndex();
		if(galleryData.length==1){
			galleryHide();
			return;
		}
		var delIndex = 0;
		for(var i=0;i<galleryData.length;i++){
			var item = galleryData[i];
			if(item.rid == rid){
				delIndex = i;
				galleryData.splice(i,1);
				break;
			}
		}
		if(index == galleryData.length){
			index = galleryData.length-1;
		}
		if(delIndex < THUMBS_SIZE){
			delIndex = delIndex || 1;
			galleryRun(galleryData[delIndex-1].rid);
		}else{
			delIndex = delIndex-(index-THUMBS_SIZE_MID);
		}
		if(delIndex == galleryData.length){
			delIndex = galleryData.length-1;
		}
		galleryRun(galleryData[delIndex].rid, index);
	}
	function galleryOverlayUp(){ $("#galleryOverlay").css("z-index",1043); }
	function galleryOverlayDown(){ $("#galleryOverlay").css("z-index",1041); }
	
	function resetSort(){
		$("#sortMenu b.ico-radio-checked").removeClass("ico-radio-checked");  
		   $("#sortMenu b.ico-radio:first").addClass("ico-radio-checked");    
		   $(".sortTitle").text( $("#sortMenu .sortName:first").text());
	}

	function resetFilter(){
//		resetSort();
		$("div.filterBoard .search_reset").attr("disable",true);
		$("div.filterBoard input[name=search_input]").val("搜索文件");
		$("div.filterBoard input[name=search_input]").addClass("standby");
	}
		
	// intro steps begin
		$("#mask_common_1").css({
			"width":$(document.body).outerWidth(), //window.innerWidth
			"top":0 - $("#body.ui-wrap.wrapper1280").offset().top,
			"left":0 - $("#body.ui-wrap.wrapper1280").offset().left -11
		});
		
		
		var coverStyle = setInterval(function(){
			if($(document.body).outerHeight() > $(window).height()){ 
				$("#mask_common_1").css({
					"height":$(document.body).outerHeight()
				});
			}
			else{
				$("#mask_common_1").css({
					"height":window.innerHeight
				});
			}
		},20);
		
		
		$("#macro-innerWrapper").css({"z-index":"51"});
		var step;
		totalStep = 6;
		
		$.ajax({
			//url:'http://localhost:8080/dct/system/userguide',
			url:site.getURL('userguide',null),
			type:'POST',
			data:"func=get&module=common",
			success:function(data){
				data = eval("("+data+")");
				step = data.step;
			},
			error:function(){
				step = 0;
			},
			statusCode:{
				450:function(){
					alert('会话已过期,请重新登录'); 
					window.location.reload();
				},
				403:function(){alert('您没有权限进行该操作');}
			}
		});
		
		
		/*if 0 < step < totalStep , this function is very useful*/
		var count = 1;
		$(".closeMe").click(function(){
			$(this).parent().hide();
			$("#mask_common_1").hide();
			$(".isHighLight").removeClass("isHighLight");
			step = totalStep;
			postStep(step);
		});
		
		function postStep(step){
			$.ajax({
				url:site.getURL('userguide',null),
				type:'POST',
				data:"func=update&module=common&step="+step,
				success:function(data){},
				error:function(){},
				statusCode:{
					450:function(){
						alert('会话已过期,请重新登录');
						window.location.reload();
					},
					403:function(){alert('您没有权限进行该操作');}
				}
			});
		}
		
		//step 5 begin
		$(".featureCover").css({
			"width":$(document.body).outerWidth(),
			"left":($("#content.std.stdRounded").outerWidth() - $(document.body).outerWidth())/2 -3,
		});
		var coverStyle = setInterval(function(){
			if($(document.body).outerHeight() > $(window).height()){ 
				$(".featureCover").css({
					"height":$(document.body).outerHeight(),
				});
			}
			else{
				$(".featureCover").css({
					"height":window.innerHeight,
				});
			}
		},20);
		
		/*close cover*/
		$(".featureContainer .closeMe").click(function(){
			$(".featureCover").hide();
			$(".featureContainer").hide();
			$(".featureNav").hide();
		});
		
		// intro steps end
		
		
		 
		 $('ul.ui-navList li').live({ mouseenter: function () {
				//$('#tip').show();
				 $(this).find("a.addToQuery").show();               
	         }, mouseleave: function () {
	        	 //$('#tip').hide();
	        	 $(this).find("a.addToQuery").hide();    
	         }
        });
		
		 $("#showAsGrid").live("click",function(){
			 showAsGrid();
			 currViewModel=getViewModel();
		 });
		 
		 $("#showAsTable").live("click",function(){
			 showAsTable();
			 currViewModel=getViewModel();
		 });
		 
		 $("div.filterBoard input[name=search_input]").on('input propertychange', function() {
		 	var that = this;
		 	if(!window.featureManual.isLoading){
				var arr=getHashArray();
				replaceArrayParam(arr,"keyWord",$(that).val());
				buildHash(arr);
			}
		});
});