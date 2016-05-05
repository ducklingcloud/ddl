<%@ page import="cn.vlabs.duckling.vwb.*" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ taglib uri="/WEB-INF/tld/vwb.tld" prefix="vwb" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<fmt:setBundle basename="templates.default"/>
<link rel="stylesheet" type="text/css" href="${contextPath}/diff/diff.css" />
<script type="text/javascript">

$(document).ready(function(){
	var ctrl = $('#diffCtrl');
	var body = $('.diffbody');
	var mPointer = $('#movePointer');
	var ctrlOri = ctrl.position().top;
	
	var diffIndex = 0;
	//get diff items
	var diffs = body.find('.add, .delete, span.update_del, span.update_add, .moveto');
	
	//initialize : positioning
	function initialize() {
		ctrl.css('width', ctrl.width());
		$('#diffCtrlSpacer').css('height', $('#diffCtrlSpacer').height());
		$('#mask').css({
				top : ctrlOri,
				width : ctrl.width(),
				height : $('.dataTable').position().top - ctrlOri
		});
		mPointer.css('left', ctrl.offset().left + ctrl.width()*0.8);
	}
	initialize();
	$(window).resize(function(){
		$('#mask').css({ width: ctrl.width() });
		mPointer.css('left', ctrl.offset().left + ctrl.width()*0.8);
	});
	
	//clean empty diffs
	var diffSum = 0;
	var movePointer;
	diffs.each(function(){
		if ($.trim($(this).html())=='') {
		//	alert('remove '+$(this).attr('key')+$(this).html());
			diffs.splice(diffSum, 1);
		}
		diffSum++;
	});
	
	diffSum = diffs.length;
	$('#diffSum').text(diffSum);
	if (diffSum==0) {
		$('#diffCtrl a.diffNext').addClass('disabled');
	}
	else {
		$('#diffCount').html('1');
	}
	
	focusElement();
	
	$(window).scroll(function(){
		var scrDiff = $(window).scrollTop() - ctrlOri;
		var limit = body.offset().top + body.height() - $(window).scrollTop();
		if (scrDiff>0 && limit>0)
			ctrl.css('position', 'fixed').css('top', '0');
		else
			ctrl.css('position', '');
	});
	
	
	
	function focusElement(){
		if (diffs.index($('.diffFocus')) != diffIndex) {
			$('.diffFocus').each(function(){
				if ($(this).hasClass('move')) {
					hideMoveFrom($(this).parents('.moveto'));
				}
				
				$(this).removeClass('diffFocus').css({
					'background-color': '',
					'color': ''
				});
			});
		}
		
		item = $(diffs[diffIndex]);
		//scroll window
		var loc = item.offset().top - $(window).scrollTop();
		var h = $(window).height();
		if (loc < h/4 || loc > h*3/4) { 
			var distance = loc - h/3;
			var topspeed = 10;
			$('html, body').animate({ scrollTop: $(window).scrollTop()+distance }, 700);
		}
		
		if (item.hasClass('moveto')) {
			//move
			item.find('.move').addClass('diffFocus').css({
				'background-color': '#fff',
				'color': '',
			});
			
			showMoveFrom(item);
			
		}
		else {
			item.addClass('diffFocus').css({
				'background-color': '#fff',
				'color': '',
			});
		}
	}
	
	function showMoveFrom(src) {
		var from = $('.movefrom[moveIndex="' + src.attr('moveIndex') + '"]');
		
		from.slideDown();
		if (src.offset().top > from.offset().top) {
			mPointer.addClass('pointUp').css('top', src.offset().top-mPointer.height());
		}
		else {
			mPointer.addClass('pointDown').css('top', src.offset().top + src.height());
		}
		mPointer.fadeIn();
		
		if (from.offset().top > ($(window).scrollTop() + $(window).height())
			|| from.offset().top < ($(window).scrollTop() - $(window).height())	
			) {
			//from out of sight
			mPointer.bind('click', function(){
				$('html, body').animate({ scrollTop: from.offset().top + 0.2*$(window).height() }, 700);
			});
		}
	}
	
	function hideMoveFrom(src) {
		var from = $('.movefrom[moveIndex="' + src.attr('moveIndex') + '"]');
		mPointer.fadeOut().unbind('click');
		from.slideUp();
	}
	
	
	$('a.diffNext').click(function(){
		if (diffIndex<diffSum-1) {
			diffIndex++;
			focusElement();
			if (diffIndex>-1)
				$('#diffCount').html(diffIndex+1);
			if (diffIndex==(diffSum-1))
				$('a.diffNext').addClass("disabled");
			$("a.diffPrev").removeClass("disabled");
		}
	});
	$('a.diffPrev').click(function(){
		if (diffIndex>0) {
			diffIndex--;
			focusElement();
			$('#diffCount').html(diffIndex+1);
			if (diffIndex==0){
				$('a.diffPrev').addClass("disabled");
			}
			$('a.diffNext').removeClass("disabled");
		}
	});
	
	diffs.each(function(){
		$(this).click(function(){
			if (!$(this).hasClass('diffFocus')
				 && !($(this).hasClass('moveto') && $(this).find('.diffFocus').length>0)	
			) {
				diffIndex = diffs.index($(this));
				focusElement();
				$('#diffCount').html(diffIndex+1);
				if (diffIndex==0) {
					$('a.diffPrev').addClass('disabled');
				}
				else if (diffIndex==(diffSum-1)) {
					$('a.diffNext').addClass('disabled');
				}
				else {
					$('a.diffPrev, a.diffNext').removeClass('disabled');
				}
			}
		});
	});
});
</script>


<form action="<vwb:Link format='url' context='info'/>${rid}" method="post" accept-charset="UTF-8">
<input type="hidden" name="func" value="diff"/>
<div id="diffcontent">
	<div id="mask"></div>
	<div id="movePointer">移动自${version }</div>
	<div id="diffCtrlSpacer">
	<div id="diffCtrl">
		<h4>
			<input type="hidden" name="page" value="<vwb:Variable key='pagename' />" />
			<fmt:message key="diff.difference">
			  <fmt:param>
			    <select id="r1" name="r1" onchange="this.form.submit();" >
			    <c:forEach items="${history}" var="i">
			      <option value="<c:out value='${i.version}'/>" <c:if test="${i.version == version}">selected="selected"</c:if> ><c:out value="${i.version}"/></option>
			    </c:forEach>
			    </select>
			  </fmt:param>
			  <fmt:param>
			    <select id="r2" name="r2" onchange="this.form.submit();" >
			    <c:forEach items="${history}" var="i">
			      <option value="<c:out value='${i.version}'/>" <c:if test="${i.version == compareTo}">selected="selected"</c:if> ><c:out value="${i.version}"/></option>
			    </c:forEach>
			    </select>
			  </fmt:param> 
			</fmt:message>
			
			<span id="diffNav">
				<a href="javascript:void(0)" class="diffPrev disabled" title="前一个"></a>
				差异：<span id="diffCount">0</span>/<span id="diffSum"></span>
				<a href="javascript:void(0)" class="diffNext" title="下一个"></a>
			</span>
			
		</h4>
	</div>
	</div>

	<div class="diffbody" id="DCT_viewcontent">
	  <vwb:InsertDiff ><i><fmt:message key="diff.nodiff"/></i></vwb:InsertDiff> 
	</div>

</div>

</form>
