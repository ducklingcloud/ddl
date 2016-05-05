<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div id="visitor" class="a1-hideout pulldownMenu" style="width:230px; position:fixed;">
	<strong>最近下载和预览过此页面的人</strong>
	<vwb:Visitor boxStyle="a1-visitor" length="9" rid="${rid }" />
	<p class="a1-hideout-control">
		<a class="closeThis"><span>关闭</span> </a>
	</p>
</div>
