/*
 *Duckling Link
 *create by diyanliang 09-7-01
 */

var dialog	= window.parent ;
var oEditor = dialog.InnerDialogLoaded() ;
var FCK			= oEditor.FCK ;
var FCKLang		= oEditor.FCKLang ;
var FCKConfig	= oEditor.FCKConfig ;
var FCKRegexLib	= oEditor.FCKRegexLib ;
var FCKTools	= oEditor.FCKTools ;
var oLink = dialog.Selection.GetSelection().MoveToAncestorNode( 'A' ) ;
if(oEditor.NodeBar._ieobj){
	//Called from nodebar, there is no seleciton
	oLink=oEditor.NodeBar._obj;
	oEditor.NodeBar._ieobj=false;
}

if (oLink){
	FCK.Selection.Release();
	FCK.Selection.SelectNode(oLink);
	FCK.Selection.Save();
}

var fckseleTxt;

// Set the dialog tabs.
//dialog.AddTab( 'DucklingLnkPage',FCKLang.DucklingLnkPage ) ;
//dialog.AddTab( 'DucklingLnkClb', FCKLang.DucklingLnkClb ) ;
dialog.AddTab( 'DucklingLnkUrl', FCKLang.DucklingLnkUrl ) ;
dialog.AddTab( 'DucklingLnkEmail', FCKLang.DucklingLnkEmail ) ;
var m_tabCode='DucklingLnkUrl';
//标签改变时候
function OnDialogTabChange( tabCode ){
	m_tabCode=tabCode;
	dialog.document.getElementById("PopupButtons").style.display="";
	ShowE('DucklingLnkUrl'		, ( tabCode == 'DucklingLnkUrl' ) ) ;
	//ShowE('DucklingLnkPage'	, ( tabCode == 'DucklingLnkPage' ) ) ;
	//ShowE('DucklingLnkClb'	, ( tabCode == 'DucklingLnkClb' ) ) ;
	ShowE('DucklingLnkEmail'	, ( tabCode == 'DucklingLnkEmail' ) ) ;
	if(tabCode == 'DucklingLnkClb'){
		//dialog.document.getElementById('PopupButtons').parentNode.removeChild(dialog.document.getElementById("PopupButtons"))
		dialog.document.getElementById("PopupButtons").style.display="none";
		//dialog.SetAutoSize( "Flex" ) ;
		Flxonlad();
		dialog.SetAutoSize( true ) ;
		return;
	}
	if(tabCode == 'DucklingLnkPage'){
		GetE('txtDucklingLnkPage').focus();
	}
	
	
	dialog.SetAutoSize( true ) ;
}



window.onload = function(){
	oEditor.FCKLanguageManager.TranslatePage(document) ;
	//GetE('DucklingLnkPage').style.display = '' ;
	dialog.SetSelectedTab( 'DucklingLnkUrl' ) ;
	dialog.SetAutoSize( true ) ;
	LoadSelection();
	LoadSelectionLink();
//	alert(tabCode)
//	if(tabCode == 'DucklingLnkPage'){
//		document.getElementById("txtDucklingLnkPage").focus();
//	}
	dialog.SetAutoSize( true ) ;
	dialog.SetOkButton( true ) ;
	
};

