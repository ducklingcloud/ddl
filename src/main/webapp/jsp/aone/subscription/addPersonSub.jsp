<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

<script type="text/javascript">
    	  
    	var addSubRequestURL = null;
    	
    	var addPersonFeedURL = null;
    	
    	var addPersonFeed = function(uid,selector){
    	};
    	
    	$("#submit-interest").live('click',function(){
			var queryString = $("#subscriptionForm").serialize();
			ajaxRequest("<vwb:Link page='${pid}' format='url'/>?func=submitSubscription",queryString,afterSubscription);
		});
    
		function prepareSubscription(url,pageId){
			ajaxRequest(url,null,renderSubscriptionDialog);
		};
		
		function initOptions(data){
			$("#page-cb").attr('value',data.pageId+"#"+"page");
			$("#page-recurse-cb").attr('value',data.pageId+"#"+"recuse");
			if(data.option=='null'){
				//hide column and person;
				$("#column-option").hide();
				$("#person-option").hide();
			}
			else if(data.option=='column'){
				//hide person;
				$("#column-cb").attr('value',data.columnId+"#"+"column");
				$('#column-name').html("("+data.columnName+")")
				$("#person-option").hide();
			}
			else if(data.option=='person'){
				$("#person-cb").attr('value',data.personId+"#"+"person");
				$("#column-option").hide();
			}
		};
		
		function renderSubscriptionDialog(data){
			ui_showDialog('interest');
			initOptions(data);
		};
		
		function afterSubscription(data){
			var str = '您已关注了：';
			$("input[name='publisher']").each(function(){
				if($(this).attr('checked')=="checked")
					str += $(this).attr('attr')+" ";
			});
			$("#interest-tips").html(str);
			ui_hideDialog('interest');
			ui_showDialog('interest-success');
			window.setTimeout(function(){window.location.reload();}, 1500);
		};
		
		$(".refresh-button").live('click',function(){
			window.location.reload();
		});
		
</script>

<div class="ui-wrap" id="dialog-hideoutMenu">

	<div class="ui-dialog" id="interest">
		<span class="ui-dialog-x"></span>
		<p class="ui-dialog-title">
			关注
		</p>
		<div class="ui-dialog-body">
			<form id="subscriptionForm">
				<p id="person-option">
					<label>
						<input type="hidden" name="uid" value=""/>
						<input type="checkbox" id="person-cb" name="publisher" attr="作者" value="person" />
						关注作者
					</label>
				</p>
 			</form>
		</div>
		<div class="ui-dialog-control">
			<input type="button" id="submit-interest" value="关注" />
			<a class="ui-dialog-close ui-text-small">取消</a>
		</div>
	</div>

	<div class="ui-dialog" id="interest-success">
		<span class="ui-dialog-x"></span>
		<p class="ui-dialog-title">
			关注成功
		</p>
		<div class="ui-dialog-body">
			<p id="interest-tips">
				您已关注了***。
			</p>
		</div>
		<div class="ui-dialog-control">
			<input style="display:none" type="button" value="确定" class="ui-dialog-close refresh-button" />
		</div>
	</div>

</div>
