/*
 * FCKeditor - The text editor for Internet - http://www.fckeditor.net
 * Copyright (C) 2003-2008 Frederico Caldeira Knabben
 *
 * == BEGIN LICENSE ==
 *
 * Licensed under the terms of any of the following licenses at your
 * choice:
 *
 *  - GNU General Public License Version 2 or later (the "GPL")
 *    http://www.gnu.org/licenses/gpl.html
 *
 *  - GNU Lesser General Public License Version 2.1 or later (the "LGPL")
 *    http://www.gnu.org/licenses/lgpl.html
 *
 *  - Mozilla Public License Version 1.1 or later (the "MPL")
 *    http://www.mozilla.org/MPL/MPL-1.1.html
 *
 * == END LICENSE ==
 *
 * FCKXml Class: class to load and manipulate XML files.
 * (IE specific implementation)
 */
var FCKXml=function(){
	this.Error=false;
};
FCKXml.GetAttribute =function(node, attName, defaultValue) {
	var attNode = node.attributes.getNamedItem(attName);
	return attNode ? attNode.value : defaultValue;
};
/**
 * Transforms a XML element node in a JavaScript object. Attributes defined for
 * the element will be available as properties, as long as child  element
 * nodes, but the later will generate arrays with property names prefixed with "$".
 *
 * For example, the following XML element:
 *
 *		<SomeNode name="Test" key="2">
 *			<MyChild id="10">
 *				<OtherLevel name="Level 3" />
 *			</MyChild>
 *			<MyChild id="25" />
 *			<AnotherChild price="499" />
 *		</SomeNode>
 *
 * ... results in the following object:
 *
 *		{
 *			name : "Test",
 *			key : "2",
 *			$MyChild :
 *			[
 *				{
 *					id : "10",
 *					$OtherLevel :
 *					{
 *						name : "Level 3"
 *					}
 *				},
 *				{
 *					id : "25"
 *				}
 *			],
 *			$AnotherChild :
 *			[
 *				{
 *					price : "499"
 *				}
 *			]
 *		}
 */
