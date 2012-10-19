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
package org.apache.struts2.showcase.ajax.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class Category {
	private static Map<Long, Category> catMap = new HashMap<Long, Category>();

	static {
		new Category(1, "Root",
				new Category(2, "Java",
						new Category(3, "Web Frameworks",
								new Category(4, "Struts"),
								new Category(7, "Stripes"),
								new Category(8, "Rife")),
						new Category(9, "Persistence",
								new Category(10, "iBatis"),
								new Category(11, "Hibernate"),
								new Category(12, "JDO"),
								new Category(13, "JDBC"))),
				new Category(14, "JavaScript",
						new Category(15, "Dojo"),
						new Category(16, "Prototype"),
						new Category(17, "Scriptaculous"),
						new Category(18, "OpenRico"),
						new Category(19, "DWR")));
	}

	public static Category getById(long id) {
		return catMap.get(id);
	}

	private long id;
	private String name;
	private List<Category> children;
	private boolean toggle;

	public Category(long id, String name, Category... children) {
		this.id = id;
		this.name = name;
		this.children = new ArrayList<Category>();
		for (Category child : children) {
			this.children.add(child);
		}

		catMap.put(id, this);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Category> getChildren() {
		return children;
	}

	public void setChildren(List<Category> children) {
		this.children = children;
	}

	public void toggle() {
		toggle = !toggle;
	}

	public boolean isToggle() {
		return toggle;
	}
}
