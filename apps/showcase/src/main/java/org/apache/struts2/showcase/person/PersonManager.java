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
package org.apache.struts2.showcase.person;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class PersonManager {
	private static Set<Person> people = new HashSet<Person>(5);
	private static long COUNT = 5;

	static {
		// create some imaginary persons
		Person p1 = new Person(new Long(1), "Patrick", "Lightbuddie");
		Person p2 = new Person(new Long(2), "Jason", "Carrora");
		Person p3 = new Person(new Long(3), "Alexandru", "Papesco");
		Person p4 = new Person(new Long(4), "Jay", "Boss");
		Person p5 = new Person(new Long(5), "Rainer", "Hermanos");
		people.add(p1);
		people.add(p2);
		people.add(p3);
		people.add(p4);
		people.add(p5);
	}

	public void createPerson(Person person) {
		person.setId(new Long(++COUNT));
		people.add(person);
	}

	public void updatePerson(Person person) {
		people.add(person);
	}

	public Set<Person> getPeople() {
		return people;
	}
}
