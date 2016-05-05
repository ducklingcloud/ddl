// JavaScript Document
function checkFileType(value){
	return (/.*\.[zZ][Ii][Pp]$/.test(value));
}
var uploadDialog={
	form:null,
	divId:"#upload",
	exist:false,
	ok:true,
	init:function(){
		this.form=document.uploadForm;
		$("#uploadbutton").click(function(){
			uploadDialog.show();
		});
		$("#upload a[name='cancel']").click(function(){
			uploadDialog.hide();
		});
		$(this.form).submit(this.submit);
	},
	show:function(){
		this.form.reset();
		this.showError("");
		ui_showDialog(this.divId);
	},
	hide:function(){
		$(this.divId).hide();
	},
	submit:function(){
		if (!this.ok){
			alert($("#uploadError").innerHtml());
			return;
		}
		if ( this.form.skinname.value==null ||  this.form.skinname.value==""){
			alert(errors['info'], errors['empty']);
			return;
		}
		if (!checkFileType(this.form.file.value)){
			alert(errors['needzip']);
			return;
		}
		if (this.exist){
			if (confirm(errors['override'])){
				this.form.elements[0].value='update';
				this.form.submit();
			}
			return;
		}
		this.form.submit();
	},
	showError:function(message){
		$("#uploadError").html(message);
	},
	checkSkinname:function (input){
		$.ajax({
			url:site.getTeamURL("skin"),
			data:{func:"check", skinname:input.value},
			dataType:"text",
			type:"POST",
			success:function(message){
				var respText =message;
				if (respText!='OK'){
					if (respText=='exist'){
						uploadDialog.exist=true;
					}else{
						uploadDialog.ok=false;
					}
					uploadDialog.showError(errors[respText]);
				}else{
					this.ok=true;
					uploadDialog.showError("");
				}
			},
			error:function(){
				alert(errors['unreachable']);
			}
		});
	}
}
var updateDialog={
	form:null,
	titleElement:null,
	divId:"#update",
	init:function(){
		this.form=document.updateForm;
		this.titleElement=$("#updateDialogTitle");
		$("#update a[name=cancel]").click(function(){
			updateDialog.hide();
		});
	},
	show:function(skinname){
		this.form.reset();
		this.form.skinname.value=skinname;
		this.titleElement.html(skinname);
		ui_showDialog(this.divId);
	},
	hide:function(){
		$(this.divId).hide();
	},
	submit:function(){
		if ( this.form.skinname.value==null || this.form.skinname.value==""){
			alert(errors['empty']);
			return;
		}
		if (!checkFileType(this.form.file.value)){
			alert(errors['needzip']);
			return;
		}
		this.form.submit();
	}
};
var dashBoard={
	skinname:null,
	skinwebpath:null,
	init:function(){
		$(".skindiv").each(function(i, div){
			$(div).mouseover(function(){
				$(this).addClass("_dct_skin_hover");
			});
			$(div).mouseout(function(){
				$(this).removeClass("_dct_skin_hover");
			});
			$(div).click(function(){
				dashBoard.applySkin($(this).attr("skinname"), $(this).attr("global"));
			});
			dashBoard._initMenu(div);
		})
	},
	_initMenu:function(div){
			$(div).contextMenu('skinMenu',{
				onContextMenu:function(e){
					dashBoard.skinname = $(e.currentTarget).attr("skinname");
					dashBoard.skinwebpath = $(e.currentTarget).attr("skinwebpath");
					return true;
				},
				bindings: {
			            'update': function(){
			            	updateDialog.show(dashBoard.skinname);
			            },
			            'remove': function(){
			            	if (confirm(errors['confirmdelete'].replace("%1",dashBoard.skinname))){
	            				document.removeform.skinname.value=dashBoard.skinname;
								document.removeform.submit();
			            	}
			            },
			            'download':function(){
			            	window.location.href= skinwebpath+"/skin.zip";
			            }
				 }
			});
	},
	applySkin:function(skin, global){
		document.skinform.skinname.value=skin;
		document.skinform.global.value=global;
		document.skinform.submit();
	}
}

$(document).ready(function(){
	updateDialog.init();
	uploadDialog.init();
	dashBoard.init();
});