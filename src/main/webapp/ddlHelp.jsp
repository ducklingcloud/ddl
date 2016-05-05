<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="net.duckling.ddl.common.*"%>
<%@taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
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
<link href="${contextPath}/jsp/aone/css/index.css" rel="stylesheet"
	type="text/css" />
<link
	href="${contextPath}/jsp/aone/css/index-nov2013.css?v=${aoneVersion}"
	rel="stylesheet" type="text/css" />
<link href="${contextPath}/images/favicon.ico" rel="shortcut icon"
	type="image/x-icon" />
<script type="text/javascript"
	src="${contextPath}/scripts/jquery/jquery-1.8.3.min.js"></script>
<script type="text/javascript">
	$(document).ready(
			function() {
				$(".left-intro h2 a").click(
						function() {
							$(this).addClass('current-nav').siblings()
									.removeClass('current-nav');
							var _index = $(this).index();
							$('.pane').eq(_index).show().siblings('.pane')
									.hide();
						});
				$("#escienceMenu").mouseenter(function() {
					$("#es-pullDownMenu").show();
				});
				$("#escienceMenu").mouseleave(function() {
					$("#es-pullDownMenu").hide();
				});
				$(".active").removeClass("active");
				$("#ddlNav-help").parent("li").addClass("active");
			});
	$(function() {
		$(".title").eq(0).show().addClass('current');
		$(".hidden").eq(0).show();
		$(".title").click(function(event) {
			event.preventDefault();
			var index = $(".title").index(this);
			$(".hidden").eq(index).toggle();
			$(this).toggleClass("current");
		});
	});
		
		$(function() {
		
			$(".question").click(function(event) {
				event.preventDefault();
				var index = $(".question").index(this);
				$(".content").eq(index).toggle();
				$(this).toggleClass("current");
			})

	});
	
