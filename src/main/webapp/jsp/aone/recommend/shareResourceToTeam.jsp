<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<link rel="stylesheet" href="${contextPath}/scripts/jquery_chosen/chosen.css" type="text/css" />
<script type="text/javascript" src="${contextPath}/scripts/jquery_chosen/chosen.jquery.js"></script>
<script type="text/javascript">
		var addRecRequestURL = null;
		$('div.a1-objSelector ul li label input').live('click', function(){
    		var li = $(this).parent().parent();
    		if ($(this).attr('checked')==true)
    			li.addClass('-selected');
    		else
    			li.removeClass('-selected');
    	});
    	
    	$('#recommend-to-all').live('click',function(){
    		if ($(this).attr('checked')==true) {
    			$('div.a1-objSelector ul li label input').each(function(){
    				$(this).attr('checked', true);
    				var li = $(this).parent().parent();
    					li.addClass('-selected');
    			});
    		}
    		else {
    			$('div.a1-objSelector ul li label input').each(function(){
    				$(this).removeAttr('checked');
    				$(this).parent().parent().removeClass('-selected');
    			});
    		}
    	});
    	
    	$("#selectAllMember").live("click",function(){
    		var select = $("#selectAllMember:checked").val();
    		if(select==null||select=="undefined"){
    			$(".search-choice-close").trigger("click");
    			$("#recommendForm").find("input[type=text]").trigger("focus");
    			
    		}else{
    			 $('#userSelect option').attr('selected', true);
    			 $('#userSelect').trigger('liszt:updated');
    		}
    		changeGroupSend();
    	});
    	
    	
    	$("#userSelect").live("change",function(){
    		var checkLength = $("#userSelect").find("option:selected").length;
    		var selectLength = $("#userSelect option").length;
    		var select = $("#selectAllMember:checked").val();
    		if(checkLength==selectLength){
    			if(select==null||select=="undefined"){
    				$("#selectAllMember").attr("checked","checked");
    			}
    		}else{
    			if(select!=null){
    				$("#selectAllMember").removeAttr("checked");
    			}
    		}
    		changeGroupSend();
    	});
    	function closeShareDialog(){
    		$("#recommend-to-all").removeAttr('checked');
    		$(".modal.shareModal").hide();
    	};
    	$(".closeDialog-hideoutMenu").die().live('click',function(){
    		closeShareDialog();
    	});
    	function changeGroupSend(){
    		var checkLength = $("#userSelect").find("option:selected").length;
    		//当选择大于两个时
    		if(checkLength>=2){
    			if($("#groupDiv").is(":hidden")){
    			//	$("#groupDiv").show();
    			//	$("#groupDiv>input").removeAttr("checked");
    			}
    		}else{
    			//$("#groupDiv>input").removeAttr("checked");
    			//$("#groupDiv").hide();
    		}
    	};
    	
    	var hello = function(data){
    		alert(data.status);
    	};
    	
    	$("ul.chzn-choices").live("click",function(e){
    		$("#recommendForm").find("input[type=text]").trigger("focus");
    		var t = setTimeout(function(){
				$(".chzn-drop").css({"left":"0px"});
			},55); 
    	});
    	//url ajax请求地址 pageId分享的页面id号 
    	  //url ajax请求地址 pageId分享的页面id号 
    	  //出现弹出框提示语不根据滚动条移动
    	  //记录原有滚动事件
    	var orgScroll=$(window).scroll;
    	function disableScroll(){
	    /* 另一种实现隐藏滚动条
	    $(document.body).css({
	    		   "overflow-x":"hidden",
	    		  "overflow-y":"hidden"
	    	 }); */
	    	 //重写滚动事件
	    	 $(window).scroll(function(){
	    	    	var pos = $("#shareToOthersDiv .token-input-list-facebook");
	         	    $(".token-input-dropdown-facebook:last").css({
	         	        "position": "fixed",
	                    "top": pos.offset().top + pos.outerHeight()-$(document).scrollTop()
	               });
	    	});
	     }
	     function enableScroll(){
	    	 $(window).scroll=orgScroll;
	     }
	     
	 
	     
	     $('#shareModal').live('show', function () {
	    	 disableScroll();
	    	});
	     $('#shareModal').live('hide', function () {
	    	 enableScroll();
	    	});
    	var showPersonListFlag = false;
    	function bundleTokerInput(){
    	    $("#targetEmailsTd").html($("#tempTemplate").html());
    	    $('#targetEmailsTd input[name="targetEmails"]').tokenInput2("<vwb:Link context='contacts' format='url'/>?func=searchUser", {
    			theme:"facebook",
    			hintText: "请输入邮箱地址，以逗号 分号 空格或回车确认",
    			searchingText: "",
    			noResultsText: "没有该用户信息，输入逗号 分号 空格或回车添加",
    			preventDuplicates: true
    		});
    	}
		function prepareRecommend(url,rid, title,itemType){
			bundleTokerInput();
			//清空历史
			cleanShareDialog();
			addRecRequestURL = url;
			showPersonList();
			$("#recommendPageField").attr("value",rid);
			$("#rec-page-title").text(title);
			$("#recommendItemType").val(itemType);
			$("textarea[name='remark']").val("");
			ajaxRequest(addRecRequestURL,null,renderRecommendDialog);
		};
		showPersonListFlag = false;
		function prepareRecommendRids(url,rids){
			alert("未修改完，修改后继续");
		};

		function showPersonList(){
			if(!showPersonListFlag){
				var url = site.getURL('task',null)+"?func=getmembers";
				$.ajax({
					url:url,
					type:'POST',
					success:function(data){
						var datajson = JSON.parse(data);
						renderData(datajson.users);
					},
					statusCode:{
						450:function(){alert('会话已过期,请重新登录');},
						403:function(){alert('您没有权限进行该操作');}
					}
				});
				showPersonListFlag = true;
			}
		};
		
		function renderData(data){
			$.each(data,function(index,element){
				var alphabet = element.id;
				var users = element.value;
				$("select.chzn-select").append("<optgroup label = '" + alphabet + "'></optgroup>");
				$.each(users, function(index2,element2){
					var uid = element2.id;
					var email =element2.email;
					var name = element2.name;
					$("select.chzn-select optgroup[label = '" + alphabet + "']").append("<option value='"+uid+"'>" + name + " ( " + email  + " ) " + "</option>");
				});
			});
			$(".chzn-select").chosen({no_results_text: "没有成员匹配"});
			$(".chzn-select").chosen();
			$(".chzn-select-deselect").chosen({allow_single_deselect:true}); 
		};
		
		function renderRecommendDialog(data){
			var i=0;
			var list_html = '';
			for(i=0;i<data.length;i++){
				list_html += '<li><label title="'+data[i].id+'"><input type="checkbox" name="users" attr="'+data[i].name+'" value="'+data[i].id+'"/>'+data[i].name+'</label></li>';
			}
			$("#candidates-list").html(list_html);
			
			ui_showDialog('recommend');
			$("#recommendForm").find("input[type=text]").trigger("focus");
			var t = setTimeout(function(){
				$(".chzn-drop").css({"left":"-9000px"});
			},55); 
			flag = false;
			clickFlag = false;
			$('#shareModal').modal('show');
		};
		
		function cleanShareDialog(){
			$("#shareToTeam").trigger("click");
			//关闭上次的选择项
			$(".search-choice-close").trigger("click");
			$("#selectAllMember").removeAttr("checked");
			$('#recommendForm textarea').text('');
			
			$('#shareModal #shareToOthersDiv .token-input-delete-token-facebook').trigger("click");
			$('#shareModal #shareToOthersDiv input[name=rid]').val("");
			$("#shareToOthersDiv textarea[name='message']").val("");
			$('#shareModal #shareToOthersDiv option:selected').attr("selected",false);
		}
		
		
		var flag = false;
		$("#recommendForm").find("input[type=text]").live("keyup",function(event){
			if(!flag){
				$(".chzn-drop").css({"left":"0px"});
				flag = true;
			}
		});
		var clickFlag = false;
		$("#recommendForm").find("input[type=text]").live("click",function(){
			if(!clickFlag){
				$(".chzn-drop").css({"left":"0px"});
				clickFlag = true;
			}
		});
		
		$(".refresh-button").live('click',function(){
			window.location.reload();
		});
		
		function afterRecommend(data){
			var html = '';
			if(data.itemType == 'DFile')
				html = '您已将该文件分享给';
			else
				html = '您已将该页面分享给';
			if(data.status=='success'){
				var s = $("#userSelect").find("option:selected");
				$.each(s,function(indext,option){
					if(indext!=0){
						html+=",";
					}
					var opHtml = $(option).html();
					var name = opHtml;
					if(opHtml.indexOf("(")>-1){
						name = opHtml.substring(0,opHtml.indexOf("("));
					}
					html+=name;
				});
				closeShareDialog();
				$("#recommend-tips").html(html);
				$('#shareModal').modal('hide');
				$("#recommend-success").modal("show");
				window.setTimeout(function(){
					$("#recommend-success").modal("hide");
				}, 1500);
			}
		};
		
		function afterSendShareEmail(data){
			var	html = '您已将该文件分享给:'+data.friendEmails;
			if(data.status=='success'){
				closeShareDialog();
				$("#recommend-tips").html(html);
				$('#shareResource').modal('hide');
				if(!showShareResource.showId){
					$("#recommend-success").modal("show");
					window.setTimeout(function(){
						$("#recommend-success").modal("hide");
					}, 1500);
				}else{
					var url ="${contextPath}/pan/shareManage";
					var msg = "分享成功，你可以在<a href='"+url+"' target='_blank'>分享历史</a>中查看详情";
					shareShowMsgAndAutoHide(showShareResource.showId,msg,"success");
				}
			}
		}
		
		function afterRecommendToOthers(data){
			$(".token-input-dropdown-facebook").remove();
			var html = '';
			html = '您已将该<a href=\''+data.fileURL+'\' target=\'_blank\'>'+data.fileName+'</a>分享给';
			if(data.status=='success'){
			 	var s = $(".token-input-token-facebook p");
				$.each(s,function(indext,option){
					if(indext!=0){
						html+=",";
					}
					var opHtml = $(option).text();
					var name = opHtml;
					if(opHtml.indexOf("(")>-1){
						name = opHtml.substring(0,opHtml.indexOf("("));
					}
					html+=name;
				}); 
				closeShareDialog();
				$("#recommend-tips").html(html);
				$('#shareModal').modal('hide');
				$("#recommend-success").modal("show");
			}
		};
		
		
		$("#shareToOthers").live("click",function(){
			$("ul.nav.nav-tabs li").removeClass("active");
			$(this).addClass("active");
			$("#shareToOthersDiv").show();
			$("#shareToTeamDiv").hide();
			$("#files-hidden-area input[name=rid]").val(showShareResource.rid);
			
			
		});
		$("#shareToTeam").live("click",function(){
			$("ul.nav.nav-tabs li").removeClass("active");
			$(this).addClass("active");
			$("#shareToOthersDiv").hide();
			$("#shareToTeamDiv").show();
		});
		
		function prepareShareResource(url,rid,title,showId){
			bundleTokerInput();
			//清空历史
			cleanShareDialog();
			showPersonList();
			showShareResource = {'url':url,'rid':rid,'title':title};
			if(showId){
				showShareResource.showId = showId;
			}
			var d = new Object();
			d.func = "getUserStatus";
			d.rid = rid;
			$("#files-hidden-area input[name=rid]").val(rid);
			ajaxRequest(url,d,getUserStatus);
			$("input[value='"+rid+"']").parent().parent().find(".headImg").append('<span class="share-icon"> </span>');
		}
		//获取用户状态
		function getUserStatus(data){
			if('false'==data.status){
				$("#shareValidateTitle").html(showShareResource.title);
				$("#shareTips").modal("show");
			}else if('true'==data.status){
				showShareResourceDialog(data);
			}else{
				$('#fobiddenShare').modal('show');
			}
			$("#currentUser").html(data.userName);
		}
		
		function showShareResourceDialog(data){
			$("#shareResourceTitle").html(showShareResource.title);
			$("#shareResourceTitleSpan").html(showShareResource.title);
			showShareResText(data);
			$("ul.nav.nav-tabs li").removeClass("active");
			$("#shareToTeam").addClass("active");
			$("#shareResource").modal('show');
		}
		function showShareResText(data){
			var input = data.url;
			if(data.fetchCode){
				$("#fetchCode").css({display:'inline-block'});
				$("#fetchCode").val(data.fetchCode);
				input+="   提取码："+data.fetchCode;
				$("#updateFetchCode").html("删除提取码");
				$("#copyShareUrl").html("复制链接和提取码");
			}else{
				$("#fetchCode").hide();
				$("#copyShareUrl").html("复制链接");
				$("#updateFetchCode").html("创建提取码");
			}
			$("#shareResourceUrlSpan").html(input);
			$("#shareResourceText").val(input);
			var imgUrl = "${contextPath}/f/qrcode?text=" + encodeURIComponent(data.url);
			$("#qrcodeWrap").html("<img title=\"二维码\" style=\"width:100px;height:100px;\" src=\""+ imgUrl +"\" /><div style=\"font-size:12px;font-weight:normal;color:#999;margin-left:14px;line-height:18px;\">扫描获取链接</div>");
		}
		
		function shareShowMsgAndAutoHide(id,msg, type,time){
			time=time||2000;
			shareShowMsg(id,msg,type);
			shareHideMsg(id,time);
		}

		function shareShowMsg(id,msg, type){
			type = type || "success";
			$("#"+id+"").removeClass().addClass("alert alert-" + type).html(msg).show(150);
		}
		function shareHideMsg(id,timeout){
			timeout = timeout || 2000;
			window.setTimeout(function(){$("#"+id+"").hide(150);}, timeout);
		}
		
