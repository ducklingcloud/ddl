/**
 * diyanliang@cnic.cn
 * 
 * 2011-6-1
 */

FCK.insertLink = function(linktext, linkurl, newwindow, rid) {
	FCKUndo.SaveUndoStep();
	var m_range = new FCKDomRange(FCK.EditorWindow);
	m_range.MoveToSelection();
	// //m_range.DeleteContents();
	var n_node = FCK.EditorDocument.createElement('A');
	n_node.innerHTML = linktext;
	n_node.href = linkurl;
	m_range.Collapse(false);
	if (newwindow)
		SetAttribute(n_node, 'target', '_blank');

	SetAttribute(n_node, 'rid', rid);

	if (FCKSelection.HasAncestorNode("A")) {
		var a_Node = FCKSelection.MoveToAncestorNode('A');
		FCKDomTools.InsertAfterNode(a_Node, n_node);
	} else
		m_range.InsertNode(n_node);
	
	m_range.InsertNode(FCK.EditorDocument.createTextNode(" ") );
}

FCK.insertImg = function(imgsrc, m_height, m_width, linkurl, rid) {
	FCKUndo.SaveUndoStep();

	var m_range = new FCKDomRange(FCK.EditorWindow);
	m_range.MoveToSelection();
	// m_range.DeleteContents();
	var oImage = FCK.EditorDocument.createElement('img');
	oImage.src = imgsrc;
	m_range.Collapse(false);
	if (m_height && !isNaN(m_height)) {
		SetAttribute(oImage, "width", m_height);
	}
	if (m_width && !isNaN(m_width)) {
		SetAttribute(oImage, "height", m_width);
	}

	var pNode = FCK.EditorDocument.createElement('p');
	if (linkurl) {
		var n_node = FCK.EditorDocument.createElement('a');
		n_node.href = linkurl;
		SetAttribute(n_node, 'target', '_blank');
		SetAttribute(n_node, 'rid', rid);
		n_node.appendChild(oImage);
		pNode.appendChild(n_node);
	} else {
		pNode.appendChild(oImage);
	}
	
	m_range.InsertNode(pNode);
	m_range.InsertNode(FCK.EditorDocument.createTextNode(" ") );
}

FCK.insertNode = function(node) {
	FCKUndo.SaveUndoStep();
	var m_range = new FCKDomRange(FCK.EditorWindow);
	m_range.MoveToSelection();
	m_range.Collapse(false);
	m_range.InsertNode(node);
	FCK.Focus();
}
