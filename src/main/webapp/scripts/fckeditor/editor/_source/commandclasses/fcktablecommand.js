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
 * FCKPastePlainTextCommand Class: represents the
 * "Paste as Plain Text" command.
 */

var FCKTableCommand = function( command )
{
	this.Name = command ;
};

FCKTableCommand.prototype.Execute = function()
{
	FCKUndo.SaveUndoStep() ;
	FCKSelection.Save();
	var result;
	if ( ! FCKBrowserInfo.IsGecko )
	{
		switch ( this.Name )
		{
			case 'TableMergeRight' :
				result = FCKTableHandler.MergeRight() ;
				break;
			case 'TableMergeDown' :
				result=  FCKTableHandler.MergeDown() ;
				break;
		}
	}

	switch ( this.Name )
	{
		case 'TableInsertRowAfter' :
			result =  FCKTableHandler.InsertRow( false ) ;
			break;
		case 'TableInsertRowBefore' :
			result =  FCKTableHandler.InsertRow( true ) ;
			break;
		case 'TableDeleteRows' :
			result =  FCKTableHandler.DeleteRows() ;
			break;
		case 'TableInsertColumnAfter' :
			result =  FCKTableHandler.InsertColumn( false ) ;
			break;
		case 'TableInsertColumnBefore' :
			result =  FCKTableHandler.InsertColumn( true ) ;
			break;
		case 'TableDeleteColumns' :
			result =  FCKTableHandler.DeleteColumns() ;
			break;
		case 'TableInsertCellAfter' :
			result =  FCKTableHandler.InsertCell( null, false ) ;
			break;
		case 'TableInsertCellBefore' :
			result =  FCKTableHandler.InsertCell( null, true ) ;
			break;
		case 'TableDeleteCells' :
			result =  FCKTableHandler.DeleteCells() ;
			break;
		case 'TableMergeCells' :
			result =  FCKTableHandler.MergeCells() ;
			break;
		case 'TableHorizontalSplitCell' :
			result =  FCKTableHandler.HorizontalSplitCell() ;
			break;
		case 'TableVerticalSplitCell' :
			result =  FCKTableHandler.VerticalSplitCell() ;
			break;
		case 'TableDelete' :
			result =  FCKTableHandler.DeleteTable() ;
			break;
		default :
			result =  alert( FCKLang.UnknownCommand.replace( /%1/g, this.Name ) ) ;
			break;
	}
	FCKSelection.Release();
	return result;
};


FCKTableCommand.prototype.GetState = function()
{
	if ( FCK.EditorDocument != null && FCKSelection.HasAncestorNode( 'TABLE' ) )
	{
		switch ( this.Name )
		{
			case 'TableHorizontalSplitCell' :
			case 'TableVerticalSplitCell' :
				if ( FCKTableHandler.GetSelectedCells().length == 1 )
					return FCK_TRISTATE_OFF ;
				else
					return FCK_TRISTATE_DISABLED ;
			case 'TableMergeCells' :
				if ( FCKTableHandler.CheckIsSelectionRectangular()
						&& FCKTableHandler.GetSelectedCells().length > 1 )
					return FCK_TRISTATE_OFF ;
				else
					return FCK_TRISTATE_DISABLED ;
			case 'TableMergeRight' :
				return FCKTableHandler.GetMergeRightTarget() ? FCK_TRISTATE_OFF : FCK_TRISTATE_DISABLED ;
			case 'TableMergeDown' :
				return FCKTableHandler.GetMergeDownTarget() ? FCK_TRISTATE_OFF : FCK_TRISTATE_DISABLED ;
			default :
				return FCK_TRISTATE_OFF ;
		}
	}
	else
		return FCK_TRISTATE_DISABLED;
};
