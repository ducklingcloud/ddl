/*
 *Duckling Link
 *create by diyanliang 09-7-01
 */
String.prototype.trim = function() { 
	return this.replace(/(^\s*)|(\s*$)/g, ""); 
}
var dialog	= window.parent ;
var oEditor = dialog.InnerDialogLoaded() ;
var FCK			= oEditor.FCK ;
var FCKLang		= oEditor.FCKLang ;
var FCKConfig	= oEditor.FCKConfig ;
var FCKRegexLib	= oEditor.FCKRegexLib ;
var FCKTools	= oEditor.FCKTools ;
var oLink = dialog.Selection.GetSelection().MoveToAncestorNode( 'A' ) ;
var fckseleTxt;
if(oEditor.NodeBar._ieobj){
	oLink=oEditor.NodeBar._obj
	oEditor.NodeBar._ieobj=false;
}
if ( oLink )
	FCK.Selection.SelectNode( oLink ) ;

// Set the dialog tabs.


window.onload = function(){
	oEditor.FCKLanguageManager.TranslatePage(document) ;
	LoadSelection();
	LoadSelectionLink();
	dialog.SetAutoSize( true ) ;
	dialog.SetOkButton( true ) ;
	
}

//编辑链接时候取到内容处理
function LoadSelection(){
	dialog.SetAutoSize( true ) ;
	//得到选中的文字
	var strinner=getInner();
	//选中文字赋值
	if(strinner[0]=='boolean'){
		if(strinner[1]==false){
			GetE('DucklingLnkPageInner').disabled="disabled";
		
			fckseleTxt='false';
		}else if(strinner[1]==true){
			fckseleTxt='true';
			//nothing to do  but its importance
		
		}
	}else{
			GetE('DucklingLnkPageInner').value=strinner[1];
			fckseleTxt='@'+strinner[1];
	}
	
}
//得到选中的区域文字 如果选中的是多个元素 或者选中的元素中有多个元素  或者选中的元素第一个元素不是文本 返回false
function getInner(){
	//1先判断选中内容是不是只有一个元素
	//2在取选中内容的
	var inner=false;
	if (oEditor.FCK.EditorWindow.getSelection){
		inner = oEditor.FCK.EditorWindow.getSelection().toString();
	}else if (oEditor.FCK.EditorWindow.document.getSelection){
		inner = oEditor.FCK.EditorWindow.document.getSelection();
	}else if (oEditor.FCK.EditorWindow.document.selection){
		inner = oEditor.FCK.EditorWindow.document.selection.createRange().text;
	}
	if(!inner)return ["boolean",true];
	//查看选中文本中有没有换行
	if(inner.indexOf("\r\n")!=-1||inner.indexOf("\n")!=-1)return ["boolean",false];
	//if(oEditor.NodeBar._obj.nodeName=="IMG")return ["boolean",false];
	return ["text",inner];

}

//#### Regular Expressions library.
var oRegex = new Object() ;

oRegex.UriProtocol = /^(((http|https|ftp|baseurl):\/\/)|mailto:)/gi ;

oRegex.UrlOnChangeProtocol = /^(http|https|ftp|baseurl):\/\/(?=.)/gi ;

