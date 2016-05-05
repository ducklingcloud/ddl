var getFileUrl = "://ddl.escience.cn/system/emailresource?callback=?";
//格式为json数组{"filename":name,"filesize":size,"url":url,"viewurl":viewurl}
//var authString = "h5b/qeS+zcZHewXi2u3aa1Oqq9lNv7ZFCTxe/6sf46nIdwvXe+kwW00Q2S0dIQHiY2PP+wUFhsMz\n6gP+CpY4PZf0jvPsDf3fGm4nXr39dsA=";
var choiceFileInfo=[];
var JQ = jQuery.noConflict();
function getDDLFileHtmlView(data){
	if(data!=null&&data!=undefined){
		choiceFileInfo = data;
	}
	var auth = getUserEmailAuth();
	getDDLFileHtml(0,1,auth);
}
//获取文件提取码,提取链接
function getFetchFileCode(downloadURL,returnFunction){
	var auth = getUserEmailAuth();
	var o = new Object();
//	var v = JQ.ajaxSettings.async;
	o.func="getFetchFileCode";
	o.downloadUrl=downloadURL;
	o.auth=auth;
	var returnData = new Object();
	var url = getFileUrls();
	JQ.getJSON(url,o,function(data){
		if(data.errorMessage){
			alert(data.errorMessage);
		}else{
			returnData={"fetchFileCode":data.fetchFileCode,"url":data.fileURL};
			returnFunction(returnData);
		}
	});
}
function getFileUrls(){
	return getProtocol()+getFileUrl;
}
function getProtocol(){
	var url = location.href;
	if(url.length>5){
		if(url.substring(0,5)=='https'){
			return 'https';
		}else{
			return 'http';
		}
	}else{
		return 'http';
	}
}

var appendedPageInfo=function(info){
	JQ("#pageDisplayUl").html(info);
}

