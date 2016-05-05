<%@ page language="java" import="cn.vlabs.duckling.vwb.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<fmt:setBundle basename="templates.default" />
<script type="text/javascript">
//add  by  diyanliang 09-4-20 for FullScreen on Cookie

var contextPath='${contextPath}';

//end 09-4-20
var groupListTimeOutVar = null
function apploginfunc(str){
	if (groupListTimeOutVar != null)window.clearInterval(groupListTimeOutVar);
	if($(str).is(":hidden")){
		$(str).show("fast");
	}else{
		$(str).hide();
	}

}
function hidemygroupList(){
	if (groupListTimeOutVar != null)
		window.clearInterval(groupListTimeOutVar);
	groupListTimeOutVar = window.setInterval("document.getElementById('mygroupList').style.display = 'none'", 800);
}
function showmygroupList(){
	if (groupListTimeOutVar != null) {
		window.clearInterval(groupListTimeOutVar);
		groupListTimeOutVar = null;
	};
	$('#mygroupList').show("fast");
}
</script>
<style>
.ppcontent {
    height: 128px;
    /*padding: 7px 0 0 8px;*/
    width: 238px;
}
.passportc {
    background: none repeat scroll 0 0 #FFFFFF;
    border: 1px solid #FFA200;
    font-size: 12px;
    height: 108px;
    text-align: left;
    width: 238px;
}

.passportc .card {
    color: #313031;
    font-weight: normal;
    padding: 0 0 0 25px;
}
.passportc ul, .passportc ol, .passportc li {
  color: #313031;
    list-style: none outside none;
}

</style>
<div class="DCT_Login_di" id="DCT_Login_di">
<%-- 
	<!-- //科技网项目要求加入的功能  start  -->
	<vwb:UserCheck status="authenticated">
	<div id="CSTNET" onmouseout="hidemygroupList()" >
	
	<span id="mygroupbutton" onmousedown="apploginfunc('#mygroupList')"><fmt:message key="duckling.userbox.myvo" /></span>
	<div style="display:none;" id="mygroupList" class="mygroupList DCT_hideoutmenu" onmouseover="showmygroupList()">   
	<vwb:SubPage pageid="1(7,6,5,4,3);2"/> 
	</div>
	<c:if test="${PORTAL_SESSION!=null&&PORTAL_SESSION.currentUser.authBy!=null&&'umt'!=PORTAL_SESSION.currentUser.authBy}">
	    <% String umt = VWBContainerImpl.findContainer().getConfig().getProperty("duckling.umt.site");
	        request.setAttribute("logincstnetURL",umt+"/user/loginThirdPartyApp");
	        request.setAttribute("loginOnlineStorageURL",umt+"/user/onlineStorageLoginServlet");
	     %>
		<ul class="DCT_Landing_ul">
			<li><a href="${logincstnetURL}" target="_blank">科技网邮箱 </a></li>
			<li><a href="${loginOnlineStorageURL}">网络硬盘 </a></li>
		</ul>
	</c:if>
	</div>
	</vwb:UserCheck>
	 <!--  //科技网项目要求加入的功能 end  -->

