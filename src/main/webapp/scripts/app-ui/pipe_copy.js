function PipeCopy(option){
	this.option = option;
	_option = {};
	var _this = this;
	_init = function(){
		var table = $("#"+_this.option.showTable);
		var left = ($(window).width()) / 2 + 180;
		table.css({"left":left});
		table.find("i.icon-max").die().live("click",function(){
			 maxTable(table);
	     });
		table.find("i.icon-minus").die().live("click",function(){
	    	 minTable(table);
	     });
		table.find("p.uploadTitle").die().live("click",function(){
	    	 toggleTable(table);
	     });
		table.find("i.fillUploadPagCal").die().live("click",function(){
			var finished = false;
			if(table.find("span.qq-upload-spinner").length==0){
				finished = true;
			}
			table.find("div.popupContent").html("");
			table.hide();
			if(typeof(_this.option.closeCallback)=="function"){
				_this.option.closeCallback(finished);
			}
	     });
		if(_option.time){
			clearInterval(_option.time);
			_option.time = null;
		}
		table.show();
		maxTable(table);
		table.die().live('hide',_this.close);
		if(typeof(_this.option.initCallback)=="function"){
			_this.option.initCallback();
		}
	};
	this.close = function(){
		$("#"+_this.option.showTable+" .close").die('click');
		if(_option.time){
			clearInterval(_option.time);
			_option.time = null;
		}
	};
	
	var queryTime;
	this.getCoyeStatus=function(){
	var time = this.option.time?this.option.time:2000;
	_option.time=setInterval(function(){
			var data = new Object();
			data.taskId = _this.option.taskId;
			var now = new Date();
			data.queryTime = now.getTime()+"";
			queryTime = data.queryTime;
			data.func="queryTask";
			$.ajax({
				url:_this.option.url,
				type:'post',
				data:data,
				dataType:'json',
				success:function(d){
					if(d.queryTime==queryTime){
						_this.showCopyStatus(d);
					}
				}
			});
			
		}, time);
	};
	_init();
	_this.showCopyStatus = function(d){
		var table = $("#"+_this.option.showTable);
		var ul;
		if(table.find(".taskId"+d.taskId).length>0){
			ul = table.find(".taskId"+d.taskId);
			ul.empty();
		}else{
			ul = $('<ul class="popupList taskId'+ d.taskId +'"></ul>');
		}
		
		for(var i=0; i < d.subTasks.length; i++){
			var li = $('<li style="list-style:none"></li>');
			var item = d.subTasks[i];
			li.append('<span class="headImg '+ item.itemType + ' ' + item.fileType + '"></span>');
			li.append('<span class="qq-upload-file">' + item.filename + '</span>');
			if(item.status!="failed" && item.status!="success" ){
				li.append('<span> ' + item.subSuccess + '/' + item.subTotal + '</span> <span class="qq-upload-spinner"> </span>');
			}else if(item.status=="failed"){
				li.append('<span class="qq-upload-failed-text"> 失败（'+ item.subFailed +'）</span>');
			}else if(item.status=="success"){
				li.append('<span style="color:green;"> 成功</span>');
			}
			ul.append(li);
		}
		if(d.status=='finished'){
			if(_option.time){
				clearInterval(_option.time);
				_option.time = null;
			}
			minTable(table);
		}
		table.find("li.hasDdoc").remove();
		if(_this.option.selectedCount && d.total < _this.option.selectedCount){
			ul.append('<li style="list-style:none;border-bottom:none;" class="hasDdoc"> <span style="color:#666;margin-left:-3px;">协作文档不支持复制到个人空间同步盘.</span> </li>');
		}
		table.find(".popupContent").append(ul);
	};
	
	function minTable(table){
		table.find(".popupContent").slideUp(2000);
		var obj=table.find("i.icon-minus");
		obj.removeClass("icon-minus");
		obj.addClass(" icon-max");
    }
    function maxTable(table){
    	table.find(".popupContent").show();
        var obj=$("i.icon-max");
        obj.removeClass("icon-max");
        obj.addClass("icon-minus");
    }
    function toggleTable(table){
		if(table.find(".popupContent").is(":hidden")){
			maxTable();
		} else{
			minTable();
		}
    }
}