<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ page import="net.duckling.ddl.common.VWBContext;"%>
<%
	VWBContext context = VWBContext.createContext(request,net.duckling.ddl.service.url.UrlPatterns.T_ATTACH);
%>
<html>
	<head>
		<title>Upload Document</title>
		<script src="common/fck_dialog_common.js?version=dct4.5" type="text/javascript"></script>
		<script src="fck_updateattach/fck_updateattach.js?version=dct4.5" type="text/javascript"></script>
	</head>
	<body class="InnerBody" >
	<input type="hidden" id="attachurl" value="" />
	 <iframe id="iframe" align="middle" width="100%" height="100%" src="" name="centerFrame" framespacing="0" frameborder="0"   scrolling="no"  noresize  >
    </iframe>
    <script type="text/javascript">
    function loadUpdateFile(){
      var obj=document.getElementById("iframe");
      var pureAttachParam=parsePureAttachParam();
      obj.src="<%=context.getBaseURL()%>/updateAttach?func=meta&docAttach="+pureAttachParam;
    }
    function parsePureAttachParam(){
      if(attachurl==null){
        attachurl="";
      }
      var a=attachurl.indexOf("/");
      if(a>=0){
        var param=attachurl.substring(a+1);
        return param;
      }
    }
   
</script>
	</body>
</html>