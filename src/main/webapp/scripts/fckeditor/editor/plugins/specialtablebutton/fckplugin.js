/*
 * add by diyanliang 2011-6-22
 */

var E2ToolBarSpecialTableButton={
		"_panel":null,
		"_functionName":null,
		"_panelTable":null,
		"Arr":[],
		"allsub":[],
		"Init":function(_Panel){
			this._panel=_Panel;
			targetDocument=this._panel.Document; 
			targetDiv=this._panel.MainNode;
			var oTable = targetDiv.appendChild( targetDocument.createElement( "TABLE" ) ) ;
			oTable.className = 'ForceBaseFont' ;		
			oTable.id="E2TableCreatebutton";
			oTable.style.tableLayout = 'fixed' ;
			oTable.cellPadding = 3 ;
			oTable.cellSpacing = 0 ;
			oTable.border = 0 ;
			oTable.width = 90 ;
			this._panelTable=oTable;
		},
		"Create":function(HasSubMenu,displayinfo,functionname,IsDisabled,SubMenuElement){
			
			this._functionName=functionname;
			PanelTable=this._panelTable;
			var B = HasSubMenu;
			var C = FCKTools.GetElementDocument(PanelTable);
			var row = this.MainElement = PanelTable.insertRow(-1);
			row.className = !IsDisabled ? 'E2ToolBarAddSubButton_Disabled' : 'MN_Item';
			
			var arguments=new Array();
			var m_Button={
					"_obj":this,
					"_m_functionName":functionname,
					"_m_styleelement":row
			};
			this.Arr[functionname]=m_Button;
			arguments[0]=m_Button;
			
				FCKTools.AddEventListenerEx(row, 'mouseover', E2ToolBarSpecialTableButton_OnMouseOver,arguments);
				if (!B)FCKTools.AddEventListenerEx(row, 'mouseout', E2ToolBarSpecialTableButton_OnMouseOut,arguments);
				FCKTools.AddEventListenerEx(row, 'click',E2ToolBarSpecialTableButton_OnClick, arguments);
			
			
			D = row.insertCell(-1);
			D.className = 'SpecialTableButton_Label';
			D.noWrap = true;
			D.appendChild(C.createTextNode(displayinfo));
			D = row.insertCell(-1);
			D.width="10%";
			if (B) {
				D.className = 'MN_Arrow';
				var E = D.appendChild(C.createElement('IMG'));
				E.src = FCK_IMAGES_PATH + 'arrow_' + FCKLang.Dir + '.gif';
				E.width = 4;
				E.height = 7;
				this.allsub[this.allsub.length]=m_Button.SubMenu=new E2InsertTableButton(this._panel);
			}
		},
		"Activate" :function(obj) {
			obj._m_styleelement.className = 'MN_Item_Over';
			if(obj.SubMenu){
				obj.SubMenu.Show(obj._m_styleelement.offsetWidth + 2, -2,obj._m_styleelement,obj._obj._panel);
			}
		},
		"Deactivate":function(obj) {
			obj._m_styleelement.className = 'MN_Item';
			
		},
		"DisabledButton":function(){
			for(var obj in this.Arr){
				if(obj!="E2Table"){
					if(!FCKSelection.HasAncestorNode("TD")){
						this.Arr[obj].Disabled="Disabled";
						
					}
					else{
						this.Arr[obj].Disabled=null;
					}
				}
				try{
					this.Arr[obj]._m_styleelement.className = this.Arr[obj].Disabled ? 'MN_Item_Disabled' : 'MN_Item';
				}catch(e){
					
				}
				
			}
		}
		
};
function E2ToolBarSpecialTableButton_OnMouseOut(ev, obj) {
	for(var i=0;i<obj._obj.allsub.length;i++){
		obj._obj.allsub[i].Hide();
	}
	if(!obj.Disabled)
		obj._obj.Deactivate(obj);
	

};
function E2ToolBarSpecialTableButton_OnMouseOver(ev, obj) {
	for(var i=0;i<obj._obj.allsub.length;i++){
		obj._obj.allsub[i].Hide();
	}

	if(!obj.Disabled)
		obj._obj.Activate(obj);
	
	
		
};
function E2ToolBarSpecialTableButton_OnClick( ev, obj ){
	
	if (obj.HasSubMenu)
		obj._obj.Activate(obj);
	else {
		obj._obj.Deactivate(obj);
		if(FCK.ToolbarSet.CurrentInstance.Commands.GetCommand(obj._m_functionName))
			FCK.ToolbarSet.CurrentInstance.Commands.GetCommand(obj._m_functionName).Execute();
		else
			eval(obj._m_functionName);
			
		obj._obj._panel.Hide() ;
	}
}
E2InsertTableButton=function(fatherpanel){
	if ( FCKBrowserInfo.IsIE ){
		this.fatherpanel=fatherpanel;
		this._Panel = fatherpanel.CreateChildPanel() ;
		this._Panel.AppendStyleSheet(FCKConfig.SkinEditorCSS);
		this._Panel.MainNode.className = 'FCK_Panel' ;
		this._CreateChooseTable(this._Panel.Document, this._Panel.MainNode);
	}else{
		var oWindow ;
		if ( FCKBrowserInfo.IsIE )
			oWindow = window ;
		else if ( FCK.ToolbarSet._IFrame )
			oWindow = FCKTools.GetElementWindow( FCK.ToolbarSet._IFrame ) ;
		else
			oWindow = window.parent ;
		
		
		this._Panel = new FCKPanel( oWindow ) ;
		fatherpanel.childPanel=this;
		this._Panel.AppendStyleSheet( FCKConfig.SkinEditorCSS ) ;
		this._Panel.MainNode.className = 'FCK_Panel' ;
		this._CreateChooseTable(this._Panel.Document, this._Panel.MainNode);
	}
};
E2InsertTableButton.prototype.Show = function( x, y, relElement)
{	
	
	if ( FCKBrowserInfo.IsGecko || FCKBrowserInfo.IsGeckoLike){
		var fpanel=this._Panel;
		var A=relElement;
		var E = fpanel.MainNode;
		if (fpanel._Popup) {
			fpanel.Show(x, y, relElement);
		} else {
			var F = FCKTools.GetDocumentPosition(fpanel._Window,
					A.nodeType == 9 ? (FCKTools.IsStrictMode(A) ? A.documentElement
							: A.body) : A);
			var G = FCKDomTools.GetPositionedAncestor(fpanel._IFrame.parentNode);
			if (G) {
				var H = FCKTools.GetDocumentPosition(FCKTools.GetElementWindow(G),
						G);
				F.x -= H.x;
				F.y -= H.y;
			}
			;
			if (fpanel.IsRTL && !fpanel.IsContextMenu)
				x = (x * -1);
			x += F.x;
			y += F.y;
			if (fpanel.IsRTL) {
				if (fpanel.IsContextMenu)
					x = x - D + 1;
				else if (A)
					x = x + A.offsetWidth - D;
			} else {
				var I = FCKTools.GetViewPaneSize(fpanel._Window);
				var J = FCKTools.GetScrollPosition(fpanel._Window);
				var K = I.Height + J.Y;
				var L = I.Width + J.X;
				if ((x + D) > L)
					x -= x + D - L;
				if ((y + E.offsetHeight) > K)
					y -= y + E.offsetHeight - K;
			}
			;
		
			FCKDomTools.SetElementStyles(fpanel._IFrame, {
				left : x + 'px',
				top : y + 'px'
			});
			
			var M = fpanel;
			var N = E.offsetWidth || E.firstChild.offsetWidth;
			var O = E.offsetHeight;
			M._IFrame.style.width = N + 'px';
			M._IFrame.style.height = O + 'px';
			FCKTools.RunFunction(fpanel.OnShow, fpanel);
		}
	}else{
		for(var i=0;i<=9;i++){
			for(var j=0;j<=9;j++){
				this._Panel._Popup.document.getElementById(i+"_"+j+"_E2InsertTableButtonBox").className = 'ColorDeselected';
			}
		}
		this._Panel.Show( x, y, relElement ) ;
	}
};

