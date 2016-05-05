/**
* Created by morrise 20080604
* For upload.jsp
*/
	//Function to create an array
	function makeArray(n)
	{
		this.length = n;
		for (var i = 0; i < n; i++)
			this[i] = 0;
		return this;
	}
	
     function autosummary() {
         if (document.uploadform.summary.disabled) 
            document.uploadform.summary.removeAttribute("disabled");
         else document.uploadform.summary.disabled = "true";
     }
     var checkTagValid = true;
     var checkTagDes = "";
     function checkUploadedAttachment()
     {
     if (document.uploadform.file.value.trim()==""){
		  alert("upload.choose.file".localize());
		  document.uploadform.file.focus();
 		  return false;
	    }
     	if(document.uploadform.title.value.trim()==""){
		   alert("upload.choose.title".localize());
		   document.uploadform.title.focus();
 		   return false;
	    }
	    var filename = document.uploadform.file.value;
	    filename = filename.substring(filename.lastIndexOf('\\')+1);
	    if (filename.length > 64){
		  alert("upload.choose.file.maxsize".localize());
		  document.uploadform.file.focus();
 		  return false;
	    }
	    if (document.uploadform.title.value.trim().length > 64){
		  alert("upload.choose.file.maxsize".localize());
		  document.uploadform.file.focus();
 		  return false;
	    }
	    if(!checkTagValid)
	    {
	       alert(checkTagDes.nodeValue);
	       return false;
	    }
	    //if (document.uploadform.tag.length < 1){
		//  alert("upload.choose.tag".localize());
 		//  return false;
	    //}
	    
	    if (document.uploadform.isBatch[1].checked) {
	    	var str = document.uploadform.file.value;
	    	var pos = str.lastIndexOf(".");
 			var suffix = str.substring(pos,str.length)
 			if (suffix.toLowerCase()!=".zip") {
		    	alert("upload.warning.zip".localize());
	    		return false;
	    	}
	    }
	    document.uploadform.notify.value = 0;
		document.uploadform.checkedValue.disabled = false;
	    document.uploadform.submit();
     }

     function checkValue() {
     	if (document.uploadform.file.value.trim()==""){
		  alert("upload.choose.file".localize());
		  document.uploadform.file.focus();
 		  return false;
	    }
     	if(document.uploadform.title.value.trim()==""){
		   alert("upload.choose.title".localize());
		   document.uploadform.title.focus();
 		   return false;
	    }
	    var filename = document.uploadform.file.value;
	    filename = filename.substring(filename.lastIndexOf('\\')+1);
	    if (filename.length > 64){
		  alert("upload.choose.file.maxsize".localize());
		  document.uploadform.file.focus();
 		  return false;
	    }
	    if (document.uploadform.title.value.trim().length > 64){
		  alert("upload.choose.file.maxsize".localize());
		  document.uploadform.file.focus();
 		  return false;
	    }
	    //if (document.uploadform.tag.length < 1){
		//  alert("upload.choose.tag".localize());
 		//  return false;
	    //}

	    if (document.uploadform.isBatch[1].checked) {
	    	var str = document.uploadform.file.value;
	    	var pos = str.lastIndexOf(".");
 			var suffix = str.substring(pos,str.length)
 			if (suffix.toLowerCase()!=".zip") {
		    	alert("upload.warning.zip".localize());
	    		return false;
	    	}
	    }
	    
	var in_group = document.uploadform.tag;
	var out_group = document.uploadform.selected;
	var result = true;	
	//Max members limit check
	if (in_group.length > 0) {
		// select all group items - except for the first one 
		in_group.options[0].selected = 1;
		for (var i = 1; i < in_group.length; i++) {
			in_group.options[i].selected = 1;
		}
	}
	document.uploadform.notify.value = 0;
	document.uploadform.checkedValue.disabled = false;
    document.uploadform.submit();
 }
     
     function checkNotifyValue() {
     	if (document.uploadform.checkedValue.value.length != 0)
     		window.open('templates/avlab/clb/voTree.jsp?value='+encodeURI(document.uploadform.objectIds.value),'add','scrollbars=yes,resizable=yes,top=200,left=600,width=400,height=460');
     	else
     		window.open('templates/avlab/clb/voTree.jsp','add','scrollbars=yes,resizable=yes,top=200,left=600,width=400,height=460');
	 }
     
     function checkNotifyValue1() {
     	if (document.uploadform.file.value.trim()==""){
		  alert("upload.choose.file".localize());
		  document.uploadform.file.focus();
 		  return false;
	    }
     	if(document.uploadform.title.value.trim()==""){
		   alert("upload.choose.title".localize());
		   document.uploadform.title.focus();
 		   return false;
	    }
	    if (document.uploadform.tag.length < 2){
		  alert("upload.choose.tag".localize());
 		  return false;
	    }

	     if (document.uploadform.isBatch[1].checked) {
	    	var str = document.uploadform.file.value;
	    	var pos = str.lastIndexOf(".");
 			var suffix = str.substring(pos,str.length)
 			if (suffix.toLowerCase()!=".zip") {
		    	alert("upload.warning.zip".localize());
	    		return false;
	    	}
	    }

		var in_group = document.uploadform.tag;
		var out_group = document.uploadform.privateTag;
		var result = true;	
		//Max members limit check
		count = in_group.length - 1;  //exclude member header
		// select all group items - except for the first one 
		in_group.options[0].selected = 0;
		for (var i = 1; i < in_group.length; i++) {
			buf = in_group.options[i].text;
			in_group.options[i].value = buf;
			in_group.options[i].selected = 1;
		}
		document.uploadform.notify.value = 1;
	    document.uploadform.submit();
	 } 
 
     function newTag()  {
     	if (document.uploadform.newtag.value.trim()==""){
     		alert("upload.addtag".localize());
		  	document.uploadform.newtag.focus();
		  	return false;
		}
		document.uploadform.operate.value="newtag";  

  		document.uploadform.submit(); 
  		return true;
  	 }  
  	 
	 function delTag()  {  
	 	var in_group = document.uploadform.tag;
	 	var m = document.getElementById( "menu-sys");
  	 	if( m && m.className == "activetab" ) {
 			var out_group = document.uploadform.sysTag;
  	 	}
  	 	m = document.getElementById( "menu-private");
  	 	if( m && m.className == "activetab" ) {
 			var out_group = document.uploadform.privateTag;
  	 	}
  	 	m = document.getElementById( "menu-share");
  	 	if( m && m.className == "activetab" ) {
 			var out_group = document.uploadform.shareTag;
  	 	}
		//Traverse the inGrp and count all selected items.
		var toRemoveCount = 0;
		var i,j;

		for (var i = 0; i < in_group.length; i++)
		{
			if (in_group.options[i].selected == 1)
			{
				toRemoveCount++;
			}
		}

		//Create an array for the items that remain & remove.
		var arrayIdx = 0;
		var toRemainArray = new makeArray(in_group.length - toRemoveCount);
		var toRemainArrayValue = new makeArray(in_group.length - toRemoveCount);
		var toRemoveArray = new makeArray(toRemoveCount);
	
		j=0;
		for (var i = 0; i < in_group.length; i++)
		{
			if (in_group.options[i].selected == 1)
			{
				in_group.options[i].selected = 0;
				if (j < toRemoveCount)
					toRemoveArray[j++] =in_group.options[i].text;
			}	
			else {
				toRemainArray[arrayIdx] = in_group.options[i].text;
				toRemainArrayValue[arrayIdx] =in_group.options[i].value;
				arrayIdx++;
			}
		}

		//Resize the list and rename its items according to the Remain array.
		in_group.length = arrayIdx;
		for (var i = 0; i < toRemainArray.length; i++) {
			in_group.options[i].text = toRemainArray[i];
			in_group.options[i].value = toRemainArrayValue[i];
		}
		//Add the array elements to the outGrp, only if not already there.
		for (j = 0; j < out_group.length; j++)
			out_group.options[j].selected = 0;
		
		for (var i = 0; i < toRemoveCount; i++)
		{
			for (j = 0; j < out_group.length; j++)
			{
				if (out_group.options[j].text == toRemoveArray[i])
				{
					out_group.options[j].selected = 1;
					break;
				}
			}
		}

		// Mark that this is the MoveOut button.
		inGrpItems = in_group.length;		
  	 }  
  	 
  	 function addTag()  {   
  	 	var in_group = document.uploadform.tag;
  	 	var m = document.getElementById( "menu-sys");
  	 	if( m && m.className == "activetab" ) {
 			var out_group = document.uploadform.sysTag;
  	 	}
  	 	m = document.getElementById( "menu-private");
  	 	if( m && m.className == "activetab" ) {
 			var out_group = document.uploadform.privateTag;
  	 	}
  	 	m = document.getElementById( "menu-share");
  	 	if( m && m.className == "activetab" ) {
 			var out_group = document.uploadform.shareTag;
  	 	}
			
		//Traverse the outGrp and store all the selected items in an array.
		var toMoveCount = 0;		
		for (var i = 0; i < out_group.length; i++)
		{
			if (out_group.options[i].selected == 1)
				toMoveCount++;
		}


		//Create an array and store the selected outGroup names in it.
		var arrayIndex = 0;
		var toMoveArray = new makeArray(toMoveCount);
		var toMoveArrayValue = new makeArray(toMoveCount);
		for (var i = 0; i < out_group.length; i++)
		{
			if (out_group.options[i].selected == 1) {
				toMoveArray[arrayIndex] = out_group.options[i].text;
				toMoveArrayValue[arrayIndex] = out_group.options[i].value;
				arrayIndex++;
			}
		}

		//Unselect all items in the inGrp.
		if (toMoveArray.length > 0)
		{
			for (i = 0; i < in_group.length; i++)
				in_group.options[i].selected = 0;
		}

		//Add the array elements to the inGroup, only if not already there.
		var j;
		var memberName;
		for (var i = 0; i < toMoveArray.length; i++)
		{
			memberName = toMoveArrayValue[i];
			for (j = 0; j < in_group.length; j++)
			{
				if (in_group.options[j].value == memberName)
				{
					in_group.options[j].selected = 1;
					break;
				}
			}

			// The member is not alreay in the group, so add it.
			if (j == in_group.length)
			{
				in_group.length = j + 1;
				in_group.options[j].text = toMoveArray[i];
				in_group.options[j].value = memberName;				
				in_group.options[j].selected = 1;
			}
		}

		// Mark that this is the MoveIn button.
		inGrpItems = in_group.length;
  	} 
