<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<html>
<head><title>Data Base Exception Page Handler!</title></head>
<body>
<% Exception ex = (Exception)request.getAttribute("exception"); %>
<H2>Exception: <%= ex.getMessage()%></H2>
<P/>
<% ex.printStackTrace(new java.io.PrintWriter(out)); %>
</body>
</html>
