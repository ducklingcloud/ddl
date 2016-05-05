(function($) {
	$.fn.pv_timing = function() {
		var inTime, outTime, stayTime;
		window.onload = function() {
			inTime = new Date().getTime();   
		};
		window.onunload = function() {
			outTime = new Date().getTime();
			stayTime = (outTime - inTime) / 1000;
			$.ajax({
				type : 'POST',
				url : '/logBrowseTime/ajax',
				data : {
					stayTime : stayTime,
					currentUrl : window.location.href
				},
				success : function() {
				}
			}); // end of ajax
		};
	}; // end of function
})(jQuery);
