/**
 * diyanliang@cnic.cn 
 * 
 * 2011-4-14
 */

var AddTableRow = {

	"_AddRowBar" : null,
	"_AddColBar" : null,
	"_doc" : document,
	"_m_table" : null,
	"BLOCK_HEIGHT" : 30,
	"_ShowAddRowBar" : function(w, table) {

		if (this._AddRowBar == null) {
			this._AddRowBar = this._doc.createElement("div");
			var paddingBar = this._AddRowBar;
			var paddingStyles = {
				'position' : 'absolute',
				'cursor' : 'pointer'
			};
			FCKDomTools.SetElementStyles(paddingBar, paddingStyles);
			this._avoidStyles(paddingBar);
			paddingBar.setAttribute('_fcktemp', true);
			this._doc.body.appendChild(paddingBar);
			FCKTools.AddEventListener(paddingBar, "mouseup",this._AddRowBarMouseDownListener);
		}

		var paddingBar = this._AddRowBar;
		var offset = this._GetIframeOffset();
		var tablePos = this._GetTablePosition(w, table);
		var barTop = offset.y + tablePos.y + table.clientHeight + 4;
		var barleft = tablePos.x + (table.clientWidth - 36) / 2;

		var paddingStyles = {
			'backgroundImage' : 'url("' + FCKConfig.BasePath
					+ 'images/addrow.jpg' + '")',
			'backgroundRepeat' : "no-repeat",
			'top' : barTop + 'px',
			'height' : 20 + 'px',
			'width' : 50 + 'px',
			'left' : barleft + 'px'
		};

		FCKDomTools.SetElementStyles(paddingBar, paddingStyles);
		var visibleBar = null;
		if (paddingBar.getElementsByTagName("div").length < 1) {
			visibleBar = this._doc.createElement("div");
			this._avoidStyles(visibleBar);
			visibleBar.setAttribute('_fcktemp', true);
			paddingBar.appendChild(visibleBar);
		} else
			visibleBar = paddingBar.getElementsByTagName("div")[0];

		FCKDomTools.SetElementStyles(visibleBar, {
			position : 'absolute',
			height : 1 + 'px',
			width : 1 + 'px',
			left : '50px',
			top : '0px'
		});
	},
	"_HideAddRowBar" : function() {
		if (this._AddRowBar) {

			FCKDomTools.SetElementStyles(this._AddRowBar, {
				top : '-100000px',
				left : '-100000px'
			//								top		: '370px',
			//								left	: '370px'
			});
		}

	},
	"_ShowAddColBar" : function(w, table) {

		//alert("_ShowResizeBar")

		if (this._AddColBar == null) {
			this._AddColBar = this._doc.createElement("div");
			var paddingBar = this._AddColBar;
			var paddingStyles = {
				'position' : 'absolute',
				'cursor' : 'pointer'
			};
			FCKDomTools.SetElementStyles(paddingBar, paddingStyles);
			this._avoidStyles(paddingBar);
			paddingBar.setAttribute('_fcktemp', true);
			this._doc.body.appendChild(paddingBar);
			FCKTools.AddEventListener(paddingBar, "mouseup",
					this._AddColBarMouseDownListener);
			//				var filler = this._doc.createElement( "img" ) ;
			//				filler.setAttribute('_fcktemp', true);
			//				filler.border = 0 ;
			//				filler.src = FCKConfig.BasePath + "images/addcol.jpg" ;
			//				filler.style.position = "absolute" ;
			//				paddingBar.appendChild( filler ) ;

		}

		var paddingBar = this._AddColBar;
		var offset = this._GetIframeOffset();
		var tablePos = this._GetTablePosition(w, table);
		var barTop = offset.y + tablePos.y + (table.clientHeight - 36) / 2;
		var barleft = tablePos.x + (table.clientWidth + 4);

		var paddingStyles = {
			'backgroundImage' : 'url("' + FCKConfig.BasePath
					+ 'images/addcol.jpg' + '")',
			'backgroundRepeat' : "no-repeat",
			'top' : barTop + 'px',
			'height' : 50 + 'px',
			'width' : 20 + 'px',
			'left' : barleft + 'px'
		};

		FCKDomTools.SetElementStyles(paddingBar, paddingStyles);
		var visibleBar = null;
		if (paddingBar.getElementsByTagName("div").length < 1) {
			visibleBar = this._doc.createElement("div");
			this._avoidStyles(visibleBar);
			visibleBar.setAttribute('_fcktemp', true);
			paddingBar.appendChild(visibleBar);
		} else
			visibleBar = paddingBar.getElementsByTagName("div")[0];

		FCKDomTools.SetElementStyles(visibleBar, {

			position : 'absolute',
			height : 1 + 'px',
			width : 1 + 'px',
			left : '50px',
			top : '0px'
		});
	},
	"_HideAddColBar" : function() {
		if (this._AddColBar) {

			FCKDomTools.SetElementStyles(this._AddColBar, {
				top : '-100000px',
				left : '-100000px'
			});
		}

	},
	"MouseMoveListener" : function(FCK, evt) {
		return AddTableRow._MouseFindHandler(FCK, evt);
	},
	"_MouseFindHandler" : function(FCK, evt) {
		if (FCK.MouseDownFlag)
			return;
		var node = evt.srcElement || evt.target;
		try {
			if (!node || node.nodeType != 1) {
				this._HideAddRowBar();
				return;
			}
		} catch (e) {
			this._HideAddRowBar();
			return;
		}

		var mouseX = evt.clientX;
		var mouseY = evt.clientY;
		if (FCKTools.GetElementDocument(node) == document) {
			var offset = this._GetIframeOffset();
			mouseX -= offset.x;
			mouseY -= offset.y;
		}

		/**
		 * 确定当前操纵表格start
		 */
		var checktable = FCKTools.GetElementAscensor(node, "table");
		//检查当前光标在不在表格内
		if (!checktable) {
			if (this._m_table) {
				try {
					tablePos = FCKTools.GetWindowPosition(FCK.EditorWindow,
							this._m_table);
					tablebottom = tablePos.y + this._m_table.clientHeight;
					if (mouseY > (tablebottom + this.BLOCK_HEIGHT)) {

						this._m_table = null;
					}
				} catch (e) {
					this._m_table = null;
				}
			}
		} else {//当前光标在表格内
			this._m_table = checktable;
		}

		/**
		 * 确定当前操纵表格end
		 */

		/**
		 * 执行显示或者隐藏start
		 */

		if (this._m_table) {
			this._ShowAddRowBar(FCK.EditorWindow, this._m_table);
			this._ShowAddColBar(FCK.EditorWindow, this._m_table);
		} else {
			this._HideAddRowBar();
			this._HideAddColBar();
		}
		return;

	},
	"_AddRowBarMouseDownListener" : function() {
		FCKTableHandler.InsertLastRow(AddTableRow._m_table);
		AddTableRow._HideAddRowBar();
		AddTableRow._HideAddColBar();
		FCK.Events.FireEvent('OnSelectionChange');
		return;
	},
	"_AddColBarMouseDownListener" : function() {
		FCKTableHandler.InsertLastColumn(AddTableRow._m_table);
		AddTableRow._HideAddRowBar();
		AddTableRow._HideAddColBar();
		return;
	},
	"_GetTablePosition" : function(w, table) {
		return FCKTools.GetWindowPosition(w, table);
	},
	"_GetIframeOffset" : function() {
		return FCKTools.GetDocumentPosition(window, FCK.EditingArea.IFrame);
	},
	"_avoidStyles" : function(element) {
		FCKDomTools.SetElementStyles(element, {
			padding : '0',
			//					backgroundImage	: 'none',
			border : '0'
		});
	}
};

FCK.Events.AttachEvent("OnMouseMove", AddTableRow.MouseMoveListener);