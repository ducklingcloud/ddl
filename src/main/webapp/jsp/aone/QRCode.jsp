<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>

<div id="QRCodeDiv" class="ui-sideCode" style="display: none;">
	<p class="sao">扫一扫<a id="closeQRCode">X</a></p>
	<a href="http://update.escience.cn/download/downloadDdlApp.html " target="_blank"><img alt="" height="120px" src="${contextPath}/images/mobileRcode3.png"></a>
	<p class="download">下载文档库客户端</p>
</div>
<script type="text/javascript">
$(document).ready(function(){
	function showQRCode(){
		var r = getCookie("QRCode");
		if(r&&r.length>0){
		}else{
			$("#QRCodeDiv").show();
		}
	}
	showQRCode();
	function getCookie(c_name)
	{
	if (document.cookie.length>0)
	  {
	  c_start=document.cookie.indexOf(c_name + "=")
	  if (c_start!=-1)
	    { 
	    c_start=c_start + c_name.length+1 
	    c_end=document.cookie.indexOf(";",c_start)
	    if (c_end==-1) c_end=document.cookie.length
	    return unescape(document.cookie.substring(c_start,c_end))
	    } 
	  }
	return ""
	}
	function setCookie(c_name, value, expiredays){
　　		var exdate=new Date();
		exdate.setDate(exdate.getDate() + expiredays);
		document.cookie=c_name+ "=" + escape(value) + ((expiredays==null) ? "" : ";expires="+exdate.toGMTString())+";path=/";
	}
	$("#closeQRCode").live('click',function(){
		setCookie("QRCode","QRCode",30)
		$("#QRCodeDiv").hide();
	})
	
});
</script>