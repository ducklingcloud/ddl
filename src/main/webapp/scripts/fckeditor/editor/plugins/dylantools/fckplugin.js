String.prototype.trim = function() { 
	return this.replace(/(^\s*)|(\s*$)/g, ""); 
}

var doNothing=function(){}
function getDCTURL(){
	dctbaseurl=FCKConfig.DucklingBaseHref
	if(dctbaseurl.substr(dctbaseurl.length-1)=="/"){
		dctbaseurl=dctbaseurl.substr(0,dctbaseurl.length-1)
	}
	return dctbaseurl
}

function getAncestorNode(oNode,nodeTagName){
	while ( oNode && oNode.nodeName != nodeTagName )
		oNode = oNode.parentNode ;
	return oNode ;
}

function SetAttribute( element, attName, attValue )
{
	if ( attValue == null || attValue.length == 0 )
		element.removeAttribute( attName, 0 ) ;			// 0 : Case Insensitive
	else
		element.setAttribute( attName, attValue, 0 ) ;	// 0 : Case Insensitive
}

function getFocusNode(e){
	var focusNode = null;
	if (FCKBrowserInfo.IsIE) {
//		if(linkpop_ietemp_focusnode == null){//如果是空的，说明是一次新的行为，重新获取焦点元素
//			e = window.event || e;
//			linkpop_ietemp_focusnode = e.srcElement || e.target;
//		}
//		return linkpop_ietemp_focusnode;
		return e.srcElement || e.target;
	}else{
		focusNode = FCKSelection.GetSelection().focusNode;
	}
	return focusNode;
}
function getXmlHttp() {
	var xmlHttp;
	try {
		// Firefox, Opera 8.0+, Safari
		xmlHttp = new XMLHttpRequest();
	} catch (e) {
		// Internet Explorer
		try {
			xmlHttp = new ActiveXObject("Msxml2.XMLHTTP");
		} catch (e) {
			try {
				xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
			} catch (e) {
				return null;
			}
		}
	}
	return xmlHttp;
}


function CreateNamedElement( oOriginal, nodeName, oAttributes )
{
	var oNewNode ;

	// IE doesn't allow easily to change properties of an existing object,
	// so remove the old and force the creation of a new one.
	var oldNode = null ;
	if ( oOriginal && FCKBrowserInfo.IsIE )
	{
		// Force the creation only if some of the special attributes have changed:
		var bChanged = false;
		for( var attName in oAttributes )
			bChanged |= ( oOriginal.getAttribute( attName, 2) != oAttributes[attName] ) ;

		if ( bChanged )
		{
			oldNode = oOriginal ;
			oOriginal = null ;
		}
	}

	// If the node existed (and it's not IE), then we just have to update its attributes
	if ( oOriginal )
	{
		oNewNode = oOriginal ;
	}
	else
	{
		// #676, IE doesn't play nice with the name or type attribute
		if ( FCKBrowserInfo.IsIE )
		{
			var sbHTML = [] ;
			sbHTML.push( '<' + nodeName ) ;
			for( var prop in oAttributes )
			{
				sbHTML.push( ' ' + prop + '="' + oAttributes[prop] + '"' ) ;
			}
			sbHTML.push( '>' ) ;
			if ( !FCKListsLib.EmptyElements[nodeName.toLowerCase()] )
				sbHTML.push( '</' + nodeName + '>' ) ;

			oNewNode = FCK.EditorDocument.createElement( sbHTML.join('') ) ;
			// Check if we are just changing the properties of an existing node: copy its properties
			if ( oldNode )
			{
				CopyAttributes( oldNode, oNewNode, oAttributes ) ;
				FCKDomTools.MoveChildren( oldNode, oNewNode ) ;
				oldNode.parentNode.removeChild( oldNode ) ;
				oldNode = null ;

				if ( FCK.Selection.SelectionData )
				{
					// Trick to refresh the selection object and avoid error in
					// fckdialog.html Selection.EnsureSelection
					var oSel = FCK.EditorDocument.selection ;
					FCK.Selection.SelectionData = oSel.createRange() ; // Now oSel.type will be 'None' reflecting the real situation
				}
			}
			oNewNode = FCK.InsertElement( oNewNode ) ;

			// FCK.Selection.SelectionData is broken by now since we've
			// deleted the previously selected element. So we need to reassign it.
			if ( FCK.Selection.SelectionData )
			{
				var range = FCK.EditorDocument.body.createControlRange() ;
				range.add( oNewNode ) ;
				FCK.Selection.SelectionData = range ;
			}
		}
		else
		{
			oNewNode = FCK.InsertElement( nodeName ) ;
		}
	}

	// Set the basic attributes
	for( var attName in oAttributes )
		oNewNode.setAttribute( attName, oAttributes[attName], 0 ) ;	// 0 : Case Insensitive

	return oNewNode ;
}