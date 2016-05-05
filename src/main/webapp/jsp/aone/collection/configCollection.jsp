<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<script type="text/javascript">
$(document).ready(function(){
	function switchView(VIEW) {
		$('#config-tab ul.tab-list > li').removeClass('current');
		$('.config-content-sub').hide();
		var view = VIEW;
		
		switch (VIEW) {
			case 'pageTab':
			case 'shortcutTab':
			case 'authTab':
				$('div#config-' + VIEW).show();
				break;
			case 'basicTab':
			default:
				view = 'basicTab';
				$('div#config-basicTab').show();
		}
		$('#config-tab ul.tab-list a[href="#' + view + '"]').parent().addClass('current');
	}
	$('#config-tab ul.tab-list li a').click(function(){
		switchView($(this).attr('href').substring(1));
	});
	
	var hash = (window.location.hash)?window.location.hash:'';
	if (hash!='') {
		switchView(hash.substring(1));
	}
	else {
		switchView();
	}
});
</script>
	<div id="content-title">
		<div id="editTool">
			<ul id="toolGroup">
				<li><a href="<vwb:Link context="viewCollection" format="url" page="${collection.resourceId}"/>" class="toolReturn"><span>返回</span></a></li>
			</ul>
			<div class="decoLeft"></div>
		</div>
		<h1>设置集合：${collection.title}</h1>
	</div>
	<div id="config-tab">
		<ul class="tab-list">
			<li><a href="#basicTab">基本设置</a></li>
			<li><a href="#pageTab">管理集合</a></li>
			<li><a href="#shortcutTab">管理快捷</a></li>
			<li><a href="#authTab">管理权限</a></li>
		</ul>
	</div>
	<div id="config-content">
		<div id="config-basicTab" class="config-content-sub config-float" style="display:none">
			<jsp:include page="/jsp/aone/collection/adminBasic.jsp"/>
		</div>
		<div id="config-pageTab" class="config-content-sub" style="display:none">
			<jsp:include page="/jsp/aone/collection/adminPages.jsp"/>
		</div>
		<div id="config-shortcutTab" class="config-content-sub" style="display:none">
			<jsp:include page="/jsp/aone/collection/adminShortcuts.jsp"/>
		</div>
		<div id="config-authTab" class="config-content-sub config-float" style="display:none">
			<jsp:include page="/jsp/aone/collection/adminAuths.jsp"/>
		</div>
	</div>

