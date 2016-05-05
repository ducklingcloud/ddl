<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb" %>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/scripts/jquery/selectcontrol.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/scripts/jquery/jquery-ui-1.7.2.custom.min.js"></script>
<link
	href="<%=request.getContextPath()%>/scripts/jquery/cupertino/jquery-ui-1.7.2.custom.css"
	rel="stylesheet" type="text/css">
<fmt:setBundle basename="portlet/AdminPortlet" />
<form action="<vwb:Link page='${vwb.viewport.resourceId}' format='url'/>/a/portalSetting?method=save" method="POST" name="p_form">
		
		<div><fmt:message key="portal.page.title" />:&nbsp;&nbsp;<input type="text" style="width: 420px;" value="${portalResource.title}" id="portaltitle" name="portaltitle"></div>
		<br>
		<table width="100%">
			<tr>
				<td width="43%" valign="top">
					<div style="margin-left:auto; width:100%;height:100%;padding-bottom:10px;">
						<fmt:message key="title.exists"></fmt:message>
						<select multiple style="height:325px; width:100%;"
							id="p_pagePortlets" name="p_pagePortlets">
							<c:forEach items="${portal_portlets}" var="portlet"
								varStatus="loopStatus">
								<option value="${portlet.id}">
									${portlet.portletName}
								</option>
							</c:forEach>
						</select>
					</div>
				</td>
				<td width="14%">
					<table style="margin-left:auto;margin-right:auto;height:300px;padding-bottom:10px;">
						<tr>
							<td>
								<div class="ui-state-default ui-corner-all"
									style="width:60px;height:60px;cursor:pointer;"
									onClick="p_addAll()">
									<img style="margin:16px;" src="<%=request.getContextPath()%>/images/removeArrow.png" title="<fmt:message key='tooltip.add'/>" />
								</div>
							</td>
						</tr>
						<tr>
							<td>
								<div class="ui-state-default ui-corner-all"
									style="width:60px;height:60px;cursor:pointer;"
									onClick="p_removeAll()">
									<img style="margin:16px"
										src="<%=request.getContextPath()%>/images/add.png" title="<fmt:message key='tooltip.remove'/>" />
								</div>
							</td>
						</tr>
					</table>
				</td>
				<td width="43%" valign="top">
					<div style="width:100%;margin-right:auto;">
						<fmt:message key="title.candidates"/>
						<select style="width:100%;"
							onChange="p_changeAvailables(this)"
							name="applications">
							<option value='-'>
								<fmt:message key="button.selectapp" />
							</option>
							<c:forEach
								items="${portletContainer.optionalContainerServices.portletContextService.portletContexts}"
								var="app">
								<option value="<c:out value="${app.applicationName}"/>">
									<c:out value="${app.applicationName}" />
								</option>
							</c:forEach>
						</select>
						<select multiple style="height:300px;width:100%"
							id="p_avaibles">
						</select>
					</div>
				</td>
			</tr>
		</table>
		<hr>
		<table width="100%">
			<tr>
				<td align="right">
					<input type="button" onclick="p_submit()" value="<fmt:message key='button.submit'/>" />
					<input type="button" onclick="p_reset()" value="<fmt:message key='button.reset'/>"/>
				</td>
			</tr>
		</table>
 </form>
 <script language="javascript">
       var apps=new Array();
       <c:forEach items="${portletContainer.optionalContainerServices.portletContextService.portletContexts}" var="app">
           apps['<c:out value="${app.applicationName}"/>'] = new Array();
         <c:forEach items="${app.portletApplicationDefinition.portlets}" var="portlet" varStatus="loopStatus">
           apps['<c:out value="${app.applicationName}"/>'][<c:out value="${loopStatus.index}"/>] = {text:'<c:out value="${portlet.portletName}"/>', value:'${app.applicationName}.${portlet.portletName}'};
         </c:forEach>
       </c:forEach>
	var p_candidate=new ControlSelect('p_avaibles');
	var p_pagePortlets = new ControlSelect('p_pagePortlets');
	function p_reset(){
		document.p_form.reset();
		p_candidate.reset();
		p_pagePortlets.reset();
	}
	
	function p_submit(){
		p_pagePortlets.selectAll();
		document.p_form.submit();
	}
	function p_addAll(){
		p_candidate.each(function(text, value){
			p_pagePortlets.append(text, value);
		});
	}
	function p_removeAll(){
		<c:choose>
			<c:when test="${page.name=='admin'}">
				if (p_pagePortlets.removeSelect("PlutoPageAdmin")){
					alert('<fmt:message key="portlet.reserve"/>');
				}
			</c:when>
			<c:otherwise>
				p_pagePortlets.removeSelect();
			</c:otherwise>
		</c:choose>
	}
	function p_changeAvailables(oList){
		p_candidate.removeAll();
		var portlets = apps[oList.value];
		if (portlets!=null){
			for (var i=0;i<portlets.length;i++){
				p_candidate.append(portlets[i].text, portlets[i].value);
			}
		}
	}	
</script>