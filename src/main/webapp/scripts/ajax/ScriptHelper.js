/* ==================== ScriptHelper 开始 ==================== */
/* scriptHelper 脚本帮助对象.
   创建人: ziqiu.zhang  2008.3.5
   添加函数: 
   getScroll():得到鼠标滚过的距离-兼容XHTML
   getClient():得到浏览器当前显示区域的大小-兼容XHTML
   showDivCommon():显示图层.
   
   使用举例:
    <div id="testDiv" style="display:none; position:absolute; border:1px #000000;">我是测试图层我是测试图层</div>
    <div style="width:400px; text-align:center;"><div><a href="#" onclick="ScriptHelper.showDivCommon(this,'testDiv', 20, 70)">事件源</a></div></div>

 */

function scriptHelper()
{
	var moveObj=null;
}


//  得到鼠标滚过的距离 scrollTop 与 scrollLeft  
/*  用法与测试:
    var myScroll = getScroll();
    alert("myScroll.scrollTop:" + myScroll.scrollTop);
    alert("myScroll.scrollLeft:" + myScroll.scrollLeft);
*/
scriptHelper.prototype.getScroll = function () 
{     
        var scrollTop = 0, scrollLeft = 0;
        
        scrollTop = (document.body.scrollTop > document.documentElement.scrollTop)? document.body.scrollTop:document.documentElement.scrollTop;
        if( isNaN(scrollTop) || scrollTop <0 ){ scrollTop = 0 ;}
        
        scrollLeft = (document.body.scrollLeft > document.documentElement.scrollLeft )? document.body.scrollLeft:document.documentElement.scrollLeft;
        if( isNaN(scrollLeft) || scrollLeft <0 ){ scrollLeft = 0 ;}
        
        return { scrollTop:scrollTop, scrollLeft: scrollLeft, scrollHeight:document.body.scrollHeight, scrollWidth:document.body.scrollWidth}; 
}

