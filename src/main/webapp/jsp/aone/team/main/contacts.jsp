<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />

<link rel="stylesheet" href="../scripts/jquery-tablesorter/theme.default.css"> 
<style>
 #candidates { border-collapse:none; }
 #candidates tr td:first-child { border-left:5px solid transparent; }
 #candidates td.authOption { color:#999; }
 #candidates tr.inner-chosen td { background:#f9f8ff; border-bottom:1px solid #69c; color:#000 !important; }
 #candidates tr.inner-chosen td:first-child { border-left:5px solid #69c; }
 #content #content-title{background:white;}
</style>
<style>
 #contactTable tbody tr.normal-row td {
     background: #eef;
 }
 #contactTable tbody tr.alt-row td {
     background: #fff;
 }
</style>
<script type="text/javascript" src="../scripts/jquery-tablesorter/jquery.tablesorter.js"></script> 
<script type="text/javascript" src="../scripts/jquery-tablesorter/jquery.tablesorter.widgets.js"></script> 

<script type="text/javascript">
 $(document).ready(function(){
     /* $('table.dataTable tr:nth-child(even)').addClass('striped'); */
     
     /* SWITCH */
     $('#contactModeSelector ul.switch a').click(function(){
	 $('#contactModeSelector ul.switch li').removeClass('chosen');
	 $(this).parent().addClass('chosen');
	 $('.content-menu-body').fadeOut();
	 $('#contact-'+$(this).attr('name')+'.content-menu-body').fadeIn();
     });
     /* initiate */
     $('#contactModeSelector ul.switch li.chosen a').click();

     /* SEARCH MATCHED ITEMS WITHIN PAGE */
     var contactSearch = new SearchBox('contactSearch', '搜索成员和信息', false, true, true);
     contactSearch.doSearch = function(QUERY) {
	 $('#contact-listMode table.dataTable tbody > tr').each(function(){
	     contactSearch.findMatches(QUERY, $(this), 'a, span, td');
	 });
	 $('#contact-cardMode ul.cardList > li').each(function(){
	     contactSearch.findMatches(QUERY, $(this), 'a, span, td');
	 });
     };
     contactSearch.isMatch = function(OBJ) { OBJ.fadeIn(); };
     contactSearch.notMatch = function(OBJ) { OBJ.fadeOut(); };
     contactSearch.resetSearch = function() {
	 $('#contact-listMode table.dataTable tbody > tr').fadeIn();
	 $('#contact-cardMode ul.cardList > li').fadeIn();
     }; 
     
     /*
	$(".feed-person").live("click",function(){
	var obj = $(this);
	ajaxRequestWithSelector(obj.attr("url"),null,obj,afterFeedPerson);
	});
	
	function afterFeedPerson(data){
	alert("关注成功");
	afterFeedPerson.selector.parent().append("<span>已关注</span>");
	afterFeedPerson.selector.remove();
	};
      */
     
     $(".add-person-feed").live("click",function(){
	 var obj = $(this);
	 ajaxRequestWithSelector(obj.attr("url"),null,obj,afterAddPersonFeed);
     });
     
     function afterAddPersonFeed(data){
	 var html = '<a class="remove-person-feed" url=<vwb:Link context="feed" format="url"/>?func=removePersonFeed&uid='+data.uid+'>取消</a>';
	 afterAddPersonFeed.selector.parent().removeClass('toFollow').addClass('followed')
			   .html('已关注 · ' + html);
	 afterAddPersonFeed.selector.remove();
     };
     
     $(".remove-person-feed").live("click",function(){
	 var obj = $(this);
	 ajaxRequestWithSelector(obj.attr("url"),null,obj,afterRemovePersonFeed);
     });
     
     function afterRemovePersonFeed(data){
	 var html = '<a class="add-person-feed" url="<vwb:Link context="feed" format="url"/>?func=addPersonFeed&uid='+data.uid+'">关注Ta</a>';
	 afterRemovePersonFeed.selector.parent().removeClass('followed').addClass('toFollow').html(html);
	 afterRemovePersonFeed.selector.remove();
     };
     
     var exportBasicURL="<vwb:Link context='notice' format='url'/>?func=exportTeamContacts";
     
     $("#exportContacts").live("click",function(){
	 var t_ids=new Array();
	 var i=0;
	 $("#contact-listMode table tbody tr").each(function(){
	     /* t_ids[i]=$(this).children().eq(1).text();
		i=i+1; */
	     var tidstr=$(this).children().eq(0).children().eq(0).attr("href");
	     t_ids[i]=tidstr.substring(tidstr.lastIndexOf("/")+1,tidstr.length);
	     i=i+1;
	 });
	 //var tidStr=$("#contactModeSelector a[href]").attr("href");
	 //var tid=tidStr.substring(tidStr.indexOf("tid=")+4,tidStr.indexOf("&"));
	 
	 var contactList={"tids":t_ids};
	 alert(JSON.stringify(contactList));
	 window.location.href = exportBasicURL +"&contactList="+
                                encodeURIComponent(JSON.stringify(contactList));
     });
     
     //添加拼音排序规则
     $.tablesorter.addParser({
	 // set a unique id 
	 id : 'data',
	 is : function(s) {
	     // return false so this parser is not auto detected 
	     return false;
	 },
	 format : function(s, table, cell, cellIndex) {
	     var $cell = $(cell);
	     // I could have used $(cell).data(), then we get back an object which contains both 
	     // data-lastname & data-date; but I wanted to make this demo a bit more straight-forward 
	     // and easier to understand. 

	     // first column (zero-based index) has lastname data attribute 
	     if (cellIndex === 0) {
		 // returns lastname data-attribute, or cell text (s) if it doesn't exist 
		 return $cell.attr('pinyin') || s;
		 // third column has date data attribute 
	     }

	     // return cell text, just in case 
	     return s;
	 },
	 // set type, either numeric or text 
	 type : 'text'
     });

     //添加表格排序功能
     $("#contactTable").tablesorter({
	 sortList : [ [ 0, 0 ] ],
	 headers : {
	     0 : { sorter: 'data' }, 
	     4 : {
		 sorter : false
	     }
	 },
	 widgets: ["zebra"],
	 widgetOptions : { 
	     zebra : [ "normal-row", "alt-row" ] 
	 } 
     });
 });