function fileSelect() {
	var filename = document.uploadform.file.value;
	filename = filename.substring(filename.lastIndexOf('\\')+1);
	document.uploadform.title.value=filename;
}
function fileSelectAvoidDuplication() {
	var uploadedfilename = document.uploadform.file.value;
	uploadedfilename = uploadedfilename.substring(uploadedfilename.lastIndexOf('\\')+1);
	document.uploadform.title.value=uploadedfilename;
	var fileName = document.getElementsByName("page");
	var ajaxurl = "upload/checkDuplication.do?method=checkDuplicationFile&wikiPageName="+fileName[0].value+"&uploadFileName="+encodeURI(uploadedfilename);
	send_request("get", ajaxurl, "", "XML", checkDuplicatoin);
}
function fckFileSelectAvoidDuplication(){
    var uploadedfilename = document.frmUpload.NewFile.value;
    uploadedfilename = uploadedfilename.substring(uploadedfilename.lastIndexOf('\\')+1);
    var fileName = window.parent.parent.document.getElementById("page");
    var ajaxurl = "../../../../upload/checkDuplication.do?method=checkDuplicationFile&wikiPageName="+fileName.value+"&uploadFileName="+encodeURI(uploadedfilename);
	send_request("get", ajaxurl, "", "XML", fckCheckDuplicatoin);  
}
function fckCheckDuplicatoin()
{
     if (xmlHttp.readyState == 4) {
		if (xmlHttp.status == 200) { 
		   var domObj = xmlHttp.responseXML;
		   if(domObj){
		     var statusNodes = domObj.getElementsByTagName("status");
	         var statusNode = statusNodes[0];
	         var status = statusNode.firstChild;
               if(status.nodeValue == "existence"){
                 var descriptionNodes = domObj.getElementsByTagName("description");
                 var descriptionNode = descriptionNodes[0];
                 var description = descriptionNode.firstChild;
                     if(!confirm(description.nodeValue)){
                       if(window.ActiveXObject){
                               var strin='<input id="txtUploadFile" style="WIDTH: 100%" type="file" size="40" name="NewFile" onchange="fckFileSelectAvoidDuplication()"/>';  
                               document.getElementById("txtUploadFile").parentNode.innerHTML=strin;
                        }else{
                         document.frmUpload.NewFile.value="";
                       }
                     }
               }      
		   }else{
		    alert("The format of xml data is wrong,The original data is :"+httpXML.responseText);
		   }
		}
	}
}
// the xml data is like :<message><status>existence</status><description>hello</description></message>
function checkDuplicatoin(){
    if (xmlHttp.readyState == 4) {
		if (xmlHttp.status == 200) { 
		   var domObj = xmlHttp.responseXML;
		   if(domObj){
		     var statusNodes = domObj.getElementsByTagName("status");
	         var statusNode = statusNodes[0];
	         var status = statusNode.firstChild;
               if(status.nodeValue == "existence"){
                 var descriptionNodes = domObj.getElementsByTagName("description");
                 var descriptionNode = descriptionNodes[0];
                 var description = descriptionNode.firstChild;
                     if(!confirm(description.nodeValue)){
                       if(window.ActiveXObject){
                        var strin="<input type='file' name='file' id='attachfilename' size='25' onchange='fileSelectAvoidDuplication()' />";  
                        var temp = document.getElementById("attachfilename").parentNode.childNodes[1].nodeValue;
                        document.getElementById("attachfilename").parentNode.innerHTML=strin+temp;
                        
                        }else{
                         document.uploadform.file.value="";
                       }
                       
                       document.uploadform.title.value="";
                     }
               }      
		   }else{
		    alert("The format of xml data is wrong,The original data is :"+httpXML.responseText);
		   }
		}
	}
}
function addNewTag() {
	//window.open('templates/blue/user/addNewTag.jsp','add','scrollbars=yes,resizable=yes,top=200,left=600,width=300,height=278');
	var divAddNewTag = document.getElementById("divAddNewTag");
	var d1 = document.getElementById("d1");
	var addNewTagPosition = document.getElementById("addNewTagPosition");
	divAddNewTag.style.position = "absolute";
//	divAddNewTag.style.left = (getL(d1) +170) + "px";
	divAddNewTag.style.top = (getT(addNewTagPosition) + 40 -245) + "px";
	if (document.uploadform.addNew.value=="upload.closeadd".localize()) {
		document.uploadform.addNew.value="upload.newtag".localize();
		document.getElementById('divAddNewTag').style.display='none'
	} else {
		document.uploadform.addNew.value="upload.closeadd".localize();
		divAddNewTag.style.display = "block";
	}
	document.getElementById("tagName").focus();
}

