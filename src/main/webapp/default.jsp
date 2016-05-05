<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="net.duckling.ddl.common.*"%>
<%@taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
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
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link href="${contextPath}/jsp/aone/css/index.css" rel="stylesheet" type="text/css"/>
<link href="${contextPath}/jsp/aone/css/index-may2012.css" rel="stylesheet" type="text/css"/>
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
			hero = (hero==5) ? 1 : (hero+1);
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
	
	$("#banner #nav ul li").hover(function(){
		switchHero($(this).attr('index'));
		hero = parseInt($(this).attr('index'));
		console.log(hero);
		clearInterval(heroAuto);
		//setTimeout(setHero, 10000);
		setHero();
	})
});
</script>
<title>科研在线</title>
</head>
<body>
	<div class="ui-wrap">
		<div id="banner">
			<a id="ddl-logo" href="http://www.escience.cn" target="_blank"></a> 
			<a id="ddl-sub-logo" href="http://ddl.escience.cn"></a>
			<div id="nav">
				<ul>
					<li id="es-ddl" index="2"><a href="http://ddl.escience.cn">文档库</a></li>
					<li id="es-csp" index="3"><a href="http://csp.escience.cn">会议服务平台</a></li>
					<li id="es-rol" index="4"><a href="http://rol.escience.cn">实验室信息系统</a></li>
					<li id="es-more" index="5"><a>更多</a></li>
				</ul>
			</div>
			<div id="tel-login">
				<div id="tel">服务热线：010-58812345</div>
				<ul id="login">
					<li><a href="<vwb:Link context='switchTeam' absolute='true' format='url'/>">用户登录</a></li>
					<li><a href="<vwb:Link context='regist' absolute='true' format='url'/>">马上注册</a></li>
				</ul>
			</div>
		</div>
	
		<div class="hero" id="mainShow">
				<div id="hero-1" class="hero-container">
					<h1><img src="${contextPath}/jsp/aone/images/portal-May2012-hero-0.png" alt="科研在线" /></h1>
					<a class="learnMore" href="http://www.escience.cn"></a>
				</div>
				<div id="hero-2" class="hero-container" style="display:none">
					<h1><img src="${contextPath}/jsp/aone/images/portal-May2012-hero-1.png" alt="文档库，面向团队的文档存储与协作工具" /></h1>
					<a class="learnMore" href="http://ddl.escience.cn"></a>
				</div>
				<div id="hero-3" class="hero-container" style="display:none">
					<h1><img src="${contextPath}/jsp/aone/images/portal-May2012-hero-2.png" alt="会务信息化管理云平台，助您轻松办会！" /></h1>
					<a class="learnMore" href="http://csp.escience.cn"></a>
				</div>
				<div id="hero-4" class="hero-container" style="display:none">
					<h1><img src="${contextPath}/jsp/aone/images/portal-May2012-hero-3.png" alt="面向实验室的云端管理信息系统，助力科研！" /></h1>
					<a class="learnMore" href="http://rol.escience.cn"></a>
				</div>
				<div id="hero-5" class="hero-container" style="display:none">
					<h1><img src="${contextPath}/jsp/aone/images/portal-May2012-hero-4.png" alt="更多" /></h1>
				</div>
				<ul class="heroNav">
					<li class="current" index="1"></li>
					<li index="2"></li>
					<li index="3"></li>
					<li index="4"></li>
					<li index="5"></li>
				</ul>
			</div>
	
		<div id="aboutUs" class="content May2012">
			<table class="ui-table-form" width="95%">
			<tbody>
				<tr>
					<td width="33%">
						<h4>关于我们</h4>
						<p><span class="ROL-small-black">&nbsp;</span>是由中国科学院计算机网络信息中心承担建设和服务的团队协同工作环境。</p>
						<p>中国科学院计算机网络信息中心(CNIC)是中国科学院下属科研事业单位, 是中国科学院信息化持续建设、运行与服务的支撑单位，国家互联网基础资源的运行管理机构，先进网络与高端应用技术的研发基地，国内外先进科技网络的重要组成部分。</p>
					</td>
					<td width="33%">
						<h4>关注我们</h4>
						<a id="sina" href="http://www.weibo.com/dcloud"></a>
						<a id="tencent" href="#"></a>
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
					<td><a class="link-duckling" href="http://duckling.escience.cn/dct/" target="_blank" title="Duckling"><span>Duckling</span></a></td>
				</tr>
			</tbody>
			</table>
		</div>
		
		<ul id="ddl-footer-nav">
			<li id="footer-logo"><a href="http://www.escience.cn"></a></li>
			<li><a href="http://ddl.escience.cn">文档库</a></li>
			<li><a href="http://csp.escience.cn">会议服务平台</a></li>
			<li><a href="http://rol.escience.cn">实验室信息系统</a></li>
			<li><a href="http://www.weibo.com/dcloud">官方微博</a></li>
			<li><a href="http://www.escience.cn/aboutus.jsp">关于我们</a></li>
		</ul>
		<div class="ui-clear"></div>
		<div id="footer">
			
			Powered by <a target="_blank" href="http://duckling.escience.cn/"> Duckling&nbsp;3.0 </a> (京ICP备09112257号-1) 
		</div>
	</div>

</body>
</html>