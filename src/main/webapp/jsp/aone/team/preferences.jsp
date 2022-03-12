<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<script type="text/javascript" src="${contextPath}/jsp/aone/js/uiLib-jQuery.js"></script>
<script type="text/javascript" src="${contextPath}/jsp/aone/js/global.js"></script>
<fmt:setBundle basename="templates.default" />

<div id="feedSelector" class="filterHolder">
	<ul class="filter">
		<li class="chosen"><a view="homePage" href="#homePage">默认首页</a></li>
		<li><a view="defaultTag" href="#defaultTag">默认标签</a></li>
	</ul>
</div>
<div>
	<div id="default-tag-display" class="content-menu-body" style="display:none">
	<table id="profileTable" class="ui-table-form"  style="width:575px;">
		<tr>
			<td>
				<input type="checkbox" name="useNameTag" ${useNameTag==true?'checked=checked':'' } id="useNameTag" onclick="javascript:clickDefaultTag()"/><span style="font-family: \5FAE\8F6F\96C5\9ED1;">创建文档时，默认使用创建人姓名标签</span>
				
				<span class="ui-spotLight" id="info"></span>
			</td>
		</tr>
	</table>
	</div>
	<div id="default-home-page-display" class="content-menu-body">
		<table id="profileTable" class="ui-table-form"  style="width:575px;">
			<tr class="titleRow">
				<th  style=" vertical-align:bottom;" width="85">当前默认首页</th>
				<th width="520" style="text-align:right;"><input id="edit-prefrences" class="largeButton small but-color-commom"  type="button" value="修改"/></th>
			</tr>
			<tr>
				<th >默认进入：</th>
				<td id="display-rtm" colspan="3">
					<c:choose>
						<c:when test="${userPreferences.refreshTeamMode eq 'default'}">
							<span>首页（Dashboard）</span>
						</c:when>
						<c:when test="${userPreferences.refreshTeamMode eq 'auto'}">
							<span>上次访问团队</span>
						</c:when>
						<c:otherwise>
							<span>自定义</span>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
			<tr>
				<th>团队名称：</th>
				<td id="display-dt" colspan="3">
					<c:choose>
						<c:when test="${userPreferences.defaultTeam > 0 }">
							<span>${defaultTeamName}</span>
						</c:when>
						<c:otherwise>
							<span>无</span>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
			<tr>
				<th>导航栏：</th>
				<td id="display-ahm" colspan="3">
					<c:choose>
						<c:when test="${userPreferences.accessHomeMode eq 'dynamic'}">
							<span>动态</span>
						</c:when>
						<c:when test="${userPreferences.accessHomeMode eq 'tagitems'}">
							<span>文件</span>
						</c:when>
						<c:when test="${userPreferences.accessHomeMode eq 'starmark'}">
							<span>星标</span>
						</c:when>
						<c:when test="${userPreferences.accessHomeMode eq 'common'}">
							<span>常用</span>
						</c:when>
						<c:otherwise>
							<span>无</span>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
		</table>
	</div>
	<div id="default-home-page-edit" class="content-menu-body">
		<form name="edit-user-preferences" action="<vwb:Link context='dashboard' format='url'/>" method="POST">
		<input type="hidden" name="func" value="userPreferences"/>
		<table class="ui-table-form" id="editProfileTable" style="width:600px;">	
			<tr class="titleRow" style="text-align:left;">
				<th colspan="4">设置默认首页</th>
			</tr>
			<tr>
				<th width="85">默认进入：</th>
				<td>
					<input type="radio" name="refreshTeamMode" 
						value="default" <c:if test="${userPreferences.refreshTeamMode eq 'default' }">checked="checked"</c:if>>首页(Dashboard)
					<input type="radio" name="refreshTeamMode" 
						value="auto" <c:if test="${userPreferences.refreshTeamMode eq 'auto' }">checked="checked"</c:if>>上次访问团队
					<input type="radio" name="refreshTeamMode" 
						value="config"  <c:if test="${userPreferences.refreshTeamMode eq 'config' }">checked="checked"</c:if>>自定义
				</td>
			</tr>
			<tr>
				<th></th>
				<td><span class="ui-text-note">登录后进入Dashboard</span></td>
			</tr>
			<tr>
				<th></th>
				<td><span  class="ui-text-note">登录后进入上次登录最后浏览的团队</span></td>
			</tr>
			<tr>
				<th >团队名称：</th>
				<td>
					<select name="defaultTeam">
					<c:forEach items="${teamList}" var="team">
						<option value="${team.id }" <c:if test="${userPreferences.defaultTeam eq team.id }"> selected="selected"</c:if>>${team.displayName }</option>
					</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<th >导航栏：</th>
				<td>
					<input type="radio" name="accessHomeMode" 
						value="tagitems"  <c:if test="${userPreferences.accessHomeMode eq 'tagitems' }">checked="checked"</c:if>>文件
					<input type="radio" name="accessHomeMode" 
						value="dynamic" <c:if test="${userPreferences.accessHomeMode eq 'dynamic' }">checked="checked"</c:if>>动态	
					<input type="radio" name="accessHomeMode" 
						value="common" <c:if test="${userPreferences.accessHomeMode eq 'common' }">checked="checked"</c:if>>常用
					<input type="radio" name="accessHomeMode" 
						value="starmark"  <c:if test="${userPreferences.accessHomeMode eq 'starmark' }">checked="checked"</c:if>>星标
				</td>
			</tr>
			<tr>
				<th> </th>
				<td> 
					<input type="submit"  class="largeButton small" style="font-size:9pt;" value="提交"/>
					<input name="cancel" type="button"  class="largeButton small but-color-cannel" value="取消"/>
				</td>
			</tr>
		</table>
		</form>
	</div>
