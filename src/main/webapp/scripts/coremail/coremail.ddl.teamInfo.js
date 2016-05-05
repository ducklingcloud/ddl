var getFileUrl = "://ddl.escience.cn/system/emailresource?callback=?";
//var authString = "h5b/qeS+zcZHewXi2u3aa1Oqq9lNv7ZFCTxe/6sf46nIdwvXe+kwW00Q2S0dIQHiY2PP+wUFhsMz\n6gP+CpY4PZf0jvPsDf3fGm4nXr39dsA=";
//从coremail传来的数据临时保存 
var ddlDataTmp = new Object();
var JQ = jQuery.noConflict();
function getAllTeamInfo(data,ddlAuth){
	var auth = getUserEmailAuth();
	var o = new Object();
	o.func="getUserAllTeam";
	o.auth=auth;
	ddlDataTmp = data;
	var url = getProtocol()+getFileUrl;
	JQ.getJSON(url,o,function(data){
		if(data.teams.length==1){
			showWaitPage()
			//直接向后台提交
			saveAttachToDDL(ddlDataTmp,data.teams[0].teamId,false,1);
		}else if(data.teams.length<1){
			showWaitPage()
			//直接向后台提交
			saveAttachToDDL(ddlDataTmp,0,false,1);
		}else{
			JQ("#ddlTeamShowBody").html("");
			JQ("#showDdlTeamHtmlDiv").html(JQ("#ddlTeamShowTemp").html());
			JQ("#ddlAllTeamTmpl").render(data.teams).appendTo(JQ("#ddlTeamShowBody"));
			JQ("#ddlTeamShowBody").children().first().find("input[name='teams']").attr("checked","checked");
			JQ("#showDdlTeamHtmlDiv").show();
		}
	});
}

function showWaitPage(){
	JQ("#showddlAttachContext").html("<br/><div style='color: #000;'><img src='"+getProtocol()+"://ddl.escience.cn/jsp/aone/images/loader2.gif'/>正在保存，请稍后</div>");
	JQ("#showDdlSaveAttachShowDiv").show();
}

function showSaveAttachOnType(data,teamId,showType){
	JQ("#nowChoiceTeam").val(teamId);
	if(showType==1){
		attachMessageShow(data);
	}else{
		attachMessageChange(data);
	}
	addAllFileIcon();
}

function attachMessageShow(data){
	JQ("#showddlAttachContext").html("");
	var length = data.length;
	JQ.each(data,function(index,item){
		var tmp = new Object();
		tmp.fileName = item.filename;
		tmp.mid = item.mid;
		if(item.statusCode==1){
			tmp.fileURL = item.attachmentURL
			JQ("#ddlSaveAttachSuccessShow").render(tmp).appendTo(JQ("#showddlAttachContext"));
		}else if(item.statusCode==7){
			tmp.fileURL = item.attachmentURL
			JQ("#ddlSaveAttachRepeatShow").render(tmp).appendTo(JQ("#showddlAttachContext"));
		}else{
			if(item.message&&item.message.indexOf("空间已满")>0){
				var vv = JQ("#ddlSaveAttachErrorShow").render(tmp);
				vv.find(".ddlReSave").parent().html("<font color='red'>保存失败。该团队空间已满！</font");
				vv.appendTo(JQ("#showddlAttachContext"));
			}else{
				JQ("#ddlSaveAttachErrorShow").render(tmp).appendTo(JQ("#showddlAttachContext"));
			}
		}
	});
	
	JQ("#showDdlSaveAttachShowDiv").show();
}
//再次提交结果显示
function attachMessageChange(data){
	if(data.length>0){
		JQ.each(data,function(index,item){
			var midObject = JQ("input[name='attachMid'][value='"+item.mid+"']");
			var midParent = midObject.parent();
			midParent.hide(500,function(){
				//	midParent.remove();
				var tmp = new Object();
				tmp.fileName = item.filename;
				tmp.mid = item.mid;
				if(item.statusCode==1){
					tmp.fileURL = item.attachmentURL
					var v = JQ("#ddlSaveAttachSuccessShow").render(tmp);
					v.hide();
					JQ(midParent).replaceWith(v);
					v.show(500,function(){
						v.removeAttr('style');
						addAllFileIcon();
					});
				}else if(item.statusCode==7){
					tmp.fileURL = item.attachmentURL
					var v = JQ("#ddlSaveAttachSuccessShow").render(tmp);
					v.hide();
					JQ(midParent).replaceWith(v);
					v.show(500,function(){
						v.removeAttr('style');
						addAllFileIcon();
					});
				}else{
					var v = JQ("#ddlSaveAttachErrorShow").render(tmp);
					v.hide();
					JQ(midParent).replaceWith(v);
					v.show(500,function(){
						v.removeAttr('style');
						addAllFileIcon();
					});
				}
			});
		});
	}
}

