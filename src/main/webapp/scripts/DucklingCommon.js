/*VLAB Common javascript create by www.cerc.cnic.cn  09-4-17
*	context list:
*   (1)get Browser's Type common function (line 11)
*	(2)dct fullscreen style function (line 50)
*   (3)javascript's cookie common function (line 100)
*   (4)js map struct (line 134)
*	(5)Fix Dom Tree(line 134)
*/
/**
 * PortalCookie是用来操作客户端的Cookie的工具类。目前已知的用户是LanguageMenu
 */
function PortalCookie() {
	this.setCookie = function (name, value, option) {
     var str=name+"="+escape(value);  
     if(option){
            if(option.expireDays){
                   var date=new Date();
                   var ms=option.expireDays*24*3600*1000;
                   date.setTime(date.getTime()+ms);
                   str+="; expires="+date.toGMTString();
            } 
            if(option.path)str+="; path="+option.path;
            if(option.domain)str+="; domain"+option.domain;
            if(option.secure)str+="; true";
     }
     document.cookie=str;
	};
	this.getCookieVal = function (offset) {
		var endstr = document.cookie.indexOf(";", offset);
		if (endstr == -1) {
			endstr = document.cookie.length;
		}
		return unescape(document.cookie.substring(offset, endstr));
	};
	this.getCookie = function (name) {
		var arg = name + "=";
		var alen = arg.length;
		var clen = document.cookie.length;
		var i = 0;
		while (i < clen) {
			var j = i + alen;
			if (document.cookie.substring(i, j) == arg) {
				return getCookieVal(j);
			}
			i = document.cookie.indexOf(" ", i) + 1;
			if (i === 0) {
				break;
			}
		}
		return null;
	};
	
	this.deleteCookie=function(name){
		this.setCookie(name,"",{expireDays:-1});
	};
}
/**
 * 用户工具栏的定位JS
 */
var UserBox={
	onPageLoad:function(){
	  this.refresh();
	  FullScreenToggler.addListener(this.toggleScreenListener);
	},
	refresh:function(width){
	  	var userbox=$('#DCT_Login_di');
	    if (userbox.length>0){
			if (width!=null){
				userbox.css("top", 0);
				userbox.css("left", width-userbox.width());
			}else{
				//append to header div
			  	var header =$('#header');
			  	var headerOffset =header.offset();
			  	userbox.css('top', 0);
		    	userbox.css("left", headerOffset.left+header.width()-userbox.width());
			}
	    }
	},
	toggleScreenListener:{
		onFullScreen:function(width, height){
			UserBox.refresh(width);
		},
		onCancelFullScreen:function(){
			UserBox.refresh();
		}
	}
};
/**
 * 全屏控制
 */
var FullScreenToggler={
	adapters:new Array(),
	boxs:["#header", "#nav", "#leftcol", "#box", "breadscrumbs"],
	onPageLoad:function(){
		cookievalue =getCookie("fullscr");
		if(cookievalue=="true"){
			FullScreenToggler.doFullScreen();
		}else{
			FullScreenToggler.cancelFullScreen();
		}
		
		$("#FullScrA").click(function(){
			FullScreenToggler.changeFullScreen();
		});
	},
	changeFullScreen:function(){
			cookievalue =getCookie("fullscr");
			if(cookievalue=="true"){
				FullScreenToggler.cancelFullScreen();
			}else{
				FullScreenToggler.doFullScreen();
			}
	},
	doFullScreen:function(){
		var obje=this.getViewPaneSize("#center_right");
		
		for (var i=0;i<this.boxs.length;i++){
			var box=$(this.boxs[i]);
			if (box!=null)
				box.css("display", "none");
		}
		
		var login_height=$("#DCT_Login_di").outerHeight()+11;
		$("#center_right").css({
			"z-index":"9",
			"position":"absolute",
			"width":obje.Width+"px",
			"height":obje.Height-login_height+"px",
			"left":"0",
			"top":"0",
			"padding-top":login_height+"px",
			"margin":"0",
			"background-color":"#F8F8F5"
		});
		
		$("#FullScrA").html("public.fullscreen.cancel".localize());
		
		this.callListener(true, obje.Width, obje.Height);
		
		setCookie("fullscr","true");
	},
	cancelFullScreen:function(){
		for (var i=0;i<this.boxs.length;i++){
			var box = $(this.boxs[i]);
			if (box!=null)
				box.css("display", "");
		}
		
		$("#center_right").removeAttr("style");
		$("#FullScrA").html("public.fullscreen".localize());
		
		this.callListener(false);
		
		delCookie("fullscr");
	},
	callListener:function(fullScreen, width, height){
		for (var i=0;i<this.adapters.length;i++){
			if (fullScreen)
				this.adapters[i].onFullScreen(width, height);
			else
				this.adapters[i].onCancelFullScreen();
		}
	},
	addListener:function(listener){
		if (listener!=null){
			this.adapters[this.adapters.length]=listener;
		}
	},
	getViewPaneSize:function(elementId){
		var realheight=$(elementId).innerHeight();
		return { Width : $(window).width(), Height : $(window).height()-30>realheight?$(window).height()-30:realheight } ;
	}
};
function changeLocale(element) {
	var locale=$(element).attr("Locale");
	var apath=$(element).attr("CookiePath");
	
	var pcookie=new PortalCookie();
	pcookie.deleteCookie("Portal.Locale");
	pcookie.setCookie("Portal.Locale", locale,{expireDays:365,path:apath});
	window.location.reload();
	
	return true;
};
$(document).ready(function(){
	UserBox.onPageLoad();
	FullScreenToggler.onPageLoad();
});

