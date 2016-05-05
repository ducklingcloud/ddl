var dialog	= window.parent ;
var oEditor = dialog.InnerDialogLoaded() ;

var FCK			= oEditor.FCK ;
var FCKLang		= oEditor.FCKLang ;
var FCKConfig	= oEditor.FCKConfig ;
var FCKRegexLib	= oEditor.FCKRegexLib ;
var FCKTools	= oEditor.FCKTools ;

var attachurl;
var oLink = dialog.Selection.GetSelection().MoveToAncestorNode( 'A' ) ;
if ( oLink )
	FCK.Selection.SelectNode( oLink ) 
window.onload = function()
{
	oEditor.FCKLanguageManager.TranslatePage(document) ;
	dialog.document.getElementById("PopupButtons").style.display="none";
	LoadSelectionLink();
	loadUpdateFile();
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
		sHRef = oLink.getAttribute( 'href' , 2 ) || '' ;
	
	var sProtocol = oRegex.UriProtocol.exec( sHRef ) ;
	if ( sProtocol ){//是连接
		alert("选中的不是附件")
	}else {//是附件和页面
		if(sHRef.indexOf("attach/")!=-1){
				attachurl=sHRef;
				document.getElementById("attachurl").value=attachurl;
				
		}else{
			alert("选中的不是附件")
		}
	}
}


function FlexCancel(){
	window.parent.CloseDialog();
	//return true;
}

//为了附件上传功能做的addLink
function addLink(fileUrl, url) {
	sUri="attach/"+url;
	var aLinks = oLink ? [ oLink ] : oEditor.FCK.CreateLink( sUri, true ) ;
	//如果没有区被选中或者没有链接地址，我们用url做文本显示
	var aHasSelection = ( aLinks.length > 0 ) ;
	if (!aHasSelection ){
		sInnerHtml = sUri;
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
}
 

function changeCookie() {	
	if (!GetE('signature').checked) {
		setCookie('signature', 'off');
	}
	else {
		setCookie('signature', 'on');
	}
}

function refreshCookie() {
	var sig = getCookie('signature', '');

	if ( (sig) && (sig == 'on')) {
		GetE('signature').checked = true;
	}
	else {
		GetE('signature').checked = false;
	}

}

function setCookie(name, value)
{
	  var Days = 365; 
	  var exp  = new Date();    //new Date("December 31, 9998");
	  exp.setTime(exp.getTime() + Days*24*60*60*1000);
	  document.cookie = name + "="+ escape(value) +";expires="+ exp.toGMTString();
}
function getCookie(name)
{
	  var arr = document.cookie.match(new RegExp("(^| )"+name+"=([^;]*)(;|$)"));
	  if(arr != null) return unescape(arr[2]); return null;
}
function delCookie(name)
{
	  var exp = new Date();
	  exp.setTime(exp.getTime() - 1);
	  var cval=getCookie(name);
	  if(cval!=null) document.cookie=name +"="+cval+";expires="+exp.toGMTString();
}

 
 
//Flex控件点击确定后调用
function OnUploadCompleted( errorNumber, fileUrl, fileName, customMsg, url ){
	switch ( errorNumber ){
		case "0" :	// No errors
			uploadFileName = fileName;			
			break ;
		case "1" :	// Custom error
			alert( customMsg ) ;
			return ;
		case "101" :	// Custom warning
			alert( customMsg ) ;
			break ;
		case "201" :
			alert( 'A file with the same name is already available. The uploaded file has been renamed to "' + fileName + '"' ) ;
			break ;
		case "202" :
			alert( 'Invalid file type' ) ;
			break ;
		default :
			alert(customMsg);			
	}
	
	if (uploadFileName && uploadFileName!=""){	
		addLink(fileUrl, fileUrl);	
		window.parent.CloseDialog();
	}	
} 