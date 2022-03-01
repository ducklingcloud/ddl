<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />

<div id="d-webpageCapture" class="content-menu-body" >
	<div id="wpc-copyLink">
		<h2>科研在线网页收藏工具</h2>
		<p>网页收藏工具可以用自动和手动选取的方式将您浏览的精彩网页内容快速保存到科研在线。</p>
		
		<h3>安装</h3>
		<p>将以下链接拖动到浏览器书签栏。
			<br/><span class="ui-text-note">或用右键点击将其保存到收藏夹。</span></p>
		<a id="webpageCaptureLink" href="javascript:var%20baseURL='${baseURL}';(function(){if(document.body&&!document.xmlVersion){var%20s=document.createElement('script');s.setAttribute('type','text/javascript');s.setAttribute('src','${baseURL}/dataCollect/dataCollect.js');s.setAttribute('charset','utf-8');document.body.appendChild(s);}})();"
			title="将此链接拖动到书签栏，或保存到收藏夹"
		>+科研在线</a>
	</div>
	
	<div id="wpc-intro">
		<h3>如何使用网页收藏工具？</h3>
		<h4>1. 打开网页，点击“保存到科研在线”</h4>
		<p>网页收藏工具将自动为您选取网页正文和标题。</p>
		<p><img src="${baseURL}/dataCollect/images/tutor-click.jpg" /></p>
		
		<h4>2. 手动调整选择范围</h4>
		<p>您也可以手动选取需要的网页段落进行收藏。</p>
		<p><img src="${baseURL}/dataCollect/images/tutor-select.jpg" /></p>

		<h4>3. 提交</h4>
		<p>选择要存入的团队和集合，点击“提交”，完成网页选中内容的收藏。</p>
		<p><img src="${baseURL}/dataCollect/images/tutor-panel.jpg" /></p>
	</div>
</div>

<script>
$(document).ready(function(){
	if ($.browser.msie && (parseInt($.browser.version, 10) < 9)) {
		$('#webpageCaptureLink').addClass('msie');
	}
});
</script>
