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
<link rel="shortcut icon" type="image/x-icon" href="${contextPath}/images/favicon.ico" /><meta name="keywords" content="云计算服务,科研管理,协同办公,移动互联网,科研在线" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/fileuploader.css" type="text/css"/>	
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/lynx.css?v=DCT 6.2.2" type="text/css" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/uiLib-0.9.2.css?v=DCT 6.2.2" type="text/css" />
<link rel="stylesheet" href="${contextPath}/help/lynxIntro/lynxIntro.css" type="text/css" />
<script type="text/javascript" src="${contextPath}/scripts/jquery/jquery-1.8.3.min.js"></script>
<title>科研在线新功能介绍</title>
</head>
<body class='common-space'>
		
<div id="macroNav" class="ui-wrap wrapperFull">
	
	<div id="macro-innerWrapper" class="wrapper1280">
	<a id="logo" title="科研在线"></a>
		<ul id="staticNav" class="spaceNav">
			<li>
				<a>首页</a>
			</li>
			<li>
				<a>个人空间</a>
			</li>
		</ul>
		<ul id="spaceNavMore" class="spaceNav">
			<li class="moreSpace"><a title="更多"><span class="iconLynx icon-more"></span></a></li>
			<li class="createSpace"><a title="创建团队"><span class="iconLynx icon-create"></span></a></li>
		</ul>
		<ul id="userBox">
			<li class="search"><div id="globalSearch" class="searchBox transition"><input type="text" value="搜索" name="search_input" class="standby" tabindex="1"><a class="search_reset" disable="true"></a><div class="search_result" style="width: 350px;"></div><div id="fullScreenCover"></div></div></li>
			<li class="msgNotification msgCount0">
				<a><span id="noticeCount">0</span>通知</a>
		 	</li>
		 	<li class="userMe">
		 		<a>your name</a>
			</li>
		</ul>
		<div id="msgMenu" class="pulldownMenu" style="width:120px;">
			<ul>
				<li><a>团队邀请<span id="top-focus-state" class="msgCount0 count">0</span></a></li>
		 		<li><a>我的消息<span id="top-recommend-count" class="msgCount0 count">0</span></a></li>
		 		<li><a>我的关注<span id="top-focus-state" class="msgCount0 count">0</span></a></li>
		 	</ul>
		</div>
		<div id="userMeMenu" class="pulldownMenu">
			<ul>
				<li><a>个人资料</a></li>
				<li><a>注销</a>
				</li>
			</ul>
		</div>

		<div id="intro_1" class="intro_step" style="display:block;">
			<div class="title">顶部导航，帮助您在首页、个人空间<br><br>及各个团队空间中快速切换。</div>
			<a class="Iknow" id="Iknow_1">下一功能</a>
		</div>
		<div id="intro_2" class="intro_step">
			<div class="title">支持跨团队搜索，使得搜索结果更全面。</div>
			<a class="Iknow" id="Iknow_2">下一功能</a>
		</div>
		<div id="intro_3" class="intro_step">
			<div class="title">管理员在此设置团队权限、邀请成员。</div>
			<a class="Iknow" id="Iknow_3">下一功能</a>
		</div>
		<div id="intro_4" class="intro_step">
			<div class="title">团队中的更新信息以及协作消息，<br><br>都可以在这里看到~</div>
			<a class="Iknow" id="Iknow_4">下一功能</a>
		</div>
		<div id="intro_5" class="intro_step">
			<div class="title">这里存放着团队的全部文档。您的常用<br><br>页面以及工作记录也在这里！</div>
			<a class="Iknow" id="Iknow_5">下一功能</a>
		</div>
		<div id="intro_6" class="intro_step">
			<div class="title">标签，可以帮助您将文档按类别<br><br>整理得井井有条。</div>
			<a class="Iknow" id="Iknow_6">下一功能</a>
		</div>
		<div id="intro_7" class="intro_step">
			<div class="title">可以使用列表、缩略图等形式<br><br>展示文档。</div>
			<a class="Iknow" id="Iknow_7">下一功能</a>
		</div>
		<div id="intro_8" class="intro_step">
			<div class="title">您可以通过修改时间、文档类型或<br><br>多标签组合对文档快速定位。</div>
			<a class="Iknow" id="Iknow_8">下一功能</a>
		</div>
		<div id="intro_9" class="intro_step">
			<div class="title">推荐阅读，让你更快寻找到团队的<br><br>重要文档。</div>
			<a class="Iknow" id="Iknow_9" href="${contextPath}/index.jsp">开始使用</a>
		</div>
	</div>
	<div id="spaceNavMenu" class="pulldownMenu"></div>
