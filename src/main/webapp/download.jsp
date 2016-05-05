<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="net.duckling.ddl.common.*"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%
	pageContext.setAttribute("contextPath", request.getContextPath());
	VWBContext context = VWBContext.createContext(request, "error");
	String baseURL = null;
	if ((request.getServerPort() == 80)
			|| (request.getServerPort() == 443))
		baseURL = request.getScheme() + "://" + request.getServerName()
				+ request.getContextPath();
	else
		baseURL = request.getScheme() + "://" + request.getServerName()
				+ ":" + request.getServerPort()
				+ request.getContextPath();
%>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>团队文档库客户端下载</title>
	<link href="${contextPath}/images/favicon.ico" rel="shortcut icon" type="image/x-icon" />
	<link href="jsp/aone/css/index.css" rel="stylesheet"	type="text/css" />
	<link href="jsp/aone/css/index-nov2013.css?v=${aoneVersion}"	rel="stylesheet" type="text/css" />
	<link href="jsp/aone/css/download.css?v=${aoneVersion}"	rel="stylesheet" type="text/css" />
	<link href="http://www.escience.cn/dface/css/dface.banner.css" rel="stylesheet" type="text/css"/>
	<link href="http://www.escience.cn/dface/css/dface.simple.footer.css" rel="stylesheet" type="text/css"/>
	<style>
		.nav-collapse.collapse {position:relative;}
		.pulldownMenu#userMeMenu {display:none; position:absolute; top:42px; right:-30px; background:#fff; border:1px solid #ddd; box-shadow:2px 2px 2px #ddd; border-radius:3px; padding:0;}
		.pulldownMenu#userMeMenu ul {margin:0; padding:0;}
		.pulldownMenu#userMeMenu ul li {float:none;border-bottom:1px dotted #ddd; padding:5px 1.5em ;margin:0;}
		.pulldownMenu#userMeMenu ul li:last-child {border:none;}
		.pulldownMenu#userMeMenu ul li a {font-weight:bold; color:#000; font-size:10pt;}
		.pulldownMenu#userMeMenu ul li:hover {background:#69f;}
		.pulldownMenu#userMeMenu ul li:hover a {color:#fff;}
	</style>
	
	<script type="text/javascript" src="scripts/jquery/jquery-1.8.3.min.js"></script>
	<script src="http://www.escience.cn/dface/js/dface.banner.js" type="text/javascript" ></script>
	<script src="http://www.escience.cn/dface/js/dface.simple.footer.js" type="text/javascript" ></script>
