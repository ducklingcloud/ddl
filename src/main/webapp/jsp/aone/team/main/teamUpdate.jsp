<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<script type="text/javascript">
$(document).ready(function(){
	$('#editTool a.editTool').click(function(){
		$('#editTool ul#collection-op').toggle();
	});
	
	/* switch */
	function switchView(VIEW) {
		$('#feedSelector ul.filter > li').removeClass('chosen');
		$('.content-menu-body').fadeOut();
		var view = VIEW;
		
		switch (VIEW) {
			case 'byPerson':
				$('#newsfeed-person').fadeIn();
				break;
			case 'byFocus':
				$('#newsfeed-focus').fadeIn();
				break;
			case 'byTime':
			default:
				view = 'byTime';
				$('#newsfeed-time').fadeIn();
		}
		$('#feedSelector ul.filter a[view="' + view + '"]').parent().addClass('chosen');
	}
	
	$('#feedSelector ul.filter a').click(function(){
		switchView($(this).attr('view'));
	});
	/* initiate */
	var hash = (window.location.hash)?window.location.hash:'';
	if (hash!='') {
		switchView(hash.substring(1));
	}
	else {
		switchView();
	}
	
/* BY PERSON: focus to show certain person */
	var showAll = $('#newsfeed-person input[name="showAll"]');
	$('a#expandPersonSelector').click(function(){
		if ($(this).attr('folded')=='true') {
			$('#newsfeed-person .picker li').show();
			$(this).next().show();
			$(this).text('收起').attr('folded', 'false');
		}
		else {
			$('#newsfeed-person .picker li[class!="fullSize"]').hide();
			$(this).next().hide();
			$(this).text('选择').attr('folded', 'true');
			
		}
	});
	
	function manipulateItem(ITEM, OP) {
		var wrap = ITEM.parent().parent();
		var uid = ITEM.val();
		var op = OP;
		switch (op) {
			case 'SELECT':
				wrap.addClass('selected');
				ITEM.attr('checked', true);
				$('#newsfeed-person *[uid="' + uid + '"]').attr('toShow', true);
				break;
			case 'DESELECT':
				wrap.removeClass('selected');
				ITEM.attr('checked', false);
				$('#newsfeed-person *[uid="' + uid + '"]').attr('toShow', false);
				break;
			default:
				if (ITEM.attr('checked')=='checked' || ITEM.attr('checked')==true) {
					op = manipulateItem(ITEM, 'SELECT');
				}
				else {
					op = manipulateItem(ITEM, 'DESELECT');
				}
		}
		return op;
	}
	
	function presentDataAll() {
		showAll.attr('checked', 'checked').attr('disabled', true);
		$('#newsfeed-person h3, #newsfeed-person ul.a1-feed').slideDown();
		$('#newsfeed-person .picker input[name="nfp-picker"]').each(function(){
			manipulateItem($(this), 'DESELECT');
		});
	}
	function presentData(OBJ, OP) {
		//present info of marked users
		var uid = OBJ.val();
		showAll.attr('checked', false).attr('disabled', false);
		if (OP=='SELECT') {
			$('#newsfeed-person *[uid="' + uid + '"]').slideDown();
		}
		else {
			$('#newsfeed-person *[uid="' + uid + '"]').hide();
		}
	}
	
	function byPersonSelectPresent(ITEM, SINGLE, OP) {
		//hide others
		var single = (typeof(SINGLE)=='undefined') ? false : SINGLE;
		$('#newsfeed-person .picker input[name="nfp-picker"]').each(function(){
			if ($(this).val()!=ITEM.val()) {
				if (!$(this).attr('checked') || single) {
					manipulateItem($(this), 'DESELECT');
					presentData($(this), 'DESELECT');
				}
			}
		});
		
		var op = manipulateItem(ITEM, OP);
		
		var toShow = $('#newsfeed-person .picker input[name="nfp-picker"]:checked').size();
		if (toShow>0) {
			presentData(ITEM, op);
		}
		else {
			presentDataAll();
		}
	}
	
	showAll.click(function(){
		if ($(this).attr('disabled')==false) {
			presentDataAll();
		}
	});
	$("#newsfeed-person .picker input[name='nfp-picker']").live("change", function(key){
		byPersonSelectPresent($(this), !listenCtrl);
	});
	
	var listenCtrl = false;
	$(document).keydown(function(key){
		if (key.ctrlKey) { listenCtrl = true; }
		else { listenCtrl = false; }
	});
/* END OF BY-PERSON */

/* SEARCH MATCHED ITEMS WITHIN PAGE */
	var byPersonSearch = new SearchBox('byPersonSearch', '搜索人员', false, false, true, true);
	byPersonSearch.doSearch = function(QUERY) {
		var query = QUERY.toLowerCase();
		var findMatch = false;
		if (query!='') {
			if ($('a#expandPersonSelector').attr('folded')=='true')
				$('a#expandPersonSelector').click();
			$('#newsfeed-person .picker input[name="nfp-picker"]').each(function(){
				manipulateItem($(this), 'DESELECT');
				presentData($(this), 'DESELECT');
			});
			$('#newsfeed-person .picker li[class!="fullSize"] label').each(function(){
				if ($(this).attr('title').toLowerCase().indexOf(query)>=0
					|| $(this).text().toLowerCase().indexOf(query)>=0) {
					manipulateItem($(this).children('input[name="nfp-picker"]'), 'SELECT');
					presentData($(this).children('input[name="nfp-picker"]'), 'SELECT');
				}
			});
		}
	}
	byPersonSearch.resetSearch = function() { presentDataAll(); };

});
</script>

