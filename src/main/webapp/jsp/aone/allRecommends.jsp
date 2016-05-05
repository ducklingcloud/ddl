<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/uiLib.css" type="text/css" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/aone.css" type="text/css" />
<script type="text/javascript" src="${contextPath}/jsp/aone/js/uiLib-jQuery.js"></script>
<script type="text/javascript" src="${contextPath}/jsp/aone/js/global.js"></script>
<script type="text/javascript">
$(document).ready(function(){
	
	//findAllUserLink('.user-link',"<vwb:Link format='url'/>","a=interest&ajaxMethod=getAllUserLink");
	
	$(".recommend-box").live('click',function(){
		prepareRecommend("<vwb:Link format='url'/>",$(this).attr('recommendPage'), $(this).attr("pageTitle"));
	});
	
});
</script>

<div class="ui-wrap">

	<jsp:include page="/jsp/aone/ms_left.jsp"></jsp:include>
	
	<div id="a1-content">
		<jsp:include page="/jsp/aone/team/main/displayRecs.jsp"></jsp:include>
	</div>
	<jsp:include page="/jsp/aone/recommend/addRec.jsp"></jsp:include>
	<jsp:include page="/jsp/aone/subscription/addSub.jsp"></jsp:include>
	<!-- for dialog background -->
	<div class="ui-dialog-cover"></div>
</div>

