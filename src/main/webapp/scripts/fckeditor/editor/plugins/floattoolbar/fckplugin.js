/**
 * diyanliang@cnic.cn 
 * 
 * 2011-4-14
 */

var FloatToolBar={
	"s_iframe_width":null,
	"_SetupOnScrollListener":function(){
		var of=FCK.EditorWindow.frameElement;
		FloatToolBar.s_iframe_width=of.offsetWidth;
		var toolbar=(FCK.ToolbarSet._TargetElement).ownerDocument.getElementById("floattoolbar");
		FCKDomTools.SetElementStyles( toolbar,
				{
					width 			: FloatToolBar.s_iframe_width+'px',
					position		: 'absolute',
					zIndex          : '99',
					top				: '0px'
				} ) ;
		var po=window.top;
		
		FCKTools.AddEventListener(po, 'scroll', FloatToolBar.ChangeToolBarPos);
		FCKTools.AddEventListener(FCK.EditorDocument, 'keyup', FloatToolBar.ChangeToolBarPos);
		FCKTools.AddEventListener(FCK.EditorDocument, 'click', FloatToolBar.ChangeToolBarPos);
		if ( FCKBrowserInfo.IsIE ){
			FCKTools.AddEventListener(FCK.EditorWindow, 'scroll', FloatToolBar.ChangeToolBarPos);
		}
		
		var of=FCK.EditorWindow.frameElement;
		m_height=toolbar.offsetHeight;
		FCKDomTools.SetElementStyles( of,
		{			
				width:FloatToolBar.s_iframe_width-2+'px',
				marginRight: '1px',
				marginLeft: '1px',
				position : 'absolute',
				top	: m_height+'px',
				border: '1px solid #696969',
				height:"87%"
		} ) ;
		FCKDomTools.SetElementStyles( document.getElementById("xEditingArea"),
		{			
				border:'none'
		} ) ;
		
	}
	,"_GetIframeOffset" : function ()
	{
		return FCKTools.GetDocumentPosition( window, FCK.EditingArea.IFrame ) ;
	},
	'para' : {
		browser : function () {
			if (FCKBrowserInfo.IsSafari) {
				return 'webkit';
			}
			if (FCKBrowserInfo.IsGecko) {
				return 'moz';	// This is not necessarily correct
			}
			if (FCKBrowserInfo.IsOpera) {
				return 'o';
			}
			if (FCKBrowserInfo.IsIE && !FCKBrowserInfo.IsIE7 && !FCKBrowserInfo.IsIE6) {
				return 'ie8plus';
			}
			if (FCKBrowserInfo.IsIE7) {
				return 'ie7';
			}
			
		},
		scrTop : function () {
			if (FCKBrowserInfo.IsSafari) {
				return window.top.pageYOffset;
			}
			else {
				return window.top.document.documentElement.scrollTop;
			}
		},
		fixStartRef : {
				webkit : 124,
				moz : 121,
				ie8plus : 123,
				ie7 : 132,
				o : 118
		},
		fixOffset : {
				webkit: 2,
				moz : 3,
				ie8plus : 0,
				ie7 : -1,
				o : -3
		}
	},
	
	"ChangeToolBarPos":function(){
		var toolbar=(FCK.ToolbarSet._TargetElement).ownerDocument.getElementById("floattoolbar");
		
		var browser = FloatToolBar.para.browser();
		
		if (FloatToolBar.para.scrTop(browser) >= FloatToolBar.para.fixStartRef[browser]) {
			FCKDomTools.SetElementStyles(toolbar, {
				top: FloatToolBar.para.fixOffset[browser] + (FloatToolBar.para.scrTop(browser) - FloatToolBar.para.fixStartRef[browser]) -38,
				zIndex: '9999'
				//width: toolbar.offsetWidth
			});
		}
		else {
			FCKDomTools.SetElementStyles(toolbar, {
				position: 'absolute',
				top: '0'
			});
		}
	}
};


function getIEOffSetTop(e){
    var offset=e.offsetTop;
    if(e.offsetParent){
    	offset+=getIEOffSetTop(e.offsetParent);
    	
    } 
    return offset;
}

FCK.Events.AttachEvent( 'OnAfterSetHTML', FloatToolBar._SetupOnScrollListener ) ; 