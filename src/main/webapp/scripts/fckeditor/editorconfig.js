/*
 * FCKeditor - The text editor for Internet - http://www.fckeditor.net
 * Copyright (C) 2003-2008 Frederico Caldeira Knabben
 *
 * == BEGIN LICENSE ==
 *
 * Licensed under the terms of any of the following licenses at your
 * choice:
 *
 *  - GNU General Public License Version 2 or later (the "GPL")
 *    http://www.gnu.org/licenses/gpl.html
 *
 *  - GNU Lesser General Public License Version 2.1 or later (the "LGPL")
 *    http://www.gnu.org/licenses/lgpl.html
 *
 *  - Mozilla Public License Version 1.1 or later (the "MPL")
 *    http://www.mozilla.org/MPL/MPL-1.1.html
 *
 * == END LICENSE ==
 *
 * Editor configuration settings.
 *
 * Follow this link for more information:
 * http://docs.fckeditor.net/FCKeditor_2.x/Developers_Guide/Configuration/Configuration_Options
 */

FCKConfig.CustomConfigurationsPath = '' ;

FCKConfig.EditorAreaCSS = FCKConfig.BasePath + 'css/fck_editorarea.css' ;

FCKConfig.EditorAreaDucklingCSS ='' ;
FCKConfig.EditorAreaStyles = '' ;
FCKConfig.ToolbarComboPreviewCSS = '' ;

//FCKConfig.DocType = '<!DOCTYPE HTML PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">' ;
FCKConfig.DocType = '';
FCKConfig.BaseHref = '' ;
//add by diyanliang 09-8-10因为ie下对a元素的href做了处理，把如href=“main”改为了href=“http://youbaseurl/main”所以增加如下逻辑
FCKConfig.DucklingBaseHref = '' ;
FCKConfig.DucklingResourceId = '' ;
FCKConfig.FullPage = false ;


FCKConfig.ducklingbodyid='DCT_viewcontent';

// The following option determines whether the "Show Blocks" feature is enabled or not at startup.
FCKConfig.StartupShowBlocks = false ;

FCKConfig.Debug = false ;
FCKConfig.AllowQueryStringDebug = true ;

FCKConfig.ToolbarType='E2';
FCKConfig.SkinPath = FCKConfig.BasePath + 'skins/e2/' ;
FCKConfig.BGColors	= 'E2Style1;E2Style2;E2Style3' ;
//FCKConfig.SkinPath = FCKConfig.BasePath + 'skins/office2003/' ;

FCKConfig.SkinEditorCSS = '' ;	// FCKConfig.SkinPath + "|<minified css>" ;
FCKConfig.SkinDialogCSS = '' ;	// FCKConfig.SkinPath + "|<minified css>" ;

FCKConfig.PreloadImages = [ FCKConfig.SkinPath + 'images/toolbar.start.gif', FCKConfig.SkinPath + 'images/toolbar.buttonarrow.gif' ] ;

FCKConfig.PluginsPath = FCKConfig.BasePath + 'plugins/' ;

FCKConfig.Plugins.Add( 'tabaction' ) ;

FCKConfig.Plugins.Add( 'changelink' ) ;
FCKConfig.Plugins.Add( 'deautogrow' ) ;
FCKConfig.Plugins.Add( 'resizetdwidth' );
FCKConfig.Plugins.Add( 'addtablerow' );
FCKConfig.Plugins.Add( 'floattoolbar' );
//FCKConfig.Plugins.Add( 'cellselection' );
FCKConfig.Plugins.Add( 'e2toolbar' );
FCKConfig.Plugins.Add( 'addattachmentapi' );
FCKConfig.Plugins.Add('nodebar');
FCKConfig.Plugins.Add('nodebar_link');
FCKConfig.Plugins.Add('nodebar_img');
FCKConfig.Plugins.Add('dylantools');
FCKConfig.Plugins.Add('specialtablebutton');
FCKConfig.Plugins.Add('e2contextmenu');
FCKConfig.Plugins.Add('specialbgcolorbutton');
FCKConfig.Plugins.Add('useractivelistenerapi');

