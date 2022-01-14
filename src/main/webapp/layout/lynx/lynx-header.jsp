<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
Object scheme = request.getAttribute("requestScheme") == null ? request.getScheme() : request.getAttribute("requestScheme");
request.setAttribute("contextPath", request.getContextPath());
%>
<link href="${contextPath}/dface/css/dface.banner.css" rel="stylesheet" type="text/css"/>
<script src="${contextPath}/dface/js/dface.banner.js" type="text/javascript" ></script>
<div id="macroNav" class="ui-wrap wrapperFull">
    <a id="logo" title="团队文档库"><b class="caret"></b></a>
    
    <div id="macro-innerWrapper" class="wrapper1280">
	<vwb:TeamPreferences/>
	<c:set var="teamType" value="${current}" scope="request"></c:set>
	<ul id="staticNav" class="spaceNav">
	    <li <c:if test="${teamType eq 'dashboard'}">class="current"</c:if>>
		<a href="<vwb:Link format='url' context='dashboard'/>">首页</a>
	    </li>
	    
	    <li <c:if test="${teamType eq 'pan'}">class="current"</c:if>>
		<a href="<vwb:Link format='url' context='panList' page=''/>">个人空间<sup class="beta">同步版Beta</sup></a>
	    </li>
	    
	    <li <c:if test="${teamType eq 'myspace'}">class="current"</c:if>>
		<a href="<vwb:Link format='url' context='switchTeam' page=''/>?func=person">个人空间</a>
	    </li>
	</ul>
	<ul id="spaceNav" class="sortableList spaceNav" style="display:none;">
	    <c:forEach items="${myTeamList}" var="item">
	 	<li id="team_${item.id }" <c:if test="${teamType eq item.name}">class="current"</c:if>><a href='<vwb:Link context="switchTeam" page="" format="url"/>?func=jump&team=${item.name}' itemid="${item.id }"><c:out value="${item.displayName}"></c:out></a></li>
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
		<li><a target="_blank" href="<vwb:Link context="toDhome" format='url'/>">学术主页</a></li>
		<li><a href="http://support.ddl.escience.cn/" target="_blank">帮助</a></li>
		<li><a href="<vwb:Link context="logout" format='url'/>"
		    title="注销">注销</a>
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
	<div id="banner-photo"></div>
	<!-- <div id="banner-insetShadow"></div> -->
	<div id="banner-innerWrapper" class="wrapper1280">
	    <c:choose>
		<c:when test="${teamType == 'dashboard'}">
		    <h1><a title="${fn:escapeXml(currPageName)}" href="<vwb:Link context='dashboard' format='url'/>">${fn:escapeXml(currPageName)}</a></h1>
		</c:when>
		<c:when test="${teamType == 'pan'}">
		    <h1><a title="${fn:escapeXml(panName)}" href="<vwb:Link context='panList' format='url'/>">${fn:escapeXml(panName)}<sup class="title-beta">同步版Beta</sup></a></h1>
		</c:when>
		<c:otherwise>
		    <h1><a title="${fn:escapeXml(currPageName)}" href='<vwb:Link context="switchTeam" page="" format="url"/>?func=jump&team=${teamCode}'>
			<c:choose>
			    <c:when test="${fn:length(currPageName) > 16}">
				${fn:escapeXml(fn:substring(currPageName, 0, 16))}...
			    </c:when>
			    <c:otherwise>
				${fn:escapeXml(currPageName)}
			    </c:otherwise>
			</c:choose>
		    </a></h1>
		</c:otherwise>
	    </c:choose>
	    <c:if test="${teamType != 'dashboard' && teamAcl == 'admin' && !isConferenceTeam}">
		<a id="teamConfig" href="<vwb:Link context='configTeam' jsp='${teamCode}' format='url' />" title="管理团队"><span style="display:inline-block; margin:30px 0 0 5px;">管理</span></a>
		<c:if test="${teamType != 'myspace' && !isConferenceTeam}">
		    <a id="invite" title="邀请" href="<vwb:Link context='configTeam' jsp='${teamCode}' format='url'/>&func=adminInvitations"><span style="display:inline-block; margin:30px 0 0 5px;">邀请</span></a>
		</c:if>
	    </c:if>
	    <c:if test="${teamType != 'dashboard' }">
		<div class="progressBar">
		    <p><strong title="<fmt:formatNumber value="${teamSize.used}" pattern="#,#00"/> 字节" style="cursor:pointer;">${teamSize.usedDisplay}</strong> / ${teamSize.totalDisplay}</p>
		    <div class="progress  progress-striped <c:choose><c:when test='${teamSize.percent> 0.9 }'> progress-danger</c:when><c:otherwise> progress-success</c:otherwise></c:choose>">
	 		<div class="bar" style="width: ${teamSize.percentDisplay};"></div>
		    </div>
		</div>
		<c:if test="${teamAcl == 'admin' || teamType == 'pan'}">
		    <c:choose>
			<c:when test="${teamSize.application }">
			    <c:set var="aColor" value="red;" scope="request"/>
			</c:when>
			<c:otherwise>
			    <c:set var="aColor" value="#ccc;"/>
			</c:otherwise>
			
		    </c:choose>
		    <c:choose>
			<c:when test="${teamType == 'pan' }">
			    <a class="enlargeSpace" style="color: ${aColor}" href="<vwb:Link context="panApplication" format='url'/>">扩容</a>
			</c:when>
			<c:otherwise>
			    <a class="enlargeSpace" style="color: ${aColor}" href="<vwb:Link context="teamHome" format='url'/>/spaceApplicetion">扩容</a>
			</c:otherwise>
		    </c:choose>
		    
		</c:if>
		<ul class="subSwitch">
		    <c:if test="${teamType!='pan' }">
			<li <c:if test="${pageType eq 'list'}">class="current"</c:if>><a class="docs" href="<vwb:Link context='files' format='url'/>" title="点击查看团队所有文件">文件 ${teamResourceAmount }</a></li>
			<li <c:if test="${pageType eq 'singleTeam'}">class="current"</c:if>><a class="fresh" href="<vwb:Link context='notice' format='url'/>?func=teamNotice" title="点击查看团队所有动态">动态</a></li>
			<c:choose>
			    <c:when test="${teamType !='myspace'}">
				<li <c:if test="${pageType eq 'contacts'}">class="current"</c:if> style="">
				    <a class="member" href="<vwb:Link context='notice' format='url'/>?func=contacts" title="点击查看所有团队成员">成员 ${teamMemberAmount }</a>
				</li>
			    </c:when>
			    <c:otherwise>
				<li style="visibility:hidden;"><a href="#" class="member" title="">成员</a></li>
			    </c:otherwise>
			</c:choose>
		    </c:if>
		</ul>
	    </c:if>
	</div>
    </div>
    
