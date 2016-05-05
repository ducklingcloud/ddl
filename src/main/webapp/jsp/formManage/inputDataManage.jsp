<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="/WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<html>
	<script type="text/javascript">
function setIdValue(id){
document.getElementById("setVauleInput").value=id;
if (confirm("delete?"))
				{
				return true;
				}
			else
				return false; 
}
</script>
	<style type="text/css">
.inputData_manager_table {
	border-collapse:collapse;
	
	
}

.inputData_manager_table_tr_1 {
	color:#FFFFFF;
	font-size:14px;
	font-weight:bold;
	line-height:25px;
}

.inputData_div{
	margin-bottom:5px;
}

a.Pagebutton{
background-color:#8FC7DA;
border:1px solid #000000;
color:#000000;
font-family:Arial;
font-size:14px;
font-weight:bolder;
margin-right:5px;
padding:1px 5px;
text-decoration:none;
}
</style>
	<body>


		<div class="inputData_div">
			<form
				action="<vwb:Link jsp='manageDdata' context='team' format='url'/>" method="post">
				<input type="hidden" name="type" value="showAllData" />
				<h3>
					<fmt:message key="userconfig.formdatamanage" />
				</h3>
				<select name="formSelect">
					<option>
						<fmt:message key="formdatamanage.defaultoption" />
					</option>
					<c:forEach var="item" items='${dmldataformlist}'>
						<option value="${item.formId}">
							${item.dmldesc}
						</option>
					</c:forEach>

				</select>

				<input type="submit" name="cmd"
					value="<fmt:message key="formdatamanage.submit" />" />

			</form>
		</div>
		<div>
			<form
				action="<vwb:Link context='team' jsp='manageDdata' format='url'/>"
				id="deleteForm" method="post">
				<input type="hidden" name="type" value="delete" />
				<input type="hidden" name="formId" value="${formId}">
				<input type="hidden" name="inputdataId" id="setVauleInput" value="">
				<table class="inputData_manager_table" border="1" width="100%">
					<tr align="left" bgcolor="#8FC7DA">
						<c:forEach var="item" items='${AllInputInfo}'>
							<td>
								${item.dmldesc}
							</td>
						</c:forEach>
						<td></td>
					</tr>
					<tbody>
						<c:forEach var="arrayItem" items='${forminputdataList}'>
							<tr>
								<c:forEach var="item" items='${arrayItem}' varStatus="status">

									<c:choose>
										<c:when test="${status.last}">
											<td align="right">

												<input type="submit" name="cmd"
													value="<fmt:message key="formdatamanage.delete" />"
													onclick="return setIdValue(${item})" />
												<vwb:Link jsp="updateDdata" context="team">
													<vwb:Param name="formId">${formId}</vwb:Param>
													<vwb:Param name="inputId">${item}</vwb:Param>
													<fmt:message key="formdatamanage.update" />
												</vwb:Link>
											</td>
										</c:when>
										<c:otherwise>
											<td>
												${item}
											</td>
										</c:otherwise>
									</c:choose>



								</c:forEach>

							</tr>
						</c:forEach>
					</tbody>
				</table>
			</form>
		</div>
		<div>
			<c:if test="${1!=totalPageCount}">
				<span> <c:if test="${!empty lastPage}">
						<a href="${lastPage}"><fmt:message
								key="formdatamanage.lastpage" />
						</a>
					</c:if> <c:forEach var="item" items='${pages}' varStatus="status">
						<c:choose>
							<c:when test="${currentPage==status.index}">
		${status.index+1}
		</c:when>
							<c:otherwise>
								<a class="Pagebutton" href="${item}">${status.index+1}</a>
							</c:otherwise>
						</c:choose>
					</c:forEach> <c:if test="${!empty nextPage}">
						<a href="${nextPage}"><fmt:message
								key="formdatamanage.nextpage" />
						</a>
					</c:if> </span>
			</c:if>
		</div>
	</body>
</html>