FCKConfig.AutoGrowMax = 8000 ;

// FCKConfig.ProtectedSource.Add( /<%[\s\S]*?%>/g ) ;	// ASP style server side code <%...%>
// FCKConfig.ProtectedSource.Add( /<\?[\s\S]*?\?>/g ) ;	// PHP style server side code
// FCKConfig.ProtectedSource.Add( /(<asp:[^\>]+>[\s|\S]*?<\/asp:[^\>]+>)|(<asp:[^\>]+\/>)/gi ) ;	// ASP.Net style tags <asp:control>

FCKConfig.AutoDetectLanguage	= true ;
FCKConfig.DefaultLanguage		= 'en-us' ;
FCKConfig.ContentLangDirection	= 'ltr' ;

FCKConfig.ProcessHTMLEntities	= true ;
FCKConfig.IncludeLatinEntities	= true ;
FCKConfig.IncludeGreekEntities	= true ;

FCKConfig.ProcessNumericEntities = false ;

FCKConfig.AdditionalNumericEntities = ''  ;		// Single Quote: "'"

FCKConfig.FillEmptyBlocks	= true ;

FCKConfig.FormatSource		= true ;
FCKConfig.FormatOutput		= true ;
FCKConfig.FormatIndentator	= '    ' ;

FCKConfig.StartupFocus	= true ;
FCKConfig.ForcePasteAsPlainText	= false ;
FCKConfig.AutoDetectPasteFromWord = true ;	// IE only.
FCKConfig.ShowDropDialog = true ;
FCKConfig.ForceSimpleAmpersand	= false ;
FCKConfig.TabSpaces		= 8 ;
FCKConfig.ShowBorders	= true ;
FCKConfig.SourcePopup	= false ;
FCKConfig.ToolbarStartExpanded	= true ;
FCKConfig.ToolbarCanCollapse	= false ;
FCKConfig.IgnoreEmptyParagraphValue = true ;
FCKConfig.FloatingPanelsZIndex = 10000 ;
FCKConfig.HtmlEncodeOutput = false ;

FCKConfig.TemplateReplaceAll = true ;
FCKConfig.TemplateReplaceCheckbox = true ;

FCKConfig.ToolbarLocation = 'In' ;
FCKConfig.DucklingLocales="en-us";
FCKConfig.site=null;
FCKConfig.ToolbarSets["Default"] = [
	['Select', 'Button', 'Link', 'Image','Unlink','Flash'],
	['Source','-','NewPage','-','SelectAll','RemoveFormat'],
	['Cut','Copy','Paste','PasteText','PasteWord','-'],
	['Undo','Redo','-','Find','Replace'],
	'/',
	['Bold','Italic','Underline','StrikeThrough','-','Subscript','Superscript'],
	['OrderedList','UnorderedList','-','Outdent','Indent'],
	['JustifyLeft','JustifyCenter','JustifyRight','JustifyFull'],
	['Table','Rule','Smiley','SpecialChar'],['Templates','DMLPlugin','DMLSection'],
	'/',
	['FontFormat','FontName','FontSize'],
	['TextColor','BGColor'],
	['FitWindow','ShowBlocks','-','About','Print']		// No comma for the last row.
] ;
/*duckling按钮数组：按钮添加规则，每行开头必须加'-'即行起始符号也是新行的标志,每个功能块用[]数组表示*/
FCKConfig.ToolbarSets["Duckling"] = [
	['-','FontName','Bold','Italic','Underline',
	'-','FontSize','Subscript','Superscript','StrikeThrough',
	'-','FontFormat','BGColor','TextColor']
	,
	['-','Select', 'UpdateAttach','Button','Link','Unlink',
	'-', 'Table','TableRedraw','Image','Flash','SpecialChar',
	'-','Rule','Templates','DMLPlugin']
	,

	['-','OrderedList','UnorderedList','Outdent','Indent',
	'-','JustifyLeft','JustifyCenter','JustifyRight','JustifyFull',
	'-','Undo','Redo','ImageScroll','DEMap']
	,
	['-','Find','Replace','NewPage',
	'-','SelectAll','RemoveFormat',
	'-','ShowBlocks']
	,
	['-','Cut','Copy','Paste',
	'-','PasteWord','PasteText','FitWindow',
	'-','Source']
	,
	['-','Form','DMLSelect','Textarea',
		'-','TextField','Radio','Checkbox',
		'-','DMLButton','DMLResultSet','DMLField']
	
];

