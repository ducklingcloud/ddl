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
 * Manage table operations.
 */

var FCKTableHandler = {};

FCKTableHandler.InsertRow = function(insertBefore) {
	var cell = null;
	var oCells = this.GetSelectedCells();
	if (oCells && oCells.length) {
		cell = oCells[insertBefore ? 0 : (oCells.length - 1)];
	}

	if (!cell) {
		return;
	}
	// 建立虚拟表
	var arr = this._CreateTableMap(cell.parentNode.parentNode);
	// 选择的单元格的虚拟表坐标
	// 重复的第一个
	var cellRCindex = testgetArrIndex(arr, cell);
	// 重复的最后一个
	var cellLastRCindex = testgetLastArrIndex(arr, cell);
	var newtablearr = [];
	var newrowindex = 0;
	for ( var i = 0; i < arr.length; i++) {
		if (!newtablearr[newrowindex]) {
			newtablearr[newrowindex] = [];
		}

		if (insertBefore) {// 上插入
			if (i != cellRCindex[0]) {

				for ( var j = 0; j < arr[i].length; j++) {
					newtablearr[newrowindex][j] = arr[i][j];
				}
				newrowindex++;
			} else {
				newtablearr[newrowindex + 1] = [];

				for ( var j = 0; j < arr[i].length; j++) {
					var newtd = FCKTools.GetElementDocument(cell)
							.createElement("td");
					if (FCKBrowserInfo.IsGeckoLike)
						FCKTools.AppendBogusBr(newtd);
					var cellrowSpan = isNaN(arr[i][j].rowSpan) ? 1
							: arr[i][j].rowSpan;
					// 需要推敲！！
					if (cellrowSpan > 1 && (i - 1) < 0) {
						newtablearr[newrowindex][j] = newtd;
					} else if (cellrowSpan > 1 && (arr[i - 1][j] == arr[i][j])) {
						newtablearr[newrowindex][j] = arr[i][j];
					} else {
						newtablearr[newrowindex][j] = newtd;
					}

					newtablearr[newrowindex + 1][j] = arr[i][j];

				}
				newrowindex += 2;

			}

		} else {// 下插入
			if (i != cellLastRCindex[0]) {

				for (j = 0; j < arr[i].length; j++) {
					newtablearr[newrowindex][j] = arr[i][j];
				}
				newrowindex++;
			} else {
				newtablearr[newrowindex + 1] = [];

				for (j = 0; j < arr[i].length; j++) {
					var newtd = FCKTools.GetElementDocument(cell)
							.createElement("td");
					if (FCKBrowserInfo.IsGeckoLike)
						FCKTools.AppendBogusBr(newtd);
					var cellrowSpan = isNaN(arr[i][j].rowSpan) ? 1
							: arr[i][j].rowSpan;
					// 需要推敲！！
					newtablearr[newrowindex][j] = arr[i][j];
					if (cellrowSpan > 1 && (i + 1) > (arr.length - 1)) {
						newtablearr[newrowindex + 1][j] = newtd;
					} else if (cellrowSpan > 1 && (arr[i + 1][j] == arr[i][j])) {
						newtablearr[newrowindex + 1][j] = arr[i][j];
					} else {
						newtablearr[newrowindex + 1][j] = newtd;
					}

				}
				newrowindex += 2;

			}

		}

	}

	this._InstallTableMap(newtablearr, oCells[0].parentNode.parentNode);
};
/**
 * add by diyanliang 11-4-18
 */
FCKTableHandler.InsertLastColumn = function(A) {
	FCKUndo.SaveUndoStep();
	// 建立虚拟表

	var otbody = A.getElementsByTagName("tbody");

	if (otbody) {

		A = otbody[0];
	}
	var arr = this._CreateTableMap(A);
	// 选择的单元格的虚拟表坐标

	// 重复的最后一个
	var LasRow = arr[arr.length - 1];
	var cellLastRCindex = [ arr.length - 1, LasRow.length - 1 ];
	C = LasRow[LasRow.length - 1];
	if (C && C.length) {
		B = C[A ? 0 : (C.length - 1)];
	} else {

		B = C;
	}

	var newtablearr = [];
	for ( var i = 0; i < arr.length; i++) {
		if (!newtablearr[i]) {
			newtablearr[i] = [];
		}
		var newcellindex = 0;
		var j = 0;

		// 右插入得到虚拟表被选中的单元格数组下标时候是最后一个
		// cellRCindex=testgetLastArrIndex(arr,B);
		while (j < arr[i].length) {
			var cellcolSpan = isNaN(arr[i][j].colSpan) ? 1 : arr[i][j].colSpan;

			if (j != cellLastRCindex[1]) {
				newtablearr[i][newcellindex] = arr[i][j];
				newcellindex++;
				j++;
			} else {
				newtablearr[i][newcellindex] = arr[i][j];
				var newtd = FCKTools.GetElementDocument(B).createElement("td");
				newtd.width = "20";
				if (FCKBrowserInfo.IsGeckoLike)
					FCKTools.AppendBogusBr(newtd);
				if (cellcolSpan > 1 && (arr[i][j + 1] == arr[i][j])) {// 如果原位置的格子colSpan>1本位置增加的就用原格子
					newtablearr[i][newcellindex + 1] = arr[i][j];
				} else {
					newtablearr[i][newcellindex + 1] = newtd;
				}
				newcellindex += 2;
				j++;
			}
		}
	}
	this._InstallTableMap(newtablearr, otbody[0]);

};
/**
 * add by diyanliang 11-4-18
 */