</div>
<div id="search-title" class="ui-wrap wrapper1280" style="display:none;">
    <h2 style="margin:-40px 0 10px 13px;">文档库搜索</h2>
</div>

<script type="text/javascript">
 window.teamLiHeight=0;
 window.teamUlHeight=0;
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
     function logoPosition(){
	 winWidth = document.documentElement.clientWidth; 
	 logoWidth = $("#macroNav #logo").outerWidth();
	 leftlogo = (winWidth - $("#macro-innerWrapper").outerWidth()) / 2;
	 $("#macro-innerWrapper").css({"padding-left":"0"});
	 if(leftlogo > 0){
	     /* $(".beta-tip").css({"left":leftlogo}); */
	     if(leftlogo > logoWidth){
		 $("#macro-innerWrapper #staticNav").css({"margin-left":"0","padding-left":0});
	     }
	     else if(0 < leftlogo < logoWidth){
		 $("#macro-innerWrapper #staticNav").css({"padding-left":logoWidth - leftlogo,"margin-left":leftlogo - logoWidth});
	     }
	 }
	 else if (leftlogo == 0){
	     $("#macro-innerWrapper #staticNav").css({"margin-left":0,"padding-left":0});
	     /* $(".beta-tip").css({"left":0}); */
	 }
     }
     window.onresize=logoPosition;
     
     //resort modify by lvly@2012/11/2 重新排序Team

     $("#spaceNavSub").sortable({
	 change:function(event, ui){
	     result=[]
	     $('#spaceNavSub li').each(function(index,item){
		 //如果是选中的项目的位置，则会没有itemid
		 var itemid=$(item).children().attr('itemid');
		 //所以从选中的li里面选
		 var selectedItemId=ui.item.children().attr('itemid');
		 if(!itemid){
		     result.push(selectedItemId);
		 }else if(itemid!=selectedItemId){
		     //去重，直接写else当前选项会重复
		     result.push(itemid);
		 }
	     });
	     $('#spaceNav').html("")
	     for(var i=0;i<result.length;i++){
		 //unkown，为啥子会酱紫？ style哪里来的
		 var obj=$('#team_'+result[i]).clone().attr('style','');
		 obj.appendTo('#spaceNav');
	     }
	     spaceNavAdjust();
	     $('#spaceNavMore .moreSpace').children('a').pulldownMenu({ 'menu': $('#spaceNavMenu'), 'eventName': '.lynx.spaceNavAdjust'});
	 },
	 update : function(event,ui){
	     var result={'resortedIds':[]};
	     $('#spaceNavSub li a').each(function(index,item){
		 result['resortedIds'].push($(item).attr('itemid'));
	     });
	     ajaxRequest("<vwb:Link context='dashboard' format='url'/>?func=resortTeam",result,function(data){
	     });
	 }
     });
     //end
     
     $(".closeBetaTip").click(function(){
	 $(this).parents(".beta-tip").hide();
	 var date = new Date();
	 date.setDate(date.getDate()+30);
	 document.cookie="beta-tip-cookie=true;path=/;expires="+date.toGMTString();
     });
     (function(){
	 var cookie = getCookie("beta-tip-cookie");
	 if(cookie==""){
	     $("#beta-tip-div").show();
	 }
     })();
     function getCookie(c_name)
     {
	 if (document.cookie.length>0)
	 {
	     c_start=document.cookie.indexOf(c_name + "=");
	     if (c_start!=-1)
	     { 
	         c_start=c_start + c_name.length+1 ;
	         c_end=document.cookie.indexOf(";",c_start);
	         if (c_end==-1) c_end=document.cookie.length;
	         return unescape(document.cookie.substring(c_start,c_end));
	     } 
	 }
	 return "";
     }
 });