FCKConfig.ToolbarSets["NodData"] = [
 	['-','FontName','Bold','Italic','Underline',
 	'-','FontSize','Subscript','Superscript','StrikeThrough',
 	'-','FontFormat','BGColor','TextColor']
 	,
 	['-','Select', 'UpdateAttach','Button','Link','Unlink',
 	'-', 'Table','TableRedraw','Image','Flash','SpecialChar',
 	'-','Rule','Templates','DMLPlugin']
 	,

 	['-','OrderedList','UnorderedList','Outdent','Indent',
 	'-','JustifyLeft','JustifyCenter','JustifyRight','JustifyFull',
 	'-','Undo','Redo','ImageScroll','DEMap']
 	,
 	['-','Find','Replace','NewPage',
 	'-','SelectAll','RemoveFormat',
 	'-','ShowBlocks']
 	,
 	['-','Cut','Copy','Paste',
 	'-','PasteWord','PasteText','FitWindow',
 	'-','Source']
 	
 	
];
FCKConfig.ToolbarSets["E2"] = [
                                 	[
                                 	 '-','E2Save',
                                 	 '-','Undo','Redo'
                                 	],
                                 	[
                                 	 '-', 'E2H1', 'E2H2', 'E2H3','E2P','E2blockquote','OrderedList','UnorderedList',
                                 	 '-','Bold','Italic','Underline','StrikeThrough','E2BGColor','Subscript','Superscript','JustifyLeft','JustifyCenter','JustifyRight','E2Comment','E2RemoveFormat'
                                 	]
                                 	,
                                 	[
                                 	 '-','E2Table','E2Link',
                                 	 '-','E2Page','E2Image'
                                 	]
                                 	,
                                 	[
                                     '-','E2Source'
                                 	]
//                                 	,
//                                 	[
//                                 	 '-','BGColor','Source','FontFormat','Style'
//                                 	]
                                 ];

FCKConfig.ToolbarSets["Basic"] = [
	['Bold','Italic','-','OrderedList','UnorderedList','-','Link','Unlink','-','About']
] ;

FCKConfig.EnterMode = 'p' ;			// p | div | br
FCKConfig.ShiftEnterMode = 'br' ;	// p | div | br

FCKConfig.Keystrokes = [
	[ CTRL + 65 /*A*/, true ],
	[ CTRL + 67 /*C*/, true ],
	[ CTRL + 70 /*F*/, true ],
	[ CTRL + 83 /*S*/, true ],
	[ CTRL + 84 /*T*/, true ],
	[ CTRL + 88 /*X*/, true ],
	[ CTRL + 86 /*V*/, true],
	[ CTRL + 45 /*INS*/, true ],
	[ SHIFT + 45 /*INS*/, 'Paste' ],
	[ CTRL + 88 /*X*/, 'Cut' ],
	[ SHIFT + 46 /*DEL*/, 'Cut' ],
	[ CTRL + 90 /*Z*/, 'Undo' ],
	[ CTRL + 89 /*Y*/, 'Redo' ],
	[ CTRL + SHIFT + 90 /*Z*/, 'Redo' ],
	[ CTRL + 76 /*L*/, 'Link' ],
	[ CTRL + 66 /*B*/, 'Bold' ],
	[ CTRL + 73 /*I*/, 'Italic' ],
	[ CTRL + 85 /*U*/, 'Underline' ],
	[ CTRL + SHIFT + 83 /*S*/, 'Save' ],
	[ CTRL + ALT + 13 /*ENTER*/, 'FitWindow' ]
] ;

