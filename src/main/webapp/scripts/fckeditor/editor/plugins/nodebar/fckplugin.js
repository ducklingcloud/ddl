/**
 * create by dylan (diyanliang@cnic.cn) 2011-6-8
 * 
 * @param tagName
 * @return
 */

function initNodeBar(node,evt){
	if(obj=getAncestorNode(node,"IMG")){
		_src=obj.src;
		if(_src.indexOf("file://")!=-1){
			return
		}
		if(obj2=getAncestorNode(node,"A")){
			NodeBar.show(evt,FCK.EditorWindow,obj,450,30);
			setupNodeBarButton("IMG&A");
		}else{
			NodeBar.show(evt,FCK.EditorWindow,obj,200,30);
			setupNodeBarButton(obj.tagName);
		}
	}else if(obj=getAncestorNode(node,"A")){
		NodeBar.show(evt,FCK.EditorWindow,obj,350,30);
		setupNodeBarButton(obj.tagName);
	}else{
		NodeBar.hidden(evt);
	}
	
}

function setupNodeBarButton(tagName){
	NodeBar.RemoveAllButton();
	
	/*
	 * 由于时间紧，这段代码写的不漂亮 其实应该将需要添加的按钮放到一个数组里，然后再统一遍历这个数组统一生成菜单， 这样就节省了一个个判断的过程
	 * 以后有时间再修改 diyanliang 2011-5
	 */
	if(tagName=="IMG"){
		_src=NodeBar._obj.src;
		if(_src.indexOf(FCKConfig.DucklingBaseHref)!=-1){
			
			NodeBar.AddButton(37,FCKLang.OpenIMG,FCKLang.OpenIMG,openIMG,2);
		}else{
			NodeBar.AddButton(44,FCKLang.DownLoadIMG,FCKLang.DownLoadIMG,downLoadImg,2);
			NodeBar.AddButton(37,FCKLang.OpenIMG,FCKLang.OpenIMG,openIMG,2);
		}
	}else if(tagName=="A"){
		_herf=NodeBar._obj.href;
		if((result = top.site.resolve(_herf))!=null){
			if(result.type=='attach'||result.type=='cachable'||result.type=='file'){
				NodeBar.AddButton(35,FCKLang.RemoveLink,FCKLang.RemoveLink,removeLink,2);
				NodeBar.AddButton(54,FCKLang.OpenAttachLink, FCKLang.OpenAttachLink+":"+getLink(),openLink,3);
				return
			}else if(result.type=='view'||result.type=='edit'){
				NodeBar.AddButton(54,FCKLang.EditPageLink,FCKLang.EditPageLink,popLinkWindow,2);
				NodeBar.AddButton(54,FCKLang.EditPage, FCKLang.EditPage,openEditPage,3);
				NodeBar.AddButton(35,FCKLang.RemoveLink,FCKLang.RemoveLink,removeLink,2);
				NodeBar.AddButton(54,FCKLang.OpenLink, FCKLang.OpenLink+":"+getLink(),openLink,3);
				return
			}
			
		}

		NodeBar.AddButton(34,FCKLang.EditLink,FCKLang.EditLink,popLinkWindow,2);
		NodeBar.AddButton(35,FCKLang.RemoveLink,FCKLang.RemoveLink,removeLink,2);
		NodeBar.AddButton(54,FCKLang.OpenLink, FCKLang.OpenLink+":"+getLink(),openLink,3);
	}else if(tagName=="IMG&A"){
		_src=NodeBar._obj.src;
		if(_src.indexOf(FCKConfig.DucklingBaseHref)!=-1){
			NodeBar.AddButton(37,FCKLang.OpenIMG,FCKLang.OpenIMG,openIMG,2);
// NodeBar.AddButton(34,FCKLang.EditLink,FCKLang.EditLink,popLinkWindow,2)
// NodeBar.AddButton(35,FCKLang.RemoveLink,FCKLang.RemoveLink,removeLink,2)
// NodeBar.AddButton(54,FCKLang.OpenLink,
// FCKLang.OpenLink+":"+getLink(),openLink,3)
		}else{
			NodeBar.AddButton(44,FCKLang.DownLoadIMG,FCKLang.DownLoadIMG,downLoadImg,2);
			NodeBar.AddButton(37,FCKLang.OpenIMG,FCKLang.OpenIMG,openIMG,2);
// NodeBar.AddButton(34,FCKLang.EditLink,FCKLang.EditLink,popLinkWindow,2)
// NodeBar.AddButton(35,FCKLang.RemoveLink,FCKLang.RemoveLink,removeLink,2)
// NodeBar.AddButton(54,FCKLang.OpenLink,
// FCKLang.OpenLink+":"+getLink(),openLink,3)
		}
		
		
		
		_herf=getAncestorNode(NodeBar._obj,"A").href;
		if((result = top.site.resolve(_herf))!=null){
			if(result.type=='attach'||result.type=='cachable'||result.type=='file'){
				NodeBar.AddButton(35,FCKLang.RemoveLink,FCKLang.RemoveLink,removeLink,2);
				NodeBar.AddButton(54,FCKLang.OpenAttachLink, FCKLang.OpenAttachLink+":"+getLink(),openLink,3);
				return
			}else if(result.type=='view'||result.type=='edit'){
				NodeBar.AddButton(54,FCKLang.EditPageLink,FCKLang.EditPageLink,popLinkWindow,2);
				NodeBar.AddButton(54,FCKLang.EditPage, FCKLang.EditPage,openEditPage,3);
				NodeBar.AddButton(35,FCKLang.RemoveLink,FCKLang.RemoveLink,removeLink,2);
				NodeBar.AddButton(54,FCKLang.OpenLink, FCKLang.OpenLink+":"+getLink(),openLink,3);
				return
			}
			
		}
		NodeBar.AddButton(34,FCKLang.EditLink,FCKLang.EditLink,popLinkWindow,2);
		NodeBar.AddButton(35,FCKLang.RemoveLink,FCKLang.RemoveLink,removeLink,2);
		NodeBar.AddButton(54,FCKLang.OpenLink, FCKLang.OpenLink+":"+getLink(),openLink,3);
	}
}




