<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<script type="text/javascript">
$(document).ready(function(){
	/* Resource Selector */
	var toolBar = new shadeConsole({
		'console': $('#resourceAction'),
		'anchor': $('.ui-wrap #content-major')
	});
	
	toolBar.show();
	$('a[name=cancel]').click(function(){
		$(this).parents('div.ui-dialog').fadeOut();
	});
	/* END resource selector */
	//fullScreen
	$('#resAction .fullScreen').toggle(
		function(){
			$("body").addClass("fullScreenView");
			$("#macroNav,#masthead,#tagSelector,#footer,#content-side").hide();
			$(this).find("a").html('<span class="resAction-fullScreen"></span>退出全屏');	
		}, 
		function(){
			$("body").removeClass("fullScreenView");
			$("#macroNav,#masthead,#tagSelector,#footer,#content-side").show();
			$(this).find("a").html('<span class="resAction-fullScreen"></span>全屏阅读');	
		}
	);
	
	$('.toolDelete').click(function(){
		$('#delete-attach-dialog').attr('style','width:400px; position:fixed; top:30%; left:30%;').fadeIn();
	});
	
	$('a.update-file-button').click(function(){
		curr_params = '{"fid":"'+$(this).attr('fid')+'", "version":"'+$(this).attr('version')+'", "bid":"'+${fileBarBid}+'"}';
		curr_params = eval("("+curr_params+")");
		ui_showDialog("upload-attach-dialog");
	});
	
	$("#share-inside").click(function(){
		prepareRecommend("<vwb:Link context='recommend' format='url'/>?func=prepareRecommend&itemId=${fileBarItemId}&itemType=DFile",'${fileBarItemId}',"${resource.title}");
	});
	
	$('#resAction-share').pulldownMenu({
		'menu' : $('#resAction-shareMenu'),
		'anchor' : $('#resAction-share'),
		'direction' : 'up',
		'position' : 'fixed'
	});
	//add by lvly@2012-07-23
	$('#resAction #resAction-hot a').pulldownMenu({
		menu : $('#visitor.pulldownMenu'),
		anchor:	$('#resAction #resAction-hot'),
		direction:	'up',
		position: 'fixed'
	});
	$('#visitor.pulldownMenu .closeThis').click(function(){
		$('#visitor.pulldownMenu').hide();
	});
	//do copy
	$('.toolCopy').click(function(){
		$.ajax({
			   type: "POST",
			   url: "<vwb:Link context='copy' format='url'/>?func=getCanEditTeamList&fromRid=${resource.rid}",
			   success: function(msg){
				   $("#copyToTeamList").html("");
				   $("#copy-team-template").tmpl(msg).appendTo("#copyToTeamList");
				   $('#copy-page-dialog').attr('style','width:400px; position:fixed; top:30%; left:30%;').fadeIn();
			   },
				statusCode:{
					450:function(){alert('会话已过期,请重新登录');},
					403:function(){alert('您没有权限进行该操作');}
				}
		});
		
	});
	$('#doCopyButton').click(function(){
		var version='${curVersion.version}';
		if(version==''){
			version='${version}';
		}
		if(version==''){
			version='${file.lastVersion}';
		}
		if(version==''){
			version='${resource.lastVersion}';
		}
		var param={toTids:[],fromRid:'${resource.rid }',cover:[],'version':version};
		$('input[name=toCopyTeam]').each(function(index,item){
			if($(item).attr("checked")){
				var teamId=$(item).attr("value");
				param.toTids.push(teamId); 
				param.cover.push($("#need_cover_"+teamId).attr("checked")?true:false);
			}
		});
		
		if(param.toTids.length==0){
			 showCopyMsg("请您至少选择一个团队");
			 return;
		}
		ui_hideDialog("copy-page-dialog");
		$.ajax({
			   type: "POST",
			   data:param,
			   url: "<vwb:Link context='copy' format='url'/>",
			   success: function(msg){
				   if(!msg){
					   showCopyMsg("复制失败！");
				   }else{
					   showCopyMsg("复制成功！");
				   }
			   },
			   error:function(){
				   showCopyMsg("复制失败！");
			   },
				statusCode:{
					450:function(){alert('会话已过期,请重新登录');},
					403:function(){alert('您没有权限进行该操作');}
				}
		});
		
	});
	function showCopyMsg(msg){
		$('#copyMsg').html(msg);
		ui_showDialog("copy-msg-dialog",1500);
	}
})
function showCoverOrCreate(obj){
	if($(obj).attr("needCover")=='true'){
		$(obj).next().show();
	}
}
</script>