function getL(e){
	var l = e.offsetLeft;
	while (e = e.offsetParent)
		l += e.offsetLeft;
	return l;
}
function getT(e){
	var t = e.offsetTop;
	while (e = e.offsetParent)
		t += e.offsetTop;
	return t;
}

function createQueryString(opt, tagName, isPub, isFavor) {
	var queryString = "opt=" + opt +  "&tagName=" + encodeURIComponent(tagName) + 
		"&isPub=" + isPub + "&isFavor=" + isFavor;
    return queryString;
}	
function isIe(){
   var i=navigator.userAgent.toLowerCase().indexOf("msie");
   //alert(navigator.userAgent.toLowerCase());
   return i>=0;
}
function isFireFox(){
    var i=navigator.userAgent.toLowerCase().indexOf("firefox");
	return i>=0;
}

function parseResult() {
	if (xmlHttp.readyState == 4) { // judge object status
		if (xmlHttp.status == 200) { // success
			var doc = xmlHttp.responseXML;
			var sysTags = doc.getElementsByTagName("SysTag");
 			var out_group = document.uploadform.sysTag;
 			
 			if ((sysTags!=null) && (sysTags.length!=0)) {
	 			out_group.length = 0;
	 			for (var i = 0; i < sysTags.length; i++)
				{
					var tag = sysTags[i];
					if(isIe()){
						var tagID = (tag.childNodes[0].firstChild==null)?"&nbsp;":tag.childNodes[0].firstChild.nodeValue;
						var tagName = (tag.childNodes[1].firstChild==null)?"&nbsp;":tag.childNodes[1].firstChild.nodeValue;						
					}else{
						//(i+1)*2 -1
						var tagID = (tag.childNodes[1].firstChild==null)?"&nbsp;":tag.childNodes[1].firstChild.nodeValue;
						var tagName = (tag.childNodes[3].firstChild==null)?"&nbsp;":tag.childNodes[3].firstChild.nodeValue;
					}
					var option = document.createElement("OPTION");
					option.text = tagName;
					option.value = tagID;
					out_group.options.add(option);
				}
 			}
  	 		
  	 		var privateTags = doc.getElementsByTagName("PrivateTag");
 			out_group = document.uploadform.privateTag;
 			
 			if ((privateTags!=null) && (privateTags.length!=0)) {
	 			out_group.length = 0;
	 			for (var i = 0; i < privateTags.length; i++)
				{
					var tag = privateTags[i];
					if(isIe()){
						var tagID = (tag.childNodes[0].firstChild==null)?"&nbsp;":tag.childNodes[0].firstChild.nodeValue;
						var tagName = (tag.childNodes[1].firstChild==null)?"&nbsp;":tag.childNodes[1].firstChild.nodeValue;						
					}else{
						//(i+1)*2 -1
						var tagID = (tag.childNodes[1].firstChild==null)?"&nbsp;":tag.childNodes[1].firstChild.nodeValue;
						var tagName = (tag.childNodes[3].firstChild==null)?"&nbsp;":tag.childNodes[3].firstChild.nodeValue;
					}
					var option = document.createElement("OPTION");
					option.text = tagName;
					option.value = tagID;
					out_group.options.add(option);
				}
 			}
 			
 			var shareTags = doc.getElementsByTagName("ShareTag");
 			out_group = document.uploadform.shareTag;
 			
 			if ((shareTags!=null) && (shareTags.length!=0)) {
	 			out_group.length = 0;
	 			for (var i = 0; i < shareTags.length; i++)
				{
					var tag = shareTags[i];
					if(isIe()){
						var tagID = (tag.childNodes[0].firstChild==null)?"&nbsp;":tag.childNodes[0].firstChild.nodeValue;
						var tagName = (tag.childNodes[1].firstChild==null)?"&nbsp;":tag.childNodes[1].firstChild.nodeValue;						
					}else{
						//(i+1)*2 -1
						var tagID = (tag.childNodes[1].firstChild==null)?"&nbsp;":tag.childNodes[1].firstChild.nodeValue;
						var tagName = (tag.childNodes[3].firstChild==null)?"&nbsp;":tag.childNodes[3].firstChild.nodeValue;
					}
					var option = document.createElement("OPTION");
					option.text = tagName;
					option.value = tagID;
					out_group.options.add(option);
				}
 			}
 			
 			var newTags = doc.getElementsByTagName("NewTag");
 			out_group = document.uploadform.tag;
 			
 			if ((newTags!=null) && (newTags.length!=0)) {
	 			for (var i = 0; i < newTags.length; i++)
				{
					var tag = newTags[i];
					if(isIe()){
						var tagID = (tag.childNodes[0].firstChild==null)?"&nbsp;":tag.childNodes[0].firstChild.nodeValue;
						var tagName = (tag.childNodes[1].firstChild==null)?"&nbsp;":tag.childNodes[1].firstChild.nodeValue;						
					}else{
						//(i+1)*2 -1
						var tagID = (tag.childNodes[1].firstChild==null)?"&nbsp;":tag.childNodes[1].firstChild.nodeValue;
						var tagName = (tag.childNodes[3].firstChild==null)?"&nbsp;":tag.childNodes[3].firstChild.nodeValue;
					}
					var option = document.createElement("OPTION");
					option.text = tagName;
					option.value = tagID;
					out_group.options.add(option);
				}
 			}
 			
 			//Error
 			var errorTags = doc.getElementsByTagName("Error");
 			if ((errorTags!=null) && (errorTags.length!=0)) {
 				for (var i=0; i<errorTags.length; i++) {
 					var tag = errorTags[i];
 					if(isIe()){
						var errorCode = (tag.childNodes[0].firstChild==null)?"&nbsp;":tag.childNodes[0].firstChild.nodeValue;
					}else{
						//(i+1)*2 -1
						var errorCode = (tag.childNodes[1].firstChild==null)?"&nbsp;":tag.childNodes[1].firstChild.nodeValue;
					}
					alert(errorCode.localize());
 				}
 			}
		}
	}
}  	

