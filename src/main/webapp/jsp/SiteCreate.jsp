<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<fmt:setBundle basename="templates.default" />

<%
	pageContext.setAttribute("basepath", request.getContextPath());
%>

<link type="text/css"
	href="${basepath}/scripts/jquery/cupertino/jquery-ui-1.7.2.custom.css"
	rel="stylesheet" />

<style>
#mailservicedetail {
	margin: 3px;
	display: none;
	float: left;
}
.label {
  padding-top: 2px;
  padding-right: 8px;
  vertical-align: top;
  text-align: right;
  width: 100px;
  white-space: nowrap;
}
.field {
  padding-bottom: 10px;
  white-space: nowrap;
  align: left; 
  width: 1000px
}
.status {
  padding-top: 2px;
  padding-left: 8px;
  vertical-align: top;
  width: 200px;
  white-space: nowrap;
}

#domainExist {
  padding-left: 16px;
  padding-bottom: 2px;
  font-weight: bold;
  color: #EA5200;
}
#siteform label.error {
  background:url("${basepath}/images/unchecked.gif") no-repeat 0px 0px;
  padding-left: 16px;
  padding-bottom: 2px;
  font-weight: bold;
  color: #EA5200;
}

#siteform label.checked {
  background:url("${basepath}/images/checked.gif") no-repeat 0px 0px;
}
</style>

<script type="text/javascript"
	src="${basepath}/scripts/jquery/jquery-ui-1.7.2.custom.min.js"></script>
<script type="text/javascript" 
	src="${basepath}/scripts/jquery/jquery.validate.js"></script>

<script type="text/javascript">

$.validator.setDefaults({
	submitHandler: function() {
	 	createSubmit(); 
	}
});

function createSubmit(){
	$('#dialog').dialog('open');
	
	var form = document.getElementById("siteform");
	form.submit();
}

function domainNotify(content){
	$("#domainExist").html(content);
}

function changeSmtpEnable(disable){
	var inputs=["#smtp", "#smtpuser", "#smtppassword", "#smtppassword2", "#replyaddress", "#sendaddress"];
	$.each(inputs, function(i, val){
		$(val).attr("disabled", disable);
	});
}

$(document).ready(function() {
	$('#dialog').dialog({
		autoOpen: false,
		modal:true,
		width: 300
	});
	
	$('#mailservice').change(function(){
		var mailservice = document.getElementById("mailservice");
		var index = mailservice.selectedIndex;
		if(index==0){
			$('#mailservicedetail').slideUp("slow");
			changeSmtpEnable(true);
		}
		if(index==1){
			$('#mailservicedetail').slideDown("slow");
			changeSmtpEnable(false);
		}
	});
	
	$('#certificate').change(function(){
		var inputs=["#sendaddress", "#smtpuser", "#smtppassword", "#smtppassword2"];
		var checked=$("#certificate").attr("checked");
		$.each(inputs, function(i, val){
			$(val).attr("disabled", !checked);
		});
	});
	
	
	jQuery.validator.addMethod("stringCheck", 
		function(value, element) {
    		var pattern = /^([.a-zA-Z0-9]|[-_]|[^\x00-\xff]){1,255}$/;
			return this.optional(element) || (pattern.test(value));
 		}, 
 		"<fmt:message key="site.create.sitename.stringcheck" />"
 	);
 	
 	jQuery.validator.addMethod("domainCheck", 
		function(value, element) {
    		var pattern = /^([.a-zA-Z0-9]|[-_]|[^\x00-\xff]){1,255}$/;
			return this.optional(element) || (pattern.test(value));
 		}, 
 		"<fmt:message key="site.create.domain.domaincheck" />"
 	);
 
/*	$("#siteform").validate({
		rules: {
			sitename:{
				required:true,
				stringCheck:true
			},
			domain:{
     			required:true,
     			domainCheck:true,
     			remote:{
      				url: "<vwb:Link jsp='createSite' format='url'/>?func=isDomainValid",
      				type: 'POST',
      				data:{
             			domain: function(){
             				return $('#domain').val();
          				}
       				}
                }
    		},
			smtp:"required",
			replyaddress:"required",
			sendaddress:"required",
			smtpuser: {
				required: true,
				minlength: 2
			},
			smtppassword: {
				required: true,
				minlength: 5
			},
			smtppassword2: {
				required: true,
				minlength: 5,
				equalTo: "#smtppassword"
			}
		},
		messages: {
			sitename: {
				required:"<fmt:message key="site.create.sitename.required" />",
				stringCheck: "<fmt:message key="site.create.sitename.stringcheck" />"  
			},
			domain: {
     			required:"<fmt:message key="site.create.domain.required" />",
     			remote:"<fmt:message key="site.create.domain.notify.invalid" />",
     			domainCheck:"<fmt:message key="site.create.domain.domaincheck" />"
    		},
			smtp: "<fmt:message key="site.create.smtp.required" />",
			replyaddress: "<fmt:message key="site.create.replyaddress.required" />",
			sendaddress: "<fmt:message key="site.create.sendaddress.required" />",
			smtpuser: {
				required: "<fmt:message key="site.create.smtpuser.required" />",
				minlength: "<fmt:message key="site.create.smtpuser.minlength" />"
			},
			smtppassword: {
				required: "<fmt:message key="site.create.smtppassword.required" />",
				minlength: "<fmt:message key="site.create.smtppassword.minlength" />"
			},
			smtppassword2: {
				required: "<fmt:message key="site.create.smtppassword2.required" />",
				minlength: "<fmt:message key="site.create.smtppassword2.minlength" />",
				equalTo: "<fmt:message key="site.create.smtppassword2.equalto" />"
			}
		},
		success: function(label) {
			label.html("&nbsp;").addClass("checked");
			if(label.attr("for") == 'domain'){
				label.html("<fmt:message key="site.create.domain.notify.valid" />");
			}
		}
	});
	*/
	
});
</script>

