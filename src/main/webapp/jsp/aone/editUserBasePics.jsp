<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%
	String path = request.getContextPath();
	request.setAttribute("jsessionid", request.getSession().getId());
%>
<html>
	<link href="<%=path%>/scripts/uploadify/default.css" rel="stylesheet" type="text/css" />
	<link href="<%=path%>/scripts/uploadify/uploadify.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="<%=path%>/scripts/uploadify/swfobject.js"></script>
	<script type="text/javascript" src="<%=path%>/scripts/uploadify/jquery.uploadify.v2.1.0.min.js"></script>
	<script type="text/javascript">
	$(document).ready(function() {
            $("#userPic").uploadify({
                'uploader'       : '<%=path%>/scripts/uploadify/uploadify.swf', //是组件自带的flash，用于打开选取本地文件的按钮
                'script'         : '<vwb:Link jsp="userInfo" format="url"/>;jsessionid=${jsessionid}?func=uploadBasePics',//处理上传的路径，这里使用Struts2是XXX.action
                'cancelImg'      : '<%=path%>/scripts/uploadify/cancel.png',//取消上传文件的按钮图片，就是个叉叉
                'folder'         : 'uploads',//上传文件的目录
                'fileDataName'   : 'userPic',//和input的name属性值保持一致就好，Struts2就能处理了
                'queueID'        : 'fileQueue',
                'auto'           : true,//是否选取文件后自动上传
                'multi'          : false,//是否支持多文件上传
                'simUploadLimit' : 1,//每次最大上传文件数
                'buttonText'     : 'select',//按钮上的文字
                'displayData'    : 'speed',//有speed和percentage两种，一个显示速度，一个显示完成百分比
                'fileDesc'       : '支持格式:jpg/gif/jpeg/png/bmp.', //如果配置了以下的'fileExt'属性，那么这个属性是必须的
                'fileExt'        : '*.jpg;*.jpeg;*.png;*.bmp',//允许的格式
                'onComplete'     : function (event, queueID, fileObj, response, data){
                   window.location.href = "<vwb:Link jsp="userInfo" format="url"/>?func=showBasePics&fileName="+response;
                }
            });
        });
 	</script>
	<script type="text/javascript">
function cutpx(str)
{
  return str.substring(0,str.length-2);
}
function setCurrentPos(index)
{
    var cp = ic.GetPos();
    $("#top"+index).attr('value',cp.Top);
    $("#left"+index).attr('value',cp.Left);
    $("#width"+index).attr('value',cp.Width);
    $("#height"+index).attr('value',cp.Height);
}
var isIE = (document.all) ? true : false;

var isIE6 = isIE && ([/MSIE (\d)\.0/i.exec(navigator.userAgent)][0][1] == 6);

var getObject = function (id) {
	return "string" == typeof id ? document.getElementById(id) : id;
};

var Class = {
	create: function() {
		return function() { 
		this.initialize.apply(this, arguments); 
		}
	}
}

var Extend = function(destination, source) {
	for (var property in source) {
		destination[property] = source[property];
	}
}

var Bind = function(object, fun) {
	return function() {
		return fun.apply(object, arguments);
	}
}

var BindAsEventListener = function(object, fun) {
	var args = Array.prototype.slice.call(arguments).slice(2);
	return function(event) {
		return fun.apply(object, [event || window.event].concat(args));
	}
}

var CurrentStyle = function(element){
	return element.currentStyle || document.defaultView.getComputedStyle(element, null);
}

function addEventHandler(oTarget, sEventType, fnHandler) {
	if (oTarget.addEventListener) {
		oTarget.addEventListener(sEventType, fnHandler, false);
	} else if (oTarget.attachEvent) {
		oTarget.attachEvent("on" + sEventType, fnHandler);
	} else {
		oTarget["on" + sEventType] = fnHandler;
	}
};

function removeEventHandler(oTarget, sEventType, fnHandler) {
    if (oTarget.removeEventListener) {
        oTarget.removeEventListener(sEventType, fnHandler, false);
    } else if (oTarget.detachEvent) {
        oTarget.detachEvent("on" + sEventType, fnHandler);
    } else { 
        oTarget["on" + sEventType] = null;
    }
};

