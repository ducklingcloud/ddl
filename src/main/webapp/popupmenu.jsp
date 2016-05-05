<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<div id="info-commpopup" class="DCT_hideoutmenu">
	<table cellspacing="0">
		<tr>
			<td>
				<a id="homepage" href=""><fmt:message key='comm.info.homepage' />
				</a>
			</td>
		</tr>
		<tr>
			<td>
				<a id="mailto" href=""><fmt:message key='comm.info.mailto' />
				</a>
			</td>
		</tr>
	</table>
</div>
<script type="text/javascript">
	 $(document).ready(function(){
	 	$("#info-commpopup").attachedMenu({
	 		trigger:".mobilepopup",
	 		alignX:"left",
	 		alignY:"top"
	 	}, 
	 	function(el){
	 		$('#homepage').attr("href", $(el).attr("homepage"));
			$('#mailto').attr("href","Mailto:"+ $(el).attr("user"));
	 	});
	 });
</script>