/*
  VERSION: escience 
  AUTHOR:  Vera. zhangshixiang@cnic.cn / shivera2004@163.com
  DATE:    Jan, 2013
*/

/* CommonBanner */

$(document).ready(function() {
    commonBanner();
    var pulldown = setTimeout(function() {
	var clientWidth = window.screen.width;
	var marginLeft = (clientWidth - $(".container").outerWidth()) / 2;
	$("ul#indexList").css( {"padding-left":marginLeft,
                                "padding-right":marginLeft} );
	$(".nav-bar.fix-top .container .caret, .nav-bar.fix-top .container a.logo, " +  //escience index
	  ".top-nav ul.nav a.nav-logo,.top-nav ul.nav .caret, " +  //nav site
	  ".navbar ul#dhome-nav.nav a.dropdown-toggle," +  //dhome
	  "#macroNav a#logo"  //ddl
	 ).click(function() {
             // Disabled temporarily <2022-03-08 Tue>
             // $("ul.headerBar").toggle();
             // window.location.href = "/ddl";
	 } );
	$(".headerBar").click(function() {
	    $(".headerBar").hide();
	} );
    }, 100);
} );

function commonBanner() {
    $("body").append(		
	'<ul class="headerBar" id="indexList" style="display:none;">'+
	    '	<li>'+
	    '		<a class="header-block escience" href="http://www.escience.cn" target="_blank">'+
	    '			<span class="logo header-escience"></span> '+
	    '			<span class="header-text">科研在线</span>'+
	    '		</a>'+
	    '	</li>'+
	    '	<li>'+
	    '		<a class="header-block ddl" href="http://ddl.escience.cn" target="_blank">'+
	    '			<span class="logo"></span> '+
	    '			<span class="header-text">文档库</span>'+
	    '		</a>'+
	    '	</li>'+
	    '	<li>'+
	    '		<a class="header-block dhome" href="http://www.escience.cn/people/" target="_blank">'+
	    '			<span class="logo"></span> '+
	    '			<span class="header-text">科研主页</span>'+
	    '		</a>'+
	    '	</li>'+
	    '	<li>'+
	    '		<a class="header-block csp" href="http://csp.escience.cn" target="_blank">'+
	    '			<span class="logo"></span> '+
	    '			<span class="header-text">会议服务平台</span>'+
	    '		</a>'+
	    '	</li>'+
	    '	<li>'+
	    '		<a class="header-block email" href="http://mail.escience.cn" target="_blank">'+
	    '			<span class="logo"></span> '+
	    '			<span class="header-text">邮箱</span>'+
	    '		</a>'+
	    '	</li>'+
	    '	<li>'+
	    '		<a class="header-block nav" href="http://www.escience.cn/site" target="_blank">'+
	    '			<span class="logo"></span> '+
	    '			<span class="header-text" style="font-size:12px">中国科技网资源导航</span>'+
	    '		</a>'+
	    '	</li>'+
	    '	<li>'+
	    '		<a class="header-block dc" href="http://rol.escience.cn" target="_blank">'+
	    '			<span class="logo"></span> '+
	    '			<span class="header-text">实验室信息系统</span>'+
	    '		</a>'+
	    '	</li>'+
	    '</ul>'
    );
}
