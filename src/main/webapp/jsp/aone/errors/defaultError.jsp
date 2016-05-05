<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<html>
<head><title>Exception!</title></head>
<body>

<H2>${exception.message}</H2>

<P/>
<%
Exception ex = (Exception)request.getAttribute("exception");
ex.printStackTrace(new java.io.PrintWriter(out)); 
%>
</body>
</html>
