<%@page import="net.duckling.ddl.service.url.URLGenerator"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ page import="org.apache.log4j.*"%>
<%@ page import="java.text.MessageFormat"%>
<%@ page import="java.util.ResourceBundle"%>
<%@ page import="net.duckling.ddl.common.*"%>
<%@ page import="net.duckling.ddl.constant.Attributes"%>
<fmt:setBundle basename="CoreResources" />
<%!Logger log = Logger.getLogger("error");%>
<%
	pageContext.getOut().clear();
	VWBContext vwbcontext = VWBContext.createContext(request,"error");
	VWBSession vwbsession = vwbcontext.getVWBSession();

	String msg = null;
	Throwable exception = (Throwable) vwbsession
			.removeAttribute(Attributes.EXCEPTION_KEY);
	Throwable realcause = null;
	if (exception != null) {
		msg = exception.getMessage();
		if (msg == null || msg.length() == 0) {
			ResourceBundle rb = vwbcontext.getBundle("CoreResources");

			msg = MessageFormat.format(rb.getString("error.unknown"),
			new Object[] { exception.getClass().getName() });
		}

		//
		//  This allows us to get the actual cause of the exception.
		//  Note the cast; at least Tomcat has two classes called "JspException"
		//  imported in JSP pages.
		//

		if (exception instanceof javax.servlet.jsp.JspException) {
			log.debug("IS JSPEXCEPTION");
			realcause = ((javax.servlet.jsp.JspException) exception)
			.getRootCause();
			log.debug("REALCAUSE=" + realcause);
		}

		if (realcause == null)
			realcause = exception;
	}
	vwbcontext.getVWBSession().addMessage(msg);
	log.debug("Error.jsp exception is: ", exception);
	String returnPage;
	String referURL = request.getHeader("Referer");
    if (referURL!=null && referURL.startsWith(vwbcontext.getBaseURL()) ){
    	pageContext.setAttribute("hasRefer", true);
    	returnPage = referURL;
    }else{
		returnPage = DDLFacade.getBean(URLGenerator.class).getURL("switchTeam",null,null);
    }
	pageContext.setAttribute("basePath", request.getContextPath());
%>

<link rel="stylesheet" href="${basePath}/jsp/aone/css/error.css" />


		<div class="content-through">
			<div class="error-center">
				<h3><fmt:message key="error.errormessage" /></h3>
						
				<p>
					<vwb:Messages div="error" />
					<vwb:Messages action="clear" />
				</p>
				<%
				if (realcause != null) {
				%>
				<p><fmt:message key="error.exception" /></p>
				<p><%=realcause.getClass().getName()%></p>
				<p><fmt:message key="error.place" /></p>
				<%
				}
				%>
				
				<hr/>
				<p>
					<fmt:message key="error.homelink" />
					<c:if test="${hasRefer!=null}">
						<a href="<%=returnPage%>"> <fmt:message
							key="security.error.noaccess.back" /> </a>
					</c:if>
					<c:if test="${hasRefer==null}">
						<a href="<%=returnPage%>"> <fmt:message
								key="security.error.noaccess.home" /> </a>
					</c:if>
				</p>
				
			</div>
		</div>
		