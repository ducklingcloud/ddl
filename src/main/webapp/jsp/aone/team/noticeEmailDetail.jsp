<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<script type="text/javascript" src="${contextPath}/jsp/aone/js/uiLib-jQuery.js"></script>
<script type="text/javascript" src="${contextPath}/jsp/aone/js/global.js"></script>

<fmt:setBundle basename="templates.default" />
<div id="feedSelector" class="filterHolder">
	<ul class="filter">
		<li><a view="defaultTag" href="#">邮件通知-按团队设置</a></li>
	</ul>
</div>
<p style="margin:30px 30px 0px 30px;font-family: \5FAE\8F6F\96C5\9ED1;">当发生以下事件，团队文档库将会以邮件的方式通知您
<c:if test="${!empty showDetailNoticeEmail }">
	<a id="cancel" onclick="javascript:window.history.back();" style="margin-left:250px">返回</a>
</c:if>
</p>
	<div id="default-home-page-display" class="content-menu-body">
		<table id="profileTable" class="ui-table-form"  style="width:575px;">
			<tr>
				<td style="padding-top:0;"><input type="checkbox" <c:if test="${allChecked||allNull }">checked="checked"</c:if> id='checkAll'/>全选</td>
				<td></td>
			</tr>
			<c:forEach items="${result}" var="item">
			<tr class="titleRow" >
				<th width="520" colspan="3" ><c:out value="${item.teamName }"/></th>
			</tr>
			<tr>
				<td id="display-rtm" colspan="3">
					<input class="singleCheck" type="checkbox" value="${item.tid }" <c:if test="${item.isShare||allNull }">checked="checked"</c:if>  name="shareNotice"/> 有新的分享(实时发送)
				</td>
			</tr>
			<tr>
				<td id="display-rtm" colspan="3">
					<input  class="singleCheck" type="checkbox" value="${item.tid }" <c:if test="${item.isAll||allNull }">checked="checked"</c:if> name="allNotice" /> 每周动态汇总 (周一早6点发送)
				</td>
			</tr> 
			</c:forEach>

			<tr>
				<td id="display-rtm" colspan="3">
						<input id="submitEmailNotice" class="largeButton small but-color-commom"  type="button" value="确定"/>
						<span id="message" style="display:none"><font color="red">设置成功！</font></span>
				</td>
			</tr>
		</table>
	</div>
<script type="text/javascript">

<c:forEach items="${result}" var="item">

</c:forEach>
$(document).ready(function(){
	var cache={};
	$(".singleCheck").each(function(index,item){
		cache[$(item).attr("name")+"_"+$(item).val()]=$(item).attr("checked");
	});
	$('.singleCheck').live("click",function(){
		var cacheKey=($(this).attr("name")+"_"+$(this).val());
		cache[cacheKey]=$(this).attr("checked");
		var isAllChecked=true;
		for(var i in cache){
			isAllChecked&=(cache[i]);
		}
		isAllChecked?$('#checkAll').attr("checked","checked"):$('#checkAll').removeAttr("checked");
	});
	$('#checkAll').live("click",function(){
		var flag=$(this).attr("checked");
		$("input[name=allNotice]").each(function(index,item){
			cache[$(item).attr("name")+"_"+$(item).val()]=flag;
			flag?$(item).attr("checked","checked"):$(item).removeAttr("checked");
		});
		$("input[name=shareNotice]").each(function(index,item){
			cache[$(item).attr("name")+"_"+$(item).val()]=flag;
			flag?$(item).attr("checked","checked"):$(item).removeAttr("checked");
		})
	});
	$("#submitEmailNotice").live("click",submitNoticeEmail);
	function submitNoticeEmail(){
		var param={allNoticeChecked:[],allNoticeUnChecked:[],shareNoticeChecked:[],shareNoticeUnChecked:[]};
		$("input[name=allNotice]").each(function(index,item){
			if($(item).attr("checked")){
				param.allNoticeChecked.push($(item).val());
			}else{
				param.allNoticeUnChecked.push($(item).val());
			}
		});
		$("input[name=shareNotice]").each(function(index,item){
			if($(item).attr("checked")){
				param.shareNoticeChecked.push($(item).val());
			}else{
				param.shareNoticeUnChecked.push($(item).val());
			}
		});
		$.ajax({
			  type: "POST",
			  url: "<vwb:Link context='dashboard' format='url'/>?func=emailNoticeDetail",
			  data:param,
			  success:function(data){
				  if(data=='false'||!data){
					  $("#cancel").hide();
				  	$("#message").show("slow");
				  	setTimeout(function(){
					  $("#message").hide("slow");		
					},2000);
				  }else{
					  window.location.href="<vwb:Link context='dashboard' format='url'/>?func=noticeEmail";
				  }
				  
			  },
				statusCode:{
					450:function(){alert('会话已过期,请重新登录');},
					403:function(){alert('您没有权限进行该操作');}
				}
			});
	}
});
</script>
