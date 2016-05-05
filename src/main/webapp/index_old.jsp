<%@ page language="java" pageEncoding="utf-8"%>
<%@ page import="net.duckling.ddl.common.*"%>
<%@taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">	
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
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="keywords" content="云计算服务,科研管理,协同办公,移动互联网,科研在线" />
	<title>科研在线，让知识在团队中流淌</title>
	<link href="${contextPath}/jsp/aone/css/index.css" rel="stylesheet" type="text/css"/>
	<link href="${contextPath}/jsp/aone/css/index-nov2011.css" rel="stylesheet" type="text/css"/>
	<script type="text/javascript" src="${contextPath}/scripts/jquery/jquery-1.8.3.min.js"></script>
	<script type="text/javascript">
$(document).ready(function(){
	function switchHero(INDEX) {
		if ($('li.current').attr('index')!=INDEX) {
			$('li.current').removeClass('current');
			$('li[index="'+INDEX+'"]').addClass('current');
			$('div.hero-container').fadeOut();
			$('div#hero-'+INDEX).fadeIn();
		}
	}
	
	var hero = 1;
	var heroAuto;
	function setHero() {
		heroAuto = setInterval(function(){
			hero = (hero==4) ? 1 : (hero+1);
			switchHero(hero);
		}, 5000);
	}
	setHero();
	
	$('.heroNav li').click(function(){
		switchHero($(this).attr('index'));
		hero = parseInt($(this).attr('index'));
		clearInterval(heroAuto);
		//setTimeout(setHero, 10000);
		setHero();
	});
	
/*
	$('.hero-container').mouseenter(function(){
		window.clearInterval(heroAuto);
		$(this).mouseout(function(){
			setHero();
			$(this).unbind('mouseout');
		});
	});
*/	
	
	if ($.browser.msie && (parseInt($.browser.version, 10) < 9)) {
		$('#webpageCaptureLink').addClass('msie');
	}
	
	$('.newFunc').click(function(){
		var range = $(window).height()/6;
		var top = $(this).offset().top - range;
		var difference = $(window).scrollTop() - top;
		
		if (difference > range || difference < -range)
			$('html, body').animate({ scrollTop: top }, 700);
	})
	
	/* browser Alert */
	var browserAlert = $('#browserAlert');
	if ($.browser.msie) {
		if (parseInt($.browser.version, 10) < 8) {
			browserAlert.show();
			$('body').addClass('browserAlert');
		}
	}
	
});
</script>
</head>

