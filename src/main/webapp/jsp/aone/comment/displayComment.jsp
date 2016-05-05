<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page import="net.duckling.ddl.common.*"%>
<%@ page import="net.duckling.ddl.constant.Constant"%>
<style>
textarea.replyContent { width:480px; max-width:480px; min-width:480px; height:3em; }
textarea.replyContent.standby { height:1.2em; color:#999; }
.replyCtrl { display:none; width:35em; }
a.reply-to-this { margin-left:1em; font-size:10pt; cursor:pointer; /* used in js, do not change name */ }
li#hide-detail-button, li#show-detail-button { cursor:pointer; }
</style>
<%
	pageContext.setAttribute("baseURL", VWBContainerImpl.findContainer().getBaseURL());
	String basePath = getServletContext().getRealPath("/");
	String aoneVersion = Constant.getVersion(basePath);
	request.setAttribute("aoneVersion", aoneVersion);
%>
<script src='${contextPath}/scripts/mentions_input/underscore-min.js' type='text/javascript'></script>
<script type="text/javascript" src="${contextPath}/scripts/mentions_input/jquery.mentionsInput.js?v=${aoneVersion}"></script>
<script type="text/javascript" src="${contextPath}/scripts/mentions_input/jquery.elastic.js"></script>
<script type="text/javascript" src="${contextPath}/scripts/mentions_input/jquery.events.input.js"></script>
<link href='${contextPath}/scripts/mentions_input/jquery.mentionsInput.css' rel='stylesheet' type='text/css'>

<p class="a1-comment-title">共<span id="count-span"></span>条回复
	<input type="hidden" name="count" value="0"/>
</p>
<ul id="a1-comment" class="a1-comment" ></ul>
<ul id="newComment" class="a1-comment" >
	<vwb:UserCheck status="authenticated">
		<li id="add-comment-box">
			<form id="commentForm">
				<input type="hidden" name="receiver" id="receiver-field" value="0"/>
				<textarea name="content" id="content-field" class="replyContent standby">添加回复</textarea>
				<div class="replyCtrl">
					<input class="btn replyButton" type="button" value="回复" title="Ctrl+回车"/>
					<span class="ui-text-note">Ctrl+回车&nbsp;即可回复</span> <br/>
					<div id="textareaAlert" class="ui-spotLight"></div>
				</div>
			</form>
		</li>
	</vwb:UserCheck>
</ul>
<script id="brief-mode-template" type="text/html">
	<li id="show-detail-button"><a>显示全部<span id="brief-count-span">{{= comment_count}}</span>条</a></li>
</script>
<script id="detail-mode-template" type="text/html">
	<li id="hide-detail-button"><a>收起回复</a></li>
</script>
<script id="comment-template" type="text/html">
	<li class="comment-display-box" id="{{= comment_id}}">
		<span>{{html delete_html}}</span>
		<a href="{{= sender_url}}">{{= sender_name}}</a>
		<span class="a1-comment-time">{{= createTime}}</span><br/>
		{{= receiver_name}}<span class="commentContent">{{html content}}</span>
		<vwb:UserCheck status="authenticated">
			{{html reply_html}}
		</vwb:UserCheck>
	</li>
</script>

<script type="text/javascript">
	$(document).ready(function(){
		
	});
	
	function getCurrentRid(){
		return "${rid}" || $("#currentRid").val();
	}

	function afterShowBriefMode(data){
		$("#a1-comment").html("");
		var temp = new Object();
		temp.comment_count = data.commentSize;
		if (temp.comment_count > 3)
			$("#brief-mode-template").tmpl(temp).appendTo("#a1-comment");
		renderCommentList(data);
		$(".comment-fold").hide();
		$(".comment-expand").show();
	};
	
	var current_loader = {
		_default:function CommentDefaultSetting(){
			this.url = site.getURL("comment",null);
			this.params = {"itemType":"${itemType}","rid":getCurrentRid()};
		},
		_setting:function(option){
			var _setting = new this._default();
			for(var prop in option){
				_setting.params[prop] = option[prop];
			}
			return _setting;
		},
		loadComments:function(callback){
			var setting = this._setting({"func":"loadComments"});
			if(getCurrentRid()){
				ajaxRequest(setting.url,setting.params,function(data){
					afterShowBriefMode(data);
					if(typeof(callback)=="function"){
						callback(data);
					}
				});
			}
		},
		addComment:function(){
			var setting = this._setting({"func":"submitComment","content":getCommentContent(),"receiver":$("#receiver-field").val(),"mentionUserId":getMentionUserId()});
			$.ajax({
				url :setting.url,
				data : setting.params,
				type : "post",
				async : false,
				dataType :"json",
				error : function(request,data1,data2){
					alert("登录信息已经过期请重新登录！");
					window.location.reload();
				},
				success : function(data){
					renderCommentRecord(data);
					updateCount('+');
					resetPage();
				},
				statusCode:{
					450:function(){alert('会话已过期,请重新登录');},
					403:function(){alert('您没有权限进行该操作');}
				}
			});
		},
		removeComment:function(id){
			var setting = this._setting({"func":"removeComment","commentId":id});
			ajaxRequest(setting.url,setting.params,function(data){
				if(data.status){
					updateCount('-');
					$(".comment-display-box[id='"+data.commentId+"']").remove();
				}
			});
		},
		showBriefComments:function(){
			var setting = this._setting({"func":"showBriefComments"});
			ajaxRequest(setting.url,setting.params,afterShowBriefMode);
		},
		showDetailComments:function(){
			var setting = this._setting({"func":"showDetailComments"});
			ajaxRequest(setting.url,setting.params,function(data){
				$("#a1-comment").html("");
				var temp = new Object();
				$("#detail-mode-template").tmpl(temp).appendTo("#a1-comment");
				renderCommentList(data);
				$(".comment-fold").show();
				$(".comment-expand").hide();
			});
		}
	};
	var userDate;
	var formHtml;
	$('textarea.replyContent.standby').die().live('click', function(){
		initFormHtml();
		focusTextarea($(this));
		addMention();
	});
	function initFormHtml(){
		if(!formHtml){
			formHtml = $("#commentForm").html();
		}
	}
	
	var mentionFlag = false;
	function addMention(){
		if(!mentionFlag){
			$('textarea#content-field').mentionsInput({
				minChars      : 0,
			    showAvatars   : false,
			    onDataRequest:function (mode, query, callback) {
			    	if(userDate){
			    		responseData = prepareUserDate(userDate);
			    		responseData = _.filter(responseData, function(item) { return item.name.toLowerCase().indexOf(query.toLowerCase()) > -1 });
			            callback.call(this, responseData);
			    	}else{
				    	$.getJSON("<vwb:Link context='recommend' format='url'/>?func=getTeamUser&rid=" + getCurrentRid() + "&itemType=${itemType}", function(responseData) {
				    		userDate = responseData;
				    		responseData = prepareUserDate(responseData);
				            responseData = _.filter(responseData, function(item) { return item.name.toLowerCase().indexOf(query.toLowerCase()) > -1 });
				            callback.call(this, responseData);
				          });
			    	}
			    }
			  });
			mentionFlag=true;
		}
		$('textarea#content-field').focus();
	};
	
	function getCommentContent(){
		var result = $('textarea#content-field').val();
		$('textarea#content-field').mentionsInput('getMentions', function(data) {
		      if(data&&data.length>0){
		    	  $.each(data,function(index,item){
			    	  var tmp = item.value;
			    	  var name ="<a href='${baseURL}/system/user/"+item.id+"' target='_blank' class='mention'>#@#"+item.avatar+"</a>";
			    	  result = result.replace(tmp,name);
		    	  });
		      }
		    });
		var reg=new RegExp("#@#","g");
		result = result.replace(reg,'@');
		var s = {"url":'${baseURL}','result':''};
		$('textarea#content-field').mentionsInput('getTextValue',s);
		return s.result;
	}
	function getMentionUserId(){
		var result =[];
		$('textarea#content-field').mentionsInput('getMentions', function(data) {
		      if(data&&data.length>0){
		    	  $.each(data,function(index,item){
			    	 result.push(item.id);
		    	  });
		      }
		 });
		return result;
	}
	function prepareUserDate(data){
		var result =[];
		if(data){
			$.each(data ,function(index,item){
				var s=new Object();
				s.id = item.id;
				s.name = item.name+"("+item.email+")"+item.pinyin;
				s.avatar=item.name;
				s.type='contact';
				s.value = '@'+item.name;
				result.push(s);
			});
		}
		return result;
	}
	
	$('textarea.replyContent').live('keypress', function(e){
		if (e.ctrlKey && e.which == 13 || e.which == 10) {
			$(".replyButton").click();
		}
	});
	
	function focusTextarea(obj){
		if (obj.text()=='添加回复') {
			obj.val('');
		}
		obj.removeClass('standby');
		obj.next().show();
	}
	
	function renderCommentRecord(currentData){
		var temp = new Object();
		temp.comment_id = currentData.id;
		temp.sender_name = currentData.sender.name;
		temp.sender_url = site.getURL("user",currentData.sender.id);
		if(currentData.receiver!=undefined && currentData.receiver.id!=0){
			temp.receiver_name = "回复"+currentData.receiver.name+": ";
		}else{
			temp.receiver_name = "";
		}
		temp.reply_html = "";
		temp.delete_html = "";
		var uid="${uid}";
		if(uid==null||uid==""||uid == undefined){
			uid = $("#currentUid").val();
		}
		if(currentData.sender.uid!=uid){
			temp.reply_html = '<a class="reply-to-this" receiver="'+currentData.sender.uid+'">回复</a>'
		}
		else{
			temp.delete_html = '<a class="a1-comment-del" title="删除"></a>';
		}
		temp.receiver_id = currentData.sender.id;
		temp.createTime = currentData.createTime;
		temp.content = currentData.content;
		$("#comment-template").tmpl(temp).appendTo("#a1-comment");
	}
	
	
	function renderCommentList(data){
		for(var i=0;i<data.commentList.length;i++){
			renderCommentRecord(data.commentList[i]);
		}
		$("input[name='count']").attr("value",data.commentSize);
		$("#count-span").html($("input[name='count']").attr("value"));
		resetPage();
	};
	
	var resetPage = function(){
		$("textarea[name='content']").attr("value","添加回复").addClass('standby').blur();
		$("input[name='receiver']").attr("value","0");
	};
	
	function updateCount(sign){
		var count = parseInt($("input[name='count']").attr("value"));
		if(sign=='+')
			count = count + 1;
		else
			count = count - 1;
		$("input[name='count']").attr("value",count);
		$("#count-span").html(count);
		var spanObj = $("#brief-count-span");
		if(spanObj!=null&&spanObj!=undefined)
			$(spanObj).html(count);
	}

	var blurTextarea = function(object){
		object.blur();
		object.next().hide();
	};
	
	$(".replyButton").die().live('click',function(){
		var val = $('textarea[name="content"]').val();
		val = val.replace(/(^\s*)|(\s*$)/g, ''); 
		if (val=='') {
			ui_spotLight('textareaAlert', 'fail', '回复内容不能为空！', 'fade');
			$('textarea[name="content"]').val('');
		}
		else {
			current_loader.addComment();
			clearMention();
			blurTextarea($("textarea[name='content']"));
		}
	});
	
	function clearMention(){
		if(mentionFlag){
			$("textarea#content-field").mentionsInput('reset');
			$("#commentForm").html(formHtml);
			$('textarea[name="content"]').val('');
			mentionFlag = false;
		}
	}
	
	$(".reply-to-this").die().live('click',function(){
		initFormHtml();
		clearMention();
		var textarea = $("textarea[name='content']");
		focusTextarea(textarea);
		$("input[name='receiver']").attr("value",$(this).attr("receiver"));
		addMention();
		textarea.val("回复"+$(this).parent().children("a").html()+":");
		$("textarea[name='content']").next().hide();
		textarea.focus();
	});
	
	current_loader.loadComments();
	resetPage();
	
	$("#a1-comment .a1-comment-del").die().live('click',function(){
		current_loader.removeComment($(this).parent().parent().attr("id"));
	});
	
	$('#hide-detail-button').die().live('click',function(){
		current_loader.showBriefComments();
		initFormHtml();
		clearMention();
	});
	
	$("#show-detail-button").die().live('click',function(){
		current_loader.showDetailComments();
		initFormHtml();
		clearMention();
		$(this).viewFocus();
	});
	
	$(".replyButton ,.reply-to-this,.a1-comment-del").die().live('click',function(){	
		var resultURL = $(document).attr("URL");
		var resultURLs= new Array(); 
		resultURLs = resultURL.split("/");
		var pid = resultURLs[resultURLs.length-1];
		if(pid.lastIndexOf("rid=")!=(-1)){
			pid = pid.substr(0,pid.lastIndexOf("?"));
	
		}
		var type = resultURLs[resultURLs.length-2];
		var params = {"type":type,"pid":pid,"oper_name":$(this).attr('class')};
		ajaxRequest("<vwb:Link context="reclogging" format="url"/>?func=dapgelog",params,function(data){	
		});
	});
	
</script>

