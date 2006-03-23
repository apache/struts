/*
	Copyright (c) 2004-2005, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.DatePicker");
dojo.require("dojo.widget.DomWidget");

dojo.widget.DatePicker = function(){
	dojo.widget.Widget.call(this);
	this.widgetType = "DatePicker";
	this.isContainer = false;

	this.months = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
	this.weekdays = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
}

dojo.inherits(dojo.widget.DatePicker, dojo.widget.Widget);
dojo.widget.tags.addParseTreeHandler("dojo:datepicker");

dojo.requireAfterIf("html", "dojo.widget.html.DatePicker");
