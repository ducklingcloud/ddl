<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/tokenInput.css" type="text/css" />
<link href="${contextPath}/jsp/aone/css/fileuploader.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="${contextPath}/jsp/aone/js/toker-jQuery.js?v=${aoneVersion}"></script>
<script type="text/javascript" src="${contextPath}/jsp/aone/js/fileuploader.js?v=${aoneVersion}"></script>
<script type="text/javascript">
$(document).ready(function(){
/*
	$('textarea[name="invitees"]').keyup(function(key){
		if ($(this).val().length > 40) {
			$(this).css('height', '5em');
		}
	});
*/
	$("#export-mail-help").hide();
	$("#export-mail").hide();
	$("#expressImportHelper").hide();
	$("#foxmailImportHelper").hide();
	$("#outlookImportHelper").hide();
	$("#thunderbirdImportHelper").hide();
	jQuery.validator.addMethod("multiemail", function(value, element) {
         if (this.optional(element))
             return true;
         var emails = value.split( new RegExp( "\\s*,\\s*", "gi" ) );
         valid = true;
         for(var i in emails) {
             value = emails[i];
             valid=valid && jQuery.validator.methods.email.call(this, value,element);
         }
         return valid;
     }, "请输入有效的邮箱地址");

	var currentValidator = $("#inviteForm").validate({
			rules: {
				//invitees: {required: true,multiemail: true}
				invitees: {required: true}
			},
			messages:{
				//invitees:{required:"请输入邮箱",multiemail:"请输入有效的邮箱地址"}
				invitees:{required:"请输入邮箱"}
			}
		});

	/* $('table.dataTable tr:nth-child(even)').addClass('striped'); */

	if ($('.content-side').height() > $('.content-major').height()) {
		//incase side is longer than main
		$('.content-major').css('min-height', $('.content-side').height());
	}

	$('#submitInvitation').click(function(){
		if ($('#invitees').val()!='') {
			submitInvitation("updateAll-spotLight");
		}
	});

	var canSend = true;
	function submitInvitation(viewId){
		var url = $("#inviteForm").attr("action");
		var d = $("#inviteForm").serialize();
		if(!canSend){
			return;
		}
		canSend = false;
		$.ajax({
			url:url,
			data:d,
			type:'post',
			dataType:'json',
			success:function(data){
				canSend = true;
				if(data.success){
					var inv = emailDisplay(data.invalidInvitees);
					var res = emailDisplay(data.restrictInvitees);
					if(inv!=""){
						var invMsg = data.validCount>0 ? "已成功邀请，其中" : "";
						invMsg = invMsg + inv +"已是该团队成员，无法重复邀请";
						ui_spotLight(viewId, 'success', invMsg, "");
						$(viewId).delay(2000).fadeOut();

					}else if(res!=""){
						var resMsg = data.validCount>0 ? "已成功邀请，其中" : "";
						resMsg = resMsg + res +"邀请次数过多，无法重复邀请，请0.5小时后重试";
						ui_spotLight(viewId, 'success', resMsg, "");
						$(viewId).delay(2000).fadeOut();
					}else{
						inv = '已成功发送邀请';
						ui_spotLight(viewId, 'success', inv, "");
						$(viewId).delay(2000).fadeOut();
					}
					if(data.validCount>0){
						setTimeout(function(){window.location.reload();},5000);
					}

				}else{
					alert(data.message);
				}
			}
		});
	}

	function emailDisplay(list){
		var result = "";
		if(list){
			for(var i=0;i<list.length;i++){
				if(i==0){
					result=list[i];
				}else{
					result+=","+list[i];
				}
			}
		}
		return result;
	}

	function showMsgAndAutoHide(msg, type,time){
		time=time||2000;
		showMsg(msg,type);
		hideMsg(time);
	}

	function showMsg(msg, type){
		type = type || "success";
		var operLeft = $(".content-major#invitation").outerWidth();
		$("#opareteUserMessage").css("margin-left",operLeft).removeClass().addClass("alert alert-" + type).html(msg).show(150);
	}
	function hideMsg(timeout){
		timeout = timeout || 2000;
		window.setTimeout(function(){$("#opareteUserMessage").hide(150);}, timeout);
	}

	var canRe = true;
	$(".reInvited").live("click",function(){
		var url = $(this).attr("url");
		if(!canRe){
			return false;
		}
		canRe = false;
		$.ajax({
			url : url,
			dataType:'json',
			success:function(data){
				canRe = true;
				var res = emailDisplay(data.restrictInvitees);
				if(res!=""){
					showMsgAndAutoHide("用户"+res+"，邀请次数过多，请0.5小时后重试", "block",3000);
				}else if(data.success){
					showMsgAndAutoHide("已成功发送邀请", "success",2000);
				}else{
					alert(data.message);
				}
			}
		});
		return false;
	});
	$(".cancelInvited").live("click",function(){
		var c = confirm("您是否确定取消邀请？取消邀请后，发送给该成员的邀请链接将会失效。");
		if(!c){
			return;
		}
		var url = $(this).attr("url");
		var td = $(this);
		$.ajax({
			url : url,
			dataType:'json',
			success:function(data){
				if(data.success){
					//showMsgAndAutoHide("已成功取消邀请", "success",2000);
					if(td.parents("tbody").children("tr").length==2){
						td.parents("table").children("thead").remove();
						td.parents("tbody").remove();
						$("#invitationShow").html("<p class='NA'>没有未反馈的邀请</p>");
					}else{
						td.parents("tr").prev().remove();
						td.parents("tr").remove();
					}
				}
			}
		});
		return false;
	});

	$('#submitInvitationMail').click(function(){
		var c="";
		$("textarea[name=message]").val($("textarea[name=messageInvitation]").val());
		$("ul#selectedEmail li").each(function(){
			c +=$(this).attr("mail")+",";
		});
		$('#invitees').val((c.substr(0,c.length-1)));
		if ($('#invitees').val()!='') {
			submitInvitation("invitationMail-spotLight");
		}
	});

	$('input#invitees').tokenInput2("<vwb:Link context='contacts' format='url'/>?func=searchUser", {
		theme:"facebook",
		hintText: "请输入邮箱地址，以逗号 分号 空格或回车确认",
		searchingText: "正在搜索……",
		noResultsText: "没有该用户信息，输入逗号 分号 空格或回车添加",
		preventDuplicates: true
	});
	var initFileUpload = true;
	$('.nav-list li').click(function(){
		var v=$(this).attr("id");
		var flag=$(this).attr("flag");
		$("div.content").hide();
		if(v=='input'){
			$("#input-mail").show();
			$("#input").addClass("current");
			$("#export").removeClass("current");
		}else if(v=='export'&&flag=='false'){
			$("#export-mail-help").show();
			$("#export").addClass("current");
			$("#input").removeClass("current");
			if(initFileUpload){
				console.log('init');
				createUploader();
			    reUploader();
			    initFileUpload = false;
			}

		}else{
			$("#export-mail").show();
			$("#export").addClass("current");
			$("#input").removeClass("current");
		}
	});

	var upload_base_url = "<vwb:Link context='configTeam'  jsp='${currTeam.name}' format='url'/>";
	var upload_url = "<vwb:Link context='configTeam'   jsp='${currTeam.name}' format='url'/>&func=uploadMailList";
	var datas = [];
	var index = 0;
	function createUploader(){
		qq.extend(qq.FileUploader.prototype,{
			_setupDragDrop:function(){
				var self = this;
				dropArea = this._find(this._element,'drop');
				dropArea.style.display='none';
			}
		});

         var uploader = new qq.FileUploader({
             element: document.getElementById('file-uploader-demo1'),
             template: document.getElementById('file-uploader-demo1').innerHTML,
             fileTemplate: '<li>' +
             '<span class="qq-upload-file"></span>' +
             '<span class="qq-upload-spinner"></span>' +
             '<span class="qq-upload-size"></span>' +
             '<a class="qq-upload-cancel" href="#">取消</a>' +
             '<span class="qq-upload-failed-text">失败</span>' +
         '</li>',
             action: upload_url,
             params:{pid:"${pid}"},
             onComplete:function(id, fileName, data){
       	   		$.each(data,function(p,items){
       	   			if(p=="list"){
       	   				//$("#mail-list-tbody").empty();
       	   				$("#mail-list-template").tmpl(items).appendTo("#mail-list-tbody");
       	   				/* $(items).each(function(index){
  	   					//alert(items[index].email);
  	   					//alert(mailValidate(items[index].email));
  	   					if(!mailValidate(items[index].email)){
  	   						$("li input[name="+items[index].name+"][type=checkbox]").attr("disable",true);
  	   					}
  	   					}); */
       	   				$("#export-mail-help").hide();
       	   			 	$("#export-mail").show();
       	   			 	$("#export").attr("flag","true");
       	   			}else if(p=="success"){
      	   				if(items==false){
      	   					$("#remind1").show();
      	   					$("#export-mail-help").show();
       	   			 		$("#export-mail").hide();
      	   				}else{
      	   					$("#remind1").hide();
      	   				}
      	   			}
       	   			});
       	   	 $('#mail-list-tbody tr:even').addClass('striped');
             },
             debug: false
         });
     };


 	function reUploader(){
        var uploader = new qq.FileUploader({
            element: document.getElementById('file-uploader-demo2'),
            template: document.getElementById('file-uploader-demo2').innerHTML,
            action: upload_url,
            onComplete:function(id, fileName, data){

            	if($("#reUploadFlag").val()=="ready"){
      	   			$("#mail-list-tbody").empty();
      	   			$("ul#selectedEmail").empty();
      	  			$("#reUploadFlag").attr("value","reUpload");
      	   		}

      	   		$.each(data,function(p,items){
      	   			$(".qq-upload-list").hide();
      	   			if(p=="list"){
      	   				//$("#mail-list-tbody").empty();
      	   				$("#mail-list-template").tmpl(items).appendTo("#mail-list-tbody");

      	   				/* $(items).each(function(index){
      	   					alert(items[index].email);
      	   				}); */
      	   			 	$("#export-mail-help").hide();
      	   			 	$("#export-mail").show();
      	   			 	$("#export").attr("flag","true");
      	   			}else if(p=="success"){
      	   				if(items==false){
      	   					$("#remind").show();
      	   				}else{
      	   					$("#remind").hide();
      	   				}
      	   			}
      	   			});
      	   	 $('#mail-list-tbody tr:even').addClass('striped');
            },
            debug: false
        });
    };

	$("div#file-uploader-demo2 .qq-upload-button").live("click",function(){
		$("#reUploadFlag").attr("value","ready");
	});

	$("#reExport").live("click",function(){
		$("input[name=files]").trigger("click");
    });
	$("a[name='cancel']").live("click",function(){
    	ui_hideDialog("upload-attach-dialog");
    });

    $("#attach-to-this-page").live("click",function(){
	   	 for(var i=0;i<datas.length;i++){
	   		$.each(datas[i],function(p,items){
	   			if(p=="list"){
	   				$("#mail-list-tbody").empty();
	   				$("#mail-list-template").tmpl(items).appendTo("#mail-list-tbody");
	   			 	$("#export-mail-help").hide();
	   			 	$("#export-mail").show();
	   			 	$("#export").attr("flag","true");
	   			}
	   			});
	   	 }
	   	 $(".qq-upload-list").html("");
	   	 datas = new Array();
	   	 index = 0;
	   	 ui_hideDialog("upload-attach-dialog");
    });

    $('#selectAll').change(function(){
		if ($(this).attr('checked')=='checked' || $(this).attr('checked')==true) {
			$("input[name='check']").each(function(){
				if($(this).attr('checked')=='checked'|| $(this).attr('checked')==true){

				}else{
					$(this).attr("checked","checked");
					$(this).trigger("change");
				}
			});
		}
		else {
			$("input[name='check']").removeAttr("checked");
			$("input[name='check']").trigger("change");
		}
	});

    $('input[name=check]').live("change",function(){
    	if($(this).attr('checked')=='checked'|| $(this).attr('checked')){
    		$("#selectedEmail").append($("<li index="+$(this).attr("id")+" mail="+$(this).next().attr("value")+">"+$(this).attr("value")+"<a class='deleteName'></a></li>"));
    		//$("#selectedEmail").append($("<li mail="+$(this).next().attr("value")+">"+$(this).attr("value")+"<a class='deleteName'></a></li>"));
    		//$("#selectedEmailData").val($("#selectedEmailData").val()+$(this).next().attr("value")+",");
    		 selectAllFlag++;
    		if(!(selectAllFlag<$('input[name=check]').length)){
    			$('#selectAll').attr("checked","checked");
    		}
    	}else{
    		$("ul#selectedEmail li").remove("li[mail="+$(this).next().attr("value")+"]");
    		$('#selectAll').removeAttr("checked");
    		selectAllFlag--;
    	}
    });
    $("a.deleteName").live("click",function(){
    	//$("input[name='check'][type='checkbox'][id=$(this).parent().attr('index')]").trigger("change");
    	$("input[id="+$(this).parent().attr('index')+"]").removeAttr("checked");
    	$("input[id="+$(this).parent().attr('index')+"]").trigger("change");
    	//$("ul#selectedEmail li").remove("li[mail="+$(this).parent().attr("mail")+"]");


    }
    );
    var selectAllFlag=0;
    function mailValidate(value){
    	var reg = /^[_a-zA-Z\d\-\.]+@[_a-zA-Z\d\-]+(\.[_a-zA-Z\d\-]+)+$/;
    	return reg.test(value);
    }
});

