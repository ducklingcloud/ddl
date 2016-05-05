/* INTERACTIVE ELEMENTS : AS OBJECT-ORIENTED PLUGIN */
/*	model: use var item = new ITEMPROTOTYPE(parameters) to define an item
 *	This would fill in DOM elements (if necessary), bind with actions, classes,
 *	or other js actions.
 *	Some of the functions must be implemented, or OVERWIRTTEN, each time to use
 *	the prototype, according to the context.
 *	There will be "register" function to set important parameters when the
 *	prototype affects other DOM elements.
 
 *	Current List of PROTOTYPES *
 	*	SEARCH BOX: fill an empty div with input area
 	*	PULLDOWN MENU: float menu that can respond to direction keys
 	*	ROOKIE GUIDE: lower half screen rookie guide
 */ 

/* SEARCH MATCHED ITEMS WITHIN PAGE */
/* Defines actions of search boxes
 * 	Fully defined: when focus, type, clear, and leave Input area
 * 	
 * 	Defined as standard procedure:
 * 		function findMatches(QUERY, OBJ, CHILD){}
 * 			QUERY: string, will find if string is contained
 * 			OBJ: as container, defines match and as present unit
 * 			CHILD: string used in jQuery selector as child of OBJ, to search from
 * 				if leave undefined, search will be for the html() of the OBJ
 * 			Works together with isMatch(OBJ) and notMatch(OBJ)
 * 	
 * 	NEED Implementation while use
 * 		function doSearch(QUERY){}
 * 			QUERY: string for search
 * 			sent search request and other actions
 * 			can use findMatches() with isMatch() and notMatch()
 * 		function resetSearch(){}
 * 			to recover from search action, e.g. present all that are hidden in search
 * 			trigger by ESC or clear input string by the cross "x"
 * 		function isMatch(OBJ){}
 * 			actions to take when an OBJ is marked as matched
 * 			defaultly called in findMatches()
 * 		function notMatch(OBJ){}
 * 			actions to take when an OBJ is marked as no match
 * 			defaultly called in findMatches()
 */
function SearchBox(ID, STANDBY_TEXT, WITH_BUTTON, SEARCH_WHILE_TYPING, SEARCH_NULL, AUTO_FOCUS) {
	//append items
	this.container = $('#' + ID);
	this.container.addClass('searchBox');
	this.container.append('<input type="text" name="search_input" value="' + STANDBY_TEXT + '"/>');
	this.container.append('<a class="search_reset"></a>');
	if (typeof(WITH_BUTTON)=='string' && WITH_BUTTON!=='') {
		this.container.append('<a class="search_start">' + WITH_BUTTON + '</a>');
	}
	this.container.append('<div class="search_result"></div>');
	
	//objects & parameters
	this.searchInput = $('#' + ID + ' input[name="search_input"]');
	this.searchResetTrigger = $('#' + ID + ' a.search_reset');
	this.searchStartTrigger = $('#' + ID + ' a.search_start');
	this.searchResult = $('#' + ID + ' div.search_result');
	this.unit = '';
	this.child = '';
	
	this.standbyText = STANDBY_TEXT;
	// Search while typing :::: TRUE: search while typing; FALSE: search after ENTER
	this.searchWhileTyping = (typeof(SEARCH_WHILE_TYPING)=='undefined') ? true : SEARCH_WHILE_TYPING;
	// Still do search when input is EMPTY, or actually RESET search
	//		:::: TRUE: Still search; FLASE: do NOT search 
	this.searchNull = (typeof(SEARCH_NULL)=='undefined') ? true : SEARCH_NULL;
	this.autoFocus = (typeof(AUTO_FOCUS)=='undefined') ? false : AUTO_FOCUS;

	//default value
	this.searchLock = false;
	this.searchInput.addClass('standby');
	this.searchResetTrigger.attr('disable', 'true');
	this.msg_na = 'No Results Available.'
	this.msg_err = 'Error with Searching.';
//	if (this.autoFocus) { this.searchInput.attr('tabIndex', '1'); }
	
	this.searchResultState = false;
	this.searchResultFocus = -1;
	this.searchResultCount = 0;
	
	//event handlers
	var entity = this;
	this.searchInput.focus(function(){
		entity.focus();
	});
	this.searchInput.blur(function(){
		entity.blur();
	});
	this.searchInput.keyup(function(KEY){
		entity.keyup(KEY);
	});
	this.searchResetTrigger.click(function(){
		entity.clearInput();
		entity.resetSearch();
		entity.blur();
	});
	this.searchStartTrigger.click(function(){
		var query = entity.searchInput.val();
		if (entity.searchNull || query!=='') {
			entity.doSearch(query);
		}
		entity.searchInput.select();
	});
	
	//functions to be overwritten for each search
	this.doSearch = function(QUERY){
		this.unit.each(function(){
			entity.findMatches(QUERY, $(this), entity.child);
		});
		this.afterSearch();
	};
	this.resetSearch = function(){ this.unit.show(); this.afterResetSearch(); };
	this.isMatch = function(OBJ){ OBJ.show(); };
	this.notMatch = function(OBJ){ OBJ.hide(); };
	this.afterSearch = function(){};
	this.afterResetSearch = function(){};
	this.appendResult = function(JSON){};
}