//图片切割
var ImgCropper = Class.create();
ImgCropper.prototype = {
  //容器对象,控制层,图片地址
  initialize: function(container, handle, url, options) {
	this._Container = getObject(container);//容器对象
	this._layHandle = getObject(handle);//控制层
	this.Url = url;//图片地址
	
	this._layBase = this._Container.appendChild(document.createElement("img"));//底层
	this._layCropper = this._Container.appendChild(document.createElement("img"));//切割层
	this._layCropper.onload = Bind(this, this.SetPos);
	//用来设置大小
	this._tempImg = document.createElement("img");
	this._tempImg.onload = Bind(this, this.SetSize);
	this.SetOptions(options);
	
	this.Opacity = Math.round(this.options.Opacity);
	this.Color = this.options.Color;
	this.Scale = !!this.options.Scale;
	this.Ratio = Math.max(this.options.Ratio, 0);
	this.Width = Math.round(this.options.Width);
	this.Height = Math.round(this.options.Height);
	
	//设置预览对象
	var oPreview = this.options.Preview;//预览对象
	this._view = new Array();
	this._viewIndex=0;
	if(oPreview){
	   for(var previewindex in oPreview)
	   {
	        var viewDiv = getObject(oPreview[previewindex]);
			viewDiv.style.position = "relative";
			viewDiv.style.overflow = "hidden";
			//预览图片对象
			this._view[previewindex] = viewDiv.appendChild(document.createElement("img"));
			this._view[previewindex].style.position = "absolute";
			this._view[previewindex].onload = Bind(this, this.SetPreview);
			this._view[previewindex].viewWidth = viewDiv.style.width;
			this._view[previewindex].viewHeight = viewDiv.style.height;		
			this._view[previewindex].viewIndex=previewindex;
		}
	}
	//设置拖放
	this._drag = new Drag(this._layHandle, { Limit: true, onMove: Bind(this, this.SetPos), Transparent: true });
	//设置缩放
	this.Resize = !!this.options.Resize;
	if(this.Resize){
		var op = this.options, _resize = new Resize(this._layHandle, { Max: true, onResize: Bind(this, this.SetPos) });
		//设置缩放触发对象
		op.RightDown && (_resize.Set(op.RightDown, "right-down"));
		op.LeftDown && (_resize.Set(op.LeftDown, "left-down"));
		op.RightUp && (_resize.Set(op.RightUp, "right-up"));
		op.LeftUp && (_resize.Set(op.LeftUp, "left-up"));
		op.Right && (_resize.Set(op.Right, "right"));
		op.Left && (_resize.Set(op.Left, "left"));
		op.Down && (_resize.Set(op.Down, "down"));
		op.Up && (_resize.Set(op.Up, "up"));
		//最小范围限制
		this.Min = !!this.options.Min;
		this.minWidth = Math.round(this.options.minWidth);
		this.minHeight = Math.round(this.options.minHeight);
		//设置缩放对象
		this._resize = _resize;
	}
	//设置样式
	this._Container.style.position = "relative";
	this._Container.style.overflow = "hidden";
	this._layHandle.style.zIndex = 200;
	this._layCropper.style.zIndex = 100;
	this._layBase.style.position = this._layCropper.style.position = "absolute";
	this._layBase.style.top = this._layBase.style.left = this._layCropper.style.top = this._layCropper.style.left = 0;//对齐
	//初始化设置
	
	this.Init();
  },
  //设置默认属性
  SetOptions: function(options) {
    this.options = {//默认值
		Opacity:	50,//透明度(0到100)
		Color:		"",//背景色
		Width:		0,//图片高度
		Height:		0,//图片高度
		//缩放触发对象
		Resize:		false,//是否设置缩放
		Right:		"",//右边缩放对象
		Left:		"",//左边缩放对象
		Up:			"",//上边缩放对象
		Down:		"",//下边缩放对象
		RightDown:	"",//右下缩放对象
		LeftDown:	"",//左下缩放对象
		RightUp:	"",//右上缩放对象
		LeftUp:		"",//左上缩放对象
		Min:		false,//是否最小宽高限制(为true时下面min参数有用)
		minWidth:	50,//最小宽度
		minHeight:	50,//最小高度
		Scale:		true,//是否按比例缩放
		Ratio:		0,//缩放比例(宽/高)
		//预览对象设置
		Preview:	""//预览对象
    };
    Extend(this.options, options || {});
  },
  //初始化对象
  Init: function() {
	//设置背景色
	this.Color && (this._Container.style.backgroundColor = this.Color);
	//设置图片
	this._tempImg.src = this._layBase.src = this._layCropper.src = this.Url;
	//this._layBase.src="http://www.baidu.com/img/baidu_jgylogo1.gif";
	//设置透明
	if(isIE){
		this._layBase.style.filter = "alpha(opacity:" + this.Opacity + ")";
	} else {
		this._layBase.style.opacity = this.Opacity / 100;
	}
	//设置预览对象
	for(var vindex in this._view)
	{
    	this._view[vindex] && (this._view[vindex].src = this.Url);
	}
	//设置缩放
	if(this.Resize){
		with(this._resize){
			Scale = this.Scale; Ratio = this.Ratio; Min = this.Min; minWidth = this.minWidth; minHeight = this.minHeight;
		}
	}
  },
  //设置切割样式
  SetPos: function() {
	//ie6渲染bug
	if(isIE6){ with(this._layHandle.style){ zoom = .9; zoom = 1; }; };
	//获取位置参数
	var p = this.GetPos();
	//按拖放对象的参数进行切割
	this._layCropper.style.clip = "rect(" + p.Top + "px " + (p.Left + p.Width) + "px " + (p.Top + p.Height) + "px " + p.Left + "px)";
	//设置预览
	this.SetPreview();
  },
  //设置预览效果
  SetPreview: function() {
    var tempIndex = this._viewIndex;
    var onload = false
    var ev = window.event || arguments.callee.caller.arguments[0]
    var target;
     if(ev&&(target=(ev.srcElement||ev.target))&&target.viewIndex)
     {
       onload = true;
       tempIndex = (target.viewIndex);	
      // getObject("dragDiv").style.width=this._view[tempIndex].viewWidth+"px";
      // getObject("dragDiv").style.height=this._view[tempIndex].viewHeight+"px";
      
     }
    
	if(this._view[tempIndex]){
	
		//预览显示的宽和高
		var ifixWidth = this._view[tempIndex].viewWidth;
		var ifixHeight = this._view[tempIndex].viewHeight
		//alert(ifixWidth+"  : "+ifixHeight);
		ifixWidth = ifixWidth.substring(0,ifixWidth.length-2);
		ifixHeight = ifixHeight.substring(0,ifixHeight.length-2);
		var p = this.GetPos();

        if(onload)
		{
		    var os = this.GetSize(this._tempImg.width, this._tempImg.height, this.Width, this.Height);
            if((p.Top+ifixHeight)>os.Height || (p.Left+ifixWidth)>os.Width)
            {
                 var temp =  this.GetSize(ifixWidth,ifixHeight,os.Width-p.Left,os.Height-p.Top);
                 p.Width = temp.Width;
		         p.Height = temp.Height;
            }else{
                 p.Width = ifixWidth;
		         p.Height = ifixHeight;
            }
         }
	    var s = this.GetSize(p.Width, p.Height, ifixWidth, ifixHeight);
	    var scale = s.Height / p.Height;
		//按比例设置参数
		var pHeight = this._layBase.height * scale, pWidth = this._layBase.width * scale, pTop = p.Top * scale, pLeft = p.Left * scale;
		
		//设置预览对象
		with(this._view[tempIndex].style){
			//设置样式
			width = pWidth + "px"; height = pHeight + "px"; top = - pTop + "px "; left = - pLeft + "px";
			//切割预览图
			var rectop = pTop;
			var rectright = Number(pLeft) + Number(s.Width);
			var rectbottom = Number(pTop) + Number(s.Height)
			var rectleft = pLeft;
			clip = "rect(" + rectop + "px " + rectright + "px " + rectbottom + "px " + rectleft + "px)";
		}
	}
	setCurrentPos(tempIndex);
  },
  //设置图片大小
  SetSize: function() {
	var s = this.GetSize(this._tempImg.width, this._tempImg.height, this.Width, this.Height);
	//设置底图和切割图
	this._layBase.style.width = this._layCropper.style.width = s.Width + "px";
	this._layBase.style.height = this._layCropper.style.height = s.Height + "px";
	//设置拖放范围
	this._drag.mxRight = s.Width; this._drag.mxBottom = s.Height;
	//设置缩放范围
	if(this.Resize){ this._resize.mxRight = s.Width; this._resize.mxBottom = s.Height; }
  },
  //获取当前样式
  GetPos: function() {
	with(this._layHandle){
		return { Top: offsetTop, Left: offsetLeft, Width: offsetWidth, Height: offsetHeight }
	}
  },
  //获取尺寸
  GetSize: function(nowWidth, nowHeight, fixWidth, fixHeight) {
	var iWidth = nowWidth, iHeight = nowHeight, scale = iWidth / iHeight;
	//按比例设置
	if(fixHeight)
	{
	 
	 iWidth = (iHeight = fixHeight) * scale; 
	}
	if(fixWidth && (!fixHeight || iWidth > fixWidth)){ iHeight = (iWidth = fixWidth) / scale; }
	//返回尺寸对象
	return { Width: iWidth, Height: iHeight }
  },
  
  GetPicShowSize:function(){
    return this.GetSize(this._tempImg.width, this._tempImg.height, this.Width, this.Height);
  },
  getPicViews:function(){
     return this._view;
  }
}

