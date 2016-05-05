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
 * Utility functions. (IE version).
 */

FCKTools.CancelEvent = function(e) {
	if (e.preventDefault) {
		e.preventDefault();
	}
	e.returnValue = false;
	return false;
};

// Appends one or more CSS files to a document.
FCKTools._AppendStyleSheet = function(documentElement, cssFileUrl) {
	if (documentElement.createStyleSheet) {
		return documentElement.createStyleSheet(cssFileUrl).owningElement;
	} else {
		//IE11
		var e = documentElement.createElement('LINK');
		e.rel = 'stylesheet';
		e.type = 'text/css';
		e.href = cssFileUrl;
		documentElement.getElementsByTagName("HEAD")[0].appendChild(e);
		return e;
	}
};

// Appends a CSS style string to a document.
FCKTools.AppendStyleString = function(documentElement, cssStyles) {
	if (!cssStyles)
		return null;

	var s = documentElement.createStyleSheet("");
	s.cssText = cssStyles;
	return s;
};

// Removes all attributes and values from the element.
FCKTools.ClearElementAttributes = function(element) {
	element.clearAttributes();
};

FCKTools.GetAllChildrenIds = function(parentElement) {
	var aIds = new Array();
	for ( var i = 0; i < parentElement.all.length; i++) {
		var sId = parentElement.all[i].id;
		if (sId && sId.length > 0)
			aIds[aIds.length] = sId;
	}
	return aIds;
};

FCKTools.RemoveOuterTags = function(e) {
	e.insertAdjacentHTML('beforeBegin', e.innerHTML);
	e.parentNode.removeChild(e);
};

FCKTools.CreateXmlObject = function(object) {
	var aObjs = null;

	switch (object) {
	case 'XmlHttp':
		// Try the native XMLHttpRequest introduced with IE7.
		try {
			return new XMLHttpRequest();
		} catch (e) {
		}

		aObjs = [ 'MSXML2.XmlHttp', 'Microsoft.XmlHttp' ];
		break;

	case 'DOMDocument':
		aObjs = [ 'MSXML2.DOMDocument', 'Microsoft.XmlDom' ];
		break;
	}
	if (aObjs) {
		for ( var i = 0; i < 2; i++) {
			try {
				return new ActiveXObject(aObjs[i]);
			} catch (e) {
			}
		}
	}

	if (FCKLang.NoActiveX) {
		alert(FCKLang.NoActiveX);
		FCKLang.NoActiveX = null;
	}
	return null;
};

FCKTools.DisableSelection = function(element) {
	//IE10 虽然提供了msUserSelect，但依然会获得焦点.
	var useStyle = (typeof element.style.msUserSelect != "undefined")&& !FCKBrowserInfo.IsIE10;
	if (useStyle) {
		element.style.msUserSelect = 'none';
	} else {
		element.setAttribute("unselectable", 'on');
	}
	var e, i = 0;
	// The extra () is to avoid a warning with strict error checking. This is ok.
	var elements = element.getElementsByTagName("*");
	while ((e = elements[i++])) {
		switch (e.tagName) {
		case 'IFRAME':
		case 'TEXTAREA':
		case 'INPUT':
		case 'SELECT':
			/* Ignore the above tags */
			break;
		default:
			if (useStyle) {
				e.style.msUserSelect = 'none';
			} else {
				e.setAttribute("unselectable", 'on');
			}
		}
	}
};

FCKTools.GetScrollPosition = function(relativeWindow) {
	var oDoc = relativeWindow.document;

	// Try with the doc element.
	var oPos = {
		X : oDoc.documentElement.scrollLeft,
		Y : oDoc.documentElement.scrollTop
	};

	if (oPos.X > 0 || oPos.Y > 0)
		return oPos;

	// If no scroll, try with the body.
	return {
		X : oDoc.body.scrollLeft,
		Y : oDoc.body.scrollTop
	};
};

FCKTools.AddEventListener = function(sourceObject, eventName, listener, userCapture) {
	if (typeof oTemp == undefined) {
		userCapture = false;
	}
	if (sourceObject.attachEvent) {
		sourceObject.attachEvent('on' + eventName, listener);
	} else {
		//IE11
		sourceObject.addEventListener(eventName, listener, userCapture);
	}
};

FCKTools.RemoveEventListener = function(sourceObject, eventName, listener) {
	if (sourceObject.detachEvent) {
		sourceObject.detachEvent('on' + eventName, listener);
	} else {
		//IE11
		sourceObject.removeEventListener(eventName, listener, false);
	}
};
FCKTools._PreventDefault = function(event) {
	if (event.preventDefault) {
		event.preventDefault();
	}
	event.returnValue = false;
};
// Listeners attached with this function cannot be detached.
FCKTools.AddEventListenerEx = function(sourceObject, eventName, listener, paramsArray) {
	// Ok... this is a closures party, but is the only way to make it clean of memory leaks.
	var o = new Object();
	o.Source = sourceObject;
	o.Params = paramsArray || []; // Memory leak if we have DOM objects here.
	o.Listener = function(ev) {
		var r = listener.apply(o.Source, [ ev ].concat(o.Params));
		if (!r) {
			FCKTools.CancelEvent(ev);
		}
		return r;
	};

	if (FCK.IECleanup)
		FCK.IECleanup.AddItem(null, function() {
			o.Source = null;
			o.Params = null;
		});

	FCKTools.AddEventListener(sourceObject, eventName, o.Listener);

	sourceObject = null; // Memory leak cleaner (because of the above closure).
	paramsArray = null; // Memory leak cleaner (because of the above closure).
};

// Returns and object with the "Width" and "Height" properties.
FCKTools.GetViewPaneSize = function(win) {
	var oSizeSource;

	var oDoc = win.document.documentElement;
	if (oDoc && oDoc.clientWidth) // IE6 Strict Mode
		oSizeSource = oDoc;
	else
		oSizeSource = win.document.body; // Other IEs

	if (oSizeSource)
		return {
			Width : oSizeSource.clientWidth,
			Height : oSizeSource.clientHeight
		};
	else
		return {
			Width : 0,
			Height : 0
		};
};

FCKTools.SaveStyles = function(element) {
	var data = FCKTools.ProtectFormStyles(element);

	var oSavedStyles = new Object();

	if (element.className.length > 0) {
		oSavedStyles.Class = element.className;
		element.className = '';
	}

	var sInlineStyle = element.style.cssText;

	if (sInlineStyle.length > 0) {
		oSavedStyles.Inline = sInlineStyle;
		element.style.cssText = '';
	}

	FCKTools.RestoreFormStyles(element, data);
	return oSavedStyles;
};

FCKTools.RestoreStyles = function(element, savedStyles) {
	var data = FCKTools.ProtectFormStyles(element);
	element.className = savedStyles.Class || '';
	element.style.cssText = savedStyles.Inline || '';
	FCKTools.RestoreFormStyles(element, data);
};

FCKTools.RegisterDollarFunction = function(targetWindow) {
	targetWindow.$ = function(id) {
		return targetWindow.document.getElementById(id);
	};
};

FCKTools.AppendElement = function(target, elementName) {
	return target.appendChild(this.GetElementDocument(target).createElement(elementName));
};

// This function may be used by Regex replacements.
FCKTools.ToLowerCase = function(strValue) {
	return strValue.toLowerCase();
};
