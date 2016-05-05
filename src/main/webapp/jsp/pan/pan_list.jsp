<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<link href="${contextPath}/scripts/bootstrap/css/bootstrap.min.css" rel="stylesheet" media="screen"/>
<link href="${contextPath}/scripts/bootstrap/css/bootstrap-responsive.min.css" rel="stylesheet"/>
<link href="${contextPath}/scripts/bootstrap/css/todc-bootstrap.css" rel="stylesheet"/>	
<link href="${contextPath}/jsp/aone/css/fileuploader.css" rel="stylesheet" type="text/css">	
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/tokenInput.css" type="text/css" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/lynx.css?v=${aoneVersion}" type="text/css" />
<link rel="stylesheet" href="${contextPath}/jsp/aone/css/lion.css?v=${aoneVersion}" type="text/css" />
<input type="hidden" id="teamUrl" value="${teamUrl }">
<input type="hidden" id="teamHome" value="${teamHome }">

<style type="text/css">
	li.files-item.element-data.dropFolder {background: #cfc}
	.dragCover {background:#0c0; position:fixed; display:none;}
	body.drag .dragCover {z-index:1000001;}
	.dragCover.top {width:100%; height:6px; left:6px;top:0}
	.dragCover.right {width:6px; height:100%; right:0px; top:0px}
	.dragCover.bottom {width:100%; height:6px; bottom:0px; left:0}
	.dragCover.left {width:6px; height:100%; left:0px; top:0px}
	.dragCoverBox {
		width:200px; height:30px; border:1px solid #0c0; background:#cfc; color:#0c0;
		left:45%; top:30px; padding:5px 10px; text-align:center; line-height:30px; 
		z-index:1000001;position:fixed; display:none;
	}
</style>
<div class="dragCover top"></div>
<div class="dragCover right"></div>
<div class="dragCover bottom"></div>
<div class="dragCover left"></div>
<div class="dragCoverBox">把文件拖动到团队文档库</div>
<div class="lionContent">
	<div class="content-menu readyHighLight0" >
		<ul class="myNavList">
		    <li class="current filterDrop">
              <a id="showAllFiles" class="">所有文件<!--  <span class="caretSpan"><b class="caret"></b></span>--></a>
            </li>
		</ul>
		<ul class="myNavList myShare">
			<li><a href="${contextPath}/pan/shareManage">分享历史</a></li>
		</ul>
		<p class="downLoadApp">
			<a href="${contextPath}/download.jsp" target="_blank">下载桌面客户端</a>
		</p>
		<p class="notice">
			<i class="icon icon-bullhorn"></i>桌面客户端是什么？<br/><a href="https://update.escience.cn/download/ddl_1.1.1_Beta_win32_setup.exe">下载</a>安装后就可以在电脑上同步个人空间（同步版Beta）下的文件了。<a href="https://update.escience.cn/download/ddl_1.1.1_Beta_win32_setup.exe">赶快下载</a>并体验，惊喜与神奇待你发现~
		</p>
	</div>
	<div class="content-menu-body lionContent panList">
	<div class="content-right-board">
		<div id="navBarDiv" class="navBar" >
			<div class="currentResource">
				<h2 class="currentResourceTitle"></h2>
			</div>
			<ol class="breadcrumb" id="navBarOl" style="max-width: 560px"></ol>
			<div class="newBtnGroup" >
				<div id="file-uploader-demo1" style="float:right; margin-right:0;">
					<a class="btn btn-large btn-primary upLoadFile" style=" margin-left:0" name="uploadFile" href="#uploadModal" data-toggle="modal" id="upLoadFile"><i class="icon-file icon-white"></i> 上传文件</a>
				</div>
				<a class="btn btn-large btn-yellow" name="addFolder" style="display:none" id="addFolder"><i class="icon-folder-open"></i> 新建文件夹</a>
			</div>	
				<!-- <a class="btn btn-large btn-primary upLoadFile" name="uploadFile" href="#uploadModal" data-toggle="modal" id="upLoadFile"><i class="icon-file icon-white"></i> 上传文件</a>			 -->
			</div>
		<div class="fixMenuBar">
			<div id="opareteFileMessage" class="alert alert-success" style="display: none;z-index:9999"></div>
			<div class="toolBar">
			 	<input type='checkbox' name="checkAllFiles" id="checkAllFiles" class="showSelectedOperate" value=""/>
				<span class="selectedOperGroup" style="display:none">
					<span id="checkedMessage">&nbsp;已选中<span class="checkedNum"></span>项</span>
					<a class="btn btn-small btn-primary selectedOper" id="moveSelected" title="移动" ><i class="icon-move icon-white"></i> 移动</a>
		           <a class="btn btn-small btn-primary selectedOper" id="copySelected" title="复制" ><i class="icon-copy icon-white"></i> 复制</a>
		           <a class="btn btn-small btn-primary selectedOper" id="deleteSelected" title="删除" ><i class="icon-trash icon-white"></i> 删除</a> 
				</span>
			 	<div class="filterBoard">
			 		<ul id="viewSwitch" class="switch">
						<li id="showAsTable" class="chosen"><a title="列表显示"><span class="iconLynxTag icon-listView"></span></a></li>
						<li id="showAsGrid"><a title="缩略图显示"><span class="iconLynxTag icon-gridView"></span></a></li>
					</ul>
					<div class="btn-group" style="float:right;margin-right:10px;" id="sortDivId">
						<a class="btn filter dropdown-toggle" data-toggle="dropdown" role="button" id="sort">
			 				<span class="sortTitle">
			 				<c:choose>
			 					<c:when test="${sortType eq 'time' }">时间正序</c:when>
			 					<c:when test="${sortType eq 'title' }">标题A-Z</c:when>
			 					<c:when test="${sortType eq 'titleDesc' }">标题Z-A</c:when>
			 					<c:otherwise>时间倒序</c:otherwise>
			 				</c:choose>
			 				</span>
			 			<span class="caret"></span></a>
						<ul class="dropdown-menu " id="sortMenu">
						     <li><a tabindex="-1" href="javascript:void(0)" class="sortFiles" sortType="timeDesc"><span class="menu-item-icon"><b class="ico-radio <c:if test="${sortType eq 'timeDesc' || sortType eq '' }"> ico-radio-checked</c:if> "></b></span><span class="sortName">时间倒序</span></a></li>
						     <li><a tabindex="-1" href="javascript:void(0)" class="sortFiles" sortType="time"><span class="menu-item-icon"><b class="ico-radio <c:if test="${sortType eq 'time'}"> ico-radio-checked</c:if>"></b></span><span class="sortName">时间正序</span></a></li>
							 <li><a tabindex="-1" href="javascript:void(0)" class="sortFiles" sortType="title"><span class="menu-item-icon"><b class="ico-radio <c:if test="${sortType eq 'title'}"> ico-radio-checked</c:if>"></b></span><span class="sortName">标题A-Z</span></a></li>
							 <li><a tabindex="-1" href="javascript:void(0)" class="sortFiles" sortType="titleDesc"><span class="menu-item-icon"><b class="ico-radio <c:if test="${sortType eq 'titleDesc'}"> ico-radio-checked</c:if>"></b></span><span class="sortName">标题Z-A</span></a></li>
						     <!--li><a tabindex="-1" href="javascript:void(0)" class="sortFiles" sortType="fileSize"><span class="menu-item-icon"><b class="ico-radio"></b></span><span>由大到小</span></a></li-->
							 <!--li><a tabindex="-1" href="javascript:void(0)" class="sortFiles" sortType="fileSizeDesc"><span class="menu-item-icon"><b class="ico-radio"></b></span><span>由小到大</span></a></li-->
						</ul>
				    </div>
				    <div class="searchBox" style="float:right;" id="resourceList-search">
			 		</div>
			 		<a class="refresh" onclick="hashChangeF();return false" title="刷新"><i class="icon-refresh icon-color"></i></a>
			 	   <div class="denote" id="imageNav" style="display: none">
			 	       <a class="denote-close" id="closeImageNav"></a>
				  <span>现在可以用缩略图模式查<br/>看文件，浏览图片更方便</span></div>
			 	</div>
			</div>
			<div class="tableHeader"  style="display:none">
				<div class="title-col col">  
					标题       
				</div>   
				<div class="creator-col col">    
					<span>修改人</span>   
				</div>        
				<div class="time-col col">
					<span>修改时间</span>
				</div>   
				<div class="version-col col">    
					<span>大小</span>   
				</div>
				<div class="search-col col">    
					<span>所在文件夹</span>   
				</div>          
			</div>
		</div>
		
		<div id="fileItemDisplay" >
			<ul id="resourceList" class="readyHighLight1 asTight"> </ul>
			<p id="notice" class="NA large  readyHighLight1 loading"  style="float:left">正在载入...</p>
			<div id="no-result-helper" class="NA" style="display:none;">
				<ul class="no-result-guide">
					<li><a class="upLoadFile" href="javascript:void(0)">
							<div id="file-uploader-link" style="margin-left:80px\9\0">上传文件</div>
							<span class="iconLynx icon-upload hover"></span> 
						</a>
					</li>
				</ul>
			</div>
			<a id="load-more-items" class="largeButton dim" begin="0" style="float:left;display:none">更多结果</a>
			<div class="clear"></div>
		</div>
	</div>
	</div>
	</div>

<div id="fileBrowserModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="fileBrowserModalLabel" aria-hidden="true">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3 id="fileBrowserModalLabel">移动到</h3>
	</div>
	<div class="modal-body" style="height: 345px;">
		<div id="teamSelWrapper" style="display:none;">
			<select id="teamSel" >
				<option value="pan" id="teamSel_pan" >个人空间同步版</option>
				<option value="${myTeamCode}" id="teamSel_${myTeamId }" selected="selected">个人空间</option>
				<c:forEach items="${myTeamList}" var="item">
					<option value="${item.name }" id="teamSel_${item.id }" <c:if test="${teamType eq item.name}">selected="selected" class='currentTeam'</c:if>>${fn:escapeXml(item.displayName)}</option>
				</c:forEach>
			</select> 
			<span class="ui-text-note" style="color:#999;">支持跨团队复制</span>
		</div>
		<div id="file_browser"></div>
	</div>
	<div class="modal-footer">
		<button id="newNodeBtn" class="btn text-left pull-left" ><i class="icon-folder-open"></i> 新建文件夹</button>
		<button class="btn btn-primary" id="moveToBtn">确定</button>
		<button class="btn" data-dismiss="modal" aria-hidden="true">取消</button>
	</div>
</div>

<div id="popupUpload" class="popupUpload" style="display:none">
	<div class="popupTitle">
		<p class="uploadTitle">上传文件</p>
		<span><i class="fillUploadPagCal icon-remove icon-white"></i></span>
		<span><i class="icon-minus icon-white"></i></span>
	</div>
	<div id="fileListDiv" class="popupContent">
		<ul id="upload-list" style="list-style:none"></ul>
	</div>
</div>
	
	
<div id="alertModel" class="modal hide fade">
  <div class="modal-header">
    <button type="button" class="close closeUpload" data-dismiss="modal" aria-hidden="true">&times;</button>
    <h3>提示</h3>
  </div>
  <div class="modal-body">
	<p class="alertContent">列表中有未上传完成的文件，确定要放弃上传吗？</p>
  </div>
  <div class="modal-footer">
  	<button class="btn btn-primary" id="okAlertContent">确认</button>
    <button class="btn closeAlertModel"  data-dismiss="modal" aria-hidden="true">取消</button>
  </div>
</div>

<div id="alertDeleteModel" class="modal hide fade" data-backdrop="static">
  <div class="modal-header">
    <button type="button" class="close closeUpload initShow" data-dismiss="modal" aria-hidden="true">&times;</button>
    <h3>提示</h3>
  </div>
  <div class="modal-body">
  <div class='initShow'>
	<p class="alertContent">确定删除所选文件吗？</p>
	<input type="hidden" name="rid" class="deleteRid" value=''/>
  </div>
   <div class="deleteShow" style="display:none">
	<p class="alertContent">正在删除...</p>
  </div>
  </div>
  <div class="modal-footer">
  	<button class="btn btn-primary initShow" id="okAlertContent">确认</button>
    <button class="btn closeAlertModel initShow"  data-dismiss="modal" aria-hidden="true">取消</button>
  </div>
</div>
	
<div id="tip" style="display:none">按住Ctrl键多选</div>

<jsp:include page="/jsp/aone/recommend/shareResourceToTeam.jsp"></jsp:include>
<jsp:include page="/jsp/aone/tag/tagFileOparate.jsp"></jsp:include>


<script type="text/x-jquery-tmpl" id="fileItemTemp">
<li class='files-item element-data {{if $data.searchResult }}searchResult{{/if}}' >
    <div class="title-col col">
	<div class='oper'>
		{{if $data.status!='unpublish'}}
		<input type='checkbox' class="showSelectedOperate" />
		{{else}}
		<div style="width:55px" rid='{{= rid}}'></div>
		{{/if}}
		<input type='hidden' class='rid' value='{{= rid}}'/>
		<input class='parentRid' value='{{= parentRid}}'type='hidden' />
	</div> 
	
       <span class="file-commands">	
			{{if $data.itemType =='DFile' }}	  
			<a class="view-file" href="${contextPath}/pan/preview?path={{= rid}}" target="_blank" title="预览" style="float:left"></a>
			<a class="share-file" title="分享" style="float:left"></a>
			{{/if}}
            {{if $data.itemType =='Folder' }}	  
				<a trigger="manual" class="share-file-none" rel="tooltip" data-placement="top" style="float:left" data-original-title="个人空间同步版暂不支持文件夹的分享功能，如需分享，请先将文件夹复制到个人空间或团队空间。"></a>
			{{/if}}
			{{if $data.rid!=0}}
			{{if $data.status!='unpublish'}}
			{{if $data.itemType =='DFile' }}
           	<a class="down-file" title="下载" style="float:left"></a>
			{{/if}}
			{{/if}}
           <div class="dropdown" style="float:left; height:25px;">
             <a class="dropdown-toggle" data-toggle="dropdown"  title="更多" href="#"></a>
             <ul class="dropdown-menu lion" role="menu" aria-labelledby="dLabel">
				{{if $data.status!='unpublish'}}
               <li rid='{{= rid}}' class="move_item">移动到</li>
               <li rid='{{= rid}}' class="copy_item">复制到</li>
               <li class="rename_item">重命名</li>
				{{/if}}
               <li class="delete_item">删除</li>
				{{if $data.itemType =='DFile' }}
				<li class="preview_history">版本历史</li>         
				{{/if}}
             </ul>
          </div>   
			{{/if}}
		</span>

		<h2>
			<a class='fileName' title='{{= fileName}}'>
				<span class='headImg {{= itemType}} {{= fileType}}'>
					<input type='hidden' class='itemType' value='{{= itemType}}'>
					{{if $data.shared==true}}<span class='share-icon'> </span>{{/if}}
				</span>
				<span class="fileNameSpan">{{= fileName}}</span>
            </a>
		</h2>
     
   </div>
		<div class='creator-col col'>
			<span>{{if $data.lastEditor }}<a class="uidTooltip" href="javascript:void(0)" trigger="hover" rel="tooltip" data-placement="bottom" data-original-title="{{= lastEditorUid}}">{{= lastEditor}}</a>{{else}}-{{/if}}</span>
		</div>
       <div class='time-col col'><span>{{= modofyTime}}</span></div>
		<div class='version-col col'>
			{{if $data.itemType != 'Folder'}}<span>{{= size}}</span>{{else}}<font>-</font>{{/if}}
		</div>
       <div class='col'>
			
		</div>
		{{if $data.searchResult }}
			<div class="searchResultDiv">
				<a class="seachResultA" title="{{= parentPathName}}" path="{{= parentRid}}">{{= parentName}}</a>
			</div>
		{{/if}}

	<div class="clear"></div>
</li>
</script>
<script type="text/html" id="new-tag-template">
<li><a id="tag-for-{{= id}}" class="tag-option multiple" key="tag" value="{{= id}}">
	<span class="tagTitle">{{= title}}</span><span class="tagResCount">{{= count}}</span></a>
    <a class="addToQuery"><span>+</span></a>
</li>
</script>
<script type="text/html" id="page-tag-template">
	<li tag_id="{{= id}}">
		<a target="_blank">{{= title}}</a>
		<a class="delete-tag-link lightDel" tag_id="{{= id }}"></a>
	</li>
</script>

<script type="text/html" id="page-tag-nav">
	<li class="tagBg" tag_id="{{= id}}" >
		<a target="_blank">{{= title}}</a>
		<a class="delete-tag-nav lightDel" tag_id="{{= id }}"></a>
	</li>
</script>

<script type="text/html" id="page-tag-newTag-template">
	<li class="newTag"  rid="{{= rid}}" item_id="{{= itemId}}" item_type="{{= itemType}}"><a title="添加标签">+</a></li>
</script>

<script type="text/html" id="render-tag-all">
<div class="tagGroupsDiv">
		{{each(i,item) groupMap}}
			<p class="ui-navList-title tagGroupTitle subNavList leftMenu-subTitle">
				<a class="iconFoldable" id="tag1Control" title="展开/折叠"></a>
				{{= item.name}}
			</p>
			<ul class="ui-navList leftMenuUl">
				{{if item.tags.length<1}}
					<li class="NA">无标签</li>
				{{/if}}
				{{each(ii,tagItem) item.tags}}
					<li><a id="tag-for-{{= tagItem.id}}" class="tag-option multiple" key="tag" value="{{= tagItem.id}}">
						<span class="tagTitle">{{= tagItem.title}}</span><span class="tagResCount">{{= tagItem.count}}</span>
						</a>
						<a class="addToQuery"><span>+</span></a>
					</li>
				{{/each}}
			</ul>
		{{/each}}
</div>
	<div class="noGroupTagTitle" {{if freeTags.length<1 }} style="display:none"{{/if}}>
		<p class="ui-navList-title tagGroupTitle subNavList leftMenu-subTitle noGroupTagTitle">
			<a class="iconFoldable" id="tag1Control" title="展开/折叠"></a>
			未分类标签
		</p>
		<ul class="ui-navList leftMenuUl" id="ungrouped-tag-list">
			{{each freeTags}}
				<li><a id="tag-for-{{= $value.id}}" class="tag-option multiple" key="tag" value="{{= $value.id}}">
					<span class="tagTitle">{{= $value.title}}</span><span class="tagResCount">{{= $value.count}}</span>
					</a>
					<a class="addToQuery" title="同时选中该标签"><span>+</span></a>
				</li>
			{{/each}}
		</ul>
		</div>
</script>
<div id="mask_common_1" class="intro_mask"></div>
<div class="ui-clear"></div>
<div id="pipeShowTable" class="popupUpload" style="display:none">
	<div class="popupTitle">
		<p class="uploadTitle">文件复制</p>
		<span><i class="fillUploadPagCal icon-remove icon-white"></i></span>
		<span><i class="icon-minus icon-white"></i></span>
	</div>
	<div class="popupContent">
	</div>
</div>
<jsp:include page="/jsp/gallery.jsp"></jsp:include>
<script type="text/javascript" src="${contextPath}/jsp/aone/js/fileuploader.js?v=${aoneVersion}"></script>
<script type="text/javascript" src="${contextPath}/jsp/aone/js/toker-jQuery-forTag.js"></script>
<script type="text/javascript" src="${contextPath}/scripts/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${contextPath}/scripts/jquery.tmpl.min.js"></script>
<script type="text/javascript" src="${contextPath}/scripts/jquery/jquery.hashchange-1.3.js"></script>
<script type="text/javascript" src="${contextPath}/scripts/jquery_tree/jquery.jstree.js"></script>
<script type="text/javascript" src="${contextPath}/scripts/jquery_tree/jquery-ui-1.10.3.custom.min.drag-drop.js"></script>
<script type="text/javascript" src="${contextPath}/scripts/app-ui/pipe_copy.js?v=${aoneVersion}"></script>
<script type="text/javascript" src="${contextPath}/scripts/app-ui/pan_documents.js?v=${aoneVersion}"></script>