<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="/WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="templates.default"/>
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/ajax/ScriptHelper.js"></script>

<div id="Editor_b_di" class="Editor_b_di"
	style="position: absolute; top: 160px; display: none; left: 350px;z-index:11000;" >
	<div class="Editor_Bn_c" id="Editor_Bn_c" style="cursor:move" >
		<div class="Editor_bn_top">
			<div class="Editor_bn_logo"></div>
			<div class="Editor_bn_shift">
			    <fmt:message key="new.page" />
			</div>
			<div class="Editor_bn_Button">
				<a onclick="ScriptHelper.closeDialog('Editor_b_di')" href="#"><img src="<%=request.getContextPath() %>/images/close_img_03.gif" />
				</a>
			</div>
		</div>


	</div>
	<div class="Editor_center">
		<form action="<vwb:Link context='team' jsp='newPage' format='url'/>" id="newPageForm" method="post">
			<div class="Find_w">
				<div class="Find">
					<fmt:message key="new.page.name" />
					:
				</div>
				<div class="Input">
					<input type="text" name="newPage" id="newPage" />
				</div>
			</div>

			<div class="Find_w">
				<div class="Find">
					<fmt:message key="new.page.parent.name" />
					:
				</div>
				<div class="Input">
				 <input disabled="disabled" type="text" name="parentPageTitle" 
						value='${page.title}' id="parentPageTitle"/>
					<input type="hidden" name="parentPage" 
						value='${RenderContext.content.id}' id="parentPage"/>
				</div>
			</div>

		</form>
	</div>
	<div class="Editor_d">
		<div class="b_Cancel" onclick="ScriptHelper.closeDialog('Editor_b_di')" style="cursor:pointer">
			<fmt:message key="new.page.cancel" />
		</div>
		<div class="b_Determine" onclick="createnewpage()"  style="cursor:pointer">
			<fmt:message key="new.page.ok" />
		</div>
	</div>

</div>

<script type="text/javascript">
var urlToModule = <%=request.getContextPath() %>/

function createnewpage()
{ 
   var myvalue = document.getElementById("newPage").value;
   if(myvalue.trim().length>0)
   {
      document.getElementById("newPageForm").submit();
   }else
   {
      alert("<fmt:message key="new.page.notnull" />");
   }
    

}
</script>