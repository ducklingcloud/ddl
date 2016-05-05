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
package net.duckling.ddl.service.resource.impl;

import java.util.Collection;
import java.util.List;

import net.duckling.ddl.service.resource.DShortcut;


public interface ShortcutDAO {

	public abstract boolean insert(final DShortcut instance);

	public abstract boolean updateSequece(final List<Integer> ids);

	public abstract boolean delete(int id);

	public abstract List<DShortcut> queryShortcut(int tid, int tgid);

	public abstract int getShortcutCount(int tid, int tgid);

	public abstract boolean update(final DShortcut instance);

	public abstract boolean update(final List<DShortcut> dcList);

	public abstract List<DShortcut> getShortcuts(Collection<Integer> ids);

	public abstract List<DShortcut> queryShortcut(int tid, Collection<Integer> ids);

	public abstract DShortcut getDSortcutById(int id);

}