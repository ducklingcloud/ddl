function addQueryClause(hash,key,value) {
	hash = hash + "&" + key + "=" + value;
	return hash;
};

function removeQueryClause(hash,key,value) {
	var regx = new RegExp("&" + key + "=" + value);
	hash = hash.replace(regx, '');
	return hash;
};

function replaceQueryClause(hash,key,value) {
	var regx = new RegExp("&" + key + "=[^&]+");
	hash = hash.replace(regx, "&" + key + "=" + value);
	return hash;
};

function upsertQueryClause(hash,key, value) {
	var regx = new RegExp("&" + key + "=[^&]+");
	if (hash.search(regx) == -1)
		hash = addQueryClause(hash,key,value);
	else
		hash = replaceQueryClause(hash,key,value);
	return hash;
};

function removeAllParameters(hash,key) {
	var regx = new RegExp("&" + key + "=[^&]+");
	while(hash.match(regx)!=null){
		hash = hash.replace(regx, '');
	}
	return hash;
};

function getParameterValue(hash, key) {
	var regx = new RegExp('&' + key + "=([^&]+)");
	var val = [];
	var temp;
	
	while((temp=hash.match(regx)) != null) {
		val.push(temp[1]);
		hash = hash.replace(regx, '');
	}
	return val;
}

function singeParameterToggle(hash,selector) {
	if ($(selector).parent().hasClass("chosen"))
		hash = upsertQueryClause(hash, $(selector).attr("key"),$(selector).attr("value"));
	else
		hash = removeQueryClause(hash, $(selector).attr("key"),$(selector).attr("value"));
	return hash;
};

function singleChosenToggle(selector) {
	if (!$(selector).parent().hasClass("chosen")) {
		$(selector).parent().parent().children().removeClass('chosen');
		$(selector).parent().addClass("chosen");
		$('#remove-single-option').parent().removeClass('chosen');
	} else {
		$(selector).parent().removeClass("chosen");
	}
};

function multipleChosenToggle(selector) {
	if (!$(selector).parent().hasClass("chosen")) {
		$(selector).parent().addClass("chosen");
		$('#remove-single-option').parent().removeClass('chosen');
	} else {
		$(selector).parent().removeClass("chosen");
	}
};

function multipleParameterToggle(hash,selector) {
	if ($(selector).parent().hasClass("chosen"))
		hash = addQueryClause(hash,$(selector).attr("key"), $(selector).attr("value"));
	else
		hash = removeQueryClause(hash,$(selector).attr("key"), $(selector).attr("value"));
	return hash;
};

function singleOptionToggle(hash,selector){
	singleChosenToggle(selector);
	hash = singeParameterToggle(hash,selector);
	return hash;
};

function multipleOptionsToggle(hash,selector){
	multipleChosenToggle(selector);
	hash = multipleParameterToggle(hash,selector);
	return hash;
};

function singleStateToggle(selector){
	var hash = location.hash;
    hash = singleOptionToggle(hash,selector);
    window.location.hash = hash;
};

function multipleStateToggle(selector){
	var hash = location.hash;
    hash = multipleOptionsToggle(hash,selector);
    window.location.hash = hash;
};

