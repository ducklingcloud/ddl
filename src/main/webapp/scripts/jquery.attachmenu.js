(function($){
	$.fn.attachedMenu=function(options, callback){
		 var settings =$.extend({
			trigger:null,
			alignX:"left",
			alignY:"top",
			delay:500
		}, options);
		return new $.AttachedMenu(this, settings, callback);
	};
	
	$.AttachedMenu=function(menu, options, callback){
		var aMenu=this;
		this.settings =options;
		if (callback)
			this.callback=callback;
		else
			this.callback=function(el){};
		this.menudiv=menu;
		this._init=function(trigger){
			if ($.isArray(trigger)){
				$.each(trigger, function(i, t){
					$(t).bind("mouseover", aMenu, function(event){
						event.data.show(this);
					}).bind("mouseout",aMenu, function(event){
						event.data.hide()
					});
				})
			}else{
				$(trigger).bind("mouseover", aMenu, function(event){
						event.data.show(this);
					}).bind("mouseout",aMenu, function(event){
						event.data.hide()
				});
			}
			this.menudiv.bind("mouseover", aMenu, function(event){
				event.data.keepShow();
			}).bind("mouseout", aMenu, function(event){
				event.data.hide();
			});
		};
		
		this.show=function(el){
			this._clearTimer();
			
			this.callback(el);
			
			var pos = this._calPosition(el);
			this.menudiv.css({
				"left": pos.left+"px",
				"top":pos.top+"px",
				"display":"block",
				"position":"absolute"
			});
		};
		this.hide=function(){
			this._clearTimer();
			this.timeout = setInterval(function(){
				aMenu._doHide();
			}, this.settings.delay);
		};
		this.keepShow=function(){
			this._clearTimer();	
			this.menudiv.css('display','block');
		};
		this._clearTimer=function(){
			if (this.timeout!=null){
				clearInterval(this.timeout);
				this.timeout=null;
			}
		};
		this._doHide=function(){
			this.menudiv.css("display", "none");
		};
		this._calPosition=function(el){
			var elPos = $(el).offset();
			var _left, _top;
			
			if ("left"==this.settings.alignX){
				_left = elPos.left;
			}else{
				_left = elPos.left+$(el).width()-this.menudiv.width();
			}
			
			if ("top"==this.settings.alignY){
				_top = elPos.top-this.menudiv.height();
			}else{
				_top = elPos.top+$(el).height();
			}
			return {top:_top,left:_left};
		};
		this._init(this.settings.trigger);
	};
})(jQuery)