</script>
<div class="content-major" id="invitation">
	<!--
	<div class="config-float">
		<h3>邀请用户加入团队
			<a href="${contextPath}/ddlInviteHelp.jsp" target="_blank" class="ui-iconButton help inviteHelp"></a>
		</h3>
		<p class="ui-text-note">将向用户发送带有邀请链接的电子邮件，他们可以通过该链接注册并加入您的团队。</p>
		<p class="ui-text-note">如果这些用户已经在本平台有帐号，他们将可以直接在系统中加入您的团队。</p>
		<br />
		<div class="ui-clear"></div>
	</div>
	-->
	<div id="opareteUserMessage" class="alert alert-success" style="display: none;z-index:9999"></div>
	<form id="inviteForm" action="<vwb:Link context='configTeam'  jsp='${currTeam.name}' format='url'/>" method="POST">
		<input type="hidden" value="sendTeamInvite" name="func"/>
		<h4 class="invite">通过邮箱添加新成员</h4>
		<div id="nav" class="nav">

		<ul class="nav-list">
			<li id="input" class="current">
				<p>输入邮箱</p>
			</li>
			<li id="export" flag="false">
				<p>从文件导入</p>
			</li>
		</ul>
		</div>

		<div id="input-mail" class="content">
			<table class="ui-table-form" style="margin-left:1em;">
				<tbody>
					<tr>
						<th>电子邮箱：</th>
						<td>
							<input type="text" id="invitees" name="invitees" />
							<span class="ui-text-note">多个邮箱可以用逗号 分号 空格或回车分割</span>
						</td>
					</tr>
					<tr>
						<th>留言：</th>
						<td><textarea name="message"></textarea></td>
					</tr>
					<tr>
						<th></th>
						<td>
							<input type="button" value="发送邀请" class="btn-success" id="submitInvitation" style="float:left;"/>
							<span class="ui-spotLight" id="updateAll-spotLight" style="padding:2px 0.5em; display:block; max-width:23em; float:left;"></span>
						</td>
					</tr>
				</tbody>
			</table>
		</div>

		<div id="export-mail-help" class="content" style="display: none">
			<p>支持excle导入成员，请参考<a href="${contextPath}/r/doc/import_example.xls" class="btn btn-mini" style="background:#070; text-decoration:none; color:#fff; padding:3px 5px; margin:0 3px;">excel模板</a>，或者从邮箱或客户端导出csv或vCard格式地址簿，再导入DDL。</p>
			<div id="step1" class="">
				<div id="file-uploader-demo1" >
					<div class="qq-uploader" >
						<div class="qq-upload-drop-area" style="display: none"><span>Drop files here to upload</span></div>
						<div class="qq-upload-button" style="width:200px; margin:15px 0px">
							上传excel或导入地址簿
						</div>
						<ul class="qq-upload-list fileList"></ul>
					</div>
				</div>

				<label id="remind1" style="display:none">上传文件格式错误！请重新导入</label>
				<div id="files-data-area"></div>
			</div>



			<ul class="invite-item">
				<li class="invite-title">常见邮件客户端软件地址簿文件（CSV、VCF）的导出方法:</li>
	   		    <li>
	   		    	<!-- <a class="invite-detail-link"  href="${contextPath}/help/outlookImportHelper.jsp"  target="_blank"> -->
		   		    	<div id="invite-outlook"></div>
		   		    	<div class="invite-detail"><span>Microsoft Outlook</span><br/>
		   		                        点击“文件” -> 选择“导出到文件”，单击下一步 -> 选择“以逗号为分隔符(Windows)”，单击下一步 -> 选择“联系人”，单击下一步 -> 选择一个位置保存文件，单击下一步完成。
						</div>
                                                <!-- Disabled <2022-03-16 Wed> -->
						<!-- <div class="clickMore">查看</div> -->
						<div class="ui-clear"></div>
					        <!-- </a> -->
	   		    </li>
				<li>
				    <!-- <a class="invite-detail-link" href="${contextPath}/help/expressImportHelper.jsp"  target="_blank"> -->
						<div id="invite-express"></div>
						<div class="invite-detail"><span>Outlook Express</span><br/>
				 		点击“文件” -> 选择“导出” -> 选择“通讯录” -> 选择“文本文件” -> 单击“导出” -> 在弹出框中选择csv文件保存的位置和文件名，单击下一步完成。
						</div>
						<!-- <div class="clickMore">查看</div> -->
						<div class="ui-clear"></div>
					        <!-- </a> -->
				</li>
				<li>
				    <!-- <a class="invite-detail-link" href="${contextPath}/help/foxmailImportHelper.jsp"  target="_blank"> -->
						<div id="invite-foxmail"></div>
						<div class="invite-detail"><span>Foxmail</span><br/>
						点击“地址簿” -> 选中“工具” -> 导出 -> 选择任何一个格式（推荐使用CSV格式） -> 在弹出的对话框中点击“浏览”选择拟保存的路径和文件名 -> 一路确定就将地址簿导出来了。
						</div>
						<!-- <div class="clickMore">查看</div> -->
						<div class="ui-clear"></div>
					        <!-- </a> -->
				</li>
				<li>
				    <!-- <a class="invite-detail-link" href="${contextPath}/help/thunderbirdImportHelper.jsp"  target="_blank"> -->
						<div id="invite-thunderbird"></div>
						<div class="invite-detail"><span>Mozilla Thunderbird</span><br />
						点击“工具” -> 选择“通讯录” -> 选择“工具” -> 选择“导出” -> 在弹出框中选择csv文件保存的位置和文件名，单击下一步完成。
						</div>
						<!-- <div class="clickMore">查看</div> -->
						<div class="ui-clear"></div>
					        <!-- </a> -->
				</li>
			 </ul>
		</div>

		<div id="export-mail" class="content" style="display: none">
			<a class="invite-detail-link" href="${contextPath}/help/outlookImportHelper.jsp"  target="_blank">常见邮件客户端软件地址簿文件（CSV、VCF）的导出方法</a>
			<table class="dataTable merge" style="border-top:1px solid #ccc;white-space:nowrap; ">
				<thead>
					<tr>
						<td class="dtCenter dt3char"><label><input class="selbtn" type="checkbox" id="selectAll">全选</label></td>
						<td class="dtName">姓名</td>
						<td>邮箱</td>
					</tr>
				</thead>
				<tbody id="mail-list-tbody">

				</tbody>
			</table>

			<div id="uploaddiv">
				<div id="file-uploader-demo2" >
					<div class="qq-uploader" >
						<div class="qq-upload-drop-area"><span>Drop files here to upload</span></div>
						<div class="qq-upload-button" >
							重新导入
						</div>
						<ul class="qq-upload-list fileList"></ul>
					</div>
			<label id="remind" style="display:none">上传文件格式错误！请重新导入</label>
			<input id="reUploadFlag"  type="hidden" value="false"/>
			</div>
		</div>
		<div class="ui-clear"></div>

		<table class="ui-table-form" style="margin-left:1em;">
			<tbody>
				<tr>
					<th>邀请联系人：</th>
					<td>
						<ul id="selectedEmail"></ul>
						<input type="hidden" id="selectedEmailData" value=""/>
					</td>
				</tr>
				<tr>
					<th>留言：</th>
					<td>
						<textarea name="messageInvitation"></textarea><br />

					</td>
				</tr>
				<tr>
					<th></th>
					<td>
						<input type="button" value="发送邀请" class="btn-success" id="submitInvitationMail" style="float:left;"/>
						<span class="ui-spotLight" id="invitationMail-spotLight" style="padding:2px 0.5em; display:block; max-width:23em; float:left;"></span>
						</td>
				</tr>
			</tbody>
		</table>
		</div>
		<c:if test="${showUrl }">
			<h4 class="invite dotted">通过推广添加新成员</h4>
			<p class="invite">
				分享下面的链接给项目成员，他们可以直接访问链接来加入，最适合分享到微信群和QQ群<br>
				${currTeamUrl}
			</p>
		</c:if>
		</form>

