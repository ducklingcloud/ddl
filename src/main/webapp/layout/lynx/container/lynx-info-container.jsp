<%@ taglib uri="/WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<fmt:setBundle basename="templates.default" />
<div id="right" class="DCT_right_body">
	<div id="info">
		<vwb:render content="${content}" />
		<div class="DCT_clear"></div>
	</div>
</div>
