<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

<link href="${contextPath}/jsp/aone/css/error.css" rel="stylesheet" type="text/css"/>
<link href="${contextPath}/scripts/bootstrap/css/bootstrap.min.css" rel="stylesheet" media="screen"/>
<link href="${contextPath}/scripts/bootstrap/css/bootstrap-responsive.min.css" rel="stylesheet"/>
<link href="${contextPath}/scripts/bootstrap/css/todc-bootstrap.css" rel="stylesheet"/>	
<link href="${contextPath}/jsp/aone/css/index-nov2013.css?v=${aoneVersion}"	rel="stylesheet" type="text/css" />
<link href="${contextPath}/jsp/aone/css/lynx.css?v=${aoneVersion}"	rel="stylesheet" type="text/css" />
<link href="${contextPath}/jsp/aone/css/lion.css?v=${aoneVersion}"	rel="stylesheet" type="text/css" />
<link href="${contextPath}/jsp/aone/css/index-aug2014.css?v=${aoneVersion}"	rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${contextPath}/scripts/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript">
	$(document).ready(function(){
		$('#value').popover('hide');
	})
</script>
<div class= "manageSpaceMain">
	<div class="userSpaceBox" style="margin:15px 0px;">
		您已赚得 <span class="space-volume spaceGainedCount-js" > ${spaceGainedCount}</span><span class="preparePopover manager inline" data-html='true' data-animation='false' data-trigger="hover" 
						data-placement="top" data-content="<font class='pop manager'>付费扩容团队空间的标准价格为每50G每月1000元</font>" id="value">团队空间</span>，已分配 <span class="space-volume allocatedSpace-js"> ${allocatedSpace}</span>，剩余 <span class="space-volume unallocatedSpace-js"> ${unallocatedSpace}</span> 
		<span class="space-day"></span>	
		<a class="space-manage" href="${contextPath}/activity/task-win-space">去赚取更多空间</a>
		<p class="subHint">您可以将赚取的空间分配到具有管理权限的团队。本次活动结束后，未分配的空间将被自动分配到“个人空间”。</p>
	</div>
	
	<div class="myTeamList">
		<h2>我的团队列表</h2>
		<ul class="teams">
			<li id="tid_${personalSpaceSize.tid}" class="exist-team">
				<div class="teamIcon"></div>
				<div class="teamInfo">
					<h3>个人空间</h3>
					<div class="progressBar">
						<div class="progress  progress-striped">
		 					<div class="bar" style="width:${personalSpaceSize.percentDisplay};"></div>
						</div>
					</div>
					<p class="progressHint">${personalSpaceSize.usedDisplay} / ${personalSpaceSize.totalDisplay}</p>
				</div>
				<div class="teamSpace">
					<a class="btn btn-popup-education-js" data-tid="${personalSpaceSize.tid}" data-total="${personalSpaceSize.totalDisplay}" >分配空间</a>
				</div>
			</li>
			
			<c:forEach var="item" items="${teamSpaceSizeList}" >
			<li id="tid_${item.tid}" class="exist-team">
				<div class="teamIcon"></div>
				<div class="teamInfo">
					<h3>${item.teamDisplayName}</h3>
					<div class="progressBar">
						<div class="progress  progress-striped">
		 					<div class="bar" style="width:${item.percentDisplay};"></div>
						</div>
					</div>
					<p class="progressHint">${item.usedDisplay} / ${item.totalDisplay}</p>
				</div>
				<div class="teamSpace">
					<a class="btn btn-popup-education-js" data-tid="${item.tid}" data-total="${item.totalDisplay}" >给该团队分配空间</a>
				</div>
			</li>
			</c:forEach>
		</ul>
		
		<div id="popup-education" class="modal hide fade" aria-hidden="false">
			<div class="modal-header">
	           <button type="button" class="close" data-dismiss="modal">×</button>
	           <h3>分配空间</h3>
	        </div>
	        <form name="edit-workinfo" id="editEdu" class="form-horizontal no-bmargin">
				<fieldset>
					<div class="modal-body">
						<div class="control-group">
		         			<label class="control-label">当前可分配空间：</label>
		          			<div class="controls marginT unallocated-js">${unallocatedSpace}</div>
		        		</div>
						<div class="control-group">
		         			<label class="control-label">当前团队空间：</label>
		          			<div class="controls marginT total-js"></div>
		        		</div>
		        		<div class="control-group">
		         			<label class="control-label">分配给该团队：</label>
		          			<div class="controls marginT">
		            			+ <select id="allocatedSize" name="allocatedSize">
		            			    <option value="all">全部可分配空间</option>
		            			    <option value="10GB">10 GB</option>
		            			    <option value="5GB">5 GB</option>
		            			    <option value="1GB">1 GB</option>
		            			    <option value="500MB">500 MB</option>
		            			    <option value="200MB">200 MB</option>
		            				<option value="100MB">100 MB</option>
		            			</select>
		            			<span class="text-error hide msg-err-js">  </span>
		          			</div>
		        		</div>
		        	</div>
		        	<div class="modal-footer">
		        		<button type="button" class="btn btn-primary">保存</button>
						<a data-dismiss="modal" class="btn" href="#">取消</a>
			        </div>
		        </fieldset>
		        
	        </form>
		</div>
	</div>
</div>

<script type="text/javascript">
<!--
$(function(){
	var allocatedTid = 0;
	$("a.btn-popup-education-js").click(function(){
		var _that = $(this);
		allocatedTid = _that.data("tid");
		$("#popup-education .total-js").text(_that.data("total"));
		$("#popup-education .unallocated-js").text($(".manageSpaceMain .unallocatedSpace-js").text());
		hideErrMsg();
		$('#popup-education').modal('show');
	});
	
	$("#popup-education .btn-primary").click(function(){
		var size = $("#allocatedSize").val();
		if(size=="all"){
			size = $("#popup-education .unallocated-js").text();
		}
		hideErrMsg();
		$.ajax({
			type: "POST",
			dataType:"JSON",
			url: "${contextPath}/system/space",
			data: {"func":"allocate","allocatedTid":allocatedTid,"allocatedSize":size},
			success: function(res){
				if(res.state==0){
					var tid = $("#tid_" + allocatedTid);
					tid.find(".progressHint").text(res.teamSpaceUsed + " / " + res.teamSpaceTotal);
					tid.find(".bar").css("width",res.teamSpacePercent);
					tid.find(".btn-popup-education-js").data("total",  res.teamSpaceTotal);
					$(".manageSpaceMain .unallocatedSpace-js").text(res.unallocatedSpace);
					$(".manageSpaceMain .allocatedSpace-js").text(res.allocatedSpace);
					$('#popup-education').modal('hide');
					successMsg(tid);
				}else{
					showErrMsg(res.msg);
				}
			}
		});
	});
	
	function showErrMsg(msg){
		var span = $("#popup-education .msg-err-js");
		span.text(msg);
		span.show();
	}
	function hideErrMsg(){
		var span = $("#popup-education .msg-err-js");
		span.text("");
		span.hide();
	}
	
	$("#banner-innerWrapper a").attr("href","javascript:;");
});

function successMsg(obj){
	obj.addClass("recentAdded");
	window.setTimeout(function(){obj.removeClass("recentAdded");}, 3000);
}
//-->
</script>
<script type="text/javascript" src="${contextPath}/scripts/bootstrap/js/bootstrap.min.js"></script>