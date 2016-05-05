/*
 * Mentions Input
 * Version 1.0.2
 * Written by: Kenneth Auchenberg (Podio)
 *
 * Using underscore.js
 *
 * License: MIT License - http://www.opensource.org/licenses/mit-license.php
 */

(function ($, _, undefined) {
  // Settings
  var KEY = { BACKSPACE : 8, TAB : 9, RETURN : 13, ESC : 27, LEFT : 37, UP : 38, RIGHT : 39, DOWN : 40, COMMA : 188, SPACE : 32, HOME : 36, END : 35 }; // Keys "enum"
  var defaultSettings = {
    triggerChar   : '@',
    onDataRequest : $.noop,
    minChars      : 2,
    showAvatars   : true,
    elastic       : true,
    classes       : {
      autoCompleteItemActive : "active"
    },
    templates     : {
      wrapper                    : _.template('<div class="mentions-input-box"></div>'),
      autocompleteList           : _.template('<div class="mentions-autocomplete-list"></div>'),
      autocompleteListItem       : _.template('<li data-ref-id="<%= id %>" data-ref-type="<%= type %>" data-display="<%= display %>"><%= content %></li>'),
      autocompleteListItemAvatar : _.template('<img src="<%= avatar %>" />'),
      autocompleteListItemIcon   : _.template('<div class="icon <%= icon %>"></div>'),
      mentionsOverlay            : _.template('<div class="mentions"><div></div></div>'),
      mentionItemSyntax          : _.template('@[<%= value %>](<%= type %>:<%= id %>)'),
      mentionItemHighlight       : _.template("<strong id='<%= id %>'><span><%= value %></span></strong>")
    }
  };

  var utils = {
    htmlEncode       : function (str) {
      return _.escape(str);
    },
    highlightTerm    : function (value, term) {
      if (!term && !term.length) {
        return value;
      }
      return value.replace(new RegExp("(?![^&;]+;)(?!<[^<>]*)(" + term + ")(?![^<>]*>)(?![^&;]+;)", "gi"), "<b>$1</b>");
    },
    setCaratPosition : function (domNode, caretPos) {
      if (domNode.createTextRange) {
        var range = domNode.createTextRange();
        range.move('character', caretPos);
        range.select();
      } else {
        if (domNode.selectionStart) {
          domNode.focus();
          domNode.setSelectionRange(caretPos, caretPos);
        } else {
          domNode.focus();
        }
      }
    },
    rtrim: function(string) {
      return string.replace(/\s+$/,"");
    },
    escapePinyin:function(string){
	    
	    var newString = string;
		if(string.lastIndexOf(")")>0){
		 newString = string.substring(0,string.lastIndexOf(")")+1);
		}
	    return newString;
	}
  };

  var MentionsInput = function (settings) {

    var domInput, elmInputBox, elmInputWrapper, elmAutocompleteList, elmWrapperBox, elmMentionsOverlay, elmActiveAutoCompleteItem;
    var mentionsCollection = [];
    var autocompleteItemCollection = {};
    var inputBuffer = [];
    var currentDataQuery;

    settings = $.extend(true, {}, defaultSettings, settings );

    function initTextarea() {
      elmInputBox = $(domInput);

      if (elmInputBox.attr('data-mentions-input') == 'true') {
        return;
      }

      elmInputWrapper = elmInputBox.parent();
      elmWrapperBox = $(settings.templates.wrapper());
      elmInputBox.wrapAll(elmWrapperBox);
      elmWrapperBox = elmInputWrapper.find('> div');

      elmInputBox.attr('data-mentions-input', 'true');
      elmInputBox.bind('keydown', onInputBoxKeyDown);
      elmInputBox.bind('keyup', onInputBoxKeyPress);
      elmInputBox.bind('keyup', onInputBoxInput);
      elmInputBox.bind('click', onInputBoxClick);
      elmInputBox.bind('blur', onInputBoxBlur);

      // Elastic textareas, internal setting for the Dispora guys
      if( settings.elastic ) {
        elmInputBox.elastic();
      }

    }

    function initAutocomplete() {
      elmAutocompleteList = $(settings.templates.autocompleteList());
      elmAutocompleteList.appendTo(elmWrapperBox);
      elmAutocompleteList.delegate('li', 'mousedown', onAutoCompleteItemClick);
    }

    function initMentionsOverlay() {
      elmMentionsOverlay = $(settings.templates.mentionsOverlay());
      elmMentionsOverlay.prependTo(elmWrapperBox);
    }

    function updateValues() {
      var syntaxMessage = getInputBoxValue();

      _.each(mentionsCollection, function (mention) {
        var textSyntax = settings.templates.mentionItemSyntax(mention);
        syntaxMessage = replaceMentionText(syntaxMessage,mention.value, textSyntax);
      });

      var mentionText = utils.htmlEncode(syntaxMessage);

      _.each(mentionsCollection, function (mention) {
        var formattedMention = _.extend({}, mention, {value: utils.htmlEncode(mention.value),id:mention.id});
        var textSyntax = settings.templates.mentionItemSyntax(formattedMention);
        var textHighlight = settings.templates.mentionItemHighlight(formattedMention);

        mentionText = replaceMentionText(mentionText,textSyntax, textHighlight);
      });

      mentionText = mentionText.replace(/\n/g, '<br />');
      mentionText = mentionText.replace(/ {2}/g, '&nbsp; ');

      elmInputBox.data('messageText', syntaxMessage);
      elmMentionsOverlay.find('div').html(mentionText);
    }
    
    function replaceMentionText(mentionText,textSyntax,textHighlight){
    	var contail = false;
    	var index = mentionText.lastIndexOf(textHighlight);
    	var s = '';
    	if(textHighlight.indexOf(textSyntax)>-1){
    		s = textHighlight.substring(textHighlight.indexOf(textSyntax)-1,textHighlight.indexOf(textSyntax)+textSyntax.length+1);
    		contail = true;
    		if(index==-1&&mentionText.lastIndexOf(s)>-1){
    			index = 0;
    		}
    	}
    	if(index<=-1){
    		return mentionText.replace(textSyntax, textHighlight);
    	}else{
    		index = textHighlight.length+index;
    		if(contail){
    			var i = mentionText.lastIndexOf(s);
    			if(i>index){
    				index = i+s.length;
    			}
    		}
    		var begin = mentionText.substring(0,index);
    		var end = mentionText.substring(index);
    		return begin+end.replace(textSyntax, textHighlight);
    	}
    }
    
    function resetBuffer() {
      inputBuffer = [];
    }

    function updateMentionsCollection() {
      var inputText = getInputBoxValue();

      mentionsCollection = _.reject(mentionsCollection, function (mention, index) {
        return !mention.value || inputText.indexOf(mention.value) == -1;
      });
      mentionsCollection = _.compact(mentionsCollection);
    }

    function addMention(mention) {

      var currentMessage = getInputBoxValue();

      // Using a regex to figure out positions
      var regex = new RegExp("\\" + settings.triggerChar + currentDataQuery, "gi");
      regex.exec(currentMessage);
      var lastIndex = inputIndex;
      var startCaretPosition = lastIndex - currentDataQuery.length - 1;
      var currentCaretPosition = lastIndex;

      var start = currentMessage.substr(0, startCaretPosition);
      var end = currentMessage.substr(currentCaretPosition, currentMessage.length);
      var startEndIndex = (start +mention.value).length + 1;

      mentionsCollection.push(mention);

      // Cleaning before inserting the value, otherwise auto-complete would be triggered with "old" inputbuffer
      resetBuffer();
      currentDataQuery = '';
      hideAutoComplete();

      // Mentions & syntax message
      var updatedMessageText = start +mention.value + ' ' + end;
      elmInputBox.val(updatedMessageText);
      updateValues();

      // Set correct focus and selection
      elmInputBox.focus();
      utils.setCaratPosition(elmInputBox[0], startEndIndex);
    }
    
    function getInputBoxValue() {
      return $.trim(elmInputBox.val());
    }

    function onAutoCompleteItemClick(e) {
      var elmTarget = $(this);
      var mention = autocompleteItemCollection[elmTarget.attr('data-uid')];

      addMention(mention);
      oldValue = getInputBoxValue();
      return false;
    }

    function onInputBoxClick(e) {
      resetBuffer();
    }

    function onInputBoxBlur(e) {
      hideAutoComplete();
    }

    function onInputBoxInput(e) {
    	if(e.keyCode==KEY.DOWN||e.keyCode==KEY.UP){
    		return;
    	}
      updateValues();
      updateMentionsCollection();
      var triggerCharIndex = _.lastIndexOf(inputBuffer, settings.triggerChar);
      if (triggerCharIndex > -1) {
        currentDataQuery = inputBuffer.slice(triggerCharIndex + 1).join('');
        currentDataQuery = utils.rtrim(currentDataQuery);

        _.defer(_.bind(doSearch, this, currentDataQuery));
      }
    }

    function onInputBoxKeyPress(e) {
      if(e.keyCode !== KEY.BACKSPACE&&(e.keyCode!=KEY.RETURN||inputBuffer.length>0)) {
        var change = getInputChange();
        if(change.length>0){
        	for(var i =0;i<change.length;i++){
        		inputBuffer.push(change.charAt(i));
        	}
        }
        if(e.keyCode==settings.triggerChar){
        	lastIndex = getInputBoxValue().length-inputIndex;
        }
        //chrome 输入法问题
        if(KEY.SPACE==e.keyCode&&inputBuffer.length>0&&inputBuffer[inputBuffer.length-1]!=KEY.SPACE){
        	var c = getInputBoxValue().substring(0,getInputBoxValue().length-lastIndex).lastIndexOf(settings.triggerChar);
        	var bus = getInputBoxValue().substring(c,getInputBoxValue().length-lastIndex);
        	inputBuffer =[];
        	for(var i =0;i<bus.length;i++){
        		inputBuffer.push(bus.charAt(i));
        	}
        	inputIndex = getInputBoxValue().length-lastIndex;
        }
      }
      oldValue = getInputBoxValue();
    }
    var inputIndex=0;
    var lastIndex=0;
    var oldValue='';
    function getInputChange(){
    	var nowValue = getInputBoxValue();
    	if(!nowValue){
    		return '';
    	}
    	if(nowValue.length<=oldValue.length){
    		return '';
    	}
    	var length = nowValue.length-oldValue.length;
    	var tmp=-1;
    	for(var i=0;i<oldValue.length;i++){
    		if(nowValue.charAt(i)!=oldValue.charAt(i)&&tmp==-1){
    			tmp=i;
    			if(i>0&&nowValue.charAt(i-1)==settings.triggerChar){
    				tmp = tmp-1;
    			}
    		}
    	}
    	if(tmp==-1){
    		tmp = oldValue.length;
    	}
    	inputIndex = tmp+length;
    	return nowValue.substring(tmp,tmp+length);
    }
    function onInputBoxKeyDown(e) {

      // This also matches HOME/END on OSX which is CMD+LEFT, CMD+RIGHT
      if (e.keyCode == KEY.LEFT || e.keyCode == KEY.RIGHT || e.keyCode == KEY.HOME || e.keyCode == KEY.END) {
        // Defer execution to ensure carat pos has changed after HOME/END keys
        _.defer(resetBuffer);

        // IE9 doesn't fire the oninput event when backspace or delete is pressed. This causes the highlighting
        // to stay on the screen whenever backspace is pressed after a highlighed word. This is simply a hack
        // to force updateValues() to fire when backspace/delete is pressed in IE9.
        if (navigator.userAgent.indexOf("MSIE 9") > -1) {
          _.defer(updateValues);
        }

        return;
      }

      if (e.keyCode == KEY.BACKSPACE) {
        inputBuffer = inputBuffer.slice(0, -1 + inputBuffer.length); // Can't use splice, not available in IE
        if(inputIndex>0){
        	inputIndex=inputIndex-1;
        }
        if(inputBuffer.length==0){
        	hideAutoComplete();
        }
        return;
      }

      if (!elmAutocompleteList.is(':visible')) {
        return true;
      }

      switch (e.keyCode) {
        case KEY.UP:
        case KEY.DOWN:
          var elmCurrentAutoCompleteItem = null;
          if (e.keyCode == KEY.DOWN) {
            if (elmActiveAutoCompleteItem && elmActiveAutoCompleteItem.length) {
              elmCurrentAutoCompleteItem = elmActiveAutoCompleteItem.next();
            } else {
              elmCurrentAutoCompleteItem = elmAutocompleteList.find('li').first();
            }
          } else {
            elmCurrentAutoCompleteItem = $(elmActiveAutoCompleteItem).prev();
          }

          if (elmCurrentAutoCompleteItem.length) {
            selectAutoCompleteItem(elmCurrentAutoCompleteItem);
          }
          if (elmAutocompleteList.find('li').length>3) {
        	  var parentHeight = $(elmAutocompleteList).children().first().offset().top;
        	  var height = $(elmActiveAutoCompleteItem).offset().top;
        	  if((height-parentHeight)>26){
        		  $(elmAutocompleteList).scrollTop(height-parentHeight-26);
        	  }
            }
          return false;

        case KEY.RETURN:
        case KEY.TAB:
          if (elmActiveAutoCompleteItem && elmActiveAutoCompleteItem.length) {
            elmActiveAutoCompleteItem.trigger('mousedown');
            return false;
          }

          break;
      }

      return true;
    }

    function hideAutoComplete() {
      elmActiveAutoCompleteItem = null;
      elmAutocompleteList.empty().hide();
    }

    function selectAutoCompleteItem(elmItem) {
      elmItem.addClass(settings.classes.autoCompleteItemActive);
      elmItem.siblings().removeClass(settings.classes.autoCompleteItemActive);
      elmActiveAutoCompleteItem = elmItem;
    }

    function populateDropdown(query, results) {
      elmAutocompleteList.show();

      // Filter items that has already been mentioned
//      var mentionValues = _.pluck(mentionsCollection, 'value');
//      results = _.reject(results, function (item) {
//        return _.include(mentionValues, item.name);
//      });

      if (!results.length) {
        hideAutoComplete();
        return;
      }

      elmAutocompleteList.empty();
      var elmDropDownList = $("<ul>").appendTo(elmAutocompleteList).hide();

      _.each(results, function (item, index) {
        var itemUid = _.uniqueId('mention_');

        autocompleteItemCollection[itemUid] = _.extend({}, item, {value: item.value});

        var elmListItem = $(settings.templates.autocompleteListItem({
          'id'      : utils.htmlEncode(item.id),
          'display' : utils.htmlEncode(item.name),
          'type'    : utils.htmlEncode(item.type),
          'content' : utils.highlightTerm(utils.escapePinyin((item.name)), query)
        })).attr('data-uid', itemUid);

        if (index === 0) {
          selectAutoCompleteItem(elmListItem);
        }

        if (settings.showAvatars) {
          var elmIcon;

          if (item.avatar) {
            elmIcon = $(settings.templates.autocompleteListItemAvatar({ avatar : item.avatar }));
          } else {
            elmIcon = $(settings.templates.autocompleteListItemIcon({ icon : item.icon }));
          }
          elmIcon.prependTo(elmListItem);
        }
        elmListItem = elmListItem.appendTo(elmDropDownList);
      });

      elmAutocompleteList.show();
      elmDropDownList.show();
    }

    function doSearch(query) {
      if ((query||query=='' ) && query.length >= settings.minChars) {
        settings.onDataRequest.call(this, 'search', query, function (responseData) {
          populateDropdown(query, responseData);
        });
      } else {
        hideAutoComplete();
      }
    }

    function resetInput() {
      elmInputBox.val('');
      mentionsCollection = [];
      updateValues();
    }

    // Public methods
    return {
      init : function (domTarget) {

        domInput = domTarget;

        initTextarea();
        initAutocomplete();
        initMentionsOverlay();
        resetInput();

        if( settings.prefillMention ) {
          addMention( settings.prefillMention );
        }

      },

      val : function (callback) {
        if (!_.isFunction(callback)) {
          return;
        }

        var value = mentionsCollection.length ? elmInputBox.data('messageText') : getInputBoxValue();
        callback.call(this, value);
      },

      reset : function () {
        resetInput();
        resetBuffer();
        inputIndex=0;
      },

      getMentions : function (callback) {
        if (!_.isFunction(callback)) {
          return;
        }

        callback.call(this, mentionsCollection);
      },
      getTextValue : function(s){
    	  var mentionText = elmMentionsOverlay.find('div').html();
    	  mentionText = mentionText.replace('<br />','/n' );
          mentionText = mentionText.replace('&nbsp; ', ' ');
          var reg=new RegExp("<strong id=\"?(\\d+?)\"?><span>(.+?)</span></strong>","gmi");
          mentionText = mentionText.replace(reg,"<a target='_blank' href='"+s.url+"/system/user/$1' class='mention'>$2</a>");
          s.result=mentionText;
      }
    };
  };

  $.fn.mentionsInput = function (method, settings) {

    var outerArguments = arguments;

    if (typeof method === 'object' || !method) {
      settings = method;
    }

    return this.each(function () {
      var instance = $.data(this, 'mentionsInput') || $.data(this, 'mentionsInput', new MentionsInput(settings));

      if (_.isFunction(instance[method])) {
        return instance[method].apply(this, Array.prototype.slice.call(outerArguments, 1));

      } else if (typeof method === 'object' || !method) {
        return instance.init.call(this, this);

      } else {
        $.error('Method ' + method + ' does not exist');
      }

    });
  };
})(jQuery, _);