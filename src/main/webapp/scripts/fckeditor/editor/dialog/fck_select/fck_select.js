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

var dialog	= window.parent ;
var oEditor = dialog.InnerDialogLoaded() ;

var FCK			= oEditor.FCK ;
var FCKLang		= oEditor.FCKLang ;
var FCKConfig	= oEditor.FCKConfig ;
var FCKRegexLib	= oEditor.FCKRegexLib ;
var FCKTools	= oEditor.FCKTools ;

var uploadFileName ;

//#### Dialog Tabs

// Set the dialog tabs.
//dialog.AddTab( 'Upload', FCKLang.DlgLnkUpload) ;

// Function called when a dialog tag is selected.
function changeSize(newheight)
{
	var nheight = parseInt(newheight);
	var oldheight = parseInt(document.myDynamicContent.height);
	nheight = oldheight + (nheight);
	//alert(document.myDynamicContent.height);
	//document.getElementById("myDynamicContent").width=document.getElementById("myDynamicContent").width+15;
	document.myDynamicContent.height=nheight
	//alert(document.myDynamicContent.height);
	//alert(document.getElementBy("myDynamicContent").height);
	dialog.SetAutoSize( true ) ;
	//alert("ok");
}



function OnDialogTabChange( tabCode )
{	
	//ShowE('divUpload'	, ( tabCode == 'Upload' ) ) ;
	
	dialog.SetAutoSize( false ) ;
}

//#### Initialization Code

// oLink: The actual selected link in the editor.
var oLink = dialog.Selection.GetSelection().MoveToAncestorNode( 'A' ) ;
if ( oLink )
	FCK.Selection.SelectNode( oLink ) ;

oEditor.FCKUndo.SaveUndoStep() ;	
var aLinks = oLink ? [ oLink ] : oEditor.FCK.CreateLink( ' url ', true ) ;	
	
window.onload = function()
{
	// Translate the dialog box texts.
	oEditor.FCKLanguageManager.TranslatePage(document) ;

	// Show the initial dialog content.
	//GetE('divUpload').style.display = '' ;

	//refreshSignature();
	
	// Set the actual uploader URL.
	//if ( FCKConfig.Upload )
	//	GetE('frmUpload').action = FCKConfig.UploadURL ;

	// Activate the "OK" button.
	//dialog.SetOkButton( true ) ;
	dialog.document.getElementById("PopupButtons").style.display="none";
	dialog.SetAutoSize( "Flex" ) ;
}
function FlexCancel(){
	oEditor.FCKUndo.Undo();
	window.parent.CloseDialog();
	//return true;
}
//refresh checkbox for signature
function refreshSignature() {	
	// If no selection, no links are created, so use the uri as the link text (by dom, 2006-05-26)
	var aHasSelection = ( aLinks.length > 0 ) ;
	
	refreshCookie();
	
	if ( aHasSelection ) {
		if ( oEditor.FCKBrowserInfo.IsIE ) {
//		//deleted by diyanliang 20080905
//			GetE('signature').checked = false;
//			GetE('signature').disabled = true;	
		}	
	}
//	else {
//		if ( oEditor.FCKBrowserInfo.IsIE ) {
//			GetE('signature').checked = true;
//			GetE('signature').disabled = true;
//		}
//		else {
//			GetE('signature').checked = true;
//			GetE('signature').disabled = false;
//		}
//	}	
}

//#### The OK button was hit.
function Ok() 
{
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
function Cancel(){
	/*if (aLinks.length > 0) {
	//add by diyanliang 20080905	
		if(oLink==null){
	//end
			if ( oEditor.FCKBrowserInfo.IsIE ) {
				oEditor.FCKUndo.Undo() ;
			}
			else{
				FCK.ExecuteNamedCommand( 'Undo' ) ;
			}
	//add by diyanliang 20080905
		}	
	//end	
	}*/
	
	oEditor.FCKUndo.Undo()
	window.parent.CloseDialog();
	return true;
}

function OnUploadCompleted( errorNumber, fileUrl, fileName, customMsg, url )
{
	
	switch ( errorNumber )
	{
		case "0" :	// No errors
			//alert( 'upload.success'.localize() ) ;
			//Modified by morrise 20080605
			//Handle the succeed proceeding
			//alert( customMsg ) ;
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
			//Modified by morrise 20080605
			//alert( 'Error on file upload. Error number: ' + errorNumber ) ;
			alert(customMsg);			
	}
	
	if (uploadFileName && uploadFileName!="") {	
	
	//edit by diyanliang 20080903
	//addLink(fileUrl, url);
	var newurl="";
	if(url!=null){
		newurl=url;
		//newurl=newurl.substring(7,newurl.length);
		//var ici=newurl.indexOf("/");
		//newurl=newurl.substring(ici,newurl.length);
	}
	
	addLink(fileUrl, newurl);	
	//end
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

function addLink(fileUrl, url) {
	//var originalText = oEditor.FCK.GetHTML(true);

//edit by diyanliang 20080905
//	var aLinks = oLink ? [ oLink, oEditor.FCK.InsertElement( 'span' ) ] : oEditor.FCK.CreateLink( ' haha ', true );
	
	var aLinks=oEditor.FCK.CreateLink( ' haha ', true );
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
		aLinks = [ oEditor.FCK.InsertElement( 'a' ),  oEditor.FCK.InsertElement( 'span' )] ;
		
	}

	for ( var i = 0 ; i < 1 ; i++ )
	{
		oLink = aLinks[i] ;

		if ( aHasSelection )
			sInnerHtml = oLink.innerHTML ;		// Save the innerHTML (IE changes it if it is like an URL).

		oLink.href = url ;
		SetAttribute( oLink, '_fcksavedurl', url ) ;
	
		oLink.innerHTML = sInnerHtml ;		// Set (or restore) the innerHTML
		
		// Target
		SetAttribute( oLink, 'target', null ) ;

		// Let's set the "id" only for the first link to avoid duplication.
		//if ( i == 0 )
		//	SetAttribute( oLink, 'id', "" ) ;
		
	}	
	
	if (fileUrl && fileUrl.length > 0) {
		aLinks[1].innerHTML = fileUrl;
		aLinks[0].parentNode.insertBefore(aLinks[1], aLinks[0].nextSibling);
		
//		if ( oEditor.FCKBrowserInfo.IsIE ){
//			
//			
//			
////			aLinks[0].outerHTML=aLinks[0].outerHTML+fileUrl;
////			alert(aLinks[0].outerHTML)
//			return true;
////			oEditor.FCK.InsertHtml(fileUrl);
//			}
//		else {
//			aLinks[1].innerHTML = fileUrl;
//			aLinks[0].parentNode.insertBefore(aLinks[1], aLinks[0].nextSibling);
//		}
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
