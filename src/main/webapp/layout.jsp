<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
	<vwb:Include page="/commonheader.jsp" />
	<fmt:setBundle basename="templates.default" />
		<title><fmt:message key="view.title.view">
				<fmt:param>
					<vwb:applicationName />
				</fmt:param>
				<fmt:param>
					<vwb:viewportTitle />
				</fmt:param>
			</fmt:message>
		</title>
	</head>
	<body class="view">
		<div class="DCT_body">
			<vwb:Include page="Header.jsp" />
			<c:if test="${RenderContext.topMenu!=null}">
				<vwb:modeCheck status="Full">
					<div id="nav" class="DCT_nav_bg" onmouseover="displayNav()"
						onmouseout="document.getElementById('nav_edit_buttom_id').style.display='none'">
						<div class="DCT_nav">
							<vwb:TopMenu content="${RenderContext.topMenu}" />
						</div>
						<div id="nav_edit_buttom_id"
							style="position:absolute;display:none;">
							<vwb:floatEdit editId="${RenderContext.topMenu.id}"
								viewPort="${RenderContext.content.id}" />
						</div>
					</div>
				</vwb:modeCheck>
				<vwb:modeCheck status="View">
					<div id="nav" class="DCT_nav_bg">
						<div class="DCT_nav">
							<vwb:TopMenu content="${RenderContext.topMenu}" />
						</div>
					</div>
				</vwb:modeCheck>
				<div class="DCT_center_t_d">
				</div>
			</c:if>
			
			<div id="content" class="DCT_center">
				<c:if test="${RenderContext.leftMenu!=null}">
					<vwb:modeCheck status="Full">
						<div id="leftcol" class="DCT_center_left"
							onmousemove="displayLeftMenu()"
							onmouseout="document.getElementById('leftmenu_edit_buttom_id').style.display='none';">
							<div class="DCT_liebiao">
								<div style="position : absolute;display:none;margin-top: 6px;"
									id="leftmenu_edit_buttom_id">
									<vwb:floatEdit editId="${RenderContext.leftMenu.id}"
										viewPort="${RenderContext.content.id}" />
								</div>
								<div id="left" class="DCT_bar">
									<vwb:LeftMenu content="${RenderContext.leftMenu}" />
								</div>
							</div>
							<div class="DCT_Online_U">
								<vwb:InsertPage page="5"/>
							</div>
						</div>
					</vwb:modeCheck>
					<vwb:modeCheck status="View">
						<div id="leftcol" class="DCT_center_left">
							<div class="DCT_liebiao">
								<div id="left" class="DCT_bar">
									<vwb:LeftMenu content="${RenderContext.leftMenu}" />
								</div>
							</div>
						</div>
					</vwb:modeCheck>
					<c:set var="rightclass">DCT_center_right</c:set>
				</c:if>
				<div id="center_right" class="${rightclass}">
					<c:if test="${RenderContext.showTrail}">
						<div>
							<div id="breadcrumbs" class="DCT_postion">
								<fmt:message key="header.yourtrail" />
								<vwb:Parent />
								<vwb:CheckRequestContext context="view">
								<jsp:include page="/jsp/aone/pageBar.jsp"></jsp:include>
								</vwb:CheckRequestContext>
							</div>
							<div class="DCT_clear"></div>
						</div>
					</c:if>
					<vwb:Content />
				</div>
				<div class="DCT_clear"></div>
			</div>
			<div class="DCT_clear"></div>
			<c:if test="${RenderContext.footer!=null}">
			<div id="bottom">
				<vwb:Footer content="${RenderContext.footer}" />
				<vwb:Include page="Version.jsp" />
			</div>
			</c:if>
		</div>
		<vwb:modeCheck status="Full">
		<script type="text/javascript">

			function displayLeftMenu()
			{
			  var pos = $('#leftcol').offset();
			  var edit_button=document.getElementById('leftmenu_edit_buttom_id');
			  edit_button.style.display='block';
			  edit_button.style.left =pos.left+document.getElementById('leftcol').offsetWidth-40+"px";
			}
			function displayNav()
			{
			  var pos = $('#nav').offset();
			  var edit_button = document.getElementById('nav_edit_buttom_id');
			  edit_button.style.display='block';
			  edit_button.style.left =pos.left+document.getElementById('nav').offsetWidth-40+"px";
			  edit_button.style.top=pos.top+10+'px';   
			}
		</script>
		</vwb:modeCheck>
	</body>
</html>
