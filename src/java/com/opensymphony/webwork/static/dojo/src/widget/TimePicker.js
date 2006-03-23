/*
	Copyright (c) 2004-2005, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.TimePicker");
dojo.require("dojo.widget.DomWidget");

dojo.widget.TimePicker = function(){
	dojo.widget.Widget.call(this);
	this.widgetType = "TimePicker";
	this.isContainer = false;

}

dojo.inherits(dojo.widget.TimePicker, dojo.widget.Widget);
dojo.widget.tags.addParseTreeHandler("dojo:timepicker");

dojo.requireAfterIf("html", "dojo.widget.html.TimePicker");
