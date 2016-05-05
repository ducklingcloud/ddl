(function ($) {
	$.fn.checkAll = function(options){
	/* use controller checkbox to check a group of checkboxes
	/* PARAMETER
	/*	slave (required) : jQuery object to controlled
	 */
		if (options.slave==null) {
			return false;
		}
		
		var controller = this;
		var slave = options.slave;
		
		this.change(function(){
			if (controller.attr('checked')=='checked' || controller.attr('checked')==true) {
				slave.attr('checked', 'checked');
			}
			else {
				slave.removeAttr('checked');
			}
		});
		
		slave.change(function(){
			controller.attr('checked', '');
		});
	};
}(jQuery));




/* uilib 0.9 */
var dialogToTop = 80;

$(document).ready(function(){
	
/* 可折叠标题行为 */
	/* initiate */
	//$('.ui-title-fold').next().hide();
	$('.ui-title-fold').attr('title', '点击以展开');
	$('.ui-title-expand').attr('title', '点击以收起');
	/* response */
	$('.ui-title-fold, .ui-title-expand').click(function(){
		if ($(this).hasClass('ui-title-fold')) {
			$(this).next().slideDown();
			$(this).removeClass('ui-title-fold').addClass('ui-title-expand');
			$(this).attr('title', '点击以收起');
		}
		else if ($(this).hasClass('ui-title-expand')) {
			$(this).next().slideUp();
			$(this).removeClass('ui-title-expand').addClass('ui-title-fold');
			$(this).attr('title', '点击以展开');
		}	
	});
	
/* 可折叠面板行为 */
	/* initiate */
	$('.ui-panel-fold').next().hide();
	$('.ui-panel-fold').attr('title', '点击以展开');
	$('.ui-panel-expand').attr('title', '点击以收起');
	/* response */
	$('.ui-panel-fold, .ui-panel-expand').click(function(){
		if ($(this).hasClass('ui-panel-fold')) {	
			if ($(this).parent().parent().hasClass('ui-panel-group')) {
				/* fold other panels if marked in the same group */
				$(this).parent().parent().find('.ui-panel-expand').click();
			}
			$(this).next().slideDown();
			$(this).removeClass('ui-panel-fold').addClass('ui-panel-expand');
			$(this).attr('title', '点击以收起');
		}
		else if ($(this).hasClass('ui-panel-expand')) {
			$(this).next().slideUp();
			$(this).removeClass('ui-panel-expand').addClass('ui-panel-fold');
			$(this).attr('title', '点击以展开');
		}
	});
	
	
/* 标签栏行为 */
	/* initiate */
	$('.ui-tab > ul > li, .ui-tab-ver > ul > li').each(function(){
		var thisIndex = $(this).parent().find('li').index($(this));
		if ($(this).hasClass('ui-tab-current')) {}
		else {
			var corDiv = $(this).parent().parent().find('.ui-tab-content').get(thisIndex);
			$(corDiv).hide();
		}
	});
	/* response */
	$('.ui-tab, .ui-tab-ver').each(function(){
		if ($(this).hasClass('ui-tab-hover')) {
			$(this).children('ul').children().mouseover(function(){
				$(this).parent().find('li').removeClass('ui-tab-current');
				$(this).parent().parent().find('.ui-tab-content').hide();
				$(this).addClass('ui-tab-current');
				var thisIndex = $(this).parent().find('li').index($(this));
				var corDiv = $(this).parent().parent().find('.ui-tab-content').get(thisIndex);
				$(corDiv).show();
			});
		}
		else {
			$(this).children('ul').children().click(function(){	
				$(this).parent().find('li').removeClass('ui-tab-current');
				$(this).parent().parent().find('.ui-tab-content').hide();
				$(this).addClass('ui-tab-current');
				var thisIndex = $(this).parent().find('li').index($(this));
				var corDiv = $(this).parent().parent().find('.ui-tab-content').get(thisIndex);
				$(corDiv).show();
			});
		}
	});
	
/* 对话框 */
//	$('.ui-dialog-cover').hide();
//	$('.ui-dialog').hide();
	
	$('.ui-dialog-close, .ui-dialog-x').click(function(){
		ui_hideDialog($(this).parents('.ui-dialog').attr('id'));
	});
	
	/*
	$(document).scroll(function(){
		$('.ui-dialog').each(function(){
			if ($(this).css('display')!='none') {
				$(this).css('top', $(document).scrollTop() + dialogToTop);
			}
		});
	});
	*/
	/* functions */
	//function ui_showDialog(objID) {}
	//function ui_hideDialog(objID) {}
	
/* 聚光灯 */
	//$('.ui-spotLight').hide();
	$('.ui-spotLight').mouseover(function(){});
	/* functions */
	//function ui_spotLight(obj, stat, text, fade) {}
	
	
/* 虚拟页面 */
	$('.ui-forgePage-content').each(function(){
		var indexThis = $(this).parent().find('.ui-forgePage-content').index($(this));
		if (indexThis==0) {
			$(this).parents('.ui-forgePage').css('height', $(this).height());
		}
		else {
			$(this).css('left', '100%');
		}
	});
	$('input.ui-forgePage-prev').click(function(){
		var $content = $(this).parents('.ui-forgePage-content');
		if ($content.prev().html()!=null) {
			if ($(this).parents('.ui-forgePage').height() < $content.prev().height()) {
				$(this).parents('.ui-forgePage').animate({height: $content.prev().height()});
			}
			$content.animate({left:'100%'});
//			$content.prev().animate({left:'0%'});
			$content.prev().fadeIn();
		}
	});
	$('input.ui-forgePage-next').click(function(){
		var $content = $(this).parents('.ui-forgePage-content');
		if ($content.next().html()!=null) {
			if ($(this).parents('.ui-forgePage').height() < $content.next().height()) {
				$(this).parents('.ui-forgePage').animate({height: $content.next().height()});
			}
//			$content.animate({left: '-100%'});
			$content.fadeOut();
			$content.next().animate({left: '0%'});
		}
	});
	
/* 进度条 */
/*	$('.ui-progress > li').click(function(){
		$(this).parent().children('li').each(function(){
			$(this).removeClass('current');
		});
		$(this).addClass('current');
	});
*/	

/* 半透明隐藏区域 */
/*	Low transparency to lower visual noise. Full transparency when focused */
	$('.ui-transparency').mouseover(function(){
		$('.ui-transparency').removeClass('solid');
		$('.ui-transparency').addClass('trans');
		$(this).addClass('solid');
		$(this).removeClass('trans');
	});
});



