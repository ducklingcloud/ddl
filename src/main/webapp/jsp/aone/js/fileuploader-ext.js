qq.extend(qq.FileUploader.prototype,{
	_addToList: function(id, fileName){
        var item = qq.toElement(this._options.fileTemplate);                
        item.qqFileId = id;

        var fileElement = this._find(item, 'file');        
        qq.setText(fileElement, this._formatFileName(fileName));
        this._find(item, 'size').style.display = 'none';        

        this._listElement.appendChild(item);
        $("#popupUpload").show();
    	//$("#fileListDiv").slideUp(5000);
        $("#fileListDiv").show();
    	//$("#fileListDiv").fadeIn(1000);
    	//$("#fileListDiv").fadeOut(5000);
    }, 
    _onComplete: function(id, fileName, result){
        qq.FileUploaderBasic.prototype._onComplete.apply(this, arguments);

        // mark completed
        var item = this._getItemByFileId(id);     
        qq.remove(this._find(item, 'cancel'));
        qq.remove(this._find(item, 'spinner'));
        if (result.success){
            qq.addClass(item, this._classes.success);
        	$(item).find("span.qq-upload-file").wrap("<a href='javascript:void(0)' class='viewFile' rid='"+result.resource.rid+"'></a>");
        } else {
            qq.addClass(item, this._classes.fail);
        }       
        
    },
    _CancelAll: function(){
        var self = this,
        list = this._listElement;
        $(list).find("a.qq-upload-cancel").each(function(){
        		   var item = this.parentNode;
	                self._handler.cancel(item.qqFileId);
	                qq.remove(item);
        } );
    } ,
    _setupDragDrop: function(){
    	var self = this;
    	var dropArea = document.getElementById("bodyElement");
    	 var dz = new qq.UploadDropZone({
    		 element: dropArea,
             onEnter: function(e){
                 e.stopPropagation();
                 if(self._isFolder(e.target)){
                	 var folder = self._getFolderElement(e);
                	 if(!$(folder).hasClass("dropFolder")){
                		 $(".dropFolder").removeClass("dropFolder");
                		 $("body").removeClass("drag");
                		 $("body .dragCover").hide("fast");
                		 $("body .dragCoverBox").slideUp("fast");
                		 folder.addClass("dropFolder");
                	 }
                 }else{
                	 $(".dropFolder").removeClass("dropFolder");
                	 $("body").addClass("drag");
                	 $("body.drag .dragCover").show("fast");
                	 $("body .dragCoverBox").slideDown("fast");
                 }
             },
             onLeave: function(e){
                 e.stopPropagation();
             },
             onLeaveNotDescendants: function(e){
            	 $("body").removeClass("drag");
            	 $("body .dragCover").hide("fast");
            	 $("body .dragCoverBox").slideUp("fast");
            	 $(".dropFolder").removeClass("dropFolder");
             },
             onDrop: function(e){
            	 e.stopPropagation();
            	 $("body").removeClass("drag");
            	 $("body .dragCover").hide("fast");
            	 $("body .dragCoverBox").slideUp("fast");
            	 $(".dropFolder").removeClass("dropFolder");
            	 var length = e.dataTransfer.files.length;
            	 if(length==0){
            		 //IE
            		 showMsgAndAutoHide('暂不支持文件夹上传 ！','error',3000);
     				return;
            	 }
            	  for (var i = 0; i < length; i++) {
            		//chrome
            		if(e.dataTransfer.items){
            			var entry = e.dataTransfer.items[i].webkitGetAsEntry();
            			if (entry.isDirectory) {
            				showMsgAndAutoHide('暂不支持文件夹上传 ！','error',3000);
            				return;
            			}
            		}else{
            			//firefox
            			var f = e.dataTransfer.files[i];
            			if (!f.type && f.size%4096 == 0 && f.size <= 102400) {
        			        //file is a directory
        			    	showMsgAndAutoHide('暂不支持文件夹上传 ！','error',3000);
            				return;
            			}
            		}
            	  }
            	 
            	 if(self._isUploadToFolder(e)){
            		 self._uploadFileList(e.dataTransfer.files,self._isUploadToFolder(e));   
            	 }else{
            		 self._uploadFileList(e.dataTransfer.files);    
            	 }
            }
       	});
    },
    _isFolder:function(target){
    	if($(target).hasClass("files-item")){
    		return this._haveFolderData($(target));
    	}else{
    		var v = $(target).parents("li.files-item");
    		return this._haveFolderData(v);
    	}
    },
    _haveFolderData:function(p){
    	if(!(p[0]===undefined)){
    		var data = p.data('tmplItem').data;
    		if(data.itemType=='Folder'){
    			return true;
    		}else{
    			return false;
    		}
    	}else{
    		return false;
    	}
    },
    _getFolderElement:function(e){
    	var target = e.target;
    	if($(target).hasClass("files-item")){
    		return $(target);
    	}else{
    		return $(target).parents("li.files-item");
    	}
    },
    _isUploadToFolder:function(e){
    	if(e.target){
    		var p = $(e.target).parents("li.files-item");
    		if(!(p[0]===undefined)){
    			var data = p.data('tmplItem').data;
    			if(data.itemType=='Folder'){
    				return {'parentRid':data.rid};
    			}else{
    				return false;
    			}
    		}else{
    			return false;
    		}
    	}else{
    		return false;
    	}
    },
});