//再次保存
JQ("a.ddlSaveOther").live("click",function(){
	var th = JQ(this);
	var fileName = JQ(th).parent().parent().children("span[name='ddlFileName']").html();
	var mid = JQ(th).parent().parent().children("input[name='attachMid']").val();
	var teamId = JQ("#nowChoiceTeam").val();
	var data = new Object();
	data.mid=mid;
	data.filename=fileName;
	var ff = new Array();
	ff.push(data);
	var pp = JQ(th).parent().parent();
    pp.html("<input name='attachMid' type='hidden' value='"+mid+"'/><div style='color: #000;'><img src='"+getProtocol()+"://ddl.escience.cn/jsp/aone/images/loader2.gif'/>正在保存，请稍后</div>");
    saveAttachToDDL(ff,teamId,true,2);
})

//重新保存
JQ("a.ddlReSave").live("click",function(){
	var th = JQ(this);
	var fileName = JQ(th).parent().parent().children("span[name='ddlFileName']").html();
	var mid = JQ(th).parent().parent().children("input[name='attachMid']").val();
	var teamId = JQ("#nowChoiceTeam").val();
	var data = new Object();
	data.mid=mid;
	data.filename=fileName;
	var ff = new Array();
	ff.push(data);
	saveAttachToDDL(ff,teamId,false,2);
})

//提交
JQ("#ddlSubmitTeamChoiceButton").live("click",function(){
	var checked = JQ("input[name='teams']:checked").next().val();
	if(checked){
		saveAttachToDDL(ddlDataTmp,checked,false,1);
		JQ("#showDdlTeamHtmlDiv").hide();
		showWaitPage();
	}else{
		//TODO 国际化
		alert("请选择团队！");
	}
});
//取消
JQ("#ddlCancelTeamChoiceButton").live("click",function(){
	JQ("#showDdlTeamHtmlDiv").hide();
});
//关闭团队信息页面
JQ("#closeTeamInfoBtn").live("click",function(){
	JQ("#showDdlTeamHtmlDiv").hide();
});
//关闭上传结果页面
JQ("#closeAttachInfoBtn").live("click",function(){
	JQ("#showDdlSaveAttachShowDiv").hide();
});
JQ("#cancelAttachInfoBtn").live("click",function(){
	JQ("#showDdlSaveAttachShowDiv").hide();
});
//提交信息显示确定按钮
JQ("#confirmAttachInfoBtn").live("click",function(){
	JQ("#showDdlSaveAttachShowDiv").hide();
});

function addAllFileIcon(){
	JQ("#showddlAttachContext>li").each(function(i){
		if(JQ(this).children("i.ico,i.sico").length <=0){
			var fileName = JQ(this).children("span[name='ddlFileName']").html();
			var nameHtml = getIcon(fileName);
			JQ(this).children("span[name='ddlFileName']").before(nameHtml);
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


//----------------测试用例，应用时删除-------------------

function testAttachmentMessage(){
	JQ.ajax({
		url:"teamjson.json",
		type :"get",
		dataType:"json",
		success : function(dd){
			attachMessageShow(dd);
		}
	});
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