SearchBox.prototype.register = function(UNIT, CHILD) {
	/* UNIT: the container of the area to be searched
	/* CHILD: direct element to be search under the container UNIT */
	this.unit = $(UNIT);
	this.child = CHILD;
}

SearchBox.prototype.findMatches = function(QUERY, OBJ, CHILD) {
	var query = QUERY.toLowerCase();
	var findMatch = false;
	
	if (typeof(CHILD)=='undefined' || CHILD.trim()==='') {
		if (OBJ.html().toLowerCase().indexOf(query)>=0) {
			OBJ.addClass('search_match');
			findMatch = true;
		}
	}
	else {
		OBJ.find(CHILD).each(function(){
			if ($(this).text().toLowerCase().indexOf(query)>=0) {
				$(this).addClass('search_match');
				findMatch = true;
			}
		});
	}
	
	if (findMatch) { this.isMatch(OBJ); }
	else { this.notMatch(OBJ); }
};
SearchBox.prototype.clearMarks = function() {
	$('.search_match').removeClass('search_match');
}

SearchBox.prototype.clearInput = function() {
	this.searchResetTrigger.attr('disable', 'true');
	this.searchInput.val('');
};
SearchBox.prototype.leaveInput = function() {
	this.searchInput.addClass('standby');
	if (this.searchInput.val()==='') {
		this.searchInput.val(this.standbyText);
		this.searchResetTrigger.attr('disable', 'true');
	}
	else {}
};
SearchBox.prototype.prepareInput = function(){
	this.searchInput.removeClass('standby');
	if (this.searchInput.val()==this.standbyText) {
		this.searchInput.val('');
	}
	else {
		this.searchInput.select();
	}
};
SearchBox.prototype.enableClear = function() {
	if (this.searchResetTrigger.attr('disable')=='true') {
		this.searchResetTrigger.attr('disable', 'false');
	}
};

SearchBox.prototype.focus = function(){
	this.searchLock = true;
	this.prepareInput();
};
SearchBox.prototype.blur = function(){
	this.searchLock = false;
	this.leaveInput();
};

