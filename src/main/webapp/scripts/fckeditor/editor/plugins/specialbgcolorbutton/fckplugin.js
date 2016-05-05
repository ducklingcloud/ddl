/**
 * 建立按钮
 */
var FCKSpecialBGColorCommand = function(type) {
	this.Name = type;
	this.Type = type;
	var oWindow;
	if (FCKBrowserInfo.IsIE)
		oWindow = window;
	else if (FCK.ToolbarSet._IFrame)
		oWindow = FCKTools.GetElementWindow(FCK.ToolbarSet._IFrame);
	else
		oWindow = window.parent;
	this._Panel = new FCKPanel(oWindow);
	this._Panel.AppendStyleSheet(FCKConfig.SkinEditorCSS);
	this._Panel.MainNode.className = 'FCK_Panel';
	this._CreatePanelBody();
	FCK.ToolbarSet.ToolbarItems.GetItem(this.Name).RegisterPanel(this._Panel);

	if (FCKConfig.DucklingBaseHref
			.substring(FCKConfig.DucklingBaseHref.length - 1) == "/") {
		fileurl = FCKConfig.DucklingBaseHref + "/jsp/aone/css/css.css";
	} else {
		fileurl = FCKConfig.DucklingBaseHref + "/jsp/aone/css/css.css";
	}
	FCKTools._AppendStyleSheet(this._Panel.Document, fileurl);
	FCKTools.DisableSelection(this._Panel.Document.body);
	this._Panel.OnHide=function(panel){
		FCKSelection.Release();
	};
};
FCKSpecialBGColorCommand.prototype.Execute = function(panelX, panelY,
		relElement) {
	FCKSelection.Save();
	this._Panel.Show(panelX, panelY, relElement);
};
FCKSpecialBGColorCommand.prototype._CreatePanelBody = function() {
	this._panel = this._Panel;
	targetDocument = this._panel.Document;
	targetDiv = this._panel.MainNode;
	var oTable = targetDiv.appendChild(targetDocument.createElement("TABLE"));
	oTable.className = 'ForceBaseFont';
	oTable.id = "E2TableCreatebutton";
	oTable.style.tableLayout = 'fixed';
	oTable.cellPadding = 3;
	oTable.cellSpacing = 0;
	oTable.border = 0;
	oTable.width = 50;
	// 增加清除按钮 start
	c_odiv = targetDocument.createElement("span");
	c_odiv.className = "RemoveFormat";
	c_odiv.innerHTML = FCKLang.Clean;
	var c_row = oTable.insertRow(-1);
	c_cell = c_row.insertCell(-1);
	c_cell.appendChild(c_odiv);
	FCKTools.AddEventListenerEx(c_row, 'mouseover',
			FCKSpecialBGColorPanel_OnMouseOver);
	FCKTools.AddEventListenerEx(c_row, 'mouseout',
			FCKSpecialBGColorPanel_OnMouseOut);
	FCKTools.AddEventListenerEx(c_row, 'click', function() {
		FCKSelection.Restore();
		command = new FCKRemoveFormatCommand();
		command.Execute();
	});
	// end

	var arratr = FCKConfig.BGColors.split(';');
	for ( var i = 0; i < arratr.length; i++) {

		var row = oTable.insertRow(-1);
		cell = row.insertCell(-1);
		odiv = targetDocument.createElement("span");
		odiv.className = arratr[i];
		odiv.innerHTML = FCKLang.Style + (i + 1);
		cell.appendChild(odiv);

		var arguments = new Array();
		var m_Button = {
			"_m_functionName" : arratr[i],
			"_m_styleelement" : row
		};
		arguments[0] = m_Button;
		FCKTools.AddEventListenerEx(row, 'mouseover',
				FCKSpecialBGColorPanel_OnMouseOver);
		FCKTools.AddEventListenerEx(row, 'mouseout',
				FCKSpecialBGColorPanel_OnMouseOut);
		FCKTools.AddEventListenerEx(row, 'click',
				FCKSpecialBGColorPanel_OnClick, arguments);

	}
	FCKTools.DisableSelection(targetDocument.body);
};
FCKSpecialBGColorCommand.prototype.GetState = function() {
	if (FCK.EditMode != FCK_EDITMODE_WYSIWYG)
		return FCK_TRISTATE_DISABLED;

	return FCK_TRISTATE_OFF;
};
/**
 * 按钮面板效果和事件
 */
var FCKSpecialBGColorPanel = {
	"Activate" : function(obj) {
		obj.className = 'MN_Item_Over';
	},
	"Deactivate" : function(obj) {
		obj.className = 'MN_Item';
	}
};
/**
 * 鼠标绑定事件
 */
function FCKSpecialBGColorPanel_OnMouseOver(ev) {
	FCKSpecialBGColorPanel.Activate(this);
}
function FCKSpecialBGColorPanel_OnMouseOut(ev) {
	FCKSpecialBGColorPanel.Deactivate(this);
}

function FCKSpecialBGColorPanel_OnClick(ev, obj) {
	FCKSelection.Restore();
	FCKUndo.SaveUndoStep();
	var style = obj._m_functionName;
	command = new FCKCoreStyleCommand(style);
	command.Execute();
}