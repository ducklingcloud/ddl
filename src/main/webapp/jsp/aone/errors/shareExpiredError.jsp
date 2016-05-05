<%@ page language="java"  import="java.util.*" pageEncoding="utf-8"%>
<html>
<head><title>文件分享过期提示</title></head>
<body>
啊哦,您要下载的文件${exception.fileName}已经过了期限，
请联系${exception.userName}(${exception.user})让TA重新开放下载！
<br/>
感谢您的使用！
<a href="${exception.redirectURL}">返回团队首页</a>
<P/>
</body>
</html>
