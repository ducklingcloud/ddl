<%@page language="java" pageEncoding="UTF-8"%>
<%@taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<script type="text/javascript"
	src="<%=basePath%>scripts/ajax/editor.js" /></script>
	
<script>
//<![CDATA[
	restoreType='${restoreType}';
	TempPageData='${TempPageData}';
//]]>


diyanliang='${restoreType}';
</script>
<div id="locker" style="DISPLAY: none">
	<fmt:message key="edit.locked">
	    <fmt:param>${locker}</fmt:param>
	</fmt:message>
</div>

<textarea cols="80" rows="4" name="inTempPage" id="inTempPage" readonly="readonly" style="display: none;">
${strTempPage}
</textarea>
<div id="pagecontent">
<form id="editform" name="editform" method="post" action="<vwb:EditLink format='url' />">
<input type="hidden" name="lockType" id="lockType" value='${lockType}'>
<input type="hidden" name="lockVersion" id="lockVersion" value='${lockVersion}'>
	<div id="tab21" class="DCT_tabmenu">
		<div id="submitbuttons">
			<input name='saveexit' type='button' id='okbutton'
				value='<fmt:message key="editor.plain.save.submit"/>'
				onclick='javascript:submitDEForm(this);' />
			<input name='save' type='button' id="savebutton"
				value='<fmt:message key="editor.plain.saveedit.submit"/>'
				onclick="javascript:saveDEeditor();" />
			<input name='preview' type='button' id="previewbutton"
				value='<fmt:message key="editor.plain.preview.submit"/>'
				onclick="javascript:submitDEForm(this);" />
			<input name='cancel' type='button' id='cancelbutton'
				value='<fmt:message key="editor.plain.cancel.submit"/>'
				onclick='javascript:submitDEForm(this);' />
			<input name="action" type="hidden" id="action" />
		</div>
	</div>
	
	<div style="float:left"><fmt:message key="editor.fck.page.title" />:<input name="title"  type="text" id="title"  value="${editDpage.meta.title}" style="width:420px"/></div> 
	<div style="color: rgb(255, 51, 0);" id="infoarea">&nbsp;</div>
	

	<jsp:include page="jsp/editor.jsp"/>
	
	<div id="tab21" class="DCT_tabmenu">
		<div id="submitbuttons">
			<input name='saveexit' type='button'
				value='<fmt:message key="editor.plain.save.submit"/>'
				onclick='javascript:submitDEForm(this);' />
			<input name='save' type='button' 
				value='<fmt:message key="editor.plain.saveedit.submit"/>'
				onclick="javascript:saveDEeditor();" />
			<input name='preview' type='button' 
				value='<fmt:message key="editor.plain.preview.submit"/>'
				onclick="javascript:submitDEForm(this);" />
			<input name='cancel' type='button' 
				value='<fmt:message key="editor.plain.cancel.submit"/>'
				onclick='javascript:submitDEForm(this);' />
			
		</div>
	</div>
</form>
</div>