</script>

<div id="contactModeSelector" class="filterHolder">
    <ul class="switch" style="float:left">
	<li class="chosen"><a name="listMode">列表模式</a></li>
	<li><a name="cardMode">名片模式</a></li>
    </ul>
    <div id="contactSearch" class="ui-RTCorner"></div>
    <div class="ui-RTCorner">
	<c:if test="${teamAcl == 'admin' && !isConferenceTeam}">
	    <a class="largeButton newUser"  href="<vwb:Link context='configTeam' page='${teamCode}' format='url'/>&func=adminInvitations">邀请和管理成员</a>
	</c:if>
    </div>
</div>

<div id="contact-cardMode" class="content-menu-body" style="display:none">
    <a href="" style="display:none;">修改我的个人信息</a>
    <ul class="cardList">
	<c:forEach items="${contacts}" var="item">
	    <li>
		<h4><a href="<vwb:Link context='user' page='${item.id}' format='url'/>">${item.name}</a>
		    <c:if test="${! empty adminUids }">
			<c:if test="${! empty adminUids[item.uid]}">
			    (管理员)
			</c:if>
		    </c:if>
		    <br/>
		    <span class="ui-text-note">${item.uid}</span>
		</h4>
		<table>
		    <tr><th>联系邮箱</th>
			<td>${item.email}</td>
		    </tr>
		    <tr><th>电话</th>
			<td>${item.telephone}</td>
		    </tr>
		    <tr><th>手机</th>
			<td>${item.mobile}</td>
		    </tr>
		</table>
	    </li>
	</c:forEach>
    </ul>
    <p class="contractCenter">
	<a class="largeButton" id="exportContacts">导出通讯录</a>
    </p>
</div>

<div id="contact-listMode" class="content-menu-body" style="display:none">
    <table class="dataTable merge lionDataTable" id="contactTable" style="margin-top:0;">
	<thead>
	    <tr>
		<td style="dtName">姓名</td>
		<td class="dtMail">联系邮箱</td>
		<td class="dtNums">电话</td>
		<td class="dtNums">手机</td>
		<td></td>
	    </tr>
	</thead>
	<tbody>
	    <c:forEach items="${contacts}" var="item" varStatus="status">
		<tr>
		    <td pinyin="${item.pinyin }"><a href="<vwb:Link context='user' page='${item.id}' format='url'/>" title="${item.uid }">${item.name}</a>
			<c:if test="${! empty adminUids }">
			    <c:if test="${! empty adminUids[item.uid]}">
				(管理员)
			    </c:if>
			</c:if>
		    </td>
		    <td>${item.email}</td>
		    <td>${item.telephone}</td>
		    <td>${item.mobile}</td>
		    <td>
			<c:choose>
			    <c:when test="${!isFeedList[status.index]}">
				<span class="toFollow"><a class="add-person-feed" url="<vwb:Link context='feed' format='url'/>?func=addPersonFeed&uid=${item.id}">关注Ta</a></span>
			    </c:when>
			    <c:otherwise>
				<span class="followed">已关注 · <a class="remove-person-feed" url="<vwb:Link context='feed' format='url'/>?func=removePersonFeed&uid=${item.id}">取消</a></span>
			    </c:otherwise>
			</c:choose>
		    </td>
		</tr>
	    </c:forEach>
	</tbody>
    </table>
    <p class="contractCenter">
	<a class="largeButton contractCenter" id="exportContacts">导出通讯录</a>
    </p>
</div>

