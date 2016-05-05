<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div id="macroNav" class="ui-wrap wrapperFull">
	<a id="logo" title="科研在线"></a>
	<div id="macro-innerWrapper" class="wrapper1280">
		<vwb:TeamPreferences/>
		<c:set var="teamType" value="${current}" scope="request"></c:set>
		<ul id="staticNav" class="spaceNav">
			<li <c:if test="${teamType eq 'dashboard'}">class="current"</c:if>>
				<a href="<vwb:Link format='url' context='dashboard'/>">首页</a>
			</li>
			<li <c:if test="${teamType eq 'myspace'}">class="current"</c:if>>
				<a href="<vwb:Link format='url' context='switchTeam' page=''/>?func=person">个人空间</a>
			</li>
		</ul>
		<ul id="spaceNav" class="sortableList spaceNav">
	 		<c:forEach items="${myTeamList}" var="item">
	 			<li <c:if test="${teamType eq item.name}">class="current"</c:if>><a href='<vwb:Link context="switchTeam" page="" format="url"/>?func=jump&team=${item.name}'>${item.displayName}</a></li>
	 		</c:forEach>
		</ul>
		<ul id="spaceNavMore" class="spaceNav">
			<li class="moreSpace"><a title="更多"><span class="iconLynx icon-more"></span></a></li>
			<li class="createSpace"><a href="<vwb:Link format='url' context='createTeam'/>" title="创建团队"><span class="iconLynx icon-create"></span></a></li>
		</ul>
		<ul id="userBox">
			<li class="search"><div id="globalSearch"></div></li>
		<vwb:UserCheck status="authenticated">
		 	<vwb:MessageCount/>
		 	<li class="msgNotification msgCount${totalCount}">
				<a href="javascript:void(0)"><span id="noticeCount">${totalCount}</span>通知</a>
		 	</li>
		 	<li class="userMe">
		 		<a href="javascript:void(0)"><vwb:UserName /></a>
			</li>
		</ul>
		<div id="msgMenu" class="pulldownMenu" style="width:120px;">
			<ul>
				<li><a href="<vwb:Link context='dashboard' format='url'/>?func=teamNotice">团队邀请<span id="top-focus-state" class="msgCount${teamInvites} count">${teamInvites}</span></a></li>
		 		<li><a href="<vwb:Link context='dashboard' format='url'/>?func=personNotice">我的消息<span id="top-recommend-count" class="msgCount${personCount} count">${personCount}</span></a></li>
		 		<li><a href="<vwb:Link context='dashboard' format='url'/>?func=monitorNotice">我的关注<span id="top-focus-state" class="msgCount${monitorCount} count">${monitorCount}</span></a></li>
		 	</ul>
		</div>
		<div id="userMeMenu" class="pulldownMenu">
			<ul>
				<li><a href="<vwb:Link context="dashboard" format='url'/>?func=profile">个人资料</a></li>
				<li><a href="<vwb:Link context="dashboard" format='url'/>?func=preferences">个人偏好</a></li>
				<li><a href="<vwb:Link context="logout" format='url'/>" title="<fmt:message key='actions.logout.title'/>">注销</a>
				</li>
			</ul>
		</div>
		</vwb:UserCheck>
		<vwb:UserCheck status="anonymous">
			<li>
				<fmt:message key="fav.greet.anonymous" />
			</li>
			<li>
				<a href="<vwb:Link context="login" format='url'/>" class="action login" title="<fmt:message key='actions.login.title'/>"><fmt:message key="actions.login" /> </a>
			</li>
			<li>
				<a href="<vwb:Link context='regist' format='url' absolute='true'/>" target="_blank"><fmt:message key="actions.register" /> </a>
			</li>
		</ul>
	</vwb:UserCheck>
	</div>
	<div id="spaceNavMenu" class="pulldownMenu"></div>
</div>
	
