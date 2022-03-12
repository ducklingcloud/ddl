<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<script type="text/javascript" src="${contextPath}/jsp/aone/js/uiLib-jQuery.js"></script>
<script type="text/javascript">
    	
    	var deleteSubRequestURL = null;
    	
		function prepareRemoveSubscription(url,pageId){
			deleteSubRequestURL = url;
			//ajaxRequest(url,null,renderRemoveSubscriptionDialog);
			renderRemoveSubscriptionDialog();
		};
		
		function renderRemoveSubscriptionDialog(data){
			ui_showDialog('remove-interest');
		};
		
		$("#submit-cancel-interest").live('click',function(){
			if($("input[name='existPublisher']:checked").length!=0){
				var queryString = "func=removeSubscription&";
				queryString += $("#existSubscriptionForm").serialize();
				ajaxRequest(deleteSubRequestURL,queryString,afterRemoveSubscription);
			}else{
				ui_hideDialog('remove-interest');
			}
		});
		
		function afterRemoveSubscription(data){
			if(data.status=='noExist'){
				$("input[name='subscriptionStatus']").attr('value','noExistSubscription');
				$("#interest-box span").html('关注');
				ui_hideDialog('remove-interest');
				ui_showDialog('remove-interest-success');
				window.setTimeout(function(){window.location.reload();}, 1500);
			}
			else{
				ui_hideDialog('remove-interest');
				ui_showDialog('remove-interest-success');
				window.setTimeout(function(){window.location.reload();}, 1500);
			}
			$("input[name='existItemSize']").attr('value',data.size);
		};
		
		$(".refresh-button").live('click',function(){
			window.location.reload();
		});
		
		$(".ui-dialog-x").live('click',function(){
			$(this).parent().hide();
		});
		$(".ui-dialog-close").live('click',function(){
			$(this).closest(".ui-dialog").hide();
		});
		
</script>

<div class="ui-wrap" id="dialog-hideoutMenu">

	<div class="ui-dialog" id="remove-interest">
		<span class="ui-dialog-x"></span>
		<form id="existSubscriptionForm">
			<p class="ui-dialog-title">取消关注</p>
			<div class="ui-dialog-body">
				<input type="hidden" name="existItemSize" value="${fn:length(existInterest)}" />
				<c:if test="${not empty existInterest }">
					<p>即将为您取消以下内容的关注：</p>
					<c:forEach var="item" items="${existInterest}">
						<c:if test="${item.publisher.type=='page'}">
							<p>
								<label>
									<input type="checkbox" name="existPublisher" checked="checked"
										value="${item.publisher.id}#${item.publisher.type}" disabled="disabled"/>
									取消本页面的关注
								</label>
							</p>
						</c:if>
						<c:if test="${item.publisher.type=='recuse'}">
							<p>
								<label>
									<input type="checkbox" name="existPublisher" checked="checked"
										value="${item.publisher.id}#${item.publisher.type}" />
									取消本页面及其子页面的关注
								</label>
							</p>
						</c:if>
						<c:if test="${item.publisher.type=='column'}">
							<p>
								<label>
									<input type="checkbox" name="existPublisher" checked="checked"
										value="${item.publisher.id}#${item.publisher.type}" />
									取消栏目的关注
									<span class="ui-text-note" id="column-name">（关注栏目及其所有子页面的变化）</span>
								</label>
							</p>
						</c:if>
						<c:if test="${item.publisher.type=='person'}">
							<p>
								<label>
									<input type="checkbox" name="existPublisher" checked="checked"
										value="${item.publisher.id}#${item.publisher.type}" />
									取消作者的关注
								</label>
							</p>
						</c:if>
					</c:forEach>
				</c:if>
			</div>
			<div class="ui-dialog-control">
				<input type="button" id="submit-cancel-interest" value="取消关注" />
				<a class="ui-dialog-close ui-text-small">保持关注</a>
			</div>
		</form>
	</div>

	<div class="ui-dialog" id="remove-interest-success">
		<span class="ui-dialog-x"></span>
		<p class="ui-dialog-title">
			取消关注成功
		</p>
		<div class="ui-dialog-body">
			<p id="remove-interest-tips">
				您已取消了关注。
			</p>
		</div>
		<div class="ui-dialog-control">
			<input style="display:none" type="button" value="确定" class="ui-dialog-close  refresh-button" />
		</div>
	</div>
</div>
