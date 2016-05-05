<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="/WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:setBundle basename="templates.default"/>
<html>
  <head>
  <script type="text/javascript" src="<%=request.getContextPath()%>/scripts/DUI/tab/dtab.js"></script>
  <script language="javascript">
	  window.onload = function(){
	 	AddTab("title","SysConfig",'site.properties.sysconfig'.localize()) //(标签容器id,显示容器id,i18nkey)
		AddTab("title","ClbConfig",'site.properties.clbconfig'.localize())
		AddTab("title","EmailConfig",'site.properties.emailconfig'.localize())
		AddTab("title","DomainConfig",'site.properties.domainconfig'.localize())
		ShowDivByTab("SysConfig");
		OnLoadSelected('<c:out value='${allmap["duckling.dateformat"]}'/>',"duckling.dateformat");
		OnLoadSelected('<c:out value='${allmap["email.mail.smtp.auth"]}'/>',"email.mail.smtp.auth");
		OnLoadSelected('<c:out value='${allmap["default.language"]}'/>',"default.language");
		
		var defaultemailype='<c:out value='${isHasDBEmail}'/>'
		if(defaultemailype=='true'){
			showEmailContent('false');
		}
		
		checkEmail('<c:out value='${allmap["email.mail.smtp.auth"]}'/>')
	  }
	  
	  function OnLoadSelectedByRadio(str,name){
	  	var arr=document.getElementsByName(name);
	  	for(i=0;i<arr.length;i++){
	  		if(arr[i].value==str){
	  			arr[i].checked=true;
	  		}
	  	}
	  }
	  function OnLoadSelected(str,name){
	  	var arr=document.getElementsByName(name)[0].options;
	  	for(i=0;i<arr.length;i++){
	  		if(arr[i].value==str){
	  			arr[i].selected=true;
	  		}
	  	}
	  }
	 // var domainId=${domainListSize};
	  function addDomainConfig(){
	  	otable=document.getElementById("addinputtable");
	  	var arrtr=otable.rows
	  	
	  	tempid=0;
	  	for(i=0;i<arrtr.length;i++){
	  		var lasttrid=arrtr[i].id
	  		var arrlasttrid=lasttrid.split(".")
	  		var lastnum=arrlasttrid[arrlasttrid.length-1]
	  		if(!isNaN(lastnum)){
	  			if(parseInt(lastnum)>tempid){
	  				tempid=parseInt(lastnum);
	  			}
	  		
	  		}
	  	}
	  	domainId=tempid
	  	if(isNaN(domainId))domainId=0
	  	domainId=parseInt(domainId)+1
	  	
	  	
	  	spTR=document.createElement("TR");
	  	spTD1=document.createElement("TD");
	  	spTD1.innerHTML="&nbsp;"
	  	spTD2=document.createElement("TD");
	  	spTD2.innerHTML="&nbsp;"
	  	spTD3=document.createElement("TD");
	  	spTD3.innerHTML="&nbsp;"
	  	spTR.appendChild(spTD1)
	  	spTR.appendChild(spTD2)
  		spTR.appendChild(spTD3)
	  	otable.appendChild(spTR)
	  	
	  	
	  	
	  	oTR=document.createElement("TR");
	  	var oTD1= document.createElement("TD");
	  	oTD1.innerHTML='<fmt:message key="duckling.domain" />';
	  	var oTD2= document.createElement("TD");
		var Txt="<input type='text' name='duckling.domain."+domainId+"'/>";
		oTD2.innerHTML=Txt;
		var oTD3= document.createElement("TD");
		var Txt3="<input type=\"button\" id=\"deleteduckling.domain."+domainId+"\"   style=\"BACKGROUND: url(../images/delete.gif) no-repeat; WIDTH: 22px; HEIGHT: 22px\" onclick=\"javascript:deleteTr(\'duckling.domain."+domainId+"\')\"/>"
		oTD3.innerHTML=Txt3;
	  	oTR.appendChild(oTD1)
	  	oTR.appendChild(oTD2)
	  	oTR.appendChild(oTD3)
	  	otable.appendChild(oTR)
	  	
	  	spTR.id="STTRduckling.domain."+domainId
	  	oTR.id="OTRduckling.domain."+domainId
	  }
	  
	  function showEmailContent(str){
	  	  document.getElementById("Emailcontent").style.display = (str=="false")?'block':'none';
	  	  OnLoadSelected(str,"defaultemailype")
	  }
	  
	  function deleteTr(str){
	  	var table = document.getElementById("addinputtable");
		table.removeChild(document.getElementById("OTR"+str))
		table.removeChild(document.getElementById("STTR"+str))
	  }
	  
	  function checkEmail(bvalue){
	 	// bvalue=obj.value
	  	if(bvalue!='true'){
	  		document.getElementsByName("email.address")[0].disabled='disabled';
	  		document.getElementsByName("email.username")[0].disabled='disabled';
	  		document.getElementsByName("email.password")[0].disabled='disabled';
	  	}else{
	  		document.getElementsByName("email.address")[0].removeAttribute('disabled'); 
	  		document.getElementsByName("email.username")[0].removeAttribute('disabled'); 
	  		document.getElementsByName("email.password")[0].removeAttribute('disabled'); 
	  	}
	  }
	  
	  function changeIt(obj){
	  	var name=obj.id
	  	name=name.substr(0,name.length-5)
	  	document.getElementsByName(name)[0].value=obj.checked?'true':'false';
	  }
	  function OnLoadChangeIt(value,id){
	  	if(value=='true'){
	  		document.getElementById(id+"check").checked=true;
	  	}
	  		
	  }
  </script>
