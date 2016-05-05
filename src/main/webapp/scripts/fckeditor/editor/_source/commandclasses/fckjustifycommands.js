/*
 * FCKeditor - The text editor for Internet - http://www.fckeditor.net
 * Copyright (C) 2003-2008 Frederico Caldeira Knabben
 *
 * == BEGIN LICENSE ==
 *
 * Licensed under the terms of any of the following licenses at your
 * choice:
 *
 *  - GNU General Public License Version 2 or later (the "GPL")
 *    http://www.gnu.org/licenses/gpl.html
 *
 *  - GNU Lesser General Public License Version 2.1 or later (the "LGPL")
 *    http://www.gnu.org/licenses/lgpl.html
 *
 *  - Mozilla Public License Version 1.1 or later (the "MPL")
 *    http://www.mozilla.org/MPL/MPL-1.1.html
 *
 * == END LICENSE ==
 *
 * FCKJustifyCommand Class: controls block justification.
 */

var FCKJustifyCommand = function(alignValue) {
	this.AlignValue = alignValue;

	// Detect whether this is the instance for the default alignment.
	var contentDir = FCKConfig.ContentLangDirection.toLowerCase();
	this.IsDefaultAlign = (alignValue == 'left' && contentDir == 'ltr')
			|| (alignValue == 'right' && contentDir == 'rtl');

	// Get the class name to be used by this instance.
	var cssClassName = this._CssClassName = (function() {
		var classes = FCKConfig.JustifyClasses;
		if (classes) {
			switch (alignValue) {
			case 'left':
				return classes[0] || null;
			case 'center':
				return classes[1] || null;
			case 'right':
				return classes[2] || null;
			case 'justify':
				return classes[3] || null;
			}
		}
		return null;
	})();

	if (cssClassName && cssClassName.length > 0)
		this._CssClassRegex = new RegExp('(?:^|\\s+)' + cssClassName + '(?=$|\\s)');
};

FCKJustifyCommand._GetClassNameRegex = function() {
	var regex = FCKJustifyCommand._ClassRegex;
	if (regex != undefined)
		return regex;

	var names = [];

	var classes = FCKConfig.JustifyClasses;
	if (classes) {
		for ( var i = 0; i < 4; i++) {
			var className = classes[i];
			if (className && className.length > 0)
				names.push(className);
		}
	}

	if (names.length > 0)
		regex = new RegExp('(?:^|\\s+)(?:' + names.join('|') + ')(?=$|\\s)');
	else
		regex = null;

	return FCKJustifyCommand._ClassRegex = regex;
};

FCKJustifyCommand.prototype = {
	Execute : function() {
		// Save an undo snapshot before doing anything.
		FCKUndo.SaveUndoStep();

		var range = new FCKDomRange(FCK.EditorWindow);
		range.MoveToSelection();

		var currentState = this.GetState();
		if (currentState == FCK_TRISTATE_DISABLED)
			return;

		// Store a bookmark of the selection since the paragraph iterator might
		// change the DOM tree and break selections.
		var bookmark = range.CreateBookmark();
		// add by diyanliang 08-11-13 解决选中多个单元格操作 格式混乱的错误
		var rangeIter = new FCKDomRangeIterator(range);
		var node;
		while ((node = rangeIter.GetNextParagraph())) {
			if (node.tagName == "P" && (node.parentNode.tagName == "TR" || node.parentNode.tagName == "TBODY")) {
				alert("请选择正确的区域进行此操作!");
				return;
			}
		}

		// end

		var cssClassName = this._CssClassName;

		// Apply alignment setting for each paragraph.
		var iterator = new FCKDomRangeIterator(range);
		var block;
		var ISchan = 0;
		var tem=null;
		while ((block = iterator.GetNextParagraph())) {
			if (block.tagName == "P" && (block.parentNode.tagName == "TR" || block.parentNode.tagName == "TBODY")) {
				var tempp = block.parentNode;
				tem = block.parentNode;
				tempp.removeChild(F);
				// var arr=FCKTableHandler._CreateTableMap(tempp.parentNode);
				// FCKTableHandler._InstallTableMap(arr, tempp.parentNode);
				// continue;
				ISchan++;
			}

			block.removeAttribute('align');

			if (cssClassName) {
				// Remove the any of the alignment classes from the className.
				var className = block.className.replace(FCKJustifyCommand._GetClassNameRegex(), '');

				// Append the desired class name.
				if (currentState == FCK_TRISTATE_OFF) {
					if (className.length > 0)
						className += ' ';
					block.className = className + cssClassName;
				} else if (className.length == 0)
					FCKDomTools.RemoveAttribute(block, 'class');
			} else {
				var style = block.style;
				if (currentState == FCK_TRISTATE_OFF)
					style.textAlign = this.AlignValue;
				else {
					style.textAlign = '';
					if (style.cssText.length == 0)
						block.removeAttribute('style');
				}
			}
		}
		if (ISchan > 0) {
			var arr = FCKTableHandler._CreateTableMap(tem.parentNode);
			FCKTableHandler._InstallTableMap(arr, tem.parentNode);
		}

		// Restore previous selection.
		range.MoveToBookmark(bookmark);
		range.Select();

		FCK.Focus();
		FCK.Events.FireEvent('OnSelectionChange');
	},

	GetState : function() {
		// Disabled if not WYSIWYG.
		if (FCK.EditMode != FCK_EDITMODE_WYSIWYG || !FCK.EditorWindow)
			return FCK_TRISTATE_DISABLED;

		// Retrieve the first selected block.
		var path = new FCKElementPath(FCKSelection.GetBoundaryParentElement(true));
		var firstBlock = path.Block || path.BlockLimit;

		if (!firstBlock || firstBlock.nodeName.toLowerCase() == 'body')
			return FCK_TRISTATE_OFF;

		// Check if the desired style is already applied to the block.
		var currentAlign;
		if (FCKBrowserInfo.IsIE)
			currentAlign = firstBlock.currentStyle.textAlign;
		else
			currentAlign = FCK.EditorWindow.getComputedStyle(firstBlock, '').getPropertyValue('text-align');
		currentAlign = currentAlign.replace(/(-moz-|-webkit-|start|auto)/i, '');
		if ((!currentAlign && this.IsDefaultAlign) || currentAlign == this.AlignValue)
			return FCK_TRISTATE_ON;
		return FCK_TRISTATE_OFF;
	}
};