var showShareResource = {url:'',rid:''};		
		
$(document).ready(function(){
	$("#submit-User-Confirm").live('click',function(){
		if(!$("#userConfirm").attr("checked")){
			alert("请同意协议！");
		}else{
			var url = showShareResource.url;
			var d= {"func":"updateUserStatus"};
			d.rid = showShareResource.rid;
			ajaxRequest(url,d,function(data){
				$('#shareValidate').modal('hide');
				getUserStatus(data);
			});			
		}
	});
	
	$("a#updateFetchCode").die().live('click',function(){
		var v = $("#fetchCode").val();
		var url = showShareResource.url;
		var d;
		if(v&&v!=''){
			d= {"func":"deleteFetchCode"};
			$("#fetchCode").val('');
		}else{
			d = {"func":"getFetchCode"};
		}
		d.rid = showShareResource.rid;
		ajaxRequest(url,d,showShareResText);
	});
	
	$("#submit-recommend").live('click',function(){
		var type=$(".typeTab li.active").attr("id");
		if(type=="shareToTeam"){
			var queryString = $("#recommendForm").serialize();
			var choice = $("#userSelect").val();
			if(choice==null||choice==''||choice=='undefined'){
				alert("请选择成员！");
				return ;
			}
			$("#recommendPageField").attr("value",rid);
			var rid = $("#recommendPageField").val();
			var itemType = $("#recommendItemType").val();
			ajaxRequest("<vwb:Link context='recommend' format='url'/>?func=addRecommend&itemType="+itemType+"&rid="+rid,queryString,afterRecommend);
		}else{
			$("#share-files-form").attr('action',showShareResource.url);
			$("#share-files-form").submit();
		}
	});
	
	$("#submitShareResouce").live('click',function(){
		var url = showShareResource.url;
		var d = $("#share-files-form").serialize();
		var choice = $("#targetEmails").val();
		if(choice==null||choice==''||choice=='undefined'){
			alert("请选择成员！");
			return ;
		}
		ajaxRequest(url,d,afterSendShareEmail);
	});
	
	$(".closeShareTips").live('click',function(){
		$("#shareTips").modal('hide');
		$('#shareValidate').modal('show');
	})
	
	//复制shareURL进剪切板
	var client = new ZeroClipboard( document.getElementById("copyShareUrl"), {
		  moviePath: "${contextPath}/scripts/zeroclipboard/ZeroClipboard-1.3.5.swf"
		} );

		client.on( "load", function(client) {

		  client.on( "complete", function(client, args) {
			  	$("#shareResourceText").select();
		    	$("#showCopySuccess").fadeIn("slow");
		    	window.setTimeout(function(){
		    		$("#showCopySuccess").fadeOut("slow");
		    	}, 2000)
		    
		  } );
		} );
		client.on("noFlash", function (client) {
		    $("#copyShareUrl").hide();
		});
		
		
	<%--用户焦点在输入框上，阻止按esc关闭弹出框事件--%>
	$('#recommendForm,#share-files-form').on("keyup",function (event) {
	    if (event.keyCode == 27){return false;}
	});
});
		
