<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />

<jsp:include page="tagMenu.jsp"/>
<div id="starMark" class="content-menu-body">
<div class="innerWrapper">
	<div id="resourceList-header" class="toolHolder">
		<div class="ui-RTCorner">
			<ul id="viewSwitch" class="switch" style="float:left;">
				<li id="showAsTable"><a title="列表显示"><span class="iconLynxTag icon-listView"></span></a></li>
				<li id="showAsTight"><a title="紧凑列表"><span class="iconLynxTag icon-tightView"></span></a></li>
				<li id="showAsGrid"><a title="缩略图显示"><span class="iconLynxTag icon-gridView"></span></a></li>
			</ul>
		</div>
		<h3>加星标的文档</h3>
	</div>
	
	<ul id="resourceList">
		<c:if test="${empty resourceList}">
			<p class="NA large">您还没有标记过重要的文档</p>
			<div class="NA">
				<p>星标用于标记您认为重要的文档。</p>
				<p>您可以点击文档标题前方的<span class="iconLynxTag icon-checkStar"></span>图标进行标记。</p>
				<p>标记的资源将显示一个醒目星标<span class="iconLynxTag icon-checkStar checked"></span>。</p>
			</div>
		</c:if>
	
		<c:forEach var="item" items="${resourceList}">
		<li class="element-data" id="item_id_${item.rid}">
			<div class="oper">
				<div class="iconLynxTag icon-checkStar checked"></div>
			</div>
			<div class="resBody">
				<h2><a class="page-link" value="${item.rid}" itemtype="${item.itemType}">
				<c:choose>
					<c:when test="${item.itemType eq 'Bundle'}">
						<span class="headImg ${item.itemType }"></span>
					</c:when>
					<c:when test="${item.itemType eq 'DPage'}">
						<span class="headImg ${item.itemType }"></span>
					</c:when>
					<c:otherwise>
						<span
							class="headImg ${item.itemType } ext ${item.fileType}"></span>
					</c:otherwise>
				</c:choose>
					${item.title }</a>
				</h2>
				<div class="resChangeLog">
					<span>${item.lastEditorName} 修改于<fmt:formatDate value="${item.lastEditTime}" pattern="yyyy-MM-dd HH:mm:ss"/></span>	
					<c:if test="${item.itemType != 'Bundle'}">
						<span>版本 ：${item.lastVersion}</span>
					</c:if>
				</div>
			</div>
			<a rid="${item.rid}" class="largeButton dim removeStar">移除星标</a>
			<c:if test="${item.itemType eq 'Bundle'}">
			<div class="showBundleChild" style="display:none">
				<ul class="bundleChildren" id="child-list-${item.rid }">
					<c:forEach items="${bundleItemMap.get(item.rid) }" var="child">
					<li rid="${child.rid }" bid="${child.bid }">
						<a><span class="headImg ${child.itemType } ex ${child.fileType}"></span>${child.title}</a>
					</li>
					</c:forEach>
				</ul>
				<c:if test="${bundleItemMap.get(item.rid).size()>=5 }">
				<div class="moreItems">
					<a href="<vwb:Link context='bundle' page='${item.rid}' format='url'/>">></a>
				</div>
				</c:if>
			</div>
			</c:if>
		</li>
		</c:forEach>
	</ul>
	<div class="ui-clear"></div>
</div>
</div>
<div class="ui-clear"></div>

<script type="text/javascript">
$(document).ready(function(){
/* view mode: table, tight or grid */
	var viewMode = $.cookie('tagItems-viewMode') || 'Table';
	
	$('#showAsTable a').click(function(){
		viewMode = 'Table';
		$('#resourceList').addClass('asTable')
			.removeClass('grid9').removeClass('asTight')
			.pitfall('clean');
		
		$(this).parent().parent().find('.chosen').removeClass('chosen');
		$(this).parent().addClass('chosen');
		$.cookie('tagItems-viewMode', 'Table');
	});
	$('#showAsGrid a').click(function(){
		viewMode = 'Grid';
		$('#resourceList').addClass('grid9')
			.removeClass('asTable').removeClass('asTight')
			.pitfall('clean');
	
		$(this).parent().parent().find('.chosen').removeClass('chosen');
		$(this).parent().addClass('chosen');
		$.cookie('tagItems-viewMode', 'Grid');
	});
	$('#showAsTight a').click(function(){
		viewMode = 'Tight';
		$('#resourceList').addClass('asTight')
			.removeClass('grid9').removeClass('asTable')
			.pitfall('clean');
		
		$(this).parent().parent().find('.chosen').removeClass('chosen');
		$(this).parent().addClass('chosen');
		$.cookie('tagItems-viewMode', 'Tight');
	})
	$('#showAs' + viewMode + ' a').click();
/* END view mode */

	$("a.removeStar").click(function(){
		var url = "<vwb:Link context='starmark' format='url'/>?func=remove";
		ajaxRequest(url,"rid="+$(this).attr("rid"),function(data){
			if (viewMode=='Grid') {
				$("#item_id_"+data.rid).fadeOut('slow', function(){
					$(this).remove();
					$('#resourceList').pitfall();
				});
			}
			else {
				$("#item_id_"+data.rid).slideUp('slow', function(){
					$(this).remove();
				});
			}
		});
	});
	
	$("a.page-link").live('click',function(){
		  if($(this).attr("itemtype")=='DPage')
			  	window.location.href = site.getViewURL($(this).attr("value"));
		   else if($(this).attr("itemtype")=="DFile")
				window.location.href = site.getURL('file',$(this).attr("value"));
		   else
				window.location.href = site.getURL('bundle',$(this).attr("value"));
	});
	
	/* show the bundle children when hovered*/
	  $("li.element-data").live("hover",function(){
		  $(this).find("div.showBundleChild").show();
	  });
	  
	  $("li.element-data").live("mouseleave",function(){
		  $(this).find("div.showBundleChild").hide();
	  }); 

	  /* 点击Bundle的子元素的事件*/
	  $("ul.bundleChildren a").live("click", function(){
		  var rid = $(this).parent("li").attr('rid');
		  var bid = $(this).parent("li").attr('bid');
		  window.location.href = site.getURL('bundle',bid)+"?rid="+rid;
	  });
});
</script>