<div id="resourceAction" class="shadeConsole">
	<ul id="resAction">
		<vwb:CLBCanUse>
		<li><a class="update-file-button"><span class="resAction-update"></span>更新</a></li>
		</vwb:CLBCanUse>
		<li><a class="toolDelete"><span class="resAction-delete"></span>删除</a></li>
		<li id="resAction-share"><a><span class="resAction-share"></span>分享</a></li>
		<li><a class="toolCopy"><span class="resAction-copy"></span>复制到</a></li>
	
		<!-- add by lvly@2012-07-23 -->
		<li id="resAction-hot"> 
				<a><span class="resAction-hot"></span><span>热度：<vwb:VisitCount /></span></a>
 			</li>
		
		<li class="fullScreen"><a><span class="resAction-fullScreen"></span>全屏阅读</a></li>
	</ul>
</div>
<div id="resAction-shareMenu" class="pulldownMenu" style="position:fixed">
	<ul>
		<li><a id="share-inside">分享给团队成员</a></li>
		<li><a href="<vwb:Link context='file' page='${fileBarItemId}' format='url'/>?func=shareExistFile">分享给其他人</a></li>
	</ul>
</div>

<c:set var="itemId" value="${fileBarItemId }" scope="request"/>
<c:set var="itemType" value="DFile" scope="request"/>
<jsp:include page="/jsp/aone/recommend/addRec.jsp"></jsp:include>

<div class="ui-dialog" id="delete-attach-dialog" style="width:400px;">
	<p class="ui-dialog-title">删除文件</p>
	<p>您真的要删除此文件吗？</p>
	<p style="color:red">提示：该操作将会使所有关于此文件的下载链接失效。并从当前组合中移除！</p>
	<div class="ui-dialog-control">
		<form action='${deleteFileURL} ' method="POST" id="deleteFileForm">
			<input id ="deleteFileButtons" type="submit" value="删除"/>
			<a id="delete-cancel" name="cancel">取消</a>
		</form>
	</div>
</div>

<div class="ui-dialog" id="upload-attach-dialog" style="width:400px;">
		<p class="ui-dialog-title">更新文件</p>
	
		<div id="file-uploader-demo1">
			<div class="qq-uploader">
				<div class="qq-upload-button">上传文件
					<input type="file" multiple="multiple" name="files">
				</div>
				<ul class="qq-upload-list fileList"></ul>
			</div>
		</div>
		
		<div class="ui-dialog-control">
			<input type="button" id="attach-to-bundle" value="完成"/>
			<a name="cancel">取消</a>
		</div>
</div>
<div class="ui-dialog" id="copy-page-dialog" style="width:400px;">
	<p class="ui-dialog-title">复制到以下团队</p>
	<p>
		<ul id="copyToTeamList" style="list-style:none; text-align:left; max-height:300px;  overflow:auto;">
			
		</ul>
	</p>
	<p style="color:red"></p>
	<div  class="ui-dialog-control">
		
			<input type="button" id="doCopyButton" value="复制"/>
			<a id="delete-cancel" name="cancel">取消</a>
	</div>
</div>
<div class="ui-dialog" id="copy-msg-dialog" style="width:400px;">
	<p class="ui-dialog-title">消息</p>
	<p id="copyMsg">
	</p>
	<p style="color:red"></p>
	<div  class="ui-dialog-control">
	</div>
</div>
<div class="ui-dialog" id="delete-error-dialog" style="width:400px;position:fixed;left:30%;">
<p class="ui-dialog-title">删除错误</p>
<p style="color:red;line-height:50px;">您无权删除该文件，只能由文件创建者或管理员进行删除！
</p>
</div>

<script type="text/javascript">
$(document).ready(function(){
	$("#deleteFileForm").validate({
		submitHandler:function(form){
			ui_hideDialog("delete-attach-dialog")
			$.ajax({
				url :'${validateURL} ',
				type : "post",
				dataType : "json",
				success : function(data){
					if(!data.status){
						$('#delete-error-dialog').attr('style','width:400px; height:120px; position:fixed; top:30%; left:30%;').fadeIn();
						window.setTimeout(function(){
							$('#delete-error-dialog').fadeOut(500);
						},4000);
					}else{
						form.submit();
					}
				},
				statusCode:{
					450:function(){alert('会话已过期,请重新登录');},
					403:function(){alert('您没有权限进行该操作');}
				}
			});
			
	}});
	
});
</script>
<script type="text/html" id="copy-team-template">
<li>
	<input onclick="showCoverOrCreate(this);" needCover="{{= needCover}}" type="checkbox" value="{{= team.id}}" name="toCopyTeam"/>{{= team.displayName}}
	<span style="display:none">	
		<input type="checkbox" value="true" id="need_cover_{{= team.id}}">勾上是覆盖，不勾是新建
	</span>
</li>
</script>
<jsp:include page="/jsp/aone/browse/fileReadLog.jsp"></jsp:include>