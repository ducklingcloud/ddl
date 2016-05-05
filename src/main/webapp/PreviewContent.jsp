<%@page language="java" pageEncoding="UTF-8"%>
<%@taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />

<script type="text/javascript" src="${contextPath}/scripts/ajax/editor.js" /></script>

<script type="text/javascript">
function submitPreviewForm(obj) {
	var strction=obj.name;
	document.previewForm.func.value=strction;
	document.previewForm.submit();
}
$(document).ready(function(){ $('body').addClass('fullFunction'); });
</script>
<style>
#tab21.DCT_tabmenu { display:none; }
#right.DCT_right_body { padding:0; margin:0; border:none; }
#info { padding:0 !important; }
#content .DCT_tabmenu.toolHolder input[name='saveexit'] { margin-left:10px; }
#content .DCT_tabmenu.tollHolder p { margin-left:10px; }
</style>

<form id="previewForm" name="previewForm" method="post"
	action="<vwb:EditLink format='url' />">
	<div class="DCT_tabmenu toolHolder">
		<p><fmt:message key="preview.info"/></p>
		<input name='saveexit' type='button' id='okbutton'
			onclick='javascript:submitPreviewForm(this);'
			value="<fmt:message key='editor.preview.save.submit'/>" accesskey="s"
			title="<fmt:message key='editor.preview.save.title'/>" />
		<input name='previewToEdit' type='button' id='previewToEdit'
			onclick='javascript:submitPreviewForm(this);'
			value='<fmt:message key='editor.preview.edit.submit'/>' accesskey="e"
			title="<fmt:message key='editor.preview.edit.title'/>" />
		
<!-- 
		<input name='cancel' type='button' id='cancelbutton'
			onclick='javascript:submitPreviewForm(this);'
			value="<fmt:message key='editor.preview.cancel.submit'/>"
			accesskey="q"
			title="<fmt:message key='editor.preview.cancel.title'/>" />
-->
		<input name="func" type="hidden" id="func" />
		
		<textarea cols="80" rows="4" name="fixDomStr" id="fixDomStr" readonly="readonly" style="display: none;">
		${htmlText}
		</textarea>
		<input name="pageTitle" type="hidden" id="pageTitle"
			value='${editDpage.meta.title}'>
		<input name="useNewCollection" type="hidden" id="useNewCollection"
			value='${useNewCollection}'>
		<input name="newCollection" type="hidden" id="newCollection"
			value='${newCollection}'>
		<input name="selectCollection" type="hidden" id="selectCollection"
			value='${selectCollection}'>
		<input name="lockVersion" type="hidden" id="lockVersion"
			value='${lockVersion}'>
	</div>
</form>
<!-- div class="previewcontent"  style="float: left"> -->
<div class="content-major">
	<div  id="DCT_viewcontent">
		${editDpage.detail.content}
	</div>
</div>