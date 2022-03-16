<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<link href="${contextPath}/jsp/aone/css/error.css" rel="stylesheet" type="text/css"/>
<link href="${contextPath}/scripts/bootstrap/css/bootstrap.min.css" rel="stylesheet" media="screen"/>
<link href="${contextPath}/scripts/bootstrap/css/bootstrap-responsive.min.css" rel="stylesheet"/>
<link href="${contextPath}/scripts/bootstrap/css/todc-bootstrap.css" rel="stylesheet"/>

<div class= "manageSpaceMain">
    <div id="opareteFileMessage" class="alert alert-success" style="display: none;z-index:9999"></div>
    <h3 style="margin-bottom:0">${team.displayName }</h3>
    <p>当前总空间为<span id="totalSize">${size.totalDisplay}</span>，已使用<span id="usedSize">${size.usedDisplay }</span></p>

    <!-- Disabled <2022-03-16 Wed> -->
    <!-- <div class="enlargeSpaceTool">
	 <a  class="btn btn-warning" style="cursor:pointer;color:#fff;" href="${contextPath}/activity/task-win-space" target="_blank">参与送空间活动</a>
         </div> -->
    
    <div class="enlargeSpaceHistory">扩容记录</div>
    <p>以下是您免费扩容的记录 合计：<span id="totalAddSize">${totalSize }</span></p>
    <div class="historyList">
	<table class="table dataTable">
	    <thead>
		<tr>
		    <td>类别</td>
		    <td>大小</td>
		    <td>日期</td>
		</tr>
	    </thead>
	    <tbody id="resordList">
		<c:forEach items="${records}" var="re">
		    <tr>
			<td>${re['type']} </td>
			<td>${re['size'] }</td>
			<td>${re['time'] }</td>
		    </tr>
		</c:forEach>
	    </tbody>

	</table>
    </div>
</div>
<script type="text/html" id="recordTmpl">
    <tr>
	<td>{{= type}}</td>
	<td>{{= size}}</td>
	<td>{{= time}}</td>
    </tr>
</script>
<script type="text/javascript" src="${contextPath}/scripts/jquery.tmpl.min.js"></script>
<script type="text/javascript" src="${contextPath}/scripts/bootstrap/js/bootstrap.min.js"></script>
