<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
</head>
<body>

</body>
</html>
<script type="text/javascript">
		(function(){
			var u = window.location.href;
			var url = "${url}";
			var nohref = '${noHref}';
			var hrefUrl="&vwb.requesturl="+encodeURIComponent(u);
			if(nohref=='no'){
				hrefUrl="";
			}
			if(url.indexOf("?")!=-1){
				location.href = "${url}"+hrefUrl;
			}else{
				location.href = "${url}"+"?"+hrefUrl;
			}
		})();
</script>