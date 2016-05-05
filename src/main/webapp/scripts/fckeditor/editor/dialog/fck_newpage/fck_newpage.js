
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
var dialog = window.parent;
var oEditor = dialog.InnerDialogLoaded();
var FCK = oEditor.FCK;
var FCKLang = oEditor.FCKLang;
var FCKConfig = oEditor.FCKConfig;
var FCKRegexLib = oEditor.FCKRegexLib;
var FCKTools = oEditor.FCKTools;
var uploadFileName;

//#### Dialog Tabs

// Set the dialog tabs.
//dialog.AddTab("Upload", FCKLang.Button);

// Function called when a dialog tag is selected.
function OnDialogTabChange(tabCode) {
	ShowE("divUpload", (tabCode == "Upload"));
	dialog.SetAutoSize(false);
}

//#### Initialization Code

// oLink: The actual selected link in the editor.
var oLink = dialog.Selection.GetSelection().MoveToAncestorNode("A");
if (oLink) {
	FCK.Selection.SelectNode(oLink);
}
window.onload = function () {
	LoadSelection()
	// Translate the dialog box texts.
	oEditor.FCKLanguageManager.TranslatePage(document);

	// Show the initial dialog content.
	GetE("divUpload").style.display = "";

	// Activate the "OK" button.
	dialog.SetOkButton(true);
	
	document.getElementById("txtUrl").focus();
	dialog.SetAutoSize( true ) ;

};
String.prototype.trim = function () {
	return this.replace("/(^s*)|(s*$)/g", "");
};  
   
//add by diyanliang 
function isTel(string) {
	var i = 0;
	var tmpchar;
	for (i = 0; i < string.length; i++) {
		tmpchar = string.charAt(i);
		if (tmpchar >= "0" && tmpchar <= "9") {
			continue;
		}
		return false;
	}
	return true;
}
//end
  
//#### The OK button was hit.
function Ok() {
	var sUri, sInnerHtml;
	sUri = GetE("txtUrl").value;
	if (!sUri) {
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
	var ajaxurl=FCKConfig.DucklingBaseHref+'team/createPage?func=createInEdit&title='+encodeURI(sUri)+'&ResourceId='+FCKConfig.DucklingCId;
//	var ajaxurl=oEditor.site.getTeamURL("createPage")+'?func=createInEdit&title='+encodeURI(sUri)+'&ResourceId='+FCKConfig.DucklingResourceId;
	sUri=send_request("get", ajaxurl, "", "text", callback); 
//	aLinks = oLink ? [oLink] : oEditor.FCK.CreateLink(sUri, true);
//	var aHasSelection = (aLinks.length > 0);
//	if (!aHasSelection) {
//		sInnerHtml = sUri;
//		var oLinkPathRegEx = new RegExp("//?([^?\"']+)([?].*)?$");
//		var asLinkPath = oLinkPathRegEx.exec(sUri);
//		if (asLinkPath != null) {
//			sInnerHtml = asLinkPath[1];
//		}  // use matched path
//		aLinks = [oEditor.FCK.InsertElement("a")];
//	}
//	for (var i = 0; i < aLinks.length; i++) {
//		oLink = aLinks[i];
//		if (aHasSelection) {
//			sInnerHtml = oLink.innerHTML;
//		}		// Save the innerHTML (IE changes it if it is like an URL).
//		oLink.href = sUri;
//		SetAttribute(oLink, "_fcksavedurl", sUri);
//		oLink.innerHTML = sInnerHtml;		// Set (or restore) the innerHTML
//		SetAttribute(oLink, "target", "");
//		SetAttribute(oLink, "title", GetE("txtUrl").value);
//		if (i == 0) {
//			SetAttribute(oLink, "id", "");
//			
//		}
//	}	
//	//显示内容处理
//	if(!GetE('DucklingLnkUrlInner').disabled){
//			if(GetE('DucklingLnkUrlInner').value.length>0)
//				oLink.innerHTML = GetE('DucklingLnkUrlInner').value ;
//	}
//	oEditor.FCKSelection.SelectNode(aLinks[0]);
//	return true;
}


function callback(){
	   if (xmlHttp.readyState == 4) { // 判断对象状态
	       if (xmlHttp.status == 200) { // 信息已经成功返回，开始处理信息
	    	   		sUri=xmlHttp.responseText;
					aLinks = oLink ? [oLink] : oEditor.FCK.CreateLink(sUri, true);
					var aHasSelection = (aLinks.length > 0);
					if (!aHasSelection) {
						sInnerHtml =  GetE("txtUrl").value;
						var oLinkPathRegEx = new RegExp("//?([^?\"']+)([?].*)?$");
						var asLinkPath = oLinkPathRegEx.exec(sUri);
						if (asLinkPath != null) {
							sInnerHtml = asLinkPath[1];
						} 
						aLinks = [oEditor.FCK.InsertElement("a")];
					}
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
						SetAttribute(oLink, "title", GetE("txtUrl").value);
						// Let's set the "id" only for the first link to avoid duplication.
						if (i == 0) {
							SetAttribute(oLink, "id", "");
							
						}
					}	
					//显示内容处理
					if(!GetE('DucklingLnkUrlInner').disabled){
							if(GetE('DucklingLnkUrlInner').value.length>0)
								oLink.innerHTML = GetE('DucklingLnkUrlInner').value ;
					}
					oEditor.FCKSelection.SelectNode(aLinks[0]);
					window.parent.CloseDialog();
	       } else { //页面不正常
	             alert("quest error!");
	       }
	   }
	}

//编辑链接时候取到内容处理
function LoadSelection(){
	dialog.SetAutoSize( true ) ;
	//得到选中的文字
	var strinner=getInner();
	//选中文字赋值
	if(strinner[0]=='boolean'){
		if(strinner[1]==false){
			GetE('DucklingLnkUrlInner').disabled="disabled";
			GetE('DucklingLnkUrlInner').className="DisableDucklingLnkUrlInner";
			fckseleTxt='false';
		}else if(strinner[1]==true){
			fckseleTxt='true';
			GetE('DucklingLnkUrlInner').className="";
			//nothing to do  but its importance
		}
	}else{
		GetE('DucklingLnkUrlInner').value=strinner[1];
		GetE('DucklingLnkUrlInner').className="";
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
	return ["text",inner];
}


