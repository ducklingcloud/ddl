<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<script type="text/javascript">
$(document).ready(function(){
	//Resource Selector
	var toolBar = new shadeConsole({
		'console': $('#resourceAction'),
		'anchor': $('#content #content-major')
	});

	toolBar.show();
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
	
	$('#resAction #resAction-hot a').pulldownMenu({
		menu : $('#visitor.pulldownMenu'),
		anchor:	$('#resAction #resAction-hot'),
		direction:	'up',
		position: 'fixed'
	});
	
	$('#visitor.pulldownMenu .closeThis').click(function(){
		$('#visitor.pulldownMenu').hide();
	});
	
	
	$(".interest-box").click(function(){
		prepareSubscription("<vwb:Link context='feed' format='url'/>?func=preparePageFeed&pid=${pid}");
	});
	
	$(".recommend-box").click(function(){
		prepareRecommend("<vwb:Link context='recommend' format='url'/>?func=prepareRecommend&itemId=${pid}&itemType=DPage",'${pid}',$("#pageTitleRecorde").val());
	});
	
	$(".remove-interest-box").click(function(){
		prepareRemoveSubscription("<vwb:Link context='feed' format='url'/>?func=removePageFeed&pid=${pid}");
	});
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
		var version='${version}';
		if(version==''){
			version='${curVersion.version}';
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
				param.cover.push($("#need_cover_"+teamId).attr("checked")?true:false)
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


$("a.toolCopy,a.toolEdit,a.toolDelete,a.toolNewPage,a.interest-box,a.remove-interest-box,a.recommend-box,a.resAction-fullScreen").live('click',function(){

	var resultURL = $(document).attr("URL");
	var resultURLs= new Array(); 
	resultURLs = resultURL.split("/");
	var pid = resultURLs[resultURLs.length-1];
	if(pid.lastIndexOf("rid=")!=(-1)){
		pid = pid.substr(0,pid.lastIndexOf("?"));
	}
	var index = pid.indexOf("?");
	if(index>-1){
		pid = pid.substr(0,index);
	}
	var type = resultURLs[resultURLs.length-2];
	var params = {"type":type,"pid":pid,"oper_name":$(this).attr('class')};
	ajaxRequest("<vwb:Link context="reclogging" format="url"/>?func=dapgelog",params,function(data){	
	});
});

function showCoverOrCreate(obj){
	if($(obj).attr("needCover")=='true'){
		$(obj).next().show();
	}
}
</script>

<div id="resourceAction" class="shadeConsole">
	<input type="hidden" id="pageTitleRecorde" value="${pageMeta.title}"/>
	<ul id="resAction">
		<vwb:Permission permission="edit">
			<input type="hidden" name="bid" id="bid-field" value="${bid}"/>
			<c:choose>
				<c:when test="${version eq latestVersion}">
					<li><a  href="<vwb:EditLink format='url'/>&bid=${bid}" class="toolEdit"><span class="resAction-edit"></span><span>编辑</span></a></li>
				</c:when>
				<c:otherwise>
					<li><a  href="<vwb:EditLink format='url'/>&version=${version}&bid=${bid}" class="toolEdit"><span class="resAction-edit"></span><span>编辑</span></a></li>
				</c:otherwise>
			</c:choose>
			
			<li>
				<a class="toolDelete" ><span class="resAction-delete"></span><span>删除</span></a>
			</li>
			<%-- <li>
				<a class="toolDelete" href="#" onClick="$('#deleteForm').submit()"><span class="resAction-delete"></span><span>删除</span></a>
			</li>
			<form action="<vwb:Link format='url' context='view'/>?func=del" class="viewPageForm" id="deleteForm" method="post"
				  onsubmit="return( confirm('确认要删除页面') && Wiki.submitOnce(this) );">
			</form> --%>
		</vwb:Permission>
		
		<vwb:UserCheck status="authenticated">
			<li>
				<input type="hidden" name="subscriptionStatus" value="" />
				<vwb:IsSubscribed flagName="flag" itemsName="existInterest" />
				<c:choose>
					<c:when test="${!flag}">
						<a class="interest-box" attr="${flag}"><span class="resAction-attention"></span><span>关注</span></a>
					</c:when>
					<c:otherwise>
						<a class="remove-interest-box" attr="${flag}"><span class="resAction-attention"></span><span>已关注</span></a>
					</c:otherwise>
				</c:choose> 
			</li>
			<li>
				<a class="recommend-box"><span class="resAction-share"></span><span>分享</span> </a>
			</li>
			<vwb:Permission permission="edit">
			<li>
				<a class="toolCopy"><span class="resAction-copy"></span><span>复制到</span></a>
			</li>
			</vwb:Permission>
 			<li id="resAction-hot"> 
				<a><span class="resAction-hot"></span><span>热度：<vwb:VisitCount /></span></a>
 			</li>
		</vwb:UserCheck>
 		<vwb:UserCheck status="notAuthenticated"> 
	 		<li id="resAction-hot">
	 			<a><span class="resAction-hot"></span><span>热度：<vwb:VisitCount /></span></a>
	 		</li> 
 		</vwb:UserCheck>
		<li class="fullScreen"><a><span class="resAction-fullScreen"></span>全屏阅读</a></li>
	</ul>
</div>
<div class="ui-dialog" id="copy-page-dialog" style="width:400px;">
	<p class="ui-dialog-title">复制到以下团队</p>
	<p>
		<ul id="copyToTeamList" style="list-style:none; text-align:left; max-height:300px; overflow:auto;">
			
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
<script type="text/html" id="copy-team-template">
<li>
	<input onclick="showCoverOrCreate(this);" needCover="{{= needCover}}" type="checkbox" value="{{= team.id}}" name="toCopyTeam"/>{{= team.displayName}}
	<span style="display:none">	
		<input type="checkbox" value="true" id="need_cover_{{= team.id}}">勾上是覆盖，不勾是新建
	</span>
</li>
</script>
<c:set var="itemId" value="${rid }" scope="request"/>
<c:set var="itemType" value="DPage" scope="request"/>
<jsp:include page="/jsp/aone/subscription/addSub.jsp"></jsp:include>
<jsp:include page="/jsp/aone/subscription/deleteSub.jsp"></jsp:include>
<jsp:include page="/jsp/aone/recommend/addRec.jsp"></jsp:include>
<jsp:include page="/jsp/aone/browse/pageReadLog.jsp"></jsp:include>
