<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="/WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default"/>
<html>
  <head>

  </head>
  
  <body>
		<div align="center" style="margin-top:80px">
		<form action="<vwb:Link jsp="manageDdata" context="team" format="url"/>" method="post">
		<input type="hidden" name="type" value="update"/>
		<table>
		
		       <c:forEach var="item" items='${UpdateDatamap}' varStatus="status">
		       
						<tr>			
						
						<td align="right">
						${item.key}&nbsp;:
						</td>
						<td>
						<c:choose>
						<c:when test="${item.value['type'] eq 'password'}">
						<input type="password" name="${item.key}" value="${item.value['value']}"/>
						</c:when>
						<c:otherwise>
						<input type="text" name="${item.key}" value="${item.value['value']}"/>
						</c:otherwise>
						</c:choose> 
						</td>
						
						</tr>					
				</c:forEach>
				<tr>
				<td align="right">
						<input type="hidden" name="formId" value="${formId}">
						<input type="hidden" name="dataId" value="${updateId}">
						
				</td>
				<td align="left">
						<input type="submit" name="cmd" value="<fmt:message key="formdatamanage.update" />" />
						<input type="button" value="<fmt:message key="formdatamanage.cancel" />" onclick="window.location='<vwb:Link jsp="manageDdata" context='team' format='url'/>?formId=${formId}';"> 
						
				</td>
				</tr>
		</table>
		</form>
		</div>
		
  </body>
</html>