</div>

<div class="content-side" id="invitationStatus">
	<div class="sideBlock">
		<h4 class="ui-title-expand"><span></span>未反馈的邀请</h4>
		<div class="ui-title-child" id="invitationShow">
		<c:choose>
			<c:when test="${not empty waitingList}">
				<table class="dataTable merge">
				<thead>
					<tr><td>受邀者</td>
						<td>邀请时间</td>
					</tr>
				</thead>
				<tbody>
				<c:forEach items="${waitingList}" var="item">
					<tr><td title="${item.invitee}">${item.invitee}<br>
						</td>
						<td>${item.inviteTime}</td>
					</tr>
					<tr><td >
							<a class="reInvited" url="<vwb:Link context='configTeam'  jsp='${currTeam.name}' format='url'/>&func=sendTeamInvite&invitees=${item.invitee}&message=${item.message}">再次邀请</a>
							&nbsp&nbsp
							<a class="cancelInvited" url="<vwb:Link context='configTeam'  jsp='${currTeam.name}' format='url'/>&func=cancelInvite&invitees=${item.invitee}&message=${item.message}">取消邀请</a>
						</td>
						<td></td>
					</tr>
				</c:forEach>
				</tbody>
				</table>
			</c:when>
			<c:otherwise>
				<p class="NA">没有未反馈的邀请</p>
			</c:otherwise>
		</c:choose>
		</div>
	</div>

	<div class="sideBlock">
		<h4 class="ui-title-expand"><span></span>已过期的邀请</h4>
		<div class="ui-title-child">
		<c:choose>
			<c:when test="${not empty validList}">
				<table class="dataTable merge">
				<thead>
					<tr><td>受邀者</td>
						<td>截止时间</td>
					</tr>
				</thead>
				<tbody>
				<c:forEach items="${validList}" var="item">
					<tr><td title="${item.invitee}">${item.invitee}
							<a style="float:right" class="reInvited"
								href="<vwb:Link context='configTeam'  jsp='${currTeam.name}' format='url'/>&func=sendTeamInvite&invitees=${item.invitee}&message=${item.message}">再次邀请</a>
						</td>
						<td>${item.inviteTime}</td>
					</tr>
				</c:forEach>
				</tbody>
				</table>
			</c:when>
			<c:otherwise>
				<p class="NA">没有过期的邀请</p>
			</c:otherwise>
		</c:choose>
		</div>
	</div>

	<div class="sideBlock" style="display:none">
		<h4 class="ui-title-fold"><span></span>已接受的邀请</h4>
		<div class="ui-title-child">
		<c:choose>
			<c:when test="${not empty acceptList}">
				<table class="dataTable merge">
				<thead>
					<tr><td width='170'>受邀者</td>
						<td>接受时间</td>
					</tr>
				</thead>
				<tbody>
				<c:forEach items="${acceptList}" var="item">
					<tr><td title="${item.invitee}">${item.invitee}</td>
						<td>${item.acceptTime}</td>
					</tr>
				</c:forEach>
				</tbody>
				</table>
			</c:when>
			<c:otherwise>
				<p class="NA">还没有人接受邀请</p>
			</c:otherwise>
		</c:choose>
		</div>
	</div>
</div>

<script id="mail-list-template" type="text/html">
	<tr id="">
		<td class="dtCenter">
			<input type="checkbox" name="check" value="{{= name}}" id="{{= index}}" />
			<input type="hidden" value="{{= email}}" />
		</td>
		<td>
			<label for="{{= index}}">{{= name}}</label>
		</td>
		<td>
			<label for="{{= index}}">{{= email}}</label>
		</td>
	</tr>
</script>

<div class="ui-clear"></div>
