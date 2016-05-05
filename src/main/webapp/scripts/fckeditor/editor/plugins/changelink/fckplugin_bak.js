var linkpop_ietemp_focusnode = null;
var DEChangeLink = {
	"_ClickListener" : function(e) {
	
		var linkpop_div = document.getElementById("linkpop_div");
		var linkpop_div_img = document.getElementById("linkpop_imgdiv");
		var linkpop_div_link = document.getElementById("linkpop_linkdiv");
		
		// 如果页面中还没有这个div层，则新插入一个
		if (linkpop_div==null) {
			createDiv();
			linkpop_div = document.getElementById("linkpop_div");
			linkpop_div_img = document.getElementById("linkpop_imgdiv");
			linkpop_div_link = document.getElementById("linkpop_linkdiv");
		}else{
			linkpop_div.style.display = "none";
			linkpop_div_img.style.display = "none";
			linkpop_div_link.style.display = "none";
		}
		linkpop_ietemp_focusnode = null;//在IE下，先把焦点的元素暂存起来
		var focusNode = getFocusNode(e);
		
		// 检查是否有图片元素被选中，如果有，则将selectedImg赋值为该图片元素
		var selectedImg = getImgElement(focusNode);
		
		// 查看外层是否有A标签包围
		focusNode = getLinkElement(focusNode,selectedImg);
		
		if(focusNode!=null){
			if (focusNode.nodeName == "A") {// 是个超链接
				var linkpop_a = document.getElementById("linkpop_a");
				var href = focusNode.getAttribute("href");
				while (linkpop_a.hasChildNodes()) {
					linkpop_a.removeChild(linkpop_a.childNodes[0]);
				}
				if(!isNaN(href)){//是个数字
					setPathByResourceId(linkpop_a,href);
				}else{
					if(href=="#"){
						linkpop_a.setAttribute("href", "javascript:;");
						linkpop_a.removeAttribute("target");
					}else{
						linkpop_a.setAttribute("href", href);
						linkpop_a.setAttribute("target", "_blank");
					}
					linkpop_a.appendChild(document.createTextNode(href));
				}
				linkpop_div_link.style.display = "block";
			}
			if (selectedImg!=null) {// 含有图片
				var linkpop_img_a = document.getElementById("linkpop_img_a");
				linkpop_img_a.setAttribute("href", selectedImg.getAttribute("src"));
				linkpop_div_img.style.display = "block";
			}
			if (focusNode.nodeName == "A" || selectedImg!=null) {
				var X = focusNode.offsetLeft;
				var Y = 0;
				if(focusNode.nodeName == "A"){
					Y = focusNode.offsetTop + 105;
				}else{
					var error = false;
					try{
						Y = focusNode.offsetTop + selectedImg.offsetHeight +90;
					}catch(e){
						error = true;
					}
					if(error){
						error = false;
						X = e.clientX;
						Y = e.clientY + 105;
					}
				}
				linkpop_div.style.left = X > 450 ? 450 : X;
				linkpop_div.style.top = Y;
				linkpop_div.style.display = "block";
			}
		}
	},
	"checkLink" : function(e) {
		var C = e.keyCode || e.which;
		var focusNode = FCKSelection.GetSelection().focusNode;
		if (focusNode && (C == 32 || C == 229) && focusNode.nodeType == 3 && focusNode.nodeValue != "") {
			if (focusNode.parentNode.nodeName != "A" && focusNode.parentNode.parentNode.nodeName != "A") {
				var reg = new RegExp("[a-zA-Z]{1,9}:\\/\\/[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+){0,4}(:[\\d]+){0,1}(\\/[\\+/&%\\.a-zA-Z0-9_-]+)*(\\?[\\+/&%\\.\"'\\,\\#\\[\\]\\(\\)=&a-zA-Z0-9_]*){0,1}", "gi");
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
		if (!FCKBrowserInfo.IsIE) {
			FCK.EditorDocument.addEventListener('click', DEChangeLink._ClickListener, true);
			FCK.EditorDocument.addEventListener('keydown', DEChangeLink.checkLink, false);
		}else{
			FCK.EditorDocument.attachEvent( 'onclick',DEChangeLink._ClickListener );
			FCK.EditorDocument.attachEvent( 'onkeydown',DEChangeLink.checkLink );
		}
	}
}
function setPathByResourceId(linkpop_a, id){
	var xmlHttp = getXmlHttp();
	if(xmlHttp==null){
		alert("您的浏览器不支持AJAX！");
		return;
	}
	var url="/dct/team/getPathByResourceId?id="+id;
	xmlHttp.onreadystatechange = function() {
		if (xmlHttp.readyState == 4) {
			var xmlDoc = xmlHttp.responseXML;
			var code, msg;
			try{
				var x = xmlDoc.getElementsByTagName("code");
				code = x[0].childNodes[0].nodeValue;
				x = xmlDoc.getElementsByTagName("msg");
				msg = x[0].childNodes[0].nodeValue;
			}catch(e){}
			if(code == 0){
				alert(msg);
			}else if(code == 1){
				if(msg=="#"){
					linkpop_a.setAttribute("href", "javascript:;");
					linkpop_a.removeAttribute("target");
					linkpop_a.appendChild(document.createTextNode("链接无法识别"));
				}else{
					linkpop_a.setAttribute("href", msg);
					linkpop_a.setAttribute("target", "_blank");
					linkpop_a.appendChild(document.createTextNode(msg));
				}
			}else{
				alert("未知的返回值："+code);
			}
		}
	}
	xmlHttp.open("GET",url,true);
	xmlHttp.send(null);
}
function getImgElement(focusNode){
	var selectedImg = null;
	if (focusNode.nodeName != null) {
		if (focusNode.nodeName == "IMG") {
			selectedImg = focusNode;
		} else if (focusNode.hasChildNodes()) {
			var childs = focusNode.childNodes;
			for (i in focusNode.childNodes) {
				if (childs[i] != null && childs[i].nodeName && childs[i].nodeName == "IMG") {
					selectedImg = childs[i];
				}
			}
		}
	}
	return selectedImg;
}
function getLinkElement(focusNode,selectedImg){
	var count = 0;
	var result = null;
	while (focusNode!=null && focusNode.parentNode!=null && count < 5) {
		if (focusNode.nodeName == "A"){
			result = focusNode;
			break;
		}
		focusNode = focusNode.parentNode;
		count = count + 1;
	}
	if(result==null&&selectedImg!=null){
		result = selectedImg;
	}
	return result;
}
function m_attachEvent(element, event_type, functionName){
	
//	FCKTools.AddEventListener( element, event_type, functionName ) ;
	if ( FCKBrowserInfo.IsIE ){
		element.attachEvent( "on"+event_type, functionName ) ;
	}else{
		element.addEventListener( event_type, functionName, false ) ;
	}
}



function createDiv() {
	var linkpop_div = document.createElement("div");
	linkpop_div.setAttribute("id", "linkpop_div");
	linkpop_div.style.fontFamily = "微软雅黑,宋体,Tahoma,Arial,Helvetica,sans-serif";
	linkpop_div.style.fontSize = "9pt";
	linkpop_div.style.padding = "3px 3px 3px 3px";
	linkpop_div.style.border = "solid 1px";
	linkpop_div.style.backgroundColor = "#F5F5F5";
	linkpop_div.style.position = "absolute";
	linkpop_div.style.left = "0px";
	linkpop_div.style.top = "0px";
	linkpop_div.style.display = "none";
	
	var linkpop_div_img = document.createElement("div");
	linkpop_div_img.setAttribute("id", "linkpop_imgdiv");
	var linkpop_div_link = document.createElement("div");
	linkpop_div_link.setAttribute("id", "linkpop_linkdiv");

	var linkpop_a = document.createElement("a");
	linkpop_a.setAttribute("id", "linkpop_a");
	linkpop_a.setAttribute("target", "_blank");

	var save_link = document.createElement("a");
	save_link.setAttribute("href", "#");
	m_attachEvent(save_link, "click", downLoadImg);
	save_link.appendChild(document.createTextNode("下载远程图片"));

	var open_link = document.createElement("a");
	open_link.setAttribute("id", "linkpop_img_a");
	open_link.setAttribute("target", "_blank");
	open_link.appendChild(document.createTextNode("打开图片"));

	var remove_link = document.createElement("a");
	remove_link.setAttribute("href", "#");
	m_attachEvent(remove_link, "click", removeLink);
	remove_link.appendChild(document.createTextNode("移除"));

	var modify_link = document.createElement("a");
	modify_link.setAttribute("href", "#");
	m_attachEvent(modify_link, "click", popLinkWindow);
//	modify_link.setAttribute("onclick", "javascript: popLinkWindow()");
	modify_link.appendChild(document.createTextNode("修改"));

	if (!FCKBrowserInfo.IsIE) {// 移除和修改功能暂时不支持IE。。。
	}
	linkpop_div_img.appendChild(save_link);
	linkpop_div_img.appendChild(document.createTextNode("  "));
	linkpop_div_img.appendChild(open_link);

	linkpop_div_link.appendChild(document.createTextNode("打开链接:"));
	linkpop_div_link.appendChild(linkpop_a);
	if (!FCKBrowserInfo.IsIE) {// 移除和修改功能暂时不支持IE。。。
	}
	linkpop_div_link.appendChild(document.createTextNode("  "));
	linkpop_div_link.appendChild(remove_link);
		linkpop_div_link.appendChild(document.createTextNode("  "));
		linkpop_div_link.appendChild(modify_link);

	linkpop_div.appendChild(linkpop_div_img);
	linkpop_div.appendChild(linkpop_div_link);
	
	linkpop_div.style.display = "none";
	linkpop_div_img.style.display = "none";
	linkpop_div_link.style.display = "none";
	
	document.body.appendChild(linkpop_div);
}

function removeLink(event) {
	var focusNode = getFocusNode(event);
	var count = 0;
	while (focusNode && focusNode.nodeName != "A" && count < 5) {
		focusNode = focusNode.parentNode;
		count = count + 1;
	}
	if (focusNode.nodeName == "A") {
		var childNodes = focusNode.childNodes;
		for ( var i = 0; i < childNodes.length; i=i+1) {
			focusNode.parentNode.insertBefore(childNodes[i], focusNode);
		}
		focusNode.parentNode.removeChild(focusNode);
	}
	var linkpop_div = document.getElementById("linkpop_div");
	linkpop_div.style.display = "none";
}
function popLinkWindow() {
	FCK.ToolbarSet.CurrentInstance.Commands.GetCommand("Link").Execute();
}
var downLoadImg=function(e) {
	var focusNode = getFocusNode(e);
	var selectedImg = getImgElement(focusNode);
	if(selectedImg!=null){
		var xmlHttp = getXmlHttp();
		if(xmlHttp==null){
			alert("您的浏览器不支持AJAX！");
			return;
		}
		var url="/dct/uploadImg?url="+selectedImg.getAttribute("src");
		xmlHttp.onreadystatechange = function() {
			if (xmlHttp.readyState == 4) {
				// 从服务器的response获得数据
				xmlDoc = xmlHttp.responseXML;
				var code, msg, newurl;
				try{
					var x = xmlDoc.getElementsByTagName("code");
					code = x[0].childNodes[0].nodeValue;
					x = xmlDoc.getElementsByTagName("msg");
					msg = x[0].childNodes[0].nodeValue;
					x = xmlDoc.getElementsByTagName("newurl");
					newurl = x[0].childNodes[0].nodeValue;
				}catch(e){}
				if(code == 0){
					alert(msg);
				}else if(code == 1){
					selectedImg.setAttribute("_fcksavedurl",newurl);
					selectedImg.setAttribute("src",newurl);
					alert(msg);
				}else{
					alert("未知的返回值："+code);
				}
			}
		}
		xmlHttp.open("GET",url,true);
		xmlHttp.send(null);
	}

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
function getFocusNode(e){
	var focusNode = null;
	if (FCKBrowserInfo.IsIE) {
		if(linkpop_ietemp_focusnode == null){//如果是空的，说明是一次新的行为，重新获取焦点元素
			e = window.event || e;
			linkpop_ietemp_focusnode = e.srcElement || e.target;
		}
		return linkpop_ietemp_focusnode;
	}else{
		focusNode = FCKSelection.GetSelection().focusNode;
	}
	
//	if (FCKBrowserInfo.IsIE) {
//		var e = window.event || event;
//		focusNode = e.srcElement || e.target;
//	} else {
//		focusNode = FCKSelection.GetSelection().focusNode;
//	}
	return focusNode;
}
FCK.Events.AttachEvent('OnAfterSetHTML', DEChangeLink._SetupClickListener);