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

package net.duckling.ddl.service.team;

import java.io.Serializable;

/**
 * Introduction Here.
 * 
 * @date 2010-5-8
 * @author Fred Zhang (fred@cnic.cn)
 */
public class TeamState implements Serializable {
	private static final long serialVersionUID = 1L;
	private String value;
	public static final TeamState WORK = new TeamState("work");
	public static final TeamState HANGUP = new TeamState("hangup");
	public static final TeamState UNINIT = new TeamState("uninit");

	private TeamState(String value) {
		this.value = value;
	};

	public String getValue() {
		return value;
	}
	
	public boolean equals(TeamState state){
		if(state==null){
			return false;
		}
		return this.getValue().equals(state.getValue());
	}

	public static TeamState valueOf(String value) {
		TeamState state = null;
		if ("work".equals(value)) {
			state = WORK;
		} else if ("hangup".equals(value)) {
			state = HANGUP;
		} else if ("uninit".equals(value)) {
			state = UNINIT;
		}
		return state;
	}
}