function addTagAjax() {
	var isPub,isFavor,opt;
	opt = "1";
	
	if (document.uploadform.isPub[1].checked) {
		isPub = "off";
	} else {	
		isPub = "on";
	}
    if (document.uploadform.isFavor.checked) {
    	isFavor = "on";
    } else {
	    isFavor = "off";
	}
	var tagName = document.uploadform.tagName.value;
	var ajaxurl = "servlet/TagServlet?timeStamp=" + new Date().getTime();
	var queryString = createQueryString(opt, tagName, isPub, isFavor);
	send_request("post", ajaxurl, queryString, "XML", parseResult);
	document.uploadform.addNew.value="upload.newtag".localize();
	document.getElementById('divAddNewTag').style.display='none';
}
    //default
function fetchTags() {
    var ajaxurl = "servlet/TagServlet?timeStamp=" + new Date().getTime();
	send_request("post", ajaxurl, " ", "XML", parseResult);
}


function checkTags()
{
   checkTagValid = true;
   var tags = document.getElementById("tags");
   var value= tags.value
   var ajaxurl = "upload/handleFile.do?method=checkTags&inputTags="+encodeURI(value);
   send_request("get", ajaxurl, "", "XML", handTagCheckResult);
}
function handTagCheckResult()
{
    if (xmlHttp.readyState == 4) {
		if (xmlHttp.status == 200) { 
		   var domObj = xmlHttp.responseXML;
		   if(domObj){
		     var statusNodes = domObj.getElementsByTagName("status");
	         var statusNode = statusNodes[0];
	         var status = statusNode.firstChild;
               if(status.nodeValue == "invalid"){
                 var descriptionNodes = domObj.getElementsByTagName("description");
                 var descriptionNode = descriptionNodes[0];
                 var description = descriptionNode.firstChild;
                 checkTagValid = false;
                 var descriptionHtml = document.getElementById("description");
                 descriptionHtml.innerHTML=description.nodeValue;
                 checkTagDes = description;
                 var tiptext = document.getElementById("tipforinputtag");
                 tiptext.style.color="red";
               }else{
                 checkTagValid = true;
                 var descriptionHtml = document.getElementById("description");
                 descriptionHtml.innerHTML="";
                 var tiptext = document.getElementById("tipforinputtag");
                 tiptext.style.color="";
               }      
		   }else{
		    alert("The format of xml data is wrong,The original data is :"+httpXML.responseText);
		   }
		}
	}
}


function checkUpdateValue() {
  	   if(!checkTagValid)
	    {
	       alert(checkTagDes.nodeValue);
	       return false;
	    }
	    if (document.updateCLBForm.file.value.trim()==""){
	    	if (!confirm("update.confirm".localize())) {		  
		  		document.updateCLBForm.file.focus();
 		  		return false;
 		  	}
	    }
	    document.updateCLBForm.submit();
 }


