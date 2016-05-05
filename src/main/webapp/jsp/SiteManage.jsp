<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<fmt:setBundle basename="templates.default" />

<%
	pageContext.setAttribute("basepath", request.getContextPath());
%>
<script type="text/javascript"
	src="${basepath}/scripts/jquery/jquery-ui-1.7.2.custom.min.js"></script>
<script type="text/javascript"
	src="${basepath}/scripts/jquery/jquery.validate.js"></script>

<script type="text/javascript">
	$(document).ready(function() {
	
		$('#tipdlg').dialog({
			autoOpen: false,
			modal:true,
			width: 180
		});
	
		$('#startdlg').dialog({
			autoOpen: false,
			modal:true,
			width: 180
		});
	
		$('#stopdlg').dialog({
			autoOpen: false,
			modal:true,
			width: 180
		});
	
		$('#destorydlg').dialog({
			autoOpen: false,
			modal:true,
			width: 180
		});
	
		$('#templatedlg').dialog({
			autoOpen: false,
			modal:true,
			width: 350
		});
		
		$('#dumpdomaindlg').dialog({
			autoOpen: false,
			modal:true,
			width: 200
		});
		
		//站点选择
		$('#lSelAll').click(function(){
			$('.siteSelAll').attr("checked", 'checked');
			$('.siteSel').attr("checked", 'checked');
			
			decideSelOps();
		});
		
		$('#lSelNone').click(function(){
			$('.siteSel').removeAttr("checked");
			$('.siteSelAll').removeAttr("checked");
			
			decideSelOps();
		});
		
		$('#lSelOther').click(function(){
			
			$('.siteSel').each(function(){
				if($(this).attr("checked")){
					$(this).removeAttr("checked");
				}else{
					$(this).attr("checked", 'checked');
				}
			})
			
			decideSelAll();
			decideSelOps();
		});
		
		$('#lSelActive').click(function(){
			clearSelAll();
			
			$("td.siteData").each(function(){
				var siteSel = $(this).children(':input[name=siteSel]');
				var siteId = $(this).children(':input[name=siteId]');
				var siteState = $(this).children(':input[name=siteState]');
				
				if(siteState.val() == 'work'){
					siteSel.attr("checked", "checked");
				}
			});
			
			decideSelAll();
			decideSelOps();
		});
		
		$('#lSelHangup').click(function(){
			clearSelAll();
		
			$("td.siteData").each(function(){
				var siteSel = $(this).children(':input[name=siteSel]');
				var siteId = $(this).children(':input[name=siteId]');
				var siteState = $(this).children(':input[name=siteState]');
				
				if(siteState.val() == 'hangup'){
					siteSel.attr("checked", "checked");
				}
			});
			
			decideSelAll();
			decideSelOps();			
		});
		
		$('#siteSelAll').click(function(){
			if($(this).attr("checked")){
				$('.siteSel').attr("checked", 'checked');
			}else{
				$('.siteSel').removeAttr("checked");
			}
			
			decideSelOps();
		});
		
		$('.siteSel').click(function(){
			decideSelAll();
			decideSelOps();
		});
		
		//站点操作提交
		$(':input[name=btnActive]').click(function(){
			Execute(1);
		});
		$(':input[name=btnHangup]').click(function(){
			Execute(2);
		});
		$(':input[name=btnDestroy]').click(function(){
			Execute(3);
		});
		$(':input[name=btnSaveTpl]').click(function(){
			Execute(4);
		});
		$(':input[name=btnClearDm]').click(function(){
			Execute(5);
		});
		$(':input[name=btnRefresh]').click(function(){
			window.location.href="<vwb:Link jsp='manageSite' format='url'/>"; 
		});
		
	});
	
	function clearSelOps(){
		$(':input[name=btnActive]').removeAttr("disabled");
		$(':input[name=btnHangup]').removeAttr("disabled");
		$(':input[name=btnDestroy]').removeAttr("disabled");
		$(':input[name=btnSaveTpl]').removeAttr("disabled");
		$(':input[name=btnClearDm]').removeAttr("disabled");
	}
	
	function decideSelOps(){
		clearSelOps();
		
		var selCount = 0;
		var hasMainSite = false;
		$("td.siteData").each(function(){
			var siteSel = $(this).children(':input[name=siteSel]');
			var siteId = $(this).children(':input[name=siteId]');
			var siteState = $(this).children(':input[name=siteState]');
			
			if(siteSel.attr("checked")){
				selCount++;
				
				if(siteId.val() == 1){
					hasMainSite = true;
				}
				
				if(siteState.val() == 'work'){
					$(':input[name=btnActive]').attr("disabled", "disabled");
				}
				
				if(siteState.val() == 'hangup'){
					$(':input[name=btnHangup]').attr("disabled", "disabled");
					$(':input[name=btnSaveTpl]').attr("disabled", "disabled");
					$(':input[name=btnClearDm]').attr("disabled", "disabled");
				}
				
				if(siteState.val() == 'uninit'){
					$(':input[name=btnActive]').attr("disabled", "disabled");
					$(':input[name=btnHangup]').attr("disabled", "disabled");
					$(':input[name=btnSaveTpl]').attr("disabled", "disabled");
					$(':input[name=btnClearDm]').attr("disabled", "disabled");
				}
			}
			
		});
		
		if(selCount == 0){
			$(':input[name=btnActive]').attr("disabled", "disabled");
			$(':input[name=btnHangup]').attr("disabled", "disabled");
			$(':input[name=btnDestroy]').attr("disabled", "disabled");
			$(':input[name=btnSaveTpl]').attr("disabled", "disabled");
			$(':input[name=btnClearDm]').attr("disabled", "disabled");
		}
			
		if(selCount > 1){
			$(':input[name=btnSaveTpl]').attr("disabled", "disabled");
			$(':input[name=btnClearDm]').attr("disabled", "disabled");
		}
		
		if(hasMainSite){
			$(':input[name=btnActive]').attr("disabled", "disabled");
			$(':input[name=btnHangup]').attr("disabled", "disabled");
			$(':input[name=btnDestroy]').attr("disabled", "disabled");
			$(':input[name=btnClearDm]').attr("disabled", "disabled");
		}
	}
	
	function clearSelAll(){
		$('.siteSel').removeAttr("checked");
		$('.siteSelAll').removeAttr("checked");
	}
	
	function decideSelAll(){
		var isAllSelected = true;
		
		$('.siteSel').each(function(){
			if(!$(this).attr("checked")){
				isAllSelected = false;
			}
		})
		
		if(isAllSelected == true){
			$('.siteSelAll').attr("checked", 'checked');
		}else{
			$('.siteSelAll').removeAttr("checked");
		}
	}

	function Execute(opId){
		var allSiteIds = '';
		var allSiteTips = '';
	
		$("td.siteData").each(function(){
			var siteSel = $(this).children(':input[name=siteSel]');
			var siteId = $(this).children(':input[name=siteId]');
			
			if(siteSel.attr("checked")){
				allSiteIds = allSiteIds + siteId.val() + ',';
				allSiteTips = allSiteTips + '<fmt:message key="site.manage.dialog.sitealias" />' + siteId.val() + ',';
			}
		});
		
		allSiteIds = allSiteIds.substr(0, allSiteIds.length-1);
		allSiteTips = allSiteTips.substr(0, allSiteTips.length-1);
		
		if(opId==0){
			$('#tipdlg').dialog('open');
			return false;
		}
		if(opId==1){
			var activateSiteId = document.getElementById("activateSiteId");
	 		activateSiteId.value=allSiteIds;
	 		manageSiteTip(1, allSiteTips);
		
			$('#startdlg').dialog('open');
			return false;
		}
		if(opId==2){
			var deactivateSiteId = document.getElementById("deactivateSiteId");
	 		deactivateSiteId.value=allSiteIds;
	 		manageSiteTip(2, allSiteTips);

			$('#stopdlg').dialog('open');
			return false;
		}
		if(opId==3){
			var destroySiteId = document.getElementById("destroySiteId");
	 		destroySiteId.value=allSiteIds;
	 		manageSiteTip(3, allSiteTips);

			$('#destorydlg').dialog('open');
			return false;
		}
		if(opId==4){
			var templateSiteId = document.getElementById("templateSiteId");
	 		templateSiteId.value=allSiteIds;
	 	
			$('#templatedlg').dialog('open');
			return false;
		}
		if(opId==5){
			var dumpDomainSiteId = document.getElementById("dumpDomainSiteId");
	 		dumpDomainSiteId.value=allSiteIds;
	 		manageSiteTip(5, allSiteTips);
	 	
			$('#dumpdomaindlg').dialog('open');
			return false;
		}
	}
	
	function closeDialog(dlgId){
		if(dlgId==0){
			$("#tipdlg").dialog('close');
		}else if(dlgId==1){
			$("#startdlg").dialog('close');
		}else if(dlgId==2){
			$("#stopdlg").dialog('close');
		}else if(dlgId==3){
			$("#destorydlg").dialog('close');
		}else if(dlgId==4){
			$("#templatedlg").dialog('close');
		}else if(dlgId==5){
			$("#dumpdomaindlg").dialog('close');
		}
	}
	
	function checkTemplateName(){
			if($("#templateName").val().trim().length>0)
			{
			   $("#createTemplateManage").submit();
			}else
			{
			    $("#templateName").after("<font color='red'><fmt:message key="duckling.site.create.templateName.require" /></font>");  
			}
	}
	
	function manageSiteTip(opId,content){
		if(opId==1){
			document.getElementById("activateSiteTip").innerHTML = content;
		}else if(opId==2){
			document.getElementById("deactivateSiteTip").innerHTML = content;
		}else if(opId==3){
			document.getElementById("destroySiteTip").innerHTML = content;
		}else if(opId==5){
			document.getElementById("dumpDomainTip").innerHTML = content;
		}
	}
	
