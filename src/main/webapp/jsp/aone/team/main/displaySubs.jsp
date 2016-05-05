<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<script type="text/javascript">

	var url = "<vwb:Link format='url' context='myspace'/>?func=updateSubscriptionStatus";
	ajaxRequest(url,null,afterUpdateSubscriptionStatus);
	
	function afterUpdateSubscriptionStatus(data){
		//TODO
	};
	
</script>

<div class="subHolder">
	<a href="<vwb:Link context='teamHome' format='url'/>?func=adminSubs" class="ui-RTCorner iconLink config">管理我的关注</a>
	<div class="ui-clear"></div>
</div>

<c:choose>
	<c:when test="${empty feedMessages}">
		<p class="a1-feed-none">最近没有关注的内容发生更新。</p>
	</c:when>
	<c:otherwise>
		<ul class="a1-feed">
			<c:forEach var="item" items="${feedMessages}">
				<c:choose>
					<c:when test="${item.type=='subscription'}">
						<li <c:if test="${item.status==0 and isAllDisplay}">class="a1-feed-unread"</c:if>>
							<div class="a1-feed-leadingImg -modify"></div>
							<div class="a1-feed-body">
								<span class="a1-feed-source">
									<c:forEach items="${item.operationList}" var="oper" varStatus="status">
										<vwb:DiffLink version="${item.diffList[status.index]}" newVersion="${item.diffList[status.index]-1}" page='${item.rid}'>
											<span>
												${oper}<fmt:formatDate value="${item.timeList[status.index]}"  pattern="yyyy-MM-dd HH:mm:ss"/><br/>
											</span>
										</vwb:DiffLink>
									</c:forEach>
									<span>.............</span>
								</span>
								<h4>
									<a class="a1-feed-title feed-link"  
										href="<vwb:Link context='view' page='${item.pageId}' format='url'/>">
									${item.title}</a>发生修改
								</h4>
								<p class="a1-digest" style="width:74%">${item.digest}...</p>
							</div>
							<div class="a1-feed-op">
								<p><a class="a1-opLink a1-feed-hide"><span>取消关注</span></a></p>
							</div>
							<div class="ui-clear"></div>
						</li>
					</c:when>
					<c:when test="${item.type=='person'}">
						<li <c:if test="${item.status==0 and isAllDisplay}">class="a1-feed-unread"</c:if> >
							<div class="a1-feed-leadingImg -person"></div>
							<div class="a1-feed-body">
								<h4>
									<a class="a1-feed-title feed-link"
									  href="<vwb:Link context='user' page='${item.from.id}' format='url'/>"
									 pageId="${item.from.id}"  url="${item.from.url}" messageType="person">${item.from.name}</a>的活动
								</h4>
								<ol class="a1-digest">
									<c:forEach items="${item.actions}" var="activity" varStatus="status">
									<li>
										<span class="ui-RTCorner a1-time"><fmt:formatDate value="${activity.time}" pattern="yyyy-MM-dd HH:mm:ss"/></span>
										${activity.act} <a class="feed-link" href="${activity.url}" messageType="person" pageId="${item.from.id}">${activity.title}</a>
									</li>
									</c:forEach>
								</ol>
							</div>
							<div class="ui-clear"></div>
						</li>
					</c:when>
					<c:otherwise>
						<!-- 页面的评论更新 -->
						<li <c:if test="${item.status==0 and isAllDisplay}">class="a1-feed-unread"</c:if>>
							<div class="a1-feed-leadingImg -talk"></div>
							<div class="a1-feed-body">
								<span class="ui-RTCorner a1-time">
									<fmt:formatDate value="${item.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
								</span>
								<h4>
								<c:forEach var="sender" items="${item.senderMap}">
									<a class="a1-feed-author user-link" 
									href="<vwb:Link context='user' page='${sender.value.id}' format='url'/>">${sender.value.name}</a>
								</c:forEach>
									在页面
									<a class="a1-feed-title feed-link"  
									href="<vwb:Link context='view' page='${item.pageId}' format='url'/>"
									pageId="${item.pageId}" messageType="feed_comment" >
										${item.title}</a>发表评论</h4>
									<div class="a1-feed-comment">
										<p>
										<c:forEach var="cItem" items="${item.contentList}" varStatus="status">
											${cItem}<br/>
										</c:forEach>
										</p>
									</div>
							</div>
							<div class="ui-clear"></div>
						</li>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</ul>
	</c:otherwise>
</c:choose>