FCKTableHandler.InsertLastRow = function(A) {
	FCKUndo.SaveUndoStep();
	//	var D = A;
	// 建立虚拟表

	var otbody = A.getElementsByTagName("tbody");

	if (otbody) {
		A = otbody[0];
	}
	var arr = this._CreateTableMap(A);
	// 选择的单元格的虚拟表坐标

	// 重复的最后一个
	var LasRow = arr[arr.length - 1];
	var cellLastRCindex = [ arr.length - 1, LasRow.length - 1 ];
	C = LasRow[LasRow.length - 1];
	if (C && C.length) {
		B = C[A ? 0 : (C.length - 1)];
	} else {

		B = C;
	}

	var newtablearr = [];
	var newrowindex = 0;
	for ( var i = 0; i < arr.length; i++) {
		if (!newtablearr[newrowindex]) {
			newtablearr[newrowindex] = [];
		}

		if (i != cellLastRCindex[0]) {

			for ( var j = 0; j < arr[i].length; j++) {
				newtablearr[newrowindex][j] = arr[i][j];
			}
			newrowindex++;
		} else {
			newtablearr[newrowindex + 1] = [];

			for ( var j = 0; j < arr[i].length; j++) {
				var newtd = FCKTools.GetElementDocument(B).createElement("td");
				if (FCKBrowserInfo.IsGeckoLike)
					FCKTools.AppendBogusBr(newtd);
				var cellrowSpan = isNaN(arr[i][j].rowSpan) ? 1
						: arr[i][j].rowSpan;
				// 需要推敲！！
				newtablearr[newrowindex][j] = arr[i][j];
				if (cellrowSpan > 1 && (i + 1) > (arr.length - 1)) {
					newtablearr[newrowindex + 1][j] = newtd;
				} else if (cellrowSpan > 1 && (arr[i + 1][j] == arr[i][j])) {
					newtablearr[newrowindex + 1][j] = arr[i][j];
				} else {
					newtablearr[newrowindex + 1][j] = newtd;
				}

			}
			newrowindex += 2;

		}

	}

	this._InstallTableMap(newtablearr, otbody[0]);

};

// 删除行
FCKTableHandler.DeleteRows = function(row) {
	// If no row has been passed as a parameter,
	// then get the row( s ) containing the cells where the selection is placed
	// in.
	// If user selected multiple rows ( by selecting multiple cells ), walk
	// the selected cell list and delete the rows containing the selected cells
	if (!row) {
		var aCells = FCKTableHandler.GetSelectedCells();
		var aRowsToDelete = [];
		// queue up the rows -- it's possible ( and likely ) that we may get
		// duplicates
		for ( var i = 0; i < aCells.length; i++) {
			var oRow = FCKTools.GetElementAscensor(aCells[i], 'TR');
			// edit by diyanliang
			var needTranType = [];
			needTranType[0] = oRow;
			needTranType[1] = aCells[i];
			aRowsToDelete[oRow.rowIndex] = needTranType;
		}
		for ( var i = aRowsToDelete.length; i >= 0; i--) {
			if (aRowsToDelete[i])
				FCKTableHandler.DeleteRows(aRowsToDelete[i]);
		}
		return;
	}

	// edit by diyanliang 2008-09-24
	// 建立虚拟表
	var arr = this._CreateTableMap(row[0].parentNode);
	// 选择的单元格的虚拟表坐标
	var cellRCindex = testgetArrIndex(arr, row[1]);
	var newtablearr = [];
	// 删除行
	i = 0;
	newrowindex = 0;

	while (i < arr.length) {
		if (!newtablearr[newrowindex]) {
			newtablearr[newrowindex] = [];
		}

		if (i == cellRCindex[0]) {
			var neei = isNaN(row[1].rowSpan) ? 1 : row[1].rowSpan;
			i += neei;

		} else {
			newtablearr[newrowindex] = arr[i];
			newrowindex++;
			i++;
		}
	}
	if (newtablearr.length == 1 && newtablearr[0].length == 0) {
		var oTable = FCKTools.GetElementAscensor(row[0], "TABLE");
		FCKTableHandler.DeleteTable(oTable);
	} else {
		this._InstallTableMap(newtablearr, row[0].parentNode);
	}
};

FCKTableHandler.DeleteTable = function(table) {
	// If no table has been passed as a parameter,
	// then get the table where the selection is placed in.
	if (!table) {
		table = FCKSelection.GetSelectedElement();
		if (!table || table.tagName != 'TABLE')
			table = FCKSelection.MoveToAncestorNode('TABLE');
	}
	if (!table)
		return;

	// Delete the table.
	FCKSelection.SelectNode(table);
	FCKSelection.Collapse();

	// if the table is wrapped with a singleton <p> ( or something similar ),
	// remove
	// the surrounding tag -- which likely won't show after deletion anyway
	if (table.parentNode.childNodes.length == 1)
		table.parentNode.parentNode.removeChild(table.parentNode);
	else
		table.parentNode.removeChild(table);
};

