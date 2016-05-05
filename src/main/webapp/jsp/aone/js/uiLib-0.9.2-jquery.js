function getEventName(eventName) {
	return (typeof(eventName) == 'string') ? 
			((eventName.charAt(0) == '.') ? eventName : '.' + eventName) : '';
};

var KEY = {
	    BACKSPACE: 8,
	    TAB: 9,
	    ENTER: 13,
	    ESCAPE: 27,
	    SPACE: 32,
	    PAGE_UP: 33,
	    PAGE_DOWN: 34,
	    END: 35,
	    HOME: 36,
	    LEFT: 37,
	    UP: 38,
	    RIGHT: 39,
	    DOWN: 40,
	    NUMPAD_ENTER: 108,
	    COMMA: 188,
	    COMMA_CHN: 188 //Chinese input mode will feed keyCode 229 for every key press. need to think later
	};

(function ($) {
	$.fn.checkAll = function(options){
	/* use controller checkbox to check a group of checkboxes
	/* PARAMETER
	/*	slave (required) : object to controlled
	 * 		* use jQuery object: for static lists
	 * 		* use string: for dynamic lists, will reselect objects by string
	 */
		
		if (!options) {
			return this;
		}
		else if (typeof(options)=='string' && typeof(methods[options])=='function') {
			return methods[options].call(this);
		}
		else if (!options || typeof(options.slave)=='undefined') {
			return this;
		}
		
		var _controller = this;
		
		var _slave = { '$list':null, 'checkCount': 0, 'list':null };
		if (typeof(options.slave.next)=='function') {
			//jQuery Ojbect
			_slave.$list = options.slave;
		}
		else if (typeof(options.slave)=='string') {
			//selector string
			_slave.list = options.slave;
		}
		
		var _eName = '.uilib.checkAll' + getEventName(options.eventName);
		
		_controller.bind('click' + _eName, function(event){
			event.stopPropagation();
			
			if (_controller.is(':checked')) {
				if (_slave.list) {
					_slave.$list = $(_slave.list);
				}
				_slave.$list.attr('checked', 'checked');
				_slave.checkCount =  _slave.$list.length;
				
				if (typeof(options.whenCheckAll) == 'function') {
					options.whenCheckAll($(this));
				}
			}
			else {
				if (_slave.list) {
					_slave.$list = $(_slave.list);
				}
				_slave.$list.removeAttr('checked');
				_slave.checkCount = 0;
				
				if (typeof(options.whenUncheckAll) == 'function') {
					options.whenUncheckAll($(this));
				}
			}
		});
		
		
		if (_slave.list) { //dynamic list prepare
			$(_slave.list)
			.live('click' + _eName, function(event){
				event.stopPropagation();
				_slave.$list = $(_slave.list);
				_check($(this));
			})
			.each(function(){ // get initial check count
				if ($(this).is(':checked')) {
					_slave.checkCount++;
				}
			});
		}
		else {
			_slave.$list.each(function() { // get initial check count
				if ($(this).is(':checked')) {
					_slave.checkCount++;
				}
			
				$(this).bind('click' + _eName, function(event){
					event.stopPropagation();
					_check($(this));
				});
			});
		}
		
		methods = {
				'getCheckedCount' : function() { return _slave.checkCount; }
		};
		
		function _check($this) {
			
			if ($this.is(':checked')) {
				_slave.checkCount++;
				if (_slave.checkCount == _slave.$list.length) {
					// all items are checked
					_controller.attr('checked', 'checked');
				}
				
				if (typeof(options.whenCheckOne) == 'function') {
					options.whenCheckOne($this);
				}
			}
			else {
				_slave.checkCount--;
				_controller.removeAttr('checked');
				
				if (typeof(options.whenUncheckOne) == 'function') {
					options.whenUncheckOne($this);
				}
			}
			
		}
		
		return this;
	};
}(jQuery));

