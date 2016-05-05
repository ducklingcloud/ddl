<%@ page language="java" pageEncoding="UTF-8"%>
<%@taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ page import="net.duckling.ddl.common.*"%>
<%@ page import="cn.vlabs.duckling.vwb.ui.command.*" %>
<html>
  <head>
    <title>DucklingLink</title>
    <script src="common/fck_dialog_common.js" type="text/javascript"></script>

	<link rel="stylesheet" type="text/css" href='../../../../scripts/DUI/autosearch/jquery.autocomplete.css' />
	<script type="text/javascript" src='../../../../scripts/jquery/jquery-1.8.3.min.js'></script>
	<script type="text/javascript" src='../../../../scripts/DUI/autosearch/jquery.autocomplete.js'></script>
	<script src="fck_link/fck_DucklingPage.js" type="text/javascript"></script> 
<script type="text/javascript" src="../../../ajax/ajax.js"></script>
  </head>
  <body class="InnerBody" >
 
 	
  	
     <div id="DucklingLnkPage"  width="500px"  >
		<table  cellspacing="0" cellpadding="0" width="100%"   >
			<tr>
				<td  class="DE_dialogboderstyle">
		  			<table cellspacing="0" cellpadding="0" width="100%" border="0" dir="ltr">
			  			<tr>
							<td width="25%"><span fckLang="LinkText">Text</span></td>
							<td width="100%"><input id="DucklingLnkPageInner" style="WIDTH:100%" type="text"  /></td>
						</tr>
						<tr><td>&nbsp;</td><td>&nbsp;</td></tr>
						<tr><td><span fckLang="DlgNewPageName">Page Name</span></td><td>&nbsp;</td></tr>
						<tr>
							<td width="25%" nowrap="nowrap">
								<input type="radio" name="IsNewPage" id="newpagetrue" onclick="IsNewPage(this)" checked="checked"/>新建页面
							</td>
							<td  nowrap="nowrap" width="100%">
								<!-- input id="txtUrl" style="WIDTH: 100%" type="text" />  -->
								<input id="txtDucklingNewPage" style="WIDTH: 100%" type="text" /> 
							</td>
						</tr>
						<tr><td>&nbsp;</td><td>&nbsp;</td></tr>
						<tr>
							<td width="25%" nowrap="nowrap">
								<input type="radio" name="IsNewPage"  id="newpagefalse" onclick="IsNewPage(this)" />已有页面
							</td>
							<td  nowrap="nowrap" width="100%">
							  <input type="hidden" class="inputText"  id="ResourceId" value="" />
							  <input type="text" class="inputText"  style="width:100%;" id="txtDucklingLnkPage" value="" disabled="disabled"/>	
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
		
		
			<table cellspacing="0" cellpadding="0" width="100%" border="0" dir="ltr">
				<tr><td>&nbsp;</td><td>&nbsp;</td></tr>
				<tr>
					<td><br/></td>
					<td ><input id="DucklingLnkPageBlock" type="checkbox" /><span fckLang="LinkBlank">Open link in new window</span></td>
				</tr>
			</table>
	
  	</div>
  
  </body>
</html>
