function E2ContextMenu(nodeName){
	switch (nodeName) {
	  case "Table":
		return {AddItems:function (menu, tag, tagName) {
			var B = (tagName == "TABLE");
			var C = (!B && FCKSelection.HasAncestorNode("TABLE"));
			if (C) {
				//menu.AddSeparator();
				var D = menu;
				D.AddItem("TableCellProp", FCKLang.CellProperties, 57, FCKCommands.GetCommand("TableCellProp").GetState() == -1);
				if (FCKBrowserInfo.IsGecko) {
					D.AddItem("TableMergeCells", FCKLang.MergeCells, 60, FCKCommands.GetCommand("TableMergeCells").GetState() == -1);
				} else {
					D.AddItem("TableMergeRight", FCKLang.MergeRight, 60, FCKCommands.GetCommand("TableMergeRight").GetState() == -1);
					D.AddItem("TableMergeDown", FCKLang.MergeDown, 60, FCKCommands.GetCommand("TableMergeDown").GetState() == -1);
				}
				D.AddItem("TableHorizontalSplitCell", FCKLang.HorizontalSplitCell, 61, FCKCommands.GetCommand("TableHorizontalSplitCell").GetState() == -1);
				D.AddItem("TableVerticalSplitCell", FCKLang.VerticalSplitCell, 61, FCKCommands.GetCommand("TableVerticalSplitCell").GetState() == -1);
				D.AddSeparator();
				D.AddItem("TableInsertRowBefore", FCKLang.InsertRowBefore, 62);
			//	D.AddItem("TableInsertRowAfter", FCKLang.InsertRowAfter, 62);
				D.AddItem("TableDeleteRows", FCKLang.DeleteRows, 63);
				menu.AddSeparator();
				D.AddItem("TableInsertColumnBefore", FCKLang.InsertColumnBefore,64);
			//	D.AddItem("TableInsertColumnAfter", FCKLang.InsertColumnAfter, 64);
				D.AddItem("TableDeleteColumns", FCKLang.DeleteColumns, 65);
			}
			if (B || C) {
				menu.AddSeparator();
				menu.AddItem("TableDelete", FCKLang.TableDelete,48);
				menu.AddItem("TableProp", FCKLang.TableProperties, 39);
			}
		}};
	  case "Image":
			return {AddItems:function (menu, tag, tagName) {
				if (tagName == "IMG" && !tag.getAttribute("_fckfakelement")&&(tag.className).toUpperCase()!="PLUGIN") {
					
					menu.AddItem("Image", FCKLang.ImageProperties, 37);
					menu.AddSeparator();
				}
			}};

	}
	return null;
	
}