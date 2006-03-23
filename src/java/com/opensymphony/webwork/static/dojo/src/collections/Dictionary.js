/*
	Copyright (c) 2004-2005, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.collections.Dictionary");
dojo.require("dojo.collections.Collections");

dojo.collections.Dictionary = function(dictionary){
	var items = {};
	this.count = 0;

	this.add = function(k,v){
		items[k] = new dojo.collections.DictionaryEntry(k,v);
		this.count++;
	};
	this.clear = function(){
		items = {};
		this.count = 0;
	};
	this.clone = function(){
		return new dojo.collections.Dictionary(this);
	};
	this.contains = this.containsKey = function(k){
		return (items[k] != null);
	};
	this.containsValue = function(v){
		var e = this.getIterator();
		while (!e.atEnd) {
			if (e.value == v) return true;
			e.moveNext();
		}
		return false;
	};
	this.getKeyList = function(){
		var arr = [];
		var e = this.getIterator();
		while (!e.atEnd) {
			arr.push(e.key);
			e.moveNext();
		}
		return arr;
	};
	this.getValueList = function(){
		var arr = [];
		var e = this.getIterator();
		while (!e.atEnd) {
			arr.push(e.value);
			e.moveNext();
		}
		return arr;
	};
	this.item = function(k){
		return items[k];
	};
	this.getIterator = function(){
		return new dojo.collections.DictionaryIterator(items);
	};
	this.remove = function(k){
		delete items[k];
		this.count--;
	};

	if (dictionary){
		var e = dictionary.getIterator();
		while (!e.atEnd) {
			 this.add(e.key, e.value);
			 e.moveNext();
		}
	}
};