<style type="text/css">
.ContentStyle{
	border-bottom: 1px solid #919B9C;
	border-left: 1px solid #919B9C;
	border-right: 1px solid #919B9C;
}

.deleteinput{
background-image:url(../images/add.png); 
width: 220px; 
height: 220px;"
}

 .deleteinput2{ 
  	height:20px;
  	border: #B9CFE6 1px solid;
	background-image:url(../images/add.png);
	background-repeat:repeat; 
	width: 60px;
	cursor:pointer;
  }
</style>
  </head>
  <body> 
  <div id="title" >
  </div>
  <!-- SiteConfig -->
	<div  id="SysConfig" align="center" width="100%" style="DISPLAY: none" class="ContentStyle">
	  <form action="<vwb:Link jsp='VWBProperties' context='team' format='url'/>" method="post" style="margin-top: -1px">
	  <input name="func" type="hidden" value="update">
	  <input name="listentype" type="hidden" value="SysConfig">
		<table align="center" cellspacing="0" cellpadding="0" width="80%" border="0"><tr><td>&nbsp;</td></tr><tr><td>
			<table  class="DCT_oldtable" cellspacing="0" cellpadding="0" width="98%" border="0" >
				<tr>
		  			<td>&nbsp;</td><td>&nbsp;</td>
		  		</tr>
				<tr>
		  			<td>&nbsp;</td><td>&nbsp;</td>
		  		</tr>
		  		
				<tr>
					<td nowrap="nowrap">
						<fmt:message key="duckling.dateformat" />
					</td>
					<td nowrap="nowrap" align="left">
					<select name="duckling.dateformat">
						<option value="yyyy-MM-dd HH:mm">yyyy-MM-dd HH:mm</option>
						<option value="MM-dd-yyyy HH:mm">MM-dd-yyyy HH:mm</option>
						<option value="dd-MM-yyyy HH:mm">dd-MM-yyyy HH:mm</option>
					</select>
					</td>
				</tr>
				<tr>
		  			<td>&nbsp;</td><td>&nbsp;</td>
		  		</tr>
		  		
		  		<tr>
					<td nowrap="nowrap">
					<fmt:message key="duckling.defaultlanguage" />
						
					</td>
					<td nowrap="nowrap" align="left">
					<select name="default.language">
						<option value="default">default</option>
						<option value="en_US">en_US</option>
						<option value="zh_CN">zh_CN</option>
					</select>
					</td>
				</tr>
				<tr>
		  			<td>&nbsp;</td><td>&nbsp;</td>
		  		</tr>
		  		
		  		
		  		
				<tr>
					<td nowrap="nowrap">
						<fmt:message key="duckling.site.name" />
					</td>
					<td nowrap="nowrap" align="left">
						<input type="text" name="duckling.site.name" value= "<c:out value='${allmap["duckling.site.name"]}'/>">
					</td>
				</tr>
				<tr>
		  			<td>&nbsp;</td><td>&nbsp;</td>
		  		</tr>
		  		
				<tr>
					<td  nowrap="nowrap">
						<fmt:message key="duckling.umt.vo" />
					</td>
					<td nowrap="nowrap" align="left">
						<input type="text" name="duckling.umt.vo" value= "<c:out value='${allmap["duckling.umt.vo"]}'/>">
					</td>
				</tr>
				
				<tr>
		  			<td>&nbsp;</td><td>&nbsp;</td>
		  		</tr>
		  		
		  		<tr>
					<td width="50%" nowrap="nowrap">
						<fmt:message key="duckling.sharepage.expireperiod" />
					</td>
					<td nowrap="nowrap" width="50%" align="left">
						<input type="text" name="duckling.sharepage.expireperiod" value= "<c:out value='${allmap["duckling.sharepage.expireperiod"]}'/>" onchange="this.value=this.value.replace(/\D/g,'')"><fmt:message key="duckling.hour" />
					</td>
				</tr>
				<tr>
		  			<td>&nbsp;</td><td>&nbsp;</td>
		  		</tr>
		  		<tr>
					<td width="50%" nowrap="nowrap">
						<fmt:message key="continuationEditMinutes" />
					</td>
					<td nowrap="nowrap" width="50%" align="left">
						<input type="text" name="continuationEditMinutes" value= "<c:out value='${allmap["continuationEditMinutes"]}'/>" onchange="this.value=this.value.replace(/\D/g,'')"><fmt:message key="duckling.minute"  />
					</td>
				</tr>
				<tr>
		  			<td>&nbsp;</td><td>&nbsp;</td>
		  		</tr>
		  		
		  		
		  		<tr>
					<td width="50%" nowrap="nowrap">
						<fmt:message key="googlemapkey" />
					</td>
					<td nowrap="nowrap" width="50%" align="left">
						<input type="text" name="googlemapkey" value= "<c:out value='${allmap["googlemapkey"]}'/>">
					</td>
				</tr>
				<tr>
		  			<td>&nbsp;</td><td>&nbsp;</td>
		  		</tr>
		  		
		  		<tr>
					<td width="50%" nowrap="nowrap">
					<fmt:message key="duckling.defaultpage" />
						
					</td>
					<td nowrap="nowrap" width="50%" align="left">
						<input type="text" name="duckling.defaultpage" value= "<c:out value='${allmap["duckling.defaultpage"]}'/>">
					</td>
				</tr>
				<tr>
		  			<td>&nbsp;</td><td>&nbsp;</td>
		  		</tr>
		  		
		  		
		  		
		  		<tr>
					<td nowrap="nowrap">
						<fmt:message key="duckling.keyword" />
					</td>
					<td nowrap="nowrap" align="left">
					<input type="text" name="duckling.keyword" value= "<c:out value='${allmap["duckling.keyword"]}'/>">
					</td>
				</tr>
				<tr>
		  			<td>&nbsp;</td><td>&nbsp;</td>
		  		</tr>
		  		<tr>
					<td nowrap="nowrap">
						<fmt:message key="duckling.descriptions" />
					</td>
					<td nowrap="nowrap" align="left">
					<input type="text" name="duckling.descriptions" value= "<c:out value='${allmap["duckling.descriptions"]}'/>">
					</td>
				</tr>
				<tr>
		  			<td>&nbsp;</td><td>&nbsp;</td>
		  		</tr>
		  		<tr>
					<td nowrap="nowrap">
						<fmt:message key="duckling.robots" />
					</td>
					<td nowrap="nowrap" align="left">
						<input type="hidden" name="duckling.robots">
						<input type="checkbox" id="duckling.robotscheck" onchange="javascript:changeIt(this)" >
					</td>
				</tr>
				<tr>
		  			<td>&nbsp;</td><td>&nbsp;</td>
		  		</tr>
		  		
		  		<tr>
					<td width="50%" nowrap="nowrap">
						<fmt:message key="duckling.ddata" />
					</td>
					<td nowrap="nowrap" width="50%" align="left">
					<input type="hidden" name="duckling.ddata">
					<input type="checkbox" id="duckling.ddatacheck" onchange="javascript:changeIt(this)" >
						<!-- select name="duckling.ddata">
							<option value="true">true</option>
							<option value="false">false</option>
						</select> -->
					</td>
				</tr>
				<tr>
		  			<td>&nbsp;</td><td>&nbsp;</td>
		  		</tr>
		  		<tr>
					<td width="50%" nowrap="nowrap">
						<fmt:message key="duckling.userbox" />
					</td>
					<td nowrap="nowrap" width="50%" align="left">
					<input type="hidden" name="duckling.userbox">
					<input type="checkbox" id="duckling.userboxcheck" onchange="javascript:changeIt(this)" >
						<!--  select name="duckling.userbox">
							<option value="true">true</option>
							<option value="false">false</option>
						</select>-->
					</td>
				</tr>
				<tr>
		  			<td>&nbsp;</td><td>&nbsp;</td>
		  		</tr>
		  		<tr>
					<td width="50%" nowrap="nowrap">
						<fmt:message key="duckling.searchbox" />
					</td>
					<td nowrap="nowrap" width="50%" align="left">
					<input type="hidden" name="duckling.searchbox">
					<input type="checkbox" id="duckling.searchboxcheck" onchange="javascript:changeIt(this)" >
						<!-- select name="duckling.searchbox">
							<option value="true">true</option>
							<option value="false">false</option>
						</select> -->
					</td>
				</tr>
				<tr>
		  			<td>&nbsp;</td><td>&nbsp;</td>
		  		</tr>
		  		
		  		
		  		
		  		<tr>
					<td width="50%" nowrap="nowrap">
						是否允许匿名访问clb
					</td>
					<td nowrap="nowrap" width="50%" align="left">
					<input type="hidden" name="duckling.allowanonymous">
					<input type="checkbox" id="duckling.allowanonymouscheck" onchange="javascript:changeIt(this)" >
						<!-- select name="duckling.searchbox">
							<option value="true">true</option>
							<option value="false">false</option>
						</select> -->
					</td>
				</tr>
				<tr>
		  			<td>&nbsp;</td><td>&nbsp;</td>
		  		</tr>
		  		<!-- tr>
					<td width="50%" nowrap="nowrap">
						<fmt:message key="duckling.bannertitle" />
					</td>
					<td nowrap="nowrap" width="50%" align="left">
					<input type="hidden" name="duckling.bannertitle">
					<input type="checkbox" id="duckling.bannertitlecheck" onchange="javascript:changeIt(this)" >
					</td>
				</tr>
				<tr>
		  			<td>&nbsp;</td><td>&nbsp;</td>
		  		</tr> -->
			</table>
		</td></tr>
		<tr><td>&nbsp;</td></tr>
		<tr><td align="center"><input type="submit" class="DuclingButton" value='<fmt:message key="page.setting.submit"/>'>&nbsp;&nbsp;</td></tr>
		<tr><td>&nbsp;</td></tr></table>
	    </form> 
     </div>
    <!-- ClbConfig -->
	<div  id="ClbConfig" align="center" width="100%" style="DISPLAY: none" class="ContentStyle">
	  <form action="<vwb:Link jsp='VWBProperties' context='team' format='url'/>" method="post" style="margin-top: 0px">
	  	<input name="func" type="hidden" value="update">
	  	<input name="listentype" type="hidden" value="ClbConfig">
		<table align="center" cellspacing="0" cellpadding="0" width="60%" border="0"><tr><td>&nbsp;</td></tr><tr><td>
			<table  class="DCT_oldtable" cellspacing="0" cellpadding="0" width="98%" border="0" >
				<tr>
		  			<td>&nbsp;</td><td>&nbsp;</td>
		  		</tr>
				<tr>
					<td width="50%" nowrap="nowrap">
						<fmt:message key="duckling.clb.localuser" />
					</td>
					<td nowrap="nowrap" width="50%">
						<input type="text" name="duckling.clb.localuser" value= "<c:out value='${allmap["duckling.clb.localuser"]}'/>">
					</td>
				</tr>
				<tr>
		  			<td>&nbsp;</td><td>&nbsp;</td>
		  		</tr>
			</table>
		</td></tr>
		<tr><td>&nbsp;</td></tr>
		<tr><td align="center"><input type="submit" class="DuclingButton" value='<fmt:message key="page.setting.submit"/>'>&nbsp;&nbsp;</td></tr>
		<tr><td>&nbsp;</td></tr></table>
     </form>
     </div>
     
     <!-- EmailConfig -->
    
	<div id="EmailConfig" align="center" width="100%" style="DISPLAY: none"  class="ContentStyle" >
	<form action="<vwb:Link jsp='VWBProperties' context='team' format='url'/>" method="post" style="margin-top: 0px">
  		<input name="func" type="hidden" value="update"> 
  		<input name="listentype" type="hidden" value="EmailConfig">
  		
		
		<table align="center" cellspacing="0" cellpadding="0" width="60%" border="0"><tr><td>&nbsp;</td></tr>
		<tr><td><fmt:message key="email.info.host" />
		<select id="defaultemailype" name="defaultemailype" onchange="javascript:showEmailContent(this.value)">
			<option value="true"><fmt:message key="email.info.defaulthost" /></option>
			<option value="false"><fmt:message key="email.info.localhost" /></option>
		</select>
		</td></tr>
		<tr><td>&nbsp;</td></tr>
		<tr><td>
		<div id="Emailcontent"  style="DISPLAY: none;border: 1px solid  blue;" >
			<table  class="DCT_oldtable" cellspacing="0" cellpadding="0" width="98%" border="0" style="margin:20px">
				<tr>
		  			<td>&nbsp;</td><td>&nbsp;</td>
		  		</tr>
				<tr>
					<td width="50%" nowrap="nowrap">
						<fmt:message key="email.mail.smtp.host" />
					</td>
					<td nowrap="nowrap" width="50%" align="left">
						<input type="text" name="email.mail.smtp.host" value= "<c:out value='${allmap["email.mail.smtp.host"]}'/>">
					</td>
				</tr>
				<tr>
		  			<td>&nbsp;</td><td>&nbsp;</td>
		  		</tr>
				<tr>
					<td width="50%" nowrap="nowrap">
						<fmt:message key="email.mail.pop3.host" />
					</td>
					<td nowrap="nowrap" width="50%" align="left">
						<input type="text" name="email.mail.pop3.host" value= "<c:out value='${allmap["email.mail.pop3.host"]}'/>">
					</td>
				</tr>
				<tr>
		  			<td>&nbsp;</td><td>&nbsp;</td>
		  		</tr>
		  		<tr>
					<td width="50%" nowrap="nowrap">
						<fmt:message key="email.mail.smtp.auth" />
					</td>
					<td nowrap="nowrap" width="50%"  align="left">
						<select name="email.mail.smtp.auth" onchange="javascript:checkEmail(this.value)">
							<option value="true">true</option>
							<option value="false">false</option>
						</select>
						<!--  input type="text" name="email.mail.smtp.auth" value= "<c:out value='${allmap["email.mail.smtp.auth"]}'/>">-->
					</td>
				</tr>
				<tr>
		  			<td>&nbsp;</td><td>&nbsp;</td>
		  		</tr>
		  		<tr>
					<td width="50%" nowrap="nowrap">
						<fmt:message key="email.fromAddress" />
					</td>
					<td nowrap="nowrap" width="50%" align="left">
						<input type="text" name="email.fromAddress" value= "<c:out value='${allmap["email.fromAddress"]}'/>">
					</td>
				</tr>
				<tr>
		  			<td>&nbsp;</td><td>&nbsp;</td>
		  		</tr>
		  		<tr>
					<td width="50%" nowrap="nowrap">
						<fmt:message key="email.address" />
					</td>
					<td nowrap="nowrap" width="50%" align="left">
						<input type="text" name="email.address" value= "<c:out value='${allmap["email.address"]}'/>">
					</td>
				</tr>
				<tr>
		  			<td>&nbsp;</td><td>&nbsp;</td>
		  		</tr>
		  		<tr>
					<td width="50%" nowrap="nowrap">
						<fmt:message key="email.username" />
					</td>
					<td nowrap="nowrap" width="50%" align="left">
						<input type="text" name="email.username" value= "<c:out value='${allmap["email.username"]}'/>">
					</td>
				</tr>
				<tr>
		  			<td>&nbsp;</td><td>&nbsp;</td>
		  		</tr>
		  		<tr>
					<td width="50%" nowrap="nowrap">
						<fmt:message key="email.password" />
					</td>
					<td nowrap="nowrap" width="50%" align="left">
						<input type="password" name="email.password" value= "<c:out value='${allmap["email.password"]}'/>">
					</td>
				</tr>
				<tr>
		  			<td>&nbsp;</td><td>&nbsp;</td>
		  		</tr>
			</table>
			</div>
		</td></tr>
		<tr><td>&nbsp;</td></tr>
		<tr><td align="center"><input type="submit" class="DuclingButton" value='<fmt:message key="page.setting.submit"/>'>&nbsp;&nbsp;</td></tr>
		<tr><td>&nbsp;</td></tr></table>
		 </form>
		 
     </div>
    
     
     
     
    <!-- DomainConfig -->
	<div  id="DomainConfig" align="center" width="100%" style="DISPLAY: none" class="ContentStyle">
	 <form action="<vwb:Link jsp='VWBProperties' context='team' format='url'/>" method="post" style="margin-top: 0px">
  		<input name="func" type="hidden" value="update"> 
  		<input name="listentype" type="hidden" value="DomainConfig">
		<table align="center" cellspacing="0" cellpadding="0" width="60%" border="0"><tr><td>&nbsp;</td></tr><tr><td>
			<table  class="DCT_oldtable" cellspacing="0" cellpadding="0" width="98%" border="0" >
				<tbody  id="addinputtable">
				<tr>
		  			<td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>
		  		</tr>
		  		<tr>
		  			<td><fmt:message key="duckling.domain.main" /></td>
			  		<td nowrap="nowrap" width="50%">
							<input type="text" name="duckling.domain" value= "<c:out value='${allmap["duckling.domain"]}'/>">
					</td>
				</tr>
				<c:forEach var="arrayItem" items='${domainList}' >
					<tr id="STTR${arrayItem.key}">
			  			<td>&nbsp;</td><td>&nbsp;</td>
			  		</tr>
					<tr id="OTR${arrayItem.key}">
						<td width="50%" nowrap="nowrap">
							<fmt:message key="duckling.domain" />
						</td>
						<td>
							<input type="text" name="${arrayItem.key}" value="${arrayItem.value}">
						</td>
						<td>
						<input type="button" id="delete${arrayItem.key}"   style="BACKGROUND: url(../images/delete.gif) no-repeat; WIDTH: 22px; HEIGHT: 22px"	onclick="javascript:deleteTr('${arrayItem.key}')">
						</td>
					</tr>
				</c:forEach>
		  		</tbody>
			</table>
		</td></tr>
		<tr><td>&nbsp;</td></tr>
		<tr>
		  	<td><a onclick="addDomainConfig()" style="color:blue;cursor:pointer;"><fmt:message key="duckling.add.domain"/></a></td>
		</tr>
		<tr><td>&nbsp;</td></tr>
		<tr><td align="center"><input type="submit" class="DuclingButton" value='<fmt:message key="page.setting.submit"/>'>&nbsp;&nbsp;</td></tr>
		<tr><td>&nbsp;</td></tr></table>
		 </form>
     </div>
     <script language="javascript">
     	OnLoadChangeIt('<c:out value='${allmap["duckling.searchbox"]}'/>',"duckling.searchbox");
		OnLoadChangeIt('<c:out value='${allmap["duckling.userbox"]}'/>',"duckling.userbox");
		OnLoadChangeIt('<c:out value='${allmap["duckling.ddata"]}'/>',"duckling.ddata");
		OnLoadChangeIt('<c:out value='${allmap["duckling.bannertitle"]}'/>',"duckling.bannertitle");
		OnLoadChangeIt('<c:out value='${allmap["duckling.robots"]}'/>',"duckling.robots");
		OnLoadChangeIt('<c:out value='${allmap["duckling.allowanonymous"]}'/>',"duckling.allowanonymous");
     </script>
    
  </body>
</html>
