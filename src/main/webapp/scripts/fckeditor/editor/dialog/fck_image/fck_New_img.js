/*
 * FCKeditor - The text editor for Internet - http://www.fckeditor.net
 * Copyright (C) 2003-2007 Frederico Caldeira Knabben
 *
 * == BEGIN LICENSE ==
 *
 * Created by morrise 20080605
 *
 * Scripts related to the Upload dialog window (see fck_select.html).
 */
 
//add by diyanliang 
// Get the selected image (if available).
var dialog		= window.parent ;
var oImage = dialog.Selection.GetSelectedElement() ;
var oEditor = dialog.InnerDialogLoaded() ;
var FCK			= oEditor.FCK ;
var FCKLang		= oEditor.FCKLang ;
var FCKConfig	= oEditor.FCKConfig ;
var FCKRegexLib	= oEditor.FCKRegexLib ;
var FCKTools	= oEditor.FCKTools ;
var uploadFileName ;


dialog.AddTab( 'Upload', FCKLang.DlgLnkUpload ) ;
dialog.AddTab( 'Link',FCKLang.DlgImgLinkTab ) ;


if ( oImage && oImage.tagName != 'IMG' && !( oImage.tagName == 'INPUT' && oImage.type == 'image' ) )
	oImage = null ;
function LoadSelection()
{
if ( ! oImage ) return;
dialog.SetSelectedTab( 'Link' ) ;
var iWidth, iHeight ;
var regexSize = /^\s*(\d+)px\s*$/i ;
if ( oImage.style.width )
	{
		var aMatchW  = oImage.style.width.match( regexSize ) ;
		if ( aMatchW )
		{
			iWidth = aMatchW[1] ;
			oImage.style.width = '' ;
			SetAttribute( oImage, 'width' , iWidth ) ;
		}
	}

	if ( oImage.style.height )
	{
		var aMatchH  = oImage.style.height.match( regexSize ) ;
		if ( aMatchH )
		{
			iHeight = aMatchH[1] ;
			oImage.style.height = '' ;
			SetAttribute( oImage, 'height', iHeight ) ;
		}
	}

	GetE('txtWidth').value	= iWidth ? iWidth : GetAttribute( oImage, "width", '' ) ;
	GetE('txtHeight').value	= iHeight ? iHeight : GetAttribute( oImage, "height", '' ) ;
	if(!GetE('txtWidth').value)GetE('txtWidth').value=oImage.width;
	if(!GetE('txtHeight').value)GetE('txtHeight').value=oImage.height;
	var tSrc=oImage.getAttribute( 'src' ) ;
	if(tSrc!=null)
		GetE('txtLnkUrl').value	= tSrc
}
//end 
var m_tabCode='Upload';
// Function called when a dialog tag is selected.
function OnDialogTabChange( tabCode )
{	m_tabCode=tabCode;
	ShowE('divUpload'	, ( tabCode == 'Upload' ) ) ;
	ShowE('divLink'		, ( tabCode == 'Link' ) ) ;
	dialog.SetAutoSize( true ) ;
}
//add by diyanliang
function SetLinkType( tabCode )
{	
	ShowE('divUpload'	, ( tabCode == 'Upload' ) ) ;
	ShowE('divLink'		, ( tabCode == 'Link' ) ) ;
	dialog.SetAutoSize( true ) ;
}

//#### Initialization Code

// oLink: The actual selected link in the editor.
var oLink = dialog.Selection.GetSelection().MoveToAncestorNode( 'A' ) ;
if ( oLink )
	FCK.Selection.SelectNode( oLink ) ;


window.onload = function()
{

	//add by diyanliang 08-12-19
	LoadSelection();
	//end 
	// Translate the dialog box texts.
	oEditor.FCKLanguageManager.TranslatePage(document) ;
	//edit by diyanliang08-12-19 
	if ( ! oImage )
		GetE('divUpload').style.display = '' ;

	refreshSignature();
	// Set the actual uploader URL.
	if ( FCKConfig.Upload )
		GetE('frmUpload').action = FCKConfig.UploadURL ;
	// Activate the "OK" button.
	dialog.SetOkButton( true ) ;
	dialog.SetAutoSize( true ) ;
}

