/*
 * Ext JS Library 2.1
 * Copyright(c) 2006-2008, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
var store;
Ext.onReady(function(){
	var module = "";
	 if( typeof(urlToModule)!= "undefined"){
	  module=urlToModule;
	 }
    Ext.BLANK_IMAGE_URL = module+'scripts/extjs/resources/images/default/s.gif';
	var allow = Ext.get('allow').getValue();
	
    Ext.QuickTips.init();
    
    var win;
	
    function getURL(cmd){
    	return site.getTeamURL("privilege")+"?func="+cmd+"&pid="+site.currentPage;
    }
    
    // renderer, format Read
    function formatType(value) {
    	if (value == 'group')
    		return 'grant.group'.localize();
    	else
    		return 'grant.user'.localize();        
    }
    
    // shorthand alias
    var fm = Ext.form;
	    
    // the column model has information about grid columns
    // dataIndex maps the column to the specific data field in
    // the data store (created below)
    
    if ((!allow) || (allow == 'false')) {
    	var checkViewNoEdit = new Ext.grid.CheckColumn({
						id: 'view',
		       			header: 'page.view.text'.localize(),
		       			align: 'center',
						dataIndex: 'view', 
						width: 80,
						sortable: true
			});	
			
	    var checkEditNoEdit = new Ext.grid.CheckColumn({
	           			id: 'edit',
		       			header: 'page.edit.text'.localize(),
	    	   			dataIndex: 'edit',
	    	   			align: 'center',
		       			width: 80,
		       			sortable: true
	        });
	    
	    var cmNoEdit = new Ext.grid.ColumnModel([{
	           id:'name',
	           header: 'grant.name'.localize(),
	           dataIndex: 'name',
	           width: 240
	        }, {
	           id:'type',
	           header: 'grant.type'.localize(),
	           renderer: formatType,
	           dataIndex: 'type',
	           align: 'center',
	           width: 100
	        }, 
	        checkViewNoEdit,
	        checkEditNoEdit
	        //checkRename
	    ]);
	
	    // by default columns are sortable
	    cmNoEdit.defaultSortable = true;
    }
    else {    
	    var checkView = new Ext.grid.CheckColumn({
						id: 'view',
		       			header: 'page.view.text'.localize(),
		       			align: 'center',
						dataIndex: 'view', 
						width: 80, 
						editor: new Ext.form.Checkbox(),
						sortable: true
			});	
			
	    var checkEdit = new Ext.grid.CheckColumn({
	           			id: 'edit',
		       			header: 'page.edit.text'.localize(),
	    	   			dataIndex: 'edit',
	    	   			align: 'center',
		       			width: 80,
		       			editor: new Ext.form.Checkbox(),
		       			sortable: true
	        });
	        
	    var checkRename = new Ext.grid.CheckColumn({
	           			id: 'rename',
		       			header: 'Rename',
	    	   			dataIndex: 'rename',
	    	   			align: 'center',
		       			width: 80,
		       			editor: new Ext.form.Checkbox(),
		       			sortable: true
	        });
	            
	    var cm = new Ext.grid.ColumnModel([{
	           id:'name',
	           header: 'grant.name'.localize(),
	           dataIndex: 'name',
	           width: 240
	        }, {
	           id:'type',
	           header: 'grant.type'.localize(),
	           renderer: formatType,
	           dataIndex: 'type',
	           align: 'center',
	           width: 100
	        }, 
	        checkView,
	        checkEdit
	        //checkRename
	    ]);
	
	    // by default columns are sortable
	    cm.defaultSortable = true;
	}
	
    // this could be inline, but we want to define the Plant record
    // type so we can add records dynamically
    var priv = Ext.data.Record.create([
    	   {name: 'type', type: 'string'},
           {name: 'name', type: 'string'},
           {name: 'view', type: 'bool'},
           {name: 'edit', type: 'bool'}
           //{name: 'rename', type: 'bool'}   
      ]);

    // create the Data Store
    store = new Ext.data.Store({
        // load using HTTP
        proxy: new Ext.data.HttpProxy({
            url: getURL('query'),
			method: 'POST'
        }),
        reader: new Ext.data.XmlReader({
               record: 'privilege'}, priv),
           	
        sortInfo:{field:'type', direction:'ASC'}
    });

    // create the editor grid
    if ((!allow) || (allow == 'false')) {
    	var grid = new Ext.grid.EditorGridPanel({
	    	title			: 'page.title'.localize(),
	        store			: store,
	        cm				: cmNoEdit,
	        renderTo		: 'pagePrivs-grid',
	        width			: 650,
	        height			: 300,
	        //autoExpandColumn:'name',
	        frame			: true,
	        clicksToEdit	: 1,
	        collapsible 	: true,
	        collapsed		: true,
	        
	        tbar: [],
	        
	        bbar: new Ext.StatusBar({
		        id: 'my-status',
		        
		        // defaults to use when the status is cleared:
		        defaultText: 'grant.status.ready'.localize(),
		        defaultIconCls: 'default-icon',
		        
		        // values to set initially:
		        text: 'grant.status.ready'.localize(),
		        iconCls: 'ready-icon',
		        
		        // any standard Toolbar items:
		        items: ['-',{
		            	text: 'grant.status.hint'.localize()
		        		}
		        ]
        	})
	    });
    }
    else {
	    var grid = new Ext.grid.EditorGridPanel({
	    	title			: 'page.title'.localize(),
	        store			: store,
	        cm				: cm,
	        renderTo		: 'pagePrivs-grid',
	        width			: 650,
	        height			: 300,
	        //autoExpandColumn:'name',
	        frame			: true,
	        clicksToEdit	: 1,
	        collapsible 	: true,
	        collapsed		: true,
	        sm				: new Ext.grid.RowSelectionModel({singleSelect:false}),
	
	        tbar: [	        	
		        {
		        	xtype:'tbfill'},	        
	        	{
		        	text: 'grant.tip.add'.localize(),
		            icon: module+'scripts/extjs/resources/images/default/dd/drop-add.gif',		            
		            cls: 'x-btn-text-icon',
		            tooltip: 'grant.tip.add'.localize(),
		            handler : function(){
		                showTree();
		            }},
	            {
	            	xtype:"tbseparator"
	            },{
	            	text: 'grant.tip.delete'.localize(),
		            icon: module+'scripts/extjs/resources/images/default/delete.gif',
		            cls: 'x-btn-text-icon',
		            tooltip: 'grant.tip.delete'.localize(),
		            handler: function() {
		            	var selected = grid.getSelectionModel().getSelected();//returns record object for the most recently selected
			
						var typeValue = "";
						var nameValue = "";
						
						//row that is in data store for grid
						if(selected){
							var selectedRows = grid.getSelectionModel().selections.items;
							for (var i=0; i<selectedRows.length; i++) {
								typeValue += selectedRows[i].get("type") + ",";
								nameValue += selectedRows[i].get("name") + ",";
							}
							while(selectedRows.length>0) {
								store.remove(selectedRows[0]);				
							}
							typeValue = typeValue.substring(0, typeValue.length - 1);
							nameValue = nameValue.substring(0, nameValue.length - 1);
						}
						else {
							Ext.MessageBox.alert('window.warning'.localize(), 'grant.noselect'.localize());
							return;
						}
							
						Ext.Ajax.request( 
									{   
										waitMsg: 'Delete privilege...',
										url: getURL('delete'),
										params: { 
												type: typeValue, 
												name: nameValue, 
												pagename: encodeURI(Ext.get('page').getValue())
										},							
										
										failure:function(response,options){
											Ext.MessageBox.alert('window.warning'.localize(), 'grant.delete.error'.localize());
											store.reload();
											// Update the status bar later in code:
											var sb = Ext.getCmp('my-status');
											sb.setStatus({
											    text: 'grant.delete.error'.localize(),
											    iconCls: 'error-icon',
											    clear: true // auto-clear after a set interval
											});					
										},                                      
										success:function(response,options){
											//Ext.MessageBox.alert('Success','Yeah...');
											//store.reload();
											// Update the status bar later in code:
											var sb = Ext.getCmp('my-status');
											sb.setStatus({
											    text: 'grant.status.complete'.localize(),
											    iconCls: 'ok-icon',
											    clear: true // auto-clear after a set interval
											});
										}                                      
						});
		            }
	        	},{
	            	xtype:"tbseparator"
	            	},
	            {
	            	text: 'page.allview'.localize(),
	            	icon: module+'scripts/extjs/resources/images/default/dd/drop-add.gif',
	            	cls: 'x-btn-text-icon',
		            tooltip: 'page.alledit.tip'.localize(),
		            handler : function(){
		            	var p = new priv({
					                    type: 'group',
					                    name: 'page.allentities'.localize(),
					                    view: true,
					                    edit: false
					                    //rename: false
					                });
					    var found = false;
			            for (var i=0; i<store.getCount(); i++) {
							var record = store.getAt(i);
							if ((record.get('type') == p.get('type')) && (record.get('name') == p.get('name'))) {
								record.set('view', true);
					            record.set('edit', false);
					            found=true;
					            break;
							}
						}
						if(!found)
						{
						   store.insert(0, p);
						}
						
						
						Ext.Ajax.request( 
							{   
								waitMsg: 'Delete privilege...',
								url: getURL('publicview'),
								params: {
										pagename: encodeURI(Ext.get('page').getValue()),
										type: p.get('type'),
										name: p.get('name')
								},							
								
								failure:function(response,options){
									Ext.MessageBox.alert('window.warning'.localize(), 'page.alledit.error'.localize());
									store.reload();
									// Update the status bar later in code:
									var sb = Ext.getCmp('my-status');
									sb.setStatus({
									    text: 'page.alledit.error'.localize(),
									    iconCls: 'error-icon',
									    clear: true // auto-clear after a set interval
									});				
								},                                      
								success:function(response,options){
									//store.insert(0, p);
									var sb = Ext.getCmp('my-status');
									sb.setStatus({
									    text: 'grant.status.complete'.localize(),
									    iconCls: 'ok-icon',
									    clear: true // auto-clear after a set interval
									});
								}                                      
						});
			        	//list.stopEditing();
			        }
	            },	
	            	{
	            	xtype:"tbseparator"
	            	},
	        	{
		        	text: 'page.alledit'.localize(),
		            icon: module+'scripts/extjs/resources/images/default/dd/drop-add.gif',		            
		            cls: 'x-btn-text-icon',
		            tooltip: 'page.alledit.tip'.localize(),
		            handler : function(){
		             var p = new priv({
					                    type: 'group',
					                    name: 'page.allentities'.localize(),
					                    view: true,
					                    edit: true
					                    //rename: false
					                });
			            Ext.Ajax.request( 
							{   
								waitMsg: 'Delete privilege...',
								url: getURL('public'),
								params: {
										pagename: encodeURI(Ext.get('page').getValue())
								},							
								
								failure:function(response,options){
									Ext.MessageBox.alert('window.warning'.localize(), 'page.alledit.error'.localize());
									store.reload();
									// Update the status bar later in code:
									var sb = Ext.getCmp('my-status');
									sb.setStatus({
									    text: 'page.alledit.error'.localize(),
									    iconCls: 'error-icon',
									    clear: true // auto-clear after a set interval
									});				
								},                                      
								success:function(response,options){
									//Ext.MessageBox.alert('Success','Yeah...');
									store.removeAll();
									store.insert(0, p);
									// Update the status bar later in code:
									var sb = Ext.getCmp('my-status');
									sb.setStatus({
									    text: 'grant.status.complete'.localize(),
									    iconCls: 'ok-icon',
									    clear: true // auto-clear after a set interval
									});
								}                                      
						});		                
		            }}
	        ],
	        
	        bbar: new Ext.StatusBar({
		        id: 'my-status',
		        
		        // defaults to use when the status is cleared:
		        defaultText: 'grant.status.ready'.localize(),
		        defaultIconCls: 'default-icon',
		        
		        // values to set initially:
		        text: 'grant.status.ready'.localize(),
		        iconCls: 'ready-icon',
		        
		        // any standard Toolbar items:
		        items: ['-',{
		            	text: 'grant.status.hint'.localize()
		        		}
		        ]
        	})
	    });	
	
		grid.on("afteredit", function(obj) {
			var r = obj.record;
			var typeValue = r.get("type");
			var nameValue = r.get("name");
			var viewValue = r.get("view");
			var editValue = r.get("edit");
			//var renameValue = r.get("rename");
			
			//check off read then check off write
			//check on write then check on read
			if ((obj.field == 'view') && (!viewValue)) {
				obj.record.set('edit', false);				
				editValue = false;			
			}
			
			if ((obj.field == 'edit') && (editValue)) {
				obj.record.set('view', true);
				viewValue = true;			
			}
			
			Ext.Ajax.request({
				scope: this,
				waitMsg: 'Update privilege',
				url:getURL('save'),
	   			success: function(result, request) {
	   				//Ext.MessageBox.alert('Success', 'Data return from the server: '+ result.responseText);
	   				//if (!r.get('view').getValue() && !r.get('edit').getValue())// && !r.get('rename').getValue())
	   				//	store.remove(r);   		
	   				// Update the status bar later in code:
					var sb = Ext.getCmp('my-status');
					sb.setStatus({
					    text: 'grant.status.complete'.localize(),
					    iconCls: 'ok-icon',
					    clear: true // auto-clear after a set interval
					});					               	
	   			},
	   			failure: function(result, request) {
	   				var record = obj.record;
					record.set(obj.field, obj.originalValue);
					//Ext.MessageBox.alert('window.warning'.localize(), result.responseText); 
					// Update the status bar later in code:
					var sb = Ext.getCmp('my-status');
					sb.setStatus({
					    text: result.responseText,
					    iconCls: 'error-icon',
					    clear: true // auto-clear after a set interval
					});	
	   			},
	   			params: { type: typeValue, name: nameValue, view: viewValue, edit: editValue, //rename: renameValue, 
	   				pagename: encodeURI(Ext.get('page').getValue())
	   				}
			});
		}, grid);
		
		//store for the window list
		var ds = new Ext.data.Store({
			reader: new Ext.data.XmlReader({
               record: 'privilege'}, priv),
           	
        	sortInfo:{field:'type', direction:'ASC'}
		});
		
		//view edit rename checkbox
		var viewCheckbox = new Ext.form.Checkbox({
			name: 'View',
			checked: true
		}); 
		
		var editCheckbox = new Ext.form.Checkbox({
			name: 'Edit'
		}); 
	
		viewCheckbox.on('check', function(obj, checked) {
			if (!checked) {
				editCheckbox.setValue(false);
			}
		});
		
		editCheckbox.on('check', function(obj, checked) {
			if (checked) {
				viewCheckbox.setValue(true);				
			}
		});
		
		//Create window list
		//Define the grid of selected group/user
	    var listcm = new Ext.grid.ColumnModel([{
		           id:'name',
		           header: 'grant.name'.localize(),
		           dataIndex: 'name',
		           width: 120,
		           sortable: true
		        },{
		           id:'type',
		           header: 'grant.type'.localize(),
		           dataIndex: 'type',
		           renderer: formatType,
		           width: 100,
		           sortable: true
		        }
	    	]);
	    	
	   // listcm.defaultSortable = true;	

	    var list = new Ext.grid.GridPanel({
	        	id				: 'listGrid',
	        	el				: 'votree-grid',
		        store			: ds,
		        cm				: listcm,
		        width			: 310,
		        height			: 280,
		        autoExpandColumn	: 'name',
		        frame			: true,
		        enableDragDrop		: true,
		        ddGroup			: 'selectVO',
		        collapsible		: true,
	        	animCollapse		: false,
		        selModel		: new Ext.grid.RowSelectionModel({singleSelect:false}),
		        
		        
		        tbar: [new Ext.Toolbar.TextItem('grant.list.title'.localize())]
	        });    
	    
	     // Tree for the center
			var voTree = new Ext.tree.TreePanel({
				id				 : 'voTree',
				el				 : 'votree-tree',
		        width			 : 310,  
	            height			 : 280,  
	            checkModel		 : 'mutiple',  
	            onlyLeafCheckable: false,  
	            animate			 : false,  
	            rootVisible		 : true,  
	            autoScroll		 : true,
	            enableDrag		 : true,
	            ddGroup			 : 'selectVO',
	            dropConfig		 : {
	            					//ddGroup: 'selectVO', 
	            					el: list.el},
	            selMode			 : new Ext.tree.MultiSelectionModel(),  
		        loader           : new Ext.tree.TreeLoader({
	        		dataUrl		 : site.getJSPURL('votree'),
	        		baseAttrs	 : { uiProvider: Ext.tree.TreeCheckNodeUI } 
		   		}),
		        root			 : new Ext.tree.AsyncTreeNode({
			        text		 : Ext.get('vo').getValue(),		        
			        //draggable	 : false,
			        checked		 : false,
			        type		 : 'group',
			        id			 : Ext.get('vo').getValue()
		    	}),
		    	
		    	tbar: [new Ext.Toolbar.TextItem('grant.tree.title'.localize())
			       	//,{xtype:'tbfill'},{
			       	//	text: 'grant.tip.adduser'.localize(),
			        //   icon: 'scripts/extjs/resources/images/default/dd/drop-add.gif',
			        //    cls: 'x-btn-text-icon',
			        //    tooltip: 'grant.tip.adduser'.localize(),
			        //    handler : function(){
			        //        addGroupUser(voTree);
				    //}            
			    	//}
			    ]
		    });
		    
		    function addGroupUser(tree) {
			var nodes = tree.getChecked();
			if (nodes && nodes.length) {
				for(var i=0; i<nodes.length; i++) {
					if(!isAlreadyExist(nodes[i], list.getStore()))
					{
						var p = new priv({
				                    type: nodes[i].attributes.type,
				                    name: nodes[i].attributes.text,
				                    view: viewCheckbox.getValue(),
				                    edit: editCheckbox.getValue()
				                });
				        	//list.stopEditing();
				        	list.getStore().insert(0, p);
			        	}
		        	}
	        }
	        else {
	        	Ext.Msg.alert("window.warning".localize(), 'grant.select.noadd'.localize());
			    return;
	        }
		}
		
		//Drop target for the list, drag source is votree
		var target = new Ext.dd.DropTarget(list.getEl(), {
		        ddGroup: list.ddGroup ,
		        notifyDrop: function(dd, e, data){
		            // determine the row
		            var sm = voTree.getSelectionModel();
	
	            	// get the rows of the tree we have selected
		            var node = sm.selNode;
		
		            // put this data into list grid
		            if(!isAlreadyExist(node, list.getStore())) {
			            var p = new priv({
					                    type: node.attributes.type,
					                    name: node.attributes.text,
					                    view: viewCheckbox.getValue(),
					                    edit: editCheckbox.getValue()
					                    //rename: renameCheckbox.getValue()
					                });
					        	//list.stopEditing();
					        list.getStore().insert(0, p);
			          }
			        		            
		            list.getView().refresh();	
			    }
		    });
		    
		var buttons = new Ext.form.FormPanel({
			id				: 'buttons',
			width			: 28,  
	        height			: 280,
	        baseCls			: 'x-panel',
	        items			: [{	
		        					html: '<br/><br/><br/><br/><br/><br/>'
	        					},
	        					{
		        					xtype: 'button',
					            	icon: module+'scripts/extjs/resources/images/default/rightarrow.png',
					            	cls: 'x-btn-icon',
					            	tooltip: 'grant.tip.adduser'.localize(),
					            	handler : function(){
					                			addGroupUser(voTree);
					    					}
				    			}, 
				    			{	
		        					html: '<br/>'
	        					},	        					
				    			{
					    			xtype: 'button',
			           				icon: module+'scripts/extjs/resources/images/default/leftarrow.png',
			            			cls: 'x-btn-icon',
			            			tooltip: 'grant.window.delete'.localize(),
			            			handler : function(){
			                		var records = list.getSelectionModel().getSelections();
			                		if(!records || records.length == 0){
			                    		Ext.Msg.alert("window.warning".localize(), 'grant.select.nodelete'.localize());
			                    		return;
			                		}
			                
			   						for (var i=0; i<records.length; i++)
						                	ds.remove(records[i]);
						            }
				    			},
				    			{	
		        					html: '<br/>'
	        					},	        					
				    			{
					    			xtype: 'button',
			           				icon: module+'scripts/extjs/resources/images/default/left2arrow.png',
			            			cls: 'x-btn-icon',
			            			tooltip: 'grant.window.deleteall'.localize(),
			            			handler : function(){
			            				ds.removeAll();
						            }
				    			}
				    		]	  
		});
		
		
		//Show Tree Window        
		function showTree() {
			//Create temp store
			// create the Data Store		
			ds.removeAll();
			//for( var i=0; i<store.getCount(); i++) {
		    //	ds.insert(0, store.getAt(i));
		    //}
	        
	        list.render();
	        
	        //Render the tree.
	        var checked = voTree.getChecked();
	        for(var i=0; i<checked.length; i++) {
	        	checked[i].getUI().checkbox.checked = false;
	        	checked[i].attributes.checked  = false;
	        	//voTree.fireEvent('check', checked[i], false);  
	        }   
	        
			voTree.getRootNode().expand();
			voTree.render();
			
			//Render view edit checkbox
	        viewCheckbox.setValue(true);
	        editCheckbox.setValue(false);	
			
	    	if (!win) {
				win = new Ext.Window({ 
		            title: 'page.window.title'.localize(),
		            el: 'win-votree',
		            closable:true,
		            modal: true,
		            width:670,
		            height:350,
		            minWidth:670,
		            minHeight:350,
		            plain:true,
		            layout: 'column',
		            closeAction:'hide',
		            items: [voTree, buttons, list],
		            
		            bbar : [{
		        			xtype:'tbfill'
		        		}, {
				            text: 'grant.button.grantview'.localize(),
				            icon: module+'scripts/extjs/resources/images/default/dd/drop-add.gif',
				            cls: 'x-btn-text-icon',
				            tooltip: 'page.button.grant.tip'.localize(),
				            handler : function(){
				                grantViewPriv(list.getStore(), store);
				            }
			            }, {
				            text: 'grant.button.grantedit'.localize(),
				            icon: module+'scripts/extjs/resources/images/default/dd/drop-add.gif',
				            cls: 'x-btn-text-icon',
				            tooltip: 'page.button.grant.tip'.localize(),
				            handler : function(){
				                grantEditPriv(list.getStore(), store);
				            }
			            }, {
			            	xtype:"tbseparator"
			            },{
				            text: 'grant.button.cancel'.localize(),
				            icon: module+'scripts/extjs/resources/images/default/cancel.gif',
				            cls: 'x-btn-text-icon',
				            tooltip: 'grant.button.cancel.tip'.localize(),
				            handler : function(){
				            	win.hide();
			            	}            
		        		}]
		        });
	        }
	        win.show();
	        win.toFront();
		}
	
		
		function grantPriv(stsrc, stdes) {
			if (!viewCheckbox.getValue() && !editCheckbox.getValue())// && !renameCheckbox.getValue()) {
			{
				Ext.MessageBox.alert('window.warning'.localize(), 'page.warning.priv'.localize());
				return;
			}
			
			if (stsrc.getCount() == 0) {
				Ext.MessageBox.alert('window.warning'.localize(), 'page.warning.entity'.localize());
				return;
			}
			
			grid.stopEditing();
		
			for (var i=0; i<stsrc.getCount(); i++) {
				var record = stsrc.data.items[i];
				var position = isExist(record, stdes);
				if(position == -1)
				{
					var p = new priv({
				                   type: record.get('type'),
				                   name: record.get('name'),
				                   view: viewCheckbox.getValue(),
				                   edit: editCheckbox.getValue()
				                   //rename: renameCheckbox.getValue()
				            });			    
					record = p;
					stdes.insert(0, p);
				}
				else {
					var record = stdes.getAt(position);
					record.set('view', viewCheckbox.getValue());
					record.set('edit', editCheckbox.getValue());
				}	
				Ext.Ajax.request({
					scope: this,
					waitMsg: 'Update privilege',
					url:getURL('save'),
			   		success: function(result, request) {
			   			//Ext.MessageBox.alert('Success', 'Data return from the server: '+ result.responseText);		   				   						               	
			   		},
			   		failure: function(result, request) {
			   			var deleted = stdes.data.items[0];
			   			stdes.remove(deleted);
			   			grid.startEditing(0, 0);
	        			grid.getView().refresh();
	        			return false;		   				 
			   		},
			   		params: { 
			   			type: record.get('type'), name: record.get('name'), view: record.get('view'), edit: record.get('edit'), //rename: record.get('rename'), 
			   			pagename: encodeURI(Ext.get('page').getValue())
			   		}			   			
				});        
	        }        	
	        
	        grid.startEditing(0, 0);
	        grid.getView().refresh();
	        
		}
		
		function grantViewPriv(stsrc, stdes) {
			//if (!viewCheckbox.getValue() && !editCheckbox.getValue())// && !renameCheckbox.getValue()) {
			//{
			//	Ext.MessageBox.alert('window.warning'.localize(), 'page.warning.priv'.localize());
			//	return;
			//}
			
			if (stsrc.getCount() == 0) {
				Ext.MessageBox.alert('window.warning'.localize(), 'page.warning.entity'.localize());
				return;
			}
			
			grid.stopEditing();
		
			for (var i=0; i<stsrc.getCount(); i++) {
				var record = stsrc.data.items[i];
				var position = isExist(record, stdes);
				if(position == -1)
				{
					var p = new priv({
				                   type: record.get('type'),
				                   name: record.get('name'),
				                   view: true,
				                   edit: false
				                   //rename: renameCheckbox.getValue()
				            });			    
					record = p;
					stdes.insert(0, p);
				}
				else {
					var record = stdes.getAt(position);
					record.set('view', true);
					record.set('edit', false);
				}	
				Ext.Ajax.request({
					scope: this,
					waitMsg: 'Update privilege',
					url: getURL('save'),
			   		success: function(result, request) {
			   			//Ext.MessageBox.alert('Success', 'Data return from the server: '+ result.responseText);
			   			// Update the status bar later in code:
			   			grid.startEditing(0, 0);
	        			grid.getView().refresh();
			   			win.hide();
						var sb = Ext.getCmp('my-status');
						sb.setStatus({
						    text: 'grant.status.complete'.localize(),
						    iconCls: 'ok-icon',
						    clear: true // auto-clear after a set interval
						});		   				   						               	
			   		},
			   		failure: function(result, request) {
			   			var deleted = stdes.data.items[0];
			   			stdes.remove(deleted);
			   			grid.startEditing(0, 0);
	        			grid.getView().refresh();
	        			win.hide();
	        			// Update the status bar later in code:
						var sb = Ext.getCmp('my-status');
						sb.setStatus({
						    text: result.responseText,
						    iconCls: 'error-icon',
						    clear: true // auto-clear after a set interval
						});		   				 
			   		},
			   		params: { 
			   			type: record.get('type'), name: record.get('name'), view: record.get('view'), edit: record.get('edit'), //rename: record.get('rename'), 
			   			pagename: encodeURI(Ext.get('page').getValue())
			   		}			   			
				});        
	        }        	
	        
	        grid.startEditing(0, 0);
	        grid.getView().refresh();
	        
	        return;
	        
		}
		
		function grantEditPriv(stsrc, stdes) {
			//if (!viewCheckbox.getValue() && !editCheckbox.getValue())// && !renameCheckbox.getValue()) {
			//{
			//	Ext.MessageBox.alert('window.warning'.localize(), 'page.warning.priv'.localize());
			//	return;
			//}
			
			if (stsrc.getCount() == 0) {
				Ext.MessageBox.alert('window.warning'.localize(), 'page.warning.entity'.localize());
				return;
			}
			
			grid.stopEditing();
		
			for (var i=0; i<stsrc.getCount(); i++) {
				var record = stsrc.data.items[i];
				var position = isExist(record, stdes);
				if(position == -1)
				{
					var p = new priv({
				                   type: record.get('type'),
				                   name: record.get('name'),
				                   view: true,
				                   edit: true
				                   //rename: renameCheckbox.getValue()
				            });			    
					record = p;
					stdes.insert(0, p);
				}
				else {
					var record = stdes.getAt(position);
					record.set('view', true);
					record.set('edit', true);
				}	
				Ext.Ajax.request({
					scope: this,
					waitMsg: 'Update privilege',
			   		url: geURL('save'),
			   		success: function(result, request) {
			   			//Ext.MessageBox.alert('Success', 'Data return from the server: '+ result.responseText);
			   			// Update the status bar later in code:
			   			grid.startEditing(0, 0);
	        			grid.getView().refresh();
			   			win.hide();
						var sb = Ext.getCmp('my-status');
						sb.setStatus({
						    text: 'grant.status.complete'.localize(),
						    iconCls: 'ok-icon',
						    clear: true // auto-clear after a set interval
						});		   				   						               	
			   		},
			   		failure: function(result, request) {
			   			var deleted = stdes.data.items[0];
			   			stdes.remove(deleted);
			   			grid.startEditing(0, 0);
	        			grid.getView().refresh();
	        			win.hide();
	        			// Update the status bar later in code:
						var sb = Ext.getCmp('my-status');
						sb.setStatus({
						    text: result.responseText,
						    iconCls: 'error-icon',
						    clear: true // auto-clear after a set interval
						});		   				 
			   		},
			   		params: { 
			   			type: record.get('type'), name: record.get('name'), view: record.get('view'), edit: record.get('edit'), //rename: record.get('rename'), 
			   			pagename: encodeURI(Ext.get('page').getValue())
			   		}			   			
				});        
	        }        	
	        
	        grid.startEditing(0, 0);
	        grid.getView().refresh();
	        
	        return;
	        
		}
		
		function isAlreadyExist(node, st) {
			for (var i=0; i<st.getCount(); i++) {
				var record = st.getAt(i);
				if ((record.get('type') == node.attributes.type) && (record.get('name') == node.attributes.text)) {
					return true;
				}
			}
			return false;
		}
		
		function isExist(rd, st) {
			for (var i=0; i<st.getCount(); i++) {
				var record = st.getAt(i);
				if ((record.get('type') == rd.get('type')) && (record.get('name') == rd.get('name'))) {
					return i;
				}
			}
			return -1;
		}	
	}	
	
	//render store of list grid
    store.load();    
		
});