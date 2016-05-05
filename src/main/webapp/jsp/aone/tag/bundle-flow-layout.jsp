<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<fmt:setBundle basename="templates.default" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/wook_mark_style.css" type="text/css"/>
<style>
	.waterfallimage {width:260px; max-height:100%;}
	#inner_container li {width:260px; padding:0; box-shadow:0px 0px 1px #ccc;}
	#inner_container li a img{border:none;}
	#inner_container li p.image-info {font-size:13px; line-height:1.4em; border-top:1px solid #ccc; margin:0; padding:5px 8px;}
	#inner_container li p.image-info .title {display:block;}
	.bundleTitle {float:left; margin:5px 20px; font-size:16px; font-weight:bold;}
</style>
		<div>
		  <div id="resourceList-header" class="toolHolder light">
		  	<div class="bundleTitle">${bundle.title}</div>
			<div class="ui-RTCorner">
				<ul id="viewSwitch" class="switch" style="float:left;">
					<li><a id="toClassic" title="经典图模式"><span class="iconLynxTag icon-tradition"></span>切换至经典图</a></li>
				</ul>
			</div>
		  </div>
		  <ul id="inner_container" style="margin:30px auto;">
		    
		  </ul>
		  <div id="loader">
	        <div id="loaderCircle"></div>
	      </div>
		  <div class="ui-clear"></div>
		  <div id="message" style="margin:20px; text-align:center; color:#999;">
		  
		  </div>
		  <div id="maxScreenHeight" >
		</div>
		</div>
	<script type="text/html" id="item-template">
		<li style="float:left">
			<a href="{{= toUrl}}">
				<img class="waterfallimage" {{= imgAttribute}} src="{{= showUrl}}"/>
			</a>
			<p class="image-info">
				<span class="title">{{= title}}</span>
				<span class="author">{{= uploaderName}}</span>
				上传于
				<span class="time">{{= uploadTime}}</span>
			</p>
		</li>
	</script>
	<script type="text/javascript" src="${contextPath}/jsp/aone/js/jquery.wookmark.js"></script>
	<script type="text/javascript">
    var handler = null;
    var offsetPic = 0;
	var size = 8;
    var isLoading = false;
    var apiURL = "<vwb:Link context='bundleBase' format='url'/>/${bundle.bid}?func=loadMore"
    var toClassicUrl="";
    var needLoad=true;
    
    // Prepare layout options.
    var options = {
      autoResize: true, // This will auto-update the layout when the browser window is resized.
      container: $('#inner_container'), // Optional, used for some extra CSS styling
      offset: 12, // Optional, the distance between grid items
      itemWidth: 260 // Optional, the width of a grid item
    };
    
    /**
     * When scrolled all the way to the bottom, add more tiles.
     */
    function onScroll(event) {
      // Only check when we're not still waiting for data.
        // Check if we're within 100 pixels of the bottom edge of the broser window.
        var closeToBottom = ($(window).scrollTop() + $(window).height() > $(document).height() - 100);
        if(closeToBottom) {
          loadData();
      }
    }
    
    /**
     * Refreshes the layout.
     */
    function applyLayout() {
      // Clear our previous layout handler.
      if(handler) handler.wookmarkClear();
      
      // Create a new layout handler.
      handler = $('#inner_container li');
      handler.wookmark(options);
    };
    
    /**
     * Loads data from the API.
     */
    function loadData() {
	  if(isLoading||!needLoad){
		  return;
	  }
      isLoading = true;
      $('#loaderCircle').show();
      $("#message").html("正在载入......");
      $.ajax({
        url: apiURL,
        async:false,
        type:"POST",
        data: {'offset': offsetPic,'size':size}, // Page parameter to make sure we load new data
        success: onLoadData,
		statusCode:{
			450:function(){alert('会话已过期,请重新登录');},
			403:function(){alert('您没有权限进行该操作');}
		}
      });
    };
    function init(){
   
    	//初始化一直加载图片，加载到满屏幕为止
    	if(needLoad&&(($(document).height()-$(window).height())<150)){
    		loadData();
    		setTimeout(init,300);
    	}else{
    		setTimeout(applyLayout,500)
    	}
    }
   
    
    /**
     * Receives data from the API, creates HTML for images and updates the layout
     */
    function onLoadData(data) {
      $('#loaderCircle').hide();
     if(data==''||typeof(data.length)=='undefined'||data==null||data.length==0||typeof(data)=='undefined'){
			if(offsetPic==0){
				needLoad=false;
				$("#message").html("没有匹配结果")
			}else{
				needLoad==false;
				$("#message").html("没有更多结果了")
			}
		}else{
			if(offsetPic==0){
				toClassicUrl=data[0].toUrl;
				$("#toClassic").attr("href",toClassicUrl);
			}
			offsetPic+=data.length;
			$(data).each(function(index,item){
				if(item.pic!=null&&item.pic!=''){
					item.imgAttribute="width='260' height='"+Math.round(item.pic.height/item.pic.width*250)+"'";
				}else{
					item.imgAttribute='';
				}
			})
			$("#item-template").tmpl(data).appendTo("#inner_container");
			$("#message").html("更多结果")
			if(data.length<size){
				needLoad=false;
				$("#message").html("没有更多结果了")
			}
		}
      // Apply layout.
      applyLayout();
      isLoading = false;
    };
  
    $(document).ready(new function() {
      // Capture scroll event.
      $(document).bind('scroll', onScroll);
      // Load first data from the API.
      init();
      //把屏幕撑开，让他出现滚动条
      //$("#maxScreenHeight").css("min-height",200);
    });
  </script>