<%@ page language="java" pageEncoding="utf-8"%>
<%@ page import="net.duckling.ddl.common.*"%>
<%@taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">	
<html>
<head>
	<%pageContext.setAttribute("contextPath", request.getContextPath()); %>
	<%
		pageContext.setAttribute("contextPath", request.getContextPath());
		VWBContext.createContext(request,"error");
	%>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="keywords" content="云计算服务,科研管理,协同办公,移动互联网,科研在线" />
	<link href="${contextPath}/jsp/aone/css/index.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="${contextPath}/scripts/jquery/jquery-1.8.3.min.js"></script>
	<script type="text/javascript" src="${contextPath}/jsp/aone/js/backToTop-jQuery.js"></script>
	<title>基本操作 - 科研在线</title>

</head>

<body>
	<div class="ui-wrap">
		<div id="aoneBanner" class="std">
			<a id="ROL" href="${contextPath}/index.jsp"><span>科研在线</span></a>
			<div class="ui-RTCorner" id="userCtrl">
				<a class="largeButton green" href="<vwb:Link context='regist' absolute='true' format='url'/>">注册</a>
				<a class="largeButton" href="<vwb:Link context='switchTeam' absolute='true' format='url'/>">登录</a>
			</div>
			<ul id="nav">
				<li><a href="${contextPath}/index.jsp">概述</a></li>
				<li><a href="${contextPath}/help/introduction.jsp">科研在线是什么？</a></li>
				<li class="current"><a href="${contextPath}/help/tutorial.jsp">使用指南</a></li>
				<li><a href="${contextPath}/help/history.jsp">更新记录</a></li>
				<li><a href="<vwb:Link context='shareFile' format='url'/>">快速分享</a></li>
			</ul>
		</div>
		
		<div id="content">
			<div class="content-title tutorial">
				<ul class="titleDivide">
					<li id="scenarioTab"><a href="tutorial.jsp">应用场景</a></li>
					<li id="operationTab" class="current"><a href="#manual">基本操作</a></li>
					<li id="conceptTab"><a href="concept.jsp">概念和名词</a></li>
				</ul>
			</div>
			<div id="manual" class="content-through sub">
				<div id="manualIndex">
					<dl>
						<dt>用户与注册</dt>
						<dd><a href="#register">注册</a></dd>
						<dd><a href="#myTeam">我的团队</a></dd>
						<dd><a href="#demographic">编辑用户资料</a></dd>
						<dd><a href="#ducklingPassport">Duckling通行证</a></dd>
					</dl>
					<dl>
						<dt>团队</dt>
						<dd><a href="#createTeam">创建团队</a></dd>
						<dd><a href="#attendTeam">加入团队</a></dd>
						<dd><a href="#teamMember">邀请团队成员</a></dd>
						<dd><a href="#teamHome">团队首页</a></dd>
						<dd><a href="#teamUpdate">团队更新</a></dd>
						<dd><a href="#teamContact">团队通讯录</a></dd>
					</dl>
					<dl>
						<dt>集合</dt>
						<dd><a href="#createCollection">创建集合</a></dd>
						<dd><a href="#collectionGrid">集合格子模式</a></dd>
						<dd><a href="#shortcut">快捷方式</a></dd>
						<dd><a href="#collectionSetting">集合权限和设置</a></dd>
					</dl>
					<dl>
						<dt>页面和文件</dt>
						<dd><a href="#createPage">创建页面</a></dd>
						<dd><a href="#savePage">保存和找回</a></dd>
						<dd><a href="#uploadFile">上传文件</a></dd>
						<dd><a href="#move">移动页面或文件</a></dd>
					</dl>
					<dl>
						<dt>分享和沟通</dt>
						<dd><a href="#follow">关注页面</a></dd>
						<dd><a href="#followMember">关注团队成员</a></dd>
						<dd><a href="#recommend">分享页面或文件</a></dd>
						<dd><a href="#comment">评论和回复</a></dd>
						<dd><a href="#message">消息通知</a></dd>
						<dd><a href="#hot">热度</a></dd>
					</dl>
					<dl>
						<dt>协同编辑</dt>
						<dd><a href="#editPage">编辑页面</a></dd>
						<dd><a href="#version">版本记录</a></dd>
						<dd><a href="#compare">版本比较</a></dd>
						<dd><a href="#pageLock">页面锁定</a></dd>
					</dl>
					<dl>
						<dt>个人应用</dt>
						<dd><a href="#myMessage">我的消息和关注</a></dd>
						<dd><a href="#myNavigation">我的导航</a></dd>
						<dd><a href="#personalApp">个人应用</a></dd>
						<dd><a href="#quickOperation">快捷操作</a></dd>
						<dd><a href="#search">搜索</a></dd>
					</dl>
					<div class="ui-clear"></div>
				</div>
				<div class="manualBlock">
					<h3>用户与团队
						<a class="totop" href="#">回顶部</a>
					</h3>
					<dl>
					<dt id="register">注册</dt>
					<dd>
						<p>用户需要使用有效的电子邮箱作为帐户进行注册。注册后需通过电子邮件激活帐户。</p>
						<p>注册的帐号使用<a href="#ducklingPassport">Duckling通行证</a>；已经注册过Duckling通行证的用户不需要再注册，可以直接登录系统。</p>
					</dd>
					<dt id="myTeam">我的团队</dt>
					<dd>
						<p>登录科研在线，首先到达的即“我的团队”页面。</p>
						<p>这里列出所有您创建或加入的团队，也可以从这里看到邀请您加入团队的信息。</p>
						<p>点击团队图标，即可进入相应的团队。</p>
						<p class="img"><img class="border" src="figure/myTeam.jpg" /></p>
					</dd>
					<dt id="demographic">编辑用户资料</dt>
					<dd>
						<p>在<a href="#myTeam">我的团队</a>页面右侧的“个人信息”栏可以查看和修改个人资料信息（作为帐户的电子邮箱不能更改）。</p>
						<p>用户资料中的信息将在<a href="#teamContact">团队通讯录</a>中出现，供团队成员查看。</p>
					</dd>
					<dt id="ducklingPassport">Duckling通行证</dt>
					<dd>
						<p>协同工作环境套件Duckling是专为团队协作提供的综合性资源共享和协同平台，提供了包括<a href="http://csp.escience.cn" target="_blank">国际会议服务平台</a>、科研在线和<a href="http://rol.escience.cn" target="_blank">2010版科研在线</a>等服务。</p>
						<p>Duckling平台的所有服务使用同一个用户名（账号）和密码，即Duckling通行证。<a class="ui-text-note" href="http://duckling.escience.cn/dct/" target="_blank">更多关于Duckling&gt;&gt;</a></p>
					</dd>
					</dl>
				</div>
				<div class="manualBlock">
					<h3>团队内容与更新
						<a class="totop" href="#">回顶部</a>
					</h3>
					<dl>
					<dt id="createTeam">创建团队</dt>
					<dd>
						<img class="floatLeft" src="figure/createTeam.jpg" height="120" />
						<p>在<a href="#myTeam">我的团队</a>点击创建团队按钮即可创建团队。</p>
						<p>创建的团队需包含一个名称和一个代号。<br/>代号由英文字母（不区分大小写）和数字组成，用作访问团队的URL地址。</p>
						<p>团队创建成功后，即可开始使用科研在线。</p>
						<p style="display:none"><a class="ui-text-note" href="concept.html#team">更多关于团队的信息&gt;&gt;</a></p>
					</dd>
					<dt id="attendTeam">加入团队</dt>
					<dd>
						<p>成员加入团队采用邀请机制，由团队创建者或管理员向允许加入团队的成员发送<a href="#teamMember">邀请</a>。</p>
						<p>若您受邀加入团队，将收到电子邮件，点击其中的链接即可接受邀请。若您尚未注册科研在线（即Duckling通行证），系统会先给出注册页面，注册完成后自动加入团队。</p>
						<p>如果您已经在使用科研在线，在<a href="#myTeam">我的团队</a>页面将看到邀请提示，选择接受邀请即加入团队。</p>
					</dd>
					<dt id="teamMember">邀请团队成员</dt>
					<dd>
						<img src="figure/manageTeam.jpg" class="floatLeft border"/>
						<p>从<a href="#teamHome">团队首页</a>右上角的“管理团队”链接进入管理页面，选择“管理邀请”。</p>
						<p>输入需邀请的用户邮箱（多个邮箱用逗号分隔），填写邀请留言，点击“发送邀请”按钮，即发出邀请邮件。</p>
					</dd>
					<dt id="teamHome">团队首页</dt>
					<dd>
						<p>团队首页是进入团队的第一个页面，是使用科研在线的入口。</p>
						<p>从团队首页可以查看：1. 全部集合及部分<a href="#shortcut">快捷方式</a>；2. 团队最近发生的<a href="#teamUpdate">更新</a>；3. 查看个人最近的<a href="#myMessage">消息、关注</a>及<a href="#myNav">最近创建和编辑</a>的页面；4. 查看<a href="#teamContact">团队通讯录</a>；5. 进入个人应用界面。</p>
						<p>通过页面右侧的快捷操作栏，也可以快速创建集合、页面或上传文件。</p>
						<p class="img"><img class="border" src="figure/teamHome.jpg" /></p>
					</dd>
					<dt id="teamHomeGrid">集合列表</dt>
					<dd>
						<p>在<a href="#teamHome">团队首页</a>可以看到全部集合列表。当集合中设置了<a href="#shortcut">快捷方式</a>时，也会将靠前的快捷方式呈现出来。						
							<br/>通过它，您可以快速访问经常使用的集合和页面。</p>
						<p>点击右侧的“编辑网格”链接，可以调整各集合的显示顺序，将最重要和常用的集合放在靠前、显眼的位置。
							<br/>集合顺序的调整通过直接拖拽完成，调整完成后需要“保存”。</p>
						<p class="img"><img class="border" src="figure/editCollectionSeq.jpg" /></p>
						<p class="ui-text-note">
							<a href="#createCollection">如何创建集合？</a>
							<a href="#deleteCollection">如何删除集合？</a>				
							<a style="display:none" href="concept.html#collection">更多关于集合&gt;&gt;</a>
						</p>
						
					</dd>
					<dt id="teamUpdate">团队更新</dt>
					<dd>
						<p>从团队首页查看团队更新，按时间倒序列出最近发生修改和创建的内容。</p>
						<p>选择“按人物”模式，可以搜索和查看特定团队成员最近修改和创建的内容。</p>
						<p class="img"><img class="border" src="figure/teamUpdateByPerson.jpg" /></p>
						<p>选择“我关注的”可以查看自己<a href="#follow">关注的页面</a>和<a href="#followMember">人物</a>的动态。</p>
							
					</dd>
					<dt id="teamContact">团队通讯录</dt>
					<dd>
						<p>从团队首页或<a href="#quickOperation">快捷操作</a>可以访问团队通讯录。</p>
						<p>通讯录展示团队成员的姓名、注册邮箱、联系方式等信息。可以用搜索框进行快速检索。
							<br/>点击成员姓名可以进入个人集合。从通讯录可以直接<a href="#followMember">关注感兴趣的团队成员</a>。</p>
					</dd>
					</dl>
				</div>
				<div class="manualBlock">
					<h3>集合
						<a class="totop" href="#">回顶部</a>
					</h3>
					<dl>
					<dt id="createCollection">创建集合</dt>
					<dd>
						<img class="floatRight" src="figure/createCollection.jpg" />
						<ol>
							<li>在页面上方横向导航栏有“创建集合”链接，点击即可创建新集合。</li>
							<li>从页面右侧<a href="#quickOperation">快捷操作</a>的“创建集合”按钮创建集合。</li>
						</ol>
						<p>创建集合后，通过拖拽调整集合在导航栏中的显示顺序。</p>
						<p style="display:none" class="ui-text-note"><a href="concept.html#collection">更多关于集合&gt;&gt;</a></p>
					</dd>
					<dt id="collectionGrid">集合格子模式</dt>
					<dd>
						<p>集合首页展示该集合所有的页面信息，分为两种视图模式：列表模式和格子模式。</p>
						<p>列表模式默认以最近更新顺序排列所有内容，可以按标题和作者进行排序；同时呈现该集合的快捷方式。</p>
						<p>格子模式允许用户将内容归类存放在不同的“格子”里，便于查找。同一个内容可以同时加入到多个格子，未加入格子的内容不会显示。</p>
						<p class="img"><img class="border" src="figure/collectionGrid.jpg" /></p>
						<p>可以在<a href="#collectionSetting">集合设置页面</a>设置其中一种模式作为默认显示，以适应不同的需要：列表模式适合内容变动较快、需要关注其更新的情形，如项目文档；格子模式适合内容变动不大，但适合分类导航的情形，如各种规章、规范化文件等。</p>
					</dd>
					<dt id="shortcut">快捷方式</dt>
					<dd>
						<p>集合首页在列表模式时，右侧显示快捷方式，用以快速进入那些需要经常访问的内容。</p>
						<p>鼠标悬停在一条内容上时，右侧会出现“添加快捷”链接，点击即可。也可以在<a href="#collectionSetting">集合设置页面</a>中进行批量设置。
							<br/>在列表模式下可以直接拖动快捷方式进行排序，也可以直接删除。这些操作也可以在集合设置页面进行。</p>
						<p class="img"><img class="border" src="figure/shortcut.jpg" /></p>
						<p>集合的快捷方式将在<a href="#teamHome">团队首页</a>的集合列表中呈现。</p>
					</dd>
					<dt id="collectionSetting">集合权限和设置</dt>
						<dd>
							<img class="floatRight border" src="figure/collectionSetting.jpg" />
							<p>通过集合首页右上角的“设置”链接进入设置页面：</p>
							<ol>
								<li>基本设置：集合名称和概述，选择默认显示模式；</li>
								<li>管理页面：将页面移动到其它集合；</li>
								<li>管理快捷：将内容设置为快捷方式，并对快捷方式排序；</li>
								<li>管理权限：设置哪些用户具有浏览、编辑内容及管理集合的权限。</li>
							</ol>
							<p>集合权限分为两个层次：集合默认权限和个人权限。默认权限应用于所有用户；针对具体用户设置个人权限，覆盖默认权限的设置。
								<a style="display:none" class="ui-text-note" href="concept.html#authority">更多关于权限&gt;&gt;</a>
							</p>
							
						</dd>
					</dl>
				</div>
				<div class="manualBlock">
					<h3>页面和文件
						<a class="totop" href="#">回顶部</a>
					</h3>
					<dl>
					<dt id="createPage">创建页面</dt>
					<dd>
						<img class="floatRight border" src="figure/createPage.jpg" />
						<p>有两种方式可以创建页面：在选定的集合内创建页面；在任何位置快捷创建页面。</p>
						<p>进入一个集合，或在访问一个页面时，可以在页面右上角找到“+新建页面”链接，点击即可创建页面。</p>
						<p>或者，直接在页面右侧的<a href="#quickOperation">快捷操作</a>中选择新建页面。在新建时需要选择页面存放的集合。</p>
						<p class="ui-text-note"><a style="display:none" href="concept.html#page">更多关于页面&gt;&gt;</a></p>
					</dd>
					<dt id="savePage">保存和找回</dt>
					<dd>
						<img src="figure/savePage.jpg" class="floatRight border" />
						<p>使用“保存”按钮随时保存文件；系统也会定时自动保存编辑的内容，以备意外情况下找回。</p>
						<p>选择“放弃编辑”时，将丢弃本次编辑（进入编辑模式开始）的所有内容，包括手动“保存”的内容。</p>
						<p>选择“保存并退出”，将保存本次编辑的内容，生成新的<a href="#version">版本</a>，并推出编辑器。</p>
						<p>当浏览器或电脑意外崩溃后，系统将保留自动保存和手动保存的版本，并将有30分钟<a href="#pageLock">页面锁定</a>时间。</p>
						<p>锁定期间，其他用户无法编辑页面，页面呈现为本次编辑前的状态。当您重新开始编辑本页面时，将看到提示，帮助您恢复自动保存的版本。</p>
						<p>锁定时间结束，若您仍未尝试重新编辑页面，其他用户可以编辑本页面，他们也将收到有未保存内容的提示。若其他用户选择丢弃未保存的内容，这些内容将无法找回。</p>
					</dd>
					<dt id="uploadFile">上传文件</dt>
					<dd>
						<img src="figure/uploadFile.jpg" class="floatRight border" />
						<p>有三种方式可以上传文件：一是在选定的集合内上传文件；二是通过快捷操作栏上传文件。如右图所示。</p>
						<p>用这两种方法上传文件时，可以一次上传多个文件，并选择为同一次上传的文件编写统一的说明文字。</p>
						<p class="ui-clear">第三种方法是在编辑页面时上传文件作为附件。</p>
						<p>在编辑页面右侧的“附件”栏，通过“上传附件”按钮可以批量上传文件。</p>
						<p>每个文件都可以以超链接的形式添加到正文中。如果上传的是图片文件，还可以以图片形式插入到正文中。</p>
						<p class="img"><img src="figure/uploadFileAttach.jpg" class="border" /></p>
						<p class="ui-text-note"><a style="display:none" href="concept.html#file">更多关于文件&gt;&gt;</a></p>
					</dd>
					<dt id="move">移动页面或文件</dt>
					<dd>
						<p>将页面或文件从当前集合移动到其他集合。</p>
						<p>有两种方式可以移动内容：</p>
						<p>在浏览页面或文件时，可以用页面右上角的“移动页面”链接，将当前页面移动到选定的集合中。</p>
						<p>进入<a href="#collectionSetting">集合设置页面</a>的“管理页面”标签，可以进行批量页面的移动。</p>
					</dd>
					</dl>
				</div>
				<div class="manualBlock">
					<h3>分享和沟通
						<a class="totop" href="#">回顶部</a>
					</h3>
					<dl>
					<dt id="follow">关注页面</dt>
					<dd>
						<img src="figure/followNotice.jpg" class="floatRight" />
						<p>关注页面，当页面发生更新时，您可以在<a href="#teamUpdate">团队更新</a>中的“我的关注”模式中看到。同时，在右上角也会有“新关注”的提示。</p>
						<p>在页面或文件的底部，有“关注按钮”，点击即可关注当前页面或文件。对于已关注的页面，也可以通过该按钮取消关注。</p>
						<p>在团队更新的我的关注模式，可以进入“管理我的关注”页面，直接访问所有已关注的页面，或取消关注。</p>
						<p class="img"><img src="figure/followRecommend.jpg" class="border" /></p>
					</dd>
					<dt id="followMember">关注团队成员</dt>
					<dd>
						<p>关注团队成员，当他们创建或修改页面、上传文件时，您将能在<a href="#teamUpdate">团队更新</a>的“我的关注”模式中看到。</p>
						<p>要关注成员，进入<a href="#teamContact">团队通讯录</a>，点击列表最右侧的“关注Ta”链接即可。您也可以在这里取消对成员的关注。</p>
					</dd>
					<dt id="recommend">分享页面或文件</dt>
					<dd>
						<p>将页面或文件分享给选定的成员，并留言。</p>
						<p>接收分享的用户将在页面右上角看到通知，他们可以到<a href="#teamHome">团队首页</a>中<a href="#myMesasge">我的消息</a>栏目查看。</p>
						<p class="img"><img src="figure/recommend.jpg" /></p>
					</dd>
					<dt id="comment">评论和回复</dt>
					<dd>
						<p>在页面底部可以进行评论，并回复其他成员的评论。被回复的成员会收到通知。</p>
					</dd>
					<dt id="message">消息通知</dt>
					<dd>
						<p>页面右上角工具栏会在有新的消息（分享、回复、关注内容的更新）时给出提示。</p>
						<p class="img"><img src="figure/msgAlert.jpg" class="border" /></p>
					</dd>
					<dt id="hot">热度</dt>
					<dd>
						<p>热度记录阅读该页面的人次（同一人同一天连续访问记1次）。您还可以查看最近访问页面的用户。</p>
					</dd>
					</dl>
				</div>
				<div class="manualBlock">
					<h3>协同编辑
						<a class="totop" href="#">回顶部</a>
					</h3>
					<dl>
					<dt id="editPage">编辑页面</dt>
					<dd>
						<p>科研在线允许多人共同编辑页面。当您拥有编辑权限时，可以通过页面右上角的“编辑”链接编辑页面。</p>
					</dd>
					<dt id="version">版本记录</dt>
					<dd>
						<p>每一次编辑页面都会产生一个版本号，保存当前的页面状态，这些版本可以用来回溯页面的变更过程。</p>
						<p>查看页面时，点击标题下方的版本链接，即进入版本查看页面（右图）。</p>
						<p>点击左侧版本号数字，即可查看相应版本的内容；点击右侧“与上（下）一版本区别”链接，可以进行<a href="#compare">版本比较</a>。</p>
						<p class="img"><img src="figure/versionEntry.jpg" class="border" width="350"/>
							<img src="figure/version.jpg" class="border" width="350" /></p>
					</dd>
					<dt id="compare">版本比较</dt>
					<dd>
						<p>科研在线的版本比较工具可以区分文字变更，并用高亮显示。</p>
						<p>删除的内容以红圈内的惊叹号“！”表示，点击可以查看被删除的文字。添加的内容以白框标出。</p>
						<p>通过顶部的工具栏，您可以切换不同的版本进行比较，也可以逐个浏览差异。</p>
						<p class="img"><img src="figure/compare.jpg" class="border" /></p>
					</dd>
					<dt id="pageLock">页面锁定</dt>
					<dd>
						<p>为避免多人协同编辑同一篇内容产生冲突，当一个用户开始编辑页面时会锁定页面。</p>
						<p>页面锁定期间，其他用户无法使用编辑功能；其他用户无法看到正在编辑的内容。</p>
						<p>正常保存退出或放弃编辑后，锁定即取消。</p>
						<p>当编辑者长时间不对页面进行操作时，页面将在30分钟后自动保存退出，解除锁定。</p>
						<p>当编辑者由于意外而退出编辑页面，如浏览器崩溃、误点关闭窗口而未退出等，系统将保持锁定30分钟，以便用户恢复系统后重新进入编辑，找回自动保存的内容。</p>
						<p class="ui-text-note"><a href="#savePage">关于保存和找回</a></p>
					</dd>
					</dl>
				</div>
				<div class="manualBlock">
					<h3>个人应用
						<a class="totop" href="#">回顶部</a>
					</h3>
					<dl>
					<dt id="myMessage">我的消息和关注</dt>
					<dd>
						<p>当有新消息时，可以在页面顶部看到<a href="#msgAlert">消息通知</a>；也可以从<a href="#teamHome">团队首页</a>进入“我的消息”、“团队更新”查看。</p>
					</dd>
					<dt id="myNavigation">我的导航</dt>
					<dd>
						<p>从<a href="#teamHome">团队首页</a>进入“我的导航”，记录用户最近编辑和创建的页面。</p>
					</dd>
					<dt id="personalApp">个人应用</dt>
					<dd>
						<p>从<a href="#teamHome">团队首页</a>左侧菜单可以找到“个人应用”栏目，这里列出与脱离团队机制的应用模块。</p>
					</dd>
					<dt id="quickOperation">快捷操作</dt>
					<dd>
						<p>通过页面右侧的快捷操作图标，可以快速地执行常见任务，包括创建页面和集合、上传文件、查看通讯录、帮助和首页等。</p>
						<p>当浏览器窗口宽度小于1024像素时，快捷操作将折叠在屏幕右侧。</p>
						<p class="img"><img src="figure/quickOperation.jpg" /></p>
					</dd>
					<dt id="search">搜索</dt>
					<dd>
						<p>页面顶部的搜索框，可以搜索团队内的资料。</p>
						<p>排位最靠前的结果直接在下拉列表中呈现。您也可选择“显示全部”查看所有的搜索结果。</p>
						<p class="img"><img src="figure/search.jpg" class="border" /></p>
					</dd>
					</dl>
				</div>
				
				<div id="manualIndexSub">
					<dl>
						<dt>用户与注册</dt>
						<dd><a href="#register">注册</a></dd>
						<dd><a href="#myTeam">我的团队</a></dd>
						<dd><a href="#demographic">编辑用户资料</a></dd>
						<dd><a href="#ducklingPassport">Duckling通行证</a></dd>
					</dl>
					<dl>
						<dt>团队</dt>
						<dd><a href="#createTeam">创建团队</a></dd>
						<dd><a href="#attendTeam">加入团队</a></dd>
						<dd><a href="#teamMember">邀请团队成员</a></dd>
						<dd><a href="#teamHome">团队首页</a></dd>
						<dd><a href="#teamUpdate">团队更新</a></dd>
						<dd><a href="#teamContact">团队通讯录</a></dd>
					</dl>
					<dl>
						<dt>集合</dt>
						<dd><a href="#createCollection">创建集合</a></dd>
						<dd><a href="#collectionGrid">集合格子模式</a></dd>
						<dd><a href="#shortcut">快捷方式</a></dd>
						<dd><a href="#collectionSetting">集合权限和设置</a></dd>
					</dl>
					<dl>
						<dt>页面和文件</dt>
						<dd><a href="#createPage">创建页面</a></dd>
						<dd><a href="#savePage">保存和找回</a></dd>
						<dd><a href="#uploadFile">上传文件</a></dd>
						<dd><a href="#move">移动页面或文件</a></dd>
					</dl>
					<dl>
						<dt>分享和沟通</dt>
						<dd><a href="#follow">关注页面</a></dd>
						<dd><a href="#followMember">关注团队成员</a></dd>
						<dd><a href="#recommend">分享页面或文件</a></dd>
						<dd><a href="#comment">评论和回复</a></dd>
						<dd><a href="#message">消息通知</a></dd>
						<dd><a href="#hot">热度</a></dd>
					</dl>
					<dl>
						<dt>协同编辑</dt>
						<dd><a href="#editPage">编辑页面</a></dd>
						<dd><a href="#version">版本记录</a></dd>
						<dd><a href="#compare">版本比较</a></dd>
						<dd><a href="#pageLock">页面锁定</a></dd>
					</dl>
					<dl>
						<dt>个人应用</dt>
						<dd><a href="#myMessage">我的消息和关注</a></dd>
						<dd><a href="#myNavigation">我的导航</a></dd>
						<dd><a href="#personalApp">个人应用</a></dd>
						<dd><a href="#quickOperation">快捷操作</a></dd>
						<dd><a href="#search">搜索</a></dd>
					</dl>
					<div class="ui-clear"></div>
				</div>
			</div>
			<div class="content-through">
				<ul class="titleDivide bottom">
					<li id="scenarioTab"><a href="tutorial.jsp">应用场景</a></li>
					<li id="operationTab" class="current"><a href="#manual">基本操作</a></li>
					<li id="conceptTab"><a href="concept.jsp">概念和名词</a></li>
				</ul>
			</div>
			<div class="ui-clear"></div>
		</div>
		
		<jsp:include page="/help/footer.jsp"></jsp:include>
		<div id="footer">
			<jsp:include page="/Version.jsp"></jsp:include>
		</div>
	</div>
</body>
</html>
