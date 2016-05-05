<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<ul class="a1-feed">
<c:forEach items="${compositeList}" var="cn" >
	<li>
		<div class="a1-feed-leadingImg">
			<span class='headImg ${cn.records[0].target.type} as ${resourceRidKeyMap[cn.records[0].target.id].fileType}'></span>
		</div>
		<c:set var="resourceId" value="${cn.records[0].target.id }" ></c:set>    
		<div class="a1-feed-body">
			<h4>
				<%-- <span class="notice-target-type-${cn.records[0].target.type}">${cn.records[0].target.typeDisplay}</span> --%>
				<vwb:ResourceName target="${cn.records[0].target}" resourceMap="${resourceMap}"/>
			</h4>
			<c:set var="uploadCount" value="0"/>
			<c:set var="createCount" value="0"/>
			<c:set var="modifyCount" value="0"/>
			<c:set var="renameCount" value="0"/>
			<c:set var="commentCount" value="0"/>
			<c:set var="moveCount" value="0"/>
			<c:forEach items="${cn.records}" var="n" varStatus="status">
				<c:choose>
					<c:when test="${n.operation.name eq 'create'}"><c:set var="createCount" value="${createCount+1}" /></c:when>
					<c:when test="${n.operation.name eq 'modify'}"><c:set var="modifyCount" value="${modifyCount+1}" /></c:when>
					<c:when test="${n.operation.name eq 'rename'}"><c:set var="renameCount" value="${renameCount+1}" /></c:when>
					<c:when test="${n.operation.name eq 'upload'}"><c:set var="uploadCount" value="${uploadCount+1}" /></c:when>
					<c:when test="${n.operation.name eq 'comment'}"><c:set var="commentCount" value="${commentCount+1}" /></c:when>
					<c:when test="${n.operation.name eq 'teamMove' }"><c:set var="moveCount" value="${moveCount+1 }"></c:set></c:when>
				</c:choose>
				<c:choose>
					<c:when test="${status.count < 3}"><c:set var="noticeDisplayFlag" value="display-notice"/></c:when>
					<c:otherwise><c:set var="noticeDisplayFlag" value="hidden-display-notice"/></c:otherwise>
				</c:choose>
				<p class="${n.noticeStatus}-notice ${noticeDisplayFlag}">
					<span class="time ui-RTCorner"><fmt:formatDate value="${n.occurTime}" type="time" /></span>
					<c:choose>
						<c:when test='${n.operation.name eq "recommend"}'>
							<span class="notice-actor"><a href="${n.actor.url}" title="${n.actor.id}">${n.actor.name}</a></span>
							<span>把${cn.records[0].target.typeDisplay}</span>
							<span class="notice-target-operation-recommend">${n.operation.displayName}给</span>
							<span class="notice-recommend-recipients">${n.additionDisplay}</span>
							<span class="notice-recommend-message">"${n.message}"</span>
						</c:when>
						<c:when test='${n.operation.name eq "comment"}'>
							<span class="notice-actor"><a href="${n.actor.url}" title="${n.actor.id}">${n.actor.name}</a></span>
							<span class="notice-target-operation-${n.target.type}">${n.operation.displayName}了${cn.records[0].target.typeDisplay}</span>
							<span class="notice-comment-message">${n.message}</span>
						</c:when>
						<c:when test='${n.operation.name eq "rename"}'>
							<span>被</span>
							<span class="notice-actor"><a href="${n.actor.url}" title="${n.actor.id}">${n.actor.name}</a></span>
							<span class="notice-target-operation-${n.target.type}">${n.operation.displayName}了，</span>
							<span>原名为</span>
							<span class="notice-target-new-name"><a href="${n.target.url}">${n.message}</a></span>
							<c:if test="${(n.target.type eq 'DPage') or (n.target.type eq 'DFile') }">
								<span class="notice-target-version">版本${n.targetVersion}</span>
							</c:if>
						</c:when>
						<c:when test='${n.operation.name eq "reply"}'>
							<span class="notice-actor"><a href="${n.actor.url}" title="${n.actor.id}">${n.actor.name}</a></span>
							<span class="notice-target-operation-${n.target.type}">${n.operation.displayName}了${n.target.typeDisplay}</span>
							<span class="notice-comment-message">${n.message}</span>
						</c:when>
						<c:otherwise>
							<span class="notice-actor"><a href="${n.actor.url}" title="${n.actor.id}">${n.actor.name}</a></span>
							<span class="notice-target-operation-${n.operation.name}">${n.operation.displayName}了${cn.records[0].target.typeDisplay}</span>
							<c:choose>
								<c:when test="${(n.operation.name eq 'create')}"></c:when>
								<c:otherwise>
									<c:if test="${(n.target.type eq 'DPage') or (n.target.type eq 'DFile') }">
										<span class="notice-target-version">版本${n.targetVersion}</span>
									</c:if>
								</c:otherwise>
							</c:choose>
						</c:otherwise>
					</c:choose>
				</p>
			</c:forEach>
			<c:if test="${createCount+uploadCount+modifyCount+renameCount+commentCount+moveCount >2}">
			<p class="display-notice-control">
				<a class="summary-count-link">
					<c:if test="${createCount>0}">创建${createCount}次</c:if>
					<c:if test="${uploadCount>0}">上传${uploadCount}次</c:if>
					<c:if test="${modifyCount>0}">修改${modifyCount}次</c:if>
					<c:if test="${renameCount>0}">重命名${renameCount}次</c:if>
					<c:if test="${commentCount>0}">评论${commentCount}次</c:if>
					<c:if test="${moveCount>0 }">移动${moveCount}次</c:if>
				</a>
				<span class="hide-count-container"><a class="hide-count-link">收起更新</a></span>
			</p>
			</c:if>
		</div>
		<div class="ui-clear"></div>
	</li>
</c:forEach>
</ul>
<div class="ui-clear"></div>
