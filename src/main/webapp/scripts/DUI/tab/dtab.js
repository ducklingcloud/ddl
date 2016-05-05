/**
 * Automatically Create Switch Tag & Automatically Generated Transitions
 * 自动创建切换标签项,并自动生成切换效果
 * author:Dylan
 * date:2010-5-11
 * Example:
   window.onload = function(){
	 	AddTab("title","SiteConfig","SiteConfig") 
		AddTab("title","ClbConfig","ClbConfig")
		ShowDivByTab("SiteConfig")
	} 
  <div id="title"></div>
  <div id="SiteConfig" align="center" width="100%" style="DISPLAY: none">SiteConfigDiv</div>
  <div id="ClbConfig" align="center" width="100%" style="DISPLAY: none">ClbConfigDiv</div>
 * 
 */	

/*
 * 参数(标签容器id,显示容器id,显示文字)
 * (containerid,contentid,tabText)
 */
function AddTab(containerid ,contentid ,tabText ){
		var containeTableId="containeTable";
		var containeTable=document.getElementById(containeTableId);
		if(!containeTable){
			containeTable=document.createElement( 'TABLE' ) ;
			containeTBODY=document.createElement( 'TBODY' ) ;
			containeTable.width="100%";
			containeTable.id="containeTable"
			containeTable.setAttribute('cellSpacing','0');   
			containeTable.setAttribute('cellPadding','0');   
			containeTable.setAttribute('border','0');   
			containeTR=document.createElement( 'TR' ) 
			containeTR.id="containeTR"
			containeTBODY.appendChild( containeTR )
			containeTable.appendChild(containeTBODY);
			document.getElementById(containerid).appendChild(containeTable);
			emptyTD = containeTR.insertCell(containeTR.cells.length - 1) ;
			emptyTD.className="PopupTabEmptyArea";
			emptyTD.width="100%";
			emptyTD.innerHTML="&nbsp;";
		}else{
			containeTR=document.getElementById("containeTR")
		}
		if(contentid){
			var containeTD = containeTR.insertCell(  containeTR.cells.length - 1 ) ;
			containeTD.setAttribute('noWrap','-1'); 
			var oDiv = document.createElement( 'DIV' ) ;
			oDiv.className = 'PopupTab' ;
			oDiv.innerHTML = tabText ;
			oDiv.TabCode = contentid ;
			oDiv.onclick = OnSelectedTab ;
			containeTD.appendChild( oDiv ) ;
		}
}

var OnSelectedTab=function(){
   		ShowDivByTab(this.TabCode) ;
}
/*
 * 参数(显示容器id)
 * (contentid)
 */
var ShowDivByTab=function(contentid){
   		containeTR=document.getElementById("containeTR");
   		Tds=containeTR.cells;
		for (i=0;i<Tds.length;i++){
			oDiv=Tds[i].firstChild;
			var divId=oDiv.TabCode;
			if(document.getElementById(divId))
				document.getElementById(divId).style.display = 'none' ;
		}
		document.getElementById(contentid).style.display = 'block' ;
		ChangeSelectedTab(contentid);
}

var ChangeSelectedTab = function(TabCode){
   		containeTR=document.getElementById("containeTR");
   		Tds=containeTR.cells;
		for (i=0;i<Tds.length;i++){
			oDiv=Tds[i].firstChild;
			if(oDiv.TabCode){
				if(oDiv.TabCode==TabCode){
					oDiv.className = 'PopupTabSelected' ;
				}else{
					oDiv.className = 'PopupTab' ;
				}
			}
		}
}