//停止传递事件
scriptHelper.prototype.stopBubble=function(event){
	if (event)
		event.stopPropagation();
	else
		window.event.cancelBubble = true;
}
//  得到浏览器当前显示区域的大小 clientHeight 与 clientWidth
/*  用法与测试:
    var myScroll = getScroll();    
    alert("myScroll.sTop:" + myScroll.sTop);
    alert("myScroll.sLeft:" + myScroll.sLeft);
*/
scriptHelper.prototype.getClient = function ()
{
    //判断页面是否符合XHTML标准
    var isXhtml = true;
    if( document.documentElement == null || document.documentElement.clientHeight <= 0)
    {
        if( document.body.clientHeight>0 )
        {
            isXhtml = false;
        }
    }
        
    this.clientHeight = isXhtml?document.documentElement.clientHeight:document.body.clientHeight;
    this.clientWidth  = isXhtml?document.documentElement.clientWidth:document.body.clientWidth;            
    return {clientHeight:this.clientHeight,clientWidth:this.clientWidth};        
}
//获取对象相对于Body对象的偏移量坐标.需要在Body元素加上position:relative, 并且保证任何父级元素都没有position:relative
/*  参数说明:
sObj      : 要弹出图层的事件源
    
用法与测试: 
var sObj = document.getElementById("divId");
var position = ScriptHelperV2.getPosition(sObj);
var sObjOffsetTop = parseInt(  position.OffsetTop );
var sObjOffsetLeft = parseInt( position.OffsetLeft );
*/
scriptHelper.prototype.getPosition = function(sObj) {
    var sObjOffsetTop = 0;      //事件源的垂直距离
    var sObjOffsetLeft = 0;     //事件源的水平距离

    /* 获取事件源对象的偏移量 */
    var tempObj = sObj; //用于计算事件源坐标的临时对象
    while (tempObj && tempObj.tagName.toUpperCase() != "BODY") {
        sObjOffsetTop += tempObj.offsetTop;
        sObjOffsetLeft += tempObj.offsetLeft;
        tempObj = tempObj.offsetParent;
    }
    tempObj = null;
    return { OffsetTop: sObjOffsetTop, OffsetLeft: sObjOffsetLeft };
}
//  显示图层,再次调用则隐藏
/*  参数说明:
sObj        : 要弹出图层的事件源
divId       : 要显示的图层ID    
moveTop     : 手工向下移动的偏移量.不移动则为0(默认).
moveLeft    : 手工向左移动的距离.不移动则为0(默认).
    
用法与测试:
<div><a href="#" onclick="ScriptHelperV2.showDivCommon(this,'testDiv', 20, 20)">事件源</a></div>  
*/
scriptHelper.prototype.showDivCommon = function(sObj, divId, moveTop, moveLeft)
{
    //取消冒泡事件
    if (typeof (window) != 'undefined' && window != null && window.event != null) {
        window.event.cancelBubble = true;
    }
    else if (ScriptHelper.showDivCommon.caller.arguments[0] != null) {
        ScriptHelper.showDivCommon.caller.arguments[0].cancelBubble = true;
    }

    //参数检测.如果没有传入参数则设置默认值
    if (moveLeft == null) {
        moveLeft = 0;
    }
    if (moveTop == null) {
        moveTop = 0;
    }

    var divObj = document.getElementById(divId); //获得弹出图层对象    
    var sObjOffsetTop = 0;      //事件源的垂直距离
    var sObjOffsetLeft = 0;     //事件源的水平距离

    var position = this.getPosition(sObj); //获取事件源对象的偏移量
    var myClient = this.getClient();       //获取屏幕大小  
    var myScroll = this.getScroll();       //获取滚动条滚动的距离
    var sWidth = sObj.offsetWidth != null ? parseInt(sObj.offsetWidth) : 0;    //事件源对象的宽度
    var sHeight = sObj.offsetHeight != null ? parseInt(sObj.offsetHeight) : 20; //事件源对象的高度
    var popDivWidth = 0;    //弹出层的宽度
    var popDivHeight = 0;   //弹出层的高度
    var bottomSpace;        //距离底部的距离

    var iframeDivId = "tempIframeDiv" + divId;  //iframe所在div的id
    var iframeId = "tempIframe" + divId;        //iframe的id
    var iframeDiv = document.getElementById(iframeDivId); //iframe所在div对象
    var iframe = document.getElementById(iframeId); //iframe对象


    if (divObj.style.display.toLowerCase() != "none") {
        //隐藏图层
        divObj.style.display = "none";
        //隐藏iframe
        if (iframe != null) {
            iframe.style.display = "none";
        }

        if (iframeDiv != null) {
            iframeDiv.style.display = "none";
        }
    }
    else {
        if (sObj == null) {
            alert("事件源对象为null");
            return false;
        }

        //先显示图层,才能获取到弹出层的长宽
        divObj.style.display = "block";
        popDivWidth = divObj.offsetWidth != null ? parseInt(sObj.offsetWidth) : 0;      //弹出层宽度
        popDivHeight = divObj.offsetHeight != null ? parseInt(divObj.offsetHeight) : 0;  //弹出层高度

        /* 获取距离底部的距离 */
        bottomSpace = parseInt(myClient.clientHeight) - (parseInt(position.OffsetTop) - parseInt(myScroll.scrollTop)) - parseInt(sHeight);

        /* 设置图层显示位置 */
        //如果事件源下方空间不足且上方控件足够容纳弹出层,则在上方显示.否则在下方显示
        if (popDivHeight > 0 && bottomSpace < popDivHeight && position.OffsetTop > popDivHeight) {
            divObj.style.top = (parseInt(position.OffsetTop) - parseInt(popDivHeight)).toString() + "px";
        }
        else {
            divObj.style.top = (parseInt(position.OffsetTop) + parseInt(sHeight)).toString() + "px";

        }
        divObj.style.left = (parseInt(position.OffsetLeft) - parseInt(moveLeft)).toString() + "px";


    }
    
    
    //如果遮盖iframe层不存在则创建
    if (iframe == null) {
        //ie6下使用dom添加节点后无法控制某些属性, 所以将iframe放在一个div中,这样才可以用写html的方式添加. 
        var tempIframeDiv = document.createElement("div");
        tempIframeDiv.setAttribute("id", iframeDivId);
        document.body.appendChild(tempIframeDiv);

        var iframeString = "<iframe id=\"" + iframeId + "\" style=\"position: absolute; display:none; border-width:0px;\"></iframe>";
        tempIframeDiv.innerHTML = iframeString;
        iframe = document.getElementById(iframeId);
        iframeDiv = document.getElementById(iframeDivId);
    }
    //使用遮盖层遮住select控件
    if (iframe != null && iframeDiv != null) {
        iframeDiv.style.display = "block";
        iframe.style.top = divObj.style.top;
        iframe.style.left = divObj.style.left;
        iframe.style.width = divObj.offsetWidth.toString() + "px";
        iframe.style.height = divObj.offsetHeight.toString() + "px";
        iframe.style.display = "block";
        iframe.style.zIndex = divObj.style.zIndex - 1;
    }
}
/*
 * 参数说明
 * 	sDialog: Dialog Div的ID.
 *	sBanner: Dialog banner的ID用于移动dialog
 *  configObj: 可定制的属性包括：
 * 			width, height, modal
 *  示例：
 * 		scriptHelper.showDialog('dialogid', 'bannerid', {width:450, height:500, modal:true});
 */
