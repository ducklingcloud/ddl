//newpage.js created by morrise 20080611
//New a wiki page.
//

Ext.BLANK_IMAGE_URL = 'scripts/extjs/resources/images/default/s.gif';

//Ext.onReady(function(){

		
//});

var newpagewin;

function AddNewPage() {
	var text = new Ext.form.TextField({
		    	id: 'newpagename',
		    	name: 'newpagename',
		    	fieldLabel: 'Page Name',
		    	allowBlank: false,
		    	enableKeyEvents: true,
		    	maxLength: 200,
		    	emptyText: 'newpage.empty'.localize()		
	});

	if (!newpagewin) {		    
		newpagewin = new Ext.Window({
			title: 'newpage.new'.localize(),
		    //el: 'newpage',
		    layout: 'fit',
		    width: 400,
		    modal: true,
		    height: 100,
		    closeAction: 'hide',
		    plain: true,
		    items: [text],
						
		    buttons: [{
		    	text:'button.submit'.localize(),
		        handler: function(){
		        	addCLB();
		        }
		    },{
		        text: 'button.cancel'.localize(),
		        handler: function(){
		        	newpagewin.hide();
		        }
		    }]
		});
	}
	newpagewin.show();
	
	text.on('keydown', function(t, e) {
		if(e.getKey() == e.ENTER){
             addCLB();
         }		
	});	
}

function addCLB() {
	if ((!Ext.get('newpagename').getValue()) || (Ext.get('newpagename').getValue().length==0)) {
		alert('addpage.enter'.localize());
		Ext.get('newpagename').focus();
		return;
	}
	
	if (Ext.get('newpagename').getValue().length > 32) {
		alert('newpage.maxlength'.localize());
		Ext.get('newpagename').focus();
		return;
	}
	
	location.href = Ext.get('editurl').getValue() + 'Edit.jsp?page=' + Ext.get('newpagename').getValue();
}