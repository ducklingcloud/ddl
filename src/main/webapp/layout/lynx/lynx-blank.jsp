<%@ page language="java" pageEncoding="utf-8"%>
<%@ page import="net.duckling.ddl.constant.Constant"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%
String basePath = getServletContext().getRealPath("/");
String aoneVersion = Constant.getVersion(basePath);
request.setAttribute("aoneVersion", aoneVersion);
request.setAttribute("contextPath", request.getContextPath());
%>
<vwb:render content="${content}"/>