// 插入列
FCKTableHandler.InsertColumn = function(insertBefore) {
	// Get the cell where the selection is placed in.
	var oCell = null;
	var nodes = this.GetSelectedCells();

	if (nodes && nodes.length)
		oCell = nodes[insertBefore ? 0 : (nodes.length - 1)];

	if (!oCell)
		return;

	// Get the cell's table.
	//	var oTable = FCKTools.GetElementAscensor(oCell, 'TABLE');

	// 建立虚拟表
	var arr = this._CreateTableMap(oCell.parentNode.parentNode);
	// testshowTable(arr);
	// 选择的单元格的虚拟表坐标
	// 重复的第一个
	var cellRCindex = testgetArrIndex(arr, oCell);
	// 重复的最后一个
	var cellLastRCindex = testgetLastArrIndex(arr, oCell);
	var newtablearr = [];

	for ( var i = 0; i < arr.length; i++) {
		if (!newtablearr[i]) {
			newtablearr[i] = [];
		}
		var newcellindex = 0;
		var j = 0;
		if (insertBefore) {// 左插入
			while (j < arr[i].length) {
				var cellcolSpan = isNaN(arr[i][j].colSpan) ? 1
						: arr[i][j].colSpan;
				//				var cellrowSpan = isNaN(arr[i][j].rowSpan) ? 1
				//						: arr[i][j].rowSpan;

				if (j != cellRCindex[1]) {
					newtablearr[i][newcellindex] = arr[i][j];
					newcellindex++;
					j++;
				} else {
					var newtd = FCKTools.GetElementDocument(oCell).createElement(
							"td");
					if (FCKBrowserInfo.IsGeckoLike)
						FCKTools.AppendBogusBr(newtd);
					if (cellcolSpan > 1 && (arr[i][j - 1] == arr[i][j])) {// 如果原位置的格子colSpan>1本位置增加的就用原格子
						newtablearr[i][newcellindex] = arr[i][j];
					} else {
						newtablearr[i][newcellindex] = newtd;
					}
					newtablearr[i][newcellindex + 1] = arr[i][j];
					newcellindex += 2;
					j++;

				}

			}
		} else {// 右插入
			// 右插入得到虚拟表被选中的单元格数组下标时候是最后一个
			// cellRCindex=testgetLastArrIndex(arr,B);
			while (j < arr[i].length) {
				var cellcolSpan = isNaN(arr[i][j].colSpan) ? 1
						: arr[i][j].colSpan;

				if (j != cellLastRCindex[1]) {
					newtablearr[i][newcellindex] = arr[i][j];
					newcellindex++;
					j++;
				} else {
					newtablearr[i][newcellindex] = arr[i][j];
					var newtd = FCKTools.GetElementDocument(oCell).createElement(
							"td");
					if (FCKBrowserInfo.IsGeckoLike)
						FCKTools.AppendBogusBr(newtd);

					if (cellcolSpan > 1 && (arr[i][j + 1] == arr[i][j])) {// 如果原位置的格子colSpan>1本位置增加的就用原格子
						newtablearr[i][newcellindex + 1] = arr[i][j];
					} else {
						newtablearr[i][newcellindex + 1] = newtd;
					}
					newcellindex += 2;
					j++;

				}

			}

		}

	}
	this._InstallTableMap(newtablearr, nodes[0].parentNode.parentNode);
};

// 得到选择的单元格在虚拟表中的数组下标(如果有合并就是合并中第一个)
testgetArrIndex = function(arr, incell) {
	for ( var i = 0; i < arr.length; i++) {
		for ( var j = 0; j < arr[i].length; j++) {
			if (arr[i][j].cellIndex == incell.cellIndex
					&& arr[i][j].parentNode.rowIndex == incell.parentNode.rowIndex) {
				return [ i, j ];
			}
		}
	}
};

// 得到选择的单元格在虚拟表中的数组下标(如果有合并就是合并中最后一个)
testgetLastArrIndex = function(arr, incell) {
	var reArr = null;
	for ( var i = 0; i < arr.length; i++) {
		for ( var j = 0; j < arr[i].length; j++) {
			if (arr[i][j].cellIndex == incell.cellIndex
					&& arr[i][j].parentNode.rowIndex == incell.parentNode.rowIndex) {
				reArr = [ i, j ];
			}
		}
	}
	return reArr;
};

// 删除列
FCKTableHandler.DeleteColumns = function(oCell) {
	// if user selected multiple cols ( by selecting multiple cells ), walk
	// the selected cell list and delete the rows containing the selected cells
	if (!oCell) {
		var aColsToDelete = FCKTableHandler.GetSelectedCells();
		for ( var i = aColsToDelete.length; i >= 0; i--) {
			if (aColsToDelete[i])
				FCKTableHandler.DeleteColumns(aColsToDelete[i]);
		}
		return;
	}

	if (!oCell)
		return;

	// edit by diyanliang 20080923
	var arr = this._CreateTableMap(oCell.parentNode.parentNode);
	// 选择的单元格的虚拟表坐标
	var cellRCindex = testgetArrIndex(arr, oCell);
	var newtablearr = [];
	// 循环行
	for (i = 0; i < arr.length; i++) {
		if (!newtablearr[i]) {
			newtablearr[i] = [];
		}
		j = 0;
		var newcellindex = 0;
		// 循环列
		while (j < arr[i].length) {
			// 要删除的列不存新表
			// 如果选择的列有colspan那这2列都不存
			if (j == cellRCindex[1]) {
				var neej = isNaN(oCell.colSpan) ? 1 : oCell.colSpan;
				j += neej;
			} else {
				newtablearr[i][newcellindex] = arr[i][j];
				newcellindex++;
				j++;
			}
		}

	}

	this._InstallTableMap(newtablearr, oCell.parentNode.parentNode);

	// endedit
};

FCKTableHandler.InsertCell = function(cell, insertBefore) {
	// Get the cell where the selection is placed in.
	var oCell = null;
	var nodes = this.GetSelectedCells();
	if (nodes && nodes.length)
		oCell = nodes[insertBefore ? 0 : (nodes.length - 1)];
	if (!oCell)
		return null;

	// Create the new cell element to be added.
	var oNewCell = FCK.EditorDocument.createElement('TD');
	if (FCKBrowserInfo.IsGeckoLike)
		FCKTools.AppendBogusBr(oNewCell);

	if (!insertBefore && oCell.cellIndex == oCell.parentNode.cells.length - 1)
		oCell.parentNode.appendChild(oNewCell);
	else
		oCell.parentNode.insertBefore(oNewCell, insertBefore ? oCell
				: oCell.nextSibling);

	return oNewCell;
};