</script>

<link type="text/css"
	href="${basepath}/scripts/jquery/cupertino/jquery-ui-1.7.2.custom.css"
	rel="stylesheet" />

<style>
.d-toolbar{
	background-color:#b0c4de;
	color:#FFFFFF;
	height:40px;
	line-height:40px;
	padding:0 10px;
	text-align:left;
}
.d-selectbar{
	background-color:#F7F7F7;
	color:#666666;
	height:30px;
	line-height:30px;
	padding:0 10px;
	text-align:left;
}
.d-list{
	
}
.d-link{
	cursor:pointer;
}
</style>

<div>
	<h3>
		<fmt:message key="site.manage.title" />
	</h3>
	
	<div>
		<table width="98%">
			<tr>
				<td width="80%">
					<div id="siteManageNotify" style="color: blue; text-align: left">
						${siteManageNotify}
					</div>
				</td>
				<td align="right">
					<a href="<vwb:Link jsp="siteTemplate" context='plain' format="url"/>"><fmt:message
							key="site.manage.link.template" /> </a>
				</td>
			</tr>
		</table>
	</div>
	
	<div class="d-toolbar">
		<table width="98%">
			<tr>
				<td>
					<div>
						<input class="DuclingButton" type="button" name="btnActive" value="激活" disabled/>
						<input class="DuclingButton" type="button" name="btnHangup" value="挂起" disabled/>
						<input class="DuclingButton" type="button" name="btnDestroy" value="销毁" disabled/>
						<input class="DuclingButton" type="button" name="btnSaveTpl" value="保存模板" disabled/>
						<input class="DuclingButton" type="button" name="btnClearDm" value="清空域名" disabled/>
						<input class="DuclingButton" type="button" name="btnRefresh" value="刷新" />
					</div>
				</td>
				<td align="right">
					<div>
						<select onchange="">
							<option selected value="1">
								1 / 1
							</option>
						</select>
					</div>
				</td>
			</tr>
		</table>
	</div>

	<div class="d-list">
		<table class="DCT_wikitable">
			<tr>
				<th width="5%">
					<input type="checkbox" class="siteSelAll" id="siteSelAll" />
				</th>
				<th width="10%">
					<fmt:message key="site.manage.siteid" />
				</th>
				<th>
					<fmt:message key="site.manage.sitename" />
				</th>
				<th width="10%">
					<fmt:message key="site.manage.state" />
				</th>
				<th width="20%">
					<fmt:message key="site.manage.createtime" />
				</th>
			</tr>


			<c:forEach var="item" items='${requestScope.allSites}'
				varStatus="status">

				<tr>
					<td class="siteData">
						<input type="checkbox" class="siteSel" name="siteSel"/>
						<input type="hidden" name="siteId" value="${item.siteId}"/>
						<c:choose>
							<c:when test="${item.state.value=='work'}">
								<input type="hidden" name="siteState" value="work"/>
							</c:when>
							<c:when test="${item.state.value=='hangup'}">
								<input type="hidden" name="siteState" value="hangup"/>
							</c:when>
							<c:when test="${item.state.value=='uninit'}">
								<input type="hidden" name="siteState" value="uninit"/>
							</c:when>
						</c:choose>
					</td>
					<td>
						<c:out value="${item.siteId}"></c:out>
					</td>
					<td>
						<c:choose>
							<c:when test="${item.state.value=='work'}">
								<a href="${item.mainDomain}" target="_blank"> <c:out
										value="${item.siteName}" /> </a>
							</c:when>
							<c:otherwise>
								<c:out value="${item.siteName}" />
							</c:otherwise>
						</c:choose>

					</td>
					<td>
						<c:choose>
							<c:when test="${item.state.value=='work'}">
								<fmt:message key="site.manage.state.work" />
							</c:when>
							<c:when test="${item.state.value=='hangup'}">
								<fmt:message key="site.manage.state.hangup" />
							</c:when>
							<c:when test="${item.state.value=='uninit'}">
								<fmt:message key="site.manage.state.uninit" />
							</c:when>
						</c:choose>
						<c:if test="${item.state.value}">
						</c:if>
					</td>
					<td>
						<c:out value="${item.createTime}"></c:out>
					</td>
				</tr>
			</c:forEach>
		</table>
	</div>

	<div class="d-selectbar">
		选择：
		<a class="d-link" id="lSelAll">全部</a>
		-
		<a class="d-link" id="lSelActive">活动</a>
		-
		<a class="d-link" id="lSelHangup">挂起</a>
		-
		<a class="d-link" id="lSelOther">反选</a>
		-
		<a class="d-link" id="lSelNone">不选</a>
	</div>

	<div class="d-toolbar">
		<table width="98%">
			<tr>
				<td>
					<div>
						<input class="DuclingButton" type="button" name="btnActive" value="激活" disabled=""/>
						<input class="DuclingButton" type="button" name="btnHangup" value="挂起" disabled=""/>
						<input class="DuclingButton" type="button" name="btnDestroy" value="销毁" disabled=""/>

						<input class="DuclingButton" type="button" name="btnSaveTpl" value="保存模板" disabled=""/>
						<input class="DuclingButton" type="button" name="btnClearDm" value="清空域名" disabled=""/>

						<input class="DuclingButton" type="button" name="btnRefresh" value="刷新" />
					</div>
				</td>
				<td align="right">
					<div>
						<select onchange="">
							<option selected="" value="1">
								1 / 1
							</option>
						</select>
					</div>
				</td>
			</tr>
		</table>
	</div>
	