<div id="masthead">
	<div id="banner" class="ui-wrap wrapperFull">
		<div id="banner-photo"><img src="/dct/jsp/aone/images/banner-leaf.jpg" /></div>
		<div id="banner-insetShadow"></div>
		<div id="banner-innerWrapper" class="wrapper1280">
			<c:choose>
				<c:when test="${teamType == 'dashboard'}">
					<h1><a href="<vwb:Link context='dashboard' format='url'/>">${currPageName}</a></h1>
				</c:when>
				<c:otherwise>
					<h1><a href="<vwb:Link format='url' context='teamHome' />">${currPageName}</a></h1>
				</c:otherwise>
			</c:choose>
			<c:if test="${teamType != 'dashboard' && teamAcl == 'admin'}">
			<a id="teamConfig" href="<vwb:Link context='configTeam' jsp='${teamCode}' format='url' />" title="管理团队">管理团队</a>
			<c:if test="${teamType != 'myspace'}">
				<a id="invite" title="邀请" href="<vwb:Link context='configTeam' page='${teamCode}' format='url'/>&func=adminInvitations">邀请</a>
			</c:if>
			</c:if>
		</div>
	</div>
	<c:if test="${teamType != 'dashboard' }">
		<div id="navigation" class="ui-wrap wrapperFull">
			<div id="navigation-innerWrapper" class="wrapper1280">
				<ul id="stdNav" class="switch">
					<li><a href="<vwb:Link context='notice' format='url'/>?func=teamNotice" title="点击查看所有星标文件">动态</a></li>
					<!-- 
					edit by lvly@2012-6-7-->
					<li><a href="<vwb:Link context='task' format='url'/>">任务</a></li> 
					<li><a href="<vwb:Link context='tag' format='url'/>" title="点击查看团队所有文件">文件<span id="resMore" class="icon13 icon-pulldownMore" title="更多"></span></a></li>
					<li><a href="<vwb:Link context='starmark' format='url'/>" title="点击查看所有星标文件"><span class="iconLynxTag icon-checkStar checked"></span></a></li>
				</ul>
				<ul id="opNav" class="switch">
					<li><a href="<vwb:Link format='url' context='task'/>?func=readyModifyTask" title="新建任务"><span class="iconLynx icon-page"></span>新建任务</a></li>
				</ul>
				<ul id="customNav">
					<c:forEach items="${navbarList}" var="nvItem">
						<li id="navbar-${nvItem.id}" >
							<a href="${nvItem.url}">${nvItem.title}</a>
							<a class="delete-nvitem-link lightDel" value="${nvItem.id}" title="移除快捷导航"></a>
						</li>
					</c:forEach>
				</ul>
				<ul id="customNavMore">
					<li class="moreSpace"><a>更多快捷</a></li>
				</ul>
				<div id="resourceMenu" class="pulldownMenu">
					<ul>
						<li><a href="<vwb:Link format='url' context='teamHome' />">常用</a></li>
						<li><a onclick="refreshAnchor('<vwb:Link format='url' context='teamHome' />#trace')">历史记录</a></li>
					</ul>
				</div>
				<div id="customNavMenu" class="pulldownMenu" style="width:560px;"></div>
			</div>
		</div>
	</c:if>
</div>


