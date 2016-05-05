<html>
  <head>
    <title>DucklingLink</title>
    <script src="common/fck_dialog_common.js" type="text/javascript"></script>
	<link rel="stylesheet" type="text/css" href='../../../../scripts/DUI/autosearch/jquery.autocomplete.css' />
	<script type="text/javascript" src='../../../../scripts/jquery/jquery-1.8.3.min.js'></script>
	<script type="text/javascript" src='../../../../scripts/DUI/autosearch/jquery.autocomplete.js'></script>
	<script src="fck_link/fck_Ducklinglink.js?v=DDL4.1.2" type="text/javascript"></script> 
	<script type="text/javascript" src="swfobject/swfobject.js"></script>
 	<style>
		.DisableDucklingLnkUrlInner{
			 background-color: #dcdcdc;
			 border: #c0c0c0 1px solid;
			 color: #000000;
			 cursor: default; 
		}
	</style>
  </head>
  <body class="InnerBody" >
  	
     <div id="DucklingLnkPage"  width="500px"  style="DISPLAY: none">
		<table  cellspacing="0" cellpadding="0" width="100%"   >
			<tr>
				<td  class="DE_dialogboderstyle"><span  fcklang="DMLPluginProValue" class="DE_dialogfontstyle"></span>
	  			<table cellspacing="0" cellpadding="0" width="100%" border="0" dir="ltr">
				<tr><td>&nbsp;</td><td>&nbsp;</td></tr>
				<tr>
					<td width="25%" nowrap="nowrap">
						<span fckLang="LinkPageName">Page Name</span><input type="hidden" class="inputText"  id="ResourceId" value="" />
					</td>
					<td  nowrap="nowrap" width="100%">
					  <input type="text" class="inputText" name="parentPage" style="width:100%;"
							id="txtDucklingLnkPage" value="" />	
							
							
					</td>
				</tr>
			
				</table>
				</td>
			</tr>
		</table>
		<fieldset class="DE_dialogboderstyle" style="text-align: center;"><legend  class="DE_legendfontstyle" fcklang="LinkInformation" ></legend>
			<table cellspacing="0" cellpadding="0" width="100%" border="0" dir="ltr">
				<tr>
					<td><br/></td>
					<td><br/></td>
				</tr>
				<tr>
					<td width="25%"><span fckLang="LinkText">Text</span></td>
					<td width="100%"><input id="DucklingLnkPageInner" style="WIDTH:100%" type="text"  /></td>
				</tr>
				<tr>
					<td><br/></td>
					<td><br/></td>
				</tr>
				<tr>
					<td><span fckLang="LinkTooltips">Tooltips</span></td>
					<td ><input id="DucklingLnkPageTooltips"  style="WIDTH: 100%" type="hidden"  /></td>
				</tr>
				<tr><td>&nbsp;</td><td>&nbsp;</td></tr>
				<tr>
					<td><br/></td>
					<td ><input id="DucklingLnkPageBlock" type="checkbox" /><span fckLang="LinkBlank">Open link in new window</span></td>
				</tr>
			</table>
		</fieldset>
  	</div>
  	<div id="DucklingLnkClb"  style="DISPLAY: none;width:450px;height:350px;" >
	  	<div id="myContent"  style="z-index:3" >
			<table width="100%" align="center"><tr><td valign="middle">
			</td></tr></table>
		</div>
    </div>
    <div id="DucklingLnkUrl" align="center" width="100%" style="DISPLAY: none">
  		<table  cellspacing="0" cellpadding="0" width="100%"   >
	  		<tr>
		  		<td  class="DE_dialogboderstyle"><span  fcklang="DMLPluginProValue" class="DE_dialogfontstyle"></span>
					<table cellspacing="0" cellpadding="0" width="100%" border="0" dir="ltr">
						<tr>
							<td width="25%" nowrap="nowrap">
							<!-- 	<span fckLang="DlgLnkProto">Protocol</span><br />
								<select id="cmbLinkProtocol" style="width:80px">
									<option value="http://" selected="selected">http://</option>
									<option value="https://">https://</option>
									<option value="ftp://">ftp://</option>
									<option value="baseurl://">baseurl://</option>
								</select> -->
								<span fckLang="DlgLnkURL">URL</span>
							</td>
							<td nowrap="nowrap" width="100%">
								
								<input id="txtDucklingLnkUrl" style="WIDTH: 100%" type="text"/>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
		<fieldset class="DE_dialogboderstyle" style="text-align: center;"><legend  class="DE_legendfontstyle" fcklang="LinkInformation" ></legend>
			<table cellspacing="0" cellpadding="0" width="100%" border="0" dir="ltr">
				<tr>
					<td><br/></td>
					<td><br/></td>
				</tr>
				<tr>
					<td width="25%"><span fckLang="LinkText">Text</span></td>
					<td width="100%"><input id="DucklingLnkUrlInner" style="WIDTH: 100%" type="text"  /></td>
				</tr>
				<tr>
					<td><br/></td>
					<td><br/></td>
				</tr>
				<tr>
					<td><span fckLang="LinkTooltips">Tooltips</span></td>
					<td width="100%"><input id="DucklingLnkUrlTooltips" style="WIDTH: 100%" type="text"  /></td>
				</tr>
				<tr>
					<td><br/></td>
					<td><br/></td>
				</tr>
				<tr>
					<td><br/></td>
					<td width="100%"><input id="DucklingLnkUrlBlock" type="checkbox" /><span fckLang="LinkBlank">Open link in new window</span></td>
				</tr>
			</table>
		</fieldset>  
     </div>
  	<div id="DucklingLnkEmail" style="DISPLAY: none">
  		<table  cellspacing="0" cellpadding="0" width="100%"   >
  			<tr>
  				<td  class="DE_dialogboderstyle"><span  fcklang="DMLPluginProValue" class="DE_dialogfontstyle"></span>
 					<table cellspacing="0" cellpadding="0" width="100%" border="0" dir="ltr">
						<tr>
							<td><br/></td>
							<td><br/></td>
						</tr>
						<tr>
							<td  width="25%" nowrap="nowrap">
								<span fckLang="LinkEMail">E-Mail</span>
							</td>
							<td  nowrap="nowrap" width="100%">
								<input id="txtDucklingLnkEmail" style="WIDTH: 100%" type="text"/>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
		<fieldset class="DE_dialogboderstyle" style="text-align: center;"><legend   class="DE_legendfontstyle" fcklang="LinkInformation" ></legend>
			<table cellspacing="0" cellpadding="0" width="100%" border="0" dir="ltr">
				<tr>
					<td><br/></td>
					<td><br/></td>
				</tr>
				<tr>
					<td width="25%"><span fckLang="LinkText">Text</span></td>
					<td width="100%"><input id="DucklingLnkEmailInner" style="WIDTH: 100%" type="text"  /></td>
				</tr>
				<tr><td>&nbsp;</td><td>&nbsp;</td></tr>
				<tr>
					<td><span fckLang="LinkTooltips">Tooltips</span></td>
					<td width="100%"><input id="DucklingLnkEmailTooltips" style="WIDTH: 100%" type="text"  /></td>
				</tr>
				<tr><td>&nbsp;</td><td>&nbsp;</td></tr>
			</table>
		</fieldset>
  	</div>
  </body>
</html>
