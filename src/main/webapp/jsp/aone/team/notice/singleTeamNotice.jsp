<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>


<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<script type="text/javascript">
$(document).ready(function(){
/* switch */
	function switchView(VIEW) {
		$('#feedSelector ul.filter > li').removeClass('chosen');
		$('.content-menu-body').fadeOut();
		var view = VIEW;
		
		switch (VIEW) {
			case 'byPerson':
				$('#singleFeed-team-person').fadeIn();
				break;
			case 'byTime':
			default:
				view = 'byTime';
				$('#singleFeed-team-time').fadeIn();
		}
		$('#feedSelector ul.filter a[view="' + view + '"]').parent().addClass('chosen');
	}
	$('#feedSelector ul.filter a').click(function(){
//		switchView($(this).attr('view'));
	});
	/* initiate */
	var hash = (window.location.hash)?window.location.hash:'';
	if (hash!='') {
//		switchView(hash.substring(1));
	}
	else {
//		switchView();
	}
	
/* BY PERSON: focus to show certain person */
	var showAll = $('#singleFeed-team-person input[name="showAll"]');
	$('a#expandPersonSelector').click(function(){
		if ($(this).attr('folded')=='true') {
			$('#singleFeed-team-person .picker li').show();
			$(this).next().show();
			$(this).text('收起').attr('folded', 'false');
		}
		else {
			$('#singleFeed-team-person .picker li[class!="fullSize"]').hide();
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
				$('#singleFeed-team-person *[uid="' + uid + '"]').attr('toShow', true);
				break;
			case 'DESELECT':
				wrap.removeClass('selected');
				ITEM.attr('checked', false);
				$('#singleFeed-team-person *[uid="' + uid + '"]').attr('toShow', false);
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
		$('#singleFeed-team-person h3, #newsfeed-person ul.a1-feed').slideDown();
		$('#singleFeed-team-person .picker input[name="nfp-picker"]').each(function(){
			manipulateItem($(this), 'DESELECT');
		});
	}
	function presentData(OBJ, OP) {
		//present info of marked users
		var uid = OBJ.val();
		showAll.attr('checked', false).attr('disabled', false);
		if (OP=='SELECT') {
			$('#singleFeed-team-person *[uid="' + uid + '"]').slideDown();
		}
		else {
			$('#singleFeed-team-person *[uid="' + uid + '"]').hide();
		}
	}
	
	function byPersonSelectPresent(ITEM, SINGLE, OP) {
		//hide others
		var single = (typeof(SINGLE)=='undefined') ? false : SINGLE;
		$('#singleFeed-team-person .picker input[name="nfp-picker"]').each(function(){
			if ($(this).val()!=ITEM.val()) {
				if (!$(this).attr('checked') || single) {
					manipulateItem($(this), 'DESELECT');
					presentData($(this), 'DESELECT');
				}
			}
		});
		
		var op = manipulateItem(ITEM, OP);
		
		var toShow = $('#singleFeed-team-person .picker input[name="nfp-picker"]:checked').size();
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
	$("#singleFeed-team-person .picker input[name='nfp-picker']").live("change", function(key){
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
			$('#singleFeed-team-person .picker input[name="nfp-picker"]').each(function(){
				manipulateItem($(this), 'DESELECT');
				presentData($(this), 'DESELECT');
			});
			$('#singleFeed-team-person .picker li[class!="fullSize"] label').each(function(){
				if ($(this).attr('title').toLowerCase().indexOf(query)>=0
					|| $(this).text().toLowerCase().indexOf(query)>=0) {
					manipulateItem($(this).children('input[name="nfp-picker"]'), 'SELECT');
					presentData($(this).children('input[name="nfp-picker"]'), 'SELECT');
				}
			});
		}
	}
	byPersonSearch.resetSearch = function() { presentDataAll(); };
	
	
/* hide and show notices */
	$('a.summary-count-link').click(function(){
		$(this).hide()
			.parents('.a1-feed-body').find('.hidden-display-notice').show();
		$(this).parents('.a1-feed-body').find('.hide-count-container').show();
	});
	$('a.hide-count-link').click(function(){
		$(this).parent().hide()
			.parents('.a1-feed-body').find('.hidden-display-notice').hide();
		$(this).parents('.a1-feed-body').find('a.summary-count-link').show(); 
	});

});
</script>

<!-- 
<div id="feedSelector" class="filterHolder">
	<ul class="filter">
		<li class="chosen"><a view="byTime" href="#byTime">按时间</a></li>
 		<li><a view="byPerson" href="#byPerson">按人物</a></li>
	</ul>
</div>
-->

<div id="singleFeed-team-time" class="content-menu-body" >
	<div class="innerWrapper">
	<c:choose>
		<c:when test="${empty teamNoticeList }">
			<p class="a1-feed-none">最近没有发生更新</p>
		</c:when>
		<c:otherwise>
			<c:forEach items="${teamNoticeList}" var="item">
				<h3>${item.date}</h3>
				<div class="singleTeam teamFeedList">
					<c:set var="compositeList" value="${item.compositeArray}" scope="request" />
					<jsp:include page="/jsp/aone/team/notice/compositeNoticeDisplay.jsp"></jsp:include>
				</div>
			</c:forEach>
		</c:otherwise>
	</c:choose>
	<div class="ui-clear"></div>
	</div>
</div>

<div id="singleFeed-team-person" class="content-menu-body" style="display:none">
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
		
		</ul>
		<div class="ui-clear"></div>
	</div>
	
</div>