E2InsertTableButton.prototype.Hide = function( )
{
	
		if (this._Panel._Popup){
			this._Panel.Hide();
			E=this.fatherpanel.MainNode;
			FCKDomTools.SetElementStyles(E, {
				width : 100 + 'px' 
			});
		}
		else{
			fpanel=this._Panel;
			if(this.todo){
				setTimeout(function(){
					FCKDomTools.SetElementStyles(fpanel._IFrame, {
						width : 0 + 'px',
						height : 0+ 'px'
					});
				}, 1000);
				this.todo=false;
			}else{
				FCKDomTools.SetElementStyles(fpanel._IFrame, {
					width : 0 + 'px',
					height : 0+ 'px'
				});
			}
			
		}

};
E2InsertTableButton.prototype._CreateChooseTable = function( targetDocument, targetDiv )
{
	var oTable = targetDiv.appendChild( targetDocument.createElement( "TABLE" ) ) ;
	oTable.className = 'ForceBaseFont' ;		// Firefox 1.5 Bug.
	oTable.id="E2TableCreatebutton";
	oTable.style.tableLayout = 'fixed' ;
	oTable.cellPadding = 0 ;
	oTable.cellSpacing = 0 ;
	oTable.border = 0 ;
	oTable.width = 150 ;
	
	function CreateSelectionDiv(i,j)//里面小的box-div
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

function FCKInsertTableCommand_OnMouseOver()
{
	
	var arrid=this.id.split("_");
	var _row=arrid[0];
    var _col=arrid[1];
	for(var i=0;i<=_row;i++){
		for(var j=0;j<=_col;j++){
			this.ownerDocument.getElementById(i+"_"+j+"_E2InsertTableButtonBox").className = 'ColorSelected';
		}
	}
	this.ownerDocument.getElementById("E2InsertTableButtonBoxTitle").innerHTML=(parseInt(_row)+1)+"x"+(parseInt(_col)+1)+FCKLang.E2Table;
}

function FCKInsertTableCommand_OnMouseOut()
{
	var arrid=this.id.split("_");
	var _row=arrid[0];
    var _col=arrid[1];
	for(var i=0;i<=_row;i++){
		for(var j=0;j<=_col;j++){
			this.ownerDocument.getElementById(i+"_"+j+"_E2InsertTableButtonBox").className = 'ColorDeselected';
		}
	}
	this.ownerDocument.getElementById("E2InsertTableButtonBoxTitle").innerHTML= FCKLang.E2Insert+FCKLang.E2Table;
}
function FCKInsertTableCommand_OnClick( ev, command )
{
	var arrid=this.id.split("_");
	var _row=arrid[0];
    var _col=arrid[1];
	for(var i=0;i<=_row;i++){
		for(var j=0;j<=_col;j++){
			this.ownerDocument.getElementById(i+"_"+j+"_E2InsertTableButtonBox").className = 'ColorDeselected';
		}
	}
	this.ownerDocument.getElementById("E2InsertTableButtonBoxTitle").innerHTML= FCKLang.E2Insert+FCKLang.E2Table;
	command.Hide() ;
	
	
	table = FCK.EditorDocument.createElement( "TABLE" ) ;
	
	SetAttribute( table, 'width'		, "80%" ) ;
	SetAttribute( table, 'border'		, 1 ) ;
	SetAttribute( table, 'cellPadding'	, 0) ;
	SetAttribute( table, 'cellSpacing'	, 0 ) ;

	var iRows = parseInt(_row)+1;
	var iCols = parseInt(_col)+1;

	for ( var r = 0 ; r < iRows ; r++ )
	{
		var oRow = table.insertRow(-1) ;
		for ( var c = 0 ; c < iCols ; c++ )
		{
	
			var oCell = oRow.insertCell(-1) ;
			if ( FCKBrowserInfo.IsGeckoLike )
				FCKTools.AppendBogusBr( oCell ) ;
		}
	}

	FCKUndo.SaveUndoStep() ;
	FCK.InsertElement( table ) ;
	FCK.Events.FireEvent( 'OnSelectionChange' ) ;
}