FCKXml.TransformToObject =function(element) {
	if (!element)
		return null;

	var obj = {};

	var attributes = element.attributes;
	for ( var i = 0; i < attributes.length; i++) {
		var att = attributes[i];
		obj[att.name] = att.value;
	}

	var childNodes = element.childNodes;
	for (i = 0; i < childNodes.length; i++) {
		var child = childNodes[i];

		if (child.nodeType == 1) {
			var childName = '$' + child.nodeName;
			var childList = obj[childName];
			if (!childList)
				childList = obj[childName] = [];

			childList.push(this.TransformToObject(child));
		}
	}

	return obj;
},
FCKXml.prototype = {
	LoadUrl : function(urlToCall) {
		this.Error = false;
		if (FCKBrowserInfo.IsGecko) {
			this.Error = false;
			var oXml;
			var oXmlHttp = FCKTools.CreateXmlObject('XmlHttp');
			oXmlHttp.open('GET', urlToCall, false);
			oXmlHttp.send(null);

			if (oXmlHttp.status == 200
					|| oXmlHttp.status == 304
					|| (oXmlHttp.status == 0 && oXmlHttp.readyState == 4)) {
				oXml = oXmlHttp.responseXML;
				// #1426: Fallback if responseXML isn't set for some
				// reason (e.g. improperly configured web server)
				if (!oXml)
					oXml = (new DOMParser()).parseFromString(
							oXmlHttp.responseText, 'text/xml');
			} else
				oXml = null;

			if (oXml) {
				// Try to access something on it.
				try {
					oXml.firstChild;
				} catch (e) {
					// If document.domain has been changed (#123), we'll
					// have a security
					// error at this point. The workaround here is
					// parsing the responseText:
					// http://alexander.kirk.at/2006/07/27/firefox-15-xmlhttprequest-reqresponsexml-and-documentdomain/
					oXml = (new DOMParser()).parseFromString(
							oXmlHttp.responseText, 'text/xml');
				}
			}

			if (!oXml || !oXml.firstChild) {
				this.Error = true;
				if (window
						.confirm('Error loading "'
								+ urlToCall
								+ '" (HTTP Status: '
								+ oXmlHttp.status
								+ ').\r\nDo you want to see the server response dump?'))
					alert(oXmlHttp.responseText);
			}

			this.DOMDocument = oXml;
		} else {
			var oXmlHttp = FCKTools.CreateXmlObject('XmlHttp');
			if (!oXmlHttp) {
				this.Error = true;
				return;
			}

			oXmlHttp.open("GET", urlToCall, false);
			//IE10 can't supports method 'selectSingleNode'.
			if(FCKBrowserInfo.IsIE10){
				try { oXmlHttp.responseType = 'msxml-document'; } catch (e) { };
			}
			oXmlHttp.send(null);

			if (oXmlHttp.status == 200
					|| oXmlHttp.status == 304
					|| (oXmlHttp.status == 0 && oXmlHttp.readyState == 4)) {
				this.DOMDocument = oXmlHttp.responseXML;

				// #1426: Fallback if responseXML isn't set for some
				// reason (e.g. improperly configured web server)
				if (!this.DOMDocument
						|| this.DOMDocument.firstChild == null) {
					this.DOMDocument = FCKTools
							.CreateXmlObject('DOMDocument');
					this.DOMDocument.async = false;
					this.DOMDocument.resolveExternals = false;
					this.DOMDocument.loadXML(oXmlHttp.responseText);
				}
			} else {
				this.DOMDocument = null;
			}

			if (this.DOMDocument == null
					|| this.DOMDocument.firstChild == null) {
				this.Error = true;
				if (window.confirm('Error loading "' + urlToCall
						+ '"\r\nDo you want to see more info?'))
					alert('URL requested: "' + urlToCall + '"\r\n'
							+ 'Server response:\r\nStatus: '
							+ oXmlHttp.status + '\r\n'
							+ 'Response text:\r\n'
							+ oXmlHttp.responseText);
			}
		}

	},
	SelectNodes : function( xpath, contextNode )
	{
		
		if ( this.Error )
			return new Array() ;
		if (FCKBrowserInfo.IsGecko){
			var aNodeArray = new Array();

			var xPathResult = this.DOMDocument.evaluate( xpath, contextNode ? contextNode : this.DOMDocument,
					this.DOMDocument.createNSResolver(this.DOMDocument.documentElement), XPathResult.ORDERED_NODE_ITERATOR_TYPE, null) ;
			if ( xPathResult )
			{
				var oNode = xPathResult.iterateNext() ;
				while( oNode )
				{
					aNodeArray[aNodeArray.length] = oNode ;
					oNode = xPathResult.iterateNext();
				}
			}
			return aNodeArray ;
		}else{
			if ( contextNode )
				return contextNode.selectNodes( xpath ) ;
			else
				return this.DOMDocument.selectNodes( xpath ) ;
		}
	},
	SelectSingleNode : function( xpath, contextNode )
	{
		if ( this.Error )
			return null ;
		if (FCKBrowserInfo.IsIE11){
			if ( contextNode )
				return contextNode.querySelector( xpath ) ;
			else
				return this.DOMDocument.querySelector( xpath ) ;
		}else if (FCKBrowserInfo.IsGecko ){
			var xPathResult ;
			if (this.DOMDocument.querySelector){
				xPathResult = this.DOMDocument.querySelector(xpath);
				
			}else{
				xPathResult = this.DOMDocument.evaluate( xpath, contextNode ? contextNode : this.DOMDocument,
						this.DOMDocument.createNSResolver(this.DOMDocument.documentElement), 9, null);
			}
			if ( xPathResult && xPathResult.singleNodeValue )
				return xPathResult.singleNodeValue ;
			else
				return null ;
		}else{
			if ( contextNode )
				return contextNode.selectSingleNode( xpath ) ;
			else
				return this.DOMDocument.selectSingleNode( xpath ) ;
		}

	}
};
