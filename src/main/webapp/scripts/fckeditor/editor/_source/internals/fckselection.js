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
 * Active selection functions.
 */

var FCKSelection = FCK.Selection = {
	UseGeckoApi:function(){
		if (window.getSelection)
			return true;
		else
			return false;
	},
	GetParentBlock : function() {
		var retval = this.GetParentElement();
		while (retval) {
			if (FCKListsLib.BlockBoundaries[retval.nodeName.toLowerCase()])
				break;
			retval = retval.parentNode;
		}
		return retval;
	},

	ApplyStyle : function(styleDefinition) {
		FCKStyles.ApplyStyle(new FCKStyle(styleDefinition));
	},
	/**
	 * Returns the native selection object.
	 */
	GetSelection : function() {
		if (FCKBrowserInfo.IsIE){
			this.Restore();
		}
		if (FCK.EditorWindow.getSelection){
			return FCK.EditorWindow.getSelection();
		}else if (FCK.EditorDocument.selection){
			return FCK.EditorDocument.selection;
		}
		return null;
	},
	GetSelectionText:function(){
		if (FCK.EditorWindow.getSelection){
			if (this.SelectionData){
				return this.SelectionData.toString();
			}
			return FCK.EditorWindow.getSelection().toString();
		}else if (FCK.EditorDocument.selection){
			return FCK.EditorDocument.selection.createRange().text;
		}
		return null;
	},
	Restore : function() {
		if (this.SelectionData) {
			FCK.IsSelectionChangeLocked = true;

			try {
//				// Don't repeat the restore process if the editor document is already selected.
//				if (this._GetSelectionDocument() == FCK.EditorDocument) {
//					FCK.IsSelectionChangeLocked = false;
//					return;
//				}
//				modified by xiejj: it's maybe bug,please note
				if (this.SelectionData.select){
					this.SelectionData.select();
				}else{
					//It's IE11
					var oSel = FCK.EditorWindow.getSelection();
					oSel.removeAllRanges();
					oSel.addRange(this.SelectionData);
				}
			} catch (e) {
			}

			FCK.IsSelectionChangeLocked = false;
			}
	},
	Release : function() {
		if (this.SelectionData) {
			delete this.SelectionData;
			this.SelectionData = null;
		}
	},
	_GetSelectionDocument : function() {
		var range;
		if (this.SelectionData){
			range = this.SelectionData;
		}else{
			range = this.GetSelectionRange();
		}
		if (!range)
			return null;
		else if (range.item)
			return FCKTools.GetElementDocument(range.item(0));
		else if (range.parentElement)
			return FCKTools.GetElementDocument(range.parentElement());
		else
			return FCKTools.GetElementDocument(this._GetRangeParent(range));
		
	},
	GetSelectionRange : function(selection) {
		if (!selection) {
			selection = this.GetSelection();
		}
		if (window.getSelection) {
			if (selection.rangeCount>0){
				return selection.getRangeAt(0);
			}
		} else {
			return selection.createRange();
		}
	},
	// Retrieves the selected element (if any), just in the case that a single
	// element (object like and image or a table) is selected.
	GetSelectedElement : function() {
		if (FCK.EditorWindow.getSelection) {
			var selection = !!FCK.EditorWindow && this.GetSelection();
			if (!selection || selection.rangeCount < 1)
				return null;

			var range = selection.getRangeAt(0);
			if (range.startContainer != range.endContainer
					|| range.startContainer.nodeType != 1
					|| range.startOffset != range.endOffset - 1)
				return null;

			var node = range.startContainer.childNodes[range.startOffset];
			if (node.nodeType != 1)
				return null;

			return node;
		} else {
			if (this.GetType() == 'Control') {
				var oRange = this.GetSelectionRange();

				if (oRange && oRange.item)
					return oRange.item(0);
			}
			return null;
		}
	},
	_GetRangeParent:function(range){
		if (range.startContainer != range.endContainer)
			return null;
		return range.startContainer;
	},
	GetParentElement : function() {
		if (this.UseGeckoApi()) {
			var selection = !!FCK.EditorWindow && this.GetSelection();
			if (!selection || selection.rangeCount < 1)
				return null;

			var range = selection.getRangeAt(0);
			if (range.startContainer != range.endContainer
					|| range.startContainer.nodeType != 1
					|| range.startOffset != range.endOffset - 1)
				return null;

			var node = range.startContainer.childNodes[range.startOffset];
			if (node.nodeType != 1)
				return null;

			return node;
		} else {
			switch (this.GetType()) {
			case 'Control':
				var el = FCKSelection.GetSelectedElement();
				return el ? el.parentElement : null;

			case 'None':
				return null;

			default:
				return this.GetSelectionRange().parentElement();
			}
		}
	},
	SelectNode : function(node) {
		if (this.UseGeckoApi()) {
			var oRange = FCK.EditorDocument.createRange();
			oRange.selectNode(node);

			var oSel = this.GetSelection();
			oSel.removeAllRanges();
			oSel.addRange(oRange);
		} else {
			FCK.Focus();
			this.GetSelection().empty();
			var oRange = null;
			try {
				// Try to select the node as a control.
				oRange = FCK.EditorDocument.body.createControlRange();
				oRange.addElement(node);
			} catch (e) {
				// If failed, select it as a text range.
				try {
					oRange = FCK.EditorDocument.body.createTextRange();
					oRange.moveToElementText(node);
				} catch (ex) {

				}
			}

			oRange.select();
		}
	},
	Collapse : function(toStart) {
		if (this.UseGeckoApi()) {
			var oSel = this.GetSelection();

			if (toStart == null || toStart === true)
				oSel.collapseToStart();
			else
				oSel.collapseToEnd();
		} else {
			FCK.Focus();
			if (this.GetType() == 'Text') {
				var oRange = this.GetSelectionRange();
				oRange.collapse(toStart == null || toStart === true);
				oRange.select();
			}
		}
	},

	// The "nodeTagName" parameter must be Upper Case.
	HasAncestorNode : function(nodeTagName) {
		if (this.UseGeckoApi()) {
			var oContainer = this.GetSelectedElement();
			if (!oContainer && FCK.EditorWindow) {
				try {
					oContainer = this.GetSelection().getRangeAt(0).startContainer;
				} catch (e) {
				}
			}

			while (oContainer) {
				if (oContainer.nodeType == 1
						&& oContainer.nodeName.IEquals(nodeTagName))
					return true;
				oContainer = oContainer.parentNode;
			}

			return false;
		} else {
			var oContainer;

			if (this.GetSelection().type == "Control") {
				oContainer = this.GetSelectedElement();
			} else {
				var oRange = this.GetSelectionRange();
				oContainer = oRange.parentElement();
			}

			while (oContainer) {
				if (oContainer.nodeName.IEquals(nodeTagName))
					return true;
				oContainer = oContainer.parentNode;
			}

			return false;
		}
	},
	Delete : function() {
		// Gets the actual selection.
		var oSel = this.GetSelection();
		if (this.UseGeckoApi()) {

			// Deletes the actual selection contents.
			for ( var i = 0; i < oSel.rangeCount; i++) {
				oSel.getRangeAt(i).deleteContents();
			}

			return oSel;
		} else {
			// Deletes the actual selection contents.
			if (oSel.type.toLowerCase() != "none") {
				oSel.clear();
			}
			return oSel;
		}
	},
	GetType : function() {
		if (this.UseGeckoApi()) {
			// By default set the type to "Text".
			var type = 'Text';

			// Check if the actual selection is a Control (IMG, TABLE, HR, etc...).

			var sel = null;
			try {
				sel = this.GetSelection();
			} catch (e) {
			}

			if (sel && sel.rangeCount == 1) {
				var range = sel.getRangeAt(0);
				if (range.startContainer == range.endContainer
						&& (range.endOffset - range.startOffset) == 1
						&& range.startContainer.nodeType == 1
						&& FCKListsLib.StyleObjectElements[range.startContainer.childNodes[range.startOffset].nodeName
								.toLowerCase()]) {
					type = 'Control';
				}
			}

			return type;
		} else {
			// It is possible that we can still get a text range object even when type=='None' is returned by IE.
			// So we'd better check the object returned by createRange() rather than by looking at the type.
			try {
				var ieType = this.GetSelection().type;
				if (ieType == 'Control' || ieType == 'Text')
					return ieType;

				if (this.GetSelectionRange().parentElement)
					return 'Text';
			} catch (e) {
				// Nothing to do, it will return None properly.
			}

			return 'None';
		}
	},
	GetBoundaryParentElement : function(startBoundary) {
		if (this.UseGeckoApi()) {
			if (!FCK.EditorWindow)
				return null;
			if (this.GetType() == 'Control')
				return this.GetSelectedElement().parentNode;
			else {
				var oSel = this.GetSelection();
				if (oSel && oSel.rangeCount > 0) {
					var range = oSel.getRangeAt(startBoundary ? 0
							: (oSel.rangeCount - 1));

					var element = startBoundary ? range.startContainer
							: range.endContainer;

					return (element.nodeType == 1 ? element
							: element.parentNode);
				}
			}
			return null;
		} else {
			switch (this.GetType()) {
			case 'Control':
				var el = this.GetSelectedElement();
				return el ? el.parentElement : null;

			case 'None':
				return null;

			default:
				var doc = FCK.EditorDocument;

				var range = this.GetSelectionRange();
				range.collapse(startBoundary !== false);

				var el = range.parentElement();

				// It may happen that range is comming from outside "doc", so we
				// must check it (#1204).
				return FCKTools.GetElementDocument(el) == doc ? el : null;
			}
		}
	},
	MoveToAncestorNode : function(nodeTagName) {
		if (this.UseGeckoApi()) {
			var oContainer = this.GetSelectedElement();
			if (!oContainer)
				oContainer = this.GetSelection().getRangeAt(0).startContainer;

			while (oContainer) {
				if (oContainer.nodeName.IEquals(nodeTagName))
					return oContainer;

				oContainer = oContainer.parentNode;
			}
			return null;
		} else {
			var oNode = null, oRange;
			if (!FCK.EditorDocument)
				return null;

			if (this.GetSelection().type == "Control") {
				oRange = this.GetSelectionRange();
				for ( var i = 0; i < oRange.length; i++) {
					if (oRange(i).parentNode) {
						oNode = oRange(i).parentNode;
						break;
					}
				}
			} else {
				oRange = this.GetSelectionRange();
				oNode = oRange.parentElement();
			}

			while (oNode && oNode.nodeName != nodeTagName)
				oNode = oNode.parentNode;

			return oNode;
		}
	},
	Save : function(bLock) {
		if (FCKBrowserInfo.IsIE){
			// Ensures the editor has the selection focus. (#1801)
			FCK.Focus();
			
			var editorDocument = FCK.EditorDocument;
			
			if (!editorDocument)
				return;
			
			if (this.locked)
				return;
			
			this.locked = !!bLock;
			var range= this.GetSelectionRange();
			
			// Ensure that the range comes from the editor document.
			if (range) {
				if (range.parentElement && FCKTools.GetElementDocument(range.parentElement()) != editorDocument)
					range = null;
				else if (range.item
						&& FCKTools.GetElementDocument(range.item(0)) != editorDocument)
					range = null;
			}
			
			this.SelectionData = range;
		}
	}
};