<form id="siteform" method="post" action="<vwb:Link format='url' jsp='createSite'/>" autocomplete="off">
	<input type="hidden" name="func" value="create">
	<table class="sitemodify" border="1" cellspacing="0" cellpadding="0"
		bordercolor="#c7c7c7">
		<tr class="tr_1">
			<td>
				<fmt:message key="site.create.title" />
			</td>
		</tr>
		<tr class="tr_2">
			<td>
				<table width="100%">
					<tr>
						<td class="label">
							<label id="lsitename" for="sitename">
								<fmt:message key="site.create.field.sitename" />
							</label>
						</td>
						<td class="field" colspan="2">
							<input id="sitename" name="sitename" type="text" value=""
								maxlength="100" />
						</td>
						<td class="status"></td>
					</tr>
					<tr>
						<td class="label">
							<label id="lsitedisplayname" for="sitedisplayname">
								<fmt:message key="site.create.field.siteDisplayName" />
							</label>
						</td>
						<td class="field" colspan="2">
							<input id="siteDisplayName" name="siteDisplayName" type="text" value=""
								maxlength="100" />
						</td>
						<td class="status"></td>
					</tr>
					<tr>
						<td class="label">
							<label id="ldomain" for="domain">
								<fmt:message key="site.create.field.domain" />
							</label>
						</td>
						<td class="field" colspan="2">
							<input id="domain" name="domain" type="text" value=""
								maxlength="100"/>
							<label id="domainExist"></label>
						</td>
						<td class="status"></td>
					</tr>
					<tr>
						<td class="label">
							<label id="ltemplate" for="template">
								<fmt:message key="site.create.field.template" />
							</label>
						</td>
						<td class="field" colspan="2">
							<select id="template" name="template">
							  
							  <c:forEach var="item" items='${requestScope.templates}' varStatus="status">
							       <option value="${item}">
								       <c:out value="${item}"/>
								   </option>
							  </c:forEach>
							</select>
						</td>
						<td class="status"></td>
					</tr>
					<tr>
						<td class="label">
							<label id="lpolicy" for="policy">
								<fmt:message key="site.create.field.policy" />
							</label>
						</td>
						<td class="field" colspan="2">
							<select id="policy" name="policy">
								<option value="teamwork">
									<fmt:message key="site.create.field.policy.option1" />
								</option>
								<option value="publish">
									<fmt:message key="site.create.field.policy.option2" />
								</option>
							</select>
						</td>
						<td class="status"></td>
					</tr>
					<tr>
						<td class="label">
							<label id="lmailservice" for="mailservice">
								<fmt:message key="site.create.field.mailservice" />
							</label>
						</td>
						<td class="field" colspan="2">
							<select id="mailservice" name="mailservice">
								<option value="1">
									<fmt:message key="site.create.field.mailservice.option1" />
								</option>
								<option value="2">
									<fmt:message key="site.create.field.mailservice.option2" />
								</option>
							</select>
						</td>
						<td class="status"></td>
					</tr>
					<tr>
						<td class="field" colspan="3">
							<div id="mailservicedetail">
								<fieldset
									style="width: 95%; margin-left: auto; margin-right: auto;">
									<legend>
										<fmt:message key="site.create.field.mailservice.legend" />
									</legend>
									<table>
										<tr>
											<td class="label" nowrap="nowrap">
												<label id="lsmtp" for="smtp">
													<fmt:message key="site.create.field.mailservice.smtp" />
												</label>
											</td>
											<td class="field">
												<input id="smtp" name="smtp" type="text" value=""
													maxlength="45" disabled/>
											</td>
										</tr>
										<tr>
											<td class="label">
												<label id="lreplyaddress" for="replyaddress">
													<fmt:message key="site.create.field.mailservice.replyaddress" />
												</label>
											</td>
											<td class="field">
												<input id="replyaddress" name="replyaddress" type="text" value=""
													maxlength="45" disabled/>
											</td>
										</tr>
										<tr>
											<td class="label">
												<label id="lcertificate" for="certificate">
													&nbsp;
												</label>
											</td>
											<td class="field">
												<input id="certificate" name="certificate" type="checkbox"
													checked />
												<fmt:message key="site.create.field.mailservice.certificate" />
											</td>
										</tr>
										<tr>
											<td class="label">
												<label id="lsendaddress" for="sendaddress">
													<fmt:message key="site.create.field.mailservice.sendaddress" />
												</label>
											</td>
											<td class="field">
												<input id="sendaddress" name="sendaddress" type="text" value=""
													maxlength="45" disabled/>
											</td>
										</tr>
										<tr>
											<td class="label">
												<label id="lsmtpuser" for="smtpuser">
													<fmt:message key="site.create.field.mailservice.smtpuser" />
												</label>
											</td>
											<td class="field">
												<input id="smtpuser" name="smtpuser" type="text" value=""
													maxlength="45" disabled/>
											</td>
										</tr>
										<tr>
											<td class="label">
												<label id="lsmtppassword" for="smtppassword">
													<fmt:message key="site.create.field.mailservice.smtppassword" />
												</label>
											</td>
											<td class="field">
												<input id="smtppassword" name="smtppassword" type="password" value=""
													maxlength="45" disabled/>
											</td>
										</tr>
										<tr>
											<td class="label">
												<label id="lsmtppassword2" for="smtppassword2">
													<fmt:message key="site.create.field.mailservice.smtppassword2" />
												</label>
											</td>
											<td class="field">
												<input id="smtppassword2" name="smtppassword2" type="password" value=""
													maxlength="45" disabled/>
											</td>
										</tr>
									</table>
								</fieldset>
							</div>
						</td>
						<td class="status"></td>
					</tr>
					<tr>
						<td class="label">
							<label id="lcreatesubmit" for="createsubmit">
							
							</label>
						</td>
						<td class="field" colspan="2">
							<input type="submit" id="createsubmit" name="createsubmit"
								value="<fmt:message key="site.create.field.createbtn" />"/>
						</td>
						<td class="status"></td>
					</tr>


				</table>
			</td>
		</tr>
	</table>
</form>

<div id="dialog" title="<fmt:message key="site.create.dialog.create.title" />">
	<p>
		<fmt:message key="site.create.dialog.create.content" />
	</p>
</div>