/*get Browser's Height  & Width 09-4-17 diyanliang@cnic.cn*/
var GetViewPaneSize=function (elementId){
	var realheight=document.getElementById(elementId).clientHeight;
	var browser=getBrowser();
	if(browser=="MSIE"){
		var oSizeSource ;
		var oDoc = window.document.documentElement ;
		if ( oDoc && oDoc.clientWidth )	{
			oSizeSource = oDoc ;// IE6 Strict Mode
		}else{
			oSizeSource = top.document.body ;// Other IEs
		}					
		return { Width : oSizeSource.clientWidth-30, Height : oSizeSource.clientHeight-30>realheight?oSizeSource.clientHeight-26:realheight } ;
	}else if(browser=="Firefox"){
		return { Width : window.innerWidth-18, Height : window.innerHeight>realheight?window.innerHeight:realheight } ;
	}
}

/*set cookie 09-4-20 diyanliang@cnic.cn*/
var setCookie=function (name, value) { 
    var argv = setCookie.arguments; 
    var argc = setCookie.arguments.length; 
    var expires = (argc > 2) ? argv[2] : null; 
    if(expires!=null) { 
        var LargeExpDate = new Date (); 
        LargeExpDate.setTime(LargeExpDate.getTime() + (expires*1000*3600*24));         
    } 
    document.cookie = name + "=" + escape (value)+((expires == null) ? "" : ("; expires=" +LargeExpDate.toGMTString())); 
}

/*delete cookie by name 09-4-20 diyanliang@cnic.cn*/
var delCookie=function (name){
  var expdate = new Date(); 
  expdate.setTime(expdate.getTime() - (86400 * 1000 * 1)); 
  setCookie(name, "", expdate); 
}

/*get cookie by name 09-4-20 diyanliang@cnic.cn*/
var getCookie=function (name){ 
    var search = name + "=" 
    if(document.cookie.length > 0){ 
        offset = document.cookie.indexOf(search) 
        if(offset != -1){ 
            offset += search.length 
            end = document.cookie.indexOf(";", offset) 
            if(end == -1) end = document.cookie.length 
            return unescape(document.cookie.substring(offset, end)) 
        } 
        else return "" 
    } 
} 

/* js map struct 09-5-15 diyanlian@cnic.cn*/
var VLAB_Map = function() {  
	 this.m=new Array();
}
VLAB_Map.MapEntry=function(k,v){
    this.key=k;
    this.value=v;
    this.keyEquals=function(key2){
        if(this.key==key2){
            return true;
        }else{
            return false;
        }
    }
};

VLAB_Map.prototype={
	test:function(){
	alert("VLAB_Map!!!!!!!");
	},
    put:function(k,v){
        var newEntry=new VLAB_Map.MapEntry(k,v);
        for(var i=0;i<this.m.length;i++){
            var entry=this.m[i];
            if(entry.keyEquals(k)){
                return;
            }
        }
        this.m.push(newEntry);        
    },
    get:function(k){
        for(var i=0;i<this.m.length;i++){
            var entry=this.m[i];
            if(entry.keyEquals(k)){
                return entry.value;
            }
        }
        return null;
    },
    remove:function(k){
        var entryPoped;
        for(var i=0;i<this.m.length;i++){
            entryPoped=this.m.pop();
            if(entryPoped.keyEquals(k)){
                break;
            }else{
                this.m.unshift(entryPoped);
            }
        }
    }
    ,
    getSize:function(){
        return this.m.length;
    },
    getKeys:function(){
        var keys=new Array();
        for(var i=0;i<this.m.length;i++){
            keys.push(this.m[i].key);
        }
        return keys;
    },
    getValues:function(){
        var values=new Array();
        for(var i=0;i<this.m.length;i++){
            values.push(this.m[i].value);
        }
        return values;
    },
    isEmpty:function(){
        return (this.m==null||this.m.length<=0);
    },
    containsKey:function(k){
        for(var i=0;i<this.m.length;i++){
            if(this.m[i].keyEquals(k))
                return true;
        }
        return false;
    },
    putAll:function(map){
        if(map==null||typeof map!="object"){
            alert("the object to be put should be a valid object");
        }
        for(var i=0;i<map.getSize();i++){
            this.put(map.m[i].key,map.m[i].value);
        }
    }
};

