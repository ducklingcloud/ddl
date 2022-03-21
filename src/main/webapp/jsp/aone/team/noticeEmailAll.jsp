<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<script type="text/javascript" src="${contextPath}/jsp/aone/js/uiLib-jQuery.js"></script>
<script type="text/javascript" src="${contextPath}/jsp/aone/js/global.js"></script>
<fmt:setBundle basename="templates.default" />
<div id="feedSelector" class="filterHolder">
	<ul class="filter">
		<li><a view="defaultTag" href="#">邮件通知</a></li>
	</ul>
</div>
<div>
	<div id="default-home-page-display" class="content-menu-body">
		<table id="profileTable" class="ui-table-form"  style="width:575px;">
			<tr class="titleRow">
				<th width="520" style=" vertical-align:bottom;">当发生以下事件，团队文档库将会以邮件的方式通知您</th>
				<th width="100" style="text-align:right; font-weight:normal;"><a href="<vwb:Link context='dashboard' format='url'/>?func=noticeEmail&showDetailNoticeEmail=true">按团队设置</a></th>
			</tr>
			<tr>
				<td id="display-rtm" colspan="3">
					<input type="checkbox"  <c:if test="${allChecked ||allNull}">checked="checked"</c:if> id="shareNotice" name="shareNotice"/> 有新的分享 (实时发送)
				</td>
			</tr>
			<tr>
				<td id="display-rtm" colspan="3">
					<input type="checkbox" <c:if test="${allChecked||allNull}">checked="checked"</c:if> id="allNotice" name="allNotice" /> 每周动态汇总 (周一早6点发送)
				</td>
			</tr>
			<tr>
				<td id="display-rtm" colspan="3">
						<input id="submitEmailNotice" class="largeButton small but-color-commom"  type="button" value="确定"/>
						<span id="message" style="display:none"><font color="red">设置成功！</font></span>
				</td>
			</tr>
		</table>
	</div>
</div>
<script type="text/javascript">
$(document).ready(function(){
	$("#submitEmailNotice").live("click",submitNoticeEmail)
	function submitNoticeEmail(){
		$.ajax({
			  type: "POST",
			  url: "<vwb:Link context='dashboard' format='url'/>?func=emailNoticeAll",
			  data:{allNotice:$("#allNotice").attr("checked")?true:false,
				    shareNotice:$("#shareNotice").attr("checked")?true:false},
			  success:function(data){
				  $("#message").show("slow");
				  setTimeout(function(){
					  $("#message").hide("slow");		
				},2000);
			  },
				statusCode:{
					450:function(){alert('会话已过期,请重新登录');},
					403:function(){alert('您没有权限进行该操作');}
				}
			});
	}
});
</script>
