<%@ taglib uri="/WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<fmt:setBundle basename="templates.default" />
<div id="right" class="DCT_right_body">
	<div id="tab21" class="DCT_tabmenu">
		<div class="DCT_Button_1">
			<ul>
				<li>
					<a id="menu-pagecontent" title="<fmt:message key='actions.view'/>"
						accesskey="i" href="<vwb:LinkTo format='url'/>"><span><fmt:message
								key='view.tab' /> </span> </a>
				</li>
				<li id="poot">
					<a><fmt:message key='actions.more.icon' /> </a>
					<div id="more" class="DCT_hideoutmenu">
						<jsp:include page="/PageActions.jsp" />
					</div>
				</li>
			</ul>
		</div>
	</div>
	<div id="info">
		<vwb:render content="${content}" />
		<div class="DCT_clear"></div>
	</div>
</div>
