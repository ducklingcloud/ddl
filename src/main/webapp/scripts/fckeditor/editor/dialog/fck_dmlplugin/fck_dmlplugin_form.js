var dialog		= window.parent ;
var oEditor		= dialog.InnerDialogLoaded() ;
var FCK			= oEditor.FCK ;
var FCKLang		= oEditor.FCKLang ;
var FCKConfig	= oEditor.FCKConfig ;
var FCKTools	= oEditor.FCKTools ;
var obj = dialog.Selection.GetSelectedElement() ;
if(obj){
	if(obj.tagName != 'IMG'){
		obj = null;
	}else {
		if( !((obj.className).toUpperCase()=="PLUGIN" )){
			obj = null;
		}
	}
}
//加载数据
function LoadSelection(){
	if ( ! obj ) window.location.href='fck_dmlplugin.html' ;
	//var arrche=obj.getElementsByTagName("span");
	//var che=arrche[0];
	//var getinnerStr=che.innerHTML;//取到plugin内容
	var getinnerStr=obj.title;//取到plugin内容
	var arrStr=getinnerStr.split(";");
	var othernum=2;
	for(i=1;i<arrStr.length;i++){//加行
		if(i>1){
			addinput();
		}
	}
	for(i=0;i<arrStr.length;i++){//填充数据
		if(arrStr[i]!=null&&trim(arrStr[i])!=""){
					var keyvalue=arrStr[i].split("=");
					if(trim(keyvalue[0])=='name'){
							var getNamevalue=trim(keyvalue[1]).replace(/(^\')|(\'$)/g, "");   
							document.getElementById('txtValue1').value=getNamevalue;
					}else{
							var getvalue=trim(String(keyvalue[1])).replace(/(^\')|(\'$)/g, "");
							var getname=trim(String(keyvalue[0])).replace(/(^\')|(\'$)/g, "");
							document.getElementById('txtName'+othernum).value=getname;
							document.getElementById(String('txtValue'+othernum)).value=getvalue;
							othernum++;
					}
				}
	}		
	//填充帮助
	var longname=document.getElementById('txtValue1').value;
	var arrlongname=longname.split(".");
	var helpvalue="";
	if(arrlongname.length >0){
		helpvalue=eval("FCKLang.help"+arrlongname[arrlongname.length-1 ]);
	}
	document.getElementById('addinfo').innerHTML=helpvalue;
}

//把帮助信息放进map
/*
function getHelpMap(){
	var map=new VLAB_Map();
	// Load the XML file.
	var oXml = new oEditor.FCKXml() ;
	//路径是script目录下的fcktemplate.xml
	oXml.LoadUrl( FCKConfig.DMLPluginXmlPath ) ;
	var aTplNodes = oXml.SelectNodes( 'Templates/Template' ) ;
	for ( var i = 0 ; i < aTplNodes.length ; i++ ){
		var o_name,o_value; 
		var oNode = aTplNodes[i] ;
		if ( (o_name = oNode.attributes.getNamedItem('title')) ){
			if((o_value = oXml.SelectSingleNode( 'Help', oNode ))){
				var m_name =o_name.value.substring(13,o_name.value.length);//只所以从8开始取是为了去掉FCKLang.title这段字符串
				var m_value=o_value.text ? o_value.text : o_value.textContent ;
				map.put(m_name,m_value);
			}
		}
	}
	return map;
}
*/



window.onload = function()
{
	LoadSelection();
	oEditor.FCKLanguageManager.TranslatePage(document) ;
	window.parent.SetAutoSize( true ) ;
	dialog.SetOkButton( true ) ;
}
function delinput(obj){
	var table = document.getElementById("addinputtable");
	table.removeChild(document.getElementById(obj))
}
function addinput(){
	var table = document.getElementById("addinputtable");
	var numTr = table.getElementsByTagName("tr").length;
	var newTr=document.createElement('tr');
	newTr.id="newTr"+(numTr);//数字取得的规律是直接取tr的长度，因为在之前有一行tr做了文字说明，而后来的输入tr是用1开头的，所以不用再+1了
	var newNameTd=document.createElement('td');
	var newValueTd=document.createElement('td');
	var newBoxTd=document.createElement('td');
	
	
	var newNameTxt=document.createElement('input');
	newNameTxt.id="txtName"+(numTr);
	newNameTxt.type="text";
	newNameTd.appendChild(newNameTxt);
	
	var newValueTxt=document.createElement('input');
	newValueTxt.id="txtValue"+(numTr);
	newValueTxt.type="text";
	newValueTd.appendChild(newValueTxt);

	var newButton=document.createElement('input');
	newButton.id="button_"+(numTr);
	newButton.type="button";

	newButton.style.background="url(fck_dmlplugin/images/delete.gif) no-repeat";
	newButton.style.width="22px";
	newButton.style.height="22px";
	if(oEditor.FCKBrowserInfo.IsIE){
		newButton.onclick=function () { delinput(newTr.id) }
		}
	else{
		newButton.setAttribute("onclick","javascript:delinput('"+newTr.id+"')");
		}
		
	newBoxTd.appendChild(newButton);
	
	
	newTr.appendChild(newNameTd);
	newTr.appendChild(newValueTd);
	newTr.appendChild(newBoxTd);
	
	table.appendChild(newTr);
	
}


function Ok(){
	var checktype=check();
	if(checktype==false)return false;
	addPlugin();
	return true;
}

function check(){
	var checkinfo=document.getElementById("txtValue1").value;
	if(checkinfo==""||checkinfo==null){
		alert(FCKLang.DMlPluginAlert);
		return false;
	}
	return true;
}

/*
<span class="plugin">
<span class="parameter">property='users';name='SessionsPlugin';</span>
</span>
*/
function addPlugin(){
	oPlugin		= FCK.EditorDocument.createElement( 'img' ) ;
	oPlugin.className="plugin";	
	//oparameter	= FCK.EditorDocument.createElement( 'span' ) ;
	//oparameter.className="parameter";
	
	//oparameter.style.display="none";
	try{
	var innerStr=getInnerStr();
	
	oPlugin.src =obj.getAttribute("src")?obj.getAttribute("src"):"scripts/fckeditor/editor/images/plugin.gif" ;
	oPlugin.title=innerStr;
	//oparameter.innerHTML=innerStr;
	//oPlugin.appendChild(oparameter);

	//oPlugin.innerHTML+="&nbsp;";
	oPlugin.contentEditable = false;
	}catch(e){
		alert("e="+e);
	}
	FCK.InsertElement( oPlugin ) ;
	//FCK.InsertHtml("&nbsp;") ;
}


function getInnerStr(){
	var table = document.getElementById("addinputtable");
	var nTr = table.getElementsByTagName("tr");
	var restr="";
	for(i=1;i<nTr.length;i++){
		var arrIN=nTr[i].getElementsByTagName("input");
		
		var getName=arrIN[0].value;
		var getValue=arrIN[1].value;
		
		if(getName!=""&&getValue!=""&&getName!=null&&getValue!=null){
			restr=restr+trim(getName);
			restr=restr+"='"+trim(getValue);
			restr=restr+"';"
		}

	}
	return restr;
}



function trim(str){  //删除左右两端的空格   
  return str.replace(/(^\s*)|(\s*$)/g, "");   
 }   
 function ltrim(str){  //删除左边的空格   
  return str.replace(/(^\s*)/g,"");   
 }   
 function rtrim(str){  //删除右边的空格   
  return str.replace(/(\s*$)/g,"");   
 }   