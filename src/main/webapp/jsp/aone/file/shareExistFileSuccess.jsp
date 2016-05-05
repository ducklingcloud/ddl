<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<div id="content-title">
	<h1>快速分享文件</h1>
</div>
<div class="content-through">
<div class="msgBox light" style="width:60%;height:280px;font-size:14px;">
<p>分享文件成功</p>
查看文件分享地址：<a href="${fileURL}">${fileName}</a>
<p><a href="<vwb:Link context='file' page='${fid}' format='url'/>">返回</a></p>
</div>
</div>