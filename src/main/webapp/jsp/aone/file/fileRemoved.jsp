<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<link href="${contextPath}/jsp/aone/css/error.css" rel="stylesheet" type="text/css"/>
<fmt:setBundle basename="templates.default" />

<div class="ui-wrap">
	<div class="error-center">
		<h3>对不起，您请求的文件已被删除！</h3>
		<hr/>
		<c:choose>
			<c:when test="${recoverFlag}">
				<p style="line-height: 1.7em;font-size: 13px;color: #333">我们为您做了备份，<a class="recoverFile">
					<input type="hidden" name="rid" value="${rid }"/>点此恢复该文档。
						</a></p>
				<p style="line-height: 1.7em;font-size: 13px;color: #333">已删除的页面不会出现在团队空间里。恢复后即可立即浏览。</p>
			</c:when>
			<c:otherwise>
				<a href="<vwb:Link context='teamHome' format='url'/>">跳转到团队首页</a>
			</c:otherwise>
		</c:choose>
	</div>
</div>
<script type="text/javascript">
$(document).ready(function(){
	$(".recoverFile").live('click',function(){
		var aa= window.confirm("您确定要恢复此文件吗？\n恢复后，团队的其他成员可以看到该文档");
		if(aa){
			var rid = $(this).find("input[name='rid']").val();
			var url = site.getURL("teamHome");
			var params ={"func":"recoverResource","rid":rid,"itemType":"DFile"};
			$.ajax({
				url:url,
				type:'post',
				data:params,
				dataType:'json',
				success:function(data){
					if(data.status){
						window.location.href=data.redirectURL;
					}else{
						alert(data.message);				
					}
				}
			});
		}
		
	});
});

</script>
