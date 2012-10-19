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
public class LotsOfOptiontransferselectAction extends ActionSupport {

	private List _favouriteCartoonCharactersKeys;
	private List _notFavouriteCartoonCharactersKeys;

	private List _favouriteCarsKeys;
	private List _notFavouriteCarsKeys;

	private List _favouriteMotorcyclesKeys;
	private List _notFavouriteMotorcyclesKeys;

	private List _favouriteCountriesKeys;
	private List _notFavouriteCountriesKeys;

	private List _favouriteSportsKeys;
	private List _nonFavouriteSportsKeys;

	private List _favouriteCities;

	private List _prioritisedFavouriteCartoonCharacters;
	private List _prioritisedFavouriteCars;
	private List _prioritisedFavouriteCountries;


	// Cartoon Characters
	public Map getDefaultFavouriteCartoonCharacters() {
		Map m = new LinkedHashMap();
		m.put("heMan", "He-Man");
		m.put("popeye", "Popeye");
		m.put("mockeyMouse", "Mickey Mouse");
		return m;
	}

	public Map getDefaultNotFavouriteCartoonCharacters() {
		Map m = new LinkedHashMap();
		m.put("donaldDuck", "Donald Duck");
		m.put("atomicAnt", "Atomic Ant");
		m.put("pinkPainter", "Pink Painter");
		return m;
	}

	public List getFavouriteCartoonCharacters() {
		return _favouriteCartoonCharactersKeys;
	}

	public void setFavouriteCartoonCharacters(List favouriteCartoonCharacters) {
		_favouriteCartoonCharactersKeys = favouriteCartoonCharacters;
	}

	public List getNotFavouriteCartoonCharacters() {
		return _notFavouriteCartoonCharactersKeys;
	}

	public void setNotFavouriteCartoonCharacters(List notFavouriteCartoonCharacters) {
		_notFavouriteCartoonCharactersKeys = notFavouriteCartoonCharacters;
	}


	// Cars
	public Map getDefaultFavouriteCars() {
		Map m = new LinkedHashMap();
		m.put("alfaRomeo", "Alfa Romeo");
		m.put("Toyota", "Toyota");
		m.put("Mitsubitshi", "Mitsubitshi");
		return m;
	}

	public Map getDefaultNotFavouriteCars() {
		Map m = new LinkedHashMap();
		m.put("ford", "Ford");
		m.put("landRover", "Land Rover");
		m.put("mercedes", "Mercedes");
		return m;
	}

	public List getFavouriteCars() {
		return _favouriteCarsKeys;
	}

	public void setFavouriteCars(List favouriteCars) {
		_favouriteCarsKeys = favouriteCars;
	}

	public List getNotFavouriteCars() {
		return _notFavouriteCarsKeys;
	}

	public void setNotFavouriteCars(List notFavouriteCars) {
		_notFavouriteCarsKeys = notFavouriteCars;
	}


	// Motorcycles
	public Map getDefaultFavouriteMotorcycles() {
		Map m = new LinkedHashMap();
		m.put("honda", "Honda");
		m.put("yamaha", "Yamaha");
		m.put("Aprillia", "Aprillia");
		return m;
	}

	public Map getDefaultNotFavouriteMotorcycles() {
		Map m = new LinkedHashMap();
		m.put("cagiva", "Cagiva");
		m.put("harleyDavidson", "Harley Davidson");
		m.put("suzuki", "Suzuki");
		return m;
	}

	public List getFavouriteMotorcycles() {
		return _favouriteMotorcyclesKeys;
	}

	public void setFavouriteMotorcycles(List favouriteMotorcycles) {
		_favouriteMotorcyclesKeys = favouriteMotorcycles;
	}

	public List getNotFavouriteMotorcycles() {
		return _notFavouriteMotorcyclesKeys;
	}

	public void setNotFavouriteMotorcycles(List notFavouriteMotorcycles) {
		_notFavouriteMotorcyclesKeys = notFavouriteMotorcycles;
	}


	// Countries
	public Map getDefaultFavouriteCountries() {
		Map m = new LinkedHashMap();
		m.put("england", "England");
		m.put("america", "America");
		m.put("brazil", "Brazil");
		return m;
	}

	public Map getDefaultNotFavouriteCountries() {
		Map m = new LinkedHashMap();
		m.put("germany", "Germany");
		m.put("china", "China");
		m.put("russia", "Russia");
		return m;
	}

	public List getFavouriteCountries() {
		return _favouriteCountriesKeys;
	}

	public void setFavouriteCountries(List favouriteCountries) {
		_favouriteCountriesKeys = favouriteCountries;
	}

	public List getNotFavouriteCountries() {
		return _notFavouriteCountriesKeys;
	}

	public void setNotFavouriteCountries(List notFavouriteCountries) {
		_notFavouriteCountriesKeys = notFavouriteCountries;
	}

	// Sports
	public Map getDefaultNonFavoriteSports() {
		Map m = new LinkedHashMap();
		m.put("basketball", "Basketball");
		m.put("football", "Football");
		m.put("baseball", "Baseball");
		return m;
	}

	public Map getDefaultFavoriteSports() {
		return new LinkedHashMap();
	}

	public List getFavouriteSports() {
		return _favouriteSportsKeys;
	}

	public void setFavouriteSports(List favouriteSportsKeys) {
		this._favouriteSportsKeys = favouriteSportsKeys;
	}

	public List getNonFavouriteSports() {
		return _nonFavouriteSportsKeys;
	}

	public void setNonFavouriteSports(List notFavouriteSportsKeys) {
		this._nonFavouriteSportsKeys = notFavouriteSportsKeys;
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

	public List getFavouriteCities() {
		return _favouriteCities;
	}

	public void setFavouriteCities(List favouriteCities) {
		this._favouriteCities = favouriteCities;
	}

	// actions

	public String input() throws Exception {
		return SUCCESS;
	}

	public String submit() throws Exception {
		return SUCCESS;
	}
}