FCKTableHandler.DeleteCell = function(cell) {
	// If this is the last cell in the row.
	if (cell.parentNode.cells.length == 1) {
		// Delete the entire row.
		FCKTableHandler.DeleteRows(FCKTools.GetElementAscensor(cell, 'TR'));
		return;
	}

	// Delete the cell from the row.
	cell.parentNode.removeChild(cell);
};

FCKTableHandler.DeleteCells = function() {
	var aCells = FCKTableHandler.GetSelectedCells();

	for ( var i = aCells.length - 1; i >= 0; i--) {
		FCKTableHandler.DeleteCell(aCells[i]);
	}
};

FCKTableHandler._MarkCells = function(cells, label) {
	for ( var i = 0; i < cells.length; i++)
		cells[i][label] = true;
};

FCKTableHandler._UnmarkCells = function(cells, label) {
	for ( var i = 0; i < cells.length; i++) {
		if (FCKBrowserInfo.IsIE)
			cells[i].removeAttribute(label);
		else
			delete cells[i][label];
	}
};

FCKTableHandler._ReplaceCellsByMarker = function(tableMap, marker, substitute) {
	for ( var i = 0; i < tableMap.length; i++) {
		for ( var j = 0; j < tableMap[i].length; j++) {
			if (tableMap[i][j][marker])
				tableMap[i][j] = substitute;
		}
	}
};

FCKTableHandler._GetMarkerGeometry = function(tableMap, rowIdx, colIdx,
		markerName) {
	var selectionWidth = 0;
	var selectionHeight = 0;
	var cellsLeft = 0;
	var cellsUp = 0;
	for ( var i = colIdx; tableMap[rowIdx][i]
			&& tableMap[rowIdx][i][markerName]; i++)
		selectionWidth++;
	for ( var i = colIdx - 1; tableMap[rowIdx][i]
			&& tableMap[rowIdx][i][markerName]; i--) {
		selectionWidth++;
		cellsLeft++;
	}
	for ( var i = rowIdx; tableMap[i] && tableMap[i][colIdx]
			&& tableMap[i][colIdx][markerName]; i++)
		selectionHeight++;
	for ( var i = rowIdx - 1; tableMap[i] && tableMap[i][colIdx]
			&& tableMap[i][colIdx][markerName]; i--) {
		selectionHeight++;
		cellsUp++;
	}
	return {
		'width' : selectionWidth,
		'height' : selectionHeight,
		'x' : cellsLeft,
		'y' : cellsUp
	};
};

FCKTableHandler.CheckIsSelectionRectangular = function() {
	// If every row and column in an area on a plane are of the same width and
	// height,
	// Then the area is a rectangle.
	var cells = FCKTableHandler.GetSelectedCells();
	if (cells.length < 1)
		return false;

	this._MarkCells(cells, '_CellSelected');

	var tableMap = this._CreateTableMap(cells[0].parentNode.parentNode);
	var rowIdx = cells[0].parentNode.rowIndex;
	var colIdx = this._GetCellIndexSpan(tableMap, rowIdx, cells[0]);

	var geometry = this._GetMarkerGeometry(tableMap, rowIdx, colIdx,
			'_CellSelected');
	var baseColIdx = colIdx - geometry.x;
	var baseRowIdx = rowIdx - geometry.y;

	if (geometry.width >= geometry.height) {
		for (colIdx = baseColIdx; colIdx < baseColIdx + geometry.width; colIdx++) {
			rowIdx = baseRowIdx + (colIdx - baseColIdx) % geometry.height;
			if (!tableMap[rowIdx] || !tableMap[rowIdx][colIdx]) {
				this._UnmarkCells(cells, '_CellSelected');
				return false;
			}
			var g = this._GetMarkerGeometry(tableMap, rowIdx, colIdx,
					'_CellSelected');
			if (g.width != geometry.width || g.height != geometry.height) {
				this._UnmarkCells(cells, '_CellSelected');
				return false;
			}
		}
	} else {
		for (rowIdx = baseRowIdx; rowIdx < baseRowIdx + geometry.height; rowIdx++) {
			colIdx = baseColIdx + (rowIdx - baseRowIdx) % geometry.width;
			if (!tableMap[rowIdx] || !tableMap[rowIdx][colIdx]) {
				this._UnmarkCells(cells, '_CellSelected');
				return false;
			}
			var g = this._GetMarkerGeometry(tableMap, rowIdx, colIdx,
					'_CellSelected');
			if (g.width != geometry.width || g.height != geometry.height) {
				this._UnmarkCells(cells, '_CellSelected');
				return false;
			}
		}
	}

	this._UnmarkCells(cells, '_CellSelected');
	return true;
};

