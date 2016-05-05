// Created by morrise 20080514
// allpages.js Create All pages type Ahead.
//
function initSearhPage(url){
	Ext.onReady(function(){
	     var module = "";
		 if( typeof(urlToModule)!= "undefined"){
		  module=urlToModule;
		 }	 
		 var ds = new Ext.data.Store({
	        proxy: new Ext.data.HttpProxy({
	            url:url,
				method: 'POST'
	        }),
	        reader: new Ext.data.XmlReader({
	               record: 'page',
	               totalRecords: '@total'
	           	}, [
				'title'
				])
			});
	
	    // Custom rendering Template
	    var resultTpl = new Ext.XTemplate(
	        '<table><tpl for="."><tr class="search-item"><td>{title}</td></tr></tpl></table>'
	    );
	  document.getElementById('page_setting_parentpageshow').style.display="block";
	  document.getElementById('page_setting_pageprivshow').style.display="block";
	  document.getElementById('page_setting_navpageshow').style.display="block";
	  document.getElementById('page_setting_leftpageshow').style.display="block";
	  document.getElementById('page_setting_bannershow').style.display="block";
	  document.getElementById('page_setting_footershow').style.display="block";
	   	var pinputBox = "parentPageTitle";
	   	//parentPage exist?
	   	if ($(pinputBox)) {
	   	var search = new Ext.form.ComboBox({
	        store: ds,
	        displayField:'title',
	        typeAhead: true,
	        loadingText: 'search.hint'.localize(),
	        width: 155,
	        hideTrigger:true,
	        tpl: resultTpl,
	        applyTo: pinputBox,
	        minChars:1,
	        itemSelector: 'tr.search-item',
	        lazyInit:true,
	        triggerAction: 'all',    
	        selectOnFocus: true
	    });
	    }
	    var ninputBox = "selectNavPageTitle";
	   	//parentPage exist?
	   	if ($(ninputBox)) {
	   	var search = new Ext.form.ComboBox({
	        store: ds,
	        displayField:'title',
	        typeAhead: true,
	        loadingText: 'search.hint'.localize(),
	        width: 155,
	        hideTrigger:true,
	        tpl: resultTpl,
	        applyTo: ninputBox,
	        minChars:1,
	        itemSelector: 'tr.search-item',
	        lazyInit:true,
	        triggerAction: 'all',        
	        selectOnFocus: true
	    });
	    }
	    var linputBox = "selectLeftPageTitle";
	   	//parentPage exist?
	   	if ($(linputBox)) {
	   	var search = new Ext.form.ComboBox({
	        store: ds,
	        displayField:'title',
	        typeAhead: true,
	        loadingText: 'search.hint'.localize(),
	        width: 155,
	        hideTrigger:true,
	        tpl: resultTpl,
	        applyTo: linputBox,
	        minChars:1,
	        itemSelector: 'tr.search-item',
	        lazyInit:true,
	        triggerAction: 'all',    
	        selectOnFocus: true
	    });
	    }
	     var finputBox = "selectFooterTitle";
	   	//parentPage exist?
	   	if ($(finputBox)) {
	   	var search = new Ext.form.ComboBox({
	        store: ds,
	        displayField:'title',
	        typeAhead: true,
	        loadingText: 'search.hint'.localize(),
	        width: 155,
	        hideTrigger:true,
	        tpl: resultTpl,
	        applyTo: finputBox,
	        minChars:1,
	        itemSelector: 'tr.search-item',
	        lazyInit:true,
	        triggerAction: 'all',    
	        selectOnFocus: true
	    });
	    }
	    
	    secBoard(transferToallPages);
	});
}