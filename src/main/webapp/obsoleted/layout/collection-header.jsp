<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div class="ui-RTCorner">
	<ul class="quickOp" id="collectinOp">
		<li class="search"><div id="globalSearch"></div><div id="searchLabel">搜索</div></li>
		<li class="icon newpage"><a href="<vwb:Link format='url' context='quick'/>?func=createPage">新建页面</a></li>
		<li class="icon upload"><a href="<vwb:Link format='url' context='quick'/>?func=uploadFiles">上传文件</a></li>
	</ul>
</div>

<div id="banner">
	<span id="bannerTitle">
		<vwb:Link context="teamHome" jsp=""><vwb:applicationName/></vwb:Link>
	</span>
</div>


<script type="text/javascript">
$(document).ready(function(){
	/* GLOBAL SEARCH */
	var globalSearch = new SearchBox('globalSearch', '', false, false, false);
	
	globalSearch.setPullDown('未找到相关资源', '搜索引擎错误', '350px');
	
	globalSearch.searchInput.focus(function(){
		globalSearch.container.addClass('loaded');
		globalSearch.container.parent().addClass('loaded');
		setTimeout(function(){ $('#searchLabel').text('搜索页面、集合'); }, 400); 
	});
	globalSearch.searchInput.blur(function(){
		if (globalSearch.searchResultState==false) {
			globalSearch.container.removeClass('loaded');
		}
		globalSearch.container.parent().removeClass('loaded');
		$('#searchLabel').text('搜索');
	});
	
	function renderCollectionResult(data){
		var html = '<li class="type">集合（'+data.size+'）</li>';
		for(var i=0;i<data.content.length;i++)
			html += '<li><a href="' + data["content"][i].url + '">' + data["content"][i].title + '</a></li>';
		return html;
	};
		
	function renderPageResult(data){
		var html = '<li class="type">页面（'+data.size+'）</li>';
		for(var i=0;i<data.content.length;i++)
			html += '<li><a href="' + data["content"][i].url + '">' + data["content"][i].title +
				'<span class="collection">' + data["content"][i].collectionName + '</span><br/>' +
				'<span class="author">' + data["content"][i].author + '</span><span class="time">'+data["content"][i].modifyTime+'</span></a></li>';
		return html;
	};
	
	function renderUserResult(data){
		var html = "";
		return html;
	};
	
	function renderTotalPart(count,keyword){
		var html = '<li class="all"><a href="<vwb:Link context="search" format="url"/>?func=searchResult&keyword='+keyword+'">显示全部（<span name="size">'+
			count+'</span>）</a></li>'
		return html;
	};
	
	globalSearch.appendResults = function(JSON) {
		this.searchResult.append('<ul></ul>').show();
		var holder = this.searchResult.children('ul');
		var keyword = $("input[name=search_input]").val();
		holder.append(renderTotalPart(JSON.count,keyword));
		if(JSON.pageResult!=null)
			holder.append(renderPageResult(JSON.pageResult));
		if(JSON.collectionResult!=null)
			holder.append(renderCollectionResult(JSON.collectionResult));
		if(JSON.userResult!=null)
			holder.append(renderUserResult(JSON.userResult));
	};
	
	globalSearch.doSearch = function(QUERY){
		this.clearPullDown();
		$.ajax({
			url: "<vwb:Link context='search' format='url'/>?keyword=" + encodeURIComponent(QUERY),
			dataType: 'json',
			type: "POST",
			beforeSend: function(data) {
				globalSearch.MSG_searching();
			},
			success: function(data){
				globalSearch.presentResults(data);
			},
			error: function(){
				globalSearch.MSG_error();
			},
			statusCode:{
				450:function(){alert('会话已过期,请重新登录');},
				403:function(){alert('您没有权限进行该操作');}
			}
		});
	};
	
	globalSearch.resetSearch = function(){
		this.hideCover();
		this.clearPullDown();
	}
	
	/* tabindex */
	globalSearch.searchInput.attr('tabIndex', 1);
	setTimeout(function(){
		globalSearch.container.addClass('transition');
	}, 200);
});
</script>
