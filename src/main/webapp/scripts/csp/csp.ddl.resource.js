$.template("cspResourceTr","<tr><td><input type=\"radio\" name=\"path\" value=\"{{= path}}\" /></td><td title=\"{{= title}}\"><span class=\"CSPHeadImg DFile {{html getExt(title)}}\"></span> <span class=\"ellipsis\">{{= title}}</span></td><td class='ddlSize'>{{html bytesToSize(size)}}</td><td class='ddlTime'>{{html formatDate(lastEditTime)}}</td></tr>");
	
var Pagination = {
  DEFAULT_PAGE_SIZE:10,
  create: function (container, pageFunction, recordTotal, pageSize, pageCurrent){
	var _t = {};
	_t.container = container;
	_t.pageCurrent = pageCurrent || 1;
	_t.recordTotal = recordTotal;
	_t.pageSize = pageSize || Pagination.DEFAULT_PAGE_SIZE;
	_t.pageTotal = parseInt(_t.recordTotal%_t.pageSize == 0 ? _t.recordTotal/_t.pageSize : _t.recordTotal/_t.pageSize + 1);
	var getLink = function(page, style){
		if(style=="active"){
			return $("<li class=\"active\"><span>"+page+"</span></li>");
		}else{
			return $("<li></li>").append($("<a href=\"javascript:void(0);\">"+page+"</a>").bind("click",{"page":page},pageFunction));
		}
	};
	var getOperateLink = function(text, page){
			return $("<li></li>").append($("<a href=\"javascript:void(0);\">"+text+"</a>").bind("click",{"page":page},pageFunction));
	};
	_t.getStart = function(pageCurrent){
		return (pageCurrent - 1) * _t.pageSize;
	};
	
	_t.change = function(pageCurrent){
		_t.pageCurrent = pageCurrent;
		_t.start = _t.getStart(pageCurrent);
		_t.end = _t.start + _t.pageSize - 1;
		if(_t.pageCurrent > _t.pageTotal){_t.pageCurrent = _t.pageTotal;}
		if(_t.pageCurrent == parseInt(_t.pageTotal && _t.recordTotal%_t.pageSize) != 0){
			_t.end = _t.recordTotal;
		}
		
		_t.container.empty();
		if(_t.recordTotal < _t.pageSize){
			return;
		}
		if(pageCurrent > 1){
			_t.container.append(getOperateLink("上一页",pageCurrent-1));
		}
		var pageStart = 1;
		var pageEnd = _t.pageTotal > Pagination.DEFAULT_PAGE_SIZE ? Pagination.DEFAULT_PAGE_SIZE  : _t.pageTotal;
		if(pageCurrent>6){
			pageStart = pageCurrent-5;
			
			if( pageCurrent+4 <_t.pageTotal){
				pageEnd = pageCurrent+4;
			}else{
				pageEnd = _t.pageTotal;
				pageStart -= 4-(_t.pageTotal - pageCurrent);
				pageStart = pageStart < 0 ? 1 : pageStart;
			}
		}
		for(var i=pageStart; i<= pageEnd; i++){
			if(i==pageCurrent){
				_t.container.append(getLink(i,"active"));
			}else{
				_t.container.append(getLink(i));
			}
		}
		if(pageCurrent < _t.pageTotal){
			_t.container.append(getOperateLink("下一页",pageCurrent+1));
		}
	};
	return _t;
  }
}

var ResourceList = {
  create : function(containerId, teamCode, auth, domain){
	var _t = {};
	var container = $("#"+containerId);
	var table = $("<table class=\"resourceTable\"><thead><tr><th></th><th class='ddlName'>文件名</th><th class='ddlSize'>大小</th><th class='ddlTime'>上传时间</th></tr></thead><tbody></tbody></table>");
	var pageContainer = $("<ul class=\"pagination\"></ul>");
	var searchBar = $("<div class=\"resourceSearch\"> <input type=\"text\" class=\"searchInput\" placeholder=\"文件搜索\" /> <input type=\"button\" class=\" searchBtn\" value=\"搜索\" /> </div>");
	var pager = null;
	var keyword = "";
	_t.teamCode = teamCode;
	_t.auth = auth;
	_t.domain = domain;
	_t.load = function(pageCurrent){
		pageCurrent = pageCurrent || 1;
		var begin = pager == null ? 0 : pager.getStart(pageCurrent);
		var limit = pager ? pager.pageSize : Pagination.DEFAULT_PAGE_SIZE;
		var param = {"begin" : begin, "limit": limit, "teamCode": teamCode, "auth":auth};
		if(keyword){
			$.extend(param,{"q":encodeURIComponent(keyword)});
		}
		
		$.getJSON(domain + "/system/csp/resource?jsoncallback=?",param,function(resp){
			table.find("tbody").empty();
			table.find("tbody").append($.tmpl("cspResourceTr", resp.data));
			
			if(resp.total>0){
				pager = new Pagination.create(pageContainer, goPage, resp.total);
			    pager.change(pageCurrent);
			}else{
				table.find("tbody").append("<tr><td colspan=\"4\" style='font-size:16px; color:#999; width:500px; text-align:center; padding:5px'>未搜索到文件.</td></tr>");
			}
		});
	};
	
	_t.setKeyword = function(val){
		keyword = val;
	};
	_t.sharing = function(callback){
		var path = table.find("input:checked").val();
		var param = {"path":encodeURIComponent(path), "teamCode": _t.teamCode, "auth": _t.auth};
		$.getJSON(_t.domain + "/system/csp/sharing?jsoncallback=?",param,function(resp){
			callback(resp);
		});
	};
	_t.hasChecked = function(){
		return table.find("input:checked").val() ? true : false;
	};
	function goPage(event){ _t.load(event.data.page); };
	function init(){
		container.append(searchBar).append(table).append(pageContainer);
		table.find("tbody").on("click","tr",function(){
			$(this).find("input").attr("checked","checked");
		});
		searchBar.find("input[type='button']").click(function(e){
			_t.setKeyword($.trim(searchBar.find("input[type='text']").val()))
			_t.load();
		});
	}
	init();
	return _t;
  }
}

function bytesToSize(bytes) {
    if (bytes === 0) return '0 B';
    var k = 1024, sizes = ['B', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'], i = Math.floor(Math.log(bytes) / Math.log(k));
   return (bytes / Math.pow(k, i)).toPrecision(3) + ' ' + sizes[i];
}
function getExt(fileName){ var i = fileName.lastIndexOf('.'); return (i==-1) ? "" : fileName.substring(i + 1); }
function formatDate(dateStr){ if(dateStr){ var dateStr = dateStr.replace(/\-/g,"/"); return dateStr.substring(0, dateStr.lastIndexOf(":")); } return "";}

