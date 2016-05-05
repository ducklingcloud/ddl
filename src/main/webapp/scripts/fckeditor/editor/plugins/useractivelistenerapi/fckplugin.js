//var UserActiveTime=""
FCK.UserActiveTime=null;
var UserActive={
		"time":null,
		setTime:function(){
			var myDate = new Date();
			FCK.UserActiveTime=myDate;
		},
		setListener:function(){
			var toolbar=(FCK.ToolbarSet._TargetElement).ownerDocument.getElementById("floattoolbar");
			FCKTools.AddEventListener(FCK.EditorDocument, 'keyup', UserActive.setTime, false);
			FCKTools.AddEventListener(FCK.EditorDocument, 'click', UserActive.setTime , false);
			FCKTools.AddEventListener(toolbar, 'click', UserActive.setTime , false);
		}
};

FCK.Events.AttachEvent( 'OnAfterSetHTML', UserActive.setListener) ; 