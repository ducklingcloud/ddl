<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />


	<div id="addSingleTagDialog" class="lynxDialog" style="top:10%">
			<div class="toolHolder light">
				<h3>添加标签</h3>
			</div>
			<!-- vera modify -->
			<div class="inner">
				<p class="tokerInput-p"><!-- <label>输入标签（回车确认添加）：<br/> -->
					<input type="text" name="typeTag" class="tagPoolAutoShow" />
				</p>
				<div class="existTags">
					<div class="tagShow">
						<p class="change">全部公有标签：</p>
						<ul class="tagTogether"></ul>
						<ul class="hideMe" style="display:none;"></ul>
					</div>
					<div class="tagShow self">
						<p>部分公有标签：</p>
						<ul class="tagSelf"></ul> 
					</div>
					<div class="tagShow">
						<p class="change2">本次新增标签：</p>
						<ul class="tagCreate"></ul>
					</div>
					<div class="tagShow">
						<p class="self">系统已有标签：</p>
						<ul class="tagList"></ul>
					</div>
				</div>
				<!-- <p class="ui-clear">常用标签：</p> -->
				<div class="tagGroupHorizon">
					<div class="tG-scroll"></div>
				</div>
			</div>
			<div class="control largeButtonHolder">
				<a class="saveThisTagDialog btn btn-primary">保存</a>
				<a class="closeThisTagDialog btn">取消</a>
			</div>
		</div>