--%>	
	<div class="DCT_Landing">
		<ul class="DCT_Landing_ul">
			<vwb:UserCheck status="anonymous">
				<li>
					<fmt:message key="fav.greet.anonymous" />
				</li>
			</vwb:UserCheck>
			<vwb:UserCheck status="asserted">
				<li>
					<fmt:message key="fav.greet.asserted">
						<fmt:param>
							<vwb:UserTrueName />
						</fmt:param>
					</fmt:message>
				</li>
			</vwb:UserCheck>
			<vwb:UserCheck status="authenticated">
				<li>
					<fmt:message key="fav.greet.authenticated">
						<fmt:param>
							<vwb:UserTrueName />
						</fmt:param>
					</fmt:message>
				</li>
			</vwb:UserCheck>
			<vwb:UserCheck status="notAuthenticated">
				<vwb:CheckRequestContext context='!login'>
					<vwb:Permission permission="login">
					<!--
					<li>
							<a onmousedown="apploginfunc('#applogindiv')" href="javascript:void(0)"
								class="action login"
								title="<fmt:message key='actions.login.title'/>"><fmt:message
									key="actions.login" /> </a>
					</li>
					
					<li>
					
						<div id="applogindiv" style="position: absolute; width: 10px; height: 10px; left: 237px; top: 27px; z-index: 1500; opacity: 0.95;display: none;">
							<div class="passportc">
								<form name="loginform" onsubmit="return PassportSC.doLogin();"
									method="post">
									<div class="pptitle">
										Duckling
										<b>通行证</b>
									</div>
									<div id="ppcontid" class="ppcontent">
										<ul class="card">
											<div id="pperrmsg" class="error"></div>
											<li>
												登录名
												<input type="text" disableautocomplete="" autocomplete="off"
													style="color: gray;" value="" class="ppinput"
													name="email">
											</li>
											<li>
												密　码
												<input type="password" disableautocomplete="" autocomplete="off"
													class="ppinput" name="password">
											</li>
											<li>
												<input type="submit" alt="登 录"
													src="http://js.sohu.com/passport/images/spacer.gif"
													onfocus="this.blur()" value="登 录" class="sign">
											</li>
										</ul>
									</div>
								</form>
							</div>
							
						</div>
						
					</li>-->
						<li>
							<a href="<vwb:Link context="plain" jsp="login" format='url'/>"
								class="action login"
								title="<fmt:message key='actions.login.title'/>"><fmt:message
									key="actions.login" /> </a>
						</li>
						<li>
							<a href="<vwb:Link context='regist' format='url' absolute='true'/>" target="_blank"><fmt:message
									key="actions.register" /> </a>
						</li>
					</vwb:Permission>
				</vwb:CheckRequestContext>
			</vwb:UserCheck>
			<vwb:UserCheck status="authenticated">
				<!-- AOne -->
				<li>
					<a href="<vwb:Link format="url" page='5928'/>">
						<vwb:MessageCount />
						<c:choose>
							<c:when test="${tagMessageSize!=0}">
								<span style="color:#f90"><strong>个人中心(${tagMessageSize})</strong></span>
							</c:when>
							<c:otherwise>
								<span><strong>个人中心</strong></span>
							</c:otherwise>
						</c:choose>
					</a>
				</li>
				<!-- AOne -->
			    <li>
					<a href="<vwb:Link context="plain" jsp="logout" format='url'/>"
						class="action logout"
						title="<fmt:message key='actions.logout.title'/>"><fmt:message
							key="actions.logout" /> </a>
				</li>
				
				<li>
					<a href="<vwb:MyVOLink/>" target="_blank"><fmt:message
							key="actions.myaccount" /> </a>
				</li>
				<vwb:CheckRequestContext context='!prefs'>
					<vwb:CheckRequestContext context='!preview'>
						<li>
							<a href="<vwb:Link jsp="myPage" format="url"/>"
								class="action prefs" accesskey="p"
								title="<fmt:message key='actions.prefs.title'/>"><fmt:message
									key="actions.prefs" /> </a>
						</li>
					</vwb:CheckRequestContext>
				</vwb:CheckRequestContext>
				<vwb:modeCheck status="Full">
					<vwb:CheckRequestContext context="view|portlet">
					<li>
						<a
							href="<vwb:Link format="url"><vwb:Param name="m">0</vwb:Param></vwb:Link>"
							title="<fmt:message key='actions.viewmode.title'/>"><fmt:message
								key="actions.viewmode" /> </a>
					</li>
					</vwb:CheckRequestContext>
				</vwb:modeCheck>
				<vwb:modeCheck status="View">
					<vwb:CheckRequestContext context="view|portlet">
						<li>
							<a
								href="<vwb:Link format="url"><vwb:Param name="m">1</vwb:Param></vwb:Link>"
								title="<fmt:message key='actions.editmode.title'/>"><fmt:message
									key="actions.editmode" /> </a>
						</li>
					</vwb:CheckRequestContext>
				</vwb:modeCheck>
				<li>
					<a href="http://duckling.escience.cn/dct/Wiki.jsp?page=DCTHelp"
						target="_blank"><fmt:message key="actions.help" /> </a>
				</li>
			</vwb:UserCheck>
			<li id="FullScrLI">
				<a id="FullScrA" class="DCT_FullScrA">
					<fmt:message>javascript.public.fullscreen</fmt:message> </a>
			</li>
			<li id="langtirgger">
				<a href="#"><fmt:message key="title.language" /> </a>
			</li>
		</ul>
	</div>
</div>

<div style="display:none" class="language DCT_hideoutmenu" id="language" >
	<ul>
		<li>
			<a href="#" Locale="zh_CN" CookiePath="${contextPath}">中文</a>
			<a href="#" Locale="en_US" CookiePath="${contextPath}">English</a>
		</li>
	</ul>
</div>
<script type="text/javascript">
	$(document).ready(function(){
		$("#language>ul>li>a").each(function(i, el){
			$(el).click(function(){
				changeLocale(this);
			})
		});
		$("#language").attachedMenu({
			trigger:"#langtirgger",
			alignX:"right",
			alignY:"bottom"
		})
	});
</script>