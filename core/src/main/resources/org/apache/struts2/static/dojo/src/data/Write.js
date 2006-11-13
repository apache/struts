/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.data.Write");
dojo.require("dojo.lang.declare");
dojo.require("dojo.data.Read");
dojo.require("dojo.experimental");

/* summary:
 *   This is an abstract API that data provider implementations conform to.  
 *   This file defines methods signatures and intentionally leaves all the
 *   methods unimplemented.
 */
dojo.experimental("dojo.data.Write");
 
dojo.declare("dojo.data.Write", dojo.data.Read, {
	newItem:
		function(/* object? */ keywordArgs) {
		/* summary:
		 *   Returns a newly created item.  Sets the attributes of the new
		 *   item based on the keywordArgs provided.
		 */
		 
		/* exceptions:
		 *   Throws an exception if *keywordArgs* is a string or a number or
		 *   anything other than a simple anonymous object.
		 * examples:
		 *   var kermit = store.newItem({name: "Kermit"});
		 */
			var newItem;
			dojo.unimplemented('dojo.data.Write.newItem');
			return newItem; // item
		},
	deleteItem:
		function(/* item */ item) {
		/* summary:
		 *   Deletes an item from the store.
		 */
		 
		/* exceptions:
		 *   Throws an exception if *item* is not an item (if store.isItem(item)
		 *   returns false).
		 * examples:
		 *   var success = store.deleteItem(kermit);
		 */
			dojo.unimplemented('dojo.data.Write.deleteItem');
			return false; // boolean
		},
	set:
		function(/* item */ item, /* attribute || string */ attribute, /* almost anything */ value) {
		/* summary:
		 *   Sets the value of an attribute on an item.
		 *   Replaces any previous value or values.
		 */
		 
		/* exceptions:
		 *   Throws an exception if *item* is not an item, or if *attribute*
		 *   is neither an attribute object or a string.
		 *   Throws an exception if *value* is undefined.
		 * examples:
		 *   var success = store.set(kermit, "color", "green");
		 */
			dojo.unimplemented('dojo.data.Write.set');
			return false; // boolean
		},
	setValues:
		function(/* item */ item, /* attribute || string */ attribute, /* array */ values) {
		/* summary:
		 *   Adds each value in the *values* array as a value of the given
		 *   attribute on the given item.
		 *   Replaces any previous value or values.
		 *   Calling store.setValues(x, y, []) (with *values* as an empty array) has
		 *   the same effect as calling store.clear(x, y).
		 */
		 
		/* exceptions:
		 *   Throws an exception if *values* is not an array, if *item* is not an
		 *   item, or if *attribute* is neither an attribute object or a string.
		 * examples:
		 *   var success = store.setValues(kermit, "color", ["green", "aqua"]);
		 *   success = store.setValues(kermit, "color", []);
		 *   if (success) {assert(!store.hasAttribute(kermit, "color"));}
		 */
			dojo.unimplemented('dojo.data.Write.setValues');
			return false; // boolean
		},
	clear:
		function(/* item */ item, /* attribute || string */ attribute) {
		/* summary:
		 *   Deletes all the values of an attribute on an item.
		 */
		 
		/* exceptions:
		 *   Throws an exception if *item* is not an item, or if *attribute*
		 *   is neither an attribute object or a string.
		 * examples:
		 *   var success = store.clear(kermit, "color");
		 *   if (success) {assert(!store.hasAttribute(kermit, "color"));}
		 */
			dojo.unimplemented('dojo.data.Write.clear');
			return false; // boolean
		},
	save:
		function() {
		/* summary:
		 *   Saves to the server all the changes that have been made locally.
		 *   The save operation may take some time.  By default the save will
		 *   be done synchronously, before the call returns.  The caller may
		 *   be request an asynchronous save by passing {async: true}.
		 *   If the caller requests an asynchronous save, the data store may do
		 *   either a synchronous or asynchronous save, whichever it prefers.
		 *   Different data store implementations may take additional optional
		 *   parameters.
		 * description:
		 * ISSUE - 
		 *   Should the async save take a callback, like this:
		 *     store.save({async: true, onComplete: callback});
		 *   Or should the async save return a Deferred, like this:
		 *     var deferred = store.save({async: true});
		 *     deferred.addCallbacks(successCallback, errorCallback);
		 *   Or should save() return boolean, like this:
		 *     var success = store.save();
		 */
		 
		/* examples:
		 *   var success = store.save();
		 *   var success = store.save({async: true});
		 */
			dojo.unimplemented('dojo.data.Write.save');
			return false; // boolean
		},
	revert:
		function() {
		/* summary:
		 *   Discards any unsaved changes.
		 */
		 
		/* examples:
		 *   var success = store.revert();
		 */
			dojo.unimplemented('dojo.data.Write.revert');
			return false; // boolean
		},
	isDirty:
		function(/* item (or store) */ item) {
		/* summary:
		 *   Returns true if the given item has been modified since the last save().
		 *   If the datastore object itself is given as a parameter instead of an
		 *   item, then this method returns true if any item has been modified since
		 *   the last save().
		 */
		 
		/* exceptions:
		 *   Throws an exception if *item* is neither an item nor the datastore itself.
		 * examples:
		 *   var trueOrFalse = store.isDirty(kermit); // true if kermit is dirty
		 *   var trueOrFalse = store.isDirty(store);  // true if any item is dirty
		 */
			dojo.unimplemented('dojo.data.Write.isDirty');
			return false; // boolean
		}
});
