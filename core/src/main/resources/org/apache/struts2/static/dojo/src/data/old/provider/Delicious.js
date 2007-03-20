/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.data.old.provider.Delicious");
dojo.require("dojo.data.old.provider.FlatFile");
dojo.require("dojo.data.old.format.Json");

// -------------------------------------------------------------------
// Constructor
// -------------------------------------------------------------------
dojo.data.old.provider.Delicious = function() {
	/**
	 * summary:
	 * The Delicious Data Provider can be used to take data from
	 * del.icio.us and make it available as dojo.data.old.Items
	 * In order to use the Delicious Data Provider, you need 
	 * to have loaded a script tag that looks like this:
	 * <script type="text/javascript" src="http://del.icio.us/feeds/json/gumption?count=8"></script>
	 */
	dojo.data.old.provider.FlatFile.call(this);
	// Delicious = null;
	if (Delicious && Delicious.posts) {
		dojo.data.old.format.Json.loadDataProviderFromArrayOfJsonData(this, Delicious.posts);
	} else {
		// document.write("<script type='text/javascript'>dojo.data.old.provider.Delicious._fetchComplete()</script>");		
		/*
		document.write("<script type='text/javascript'>alert('boo!');</script>");		
		document.write("<script type='text/javascript'>var foo = 'not dojo'; alert('dojo == ' + foo);</script>");		
		document.write("<script type='text/javascript'>var foo = fetchComplete; alert('dojo == ' + foo);</script>");		
		fetchComplete();
		*/
		// dojo.debug("Delicious line 29: constructor");
	}
	var u = this.registerAttribute('u');
	var d = this.registerAttribute('d');
	var t = this.registerAttribute('t');
	
	u.load('name', 'Bookmark');
	d.load('name', 'Description');
	t.load('name', 'Tags');
	
	u.load('type', 'String');
	d.load('type', 'String');
	t.load('type', 'String');
};
dojo.inherits(dojo.data.old.provider.Delicious, dojo.data.old.provider.FlatFile);

/********************************************************************
 * FIXME: the rest of this is work in progress
 *
 
dojo.data.old.provider.Delicious.prototype.getNewItemToLoad = function() {
	var newItem = this._newItem();
	this._currentArray.push(newItem);
	return newItem; // dojo.data.old.Item
};

dojo.data.old.provider.Delicious.prototype.fetchArray = function(query) {
	if (!query) {	
		query = "gumption";
	}
	this._currentArray = [];
	alert("Delicious line 60: loadDataProviderFromArrayOfJsonData");
	alert("Delicious line 61: " + dojo);
		var sourceUrl = "http://del.icio.us/feeds/json/" + query + "?count=8";
		document.write("<script type='text/javascript' src='" + sourceUrl + "'></script>");
		document.write("<script type='text/javascript'>alert('line 63: ' + Delicious.posts[0].u);</script>");		
		document.write("<script type='text/javascript'>callMe();</script>");		
	alert("line 66");
	dojo.data.old.format.Json.loadDataProviderFromArrayOfJsonData(this, Delicious.posts);
	return this._currentArray; // Array
};

callMe = function() {
	alert("callMe!");
};

*/
