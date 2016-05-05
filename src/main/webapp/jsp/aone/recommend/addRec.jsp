<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<link rel="stylesheet" href="${contextPath}/scripts/jquery_chosen/chosen.css" type="text/css" />
<script type="text/javascript" src="${contextPath}/scripts/jquery_chosen/chosen.jquery.js"></script>
<script type="text/javascript">
		var addRecRequestURL = null;
		
		$('div.a1-objSelector ul li label input').live('click', function(){
    		var li = $(this).parent().parent();
    		if ($(this).attr('checked')==true)
    			li.addClass('-selected');
    		else
    			li.removeClass('-selected');
    	});
    	
    	$("#submit-recommend").live('click',function(){
			var queryString = $("#recommendForm").serialize();
			var choice = $("#userSelect").val();
			if(choice==null||choice==''||choice=='undefined'){
				alert("请选择成员！");
				return ;
			}
			ajaxRequest("<vwb:Link context='recommend' format='url'/>?func=addRecommend&rid=${rid}&itemType=${itemType}",queryString,afterRecommend);
		});
    	
    	$('#recommend-to-all').live('click',function(){
    		if ($(this).attr('checked')==true) {
    			$('div.a1-objSelector ul li label input').each(function(){
    				$(this).attr('checked', true);
    				var li = $(this).parent().parent();
    					li.addClass('-selected');
    			});
    		}
    		else {
    			$('div.a1-objSelector ul li label input').each(function(){
    				$(this).removeAttr('checked');
    				$(this).parent().parent().removeClass('-selected');
    			});
    		}
    	});
    	
    	$("#selectAllMember").live("click",function(){
    		var select = $("#selectAllMember:checked").val();
    		if(select==null||select=="undefined"){
    			$(".search-choice-close").trigger("click");
    			$("#recommendForm").find("input[type=text]").trigger("focus");
    			
    		}else{
    			 $('#userSelect option').attr('selected', true);
    			 $('#userSelect').trigger('liszt:updated');
    		}
    		changeGroupSend();
    	});
    	
    	
    	$("#userSelect").live("change",function(){
    		var checkLength = $("#userSelect").find("option:selected").length;
    		var selectLength = $("#userSelect option").length;
    		var select = $("#selectAllMember:checked").val();
    		if(checkLength==selectLength){
    			if(select==null||select=="undefined"){
    				$("#selectAllMember").attr("checked","checked");
    			}
    		}else{
    			if(select!=null){
    				$("#selectAllMember").removeAttr("checked");
    			}
    		}
    		changeGroupSend();
    	});
    	
    	function changeGroupSend(){
    		var checkLength = $("#userSelect").find("option:selected").length;
    		//当选择大于两个时
    		if(checkLength>=2){
    			if($("#groupDiv").is(":hidden")){
    			//	$("#groupDiv").show();
    			//	$("#groupDiv>input").removeAttr("checked");
    			}
    		}else{
    			//$("#groupDiv>input").removeAttr("checked");
    			//$("#groupDiv").hide();
    		}
    	};
    	
    	var hello = function(data){
    		alert(data.status);
    	};
    	
    	$("ul.chzn-choices").live("click",function(e){
    		$("#recommendForm").find("input[type=text]").trigger("focus");
    		var t = setTimeout(function(){
				$(".chzn-drop").css({"left":"0px"});
			},55); 
    	});
    	//url ajax请求地址 pageId分享的页面id号 
    	var showPersonListFlag = false;
		function prepareRecommend(url,pageId, pageTitle){
			addRecRequestURL = url;
			showPersonList();
			$("#recommendPageField").attr("value",pageId);
			$("#rec-page-title").text(pageTitle);
			$("textarea[name='remark']").val("");
			ajaxRequest(addRecRequestURL,null,renderRecommendDialog);
		};

		function showPersonList(){
			if(!showPersonListFlag){
				var url = site.getURL('task',null)+"?func=getmembers";
				$.ajax({
					url:url,
					type:'POST',
					success:function(data){
						var datajson = JSON.parse(data);
						renderData(datajson.users);
					},
					statusCode:{
						450:function(){alert('会话已过期,请重新登录');},
						403:function(){alert('您没有权限进行该操作');}
					}
				});
				showPersonListFlag = true;
			}
		};
		
		function renderData(data){
			$.each(data,function(index,element){
				var alphabet = element.id;
				var users = element.value;
				
				$("select.chzn-select").append("<optgroup label = '" + alphabet + "'></optgroup>");
				$.each(users, function(index2,element2){
					var uid = element2.id;
					var email =element2.email;
					var name = element2.name;
					$("select.chzn-select optgroup[label = '" + alphabet + "']").append("<option value='"+uid+"'>" + name + " ( " + email  + " ) " + "</option>");
				});
				
			});
			$(".chzn-select").chosen({no_results_text: "没有成员匹配"});
			$(".chzn-select").chosen();
			$(".chzn-select-deselect").chosen({allow_single_deselect:true}); 
		};
		
		function renderRecommendDialog(data){
			var i=0;
			var list_html = '';
			for(i=0;i<data.length;i++){
				list_html += '<li><label title="'+data[i].id+'"><input type="checkbox" name="users" attr="'+data[i].name+'" value="'+data[i].id+'"/>'+data[i].name+'</label></li>';
			}
			$("#candidates-list").html(list_html);
			$('#recommendForm textarea').css('height', '4em').text('');
			//关闭上次的选择项
			$(".search-choice-close").trigger("click");
			$("#selectAllMember").removeAttr("checked");
			ui_showDialog('recommend');
			$("#recommendForm").find("input[type=text]").trigger("focus");
			var t = setTimeout(function(){
				$(".chzn-drop").css({"left":"-9000px"});
			},55); 
			flag = false;
			clickFlag = false;
		};
		
		var flag = false;
		$("#recommendForm").find("input[type=text]").live("keyup",function(event){
			if(!flag){
				$(".chzn-drop").css({"left":"0px"});
				flag = true;
			}
		});
		var clickFlag = false;
		$("#recommendForm").find("input[type=text]").live("click",function(){
			if(!clickFlag){
				$(".chzn-drop").css({"left":"0px"});
				clickFlag = true;
			}
		});
		
		$(".refresh-button").live('click',function(){
			window.location.reload();
		});
		
		function afterRecommend(data){
			var html = '';
			if(data.itemType == 'DFile')
				html = '您已将该文件分享给';
			else
				html = '您已将该页面分享给';
			if(data.status=='success'){
				var s = $("#userSelect").find("option:selected");
				$.each(s,function(indext,option){
					if(indext!=0){
						html+=",";
					}
					//var leng = 6+$(option).val().length;
					var opHtml = $(option).html();
					//var name = opHtml.substring(0,(opHtml.length-leng));
					var name = opHtml;
					if(opHtml.indexOf("(")>-1){
						name = opHtml.substring(0,opHtml.indexOf("("));
					}
					html+=name;
				});
				
				ui_hideDialog('recommend');
				$("#recommend-to-all").removeAttr('checked');
				$("#recommend-tips").html(html);
				ui_showDialog('recommend-success', 1500);
			}
		};
		
