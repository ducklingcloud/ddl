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
 * Defines the FCKToolbarSet object that is used to load and draw the
 * toolbar.
 */

function FCKToolbarSet_Create(overhideLocation) {
	var oToolbarSet;
	var sLocation;

	if (overhideLocation)
		sLocation = overhideLocation;
	else
		sLocation = FCKConfig.ToolbarLocation;

	switch (sLocation) {
	case 'In':
		document.getElementById('xToolbarRow').style.display = '';
		oToolbarSet = new FCKToolbarSet(document);
		break;
	case 'None':
		oToolbarSet = new FCKToolbarSet(document);
		break;

	// case 'OutTop' :
	// Not supported.

	default:
		FCK.Events.AttachEvent('OnBlur', FCK_OnBlur);
		FCK.Events.AttachEvent('OnFocus', FCK_OnFocus);

		var eToolbarTarget = null;

		// Out:[TargetWindow]([TargetId])
		var oOutMatch = sLocation.match(/^Out:(.+)\((\w+)\)$/);
		if (oOutMatch) {
			if (FCKBrowserInfo.IsAIR)
				FCKAdobeAIR.ToolbarSet_GetOutElement(window, oOutMatch);
			else
				eToolbarTarget = eval('parent.' + oOutMatch[1]).document.getElementById(oOutMatch[2]);
		} else {
			// Out:[TargetId]
			oOutMatch = sLocation.match(/^Out:(\w+)$/);
			if (oOutMatch)
				eToolbarTarget = parent.document.getElementById(oOutMatch[1]);
		}

		if (!eToolbarTarget) {
			alert('Invalid value for "ToolbarLocation"');
			return arguments.callee('In');
		}

		// If it is a shared toolbar, it may be already available in the target
		// element.
		oToolbarSet = eToolbarTarget.__FCKToolbarSet;
		if (oToolbarSet)
			break;

		// Create the IFRAME that will hold the toolbar inside the target
		// element.
		var eToolbarIFrame = FCKTools.GetElementDocument(eToolbarTarget).createElement('iframe');
		eToolbarIFrame.src = 'javascript:void(0)';
		eToolbarIFrame.frameBorder = 0;
		eToolbarIFrame.width = '100%';
		eToolbarIFrame.height = '10';
		eToolbarTarget.appendChild(eToolbarIFrame);
		eToolbarIFrame.unselectable = 'on';

		// Write the basic HTML for the toolbar (copy from the editor main
		// page).
		var eTargetDocument = eToolbarIFrame.contentWindow.document;

		// Workaround for Safari 12256. Ticket #63
		var sBase = '';
		if (FCKBrowserInfo.IsSafari)
			sBase = '<base href="' + window.document.location + '">';

		// Initialize the IFRAME document body.
		eTargetDocument.open();
		eTargetDocument
				.write("<html><head>"
						+ sBase
						+ "<script type=\"text/javascript\"> var adjust = function() { window.frameElement.height = document.body.scrollHeight ; }; window.onresize = window.onload = function(){var timer = null;var lastHeight = -1;var lastChange = 0;var poller = function(){var currentHeight = document.body.scrollHeight || 0;var currentTime = (new Date()).getTime();if (currentHeight != lastHeight){lastChange = currentTime;adjust();lastHeight = document.body.scrollHeight;}if (lastChange < currentTime - 1000) clearInterval(timer);};timer = setInterval(poller, 100);}</script></head><body style=\"overflow: hidden\">"
						+ document.getElementById("xToolbarSpace").innerHTML + "</body></html>");
		eTargetDocument.close();

		if (FCKBrowserInfo.IsAIR)
			FCKAdobeAIR.ToolbarSet_InitOutFrame(eTargetDocument);

		FCKTools.AddEventListener(eTargetDocument, 'contextmenu', FCKTools.CancelEvent);

		// Load external resources (must be done here, otherwise Firefox will
		// not
		// have the document DOM ready to be used right away.
		FCKTools.AppendStyleSheet(eTargetDocument, FCKConfig.SkinEditorCSS);

		oToolbarSet = eToolbarTarget.__FCKToolbarSet = new FCKToolbarSet(eTargetDocument);
		oToolbarSet._IFrame = eToolbarIFrame;

		if (FCK.IECleanup)
			FCK.IECleanup.AddItem(eToolbarTarget, FCKToolbarSet_Target_Cleanup);
	}

	oToolbarSet.CurrentInstance = FCK;
	if (!oToolbarSet.ToolbarItems)
		oToolbarSet.ToolbarItems = FCKToolbarItems;

	FCK.AttachToOnSelectionChange(oToolbarSet.RefreshItemsState);

	return oToolbarSet;
}

