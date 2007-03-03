/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.collections.Set");
dojo.require("dojo.collections.Collections");
dojo.require("dojo.collections.ArrayList");

dojo.collections.Set = new function(){
	//	summary
	//	Singleton for dealing with common set operations.
	this.union = function(/* array */setA, /* array */setB){
		//	summary
		//	Return the union of the two passed sets.
		if (setA.constructor == Array) var setA = new dojo.collections.ArrayList(setA);
		if (setB.constructor == Array) var setB = new dojo.collections.ArrayList(setB);
		if (!setA.toArray || !setB.toArray) dojo.raise("Set operations can only be performed on array-based collections.");
		var result = new dojo.collections.ArrayList(setA.toArray());
		var e = setB.getIterator();
		while(!e.atEnd()){
			var item=e.get();
			if(!result.contains(item)){
				result.add(item);
			}
		}
		return result;	//	dojo.collections.ArrayList
	};
	this.intersection = function(/* array */setA, /* array */setB){
		//	summary
		//	Return the intersection of the two passed sets.
		if (setA.constructor == Array) var setA = new dojo.collections.ArrayList(setA);
		if (setB.constructor == Array) var setB = new dojo.collections.ArrayList(setB);
		if (!setA.toArray || !setB.toArray) dojo.raise("Set operations can only be performed on array-based collections.");
		var result = new dojo.collections.ArrayList();
		var e = setB.getIterator();
		while(!e.atEnd()){
			var item=e.get();
			if(setA.contains(item)){
				result.add(item);
			}
		}
		return result;	//	dojo.collections.ArrayList
	};
	this.difference = function(/* array */setA, /* array */setB){
		//	summary
		//	Returns everything in setA that is not in setB.
		if (setA.constructor == Array) var setA = new dojo.collections.ArrayList(setA);
		if (setB.constructor == Array) var setB = new dojo.collections.ArrayList(setB);
		if (!setA.toArray || !setB.toArray) dojo.raise("Set operations can only be performed on array-based collections.");
		var result = new dojo.collections.ArrayList();
		var e=setA.getIterator();
		while(!e.atEnd()){
			var item=e.get();
			if(!setB.contains(item)){
				result.add(item);
			}
		}
		return result;	//	dojo.collections.ArrayList
	};
	this.isSubSet = function(/* array */setA, /* array */setB) {
		//	summary
		//	Returns if set B is a subset of set A.
		if (setA.constructor == Array) var setA = new dojo.collections.ArrayList(setA);
		if (setB.constructor == Array) var setB = new dojo.collections.ArrayList(setB);
		if (!setA.toArray || !setB.toArray) dojo.raise("Set operations can only be performed on array-based collections.");
		var e = setA.getIterator();
		while(!e.atEnd()){
			if(!setB.contains(e.get())){
				return false;	//	boolean
			}
		}
		return true;	//	boolean
	};
	this.isSuperSet = function(/* array */setA, /* array */setB){
		//	summary
		//	Returns if set B is a superset of set A.
		if (setA.constructor == Array) var setA = new dojo.collections.ArrayList(setA);
		if (setB.constructor == Array) var setB = new dojo.collections.ArrayList(setB);
		if (!setA.toArray || !setB.toArray) dojo.raise("Set operations can only be performed on array-based collections.");
		var e = setB.getIterator();
		while(!e.atEnd()){
			if(!setA.contains(e.get())){
				return false;	//	boolean
			}
		}
		return true;	//	boolean
	};
}();
