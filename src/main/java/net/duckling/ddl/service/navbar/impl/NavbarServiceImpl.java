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
package net.duckling.ddl.service.navbar.impl;

import java.util.List;

import net.duckling.ddl.service.navbar.INavbarService;
import net.duckling.ddl.service.navbar.NavbarItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class NavbarServiceImpl implements INavbarService {

	@Autowired
	private NavbarItemDAO navbarItemDAO;
	
	/**
	 * @param navbarItemDAO the navbarItemDAO to set
	 */
	public void setNavbarItemDAO(NavbarItemDAO navbarItemDAO) {
		this.navbarItemDAO = navbarItemDAO;
	}

	@Override
	public List<NavbarItem> getNavbarItems(String uid, int tid) {
		return navbarItemDAO.getAllNavbarItemByUidTid(uid, tid);
	}

	@Override
	public int create(NavbarItem item) {
		return navbarItemDAO.create(item);
	}

	@Override
	public int delete(int id) {
		return navbarItemDAO.delete(id);
	}

}