FCKConfig.ContextMenu = ['DMLPlugin','Generic','Link','Anchor','Image','Flash','Select','Textarea','Checkbox','Radio','TextField','HiddenField','ImageButton','Button','BulletedList','NumberedList','Table','Form','UpdateAttach'] ;
FCKConfig.BrowserContextMenuOnCtrl = false ;

FCKConfig.EnableMoreFontColors = true ;
FCKConfig.FontColors = '000000,993300,333300,003300,003366,000080,333399,333333,800000,FF6600,808000,808080,008080,0000FF,666699,808080,FF0000,FF9900,99CC00,339966,33CCCC,3366FF,800080,999999,FF00FF,FFCC00,FFFF00,00FF00,00FFFF,00CCFF,993366,C0C0C0,FF99CC,FFCC99,FFFF99,CCFFCC,CCFFFF,99CCFF,CC99FF,FFFFFF' ;

FCKConfig.FontFormats	= 'p;h1;h2;h3;h4;pre;address;div' ;
//FCKConfig.FontFormats	= 'p;h1;h2;h3;h4;h5;h6;pre;address;div' ;
FCKConfig.FontNames		= '宋体;黑体;隶书;楷体_GB2312;Arial;Comic Sans MS;Courier New;Tahoma;Times New Roman;Verdana' ;
//FCKConfig.FontSizes		= 'smaller;larger;xx-small;x-small;small;medium;large;x-large;xx-large' ;
//FCKConfig.FontSizes		= 'xx-small;x-small;small;medium;large;x-large;xx-large' ;
FCKConfig.FontSizes		= '6;8;10;12;14;16;18;20;22;24;26;28;36;48;72' ;
FCKConfig.StylesXmlPath		= FCKConfig.EditorPath + 'fckstyles.xml' ;
FCKConfig.TemplatesXmlPath	= FCKConfig.EditorPath + 'fcktemplates.xml' ;
FCKConfig.DMLPluginXmlPath	= FCKConfig.EditorPath + 'fcktemplates.xml' ;

FCKConfig.SpellChecker			= 'ieSpell' ;	// 'ieSpell' | 'SpellerPages'
FCKConfig.IeSpellDownloadUrl	= 'http://www.iespell.com/download.php' ;
FCKConfig.SpellerPagesServerScript = 'server-scripts/spellchecker.php' ;	// Available extension: .php .cfm .pl
FCKConfig.FirefoxSpellChecker	= false ;

FCKConfig.MaxUndoLevels = 15 ;

FCKConfig.DisableObjectResizing = false ;
FCKConfig.DisableFFTableHandles = true ;

FCKConfig.LinkDlgHideTarget		= false ;
FCKConfig.LinkDlgHideAdvanced	= false ;

FCKConfig.ImageDlgHideLink		= false ;
FCKConfig.ImageDlgHideAdvanced	= false ;

FCKConfig.FlashDlgHideAdvanced	= false ;

FCKConfig.ProtectedTags = '' ;

// This will be applied to the body element of the editor
FCKConfig.BodyId = '' ;
FCKConfig.BodyClass = '' ;

FCKConfig.DefaultStyleLabel = '' ;
FCKConfig.DefaultFontFormatLabel = '' ;
FCKConfig.DefaultFontLabel = '' ;
FCKConfig.DefaultFontSizeLabel = '' ;

FCKConfig.DefaultLinkTarget = '' ;

// The option switches between trying to keep the html structure or do the changes so the content looks like it was in Word
FCKConfig.CleanWordKeepsStructure = false ;