FCKTableHandler.MergeCells = function() {
	// Get all selected cells.
	var cells = this.GetSelectedCells();
	if (cells.length < 2)
		return;

	// Assume the selected cells are already in a rectangular geometry.
	// Because the checking is already done by FCKTableCommand.
	var refCell = cells[0];
	var tableMap = this._CreateTableMap(refCell.parentNode.parentNode);
	var rowIdx = refCell.parentNode.rowIndex;
	var colIdx = this._GetCellIndexSpan(tableMap, rowIdx, refCell);

	this._MarkCells(cells, '_SelectedCells');
	var selectionGeometry = this._GetMarkerGeometry(tableMap, rowIdx, colIdx,
			'_SelectedCells');

	var baseColIdx = colIdx - selectionGeometry.x;
	var baseRowIdx = rowIdx - selectionGeometry.y;
	var cellContents = FCKTools.GetElementDocument(refCell)
			.createDocumentFragment();
	for ( var i = 0; i < selectionGeometry.height; i++) {
		var rowChildNodesCount = 0;
		for ( var j = 0; j < selectionGeometry.width; j++) {
			var currentCell = tableMap[baseRowIdx + i][baseColIdx + j];
			while (currentCell.childNodes.length > 0) {
				var node = currentCell.removeChild(currentCell.firstChild);
				if (node.nodeType != 1
						|| (node.getAttribute('type', 2) != '_moz' && node
								.getAttribute('_moz_dirty') != null)) {
					cellContents.appendChild(node);
					rowChildNodesCount++;
				}
			}
		}
		if (rowChildNodesCount > 0)
			cellContents.appendChild(FCKTools.GetElementDocument(refCell)
					.createElement('br'));
	}

	this._ReplaceCellsByMarker(tableMap, '_SelectedCells', refCell);
	this._UnmarkCells(cells, '_SelectedCells');
	this._InstallTableMap(tableMap, refCell.parentNode.parentNode);
	refCell.appendChild(cellContents);

	if (FCKBrowserInfo.IsGeckoLike && (!refCell.firstChild))
		FCKTools.AppendBogusBr(refCell);

	this._MoveCaretToCell(refCell, false);
};

FCKTableHandler.MergeRight = function() {
	var target = this.GetMergeRightTarget();
	if (target == null)
		return;
	var refCell = target.refCell;
	var tableMap = target.tableMap;
	var nextCell = target.nextCell;

	var cellContents = FCK.EditorDocument.createDocumentFragment();
	while (nextCell && nextCell.childNodes && nextCell.childNodes.length > 0)
		cellContents.appendChild(nextCell.removeChild(nextCell.firstChild));

	nextCell.parentNode.removeChild(nextCell);
	refCell.appendChild(cellContents);
	this._MarkCells([ nextCell ], '_Replace');
	this._ReplaceCellsByMarker(tableMap, '_Replace', refCell);
	this._InstallTableMap(tableMap, refCell.parentNode.parentNode);

	this._MoveCaretToCell(refCell, false);
};

FCKTableHandler.MergeDown = function() {
	var target = this.GetMergeDownTarget();
	if (target == null)
		return;
	var refCell = target.refCell;
	var tableMap = target.tableMap;
	var nextCell = target.nextCell;

	var cellContents = FCKTools.GetElementDocument(refCell)
			.createDocumentFragment();
	while (nextCell && nextCell.childNodes && nextCell.childNodes.length > 0)
		cellContents.appendChild(nextCell.removeChild(nextCell.firstChild));
	if (cellContents.firstChild)
		cellContents.insertBefore(FCKTools.GetElementDocument(nextCell)
				.createElement('br'), cellContents.firstChild);
	refCell.appendChild(cellContents);
	this._MarkCells([ nextCell ], '_Replace');
	this._ReplaceCellsByMarker(tableMap, '_Replace', refCell);
	this._InstallTableMap(tableMap, refCell.parentNode.parentNode);

	this._MoveCaretToCell(refCell, false);
};

// 横拆单元格
FCKTableHandler.HorizontalSplitCell = function() {
	var cells = FCKTableHandler.GetSelectedCells();
	if (cells.length != 1)
		return;

	var refCell = cells[0];
	// add by diyanliang 20080908 解决了表格先纵拆再横拆出错的问题
	var Browspan = refCell.rowSpan;
	// end
	var tableMap = this._CreateTableMap(refCell.parentNode.parentNode);
	var rowIdx = refCell.parentNode.rowIndex;
	var colIdx = FCKTableHandler._GetCellIndexSpan(tableMap, rowIdx, refCell);
	var cellSpan = isNaN(refCell.colSpan) ? 1 : refCell.colSpan;

	if (cellSpan > 1) {
		// Splittng a multi-column cell - original cell gets ceil(colSpan/2)
		// columns,
		// new cell gets floor(colSpan/2).
		var newCellSpan = Math.ceil(cellSpan / 2);
		var newCell = FCKTools.GetElementDocument(refCell).createElement('td');
		if (FCKBrowserInfo.IsGeckoLike)
			FCKTools.AppendBogusBr(newCell);
		var startIdx = colIdx + newCellSpan;
		var endIdx = colIdx + cellSpan;
		var rowSpan = isNaN(refCell.rowSpan) ? 1 : refCell.rowSpan;
		for ( var r = rowIdx; r < rowIdx + rowSpan; r++) {
			for ( var i = startIdx; i < endIdx; i++)
				tableMap[r][i] = newCell;
		}
	} else {
		// Splitting a single-column cell - add a new cell, and expand
		// cells crossing the same column.
		var nTdType=1; 
		var newTableMap = [];
		for ( var i = 0; i < tableMap.length; i++) {
			var newRow = tableMap[i].slice(0, colIdx);
			if (tableMap[i].length <= colIdx) {
				newTableMap.push(newRow);
				continue;
			}
			if (tableMap[i][colIdx] == refCell) {
				newRow.push(refCell);
				// edit by diyanliang 20080909

				var NTD = FCKTools.GetElementDocument(refCell).createElement("td");
				// if(nTdType!=0)
				newRow.push(NTD);

				// end
				if (FCKBrowserInfo.IsGeckoLike)
					FCKTools.AppendBogusBr(newRow[newRow.length - 1]);

				if (Browspan > 1) {
					// alert("Browspan>1");
					newRow[newRow.length - 1]._dirowspan = Browspan;
					newRow[newRow.length - 1].nTdType = nTdType;
					// alert("M[M.length - 1]._dirowspan="+M[M.length -
					// 1]._dirowspan);
					// alert("M[M.length - 1]="+M[M.length - 1].outerHTML);
					nTdType = 0;
				}
			} else {
				newRow.push(tableMap[i][colIdx]);
				newRow.push(tableMap[i][colIdx]);
			}
			for ( var j = colIdx + 1; j < tableMap[i].length; j++)
				newRow.push(tableMap[i][j]);
			newTableMap.push(newRow);
		}

		tableMap = newTableMap;
	}

	this._InstallTableMap(tableMap, refCell.parentNode.parentNode);
};