SearchBox.prototype.keyup = function(KEY) {
	var query = this.searchInput.val();
	
	if (query!=='') {
		this.enableClear();
	}
	
	if (KEY.which==27) { //ESC
		this.clearInput();
		this.clearMarks();
		this.resetSearch();
		this.searchInput.blur();
	}
	else if ((KEY.which==13 && query!=this.standbyText && this.searchResultFocus<0)
		|| (KEY.which==40 && this.searchResultState==false)) {
		//Enter && not focusing on searchResult list
		if (query==='' && this.searchNull) {
			this.clearMarks();
			this.resetSearch();
		}
		else if (query!=='') {
			this.clearMarks();
			this.doSearch(query);
			this.searchInput.select();
		}
	}
	else if (this.searchResultState==true && (KEY.which==38 || KEY.which==40)) {
		switch(KEY.which) {
			case 38: this.focusUp(); break;
			case 40: this.focusDown(); break;
		}
	}
	else if (KEY.which==13 && this.searchResultFocus>=0) {
		//Enter with focusing on the search result list
		window.location.href = this.searchResult.find('li a.chosen').attr('href');	
	}
	else { //search and match
		if (this.searchWhileTyping) {
			if (query==='' && this.searchNull) {
				this.clearMarks();
				this.resetSearch();
			}
			else if (query!=='') {
				this.clearMarks();
				this.doSearch(query);
			}
		}
	}
};

/* SETTINGS & METHODS for PULLDOWN RESULT LIST */
SearchBox.prototype.setPullDown = function (MSG_NA, MSG_ERR, WIDTH) {
	this.msg_na = (typeof(MSG_NA)=='undefined') ? this.msg_na : MSG_NA;
	this.msg_err = (typeof(MSG_ERR)=='undefined') ? this.msg_err : MSG_ERR;
	this.searchResult.css('width', WIDTH);
	
	this.cover = $('#fullScreenCover');
	if (this.cover.length<1) {
		this.container.append('<div id="fullScreenCover"></div>');
		this.cover = this.container.find('#fullScreenCover');
	}
	
	var entity = this;
	this.cover.live('click', function(){
		entity.hideCover();
		entity.clearPullDown();
	});
	
	this.searchResult.live('mouseenter', function(){
		$(this).find('li a.chosen').removeClass('chosen');
	});
	
}

SearchBox.prototype.showCover = function(){
	this.cover.css('width', $(document).width()).css('height', $(document).height()).show();
}
SearchBox.prototype.hideCover = function() {
	this.cover.hide();
}
SearchBox.prototype.MSG_noResult = function() {
	this.clearPullDown();
	this.searchResult.append('<p class="NA">' + this.msg_na + '</p>').show();
	this.searchResultState = false;
};
SearchBox.prototype.MSG_error = function() {
	this.clearPullDown();
	this.searchResult.append('<p class="NA">' + this.msg_err + '</p>').show();
	this.searchResultState = false;
};
SearchBox.prototype.MSG_searching = function() {
	this.clearPullDown();
	this.searchResult.append('<label class="checking"></label>').show();
	this.searchResultState = true;
}

SearchBox.prototype.clearPullDown = function(){
	this.searchResult.html('').hide();
	this.searchResultState = false;
}

SearchBox.prototype.presentResults = function(JSON) {
	this.clearPullDown();
	this.showCover();
	if (JSON.count<1) {
		this.MSG_noResult();
	}
	else {
		this.appendResults(JSON);
		this.searchResultState = true;
		this.searchResultCount = this.searchResult.find('li a').size();
	}
};

SearchBox.prototype.focusUp = function(){
	if (this.searchResultFocus>0) {
		this.searchResult.find('li a.chosen').removeClass('chosen');
		this.searchResultFocus--;
		this.searchResult.find('li a:eq('+this.searchResultFocus+')').addClass('chosen');
	}
};
SearchBox.prototype.focusDown = function(){
	if (this.searchResultFocus<this.searchResultCount-1) {
		this.searchResult.find('li a.chosen').removeClass('chosen');
		this.searchResultFocus++;
		this.searchResult.find('li a:eq('+this.searchResultFocus+')').addClass('chosen');
	}
};

/* END OF SEARCH MATCH WITHIN PAGE */


