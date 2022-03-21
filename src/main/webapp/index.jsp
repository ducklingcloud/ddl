<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="net.duckling.ddl.common.*"%>
<%@ page import="net.duckling.ddl.constant.Constant"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
    <head>
        <%
	pageContext.setAttribute("contextPath", request.getContextPath());
	String basePath = getServletContext().getRealPath("/");
	String aoneVersion = Constant.getVersion(basePath);
	request.setAttribute("aoneVersion", aoneVersion);
	VWBContext context = VWBContext.createContext(request, "error");
	String umtPath = context.getContainer().getProperty("duckling.umt.baseURL");
	request.setAttribute("umtPath", umtPath);
	String ddlPath = context.getContainer().getBaseURL();
	request.setAttribute("ddlPath", ddlPath);
	String baseURL = null;
	if ((request.getServerPort() == 80)
	    || (request.getServerPort() == 443)) {
	    baseURL = request.getScheme() + "://" + request.getServerName()
	    + request.getContextPath();
        } else {
	    baseURL = request.getScheme() + "://" + request.getServerName()
	    + ":" + request.getServerPort()
	    + request.getContextPath();
        }
        %>
        
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <link href="${contextPath}/jsp/aone/css/index.css" rel="stylesheet" type="text/css" />
        <link href="${contextPath}/jsp/aone/css/index-nov2013.css?v=${aoneVersion}" rel="stylesheet" type="text/css" />
        <link href="${contextPath}/jsp/aone/css/index-aug2014.css?v=${aoneVersion}" rel="stylesheet" type="text/css" />
        <link href="${contextPath}/images/favicon.ico" rel="shortcut icon" type="image/x-icon" />
        <script type="text/javascript" src="${contextPath}/scripts/jquery/jquery-1.8.3.min.js"></script>
        <script type="text/javascript" src="${umtPath}/js/passport.js"></script>
        <script type="text/javascript">
	 $(document).ready(function() {
	     var i = 0;
	     var timer = setInterval(function() {
		 i = i+1;
		 $("#index-carousel-1").css({"background-position":"0 "+i+"px"});
	     }, 100);
	     $("#escienceMenu").mouseenter(function() {
		 $("#es-pullDownMenu").show();
	     });
	     $("#escienceMenu").mouseleave(function() {
		 $("#es-pullDownMenu").hide();
	     });
	     $(".active").removeClass("active");
	     $("#ddlNav-index").parent("li").addClass("active");

	     var url = "${contextPath}/home";
	     $.ajax({
		 url : url,
		 type : 'GET',
		 dataType : 'json',
		 success : function(data) {
		     if (typeof (data) == 'undefined'
			 || data == null) {
			 $(".ddl-active-info").remove();
		     } else {
			 var html = data.totalTeamNum + "个团队     "
				  + data.totalUserNum + "个成员";
			 $(".ddl-active-info").text(html);
		     }
		 },
		 statusCode : {
		     450 : function() {
			 alert('会话已过期,请重新登录');
		     },
		     403 : function() {
			 alert('您没有权限进行该操作');
		     }
		 }
	     });

	     var isMobile = {
		 Android : function() {
		     return navigator.userAgent.match(/Android/i) ? true
			  : false;
		 },
		 BlackBerry : function() {
		     return navigator.userAgent.match(/BlackBerry/i) ? true
			  : false;
		 },
		 iOS : function() {
		     return navigator.userAgent
				     .match(/iPhone|iPad|iPod/i) ? true
			  : false;
		 },
		 Windows : function() {
		     return navigator.userAgent.match(/IEMobile/i) ? true
			  : false;
		 },
		 any : function() {
		     return (isMobile.Android() || isMobile.iOS());
		 }
	     };
	     if (isMobile.any()) {
		 $("#mobileInfo").show();
		 if (isMobile.Android()) {
		     $(".andoidMobile").show();
		     $(".iphoneMobile").hide();
		 } else if (isMobile.iOS()) {
		     $(".andoidMobile").hide();
		     $(".iphoneMobile").show();
		 }
	     }
	     $(".closeMobileInfo").live('click', function() {
		 $("#mobileInfo").hide();
	     })
	     
	     /* carousel here */
	     $("#index_prev").click(function(){
		 clearInterval(bannerAuto);
		 var INDEX = parseInt($('div.ddl-detail:visible').attr("id").substring(15,16)) - 1;
		 if (INDEX < 1) {INDEX = 3;}
		 $("ul.bannerNav li").removeClass("current");
		 $("ul.bannerNav li#" + INDEX ).addClass("current");
		 $('div.ddl-detail:visible').fadeOut(1000);
		 $('div#index-carousel-'+INDEX).fadeIn(1000); 
		 setBanner();
	     })
	     $("#index_next").click(function(){
		 clearInterval(bannerAuto);
		 var INDEX = parseInt($('div.ddl-detail:visible').attr("id").substring(15,16)) + 1;
		 if (INDEX > 3) {INDEX = 1;}
		 $("ul.bannerNav li").removeClass("current");
		 $("ul.bannerNav li#" + INDEX ).addClass("current");
		 $('div.ddl-detail:visible').fadeOut(1000);
		 $('div#index-carousel-'+INDEX).fadeIn(1000); 
		 setBanner();
	     })
	     
	     /*banner switch*/
	     function switchBanner(INDEX) {
		 if ($('li.current').attr('id')!=INDEX) {
		     $('li.current').removeClass('current');
		     $('li[id="'+INDEX+'"]').addClass('current');
		     $('div.ddl-detail:visible').fadeOut(1000);
		     $('div#index-carousel-'+INDEX).fadeIn(1000);
		 }
	     }
	     
	     var banner = 1;
	     var bannerAuto;
	     function setBanner() {
		 bannerAuto = setInterval(function(){
		     banner = (banner>=3) ? 1 : (banner+1);
		     switchBanner(banner);
		 }, 10000);
	     }
	     setBanner();
	     
	     $('.bannerNav li').click(function(){
		 switchBanner($(this).attr('id'));
		 banner = parseInt($(this).attr('id'));
		 clearInterval(bannerAuto);
		 setBanner();
	     });
	 });
        </script>
        
        <style>
         .closeMobileInfo {
	     background: url(${contextPath}/jsp/aone/images/closeApp.png) 0 0
	     no-repeat;
	     display: inline-block;
	     width: 30px;
	     height: 30px;
	     position: absolute;
	     right: 5%;
	     top: 15%
         }

         #mobileAppInstall>a {
	     text-decoration: none;
         }

         #mobileAppInstall>a>.sb-l {
	     float: left;
	     width: 200px;
	     margin-left: 5%;
         }

         #mobileAppInstall>a>.sb-r {
	     float: left;
	     margin-left: 2%;
         }

         #mobileAppInstall>a>.sb-r>h2 {
	     margin: 0.3em 0;
	     color: #333;
	     font-family: Arial, "微软雅黑", "黑体";
	     font-size: 2em;
         }

         #mobileAppInstall>a>.sb-r>p {
	     margin: 0.2em 0;
	     color: #666;
	     font-size: 1.5em
         }

         #mobileAppInstall>a>.sb-r>p>span.btn {
	     border: 1px solid #aaa;
	     border-radius: 5px;
	     padding: 5px 1em;
	     background: #f5f5f5;
	     display: inline-block;
         }
        </style>
        
        <title>团队文档库，面向团队的文档协作与管理工具</title>
        <meta name="description"
	      content="团队文档库，是一款面向团队的文档协作与管理工具，用户可以免费创建团队，邀请用户，在团队内创建、上传、管理、组织、阅读文档，并在同事间相互推荐与共享，所有这一切都在云端完成，是中小团队理想的云中工作室。" />
    </head>
    
    <body class="texure">
	<jsp:include page="/ddlHeader.jsp"></jsp:include>
	<div id="index-carousel-1" class="ddl-detail ddl-intro index index-carousel">
	    <div class="ui-wrap">
		<div class="left-intro">
		    <h2><span>团队文档库 </span> 面向团队的文档协作与管理工具</h2>
		    <p>适合个人，机构，项目团队，中小企业，社团组织等使用。可用于文档资料共享<br />
                        和管理，即时沟通交流和移动办公。提升团队协作效率。</p>
		    <p class="ddl-active-info"></p>
		</div>
	    </div>
	</div>
	
	<div class="ddl-detail ddl-detail-second pink index-carousel" id="index-carousel-2" style="display:none">
	    <div class="ui-wrap">
		<div class="left-intro">
		    <h2>安全方便的云存储服务</h2>
		    <p>除了个人空间，还可以随心创建多个团队空间。您可以放心的将文档资料上传至云端，轻松<br />
                        管理和共享文档。注重文档协作，支持在线预览、协作编辑、版本管理、讨论和动态消息，<br />
                        让文档协作简单高效！</p>
		    <p class="ddl-active-info"></p>
		</div>
	    </div>
	</div>
	
	<div class="ddl-detail ddl-detail-second yellow index-carousel" id="index-carousel-3" style="display:none">
	    <div class="ui-wrap">
		<div class="left-intro">
		    <h2>高效的沟通和交流</h2>
		    <p>团队动态、消息通知帮助成员了解最新的工作进展；在线评论和分享通知，帮助团队<br />
                        头脑风暴，快速有效的沟通。</p>
		    <p class="ddl-active-info"></p>
		</div>
	    </div>
	</div>
	
	<%-- <div class="ddl-detail ddl-detail-second green index-carousel" id="index-carousel-4" style="display:none">
	     <div class="ui-wrap">
	     <div class="left-intro">
	     <h2>多客户端随时随地移动工作</h2>
	     <p>告别U盘吧！无论您身处何地，您都可以使用我们的网页端、PC客户端、手机端轻松<br />
             共享资源，快速进行协作，体验移动工作的美妙。</p>
	     <p class="ddl-active-info"></p>
	     </div>
	     </div>
	     </div> --%>
	
	<div class="ui-wrap" style="position:relative;">
	    <div class="right-login fixed">
		<p><a id="ddl-regist" class="login-regist" href="<%=umtPath%>/regist.jsp">创建账号</a></p>
		<p><a class="login-regist" href="<vwb:Link context='switchTeam' absolute='true' format='url'/>">直接登录</a></p>
                
		<!-- <p class="mobileLink">
		     <a class="iphone_icon" href="http://itunes.apple.com/cn/app/ke-yan-zai-xian/id495109931"
                     target="_blank" title="iOS版本"><span>iPhone</span></a> 
		     <a class="android_apk" href="http://www.escience.cn/apks/ddl-latest.apk" target="_blank"
                     title="Android版本"><span>Android</span></a>
		     </p> -->
	    </div>
	    <ul class="bannerNav">
		<li class="current" id="1">&nbsp;</li>
		<li id="2">&nbsp;</li>
		<li id="3">&nbsp;</li>
		<!-- <li id="4">&nbsp;</li> -->
	    </ul>
	</div>
	<div class="arrowBotton">
	    <a id="index_prev" style=""></a>
	    <a id="index_next" style=""></a>
	</div>
	
	<%-- <div class="downloadDiv">
	     <div class="ui-wrap">
	     <div class="leftText">
	     <h2>为你提供多种客户端，完美贴心的满足您的每一种需求，在云端极速同步~</h2>
	     <h3>----快速、自动同步文件，让您随时随地轻松查看，实现设备之间的无限连通！</h3>
	     <p><a class="btn btn-large btn-success" href="${contextPath}/download.jsp" target="_blank">
             查看更多客户端下载</a></p>
	     </div>
	     <div class="rightImg">
	     <p style="text-align: center;">
	     <img class="mobileCode" src="images/mobileRcode3.png" />
             <br />手机扫描下载客户端</p>
	     </div>
	     <div class="clear"></div>
	     </div>
	     </div> --%>
	
	<div class="featureDiv">
	    <div class="ui-wrap">
		<!-- <h1>用心做好产品</h1> -->
		<ul>
		    <li style="margin-left:10px">
                        <a href="${contextPath}/ddlFeature.jsp#featureDetail-1" target="_blank">
                            <span class="feature feature-1"></span>文档云存储<br>
                            <span class="sub_hint">提供方便快捷搜索，迅速定位所需文档。</span>
                        </a>
                    </li>
		    <li><a href="${contextPath}/ddlFeature.jsp#featureDetail-2" target="_blank">
                        <span class="feature feature-2"></span>专属团队空间<br>
                        <span class="sub_hint">可添加多个成员并给每个成员设置权限。</span>
                    </a></li>
		    <li><a href="${contextPath}/ddlFeature.jsp#featureDetail-3" target="_blank">
                        <span class="feature feature-3"></span>支持协作编辑<br>
                        <span class="sub_hint">提供完整的版本记录，不必担心文档丢失。</span>
                    </a></li>
		    <li><a href="${contextPath}/ddlFeature.jsp#featureDetail-5" target="_blank">
                        <span class="feature feature-4"></span>移动工作<br>
                        <span class="sub_hint">支持Windows   Mac   Linux Andriod iPhone</span>
                    </a></li>
		    <li><a href="${contextPath}/ddlFeature.jsp#featureDetail-4" target="_blank">
                        <span class="feature feature-5"></span>高效交流<br>
                        <span class="sub_hint">提供文档评论和@功能，减少邮件流转。</span>
                    </a></li>
		    <li style="margin-right:10px">
                        <a href="${contextPath}/ddlFeature.jsp#featureDetail-6" target="_blank">
                            <span class="feature feature-6"></span>多客户端同步<br>
                            <span class="sub_hint">让你告别U盘时代，多台电脑时刻同步文件。</span>
                        </a>
                    </li>
		</ul>
	    </div>
	</div>
	
	<%-- <div class="chooseus">
	     <div class="ui-wrap">
	     <h1>为什么选择我们？</h1>
	     <ul>
	     <li class="chooseus chooseus-1"  style="margin-left:10px">快速上传下载</li>
	     <li class="chooseus chooseus-2">放心品质保证</li>
	     <li class="chooseus chooseus-3">数据安全</li>
	     <li class="chooseus chooseus-4" style="margin-right:10px">贴心的服务</li>
	     </ul>
	     </div>
	     </div>
	
	     <div class="linkus">
	     <div class="ui-wrap">
	     <h1>把建议和想法告诉我们</h1>
	     <ul>
	     <li><a class="linkus linkus-1" style="margin-left:10px" href="tencent://message/?uin=2813954364&Uin=2813954364&Site=QQ%E5%92%A8%E8%AF%A2&Menu=yes" target="_blank">2813954364</a></li>
	     <li><a class="linkus linkus-2" href="http://e.weibo.com/dcloud" target="_blank">@科研在线</a></li>
	     <li><a class="linkus linkus-3" href="mailto:vlab@cnic.cn">vlab@cnic.cn</a></li>
	     <li><a class="linkus linkus-4">010-58812378</a></li>
	     <li><a class="linkus linkus-5" style="margin-right:10px" href="http://iask.cstnet.cn/?/home/explore/category-11" target="_blank">在线问答</a></li>
	     </ul>
	     </div>
	     </div> --%>
	
	<div id="footer">
	    <jsp:include page="/Version.jsp"></jsp:include>
	</div>

	<div id="mobileInfo"
		 style="display: none; position: absolute; font-size: 1.5em; left: 0; top: 0; width: 102% !important; z-index: 200; padding: 10px 0; background: -webkit-gradient(linear, left top, left bottom, from(whiteSmoke), to(#D2D2D2) ); border-bottom: 1px white solid">
	    <a class="closeMobileInfo" href="javascript:void(0)"></a>
	    <div id="mobileAppInstall">
		<a class="andoidMobile"
			  href="http://www.escience.cn/apks/ddl-latest.apk" target="_blank"
			  title="下载Android客户端APK文件">
		    <div class="sb-l">
			<img src="${contextPath}/jsp/aone/images/app-icon200.png"
				  alt="文档自由组织与管理" />
		    </div>
		    <div class="sb-r">
			<h2>团队文档库Android版</h2>
			<p>面向团队的文档协作与管理工具</p>
			<p>
			    <span class="btn">下载</span>
			</p>
		    </div>
		</a> <a class="iphoneMobile"
			       href="http://itunes.apple.com/cn/app/ke-yan-zai-xian/id495109931"
			       target="_blank" title="连接到App Store安装应用">
		    <div class="sb-l">
			<img src="${contextPath}/jsp/aone/images/app-icon200.png"
				  alt="文档自由组织与管理" />
		    </div>
		    <div class="sb-r">
			<h2>团队文档库iPhone版</h2>
			<p>面向团队的文档协作与管理工具</p>
			<p>
			    免费-在App Store <span class="btn">下载</span>
			</p>
		    </div>
		</a>
	    </div>
	</div>
    </body>

</html>
