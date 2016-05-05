<%@ taglib uri="/WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="templates.default"/>
<%-- Provides a simple searchbox that can be easily included anywhere on the page --%>
<%-- Powered by jswpwiki-common.js//SearchBox --%>


<%
pageContext.setAttribute("contextPath", request.getContextPath());
%>
<script type="text/javascript" src="${contextPath}/scripts/DUI/autosearch/jquery.autocomplete.js"></script>
<script language="javascript">
 <vwb:UserCheck status="notAuthenticated">
 	var sboxmessage='<fmt:message key="sbox.login"/>'
 </vwb:UserCheck>
function addSearchBox() {
	var box = document.getElementById("box");
	var header = document.getElementById("header");
	var pos = $('#header').offset();
	var boxleft=(header.offsetWidth+pos.left);
	box.style.left = boxleft-220 + "px";
	box.style.top="0px";
}

function getWidth(){
	if (navigator.userAgent.indexOf('MSIE')){
		return document.body.offsetWidth;
	}
	return window.innerWidth;
}
function getL(e){
	 var l = e.offsetLeft;
	 while (e = e.offsetParent)
	 l += e.offsetLeft;
	 return l;
}
function getT(e){
	 var t = e.offsetTop;
	 while (e = e.offsetParent)
	 t += e.offsetTop;
	 return t;
}
addSearchBox();
window.onresize=addSearchBox;





url='<vwb:Link context="plain" jsp="allResource" format="url"/>';
//url='<vwb:Link jsp="JSON-RPC" format="url" context="plain"/>'
$().ready(function() {
$('#bannerSearch').autocomplete(url,{
			parse: function(data) { 
				return $.map(data, function(row) { 
					return {data: row, value: row.title,m_value:row.id} 
				}); 
			}, 
			formatItem: function(row, i, max) {
				return "<td><a href=\"<vwb:Link page='"+row.id+"' format='url'/>\">" + row.title+"("+ row.id+")</a></td>";
			},
			extraParams: {query:function(){return $('#bannerSearch').val();},datatype:"jsonrpc"},
			dataType: "json",
			delay:10,
			scroll:false,
			selectFirst:false,
			onSelected:onselectbackcall,
			resultsClass:"searchboxMenu",
			keyControl:false,
			max:100
		}
	);
})

function onselectbackcall(row){
	if(row.m_value&&row.m_value!=""){
		window.location.href="<vwb:Link page='"+row.m_value+"' format='url'/>";
	}
}
</script>


<form method="post" id="searchForm" class="wikiform" accept-charset="UTF-8" action="<vwb:Link jsp='search' format='url'/>">
  <!--div style="position:relative">  -->
  <div>
  <input type="text" name="query" id="bannerSearch" value=""></input> 
  <button type="submit" style="height:20px"
  		 name="searchSubmit" id="button"
  		value="<fmt:message key='find.submit.go'/>"
  		title="<fmt:message key='find.submit.go'/>"></button>
  </div>
  <!-- edit by diyanliang 09-3-21 -->
  <!--  div id="searchboxMenu" style='visibility:hidden;'-->
  <div id="searchboxMenu" style='display:none'>
    <div id="searchResult">
	  <%--<fmt:message key='sbox.search.result'/>--%>
      <span id="searchTarget" style="display:none;">
         <%--<fmt:message key='sbox.search.target'/>--%></span> 
      <span id="searchSpin" class="DCT_spin" style="position:absolute;display:none;"></span>
	  <div id="searchOutput" ></div>
    </div>
    <div id="recentSearches" style="display:none;">
      <fmt:message key="sbox.recentsearches"/>
      <span><a href="#" id="recentClear"><fmt:message key="sbox.clearrecent"/></a></span>
    </div>
  </div>
</form>