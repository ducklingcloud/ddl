var DETabAction = {
	"tabPluginActed" : false,
	"tabButton" : function(e) {
		DETabAction.tabPluginActed = false;
		var C = e.keyCode || e.which;
		if(!e.shiftKey&&C==9){
			var A = new FCKDomRange(FCK.EditorWindow);
			A.MoveToSelection();
			var C = FCKDomTools.GetCommonParentNode(A.StartNode || A.StartContainer, A.EndNode || A.EndContainer, ["ul", "ol"]);
			if (C) {
				FCK.ToolbarSet.CurrentInstance.Commands.GetCommand("Indent").Execute();
				DETabAction.tabPluginActed = true;
			}
			return false;
		}
	},
	"shiftTabButton" : function(e) {
		var C = e.keyCode || e.which;
		if(e.shiftKey&&C==9){
			var A = new FCKDomRange(FCK.EditorWindow);
			A.MoveToSelection();
			var C = FCKDomTools.GetCommonParentNode(A.StartNode || A.StartContainer, A.EndNode || A.EndContainer, ["ul", "ol"]);
			if (C) {
				FCK.ToolbarSet.CurrentInstance.Commands.GetCommand("Outdent").Execute();
				DETabAction.tabPluginActed = true;
			}
			
			e.returnValue = false;
			e.cancelBubble = true;
			e.keyCode=0;
			return false;
		}
	},
	"_SetupClickListener" : function() {
		FCKTools.AddEventListener(FCK.EditorDocument,"keydown",  DETabAction.tabButton, true);
		FCKTools.AddEventListener(FCK.EditorDocument,"keydown",  DETabAction.shiftTabButton, true);
	}
};

FCK.Events.AttachEvent('OnAfterSetHTML', DETabAction._SetupClickListener);