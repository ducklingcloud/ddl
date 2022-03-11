<%@page import="java.net.URLEncoder"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="net.duckling.ddl.common.*"%>
<%@taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ page import="net.duckling.ddl.constant.Constant"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
<%
	pageContext.setAttribute("contextPath", request.getContextPath());
	String basePath = getServletContext().getRealPath("/");
	String aoneVersion = Constant.getVersion(basePath);
	request.setAttribute("aoneVersion", aoneVersion);
	VWBContext context = VWBContext.createContext(request, "error");
	String umtPath = context.getContainer().getProperty(
			"duckling.umt.baseURL");
	request.setAttribute("umtPath", umtPath);
	String ddlPath = context.getContainer().getBaseURL();
	String umtReturnURI = umtPath + "/login?appname=dct&WebServerURL="
			+ URLEncoder.encode(ddlPath, "utf-8");
	request.setAttribute("ddlPath", ddlPath);
	request.setAttribute("umtReturnURI", umtReturnURI);
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
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="${contextPath}/jsp/aone/css/index.css" rel="stylesheet"
	type="text/css" />
<link
	href="${contextPath}/jsp/aone/css/index-may2012.css?v=${aoneVersion}"
	rel="stylesheet" type="text/css" />
<link href="http://www.escience.cn/dface/images/favicon.ico" rel="shortcut icon"
	type="image/x-icon" />
<script type="text/javascript"
	src="${contextPath}/scripts/jquery/jquery-1.8.3.min.js"></script>
<script type="text/javascript" src="${umtPath}/js/passport.js"></script>
<script type="text/javascript">
	$(document)
			.ready(
					function() {
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
									$("#ddl-active-info").remove();
								} else {
									var html = data.totalTeamNum + "个团队     "
											+ data.totalUserNum + "个成员";
									$("#ddl-active-info").text(html);
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
<title>科研在线文档库，面向团队的文档协作与管理工具</title>
<meta name="description"
	content="科研在线文档库，是一款面向团队的文档协作与管理工具，用户可以免费创建团队，邀请用户，在团队内创建、上传、管理、组织、阅读文档，并在同事间相互推荐与共享，所有这一切都在云端完成，是中小团队理想的云中工作室。" />
</head>
<body class="texure">
	<div class="ui-wrap">
		<jsp:include page="/ddlHeader.jsp"></jsp:include>
		<div id="content" class="ddl-content">
			<div id="ddl-intro">
				<h1>面向团队的文档协作与管理工具</h1>
				<p>最适合科研团队、项目组、兴趣小组、创业团队、中小规模企业等工作用的文档协作与管理工具</p>
				<a id="ddl-regist"
					href="<vwb:Link context='regist' absolute='true' format='url'/>">免费注册</a>
				<span style="color: #666666; margin-left: 5px;">或</span> <a
					href="<vwb:Link context='switchTeam' absolute='true' format='url'/>">直接登录</a>
				<br />

				<p id="ddl-active-info"></p>
				<a class="iphone_icon"
					href="http://itunes.apple.com/cn/app/ke-yan-zai-xian/id495109931"
					target="_blank" title="连接到App Store安装应用"><span>iPhone</span></a> <a
					class="android_apk"
					href="http://www.escience.cn/apks/ddl-latest.apk" target="_blank"
					title="下载Android客户端APK文件"><span>Android</span></a>
			</div>

			<div id="ddl-detail">
				<div class="ddl-feature">
					<div class="ddl-feature-text">
						<h2>文档自由组织与管理</h2>
						<p>团队协作所需的全部文档均可放心的存入文档库中。文档采用标签模式进行多维度自由分类，由团队成员共同编写和整理，合作更高效。</p>
					</div>
					<div class="ddl-feature-img">
						<img src="${contextPath}/jsp/aone/images/feature-1.png"
							alt="文档自由组织与管理" />
					</div>
					<div class="ui-clear"></div>
				</div>

				<div class="ddl-feature">
					<div class="ddl-feature-img">
						<img src="${contextPath}/jsp/aone/images/feature-2.png"
							alt="文档自由组织与管理" />
					</div>
					<div class="ddl-feature-text">
						<h2>高效沟通与协作</h2>
						<p>团队中内容的每次变更，都会实时呈现，您不会错过任何重要信息。您还可以：</p>
						<ul>
							<li>关注某个成员，了解他的所有动态，包括他何时新建或修改了哪篇文档；</li>
							<li>关注某个文档，当其他成员修改它时，会为您推送提醒消息；</li>
							<li>将您认为有用的文档分享给同事，还可以附上几句留言。</li>
							<li>在页面下方留下评论，并可与同事在线展开讨论。</li>
						</ul>
						<p>更新、关注、分享、评论，让您轻松hold住全场。</p>
					</div>
					<div class="ui-clear"></div>
				</div>

				<div class="ddl-feature">
					<div class="ddl-feature-text">
						<h2>随时随地移动工作</h2>
						<p>合作，不再受异地的困扰。只要有网络的地方，您就可以与团队保持连接。另外，我们还提供iPhone及Android手机客户端，让您随时随地投入工作。</p>
						<div id="mobileAppInstall">
							<a id="iphone"
								href="http://itunes.apple.com/cn/app/ke-yan-zai-xian/id495109931"
								target="_blank" title="连接到App Store安装应用"><span>iPhone</span></a>
							<a id="androidApk"
								href="http://www.escience.cn/apks/ddl-latest.apk"
								target="_blank" title="下载Android客户端APK文件"><span>Android</span></a>
							<a href="http://ddl.escience.cn/home?func=getMobile"
								target="_blank"> <img style="margin-top: 5px; height: 120px"
								src="${contextPath}/images/mobileRcode.png" /></a>
						</div>
					</div>
					<div class="ddl-feature-img">
						<img src="${contextPath}/jsp/aone/images/feature-3.png"
							alt="文档自由组织与管理" />
					</div>
					<div class="ui-clear"></div>
				</div>
			</div>

		</div>

		<jsp:include page="/help/footer.jsp"></jsp:include>
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
						<h2>科研在线文档库Android版</h2>
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
						<h2>科研在线文档库iPhone版</h2>
						<p>面向团队的文档协作与管理工具</p>
						<p>
							免费-在App Store <span class="btn">下载</span>
						</p>
					</div>
				</a>
			</div>
		</div>
	</div>
</body>

</html>