//add by diyanliang DMLForm提交按钮检查
function check_DML_Input(obj){
	var  arrobj=document.getElementsByTagName("input");
	for(i=0;i<arrobj.length;i++){
		if(arrobj[i].getAttribute("allnulltype")=="false"){
			if(!arrobj[i].value){
				alert(arrobj[i].getAttribute('dmldesc')+" must be not null!")
				return ;
			}
		}
	}
	obj.submit();
	/*alert("goon")
	var  arrform=document.getElementsByTagName("form")
	for(i=0;i<arrform.length;i++){
			//alert(arrform[i].outerHTML)
	}
	var pFORM=findParent(obj,"FORM");
	if(pFORM.nodeName=="FORM"){
		pFORM.submit();
	}*/
	
}
//add by diyanliang 查看上级元素是否有传入的参数
function findParent(obj,pname){

	//alert(obj.parentNode.nodeName)
	while(obj.nodeName!=pname){
		obj=obj.parentNode;
	}
	return obj;
}


$(document).ready(function() {
	/*左菜单树 start dylan 2010-7-16*/
	$(".DCT_leftmenu>li>ul").not( $(".DefaultShow") ).css("display", "none");//二级菜单隐藏
	$(".DCT_leftmenu_mutex>li>ul").not( $(".DefaultShow") ).css("display", "none");//二级菜单隐藏
	var obj=$(".DCT_leftmenu>li")
	obj.each(function(i){
		odiv=document.createElement("div");
		odiv.style.cssText="cursor:pointer;"
		firstnode=this.childNodes[0];
		cloneNode=firstnode.cloneNode(true);
		if(firstnode.nodeType==3){
			odiv.innerHTML=firstnode.nodeValue;
		}else{
			odiv.appendChild(cloneNode);
		}
		this.replaceChild(odiv,this.childNodes[0]);
		odiv.onclick=function(){
			var displaytype=$(this).parent().find("ul").css("display");
			$(this).parent().find("ul").css("display",(displaytype=='none')?"block":"none");
		}
	})
	
	
	var obj=$(".DCT_leftmenu_mutex>li")
	obj.each(function(i){
		odiv=document.createElement("div");
		odiv.style.cssText="cursor:pointer;"
		firstnode=this.childNodes[0];
		cloneNode=firstnode.cloneNode(true);
		if(firstnode.nodeType==3){
			odiv.innerHTML=firstnode.nodeValue;
		}else{
			odiv.appendChild(cloneNode);
		}
		this.replaceChild(odiv,this.childNodes[0]);
		odiv.onclick=function(){
			$(".DCT_leftmenu_mutex>li>ul").css("display", "none");
			var displaytype=$(this).parent().find("ul").css("display");
			$(this).parent().find("ul").css("display",(displaytype=='none')?"block":"none");
		}
	})
	
	/*顶菜单下拉列表 dylan 2010-7-16*/
	/*二级菜单隐藏*/
	$(".DCT_topmenu>li>ul>li").css({"display": 'list-item',"float": 'none'});
	$(".DCT_topmenu>li>ul").css({display:'none', visibility:'visible',position: 'absolute'});
	var obj=$(".DCT_topmenu>li")
	obj.each(function(i){
		odiv=document.createElement("div");
		odiv.style.cssText="cursor:pointer;"
		firstnode=this.childNodes[0];
		cloneNode=firstnode.cloneNode(true);
		if(firstnode.nodeType==3){
			odiv.innerHTML=firstnode.nodeValue;
		}else{
			odiv.appendChild(cloneNode);
		}
		this.replaceChild(odiv,this.childNodes[0]);
		this.onmouseover=function(){
			$(this).find("ul").css({display:'block', visibility:'visible'})
		}
		this.onmouseout=function(){
			$(this).find("ul").css({display:'none', visibility:'visible'})
		}
	})
	
	
	/*切换语言栏链接*/
	var oDCT_locale_enus=$(".DCT_enus")
	oDCT_locale_enus.each(function(i){
		m_href=this.href;
		this.onclick=function(){
			change_locale('en_US',contextPath,m_href)
			//window.location=m_href;
			
		}
		
		
	})
	
	var oDCT_locale_enus=$(".DCT_zhcn")
	oDCT_locale_enus.each(function(i){
		m_href=this.href;
		this.onclick=function(){
			change_locale('zh_CN',contextPath,m_href)
			//window.location=m_href;
			
		}
		
		
	})
	
	
})