// 纵拆单元格
FCKTableHandler.VerticalSplitCell = function() {
	var cells = FCKTableHandler.GetSelectedCells();
	if (cells.length != 1)
		return;

	var currentCell = cells[0];
	var tableMap = this._CreateTableMap(currentCell.parentNode.parentNode);
	var cellIndex = FCKTableHandler._GetCellIndexSpan(tableMap,
			currentCell.parentNode.rowIndex, currentCell);
	var currentRowSpan = currentCell.rowSpan;
	// add by diyanliang 20080908 解决了表格先横拆再纵拆出错的问题
	var Bcolspan = currentCell.colSpan;
	// end
	var currentRowIndex = currentCell.parentNode.rowIndex;
	if (isNaN(currentRowSpan))
		currentRowSpan = 1;

	if (currentRowSpan > 1) {
		// 1. Set the current cell's rowSpan to 1.
		currentCell.rowSpan = Math.ceil(currentRowSpan / 2);

		// 2. Find the appropriate place to insert a new cell at the next row.
		var newCellRowIndex = currentRowIndex + Math.ceil(currentRowSpan / 2);
		var insertMarker = null;

		for ( var i = cellIndex + 1; i < tableMap[newCellRowIndex].length; i++) {
			if (tableMap[newCellRowIndex][i].parentNode.rowIndex == newCellRowIndex) {
				insertMarker = tableMap[newCellRowIndex][i];
				break;
			}
		}

		// 3. Insert the new cell to the indicated place, with the appropriate
		// rowSpan, next row.
		var newCell = FCK.EditorDocument.createElement('td');
		newCell.rowSpan = Math.floor(currentRowSpan / 2);
		if (FCKBrowserInfo.IsGeckoLike)
			FCKTools.AppendBogusBr(newCell);
		// add by diyanliang 200809008 解决了表格先横拆再纵拆出错的问题
		if (Bcolspan > 1) {
			newCell.colSpan = Bcolspan;
		}
		currentCell.parentNode.parentNode.rows[newCellRowIndex].insertBefore(
				newCell, insertMarker);
	} else {
		// 1. Insert a new row.
		// add by diyanliang 20080903 for last row verticalSplitCell
		var newCellRowIndex = currentRowIndex + 1;
		var newRow = FCK.EditorDocument.createElement('tr');

		var tableNode = currentCell.parentNode.parentNode;
		if (tableNode.rows.length > newCellRowIndex)
			tableNode.insertBefore(newRow, tableNode.rows[newCellRowIndex]);
		else
			tableNode.appendChild(newRow);
		// end

		// 2. +1 to rowSpan for all cells crossing currentCell's row.
		for ( var i = 0; i < tableMap[currentRowIndex].length;) {
			var colSpan = tableMap[currentRowIndex][i].colSpan;
			if (isNaN(colSpan) || colSpan < 1)
				colSpan = 1;
			if (i == cellIndex) {
				i += colSpan;
				continue;
			}

			var rowSpan = tableMap[currentRowIndex][i].rowSpan;
			if (isNaN(rowSpan))
				rowSpan = 1;
			tableMap[currentRowIndex][i].rowSpan = rowSpan + 1;
			i += colSpan;
		}

		// 3. Insert a new cell to new row.
		var newCell = FCK.EditorDocument.createElement('td');
		// add by diyanliang 200809008 解决了表格先横拆再纵拆出错的问题
		if (Bcolspan > 1) {
			newCell.colSpan = Bcolspan;
		}
		//end
		if (FCKBrowserInfo.IsGeckoLike)
			FCKTools.AppendBogusBr(newCell);
		newRow.appendChild(newCell);
	}
};

// edit by diyanliang 20080904

// Get the cell index from a TableMap.
FCKTableHandler._GetCellIndexSpan = function(tableMap, rowIndex, cell) {
	if (tableMap.length < rowIndex + 1)
		return null;

	var oRow = tableMap[rowIndex];

	for ( var c = 0; c < oRow.length; c++) {
		if (oRow[c] == cell)
			return c;
	}

	return null;
};

// Get the cell location from a TableMap. Returns an array with an [x,y]
// location
FCKTableHandler._GetCellLocation = function(tableMap, cell) {
	for ( var i = 0; i < tableMap.length; i++) {
		for ( var c = 0; c < tableMap[i].length; c++) {
			if (tableMap[i][c] == cell)
				return [ i, c ];
		}
	}
	return null;
};