var getDDLFileHtml = function(tid,offer,auth,type){
	var o = new Object();
	if(type=="search"){
		o.func="searchReferableFiles";
		o.term=searchKeyword;
	}else{
		o.func="findUserPersonTeamFiles";
	}
	o.offer=offer;
	o.auth=auth;
	o.tid=tid;
	var url = getFileUrls();
	JQ.getJSON(url,o,function(data){
		if(data.errorMessage){
			alert(data.errorMessage);
			return;
		}
		JQ("#showDdlHtmlDiv").html(JQ("#ddlTableTemp").html());
		if(data.searchWord!=undefined){
			JQ("#searchReferableFiles").val(data.searchWord);
		}
		if(data.message.length>0){
			JQ("#tableTr").render(data.message).appendTo(JQ("#ddlTableBody"));
		}else{ 
			if(type=="search"){
				JQ("#ddlTableBody").html("<tr class='ddl_noResult'><td colspan='4'>未搜索到您创建的文档！</td></tr>");
			}else{
				JQ("#ddlTableBody").html("<tr class='ddl_noResult'><td colspan='4'>您在该团队暂未创建文档！</td></tr>");
			}
		}
		JQ("#showDdlHtmlDiv").show();
		var total = data.total;
		var info="";
		if(total==0){
			info="<li class='current'></li>";
		}else{
			if(total<=5){
				if(total==1){
					info="<li class='current'></li>";
				}else{
					var i=1;
					for(;i<data.page;i++){
						info=info+getPageString(tid,i,auth,i,type)
					}
					info=info+"<li class='current'>"+data.page+"</li>";
					i=data.page+1;
					for(;i<=total;i++){
						info=info+getPageString(tid,i,auth,i,type);
					}
				}
			}else{
				var begin=1;
				if(data.page-2>1){
					info = getPageString(tid,1,auth,"首页",type);
					begin=data.page-2;
				}
				var end = total;
				if(begin==1){
					end=data.page+4-(data.page-begin);
				}else{
					if(data.page+2<=total){
						end=data.page+2;
					}else{
						i=total;
					}
				}
				if(end+2>total){
					begin=data.page-4+(end-data.page);
				}
				var i=begin;
				if(data.page-begin>0){
					info = info+getPageString(tid,data.page-1,auth,"<上一页",type);
				}
				for(;i<data.page;i++){
					info=info+getPageString(tid,i,auth,i,type);
				}
				info=info+"<li class='current'>"+data.page+"</li>";
				i=data.page+1;
				for(;i<=end;i++){
					info=info+getPageString(tid,i,auth,i,type);
				}
				if(total-data.page>0){
					info=info+getPageString(tid,data.page+1,auth,"下一页>",type);
				}
				if(end<total){
					info=info+getPageString(tid,total,auth,"尾页",type)
				}
			}
		}
		appendedPageInfo(info)
		appendTeamInfo(data.teams,data.currentTeam,auth);
		addAllFileIcon();
		initCheckAndFileView();
	});
}
var getPageString = function(tid,offer,auth,name,type){
	return "<li><a href='' onclick='getPageInfo("+tid+","+offer+",0,\""+type+"\");return false'>"+name+"</a></li>";
}
//添加团队信息
var appendTeamInfo=function(teams,currentTeam,auth){
	var info = "";
	if(currentTeam==0){
		info = "<li class='choice' teamId='0'>全部团队</li>";
	}else{
		info = "<li onclick='getPageInfo(0,1,0)' teamId='0'>全部团队</li>"
		
	}
	JQ.each(teams,function(index,item){
		if(item.teamId==currentTeam){
			info = info+"<li class='choice' teamId='"+item.teamId+"'>"+item.teamName+"</li>";
		}else{
			info = info+"<li onclick='getPageInfo("+item.teamId+",1,0)' teamId='"+item.teamId+"'>"+item.teamName+"</li>";
		}
	});
	JQ("#teamInfoUl").html(info);
}
//添加选项信息
JQ(".ddlFileChoise").live("click",function(){
	if(JQ(this).attr("checked")){
		var url = JQ(this).parent().children("input[name='url']").val();
		var viewurl = JQ(this).parent().children("input[name='viewUrl']").val();
		var size = JQ(this).parent().children("input[name='fileSize']").val();
		var name = JQ(this).parent().children("input[name='fileName']").val();
		var data = {"filename":name,"filesize":size,"url":url,"viewurl":viewurl};
		if(endWith(name,"exe")){
			JQ(this).removeAttr("checked");
			alert("您所选择的文件 "+name+" 被禁止上传.\n(禁止上传的文件类型: .exe , .bat )");
			return;
		}
		if(endWith(name,"bat")){
			JQ(this).removeAttr("checked");
			alert("您所选择的文件 "+name+" 被禁止上传.\n(禁止上传的文件类型: .exe , .bat )");
			return;
		}
		if(isBigData(data)){
			getFetchFileCode(data.url,function(dd){
				if(dd){
					data.fetchFileData=dd;
					choiceFileInfo.push(data);
					addFileView(url,name);
				}
			});
		}else{
			choiceFileInfo.push(data);
			addFileView(url,name);
		}
	}else{
		var url = JQ(this).parent().children("input[name='url']").val();
		removeFileView(url);
	}
});

function endWith(src,end){
	if(src==null||src==''){
		return false;
	}
	var i = src.lastIndexOf(".");
	if(i<=0||(i+1)==src.length){
		return false;
	}
	var e = src.substring(i+1).toUpperCase();
	return e==end.toUpperCase();
}
function isBigData(data){
	return isLinkMode(data);
}
//移除选择信息
var removeFileView = function(url){
	JQ.each(choiceFileInfo,function(index,item){
		if(item.url==url){
			choiceFileInfo.splice(index,1);
			return false;
		}
	});
	JQ("li span[name='"+url+"']").parent().remove();
	var checked = JQ("input[name='url'][value='"+url+"']");
	if(checked!=undefined&&checked!=null){
		JQ(checked).parent().children("input[type='checkbox']").removeAttr("checked");
	}
}
var addFileView=function(url,name){
	var info = "<li><span>"+name+"</span><span class='unchosen' name='"+url+"' onclick='removeFileView(\""+url+"\")'>x</span></li>";
	JQ("#displayChoiceFile").append(info);
}
var getPageInfo=function(tid,offer,auths,type){
//	JQ("#showDdlHtmlDiv").html("");
//	JQ("#showDdlHtmlDiv").hide();
	var auth = getUserEmailAuth();
	getDDLFileHtml(tid,offer,auth,type);
}