oRegex.UrlOnChangeTestOther = /^((javascript:)|[#\/\.])/gi ;

oRegex.ReserveTarget = /^_(blank|self|top|parent)$/i ;

oRegex.PopupUri = /^javascript:void\(\s*window.open\(\s*'([^']+)'\s*,\s*(?:'([^']*)'|null)\s*,\s*'([^']*)'\s*\)\s*\)\s*$/ ;

// Accessible popups
oRegex.OnClickPopup = /^\s*on[cC]lick="\s*window.open\(\s*this\.href\s*,\s*(?:'([^']*)'|null)\s*,\s*'([^']*)'\s*\)\s*;\s*return\s*false;*\s*"$/ ;

oRegex.PopupFeatures = /(?:^|,)([^=]+)=(\d+|yes|no)/gi ;


function LoadSelectionLink(){
	if ( !oLink ) return ;
	var sHRef = oLink.getAttribute( '_fcksavedurl' ) ;
	if ( sHRef == null )
		sHRef = oLink.href || '' ;


	if((_result = top.site.resolve(sHRef))!=null){
		
		_type=_result.type
		if(_type=="view"||_type=="edit"){
			
			GetE('newpagefalse').checked="checked";
			GetE('txtDucklingLnkPage').disabled="";
			GetE('txtDucklingNewPage').disabled="disabled";
			sHRef=_result.key
			
			GetE('ResourceId').value=sHRef
			dialog.SetAutoSize( true ) ;
			var target=oLink.getAttribute( 'target' );
			if(target&&target=="_blank"){
				GetE('DucklingLnkPageBlock').checked=true;
			}
			
			var title=oLink.getAttribute( 'title' );
			if(title){
				GetE('txtDucklingLnkPage').value=title
			}
		}
			
	}
	
	
	
	
}

function Ok(){
	oEditor.FCKUndo.SaveUndoStep() ;
	if(GetE('newpagetrue').checked){
		doNewPage()
	}else{
		return insertPageLink()
	}
	
}
function doNewPage(){
	sUri = GetE("txtDucklingNewPage").value;
	if (sUri) {
		sUri = sUri.trim();
	}
	if (!sUri || sUri.length == 0) {
		alert(FCKLang.DlgNewPageEmpty);
		return false;
	}
	if (sUri.length > 255) {
		alert(FCKLang.DlgNewPageMaxLength);
		return false;
	}
	var pageNameRule = /^([.a-zA-Z0-9]|[-_]|[^\x00-\xff]){1,255}$/;
    if (!pageNameRule.exec(sUri))
    {
      alert(FCKLang.DlgPageNameRule);
      return false  
    }
	oEditor.FCKUndo.SaveUndoStep();
	if (sUri.length == 0) {
		alert(FCKLang.DlnLnkMsgNoUrl);
		return false;
	}	
	//建立新的resourseid 10-3-5
	var ajaxurl=top.site.getTeamURL("createPage")+'?func=createInEdit&title='+encodeURI(sUri)+'&cid='+FCKConfig.DucklingCId;
//	var ajaxurl=FCKConfig.DucklingBaseHref+'team/createPage?func=createInEdit&title='+encodeURI(sUri)+'&ResourceId='+FCKConfig.DucklingResourceId;
//	var ajaxurl=oEditor.site.getTeamURL("createPage")+'?func=createInEdit&title='+encodeURI(sUri)+'&ResourceId='+FCKConfig.DucklingResourceId;
	sUri=send_request("post", ajaxurl, "", "text", newpagecallback); 
}

function newpagecallback(){
	   if (xmlHttp.readyState == 4) { // 判断对象状态
	       if (xmlHttp.status == 200) { // 信息已经成功返回，开始处理信息
	    	   		sUri=xmlHttp.responseText;
	    	   		
	    	   		//sUri=top.site.getURL("view",sUri)
					aLinks = oLink ? [oLink] : oEditor.FCK.CreateLink(sUri, true);
					var aHasSelection = (aLinks.length > 0);
					if (!aHasSelection) {
						sInnerHtml =  GetE("txtDucklingNewPage").value;
						var oLinkPathRegEx = new RegExp("//?([^?\"']+)([?].*)?$");
						var asLinkPath = oLinkPathRegEx.exec(sUri);
						if (asLinkPath != null) {
							sInnerHtml = asLinkPath[1];
						} 
						aLinks = [oEditor.FCK.InsertElement("a")];
					}
					sUri=top.site.getURL("view",sUri)
					for (var i = 0; i < aLinks.length; i++) {
						oLink = aLinks[i];
						if (aHasSelection) {
							sInnerHtml = oLink.innerHTML;
						}
						
						oLink.href = sUri;
						SetAttribute(oLink, "_fcksavedurl", sUri);
						oLink.innerHTML = sInnerHtml;		// Set (or restore) the innerHTML
						// Target
						SetAttribute(oLink, "target", "");
						SetAttribute(oLink, "title", GetE("txtDucklingNewPage").value);
						// Let's set the "id" only for the first link to avoid duplication.
						if (i == 0) {
							SetAttribute(oLink, "id", "");
							
						}
					}	
					//显示内容处理
					if(!GetE('DucklingLnkPageInner').disabled){
							if(GetE('DucklingLnkPageInner').value.length>0)
								oLink.innerHTML = GetE('DucklingLnkPageInner').value ;
					}
					oEditor.FCKSelection.SelectNode(aLinks[0]);
					window.parent.CloseDialog();
	       } else { //页面不正常
	             alert("quest error!");
	       }
	   }
	}

function insertPageLink(){
	sUri=GetE('ResourceId').value 
	
	if ( sUri.length == 0 ){
		alert( FCKLang.DlnLnkMsgNoPage ) ;
		return false ;
	}else{
		sUri=top.site.getURL("view",sUri)
	}
	
	//得到选中的内容
	var aLinks = oLink ? [ oLink ] : oEditor.FCK.CreateLink( sUri, true ) ;
	//如果没有区被选中或者没有链接地址，我们用url做文本显示
	var aHasSelection = ( aLinks.length > 0 ) ;
	if (!aHasSelection ){
		sInnerHtml = GetE('txtDucklingLnkPage').value;
		
		aLinks = [ oEditor.FCK.InsertElement( 'a' ) ] ;
		
	}
	
	for ( var i = 0 ; i < aLinks.length ; i++ ){
		oLink = aLinks[i] ;
		if ( aHasSelection )
			sInnerHtml = oLink.innerHTML ;	

		oLink.href = sUri ;
		SetAttribute( oLink, '_fcksavedurl', sUri ) ;
		oLink.innerHTML = sInnerHtml ;
	}
	if(GetE('DucklingLnkPageBlock').checked){
		SetAttribute( oLink, 'target', '_blank') ;
	}else{
		SetAttribute( oLink, 'target', null ) ;
	}
	
	
	var DucklingLnkUrlTooltips=GetE('txtDucklingLnkPage').value;
	if(DucklingLnkUrlTooltips.length>0){
		SetAttribute( oLink, 'title', DucklingLnkUrlTooltips) ;
	}else{
		SetAttribute( oLink, 'title', null) ;
	}
	
	if(!GetE('DucklingLnkPageInner').disabled){
		if(GetE('DucklingLnkPageInner').value.length>0)
			oLink.innerHTML = GetE('DucklingLnkPageInner').value ;
	}
	oEditor.FCKSelection.SelectNode( aLinks[0] );
	return true ;
}
/*
//点击确定以后
function Ok(){
	var sUri, sInnerHtml ;
	oEditor.FCKUndo.SaveUndoStep() ;
	//根据tag类型取录入框的地址
	if(m_tabCode == 'DucklingLnkUrl'){
		sUri = GetE('txtDucklingLnkUrl').value ;
		if ( sUri.length == 0 )
			{
				alert( FCKLang.DlnLnkMsgNoUrl ) ;
				return false ;
			}

		//sUri = GetE('cmbLinkProtocol').value + sUri ;
	}else if(m_tabCode == 'DucklingLnkPage'){
		
		sUri=GetE('ResourceId').value 
		
		if ( sUri.length == 0 ){
			alert( FCKLang.DlnLnkMsgNoUrl ) ;
			return false ;
		}else{
			sUri=top.site.getURL("view",sUri)
		}
		
		
//		sUri = GetE('txtDucklingLnkPage').value ;
//		if ( sUri.length == 0 )
//			{
//				alert( FCKLang.DlnLnkMsgNoUrl ) ;
//				return false ;
//			}
//		if (!sUri) {
//			sUri = sUri.trim();
//		}
//		if (!sUri || sUri.length == 0) {
//			alert(FCKLang.DlgNewPageEmpty);
//			return false;
//		}
//		if (sUri.length > 255) {
//			alert(FCKLang.DlgNewPageMaxLength);
//			return false;
//		}
//		if (sUri.length == 0) {
//			alert(FCKLang.DlnLnkMsgNoUrl);
//			return false;
//		}	
//		
//	  var pageNameRule = /^([.a-zA-Z0-9]|[-_]|[^\x00-\xff]){1,255}$/;
//	    if (!pageNameRule.exec(sUri))
//	    {
//	      alert(FCKLang.DlgPageNameRule);
//	     return false  
//	    }	
			
	}else if(m_tabCode == 'DucklingLnkEmail'){
		sUri = GetE('txtDucklingLnkEmail').value ;
		if ( sUri.length == 0 )
			{
				alert( FCKLang.DlnLnkMsgNoUrl ) ;
				return false ;
			}
		sUri =  'mailto:' + sUri ;
	}
	//得到选中的内容
	var aLinks = oLink ? [ oLink ] : oEditor.FCK.CreateLink( sUri, true ) ;
	//如果没有区被选中或者没有链接地址，我们用url做文本显示
	var aHasSelection = ( aLinks.length > 0 ) ;
	if (!aHasSelection ){
		sInnerHtml = GetE('txtDucklingLnkPage').value;
		if(m_tabCode == 'DucklingLnkUrl'){
			var oLinkPathRegEx = new RegExp("//?([^?\"']+)([?].*)?$") ;
			var asLinkPath = oLinkPathRegEx.exec( sUri ) ;
			if (asLinkPath != null)
				sInnerHtml = asLinkPath[1]; 
		}
		aLinks = [ oEditor.FCK.InsertElement( 'a' ) ] ;
		
	}
	
	for ( var i = 0 ; i < aLinks.length ; i++ ){
		oLink = aLinks[i] ;
		if ( aHasSelection )
			sInnerHtml = oLink.innerHTML ;	

		oLink.href = sUri ;
		SetAttribute( oLink, '_fcksavedurl', sUri ) ;
		oLink.innerHTML = sInnerHtml ;
	}
	//提示信息和弹出窗口的处理
	if(m_tabCode == 'DucklingLnkUrl'){
		if(GetE('DucklingLnkUrlBlock').checked){
			SetAttribute( oLink, 'target', '_blank') ;
		}else{
			SetAttribute( oLink, 'target', null ) ;
		}
		var DucklingLnkUrlTooltips=GetE('DucklingLnkUrlTooltips').value;
		if(DucklingLnkUrlTooltips.length>0){
			SetAttribute( oLink, 'title', DucklingLnkUrlTooltips) ;
		}else{
			SetAttribute( oLink, 'title', null) ;
		}
	}else if(m_tabCode == 'DucklingLnkPage'){
		if(GetE('DucklingLnkPageBlock').checked){
			SetAttribute( oLink, 'target', '_blank') ;
		}else{
			SetAttribute( oLink, 'target', null ) ;
		}
		var DucklingLnkUrlTooltips=GetE('txtDucklingLnkPage').value;
		if(DucklingLnkUrlTooltips.length>0){
			SetAttribute( oLink, 'title', DucklingLnkUrlTooltips) ;
		}else{
			SetAttribute( oLink, 'title', null) ;
		}
	}else if(m_tabCode == 'DucklingLnkEmail'){

		var DucklingLnkUrlTooltips=GetE('DucklingLnkEmailTooltips').value;
		if(DucklingLnkUrlTooltips.length>0){
			SetAttribute( oLink, 'title', DucklingLnkUrlTooltips) ;
		}else{
			SetAttribute( oLink, 'title', null) ;
		}
	}
	//显示内容处理
	if(m_tabCode == 'DucklingLnkUrl'){
		if(!GetE('DucklingLnkUrlInner').disabled){
			if(GetE('DucklingLnkUrlInner').value.length>0)
				oLink.innerHTML = GetE('DucklingLnkUrlInner').value ;
		}
	}else if(m_tabCode == 'DucklingLnkPage'){
		if(!GetE('DucklingLnkPageInner').disabled){
			if(GetE('DucklingLnkPageInner').value.length>0)
				oLink.innerHTML = GetE('DucklingLnkPageInner').value ;
		}
	}else if(m_tabCode == 'DucklingLnkEmail'){
		if(!GetE('DucklingLnkEmailInner').disabled){
			if(GetE('DucklingLnkEmailInner').value.length>0)
				oLink.innerHTML = GetE('DucklingLnkEmailInner').value ;
		}
	}
	oEditor.FCKSelection.SelectNode( aLinks[0] );
	return true ;
}
*/

$().ready(function() {
//$("#txtDucklingLnkPage").autocomplete(FCKConfig.DucklingBaseHref+'allResource',
$("#txtDucklingLnkPage").autocomplete(top.site.getTeamURL("allResource"),	
		{
			
			parse: function(data) { //|必选
				if(!data){
					return {};
				}
				return $.map(data, function(row) { 
					return {data: row, value: row.title,m_value:row.pid} //value是input框显示的结果,m_value是自定义内容 可以让后续方法用
				}); 
			}, 
			formatItem: function(row, i, max) {//显示效果|必选
				return "<td><div style=\"width:150;height:20px;overflow:hidden\">" + row.title+ "</div></td><td>" + row.pid +"</td>";
			},
			
			extraParams: {query:function(){return $('#txtDucklingLnkPage').val();},datatype:"json"},//ajax传参数|用ajax时候必选
			dataType: "json", //ajax数据结构只能是json|用ajax时候必选
			
			onSelected:onselectbackcall,//选择后处理方法 参数row
			autoFill: true,//自动填充
			scroll:true,//是否加滚动条
			scrollHeight:100,//有滚动条时候下拉框长度
			delay:10,//延迟10秒  
			max:100//下拉列表显示的数据长度
			
		}
	);
})

function onselectbackcall(row){
	document.getElementById("ResourceId").value=row.m_value;
}

function getResourceId(rid,rtitle){
	document.getElementById("ResourceId").value=rid;
//	document.getElementById("DucklingLnkPageTooltips").value=rtitle;
}
function IsNewPage(obj){

	if(obj.id=="newpagetrue"){
		document.getElementById("txtDucklingLnkPage").disabled="disabled";
		document.getElementById("txtDucklingNewPage").disabled="";
	}else{
		document.getElementById("txtDucklingLnkPage").disabled="";
		document.getElementById("txtDucklingNewPage").disabled="disabled";
	}
}
 