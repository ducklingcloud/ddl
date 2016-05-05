/* grid and tag
 *	append to webpage to use.
 *	version: 0.9.1. May.3, 2012
 *  by zhangshixiang@cnic.cn
 */

$(document).ready(function(){
	/*$('p.ui-navList-title').click(function(){
		$(this).viewFocus();
	});*/
	$("#resourceList tr").mouseover(function(){
		$(this).css({"background-color":"#eef"});
	});
	$("#resourceList tr").mouseout(function(){
		$(this).css({"background-color":""});
	});
	$("#pullDown").click(function(){
		$(".pulldownMenu").show();
	});
	$("#selection").bind("click",function(){
		$(this).hide();
	});
	
	$("#checkOrNot").mouseleave(function(){
		$(".pulldownMenu").hide();
	});
	
	$("#showByList").click(function(){
		$("#resourceList").show();
		$("#resourceGrid").hide();
		$(".chosen").removeClass("chosen");
		$(this).addClass("chosen");
	});
	$("#showByThumbnail").click(function(){
		$("#resourceGrid").show();
		$("#resourceList").hide();
		$(".chosen").removeClass("chosen");
		$(this).addClass("chosen");
	});
	
	$("#tagSelector p.ui-navList-title .iconLink").click(function(event){
		event.stopPropagation();
		alert("manage the tags");
	});
	
	$("#commonlyUse ul#grid9 div").click(function () {
	      $(this).toggleClass("grid9Pin");
	});

	$("#resourceList #starID1,#resourceGrid .oper div").click(function () {
	      $(this).toggleClass("resStar");
	});

	
/* navigation begin */
	/*$('p.ui-navList-title').click(function(){
		$(this).viewFocus();
	});*/
	
	$('#stdNav #resMore').pulldownMenu({
		'menu': $('#resourceMenu'),
		'block': true,
		'anchor': $('#stdNav #resMore').parent()
	});
	
	
	var userbox = $('#userBox');
	var spaceNav = $('#spaceNav');
	var spaceNavLi = $('#spaceNav li');
	var spaceNavMenu = $('#spaceNavMenu');
	
	var spacePDMenu = $('#spaceNavMore .moreSpace a');
	spacePDMenu.pulldownMenu({ 'menu': $('#spaceNavMenu') });
	
	$('label[name="select"]').pulldownMenu({ 'menu': $('#selectionMenu') });
	
	//resizeSpaceNav();
	
	spaceNavAdjust();
	spacePDMenu.resetPosition();
	
	leftMenu_bind();
/* navigation end */
	
/* leftMenu fixed begin */
	function leftMenu_bind() {
		// locate local navigation bar when window scrolls
		var _leftMenu = {
				'$subject': $(".common-space #tagSelector"),
				'fixed': false
			};
		var _threshold = parseInt(_leftMenu.$subject.css('top')) - $('#macroNav').outerHeight(); 
		$(window).scroll(function(){
			if (!_leftMenu.fixed && $(window).scrollTop() > _threshold) {
				_leftMenu.$subject.css({"top":"78px"});
				_leftMenu.fixed = true;
			}
			else if (_leftMenu.fixed && $(window).scrollTop() <= _threshold) {
				_leftMenu.$subject.css({"top":"109px"});
				_leftMenu.fixed = false;
			}
		});
	}
/*leftMenu fixed end */
	
});