/* --- 自定义函数 --- */
/* 对话框 */
function ui_showDialog(objID, duration, POSITION) {
	var dialog = $('#'+objID+'.ui-dialog');
	if (!POSITION==undefined) {
		switch (POSITION['pos']) {
			case 'mouseside':
				break;
			case 'belowAction':
				var posLeft = POSITION['anchor'].offset().left-$(dialog).width()/2;
				if ((posLeft + $(dialog).width())>document.body.scrollWidth) {
					posLeft = document.body.scrollWidth - $(dialog).outerWidth();
				}
				$(dialog).css('top', POSITION['anchor'].offset().top)
					.css('left', posLeft);
				break;
			default:
				var posTop = $(document).scrollTop();
				var posLeft = (document.body.scrollWidth - $(dialog).width())/2;
				if (posLeft<0) posLeft = 0;
				$('.ui-dialog-cover').css('width', $(document).width())
					.css('height', $(window).height());
					//30 is an adjustment in FF and IE
				$(dialog).css('top', posTop + dialogToTop);
				$(dialog).css('left', posLeft);
		}
	}
	else {
		var posTop = $(document).scrollTop();
		var posLeft = (document.body.scrollWidth - $(dialog).width())/2;
		if (posLeft<0) posLeft = 0;
		$('.ui-dialog-cover').css('width', $(document).width())
			.css('height', $(window).height());
			//30 is an adjustment in FF and IE
		$(dialog).css('top', posTop + dialogToTop);
		$(dialog).css('left', posLeft);
	}
	
	$('.ui-dialog-cover').show();
	$(dialog).fadeIn();
	
	if (typeof(duration)=='number') {
		ui_hideDialog(objID, duration);
	}
}

function ui_showShareDialog(objID, duration, POSITION) {
	var dialog = $('#'+objID);
	var posTop = $(document).scrollTop();
	var posLeft = (document.body.scrollWidth - $(dialog).width())/2;
	if (posLeft<0) posLeft = 0;
	$('.ui-dialog-cover').css('width', $(document).width())
		.css('height', $(window).height());
		//30 is an adjustment in FF and IE
	$(dialog).css('top', posTop + dialogToTop);
	$(dialog).css('left', posLeft);
	
	$('.ui-dialog-cover').show();
	$(dialog).fadeIn();
	
	if (typeof(duration)=='number') {
		ui_hideDialog(objID, duration);
	}
}

function ui_hideDialog(objID, delay) {
	var dialog = document.getElementById(objID);
	
	if (typeof(delay)=='number') {
		window.setTimeout(function(){
			$(dialog).hide(0);
			$('.ui-dialog-cover').hide(0);
		}, delay);
	}
	else {
		
		//$('.ui-dialog-cover').hide();
		$(dialog).hide();
	}
}


/* 聚光灯 */
function ui_spotLight(obj, stat, text, fade) {
	if (typeof(obj)=='object')
		var spotLight = obj;
	else if (typeof(obj)=='string')
		var spotLight = document.getElementById(obj);
	$(spotLight).removeClass('ui-spotLight-success')
		.removeClass('ui-spotLight-fail')
		.removeClass('ui-spotLight-processing');
	
	switch(stat) {
	case 'processing': {
		$(spotLight).addClass('ui-spotLight-processing');
		$(spotLight).html(text);
		$(spotLight).show();
		break;
	}
	case 'success': {
		$(spotLight).addClass('ui-spotLight-success');
		$(spotLight).html(text);
		$(spotLight).show();
		if (fade=='fade')
			$(spotLight).delay(1000).fadeOut();
		break;
	}
	case 'fail': {
		$(spotLight).addClass('ui-spotLight-fail');
		$(spotLight).html(text);
		$(spotLight).show();
		if (fade=='fade')
			$(spotLight).delay(2000).fadeOut();
		break;
	}
	case 'show': {
		$(spotLight).slideDown();
		break;
	}
	case 'hide': {
		$(spotLight).fadeOut();
		break;
	}
	}//end of switch
}