<div id="feedSelector" class="filterHolder">
	<ul class="filter">
		<li class="chosen"><a view="byTime" href="#byTime">按时间</a></li>
		<li><a view="byPerson" href="#byPerson">按人物</a></li>
		<li><a view="byFocus" href="#byFocus">我关注的</a></li>
	</ul>
</div>
	

<div id="newsfeed-time" class="content-menu-body" style="display:none">
	<c:forEach items="${recentByDay}" var="item">
		<h3>${item.date}</h3>
		<c:choose>
		<c:when test="${empty item.records }">
			<p class="a1-feed-none">最近没有发生更新</p>
		</c:when>
		<c:otherwise>
			<ul class="a1-feed">
			<c:forEach items="${item.records}" var="dItem">
				<li>
					<c:choose>
						<c:when test='${dItem.operation eq "CREATE"}'>
							<div class="a1-feed-leadingImg -newpage"></div>
						</c:when>
						<c:otherwise>
							<div class="a1-feed-leadingImg -modify"></div>
						</c:otherwise>
					</c:choose>
					
					<div class="a1-feed-body">
						<h4>
							<a href="<vwb:Link context='page' format='url' page='${dItem.pid}'/>" class="a1-feed-title" target="_blank">${dItem.pageTitle}</a>
							<c:choose>
								<c:when test='${dItem.operation eq "CREATE"}'>被创建</c:when>
								<c:otherwise>被修改</c:otherwise>
							</c:choose>
						</h4>
						<p class="a1-digest">
							<span class="author">${dItem.userName}(${dItem.uid})</span>
							<span class="time">${dItem.time}</span>
							<span class="version">第${dItem.version}版</span>
						</p>
					</div>
					
					<div class="ui-clear"></div>
				</li>
			</c:forEach>
			</ul>
			<div class="ui-clear"></div>
		</c:otherwise>
		</c:choose>
	</c:forEach>
</div>
<div id="newsfeed-person" class="content-menu-body" style="display:none">
	<div class="subHolder">
		<ul class="picker">
			<li class="fullSize">
				<label>
					<input type="checkbox" name="showAll" checked="checked" disabled="disabled" />全部
				</label>
				<a id="expandPersonSelector" class="iconLink expandY" folded="true">选择</a>
				<span class="ui-text-note" style="display:none;">按住Ctrl以选择多个</span>
				<div id="byPersonSearch" class="ui-RTCorner"></div>
			</li>
		<c:forEach items="${recentByMember}" var="item">
			<li style="display:none;"><label title="${item.uid}" >
				<input type="checkbox" name="nfp-picker" value="${item.uid}" />${item.userName}
			</label></li>
		</c:forEach>
		</ul>
		<div class="ui-clear"></div>
	</div>
	<c:forEach items="${recentByMember}" var="item">
		<h3 uid="${item.uid}">${item.userName}(${item.uid})</h3>
		<c:choose>
		<c:when test="${empty item.records }">
			<p class="a1-feed-none">最近没有进行更新</p>
		</c:when>
		<c:otherwise>
			<ul class="a1-feed" uid="${item.uid}">
			<c:forEach items="${item.records}" var="mItem">
				<li>
					<c:choose>
						<c:when test='${mItem.operation eq "CREATE"}'>
							<div class="a1-feed-leadingImg -newpage"></div>
						</c:when>
						<c:otherwise>
							<div class="a1-feed-leadingImg -modify"></div>
						</c:otherwise>
					</c:choose>
					
					<div class="a1-feed-body">
						<h4>
						<c:choose>
							<c:when test='${mItem.operation eq "CREATE"}'>创建页面</c:when>
							<c:otherwise>修改页面</c:otherwise>
						</c:choose>
						<a href="<vwb:Link context='page' format='url' page='${mItem.pid}'/>" class="a1-feed-title" target="_blank">${mItem.pageTitle}</a>
						</h4>
						<p class="a1-digest">
							<span class="day">${mItem.day}</span>
							<span class="time">${mItem.time}</span>
							<span class="version">第${mItem.version}版</span>
						</p>
					</div>
					
					<div class="ui-clear"></div>
				</li>
			</c:forEach>
			</ul>
			<div class="ui-clear"></div>
		</c:otherwise>
		</c:choose>
	</c:forEach>
</div>
<div id="newsfeed-focus" class="content-menu-body" style="display:none">
	<jsp:include page="/jsp/aone/team/main/displaySubs.jsp"></jsp:include>
</div>