function FCK_OnBlur(editorInstance) {
	var eToolbarSet = editorInstance.ToolbarSet;

	if (eToolbarSet.CurrentInstance == editorInstance)
		eToolbarSet.Disable();
}

function FCK_OnFocus(editorInstance) {
	var oToolbarset = editorInstance.ToolbarSet;
	var oInstance = editorInstance || FCK;

	// Unregister the toolbar window from the current instance.
	oToolbarset.CurrentInstance.FocusManager.RemoveWindow(oToolbarset._IFrame.contentWindow);

	// Set the new current instance.
	oToolbarset.CurrentInstance = oInstance;

	// Register the toolbar window in the current instance.
	oInstance.FocusManager.AddWindow(oToolbarset._IFrame.contentWindow, true);

	oToolbarset.Enable();
}

function FCKToolbarSet_Cleanup() {
	this._TargetElement = null;
	this._IFrame = null;
}

function FCKToolbarSet_Target_Cleanup() {
	this.__FCKToolbarSet = null;
}

var FCKToolbarSet = function(targetDocument) {
	this._Document = targetDocument;

	// Get the element that will hold the elements structure.
	this._TargetElement = targetDocument.getElementById('xToolbar');

	// Setup the expand and collapse handlers.
	var eExpandHandle = targetDocument.getElementById('xExpandHandle');
	var eCollapseHandle = targetDocument.getElementById('xCollapseHandle');

	eExpandHandle.title = FCKLang.ToolbarExpand;
	FCKTools.AddEventListener(eExpandHandle, 'click', FCKToolbarSet_Expand_OnClick);

	eCollapseHandle.title = FCKLang.ToolbarCollapse;
	FCKTools.AddEventListener(eCollapseHandle, 'click', FCKToolbarSet_Collapse_OnClick);

	// Set the toolbar state at startup.
	if (!FCKConfig.ToolbarCanCollapse || FCKConfig.ToolbarStartExpanded)
		this.Expand();
	else
		this.Collapse();

	// Enable/disable the collapse handler
	eCollapseHandle.style.display = FCKConfig.ToolbarCanCollapse ? '' : 'none';

	if (FCKConfig.ToolbarCanCollapse)
		eCollapseHandle.style.display = '';
	else
		targetDocument.getElementById('xTBLeftBorder').style.display = '';

	// Set the default properties.
	this.Toolbars = new Array();
	this.IsLoaded = false;

	if (FCK.IECleanup)
		FCK.IECleanup.AddItem(this, FCKToolbarSet_Cleanup);
};

function FCKToolbarSet_Expand_OnClick() {
	FCK.ToolbarSet.Expand();
}

function FCKToolbarSet_Collapse_OnClick() {
	FCK.ToolbarSet.Collapse();
}

FCKToolbarSet.prototype.Expand = function() {
	this._ChangeVisibility(false);
};

FCKToolbarSet.prototype.Collapse = function() {
	this._ChangeVisibility(true);
};

FCKToolbarSet.prototype._ChangeVisibility = function(collapse) {
	this._Document.getElementById('xCollapsed').style.display = collapse ? '' : 'none';
	this._Document.getElementById('xExpanded').style.display = collapse ? 'none' : '';

	if (FCKBrowserInfo.IsGecko) {
		// I had to use "setTimeout" because Gecko was not responding in a right
		// way when calling window.onresize() directly.
		FCKTools.RunFunction(window.onresize);
	}
};

FCKToolbarSet.prototype.Load = function(toolbarSetName) {
	// add for duckling toolbar 09-6-8

	if (toolbarSetName) {
		if (FCKConfig.ToolbarType) {
			eval("this." + FCKConfig.ToolbarType + "ToolBar(toolbarSetName)");
		} else {
			this.ducklingToolBar(toolbarSetName);
		}

	} else {
		this.Name = toolbarSetName;

		this.Items = new Array();

		// Reset the array of toolbar items that are active only on WYSIWYG
		// mode.
		this.ItemsWysiwygOnly = new Array();

		// Reset the array of toolbar items that are sensitive to the cursor
		// position.
		this.ItemsContextSensitive = new Array();

		// Cleanup the target element.
		this._TargetElement.innerHTML = '';

		var ToolbarSet = FCKConfig.ToolbarSets[toolbarSetName];

		if (!ToolbarSet) {
			alert(FCKLang.UnknownToolbarSet.replace(/%1/g, toolbarSetName));
			return;
		}

		this.Toolbars = new Array();

		for ( var x = 0; x < ToolbarSet.length; x++) {
			var oToolbarItems = ToolbarSet[x];

			// If the configuration for the toolbar is missing some element or
			// has any extra comma
			// this item won't be valid, so skip it and keep on processing.
			if (!oToolbarItems)
				continue;

			var oToolbar = null;

			if (typeof (oToolbarItems) == 'string') {
				if (oToolbarItems == '/')
					oToolbar = new FCKToolbarBreak();
			} else {
				oToolbar = new FCKToolbar();

				for ( var j = 0; j < oToolbarItems.length; j++) {
					var sItem = oToolbarItems[j];

					if (sItem == '-')
						oToolbar.AddSeparator();
					else {
						var oItem = FCKToolbarItems.GetItem(sItem);
						if (oItem) {
							oToolbar.AddItem(oItem);

							this.Items.push(oItem);

							if (!oItem.SourceView)
								this.ItemsWysiwygOnly.push(oItem);

							if (oItem.ContextSensitive)
								this.ItemsContextSensitive.push(oItem);
						}
					}
				}

				// oToolbar.AddTerminator() ;
			}

			oToolbar.Create(this._TargetElement);

			this.Toolbars[this.Toolbars.length] = oToolbar;
		}

		FCKTools.DisableSelection(this._Document.getElementById('xCollapseHandle').parentNode);

		if (FCK.Status != FCK_STATUS_COMPLETE)
			FCK.Events.AttachEvent('OnStatusChange', this.RefreshModeState);
		else
			this.RefreshModeState();

		this.IsLoaded = true;
		this.IsEnabled = true;

		FCKTools.RunFunction(this.OnLoad);
	}// end else
};

