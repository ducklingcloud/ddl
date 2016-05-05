<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page errorPage="/Error.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/vwb.tld" prefix="vwb"%>


<div class = "compare-hint">
	<h3>有未发布的内容，请先选择使用哪个版本？</h3>
	<p>由于编辑超时或浏览器意外退出，系统为您保存尚未发布的内容。</p>
	<p>请比较“当前版本”和“自动保存版本”，谨慎选择合适的版本进行编辑。</p>
</div>
<table id="compareDraft" class="ui-table-form" style="margin:0">
<thead>
	<tr class="largeButtonHolder">
		<td><form action="<vwb:Link context='edit' format='url' page='${rid}'/>" name="unrestoreDraftForm" method="POST">
				<input type="hidden" value="unrestoreDraft" name="func"/> 
				<input type="hidden" value="" name="publishText"/>
				<!--br/><span class="ui-text-alert">自动保存的内容将无法找回</span-->
			<h4>当前版本（已公开发布）<input type="submit" value="使用该版本" name="unrestoreButton"/><font color="#E00" size="2.5em">(自动保存内容将无法找回)</font></h4>
			<div class="version-info" >由${resource.lastEditorName }修改于<fmt:formatDate value="${resource.lastEditTime }" pattern="yyyy-MM-dd HH:mm:ss"/></div>
			</form></td>
		<td>
		<form action="<vwb:Link context='edit' format='url' page='${rid}'/>" name="restoreDraftForm" method="POST">
				<input type="hidden" value="restoreDraft" name="func"/> 
				<input type="hidden" value="" name="draftText"/>
		
		<h4>上次自动保存版本（未公开发布）<input type="submit" value="使用该版本" name="restoreButton"/></h4>	
		<div class="version-info" >
		<c:choose>
			<c:when test="${draft.type=='manual' }">
				由您保存于
			</c:when>
			<c:otherwise>
				由系统保存于
			</c:otherwise>
		</c:choose>
		<fmt:formatDate value="${draft.modifyTime }" pattern="yyyy-MM-dd HH:mm:ss"/>
		</div></form></td>
	</tr>
</thead>
<tbody>
	<tr>
		<td>
			<div id="publishText" class="viewer">
				${currHtml}
			</div>
		</td>
		<td>
			<div id="draftText" class="viewer">
				${autoHtml}
			</div>
		</td>
	</tr>
</tbody>

</table>

<script>
document.unrestoreDraftForm.publishText.value = $("#publishText").html();
document.restoreDraftForm.draftText.value = $("#draftText").html();
</script>
