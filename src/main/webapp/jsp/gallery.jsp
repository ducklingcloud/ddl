<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<link rel="stylesheet" href="${contextPath}/scripts/galleria/themes/classic/galleria.classic.css">
<style>
.hideScroll{
overflow:hidden;
}
.imgBox {
	color:#d3d3d3;
	font:12px/1.4 "helvetica neue", arial, sans-serif;
	position:fixed;
	z-index:1042;
	top:0;
	left:0;
	width:100%;
	display:none;
}
.imgBox .img-name {
	height:35px;
	margin:0;
	padding:3px;
	background:#252525;
	box-shadow:1px 1px 3px #000;
}
.imgBox .img-name h3{
	font:14px/2em "微软雅黑";
	text-indent:4em;
	margin:0;
}
.img-name a {
	position:absolute;
	top:1em;
	right:0;
	display:inline-block;
	margin-right:1em;
	color:#999;
	text-decoration:none;
}
a.magnifier {
	right:8em;
	background:url(${contextPath}/scripts/galleria/themes/classic/img_preview_ic.png) no-repeat 0 -334px;
	padding: 0px 0 6px 22px;
}
a.img-close {
	background: url(${contextPath}/scripts/galleria/themes/classic/close.png) no-repeat 0 0;
	height:46px;
	width:35px;
	top:0.6em;
}
.imgBox .operation {
	position: relative;
	z-index:9999;
	top:-115px;
}
.imgBox .operation-but {
	background:black;
	border-radius:8px;
	opacity: .8;
	filter: alpha(opacity=80); 
	padding:5px 2em;
	float:left;
}
.imgBox .operation-but a,.imgBox .dropdown.share {
	margin-right:4em;
	display:inline-block;
	height:30px;
	width:30px;
	float:left;
	background:url(${contextPath}/scripts/galleria/themes/classic/img_preview_ic.png) no-repeat 0 0;
}
.imgBox a.rotate-left {background-position:1px 2px}
.imgBox a.rotate-right {background-position:3px -32px}
/* .imgBox a.share-file { background-position: 2px -180px;} */
.imgBox .dropdown.share {background-position: 2px -180px;}
.imgBox a.down-file {background-position: 2px -210px;}
.imgBox a.move-file {background-position: 2px -91px;}
.imgBox a.delete-file {background-position: 2px -241px;margin-right:0px;}
.imgBox a.edit-file {background-position: 2px -272px;}
/* .imgBox a.share-file:hover,  */
.imgBox .dropdown.share:hover,
	.imgBox a.down-file:hover, 
	.imgBox a.move-file:hover, 
	.imgBox a.delete-file:hover, 
	.imgBox a.edit-file:hover {
	background:url(${contextPath}/scripts/galleria/themes/classic/img_preview_ic.png) no-repeat 0 0;
}
.imgBox a.rotate-left:hover {background-position:-34px 2px}
.imgBox a.rotate-right:hover {background-position:-32px -32px}
/* .imgBox a.share-file:hover {background-position: -32px -180px;} */
.imgBox .dropdown.share:hover {background-position: -32px -180px;}
.imgBox a.down-file:hover {background-position: -32px -210px;}
.imgBox a.move-file:hover {background-position: -33px -91px;}
.imgBox a.delete-file:hover {background-position: -32px -241px;}
.imgBox a.edit-file:hover {background-position: -30px -272px;}
.imgBox a.see-detail {
	background:black;
	opacity: .8;
	filter: alpha(opacity=80);
	color:white;
	padding:0 20px;
	font:16px/39px "微软雅黑";
	text-decoration:none;
	border-radius:8px;
	float:left;
	display:inline-block;
	margin-right:10px;
}
.imgBox a.see-detail:hover {
	opacity: 1.0;
	filter: alpha(opacity=100);
}
.imgBox a.dropdown-toggle {background:none;}
.imgBox .galleria-container{ background:none; }
#galleria{ 
	margin:10px auto; 
	width:960px;
	height:620px;
}
.galleria-stage { bottom:120px; -moz-user-select: none;  -webkit-user-select: none;  -ms-user-select: none;  }
.galleria-thumbnails .galleria-image {height: 48px;width: 72px;background:none;border:1px solid #222;}
.galleria-thumbnails .active{ border:1px solid #e0e0e0;}
.galleria-thumbnails-container {height:50px;}
.galleria-thumb-nav-left {background-position: -495px 10px;height: 50px;}
.galleria-thumb-nav-right {background-position: -578px 10px;height: 50px;}

.commentBox {
	color: #CCC;
	top: 55px;
	float:right;
	text-align:left;
}
.commentBox h2,.commentBox p {
	display: inline-block;
	float: left;
}
.commentBox h2 {
	font-size:16px;
	font-weight:normal;
}

p.a1-comment-title {
	margin-left: 0px;
	line-height:3em;
}

ul#newComment,ul#a1-comment {
	margin-left: 0;
	padding: 0 7px 0 0;
	width:225px;
}
ul.a1-comment .replyCtrl {
	margin-top: 10px;
}
ul.a1-comment{
	clear: both;
	margin: 0px 15px 15px 0px;
	padding: 0 0 10px 0;
}
#a1-comment{
	overflow-y: auto;
	margin-bottom:10px;
}
#a1-comment li{
	border-bottom: 1px solid #ccc;
	margin-bottom:10px;
}
ul.a1-comment li {
	background: none;
	border: none;
	padding: 0;
	line-height: 2em;
	clear: both;
}
.a1-comment-time{color:#ccc;font-size:12px;}
ul.a1-comment .comment-name {
	float: left;
	font: 16px "微软雅黑";
	color: #CCC;
}
ul.a1-comment .comment-time {
	float: right;
	font-size: 14px;
	color: #000;
	font-weight: bold;
}
ul#a1-comment.comment-list {
	padding: 0 0 5px 0;
	font-size: 12px;
	margin: 0 0 15px 2px;
}
#comment.commentBox textarea.replyContent,.ui-wrap li#add-comment-box div.mentions-input-box,.ui-wrap li#add-comment-box div.mentions-input-box
	{
	width: 210px;
	min-width:210px;
	max-width:210px;
	min-height: 60px;
	border-radius:3px;
}
textarea.replyContent.standby {width: 185px;}
.ui-wrap a,.imgBox .operation-but a {transition:none;}	
.ui-text-note {color:#ccc;font-weight:bold;}
ul li.comment-display-box{padding-bottom:5px;}
span.diams{font-size:18px;line-height:2.7em;margin:0 3px;color:#0088AA;display:inline-block;float:left;}
.galleria-loader {  
    top: 45%;
    right:45%;
    height:35px;
    width:35px;    
}
.ui-spotLight-fail {
	font-weight:bold;
	width:195px;
	padding:0px;
	text-align:center;
	margin-top:10px;
}
#sidebar_button{
	background: #313131;
	width: 25px;
	font-size: 20px;
	cursor: pointer;
	float:right;
	border:none;
	outline:none;
	margin-top:1px;
	color:#D3D3D3;
}
#sidebar_button:hover{
	background: #393939;
}
.img-content{text-align:center;}
.img-content .img-left{display:inline-block;margin:0;padding:0;}
ul.dropdown-menu.lion li {cursor:pointer; color:#000; padding:5px;}
ul.dropdown-menu.lion li:hover {background:#eef;}
</style>
<div id="galleryBox" class="imgBox" style="display:none;">
  <div class="img-name">
    <h3> </h3>
    <div class="name-right">
	    <a href="javascript:void(0);" target="_blank" class="magnifier">查看原图</a>
   		<a href="javascript:void(0);" class="img-close"></a>
    </div>
  </div>
  <div class="img-content" >
  	<div class="img-left">
  		<div id="galleria">
   		</div>
	   		<div class="operation">
	   			<c:if test="${teamType != 'pan'}">
	   				<a href="javascript:void(0);"  target="_blank" class="see-detail" title="查看详情">查看详情</a>
	   			</c:if>
		    	<div class="operation-but">
		    		<a class="rotate-left" title="左旋转"> </a>
		    		<a class="rotate-right" title="右旋转"> </a>
		    		<c:if test="${teamType != 'pan'}">
		    			<div class="dropdown share" style="float:left; height:25px;">
			             <a class="dropdown-toggle" data-toggle="dropdown"  title="分享" href="#"></a>
			             <ul class="dropdown-menu lion" role="menu" aria-labelledby="dLabel">
			                 <li class="share-file" style="border-bottom:1px solid #ccc">团队内分享</li>
			                 <li class="picture-share-resource">公开链接</li>            
			             </ul>
			          	</div> 
			          	<a class="move-file" title="移动"> </a>
		    		</c:if>
		    		<a href="javascript:void(0);" class="down-file" title="下载图片"> </a>
		    		<a class="delete-file" title="删除"> </a>
		    	</div>
			</div>
		
  	</div>
	
	<c:if test="${teamType != 'pan'}">
		<input type="hidden" id="noPan" value="noPan">
		<div id="comment" class="commentBox">
			<h2>评论</h2>
			<span class='diams'>&bull;</span>
			<div>
				<c:set var="itemType" value="DFile" scope="request" />
				<jsp:include page="/jsp/aone/comment/displayComment.jsp"></jsp:include>
			</div>
		</div>
		<button id="sidebar_button">»</button>
	</c:if>
	 <div style="clear:both"></div>
  </div>

</div>
<input type="hidden" id="currentRid" value="" />


<script src="${contextPath}/scripts/galleria/galleria-1.3.3.min.js"></script>
<script src="${contextPath}/scripts/galleria/themes/classic/galleria.classic.min.js"></script>
<script src="${contextPath}/scripts/jquery.mousewheel.js"></script>
<script type="text/javascript">
<!--
var rotator ={
	rot : 0,
	right : function(img){
		this.rot +=90;
		if(this.rot === 360){
			this.rot = 0;	
		};
		this.rotate(img, this.rot);
	},
	left: function(img){
		this.rot -=90;
		if(this.rot === -90){
			this.rot = 270;	
		}
		this.rotate(img, this.rot);
	},
	rotate: function(img, rot){
		$(img).css({
			"-webkit-transform":"rotate(" + this.rot + "deg)",
			"transform":"rotate(" + this.rot + "deg)",
			"-moz-transform":"rotate(" + this.rot + "deg)",
			"filter":"progid:DXImageTransform.Microsoft.BasicImage(rotation="+ this.rot/90 +")",
			"-ms-filter":"progid:DXImageTransform.Microsoft.BasicImage(rotation="+ this.rot/90 +")"
		});
	},
	clear:function(){this.rot = 0;}
};

var gallerySlide = function(delta) {
	var gallery = Galleria.get(0);
    if (delta > 0 && gallery.getIndex()>0){
    	gallery.prev();
		
    }else if (delta < 0 && gallery.getIndex()<(gallery.getDataLength()-1)){
    	gallery.next();
    }
};
$('#galleryBox').mousewheel(function(event, delta) {
	if(isGalleryShow()){
		gallerySlide(delta);
	    return false; // prevent default
	}
});
var SIDERBAR_OPEN = true;
var COMMENT_LOADED = false;
//gallery custom
var THUMBS_SIZE = (function(container,box){
	var wh=$(window).height(), ww=$(window).width();
    var gh = wh - 60, gw = ww * 0.7;
    container.css("height",gh +"px");
    container.css("width", gw + "px");
    var len = gw/2 - 330 + "px";
    if($("#noPan")&&!$("#noPan").val()){
    	len = gw/2 - 150 + "px";
    }
    box.find(".operation").css("left", len);
    $("#sidebar_button").css("height", gh + 25 + "px");
    $("#a1-comment").css("max-height", gh-160);
	box.find(".img-close").click(function(){
		galleryHide();
  	});
	
	$("#sidebar_button").click(function(){
		if($(this).text() === "»"){
			sidebarHide();
		}else{
			sidebarOpen();
		}
	});
	
	$(document).keyup(function(KEY){
		if(isGalleryShow()){
			var gallery = Galleria.get(0);
			if (KEY.which=='27') {
				box.find(".img-close").click();
			}else if ((KEY.which=='37' || KEY.which=='38') && gallery.getIndex()>0){
			    gallery.prev();
			}else if ((KEY.which=='39' || KEY.which=='40') &&
						gallery.getIndex()<(gallery.getDataLength()-1)){
				gallery.next();
			}
			return false;
		}
	});
	return parseInt((container.width()-20)/79);
})($("#galleria"),$("#galleryBox"));
var THUMBS_SIZE_MID = parseInt((THUMBS_SIZE-1)/2);

function sidebarOpen(){
	$("#comment").show(300);
	$("#sidebar_button").text("»");
	SIDERBAR_OPEN = true;
	
	if(!COMMENT_LOADED){
		var res = Galleria.get(0).getData();
		loadComments(res.rid);
	}
}
function sidebarHide(){
	$("#comment").hide(300);
	$("#sidebar_button").text("«");
	SIDERBAR_OPEN = false;
}
function galleryHide(){
	$("#galleryBox").hide();
	$("#galleryOverlay").remove();
	$("body").removeClass("hideScroll");
	Galleria.get(0).destroy();
}
function isGalleryShow(){
	return $("#galleryOverlay").length>0;
}
//加载评论
function loadComments(rid){
	COMMENT_LOADED = true;
	$("#currentRid").val(rid);
    current_loader.loadComments();
    clearMention();
}
//-->
</script>