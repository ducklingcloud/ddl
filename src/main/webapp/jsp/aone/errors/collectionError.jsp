<%@ page language="java"  import="java.util.*" pageEncoding="utf-8"%>
<html>
<head><title>RuntimeException Page</title></head>
<body>
<H2>${exception.message}</H2>
${exception}<br/>
啊哦,您输入的集合并不存在于团队:<b></b>中
<br/>
三秒后跳转
<a href="${exception.redirectURL}">返回团队首页</a>
<P/>
</body>
</html>
