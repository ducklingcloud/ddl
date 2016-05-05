/*
 * Copyright (c) 2008-2016 Computer Network Information Center (CNIC), Chinese Academy of Sciences.
 * 
 * This file is part of Duckling project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 *
 */

package net.duckling.ddl.service.relaterec;

import java.util.List;



/**
 * @date 2011-8-30
 * @author Clive Lee
 */
public class DGridDisplay {
	 private DGrid grid;
	 private List<DGridItemDisplay> gridItemList;
	/**
	 * @return the grid
	 */
	public DGrid getGrid() {
		return grid;
	}
	/**
	 * @param grid the grid to set
	 */
	public void setGrid(DGrid grid) {
		this.grid = grid;
	}
	/**
	 * @return the gridItemList
	 */
	public List<DGridItemDisplay> getGridItemList() {
		return gridItemList;
	}
	/**
	 * @param gridItemList the gridItemList to set
	 */
	public void setGridItemList(List<DGridItemDisplay> gridItemList) {
		this.gridItemList = gridItemList;
	}
	 
	 
}
