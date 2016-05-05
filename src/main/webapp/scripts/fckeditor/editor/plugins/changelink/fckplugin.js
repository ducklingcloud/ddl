var DEChangeLink = {
	"checkLink" : function(e) {
		var C = e.keyCode || e.which;
		var focusNode = FCKSelection.GetSelection().focusNode;
		if (focusNode && (C == 32 || C == 229) && focusNode.nodeType == 3 && focusNode.nodeValue != "") {
			if (focusNode.parentNode.nodeName != "A" && focusNode.parentNode.parentNode.nodeName != "A") {
				var reg = new RegExp("[a-zA-Z]{1,9}:\\/\\/[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+){0,4}(:[\\d]+){0,1}(\\/[\\+/&%\\#.a-zA-Z0-9_-]+)*(\\?[\\+/&%\\.\"'\\,\\#\\[\\]\\(\\)=&a-zA-Z0-9_]*){0,1}", "gi");
				var arr = reg.exec(focusNode.nodeValue);
				if (arr && arr[0]) {
					var href = arr[0];
					var link = document.createElement("a");
					link.setAttribute("href", href);
					link.setAttribute("target", "_blank");
					link.appendChild(document.createTextNode(href));
					var stringLength = focusNode.nodeValue.length;
					var hrefLength = href.length;
					var hrefStart = focusNode.nodeValue.indexOf(href);
					var valueStart = focusNode.nodeValue.substring(0, hrefStart);
					var valueEnd = focusNode.nodeValue.substring(hrefStart + hrefLength, stringLength);
					focusNode.parentNode.insertBefore(document.createTextNode(valueStart), focusNode);
					focusNode.parentNode.insertBefore(link, focusNode);
					focusNode.parentNode.insertBefore(document.createTextNode(valueEnd), focusNode);
					focusNode.parentNode.removeChild(focusNode);
				}
			}
		}
	},
	"_SetupClickListener" : function() {
		FCKTools.AddEventListener(FCK.EditorDocument, "keydown", DEChangeLink.checkLink);
	}
};

FCK.Events.AttachEvent('OnAfterSetHTML', DEChangeLink._SetupClickListener);