// Only inline elements are valid.
FCKConfig.RemoveFormatTags = 'b,big,code,del,dfn,em,font,i,ins,kbd,q,samp,small,span,strike,strong,sub,sup,tt,u,var' ;

// Attributes that will be removed
FCKConfig.RemoveAttributes = 'class,style,lang,width,height,align,hspace,valign' ;

FCKConfig.CustomStyles =
{
	'Red Title'	: { Element : 'h3', Styles : { 'color' : 'Red' } }
	
};

// Do not add, rename or remove styles here. Only apply definition changes.
FCKConfig.CoreStyles =
{
	// Basic Inline Styles.
	'Bold'			: { Element : 'strong', Overrides : 'b' },
	'Italic'		: { Element : 'em', Overrides : 'i' },
	'Underline'		: { Element : 'u' },
	'StrikeThrough'	: { Element : 'strike' },
	'Subscript'		: { Element : 'sub' },
	'Superscript'	: { Element : 'sup' },

	// Basic Block Styles (Font Format Combo).
	'p'				: { Element : 'p' },
	'div'			: { Element : 'div' },
	'pre'			: { Element : 'pre' },
	'address'		: { Element : 'address' },
	'h1'			: { Element : 'h1' },
	'h2'			: { Element : 'h2' },
	'h3'			: { Element : 'h3' },
	'h4'			: { Element : 'h4' },
	'h5'			: { Element : 'h5' },
	'h6'			: { Element : 'h6' },
	'blockquote'    : { Element : 'blockquote'},
	'E2Style1' : 
	{
		Element : 'span',
		Attributes : { 'class' : 'E2Style1' }
	},
	'E2Style2' : 
	{
		Element : 'span',
		Attributes : { 'class' : 'E2Style2' }
	},	
	'E2Style3' : 
	{
		Element : 'span',
		Attributes : { 'class' : 'E2Style3' }
	},
	'E2BGColor' : 
	{
		Element : 'span',
		Attributes : { 'class' : 'bgMark' }
	},
	'E2Comment':
	{
		Element:'span',
		Attributes:{'class':'note'}
	},

	// Other formatting features.
	'FontFace' :
	{
		Element		: 'span',
		Styles		: { 'font-family' : '#("Font")' },
		Overrides	: [ { Element : 'font', Attributes : { 'face' : null } } ]
	},

	'Size' :
	{
		Element		: 'span',
		Styles		: { 'font-size' : '#("Size","fontSize")' },
		Overrides	: [ { Element : 'font', Attributes : { 'size' : null } } ]
	},

	'Color' :
	{
		Element		: 'span',
		Styles		: { 'color' : '#("Color","color")' },
		Overrides	: [ { Element : 'font', Attributes : { 'color' : null } } ]
	},

	'BackColor'		: { Element : 'span', Styles : { 'background-color' : '#("Color","color")' } },

	'SelectionHighlight' : { Element : 'span', Styles : { 'background-color' : 'navy', 'color' : 'white' } }
};

// The distance of an indentation step.
FCKConfig.IndentLength = 40 ;
FCKConfig.IndentUnit = 'px' ;

// Alternatively, FCKeditor allows the use of CSS classes for block indentation.
// This overrides the IndentLength/IndentUnit settings.
FCKConfig.IndentClasses = [] ;

// [ Left, Center, Right, Justified ]
FCKConfig.JustifyClasses = [] ;

// The following value defines which File Browser connector and Quick Upload
// "uploader" to use. It is valid for the default implementaion and it is here
// just to make this configuration file cleaner.
// It is not possible to change this value using an external file or even
// inline when creating the editor instance. In that cases you must set the
// values of LinkBrowserURL, ImageBrowserURL and so on.
// Custom implementations should just ignore it.
var _FileBrowserLanguage	= 'asp' ;	// asp | aspx | cfm | lasso | perl | php | py
var _QuickUploadLanguage	= 'jsp' ;	// asp | aspx | cfm | lasso | php  | jsp

