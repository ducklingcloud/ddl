
/**
 * diyanliang@cnic.cn 
 * 
 * 2011-5-5
 */
var FCKAutoGrow_Min = window.frameElement.offsetHeight ;
var AutoGrow={
	"safariType":0,
	"FCKAutoGrow_Min":null,
	"_SetupListener":function(){
		if(FCKBrowserInfo.IsSafari){
			return;
		}
		
		AutoGrow.FCKAutoGrow_Min = window.frameElement.offsetHeight ;
		FCKTools.AddEventListener(FCK.EditorWindow, 'scroll', AutoGrow.FCKAutoGrow_Check, false);
		FCKTools.AddEventListener(FCK.EditorDocument, 'keydown', AutoGrow.FCKAutoGrow_Check, false);

		//fix ie iframe eat context bug 
		if ( FCKBrowserInfo.IsIE ){
			var m_toolbarblock=(FCK.ToolbarSet._TargetElement).ownerDocument.getElementById("toolbarblock");
			FCKDomTools.SetElementStyles( m_toolbarblock,
			{
				height	:100+'px'
			} ) ;
		}
	},
	"FCKAutoGrow_Check":function(){
		if(FCKBrowserInfo.IsIE11 || FCKBrowserInfo.IsIE10 || FCKBrowserInfo.IsIE9 || FCKBrowserInfo.IsSafari){
			return;
		}
		var oInnerDoc = FCK.EditorDocument ;
		
		var oif = FCK.EditorWindow.frameElement;
		
		if (oif.style.height) { 
			
			try{
				oif.style.height = "100%";
			}catch(e){
				oif.height = "100%";
			}
		} 
		else {
			oif.height = "100%";
		} 
		
		otd=oif.parentNode;
		
		var iInnerHeight;
		if ( FCKBrowserInfo.IsIE )
		{
			iInnerHeight = oInnerDoc.body.scrollHeight ;
		}
		else
		{
			iInnerHeight = oInnerDoc.body.offsetHeight ;//里面内容实际高度
		}
		if(iInnerHeight<AutoGrow.FCKAutoGrow_Min){
			iInnerHeight=AutoGrow.FCKAutoGrow_Min;
		}
		var iMainFrameSize =iInnerHeight;

	
		var toolbar=(FCK.ToolbarSet._TargetElement).ownerDocument.getElementById("floattoolbar");
		m_height=toolbar.offsetHeight;//工具栏的高度
		
			iMainFrameSize =iInnerHeight+m_height+50;
		
		iMainFrameSize=	Math.max( iMainFrameSize, FCKAutoGrow_Min );
		var of=window.frameElement;
		if (of.style.height) { 
			try{
				of.style.height = iMainFrameSize+"px"; 
			}catch(e){
				of.height = iMainFrameSize+"px"; 
			}
		} 
		else {
			of.height = iMainFrameSize ;
		} 
	}
};
FCK.AttachToOnSelectionChange(AutoGrow.FCKAutoGrow_Check ) ;
FCK.Events.AttachEvent( 'OnAfterSetHTML', AutoGrow._SetupListener ) ; 
FCK.Events.AttachEvent( 'OnAfterSetHTML', AutoGrow.FCKAutoGrow_Check ) ; 