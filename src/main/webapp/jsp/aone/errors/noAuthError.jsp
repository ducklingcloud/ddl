<%@ page language="java"  import="java.util.*" pageEncoding="utf-8"%>
<html>
<head><title>No Auth Page</title></head>
<body>
<H2>${exception.message}</H2>
${exception}<br/>
<br/>
三秒后跳转
<a href="${exception.redirectURL}">返回团队首页</a>
<P/>
</body>
</html>