</script>
<script type="text/javascript">
 function addResource(url){
     try{
	 var tagIds=''
	 $('#tagQuery a').each(function(index,item){
	     tagIds+=$(item).attr("value")+",";
	 });
	 if(tagIds.indexOf(',')>-1){
	     tagIds=tagIds.substring(0,tagIds.lastIndexOf(','));
	 }
	 var param=''
	 if(tagIds!=''){
	     param="&tagIds="+tagIds;
	 }
	 window.location.href=url+param;
     }catch(e){
	 window.location.href=url;
     }
 }
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
	 var html = '<li class="all"><a href="<vwb:Link context="teamSearch" format="url"/>?keyword='+keyword+'">显示全部（<span name="size">'+
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
	 var curURL = window.location.href;
	 var url = "<vwb:Link context='teamSearch' format='url'/>?keyword=" + encodeURIComponent(QUERY);
	 if(curURL.indexOf("dashboard")>=0 || curURL.indexOf("system/search")>=0||curURL.indexOf("/pan/")>=0){
	     url = "<vwb:Link context='globalSearch' format='url'/>?func=searchResult&keyword="+encodeURIComponent(QUERY);
	 }
	 
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
<script type="text/javascript">
 $(document).ready(function(){
     function init(){
	 var url = window.location.href;
	 if(url.indexOf("system")>0 && url.indexOf("search")>0){
	     $("#search-title").show();
	 }
     }
     init();
     setTimeout(function(){
	 $("ul#indexList").css({"padding-left":"30px","padding-right":"10px","top":"18px"});
	 $("a.header-block.nav").css({"font-weight":"normal"});
     },200);
 });
</script>