scriptHelper.prototype.showDialog=function(sDialog, sBanner, configObj){
	//取消冒泡事件
    if (typeof (window) != 'undefined' && window != null && window.event != null) {
        window.event.cancelBubble = true;
    }
    else if (ScriptHelper.showDialog.caller.arguments[0] != null) {
        ScriptHelper.showDialog.caller.arguments[0].cancelBubble = true;
    }
    
    //计算居中位置
	var oDialog = document.getElementById(sDialog);
	if (oDialog==null )
		return false;
	var client = ScriptHelper.getClient();
	if (configObj==null)
		configObj={modal:false};
	if (configObj.width==null)
		configObj.width=400;
	if (configObj.height==null)
		configObj.height=300;
		
	oDialog.style.position='absolute';
	oDialog.style.left=(client.clientWidth-configObj.width)/2;
	oDialog.style.top=(client.clientHeight-configObj.height)/2;
	oDialog.style.display='block';
	
	
	
	//创建Iframe
	var iframeDivId = "tempIframeDiv" + sDialog;  //iframe所在div的id
    var iframeId = "tempIframe" + sDialog;        //iframe的id
    var iframeDiv = document.getElementById(iframeDivId); //iframe所在div对象
    var iframe = document.getElementById(iframeId); //iframe对象
    if (iframe == null) {
        //ie6下使用dom添加节点后无法控制某些属性, 所以将iframe放在一个div中,这样才可以用写html的方式添加. 
        var tempIframeDiv = document.createElement("div");
        tempIframeDiv.setAttribute("id", iframeDivId);
        document.body.appendChild(tempIframeDiv);

        var iframeString = "<iframe id=\"" + iframeId + "\" style=\"position: absolute; display:none; border-width:0px;background-color: #CCCCCC;opacity:0.5;filter:FILTER: alpha(opacity=50);\"></iframe>";
        tempIframeDiv.innerHTML = iframeString;
        iframe = document.getElementById(iframeId);
        iframeDiv = document.getElementById(iframeDivId);
    }
    //使用遮盖层遮住select控件
    if (iframe != null && iframeDiv != null) {
    	var client = ScriptHelper.getClient()
    	var scroll = ScriptHelper.getScroll();
        iframeDiv.style.display = "block";
        iframe.style.top = 0;
        iframe.style.left = 0;
        iframe.style.width = client.clientWidth+'px';
        iframe.style.height = scroll.scrollHeight+'px';
        iframe.style.display = "block";
        iframe.style.zIndex = oDialog.style.zIndex - 1;
    }
	
	//挂上移动
	var oBanner= document.getElementById(sBanner);
	oBanner.onmousedown=function(event){
		ScriptHelper.moveObj={drag:true};
		with(ScriptHelper){
			moveObj.oDialog=oDialog;
			moveObj.offsetLeft=oDialog.offsetLeft;
			moveObj.offsetTop=oDialog.offsetTop;
			moveObj.x=oevent(event).clientX;
			moveObj.y=oevent(event).clientY;
			var client = getClient();
			moveObj.clientWidth=client.clientWidth;
			moveObj.clientHeight=client.clientHeight;
//			stopBubble(event);
		}
	}
	document.onmousemove=function(event){
		if (ScriptHelper.moveObj==null ||!ScriptHelper.moveObj.drag){
			return true;
		}
		with(ScriptHelper){
			var left= moveObj.offsetLeft + oevent(event).clientX - moveObj.x;
			if ((left+moveObj.oDialog.clientWidth+5)>moveObj.clientWidth)
				left=moveObj.clientWidth-moveObj.oDialog.clientWidth-5;
			if (left<0)
				left=0;
			 moveObj.oDialog.style.left = left + "px";
					
			var top = moveObj.offsetTop + oevent(event).clientY -moveObj.y;
			if ((top+moveObj.oDialog.clientHeight)>moveObj.clientHeight)
				top = moveObj.clientHeight-moveObj.oDialog.clientHeight;
			if (top<0)
				top=0;
			moveObj.oDialog.style.top = top+ "px";
			//取消冒泡事件
//			stopBubble(event);
		}
		
		
	}
	document.onmouseup=function(event){
		ScriptHelper.moveObj=null;
	}
}

scriptHelper.prototype.closeDialog=function(sDialog){
	var iframeDivId = "tempIframeDiv" + sDialog;  //iframe所在div的id
    var iframeId = "tempIframe" + sDialog;        //iframe的id
    var iframeDiv = document.getElementById(iframeDivId); //iframe所在div对象
    var iframe = document.getElementById(iframeId); //iframe对象
    var dialog=document.getElementById(sDialog);
    
    dialog.style.display='none';
    iframeDiv.style.display='none';
    iframe.style.display='none';
}
scriptHelper.prototype.oevent=function(e){
	if (!e) e = window.event;return e;
}
//  关闭图层
/*  参数说明:
divId        : 要隐藏的图层ID    
    
用法与测试:
ScriptHelperV2.closeDivCommon('testDiv');    
*/
scriptHelper.prototype.closeDivCommon = function(divId) {
    var iframeDivId = "tempIframeDiv" + divId;  //iframe所在div的id
    var iframeId = "tempIframe" + divId;        //iframe的id
    
    var divObj = document.getElementById(divId); //获得图层对象    
    if (divObj != null) {
        divObj.style.display = "none";
    }

    var iframe = document.getElementById(iframeId);
    if (iframe != null) {
        iframe.style.display = "none";
    }

    var iframeDiv = document.getElementById(iframeDivId);
    if (iframeDiv != null) {
        iframe.style.display = "none";
    } 
}

//建立scriptHelper类的一个实例对象.全局使用.
var ScriptHelper = new scriptHelper();

