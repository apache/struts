/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.data.Result");
dojo.require("dojo.lang.declare");
dojo.require("dojo.experimental");

/* summary:
 *   This is an abstract API used by data provider implementations.  
 *   This file defines methods signatures and intentionally leaves all the
 *   methods unimplemented.
 */
dojo.experimental("dojo.data.Result");

dojo.declare("dojo.data.Result", null, {
	forEach:
		function(/* function */ callbackFunction, /* object? */ callbackObject, /* object? */ optionalKeywordArgs) {
		/* summary:
		 *   Loops through the result list, calling a callback function
		 *   for each item in the result list.
		 * description:
		 *   The forEach() method will call the callback function once for 
		 *   each item in the result list.  The forEach() method will pass 
		 *   3 arguments to the callbackFunction: an item, the index of 
		 *   item in the context of this forEach() loop, and the result object
		 *   itself.  The signature of this forEach() method was modeled on
		 *   the forEach() method of Mozilla's Array object in JavaScript 1.6:
		 *   http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Reference:Objects:Array:forEach
		 *   The forEach() method will returns true if the entire result list 
		 *   has been looped through, or false if the result list has not yet
		 *   been looped through.
		 *   The forEach() method will ignore any return value returned by
		 *   the callbackFunction.
		 *   After the forEach() operation has finished (or been cancelled)
		 *   result.forEach() can be called again on the same result object.
		 * ISSUES -
		 *   We haven't yet decided what other parameters we might allow to
		 *   support fancy features.  Here are some ideas:
		 *     results.forEach({callback:callbackFunction, onCompletion: finishedFunction});
		 *     results.forEach({callback:callbackFunction, first: 201, last: 300}); // partial loop
		 *     results.forEach({callback:callbackFunction, first: 200, numItems: 50}); // partial loop from 200 to 250
		 *   CCM - How to specify datastore-specific options to allow caching n
		 *   items before/after current window of items being viewed?
		 * callbackObject:
		 *   If a callbackObject is provided the callbackFunction will be called
		 *   in the context of the callbackObject (the callbackObject will be 
		 *   used as the 'this' for each invocation of the callbackFunction).
		 *   If callbackObject is not provided, or is null, the global object
		 *   associated with callback is used instead.
		 * optionalKeywordArgs:
		 *   The forEach() method may accept a third parameter, which should be
		 *   an object with keyword parameters.  Different implementations may
		 *   make use of different keyword paramters.  Conforming 
		 *   implementations ignore keyword parameters that they don't 
		 *   recognize.
		 */
		 
		/* examples:
		 *   var results = store.find("recent books");            // synchronous
		 *   var results = store.find("all books", {sync: false}); // asynchronous
		 *   someCallbackFunction = function(item, resultObject) {};
		 *   results.forEach(someCallbackFunction);
		 *   results.forEach({object:someHandlerObject, callback:"someCallbackMethod"});
		 */
			dojo.unimplemented('dojo.data.Result.forEach');
			return false; // boolean
		},
	getLength:
		function() {
		/* summary:
		 *   Returns an integer -- the number of items in the result list.
		 *   Returns -1 if the length is not known when the method is called.
		 */
			dojo.unimplemented('dojo.data.Result.getLength');
			return -1; // integer
		},
	inProgress:
		function() {
		/* summary:
		 *   Returns true if a forEach() loop is in progress.
		 */
			dojo.unimplemented('dojo.data.Result.inProgress');
			return false; // boolean
		},
	cancel:
		function() {
		/* summary:
		 *   Calling cancel() stops any and all processing associated with this
		 *   result object.  
		 * description: 
		 *   If a forEach() loop is in progress, calling cancel() will stop 
		 *   the loop.  If a store.find() is in progress, and that find() 
		 *   involves an XMLHttpRequest, calling cancel() will abort the 
		 *   XMLHttpRequest.  If callbacks have been set using setOnFindCompleted() 
		 *   or setOnError(), calling cancel() will cause those callbacks to 
		 *   not be called under any circumstances.
		 */
			dojo.unimplemented('dojo.data.Result.cancel');
		},
	setOnFindCompleted:
		function(/* function */ callbackFunction, /* object? */ callbackObject) {
		/* summary:
		 *   Allows you to register a callbackFunction that will
		 *   be called when all the results are available.
		 * description:
		 *   If a callbackObject is provided the callbackFunction will be 
		 *   called in the context of the callbackObject (the callbackObject
		 *   will be used as the 'this' for each invocation of the
		 *   callbackFunction).  If callbackObject is not provided, or is 
		 *   null, the global object associated with callback is used instead.
		 *   The setOnFindCompleted() method will ignore any return value 
		 *   returned by the callbackFunction.
		 * ISSUES -
		 *   We have not yet decided what parameters the setOnFindCompleted() 
		 *   will pass to the callbackFunction...
		 *   (A) The setOnFindCompleted() method will pass one parameter to the 
		 *   callbackFunction: the result object itself.
		 *   (B) The setOnFindCompleted() method will pass two parameters to the 
		 *   callbackFunction: an iterator object, and the result object itself.
		 */
			dojo.unimplemented('dojo.data.Result.setOnFindCompleted');
		},
	setOnError:
		function(/* function */ errorCallbackFunction, /* object? */ callbackObject) {
		/* summary:
		 *   Allows you to register a errorCallbackFunction that
		 *   will be called if there is any sort of error.
		 * description:
		 *   If a callbackObject is provided the errorCallbackFunction will
		 *   be called in the context of the callbackObject (the callbackObject
		 *   will be used as the 'this' for each invocation of the
		 *   errorCallbackFunction).  If callbackObject is not provided, or is 
		 *   null, the global object associated with callback is used instead.
		 *   The setOnError() method will pass two parameters to the 
		 *   errorCallbackFunction: an Error object, and the result object 
		 *   itself:
		 *     errorCallbackFunction(errorObject, resultObject); 
		 *   The setOnError() method will ignore any return value returned 
		 *   by the errorCallbackFunction.
		 */
			dojo.unimplemented('dojo.data.Result.setOnError');
		},
	getStore:
		function() {
		/* summary:
		 *   Returns the datastore object that created this result list
		 */
			dojo.unimplemented('dojo.data.Result.getStore');
			return null; // an object that implements dojo.data.Read
		}
});
