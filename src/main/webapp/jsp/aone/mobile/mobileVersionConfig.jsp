<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

		<div style="width:80%; margin:40px auto 20px; ">
		<c:if test="${!empty android}">
			Android 最新版本号
			<table class="dataTable">
				<thead>
					<tr>
						<th>版本号</th>
						<th>更新时间</th>
						<th>更新人</th>
						<th>更描述</th>
					</tr>
				</thead>
				<tr>
					<td>${android.version }</td>
					<td><fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" value="${android.createTime }"/></td>
					<td>${android.creator }</td>
					<td>${android.description }</td>
				</tr>
			</table>
		</c:if>
			<p style="margin:10px 0;">android 最新版本号配置</p>
			<form action="/system/mobileversion" method="post" style="margin:5px 0 20px;padding-bottom:20px; border-bottom:1px dotted #ccc;">
				<input type="hidden" name="func" value="createNewVersion">
				<input type="hidden" name="type" value="android">
				版本号：<input type="text" name="mobileVersion"><br/>
				版本更新描述：<textarea rows="" cols="" name="description"></textarea><br/>
				<input type="submit" value="提交">
			</form>
		<c:if test="${! empty ios}">
			iPhone 最新版本号
			<table class="dataTable">
				<thead>
					<tr>
						<th>版本号</th>
						<th>更新时间</th>
						<th>更新人</th>
						<th>更描述</th>
					</tr>
				</thead>
				<tr>
					<td>${ios.version }</td>
					<td><fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" value="${ios.createTime }"/></td>
					<td>${ios.creator }</td>
					<td>${ios.description }</td>
				</tr>
			</table>
		</c:if>
			<p style="margin:10px 0;">ios 最新版本号配置</p>
			<form action="/system/mobileversion" method="post">
				<input type="hidden" name="func" value="createNewVersion">
				<input type="hidden" name="type" value="ios">
				版本号：<input type="text" name="mobileVersion"><br/>
				版本更新描述：<textarea rows="" cols="" name="description"></textarea><br/>
				<input type="submit" value="提交">
			</form>
		</div>
