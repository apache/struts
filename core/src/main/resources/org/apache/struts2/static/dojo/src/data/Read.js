/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.data.Read");
dojo.require("dojo.lang.declare");
dojo.require("dojo.data.Result");
dojo.require("dojo.experimental");

/* summary:
 *   This is an abstract API that data provider implementations conform to.  
 *   This file defines methods signatures and intentionally leaves all the
 *   methods unimplemented.
 */
dojo.experimental("dojo.data.Read");
 
dojo.declare("dojo.data.Read", null, {
	get:
		function(/* item */ item, /* attribute || attribute-name-string */ attribute, /* value? */ defaultValue) {
		/* summary:
		 *   Returns a single attribute value.
		 *   Returns defaultValue if item does not have a value for attribute.
		 *   Returns null if null was explicitly set as the attribute value.
		 *   Returns undefined if the item does not have a value for the given attribute.
		 *   (So, if store.hasAttribute(item, attribute) returns false, then
		 *   store.get(item, attribute) will return undefined.)
		 */
		 
		/* exceptions:
		 *   Conforming implementations should throw an exception if *item* is not
		 *   an item, or *attribute* is neither an attribute object or a string.
		 * examples:
		 *   var darthVader = store.get(lukeSkywalker, "father");
		 */
			dojo.unimplemented('dojo.data.Read.get');
			var attributeValue = null;
			return attributeValue; // a literal, an item, null, or undefined (never an array)
		},
	getValues:
		function(/* item */ item, /* attribute || attribute-name-string */ attribute) {
		/* summary:
		 *   This getValues() method works just like the get() method, but getValues()
		 *   always returns an array rather than a single attribute value.  The array
		 *   may be empty, may contain a single attribute value, or may contain many
		 *   attribute values.
		 *   If the item does not have a value for the given attribute, then getValues()
		 *   will return an empty array: [].  (So, if store.hasAttribute(item, attribute)
		 *   returns false, then store.getValues(item, attribute) will return [].)
		 */
		 
		/* exceptions:
		 *   Throws an exception if item is not an item, or attribute is neither an 
		 *   attribute object or a string.
		 * examples:
		 *   var friendsOfLuke = store.get(lukeSkywalker, "friends");
		 */
			dojo.unimplemented('dojo.data.Read.getValues');
			var array = null;
			return array; // an array that may contain literals and items
		},
	getAttributes:
		function(/* item */ item) {
		/* summary:
		 *   Returns an array with all the attributes that this item has.
		 */
		 
		/* exceptions:
		 *   Throws an exception if item is not an item. 
		 * examples:
		 *   var array = store.getAttributes(kermit);
		 */
			dojo.unimplemented('dojo.data.Read.getAttributes');
			var array = null;
			return array; // array
		},
	hasAttribute:
		function(/* item */ item, /* attribute || attribute-name-string */ attribute) {
		/* summary:
		 *   Returns true if the given *item* has a value or the given *attribute*.
		 */
		 
		/* exceptions:
		 *   Throws an exception if item is not an item, or attribute is neither an 
		 *   attribute object or a string.
		 * examples:
		 *   var yes = store.hasAttribute(kermit, "color");
		 */
			dojo.unimplemented('dojo.data.Read.hasAttribute');
			return false; // boolean
		},
	hasAttributeValue:
		function(/* item */ item, /* attribute || attribute-name-string */ attribute, /* anything */ value) {
		/* summary:
		 *   Returns true if the given *value* is one of the values that getValue()
		 *   would return.
		 */
		 
		/* exceptions:
		 *   Throws an exception if item is not an item, or attribute is neither an 
		 *   attribute object or a string.
		 * examples:
		 *   var yes = store.hasAttributeValue(kermit, "color", "green");
		 */
			dojo.unimplemented('dojo.data.Read.hasAttributeValue');
			return false; // boolean
		},
	isItem:
		function(/* anything */ something) {
		/* summary:
		 *   Returns true if *something* is an item.  Returns false if *something*
		 *   is a literal or is any object other than an item.
		 */
		 
		/* examples:
		 *   var yes = store.isItem(store.newItem());
		 *   var no  = store.isItem("green");
		 */
			dojo.unimplemented('dojo.data.Read.isItem');
			return false; // boolean
		},
	find:
		function(/* implementation-dependent */ query, /* object */ optionalKeywordArgs ) {
		/* summary:
		 *   Given a query, this method returns a Result object containing
		 *   all the items in the query result set.
		 * description:
		 *   A Result object will always be returned, even if the result set
		 *   is empty.  A Result object will always be returned immediately.
		 *   By default the Result object will be fully populated with result
		 *   items as soon as it is created (synchronously).  The caller may request
		 *   an asynchronous Result, meaning a Result that will be populated
		 *   with result items at some point in the future.  If the caller requests
		 *   an asynchronous Result, the data store may return either a synchronous
		 *   or asynchronous Result, whichever it prefers.  Simple data store
		 *   implementations may always return synchronous Results.
		 *   For more info about the Result API, see dojo.data.Result
		 * query:
		 *   The query may be optional in some data store implementations.
		 *   The dojo.data.Read API does not specify the syntax or semantics
		 *   of the query itself -- each different data store implementation
		 *   may have its own notion of what a query should look like.
		 *   In most implementations the query will probably be a string, but
		 *   in some implementations the query might be a Date, or a number,
		 *   or some complex keyword parameter object.  The dojo.data.Read
		 *   API is completely agnostic about what the query actually is.
		 * optionalKeywordArgs:
		 *   The optionalKeywordArgs argument is a object like {async: true}.
		 *   All implementations should accept {async: true} and {async: false}
		 *   as valid parameters, although the API does not require that the
		 *   the implementation actually perform asynchronously when
		 *   {async: true} is set.  Some implementations may take additional
		 *   keyword options, such as {async: true, maxResults:100}.
		 */
		
		/* exceptions:
		 *   Throws an exception if the query is not valid, or if the query
		 *   is required but was not supplied.
		 * examples:
		 *   var results = store.find("all books");
		 *   var results = store.find();
		 *   var results = store.find("foo/bar", {async: true});
		 *   var results = store.find("foo/bar", {async: false});
		 *   var results = store.find({author:"King", {async: true, maxResults:100});
		 */
			dojo.unimplemented('dojo.data.Read.find');
			var result = null; // new dojo.data.Result().
			return result; // an object that implements dojo.data.Result
		},
	getIdentity:
		function(/* item */ item) {
		/* summary:
		 *   Returns a unique identifer for an item.  The return value will be
		 *   either a string or something that has a toString() method (such as,
		 *   for example, a dojo.uuid.Uuid object).
		 * description:
		 * ISSUE - 
		 *   Should we move this method out of dojo.data.Read, and put it somewhere
		 *   else, like maybe dojo.data.Identity?
		 */
		 
		/* exceptions:
		 *   Conforming implementations may throw an exception or return null if
		 *   item is not an item.
		 * examples:
		 *   var itemId = store.getIdentity(kermit);
		 *   assert(kermit === store.getByIdentity(store.getIdentity(kermit)));
		 */
			dojo.unimplemented('dojo.data.Read.getIdentity');
			var itemIdentifyString = null;
			return itemIdentifyString; // string
		},
	getByIdentity:
		function(/* string */ id) {
		/* summary:
		 *   Given the id of an item, this method returns the item that has that id.
		 *   Conforming implementations should return null if there is no item with
		 *   the given id.
		 * description:
		 * ISSUE - 
		 *   We may want to change the name from getByIdentity() to findByIdentity(),
		 *   to reflect the fact that an implementation may not be able to get the
		 *   item from a local cache, and may need to send a request to the server.
		 * ISSUE - 
		 *   Can this method run asynchronously?  Should the return value be a Deferred?
		 * ISSUE - 
		 *   Should we move this method out of dojo.data.Read, and put it somewhere
		 *   else, like maybe dojo.data.Identity?
		 */
		 
		/* examples:
		 *   var alaska = store.getByIdentity("AK");
		 *   assert("AK" == store.getIdentity(store.getByIdentity("AK")));
		 */
			dojo.unimplemented('dojo.data.Read.getByIdentity');
			var item = null;
			return item; // item
		}
});
