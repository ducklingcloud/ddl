<%@ page language="java" pageEncoding="UTF-8" %>
<%@ page import="net.duckling.ddl.common.*" %>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="net.duckling.ddl.util.UrlUtil" %>
<%@ taglib uri="/WEB-INF/tld/vwb.tld" prefix="vwb" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
pageContext.setAttribute("contextPath", request.getContextPath());
request.setAttribute("requestScheme",
    request.getHeader("x-forwarded-proto")==null
    ? request.getScheme()
    : request.getHeader("x-forwarded-proto"));
VWBContext context = VWBContext.createContext(request, "error");
String umtPath = context.getContainer().getProperty("duckling.umt.baseURL");
Object scheme = request.getAttribute("requestScheme");
if (scheme == null) scheme = request.getScheme();

/* request.setAttribute("escienceDomain", scheme + "://www.escience.cn"); */

if("https".equals(scheme)){
    umtPath = UrlUtil.changeSchemeToHttps(umtPath, 443);
}
request.setAttribute("umtPath", umtPath);

String currentReqUrl = context.getBaseURL() + request.getRequestURI();
currentReqUrl = UrlUtil.changeSchemeToHttps(currentReqUrl, request);
String loginUri=UrlUtil.changeSchemeToHttps(VWBContainerImpl.findContainer().getURL("login", null, "action=saveprofile", true),request)+"&sussessUrl="+URLEncoder.encode(currentReqUrl,"utf-8");
String ddlPath = loginUri;
//String ddlPath = context.getContainer().getProperty("duckling.ddl.baseURL");
request.setAttribute("ddlPath", ddlPath);

/* this seems not-in-use */
/* String umtReturnURI=umtPath+"/login?appname=dct&WebServerURL="+URLEncoder.encode(ddlPath, "utf-8");
   request.setAttribute("umtReturnURI", umtReturnURI); */

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

<fmt:setBundle basename="templates.default" />
<script type="text/javascript" src="${umtPath}/js/passport.js"></script>
<script type="text/javascript">
 $(document).ready(function() {
     initLogin = function() {
	 $.ajax({
	     url:"${contextPath}/home?func=getStatus",
	     type:'GET',
	     cache:false,
	     dataType:"json",
	     success:function(datass) {
		 if (datass["status"]) {
		     $("ul#login").html("").append("<li><a href='<vwb:Link context='switchTeam' absolute='true' format='url'/>'>进入团队</a></li>");
		     $("ul#login").append("<li class='userMe'><a href='javascript:void(0)' >"+datass["userEmail"]+"</a></li>");
		 } else if (datass["haveUmtId"]) {
		     var passport=new Passport({umtUrl:'${umtPath}'});
		     passport.checkAndLogin('${ddlPath}',{appname:'dct'});
		 }
	     },
	     error:function(){ }
	 });
     };
     initLogin();
     $('li.userMe>a').live("click", function(e) {
	 $("#userMeMenu").toggle();
	 e.stopPropagation();
     });
     $("*:not(li.userMe>a)").live("click",function(e) {
	 $("#userMeMenu").hide();
     });
 });
</script>

<link href="${contextPath}/dface/css/dface.banner.css" rel="stylesheet" type="text/css"/>
<style>
 .nav-collapse.collapse {position:relative;}
 .pulldownMenu#userMeMenu {display:none; position:absolute; top:42px; right:-30px; background:#fff; border:1px solid #ddd; box-shadow:2px 2px 2px #ddd; border-radius:3px; padding:0; z-index:30000;}
 .pulldownMenu#userMeMenu ul {margin:0; padding:0;}
 .pulldownMenu#userMeMenu ul li {float:none;border-bottom:1px dotted #ddd; padding:0; margin:0;}
 .pulldownMenu#userMeMenu ul li:last-child {border:none;}
 .pulldownMenu#userMeMenu ul li a {font-weight:bold; color:#000; font-size:10pt;padding:5px 1.5em ;display:block;}
 .pulldownMenu#userMeMenu ul li:hover {background:#69f;}
 .pulldownMenu#userMeMenu ul li:hover a {color:#fff;}
</style>

<script src="${contextPath}/dface/js/dface.banner.js" type="text/javascript" ></script>

<div class="navbar navbar-fixed-top">
    <div class="navbar-inner">
	<div class="container">
	    <div class="nav-collapse collapse">
		<div class="brand ddl-logo" id="macroNav">
		    <a title="团队文档库" id="logo"><b class="caret"></b></a>
		</div>
		<ul class="nav-right" id="login">
		    <li><a href="<vwb:Link context='switchTeam' absolute='true' format='url'/>">登录</a></li>
		    <li><a href="<vwb:Link context='regist' absolute='true' format='url'/>">注册</a></li>
		</ul>
		<div id="userMeMenu" class="pulldownMenu">
		    <ul>
			<li><a href="<vwb:Link context="dashboard" format='url'/>?func=profile">个人资料</a></li>
			<li><a href="<vwb:Link context="dashboard" format='url'/>?func=preferences">个人偏好</a></li>
			<!-- <li><a target="_blank" href="<vwb:Link context="toDhome" format='url'/>">学术主页</a></li> -->
			<li><a href="https://github.com/ducklingcloud/ddl">查看GitHub</a></li>
			<li><a href="<vwb:Link context="logout" format='url'/>"
			    title="注销">注销</a>
			</li>
		    </ul>
		</div>
		<ul class="nav">
		    <li class="active"><a id="ddlNav-index" href="${contextPath}/index.jsp">首页</a></li>
		    <li><a id="ddlNav-feature" href="${contextPath}/ddlFeature.jsp">功能特性</a></li>					
		    <li><a id="ddlNav-scene" class="current-nav" href="${contextPath}/ddlScene.jsp">应用场景</a></li>
                    <%-- <li><a id="ddlNav-qa" href="${contextPath}/ddlQA.jsp">常见问题</a></li>
		    <li><a id="ddlNav-updateLog" href="${contextPath}/ddlUpdateLog.jsp">更新日志</a></li>  --%>
		    <!-- <li><a id="ddlNav-help" href="http://support.ddl.escience.cn/cases/">用户案例</a></li> -->
		    <li><a id="ddlNav-help" href="https://github.com/ducklingcloud/ddl/wiki/DDL">项目Wiki</a></li>
		    <!-- <li><a id="ddlNav-news" href="${contextPath}/ddlNews.jsp">动态</a></li> -->
		    <li><a id="ddlNav-download" href="${contextPath}/download.jsp">客户端</a></li>
		    
		    <%-- 	
		    <li><a id="ddlNav-share" href="<vwb:Link context='shareFile' format='url'/>">快速分享</a></li>
		    --%>	
		</ul>
	    </div>
	</div>
    </div>
</div>
<div class="blankForFixed"></div>