/* I18N dylan 2010-7-20*/
String.prototype.localize=function(){
	var s = LocalizedStrings["javascript."+this], args = arguments;
	if(!s) return("???" + this + "???");
	return s.replace(/\{(\d)\}/g, function(m){ 
		return args[m.charAt(1)] || "???"+m.charAt(1)+"???";
	});
}

String.prototype.trim = function() { 
	return this.replace(/(^\s*)|(\s*$)/g, ""); 
} 


/*滚动图片效果 Dylan*/
function sendImageScrollajax(name){
	var strpath=""
	var img=$("#"+name)
	img.each(function(i){
		strpath+=this.id+":"+this.getAttribute("path")+"|";
	})
	$.ajax({
	   type: "POST",
	   url: Wiki["BaseUrl"]+"/imgScrService",
	   data: "path="+strpath,
	   dataType:"text",
	   success: function(msg){
	   		imageScrollCallBack(msg,name);
	   }
	});
}
/*滚动图片效果 Dylan*/
function imageScrollCallBack(msg,name){
	var obj=document.getElementById(name);
	if(obj){
		eval(msg)
		var img=$("#"+name)
		img.each(function(i){
			if(!this.imgid)this.imgid=0;
			var list=eval(this.id);
			this.lh=eval(this.id).length;
			if(this.imgid>=this.lh){
				this.imgid=0;
			}
//			this.src=list[this.imgid]+"?ver="+Math.random();
			this.src=list[this.imgid];
			this.imgid++;
			
			this.onload = function(){
				
				$(this).css("width", "auto"); // 设定实际显示宽度
			    $(this).css("height", "auto");  // 设定等比例缩放后的高度
				/*增加图片缩放功能*/
				this.removeAttribute("style");
				var maxWidth = this.getAttribute("maxWidth"); // 图片最大宽度
			    var maxHeight = this.getAttribute("maxHeight");   // 图片最大高度
			    indentwidth=parseInt(maxWidth/10)
			    indentheight=parseInt(maxHeight/10)
			    maxWidth=maxWidth-10;
			    maxHeight=maxHeight-10;
//			    alert(maxWidth+"|"+maxHeight)
			    var ratio = 0;  // 缩放比例
			    var width = $(this).width();    // 图片实际宽度
			    var height = $(this).height();  // 图片实际高度
//			    alert("1|maxWidth="+maxWidth+"|maxHeight="+maxHeight+"|width="+width+"|height="+height)
			    // 检查图片是否超宽
			    if(width > maxWidth){
			        ratio = maxWidth / width;   // 计算缩放比例
			        
			        $(this).css("width", maxWidth+"px"); // 设定实际显示宽度
			        $(this).css("height", height * ratio+"px");  // 设定等比例缩放后的高度
			        width=maxWidth;
			        height=height * ratio;
			    }
//			    alert("2|maxWidth="+maxWidth+"|maxHeight="+maxHeight+"|width="+width+"|height="+height)
			    // 检查图片是否超高
			    if(height > maxHeight){
			        ratio = maxHeight / height; // 计算缩放比例
			        $(this).css("height", maxHeight+"px");   // 设定实际显示高度
			        $(this).css("width", width * ratio+"px");    // 设定等比例缩放后的高度
			        width=width * ratio;
			        height=maxHeight;
			    }
//			    alert("3|maxWidth="+maxWidth+"|maxHeight="+maxHeight+"|width="+width+"|height="+height)
		        
			    	
			}

			
		});
	}
	
}