FCKToolbarSet.prototype.Enable = function() {
	if (this.IsEnabled)
		return;

	this.IsEnabled = true;

	var aItems = this.Items;
	for ( var i = 0; i < aItems.length; i++)
		aItems[i].RefreshState();
};

FCKToolbarSet.prototype.Disable = function() {
	if (!this.IsEnabled)
		return;

	this.IsEnabled = false;

	var aItems = this.Items;
	for ( var i = 0; i < aItems.length; i++)
		aItems[i].Disable();
};

FCKToolbarSet.prototype.RefreshModeState = function(editorInstance) {
	if (FCK.Status != FCK_STATUS_COMPLETE)
		return;

	var oToolbarSet = editorInstance ? editorInstance.ToolbarSet : this;
	var aItems = oToolbarSet.ItemsWysiwygOnly;

	if (FCK.EditMode == FCK_EDITMODE_WYSIWYG) {
		// Enable all buttons that are available on WYSIWYG mode only.
		for ( var i = 0; i < aItems.length; i++)
			aItems[i].Enable();

		// Refresh the buttons state.
		oToolbarSet.RefreshItemsState(editorInstance);
	} else {
		// Refresh the buttons state.
		oToolbarSet.RefreshItemsState(editorInstance);

		// Disable all buttons that are available on WYSIWYG mode only.
		for ( var j = 0; j < aItems.length; j++)
			aItems[j].Disable();
	}
};

FCKToolbarSet.prototype.RefreshItemsState = function(editorInstance) {

	var aItems = (editorInstance ? editorInstance.ToolbarSet : this).ItemsContextSensitive;

	for ( var i = 0; i < aItems.length; i++)
		aItems[i].RefreshState();

	// add by diyanliang2008-11-14
	hiddenBarForTable();
	// add by diyanliang2008-11-21
	hiddenBarForOrderedList();
	try {
		HiddenButton();
	} catch (e) {
	}
};
/*
 * add by diyanliang 2009-6-8 dcukling纵排显示按钮
 */
