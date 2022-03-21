<%@ page language="java" pageEncoding="utf-8"%>
<%@ page import="net.duckling.ddl.common.*"%>
<%@taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%
		pageContext.setAttribute("contextPath", request.getContextPath());
		VWBContext.createContext(request,"error");
	%>

<div class="ui-clear"></div>
<a id="getFeedback" href="http://iask.cstnet.cn/?/home/explore/category-11" target="_blank">意见反馈</a>

<script type="text/javascript" src="${contextPath}/jsp/aone/js/backToTop-jQuery.js"></script>
<script type="text/javascript" src="${contextPath}/jsp/aone/js/uiLib-0.9.2-jquery.js?v=${aoneVersion}"></script>
<script type="text/javascript" src="${contextPath}/jsp/aone/js/lynx-jquery.js?v=${aoneVersion}"></script>
<script language="javaScript">
	$(document).ready(function(){
	
		//----------------------------------------------------//
		browserAlert();
		$('.msgFloat').mouseout(function(){
			var obj = $(this);
			var msgTimeout = setTimeout(function(){ obj.hide(); }, 1000);
			obj.attr('msgTimeoutId', msgTimeout);
		});
		$('.msgFloat').mouseover(function(){
			window.clearTimeout($(this).attr('msgTimeoutId'));
		});
		
		function placeDock() {
			if ($(window).width() < 1024) {
				$('body').addClass('narrowConfigure');
			}
		}
		
		$(window).resize(placeDock);
		placeDock();
		
		var backBox = new BackToTop('回顶部');	
	});
</script>