/**
 * 站点对象的JS实现，可以调用相关的方法在JS中获取站点的访问URL
 * 例：
 * 	String url = site.getViewURL(${pageId});
 */
var site = {
	params:{},
	patterns:{},
	reversePattern:[],
	currentPage:-1,
	absoluteTeambase:"",
	relativeTeambase:"",
	init:function(settings){
		if (settings.params){
			this.params=settings.params;
		}
		if (settings.patterns){
			this.patterns=settings.patterns;
		}
		if (settings.currpage){
			this.currentPage=settings.currpage;
		}
		if (settings.reversePattern){
			this.reversePattern = settings.reversePattern;
		}
		if (settings.absoluteTeambase){
			this.absoluteTeambase = settings.absoluteTeambase;
		}
		if (settings.relativeTeambase){
			this.relativeTeambase = settings.relativeTeambase;
		}
		
		if (!String.prototype.startsWith){
			String.prototype.startsWith = function(str){
				return this.substring(0, str.length) === str;
			}
		}
	},
	endWith:function(bematch){
		if (bematch){
			var len = this.baseurl.length;
			var str=this.baseurl.substr(len - bematch.length);
			return str==bematch;
		}
		return true;
	},
	getTeamURL:function(jsp){
		return this.getURL("team", jsp);
	},
	getViewURL:function(pageid){
		if (!pageid){
			pageid=this.currentPage;
		}
		return this.getURL("view", pageid);
	},
	getEditURL:function(pageid){
		if (!pageid){
			pageid=this.currentPage;
		}
		return this.getURL("edit", pageid);
	},
	getJSPURL: function(jsp){
		return this.getURL("plain", jsp);
	},
	getURL:function(context, page){
		var url = this.patterns[context];
		url = url.replace(/%u/g, this.params["%u"]);
		url = url.replace(/%U/g, this.params["%U"]);
		url = url.replace(/%t/g, this.params["%t"]);
		url = url.replace(/%p/g, this.params["%p"]);
		if (page!== undefined && page!== null){
			url = url.replace(/%n/g, page);
		}
		return url;
	},
	isInternalURL:function(url){
		if(!url){
			return false;
		}
		if (url.startsWith(this.absoluteTeambase)){
			return true;
		}
		if (url.startsWith(this.relativeTeambase)){
			return true;
		}
		return false;
	},
	resolve:function(url){
		if (this.isInternalURL(url)){
			var matchResult=null;
			for (var i=0;i<this.reversePattern.length;i++){
				matchResult = url.match(this.reversePattern[i].pattern);
				if (matchResult!=null && matchResult.length==2){
					return {type:this.reversePattern[i].type, key:matchResult[1]};
				}
			}
		}
		return null;
	}
};