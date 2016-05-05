<%@ page language="java" pageEncoding="UTF-8"%>
<%@taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%
String path = request.getContextPath();
%>
<script type="text/javascript" src="<%=path%>/scripts/fckeditor/fckeditor.js?v=${aoneVersion}"></script>	
<input id="fixDomStr" name="fixDomStr" type="hidden" value="false">
<input id="ResourceId" name="ResourceId" type="hidden" value="${editDpage.meta.rid}">
<!--the input 'section' is interface & need fix -->
<input id="section" name="section" type="hidden" >
<script type="text/javascript">
//<![CDATA[
	var oFCKeditor = new FCKeditor( 'htmlPageText' ) ;
	
	
	oFCKeditor.BasePath = '<vwb:Link context="plain" jsp="scripts/fckeditor/" format="url"/>';
	oFCKeditor.Height	= '550' ;
	oFCKeditor.Width  = '75%';
	oFCKeditor.Value = '${editDpage.detail.content}';
	oFCKeditor.Config['EditorAreaDucklingCSS'] = '<%=path%>/scripts/fckeditor/editor/css/skin.css';
	oFCKeditor.Config['DMLPluginXmlPath'] = oFCKeditor.BasePath+'dmlplugin.xml';
	oFCKeditor.Config['DucklingBaseHref'] ='<vwb:Link context="plain" jsp="" format="url" absolute="true"/>';
	oFCKeditor.Config['EditorAreaCSS'] = '<%=request.getContextPath()%>/jsp/aone/css/css.css';
	oFCKeditor.Config['DucklingResourceId']='${editDpage.meta.rid}';
	oFCKeditor.Config['DucklingLocales']='${locale}';
	oFCKeditor.Config['A1AjaxUrl']=site.getTeamURL("");
	oFCKeditor.Config['ResourceId']="${editDpage.meta.rid}";
	oFCKeditor.Config['DucklingCId']="${editDpage.meta.rid}";
//]]>
//	oFCKeditor.ToolbarSet = ('${useddata}'=='true')?'Duckling':'NodData'
	
	oFCKeditor.Create() ;
	runAutoSavePage();
	fLocker();
	//var oFCKeditor = FCKeditorAPI.GetInstance('htmlPageText');
	//oFCKeditor.Events.AttachEvent("keydown", editor_keyup);
</script>

