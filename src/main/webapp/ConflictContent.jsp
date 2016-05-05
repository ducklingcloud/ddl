<%@page language="java" pageEncoding="UTF-8"%>
<%@taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />


<div id="tab21" class="DCT_tabmenu">
	<div class="DCT_Button_1" style="float:left">
  	    <a id="menuWaring" class="activetab" onclick="showDiv('Waring')" ><fmt:message key="conflict.oops.title"/></a>
	 	<a id="menuOther" onclick="showDiv('Other')"><fmt:message key="conflict.modified"/></a>
	 	<a id="menuOwn" onclick="showDiv('Own')"><fmt:message key="conflict.yourtext"/></a>
	</div>
</div>	
<div id="conflictWaring" class="DCT_hidetab">
<fmt:message key="conflict.oops"/>
<fmt:message key="conflict.goedit" >
	<fmt:param><a href="${editurl}">${pageTitle}</a></fmt:param>
</fmt:message>
</div>
<div id="conflictOther" class="DCT_hidetab">${otherinner}</div>

<div id="conflictOwn" class="DCT_hidetab">${myinner}</div>

<script>
function showDiv(name){
	document.getElementById("conflictOther").style.display="none";
	document.getElementById("conflictOwn").style.display="none";
	document.getElementById("conflictWaring").style.display="none";
	
	document.getElementById("menuWaring").className="none";
	document.getElementById("menuOther").className="none";
	document.getElementById("menuOwn").className="none";
	
	document.getElementById("conflict"+name).style.display="block";
	document.getElementById("menu"+name).className="activetab";
	
}
showDiv("Waring")
</script>