<%@ page language="java" pageEncoding="utf-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<div id="shortcutShow" style="display: none; height:1%;overflow:hidden;">
	<span id="ShortcutSpanTitle">推荐阅读：</span>
	<div class="showorhideSC">
		<ul id="shortcutView" style="height:100%;overflow:hidden;">
			<c:if test="${not empty resources }">
				<c:forEach var="item" items="${resources }" >
					<li class="shortcutItem">
						<a title="${item.resourceTitle}" href="${item.resourceURL}" style="color:${item.color}" target="_blank">${item.resourceTitle}</a> 
					</li>
				</c:forEach>
			</c:if>
		</ul>
	</div>
	<a class="openall" id="moreShortcuts">展开</a>
	<input type="button" id="addShortcutButton" class="addShortcut config" title="管理推荐阅读">
</div>
	
<script type="text/javascript">

$(document).ready(function(){
	$(".openall").live("click",function(){
			$(".showorhideSC").css({"overflow":"visible","height":"auto"});
			$(this).html("收起");
			$(this).removeClass("openall").addClass("closeall");
	});
	$(".closeall").live("click",function(){
		$(".showorhideSC").css({"overflow":"hidden","height":"2.2em"});
		$(this).html("展开");
		$(this).removeClass("closeall").addClass("openall");
	})
	
	showOpen = function (){
		var length =  0;
		
		$.each($(".shortcutItem"),function(index,item){
			 var l = $(item).outerWidth();
			length=length + l;
		});
//		var spanLength = $("#ShortcutSpanTitle").outerWidth();
		var body=$(".showorhideSC").outerWidth();
//		alert("spanLength:"+spanLength+"length:"+length+"body:"+body);
		if(length>(body)){
			$("#moreShortcuts").show();
		}else{
			$("#moreShortcuts").hide();
		}
	}
	
});
</script>