</div>
	
<div id="masthead">
	<div id="banner" class="ui-wrap wrapperFull">
		<div id="banner-photo"><img src="${contextPath}/jsp/aone/images/banner-leaf.jpg" /></div>
		<div id="banner-insetShadow"></div>
		<div id="banner-innerWrapper" class="wrapper1280">
			<h1><a href="/dct/cerc">我的团队空间</a></h1>
			<a id="teamConfig" title="管理团队">管理团队</a>
			</div>
	</div>
	<div id="navigation" class="ui-wrap wrapperFull">
			<div id="navigation-innerWrapper" class="wrapper1280">
				<ul id="stdNav" class="switch">
					<li id="step_2"><a title="点击查看团队所有动态">动态</a></li>
					<li id="step_3"><a title="点击查看团队所有文档">文档<span id="resMore" class="icon13 icon-pulldownMore" title="更多"></span></a></li>
					<li id="step_4"><a title="点击查看所有星标文件"><span class="iconLynxTag icon-checkStar checked"></span></a></li>
				</ul>
				<ul id="opNav" class="switch">
					<li><a title="编写页面"><span class="iconLynx icon-page"></span> 编写页面</a></li>
					<li><a title="上传文件"><span class="iconLynx icon-upload"></span> 上传文件</a></li>
				</ul>
				<ul id="customNav">
					</ul>
				<ul id="customNavMore">
					<li class="moreSpace"><a>更多快捷</a></li>
				</ul>

				<div id="resourceMenu" class="pulldownMenu">
					<ul>
						<li><a href="/dct/cerc">常用</a></li>
						<li><a href="/dct/cerc#trace">历史记录</a></li>
					</ul>
				</div>
				<div id="customNavMenu" class="pulldownMenu" style="width:560px;"></div>
			</div>
		</div>
	</div>
	

