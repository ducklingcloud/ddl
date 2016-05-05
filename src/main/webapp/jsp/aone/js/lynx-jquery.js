function arrIndexOf(arr, item) {
	var pos = -1;
	
	for (var i=0; i<arr.length; i++) {
		if (arr[i] == item) {
			pos = i;
			break;
		}
	}
	return pos;
}

function browserAlert(){
/*	CHECK BROWSER VERSION AND GIVE ALERT */
	this._settings = {
		eventName : '.lynx.browserAlert'
	};
	
	var alertText = '' +
		'<div id="browserAlert" class="fullWidth">' +
			'<h3>浏览器不兼容</h3>' +
			'<p>系统不能完全支持您所使用的浏览器：内容显示和部分功能可能无法正常运行。建议您使用以下更好的浏览器：</p>' +
			'<p id="bAlert-browser">' +
				'<a class="firefox" href="http://firefox.com.cn/" target="_blank">Firefox</a>' +
				'<a class="msie" href="http://windows.microsoft.com/zh-cn/internet-explorer/download-ie/" target="_blank">IE</a>' +
				'<a class="safari" href="http://support.apple.com/kb/dl1531" target="_blank">Safari</a>' +
				'<a class="chrome" href="http://www.google.com/chrome" target="_blank">Chrome</a>' +
				'<a class="opera" href="http://www.opera.com/" target="_blank">Opera</a>' +
			'</p>' +
			'<p id="bAlert-action"><a>知道了，继续使用</a></p>' +
		'</div>';

	if (isUnsupported()) {
		$('body').prepend(alertText).addClass('bAlert');
		if ($.cookie('browserAlert') == 'fold') {
			$('#browserAlert').addClass('fold');
		}
		
		$('#bAlert-action a').bind('click' + this._settings.eventName, function(){
			$('#browserAlert').addClass('fold');
			$.cookie('browserAlert', 'fold', { expires:1 });
		});
		$('#browserAlert h3').bind('click' + this._settings.eventName, function(){
			$('#browserAlert').removeClass('fold');
		});
		
		$('body').addClass('browserAlert');
	}

	/* supportive functions */
	function isUnsupported() {
		//return TRUE if is unsupported browser. FALSE for supported browser
		var iu = $.browser.msie && parseInt($.browser.version, 10) < 8;
		return iu;
	}

};