// Don't care about the following two lines. It just calculates the correct connector
// extension to use for the default File Browser (Perl uses "cgi").
var _FileBrowserExtension = _FileBrowserLanguage == 'perl' ? 'cgi' : _FileBrowserLanguage ;
var _QuickUploadExtension = _QuickUploadLanguage == 'perl' ? 'cgi' : _QuickUploadLanguage ;

FCKConfig.LinkBrowser = false ;
FCKConfig.LinkBrowserURL = FCKConfig.BasePath + 'filemanager/browser/default/browser.html?Connector=' + encodeURIComponent( FCKConfig.BasePath + 'filemanager/connectors/' + _FileBrowserLanguage + '/connector.' + _FileBrowserExtension ) ;
FCKConfig.LinkBrowserWindowWidth	= FCKConfig.ScreenWidth * 0.7 ;		// 70%
FCKConfig.LinkBrowserWindowHeight	= FCKConfig.ScreenHeight * 0.7 ;	// 70%

FCKConfig.ImageBrowser = false ;
FCKConfig.ImageBrowserURL = FCKConfig.BasePath + 'filemanager/browser/default/browser.html?Type=Image&Connector=' + encodeURIComponent( FCKConfig.BasePath + 'filemanager/connectors/' + _FileBrowserLanguage + '/connector.' + _FileBrowserExtension ) ;
FCKConfig.ImageBrowserWindowWidth  = FCKConfig.ScreenWidth * 0.7 ;	// 70% ;
FCKConfig.ImageBrowserWindowHeight = FCKConfig.ScreenHeight * 0.2 ;	// 70% ;

FCKConfig.FlashBrowser = false ;
FCKConfig.FlashBrowserURL = FCKConfig.BasePath + 'filemanager/browser/default/browser.html?Type=Flash&Connector=' + encodeURIComponent( FCKConfig.BasePath + 'filemanager/connectors/' + _FileBrowserLanguage + '/connector.' + _FileBrowserExtension ) ;
FCKConfig.FlashBrowserWindowWidth  = FCKConfig.ScreenWidth * 0.7 ;	//70% ;
FCKConfig.FlashBrowserWindowHeight = FCKConfig.ScreenHeight * 0.7 ;	//70% ;

FCKConfig.Upload = true ;
FCKConfig.UploadURL = FCKConfig.BasePath + 'filemanager/upload/' + _QuickUploadLanguage + '/upload.' + _QuickUploadLanguage ;
FCKConfig.UploadAllowedExtensions	= "" ;			// empty for all
FCKConfig.UploadDeniedExtensions	= "" ;//".(html|htm|php|php2|php3|php4|php5|phtml|pwml|inc|asp|aspx|ascx|jsp|cfm|cfc|pl|bat|exe|com|dll|vbs|js|reg|cgi|htaccess|asis|sh|shtml|shtm|phtm)$" ;	// empty for no one

FCKConfig.LinkUpload = false ;
FCKConfig.LinkUploadURL = FCKConfig.BasePath + 'filemanager/connectors/' + _QuickUploadLanguage + '/upload.' + _QuickUploadExtension ;
FCKConfig.LinkUploadAllowedExtensions	= ".(7z|aiff|asf|avi|bmp|csv|doc|fla|flv|gif|gz|gzip|jpeg|jpg|mid|mov|mp3|mp4|mpc|mpeg|mpg|ods|odt|pdf|png|ppt|pxd|qt|ram|rar|rm|rmi|rmvb|rtf|sdc|sitd|swf|sxc|sxw|tar|tgz|tif|tiff|txt|vsd|wav|wma|wmv|xls|xml|zip)$" ;			// empty for all
FCKConfig.LinkUploadDeniedExtensions	= "" ;	// empty for no one