</script>

<div class="ui-wrap" id="dialog-hideoutMenu">
	<div id="shareModal" class="modal hide fade shareModal" style="width:550px;" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
        <div class="modal-header">
          <button type="button" class="close closeDialog-hideoutMenu" data-dismiss="modal" aria-hidden="true">&times;</button>
          <h3>通过邮件发送：<span id="rec-page-title"></span></h3>
        </div>
        <div class="modal-body"  style="height:320px;">
			<div>
	             <p>
					请输入收件人姓名：
					<span class="ui-RTCorner"> <label>
							<input style="margin:0" type="checkbox" id="selectAllMember" />
							全部成员
						</label> </span> 
				</p>
				<form id="recommendForm">
					<input type="hidden" name="itemId" id="recommendPageField" value=""/>
					<input type="hidden" name="itemId" id="recommendItemType" value=""/>
					<select id="userSelect" name="users" data-placeholder="输入成员名称" style="width:350px;" class="chzn-select" multiple tabindex="6">
			        </select>
					<div id="groupDiv" style="display:none">
						<input type="checkbox" name="sendType"  value="group">群发多显（每个收件人能看到所有群发的邮件地址）
					</div>
					<p>附加信息：</p>
					<p>
						<textarea name="remark" style="width:95%; height:8em; max-width:95%; resize:none; font-size:13px;" placeholder="请添加留言"></textarea>
					</p>
				</form>
			</div>
        </div>
        <div class="modal-footer">
        	<span class="protol"><input type="checkbox" checked="checked" disabled>同意<a href="${contextPath}/sharingAgreements.jsp" target="_blank">《团队文档库分享服务协议》</a></span>
            <a class="btn btn-primary" id="submit-recommend">分享</a>
			<a class="btn closeDialog-hideoutMenu"  data-dismiss="modal" aria-hidden="true">关闭</a>
        </div>
    </div>
	
	
	<div id="shareResource" class="modal hide fade shareModal" style="width:550px;" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
        <div class="modal-header">
          <button type="button" class="close closeDialog-hideoutMenu" data-dismiss="modal" aria-hidden="true">&times;</button>
          <h3>分享文件：<span id="shareResourceTitle"></span></h3>
        </div>
        <div class="modal-body"  style="height:385px;padding-bottom:0;">
        	<div class="shareSuccess">
        		<p class="copyOk">
	        		<span class="codeSpan">
		        		<input type="text" id="fetchCode" readonly> 
		        		<a id="updateFetchCode">添加密码</a>
	        		</span>
	        		<i class="share-icon share-ok"></i>已成功创建公开链接！
        		</p>
        	</div>
	        <div>
	        	<ul class="nav nav-tabs typeTab" style="height:39px">
				  <li class="active" id="shareToTeam">
				    <a href="javascript:void(0)"><i class="share-icon share-link"></i>链接分享</a>
				  </li>
				  <li id="shareToOthers">
				  	<a href="javascript:void(0)"><i class="share-icon share-mail"></i>发送邮件</a>
				  </li>
				</ul>
	        </div>
        	<div id="shareToTeamDiv">
				<div id="shareResourceUrl">
					
					<div class="clearfix">
						<div class="pull-left">
							<p>请复制一下链接地址发送给同事或好友：</p>
							<input type="text" id="shareResourceText" readonly style="width: 350px; cursor:text;">
							<br/>
							<button class="btn btn-primary" id="copyShareUrl" data-clipboard-target="shareResourceText" style="cursor:pointer;">
							复制链接</button>
							<span id="showCopySuccess" style="display: none">已复制到剪贴板</span>
						</div>
						<div id="qrcodeWrap" class="pull-right"></div>
					</div>
					
					<p class="hint" style="margin-top:70px;">公开链接让您轻松分享文件给任何人，
					<c:choose>
						<c:when test="${teamType eq 'myspace' or teamType eq 'pan'}">
							无需登录团队文档库即可查看下载。						
						</c:when>
						<c:otherwise>
							团队之外的成员也可以浏览下载。
						</c:otherwise>
					</c:choose>
					</p>
					<p class="hint">可以添加提取码，私密的分享文件。</p>
					<p class="hint">可以在分享历史中取消分享链接，取消后，此链接失效，其他人无法访问。</p>
				</div>
			</div>
			<div id="shareToOthersDiv" style="display:none">
	           <div class="content-through">
					<form id="share-files-form" method="POST" action="" style="margin:0">
					<div class="shareHolder">
						<div id="files-hidden-area">
							<input type="hidden" name="func" value=sendShareResourceEmail />
							<input type="hidden" name="rid" value=""/>
						</div>
						<p>将链接发送到好友邮箱</p>
						<table>
							<tr>
								<td style="width:15em" id="targetEmailsTd">
									<input type="text" id="targetEmails" name="targetEmails" class="ui-textInput-xLong" />
									<br/>
									<span class="ui-text-note">请输入邮箱地址，以逗号 分号 空格或回车确认</span>
									<input type="hidden" id="targetEmailValues" name="targetEmailValues" value=""/>
								</td>
								<td class="errorContainer" style="width:8em"></td>
							</tr>
						</table>
						<div style="background:#eef;padding:0.5em; margin:0.5em 0">
							<p>
								发送内容：您的好友<span id="currentUser"></span>和您分享团队文档库的文件：<span id="shareResourceTitleSpan"></span>
							</p>
							<p>
								提取地址：<span id="shareResourceUrlSpan"></span>
							</p>
							<table>
								<tr>
									<td><textarea name="message" style="width:388px; height:4em; resize:none; font-size:13px;" placeholder="请添加留言"></textarea></td>
									<td class="errorContainer" style="width:8em"></td>
								</tr>
							</table>
						</div>
						</div>
			            <a class="btn btn-primary" id="submitShareResouce">发送</a>
					</form>
				</div>
			</div>
        </div>
        <div class="modal-footer">
        	<span class="protol"><input type="checkbox" checked="checked" disabled>同意<a href="${contextPath}/sharingAgreements.jsp" target="_blank">《团队文档库分享服务协议》</a></span>
			<a class="btn closeDialog-hideoutMenu"  data-dismiss="modal" aria-hidden="true">关闭</a>
        </div>
    </div>
	
	
	<div id="shareValidate" class="modal hide fade shareModal" style="width:550px;" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
        <div class="modal-header">
          <button type="button" class="close closeDialog-hideoutMenu" data-dismiss="modal" aria-hidden="true">&times;</button>
          <h3>分享文件：<span id="shareValidateTitle"></span></h3>
        </div>
        <div class="modal-body"  style="height:380px;">
        	<h4>团队文档库分享服务协议</h4>
        	<p>请您在使用分享外链服务之前，认真阅读分享服务使用协议。<p>
			<p>第一次使用该服务，请先确认以下协议：</p>
			<ol>
				<li>请您对分享内容负责，分享内容不得涉及淫秽色情内容的文字、图片、视频以及危害国家公共安全和社会和谐稳定的内容及信息。</li>
				<li>团队文档库有权对您公开分享的内容进行审核，一旦发现有涉及违法信息将一律删除，并中止您的分享服务。</li>
			</ol>
			<p class="agree"><label><input type="checkbox" id="userConfirm"> 我同意并遵守<a href="${contextPath}/sharingAgreements.jsp" target="_blank">《团队文档库分享服务协议》</a></label></p>
			<a class="btn btn-primary btn-large" id="submit-User-Confirm">下一步</a>
        </div>
        <div class="modal-footer">
            
			<a class="btn closeDialog-hideoutMenu"  data-dismiss="modal" aria-hidden="true">取消</a>
        </div>
    </div>
	
	<div id="fobiddenShare" class="modal hide fade shareModal" style="width:550px;" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
        <div class="modal-header">
          <button type="button" class="close closeDialog-hideoutMenu" data-dismiss="modal" aria-hidden="true">&times;</button>
          <h3>您的分享被禁用</h3>
        </div>
        <div class="modal-body"  style="height:380px;">
        	<div>
        		您的分享功能被禁用，请联系管理员vlab@cnic.cn
        	</div>
        </div>
        <div class="modal-footer">
			<a class="btn closeDialog-hideoutMenu"  data-dismiss="modal" aria-hidden="true">取消</a>
        </div>
    </div>
	
	<div id="shareTips" class="modal hide fade shareModal" style="width:550px;" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
          <h3>分享公开链接小提示</h3>
        </div>
        <div class="modal-body"  style="height:100px;">
       		<p>恭喜您，即将生成一个访问链接！可以方便让所有人查看这个文件，无需登录团队文档库。</p>
       		<p>在左侧分享历史中，可以随时停用访问链接。</p>
        </div>
        <div class="modal-footer">
			<a class="btn  closeShareTips"  data-dismiss="modal" aria-hidden="true">确定</a>
        </div>
    </div>
	
	<div class="modal fade" id="recommend-success" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" style="display: none;">
        <div class="modal-header">
          <button type="button" class="close closeDialog-hideoutMenu" data-dismiss="modal" aria-hidden="true">&times;</button>
          <h3>分享成功</h3>
        </div>
        <div class="modal-body">
             <p id="recommend-tips">
				您已将该
				<c:choose>
					<c:when test="${itemType eq 'DFile'}">
						文件
					</c:when>
					<c:otherwise>
					页面
					</c:otherwise>
				</c:choose>
				分享给***
			</p>
        </div>
        <div class="modal-footer">
            <input style="display:none" type="button" class="btn btn-primary ui-dialog-close" value="确定" />
        </div>
    </div>
    
