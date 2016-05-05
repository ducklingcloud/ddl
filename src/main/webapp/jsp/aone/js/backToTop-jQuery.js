/* BACK TO TOP */
function BackToTop(TEXT) {
	//append items
	this.fulH = $(document).height();
	this.winH = $(window).height();
	$('#content').append('<div id="backToTop" title="'+TEXT+'"></div>');
	this.trigger = $('#backToTop');
	this.trigger.css('left', $('#content').offset().left + parseInt(this.trigger.css('left')));
	var entity = this;
	this.trigger.click(function(){
		entity.toTop();
	});
	$(window).scroll(function(){
		entity.appearance($(this).scrollTop());
	});
}
	BackToTop.prototype.appearance = function(SCRTOP) {
		if (SCRTOP > 0.2*this.winH) {
			this.trigger.fadeIn();
		}
		else {
			this.trigger.fadeOut();
		}
	};
	
	BackToTop.prototype.toTop = function() {
		$(window).scrollTop(0);
	};
