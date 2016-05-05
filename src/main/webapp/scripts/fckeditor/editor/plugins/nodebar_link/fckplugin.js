/**
 * diyanliang@cnic.cn 11-6-13
 * @return
 */
function getLink(){
//	node = FCKSelection.GetSelection().focusNode;
	var node=NodeBar._obj;
	
	var anode=getAncestorNode(node,"A");
	
	//var m_herf=anode.getAttribute( 'href' , 2 )
	var m_herf=anode.href;
	var m_url=m_herf;
	if(!isNaN(m_herf)){//是个数字
		m_url=window.top.site.getViewURL(m_herf);
	}
	return m_url;
}

function openLink(){
	window.open(getLink());
}

function openEditPage(){
	var node=NodeBar._obj;
	var anode=getAncestorNode(node,"A");
	var m_herf=anode.href;
	var m_url=m_herf;
	if((result = top.site.resolve(_herf))!=null){
		m_url=window.top.site.getEditURL(result.key);
	}
	
	window.open(m_url);
}

function popLinkWindow() {
	var node = NodeBar._obj;
	var mnode=getAncestorNode(node,"A");
	
	var range = new FCKDomRange(FCK.EditorWindow);
	range.MoveToElementEditStart(mnode);
	range.Select();
	
	sHRef = mnode.href|| '' ;
	
	if((obj=top.site.resolve(sHRef))!=null){
		
		if(obj.type=="view"||obj.type=="edit")
			FCK.ToolbarSet.CurrentInstance.Commands.GetCommand("E2Page").Execute();
		else
			FCK.ToolbarSet.CurrentInstance.Commands.GetCommand("E2Link").Execute();
		
		
	}else
		FCK.ToolbarSet.CurrentInstance.Commands.GetCommand("E2Link").Execute();
}
function removeLink(event) {
	FCKUndo.SaveUndoStep();
//	FCK.ToolbarSet.CurrentInstance.Commands.GetCommand("Unlink").Execute();
	var node = NodeBar._obj;
	
	var mnode=getAncestorNode(node,"A");
	var focusNode = mnode;
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
	//NodeBar.hidden(event)

}