<div id="tipdlg" title="<fmt:message key="site.manage.dialog.tip" />">
	<p>
		<fmt:message key="site.manage.dialog.tip.content" />
	</p>
	<input id="calcelsubmit" name="cancelsubmit" type="button"
		value="<fmt:message key="site.manage.dialog.cancel" />"
		onclick="closeDialog(0);" />
</div>

<div id="startdlg"
	title="<fmt:message key="site.manage.dialog.activate" />">
	<p>
		<span id="activateSiteTip"></span>&nbsp;
		<fmt:message key="site.manage.dialog.activate.content" />
	</p>
	<form id="activateSiteManage"
		action="<vwb:Link jsp="manageSite" format="url"/>"
		method="post" name="siteManageForm">
		<input type="hidden" name="func" value="activateSite"/>
		<input id="activateSiteId" name="activateSiteId" type="hidden"
			value="" />
		<input id="oksubmit" name="oksubmit" type="submit"
			value="<fmt:message key="site.manage.dialog.ok" />" />
		<input id="calcelsubmit" name="cancelsubmit" type="button"
			value="<fmt:message key="site.manage.dialog.cancel" />"
			onclick="closeDialog(1);" />
	</form>
</div>

<div id="stopdlg"
	title="<fmt:message key="site.manage.dialog.inactivate" />">
	<p>
		<span id="deactivateSiteTip"></span>&nbsp;
		<fmt:message key="site.manage.dialog.inactivate.content" />
	</p>
	<form id="deactivateSiteManage"
		action="<vwb:Link jsp="manageSite" format="url"/>"
		method="post" name="siteManageForm">
		<input type="hidden" name="func" value="deactivateSite"/>
		<input id="deactivateSiteId" name="deactivateSiteId" type="hidden"
			value="" />
		<input id="oksubmit" name="oksubmit" type="submit"
			value="<fmt:message key="site.manage.dialog.ok" />" />
		<input id="calcelsubmit" name="cancelsubmit" type="button"
			value="<fmt:message key="site.manage.dialog.cancel" />"
			onclick="closeDialog(2);" />
	</form>