//编辑链接时候取到内容处理
function LoadSelection(){
	//得到选中的文字
	var strinner=getInner();
	//选中文字赋值
	if(strinner[0]=='boolean'){
		if(strinner[1]==false){
			GetE('DucklingLnkUrlInner').disabled="disabled";
			GetE('DucklingLnkEmailInner').disabled="disabled";
			GetE('DucklingLnkUrlInner').className="DisableDucklingLnkUrlInner";
			GetE('DucklingLnkEmailInner').className="DisableDucklingLnkUrlInner";
			fckseleTxt='false';
		}else if(strinner[1]==true){
			fckseleTxt='true';
			//nothing to do  but its importance
			GetE('DucklingLnkUrlInner').className="";
			GetE('DucklingLnkEmailInner').className="";
		}
	}else{
			GetE('DucklingLnkUrlInner').value=strinner[1];
			GetE('DucklingLnkEmailInner').value=strinner[1];
			GetE('DucklingLnkUrlInner').className="";
			GetE('DucklingLnkEmailInner').className="";
			fckseleTxt='@'+strinner[1];
	}
	
}
//得到选中的区域文字 如果选中的是多个元素 或者选中的元素中有多个元素  或者选中的元素第一个元素不是文本 返回false
function getInner(){
	//1先判断选中内容是不是只有一个元素
	//2在取选中内容的
	var inner=FCK.Selection.GetSelectionText();
	if(!inner)return ["boolean",true];
	//查看选中文本中有没有换行
	if(inner.indexOf("\r\n")!=-1||inner.indexOf("\n")!=-1)return ["boolean",false];
	
	return ["text",inner];
/*
		var inner=false;
		var range = new oEditor.FCKDomRange( oEditor.FCK.EditorWindow ) ;
		var oDoc = oEditor.FCK.EditorDocument ;
		//dialog.Selection.GetSelection() ;
		range.MoveToSelection() ;
		m_RangeItr=new oEditor.FCKDomRangeIterator(range);
		var rangelength=0;
		var Fobj;
		//遍历选中的区域 取得长度
		while ((m_obj = m_RangeItr.GetNextParagraph())){
			rangelength++;
			Fobj=m_obj;
		}
		alert("F选中的元素长度为"+rangelength)
		//如果选中的只是一个父元素
		if(rangelength==1){//如果选中的内容中只有一个元素
			alert("F选中的元素类型"+Fobj.nodeType+"父元素是"+Fobj)
			if(Fobj.nodeType==3){//如果这个元素师是本类型
				inner=Fobj.nodeValue;
			}else if(Fobj.nodeType==1){//如果这个元素是元素类型
				var childnodes = Fobj.childNodes;
				alert("C选中的元素子元素长度为"+childnodes.length)
				if(childnodes.length==1){//如果他的子元素只有一个
					alert("C选中的元素子元素类型"+Fobj.firstChild.nodeType+"|Fobj.firstChild="+Fobj.firstChild)
					if(Fobj.firstChild.nodeType==3){
						inner=Fobj.firstChild.nodeValue;
					}else{
						if(oLink){//如果选中的元素是A会出现这种情况！元素的第一个元素是元素而不是内容文本
							inner = oLink.innerHTML 
						}else{
							inner=true;
						}
					}
				}
			}

		}
		return inner;
*/
}

//#### Regular Expressions library.

var oRegex = new Object() ;

oRegex.UriProtocol = /^(((http|https|ftp|baseurl):\/\/)|mailto:)/gi ;

oRegex.UrlOnChangeProtocol = /^(http|https|ftp|baseurl):\/\/(?=.)/gi ;

