<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div id="visitor" class="a1-hideout pulldownMenu popover fade bottom in" style="width:230px; position:absolute;">
	<strong>最近看过此页面的人</strong>
	<vwb:Visitor boxStyle="a1-visitor" length="9" rid="${rid }" />
	<p class="a1-hideout-control">
		<a class="closeThis"><span>关闭</span> </a>
	</p>
</div>
<script>
	$(document).ready(function(){
		$(".closeThis").live("click",function(){
			$("#visitor").hide();
		});
	});
</script>
