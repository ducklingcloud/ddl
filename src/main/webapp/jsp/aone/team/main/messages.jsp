<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/uiLib.css" type="text/css" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/aone.css" type="text/css" />
<script type="text/javascript">
	var url = "<vwb:Link format='url' context='myspace'/>?func=updateRecommendStatus";
	
	//ajaxRequest(url,null,afterUpdateRecommendStatus);
	
	function afterUpdateRecommendStatus(data){
		//TODO
	};
	
</script>
<div id="messageFeed" class="content-menu-body">
	<jsp:include page="/jsp/aone/team/main/displayRecs.jsp"></jsp:include>
</div>

	<!-- for dialog background -->
	<div class="ui-dialog-cover"></div>