</div>


<script type="text/javascript" src="${contextPath}/jsp/aone/js/fileuploader.js?v=${aoneVersion}"></script>
<link href="${contextPath}/jsp/aone/css/fileuploader.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="${contextPath}/scripts/jquery/jquery-ui-1.9.2.min.js"></script>
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/tokenInput.css" type="text/css" />
<script type="text/javascript" src="${contextPath}/jsp/aone/js/toker-jQuery.js?v=${aoneVersion}"></script>
<script type="text/javascript" src="${contextPath}/scripts/zeroclipboard/ZeroClipboard-1.3.5.js"></script>

<script type="text/javascript">
$(document).ready(function(){
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
     }, "请输入有效的邮箱地址,并用英文逗号分隔"); 
	
	$("input[name='newRegister']").blur(function(){
		var url = "<vwb:Link context='shareFile' format='url'/>?func=isExistRegister";
		var params = "newRegister="+$(this).val();
		ajaxRequest(url,params,afterValidateUser);
	});
	
	function afterValidateUser(data){
		if(data.result){
			$("input[name='isFirst']").attr("value",false);
			$("#password-input-row").show();
			$("input[name='password']").focus();
		}else{
			$("input[name='isFirst']").attr("value",true);
			$("#password-input-row").hide();
		}
	};
	
	$("input[name='password']").change(function(){
		var url = "<vwb:Link context='shareFile' format='url'/>?func=isPasswordCorrect";
		var params = "newRegister="+$("input[name='newRegister']").val()+"&password="+$(this).val();
		ajaxRequest(url,params,afterValidatePassword);
	});
	
	function afterValidatePassword(data){
		if(data.result){
			$("input[name='password']").parent().next().html("");
			$("input[name='name']").attr("value",data.message);
			$("#share-file-submit-button").attr("disabled",false);
		}else{
			$("input[name='password']").parent().next().html("密码填写错误！");
			$("input[name='name']").attr("value","");
			$("#share-file-submit-button").attr("disabled",true);
		}
	};
     
    var loginURL = "<vwb:Link context='shareFile' format='url'/>?func=shareAgain";
    var currentValidator = $("#share-files-form").validate({
    	submitHandler:function(){
			var queryString = $("#share-files-form").serialize();
    		var rid=$("#share-files-form input[name=rid]").val();
    		var url="<vwb:Link context='file'  format='url'/>/"+rid;
			ajaxRequest(url,queryString,afterRecommendToOthers);
    	},
		rules: {
			newRegister: {required: true,email: true},
			targetEmails: {required: true,multiemail: true},
			name:{required:true}
		},
		messages:{
			newRegister:{required:"请输入邮箱",email:"请输入有效的邮箱地址"},
			targetEmails:{required:"请输入邮箱",multiemail:"请输入有效的邮箱地址,并用英文逗号分隔"},
			name:{required:"请输入您的姓名"}
		},
		errorPlacement: function(error, element){
			error.appendTo(element.parent().next(".errorContainer"));
		}
	});
    
    function split( val ) {
		return val.split( /,\s*/ );
	};
	
	function extractLast( term ) {
		return split( term ).pop();
	};
    $( "#targetEmails" ).autocomplete({
		minLength: 0,
		source: function( request, response ) {
			var search_user_url = "<vwb:Link context='contacts' format='url'/>?func=searchUser";
			if(request.term!=""){
				$.ajax({
						url: search_user_url,
						dataType: "json",
						type: "POST",
						data: {
							pinyin: extractLast( request.term )
						},
						success: response,
						statusCode:{
							450:function(){alert('会话已过期,请重新登录');},
							403:function(){alert('您没有权限进行该操作');}
						}
				});
			}
		},
		focus: function( event, ui ) {
			//$( "#project" ).val( ui.item.label );
			return false;
		},
		select: function( event, ui ) {
			var terms = split( this.value );
			// remove the current input
			terms.pop();
			// add the selected item
			terms.push( ui.item.name );
			// add placeholder to get the comma-and-space at the end
			terms.push( "" );
			this.value = terms.join( ", " );
			var email = $("#targetEmailValues").attr("value");
			if(email == "") {
				email = ui.item.email;
			}
			else {
				email = email + "," + ui.item.email;
			}
			$("#targetEmailValues").attr("value", email);
			return false;
		},
		delay:30
	})
	.data( "autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li></li>" )
			.data( "item.autocomplete", item )
			.append( "<a>" + item.name+"("+item.email+")" +"</a>" )
			.appendTo( ul );
	};
});
</script>

<div id="tempTemplate" style="display:none">
<input type="text" id="targetEmails" name="targetEmails" class="ui-textInput-xLong" />
			<span class="ui-text-note">多个邮箱可以用逗号 分号 空格或回车分割</span>
			<input type="hidden" id="targetEmailValues" name="targetEmailValues" value=""/>
</div>


