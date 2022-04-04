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
package org.apache.struts2.showcase;

import com.opensymphony.xwork2.ActionSupport;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class MoreSelectsAction extends ActionSupport {


	private List _prioritisedFavouriteCartoonCharacters;
	private List _prioritisedFavouriteCars;
	private List _prioritisedFavouriteCountries;
	private List favouriteNumbers;


	// Cartoon Characters
	public Map getDefaultFavouriteCartoonCharacters() {
		Map m = new LinkedHashMap();
		m.put("heMan", "He-Man");
		m.put("popeye", "Popeye");
		m.put("mockeyMouse", "Mickey Mouse");
		return m;
	}


	// Cars
	public Map getDefaultFavouriteCars() {
		Map m = new LinkedHashMap();
		m.put("alfaRomeo", "Alfa Romeo");
		m.put("Toyota", "Toyota");
		m.put("Mitsubitshi", "Mitsubitshi");
		return m;
	}


	// Countries
	public Map getDefaultFavouriteCountries() {
		Map m = new LinkedHashMap();
		m.put("england", "England");
		m.put("america", "America");
		m.put("brazil", "Brazil");
		return m;
	}

	public List getDefaultFavouriteNumbers() {
		List list = new ArrayList();
		list.add("Three");
		list.add("Seven");
		return list;
	}


	public List getPrioritisedFavouriteCartoonCharacters() {
		return _prioritisedFavouriteCartoonCharacters;
	}

	public void setPrioritisedFavouriteCartoonCharacters(List prioritisedFavouriteCartoonCharacters) {
		_prioritisedFavouriteCartoonCharacters = prioritisedFavouriteCartoonCharacters;
	}

	public List getPrioritisedFavouriteCars() {
		return _prioritisedFavouriteCars;
	}

	public void setPrioritisedFavouriteCars(List prioritisedFavouriteCars) {
		_prioritisedFavouriteCars = prioritisedFavouriteCars;
	}


	public List getPrioritisedFavouriteCountries() {
		return _prioritisedFavouriteCountries;
	}

	public void setPrioritisedFavouriteCountries(List prioritisedFavouriteCountries) {
		_prioritisedFavouriteCountries = prioritisedFavouriteCountries;
	}

	public List getFavouriteNumbers() {
		return favouriteNumbers;
	}

	public void setFavouriteNumbers(List favouriteNumbers) {
		this.favouriteNumbers = favouriteNumbers;
	}

	public Map getAvailableCities() {
		Map map = new LinkedHashMap();
		map.put("boston", "Boston");
		map.put("new york", "New York");
		map.put("london", "London");
		map.put("rome", "Rome");
		return map;
	}

	public List getDefaultFavouriteCities() {
		List list = new ArrayList();
		list.add("boston");
		list.add("rome");
		return list;
	}

	// actions

	public String input() throws Exception {
		return SUCCESS;
	}

	public String submit() throws Exception {
		return SUCCESS;
	}
}
