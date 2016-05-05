<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<fmt:setBundle basename="templates.default" />
<script type="text/javascript">
$(document).ready(function(){
	var navHeight = $('#navigation').height();
	var min = $('#expand').height();
	
	var fullCol = new PullDownMenu('fullCollection');
	fullCol.register('#expandCollection');
	fullCol.setPosition('#expandCollection', 'left');
	
});

</script>

<div id="colBody">
</div>
<div id="createCollection">
</div>
<div id="expand">
</div>
<div id="fullCollection" class="pulldownMenu">
</div>

<div class="clear"></div>