// Get the cells available in a column of a TableMap.
FCKTableHandler._GetColumnCells = function(tableMap, columnIndex) {
	var aCollCells = new Array();

	for ( var r = 0; r < tableMap.length; r++) {
		var oCell = tableMap[r][columnIndex];
		if (oCell
				&& (aCollCells.length == 0 || aCollCells[aCollCells.length - 1] != oCell))
			aCollCells[aCollCells.length] = oCell;
	}

	return aCollCells;
};

// This function is quite hard to explain. It creates a matrix representing all
// cells in a table.
// The difference here is that the "spanned" cells (colSpan and rowSpan) are
// duplicated on the matrix
// cells that are "spanned". For example, a row with 3 cells where the second
// cell has colSpan=2 and rowSpan=3
// will produce a bi-dimensional matrix with the following values (representing
// the cells):
// Cell1, Cell2, Cell2, Cell 3
// Cell4, Cell2, Cell2, Cell 5
FCKTableHandler._CreateTableMap = function(table) {
	var aRows = table.rows;

	// Row and Column counters.
	var r = -1;

	var aMap = [];

	for ( var i = 0; i < aRows.length; i++) {
		r++;
		if (!aMap[r])
			aMap[r] = new Array();

		var c = -1;

		for ( var j = 0; j < aRows[i].cells.length; j++) {
			var oCell = aRows[i].cells[j];

			c++;
			while (aMap[r][c])
				c++;

			var iColSpan = isNaN(oCell.colSpan) ? 1 : oCell.colSpan;
			var iRowSpan = isNaN(oCell.rowSpan) ? 1 : oCell.rowSpan;

			for ( var rs = 0; rs < iRowSpan; rs++) {
				if (!aMap[r + rs])
					aMap[r + rs] = new Array();

				for ( var cs = 0; cs < iColSpan; cs++) {
					aMap[r + rs][c + cs] = aRows[i].cells[j];
				}
			}

			c += iColSpan - 1;
		}
	}
	return aMap;
};

// edit by diyanliang main src is here 20080904
// This function is the inverse of _CreateTableMap - it takes in a table map and
// converts it to an HTML table.
FCKTableHandler._InstallTableMap = function(tableMap, table) {
	var attr = FCKBrowserInfo.IsIE ? "_fckrowspan" : "rowSpan";
	// Clear the table of all rows first.
	while (table.rows.length > 0) {
		var row = table.rows[0];
		row.parentNode.removeChild(row);
	}

	// Disconnect all the cells in tableMap from their parents, set all colSpan
	// and rowSpan attributes to 1.
	for ( var i = 0; i < tableMap.length; i++) {
		for ( var j = 0; j < tableMap[i].length; j++) {
			var cell = tableMap[i][j];
			if (cell.parentNode)
				cell.parentNode.removeChild(cell);
			cell.colSpan = cell[attr] = 1;
			// ad by diyanliang 20080909

			if (!isNaN(cell["_dirowspan"])) {

				cell[attr] = cell["_dirowspan"];
				cell.colSpan = 1;
			}
			// end
		}
	}

	// Scan by rows and set colSpan.
	var maxCol = 0;

	for ( var i = 0; i < tableMap.length; i++) {
		for ( var j = 0; j < tableMap[i].length; j++) {
			var cell = tableMap[i][j];
			if (!cell)
				continue;
			if (j > maxCol)
				maxCol = j;
			if (cell._colScanned === true)
				continue;
			if (tableMap[i][j - 1] == cell)
				cell.colSpan++;
			if (tableMap[i][j + 1] != cell)
				cell._colScanned = true;
		}
	}

	// Scan by columns and set rowSpan.
	for ( var i = 0; i <= maxCol; i++) {
		for ( var j = 0; j < tableMap.length; j++) {
			if (!tableMap[j])
				continue;
			var cell = tableMap[j][i];
			if (!cell || cell._rowScanned === true)
				continue;
			if (tableMap[j - 1] && tableMap[j - 1][i] == cell)
				cell[attr]++;
			if (!tableMap[j + 1] || tableMap[j + 1][i] != cell)
				cell._rowScanned = true;
		}
	}

	// Clear all temporary flags.
	for ( var i = 0; i < tableMap.length; i++) {
		for ( var j = 0; j < tableMap[i].length; j++) {
			var cell = tableMap[i][j];
			FCKDomTools.RemoveAttr(cell, '_colScanned');
			FCKDomTools.RemoveAttr(cell, '_rowScanned');
		}
	}

	// Insert physical rows and columns to the table.
	for ( var i = 0; i < tableMap.length; i++) {
		var rowObj = FCKTools.GetElementDocument(table).createElement('tr');
		for ( var j = 0; j < tableMap[i].length;) {
			var cell = tableMap[i][j];
			if (tableMap[i - 1] && tableMap[i - 1][j] == cell) {
				j += cell.colSpan;
				continue;
			}
			if (cell["nTdType"] != 0)
				rowObj.appendChild(cell);

			if (attr != "rowSpan") {
				// add by diyanliang
				if (!isNaN(cell[attr])) {
					// end
					cell.rowSpan = cell[attr];
					cell["_dirowspan"] = 1;
					FCKDomTools.RemoveAttr(cell, '_dirowspan');
					FCKDomTools.RemoveAttr(cell, attr);
					// add by diyanliang
				}// end
			}

			j += cell.colSpan;
			if (cell.colSpan == 1)
				FCKDomTools.RemoveAttr(cell,'colspan');
			if (cell.rowSpan == 1)
				FCKDomTools.RemoveAttr(cell,'rowspan');
		}
		// add by diyanliang
		if (tableMap[i].length != 0)// end
			table.appendChild(rowObj);
	}
	FCK.ReDrawTable(table);
};