</div>

<div id="destorydlg"
	title="<fmt:message key="site.manage.dialog.destroy" />">
	<p>
		<span id="destroySiteTip"></span>&nbsp;
		<fmt:message key="site.manage.dialog.destroy.content" />
	</p>
	<form id="destroySiteManage"
		action="<vwb:Link jsp="manageSite" format="url"/>"
		method="post" name="siteManageForm">
		<input type="hidden" name="func" value="destroySite"/>
		<input id="destroySiteId" name="destroySiteId" type="hidden" value="" />
		<input id="oksubmit" name="oksubmit" type="submit"
			value="<fmt:message key="site.manage.dialog.ok" />" />
		<input id="calcelsubmit" name="cancelsubmit" type="button"
			value="<fmt:message key="site.manage.dialog.cancel" />"
			onclick="closeDialog(3);" />
	</form>
</div>

<div id="templatedlg"
	title="<fmt:message key="site.manage.dialog.savetemplate" />">
	<form id="createTemplateManage"
		action="<vwb:Link jsp="manageSite" format="url"/>"
		method="post" name="siteManageForm">
		<input type="hidden" name="func" value="createTempate"/>
		<input id="templateSiteId" name="templateSiteId" type="hidden"
			value="" />
		<table>
			<tr>
				<td>
					<fmt:message key="site.manage.dialog.savetemplate.content" />
				</td>
				<td>
					<input id="templateName" name="templateName" type="text" value=""
						maxlength="45" />
				</td>
			</tr>
			<tr>
				<td>
					&nbsp;
				</td>
				<td>
					<input id="savesubmit" name="savesubmit" type="button"
						value="<fmt:message key="site.manage.dialog.save" />"
						onclick="checkTemplateName();" />
					<input id="calcelsubmit" name="cancelsubmit" type="button"
						value="<fmt:message key="site.manage.dialog.cancel" />"
						onclick="closeDialog(4);" />
				</td>
			</tr>
		</table>
	</form>
</div>

<div id="dumpdomaindlg"
	title="<fmt:message key="site.manage.dialog.dumpdomain" />">
	<p>
		<span id="dumpDomainTip"></span>&nbsp;
		<fmt:message key="site.manage.dialog.dumpdomain.content" />
	</p>
	<form id="dumpDomainManage"
		action="<vwb:Link jsp="manageSite" format="url"/>"
		method="post" name="siteManageForm">
		<input type="hidden" name="func" value="dumpDomain"/>
		<input id="dumpDomainSiteId" name="dumpDomainSiteId" type="hidden"
			value="" />
		<input id="oksubmit" name="oksubmit" type="submit"
			value="<fmt:message key="site.manage.dialog.ok" />" />
		<input id="calcelsubmit" name="cancelsubmit" type="button"
			value="<fmt:message key="site.manage.dialog.cancel" />"
			onclick="closeDialog(5);" />
	</form>
</div>