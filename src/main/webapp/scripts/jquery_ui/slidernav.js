/*
 *  SliderNav - A Simple Content Slider with a Navigation Bar
 *  Copyright 2010 Monjurul Dolon, http://mdolon.com/
 *  Released under the MIT, BSD, and GPL Licenses.
 *  More information: http://devgrow.com/slidernav
 */
$.fn.sliderNav = function(options) {
	var defaults = { items: ["a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"], debug: false, height: null, arrows: true};
	var opts = $.extend(defaults, options); 
	var o = $.meta ? $.extend({}, opts, $$.data()) : opts; 
	var slider = $(this); $(slider).addClass('slider');
	
	$('.slider-content li:first', slider).addClass('selected');
	$(slider).append('<div class="slider-nav"><ul></ul></div>');
	
	for(var i in o.items) $('.slider-nav ul', slider).append("<li><a alt='#"+o.items[i]+"'>"+o.items[i]+"</a></li>");
	var height = $('.slider-nav', slider).height();
	if(o.height) height = o.height;
	$('.slider-content, .slider-nav', slider).css('height','530px');
	if(o.debug) {
		$(slider).append('<div id="debug">Scroll Offset: <span>0</span></div>');
	}
	$('.slider-nav a', slider).mouseover(function(event){
		var target = $(this).attr('alt');
		var cOffset = $('.slider-content', slider).offset().top;
		var tOffset = $('.slider-content '+target, slider).offset().top;
		var height = $('.slider-nav', slider).height();
		if(o.height) {
			height = o.height;
		}
		var pScroll = (tOffset - cOffset) - height/8;
		$('.slider-content li', slider).removeClass('selected');
		$(target).addClass('selected');
		$('.slider-content', slider).stop().animate({scrollTop: '+=' + pScroll + 'px'});
		if(o.debug) $('#debug span', slider).html(tOffset);
	});
	if(o.arrows){
		$('.slider-nav',slider).css('top','30px');
		$(slider).prepend(
				'<div class="listTitle">' +
				'	从团队成员列表中选择' +
				'	<span class="closeDia">x</span>' +
				/*'<div class="listTitle">' +
				'	<input type="text" id="inner_invitees" name="inner_invitees" />' +
				'</div>' +
				'</div>' +
				'<div id="slider" class="lynxDialog">' +
				'	<div class="slider-content">' +
				'		<ul class="taskPersonList">' +
				'		</ul>' +
				'	</div>' +*/
				'</div>' 
		);
		$(slider).append(
				'<div class="selectedPerson">' +
				'	<div class="selectedShow">已选择：</div>' + 
				'	<ul></ul>' +
				'	<div class="submitButton">' +
				'		<input class="largeButton small" id="submitPerson" type="button" value="確定">' +
				'		<input class="largeButton small" id="canclePerson" type="button" value="取消">' +
				'	</div>'+
				'</div>'
		);
		$('.slide-down',slider).click(function(){
			$('.slider-content',slider).animate({scrollTop : "+="+height+"px"}, 500);
		});
		$('.slide-up',slider).click(function(){
			$('.slider-content',slider).animate({scrollTop : "-="+height+"px"}, 500);
		});
		
		//----------------------------click from left div to right div---begin
		var clickIndex = 0;
		function clickEvent(e){
			clickIndex ++;
			e.addClass("selected");
			if(!e.attr("clickId")){
				e.attr("clickId",clickIndex);
				var copier  = e.parent().clone().append("<a class='delete' uid='"+e.attr("uid")+"' clickId= '" + clickIndex + "'>移除</a>");
				copier.appendTo(".selectedPerson ul");
			}
			//$(".slider-content ul ul li a").die("click");
		}
		
		$(".slider-content ul ul li a").live("click.clickItem",function(){
			clickEvent($(this));
		});
		
		$(".selectedPerson ul li a.delete").live("click",function(){
			var theText = $(this).prev().text();
			$(this).parent().remove();
			
			var click_id = $(this).attr("clickId");
			$('.slider-content ul ul li a[clickId="'+ click_id + '"]').live("click.clickItem",function(){
				clickEvent($(this));
			});
			$('.slider-content ul ul li a[clickId="'+ click_id + '"]').removeClass("selected").removeAttr("clickId");
			$("ul.token-input-list-facebook").children("li").each(function(){
				var inputText = $(this).find("p").text();
				if(theText == inputText){
					$(this).remove();
				}
			})
		});
		
		$("#submitPerson").live("click",function(){
			var cc = $(".token-input-list-facebook");
			var invitees=$("#invitees_pop");
			//cc.html("");
			//去重
			var exitName = cc.find("p").text();
			var addUid='';
			$(".selectedPerson ul li a.selected").each(function(){
				var thisName = $(this).text();
				var thisUid=$(this).attr("uid");
				
				if(exitName.indexOf(thisName) < 0){
					cc.prepend("<li class='token-input-token-facebook'><p>" + $(this).html() + "</p><span class='deleteName token-input-delete-token-facebook'>x</span></li>");
					
				}
				addUid+=","+thisUid;
			});
			invitees.attr("value",addUid);
			closeDialog();
		});
		
		$("#canclePerson").live("click",function(){
			closeDialog();
		});
		
		//-----------------------------click from left div to right div---end
		
		$(".listTitle .closeDia").click(function(){
			closeDialog();
		});
		
		function closeDialog(){
			/*$(".slider-content ul ul li a").removeClass("selected").removeAttr("clickId");
			$("ul.taskPersonList").html("");
			$(".selectedPerson ul").html("");*/
			$("#slider").hide();
		}
	}
};