FCKToolbarSet.prototype.ducklingToolBar = function(toolbarSetName) {
	this.Name = toolbarSetName;
	this.Items = [];
	this.ItemsWysiwygOnly = [];
	this.ItemsContextSensitive = [];
	this._TargetElement.innerHTML = "";
	var B = FCKConfig.ToolbarSets[toolbarSetName];
	if (!B) {
		alert(FCKLang.UnknownToolbarSet.replace(/%1/g, toolbarSetName));
		return;
	}
	this.Toolbars = [];
	var ToolBarBoxfa = this._TargetElement;

	// add by diyanliang
	var ToolBarDiv = document.createElement("div");
	ToolBarDiv.id = "DE_ToolBarDiv";
	ToolBarDiv.style.overflow = "hidden";
	// ToolBarDiv.style.width="800px";
	ToolBarDiv.style.height = "88px";
	ToolBarBoxfa.appendChild(ToolBarDiv);
	// end

	var ToolBarBoxT = document.createElement("table");
	ToolBarBoxT.className = "TB_DucklingToolbar";
	var toolboxrow = ToolBarBoxT.insertRow(-1);
	ToolBarBox = toolboxrow.insertCell(-1);
	// ToolBarBoxfa.appendChild(ToolBarBoxT);
	ToolBarDiv.appendChild(ToolBarBoxT);

	// alert("outerHTML(this._TargetElement)="+outerHTML(this._TargetElement));
	for ( var x = 0; x < B.length; x++) {
		// 加bar
		var C = B[x];
		if (!C) {
			continue;
		}
		var D;
		D = new FCKToolbar();
		for ( var j = 0; j < C.length; j++) {
			var E = C[j];
			if (E == "-") {
				// 如果数据中有"-"向里面存"-"
				D.AddItem(E);
			} else {
				var F = FCKToolbarItems.GetItem(E);
				if (F) {
					D.AddItem(F);
					this.Items.push(F);
					if (!F.SourceView) {
						this.ItemsWysiwygOnly.push(F);
					}
					if (F.ContextSensitive) {
						this.ItemsContextSensitive.push(F);
					}
				}
			}
		}
		// 生成有边框的效果的table
		var boxtable = document.createElement("table");
		boxtable.className = "TB_DucklingToolbarBox";
		boxtable.style.styleFloat = boxtable.style.cssFloat = (FCKLang.Dir == "ltr" ? "left" : "right");
		boxtable.dir = FCKLang.Dir;
		boxtable.cellPadding = 0;
		boxtable.cellSpacing = 0;
		// boxtable.border="1px #acc7de solid";
		var boxrow = boxtable.insertRow(-1);
		boxcell = boxrow.insertCell(-1);
		// childtable
		chtable = document.createElement("table");
		chtable.cellPadding = 0;
		chtable.cellSpacing = 0;
		chtable.border = 0;
		var chrow1 = chtable.insertRow(-1);
		chcell11 = chrow1.insertCell(-1);
		chcell12 = chrow1.insertCell(-1);
		chcell13 = chrow1.insertCell(-1);
		var chrow2 = chtable.insertRow(-1);
		chcell21 = chrow2.insertCell(-1);
		chcell22 = chrow2.insertCell(-1);
		chcell23 = chrow2.insertCell(-1);
		var chrow3 = chtable.insertRow(-1);
		chcell31 = chrow3.insertCell(-1);
		chcell32 = chrow3.insertCell(-1);
		chcell33 = chrow3.insertCell(-1);

		chcell11.className = "top_left";
		chcell12.className = "top_center";
		chcell13.className = "top_right";
		chcell21.className = "center_left";
		chcell23.className = "center_right";
		chcell31.className = "bottom_left";
		chcell32.className = "bottom_center";
		chcell33.className = "bottom_right";

		boxcell.appendChild(chtable);
		ToolBarBox.appendChild(boxtable);
		// end
		D.DucklingCreate(chcell22);
		this.Toolbars[this.Toolbars.length] = D;
	}
	FCKTools.DisableSelection(this._Document.getElementById("xCollapseHandle").parentNode);
	if (FCK.Status != 2) {
		FCK.Events.AttachEvent("OnStatusChange", this.RefreshModeState);
	} else {
		this.RefreshModeState();
	}
	this.IsLoaded = true;
	this.IsEnabled = true;
	FCKTools.RunFunction(this.OnLoad);
};

/*
 * add by diyanliang 2008-11-14 如果选中的内容有table边框 隐藏相应的按钮
 * 该事件在fireEvent（）中触发，如果选择的元素是TD就禁用这些按钮
 */
function hiddenBarForTable() {
	var HiddenBSList = [];
	HiddenBSList.push('Select', 'Button', 'Link', 'Image', 'Unlink', 'UnorderedList', 'Outdent', 'Indent');
	HiddenBSList.push('Bold', 'Italic', 'Underline', 'StrikeThrough', 'OrderedList');
	HiddenBSList.push('JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyFull');
	HiddenBSList.push('Rule', 'SpecialChar');
	HiddenBSList.push('Subscript', 'Superscript');
	HiddenBSList.push('FontFormat', 'FontName', 'FontSize');
	var getSE = FCKSelection.GetSelectedElement();
	if (getSE != null) {
		if (getSE.tagName == "TD") {
			for ( var i = 0; i < HiddenBSList.length; i++) {
				var HSE = FCKToolbarItems.GetItem(HiddenBSList[i]);
				try {
					if (HSE)
						HSE.Disable();
				} catch (e) {
					doNothing();
				}

			}

		}
	}
}
/*
 * add by diyanliang 2008-11-21 该事件在fireEvent（）中触发，如果选择的元素是OL,UL就禁用标题
 */
function hiddenBarForOrderedList() {
	var HiddenBSList = [];
	HiddenBSList.push('FontFormat');
	var getSE = FCKSelection.HasAncestorNode("UL") || FCKSelection.HasAncestorNode("OL")
			|| FCKSelection.HasAncestorNode("LI");
	if (getSE) {
		for ( var i = 0; i < HiddenBSList.length; i++) {
			var HSE = FCKToolbarItems.GetItem(HiddenBSList[i]);
			HSE.Disable();
		}
	}
}