FCKTableHandler._MoveCaretToCell = function(refCell, toStart) {
	var range = new FCKDomRange(FCK.EditorWindow);
	range.MoveToNodeContents(refCell);
	range.Collapse(toStart);
	range.Select();
};

FCKTableHandler.ClearRow = function(tr) {
	// Get the array of row's cells.
	var aCells = tr.cells;

	// Replace the contents of each cell with "nothing".
	for ( var i = 0; i < aCells.length; i++) {
		aCells[i].innerHTML = '';

		if (FCKBrowserInfo.IsGeckoLike)
			FCKTools.AppendBogusBr(aCells[i]);
	}
};

FCKTableHandler.GetMergeRightTarget = function() {
	var cells = this.GetSelectedCells();
	if (cells.length != 1)
		return null;

	var refCell = cells[0];
	var tableMap = this._CreateTableMap(refCell.parentNode.parentNode);
	var rowIdx = refCell.parentNode.rowIndex;
	var colIdx = this._GetCellIndexSpan(tableMap, rowIdx, refCell);
	var nextColIdx = colIdx + (isNaN(refCell.colSpan) ? 1 : refCell.colSpan);
	var nextCell = tableMap[rowIdx][nextColIdx];

	if (!nextCell)
		return null;

	// The two cells must have the same vertical geometry, otherwise merging
	// does not make sense.
	this._MarkCells([ refCell, nextCell ], '_SizeTest');
	var refGeometry = this._GetMarkerGeometry(tableMap, rowIdx, colIdx,
			'_SizeTest');
	var nextGeometry = this._GetMarkerGeometry(tableMap, rowIdx, nextColIdx,
			'_SizeTest');
	this._UnmarkCells([ refCell, nextCell ], '_SizeTest');

	if (refGeometry.height != nextGeometry.height
			|| refGeometry.y != nextGeometry.y)
		return null;

	return {
		'refCell' : refCell,
		'nextCell' : nextCell,
		'tableMap' : tableMap
	};
};

FCKTableHandler.GetMergeDownTarget = function() {
	var cells = this.GetSelectedCells();
	if (cells.length != 1)
		return null;

	var refCell = cells[0];
	var tableMap = this._CreateTableMap(refCell.parentNode.parentNode);
	var rowIdx = refCell.parentNode.rowIndex;
	var colIdx = this._GetCellIndexSpan(tableMap, rowIdx, refCell);
	var newRowIdx = rowIdx + (isNaN(refCell.rowSpan) ? 1 : refCell.rowSpan);
	if (!tableMap[newRowIdx])
		return null;

	var nextCell = tableMap[newRowIdx][colIdx];

	if (!nextCell)
		return null;

	// The two cells must have the same horizontal geometry, otherwise merging
	// does not makes sense.
	this._MarkCells([ refCell, nextCell ], '_SizeTest');
	var refGeometry = this._GetMarkerGeometry(tableMap, rowIdx, colIdx,
			'_SizeTest');
	var nextGeometry = this._GetMarkerGeometry(tableMap, newRowIdx, colIdx,
			'_SizeTest');
	this._UnmarkCells([ refCell, nextCell ], '_SizeTest');

	if (refGeometry.width != nextGeometry.width
			|| refGeometry.x != nextGeometry.x)
		return null;

	return {
		'refCell' : refCell,
		'nextCell' : nextCell,
		'tableMap' : tableMap
	};
};

FCKTableHandler.GetSelectedCells = function() {
	var aCells = [];
	if (window.getSelection) {

		var oSelection = FCKSelection.GetSelection();

		// If the selection is a text.
		if (oSelection.rangeCount == 1 && oSelection.anchorNode.nodeType == 3) {
			var oParent = FCKTools.GetElementAscensor(oSelection.anchorNode,
					'TD,TH');

			if (oParent)
				aCells[0] = oParent;

			return aCells;
		}

		for ( var i = 0; i < oSelection.rangeCount; i++) {
			var oRange = oSelection.getRangeAt(i);
			var oCell;

			if (oRange.startContainer.tagName.Equals('TD', 'TH'))
				oCell = oRange.startContainer;
			else
				oCell = oRange.startContainer.childNodes[oRange.startOffset];

			if (oCell.tagName.Equals('TD', 'TH'))
				aCells[aCells.length] = oCell;
		}

		return aCells;
	} else {
		if (FCKSelection.GetType() == 'Control') {
			var td = FCKSelection.MoveToAncestorNode('TD');
			return td ? [ td ] : [];
		}

		var oRange = FCKSelection.GetSelectionRange();
		var oParent = FCKSelection.GetParentElement();

		if (oParent && oParent.tagName.Equals('TD', 'TH'))
			aCells[0] = oParent;
		else {
			oParent = FCKSelection.MoveToAncestorNode('TABLE');

			if (oParent) {
				// Loops throw all cells checking if the cell is, or part of it, is inside the selection
				// and then add it to the selected cells collection.
				for ( var i = 0; i < oParent.cells.length; i++) {
					var oCellRange = FCK.EditorDocument.body.createTextRange();
					oCellRange.moveToElementText(oParent.cells[i]);

					if (oRange.inRange(oCellRange)
							|| (oRange.compareEndPoints('StartToStart',
									oCellRange) >= 0 && oRange
									.compareEndPoints('StartToEnd', oCellRange) <= 0)
							|| (oRange.compareEndPoints('EndToStart',
									oCellRange) >= 0 && oRange
									.compareEndPoints('EndToEnd', oCellRange) <= 0)) {
						aCells[aCells.length] = oParent.cells[i];
					}
				}
			}
		}

		return aCells;
	}
};