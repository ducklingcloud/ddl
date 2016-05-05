<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<!-- n 为 notice -->
<c:choose>
<c:when test="${empty noticeList }">
	<p class="a1-feed-none">最近没有新的记录</p>
</c:when>
<c:otherwise>
<ul style="word-break:break-all;overflow:hidden;">
<c:forEach items="${noticeList}" var="n" >
	<li class="${n.noticeStatus}-notice ${n.operation.name}" eventid="${n.eventId }" tid="${n.tid }" targetid="${n.target.id}">
		<div class="notice-leadImg  ${n.operation.name} position"></div>
		<c:choose>
			<c:when test="${noticeQueue eq 'SingleTeamQueue' }">
				<span class="time"><fmt:formatDate value="${n.occurTime}" type="time" /></span>
			</c:when>
			<c:otherwise>
				<span class="time"><fmt:formatDate value="${n.occurTime}" type="both" /></span>
			</c:otherwise>
		</c:choose>
		<c:choose>
			<c:when test='${n.operation.name eq "recommend"}'>
				<!-- 句式0: 某人A 把 页面/文件B 推荐给了 C,D,E ：Blalaala	 -->
				<span class="notice-actor"><a href="${n.actor.url}" title="${n.actor.id}">
				${n.actor.name}
				</a></span>
				<span>把</span>
				<span class="notice-target-type-${n.target.type}">${n.target.typeDisplay}</span>
				<span class="notice-target-name">
					<vwb:ResourceName target="${n.target}" resourceMap="${resourceMap }" onclick="updateOneNotice(this);return false"/>
				</span>
				<c:if test="${(n.target.type eq 'DPage') or (n.target.type eq 'DFile') }">
					<span class="notice-target-version">版本${n.targetVersion}</span>
				</c:if>
				<span class="notice-target-operation-recommend">${n.operation.displayName}给了</span>
				<c:choose>
					<c:when test="${noticeTab eq 'HistoryTab'}">
						<span class="notice-recommend-recipients">${n.additionDisplay}</span>
					</c:when>
					<c:otherwise>
						<span class="notice-recommend-recipients">${n.relative.name}</span>
					</c:otherwise>
				</c:choose>
			 	<c:if test='${n.message!=null&&fn:trim(n.message)!=""}'>
				 <p class="notice-recommend-message">
				 	${fn:escapeXml(n.message)}
				 </p> 
			 	</c:if>
			</c:when>
			<c:when test='${n.operation.name eq "reply"}'>
				<!-- 句式1: 某人A 回复 了 某人B 在 页面/文件C处的评论 TA说：Blalaala	 -->
				<span class="notice-actor"><a href="${n.actor.url}" title="${n.actor.id}">${n.actor.name}</a></span>
				<span class="notice-target-operation-${n.operation.name}">${n.operation.displayName}了</span>
				<span class="notice-target-type">${n.target.typeDisplay}</span>
				<span class="notice-target-name">
					<vwb:ResourceName target="${n.target}" resourceMap="${resourceMap }" onclick="updateOneNotice(this);return false"/>
				</span>
				<span class="notice-target-version">版本${n.targetVersion}</span>
				<p class="notice-comment-message"><c:out value="${n.message}"/></p>
			</c:when>
			<c:when test='${n.operation.name eq "comment"}'>
				<!-- 句式1: 某人A 评论 了 页面/文件 B TA说：Blalaala	 -->
				<span class="notice-actor"><a href="${n.actor.url}" title="${n.actor.id}">${n.actor.name}</a></span>
				<span class="notice-target-operation-${n.operation.name}">${n.operation.displayName}了</span>
				<span class="notice-target-type">${n.target.typeDisplay}</span>
				<span class="notice-target-name">
					<vwb:ResourceName target="${n.target}" resourceMap="${resourceMap }" onclick="updateOneNotice(this);return false"/>
				</span>
				<span class="notice-target-version">版本${n.targetVersion}</span>
				<span class="notice-comment-message">${n.message}"</span>
			</c:when>
			<c:when test='${n.operation.name eq "rename"}'>
				<!-- 句式2: 某人A 将 页面/文件/集合/团队oldB 重命名成了  newB -->
				<span class="notice-actor"><a href="${n.actor.url}" title="${n.actor.id}">${n.actor.name}</a></span>
				<span>将</span>
				<span class="notice-target-type">${n.target.typeDisplay}</span>
				<span class="notice-target-old-name"><a href="${n.target.url}">${n.message}</a></span>
				<span class="notice-target-operation-${n.operation.name}">${n.operation.displayName}为</span>
				<span class="notice-target-new-name">
					<vwb:ResourceName target="${n.target}" resourceMap="${resourceMap }" onclick="updateOneNotice(this);return false"/>
				</span>
				<c:if test="${(n.target.type eq 'DPage') or (n.target.type eq 'DFile') }">
					<span class="notice-target-version">版本${n.targetVersion}</span>
				</c:if>
			</c:when>
			<c:when test='${n.operation.name eq "delete"}'>
				<span class="notice-actor"><a href="${n.actor.url}" title="${n.actor.id}">${n.actor.name}</a></span>
				<span class="notice-target-operation-${n.operation.name}">${n.operation.displayName}了</span>
				<span class="notice-target-type">${n.target.typeDisplay}</span>
				<span class="notice-target-name">
					<vwb:ResourceName target="${n.target}" resourceMap="${resourceMap }" onclick="updateOneNotice(this);return false"/>
				</span>
				<c:if test="${(n.target.type eq 'DPage') or (n.target.type eq 'DFile') }">
				<!-- 	<span class="notice-target-version">版本${n.targetVersion}</span>  -->
				</c:if>
				<%-- 
				<c:if test="${n.actor.name eq'我'}">
					(<a class="recoverResource"><input type="hidden" name="targetId" value="${n.target.id }">
						<input type="hidden" name="targetType" value="${n.target.type}">
						<span>恢复文档</span> 
					</a>)
				</c:if>
				--%>
			</c:when>
			<c:when test='${n.operation.name eq "mention"}'>
				<span class="notice-actor"><a href="${n.actor.url}" title="${n.actor.id}">${n.actor.name}</a></span>
				<span>在</span>
				<span class="notice-target-type">${n.target.typeDisplay}</span>
				<span class="notice-target-name">
					<vwb:ResourceName target="${n.target}" resourceMap="${resourceMap }" onclick="updateOneNotice(this);return false"/>
				</span>
				<c:if test="${(n.target.type eq 'DPage') or (n.target.type eq 'DFile') }">
				<span class="notice-target-operation-${n.operation.name}">${n.operation.displayName}</span>
				</c:if>
				<span class="notice-comment-message">${n.message}"</span>
			</c:when>
			<c:otherwise>
				<!-- 句式3: 某人A 修改/上传/创建 了 页面/文件/集合/团队 B -->
				<span class="notice-actor"><a href="${n.actor.url}" title="${n.actor.id}">${n.actor.name}</a></span>
				<span class="notice-target-operation-${n.operation.name}">${n.operation.displayName}了</span>
				<span class="notice-target-type">${n.target.typeDisplay}</span>
				<span class="notice-target-name">
					<vwb:ResourceName target="${n.target}" resourceMap="${resourceMap }" onclick="updateOneNotice(this);return false"/>
				</span>
				<c:if test="${(n.target.type eq 'DPage') or (n.target.type eq 'DFile') }">
				<!-- 	<span class="notice-target-version">版本${n.targetVersion}</span>  -->
				</c:if>
			</c:otherwise>
		</c:choose>
	</li>
</c:forEach>
</ul>
</c:otherwise>
</c:choose>