</head>
<body>
	<jsp:include page="ddlHeader.jsp"></jsp:include>
	<div id="navbarspy" class="navbar navbar-static scrollspy">
        <div class="navbar-inner">
          <div class="container">
            <ul class="nav nav-pills">
              <li class="active"><a href="#pc">Windows客户端</a></li>
              <li><a href="#andro">Android版</a></li>
              <li><a href="#ios">iPhone版</a></li>
              <li><a href="#encrypt">加密客户端</a></li>
              <li><a href="#mac">Mac同步版</a></li>
              <li><a href="#linux">Linux同步版</a></li>
            </ul>
          </div>
        </div>
    </div>
    
	<div class="scrollspyContent container">
		<div class="downloadContent" id="pc">
			<div class="text">
				<h3>团队文档库Windows客户端</h3>
				<h4>快速、自动同步</h4>
				<ul>
					<li>版本：V1.1.1 Beta</li>
					<li>大小：12.9MB</li>
					<li>更新：2014-09-29</li>
					<li>适用系统：Win7/XP/Vista</li>	
				</ul>
				<a href="https://update.escience.cn/download/ddl_1.1.1_Beta_win32_setup.exe" class="btn btn-large btn-success">下载Windows客户端</a>
			</div>
			<div class="img">
				<img src="jsp/aone/images/ddl-pc.png">
			</div>
		</div>
       
	    <div class="downloadContent" id="andro">
	    	<div class="img">
				<img src="jsp/aone/images/ddl-and.png">
			</div>
	    	<div class="text">
				<h3>团队文档库Android版</h3>
				<h4>快速上传照片，随时查看文件</h4>
				<ul>
					<li>版本：V4.4.7 </li>
					<li>大小：4.8MB</li>
					<li>更新：2015-11-10</li>
					<li>适用系统：Android 2.3及更高版本</li>	
				</ul>
				<a class="btn btn-large btn-success" href="http://www.escience.cn/apks/ddl-latest.apk">下载Android客户端</a>
				<br><img class="mobileCode" src="images/mobileRcode3.png"/>
			</div>
         </div>
         
         <div class="downloadContent" id="ios">
	         <div class="text">
				<h3>团队文档库iPhone版</h3>
				<h4>随时查看文件，获取团队动态</h4>
				<div class="halfText">
					<ul>
						<li>版本：V4.4.9</li>
						<li>大小：14.3MB</li>
						<li>更新：2015-12-11</li>
						<li>适用系统： iOS6.0及以上版本</li>	
					</ul>
					<a class="btn btn-large btn-success" href="http://itunes.apple.com/cn/app/ke-yan-zai-xian/id495109931">从App Store下载</a>
					<br><img class="mobileCode" src="images/mobileRcode3.png"/>
				</div>
				<div class="halfText">
					<ul>
						<li>版本：V4.4.9</li>
						<li>大小：14.3MB</li>
						<li>更新：2015-12-11</li>
						<li>适用系统： iOS6.0及以上版本</li>	
					</ul>
					<a class="btn btn-large btn-primary" href="https://update.escience.cn/download/ddl.jsp">下载机构发行版</a><font style="font-size:14px;color:#999;margin-left:-20px">（科学院用户）</font>
					<br><img class="mobileCode" src="images/mobileRcode2.png"/>
				</div>
			</div>
			<div class="img">
				<img src="jsp/aone/images/ddl-ios.png">
			</div>
	    </div>
	    
	    <div class="downloadContent" id="encrypt">
        	<div class="img">
				<img src="jsp/aone/images/ddl-encrypt.png">
			</div>
        	<div class="text" style="width:55%; margin-left:5%">
				<h3>团队文档库加密客户端</h3>
				<h4>提供更多一层保护，查看文件更安全</h4>
				<ul>
					<li>版本：V0.3.0 </li>
					<li>大小：9.2MB</li>
					<li>更新：2014-07-25</li>
					<li>适用系统：Win7/XP/Vista/2003</li>	
				</ul>
				<a class="btn btn-large btn-success" href="http://update.escience.cn/downloadVersion/21">下载加密客户端</a>
			</div>
	    </div>
	    
	    <div class="downloadContent" id="mac">
			<div class="text">
				<h3>团队文档库Mac同步版</h3>
				<h4>快速、自动同步</h4>
				<ul>
					<li>版本：V1.1.1 Beta</li>
					<li>大小：36.7MB</li>
					<li>更新：2014-09-22</li>
					<li>适用系统：Mac OS X 10.6+</li>	
				</ul>
				<a href="https://update.escience.cn/download/ddl-drive-macosx-1.1.1.beta.dmg" class="btn btn-large btn-success">下载Mac同步版</a>
			</div>
			<div class="img">
				<img src="jsp/aone/images/ddl-mac.png">
			</div>
		</div>
		
		<div class="downloadContent" id="linux">
        	<div class="img">
				<img src="jsp/aone/images/ddl-linux.png">
			</div>
        	<div class="text" style="width:55%; margin-left:5%">
				<h3>团队文档库Linux同步版</h3>
				<h4>独一无二的Linux同步客户端，方便同步数据</h4>
				<ul>
					<li>版本：V1.1.2 </li>
					<li>大小：34MB</li>
					<li>更新：2014-11-06</li>
					<li>适用系统：ubuntu12.04以上</li>	
				</ul>
				<div class="clear"></div>
				<a class="btn btn-large btn-success" href="http://update.escience.cn/download/ddl-drive.1.1.2.beta.linux-i386.tar.gz">下载Linux同步版(32位)</a>
				<a class="btn btn-large btn-primary" href="http://update.escience.cn/download/ddl-drive.1.1.2.beta.linux-x86_64.tar.gz">下载Linux同步版(64位)</a>
				
			</div>
	    </div>
    </div>
</body>

<script type="text/javascript">
	$(document).ready(function(){
		$(".navbar.navbar-fixed-top").addClass("navbar-inverse").addClass("static");
		$(".blankForFixed").css({"height":"0"});
		
		$(".nav-collapse ul.nav li.active").removeClass("active");
		
		$("#ddlNav-download").parent("li").addClass("active");
		/*lvlongyun write as scrollspy by himself begin*/	
		var ids=[];
		$('#navbarspy ul li a').each(function(i,n){
			var id=$(n).attr('href').replace('#','');
			ids.push(id);
		});
		for(var i=0;i<ids.length;i++){
			var id=ids[i];
			ids[i]={'id':id,'start':$('#'+id).offset().top-76};
			if(i<ids.length-1){
				ids[i].end=$('#'+ids[i+1]).offset().top;
			}else{
				ids[i].end=1000000;
			}
		}

		function judiceWhereAndActive(scroll){
			if($(document).scrollTop() >=$(document).height()-$(window).height()){
				toActive(ids[ids.length-1]);
				return;
			}
			$(ids).each(function(i,n){
				if(scroll>=n.start&&scroll<= n.end){
					toActive(n);
				}
			});
		}
		function toActive(n){
			$('#navbarspy ul li').removeClass('active');
			$('a[href="#'+n.id+'"]').parent().addClass('active');
		}
		judiceWhereAndActive($('body').scrollTop());

		$(document).scroll(function(){
			var scrollTopValueChrome = $('body').scrollTop();
			var scrollTopValueFirfox = document.documentElement.scrollTop;
			var scrollTopValue = ((scrollTopValueChrome > scrollTopValueFirfox) ? scrollTopValueChrome : scrollTopValueFirfox);
			if (scrollTopValue > 40) {
				$('#navbarspy').addClass("navbar-fixed-top");
				$(".blankForFixed").css({"height":"75px"});
			}
			else {
				$('#navbarspy').removeClass("navbar-fixed-top");
				$(".blankForFixed").css({"height":"0"});
			}
			
			judiceWhereAndActive(scrollTopValue);
		});
		/*lvlongyun write as scrollspy by himself end*/
		
	});
</script>
</html>