oRegex.UrlOnChangeTestOther = /^((javascript:)|[#\/\.])/gi ;

oRegex.ReserveTarget = /^_(blank|self|top|parent)$/i ;

oRegex.PopupUri = /^javascript:void\(\s*window.open\(\s*'([^']+)'\s*,\s*(?:'([^']*)'|null)\s*,\s*'([^']*)'\s*\)\s*\)\s*$/ ;

// Accessible popups
oRegex.OnClickPopup = /^\s*on[cC]lick="\s*window.open\(\s*this\.href\s*,\s*(?:'([^']*)'|null)\s*,\s*'([^']*)'\s*\)\s*;\s*return\s*false;*\s*"$/ ;

oRegex.PopupFeatures = /(?:^|,)([^=]+)=(\d+|yes|no)/gi ;


function LoadSelectionLink(){
	if ( !oLink ) return ;
	var sHRef = oLink.getAttribute( '_fcksavedurl' ) ;
	if ( sHRef == null )
		sHRef = oLink.getAttribute( 'href' , 2 ) || '' ;
	var viewKey;
	if((_result = top.site.resolve(sHRef))!=null){
		_type=_result.type
		if(_type=="view")
			viewKey=_result.key
	}
	
	
	
	
	var sProtocol = oRegex.UriProtocol.exec( sHRef ) ;
	if ( sProtocol ){//是连接
		
		
		
		sProtocol = sProtocol[0].toLowerCase() ;
		//GetE('cmbLinkProtocol').value = sProtocol ;
		//var sUrl = sHRef.replace( oRegex.UriProtocol, '' ) ;
		var sUrl = sHRef
		if ( sProtocol == 'mailto:' ){	// 如果是邮件
			dialog.SetSelectedTab( 'DucklingLnkEmail' ) ;
			dialog.SetAutoSize( true ) ;
			GetE('txtDucklingLnkEmail').value = sUrl ;
			var title=oLink.getAttribute( 'title' );
			if(title&&title!='null'){
				GetE('DucklingLnkUrlTooltips').value = title ;
				GetE('DucklingLnkPageTooltips').value = title ;
				GetE('DucklingLnkEmailTooltips').value = title ;
			}
		}else{//其他链接
			dialog.SetSelectedTab( 'DucklingLnkUrl' ) ;
			GetE('txtDucklingLnkUrl').value = sUrl ;
			
			var target=oLink.getAttribute( 'target' );
			if(target&&target=="_blank"){
				GetE('DucklingLnkUrlBlock').checked=true;
				GetE('DucklingLnkPageBlock').checked=true;
			}
			
			var title=oLink.getAttribute( 'title' );
			if(title&&title!='null'){
				GetE('DucklingLnkUrlTooltips').value = title ;
				GetE('DucklingLnkPageTooltips').value = title ;
				GetE('DucklingLnkEmailTooltips').value = title ;
			}
		}
	}else {//是附件和页面
		if(sHRef.indexOf("attach/")!=-1){
			dialog.SetSelectedTab( 'DucklingLnkClb' ) ;
			dialog.SetAutoSize( true ) ;
		}else{
			GetE('ResourceId').value=sHRef
			dialog.SetSelectedTab( 'DucklingLnkPage' ) ;
			dialog.SetAutoSize( true ) ;
			var target=oLink.getAttribute( 'target' );
			if(target&&target=="_blank"){
				GetE('DucklingLnkUrlBlock').checked=true;
				GetE('DucklingLnkPageBlock').checked=true;
			}
			
			var title=oLink.getAttribute( 'title' );
			if(title){
				GetE('DucklingLnkUrlTooltips').value = title ;
				GetE('DucklingLnkPageTooltips').value = title ;
				GetE('DucklingLnkEmailTooltips').value = title ;
				GetE('txtDucklingLnkPage').value=title
			}
		}
	
	}
}
//点击确定以后
function Ok(){
	var sUri, sInnerHtml ;
	oEditor.FCKUndo.SaveUndoStep() ;
	//根据tag类型取录入框的地址
	if(m_tabCode == 'DucklingLnkUrl'){
		sUri = GetE('txtDucklingLnkUrl').value ;
		if ( sUri.length == 0 )
			{
				alert( FCKLang.DlnLnkMsgNoUrl ) ;
				return false ;
			}
		
		var sProtocol = oRegex.UriProtocol.exec( sUri ) ;
		var b=sUri.match("((http|https|ftp|baseurl):\/\/)");
		if(!b){
			sUri="http://"+sUri;
		}
		//sUri = GetE('cmbLinkProtocol').value + sUri ;
	}else if(m_tabCode == 'DucklingLnkEmail'){
		sUri = GetE('txtDucklingLnkEmail').value ;
		if ( sUri.length == 0 )
			{
				alert( FCKLang.DlnLnkMsgNoUrl ) ;
				return false ;
			}
		var b=sUri.match("mailto:")
		if(!b){
			sUri="mailto:"+sUri;
		}
	}
	//得到选中的内容
	var aLinks = oLink ? [ oLink ] : oEditor.FCK.CreateLink( sUri, true ) ;
	//如果没有区被选中或者没有链接地址，我们用url做文本显示
	var aHasSelection = ( aLinks.length > 0 ) ;
	if (!aHasSelection ){
		sInnerHtml = GetE('txtDucklingLnkPage').value;
		if(m_tabCode == 'DucklingLnkUrl'){
			var oLinkPathRegEx = new RegExp("//?([^?\"']+)([?].*)?$") ;
			var asLinkPath = oLinkPathRegEx.exec( sUri ) ;
			if (asLinkPath != null)
				sInnerHtml = asLinkPath[1]; 
		}
		aLinks = [ oEditor.FCK.InsertElement( 'a' ) ] ;
		
	}
	
	for ( var i = 0 ; i < aLinks.length ; i++ ){
		oLink = aLinks[i] ;
		if ( aHasSelection )
			sInnerHtml = oLink.innerHTML ;	

		oLink.href = sUri ;
		SetAttribute( oLink, '_fcksavedurl', sUri ) ;
		oLink.innerHTML = sInnerHtml ;
	}
	//提示信息和弹出窗口的处理
	if(m_tabCode == 'DucklingLnkUrl'){
		if(GetE('DucklingLnkUrlBlock').checked){
			SetAttribute( oLink, 'target', '_blank') ;
		}else{
			SetAttribute( oLink, 'target', null ) ;
		}
		var DucklingLnkUrlTooltips=GetE('DucklingLnkUrlTooltips').value;
		if(DucklingLnkUrlTooltips.length>0){
			SetAttribute( oLink, 'title', DucklingLnkUrlTooltips) ;
		}else{
			SetAttribute( oLink, 'title', null) ;
		}
	}else if(m_tabCode == 'DucklingLnkEmail'){

		var DucklingLnkUrlTooltips=GetE('DucklingLnkEmailTooltips').value;
		if(DucklingLnkUrlTooltips.length>0){
			SetAttribute( oLink, 'title', DucklingLnkUrlTooltips) ;
		}else{
			SetAttribute( oLink, 'title', null) ;
		}
	}
	//显示内容处理
	if(m_tabCode == 'DucklingLnkUrl'){
		if(!GetE('DucklingLnkUrlInner').disabled){
			if(GetE('DucklingLnkUrlInner').value.length>0)
				oLink.innerHTML = GetE('DucklingLnkUrlInner').value ;
		}
	}else if(m_tabCode == 'DucklingLnkEmail'){
		if(!GetE('DucklingLnkEmailInner').disabled){
			if(GetE('DucklingLnkEmailInner').value.length>0)
				oLink.innerHTML = GetE('DucklingLnkEmailInner').value ;
			else{
				oLink.innerHTML =sUri
			}
				
		}
	}
	oEditor.FCKSelection.SelectNode( aLinks[0] );
	return true ;
}


function FlexCancel(){
	window.parent.CloseDialog();
	//return true;
}

//为了附件上传功能做的addLink
function addLink(fileUrl, url) {
	var aLinks=oEditor.FCK.CreateLink( ' haha ', true );
	var sig = document.createElement( 'span' );
	sig.innerHTML = fileUrl;
	aLinks.push ( sig );	

	var sUri, sInnerHtml ;

	sUri = uploadFileName ;
	
	var aHasSelection = ( aLinks.length > 1 ) ;
	if ( !aHasSelection )
	{
		sInnerHtml = sUri;

		var oLinkPathRegEx = new RegExp("//?([^?\"']+)([?].*)?$") ;
		var asLinkPath = oLinkPathRegEx.exec( sUri ) ;
		if (asLinkPath != null)
			sInnerHtml = asLinkPath[1];  // use matched path
			
		aLinks = [ oEditor.FCK.InsertElement( 'a' ),  oEditor.FCK.InsertElement( 'span' )] ;
		
	}
	//edit by diyanliang 09-8-25
	//for ( var i = 0 ; i < 1 ; i++ )
	for ( var i = 0 ; i < aLinks.length-1 ;  i++ )
	{
		oLink = aLinks[i] ;
		if ( aHasSelection )
			sInnerHtml = oLink.innerHTML ;		// Save the innerHTML (IE changes it if it is like an URL).

		oLink.href = url ;
		SetAttribute( oLink, '_fcksavedurl', url ) ;
	
		oLink.innerHTML = sInnerHtml ;		// Set (or restore) the innerHTML
		
		// Target
		SetAttribute( oLink, 'target', null ) ;
		
	}	
	
	if (fileUrl && fileUrl.length > 0) {
		if ( oEditor.FCKBrowserInfo.IsIE ){
			aLinks[0].outerHTML=aLinks[0].outerHTML+fileUrl;
			return true;
			}
		else {
			aLinks[1].innerHTML = fileUrl;
			aLinks[0].parentNode.insertBefore(aLinks[1], aLinks[0].nextSibling);
		}
	}
	
	// Select the (first) link.
	oEditor.FCKSelection.SelectNode( aLinks[0] );
	
	return true;
}
 

function changeCookie() {	
	if (!GetE('signature').checked) {
		setCookie('signature', 'off');
	}
	else {
		setCookie('signature', 'on');
	}
}

function refreshCookie() {
	var sig = getCookie('signature', '');

	if ( (sig) && (sig == 'on')) {
		GetE('signature').checked = true;
	}
	else {
		GetE('signature').checked = false;
	}

}

function setCookie(name, value)
{
	  var Days = 365; 
	  var exp  = new Date();    //new Date("December 31, 9998");
	  exp.setTime(exp.getTime() + Days*24*60*60*1000);
	  document.cookie = name + "="+ escape(value) +";expires="+ exp.toGMTString();
}
function getCookie(name)
{
	  var arr = document.cookie.match(new RegExp("(^| )"+name+"=([^;]*)(;|$)"));
	  if(arr != null) return unescape(arr[2]); return null;
}
function delCookie(name)
{
	  var exp = new Date();
	  exp.setTime(exp.getTime() - 1);
	  var cval=getCookie(name);
	  if(cval!=null) document.cookie=name +"="+cval+";expires="+exp.toGMTString();
}

 
 
//Flex控件点击确定后调用
function OnUploadCompleted( errorNumber, fileUrl, fileName, customMsg, url ){
	switch ( errorNumber ){
		case "0" :	// No errors
			uploadFileName = fileName;			
			break ;
		case "1" :	// Custom error
			alert( customMsg ) ;
			return ;
		case "101" :	// Custom warning
			alert( customMsg ) ;
			break ;
		case "201" :
			alert( 'A file with the same name is already available. The uploaded file has been renamed to "' + fileName + '"' ) ;
			break ;
		case "202" :
			alert( 'Invalid file type' ) ;
			break ;
		default :
			alert(customMsg);			
	}
	
	if (uploadFileName && uploadFileName!=""){	
		var newurl="";
		if(url!=null){
			newurl=url;
		}
		addLink(fileUrl, newurl);	
		window.parent.CloseDialog();
	}	
} 

$().ready(function() {
$("#txtDucklingLnkPage").autocomplete(FCKConfig.DucklingBaseHref+'allResource',
		{
			
			parse: function(data) { //|必选
				return $.map(data, function(row) { 
					return {data: row, value: row.title,m_value:row.pid} //value是input框显示的结果,m_value是自定义内容 可以让后续方法用
				}); 
			}, 
			formatItem: function(row, i, max) {//显示效果|必选
				return "<td><div style=\"width:150;height:20px;overflow:hidden\">" + row.title+ "</div></td><td>" + row.pid +"</td>";
			},
			
			extraParams: {query:function(){return $('#txtDucklingLnkPage').val();},datatype:"json"},//ajax传参数|用ajax时候必选
			dataType: "json", //ajax数据结构只能是json|用ajax时候必选
			
			onSelected:onselectbackcall,//选择后处理方法 参数row
			autoFill: true,//自动填充
			scroll:true,//是否加滚动条
			scrollHeight:100,//有滚动条时候下拉框长度
			delay:10,//延迟10秒  
			max:100//下拉列表显示的数据长度
			
		}
	);
})

function onselectbackcall(row){
	document.getElementById("ResourceId").value=row.m_value;
}
 /*
//Page标签下自动搜索
Ext.onReady(function(){
	 var ds = new Ext.data.Store({
        proxy: new Ext.data.HttpProxy({
            url: FCKConfig.DucklingBaseHref+'allResource',
			method: 'POST'
        }),
        reader: new Ext.data.XmlReader({
               record: 'page',
               totalRecords: '@total'
           	}, [
           	    {name:'id', mapping:'id'},
           	    'title','type'
			])
		});

    // Custom rendering Template
    var resultTpl = new Ext.XTemplate(
        '<table><tpl for="."><tr class="search-item" ><td title="{title}"><div style="width:150;overflow:hidden">{title}({id})</div></td><td >{type}</td></tr></tpl></table>'
    );

   	var inputBox = "txtDucklingLnkPage";
   	if ((inputBox)) {
   	var search = new Ext.form.ComboBox({
		hiddenName:"ResourceId",
		valueField : 'id',//值
   		listWidth: 250,
        store: ds,
        displayField:'title',
        typeAhead: true,
        loadingText: 'loading...',
        width: 250,
        hideTrigger:true,
        tpl: resultTpl,
        applyTo: inputBox,
        minChars:1,
        itemSelector: 'tr.search-item',
        lazyInit:true,
        //triggerAction: 'all',
        selectOnFocus: true
    });
    }
   	
});
*/
function getResourceId(rid,rtitle){
	document.getElementById("ResourceId").value=rid;
//	document.getElementById("DucklingLnkPageTooltips").value=rtitle;
}