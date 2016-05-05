var inputImgTitleElement=function(){
	var panel=document.createElement("div")
	FCKDomTools.SetElementStyles( panel,
			{
				'marginBottom'		: '10px'
			} ) ;
//	var newNameTxt=document.createElement('input');
//	newNameTxt.id="txtName"+(1231231);
//	newNameTxt.type="text";
//	panel.appendChild(newNameTxt);
	
	var oIFrame = this._IFrame = document.createElement('iframe') ;
	FCKTools.ResetStyles( oIFrame );
	oIFrame.src					= FCKConfig.BasePath+'plugins/nodebar_img/input.html' ;
	oIFrame.allowTransparency	= true ;
	oIFrame.frameBorder			= '0' ;
	oIFrame.scrolling			= 'no' ;
	oIFrame.style.width = '200px'
		oIFrame.style.height = '40px' ;
	panel.appendChild(oIFrame);
	return panel
}

var downLoadImg=function(e) {
	
	var focusNode =NodeBar._obj;
	var selectedImg = getImgElement(focusNode);
	
	_src=selectedImg.src
	index =_src.lastIndexOf("/")
	filename=_src.substr(index+1);
	if(selectedImg!=null){
		var xmlHttp = getXmlHttp();
		if(xmlHttp==null){
			alert("您的浏览器不支持AJAX！");
			return;
		}
		
		var url=top.site.getTeamURL("uploadImg")+"?url="+selectedImg.getAttribute("src")+"&pid="+FCKConfig.DucklingResourceId+"&filename="+filename;
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
					
					x = xmlDoc.getElementsByTagName("vwbClbId");
					vwbclbid = x[0].childNodes[0].nodeValue;
					x = xmlDoc.getElementsByTagName("docId");
					docId = x[0].childNodes[0].nodeValue;
					x = xmlDoc.getElementsByTagName("infoURL");
					infoURL = x[0].childNodes[0].nodeValue;
					x = xmlDoc.getElementsByTagName("title");
					title = x[0].childNodes[0].nodeValue;
					x = xmlDoc.getElementsByTagName("previewURL");
					previewURL = x[0].childNodes[0].nodeValue;
					x = xmlDoc.getElementsByTagName("type");
					type = x[0].childNodes[0].nodeValue;
				}catch(e){}
				if(code == 0){
					alert(msg);
				}else if(code == 1){
					selectedImg.setAttribute("_fcksavedurl",newurl);
					selectedImg.setAttribute("src",newurl);
					alert(msg);
					top.addToRightList(vwbclbid,docId,infoURL,title,previewURL,type)
				}else{
					alert("未知的返回值："+code);
				}
			}
		}
		xmlHttp.open("GET",url,true);
		xmlHttp.send(null);
	}

}

function getImgSrc(){
	node=NodeBar._obj;
	anode=getAncestorNode(node,"IMG")
	var m_herf=anode.getAttribute( 'src' , 2 )
	return m_herf;
}

function openIMG(){
	window.open(getImgSrc())
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