<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<link href="${contextPath}/jsp/aone/css/tag-z.css" rel="stylesheet" type="text/css"/>
<link href="${contextPath}/scripts/bootstrap/css/bootstrap.min.css" rel="stylesheet" media="screen"/>
<link href="${contextPath}/scripts/bootstrap/css/bootstrap-responsive.min.css" rel="stylesheet"/>
<link href="${contextPath}/scripts/bootstrap/css/todc-bootstrap.css" rel="stylesheet"/>	
<script src="${contextPath}/scripts/bootstrap/js/bootstrap.min.js"></script>
<fmt:setBundle basename="templates.default" />

<script type="text/javascript">
$(document).ready(function(){
	var joinUrl = "<vwb:Link context='joinPublicTeam' format='url'/>?func=join&teamId=";
	
	$("a.cancelApply").live("click",function(){
		var tid = $(this).parents("li[tid]").attr('tid');
		$.ajax({
			url: site.getURL('joinPublicTeam', null),
			type: 'POST',
			data:'func=cancelApply&teamId='+tid,
			success:afterCancelApply,
			error:function(){
				alert('加入失败!');
			},
			statusCode:{
				450:function(){alert('会话已过期,请重新登录');},
				403:function(){alert('您没有权限进行该操作');}
			}
		});
	});
	
	function afterCancelApply(data){
		data = JSON.parse(data);
		if(typeof(data.status) != 'undefined' && data.status){
			var target = $('#status-button-'+data.tid);
			target.html("<a class='joinToTeam' href='"+joinUrl+data.tid+"'>加入团队</a>");
			alert("取消申请成功！");
		}else{
			alert("取消申请失败！");
		}
	}
	
	function afterJoinToTeam(data){
		data = eval("("+data+")");
		if(data.status != 'success'){
			if(data.status == 'unAuth'){
				var isOK = confirm("您尚未登录，请登录后在【我的团队】中使用该功能！\n是否跳转到登录页面？");
				console.log(site.getURL('switchTeam',null));
				if(isOK)
					window.location.href=site.getURL('switchTeam',null);
			}else{
				alert('您已经是该团队的成员，无需加入！');
			}	
			return;
		}
		var target = $('#status-button-'+data.tid);
		if(data.accessType=='public'){
			var teamUrl = site.getURL('switchTeam', null)+"?func=jump&team="+data.tname;
			target.html("<span>已是团队成员</span><a target='_blank' href='"+teamUrl+"'>前往团队</a><a class='quitFromTeam'>退出团队</a>");
			alert('加入成功，您现在可以浏览该团队信息！');
		}else{
			target.html("<span>已申请, 待审核</span><a class='cancelApply'>取消申请</a>");
			alert('申请中，正在等待团队管理员审核！');
		}
	}
	
	<%-- 团队退出功能统一放到个人面板管理
	$('a.quitFromTeam').live('click',function(){
		var tid = $(this).parents("li[tid]").attr('tid');
		$.ajax({
			url: site.getURL('joinPublicTeam', null),
			type: 'POST',
			data:'func=quit&teamId='+tid,
			success:afterQuitFromTeam,
			error:function(){
				alert('退出失败!');
			},
			statusCode:{
				450:function(){alert('会话已过期,请重新登录');},
				403:function(){alert('您没有权限进行该操作');}
			}
		});
	});
	--%>
	
	function afterQuitFromTeam(data){
		data = eval("("+data+")");
		if(!data.status){
			alert("退出失败！您并不是该团队的成员！");
			return;
		}
		var target = $('#status-button-'+data.tid);
		target.html("<a class='joinToTeam' href='"+joinUrl+data.tid+"'>加入团队</a>");
		alert("退出成功！您已经不是该团队的成员，将无法浏览该团队信息！");
	}
	
	$("li:not(.active) .changePageNo").live("click",function(){
		$("#submitForm input[name=pageNum]").val($(this).attr('page'));
		var v = $("#queryKeyword").val();
		if(v){
			$("#submitForm form").append("<input type='hidden' name='func' value='search'>");
			var s = $("#submitForm form").serialize();
			loadQuery(s);			
		}else{
			$("#submitForm form").submit();
		}
	});
	function loadQuery(s){
		var l = window.location.pathname;
		window.location = l+"?"+s;
	}
	
	var teamSearch = new SearchBox('searchTeam', "输入团队名称搜索团队", false, false, false);
	teamSearch.setPullDown('未找到相关资源', '搜索引擎错误', '350px');
	teamSearch.container.addClass('loaded');
	teamSearch.container.parent().addClass('loaded');
	
	teamSearch.doSearch = function(QUERY){
		if(!QUERY){
			window.location = window.location.pathname;
			return;
		}
		$("#submitForm form").append("<input type='hidden' name='func' value='search'>");
		$("#submitForm input[name=pageNum]").val('1');
		$("#queryKeyword").val(QUERY);
		var s = $("#submitForm form").serialize();
		$("#queryKeyword").val("");
		loadQuery(s);
	};
	(function(){
		if('${keyword}'){
			$("#searchTeam input.standby").val("${keyword}");
			$("#searchTeam a.search_reset").removeAttr("disable");
		}
	})();
	
	
	teamSearch.resetSearch = function(){
		this.hideCover();
		this.clearPullDown();
		window.location = window.location.pathname;
	};
	
	/* tabindex */
	teamSearch.searchInput.attr('tabIndex', 1);
	setTimeout(function(){
		teamSearch.container.addClass('transition');
	}, 200);
	
});
</script>

