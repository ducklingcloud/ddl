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

<div id="home"><vwb:Link context="teamHome" jsp="">首页</vwb:Link></div>
<div id="colBody">
	<vwb:CollectionTopMenu/>
</div>
<div id="createCollection">
	<a href="<vwb:Link context="editCollections" format="url"/>#create">创建集合</a>
</div>
<div id="expand">
	<a id="expandCollection" folded="true" class="iconLink expandY">更多</a>
</div>
<div id="fullCollection" class="pulldownMenu">
	<div id="editCollections">
		<a href="<vwb:Link context="editCollections" format="url"/>" class="iconLink config">编辑</a>
	</div>
	<vwb:CollectionTopMenu/>
</div>

<div class="clear"></div>