//refresh checkbox for signature
function refreshSignature() {	
	refreshCookie();
	
			GetE('signature').checked = true;
}

//#### The OK button was hit.
function Ok() {
var isNotNum="yes";
if(isNaN(GetE('txtWidth').value)){
	isNotNum="no";
}
if(isNaN(GetE('txtHeight').value)){
	isNotNum="no";
}
if(isNotNum=="no"){
	alert("Width or Height is NaN");
	return false;
}
var type=m_tabCode
	if(type=="Upload")
	{
		return UPloadImg();
	}else{
//	alert(GetE('txtLnkUrl').value);
		addImg(GetE('txtLnkUrl').value);
		return true;
	}
}


function UPloadImg(){
var filename = GetE('txtUploadFile').value;
	filename = filename.substring(filename.lastIndexOf('\\')+1);
	if (filename.length > 64){
		  alert(FCKLang.DlgUploadMaxLength);
		  GetE('txtUploadFile').focus();
 		  return false;
	}
	
	CheckUpload();
	return false;		
}
//#### The Cancel button was hit
function Cancel()
{
//	if (aLinks.length > 0) {
//		if ( oEditor.FCKBrowserInfo.IsIE ) {
//			oEditor.FCKUndo.Undo() ;
//		}
//		else
//			FCK.ExecuteNamedCommand( 'Undo' ) ;
//	}
	return true;
}

function OnUploadCompleted( errorNumber, fileUrl, fileName, customMsg,url ){
	switch ( errorNumber )
	{
		case 0 :	// No errors
			//alert( 'upload.success'.localize() ) ;
			//Modified by morrise 20080605
			//Handle the succeed proceeding
			//alert( customMsg ) ;
			uploadFileName = fileName;			
			break ;
		case 1 :	// Custom error
			alert( customMsg ) ;
			return ;
		case 101 :	// Custom warning
			alert( customMsg ) ;
			break ;
		case 201 :
			alert( 'A file with the same name is already available. The uploaded file has been renamed to "' + fileName + '"' ) ;
			break ;
		case 202 :
			alert( 'Invalid file type' ) ;
			break ;
		default :
			//Modified by morrise 20080605
			//alert( 'Error on file upload. Error number: ' + errorNumber ) ;
			alert(customMsg);			
	}
	
	if (uploadFileName && uploadFileName!="") {		
		//addLink(fileUrl);
//		addLink(url);
//add by diyanliang for Upload img get src by relative url 2008-12-18
   // arr=url.split("attach");
//    alert("arr="+arr[1])
    //url="attach"+arr[1];
//end  
		addImg(url)
		window.parent.CloseDialog();
	}	
}

var oUploadAllowedExtRegex	= new RegExp( FCKConfig.UploadAllowedExtensions, 'i' ) ;
var oUploadDeniedExtRegex	= new RegExp( FCKConfig.UploadDeniedExtensions, 'i' ) ;

function CheckUpload()
{
	var sFile = GetE('txtUploadFile').value ;

	if ( sFile.length == 0 )
	{
		alert( FCKLang.DlgUploadNoSelect ) ;
		return false ;
	}

	if ( ( FCKConfig.UploadAllowedExtensions.length > 0 && !oUploadAllowedExtRegex.test( sFile ) ) ||
		( FCKConfig.UploadDeniedExtensions.length > 0 && oUploadDeniedExtRegex.test( sFile ) ) )
	{
		OnUploadCompleted( 202 ) ;
		return;
	}

	GetE('pageName').value = oEditor.parent.Wiki.PageName;
	
	if (GetE('signature').disabled) {
		GetE('signature').disabled = false;	
		document.frmUpload.submit();
		GetE('signature').disabled = true;
	}
	else
	{
		document.frmUpload.submit();
	}
	//return true ;
}