(function(){
	$.fn.viewFocus = function (options) {
	/* focus the caller to a proper position of the screen
	 * PARAMETER
	 * 	position: 	px from window top
	 * 				the position an item should be ideally displayed
	 * 				default value won't change when window resizes
	 * 
	 * 	range:		px
	 * 				the range considered to be 'proper' around the ideal position
	 * 			
	 * 	speed:		ms
	 * 				how many time it takes to animate focus
	 */
		if (typeof(this.offset)=='undefined') {
			return false;
		}
		
		var _pos = $(window).height()/6;
		var _range = $(window).height()/6;
		var _speed = 700;
		if (options) {
			_pos = option.position || _pos;
			_range = options.range || _range;
			_speed = options.speed || _speed;
		}
		
		var top = this.offset().top - _pos;
		var diff = $(window).scrollTop() - top;
		if (diff>_range || diff<-_range) {
			$('html, body').animate({ scrollTop: top }, _speed);
		}
		return this;
	};
}(jQuery));


(function(){
	$.fn.pulldownMenu = function(options){
	/* attaches pulldownMenu to the caller, and bind actions
	 * PARAMETERS
	 * 	menu (required) :	jQuery object
	 * 						the menu
	 * 	fixed:				'flow', 'fixed'
	 */	
		var _ctrl = this;
		
		var _menu;
		var _action = 'click';
		var _block = false;
		var _anchor = false;
		var _direction = false;	// default: pull DOWN. optional: 'up'
		var _position = 'absolute';
		var _blockHideList = {};
		
		if (!options || typeof(options.menu)=='undefined' || typeof(options.menu.next)=='undefined') {
			//no valid menu object
			return this;
		}
		else {
			_menu = options.menu;
			_close = options.close || null;
			_block = options.block || _block;
			_anchor = options.anchor || _anchor;
			_direction = options.direction || _direction;
			_position = options.position || _position;
			_blockHideList = options.blockHideList || _blockHideList;
			_eName = getEventName(options.eventName);
			
			_func = {
					beforeShow: options.beforeShow || null,
					afterShow: options.afterShow || null,
					beforeHide: options.beforeHide || null,
					afterHide: options.afterHide || null
			};
			
			_ctrl
			.bind(_action + '.uiLib.pulldownMenu' + _eName, function(e){
				e.stopPropagation();
				
				if (_block) {
					e.preventDefault();
				}
				
				if (_menu.is(':visible') && typeof(_func.beforeHide)=='function') {
					_func.beforeHide.call(this);
				}
				else if (!_menu.is(':visible') && typeof(_func.beforeShow)=='function') {
					_func.beforeShow.call(this);
				}
				
				_menu.toggle();
				// write visibility control separately to avoid jam with status 
				if (_menu.is(':visible')) {
					showMenu(_menu, _ctrl);
					if (typeof(_func.afterShow)=='function') {
						_func.afterShow.call(this);
					}
				}
				else {
					hideMenu(_menu);
					if (typeof(_func.afterHide)=='function') {
						_func.afterHide.call(this);
					}
				}
			})
			
			if (_close && typeof(_close.next)=='function') {
				_close.bind('click' + '.uiLib.pulldownMenu' + _eName, function(){
					if (typeof(_func.beforeHide)=='function') {
						_func.beforeHide.call(this);
					}
					_menu.hide();
					hideMenu(_menu);
					if (typeof(_func.afterHide)=='function') {
						_func.afterHide.call(this);
					}
				});
			}
			
			// for outside use
			this.resetPosition = function(){
				setPosition(_ctrl, _menu);
				return this;
			};
			this.showMenu = function(){
				_menu.show();
				showMenu(_menu, _ctrl);
				return this;
			};
			this.hideMenu = function(){
				_menu.hide();
				hideMenu(_menu);
				return this;
			};
			this.refresh = function(options) {
				if (options) {
					_block = options.block || _block;
					_anchor = options.anchor || _anchor;
					_direction = options.direction || _direction;
					_blockHideList = options.blockHideList || _blockHideList;
				}
				return this;
			};
			
		}
		
		function setPosition(CTRL, MENU) {
			var top;
			var left;
			
			switch (_direction) {
			case 'up':
				top = CTRL.position().top - MENU.outerHeight();
				left = CTRL.position().left;
				
				if (_position == 'fixed' && _anchor) {
					top = _anchor.offset().top - $(window).scrollTop() - MENU.outerHeight();
					left = _anchor.offset().left;
				}
				else if (_anchor) {
					top = _anchor.position().top - MENU.outerHeight();
					left = _anchor.position().left;
				}
				
				break;
			case 'none':
				break;
			default:
				top = CTRL.position().top + CTRL.outerHeight()/2 + CTRL.height()/2;
				left = CTRL.position().left;
			
				if (_anchor) {
					top = _anchor.position().top + _anchor.outerHeight();
					left = _anchor.position().left;
				}
			}
			
			top = (top+MENU.outerHeight() > $(document).height()) ? $(document).height()-MENU.outerHeight()-20 : top;
			left = (left+MENU.outerWidth() > $(document).width()) ? $(document).width()-MENU.outerWidth()-20 : left;
			
			MENU.css({ 'top': top, 'left': left });
		}
		
		function showMenu(MENU, CTRL) {
			setPosition(CTRL, MENU);
			//MENU.show();
			
			var current = -1;
			var list = MENU.find('li');
			
			$(window).bind('keydown.uiLib.pulldownMenu.pdmkeyboard' + _eName, function(event){
				if (event.keyCode == 40 || event.keyCode == 38 || event.keyCode == 27) {
					// 40: down, 38: up, 27: esc, 32: space
					event.preventDefault();
					var item;
					
					switch (event.keyCode) {
					case 40:
						current = (current==list.length-1) ? 0 : current+1;
						MENU.find('.focus').removeClass('focus');
						item = list.get(current);
						$(item).addClass('focus').find('a').focus();
						break;
					case 38:
						current = (current==0) ? list.length-1 : current-1;
						MENU.find('.focus').removeClass('focus');
						item = list.get(current);
						$(item).addClass('focus').find('a').focus();
						break;
					case 27:
						MENU.hide();
						hideMenu(MENU);
					}
				}
			})
			.bind('click.uiLib.pulldownMenu.pdmmouse' + _eName, function(event){
				event.stopPropagation();
				
				var target = $(event.target).html();
				var toHide = MENU.html()!=target && CTRL.html()!=target
						&& MENU.has(event.target).length==0 && CTRL.has(event.target).length==0;
				
				if (_blockHideList.length>0) {
					toHide = toHide
						&& _blockHideList.html()!=target
						&& _blockHideList.has(event.target).length==0;
				}
				
				if (toHide) {
					MENU.hide();
					hideMenu(MENU);
				};
			})
		}
		
		function hideMenu(MENU) {
			MENU.find('.focus').removeClass('focus');
			$(window).unbind('.pulldownMenu.pdmkeyboard, .pulldownMenu.pdmmouse');
		}
		return this;
	};
}(jQuery));