/*自动缩放image dylan*/
function changeimg(strobj){
	
	obj=document.getElementById(strobj);
	obj.onload = function(){
		
		$(this).css("width", "auto"); // 设定实际显示宽度
	    $(this).css("height", "auto");  // 设定等比例缩放后的高度
		/*增加图片缩放功能*/
		this.removeAttribute("style");
		var maxWidth = this.getAttribute("maxWidth"); // 图片最大宽度
	    var maxHeight = this.getAttribute("maxHeight");   // 图片最大高度
	    indentwidth=parseInt(maxWidth/10)
	    indentheight=parseInt(maxHeight/10)
	    maxWidth=maxWidth-10;
	    maxHeight=maxHeight-10;
//	    alert(maxWidth+"|"+maxHeight)
	    var ratio = 0;  // 缩放比例
	    var width = $(this).width();    // 图片实际宽度
	    var height = $(this).height();  // 图片实际高度
//	    alert("1|maxWidth="+maxWidth+"|maxHeight="+maxHeight+"|width="+width+"|height="+height)
	    // 检查图片是否超宽
	    if(width > maxWidth){
	        ratio = maxWidth / width;   // 计算缩放比例
	        
	        $(this).css("width", maxWidth+"px"); // 设定实际显示宽度
	        $(this).css("height", height * ratio+"px");  // 设定等比例缩放后的高度
	        width=maxWidth;
	        height=height * ratio;
	    }
//	    alert("2|maxWidth="+maxWidth+"|maxHeight="+maxHeight+"|width="+width+"|height="+height)
	    // 检查图片是否超高
	    if(height > maxHeight){
	        ratio = maxHeight / height; // 计算缩放比例
	        $(this).css("height", maxHeight+"px");   // 设定实际显示高度
	        $(this).css("width", width * ratio+"px");    // 设定等比例缩放后的高度
	        width=width * ratio;
	        height=maxHeight;
	    }
//	    alert("3|maxWidth="+maxWidth+"|maxHeight="+maxHeight+"|width="+width+"|height="+height)
	}
}

/*
 * E2 editor API insert link in E2editor create by Dylan(diyanliang@cnic.cn) 2011-6-1
 * sample: 1、<input type="button" onclick="E2APIinsertLink('E2APIinsertLink','http://g.cn')" value="E2insertLink" />
 *         2、<input type="button" onclick="E2APIinsertLink('E2APIinsertLink','http://g.cn'，true)" value="E2insertLink" /> 
 */
function E2APIinsertLink(linktext,linkurl,newwindow,fileid){
	var oEditor = FCKeditorAPI.GetInstance('htmlPageText') ;
	oEditor.insertLink(linktext,linkurl,newwindow,fileid);
}
/*
 * E2 editor API insert Img in E2editor create by Dylan(diyanliang@cnic.cn) 2011-6-1
 * sample: 1、<input type="button" onclick="E2APIinsertImg('http://www.google.cn/landing/cnexp/google-search.png','10','10','http://g.cn')" value="E2insertImg" />
 *         2、<input type="button" onclick="E2APIinsertImg('http://www.google.cn/landing/cnexp/google-search.png')" value="E2insertImg" />
 */
function E2APIinsertImg(imgsrc,m_height,m_width,linkurl,fileid){
	var oEditor = FCKeditorAPI.GetInstance('htmlPageText') ;
	oEditor.insertImg(imgsrc,m_height,m_width,linkurl,fileid);
	
}

function E2APIinsertNode(node){
	var oEditor = FCKeditorAPI.GetInstance('htmlPageText') ;
	oEditor.insertNode(node);
}

function getE2UserActiveTime(){
	var oEditor = FCKeditorAPI.GetInstance('htmlPageText') ;
	return oEditor.UserActiveTime;
	
}

(function(navi){
	var browser={
            versions:function(){
		        var u = navi.userAgent, app = navi.appVersion;
		        return {
		            trident: u.indexOf('Trident') > -1, //IE
		            presto: u.indexOf('Presto') > -1, //opera
		            webKit: u.indexOf('AppleWebKit') > -1, //苹果、谷歌内核
		            gecko: u.indexOf('Gecko') > -1 && u.indexOf('KHTML') == -1,//firefox
		            mobile: !!u.match(/AppleWebKit.*Mobile.*/), //是否为移动终端
		            ios: !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/), //ios
		            android: u.indexOf('Android') > -1 || u.indexOf('Linux') > -1, //android终端或者uc浏览器
		            iPhone: u.indexOf('iPhone') > -1 , //是否为iPhone或者QQHD浏览器
		            iPad: u.indexOf('iPad') > -1, //是否iPad
		            webApp: u.indexOf('Safari') == -1, //是否web应该程序，没有头部与底部
		            weixin: u.indexOf('MicroMessenger') > -1, //是否微信 （2015-01-22新增）
		            qq: u.match(/\sQQ/i) == " qq" //是否QQ
		        };
		    }(),
		    language:(navigator.browserLanguage || navigator.language).toLowerCase()
		}
	window.browser = browser;
})(window.navigator);