function resizepreview()
{
   var p = ic.GetPos();
   var picwidth = ic._layBase.style.width;
	var picheight = ic._layBase.style.height;
   alert("Top:"+p.Top + "  Left:"+p.Left+"   Width:"+p.Width+"   Height:"+p.Height+"  宽："+picwidth+"    高:"+picheight);
}
function returnBackUserInfo()
{
   window.location.href = "<vwb:Link jsp="userInfo" format="url"/>";
}
function changeDragScale(width,height,index)
{
	var picwidth = ic._layBase.style.width;
	var picheight = ic._layBase.style.height;
	picwidth = Number(picwidth.substring(0,picwidth.length-2))-5;
	picheight = Number(picheight.substring(0,picheight.length-2))-5;
	var dragpos = ic.GetPos();
   
	var size = ic.GetSize(width,height,picwidth-dragpos.Left,picheight-dragpos.Top);
   getObject("dragDiv").style.width=size.Width+"px";
   getObject("dragDiv").style.height=size.Height+"px";
   ic._viewIndex=index;
	//设置图片
   ic._tempImg.src = ic._layBase.src = ic._layCropper.src = ic.Url;
   if(ic.Resize){
		with(ic._resize){
			Scale = ic.Scale; Ratio = ic.Ratio; Min = ic.Min; minWidth = ic.minWidth; minHeight = ic.minHeight;
		}
	}
   for(var n in ic.options.Preview)
   {
     
      getObject(ic.options.Preview[n]).style.border="3px solid #B7D2E2"
   }
   var viewDiv = getObject(ic.options.Preview[index]);
   viewDiv.style.border="3px solid #FFCF5C";
   setCurrentPos(index);
}
</script>
	<script type="text/javascript" src="<%=path%>/scripts/aone/Drag.js"></script>
	<script type="text/javascript" src="<%=path%>/scripts/aone/Resize.js"></script>
	<style type="text/css">