</script>

<div class="ui-wrap" id="dialog-hideoutMenu">

	<div class="ui-dialog" id="recommend">
		<span class="ui-dialog-x"></span>
		<p class="ui-dialog-title">
			通过邮件分享：<span id="rec-page-title"></span>
		</p>
		<div class="ui-dialog-body" style="overflow:visible">
			
			<p>
				请输入收件人姓名：
				<span class="ui-RTCorner"> <label>
						<input style="margin:0" type="checkbox" id="selectAllMember" />
						全部成员
					</label> </span> 
			</p>
			<form id="recommendForm">
				<input type="hidden" name="itemId" id="recommendPageField" value=""/>
				<!-- <div class="a1-objSelector">
					<ul id="candidates-list">
					</ul>
				</div> -->
				
				<select id="userSelect" name="users" data-placeholder="输入成员名称" style="width:350px;" class="chzn-select" multiple tabindex="6">
		        </select>
				<div id="groupDiv" style="display:none">
					<input type="checkbox" name="sendType"  value="group">群发多显（每个收件人能看到所有群发的邮件地址）
				</div>
				<p>
					附加信息：
				</p>
				<p align="center">
					<textarea name="remark" style="width:100%; height:4em; max-width:100%;"></textarea>
				</p>
			</form>
		</div>
		<div class="ui-dialog-control">
			<input type="button" id="submit-recommend" value="分享" />
			<a class="ui-dialog-close ui-text-small">取消</a>
		</div>
	</div>
	<div class="ui-dialog" id="recommend-success">
		<span class="ui-dialog-x"></span>
		<p class="ui-dialog-title">
			分享成功
		</p>
		<div class="ui-dialog-body">
			<p id="recommend-tips">
				您已将该
				<c:choose>
					<c:when test="${itemType eq 'DFile'}">
						文件
					</c:when>
					<c:otherwise>
					页面
					</c:otherwise>
				</c:choose>
				分享给***
			</p>
		</div>
		<div class="ui-dialog-control">
			<input style="display:none" type="button" class="ui-dialog-close" value="确定" />
		</div>
	</div>
</div>
