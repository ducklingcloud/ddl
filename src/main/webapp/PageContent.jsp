<%@ taglib uri="/WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<fmt:setBundle basename="templates.default" />
<div id="right" class="DCT_right_body">
	<vwb:modeCheck status="Full">

		<div id="tab21" class="DCT_tabmenu">
			<div class="DCT_Button_1">
				<jsp:include page="/jsp/page/newpage.jsp"></jsp:include>
				<ul>
					<vwb:CheckRequestContext context='view|info|diff|upload'>
						<vwb:Permission permission="edit">
							<li>
								<a
									onclick="ScriptHelper.showDialog('Editor_b_di', 'Editor_Bn_c')"
									id='new_page' href="#"><fmt:message key='new.page' /> </a>
							</li>
							<!--
							<li>
								<a href="<vwb:EditLink page='5930' format='url'/>" class="action edit"><fmt:message key="aone.new.column"/></a>
							</li>
							  -->
							<li>
								<a href="<vwb:EditLink format='url' />" accesskey="e"
									class="action edit"
									title="<fmt:message key='actions.edit.title'/>"><fmt:message
										key='actions.edit' /> </a>
							</li>
						</vwb:Permission>
					</vwb:CheckRequestContext>
					<li id="poot">
						<a><fmt:message key='actions.more.icon' /> </a>
						<div id="more" class="DCT_hideoutmenu">
							<jsp:include page="/PageActions.jsp" />
						</div>
					</li>
				</ul>
			</div>
		</div>
		<div id="pagecontent">
			<div id="DCT_viewcontent">
				<jsp:include page="/PageTab.jsp" />
			</div>
		</div>
	</vwb:modeCheck>
	<vwb:modeCheck status="View">
		<div id="pagecontent">
			<div id="DCT_viewcontent">
				<jsp:include page="/PageTab.jsp" />
			</div>
		</div>
	</vwb:modeCheck>
	<div class="DCT_clear"></div>
</div>
<vwb:modeCheck status="Full">
	<jsp:include page="/PageActionsBottom.jsp" />
</vwb:modeCheck>
<jsp:include page="/jsp/aone/comment/displayComment.jsp"></jsp:include>
<script type="text/javascript">
function changeByTagName(pageTag)
{
  if('attachmentTag' == pageTag)
  {
    document.getElementById("pagecontent").className = "DCT_hidetab";
    document.getElementById("menu-attach").className = "activetab";
    document.getElementById("menu-pagecontent").className = "";  
    document.getElementById("attach").className = "";
    document.getElementById("attach").style.display = "";
  }
}
changeByTagName('<%=request.getParameter("pageTag")%>');
</script>