#rRightDown,#rLeftDown,#rLeftUp,#rRightUp,#rRight,#rLeft,#rUp,#rDown{
	position:absolute;
	background:#FFF;
	border: 1px solid #333;
	width: 6px;
	height: 6px;
	z-index:500;
	font-size:0;
	opacity: 0.5;
	filter:alpha(opacity=50);
}

#rLeftDown,#rRightUp{cursor:ne-resize;}
#rRightDown,#rLeftUp{cursor:nw-resize;}
#rRight,#rLeft{cursor:e-resize;}
#rUp,#rDown{cursor:n-resize;}

#rLeftDown{left:-4px;bottom:-4px;}
#rRightUp{right:-4px;top:-4px;}
#rRightDown{right:-4px;bottom:-4px;background-color:#00F;}
#rLeftUp{left:-4px;top:-4px;}
#rRight{right:-4px;top:50%;margin-top:-4px;}
#rLeft{left:-4px;top:50%;margin-top:-4px;}
#rUp{top:-4px;left:50%;margin-left:-4px;}
#rDown{bottom:-4px;left:50%;margin-left:-4px;}
#bgDiv{width:300px; height:400px; border:1px solid #666666; position:relative;}
#dragDiv{border:1px dashed #fff; top:10px; left:10px; cursor:move; }
</style>
	<br>
	<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td width="300">
				<div id="fileQueue"></div>
				<input type="file" name="userPic" id="userPic" />
				<div class="r3">
					选择一张您电脑中的图片。
					<br>
					支持*.jpg;*.jpeg;*.png;*.bmp'的格式
				</div>
				<br>
				<div id="bgDiv">
					<div id="dragDiv">
						<div id="rRightDown">
						</div>
						<div id="rLeftDown">
						</div>
						<div id="rRightUp">
						</div>
						<div id="rLeftUp">
						</div>
						<div id="rRight">
						</div>
						<div id="rLeft">
						</div>
						<div id="rUp">
						</div>
						<div id="rDown"></div>
					</div>
				</div>
			</td>
			<td width="20"></td>
			<td align="right">
				<br>
				<table>
					<tr>
						<td>
							<div id="viewDiv" style="width:200px; height:267px;"></div>
						</td>
						<td>
							<a onclick="changeDragScale(200,267,0);" style="cursor:pointer">编辑</a>
						</td>
					</tr>
				</table>
				<table>
					<tr>
						<td>
							<div id="viewDivmiddle" style="width:100px; height:100px;">
							</div>
						</td>
						<td style="v-align:bottom">
							<a onclick="changeDragScale(100,100,1);" style="cursor:pointer">编辑</a>
						</td>
					</tr>
				</table>
				<table>
					<tr>
						<td>
							<div id="viewDivsmall" style="width:50px; height:50px;">
							</div>
						</td>
						<td>
							<a onclick="changeDragScale(50,50,2);" style="cursor:pointer">编辑</a>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr height="50px">
			<th>
				<input type="button" value="保存"
					onclick="document.userInfo.submit();">
			</th>
			<td width="20"></td>
			<td>
				<input type="button" value="返回" onclick="returnBackUserInfo()">
			</td>



		</tr>
	</table>
	<form name="userInfo" method="post"
		action='<vwb:Link jsp="userInfo" format="url"/>?func=saveBasePics'>
		<input type="hidden" name="showPicWidth" id="showPicWidth" />
		<input type="hidden" name="showPicHeight" id="showPicHeight" />
		<input type="hidden" name="top0" id="top0" />
		<input type="hidden" name="left0" id="left0" />
		<input type="hidden" name="width0" id="width0" />
		<input type="hidden" name="height0" id="height0" />
		<input type="hidden" name="top1" id="top1" />
		<input type="hidden" name="left1" id="left1" />
		<input type="hidden" name="width1" id="width1" />
		<input type="hidden" name="height1" id="height1" />
		<input type="hidden" name="top2" id="top2" />
		<input type="hidden" name="left2" id="left2" />
		<input type="hidden" name="width2" id="width2" />
		<input type="hidden" name="height2" id="height2" />


		<input type="hidden" name="realWidth1" id="realWidth1" />
		<input type="hidden" name="realHeight1" id="realHeight1" />
		<input type="hidden" name="realWidth2" id="realWidth2" />
		<input type="hidden" name="realHeight2" id="realHeight2" />
		<input type="hidden" name="realWidth0" id="realWidth0" />
		<input type="hidden" name="realHeight0" id="realHeight0" />

		<input type="hidden" name="editPics" value="${editPics}" />

	</form>
	<script>
