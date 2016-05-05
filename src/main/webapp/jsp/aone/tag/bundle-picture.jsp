<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<style>
textarea.replyContent { width:455px; max-width:455px; min-width:455px; height:3em; }
textarea.replyContent.standby { height:1.2em; color:#999; }
.replyCtrl { display:none; width:35em; }
a.reply-to-this { margin-left:1em; font-size:10pt; cursor:pointer; /* used in js, do not change name */ }
li#hide-detail-button, li#show-detail-button { cursor:pointer; }
</style>
	<div class="prevNext">
		<input type="button" id="pageprev" class="prevOne" value=""/>
		<div id="pageInfo"><p><span id="up">${up }</span>/<span id="down">${down }</span></p></div>
		<input type="button" id="pagenext" class="nextOne" value=""/>
		<div class="ui-clear"></div>
	</div>
	<input type="hidden" value="${uid }" id="currentUid"/>
	<c:forEach items="${image }" var="img" varStatus="status">
		<c:if test="${!empty resource}">
			<c:choose>
					<c:when test="${img.rid eq resource.rid }">
						<div resource_id="${img.rid }" class="showThis imgHolder">
					</c:when>
					<c:otherwise>
						<div resource_id="${img.rid }" style="display: none" class="imgHolder">
					</c:otherwise>
			</c:choose>
			<div id="content-title">
				<h1 style="width:500px;float:left;">图片： ${img.title}</h1>
				<ul id="viewSwitch" class="switch" style="float:right; background:#f7f7f7; margin-top:8px; margin-right:130px;">
					<li><a href="<vwb:Link context='bundle' page='${bundle.itemId }' format='url'/>" title="缩略图模式"><span class="iconLynxTag icon-waterfall"></span>切换至缩略图</a></li>
				</ul>
				<div class="ui-clear"></div>
			</div>
			<div id="notice" style="color:#ccc; text-align:center; text-align:center; line-height:30px; height:30px;" ></div>
			<div class="content-major">
			<c:choose>
					<c:when test="${img.rid eq resource.rid }">
					<c:set var="itemId" value="${img.itemId}" scope="request"/>
					<div item_id="${img.itemId}" class="resImage">
						<a class="content-image">
							<img  src="<vwb:Link context='download' page='${img.itemId }' format='url'/>?type=doc" />
						</a>
					</div>
					</c:when>
					<c:otherwise>
					<div item_id="${img.itemId}" class="resImage">
						<a class="content-image"></a>
					</div>
					</c:otherwise>
			</c:choose>
			</div>
		</c:if>
		<c:if test="${empty resource}">
				<c:choose>
					<c:when test="${status.index eq 0 }">
						<div resource_id="${img.rid }" class="showThis imgHolder">
					</c:when>
					<c:otherwise>
						<div resource_id="${img.rid }" style="display: none" class="imgHolder">
					</c:otherwise>
				</c:choose>
				<div id="content-title">
					<h1><span>图片：</span>${img.title}</h1>
				</div>
				<div class="content-major">
				<c:choose>
					<c:when test="${status.index eq 0 }">
					<div item_id="${img.itemId}" class="resImage">
						<a class="content-image">
							<img  src="<vwb:Link context='download' page='${img.itemId }' format='url'/>?type=doc" />
						</a>
					</div>
					</c:when>
					<c:otherwise>
					<div item_id="${img.itemId}" class="resImage">
						<a class="content-image"></a>
					</div>
					</c:otherwise>
				</c:choose>
				</div>
		</c:if>
			<div class="imageDownload">
				<a class="largeButton extra" 
					href="<vwb:Link context='download' page='${img.itemId }' format='url'/>?type=doc&imageType=original">下载
					<span class="ui-text-note">(${pageInfo[status.index].shortFileSize })</span></a>
			</div>
			<h3>${file }</h3>
			<div class="imageDetail">
				<div id="version">
					${img.creatorName} | 修改于 <fmt:formatDate value="${img.lastEditTime}" type="both" dateStyle="medium" />
					 | <a href="#">版本：${img.lastVersion }</a>|<a target="_blank" href="<vwb:Link context='originalImage' page='${img.itemId }' format='url'/>?version=${img.lastVersion}">查看原图</a>
				</div>
				<div class="ui-clear"></div>
				<%-- <c:set var="tagExist" value="${pageInfo[status.index].tagExist } " scope="request"/> --%>
				<c:set var="tagMap" value="${tagMapList[status.index] }" scope="request"/>
				<c:set var="rid" value="${img.rid }" scope="request"/>
				<c:set var="starmark" value="${pageInfo[status.index].starmark }" scope="request"/>
				<jsp:include page="bundle-add-tag.jsp"/>
			</div>
		<div class="ui-clear"></div>
	</div>
	</c:forEach>
	<div id="comment">
		${img.itemId}
		
		<c:set var="itemType" value="DFile" scope="request"/>
		<jsp:include page="/jsp/aone/comment/displayComment.jsp"></jsp:include>
	</div>
	<div class="ui-clear bedrock"></div>

<script type="text/javascript">
	function scrollNav(){
		var lis=$("#bundle-navList li");
		var scrollValue=0;
		for(var i=0;i<(parseInt($("#up").html())-1);i++){
			scrollValue+=$(lis[i]).height();
		}
		$('#bundle-navList').scrollTop(scrollValue);
	}
	
	//load_picture_comment(${(empty resource)?image[0].itemId:resource.itemId});
</script>	

<script type="text/javascript">
$(document).ready(function(){
	//add by lvly@2012-11-18,加上前后按钮
	$("#pageprev").click(prev).bind('contextmenu', function(event){
		event.preventDefault();
	});
	$("#pagenext").click(next).bind('contextmenu', function(event){
		event.preventDefault();
	});
	var imgurl = ${imgurls};
	//var img = { "567":'/dct/sldflsd.', "234": 'dct/sdflj' };
	

	
	function loadSingleImage(imgIndex) {
		if ($('div[resource_id="' + imgIndex + '"]').find('.content-image img').length<1) {
			$('div[resource_id="' + imgIndex + '"]').find('.content-image').append('<img src="' + imgurl[imgIndex] + '" />');
		}
		var itemId = $('div[resource_id="' + imgIndex + '"] .resImage').attr('item_id');
		load_picture_comment(itemId);
	}
	
	load_picture_comment = function (itemId){
		current_loader._default = function CommentDefaultSetting(){
			this.url = site.getURL("comment",null);
			this.params = {"itemId":itemId,"itemType":"DFile"};
		}
		current_loader.loadComments();
		resetPage();
	}
	
	/*function loadOtherImages() {
		for (i in imgurl) {
			loadSingleImage(i);
		}
	}
	var loadDelay = setTimeout(loadOtherImages, 3000);
	$('.showThis img').load(function(){
		clearTimeout(loadDelay);
		loadOtherImages();
	});*/
	/*cursor changed when hover image*/
	var isLeft = true;
	$(".bundleContent")
	.css('position', 'relative')
	.mousemove(function(e){
		if(e.pageX <= $(this).offset().left + $(this).width()/2){
			$('.resImage').removeClass("cursorNext").addClass("cursorPre")
				.attr('title','点击查看上一张');
		}
		else{
			$('.resImage').removeClass("cursorPre").addClass("cursorNext")
		    	.attr('title','点击查看下一张');
		}
	});
	
	$(".resImage").click(nextOrPrev)
	.bind('contextmenu', function(event){
		event.preventDefault();
	});
	function next(){
		var $thisBody=$(".imgHolder:visible");
		var $next = $thisBody.next('.imgHolder');
		if($next.length < 1){
			$('div#notice').text("已经是最后一张了。");
			return;
		}
		else {
			$('div#notice').text('');
			loadSingleImage($next.attr('resource_id'));
			$thisBody.hide();
			navActiveDown();
			$next.show();
		}
		$('#up').html(parseInt($('#up').html())+1);
		scrollNav();
	}
	function prev(){
		var $thisBody=$(".imgHolder:visible");
		var $prev = $thisBody.prev('.imgHolder');
		if ($prev.length < 1){
			$('div#notice').text("已经是第一张了。");
			return;
		}
		else {
			$('div#notice').text('');
			loadSingleImage($prev.attr('resource_id'));
			$thisBody.hide();
			navActiveUp();
			$prev.show(); 
		}
		$('#up').html(parseInt($('#up').html())-1);
		scrollNav();
	}
	function nextOrPrev(e){
		e.preventDefault();
		if(e.pageX <= $(this).parent().offset().left + $(this).parent().width()/2){
			prev();
		}
		else{
			next();
		}
	}
	/**设置边栏选中状态往下一格*/
	function navActiveDown(){
		$("#bundle-navList li").each(function(index, item){
			if($(item).attr("class")=='active'){
				$(item).removeClass();
				$(item).next().attr("class",'active');
				return false;
			}
		});
	}
	/**设置边栏选中状态往上一格*/
	function navActiveUp(){
		$("#bundle-navList li").each(function(index, item){
			if($(item).attr("class")=='active'){
				$(item).removeClass();
				$(item).prev().attr("class",'active');
				return false;
			}
		});
	}
	/*focus the very picture*/
	$("ul#bundle-navList li a").click(function(e){
		e.preventDefault();
		$(".imgHolder").hide();
		var li= $(this).parent();
		$("#up").html(li.attr("index"));
		$("ul#bundle-navList li").each(function(index,item){
			$(item).removeClass("active");
		});
		li.attr("class","active");
		var resID = $(this).parent().attr('resource_id');
		var $relateDiv = $("div[resource_id=" + resID + ']');
		loadSingleImage(resID);
		$relateDiv.show();
	});
	//setScroll();
	scrollNav();
});
</script>