FCKConfig.ImageUpload = true ;
FCKConfig.ImageUploadURL = FCKConfig.BasePath + 'filemanager/connectors/' + _QuickUploadLanguage + '/upload.' + _QuickUploadExtension + '?Type=Image' ;
FCKConfig.ImageUploadAllowedExtensions	= ".(jpg|gif|jpeg|png|bmp)$" ;		// empty for all
FCKConfig.ImageUploadDeniedExtensions	= "" ;							// empty for no one

FCKConfig.FlashUpload = true ;
FCKConfig.FlashUploadURL = FCKConfig.BasePath + 'filemanager/connectors/' + _QuickUploadLanguage + '/upload.' + _QuickUploadExtension + '?Type=Flash' ;
FCKConfig.FlashUploadAllowedExtensions	= ".(swf|flv)$" ;		// empty for all
FCKConfig.FlashUploadDeniedExtensions	= "" ;					// empty for no one

FCKConfig.SmileyPath	= FCKConfig.BasePath + 'images/smiley/msn/' ;
FCKConfig.SmileyImages	= ['regular_smile.gif','sad_smile.gif','wink_smile.gif','teeth_smile.gif','confused_smile.gif','tounge_smile.gif','embaressed_smile.gif','omg_smile.gif','whatchutalkingabout_smile.gif','angry_smile.gif','angel_smile.gif','shades_smile.gif','devil_smile.gif','cry_smile.gif','lightbulb.gif','thumbs_down.gif','thumbs_up.gif','heart.gif','broken_heart.gif','kiss.gif','envelope.gif'] ;
FCKConfig.SmileyColumns = 8 ;
FCKConfig.SmileyWindowWidth		= 320 ;
FCKConfig.SmileyWindowHeight	= 210 ;

FCKConfig.BackgroundBlockerColor = '#ffffff' ;
FCKConfig.BackgroundBlockerOpacity = 0.50 ;

FCKConfig.MsWebBrowserControlCompat = false ;


FCKConfig.TableRedrawPath	= FCKConfig.BasePath + 'images/tableredraw/' ;
FCKConfig.TableRedrawImages	= [ 'DCT_oldtable.gif','DCT_blacktable.gif','DCT_crosstable.gif','DCT_blueheader.gif','DCT_bluelefter.gif',
	                           	'DCT_black_1.gif','DCT_blue_1.gif','DCT_green_1.gif','DCT_purple_1.gif','DCT_red_1.gif',
	                           	'DCT_black_3.gif','DCT_blue_3.gif','DCT_green_3.gif','DCT_purple_3.gif','DCT_red_3.gif',
	                        	'DCT_black_2.gif','DCT_blue_2.gif','DCT_green_2.gif','DCT_purple_2.gif','DCT_red_2.gif',
	                           	'DCT_black_4.gif','DCT_blue_4.gif','DCT_green_4.gif','DCT_purple_4.gif','DCT_red_4.gif',
	                           	'DCT_black_5.gif','DCT_blue_5.gif','DCT_green_5.gif','DCT_purple_5.gif','DCT_red_5.gif',
	                           	'DCT_black_6.gif','DCT_blue_6.gif','DCT_green_6.gif','DCT_purple_6.gif','DCT_red_6.gif',
	                           	'DCT_black_7.gif','DCT_blue_7.gif','DCT_green_7.gif','DCT_purple_7.gif','DCT_red_7.gif',
	                           	'DCT_black_8.gif','DCT_blue_8.gif','DCT_green_8.gif','DCT_purple_8.gif','DCT_red_8.gif',
	                           	'DCT_black_9.gif','DCT_blue_9.gif','DCT_green_9.gif','DCT_purple_9.gif','DCT_red_9.gif',
	                           	'DCT_black_10.gif','DCT_blue_10.gif','DCT_green_10.gif','DCT_purple_10.gif','DCT_red_10.gif',
	                           	'DCT_black_11.gif','DCT_blue_11.gif','DCT_green_11.gif','DCT_purple_11.gif','DCT_red_11.gif',
	                           ]
FCKConfig.TableRedrawColumns = 5 ;
