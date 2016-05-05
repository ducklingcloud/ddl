/*
VERSION:	dHome 1.0
AUTHOR:		Vera. zhangshixiang@cnic.cn / shivera2004@163.com
DATE:		August, 2012
*/
/* Left Menu*/
$(document).ready(function(){
	var clientWidth = window.screen.width;
	var marginLeft = (clientWidth - $("#macro-innerWrapper").outerWidth()) / 2;
	$("ul#indexList").css({"padding-left":marginLeft,"padding-right":marginLeft,"top":"18px"});
	$("ul.nav.nav-list li").hover(function(){
		$(this).find(".left-pull-down").css({"border-top-color":"#000"});
	});
	$("ul.nav.nav-list li").mouseleave(function(){
		$(this).find(".left-pull-down").css({"border-top-color":"transparent"});
	});
	$("ul.nav.nav-list li span").click(function(event){
		var itemId = $(this).attr("itemId");
		$(".left-dropdown-menu").show();
		$(".left-dropdown-menu").attr("parentId",itemId); //子菜单操作的父元素的menu_item_id
		var top = $(this).offset().top;
		var left = $(this).offset().left;
		$(".left-dropdown-menu").css({"top":top,"left":left});
	});
	$(".left-dropdown-menu li").live("click",function(){
		$(".left-dropdown-menu").hide();
	});
	var backBox = new BackToTop("回顶部");
});