//add by diyanliang 
function addImg(fileUrl){
oImage = FCK.InsertElement( 'img' ) ;
oImage.src = fileUrl ;
SetAttribute( oImage, "_fcksavedurl",fileUrl ) ;
SetAttribute( oImage, "width" , GetE('txtWidth').value ) ;
SetAttribute( oImage, "height", GetE('txtHeight').value ) ;
}
function addLink(fileUrl) {
alert('in add link :'+fileUrl )
	//var originalText = oEditor.FCK.GetHTML(true);
	oEditor.FCKUndo.SaveUndoStep() ;
	
	var aLinks = oLink ? [ oLink, oEditor.FCK.InsertElement( 'span' ) ] : oEditor.FCK.CreateLink( ' haha ', true );
	var sig = document.createElement( 'span' );
	sig.innerHTML = fileUrl;
	aLinks.push ( sig );	
			
	var sUri, sInnerHtml ;

	sUri = uploadFileName ;
	
	// If no link is selected, create a new one (it may result in more than one link creation - #220).
	//aLinks = oLink ? [ oLink ] : oEditor.FCK.CreateLink( sUri, true ) ;

	// If no selection, no links are created, so use the uri as the link text (by dom, 2006-05-26)
	var aHasSelection = ( aLinks.length > 1 ) ;
	if ( !aHasSelection )
	{
		sInnerHtml = sUri;

		// Built a better text for empty links.
		var oLinkPathRegEx = new RegExp("//?([^?\"']+)([?].*)?$") ;
		var asLinkPath = oLinkPathRegEx.exec( sUri ) ;
		if (asLinkPath != null)
			sInnerHtml = asLinkPath[1];  // use matched path
			
		// Create a new (empty) anchor.
		aLinks = [ oEditor.FCK.InsertElement( 'img' ),  oEditor.FCK.InsertElement( 'span' )] ;
		
	}

	for ( var i = 0 ; i < 1 ; i++ )
	{
		oLink = aLinks[i] ;

		if ( aHasSelection )
			sInnerHtml = oLink.innerHTML ;		// Save the innerHTML (IE changes it if it is like an URL).

		oLink.href = sUri ;
		SetAttribute( oLink, '_fcksavedurl', sUri ) ;
	
		oLink.innerHTML = sInnerHtml ;		// Set (or restore) the innerHTML
		
		// Target
		SetAttribute( oLink, 'target', null ) ;

		// Let's set the "id" only for the first link to avoid duplication.
		//if ( i == 0 )
		//	SetAttribute( oLink, 'id', "" ) ;
		
	}	
	
	if (fileUrl && fileUrl.length > 0) {
		aLinks[1].innerHTML = fileUrl;
//		alert('Alinks 1' + aLinks[1].outerHTML);
//		alert('Alinks 0' + aLinks[0].outerHTML);
//		alert(aLinks[0].parentNode.outerHTML);
//		alert(aLinks[0].nextSibling.outerHTML);
		if ( oEditor.FCKBrowserInfo.IsIE )
			aLinks[0].appendChild(aLinks[1]);
		else
			aLinks[0].parentNode.insertBefore(aLinks[1], aLinks[0].nextSibling);
	}
	
	// Select the (first) link.
	oEditor.FCKSelection.SelectNode( aLinks[0] );
	
	return true;
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

function CheckImg(obj){
	if(obj.value.length>0){ 
		var af="jpg,gif,png,jpeg,bmp"; 
		if(eval("with(obj.value)if(!/"+af.split(",").join("|")+"/ig.test(substring(lastIndexOf('.')+1,length)))1;")){
			alert(FCKLang.ImgWarning);
			obj.value="";
			//obj.createTextRange().execCommand('delete')
		}; 
	}
} 