</script>
<title>帮助中心-科研在线团队文档库</title>
</head>
<body class="texure">
	<jsp:include page="/ddlHeader.jsp"></jsp:include>
	<div id="ddl-intro" class="help">
		<div class="ui-wrap">
			<div class="left-intro" style="width:100%">
				<h2 class="help">
					<a href="#" class="current-nav">新手指南</a><a href="#">常见问题 </a><a
						href="#" style="margin-right: 0px;">更新日志</a>
				</h2>
			</div>
		</div>
	</div>
	<div class="ui-wrap help-content">
		<div class="pane" id="ddl-newer">
			<!--div id="steps" class="content-through newer">
				<h3 style="float: left;">怎样开始？</h3>
				<%-- 	<a class="ui-iconButton help smallText"
					href="${contextPath}/help/lynxIntro/introSteps.jsp" target="_blank">查看演示</a> --%>
				<ul>
					<li><h4>注册</h4>
						<p>使用有效邮箱注册账号，邮箱会收到一封邮件，点击其中的激活链接后，账号注册成功。</p></li>
					<li><h4>创建团队</h4>
						<p>可为您的团队设置名称以及网址。</p></li>
					<li><h4>邀请用户</h4>
						<p>用邮件邀请用户加入团队，支持邮箱批量导入。</p></li>
					<li><h4>开始使用</h4>
						<p>新建协作文档或上传文件，建立共享文档库。支持成员之间社会化的沟通协作。</p></li>
				</ul>
				<div class="ui-clear"></div>
			</div-->
			<div class="content-through newer">
             	<h3>
					<a href="#" class="title">1. 创建一个团队开始工作</a>
				</h3>
				<div class="hidden">
					<p>(1) 进入“个人面板”，点击 【创建团队】，或点击顶部黑色导航栏中的"加号"图标即可创建团队。</p>
					<p>
						<img src="${contextPath}/jsp/aone/images/ddlNewer-1-index.png"
							class="newImg" />
					</p>
					<p>
						(2) 在创建团队页面填入相应信息，包括“团队名称”、“团队网址”、“团队权限”等信息。点击创建按钮即可完成团队创建。
					</p>
					<p>
						<img src="${contextPath}/jsp/aone/images/ddlNewer-2-team.png"
							class="newImg" />
					</p>

					<p style="margin-left: 3em;">注：团队分为“完全保密”、“公开需审核”，“完全公开”三种权限。</p>
					
					<ul class="pay-attention">
						<li>完全保密，团队成员只能由管理员用邮件邀请的方式加入。</li>
						<li>公开需审核，团队成员可以主动加入，但需要管理员审核后才能加入。</li>
						<li>完全公开，用户无需管理员审核即可加入。</li>
					</ul>
				</div>
			</div>

			<div class="content-through newer">
				<h3>
					<a href="#" class="title">2. 邀请成员加入团队</a>
				</h3>
				<div class="hidden">
					<p>(1) 创建团队后，点击团队名称右侧的邀请按钮，管理员可以邀请用户加入自己的团队。</p>
					<p>
						<img src="${contextPath}/jsp/aone/images/ddlNewer-1-invite.png"
							class="newImg " />
					</p>
					<p>(2) 输入受邀成员的邮箱：</p>
					<p class="text-indent">a. 输入受邀成员的邮箱地址，点击发送按钮，对方会收到邀请邮件。等到对方接受，便可以在一个团队中了。
					</p>
					<p>
						<img src="${contextPath}/jsp/aone/images/ddlNewer-2-invite.png"
							class="newImg" />
					</p>
					<p class="text-indent">b. 还有一种方式是通过地址簿文件导入，我们支持CSV或vCard格式的地址簿文件。选择导入地址簿，并导入文件。</p>
					<p>
						<img src="${contextPath}/jsp/aone/images/ddlNewer-3-invite.png"
							class="newImg " />
					</p>
				</div>
			</div>

			<div class="content-through newer">
				<h3>
					<a href="#" class="title">3. 如何加入团队</a>
				</h3>
				<div class="hidden">
					<p>(1) 在个人面板，点击加入公开团队，选择感兴趣的团队，点击加入。公开团队可以直接加入，公开需审核团队需要管理员的审核，才能加入。审核结果会以邮件形式发送您的联系邮箱。</p>
					<p style="margin-bottom: 20px;">(2) 非文档库用户，点击该公开团队的【推广链接】（管理员在团队的基本设置界面可查看该团队的推广链接），注册为文档库用户后即可加入。</p>
				</div>

			</div>
			<div class="content-through newer">
				<h3>
					<a href="#" class="title">4. 了解团队文档库的整体布局</a>
				</h3>
				<div class="hidden">
					<p>(1) 进入团队后，蓝色banner条的右侧是团队导航区域，可以切换浏览团队的所有文档、最新动态和成员。</p>
					<p>(2) 左侧是文档导航栏，可以浏览团队所有文档、团队最近更新文档和个人常用文档（我常用的、我创建的、已加星标）。</p>
					<p>(3) 左侧下方是文档标签导航栏，可以根据标签浏览文档。</p>
					<p>(4) 右侧是文档列表，可以点击查看文档，进行分享、移动等操作。</p>
					<p>
						<img src="${contextPath}/jsp/aone/images/ddlNewer-1-team.png"
							class="newImg" />
					</p>
				</div>
			</div>
			<div class="content-through newer">
				<h3>
					<a href="#" class="title">5. 创建文档、上传文件以便共享资源</a>
				</h3>
				<div class="hidden">
					<p>团队文档库中的文档有两种类型：</p>
					<p>(1) 协作文档，在线创建的文档，使用文档库提供的编辑器来编辑内容，可以多人协作编辑。</p>
					<p>(2) 文件，本地上传的文件，不限格式。对PDF，Office系列文件提供在线预览功能。</p>
					<p>
						<img src="${contextPath}/jsp/aone/images/ddlNewer-1-create.png"
							class="newImg " />
					</p>
					<p>新建协作文档：</p>
					<p class="text-indent">a. 点击导航栏中的【新建协作文档】按钮，可以快速创建协作文档。文档库自带的编辑器，可以实现丰富的编辑功能，帮助您创建包含表格、图片等复杂格式的页面。</p>
					<p>
						<img src="${contextPath}/jsp/aone/images/ddlNewer-3-create.png"
							class="newImg " />
					</p>
					<p class="text-indent">b. 选择【保存并退出】，将保存本次编辑的内容，生成新的版本，并退出编辑器。</p>
					<p class="text-indent">c. 使用【保存】按钮随时保存文件。</p>
					<p class="text-indent">d. 选择【放弃编辑】时，将丢弃本次编辑（进入编辑模式开始）的所有内容，包括手动“保存”的内容。</p>
					<p class="text-indent">e. 系统也会定时自动保存编辑的内容，以备意外情况下找回。</p>
					<p class="text-indent">f. 新建文档时，如果没手动保存意外退出浏览器，可在【我创建的】里查找系统保存的草稿，编辑后可发布。</p>
					<p>上传文件：</p>
					<p class="text-indent">点击导航栏中的【上传文件】按钮，一次可上传多个文件。文件不允许重名，对相同名字的文件，上传仅更新版本。</p>
				</div>
			</div>
			<div class="content-through newer">
				<h3>
					<a href="#" class="title">6. 快速分享一个文档</a>
				</h3>
				<div class="hidden">
					<p>(1) 点击文档旁边的分享按钮</p>
					<p>
						<img src="${contextPath}/jsp/aone/images/ddlNewer-1-share.png"
							class="newImg " />
					</p>
					<p>(2) 分享给团队成员：</p>
					<p class="text-indent">a. 分享文件时，可以选择分享给团队成员或分享给团队之外的人；分享协作文档目前只能分享给团队成员。</p>
					<p class="text-indent">b. 在输入框内选择您要分享的成员，点击【分享】按钮，完成分享。</p>
					<p class="text-indent">c. 分享信息会以邮件形式发送到对方的联系邮箱中。对方登录团队文档库，也可以在个人消息中看到分享信息。</p>
					<p>
						<img src="${contextPath}/jsp/aone/images/ddlNewer-2-share.png"
							class="newImg " />
					</p>
					<p>(3) 分享给外部成员：</p>
					<p class="text-indent">a. 文件支持分享给团队外部成员。选择分享给其他人，输入有效时间、您要分享的人的邮箱，点击【分享】。</p>

					<p>
						<img src="${contextPath}/jsp/aone/images/ddlNewer-2-share.png"
							class="newImg " />
					</p>
				</div>
			</div>
			<div class="content-through newer">
				<h3>
					<a href="#" class="title">7. 在协作文档中插入其他文档及图片</a>
				</h3>
				<div class="hidden">
					<p>(1) 编辑文档时，用户可以将文档库中的其他文档以链接的形式插入到协作文档中，让成员便捷的浏览到相关文档。支持一次插入多个文档。选择编辑器中的【文档】按钮。用户可以选择已有文档，也可以点击【上传文件】从本地上传文件，再进行选择。</p>
					<p>
						<img src="${contextPath}/jsp/aone/images/ddlNewer-1-insert.png"
							class="newImg" />
					</p>
					<p>(2) 用户也可以将文档库中或者本地的图片插入到页面中。支持一次插入多个图片。选择编辑器中的【图像】按钮。</p>
					<p>
						<img src="${contextPath}/jsp/aone/images/ddlNewer-2-insert.png"
							class="newImg" />
					</p>
				</div>
			</div>
			<div class="content-through newer">
				<h3>
					<a href="#" class="title">8. 如何实现版本控制</a>
				</h3>
				<div class="hidden">
					<p>(1) 版本控制：</p>
					<ul><li>对于协作文档，用户可对页面内容进行在线编辑。每一次编辑，都将创建新的版本。</li>
					<li>对于文件，用户上传的文件名称如果已存在，会更新相同名称的文件，创建新的版本。</li></ul>
					<p>(2) 入口：</p>
					<ul><li>点击页面内的版本信息，即进入版本管理页面。管理版本页面有完整的版本历史，且提供版本差异比较的功能。</li></ul>
					<p>
						<img src="${contextPath}/jsp/aone/images/ddlNewer-1-version.png"
							class="newImg" />
					</p>
					<ul><li>差异比较工具，可以识别增加、删除、移动等操作，帮助用户了解其他人对文档的修改内容。其中，增加的文字用黄色底色标识，被删除的文字用红色底色标识，并动态显示移动前位置。</li></ul>
					<p>(3) 版本恢复：</p>
					<p class="text-indent">版本管理界面还提供对某一历史版本的恢复功能。恢复某个历史版本后，该历史版本会发布为当前最新版本。</p>
					<p>
						<img src="${contextPath}/jsp/aone/images/ddlNewer-2-version.png"
							class="newImg" />
					</p>
					
				</div>
			</div>
			<div class="content-through newer">
				<h3>
					<a href="#" class="title">9. 使用文件夹整理文档</a>
				</h3>
				<div class="hidden">
					<p>文档库提供文件夹的文档管理方式，您可以将若干相关文档存放在一个文件夹里，便于查找。</p>
					<p>
						<img src="${contextPath}/jsp/aone/images/ddlNewer-1-folder.png"
							class="newImg" />
					</p>
					<p>(1) 如何移动或复制</p>
					<p class="text-indent">a.勾选要移动的文档，点击移动或复制按钮。</p>
					<p>
						<img src="${contextPath}/jsp/aone/images/ddlNewer-2-folder.png"
							class="newImg " />
					</p>
					<p class="text-indent">b. 在弹出框内选择要移动的目标文件夹，也可以新建文件夹。点击确定，所选文档或文件夹都被移动到目标文件夹内。</p>
					<p>
						<img src="${contextPath}/jsp/aone/images/ddlNewer-3-folder.png"
							class="newImg " />
					</p>
				</div>
			</div>
			<div class="content-through newer">
				<h3>
					<a href="#" class="title">10. 使用标签管理文档</a>
					
				</h3>
				<div class="hidden"><p class="text-indent"><a href="${contextPath}/ddlTagHelp.jsp" target="_blank">查看标签使用说明</a></p></div>
			</div>
			<div class="content-through newer">
				<h3>
					<a href="#" class="title">11. 添加相关文档</a>
				</h3>
				<div class="hidden">
					<p>如果文档之间关联性较强，除了用“文件夹”、打相同“标签”等方式进行关联外，还可以在文档界面右下方的“相关文档”列表中进行添加。</p>

					<p>
						<img src="${contextPath}/jsp/aone/images/ddlNewer-1-doc.png"
							class="newImg" />
					</p>
				</div>
			</div>
			<div class="content-through newer">
				<h3>
					<a href="#" class="title">12. 如何找到个人常用文档</a>
				</h3>
				<div class="hidden">
					<p>为方便用户快速从大量的文档中找到自己常用的，团队文档库提供针对个人的文档浏览视角：</p>
					<p>(1) 星标文档集</p>
					<p class="text-indent">通过点击标题前方的空星标志，为自己感兴趣或认为重要的文档打上星标。打上星标的文档，将被放入星标文档集，用户通过点击左侧导航栏上的【已加星标】按钮，快速查看所有星标文档。</p>
					<p class="text-indent">决定将哪些文档打上星标，是用户个性化的设置，只有用户自己了解，不与团队其他成员共享。</p>
					<p>
						<img src="${contextPath}/jsp/aone/images/ddlNewer-2-doc.png"
							class="newImg" />
					</p>
					<p>(2) 常用文档</p>
					<p class="text-indent">在左侧导航栏【我常用的】按钮中，可以看到系统自动生成的用户最常访问的文档链接。</p>
					<p>
						<img src="${contextPath}/jsp/aone/images/ddlNewer-3-doc.png"
							class="newImg " />
					</p>
					<p>(3) 我创建的</p>
					<p class="text-indent">在左侧导航栏【我创建的】按钮中，可以看到个人创建的协作文档和上传的文件。</p>
					<p>
						<img src="${contextPath}/jsp/aone/images/ddlNewer-4-doc.png"
							class="newImg " />
					</p>
					<p>(4) 我的足迹</p>
					<p class="text-indent">
						点击主导航栏【动态】按钮，可以看到团队动态，以及我的消息、足迹、关注等信息。其中，【我的足迹】可以看到自己所有的历史操作。</p>
					<p>
						<img src="${contextPath}/jsp/aone/images/ddlNewer-5-doc.png"
							class="newImg " />
					</p>
				</div>
			</div>
			<div class="content-through newer">
				<h3>
					<a href="#" class="title">13. 删除和恢复文档</a>
				</h3>
				<div class="hidden">
					<p>(1) 删除文档</p>
					<p class="text-indent">若要删除文档，可点击【删除】按钮，进行删除。只有团队管理员或文档创建者才能删除文档。</p>
					<p>
						<img src="${contextPath}/jsp/aone/images/ddlNewer-6-doc.png"
							class="newImg " />
					</p>
					<p>(2) 恢复被删除的文档</p>
					<p class="text-indent">若要恢复已被删除的文档，可以点击【我的足迹】，查看删除的历史记录。</p>
					<p>
						<img src="${contextPath}/jsp/aone/images/ddlNewer-7-doc.png"
							class="newImg " />
					</p>
					<p>(3) 点击被删除的文档链接，进入到删除文档的详细页面。点击【点此恢复该文档】，即可恢复文档。</p>
					<p>
						<img src="${contextPath}/jsp/aone/images/ddlNewer-8-doc.png"
							class="newImg " />
					</p>
				</div>
			</div>
			<div class="content-through newer">
				<h3>
					<a href="#" class="title">14. 如何与队友分享、交流</a>
				</h3>
				<div class="hidden">
					<p>(1) 将你认为有用的文档分享给同事，还可以附上留言，同事将收到系统实时发出的提醒消息。</p>
					<p>
						<img src="${contextPath}/jsp/aone/images/ddlNewer-1-exchange.png"
							class="newImg " />
					</p>
					<p>(2) 每个页面下方都设有讨论区，您可以与同事就该文档在线展开讨论。</p>
					<p>
						<img src="${contextPath}/jsp/aone/images/ddlNewer-2-exchange.png"
							class="newImg " />
					</p>
					<p>(3) @成员：</p>
					<p class="text-indent">
						在评论时，可以@团队成员。在回复框中键入@符号，自动弹出团队成员列表，供用户方便找到成员。该消息将会以邮件形式发送给对方。
					</p>
					<p>
						<img src="${contextPath}/jsp/aone/images/ddlNewer-3-exchange.png"
							class="newImg " />
					</p>
				</div>
			</div>
			<div class="content-through newer">
				<h3>
					<a href="#" class="title">15. 个性化设置团队</a>
				</h3>
				<div class="hidden">					
					<p>进入团队空间后，管理员通过点击团队名称右侧的管理按钮，可以对团队进行如下操作：基本设置、管理用户、管理申请、邀请成员、导出文档。</p>
					<p>
						<img src="${contextPath}/jsp/aone/images/ddlNewer-1-set.png"
							class="newImg " />
					</p>
					<p>(1) 基本设置-个性化设置团队默认进入的首页：</p>
					<p class="text-indent">可以设置团队成员进入团队后首先看到的页面，是文档还是动态页面。</p>
					<p>
						<img src="${contextPath}/jsp/aone/images/ddlNewer-2-set.png"
							class="newImg " />
					</p>
					<p>(2) 管理成员：</p>
					<p class="text-indent">管理员可为团队成员分配不同权限，包括可查看、可编辑、可管理三种；可以移除成员；</p>
					<p>(3) 管理申请，如果团队权限设置为“公开需审核”，则管理员可以审核申请加入的成员。</p>
					<p>(4) 邀请成员，指管理员通过邮箱邀请用户加入团队，支持邮箱的批量导入。</p>
					<p>(5) 导出文档，管理员可以选择将团队文档导出为ZIP格式或ePub格式。</p>
				</div>

			</div>
			<div class="clear"></div>
		</div>
		<div class="pane hidden" id="ddl-qa">
			<ol>
				<li>
					<a href="javascript:void(0);" class="question">团队文档库是免费使用的吗？</a>
					<div class="content">
					<p>团队文档库个人空间初始容量为10G。空间不足5G时，用户可以免费扩容 。<br/>
					用户可以免费创建10个团队空间，每个团队的免费容量是10G。团队空间扩容，每50GB/20个成员，1000元/月</p></div>
				</li>
				<li>
					<a href="javascript:void(0);" class="question">团队空间有没有人数限制？</a>
					<div class="content"><p>目前没有人数限制，可以任意邀请用户加入团队。</p></div>
				</li>
				<li>
					<a href="javascript:void(0);" class="question">团队空间和个人空间有什么区别？</a>
					<div class="content"><p>
						云端文件可存储在团队空间或个人空间中。团队空间是由团队成员组成的共享空间，可多人协作。个人空间是私人空间，在该空间中存放的文档只有自己可见，可以作为用户便捷的云端硬盘。</p></div>
				</li>
				
				<li>
					<a href="javascript:void(0);" class="question">什么是个人空间（同步版Beta）？</a><span class="new-sub">new</span>
					<div class="content"><p>
						个人空间（同步版Beta）是具有同步功能的个人私密空间。使用团队文档库Windows客户端，可以在电脑上同步这个空间下的文件。</p></div>
				</li>
				<li>
					<a href="javascript:void(0);" class="question">个人空间（同步版Beta）和个人空间有什么区别？</a><span class="new-sub">new</span>
					<div class="content"><p>
						个人空间（同步版Beta）是新推出的具有同步功能的私密的个人空间。使用团队文档库Windows客户端，可以在电脑上同步这个空间下的文件。个人空间不具备同步功能。个人空间（同步版Beta）的初始容量是20G，个人空间的容量是10G。用户在原个人空间中的文件数据不受影响。</p></div>
				</li>
				<li>
					<a href="javascript:void(0);" class="question">如何使用团队文档库Windows客户端？</a><span class="new-sub">new</span>
					<div class="content"><p>
						安装团队文档库Windows客户端，在安装时设置一个同步文件夹。你可以拖拽文件到这个文件夹内，客户端将对这个文件夹里的文件和服务器上的文件进行自动同步。您不必在每次更新文件后都再把文件上传一次，只要开着客户端，这些操作都会被客户端自动完成，省时又省心。所有的文件都会同步到个人空间（同步版Beta）中。</p></div>
				</li>
				<li>
					<a href="javascript:void(0);" class="question">团队文档库Windows客户端最多可以在几台电脑上运行？</a><span class="new-sub">new</span>
					<div class="content"><p>
						不限制同时运行同一个帐户的电脑数量，您只要在任意一台电脑上对文件进行操作，其他电脑上都会做同样的操作，大大节省用户的工作量。</p></div>
				</li>
				<li>
					<a href="javascript:void(0);" class="question">怎么才能知道我的文件有没有被成功同步？</a><span class="new-sub">new</span>
					<div class="content"><p>
						为了方便您了解文件的同步状态，桌面客户端在同步目录下对文件进行了标示，带有绿色对勾的文件，是已经同步完成的；带有蓝色循环箭头的文件，是正在同步过程中的；带有红色叉号的文件是同步出问题的；带有灰色减号符合的文件是尚未同步。</p></div>
				</li>
				<li>
					<a href="javascript:void(0);" class="question">团队文档库Windows客户端，怎样切换或者注销账户？</a><span class="new-sub">new</span>
					<div class="content"><p>
						打开偏好设置，进入到账户的tab内，点击“断开计算机连接”，即可注销该账户。再重新启动团队文档库Windows客户端，可以用其他账号登录实现切换账号。<br/>
						<img src="${contextPath}/jsp/aone/images/ddlQA-PC-changeuser.png" class="newImg"/> </p></div>
				</li>
				<li>
					<a href="javascript:void(0);" class="question">如何同步eml类型文件？</a><span class="new-sub">new</span>
					<div class="content"><p>
						eml类型文件只会在初次使用的时候同步一次，如果后续对eml类型的文件内容进行了修改，不会再同步。用户可以通过重命名文件，客户端则会再次进行同步。该情况只是针对于eml类型的文件，其他类型的文件均可正常同步。<br/></p></div>
				</li>
				 
				<li>
					<a href="javascript:void(0);" class="question">用户能不能设定进入团队后首先进入的页面</a>
					<div class="content"><p>可以。管理员在团队设置中的“默认首页”一栏，可以定制默认首页。</p></div>
				</li>
				<li>
					<a href="javascript:void(0);" class="question">用户能不能自己设定登录文档库后首先进入哪个团队？</a>
					<div class="content"><p>可以。在首页设置中的“个人偏好”一栏，可以定制默认首页。</p></div>
				</li>

				<li>
					<a href="javascript:void(0);" class="question">一个人可以属于多个团队吗？</a>
					<div class="content"><p>可以。用户可以创建多个团队，也可以加入其他人创建的团队。团队之间相互独立。</p></div>
				</li>
				<li>
					<a href="javascript:void(0);" class="question">团队文档库中，有哪些内容是全团队共享的，哪些内容是个性化的？</a>
					<div class="content"><p>所有的文档、文档的标签信息以及团队动态消息都是全团队共享的。星标文档、登录后进入的默认首页是每位用户根据自己的使用情况设定的。</p></div>
				</li>
				<li>
					<a href="javascript:void(0);" class="question">能大批量上传文件吗？</a>
					<div class="content"><p>可以。一次可上传多个文件。</p></div>
				</li>
				<li>
					<a href="javascript:void(0);" class="question">如何下载科研在线客户端？</a>
					<div class="content"><p>
						1) iOS版，在App Store中搜索“科研在线”进行查询，在结果列表中点选“科研在线”，免费下载安装。<br /> 2)
						Android版，<a href="http://www.escience.cn/apks/ddl-latest.apk">点击此处下载</a>
					</p></div>
				</li>
				<li>
					<a href="javascript:void(0);" class="question">我有问题该如何反馈或求助？</a>
					<div class="content"><p>
						您可以通过意见反馈、email和电话与我们交流<br /> 意见反馈在每个网页的右侧：<a
							href="http://iask.cstnet.cn/?/home/explore/category-11">我们倾听您的意见</a><br />
						邮件咨询：<a href="mailto:vlab@cnic.cn">vlab@cnic.cn</a><br />
						客服热线：010-58812378<br /> 新浪微博：<a href="http://e.weibo.com/dcloud"
							target="_blank">http://e.weibo.com/dcloud</a><br /> 腾讯微博：<a
							href="http://t.qq.com/keyanzaixian" target="_blank">http://t.qq.com/keyanzaixian</a><br />
						QQ咨询：<a class="link-block qq"
							href="tencent://message/?uin=2813954364&amp;Uin=2813954364&amp;Site=QQ咨询&amp;Menu=yes"
							target="_blank">2813954364</a>
					</p></div>

				</li>
			</ol>
		</div>
		<div class="pane hidden" id="history">
		<h1>更新记录</h1>
		<h4>2014年9月3日</h4>
			<ul>
				<li>发布科研在线团队文档库4.4.4（DDL 4.4.4）</li>
				<li>支持个人空间同步版跟其它空间文件的双向复制</li>				
			</ul>
		<h4>2014年7月11日</h4>
			<ul>
				<li>发布科研在线团队文档库4.4.3（DDL 4.4.3）</li>
				<li>加入公开链接分享。可以方便让所有人查看文件，无需登录团队文档库。</li>				
			</ul>
		<h4>2014年6月5日</h4>
			<ul>
				<li>发布科研在线团队文档库4.4.1（DDL 4.4.1）</li>
				<li>上传文件支持拖拽到窗口上传，上传文件更方便</li>		
				<li>公开团队加入搜索功能</li>	
				
			</ul>
		<h4>2014年5月23日</h4>
			<ul>
				<li>发布科研在线团队文档库4.4.0（DDL 4.4.0）</li>
				<li>增加个人空间（同步版Beta）</li>		
				<li>发布Windows桌面客户端，<a href="https://update.escience.cn/download/ddl_1.1.1_Beta_win32_setup.exe">下载试用</a></li>	
				<li>发布加密客户端，<a href="http://update.escience.cn/downloadVersion/13">下载试用</a></li>	
			</ul>
		<h4>2014年4月11日</h4>
			<ul>
				<li>发布科研在线团队文档库4.2.1（DDL 4.2.1）</li>
				<li>个人空间，增加用户手工扩容功能。空间剩余不足5G，可手工扩容。</li>		
			</ul>
		<h4>2014年3月18日</h4>
			<ul>
				<li>发布科研在线团队文档库4.2.0（DDL 4.2.0）</li>
				<li>对Office系列文档增加Office预览模式</li>		
				<li>支持中科院邮件系统的邮件附件在文档库中预览功能（现部分测试用户可使用）</li>		
	
			</ul>
		<h4>2014年2月27日</h4>
			<ul>
				<li>发布科研在线团队文档库4.1.2（DDL 4.1.2）</li>
				<li>编辑器支持IE11和IE10浏览器</li>		
	
			</ul>
		<h4>2014年1月24日</h4>
			<ul>
				<li>发布科研在线团队文档库4.1.1（DDL 4.1.1）</li>
				<li>图片加入左右旋转 </li>
				<li>图像评论区可显示和隐藏 </li>
				<li>键盘上下左右键或鼠标滚轮控制图片浏览翻看图片 </li>
				<li>文件夹内的子资源修改后，文件夹更新修改时间 </li>
				<li>修复Bug</li>			
		
				
			</ul>
		   <h4>2013年12月13日</h4>
			<ul>
				<li>发布科研在线团队文档库4.1.0（DDL 4.1.0）</li>
				<li>改善图片浏览，增加图片浏览相册模式</li>
				<li>新增缩略图模式和列表模式切换</li>
				<li>增加空间容量统计</li>
				<li>增加空间容量的上限，团队免费空间容量为10G</li>
				<li>标签支持多个标签过滤</li>
				<li>支持oAuth登录</li>
				<li>改进过滤查找和插入文档的搜索</li>
				<li>协作文档页面较长时，增加文档操作快捷按钮</li>
				
			</ul>
			<h4>2013年11月21日</h4>
			<ul>
				<li>发布科研在线团队文档库4.0.1（DDL 4.0.1）</li>
				<li>文档复制支持跨团队复制</li>
				<li>添加和插入相关文档时，支持从本地上传文件</li>
				<li>添加相关文档，改为双向关联</li>
				<li>协作文档支持全屏查看</li>
				<li>优化公开团队页面，支持分页浏览</li>
				<li>修复网页收藏的文档无法正确保存的Bug</li>
				
			</ul>
			<h4>2013年11月8日</h4>
			<ul>
				<li>发布科研在线团队文档库4.0.0（DDL 4.0.0）</li>
				<li>更名为科研在线团队文档库</li>
				<li>新增文件夹文档归类方式。现在可以方面的建立文件夹，分类管理文档更简单</li>
				<li>页面改为协作文档，类型为ddoc。团队中的文档包括在线创建的协作文档和上传的文件</li>
				<li>支持文件夹和文档的移动、复制、重命名和删除</li>
				<li>支持添加相关文档，在协作文档中插入其他文档链接和插入图片</li>
				<li>重新设计文档导航，团队文档和个人浏览视角相结合</li>
				<li>重新设计前端界面和优化界面交互，采用扁平化的设计，更简洁使用</li>
			</ul>
			<h4>2013年8月9日</h4>
			<ul>
				<li>发布科研在线文档库3.0.8（DDL 3.0.8）</li>
				<li>增加与VMT的团队信息同步更新</li>
			</ul>
			<h4>2013年7月12日</h4>
			<ul>
				<li>发布科研在线文档库3.0.6（DDL 3.0.6）</li>
				<li>增加@的拼音提示</li>
				<li>增加移动客户端的二维码下载提示</li>
				<li>修复Bug</li>
			</ul>
			<h4>2013年7月3日</h4>
			<ul>
				<li>发布科研在线文档库3.0.5（DDL 3.0.5）</li>
				<li>手机访问首页时，弹出移动客户端下载提示</li>
				<li>改进首页显示</li>
				<li>增加移动客户端的API</li>
			</ul>
			<h4>2013年6月28日</h4>
			<ul>
				<li>发布科研在线文档库3.0.4（DDL 3.0.4）</li>
				<li>文件在线预览升级</li>
				<li>发布Android手机客户端 3.0.0（<a
					href="http://www.escience.cn/apks/ddl-latest.apk" target="_blank"
					title="下载Android客户端"><span>获取</span></a>）
				</li>
			</ul>
			<h4>2013年6月25日</h4>
			<ul>
				<li>发布科研在线文档库3.0.3（DDL 3.0.3）</li>
				<li>个人空间的团队名称不再允许手动修改</li>
			</ul>
			<h4>2013年6月18日</h4>
			<ul>
				<li>发布科研在线文档库3.0.2（DDL 3.0.2）</li>
				<li>新用户和新建团队空间，增加示例文档</li>
				<li>提高文件下载性能</li>
				<li>修复中科院邮件系统的文档库附件功能</li>
			</ul>
			<h4>2013年6月9日</h4>
			<ul>
				<li>发布科研在线文档库3.0.1（DDL 3.0.1）</li>
				<li>评论增加@通知功能</li>
				<li>已删除文件支持找回</li>
				<li>版本管理页面增加版本恢复入口</li>
				<li>管理员审核和用户申请加入公开需审核团的邮件通知</li>
				<li>通讯录增加管理员标识</li>
				<li>修复支持移动客户端(Android)API</li>
			</ul>
			<h4>2013年5月17日</h4>
			<ul>
				<li>发布科研在线文档库3.0.0p1（DDL 3.0.0p1）</li>
				<li>修改中科院邮件系统的文档库附件功能（现部分测试用户可使用）</li>
				<li>修复Bug</li>
				<li>修复支持iOS的API</li>
			</ul>
			<h4>2013年5月9日</h4>
			<ul>
				<li>发布科研在线文档库3.0.0（DDL 3.0.0）</li>
				<li>支持中科院邮件系统的文档库附件功能（现部分测试用户可使用）</li>
				<li>修复Bug</li>
			</ul>
			<h4>2013年4月22日</h4>
			<ul>
				<li>发布科研在线文档库2.1.12（DDL 2.1.12）</li>
				<li>个人资料里支持邮箱的修改</li>
				<li>优化编辑页面版本比较</li>
				<li>修改组合名时，支持回车即保存</li>
				<li>修复支持iOS的API</li>
				<li>发布iPhone客户端1.2.2（<a
					href="https://itunes.apple.com/cn/app/ke-yan-zai-xian/id495109931"
					target="_blank">获取</a>）
				</li>

			</ul>

			<h4>2013年4月9日</h4>
			<ul>
				<li>发布科研在线文档库2.1.11（DDL 2.1.11）</li>
				<li>修改文件分享给外部成员，下载后版本不对的Bug</li>
				<li>发布iPhone客户端1.2.1（<a
					href="https://itunes.apple.com/cn/app/ke-yan-zai-xian/id495109931"
					target="_blank">获取</a>）
				</li>

			</ul>

			<h4>2013年3月29日</h4>
			<ul>
				<li>发布科研在线文档库2.1.10（DDL 2.1.10）</li>
				<li>加入中国科技网通行证单点登录</li>
				<li>改善文档搜索结果排序</li>
				<li>修改了对iOS的API支持</li>
			</ul>
			<h4>2013年3月13日</h4>
			<ul>
				<li>发布科研在线文档库2.1.9（DDL 2.1.9）</li>
				<li>修复文件名过长在IE浏览器下会产生乱码或文件名被改变</li>
				<li>加入编辑冲突提示</li>
			</ul>
			<h4>2013年2月21日</h4>
			<ul>
				<li>发布科研在线文档库2.1.8（DDL 2.1.8）</li>
				<li>修改组合全是图片的时候无法展示缩略图以及切换到缩略图的bug</li>
				<li>修改了对手机API的支持</li>
			</ul>
			<h4>2013年1月22日</h4>
			<ul>
				<li>发布科研在线文档库2.1.7（DDL 2.1.7）</li>
				<li>提高文件上传下载性能</li>
				<li>Bug修复</li>
			</ul>
			<h4>2013年1月15日</h4>
			<ul>
				<li>发布科研在线文档库2.1.5（DDL 2.1.5）</li>
				<li>支持IE10下的页面编辑</li>
				<li>文档分享加入全选收件人功能</li>
				<li>Bug修复</li>
				<li>发布iPhone客户端1.1.2（<a
					href="https://itunes.apple.com/cn/app/ke-yan-zai-xian/id495109931"
					target="_blank">获取</a>）
				</li>
			</ul>
			<h4>2013年1月7日</h4>
			<ul>
				<li>发布科研在线文档库2.1.4（DDL 2.1.4）</li>
				<li>支持团队通讯录和个人通讯录的表格排序，可按姓名、邮箱等排序查看联系人</li>
				<li>优化文档库的页面和文件分享功能</li>
				<li>修复标签管理和添加</li>
				<li>更新移动客户端接口</li>
			</ul>
			<h4>2012年12月28日</h4>
			<ul>
				<li>发布科研在线文档库2.1.3（DDL 2.1.3）</li>
				<li>支持用户接受团队邀请时，可以使用接受邀请邮箱以外的邮箱注册或登录</li>
				<li>团队动态中增加显示已删除的文档，并用删除线标记</li>
				<li>分享邮件里的发件人地址改为分享人的邮件地址</li>
				<li>改善分享选择收件人功能，支持按姓名或邮箱检索以及按姓名拼音首字母分类显示收件人</li>
				<li>改善文档搜索结果排序</li>
			</ul>
			<h4>2012年12月13日</h4>
			<ul>
				<li>发布科研在线文档库2.1.2（DDL 2.1.2）</li>
				<li>在账户名的菜单内增加用户的科研主页链接</li>
				<li>用户访问可查看权限的团队时，组合页面左侧解散和删除组合区域不予显示</li>
				<li>Bug修复</li>
			</ul>

			<h4>2012年12月11日</h4>
			<ul>
				<li>发布科研在线文档库2.1.1（DDL 2.1.1）</li>
				<li>文档删除增加权限，只有文档创建人或团队管理员可以删除文档</li>
				<li>支持感兴趣的页面推荐</li>
				<li>预览文件时，在新窗口打开预览页面</li>
				<li>增加团队文档推荐阅读和按团队搜索的用户指南</li>
				<li>改善页面编辑的word粘贴功能</li>
				<li>改善文档搜索的结果排序</li>
			</ul>

			<h4>2012年11月23日</h4>
			<ul>
				<li>发布科研在线文档库2.1.0（DDL 2.1.0）</li>
				<li>支持文档在不同团队间的复制</li>
				<li>邮件通知与邮件提醒</li>
				<li>跨团队的搜索</li>
				<li>团队文档推荐阅读</li>
				<li>动态/消息的查看过后的状态变更</li>
				<li>顶部团队导航顺序的调整</li>
				<li>文档按更新时间排列查找困难，支持按文件名顺序展示</li>
				<li>团队权限增加至三种："完全保密","公开需审核","完全公开"</li>
				<li>支持图片缩略图预览</li>
			</ul>
			<h4>2012年8月8日</h4>
			<ul>
				<li>发布科研在线文档库2.0.0（DDL 2.0.0）</li>
				<li>支持文件、页面、组合的批量删除</li>
				<li>导航栏的自定义排序</li>
				<li>支持搜索结果的标签添加与管理</li>
				<li>支持搜索结果的星标添加与管理</li>
				<li>支持新建/编辑页面、上传文件时的标签添加与管理</li>
				<li>支持多选文档的标签整理</li>
				<li>支持标签的检索与自动完成</li>
				<li>标签检索支持按拼音进行检索</li>
				<li>支持文件热度，统计文件查阅及下载次数</li>
				<li>左侧菜单支持按“所有文档”进行分类检索</li>
				<li>bug修改与界面优化</li>
			</ul>
			<h4>2012年6月8日</h4>
			<ul>
				<li>发布版本DDL 1.0.0（文档库）</li>
				<li>基于标签的内容组织与管理</li>
				<li>支持个人常用及历史记录</li>
				<li>支持星标个人文档收藏</li>
				<li>支持word、ppt等的在线浏览</li>
				<li>发布网页内容采集工具</li>
				<li>支持内容组合成专题（图片集等），支持多种组合阅读模式</li>
				<li>增加个人偏好，可设置不同登录后的默认首页</li>
				<li>增加用户新手引导</li>
			</ul>
			<h4>2012年3月31日</h4>
			<ul>
				<li>发布版本DCT 6.1.0</li>
				<li>发布iPhone手机客户端（<a id="iphone"
					href="http://itunes.apple.com/cn/app/ke-yan-zai-xian/id495109931"
					target="_blank" title="连接到App Store安装应用"><span>获取</span></a>）
				</li>
				<li>支持PDF文件的在线预览（需要最新的浏览器版本）</li>
				<li>支持通讯录的批量导入导出（支持Outlook, Outlook Express, Thunderbird,
					foxmail）</li>
				<li>支持团队成员退出</li>
				<li>页面创建入口统一</li>
				<li>支持页面浏览模式下的文件上传与下载</li>
			</ul>
			<h4>2011年11月28日</h4>
			<ul>
				<li>发布最新版本：科研在线2011版，版本号 DCT 6.0.0</li>
				<li>支持个人空间和多团队协作，整合消息模块</li>
				<li>增加个人通讯录与团队通讯录的整合</li>
				<li>改善编辑器功能，具备清除格式和使用HTML源代码编写能力</li>
				<li>优化集合首页信息呈现方式，允许对页面、文件分别筛选查看</li>
				<li>整合快捷工具栏、消息通知和个人账户控制工具栏</li>
			</ul>

			<h4>2011年9月22日</h4>
			<ul>
				<li>发布Android手机客户端，支持团队更新和集合内容的查看与搜索</li>
				<li>集合首页增加网格模式，支持对有关联的内容进行整理和呈现</li>
				<li>新增快速上传、快速创建页面功能和快捷工具栏</li>
				<li>实现文件与页面混排功能，支持文件的版本更新</li>
				<li>改进编辑页面时的锁定、过期解锁、自动保存和恢复机制</li>
				<li>改进“关注”功能，自动对自己创建的页面进行关注</li>
			</ul>
			<h4>2011年7月8日</h4>
			<ul>
				<li>发布重要版本 DCT 5.1.49a</li>
				<li>增加全局内容的搜索功能，支持页面顶部快速搜索和详细搜索</li>
				<li>重构分享和关注功能</li>
				<li>改进编辑冲突处理机制，引入用户信息</li>
				<li>增加邀请加入团队机制和相应管理、配置功能</li>
				<li>新增用户注册激活机制</li>
				<li>新增创建多个团队功能及相应的管理和切换功能</li>
				<li>新增图片和附件的区分和描述页，改进E2编辑器对文件和图片上传、嵌入的机制</li>
				<li>改进团队首页设计，支持快速查看团队内容和团队通讯录</li>
				<li>更新系统框架，采用新页面组织结构：“团队-集合-页面”三层结构组织内容；对界面框架进行相应调整</li>
				<li>升级编辑器，面向内容语义简化编辑工具，调整显示模式以适应长内容的编辑需要</li>
			</ul>
			<h4>2011年3月17日</h4>
			<ul>
				<li>发布A1第一版原型（即当前版本科研在线的内部原型），在内容管理体系中融入社会化元素</li>
				<li>增加对内容的关注机制、内容分享机制、评论和回复机制、更新推送和通知中心</li>
			</ul>
		</div>
	</div>
	<div id="footer">
		<jsp:include page="/Version.jsp"></jsp:include>
	</div>
</body>
</html>