var NodeBar={
	"_nodebar":null,
	"Listeners":[],
	"_Timer":null,
	"_obj":null,
	"RemoveAllButton":function(){
		if(NodeBar._nodebar){
			NodeBar._nodebar.innerHTML="";
		}
	},
	"AddSpecialButton":function(inner){
		this.barplan=document.createElement("div");
		NodeBar._nodebar.appendChild(this.barplan);
		this.barplan.appendChild(inner);
	},
	"AddButton":function(iconPathOrStripInfoArray,displayinfo,titleinfo,functionname,buttontype){
		

		
		this.barplan=document.createElement("div");
		if(titleinfo){
			this.barplan.title=titleinfo;
		}
		NodeBar._nodebar.appendChild(this.barplan);
		var paddingStyles = { 'cursor' : 'pointer','display':'inline' } ;
		this.barplan.style.cssFloat='left';
		FCKDomTools.SetElementStyles( this.barplan, paddingStyles ) ;
		otable=document.createElement("table");
		r=otable.insertRow(-1);
		
		if(buttontype==1){// 纯图片
			D = r.insertCell(-1);
			this.Icon = new FCKIcon( iconPathOrStripInfoArray ) ;
			var _img=this.Icon.CreateIconElement( document );
			var _imgStyles = { 'margin' : '0' } ;
			FCKDomTools.SetElementStyles( _img, _imgStyles ) ;
			D.appendChild(_img ) ;
			
		}else if(buttontype==2){// 图片 +文字
			D = r.insertCell(-1);
			this.Icon = new FCKIcon( iconPathOrStripInfoArray ) ;
			var _img=this.Icon.CreateIconElement( document );
			var _imgStyles = { 'margin' : '0' ,'height':'16px'} ;
			FCKDomTools.SetElementStyles( _img, _imgStyles ) ;
			D.appendChild(_img );  
			D = r.insertCell(-1);
			D.appendChild(document.createTextNode(displayinfo));
		}else if(buttontype==3){// 纯文字
			D = r.insertCell(-1);
			D.appendChild(document.createTextNode(displayinfo));
		}
		this.barplan.appendChild(otable);
		
		FCKTools.AddEventListenerEx(this.barplan, "click", function(){
		
		FCK.Selection.SelectNode( NodeBar._obj ) ;
		NodeBar._ieobj=true;
		}, this);
		FCKTools.AddEventListenerEx(this.barplan, "click", functionname, this);
		FCKTools.AddEventListenerEx(this.barplan, "mouseover", function(){this.className = "TB_Button_Off_Over";}, this);
		FCKTools.AddEventListenerEx(this.barplan, "mouseout", function(){this.className = "TB_Button_Off";}, this);
	},
	"_SetupClickListener":function(){
		FCKTools.AddEventListener(FCK.EditorDocument, 'click', NodeBar._ClickListener, true);
	},"_ClickListener":function(evt){
		node = FCKSelection.GetSelection().focusNode;
		initNodeBar(node,evt);
	},
	"_MouseMoveListener":function(FCK, evt){
		if ( FCK.MouseDownFlag )
			return ;
		var node = evt.srcElement || evt.target ;
		initNodeBar(node,evt);
	},
	"create":function(){
		this._nodebar=document.getElementById("nodebar");
		if(!this._nodebar){
			this._nodebar=document.createElement("div");
			this._nodebar.className="nodebar";
			document.body.appendChild(this._nodebar);
		}
		FCKTools.AddEventListener( this._nodebar, "mouseover", function(){
			window.clearInterval( NodeBar._Timer ) ;
			this._Timer = null ;
		}) ;
	},
	"show":function(evt,w,obj,m_width,m_height){
		
	
		window.clearInterval( NodeBar._Timer ) ;
		this._Timer = null ;
		if(!this._nodebar){
			this.create();
		}
		var offset = this._GetIframeOffset() ;
		var objPos = this._GetObjPosition( w, obj ) ;
		var barTop =offset.y+ objPos.y +obj.offsetHeight+2;
		var barWidth=m_width?m_width:250;
		var barHeight=m_height?m_height:50;
		var barLeft;
		if((objPos.x+barWidth)>FCK.EditingArea.IFrame.offsetWidth){
			barLeft =offset.x+ objPos.x-((objPos.x+barWidth)-FCK.EditingArea.IFrame.offsetWidth)-2;
		}else{
			barLeft =offset.x+ objPos.x;
		}
		var paddingStyles =
		{
			'position' : 'absolute',
			'background':'#F5F5F5',
			'top'		: barTop+'px',
			'height'	: barHeight+'px',
			'width'	: isNaN(barWidth)?barWidth:(barWidth+'px'),
			'left'		: barLeft+'px',
			'border':'1px solid #CCCCCC'
		} ;
		FCKDomTools.SetElementStyles( this._nodebar, paddingStyles ) ;
		NodeBar._obj=obj;

	},"hidden":function(evt){
		if(evt.type=="click"){
			if ( NodeBar._nodebar ){
				FCKDomTools.SetElementStyles( NodeBar._nodebar,
				{
					top		: '-100000px',
					left	: '-100000px'
				} ) ;
			}
		}else{
			window.clearInterval( NodeBar._Timer ) ;
			NodeBar._Timer = null ;
			NodeBar._Timer = window.setInterval( function(){
				if ( NodeBar._nodebar ){
					FCKDomTools.SetElementStyles( NodeBar._nodebar,
							{
							top		: '-100000px',
							left	: '-100000px'
							} ) ;
				}
				
			}, 1000, this ) ;
		}

		
	},	"_GetObjPosition" : function ( w, obj )
	{
		return FCKTools.GetWindowPosition( w, obj ) ;
	},"_GetIframeOffset" : function ()
	{
		return FCKTools.GetDocumentPosition( window, FCK.EditingArea.IFrame ) ;
	}
};

FCK.Events.AttachEvent( "OnMouseMove", NodeBar._MouseMoveListener ) ;
FCK.Events.AttachEvent('OnAfterSetHTML', NodeBar._SetupClickListener);