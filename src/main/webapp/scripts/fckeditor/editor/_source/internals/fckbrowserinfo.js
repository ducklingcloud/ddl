/*
 * FCKeditor - The text editor for Internet - http://www.fckeditor.net
 * Copyright (C) 2003-2008 Frederico Caldeira Knabben
 *
 * == BEGIN LICENSE ==
 *
 * Licensed under the terms of any of the following licenses at your
 * choice:
 *
 *  - GNU General Public License Version 2 or later (the "GPL")
 *    http://www.gnu.org/licenses/gpl.html
 *
 *  - GNU Lesser General Public License Version 2.1 or later (the "LGPL")
 *    http://www.gnu.org/licenses/lgpl.html
 *
 *  - Mozilla Public License Version 1.1 or later (the "MPL")
 *    http://www.mozilla.org/MPL/MPL-1.1.html
 *
 * == END LICENSE ==
 *
 * Contains browser detection information.
 */

var FCKBrowserInfo = (function() {
	var s = navigator.userAgent.toLowerCase();
	var browserInfo = {
		IsIE : /* @cc_on!@ */false || s.indexOf('trident')!=-1 ,
		IsIE6 : /* @cc_on!@ */false && (parseInt(s.match(/msie (\d+)/)[1], 10) >= 6),
		IsIE7 : !!s.match(/msie (\d+)/) && (parseInt(s.match(/msie (\d+)/)[1], 10) >= 7) && (parseInt(s.match(/msie (\d+)/)[1], 10) <= 8),
		IsIE9 : !!s.match(/msie (\d+)/) && (parseInt(s.match(/msie (\d+)/)[1], 10) == 9),
		IsIE10 : !!s.match(/msie (\d+)/) && (parseInt(s.match(/msie (\d+)/)[1], 10) == 10),
		IsIE11 : s.indexOf('trident') != -1 && (parseFloat(s.match(/trident\/(\d+)/)[1], 10) >= 7.0),
		IsSafari : s.Contains(' applewebkit/'), // Read "IsWebKit"
		IsOpera : !!window.opera,
		IsAIR : s.Contains(' adobeair/'),
		IsMac : s.Contains('macintosh'), 
		IsChrome : s.Contains('chrome')
	};

	// Completes the browser info with further Gecko information.
	browserInfo.IsGecko = (navigator.product == 'Gecko')
			&& !browserInfo.IsSafari && !browserInfo.IsOpera;
	browserInfo.IsGeckoLike = (browserInfo.IsGecko || browserInfo.IsSafari || browserInfo.IsOpera);

	if (browserInfo.IsGecko) {
		var geckoMatch = s.match(/rv:(\d+\.\d+)/);
		var geckoVersion = geckoMatch && parseFloat(geckoMatch[1]);

		// Actually "10" refers to Gecko versions before Firefox 1.5, when
		// Gecko 1.8 (build 20051111) has been released.

		// Some browser (like Mozilla 1.7.13) may have a Gecko build greater
		// than 20051111, so we must also check for the revision number not to
		// be 1.7 (we are assuming that rv < 1.7 will not have build >
		// 20051111).

		if (geckoVersion) {
			browserInfo.IsGecko10 = (geckoVersion < 1.8);
			browserInfo.IsGecko19 = (geckoVersion > 1.8);
		}
	}
	browserInfo.IsGeckoFamily=browserInfo.IsGecko || browserInfo.IsGeckoLike;
	return browserInfo;
})();
