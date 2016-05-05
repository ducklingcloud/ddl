/**
 * diyanliang@cnic.cn
 * 
 * 2011-5-16
 */
FCKToolbarSet.prototype.E2ToolBar=function (A){
	A="E2";
	this.Name = A;
	this.Items = [];
	this.ItemsWysiwygOnly = [];
	this.ItemsContextSensitive = [];
	this._TargetElement.innerHTML = "";
	var B = FCKConfig.ToolbarSets[A];
	if (!B) {
		alert(FCKLang.UnknownToolbarSet.replace(/%1/g, A));
		return;
	}
	this.Toolbars = [];
	var ToolBarBoxfa=this._TargetElement;

	var ToolBarDiv=document.createElement("div");
	ToolBarDiv.id="DE_ToolBarDiv";
	ToolBarDiv.style.overflow="hidden";
	ToolBarBoxfa.appendChild(ToolBarDiv);

	var ToolBarBoxT = document.createElement("table");
	ToolBarBoxT.className = "TB_DucklingToolbar";
	var toolboxrow = ToolBarBoxT.insertRow(-1);
	ToolBarBox = toolboxrow.insertCell(-1);
	ToolBarDiv.appendChild(ToolBarBoxT);

	// alert("outerHTML(this._TargetElement)="+outerHTML(this._TargetElement));
	for (var x = 0; x < B.length; x++) {
		// 加bar
		var C = B[x];
			if (!C) {
				continue;
			}
			var D;
			D = new FCKToolbar();
			for (var j = 0; j < C.length; j++) {
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
			chtable= document.createElement("table");
			chtable.cellPadding = 0;
			chtable.cellSpacing = 0;
			chtable.border=0;
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
			

			chcell11.className="top_left";
			chcell12.className="top_center";
			chcell13.className="top_right";
			chcell21.className="center_left";
			chcell23.className="center_right";
			chcell31.className="bottom_left";
			chcell32.className="bottom_center";
			chcell33.className="bottom_right";

			boxcell.appendChild(chtable);
			ToolBarBox.appendChild(boxtable);
			// end
			D.E2Create(chcell22);
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

FCKToolbar.prototype.E2Create = function (A) {
	var B = FCKTools.GetElementDocument(A);
	var e = B.createElement("table");
	e.className = "TB_Toolbar";
	e.style.styleFloat = e.style.cssFloat = (FCKLang.Dir == "ltr" ? "left" : "right");
	e.dir = FCKLang.Dir;
	e.cellPadding = 0;
	e.cellSpacing = 0;
	e.border=1;
	var C = e.insertRow(-1);
	var D;
	for (var i = 0; i < this.Items.length; i++) {
		if(this.Items[i]=="-"){
			var Break = A.ownerDocument.createElement("div");
			Break.innerHTML="";
			Break.style.clear = Break.style.cssFloat = FCKLang.Dir == "rtl" ? "right" : "left";
			A.appendChild(Break);



			e = B.createElement("table");
			
			if(i==0){
				e.className = "TB_Toolbar_Top";
			}else{
				e.className = "TB_Toolbar";
			}
			e.style.styleFloat = e.style.cssFloat = (FCKLang.Dir == "ltr" ? "left" : "right");
			e.dir = FCKLang.Dir;
			e.cellPadding = 0;
			e.cellSpacing = 0;
			C = e.insertRow(-1);
			if (!this.HideEnd) {
				D = C.insertCell(-1);
				D.appendChild(B.createElement("div")).className = "TB_End1111111111";
			}
			A.appendChild(e);
		}else{
			this.Items[i].Create(C.insertCell(-1));
		}
		
	}
	if (!this.HideEnd) {
		D = C.insertCell(-1);
		D.appendChild(B.createElement("div")).className = "TB_End222";
	}
	A.appendChild(e);
};
/*
 * 按钮事件 new FCKToolbarButton(A, B, C, D, E, F, G); new
 * FCKToolbarPanelButton(A,B,C,D,G); A按钮名称 B显示名称 C提示名称 D=1只显示文字 =0只显示图片=2文字图片都显示
 * F=true元素如果符合该效果按钮做选中效果 G图片位置
 * 
 */
FCKToolbarItems.GetE2Item = function (A) {
	switch (A) {
	  case "E2H1":
		B = new FCKToolbarButton("E2H1", FCKLang.DlgGenTitle1, FCKLang.DlgGenTitl1, 1, true, true, 0);
		break;
	  case "E2H2":
			B = new FCKToolbarButton("E2H2", FCKLang.DlgGenTitle2, FCKLang.DlgGenTitl2, 1, true, true, 0);
			break;
	  case "E2H3":
			B = new FCKToolbarButton("E2H3", FCKLang.DlgGenTitle3, FCKLang.DlgGenTitl3, 1, true, true, 0);
			break;
	  case "E2P":
			B = new FCKToolbarButton("E2P", FCKLang.DlgGenP, FCKLang.DlgGenP, 1, true, true, 0);
			break;
	  case "E2Save":
			B = new FCKToolbarButton("E2Save", FCKLang.Save, FCKLang.Save, 2, true, true, 3);
			break;
	  case "E2RemoveFormat":
			B = new FCKToolbarButton("E2RemoveFormat", FCKLang.RemoveFormat, FCKLang.RemoveFormatLbl, 1, false, true, 4);
			break;
	  case "E2blockquote":
			B = new FCKToolbarButton("E2blockquote", FCKLang.Blockquote, FCKLang.Blockquote, 1, false, true, 0);
			break;
	  case "E2BGColor":
		  B = new FCKToolbarPanelButton("E2BGColor", FCKLang.BGColor, FCKLang.BGColor, 0, 46);
// B = new FCKToolbarButton("E2BGColor", FCKLang.BGColor,FCKLang.BGColor,0,true,
// true, 46);
			break;
	  case "E2Comment":
		    B = new FCKToolbarButton("E2Comment", FCKLang.E2Comment,FCKLang.E2Comment,1,true, true, 0);
			break;
	  case "E2Table":
// B = new FCKToolbarButton("E2Table", FCKLang.InsertTableLbl,
// FCKLang.InsertTable, 2, false, true, 39);
			B = new FCKToolbarPanelButton("E2Table", FCKLang.InsertTableLbl, FCKLang.InsertTable, 2, 39);
			break;
	  case "E2Link":
			B = new FCKToolbarButton("E2Link", FCKLang.InsertLinkLbl, FCKLang.InsertLink, 2, false, true, 34);
			break;
	  case "E2Page":
			B = new FCKToolbarButton("E2Page", FCKLang.DucklingLnkPageLbl, FCKLang.DucklingLnkPage, 2, false, true, 54);
			break;
	  case "E2Source":
			B = new FCKToolbarButton("E2Source", FCKLang.Source, FCKLang.SourceLbl, 2, true, true, 1);
			break;
	  case "E2Image":
			B = new FCKToolbarButton("E2Image", FCKLang.InsertImageLbl, FCKLang.InsertImage, 2, true, true, 37);
			break;
	  default:
		alert(FCKLang.UnknownToolbarItem.replace(/%1/g, A));
		return null;
	}
	FCKToolbarItems.LoadedItems[A] = B;
	return B;
};

/*
 * 绑定按钮命令
 * 
 */

FCKCommands.GetE2Command = function (A) {
	var B = FCKCommands.LoadedCommands[A];
	if (B) {
		return B;
	}
	switch (A) {
	  case "E2H1":
			B = new FCKHeadCommand("h1");
			break;
	  case "E2H2":
			B = new FCKHeadCommand("h2");
			break;
	  case "E2H3":
			B = new FCKHeadCommand("h3");
			break;
	  case "E2P":
			B = new FCKHeadCommand("p");
			break;
	  case "E2Save":
			B = new FCKE2SaveCommand();
			break;	
	  case "E2RemoveFormat":
			B = new FCKRemoveFormatCommand();
			break;
	  case "E2blockquote":
		  	B = new FCKBlockQuoteCommand();
			break;
	  case "E2BGColor":
// B = new FCKCoreStyleCommand("E2BGColor");
		  B = new FCKSpecialBGColorCommand("E2BGColor");
			break;
	  case "E2Comment":
		  B = new FCKCoreStyleCommand("E2Comment");
			break;
	  case "E2Table":
// B = new FCKDialogCommand("E2Table", FCKLang.DlgTableTitle,
// "dialog/fck_table.html", 480, 280);
			B = new FCKInsertTableCommand("E2Table");
			break;
	  case "E2Link":
			B = new FCKDialogCommand("E2Link", FCKLang.DlgLnkWindowTitle, "dialog/fck_DucklingLink.jsp", 450, 350);
			break;
	  case "E2Page":
			B = new E2ResourceDialogCommand();
			break;
	  case "E2Source":
			B = new E2SourceCommand();
			break;
	  case "E2Image":
			B = new E2ImageDialogCommand();
			break;
	  default:
		if (FCKRegexLib.NamedCommands.test(A)) {
			B = new FCKNamedCommand(A);
		} else {
			alert(FCKLang.UnknownCommand.replace(/%1/g, A));
			return null;
		}
	}
	
	
	FCKCommands.LoadedCommands[A] = B;
	return B;
};
/*
 * 按钮命令执行
 */
/* 保存 */
var FCKE2SaveCommand = function () {
	this.Name = "E2Save";
};
FCKE2SaveCommand.prototype.Execute = function () {
	 window.top.saveDEeditor();
	 FCKToolbarItems.GetItem("E2Save").Disable();
	 window.setTimeout(function(){FCKToolbarItems.GetItem("E2Save").Enable();}, 2000); 
};
FCKE2SaveCommand.prototype.GetState = function () {
	return 0;
};
/* 标题 */
var FCKHeadCommand = function (A) {
	this.Name = "CoreStyle";
	this.StyleName = "_FCK_" + A;
	this.IsActive = false;
	this._in=A;
	FCKStyles.AttachStyleStateChange(this.StyleName, this._OnStyleStateChange, this);
};
FCKHeadCommand.prototype = {
	Execute:function () {
	
			FCKUndo.SaveUndoStep();
			/*
			 * 检查在不在列表内
			 */
			try{
				var A = FCKSelection.GetBoundaryParentElement(true);
				var B = A;
				while (B) {
					if (B.nodeName.IEquals( [ 'ul', 'ol' ]))
						break;
					B = B.parentNode;
				}
				if (B &&(B.nodeName.IEquals("ul"))){
					var test=new FCKListCommand("insertunorderedlist", "ul");
					test.Execute();
				}else if (B &&(B.nodeName.IEquals("ol"))){
					var test=new FCKListCommand("insertunorderedlist", "ol");
					test.Execute();
				}
					
			}catch(e){
			}
			/* end */
	
			try{
				if (this.IsActive) {
					
					FCKStyles.RemoveStyle(this.StyleName);
				} else {
					FCKStyles.ApplyStyle(this.StyleName);
				}
			}catch(e){
				alert(e);
			}
			
			FCK.Focus();
			FCK.Events.FireEvent("OnSelectionChange");
		
	},
	GetState:function () {
		if (FCK.EditMode != 0) {
			return -1;
		}
		return this.IsActive ? 1 : 0;
	}, _OnStyleStateChange:function (A, B) {
		this.IsActive = B;
	}
};
/* span加样式 */


/* 插入表格按钮 */
var FCKInsertTableCommand = function( type )
{
	this.Name = type ;
	this.Type = type ;
	var oWindow ;
	if ( FCKBrowserInfo.IsIE )
		oWindow = window ;
	else if ( FCK.ToolbarSet._IFrame )
		oWindow = FCKTools.GetElementWindow( FCK.ToolbarSet._IFrame ) ;
	else
		oWindow = window.parent ;
	this._Panel = new FCKPanel( oWindow ) ;
	this._Panel.AppendStyleSheet( FCKConfig.SkinEditorCSS ) ;
	this._Panel.MainNode.className = 'FCK_Panel' ;
	this._CreatePanelBody() ;
	FCK.ToolbarSet.ToolbarItems.GetItem( this.Name ).RegisterPanel( this._Panel ) ;

	FCKTools.DisableSelection( this._Panel.Document.body ) ;
};

FCKInsertTableCommand.prototype.Execute = function( panelX, panelY, relElement )
{
	
	this._Panel.Show( panelX, panelY, relElement ) ;
	
	E2ToolBarSpecialTableButton.DisabledButton();
	
};
FCKInsertTableCommand.prototype._CreatePanelBody=function( ){
	

	E2ToolBarSpecialTableButton.Init(this._Panel);
	E2ToolBarSpecialTableButton.Create(true,"插入表格","E2Table");
	
	E2ToolBarSpecialTableButton.Create(false,FCKLang.InsertRowBefore,"TableInsertRowBefore");
	E2ToolBarSpecialTableButton.Create(false,FCKLang.InsertRowAfter,"TableInsertRowAfter");
	E2ToolBarSpecialTableButton.Create(false,FCKLang.InsertColumnBefore,"TableInsertColumnBefore");
	E2ToolBarSpecialTableButton.Create(false,FCKLang.InsertColumnAfter,"TableInsertColumnAfter");
	E2ToolBarSpecialTableButton.Create(false,FCKLang.DeleteRows,"TableDeleteRows");
	E2ToolBarSpecialTableButton.Create(false,FCKLang.DeleteColumns,"TableDeleteColumns");
	E2ToolBarSpecialTableButton.Create(false,FCKLang.TableDelete,"TableDelete");
};
FCKInsertTableCommand.prototype._CreateChooseTable = function( targetDocument, targetDiv )
{
	var oTable = targetDiv.appendChild( targetDocument.createElement( "TABLE" ) ) ;
	oTable.className = 'ForceBaseFont' ;		// Firefox 1.5 Bug.
	oTable.id="E2TableCreatebutton";
	oTable.style.tableLayout = 'fixed' ;
	oTable.cellPadding = 0 ;
	oTable.cellSpacing = 0 ;
	oTable.border = 0 ;
	oTable.width = 150 ;
	
	function CreateSelectionDiv(i,j)// 里面小的box-div
	{
		var oDiv = targetDocument.createElement( "DIV" ) ;
		oDiv.className = 'ColorDeselected' ;
		FCKTools.AddEventListenerEx( oDiv, 'mouseover', FCKInsertTableCommand_OnMouseOver ) ;
		FCKTools.AddEventListenerEx( oDiv, 'mouseout', FCKInsertTableCommand_OnMouseOut ) ;
		oDiv.id=i+"_"+j+"_E2InsertTableButtonBox";
		return oDiv ;
	}
	
	
	var oCell = oTable.insertRow(-1).insertCell(-1) ;
	oCell.colSpan = 10 ;
	oCell.id="E2InsertTableButtonBoxTitle";
	oCell.innerHTML = FCKLang.E2Insert+FCKLang.E2Table;

	for(var i=0;i<10;i++){
		var oRow = oTable.insertRow(-1) ;
		for(var j=0;j<10;j++){
			oDiv = oRow.insertCell(-1).appendChild( CreateSelectionDiv(i,j) ) ;
			oDiv.innerHTML = '<div class="E2TableBoxBorder"><div class="E2TableBox"></div></div>' ;
			FCKTools.AddEventListenerEx( oDiv, 'click', FCKInsertTableCommand_OnClick ,this) ;
			
		}
	}
	FCKTools.DisableSelection(targetDocument.body);
};
FCKInsertTableCommand.prototype.GetState = function()
{
	if ( FCK.EditMode != FCK_EDITMODE_WYSIWYG )
		return FCK_TRISTATE_DISABLED ;
	
	return FCK_TRISTATE_OFF ;
};

function HiddenButton(){
	var node=null;
	if(FCKBrowserInfo.IsIE && !FCKBrowserInfo.IsGeckoLike){
		node = FCKSelection.MoveToAncestorNode("A");
	}else{
		// IE11 下测试没有选中元素时的情况
		var selection = FCKSelection.GetSelection();
		if (selection){
			node = selection.focusNode;
			node=getAncestorNode(node,"A");
		}
	}
		
	
	if(node){
		_herf=node.href;
		if((result = top.site.resolve(_herf))!=null){
			if(result.type=='attach'||result.type=='cachable'||result.type=='file'){
				FCKToolbarItems.GetItem("E2Page").Disable();
				FCKToolbarItems.GetItem("E2Link").Disable();
				return
			}else if(result.type=='view'||result.type=='edit'){
				FCKToolbarItems.GetItem("E2Page").Enable();
				FCKToolbarItems.GetItem("E2Link").Disable();
				return
			}else{
				FCKToolbarItems.GetItem("E2Page").Disable();
				FCKToolbarItems.GetItem("E2Link").Enable();
				return
			}
		}else{
			FCKToolbarItems.GetItem("E2Page").Disable();
			FCKToolbarItems.GetItem("E2Link").Enable();
			return
		}
		
		
	}
	
}


var E2SourceCommand = function () {
	this.Name = "E2Source";
};
E2SourceCommand.prototype.Execute = function () {
		var A = FCKConfig.ScreenWidth * 0.65;
		var B = FCKConfig.ScreenHeight * 0.65;
		FCKDialog.OpenDialog("FCKDialog_Source", FCKLang.Source, "dialog/fck_source.html", A, B, null, null, true);
};
E2SourceCommand.prototype.GetState = function () {
	return (FCK.EditMode == 0 ? 0 : 1);
};

// ### Resource Dialog Box Command.
var E2ResourceDialogCommand = function( ) {
};

E2ResourceDialogCommand.prototype.Execute = function()
{
	FCK.Selection.Save();
	parent.EditorModal.getResourceModal().open();
};

E2ResourceDialogCommand.prototype.GetState = function()
{
	return 0 ;
};


// ### Image Dialog Box Command.
var E2ImageDialogCommand = function( ) {
};

E2ImageDialogCommand.prototype.Execute = function()
{
	FCK.Selection.Save();
	parent.EditorModal.getImageModal().open();
};

E2ImageDialogCommand.prototype.GetState = function()
{
	return 0 ;
};