<script type="text/javascript">
$(document).ready(function(){
	spaceNavAdjust();
	customNavAdjust();
	localNav_bind();
	sendDecreaseScore();
	$('#stdNav #resMore').pulldownMenu({
		'menu': $('#resourceMenu'),
		'block': true,
		'anchor': $('#stdNav #resMore').parent()
	});
	
	$('li.msgNotification>a').pulldownMenu({ 'menu': $('#msgMenu') });
	$('li.userMe>a').pulldownMenu({ 'menu': $('#userMeMenu') });
	
	$("a.delete-nvitem-link").live('click', function(){
		 var $a = $(this);
		 var params = {"func":"delete","id":$a.attr("value")};
		 ajaxRequest(site.getURL("navbar",null),params,function(data){
			 $('#customNav #' + $a.parent().attr('id')).remove();
			 if (typeof($a.parent())!='undefined') {
				$a.parent().remove(); 
			 }
			 customNavAdjust('refresh');
		 });
	});
	
	function renewCookieTime(){
		var date = new Date();  
    	date.setTime(date.getTime() + ( 24 * 60 * 60 * 1000 ));  
	    $.cookie('decreaseScore', 'test', { path: '/', expires: date });  
	}
	
	function sendDecreaseScore(){
		if(!$.cookie('decreaseScore')){
			ajaxRequest(site.getURL('dashboard',null),"func=decreaseScore",function(data){
				if(data.status=='success'){
					renewCookieTime();
				}
			});
		}
	}
	
	
});
</script>
<script type="text/javascript">
$(document).ready(function(){
	/* GLOBAL SEARCH */
	var globalSearch = new SearchBox('globalSearch', '搜索', false, false, false);
	
	globalSearch.setPullDown('未找到相关资源', '搜索引擎错误', '350px');
	
	globalSearch.searchInput.focus(function(){
		globalSearch.container.addClass('loaded');
		globalSearch.container.parent().addClass('loaded');
	});
	globalSearch.searchInput.blur(function(){
		if (globalSearch.searchResultState==false) {
			globalSearch.container.removeClass('loaded');
		}
		globalSearch.container.parent().removeClass('loaded');
	});
	
	function renderCollectionResult(data){
		var html = '<li class="type">集合（'+data.size+'）</li>';
		for(var i=0;i<data.content.length;i++)
			html += '<li><a href="' + data["content"][i].url + '">' + data["content"][i].title + '</a></li>';
		return html;
	};
		
	function renderPageResult(data){
		var html = '<li class="type">页面（'+data.size+'）</li>';
		for(var i=0;i<data.content.length;i++)
			html += '<li><a href="' + data["content"][i].url + '">' + data["content"][i].title +
				'<span class="collection">' + data["content"][i].collectionName + '</span><br/>' +
				'<span class="author">' + data["content"][i].author + '</span><span class="time">'+data["content"][i].modifyTime+'</span></a></li>';
		return html;
	};
	
	function renderUserResult(data){
		var html = "";
		return html;
	};
	
	function renderTotalPart(count,keyword){
		var html = '<li class="all"><a href="<vwb:Link context="search" format="url"/>?func=searchResult&keyword='+keyword+'">显示全部（<span name="size">'+
			count+'</span>）</a></li>'
		return html;
	};
	
	globalSearch.appendResults = function(JSON) {
		this.searchResult.append('<ul></ul>').show();
		var holder = this.searchResult.children('ul');
		var keyword = $("input[name=search_input]").val();
		holder.append(renderTotalPart(JSON.count,keyword));
		if(JSON.pageResult!=null)
			holder.append(renderPageResult(JSON.pageResult));
		if(JSON.collectionResult!=null)
			holder.append(renderCollectionResult(JSON.collectionResult));
		if(JSON.userResult!=null)
			holder.append(renderUserResult(JSON.userResult));
	};
	
	globalSearch.doSearch = function(QUERY){
		var url = "<vwb:Link context='search' format='url'/>?func=searchResult&keyword=" + encodeURIComponent(QUERY);
		window.location.href = url;
		
		/*
		this.clearPullDown();
		$.ajax({
			url: "<vwb:Link context='search' format='url'/>?keyword=" + encodeURIComponent(QUERY),
			dataType: 'json',
			type: "POST",
			beforeSend: function(data) {
				globalSearch.MSG_searching();
			},
			success: function(data){
				globalSearch.presentResults(data);
			},
			error: function(){
				globalSearch.MSG_error();
			}
		});
		*/
	};
	
	globalSearch.resetSearch = function(){
		this.hideCover();
		this.clearPullDown();
	}
	
	/* tabindex */
	globalSearch.searchInput.attr('tabIndex', 1);
	setTimeout(function(){
		globalSearch.container.addClass('transition');
	}, 200);
});
</script>