</div>
<script type="text/javascript">
$(document).ready(function(){
	init();
	var hash = (window.location.hash)?window.location.hash:'';
	if (hash!='') {
		switchView(hash.substring(1));
	}
	else {
		switchView();
	}
	function init(){
		$("#default-home-page-edit").attr('style','display:none');
		selected();
		$("input[name=refreshTeamMode]").live('change',selected);
	}
	
	function selected(){
		
		var rtm;
		$("input[name=refreshTeamMode][type=radio]").each(function(){
			if($(this).attr('checked')){
				rtm = $(this).val();
				return;
			}
		});
		if(rtm!='config'){
			var parent = $("form[name=edit-user-preferences] table tbody");
			if(rtm == 'default'){
				parent.children("tr:eq(1)").removeAttr("style");
				parent.children("tr:eq(2)").removeAttr("style");
				parent.children("tr:eq(3)").attr("style","display:none");
				parent.children("tr:eq(4)").attr("style","display:none");
				parent.children("tr:eq(5)").attr("style","display:none");
			}
			else if(rtm == 'auto'){
				parent.children("tr:eq(1)").removeAttr("style");
				parent.children("tr:eq(3)").removeAttr("style");
				parent.children("tr:eq(2)").attr("style","display:none");
				parent.children("tr:eq(4)").attr("style","display:none");
				parent.children("tr:eq(5)").attr("style","display:none");
			}
			$("input[name=defaultTeam]").attr('disabled','true');
			$("input[name=accessHomeMode]").attr('disabled','true'); 
		}else{
			var parent = $("form[name=edit-user-preferences] table tbody");
			parent.children("tr:eq(1)").removeAttr("style");
			parent.children("tr:eq(4)").removeAttr("style");
			parent.children("tr:eq(5)").removeAttr("style");
			parent.children("tr:eq(2)").attr("style","display:none");
			parent.children("tr:eq(3)").attr("style","display:none");
			
			$("input[name=defaultTeam]").removeAttr('disabled');
			$("input[name=defaultTeam] option:eq(0)").attr('selected','selected');
			$("input[name=accessHomeMode]").removeAttr('disabled');
			$("input[name=accessHomeMode] option:eq(0)").attr('selected','selected');
		}
	}
	
	$("#edit-prefrences").click(function(){
		var display = $("#default-home-page-edit").attr('style');
		$("#default-home-page-edit").removeAttr('style');
		$("#default-home-page-display").attr('style','display:none');
	});
	
	$("input[name=cancel]").click(function(){
		$("#default-home-page-display").removeAttr('style');
		$("#default-home-page-edit").attr('style','display:none');
	});
	//add by lvly@2012-07-26
	function switchView(VIEW) {
		$('#feedSelector ul.filter > li').removeClass('chosen');
		$('.content-menu-body').fadeOut();
		var view = VIEW;
		
		switch (VIEW) {
			case 'homePage':
				$('#default-home-page-display').fadeIn();
				break;
			case 'defaultTag':
				$('#default-tag-display').fadeIn();
				break;
			default:
				view='homePage';
				$('#default-home-page-display').fadeIn();
				break;
		}
		$('#feedSelector ul.filter a[view="' + view + '"]').parent().addClass('chosen');
	};
	$('#feedSelector ul.filter a').click(function(){
		switchView($(this).attr('view'));
	});
});
function clickDefaultTag(){
	var flag = true;
	if(!$("#useNameTag").attr("checked")){
		flag = false;
	}
	$.ajax({
		  type: "GET",
		  url: "<vwb:Link context='dashboard' format='url'/>?func=useNameTag",
		  data:"useNameTag="+flag,
		  success:function(data){
			  ui_spotLight('info', 'success', '当前设置已保存', 'fade');
		  },
			statusCode:{
				450:function(){alert('会话已过期,请重新登录');},
				403:function(){alert('您没有权限进行该操作');}
			}
		});
}
</script>