var ic = new ImgCropper("bgDiv", "dragDiv", "<%=path%>/_tempDir/${editPics}", {
	Width: 300, Height: 400, Color: "#000",
	Resize: true,
	Right: "rRight", Left: "rLeft", Up:	"rUp", Down: "rDown",
	RightDown: "rRightDown", LeftDown: "rLeftDown", RightUp: "rRightUp", LeftUp: "rLeftUp",
	Preview: ["viewDiv","viewDivmiddle","viewDivsmall"]
})

function initUserPic()
{
    if(document.readyState == "complete")
    {
         clearInterval(initp);
	     changeDragScale(50,50,2);
	     changeDragScale(100,100,1);
	     changeDragScale(200,267,0);
	     var pic = ic.GetPicShowSize();
	     var pic = ic.GetPicShowSize();
	     $("#showPicWidth").attr('value',pic.Width);
	     $("#showPicHeight").attr('value',pic.Height);
	     var views = ic.getPicViews();
	     for(var view in views)
	     {
	      $("#realWidth"+views[view].viewIndex).attr('value',cutpx(views[view].viewWidth));
	      $("#realHeight"+views[view].viewIndex).attr('value',cutpx(views[view].viewHeight));
	     }
    }
}
var initp =  setInterval("initUserPic();",100);
</script>