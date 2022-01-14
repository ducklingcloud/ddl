/* CommonBanner*/
$(document).ready(function(){
    commonSimpleFooter();
});

function commonSimpleFooter(){
    // TODO update for vmt, if any
    // var isZh=(!umtLocale||umtLocale=='zh_CN');
    var html='';
    if (1) { // if(isZh){
	html='<div class="dface container footer">'+
	    '	<p>'+
	    '		Powered by'+
	    '		<a target="_blank" href="http://ducklingcloud.net/"> Duckling 4.0 </a>'+
	    '		<span id="app-version"></span> '+
	    '	</p>'+
	    '</div>';
    }
    
    $("#footer").append(html);
}