function spaceNavAdjust() {
	// hide spaceNav when there are more spaces than the bar can hold
	this._settings = {
			eventName: '.lynx.spaceNavAdjust'
	};
	var avatar = this;
	
	var _nav = {	// default shown, hide items when they overflow
			'$body': $('#spaceNav'),
			'$i': $('#spaceNav li'),
			'fold': false
	};
	var _sub = {	// sub menu to be called when there're hidden items 
			'$body': $('#spaceNavSub'),
			'$holder': $('#spaceNavMenu')
	}
	var _ctrl = {	// operation to be shown: add new space or show more items 
			'$create': $('#spaceNavMore .createSpace'),
			'$more': $('#spaceNavMore .moreSpace')
	}
	
	_ctrl.$more.children('a').pulldownMenu({ 'menu': _sub.$holder, 'eventName': avatar._settings.eventName });
	
	//var _limit = 450;	// fixed length for spaceNav
	//var _limit = $('#userBox').position().left - _nav.$body.position().left;	// dynamic length to fill the width
	var leftMarign = $("ul#staticNav").offset().left +  $("ul#staticNav").width() +$("#spaceNavMore").width() + 80;
	var _limit = $('#userBox').offset().left - leftMarign;//同步盘700正常是570,加上活动预告
	
	
	$(".sortableList.spaceNav#spaceNav").show();
	var _cumuPos = $(_nav.$body.children('.current')).outerWidth(false);
	// go through items to find out whether fold and hide overflow items
	_nav.$i.each(function(){
		if (!$(this).hasClass('current')) {
			_cumuPos += $(this).outerWidth(false);
			if (_nav.fold || _cumuPos > _limit) {
				_nav.fold = true;
				$(this).hide();
			}
			else {
				$(this).show();
			}
		}
		else {
			$(this).show();
		}
	});
	if (_nav.fold) {
		if (_sub.$body.length==0) {
			// copy spaceNav to generate folded list
			_sub.$body = _nav.$body.clone();
			
			//vera add icon begin
			_sub.$body.children().each(function(){
				var text = html_encode($(this).children().text());
				var span = "<span class='text'>" + text +"</span>";
				$(this).children("a").html(span + "<span class='sortableIcon'></span>");
				$(this).children("a").addClass("sortableLink");
				$(this).children("a").attr("title",$(this).children().text());
				//old
				//$(this).children().append("<span class='sortableIcon'></span>");
			});
			//vera end
			
			_sub.$body.attr('id', 'spaceNavSub').removeClass('spaceNav')
				.prependTo(_sub.$holder)
				.children().show();
		}
		
		// make sub menu sortable, whilst main nav unsortable
		//_nav.$body.unbind('.sortableList');
		//_sub.$body.bind('.sortableList');
		
		//_ctrl.$create.hide();
		_ctrl.$more.show();
	}
	else {
		// make main menu sortable, whilst sub nav not
		//_sub.$body.unbind('.sortableList');
		//_nav.$body.bind('.sortableList');
		_ctrl.$more.hide();
		//_ctrl.$create.show();
	}
	
}
function html_encode(str)  
{  
	  var s = "";  
	  if (str.length == 0) return "";  
	  s = str.replace(/&/g, "&gt;");  
	  s = s.replace(/</g, "&lt;");  
	  s = s.replace(/>/g, "&gt;");  
	  s = s.replace(/ /g, "&nbsp;");  
	  s = s.replace(/\'/g, "&#39;");  
	  s = s.replace(/\"/g, "&quot;");  
	  s = s.replace(/\n/g, "<br>");  
	  return s;  
} 

function customNavAdjust(para) {
	// hide spaceNav when there are more spaces than the bar can hold
	this._settings = {
			eventName: '.lynx.customNavAdjust'
	};
	var avatar = this;
	
	var _nav = {	// default shown, hide items when they overflow
			'$body': $('#customNav'),
			'$i': $('#customNav li'),
			'fold': false
	};
	var _sub = {	// sub menu to be called when there're hidden items 
			'$body': $('#customNavSub'),
			'$holder': $('#customNavMenu')
	}
	var _ctrl = {	// operation to be shown: add new space or show more items 
			'$more': $('#customNavMore .moreSpace')
	}
	
	if (!para || para!='refresh') {
		_ctrl.$more.children('a').pulldownMenu({
			'menu': _sub.$holder,
			'eventName': avatar._settings.eventName,
			'block': true,
			'anchor': $('#customNav')
		});
	}
	else {
		_sub.$body.html();
	}
	
	var _limit = _nav.$body.width();	// fixed length for spaceNav
	//var _limit = $('#userBox').position().left - _nav.body.position().left - 150;	// dynamic length to fill the width
	
	var _cumuPos = 0;
	
	// go through items to find out whether fold and hide overflow items
	_nav.$i.each(function(){
		_cumuPos += $(this).outerWidth(false);
		if (_nav.fold || _cumuPos > _limit) {
			_nav.fold = true;
			$(this).hide();
		}
		else {
			$(this).show();
		}
	});
	
	if (_nav.fold) {
		if (_sub.$body.length==0) {
			// copy spaceNav to generate folded list
			_sub.$body = _nav.$body.clone();
			_sub.$body.attr('id', 'customNavSub')
				.prependTo(_sub.$holder)
				.children().show();
		}
		
		// make sub menu sortable, whilst main nav unsortable
		//_nav.$body.unbind('.sortableList');
		//_sub.$body.bind('.sortableList');
		
		//_ctrl.$create.hide();
		_ctrl.$more.show();
	}
	else {
		// make main menu sortable, whilst sub nav not
		//_sub.$body.unbind('.sortableList');
		//_nav.$body.bind('.sortableList');
		
		_ctrl.$more.hide();
		//_ctrl.$create.show();
	}
	
}


function localNav_bind() {
	// locate local navigation bar when window scrolls
	this._settings = {
			eventName: '.lynx.localNav'
	}
	
	var _nav = {
			'$subject': $("#navigation"),
			'fixed': false
		};
	var _threshold = parseInt(_nav.$subject.css('top')) - $('#macroNav').outerHeight(); 
	
	$(window).bind('scroll' + this._settings.eventName, function(){
		if (!_nav.fixed && $(window).scrollTop() > _threshold) {
			_nav.$subject.addClass('fixed');
			_nav.fixed = true;
		}
		else if (_nav.fixed && $(window).scrollTop() <= _threshold) {
			_nav.$subject.removeClass('fixed');
			_nav.fixed = false;
		}
	});
}

function switchToLightNav(para) {
	$(window).unbind('.localNav');
	$("#masthead").css({"height":"60px"});
	$("#navigation").addClass("fixed");
	$("#banner").hide();
	
	if (para && para=='noLocalNav') {
		$('#navigation').hide();
	}
	
}

/* FOLDABLE MENU */

function foldableMenu(options) {
	if (!options || typeof(options.controller)!='string') {
		return false;
	}
	var avatar = this;
	
	this._settings = {
			eventName: '.lynx.foldableMenu',
			focus: options.focus || false
	}
	
	$(options.controller)
		.addClass('foldable')
		.live('click' + this._settings.eventName, function(){
			if ($(this).hasClass('folded')) {
				$(this).removeClass('folded');
				$(this).next().slideDown();
				if (avatar._settings.focus) {
					$(this).viewFocus();
				}
			}
			else {
				$(this).addClass('folded');
				$(this).next().slideUp();
			}
		});
	
}



/* SHADE CONSOLE */
/* PARAMETER: options as obj
 *	console:	jquery object, the console itself
 * 	anchor:		jquery object, the object to align to, better a parent object
 */

/* METHODS:
 * 	show()
 * 		show the console.
 * 	hide({ delay:* }):
 * 		hide the console
 * 		* delay: hide after specified ms. notice that calling "show()" will terminate the hide delay
 * 
 * resetPosition({ anchor:, marginX:, marginY: })
 * 		reset the position of the console object
 * 		* anchor:	replace the object to align to
 * 		* marginX:	X-axis margin from anchor
 * 		* marginY:	Y-axis margin to window bottom
 * 
 * is(options)
 * 		return the result of console.is(options)
 * 				
 */


function shadeConsole (options) {
	if (!options || typeof(options.console) == 'undefined' || typeof(options.console.next) == 'undefined' ) {
		return false;
	}
	
	this._settings = {
			eventName : '.lynx.shadeConsole',
			marginX: options.marginX || 20,
			marginY: options.marginY || 20,
			autoHide: (typeof(options.autoHide)!='undefined') ? options.autoHide : false 
	};
	
	this.$console = options.console;
	this.$anchor = options.anchor;
	this._timeout;
	this._hideProtect = false;
	
	this._init();
}
shadeConsole.prototype = {
	_init : function () {
		var avatar = this;
		
		this.resetPosition();
			
		$(window)
		.bind('resize' + avatar._settings.eventName, function(){
				avatar.resetPosition();
		});
		
		if (this._settings.autoHide) {
			$(window).bind('mousemove' + avatar._settings.eventName, function(event){
				if (event.pageX > avatar.$anchor.offset().left
						&& event.pageX < avatar.$anchor.offset().left + avatar.$anchor.outerWidth(false)
						&& event.pageY > 0.9 * $(window).height() + $(window).scrollTop()
						&& !avatar._hideProtect
				) {
					avatar.show();
				}
			});
			
			avatar.$console.bind('mouseleave' + avatar._settings.eventName, function(){
				avatar.hide(2000);
				avatar._hideProtect = true;
				setTimeout(function(){ avatar._hideProtect = false }, 1000);
			});
		}
		
	},
	
	resetPosition : function (options) {
		var avatar = this;
		
		if (options) {
			this.$anchor = options.anchor || this.$anchor;
			this._settings.marginX = options.marginX || this._settings.marginX;
			this._settings.marginY = options.marginY || this._settings.marginY;
		}
		
		this.$console.css({
			left : this.$anchor.offset().left + this._settings.marginX,
			right : $(window).width() - (this.$anchor.offset().left+this.$anchor.width()) + this._settings.marginX,
			bottom : this._settings.marginY
		});
		
		return this;
	},
	
	hide: function (options) {
		var avatar = this;
		var delay = 0;
		
		clearTimeout(this._timeout);
		
		if (options) {
			if (typeof(options)=='number') {
				delay = options;
			}
			else if (typeof(options.delay)!='undefined') {
				delay = parseInt(options.delay);
			}
		}
	
		if (delay!=0) {
			this._timeout = setTimeout(function(){
				avatar.$console.fadeOut();
			}, delay);
		}
		else {
			this.$console.fadeOut();
		}
		
		return this;
	},
	
	show: function () {
		var avatar = this;
		
		clearTimeout(this._timeout);
		this.$console.fadeIn();
		
		return this;
	},
	
	is: function (options) {
		return this.$console.is(options);
	}
	
};


/* show & hide dialog box */
/* PARAMETERS : options as object { name: value, name: value, ... }
 * 	instanceName: name for the instance, to 
 * 	dialog: jquery object, the dialog box to be shown
 * 	close: jquery object, trigger close action
 */

function lynxDialog(options) {
	if (!options || typeof(options.dialog) == 'undefined' || typeof(options.dialog.next) == 'undefined' ) {
		return false;
	}
	
	this._settings = {
			eventName : '.lynx.dialog'
	};
		
	this.$dialog = options.dialog;
	this.$close = options.close;
	this._timeout;
	
	this._init(options);
}
lynxDialog.prototype = {
	_init : function (options) {
		var avatar = this;
		
		this._settings.instName = options.instanceName || ('.'+new Date().getTime());
		
		this._func = {
				beforeShow: (typeof(options.beforeShow) == 'function') ? options.beforeShow : null,
				afterShow: (typeof(options.afterShow) == 'function') ? options.afterShow : null,
				beforeHide: (typeof(options.beforeHide) == 'function') ? options.beforeHide : null,
				afterHide: (typeof(options.afterHide) == 'function') ? options.afterHide : null
		};
		
		this._refresh = options.refresh || {};
		
		if (typeof(options.trigger) != 'undefined') {
			this.$trigger = options.trigger;
			this.$trigger.bind('click' + avatar._settings.eventName + avatar._settings.instName, function(event){
				event.stopPropagation();
				avatar.show();
			});
		}
		
		this.$close.bind('click' + avatar._settings.eventName + avatar._settings.instName, function(){
			avatar.hide();
		});
	},
	
	refresh: function (options) {
		var avatar = this;
		
		var resetTrigger = this._refresh.resetTrigger || false;
		var trigger = $(this._refresh.trigger) || $();
		
		if (options) {
			resetTrigger = options.resetTrigger || resetTrigger;
			trigger = (typeof(options.trigger.next)!='undefined') ? options.trigger : trigger;
		}
		
		if (resetTrigger) {
			$('*').unbind(avatar._settings.eventName + avatar._settings.instName + '.trigger');
		}
		if (trigger) {
			trigger.bind('click' + avatar._settings.eventName + avatar._settings.instName + '.trigger', function(event) {
				event.stopPropagation();
				avatar.show($(this));
			});
		}
		
		return this;
	},
	
	_doHide : function () {
		if (this._func.beforeHide != null) {
			this._func.beforeHide.call(this);
		}
		
		this.$dialog.fadeOut();
		
		if (this._func.afterHide != null) {
			this._func.afterHide.call(this);
		}
	},
	
	hide: function (options) {
		var avatar = this;
		
		clearTimeout(this._timeout);
		if (options && typeof(options.delay)!='undefined') {
			this._timeout = setTimeout(function(){
				avatar._doHide();
			}, parseInt(options.delay));
		}
		else {
			this._doHide();
		}
		return this;
	},
	
	show: function (target) {
		var avatar = this;
		
		clearTimeout(this._timeout);
		if (this._func.beforeShow != null) {
			this._func.beforeShow.call(this, target);
		}
		this.$dialog.fadeIn();
		if (this._func.afterShow != null) {
			this._func.afterShow.call(this, target);
		}
		
		return this;
	},
	
	is: function (options) {
		return this.$dialog.is(options);
	}
};


/* ADD TAG BOX */
function stopDefault( e ) {
    if ( e && e.preventDefault )
       e.preventDefault();
   else
       window.event.returnValue = false;
   return false;
} 

function addTagBox(options) {
	if (!options || typeof(options.input)=='undefined' || typeof(options.input.focus)=='undefined') {
		return false;
	}
	var avatar = this;
	
	// initialize
	//var input = new lynxInput(options.input);
	this.input = new lynxInput({
		input: options.input,
		afterInput: function(val, event, obj) {
			var v = val;
			obj.val('');
			avatar.addTag(v);
			
			if (event.ctrlKey || event.metaKey) {
				avatar.save();
			}
		}
	});
	this.log = { add:[], create:[], del:[], rid:[], tgid:[], multiple:false };
	
	this.$existTList = options.tagList;
	this.pool = { grouped:{}, ungrouped:{} };
	
	this.tp = options.tagPool;
	
	this.$ctrl = {
			$save: options.save,
			$cancel: options.cancel
	};
	
	this.sRef = { curTag : '' };
	
	this.prepareRids = [];
};
addTagBox.eventName = '.lynx.addTabBox';
addTagBox.prototype = {
	refresh: function() {
		this.pool.grouped = this.tp.getItem('blockData').GROUPED;
		this.pool.ungrouped = this.tp.getItem('blockData').UNGROUPED;
		
		var avatar = this;
		this.tp.getItem('blocks').find('a').bind('click.toggleExistTag' + addTagBox.eventName, function(){
			if (!$(this).hasClass('existLock')) {
				if ($(this).parent().hasClass('chosen')) {
					avatar.removeTag({ id: $(this).attr('tag_id'), type:'exist' });
				}
				else {
					avatar.addTag({ id: $(this).attr('tag_id'), text: $(this).text() });
				}
				//vera: if selfTag, can turn into orange
				$(this).removeClass('selfExistLock');
				$(".existTags ul.tagSelf").find('li a[tag_id="'+ $(this).attr('tag_id') + '"]').parent().remove();
			}
		});
		
		return this;
	},
		
	prepare : function(para) {
		var avatar = this;
		
		//$(".existTags ul.tagList, .existTags ul.tagCreate, .existTags ul.tagTogether, .existTags ul.tagSelf").html(""); //vera clean tagList
		$(".existTags ul.tagList, .existTags ul.tagCreate").html(""); //clear
		var chosenNum = 1;
		if(typeof(para.source)!='undefined' && para.source == "toolBar"){
			chosenNum = $("ul#resourceList").find("li.chosen").length; //the num of checked items
		}
		if (para && para.ul) {
			this.log.multiple = true;
			para.ul.children('li').each(function(){
				if ($(this).hasClass('newTag')) {
					avatar.log.rid.push($(this).attr('rid'));
					avatar.prepareRids.push($(this).attr('rid'));
				}
				else {
					// copy to box
					var tgid = $(this).attr('tag_id');
					//if(avatar.log.tgid.length==0 || (avatar.log.tgid.length>0 && $.inArray(""+tgid, avatar.log.tgid)==-1)){//if the array is null or the tagid not exist in the array
					if(avatar.log.tgid.length==0 || (avatar.log.tgid.length>0 && $.inArray(""+tgid, avatar.log.tgid)==-1)){
						avatar.log.tgid.push(tgid);
					}
					$(this).clone().appendTo($(".existTags ul.hideMe"));
					// mark corresponding item in the tag pool
					avatar.tp.getItem('blocks').find('li a[tag_id="'+ $(this).attr('tag_id') + '"]')
						.addClass('selfExistLock')
						.parent().addClass('chosen');
				}
			});
			
			if (para.ridArr) {
				avatar.log.rid = para.ridArr;
			}

		}
		else if (para && para.ridArr){
			this.log.multiple = true;
			avatar.log.rid = avatar.log.rid.concat(para.ridArr);
		}
// -------------vera mark the num begin
		if(avatar.prepareRids.length == chosenNum){
			var tag = new Array();
			var num = new Array();
			var i=0;
		    $(".existTags ul.hideMe").children("li").each(function(){
		    	var tagid = $(this).attr('tag_id');
				if(arrIndexOf(tag, tagid)==-1){//tag._indexOf(tagid)==-1){
					tag[i] = tagid;
					num[arrIndexOf(tag, tagid)]=1;
					i++;
				}else{
					num[arrIndexOf(tag, tagid)]++;
				}
			})
		    avatar.prepareRids = [];
			$.each(tag, function(index, element){
				var cc = $(".existTags ul.hideMe").find('li[tag_id="'+ element + '"]:first');
				if(num[index] == chosenNum){
					if(arrIndexOf($(".existTags ul.tagTogether").text(), cc.text()) < 0){
			    	//if($(".existTags ul.tagTogether").text()._indexOf(cc.text()) < 0){
			    		cc.clone().appendTo($(".existTags ul.tagTogether"));
			    	}
			    	$(".existTags ul.tagSelf").find('li[tag_id="'+ element + '"]').remove();
			    	avatar.tp.getItem('blocks').find('li a[tag_id="'+ element + '"]')
					.removeClass('selfExistLock').addClass('existLock')
			    }else{
			    	cc.clone().appendTo($(".existTags ul.tagSelf"));
			    }
			});
		    
		    $(".existTags ul.tagSelf li a:not(.delete-tag-link)").live('click',function(e){
				stopDefault(e); 
				if(arrIndexOf(avatar.$existTList.text(), $(this).parent().text()) < 0){
					$(this).parent().clone().appendTo(avatar.$existTList);
				}
				$(this).parent().remove();
			});
		}
		
// -------------vera mark the num end
		avatar.log.rid = $.unique(avatar.log.rid);
		return this;
	},
	
	/*prepare : function(para) {
		var avatar = this;
		if (para && para.ul) {
			this.log.multiple = false;
			para.ul.children('li').each(function(){
				if ($(this).hasClass('newTag')) {
					avatar.log.rid.push($(this).attr('rid'));
				}
				else {
					// copy to box
					var tgid = $(this).attr('tag_id');
					if(avatar.log.tgid.length==0 || (avatar.log.tgid.length>0 && $.inArray(""+tgid, avatar.log.tgid)==-1)){
						$(this).clone().appendTo(avatar.$existTList);
						avatar.log.tgid.push(tgid);
					}
					// mark corresponding item in the tag pool
					avatar.tp.getItem('blocks').find('li a[tag_id="'+ $(this).attr('tag_id') + '"]')
						.addClass('existLock')
						.parent().addClass('chosen');
				}
				
			});
			
			if (para.ridArr) {
				avatar.log.rid = para.ridArr;
			}*/
			
/*
			this.$existTList.children().each(function(){
				var li = $(this);
				$(this).addClass('exist').append('<a class="lightDel"></a>');
				$(this).children('.lightDel').bind('click.delTag' + addTagBox.eventName, function(){
					avatar.removeTag({ id: li.attr('tag_id'), type: 'prev' });
				}); 
			});
*/
		/*}
		else if (para && para.ridArr){
			this.log.multiple = true;
			avatar.log.rid = avatar.log.rid.concat(para.ridArr);
		}
		avatar.log.rid = $.unique(avatar.log.rid);
		return this;
	},*/
	clean: function() {
		this.$existTList.find('a.lightDel').unbind(addTagBox.eventName);
		this.$existTList.html('');
		this.$existTList.parent().parent().find("ul").html('');   //vera clean the four ul
		this.input.getItem().val('');
		this.log.add.length=0;
		this.log.create.length=0;
		this.log.del.length=0;
		this.log.rid.length=0;
		this.tp.getItem('blocks').find('.chosen').removeClass('chosen');
		return this;
	},
	
	addTag: function (para) {
		var avatar = this;
		var targetID = -1;	// -1: do not handle append tag
		var newTag = '';
		var doAdd = true;	// true: create new tag
		
		// prepare data
		if (typeof(para.id)!='undefined' && typeof(para.text)!='undefined') {
			// from exist tag list
			targetID = para.id;
			doAdd = false;
			newTag = para.text;
			if (arrIndexOf(avatar.log.add, targetID)!=-1) {
				targetID = -1;
			}
		} 
		else if (typeof(para)=='string' && $.trim(para)!='') {
			var newTag = $.trim(para);
			var newTagLow = newTag.toLowerCase();
			
			// check exist tag list ( previous + newly added )
			this.$existTList.children().each(function(){
				if ($.trim($(this).text()).toLowerCase() == newTagLow) {
					$(this).css('border-color', '#f90');
					doAdd = false;
				}
			});

			if (doAdd && avatar.pool.ungrouped) {
				// check ungrouped tag list
				if (avatar.pool.ungrouped[newTagLow]) {
					targetID = avatar.pool.ungrouped[newTagLow];
					doAdd = false;
				}
			}
			
			if (doAdd && avatar.pool.grouped) {
				// check grouped tag list
				for (var g in avatar.pool.grouped) {
					if (doAdd && avatar.pool.grouped[g][newTagLow]) {
						targetID = avatar.pool.grouped[g][newTagLow];
						doAdd = false;
					}
				}
			}
		}
		
		if (!doAdd && targetID!=-1) {
			// in exist tags
			var bk = this.tp.getItem('blocks');
			
			bk.find('li a[tag_id="'+ targetID + '"]').parent().addClass('chosen');
			var thisItem = $('<li class="add" tag_id="' + targetID + '">' + newTag + '<a class="lightDel"></a></li>').appendTo(this.$existTList);
			thisItem.children('.lightDel').bind('click.delTag' + addTagBox.eventName, function(){
				avatar.removeTag({ id: targetID, type: 'exist' }, $(this));
			});
			this.$existTList.scrollTop(thisItem.position().top);
			
			this.log.add.push(targetID);
		}
		else if (doAdd) {
			// create new tag
			var thisItem = $('<li class="create">' + newTag + '<a class="lightDel"></a></li>').appendTo(this.$existTList);
			thisItem.children('.lightDel').bind('click.delTag' + addTagBox.eventName, function(){
				avatar.removeTag(newTag, $(this));
			});
			this.$existTList.scrollTop(thisItem.position().top);
			
			this.log.create.push(newTag);
		}
		return this;
	},
		
	removeTag: function (para, obj) {
		var avatar = this;
		
		if (typeof(para.id)!='undefined' && typeof(para.type)!='undfined') {
			var bk = this.tp.getItem('blocks');
			
			switch (para.type) {
			case 'prev':	// delete previous tags, to delete
				avatar.log.del.push(para.id);
				break;
			case 'exist': // remove exist tags from 'add' list
				var pos = arrIndexOf(avatar.log.add, para.id);
				if (pos!=-1) {
					avatar.log.add.splice(pos, 1);
				}
				break;
			}
			
			if (obj && typeof(obj.remove)=='function') {
				obj.unbind(addTagBox.eventName).parent().remove();
			}
			else {
				avatar.$existTList.find('li[tag_id="'+ para.id +'"] a')
					.unbind(addTagBox.eventName)
					.parent().remove();
			}
			
			bk.find('li a[tag_id="' + para.id + '"]').parent().removeClass('chosen');
		}
		else if (typeof(para)=='string' && $.trim(para)!='') {
			// remove new tag from 'create' list
			var pos = arrIndexOf(avatar.log.create, $.trim(para));
			if (pos!=-1) {
				avatar.log.create.splice(pos, 1);
			}
			
			if (obj && typeof(obj.remove)=='function') {
				obj.unbind(addTagBox.eventName).parent().remove();
			}
		}
		return this;
	},
	
	save : function() { return this; }
};


function lynxInput(options) {
	if (!options || typeof(options.input)=='undefined' || typeof(options.input.val)=='undefined') {
		return false;
	}
	var avatar = this;
	
	this.$input = options.input;
	this.afterInput = options.afterInput || function() { this.$input.select(); };
	this.emptyInput = options.emptyInput || function() {};
	this._func = options.events || {};
	
	this._settings = {
			eventName: '.lynx.lynxInput'
	}
	
	var kref = { valLastKeyUp : '', lastKeyDown : '', ready : false };
	
	this.$input
		.bind('keydown' + this._settings.eventName, function(key){
			if (key.keyCode==KEY.ENTER && (kref.lastKeyDown != 229 || kref.ready)) {
				if ($(this).val()!='') {
					avatar.afterInput($(this).val(), key, $(this));
					key.preventDefault();
				}
				else {
					avatar.emptyInput(key, $(this));
				}
			}
			
			kref.lastKeyDown = key.keyCode;
			
			if (typeof(avatar._func.keydown)=='function') {
				avatar._func.keydown.call(this, key);
			}
		})
		.bind('keyup' + this._settings.eventName, function(key){
			if (key.keyCode==KEY.ENTER && $(this).val() == kref.valLastKeyUp && kref.lastKeyDown == 229 && $(this).val()!='') {
				// Chinese input, key Enter doesn't change value, mark as input completed
				kref.ready = true;
			}
			else {
				kref.ready = false;
			}
			
			kref.valLastKeyUp = $(this).val();
			kref.lastKey = key.keyCode;
			
			if (typeof(avatar._func.keyup)=='function') {
				avatar._func.keyup.call(this, key);
			}
		})
		.bind('change' + this._settings.eventName, function(event){
			// mozilla, opera & other do not respond to Chinese input
			if ($(this).val()!='' && $(this).is(':focus')) {
				avatar.afterInput($(this).val(), event, $(this));
			}
			else if ($(this).val()==''){
				avatar.emptyInput(event, $(this));
			}
			
			if (typeof(avatar._func.change)=='function') {
				avatar._func.change.call(this, key);
			}
		});
	
	for (var f in avatar._func) {
		if (f!='keydown' && f!='keyup' && f!='change') {
			this.$input.bind(f + this._settings.eventName, avatar._func[f]);
		}
	}
	
}
lynxInput.prototype = {
	getItem : function() {
		return this.$input;
	},
	
	refreshAction : function(func) {
		if (typeof(func)=='function') {
			this.afterInput = func;
		}
		return this;
	}
};


function tagPool(options) {
	var avatar = this;
	
	// initialize
	this.$pl = options.pool;
	this.$scr = options.scroller;
	this.$bk = options.blocks || null;
	this.bkData = options.blockData || null;

	this._settings = {
			bkClass : options.blockClass || 'tG-block'
	}
	
	this._init();
}
tagPool.eventName = '.lynx.tagPool';
tagPool.prototype = {
	_init: function() {
		return this;
	},
		
	getItem : function(i) {
		switch (i) {
		case 'scroller':		return this.$scr; break;
		case 'blocks':			return this.$bk; break;
		case 'blockData':		return this.bkData; break;
		case 'pool':
		default:	return this.$pl;
		}
	},
	locate: function (index) {
		if (typeof(index) == 'number' && index>=0 && index<this.$bk.length) {
			// index within range
			var pos = this.$_scrPara.mid - $(this.$bk[index]).position().left;
			pos = (pos < this._scrPara.min) ? this._scrPara.min : (
					(pos > this._scrPara.max) ? this._scrPara.max : pos
			);
			this.$scr.animate({ left:pos }, 700);						
		}
		return this;
	},
	refresh: function (para) {
		avatar = this;

		if (para) {
			this.bkData = { UNGROUPED:{}, GROUPED:{} };
			this.$scr.html('');
			bkIndex = 0;
			
			// ungrouped tags
			bk = $('<div class="ungrouped ' + this._settings.bkClass + '" bkindex="' + (bkIndex++) + '"><p class="tG-groupName">未分组标签</p><ul class="tG-list"></ul></div>').appendTo(this.$scr);
			ul = bk.find('ul.tG-list');
			for (var i=0; i < para.freeTags.length; i++) {
				if (para.freeTags[i]) {
					var t = para.freeTags[i];
					this.bkData.UNGROUPED[$.trim(t.title).toLowerCase()] = t.id;
					ul.append('<li><a tag_id="' + t.id + '">' + t.title + '</a></li>');
				}
			}
			if (para.freeTags.length==0) {
				ul.append('<li class="NA">无</li>');
			}
			if(i == para.freeTags.length){
				ul.append("<li class='ui-clear'></li>")
				// ------tagPool show firstFive begin
				ul.each(function(){
					var childrenNum = $(this).find("li").length;
					var i = 0;
					if(childrenNum > 5) {
						$(this).find("li").each(function(){
							i++;
							if(i>5){
								$(this).hide();
							}
						})
						//$(this).find("li.moreTags").remove();
						//$(this).append("<li class='moreTags'> > more</li>");
					}
				})
				
				ul.mouseenter(function(){
					$(this).children().show("normal");
					$(this).find("li.moreTags").remove();
				})
				ul.mouseleave(function(){
					var j = 0;
					$(this).children().each(function(){
						j++;
						if(j>5){
							$(this).hide();
							//$(this).parent().find("li.moreTags").remove();
							//$(this).parent().append("<li class='moreTags'> > more</li>");
						}
					})
					
				})
				// ------tagPool show firstFive end
			}
			
			for (var i=0;i<para.groupMap.length;i++){
				var g = para.groupMap[i];
				var gi = $.trim(g).toLowerCase();
				this.bkData.GROUPED[gi] = {};
				bk = $('<div class="grouped ' + this._settings.bkClass + '" bkindex="' + (bkIndex++) + '"><p class="tG-groupName">' + g["name"] + '</p><ul class="tG-list"></ul></div>').appendTo(this.$scr);
				ul = bk.find('ul.tG-list');
				for (var j=0; j < g["tags"].length; j++) {
					var t = g["tags"][j];
					if (t) {
						this.bkData.GROUPED[gi][$.trim(t.title).toLowerCase()] = t.id;
						ul.append('<li><a tag_id="' + t.id + '">' + t.title + '</a></li>');
					}
				}
				if (g["tags"].length==0) {
					ul.append('<li class="NA">无</li>');
				}
				if(j == g["tags"].length){
					ul.append("<li class='ui-clear'></li>")
					// ------tagPool show firstFive begin
					ul.each(function(){
						var childrenNum = $(this).find("li").length;
						var i = 0;
						if(childrenNum > 5) {
							$(this).find("li").each(function(){
								i++;
								if(i>5){
									$(this).hide();
								}
							})
							//$(this).find("li.moreTags").remove();
							//$(this).append("<li class='moreTags'> > more</li>");
						}
					})
					
					ul.mouseenter(function(){
						$(this).children().show("normal");
						$(this).find("li.moreTags").remove();
					})
					ul.mouseleave(function(){
						var j = 0;
						$(this).children().each(function(){
							j++;
							if(j>5){
								$(this).hide();
								//$(this).parent().find("li.moreTags").remove();
								//$(this).parent().append("<li class='moreTags'> > more</li>");
							}
						})
						
					})
					// ------tagPool show firstFive end
				}
			}
		};
		
		this.$bk = this.$scr.children('.' + this._settings.bkClass);
		
		this.$scr.pitfall({ column: 4 });
		
		return this;
	},
	refreshAppearance : function () {
		this.$scr.pitfall({ column: 4 });
	}
	
	
};



/* SEARCH INPUT */
function searchBox(options) {
	if (!options || typeof(options.container)=='undefined') {
		return false;
	}
	var avatar = this;
	
	this.$con = options.container;
	this._settings = {
			stdbyText: (options.standbyText) || '',
			whileType: (options.searchWhileTyping) || false
	};
	
	this._init();
}
searchBox.eventName = '.lynx.searchBox';
searchBox.prototype = {
	_init: function() {
		var avatar = this;
		
		this.$con.addClass('searchBox');
		
		this.$input = $('<input type="text" name="search_input" value="' + this._settings.stdbyText + '" />');
		this.$input.addClass('standby').appendTo(this.$con);
		
		this.lynxi = new lynxInput({
			input: avatar.$input, 
			afterInput: function(val, event, obj){
				avatar.doSearch(val,event);
			},
			emptyInput: function(event, obj) {
				avatar.resetSearch();
			},
			events: {
				keyup: function(key) {
					if (key.keyCode == KEY.ESCAPE) { //ESC
						avatar._clearInput().resetSearch();
						$(this).blur();
					}
					else {
						avatar._enableClear();
					}
				},
				focus: function() {	avatar._prepareInput(); },
				blur: function() {	avatar._leaveInput(); }
			}
		})
		
		this.$reset = $('<a class="search_reset"></a>');
		this.$reset.attr('disable', 'true').appendTo(this.$con);
		
		this.$reset.bind('click' + searchBox.eventName, function(){
			avatar._clearInput()._leaveInput().resetSearch();
		});
	},
	
	_clearInput : function() {
		this.$reset.attr('disable', 'true');
		this.$input.val('');
		return this;
	},
	_leaveInput : function() {
		this.$input.addClass('standby');
		if (this.$input.val()==='') {
			this.$input.val(this._settings.stdbyText);
			this.$reset.attr('disable', 'true');
		}
		else {}
		return this;
	},
	_prepareInput : function(){
		this.$input.removeClass('standby');
		if (this.$input.val()==this._settings.stdbyText) {
			this.$input.val('');
		}
		else {
			this.$input.select();
		}
		return this;
	},
	_enableClear : function() {
		if (this.$reset.attr('disable')=='true') {
			this.$reset.attr('disable', 'false');
		}
		return this;
	},
	
	focus : function() {
		this.input.$input.focus();
		return this;
	},
	blur : function() {
		this.input.$input.blur();
		return this;
	},

	doSearch: function(QUERY) {
		alert('please override doSearch(QUERY): search QUERY = ' + QUERY);
		return this;
	},
	resetSearch: function() {
		alert('please override resetSearch(): triggered by empty query or quit search');
		return this;
	}
	
};
/* END of searchBox */




/* CHECK ITEMS */
/* binary status, allows ajax communication with server */

/* PARAMETER: options as an object */
/* 	instName:		instance name to be added to events.		default: create time. ms
 
 * -------- check related -------------------- 
 *	checkClass:		class to be added when item is checked.		default: "checked"
 *	getCheckObject:
 *					a function to return an object to be add the checkClass.		default: the object itself
 *	whenCheck:		a function to be called when the item is checked.				default: null
 *	whenUncheck:	a function to be called when the item is UN-checked.			default: null
 
 * -------- ajax related ---------------------
 * 	url:		static url for ajax
 * 	makeUrl:
 * 		a function to create dynamic url with the object	default: null
 * 			* makeUrl will override the setting of static url
 *	
 *	! if none of "url" or "makeUrl" is defined, the ajax will NOT be called
 *
 *	whenSuccess:
 *		a function to be called at $.ajax:success		default: null
 *			* if defined, need manually operate check action
 *				* this object is passed as second parameter (data, obj)
 *				* call obj.check(true) / obj.check(false) to Check / Uncheck, call obj.check() to toggle
 *			* if not defined, will simply toggle the check	
 *	whenFail:
 *		a function to be called when $.ajax:error		default: null
 *			* this object is passed as the last parameter apart from error default parameters
 *				(jqXHR, textStatus, errorThrown, thisObj)
 */

(function ($) {
	$.fn.checkItem = function(options){
		var avatar = this;
		var _status;
				
		this._settings = {
				eventName: '.lynx.checkItem',
				ckClass: 'checked'
		}
		
		this._func = {
				getCheckObj: function(obj) { return obj; }
		};
		
		if (options) {
			this._settings.instName = options.instanceName || ('.'+new Date().getTime());
			this._settings.url = options.url || null;
			this._settings.ckClass = options.checkClass || this._settings.ckClass;
			
			this._func.getCheckObj = options.getCheckObject || this._func.getCheckObj;
			this._func.whenCheck = options.whenCheck || null;
			this._func.whenUncheck = options.whenUncheck || null;
			
			this._func.makeUrl = options.makeUrl || null;
			this._func.whenSuccess = options.whenSuccess || null;
			this._func.whenFail = options.whenFail || null;
		}
		
		this.checkThis = function(para) {
			_normalCheck(para);
		}
		
		this
		.unbind('click' + this._settings.eventName)
		.bind('click' + this._settings.eventName + this._settings.instName, function(event){
			event.stopPropagation();
			
			if ((avatar._func.getCheckObj(avatar)).hasClass(avatar._settings.ckClass)) {
				_status = false;
			}
			else {
				_status = true;
			}
			
			var query;
			if (avatar._func.makeUrl != null) {
				query = avatar._func.makeUrl(avatar) || avatar._settings.url || '';
			}
			else {
				query = avatar._settings.url || '';
			}
			
			if (query!='') {
				// calls ajax
				$.ajax({
					url: query,
					success: function(data){
						if (avatar._func.whenSuccess != null) {
							avatar._func.whenSuccess(data, avatar);
						}
						else {
							_normalCheck();
						}
					},
					error: function(jqXHR, textStatus, errorThrown){
						if (avatar._func.whenFail != null) {
							avatar._func.whenFail(jqXHR, textStatus, errorThrown, avatar);
						}
					},
					type: 'POST',
					dataType: 'json'
				});
			}
			else {
				_normalCheck();
			}
		});
		
		function _normalCheck(para) {
			var obj = avatar._func.getCheckObj(avatar);
			var c = avatar._settings.ckClass;

			(typeof(para) != 'undefined')
				? (
					para ? _checkTrue(obj, c) : _checkFalse(obj, c)
				)
				: (
					obj.hasClass(c) ? _checkFalse(obj, c) : _checkTrue(obj, c)
				);
		}
		
		function _checkTrue(obj, c) {
			obj.addClass(c);
			if (avatar._func.whenCheck != null) {
				avatar._func.whenCheck(avatar, obj);
			}
		}
		
		function _checkFalse(obj, c) {
			obj.removeClass(c);
			if (avatar._func.whenUncheck != null) {
				avatar._func.whenUncheck(avatar, obj);
			}
		}
	};
}(jQuery));

(function ($) {
	$.fn.pitfall = function(para) {
		var _settings = {
				eventName: '.lynx.pitfall'
		}
		
		if (para == 'clean') {
			_clean(this);
			return this;
		}
		else if (para == 'refresh') {
			_makePitfall();
			return this;
		}
		
		var _val = {
				colCount : 3,
				mTop: 20,
				mBottom: 20,
				mLeft: 20,
				mX: 20,
				mY: 20,
				colWidth: (this.outerWidth(false)-80) / 3,
				manualWidth: false,
				resize: true
		};
		
		if (para) {
			_val = {
					colCount : para.column || _val.colCount,
					mTop : para['margin-top'] || _val.mTop,
					mBottom : para['margin-bottom'] || _val.mBottom,
					mLeft : para['margin-left'] || _val.mLeft,
					mX : para.marginX || para.margin || _val.mX,
					mY : para.marginY || para.margin || _val.mY,
					colWidth : para.width || 0,
					manualWidth: (typeof(para.width)=='undefined') ? false : true, 
					resize : (typeof(para.resize)=='undefined') ? true : para.resize
			}
			if (_val.colWidth == 0 && !_val.manualWidth) {
				_val.colWidth = (this.width() - 2*_val.mLeft - (_val.colCount-1)*_val.mY) / _val.colCount;
			}
		}
		
		
		var index;
		var _time;
		
		var avatar = this.addClass('pitfall');
		
		_time = setTimeout(_makePitfall, 300);
		if (_val.resize) {
			$(window).bind('resize' + _settings.eventName, function(){
				clearTimeout(_time);
				_time = setTimeout(_makePitfall, 300);
			});
		}
		
		function _makePitfall() {
			var col = [];
			var left = [];
			
			if (!_val.manualWidth) {
				_val.colWidth = (avatar.outerWidth(false) - 2*_val.mLeft - (_val.colCount-1)*_val.mY) / _val.colCount;
			}
			
			avatar.children().css('width', _val.colWidth);
			
			for (var i=0; i<_val.colCount; i++) {
				col.push(_val.mTop);
				left.push(_val.mLeft + _val.colWidth * i + _val.mX * i);
			}
			
			var iThis = 0;	// total index of the item
			var prevCol = -1;	// record previous item column index
			setTimeout(function(){
				avatar.children().each(function(){
					var index = _getColumn(col, (prevCol<col.length-1) ? prevCol+1 : 0, $(this).height());
					
					$(this).css({
						'position': 'absolute',
						'top': index.top,
						'left': left[index.colIndex]
					}).show();
					col[index.colIndex] = index.top + $(this).height() + _val.mY;
					
					iThis ++;
					prevCol = index.colIndex;
				});
				
				index = _getHeight(col);
				avatar.css({ 'height': index-2*parseInt(avatar.css('padding-top'))+_val.mBottom });
			}, 150);
		}
		
		function _getColumn(C, I, H) {
			var r = { 'colIndex':I, 'top': C[I], 'oriTop': C[I] };
			for (var i=0; i<C.length; i++) {
				if (r.top > C[i] && (r.oriTop - C[i]) > 0.7*H) {
					r.colIndex = i;
					r.top = C[i];
				}
			}
			return r;
		}
		
		function _getHeight(C) {
			var height = 0;
			for (var i=0; i<C.length; i++) {
				height = (height > C[i]) ? height : C[i];
			}
			return height;
		}
		
		function _clean(obj) {
			obj.removeClass('pitfall').css({ 'height': '' });
			obj.children().css({ 'position': '', 'top': '', 'left': '', 'width':'' });
			$(window).unbind(_settings.eventName);
		}
		
		return this;
	}
	
}(jQuery));