<div id="body" class="ui-wrap wrapper1280">
			<div id="content" class="std stdRounded">
				<div id="tagSelector" class="content-menu readyHighLight6">
		<p class="ui-navList-title"><a class="iconLink config ui-RTCorner"></a>查看</p>

		<ul class="ui-navList">
			<li><a class="filter-option single" key="filter" value="untaged" ><span class="tagTitle">无标签资料</span></a></li>
		</ul>
	<p class="ui-navList-title tagGroupTitle">
			<a class="iconLink config ui-RTCorner" title="管理标签"></a>
			<a class="iconFoldable" id="tag1Control" title="展开/折叠"></a>
			未分类标签
		</p>
		<ul class="ui-navList" id="ungrouped-tag-list">

			<li><a id="tag-for-98" class="tag-option multiple" key="tag" value="98">
					<span class="tagTitle">默认集合</span><span class="tagResCount">754</span>
					</a>
					<a class="addToQuery" title="同时选中该标签"><span>+</span></a>
				</li>
			<li><a id="tag-for-2" class="tag-option multiple" key="tag" value="2">
					<span class="tagTitle">科研点项目</span><span class="tagResCount">527</span>

					</a>
					<a class="addToQuery" title="同时选中该标签"><span>+</span></a>
				</li>
			<li><a id="tag-for-26" class="tag-option multiple" key="tag" value="26">
					<span class="tagTitle">项目管理</span><span class="tagResCount">90</span>
					</a>
					<a class="addToQuery" title="同时选中该标签"><span>+</span></a>

				</li>
			<li><a id="tag-for-6" class="tag-option multiple" key="tag" value="6">
					<span class="tagTitle">DASv6</span><span class="tagResCount">32</span>
					</a>
					<a class="addToQuery" title="同时选中该标签"><span>+</span></a>
				</li>
			<li><a id="tag-for-184" class="tag-option multiple" key="tag" value="184">

					<span class="tagTitle">科研与教育</span><span class="tagResCount">30</span>
					</a>
					<a class="addToQuery" title="同时选中该标签"><span>+</span></a>
				</li>
			<li><a id="tag-for-241" class="tag-option multiple" key="tag" value="241">
					<span class="tagTitle">运行维护</span><span class="tagResCount">9</span>

					</a>
					<a class="addToQuery" title="同时选中该标签"><span>+</span></a>
				</li>
			<li><a id="tag-for-21" class="tag-option multiple" key="tag" value="21">
					<span class="tagTitle">学生工作</span><span class="tagResCount">7</span>
					</a>
					<a class="addToQuery" title="同时选中该标签"><span>+</span></a>

				</li>
			</ul>
	<p class="ui-navList-title tagGroupTitle">
				<a class="iconLink config ui-RTCorner" title="管理标签"></a>
				<a class="iconFoldable" id="tag1Control" title="展开/折叠"></a>
				日常</p>
			<ul class="ui-navList">
				<li><a id="tag-for-20" class="tag-option multiple" key="tag" value="20">
						<span class="tagTitle">快易通</span><span class="tagResCount">1341</span>
						</a>
						<a class="addToQuery"><span>+</span></a>
					</li>
				<li><a id="tag-for-1" class="tag-option multiple" key="tag" value="1">
						<span class="tagTitle">考勤与公出</span><span class="tagResCount">222</span>

						</a>
						<a class="addToQuery"><span>+</span></a>
					</li>
				<li><a id="tag-for-233" class="tag-option multiple" key="tag" value="233">
						<span class="tagTitle">全室共享</span><span class="tagResCount">10</span>
						</a>
						<a class="addToQuery"><span>+</span></a>

					</li>
				</ul>
		<p class="ui-navList-title tagGroupTitle">
				<a class="iconLink config ui-RTCorner" title="管理标签"></a>
				<a class="iconFoldable" id="tag1Control" title="展开/折叠"></a>
				团队建设</p>
			<ul class="ui-navList">
				<li><a id="tag-for-18" class="tag-option multiple" key="tag" value="18">
						<span class="tagTitle">自在园</span><span class="tagResCount">108</span>
						</a>
						<a class="addToQuery"><span>+</span></a>
					</li>
				<li><a id="tag-for-3" class="tag-option multiple" key="tag" value="3">
						<span class="tagTitle">团队建设</span><span class="tagResCount">29</span>
						</a>
						<a class="addToQuery"><span>+</span></a>
					</li>
				</ul>
		</div>
	<div id="tagItemsJSP" class="content-menu-body">

	<div class="innerWrapper">
		<div class="toolHolder" id="tagQueryHolder">
			<a id="dragTag" class="ui-RTCorner" title="将选择条件保存到快捷导航"><span class="iconLynx icon-shortcut"></span></a>
			<label class="statement">您已经选择：</label>
			<ul id="tagQuery"><li class="tQ-tag tQ-all"><label>全部文档</label></li></ul>
			<ul id="tagQuery"></ul>
		</div>
		<div class="readyHighLight9">
			<div style="height: 1%; overflow: hidden;" id="shortcutShow">
				<span id="ShortcutSpanTitle">推荐阅读：</span>
				<div class="showorhideSC">
					<ul style="height:100%;overflow:hidden;" id="shortcutView">
						<li class="shortcutItem">
							<a style="color:#f90">产品开发任务书</a> 
						</li>
						<li class="shortcutItem">
							<a style="color:#ff6600">考勤公示：2012年12月</a> 
						</li>
						<li class="shortcutItem">
							<a style="color:#0f0">ddl v1.0开发计划说明说</a> 
						</li>
						<li class="shortcutItem">
							<a style="color:#f90">取暖报销的说明</a> 
						</li>
						<li class="shortcutItem">
							<a style="color:#ff6600">新人入室指南说明</a> 
						</li>
					</ul>
				</div>
				<a id="moreShortcuts" class="openall">展开</a>
			</div>
		</div>
		<div class="toolHolder light readyHighLight8" >
			<ul class="filter" style="float:left;">
				<li><a id="remove-single-option">全部</a></li>
			</ul>
			<ul id="timeSelector" class="filter">
				<li><a class="date-option single" key="date" value="today">今天</a></li>
				<li><a class="date-option single" key="date" value="yesterday">昨天</a></li>
				<li><a class="date-option single" key="date" value="thisweek">本周</a></li>
				<li><a class="date-option single" key="date" value="lastweek">上周</a></li>

				<li><a class="date-option single" key="date" value="thismonth">本月</a></li>
				<li><a class="date-option single" key="date" value="lastmonth">上月</a></li>
			</ul>
			<ul id="typeSelector" class="filter">
				<li><a class="type-option single" key="type" value="DPage">页面</a></li>
				<li><a class="type-option single" key="type" value="doc">doc</a></li>
				<li><a class="type-option single" key="type" value="ppt">ppt</a></li>

				<li><a class="type-option single" key="type" value="xls">xls</a></li>
				<li><a class="type-option single" key="type" value="pdf">pdf</a></li>
				<!-- <li><a class="type-option single" key="type" value="other">其他文件</a></li> -->
				<li><a class="type-option single" key="type" value="Picture">图片</a></li>
				<li><a class="type-option single" key="type" value="Bundle">组合</a></li>
			</ul>
		</div>

		
		
		<div id="resourceList-header" class="toolHolder light">
			<label class="res-checkAll"><input type="checkbox" name="checkAll" />全选</label>
			<label class="res-checkOption" style="display:none;"><span id="checkOption" class="icon13 icon-pulldownMore"></span></label>
			<label class="res-star" style="display:none;"><span id="checkStar" class="iconLynxTag icon-greyStar"></span></label>
			<label class="res-resource">资源</label>
			<label class="res-tag">标签</label>
			
			<div class="ui-RTCorner">

				<div id="resourceList-search" class="searchBox" style="float:left;"></div>
				<ul id="viewSwitch" class="switch" style="float:left;">
					<li id="showAsTable"><a title="列表显示"><span class="iconLynxTag icon-listView"></span></a></li>
					<li id="showAsTight"><a title="紧凑列表"><span class="iconLynxTag icon-tightView"></span></a></li>
					<li id="showAsGrid"><a title="缩略图显示"><span class="iconLynxTag icon-gridView"></span></a></li>
				</ul>
			</div>
			
		</div>
		<ul id="resourceList" class="asTable">

		</ul>
		
		<div class="ui-clear"></div>
		
		<ul class="asTable  readyHighLight7" id="resourceList">
			<li item_id="997" class="element-data">  
				<div class="oper">   
					<input type="checkbox">   
					<div rid="997" class="iconLynxTag icon-checkStar unchecked"></div>  
				</div>  
				<div class="resBody">   
					<h2><a href="/dct/cerc/page/66519" class="page-link"><span class="headImg DPage "></span>考勤公示  </a></h2>  
					<div class="resChangeLog">    
						<span>周天 修改于 2012-05-23 15:56:53</span>     
						<span>版本：128</span>   
					</div>  
				</div>  
				<ul id="tag-item-997" class="tagList">
					<li tag_id="1">考勤与公出</li><li tag_id="233">全室共享</li>
					<li item_type="DPage" item_id="66519" rid="997" class="newTag"><a>+</a></li>
				</ul> 
			</li><li item_id="3944" class="element-data">  <div class="oper">   <input type="checkbox">   <div rid="3944" class="iconLynxTag icon-checkStar unchecked"></div>  </div>  <div class="resBody">   <h2><a href="/dct/cerc/page/81155" class="page-link"><span class="headImg DPage "></span>2012.05</a></h2>   <div class="resChangeLog">    <span>王力 修改于 2012-05-23 14:45:10</span>     <span>版本：51</span>   </div>  </div>  <ul id="tag-item-3944" class="tagList"><li tag_id="38">王力</li><li item_type="DPage" item_id="81155" rid="3944" class="newTag"><a>+</a></li></ul> </li><li item_id="8336" class="element-data">  <div class="oper">   <input type="checkbox">   <div rid="8336" class="iconLynxTag icon-checkStar unchecked"></div>  </div>  <div class="resBody">   <h2><a href="/dct/cerc/file/4432" class="page-link"><span class="headImg DFile docx"></span>产品开发任务书.docx</a></h2>   <div class="resChangeLog">    <span>王月 修改于 2012-05-23 14:39:33</span>     <span>版本：4</span>   </div>  </div>  <ul id="tag-item-8336" class="tagList"><li tag_id="9">CSP                         </li><li item_type="DFile" item_id="4432" rid="8336" class="newTag"><a>+</a></li></ul> </li><li item_id="2680" class="element-data">  <div class="oper">   <input type="checkbox">   <div rid="2680" class="iconLynxTag icon-checkStar unchecked"></div>  </div>  <div class="resBody">   <h2><a href="/dct/cerc/page/79817" class="page-link"><span class="headImg DPage "></span>DELL刀片服务器使用汇总</a></h2>   <div class="resChangeLog">    <span>王力 修改于 2012-05-23 13:44:29</span>     <span>版本：217</span>   </div>  </div>  <ul id="tag-item-2680" class="tagList"><li tag_id="241">运行维护</li><li tag_id="12">综合部内部管理</li><li item_type="DPage" item_id="79817" rid="2680" class="newTag"><a>+</a></li></ul> </li><li item_id="8335" class="element-data">  <div class="oper">   <input type="checkbox">   <div rid="8335" class="iconLynxTag icon-checkStar unchecked"></div>  </div>  <div class="resBody">   <h2><a href="/dct/cerc/file/4431" class="page-link"><span class="headImg DFile docx"></span>产品规格说明书.docx</a></h2>   <div class="resChangeLog">    <span>王月 修改于 2012-05-23 10:48:08</span>     <span>版本：3</span>   </div>  </div>  <ul id="tag-item-8335" class="tagList"><li tag_id="9">CSP                         </li><li item_type="DFile" item_id="4431" rid="8335" class="newTag"><a>+</a></li></ul> </li><li item_id="8452" class="element-data">  <div class="oper">   <input type="checkbox">   <div rid="8452" class="iconLynxTag icon-checkStar unchecked"></div>  </div>  <div class="resBody">   <h2><a href="/dct/cerc/page/81299" class="page-link"><span class="headImg DPage "></span>考勤公示：2012年3月21日-2012年4月20日</a></h2>   <div class="resChangeLog">    <span>周天 修改于 2012-05-23 10:40:33</span>     <span>版本：2</span>   </div>  </div>  <ul id="tag-item-8452" class="tagList"><li tag_id="1">考勤与公出</li><li item_type="DPage" item_id="81299" rid="8452" class="newTag"><a>+</a></li></ul> </li><li item_id="8434" class="element-data">  </ul>
		<a id="load-more-items" class="largeButton dim" style="display:none">更多结果</a>
			
		<div id="resourceAction" class="shadeConsole">
			<ul id="resAction">
				<li id="resAction-list"><a>选中<span class="resCount">0</span>个资料
					<span class="icon13 icon-pullupMore"></span></a>

				</li>
				<li id="resAction-tag"><a><span class="resAction-addTag"></span>添加标签</a></li>
				<li id="resAction-bundle"><a><span class="resAction-bundle"></span>组合</a></li>
				<li id="resAction-view"><a><span class="resAction-view"></span>打开</a></li>
			</ul>
		</div>
		<div id="resAction-listMenu" class="pulldownMenu" style="position:fixed;">

			<ul></ul>
			<a id="clearSelection" >清除选择</a>
			<!-- <a id="deleteSelected" class="icon13 icon-trash" title="删除选中的资料"></a> -->
		</div>
		
		<div id="addSingleTagDialog" class="lynxDialog" style="top:10%">
			<div class="toolHolder light">
				<h3>添加标签</h3>
			</div>

			<div class="inner">
				<div class="existTags">
					<p>已有标签：</p>
					<ul class="tagList"></ul>
				</div>
			
				<p><label>输入标签（用回车分隔）：<br/>
					<input type="text" name="typeTag" /></label>
				</p>

				<p class="ui-clear">或选择常用标签：</p>
				<div class="tagGroupHorizon">
					<div class="tG-scroll"></div>
				</div>
			</div>
			<div class="control largeButtonHolder">
				<input type="button" class="saveThis" value="保存">
				<input type="button" class="closeThis" value="取消">

			</div>
		</div>
		
		<div id="makeBundleDialog" class="lynxDialog">
			<div class="toolHolder light"><h3>将页面组合</h3></div>
			<div class="inner">
				<p><label>组合名称：<input type="text" name="m-bundle-title"/></label></p>
			</div>
			<div class="control largeButtonHolder">
				<input type="button" class="saveThis" value="组合">
				<input type="button" class="closeThis" value="取消">
			</div>
		</div>
		
		<div id="addShortcutDialog" class="lynxDialog">
			<div class="toolHolder light"><h3>添加快捷导航</h3></div>
			<div class="inner">
				<p><label>导航名称：<input type="text" name="navbarTitle" id="navbar-title" /></label></p>
			</div>
			<div class="control largeButtonHolder">
				<input type="button" class="saveThis" value="保存">
				<input type="button" class="closeThis" value="取消">
			</div>
		</div>

	
	</div>
	<div class="ui-clear"></div>
	</div>
	<div class="ui-clear"></div>

</div>
			<div class="clear"></div>
		</div>
		<a id="getFeedback" class="ui-sideTouch" href="http://iask.cstnet.cn/?/home/explore/category-11" target="_blank">意见反馈</a>
		
		<!-- intro begins here -->
		<div id="mask_1" class="intro_mask"></div>
	</body>

<script type="text/javascript">
$(document).ready(function(){
	var count = 1;
	$("#mask_1").show();
	
	$(".Iknow").click(function(){
		count++;
		$(this).parent().hide();
		$(this).parent().next().show();
		$(".isHighLight").removeClass("isHighLight");
		$(".readyHighLight" + count).addClass("isHighLight");
	});
	$("#Iknow_9").click(function(){
		$("#mask_1").hide();
	})
})
</script>
</html>