(function(){
/*	Switch Between Views, can be related to window.location.hash */
/*	
 * CALL:
 * 	(recommend) by the container of the controller, e.g. ul.switch
 * 
 */
	$.fn.viewSwitch = function (options) {
		if (options && options.pairs) {
			for (var pair in options.pairs) {
			}
		}
		return this;
	};
}(jQuery));


/*!
 * jQuery Cookie Plugin
 * https://github.com/carhartl/jquery-cookie
 *
 * Copyright 2011, Klaus Hartl
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * http://www.opensource.org/licenses/mit-license.php
 * http://www.opensource.org/licenses/GPL-2.0
 */
(function($) {
    $.cookie = function(key, value, options) {

        // key and at least value given, set cookie...
        if (arguments.length > 1 && (!/Object/.test(Object.prototype.toString.call(value)) || value === null || value === undefined)) {
            options = $.extend({}, options);

            if (value === null || value === undefined) {
                options.expires = -1;
            }

            if (typeof options.expires === 'number') {
                var days = options.expires, t = options.expires = new Date();
                t.setDate(t.getDate() + days);
            }

            value = String(value);

            return (document.cookie = [
                encodeURIComponent(key), '=', options.raw ? value : encodeURIComponent(value),
                options.expires ? '; expires=' + options.expires.toUTCString() : '', // use expires attribute, max-age is not supported by IE
                options.path    ? '; path=' + options.path : '',
                options.domain  ? '; domain=' + options.domain : '',
                options.secure  ? '; secure' : ''
            ].join(''));
        }

        // key and possibly options given, get cookie...
        options = value || {};
        var decode = options.raw ? function(s) { return s; } : decodeURIComponent;

        var pairs = document.cookie.split('; ');
        for (var i = 0, pair; pair = pairs[i] && pairs[i].split('='); i++) {
            if (decode(pair[0]) === key) return decode(pair[1] || ''); // IE saves cookies with empty string as "c; ", e.g. without "=" as opposed to EOMB, thus pair[1] may be undefined
        }
        return null;
    };
})(jQuery);