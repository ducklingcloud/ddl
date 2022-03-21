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
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<link href="${contextPath}/jsp/aone/css/index.css" rel="stylesheet" type="text/css"/>
<link href="${contextPath}/jsp/aone/css/index-may2012.css" rel="stylesheet" type="text/css"/>
<link href="${contextPath}/images/favicon.ico" rel="shortcut icon" type="image/x-icon" />
<script type="text/javascript" src="${contextPath}/scripts/jquery/jquery-1.8.3.min.js"></script>
<script type="text/javascript">
$(document).ready(function(){
	$("#escienceMenu").mouseenter(function(){
		$("#es-pullDownMenu").show();
	});
	$("#escienceMenu").mouseleave(function(){
		$("#es-pullDownMenu").hide();
	});
	$(".active").removeClass("active");
	$("#ddlNav-updateLog").parent("li").addClass("active");
});
</script>
<title>科研在线-文档库</title>
</head>
<body class="texure">
	<div class="ui-wrap" >
		<jsp:include page="/ddlHeader.jsp"></jsp:include>
		
		<div class="content" id="history">
			<h1>更新记录</h1>
			<h4>2013年8月9日</h4>
			<ul>
		        <li>发布科研在线文档库3.0.8（DDL 3.0.8）</li>
		        <li>增加与VMT的团队信息同步更新 </li>	        
			 </ul>
			<h4>2013年7月12日</h4>
			<ul>
		        <li>发布科研在线文档库3.0.6（DDL 3.0.6）</li>
		        <li>增加@的拼音提示 </li>
		        <li>增加移动客户端的二维码下载提示</li>
		        <li>修复Bug</li>
			 </ul>
			<h4>2013年7月3日</h4>
			<ul>
		        <li>发布科研在线文档库3.0.5（DDL 3.0.5）</li>
		        <li>手机访问首页时，弹出移动客户端下载提示 </li>
		        <li>改进首页显示 </li>
		        <li>增加移动客户端的API</li>
			 </ul>
			<h4>2013年6月28日</h4>
			<ul>
		        <li>发布科研在线文档库3.0.4（DDL 3.0.4）</li>
		        <li>文件在线预览升级</li>
		        <li>发布Android手机客户端 3.0.0（<a href="http://www.escience.cn/apks/ddl-latest.apk" target="_blank" title="下载Android客户端"><span>获取</span></a>）</li>
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
			    <li>发布iPhone客户端1.2.2（<a href="https://itunes.apple.com/cn/app/ke-yan-zai-xian/id495109931" target="_blank">获取</a>）</li>	
			    
			</ul>
			
			<h4>2013年4月9日</h4>
			<ul>
			    <li>发布科研在线文档库2.1.11（DDL 2.1.11）</li>
			    <li>修改文件分享给外部成员，下载后版本不对的Bug</li>
			    <li>发布iPhone客户端1.2.1（<a href="https://itunes.apple.com/cn/app/ke-yan-zai-xian/id495109931" target="_blank">获取</a>）</li>
			    
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
			    <li>发布iPhone客户端1.1.2（<a href="https://itunes.apple.com/cn/app/ke-yan-zai-xian/id495109931" target="_blank">获取</a>）</li>
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
				<li>发布版本DDL 1.0.0（文档库）
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
				<li>发布iPhone手机客户端（<a id="iphone" href="http://itunes.apple.com/cn/app/ke-yan-zai-xian/id495109931" target="_blank" title="连接到App Store安装应用"><span>获取</span></a>）</li>
				<li>支持PDF文件的在线预览（需要最新的浏览器版本）</li>
				<li>支持通讯录的批量导入导出（支持Outlook, Outlook Express, Thunderbird, foxmail）</li>
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
		
		<jsp:include page="/help/footer.jsp"></jsp:include>
		<div id="footer">
			<jsp:include page="/Version.jsp"></jsp:include>
		</div>
	</div>

</body>
</html>