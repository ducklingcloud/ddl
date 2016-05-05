<%@ page language="java" pageEncoding="utf-8"%>
<%@ page errorPage="/Error.jsp"%>
<%@ taglib uri="/WEB-INF/tld/vwb.tld" prefix="vwb"%>
<div id="Dct_plugin_content">
<jsp:include flush="true" page="/ConfigMenu.jsp"/>
<div class="right">
	<vwb:render content="${RenderContext.content}"/>
</div>
</div>