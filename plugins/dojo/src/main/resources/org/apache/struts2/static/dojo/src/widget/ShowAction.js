/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.ShowAction");
dojo.require("dojo.widget.*");
dojo.widget.defineWidget("dojo.widget.ShowAction", dojo.widget.HtmlWidget, {on:"", action:"fade", duration:350, from:"", to:"", auto:"false", postMixInProperties:function () {
	if (dojo.render.html.opera) {
		this.action = this.action.split("/").pop();
	}
}});

