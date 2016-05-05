/* dataCollect
 *	append to webpage to use.
 *	version: 0.9.1. Apr.22, 2012
 */

if (typeof($)==='undefined' || typeof($.ajax)==='undefined') {
	//load jquery
	var jq=document.createElement('script');
	jq.type='text/javascript';
	document.body.appendChild(jq);
	jq.src= baseURL + '/scripts/jquery/jquery-1.5.2.min.js';
	jq.charset='utf-8';
}

var cjq = setInterval(function(){
		//wait till jquery is perfectly loaded
		if (typeof($)!=='undefined' && typeof($.ajax)!=='undefined') {
			clearInterval(cjq);
			var c=document.createElement('link');
			c.rel='stylesheet';
			document.body.appendChild(c);
			c.href= baseURL + '/dataCollect/dataCollect.css';
			startMainThread();
		}
}, 20);

function startMainThread() {
	
	/* start main thread -------------------------------------------------*/ 
	
	$(document).ready(function(){
		var loginStatus;
		var regularCheck;	// interval token for login check
		
		var current;	// track the element being hovered
		var index = 0;	// identify selected item
		
		var urlCheckLoginStatus = baseURL + '/system/bookmark';
		var urlSaveContent = baseURL + '/system/bookmark?func=newPage';
		var urlLoginBox = baseURL + '/dataCollect/login.jsp';
		var urlJumpOutLogin = baseURL + '/system/switch';

		var copyTitle;		// container of webpage title
		var copyReciever;	// container of selected content
		
		$.ajax({
			url: urlCheckLoginStatus,
			type:'GET',
			dataType: 'script',
			success:function(){		
				if(logstatus){
					showFormIframe();
					textArea();	
				}
				else{
					showLoginBox();
					regularCheck = startRegularCheck();
				}
			},
			error: function() {			
				alert("对不起，您暂时没有权限访问科研在线网页收藏工具。");
			}
		});

		function startRegularCheck(){
			var token = setInterval(function(){
				$.ajax({
					url: urlCheckLoginStatus,
					type:'GET',
					dataType: 'script',
					success:function(){	
						if(logstatus){
							window.clearInterval(token); 
							$("#DC_loginIframe").remove();
							showFormIframe();
							textArea();	
						}
					},
					error: function() {			
						alert("对不起，您暂时没有权限访问科研在线网页收藏工具。");
					}
				});
				
			}, 2000);
			
			return token;
		}
		
		
		function showFormIframe(){
			$('#DC_loginHref').remove();
			$("body").append(		
					"<div id='DC_cover_follow' style='display:none;'>&nbsp;</div>" +
					"<div id='DC_popupArea'>" +
						"<form id='DC_form' accept-charset='utf-8' name='DC_form' method='POST' action='" + urlSaveContent + "' target='test_iframe' >"+
							"<div class='duckling_logo'>科研在线网页收藏工具</div>" +
							"<div id='wholeHtml' style='display:none'>全网页拷贝</div>" +
							"<div id='close' title='关闭'></div>" +
							"<div class='clear'></div>" +
							"<div id='DC_show_title' contentEditable='true'></div>" +
							"<div id='team'>所属团队：" +
								"<select id='DC_teamList' name='tid'></select>" +
							"</div>" +
							"<div class='clear'></div>" +
							"<div id='DC_show_textArea' contentEditable='true'></div>" +
							"<input id='DC_title' type='hidden' name='title' value=''/>" +
							"<input id='DC_content' type='hidden' name='content' value=''/>" +
							"<div class='oper'>" +
								"<input type='button' id='DC_submit' value='提交'/>" +
								"<input type='button' class='redo' value='重置'/>" +
							"</div>" +
						"</form>"+
					"</div>"
			);
			
			copyReciever = $('#DC_show_textArea');
			copyTitle = $('#DC_show_title');
			
			// check login status before submit form
			$('#DC_submit').bind('click.dataCollect',function(){
				if (copyTitle.html()!='' && copyReciever.html()!='') {
					$.ajax({
						url: urlCheckLoginStatus,
						type:'GET',
						dataType: 'script',
						success:function(){	
							if(logstatus){
								submitForm();
							}
							else{
								// show login box and start regular check
								showLoginBox();
								alert('连接超时，请重新登录');
								
								var count = 0;
								regularCheck = setInterval(function(){
									$.ajax({
										url: urlCheckLoginStatus,
										type:'GET',
										dataType: 'script',
										success:function(){	
											if(logstatus){
												window.clearInterval(regularCheck); 
												$("#DC_loginIframe").remove();
												submitForm();
											}
										},
										error: function() {			
											alert("对不起，您暂时没有权限访问科研在线网页收藏工具。");
										}
									});
									
								}, 2000);
							}
						},
						error: function() {			
							alert("对不起，您暂时没有权限访问科研在线网页收藏工具。");
						}
					});
				}
				else {
					alert('标题和内容都不能为空，请选择后提交。');
				}
				
				function submitForm() {
					$('#test_iframe').remove();
					$('#DC_popupArea').append("<iframe id='test_iframe' width='0' height='0' src='about:blank' name='test_iframe'></iframe>");
					//document.getElementById('test_iframe').contentWindow.name = 'test_iframe';
					
					$('#DC_show_textArea').find("a").each(function(){
						var href = $(this).attr("href");
						
						var patrn=/^(\/)/i; 
						var patrnSharp = /^(\#)/i;
						var patrnJS = /^(\javascript)/i;
						if (patrn.exec(href)){
							href = "http://" + window.location.hostname  + href;
							$(this).attr("href",href);
						}
						if(patrnSharp.exec(href)){
							href = window.location.href  + href;
							$(this).attr("href",href);
						}
						if(patrnJS.exec(href)){
							$(this).removeAttr("href");
						}
					});
					
					var selectHTML = $('#DC_show_textArea').html();
					
					var sentHTML = selectHTML + "<p></p><p></p><p>本文来源：<a href=" + window.location.href + ">" + window.location.href + "</a></p>";
					
					$('#DC_title').val($('#DC_show_title').html());
					$('#DC_content').val(sentHTML);
					document.charset='utf-8';
					document.DC_form.submit();
					
					//clear selected items
					$("#DC_popupArea .redo").click();
				}
			})

			
			$('#DC_teamList').html(selectTeam(teamColls)); 
			
			function selectTeam(teamColls){
				var teamList;
				for(var i=0;i<teamColls.length;i++){
					teamList+='<option value="'+teamColls[i].tid+'" >' + teamColls[i].tname + '</option>';
		          }
				$("#DC_popupArea #DC_teamList").append(teamList);
			}
		}
		
		function showLoginBox(){
			if ($('#DC_loginHref').length==0) {
				$("body").append(
						"<div id='DC_loginHref'>" +
						"	<p id='DC_login_p'>您需要<a href='" + urlJumpOutLogin + "' target='_blank'>登录中国科技网通行证</a>，才可以继续使用协同数据采集器。</p>" +
						"   <div id='closeLoginBox'></div>" +
						"</div>"
				);
				
				$('#closeLoginBox').bind('click', function(){
					clearInterval(regularCheck);
					$('#DC_loginHref').remove();
				});
			}
		}
		
		function textArea(){
			var follower = $('#DC_cover_follow');
			//	var copyReciever = $('#DC_urlBox').contents().find("#textArea");
			autoTextArea();
			$("body *:not(#DC_cover_follow, #DC_popupArea, #DC_popupArea *, .DC_cover_selected, .DC_cover_selected *)").bind('mouseenter.dataCollect', function(event){ //hover over an element
				event.stopPropagation();	//stop bubbling of mouse-enter event
				current = $(this);
				locateFollower($(this));
				bringUpChildren($(this));
			});

			follower.click(function(){	//select an element
				selectItem(current);
				appendText();
			});
			
			$(".expandArea").live('click', function(event){	//select parent
				event.stopPropagation();
				sourceItem = $('.isClicked[clickIndex=' + $(this).parent().attr('clickIndex') + ']');	
				selectedItem = sourceItem.parent();
				selectItem(selectedItem, sourceItem);
				appendText();
			});
			
			$(".DC_cover_selected").live('mouseover', function(){
				clearFollower();
			});
			
			$(".DC_cover_selected").live('click', function(){
				// cancel a selection
				$(this).remove();
				clearFollower();
				$('.isClicked[clickIndex=' + $(this).attr('clickIndex') + ']').removeClass("isClicked");
				appendText();
				
			});

			$("body").bind('mouseleave.dataCollect', function(){
				clearFollower();
			});
			 
			$("#DC_popupArea .redo").click(function(){
				clearText();
				$(".DC_cover_selected").remove();
				$(".isClicked").removeClass("isClicked").removeAttr("clickIndex");
				$('html, body').animate({ scrollTop: 0 }, 700);
			});
			
			$("#DC_popupArea #close").live('click',function(){
				$("#DC_popupArea").remove();
				follower.remove();
				$(".DC_cover_selected").remove();
				$(".isClicked").removeClass("isClicked").removeAttr("clickIndex");
				$('.up_zindex').removeClass('up_zindex').css({'position':'', 'z-index':''});
				$("body, body *").unbind('.dataCollect');
			});
			
			$("#DC_popupArea #wholeHtml").click(function(){
				selectItem($("body"));
				follower.hide();
				appendText();
			});
			
			function autoTextArea(){	
				selectItem(findTopScore());
				appendText();
				var range = $(window).height()/6;
				var top = $('.DC_cover_selected').offset().top - range;
				var distance = $(window).scrollTop() - top;
				if (distance > range || distance < -range) {
					$('html, body').animate({ scrollTop: top }, 700);
				}
			} 

			function score(OBJ) {
				var s; // score of OBJ
				
				var pNum = tagNum(OBJ,"p");
				var brNum = tagNum(OBJ,"br");
				var aNum = tagNum(OBJ,"a");
				var imgNum = tagNum(OBJ,"img");
				var tdNum = tagNum(OBJ,"td");
				
				var aPerLength = perLength(OBJ,"a");
				var prePerLength = perLength(OBJ,"pre");
				var pPerLength = perLength(OBJ, "p");
				

				if(tdNum > 3*pNum){
					s = 2*tdNum + brNum + 3*imgNum + prePerLength - 3*aNum - aPerLength; 
				}
				else{
					s = 2*pPerLength + 5*pNum + brNum + 3*imgNum + prePerLength - 3*aNum - aPerLength; 
				}
				
				return s;
			}
			
			function findTopScore() {
				var topScore = 0;
				var topScoreElement;
				var thisScore, childScore;
				
				$("body li, body td, body div:not(#DC_cover_follow, #DC_popupArea, " +
						"#DC_popupArea *, .DC_cover_selected, .DC_cover_selected *)").each(function(){
					thisScore = score($(this));
					
					if(thisScore >= topScore){
						topScore = thisScore;
						topScoreElement = $(this);
						
						//inner children is the artical
						topScoreElement.each(function(){
							childScore = score($(this));
							if(childScore >= 0.85*topScore){
								topScoreElement = $(this);	
							}
						})
					}
				});
				
				return topScoreElement;
			}

			function ByteWordCount(txt) { 
		        txt = txt.replace(/([\u0391-\uFFE5])/ig,'11');
		        var count = txt.length;
		        return count;
			}
			
			// children better than find. save time
			function perLength(OBJ,tagName){
				var stringLength = ByteWordCount($(OBJ.children(tagName)).text())
								   - ByteWordCount($(OBJ.children(tagName).find("link,script,styles,meta,title,embed")).text())
								   - ByteWordCount($(OBJ.children(tagName).find("*").css(":display") == "none").text());

				var tagNum = $(OBJ.children(tagName)).length + $(OBJ.children(tagName).find("*")).length;
				var perLength;
				
				if(tagNum == 0){
					perLength = 0;
				}
				else{
					perLength = stringLength/tagNum;
				}
				
				return perLength;
			}
			
			function tagNum(OBJ,tagName){
				var tagNum = $(OBJ.find(tagName)).length;
				return tagNum;
			}

			function locateFollower(OBJ) {
				// locate the follower to wrap hovered element			
				setSize(OBJ, follower);
			}
			
			function clearFollower() {
				follower.hide();
				current = null;
			}
			
			function selectItem(OBJ, SRC) {
				// select element, set clickIndex to bond element and corresponding mask('DC_coverMask_[index]')
				
				OBJ.addClass('isClicked').attr('clickIndex', index);
				
				// de-select children
				
				OBJ.find('.isClicked').each(function(){
					$('#DC_coverMask_' + $(this).attr('clickIndex')).remove();
					$(this).removeClass('isClicked');
				});
				
				$("body").append(
						"<div id= 'DC_coverMask_" + index + "' class='DC_cover_selected' clickIndex= '" + index + "'>" +
								"<div class='innerTransparent'>&nbsp;</div>" +
								"<div class='expandArea'>&nbsp;</div>" +
								"<div class='cancleArea'>&nbsp;</div>" +
						"</div>"
				);
				
				if (SRC!=undefined) {
					setSize(SRC, $('#DC_coverMask_'+index));
				}
				
				setSize(OBJ, $('#DC_coverMask_'+index));
				
				if (OBJ[0].tagName.toLowerCase() =='body') {
					$('#DC_coverMask_'+index).css('height', $(document).height()-6);
					$('#DC_coverMask_'+index+ ' .expandArea').remove();
				}
				
				index++;
			}
			
			function setSize(OBJ, SELECTOR) {
				SELECTOR.css({
					'left': OBJ.offset().left-3,
					'top': OBJ.offset().top-3,
					'width': OBJ.outerWidth(),
					'height': OBJ.outerHeight()
				}).show();
			}
			
			function clearText(){
				copyTitle.html('');
				copyReciever.html("");
			}
			
			function appendText(){
				clearText();
				var copiedHTML = '';
				var copiedBODYHTML = '';
				$('.isClicked').each(function(){	
					//alert($(this)[0].tagName);
					if ($(this)[0].tagName.toLowerCase()=='body') {
						var str = $('body').children(':not(#DC_popupArea, #DC_cover_follow, .DC_cover_selected, script, link, meta, title)').html();
						str = str.replace(/<!\[CDATA\[[^\]]*?\]]/ig,"");
						str = str.replace(/<script.*?<\/script>/ig,"");
						str = str.replace(/<embed.*?>/ig,"");
						str = str.replace(/<iframe.*?<\/iframe>/ig,"");
						
						copiedBODYHTML += str;
						
						copyReciever.html(copiedBODYHTML);
					}
					else{
						var str = $(this).html();
						str = str.replace(/<!\[CDATA\[[^\]]*?\]]/ig,"");
						str = str.replace(/<script.*?<\/script>/ig,"");
						str = str.replace(/<embed.*?>/ig,"");
						str = str.replace(/<iframe.*?<\/iframe>/ig,"");
						
						copiedHTML += str;
						
						copyReciever.html(copiedHTML);
					}
				});

				copyReciever.find("*").removeAttr("class").removeAttr("clickIndex").removeAttr("id")
					.css({ 'position':'', 'z-index':'', 'top':'', 'left':'',
						'display':'', 'height':'', 'width':'','float':'' });
				if (copyTitle.text()=='') {
					copyTitle.text(document.title);
				}
			}
			
			
			function bringUpChildren(OBJ) {
				// bring up children for further mouse-enter
				$('body .up_zindex').removeClass('up_zindex').css({'position':'', 'z-index':''});
				OBJ.children().each(function(){
					if($(this).css("position") == "absolute" || $(this).css("position") == "fixed" || $(this).css("position") == "relative"){
						$(this).addClass('up_zindex').css({
							"z-index":200000
						});
					}
					else{
						$(this).addClass('up_zindex').css({
							'position': 'relative',
							'z-index': 200000
						});
					}
				});
			}
		}
		
	});
	/* end of main thread -------------------------------------------------- */
	
}
