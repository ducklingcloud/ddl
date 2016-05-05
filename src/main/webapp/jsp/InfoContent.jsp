<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/tld/vwb.tld" prefix="vwb"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<fmt:setBundle basename="templates.default" />

<script language="javascript">
$(document).ready(function(){
	$('body').addClass('fullFunction');
	$('table.dataTable tbody tr:nth-child(even)').addClass('striped');
	
	$(".recoverPageVersion").live("click",function(){
		var version = $(this).find("input[name='version']").val();
		var tid = $(this).find("input[name='tid']").val();
		var aa= window.confirm("您确定要恢复此版本的文本文档吗？");
		if (aa) {
			window.location.href="<vwb:Link context='info' format='url' page='${rid}'/>"+"&func=recoverVersion&tid="+tid+"&version="+version;
		}
	});
	
	$("#goBack").live("click",function(){
		var url="<vwb:Link context='viewFile' format='url'  page='${rid }' />";
		window.location.href=url;
	});
	
});
function submitRename(pagename) {
	var renameto = document.getElementById("renameto").value;
	if ((renameto == null) || (renameto.trim() == ""))
    {
    	alert("info.renameto".localize());
	}
	else 
	{	
		if (pagename.toLowerCase().trim() == renameto.toLowerCase().trim())
		{
			alert("info.samename".localize());	
			
		}
		else
		{
		    var pageNameRule = /^([a-zA-Z0-9]|[-_]|[^\x00-\xff]){1,255}$/;
	        if (!pageNameRule.exec(renameto))
	        {
		       alert("pagename.rules.tip".localize());
		    }else
		    {
		      document.renameform.submit();
		    }	
		}
				
	}
    
}
</script>

<style>
#tab21.DCT_tabmenu { display:none; }
#right.DCT_right_body { padding:0; margin:0; border:none; }
#info { padding:0 !important; }
#content .toolHolder.control input[name=back] { margin-left:10px; }
#content .toolHolder.control h1 { display:inline; font-size:1.5em; margin:0.5em 0 0 0.5em; }

#versionInfo.sideBlock h4 { line-height:2em; vertical-align:bottom; }
#versionInfo.sideBlock h4 .versionText {}
#versionInfo.sideBlock h4 .versionDigit { padding:0; font-size:2em; line-height:1em; font-weight:bold;}

#incomingLinks.sideBlock a, #outgoingLinks.sideBlock a {
	display:block; margin:5px 1em;
}
	
</style>

<%-- PageExists --%>
<%-- part 1 : normal pages --%>


<div class="toolHolder control">
	<input type="button" name="back" value="返回"  id="goBack"/>
	<h1><vwb:Variable key='pagetitle' /></h1>
</div>

<div class="content-major">
	<div class="innerWrapper">
	<%-- DIFF section --%>
	<vwb:CheckRequestContext context='diff'>
		<vwb:Include page="DiffTab.jsp" />
	</vwb:CheckRequestContext>
	<%-- DIFF section --%>

	<vwb:CheckVersion mode="first" rid="${rid}">
		<p class="a1-feed-none"><fmt:message key="info.noversions" /></p>
	</vwb:CheckVersion>
	<vwb:CheckVersion mode="notfirst" rid="${rid}">

		<vwb:SetPagination start="${startitem}" total="${itemcount}" pagesize="${pagesize}" rid="${rid}" urlPatterns="info"  maxlinks="9" fmtkey="info.pagination" href='${baseURL})' />

		<div class="zebra-table sortable table-filter">
			<table class="dataTable merge">
			<thead>
				<tr>
					<td class="dtDigit" style="width:3em"><fmt:message key="info.version" /></td>
					<td class="dtTitle">标题</td>
					<td class="dtTime"><fmt:message key="info.date" /></td>
					<td class="dtStd"><fmt:message key="info.author" /></td>
					<td>版本区别</td>
					<td>恢复</td>
				</tr>
			</thead>
			
			<tbody>
				<c:forEach items="${versionList}" var="currentPage">
						<tr>
							<td class="dtRight">
								<%-- <vwb:LinkTo version="${currentPage.version}"></vwb:LinkTo> --%>
								<a href="<vwb:Link context='viewFile' format='url' page='${rid}'/>?version=${currentPage.version}">版本${currentPage.version} </a>
							</td>
							<td>
								${currentPage.title }
							</td>
							<td>
								<fmt:formatDate value="${currentPage.editTime }" type="both" dateStyle="medium"/>
							</td>
							<td title="${currentPage.editor}">${currentPage.editorName}</td>
							<td>
								<c:choose>
									<c:when test="${currentPage.version == 1 }">
									</c:when>
									<c:otherwise>
										<a href="<vwb:Link context='info' format='url' page='${rid}'/>&func=diff&r1=${currentPage.version}&r2=${currentPage.version-1}">版本区别</a>
									</c:otherwise>
								</c:choose>
							</td>
							<td>
								<c:if test="${currentPage.version != lastversion}">
									<span class="recoverPageVersion">
										<input type="hidden" name="rid" value="${currentPage.rid }">
										<input type="hidden" name="version" value="${currentPage.version }">
										<input type="hidden" name="tid" value="${currentPage.tid }">
										<a title="恢复该版本">恢复</a>
									</span>
								</c:if>
							</td>
						</tr>
				</c:forEach>
			</tbody>
			</table>
		</div>
		${pagination}
	</vwb:CheckVersion>
	</div>
</div>
<div class="content-side">
<div class="innerWrapper">
	<div class="sideBlock" id="versionInfo">
		<h4>
			<span class="versionDigit ui-RTCorner">
				${version}
			</span>
			<span class="versionText">版本</span>
		</h4>
		<div class="ui-clear"></div>
		
		<table class="ui-table-form-2col">
		<tr><th>修改者：</th>
			<td>${editor}</td>
		</tr>
		<tr><th>修改时间：</th>
			<td><fmt:formatDate value="${editTime}" type="both" dateStyle="medium"/></td>
		</tr>
		<tr><th>创建者：</th>
			<td>${creator }</td>
		</tr>
		<tr><th>创建时间：</th>
			<td>
				<fmt:formatDate value="${createTime}" type="both" dateStyle="medium"/><br/>
			</td>
		</tr>
		</table>
	</div>
	
	<div class="sideBlock" id="incomingLinks">
		<h4>
			<fmt:message key="info.tab.incoming" />
		</h4>
		<vwb:LinkTo>
			<vwb:PageTitle />
		</vwb:LinkTo>
	</div>
	
	<div class="sideBlock" id="outgoingLinks">
		<h4>
			<fmt:message key="info.tab.outgoing" />
		</h4>
	</div>
</div>
</div>


<vwb:NoSuchPage>
<div class="content-through">
	<fmt:message key="common.nopage">
		<fmt:param>
			<vwb:EditLink>
				<fmt:message key="common.createit" />
			</vwb:EditLink>
		</fmt:param>
	</fmt:message>
</div>
</vwb:NoSuchPage>