//添加页面的已选择项，并根据参数判断是否添加fileView
var initCheckAndFileView=function(){
	JQ.each(choiceFileInfo,function(index,item){
		var url = item.url;
		var checked = JQ("input[name='url'][value='"+url+"']");
		if(checked!=undefined&&checked!=null){
			JQ(checked).parent().children("input[type='checkbox']").attr("checked", true);
		}
		addFileView(item.url,item.filename)
	});
	
}
//数据查询
JQ("#searchReferableFiles").live("keypress",function(event){
	var key = event.which;
	if(key==13||event.which == 10){
		searchKeyword = JQ("#searchReferableFiles").val();
		var teamId = JQ("ul#teamInfoUl li.choice").attr("teamid");
		if(teamId==undefined){
			teamId = 0;
		}
		var auths= getUserEmailAuth();
		getDDLFileHtml(teamId,0,auths,"search");
	}
});

var searchKeyword ="";

function addAllFileIcon(){
	JQ("#ddlTableBody>tr").each(function(i){
		if(JQ(this).children(":eq(1)").children("i.ico,i.sico").length<=0){
			var tr = JQ(this).children(":eq(1)");
			var title = tr.attr("title");
			var nameHtml = getIcon(title);
			tr.html(nameHtml+title);
		}
	});
}

function fGetIcon(filename) {
    if (/xls[x]?$/i.test(filename)) return "ico fMSEXCEL";
    if (/(doc[x]?)$|(wps)$/i.test(filename)) return "ico fMSWORD";
    if (/(ppt[x]?$|(dps)$)/i.test(filename)) return "ico fMSPOWERPOINT";
    if (/(zip)$|(rar)$|(tar)$|(gz)$/i.test(filename)) return "ico fARCHIVE";
    if (/bat$/i.test(filename)) return "sico sfBAT";
    if (/exe$/i.test(filename)) return "sico sfEXE";
    if (/pdf$/i.test(filename)) return "ico fPDF";
    if (/swf$/i.test(filename)) return "ico fFLASH";
    if (/txt$/i.test(filename)) return "ico fTEXT";
    if (/mp3$/i.test(filename)) return "sico sfMP3";
    if (/ai$/i.test(filename)) return "sico sfAI";
    if (/psd$/i.test(filename)) return "sico sfPS";
    if (/(jp[e]?g)$/i.test(filename)) return "sico sfJPG";
    if (/gif$/i.test(filename)) return "ico fIMAGE";
    if (/bmp$/i.test(filename)) return "ico fIMAGE";
    if (/xml$/i.test(filename)) return "ico fXML";
    if (/csv$/i.test(filename)) return "ico fCSV";
    if (/png$/i.test(filename)) return "ico fIMAGE";;
    if (/(avi)$|(mpg$)|(miv$)|(mpe$)|(mpeg$)|(vob$)|(mov$)|(asf$)|(wmv$)|(rmvb$)|(dat$)|(qt$)/i.test(filename)) return "ico fMEDIA";
    return "ico fUNKNOWN";
}
function getIcon(fileName){       
	var html='<i class="' +fGetIcon(fileName)+ '"></i>';
	return html;
}


//确定提交按钮
JQ("#ddlSubmitChoiceButton").live("click",function(){
	addFileAsAttactToMail(choiceFileInfo);
	JQ("#showDdlHtmlDiv").html("");
	JQ("#showDdlHtmlDiv").hide();
});
//取消提交按钮
JQ("#ddlCancelChoiceButton").live("click",function(){
	JQ("#showDdlHtmlDiv").html("");
	JQ("#showDdlHtmlDiv").hide();
});
JQ("div.dialogCloseBtn").live("click",function(){
	JQ("#showDdlHtmlDiv").html("");
	JQ("#showDdlHtmlDiv").hide();
});