<div id="team-list" class="existTeamList">
	<h3>
		<%-- <span class="search">
			<input type="text" id="queryTeamText" value="${keyword }"> 
			<input type="button" id="queryTeam" value="输入团队名称搜索团队">
		</span> --%>
		<ul class="quickOp" id="collectinOp" style="float:right;">
			<li id = "searchTeamPanel" class="search"><div id="searchTeam"></div></li>
		</ul>
		公开团队
	</h3>
	
	<c:if test="${teamList.size() <=0 }">
		<div>
			<c:choose>
				<c:when test="${empty keyword}">
					<h3>当前没有公开的团队！</h3>
				</c:when>
				<c:otherwise>
					<h3>当前没有搜索到公开的团队！</h3>
				</c:otherwise>			
			</c:choose>
		
		</div>
	</c:if>
	
	<ul class="teamList">
		<c:forEach items="${teamList}" var="team" varStatus="cur">
			<li class="element-data" tid="${team.id }">
				<div class="resBody">
					<h2>
						<span class="page-link">${team.displayName}</span>
						<span class="create">${creatorNames[cur.index] } 创建于 ${team.createTime }</span> 
						<c:if test="${team.accessType =='protected' }">
							<span class="need">（需审核）</span>
						</c:if>
					</h2>
					<div class="resChangeLog">
						<span class="team-url">${teamUrl[cur.index] }</span>
					</div>
				</div>
				<c:choose>
					<c:when test="${isAuthenticated }">
						<div id="status-button-${team.id}" class="oper">
							<c:choose>
								<c:when test="${teamStatus[cur.index].status == 'teamMember'}">
									<span>已是团队成员</span>
									<a target="_blank" style="border-right: none;" href="<vwb:Link context='switchTeam' format='url'/>?func=jump&team=${team.name}">前往团队</a>
									<%-- <a class="quitFromTeam btn-mini btn btn-danger">退出团队</a>  --%>
								</c:when>
								<c:when test="${team.accessType=='protected' && teamStatus[cur.index].status == 'reject' }">
									<span>审核未通过</span>
									<span>理由：${teamStatus[cur.index].reason }</span>
									<a class="joinToTeam btn-mini btn btn-success" href="<vwb:Link context='joinPublicTeam' format='url'/>?func=join&teamId=${team.id}">加入团队</a>
								</c:when>
								<c:when test="${team.accessType=='protected' && teamStatus[cur.index].status == 'waiting'}">
									<span>已申请, 待审核</span>
									<a class="cancelApply">取消申请</a>
								</c:when>
								<c:otherwise>
									<a class="joinToTeam btn-mini btn btn-success" href="<vwb:Link context='joinPublicTeam' format='url'/>?func=join&teamId=${team.id}">加入团队</a>
								</c:otherwise>
							</c:choose>
						</div>
					</c:when>
					<c:otherwise>
						<div id="status-button-${team.id}" class="oper">
							<a class="joinToTeam btn-mini btn btn-success" href="<vwb:Link context='joinPublicTeam' format='url'/>?func=join&teamId=${team.id}">加入团队</a>
						</div>
					</c:otherwise>
				</c:choose>
				<div class="ui-clear"></div>
				<div class="teamDes">
					<span>团队描述：${team.description}</span>
				</div>
				<div class="ui-clear"></div>
			</li>
			
		</c:forEach>
		
	</ul>
	<c:if test="${pageNum.totalPageNum>1 }">
	<div class="pagination">
	  <p class="totalCount">共${pageNum.totalPageNum}页  ${pageNum.totalNum}团队</p>
	  <ul>
    	<c:choose>
    		<c:when test="${pageNum.hasPre()==true }">
    			<li><a href="javascript:void(0)" page='${pageNum.currPageNum-1}' class='changePageNo'>上一页</a></li>
    		</c:when>
    	</c:choose>
	   <c:forEach items="${pageNum.pageNumList }" var="item">
	    	<c:choose>
	    		<c:when test="${item==pageNum.currPageNum }">
	    			<li class='active'><a href="javascript:void(0)" page='${item}' class='changePageNo'>${item}</a></li>
	    		</c:when>
	    		<c:otherwise>
	    			<li><a href="javascript:void(0)" page='${item}' class='changePageNo'>${item}</a></li>
	    		</c:otherwise>
	    	</c:choose>
	    </c:forEach> 
    	<c:choose>
    		<c:when test="${pageNum.hasNext()==true }">
    			<li><a href="javascript:void(0)" page='${pageNum.currPageNum+1}' class='changePageNo active'>下一页</a></li>
    		</c:when>
    	</c:choose>
	  </ul>
	 
	  <div class="clear"></div>
	</div>
	</c:if>
</div>

<div id="submitForm" style="display:none">
	<form action="<vwb:Link context='joinPublicTeam' format='url'/>" method='post'>
		<input name="pageNum" value="" />
		<input type="hidden" id="queryKeyword" name="keyword" value="${keyword }">
	</form>
</div>



