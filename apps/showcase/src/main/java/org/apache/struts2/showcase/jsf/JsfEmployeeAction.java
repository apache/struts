/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.showcase.jsf;

import org.apache.struts2.showcase.action.EmployeeAction;
import org.apache.struts2.showcase.dao.SkillDao;
import org.apache.struts2.showcase.model.Employee;
import org.apache.struts2.showcase.model.Skill;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Overriding the EmployeeAction to main provide getters returning the data in
 * the form required by the JSF components
 */
public class JsfEmployeeAction extends EmployeeAction {

	private static final long serialVersionUID = 1L;

	@Autowired
	private SkillDao skillDao;

	/**
	 * Creating a default employee and main skill, since the JSF EL can't handle
	 * creating new objects as necessary
	 */
	public JsfEmployeeAction() {
		Employee e = new Employee();
		e.setMainSkill(new Skill());
		setCurrentEmployee(e);
	}


	/**
	 * Returning a List because the JSF dataGrid can't handle a Set for some
	 * reason
	 */
	@Override
	public Collection getAvailableItems() {
		return new ArrayList(super.getAvailableItems());
	}

	/**
	 * Changing the String array into a Map
	 */
	public Map<String, String> getAvailablePositionsAsMap() {
		Map<String, String> map = new LinkedHashMap<String, String>();
		for (String val : super.getAvailablePositions()) {
			map.put(val, val);
		}
		return map;
	}

	/**
	 * Converting the list into a map
	 */
	public Map getAvailableLevelsAsMap() {
		Map map = new LinkedHashMap();
		for (Object val : super.getAvailableLevels()) {
			map.put(val, val);
		}
		return map;
	}

	/**
	 * Converting the Skill object list into a map
	 */
	public Map<String, String> getAvailableSkills() {
		Map<String, String> map = new HashMap<String, String>();
		for (Object val : skillDao.findAll()) {
			Skill skill = (Skill) val;
			map.put(skill.getDescription(), skill.getName());
		}
		return map;
	}

	/**
	 * Gets the selected Skill objects as a list
	 */
	public List<String> getSelectedSkillsAsList() {
		System.out.println("asked for skills");
		List<String> list = new ArrayList<String>();
		List skills = super.getSelectedSkills();
		if (skills != null) {
			for (Object val : skills) {
				if (val instanceof Skill) {
					list.add(((Skill) val).getDescription());
				} else {
					Skill skill = skillDao.getSkill((String) val);
					list.add(skill.getDescription());
				}
			}
		}
		return list;
	}
}
