<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />

<style>
.contactTable tbody tr.normal-row td {
	background: #eef;
}
.contactTable tbody tr.alt-row td {
	background: #fff;
}
.contactTable tbody tr.alt-row.chosen td {background:#69f;}
.contactTable tbody tr.normal-row.chosen td {background:#69f;}
</style>
<link rel="stylesheet" href="scripts/jquery-tablesorter/theme.default.css"> 
<script type="text/javascript" src="scripts/jquery-tablesorter/jquery.tablesorter.js"></script> 
<script type="text/javascript" src="scripts/jquery-tablesorter/jquery.tablesorter.widgets.js"></script> 
<script type="text/javascript">
$(document).ready(function(){
	
	/* editing & creating */
	$('a.editContact').live('click', function(){
		$('#detail').hide();
		var requestURL = "<vwb:Link context='contacts' format='url'/>?func=getItemData";
		ajaxRequest(requestURL,"itemId="+this.id+"&itemType="+this.name,editData);
		$('#editDetail').fadeIn();
	});
	$('a.deleteContact').live('click', function(){
		ui_showDialog('delete-contact-dialog');
		var itemID = this.id;
		$('#delete-contact-confirm').bind('click', function(){
			var requestURL = "<vwb:Link context='contacts' format='url'/>?func=deleteItem";
			ajaxRequest(requestURL,"itemId="+itemID, refresh);
		});
	});
	
	$("a.ui-dialog-close.ui-text-small").live("click",function(){
		$(".ui-dialog-cover").hide(); 
	})
	$("span.ui-dialog-close").live("click",function(){
		$(".ui-dialog-cover").hide(); 
	})
	$('#save-contact-button').live('click', function(){
		var validCheck = true;
		var username = $('#person-item-form input[name="name"]');
		var usernameAlertText = '请填写联系人姓名';
		if(trim(username.attr("value")) == "") {
			username.addClass('error').val(usernameAlertText)
				.focus(function(){
					if (trim($(this).val()) == usernameAlertText) {
						$(this).val('');
					}
				})
				.keydown(function(){
					$(this).removeClass('error');
				})
				.blur(function(){
					if (trim($(this).val()) != '') {
						$(this).removeClass('error');
					}
					else {
						$(this).addClass('error').val(usernameAlertText);
					}
				});
			alertFade(username);
			validCheck = false;
		}
		
		var email = $('#person-item-form input[name="mainEmail"]');
		var emailAlertText = '请填写有效邮箱地址';
		var emailOrigin = email.val();
		if (!validMail(email.val())) {
			email.addClass('error').val(emailAlertText)
				.focus(function(){
					if (trim($(this).val()) == emailAlertText) {
						$(this).val(emailOrigin);
					}
				})
				.keydown(function(){
					$(this).removeClass('error');
				})
				.blur(function(){
					if (validMail($(this).val())) {
						$(this).removeClass('error');
					}
					else {
						$(this).addClass('error').val(emailAlertText);
					}
				});
			alertFade(email);
			validCheck = false;
		}
		
		if (validCheck) {
			ui_spotLight('save-result', 'processing', '正在保存……');
			var requestURL = "<vwb:Link context='contacts' format='url'/>?func=saveItem";
			ajaxRequest(requestURL,$('#person-item-form').serialize(), showSaveResult);
		}
	});
	$('#person-item-form a.cancel').live('click', function(){
		$('#editDetail').fadeOut().html('');
		$('#detail').fadeIn();
	});
	$("a#addContact").click(function(){
		ui_showDialog('add-contact-dialog');
	});
	/* $('#submit-contact-button').click(function(){
		var requestURL = "<vwb:Link context='contacts' format='url'/>?func=addItem";
		ajaxRequest(requestURL,$('#add-contact-form').serialize(), showAddResult);
	}); */
	
	/* search */
	var searchContact = new SearchBox('searchContact', '搜索联系人', false, true, true, true);
	searchContact.register('table.dataTable tbody tr', 'td');
	searchContact.afterSearch = function() {
		$('.dataTable tbody tr:visible:first').click();
	};
	searchContact.afterResetSearch = function() {
		detailSlideOut();
	};
	
	/*add to person contacts*/
	$('a.addToPersonal').live('click',function(){
		var requestURL = "<vwb:Link context='contacts' format='url'/>?func=addToPersonContacts";
		ajaxRequest(requestURL,"itemId="+this.id, processAdd2Result);
	});
	
	
	/* contactDetail Panel */
	var left = $('#contactSelector').offset().left+$('#contactSelector').width()-$('#contactDetail').width();
	//var right = $(window).width() - ($('#contactSelector').offset().left+$('#contactSelector').width()) -2;
	var right = -2;
	//var top = $('.content-menu-body').offset().top-1;
	var top = $("table#contact tbody").offset().top - $("#content").offset().top -1;
		
	function setContactDetail() {
		$('#contactDetail').css('right', right).css('top', top)
			.css('height', $('.content-menu-body').height()+1);
		$('#contactDetail-shade').css('width', $(window).width()).css('height', $(window).height());
	}
	
	function setContactDetailFixed() {
		var limit = 300;
		if ($(window).scrollTop()<limit) {
			$('#contactDetail-fixed').css('left', 0).css('top', 10).css('position', 'absolute');
		}
		else {
			$('#contactDetail-fixed').css('left', left+1).css('top', top+10-limit).css('position', 'fixed');
		}
	}
	
	function detailSlideIn() {
		if ($('#contactDetail-fixed').css('position')=='fixed') {
			//to allow content slide with its container, need to releave from fixed state
			//114 is conpensate from setContactDetailFixed(): limit -10(margin) +1(border)
			$('#contactDetail-fixed').css('position', 'absolute').css('left', 0).css('top', $(window).scrollTop()-114);
		}
		var contactWidth = $('#contactDetail').width();
		$('#contactDetail').width(0);
		$('#contactDetail').show().animate({ width: contactWidth }, 400);
		$('#contactDetail-shade').show();
	}
	function detailSlideOut() {
		if ($('#contactDetail-fixed').css('position')=='fixed') {
			//to allow content slide with its container, need to releave from fixed state
			//114 is conpensate from setContactDetailFixed(): limit -10(margin) +1(border)
			$('#contactDetail-fixed').css('position', 'absolute').css('left', 0).css('top', $(window).scrollTop()-114);
		}
		var contactWidth = $('#contactDetail').width();
		$('#contactDetail').animate({ width:0 }, 400, function(){ $(this).hide().width(contactWidth); });
		$('#contactDetail-shade').hide();
	}
	$('.dataTable tbody tr').click(function(){
		$('.dataTable tr.chosen').removeClass('chosen');
		$(this).addClass('chosen');
		var requestURL = "<vwb:Link context='contacts' format='url'/>?func=getItemData";
		ajaxRequest(requestURL,"itemId="+this.id+"&itemType="+$(this).attr('name'),showData);
		
		if ($('#detail').css('display')=='none') {
			$('#editDetail').hide();
			$('#detail').show();
		}
		
		//slide out right
		if ($('#contactDetail').css('display')=='none') {
			detailSlideIn();
		}
		
	});
	$('#contactDetail #fold').click(detailSlideOut);
	$('#contactDetail-shade').click(detailSlideOut);
	
	setContactDetail();
	setContactDetailFixed();
	$(window).resize(setContactDetail);
	$(window).scroll(setContactDetailFixed);
	
	function dtStripe(TABLE) {
		var table = (typeof(TABLE)!='undefined' && TABLE!='') ? TABLE : '.dataTable';
		$(table + ' tbody tr:nth-child(even)').addClass('striped');
	}
	dtStripe();
	
	/* form validate */
	$('#add-contact-form').validate({
		submitHandler: function(form) {
			var requestURL = "<vwb:Link context='contacts' format='url'/>?func=addItem";
			ajaxRequest(requestURL,$('#add-contact-form').serialize(), showAddResult);
		},
		rules: {
			name: {required: true},
			mainEmail: {required: true,	email:true}
		},
		messages: {
			name: { required:'请填写联系人姓名'},
			mainEmail: { required:'请填写有效邮箱地址', email:'请填写有效邮箱地址' }
		},
		errorPlacement: function(error, element){
			error.appendTo(element.parent().find(".errorContainer"));
		}
	});
	
	var exportBasicURL="<vwb:Link context='contacts' format='url'/>?func=exportPersonalContacts";
	
	$('#exportContacts').live("click",function(){
		var p_ids=new Array();
		var t_ids=new Array();
		var i=0,j=0;
		$("#contact tbody tr").each(function(index,element){
			if($(element).attr("name")=="teamItem"){
				if(!inArray(t_ids,element.id)){
					t_ids[j]=element.id;
					j=j+1;
				}
			}
			else{
				p_ids[i]=element.id;
				i=i+1;
			}
		});
		
		var contactList={"pids":p_ids,"tids":t_ids};
		//alert(JSON.stringify(contactList));
/* 		var form = $("#export-contact-form");
		form.action = exportBasicURL+"&contactList="+JSON.stringify(contactList);
		form.method = "POST";
		form.submit(); */
		window.location.href = exportBasicURL+"&contactList="+JSON.stringify(contactList);
	});
	
	//添加拼音排序规则
	$.tablesorter.addParser({
		// set a unique id 
		id : 'data',
		is : function(s) {
			// return false so this parser is not auto detected 
			return false;
		},
		format : function(s, table, cell, cellIndex) {
			var $cell = $(cell);
			// I could have used $(cell).data(), then we get back an object which contains both 
			// data-lastname & data-date; but I wanted to make this demo a bit more straight-forward 
			// and easier to understand. 

			// first column (zero-based index) has lastname data attribute 
			if (cellIndex === 0) {
				// returns lastname data-attribute, or cell text (s) if it doesn't exist 
				return $cell.attr('pinyin') || s;
				// third column has date data attribute 
			}

			// return cell text, just in case 
			return s;
		},
		// set type, either numeric or text 
		type : 'text'
	});

	//添加表格排序功能
	$(".contactTable").tablesorter({
		sortList : [ [ 0, 0 ] ],
		headers : {
			0 : { sorter: 'data' }, 
			4 : {
				sorter : false
			}
		},
		widgets: ["zebra"],
		widgetOptions : { 
		      zebra : [ "normal-row", "alt-row" ] 
		    } 
	});
	
	
	
});

function inArray(array,element){
	if(array.length==0) return false;
	for(var i=0;i<array.length;i++){
		if(array[i]==element)
			return true;
	}
	return false;
}

function showData(data){
	//var container = '#view-table';
	var container = '#contactDetail-fixed #detail';
	if(!isArray(data)){
		$(container).html("");
		if(data.type == "person") {
			$("#person-template").tmpl(data).appendTo(container);
			var itemName = $('.dataTable tr.chosen').attr('name');
			$('#detail a.editContact').attr('name', itemName);
		}
		else {
			$("#view-team-template").tmpl(data).appendTo(container);
		}
		//ui_showDialog('view-item-dialog');
	}
	else {
		alert("个人通讯录和团队通讯录里都有此人，待处理");
	}
}
function editData(data){
	var container = '#contactDetail-fixed #editDetail';
	if(!isArray(data)){
		$(container).html("");
		$("#person-edit-template").tmpl(data).appendTo(container);
		if ($(container+' select[name="sex"]').attr('ori')=='F') {
			$(container+' select[name="sex"] option[value="F"]').attr('selected', 'selected');
		}
	} 
}
function refresh(data) {
	location.replace(location.href);
}
function showSaveResult(data) {
	if(data.result == "success"){
		ui_spotLight('save-result', 'success', '保存成功');
		ui_hideDialog('edit-item-dialog', 5000);
		refresh();
	}
}
function showAddResult(data) {
	if(data.result == "success"){
		ui_spotLight('submit-result', 'success', '提交成功');
		ui_hideDialog('add-contact-dialog', 5000);
		refresh();
	}
	else {
		alert(data.detail);
	}
}
function processAdd2Result(data) {
	if(data.result == "success"){
		/* ui_spotLight('submit-result', 'success', '提交成功');
		ui_hideDialog('add-contact-dialog', 5000);
		refresh(); */
		ui_spotLight('addToPersonal-spotLight', 'success', '添加成功');
		setTimeout(refresh, 900);
	}
	else {
		alert(data.detail);
	}
}
function isArray(arr)
{
   return typeof arr == "object" && arr.constructor == Array;
}
function trim(str) {
	return str.replace(/^\s*|\s*$/, "");
}

function validMail(EMAIL) {
	var email = trim(EMAIL);
	var reg = /[\w\.]+@{1}[\w]+[\.]{1}[\w\.]+/gi;
	var match = reg.exec(email);
	if (match==null)
		return false;
	else {
		return true;
	} 
}

function alertFade(obj, rgb, duration) {
	var fadeDuration = (duration==null) ? 2000 : duration;
	var fadeInterval = 15;
	var fadeStep = fadeDuration/fadeInterval;
	
	if (rgb == null) {
		rgb = [255, 200, 200];
	}
	step = [((255-rgb[0])/fadeStep>1) ? parseInt((255-rgb[0])/fadeStep) : 1,
			((255-rgb[1])/fadeStep>1) ? parseInt((255-rgb[1])/fadeStep) : 1,
			((255-rgb[2])/fadeStep>1) ? parseInt((255-rgb[2])/fadeStep) : 1];

	obj.css('background-color', 'rgb(' + rgb[0] + ',' + rgb[1] + ',' + rgb[2] + ')');
	var colorFade = setInterval(function(){
		rgb[0] = (rgb[0]>=255) ? 255 : (rgb[0]+step[0]);
		rgb[1] = (rgb[1]>=255) ? 255 : (rgb[1]+step[1]);
		rgb[2] = (rgb[2]>=255) ? 255 : (rgb[2]+step[2]);
		obj.css('background-color', 'rgb(' + rgb[0] + ',' + rgb[1] + ',' + rgb[2] + ')');
		if (rgb[0]+rgb[1]+rgb[2]>=255*3) {
			clearInterval(colorFade);
		}
	}, fadeInterval);
}


</script>

<div id="contactSelector" class="filterHolder">
	<ul class="filter">
	<c:choose>
		<c:when test="${listMode == 'byName'}"><li class="chosen"></c:when>
		<c:otherwise><li></c:otherwise>
	</c:choose>
			<a href="<vwb:Link context='dashboard' format='url'/>?func=contacts&type=name">按姓名</a>
		</li>
	<c:choose>
		<c:when test="${listMode == 'byTeam'}"><li class="chosen"></c:when>
		<c:otherwise><li></c:otherwise>
	</c:choose>
			<a href="<vwb:Link context='dashboard' format='url'/>?func=contacts&type=team">按团队</a>
		</li>
	</ul>
	<div class="ui-RTCorner" id="searchContact"></div>
	<a class="largeButton newUser ui-RTCorner" id="addContact">添加联系人</a>
	<a class="largeButton ui-RTCorner" id="exportContacts">导出通讯录</a>
</div>

<div class="content-menu-body" style="overflow-x:hidden">
	<div id="contactDetail-shade"></div>
	<div id="contactDetail">
		<div id="contactDetail-fixed">
			<div id="fold" title="隐藏详细信息"></div>
			<div id="detail"></div>
			<div id="editDetail"></div>
		</div>
	</div>

	<c:if test="${listMode == 'byName'}">
	<div id="contact-byName">
		<table class="dataTable merge tight contactTable" id="contact" style="margin-top:0;">
		<thead>
			<tr>
				<td class="dtName">姓名</td>
				<td class="dtMail">联系邮箱</td>
				<td class="dtNums">电话</td>
				<td class="dtNums">手机</td>
				<td></td>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${contacts}" var="item" varStatus="status">
			<c:if test="${item.tag != 7}">
			<c:choose>
				<c:when test="${item.tag < 4}">
					<tr id="${item.id}" name="${item.tag}">
				</c:when>
				<c:otherwise>
					<tr id="${item.id}" name="teamItem">
				</c:otherwise>
			</c:choose>
				<td pinyin="${item.pinyin}">
				<c:choose>
					<c:when test="${item.tag < 4}">
						<span class="contactTag${item.tag}" title="来自个人通讯录"></span>
					</c:when>
					<c:otherwise>
						<span class="contactTag${item.tag}" title="来自团队通讯录"></span>
					</c:otherwise>
				</c:choose>
					<span class="head${item.sex}"></span>
					<span>${item.name}</span><span class="hidden">${item.pinyin}</span>
				</td>
				<td>${item.mainEmail}</td>
				<td>${item.telephone}</td>
				<td>${item.mobile}</td>
				<td></td>
			</tr>
			</c:if>
		</c:forEach>
		</tbody>
		</table>
	</div>
	</c:if>
	
	<c:if test="${listMode == 'byTeam'}">	
		<div id="contact-byTeam">
			<h3 class="toolTab icon personal">个人通讯录</h3>
			<table class="dataTable tight contactTable" id="contact">
				<thead>
					<tr>
						<td class="dtName">姓名</td>
						<td class="dtMail">联系邮箱</td>
						<td class="dtNums">电话</td>
						<td class="dtNums">手机</td>
						<td></td>
					</tr>
				</thead>
			<tbody>
			<c:forEach items="${userContacts}" var="item" varStatus="status">
				<tr id="${item.id}" name="personItem">
					<td pinyin="${item.pinyin}"><span class="head${item.sex}"></span><span>${item.name}</span><span class="hidden">${item.pinyin}</span></td>
					<td>${item.mainEmail}</td>
					<td>${item.telephone}</td>
					<td>${item.mobile}</td>
					<td></td>
				</tr>
			</c:forEach>
			</tbody>
			</table>

			<c:forEach items="${teamContacts}" var="entry">
				<h3 class="toolTab icon team">${teamNames.get(entry.key)}</h3>
				<table class="dataTable tight contactTable" id="contact">
				<thead>
					<tr>
						<td class="dtName">姓名</td>
						<td class="dtMail">联系邮箱</td>
						<td class="dtNums">电话</td>
						<td class="dtNums">手机</td>
						<td></td>
					</tr>
				</thead>
				<tbody>
				<c:forEach items="${entry.value}" var="item" varStatus="status">
					<tr id="${item.id}" name="teamItem">
						<td pinyin="${item.pinyin}"><span class="head${item.sex}"></span><span>${item.name}</span><span class="hidden">${item.pinyin}</span></td>
						<td>${item.email}</td>
						<td>${item.telephone}</td>
						<td>${item.mobile}</td>
						<td></td>
					</tr>
				</c:forEach>
				</tbody>
				</table>
			</c:forEach>
		</div>
	</c:if>
</div>


<div class="ui-dialog-cover"></div>
<div class="ui-dialog" id="add-contact-dialog" style="width:800px">
	<span class="ui-dialog-close ui-dialog-x"></span>
	<p class="ui-dialog-title">添加联系人</p>
	<form id="add-contact-form">
		<div class="ui-dialog-body">
			<h2 class="name">姓名：<input id="name" name="name" type="text" />
				<span class="head{{= sex}}"><select name="sex" id="sex"><option value="M">男</option><option value="F">女</option></select></span>
				<p class="errorContainer"></p>
			</h2>
			<table id="profileTable" class="edit ui-table-form">
				<tr class="invisible titleRow"><th width="75"></th><td width="190"></td><th width="55"></th><td width="140"></td></tr>
				<tr><th>联系邮箱：</th><td colspan="3"><input id="mainEmail" name="mainEmail" type="text" />
					<p class="errorContainer"></p>
				</td></tr>
				<tr><th>电话：</th><td><input id="telephone" name="telephone" type="text" /></td><th></th><td></td></tr>
				<tr><th>手机：</th><td><input id="mobile" name="mobile" type="text" /></td><th></th><td></td></tr>
				<tr class="titleRow"><th colspan="4"></th></tr>
				<tr>
					<th>QQ：</th><td><input id="qq" name="qq" type="text" /></td></tr>
				<tr>
					<th>微博：</th><td colspan="4"><input id="weibo" name="weibo" type="text" /></td></tr>
				<tr class="titleRow">
					<th colspan="4"></th>
				</tr>
				<tr><th>单位：</th><td colspan="3"><input id="orgnization" name="orgnization" type="text" /></td></tr>
				<tr><th>部门：</th><td colspan="3"><input id="department" name="department" type="text" /></td>
				</tr>
				<tr><th>地址：</th><td colspan="3"><input id="address" name="address" type="text" /></td></tr>
			</table>
			<div class="ui-dialog-control">
				<input type="submit" id="submit-contact-button" value="保存"/>
				<a class="ui-dialog-close ui-text-small">取消</a>
			</div>
			<span class="ui-spotLight" id="submit-result"></span>
		</div>
	</form>
</div>
<div class="ui-dialog" id="delete-contact-dialog" style="width:450px;">
	<p class="ui-dialog-title">删除联系人</p>
	<div class="ui-dialog-body">
		<p>确实要从个人通讯录删除该联系人吗？</p>
		<ul class="ui-text-note">
			<li>您输入的关于该联系人的信息将被删除，不可恢复</li>
			<li>如果该联系人也在您参加的团队中，您仍能看到该联系人的信息</li>
		</ul>
	</div>
	<div class="ui-dialog-control">
		<input type="button" id="delete-contact-confirm" value="删除联系人" />
		<a class="ui-dialog-close ui-text-small">取消</a>
	</div>
</div>

<script id="person-edit-template" type="text/html">
	<form id="person-item-form">
			<a class="cancel largeButton dim small ui-RTCorner">取消</a> 
			<h2 class="name"><input class="contact-info-input" id="name" name="name" type="text" value="{{= name}}"/>
				<select name="sex" id="sex" ori="{{= sex}}"><option value="M">男</option><option value="F">女</option></select></h2>
			<p class="ui-text-note mail">{{= uid}}</p>
			<table id="profileTable" class="edit ui-table-form">
				<tr class="invisible titleRow"><th width="75"></th><td width="190"></td><th width="55"></th><td width="140"></td></tr>
				<tr><th>联系邮箱：</th><td colspan="3"><input class="contact-info-input" id="mainEmail" name="mainEmail" type="text" value="{{= mainEmail}}"/></td></tr>
				<tr><th>电话：</th><td><input class="contact-info-input" id="telephone" name="telephone" type="text" value="{{= telephone}}"/></td><th></th><td></td></tr>
				<tr><th>手机：</th><td><input class="contact-info-input" id="mobile" name="mobile" type="text" value="{{= mobile}}"/></td><th></th><td></td></tr>
				<tr class="titleRow"><th colspan="4"></th></tr>
				<tr>
					<th>QQ：</th><td><input class="contact-info-input" id="qq" name="qq" type="text" value="{{= qq}}"/></td></tr>
				<tr>
					<th>微博：</th><td colspan="4"><input class="contact-info-input" id="weibo" name="weibo" type="text" value="{{= weibo}}"/></td></tr>
				<tr class="titleRow">
					<th colspan="4"></th>
				</tr>
				<tr><th>单位：</th><td colspan="3"><input class="contact-info-input" id="orgnization" name="orgnization" type="text" value="{{= orgnization}}"/></td></tr>
				<tr><th>部门：</th><td colspan="3"><input class="contact-info-input" id="department" name="department" type="text" value="{{= department}}"/></td>
				</tr>
				<tr><th>地址：</th><td colspan="3"><input class="contact-info-input" id="address" name="address" type="text" value="{{= address}}"/></td></tr>
				<tr><th></th><td colspan="3">
					<input class="largeButton" type="button" id="save-contact-button" value="保存"/>
					<input id="id" name="id" type="hidden" value="{{= id}}"/>
					<a class="cancel largeButton dim">取消</a>
					<span class="ui-spotLight" id="save-result"></span>
				</tr>
			</table>
	</form>
</script>
<script id="person-template" type="text/html">
			<a class="deleteContact ui-RTCorner largeButton alert small" id="{{= id}}" title="从个人通讯录中删除">删除</a>
			<a class="editContact ui-RTCorner largeButton small" id="{{= id}}">修改</a>                  
			<h2 class="name">{{= name}}<span class="head{{= sex}}"></span><span class="personalContact" title="个人通讯录"></span></h2>
			<p class="ui-text-note mail">{{= uid}}</p>
			<table id="profileTable" class="ui-table-form">
				<tr class="invisible titleRow"><th width="75"></th><td width="190"></td><th width="55"></th><td width="140"></td></tr>
				<tr><th>联系邮箱：</th><td colspan="3">{{= mainEmail}}</td></tr>
				<tr><th>电话：</th><td>{{= telephone}}</td><th></th><td></td></tr>
				<tr><th>手机：</th><td>{{= mobile}}</td><th></th><td></td></tr>
				<tr class="titleRow"><th colspan="4"></th></tr>
				<tr>
					<th>QQ：</th><td>{{= qq}}</td></tr>
				<tr>
					<th>微博：</th><td colspan="4">{{= weibo}}</td></tr>
				<tr class="titleRow">
					<th colspan="4"></th>
				</tr>
				<tr><th>单位：</th><td colspan="3">{{= orgnization}}</td></tr>
				<tr><th>部门：</th><td colspan="3">{{= department}}</td>
				</tr>
				<tr><th>地址：</th><td colspan="3">{{= address}}</td></tr>
			</table>
</script>

<script id="view-team-template" type="text/html">
			<a class="addToPersonal ui-RTCorner largeButton small" id="{{= id}}">添加到个人通讯录</a>
			<span class="ui-spotLight ui-RTCorner" id="addToPersonal-spotLight"></span>
			<h2 class="name">{{= name}}<span class="head{{= sex}}"></span></h2>
			<p class="ui-text-note mail">{{= uid}}</p>
			<table id="profileTable" class="ui-table-form">
				<tr class="invisible titleRow"><th width="75"></th><td width="190"></td><th width="55"></th><td width="140"></td></tr>
				<tr><th>联系邮箱：</th><td colspan="3">{{= email}}</td></tr>
				<tr><th>电话：</th><td>{{= telephone}}</td><th></th><td></td></tr>
				<tr><th>手机：</th><td>{{= mobile}}</td><th></th><td></td></tr>
				<tr class="titleRow"><th colspan="4"></th></tr>
				<tr>
					<th>QQ：</th><td>{{= qq}}</td></tr>
				<tr>
					<th>微博：</th><td colspan="4">{{= weibo}}</td></tr>
				<tr class="titleRow">
					<th colspan="4"></th>
				</tr>
				<tr><th>单位：</th><td colspan="3">{{= orgnization}}</td></tr>
				<tr><th>部门：</th><td colspan="3">{{= department}}</td>
				</tr>
				<tr><th>地址：</th><td colspan="3">{{= address}}</td></tr>
			</table>
</script>

<div class="clear"></div>
