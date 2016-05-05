/**
 * diyanliang@cnic.cn 
 * 
 * 2011-5-10
 */

var CellSelection={
	
		"BLOCK_HEIGHT":30,
		"_doc" : document,
		"_SelecetColICO":null,
		"_m_td":null,
		"_m_table":null,
		"_ShowSelectionIcon" : function( w, _index )
		{
			//先生成选择按钮
			
			if ( this._SelecetColICO == null )
			{
				
				this._SelecetColICO = this._doc.createElement( "div" ) ;
				var paddingColBar = this._SelecetColICO ;
				var paddingColStyles = { 'position' : 'absolute', 'cursor' : 'pointer' } ;
				FCKDomTools.SetElementStyles( paddingColBar, paddingColStyles ) ;
				this._avoidStyles( paddingColBar );
				paddingColBar.setAttribute('_fcktemp', true);
				this._doc.body.appendChild( paddingColBar ) ;
				FCKTools.AddEventListener( paddingColBar, "mouseup", this._SelecetColMouseDownListener ) ;
			}
			var paddingColBar = this._SelecetColICO ;
			fristColindex=w[_index[0]][0]
			fristRowindex=w[0][_index[1]]
           	//先找选择列的位置
			                   
            var offset = this._GetIframeOffset() ;
			var fristRowindexPos = this._GetTDPosition(  FCK.EditorWindow  , fristRowindex ) ;
			selecetColICOposX=offset.x+ fristRowindexPos.x+(fristRowindex.clientWidth/2)
			selecetColICOposY=offset.y+ fristRowindexPos.y
			
			
			var paddingColStyles =
			{
				'backgroundImage' :'url("'+FCKConfig.BasePath + 'images/arrdown.png'+'")',
				'backgroundRepeat':"no-repeat",
				'top'		: selecetColICOposY-20 + 'px',
				'height'	:20+'px',
				'width'	:20+'px',
				'left'		: selecetColICOposX-10+ 'px'
			} ;

			FCKDomTools.SetElementStyles( paddingColBar, paddingColStyles ) ;
			
			var visibleColBar = null ;
			if ( paddingColBar.getElementsByTagName( "div" ).length < 1 )
			{
				visibleColBar = this._doc.createElement( "div" ) ;
				this._avoidStyles( visibleColBar );
				visibleColBar.setAttribute('_fcktemp', true);
				paddingColBar.appendChild( visibleColBar ) ;
			}
			else
				visibleColBar = paddingColBar.getElementsByTagName( "div" )[0] ;

			FCKDomTools.SetElementStyles( visibleColBar,
				{
					position		: 'absolute',
					height	:1+'px',
					width	:1+'px',
					left			: '10px',
					top				: '0px'
				} ) ;
			
//			alert(fristColindex.innerHTML)
		},"_HideSelectionIcon":function(){
			if ( this._SelecetColICO ){
				FCKDomTools.SetElementStyles( this._SelecetColICO,
						{
							top		: '-100000px',
							left	: '-100000px'
						} ) ;
			}
		},"MouseMoveListener" : function( FCK, evt )
		{
				return CellSelection._MouseFindHandler( FCK, evt ) ;
		},
		"_MouseFindHandler" : function( FCK, evt )
		{
			

			if ( FCK.MouseDownFlag )
				return ;
			var node = evt.srcElement || evt.target ;
			try
			{
				if ( ! node || node.nodeType != 1 )
				{
					this._HideAddRowBar() ;
					return ;
				}
			}
			catch ( e )
			{
				this._HideAddRowBar() ;
				return ;
			}

			var mouseX = evt.clientX ;
			var mouseY = evt.clientY ;
			if ( FCKTools.GetElementDocument( node ) == document )
			{
				var offset = this._GetIframeOffset() ;
				mouseX -= offset.x ;
				mouseY -= offset.y ;
			}

			
			
			//先找到当前TD
			var std = FCKTools.GetElementAscensor( node, "td" ) ;
			//找到当前Table
			var checktable = FCKTools.GetElementAscensor( node, "table" ) ;
			
			if(!checktable){//不在表格内
				if(this._m_table){
					try{
						tablePos=FCKTools.GetWindowPosition( FCK.EditorWindow, this._m_table)
						tablebottom=tablePos.y;
						if(mouseY<(tablebottom-this.BLOCK_HEIGHT)){
							this._m_table=null;
						}
					}catch(e){
						this._m_table=null;
					}
				}
			}else{
				this._m_table=checktable;
			}
			
			
			if(this._m_table){
				if(std){
					var arr=FCKTableHandler._CreateTableMap(std.parentNode.parentNode);
					var _index=testgetArrIndex(arr,std)
					this._m_td=std
					this._ShowSelectionIcon(arr,_index)
					
				}
			}else{
				this._HideSelectionIcon();
			}
			
//			if(std){
//				
//				var arr=FCKTableHandler._CreateTableMap(std.parentNode.parentNode);
//				var _index=testgetArrIndex(arr,std)
//				this._ShowSelectionIcon(arr,_index)
//			}else{
//				this._HideSelectionIcon();
//			}
			
			
//			/**
//			 * 确定当前操纵表格start
//			 */
//			//检查当前光标在不在表格内
//			if(!checktable){
//				if(this._m_table){
//					try{
//						tablePos=FCKTools.GetWindowPosition( FCK.EditorWindow, this._m_table)
//						tablebottom=tablePos.y+this._m_table.clientHeight;
//						if(mouseY>(tablebottom+this.BLOCK_HEIGHT)){
//							
//							this._m_table=null;
//						}
//					}catch(e){
//						this._m_table=null;
//					}
//				}
//			}else{//当前光标在表格内
//				this._m_table=checktable;
//			}
//			
//			/**
//			 * 确定当前操纵表格end
//			 */
//			
//			
//			/**
//			 * 执行显示或者隐藏start
//			 */
//			
//			
//			if(this._m_table){
//				this._ShowAddRowBar( FCK.EditorWindow,this._m_table) ;
//				this._ShowAddColBar( FCK.EditorWindow,this._m_table) ;
//			}else{
//				this._HideAddRowBar() ;
//				this._HideAddColBar() ;
//			}
//				return;
		
				

		},"_AddRowBarMouseDownListener":function(){
//			alert("_AddRowBarMouseDownListener")
			
			FCKTableHandler.InsertLastRow(AddTableRow._m_table);
			AddTableRow._HideAddRowBar();
			AddTableRow._HideAddColBar() ;
			return ;
		},"_SelecetColMouseDownListener":function(){
			
			
			std=CellSelection._m_td
			var arr=FCKTableHandler._CreateTableMap(std.parentNode.parentNode);
			var _index=testgetArrIndex(arr,std)
			var oSel = FCKSelection.GetSelection()
			FCKSelection.SelectNode( CellSelection._m_table);
//			
//			
//			for(i=0;i<arr.length;i++){
//				//ff
//				var oRange = FCK.EditorDocument.createRange() ;
//				oRange.selectNodeContents( arr[i][_index[1]] ) ;
//				oSel.addRange( oRange ) ;
//				
//				
//			}
			
			
//			var oRange ;
//			try
//			{
//				// Try to select the node as a control.
//				oControlRange = FCK.EditorDocument.body.createControlRange() ;
//				
//				var buttons =FCK.EditorDocument.body.getElementsByTagName ("input");
//				
//				for (var i = 0; i < buttons.length; i++) {
//					  alert(i)
//					  oControlRange.add(buttons[i]);
//	            }
//
//
//
////				for(i=0;i<arr.length;i++){
////				}
//					
//
//			}
//			catch(e)
//			{
//				alert(e)
//				// If failed, select it as a text range.
//				
//				
//			}
//
//		oControlRange.select() ;
			return ;
		},	"_GetTDPosition" : function ( w, td )
		{

			//alert("_GetTablePosition")
		
			return FCKTools.GetWindowPosition( w, td ) ;
		},"_GetIframeOffset" : function ()
		{

			//alert("_GetIframeOffset")
		
			return FCKTools.GetDocumentPosition( window, FCK.EditingArea.IFrame ) ;
		},"_avoidStyles" : function( element )
		{

			//alert("_avoidStyles")
		
			
			FCKDomTools.SetElementStyles( element,
				{
					padding		: '0',
//					backgroundImage	: 'none',
					border		: '0'
				} ) ;
		}
}



FCK.Events.AttachEvent( "OnMouseMove", CellSelection.MouseMoveListener ) ;