<body>
	<div class="ui-wrap">
		<div id="browserAlert" class="fullWidth">
			<p>系统不完全支持您所使用的浏览器：内容显示和部分功能可能无法正常运行。建议您使用以下更好的浏览器：</p>
			<p class="browsers">
				<a class="firefox" href="http://firefox.com.cn/" target="_blank">Firefox</a>
				<a class="msie" href="http://windows.microsoft.com/zh-CN/internet-explorer/products/ie/home" target="_blank">IE8</a>
				<a class="safari" href="http://www.apple.com.cn/safari/" target="_blank">Safari</a>
				<a class="chrome" href="http://www.google.com/chrome" target="_blank">Chrome</a>
				<a class="opera" href="http://www.opera.com/" target="_blank">Opera</a>
			</p>
		</div>
		
		<div id="aoneBanner" class="std">
			<a id="ROL" href="${contextPath}/index.jsp"><span>科研在线</span></a>
			<div class="ui-RTCorner" id="userCtrl">
				<a class="largeButton green" href="<vwb:Link context='regist' absolute='true' format='url'/>">注册</a>
				<a class="largeButton" href="<vwb:Link context='switchTeam' absolute='true' format='url'/>">登录</a>
			</div>
			<ul id="nav">
				<li class="current"><a href="${contextPath}/index.jsp">概述</a></li>
				<li><a href="${contextPath}/help/introduction.jsp">科研在线是什么？</a></li>
				<li><a href="${contextPath}/help/tutorial.jsp">使用指南</a></li>
				<li><a href="${contextPath}/help/history.jsp">更新记录</a></li>
				<li><a href="<vwb:Link context='shareFile' format='url'/>">快速分享</a></li>
			</ul>
		</div>
		
		<div id="nov2011" class="content">
			<div class="text-fixed">
				<h1><img src="${contextPath}/jsp/aone/images/portal-Nov2011-slogan.png" alt="科研在线，让知识在团队中流淌" /></h1>
				<p class="intro">建立团队工作环境，与同伴同步分享观点、文件和图表
					<br/>整理资料和数据，积累团队的知识库和资料库
					<br/>组织项目，分享和协同编辑文档，追踪版本变更
					<br/>云端应用，移动应用访问资源
				</p>
				<a class="rolRegister" href="<vwb:Link context='regist' absolute='true' format='url'/>"><span>注册科研在线</span></a>
				<p class="plus">使用<a href="#ducklingPassport"><span class="duckling">Duckling</span>通行证</a></p>
				<p class="plus">2012年3月31日，科研在线更新发布至DCT 6.1.0<br/><a href="${contextPath}/help/history.jsp">了解详情&gt;&gt;</a></p>
			</div>
			<div class="hero">
				<div id="hero-1" class="hero-container">
					<h1><img src="${contextPath}/jsp/aone/images/portal-Nov2011-hero-1.png" alt="简洁实用的内容发布" /></h1>
					<h3>简洁实用的内容发布</h3>
					<p class="intro">使用简洁的面向内容的编辑器，支持附件和多文件上传
						<br/>快速有效地发布内容、分享知识和信息
					</p>
				</div>
				<div id="hero-2" class="hero-container" style="display:none">
					<h1><img src="${contextPath}/jsp/aone/images/portal-Nov2011-hero-2.png" alt="便捷高效的协作沟通" /></h1>
					<h3>便捷高效的协作沟通</h3>
					<p class="intro">实施查看团队成员更新的内容和上传的文件
						<br/>通过评论、分享和关注实现高效的沟通
					</p>
				</div>
				<div id="hero-3" class="hero-container" style="display:none">
					<h1><img src="${contextPath}/jsp/aone/images/portal-Nov2011-hero-3.png" alt="参与多个团队，密切协作" /></h1>
					<h3>参与多个团队，密切协作</h3>
					<p class="intro">为不同的项目加入多个团队，分别运作
						<br/>团队之间信息独立，保障安全；个人空间集中查看各团队信息
					</p>
				</div>
				<div id="hero-4" class="hero-container" style="display:none">
					<h1><img src="${contextPath}/jsp/aone/images/portal-Nov2011-hero-4.png" alt="云端服务，自由，轻盈" /></h1>
					<h3>云端服务，自由，轻盈</h3>
					<p class="intro">随时随地查看资料、参与沟通，更可以使用Android手机客户端
						<br/>仅仅需要关注内容和工作，不再分心于系统和平台的维护
					</p>
				</div>
				<ul class="heroNav">
					<li class="current" index="1"></li>
					<li index="2"></li>
					<li index="3"></li>
					<li index="4"></li>
				</ul>
			</div>
			<div class="ui-clear"></div>
		</div>
		
		<div id="aspect">
			<div id="webpageCapture" class="std stdRounded">
				<div class="newFunc"></div>
				
				<h3>科研在线网页收藏工具</h3>
				<img class="capitalFig left" src="${contextPath}/dataCollect/images/tutor-select-small.jpg" />
				<p>网页收藏工具可以将您浏览的精彩网页内容快速保存到科研在线。</p>
				<p>在您浏览页面时，打开网页收藏工具，收藏工具将自动为您选择网页标题和正文。您也可以手工选取需要的段落，将选中的内容保存到您的团队中，与同伴共享。</p>
				
				<p>&nbsp;</p>
				<p><strong>安装：</strong> 将以下链接拖动到浏览器书签栏。<span class="ui-text-note">或用右键点击将其保存到收藏夹</span></p>
				<a id="webpageCaptureLink" href="javascript:var%20baseURL='<%=baseURL%>';(function(){if(document.body&&!document.xmlVersion){var%20s=document.createElement('script');s.setAttribute('type','text/javascript');s.setAttribute('src','<%=baseURL%>/dataCollect/dataCollect.js');s.setAttribute('charset','utf-8');document.body.appendChild(s);}})();"
					title="将此链接拖动到书签栏，或保存到收藏夹"
				>+科研在线</a>
				<div class="ui-clear"></div>
			</div>
		
			<div id="textIntro" class="std stdRounded">
				<h3>面向科研团队的在线协同工作环境</h3>
				<p>科研在线提供协同编辑、信息发布、文件文档上传和整理、文献共享、知识积累、沟通交流等在线服务。</p>
				<p>采用简洁而实用的内容发布、组织和分享工具，强大的协同编辑和沟通机制；“云端”服务方式使您可以随时随地查找资料、参与协作，同时也免去了维护系统的麻烦。
					<a class="ui-text-note" href="${contextPath}/help/introduction.jsp">了解详情&gt;&gt;</a>
				</p>
			</div>
			<div id="mobile" class="std stdRounded">
				<h3>移动客户端</h3>
				<p>科研在线提供支持Android系统的移动客户端，使您随时随地查看资料、与团队协作。</p>
				<div id="mobileAppInstall">
					<a id="androidApk" href="http://www.escience.cn/apks/ddl-latest.apk" target="_blank" title="下载Android客户端APK文件"><span>Android</span></a>
					<a id="iphone" href="http://itunes.apple.com/cn/app/ke-yan-zai-xian/id495109931" target="_blank" title="连接到App Store安装应用"><span>iPhone</span></a>
				</div>
			</div>
			<div id="ducklingPassport" class="std stdRounded">
				<h3><span class="duckling">Duckling通行证</span></h3>
				<p>科研在线，只是协同工作环境套件“Duckling”众多应用中的一个。使用同样的用户名（账号）和密码，即“Duckling通行证”，您可以访问如科研在线（<a href="http://rol.escience.cn" target="_blank">旧版</a>）、<a href="http://csp.escience.cn" target="_blank">国际会议服务平台</a>等服务。</p>
				<p>协同工作环境套件 Duckling，是专为团队协作提供的综合性资源共享和协同平台。集成网络环境中的硬件、软件、数据、信息等各类资源，集协同编辑、信息发布、文档管理、即时通讯、网络电话、短信通知、组织结构、文献共享、数据计算等为一体，为团队提供先进的信息化平台。</p>
			</div>
			<div id="directShare" class="std stdRounded">
				<h3>快速分享</h3>
				<p>不需注册或登录，可以直接与朋友分享文件！通过邮件通知中的链接，可以快速获得分享的文件。</p>
				<p class="ui-text-note">支持超过50MB文件的上传和下载。</p>
				<a id="fileShare" href="<vwb:Link context='shareFile' format='url'/>"></a>
			</div>

			<div class="ui-clear"></div>
		</div>


		<div id="aboutUs" class="content">
			<table class="ui-table-form" width="95%">
			<tbody>
				<tr>
					<td width="33%">
						<h4>关于我们</h4>
						<p><span class="ROL-small-black">&nbsp;</span>是由中国科学院计算机网络信息中心承担建设和服务的团队协同工作环境。</p>
						<p>中国科学院计算机网络信息中心(CNIC)是中国科学院下属科研事业单位, 是中国科学院信息化持续建设、运行与服务的支撑单位，国家互联网基础资源的运行管理机构，先进网络与高端应用技术的研发基地，国内外先进科技网络的重要组成部分。</p>
					</td>
					<td width="33%">
						<h4></h4>
						<p>协同工作环境研究中心的定位是研发和建设支撑e-Science活动的协同工作环境，承担全院国际会议的服务与管理，为我院的科研人员开展e-Science应用提供信息技术工具和相应的支撑服务，提供院内外各类会议相关的信息技术服务支撑。协同工作环境研究中心的核心业务包括：协同工作环境的建设和服务；各类会议网站的搭建和维护、管理。</p>
					</td>
					<td>
						<h4>联系方式</h4>
						<table class="ui-table-form">
						<tbody>
							<tr><th>联系电话：</th>
								<td>010-58812312</td>
							</tr>
							<tr><th>电子邮箱：</th>
								<td>vlab@cnic.cn</td>
							</tr>
							<tr><th>通讯地址：</th>
								<td>中科院计算机网络信息中心<br/>协同工作环境研究中心</td>
							</tr>
						</tbody>
						</table>
					</td>
				</tr>
			</tbody>
			</table>
			<hr/>
			<table class="ui-table-form" width="80%" id="privateLinks">
			<tbody>
				<tr>
					<td><a class="link-cas" href="http://www.cas.cn/" target="_blank" title="中国科学院"><span>中国科学院</span></a></td>
					<td><a class="link-cnic" href="http://www.cnic.cn/" target="_blank" title="中科院计算机网络信息中心"><span>中科院计算机网络信息中心</span></a></td>
					<td><a class="link-cerc" href="http://www.cerc.cnic.cn/" target="_blank" title="中科院网络中心协同工作环境研究中心"><span>中科院网络中心协同工作环境研究中心</span></a></td>
				</tr>
				<tr>
					<td><a class="link-duckling" href="http://duckling.escience.cn/dct/" target="_blank" title="Duckling"><span>Duckling</span></a></td>
					<td><a class="link-csp" href="http://csp.escience.cn/" target="_blank" title="中科院国际会议服务平台"><span>中科院国际会议服务平台 CSP</span></a></td>
					<td><a class="link-rol-old" href="http://rol.escience.cn/" target="_blank" title="科研在线2010版"><span>科研在线2010版</span></a></td>
				</tr>
			</tbody>
			</table>
		</div>
		
		<jsp:include page="/help/footer.jsp"></jsp:include>
		<div id="footer">
			<jsp:include page="/Version.jsp"></jsp:include>
		</div>
	</div>
</body>

</html>