/* PULLDOWN MENU */
function PullDownMenu(ID) {
	this.container = $('#' + ID);
	this.container.addClass('pulldownMenu');
	
	this.menu = $('#' + ID + ' > ul');
	this.lis = $('#' + ID + ' > ul > li');
	
	$('#fixedTop').append('<div id="cover'+ ID + '" class="fullScreenCover"></div>');
	this.cover = $('body #cover' + ID);
	
	var entity = this;
	this.container.live('keydown', function(KEY){
		switch (KEY.which) {
			case 39: //right
			case 40: //down
			//	entity.focus('down');
				break;
			case 37: //left
			case 38: //up
			//	entity.focus('up');
				break;
			case 27://ESC
				entity.hide();
				break;
		}
	});
	
	this.cover.live('click', function(){ entity.reset(); });
}

PullDownMenu.prototype.register = function(TRIGGER) {
	var entity = this;
	$(TRIGGER).click(function(){
		if (entity.container.attr('display', 'none')) {
			entity.show();
		}
		else {
			entity.hide();
		}
	});
};

PullDownMenu.prototype.setPosition = function(REF, POS) {
	switch (POS) {
		case 'left':
			var top = $(REF).offset().top;
			var rightOri = $(REF).offset().left;
			if (rightOri<this.container.width()) {
				left = 0;
			}
			else {
				left = rightOri-this.container.width();
			}
			this.container.css('top', top).css('left', left);
			break;
		case 'below':
		default:
			var top = $(REF).offset().top+$(REF).outerHeight();
			var leftOri = $(REF).offset().left;
			if (leftOri+this.container.width()>$(window).width()) {
				left = $(window).width()-this.container.width();
			}
			else {
				left = leftOri;
			} 
			this.container.css('top', top).css('left', left);
	}
}


PullDownMenu.prototype.show = function(){
	this.container.show();
	this.cover.css('width', $(document).width()).css('height', $(document).height()).show();
//	this.focus();
};
PullDownMenu.prototype.hide = function(){
	this.container.hide();
	this.cover.hide();
};
PullDownMenu.prototype.setDisable = function(SELECTOR) {
	this.menu.find(SELECTOR).addClass('disabled');
}

PullDownMenu.prototype.reset = function() {
	this.hide();
	this.menu.children().removeClass('focus').removeClass('disabled');
}

PullDownMenu.prototype.focus = function(INDEX){
	if (typeof(INDEX)=='string') {
		var obj = this.menu.children('.focus');
		if (obj.length<1) {
			this.focus();
		}
		else {
			var curIndex = this.menu.children().index(obj);
			switch (INDEX) {
				case 'up':
					if (curIndex > 0) {
						obj.prev().addClass('focus');
						obj.removeClass('focus');
						obj.prev().children('a').focus();
					}
					else {
						obj.children('a').focus();
					}
					break;
				case 'down':
					if (curIndex < this.menu.children().length-1) {
						obj.next().addClass('focus');
						obj.removeClass('focus');
						obj.next().children('a').focus();
					}
					else {
						obj.children('a').focus();
					}
					break;
			}
		}
	}
	else {
		var obj = $(this.lis.get(0));
		obj.addClass('focus');
		obj.children('a').focus();
	}
};
/* END of PULL DOWN MENU */


/* ROOKIE GUIDE */
function RookieGuide(ID) {
	this.container = $('#' + ID);
}

/* END of ROOKIE GUIDE */


/* BACK TO TOP */
function BackToTop(TEXT) {
	//append items
	this.fulH = $(document).height();
	this.winH = $(window).height();
	$('#content').append('<div id="backToTop" class="ui-bottomTouch" title="'+TEXT+'"></div>');
	this.trigger = $('#backToTop');
	this.trigger.css('left', $('#content').offset().left + parseInt(this.trigger.css('left')));
	var entity = this;
	this.trigger.click(function(){
		entity.toTop();
	});
	$(window).scroll(function(){
		entity.appearance($(this).scrollTop());
	});
}
	BackToTop.prototype.appearance = function(SCRTOP) {
		if (SCRTOP > 0.2*this.winH) {
			this.trigger.fadeIn();
		}
		else {
			this.trigger.fadeOut();
		}
	};
	
	BackToTop.prototype.toTop = function() {
		$('html, body').animate({ scrollTop:0 }, 700);
	};
/* END of BACK TO TOP */