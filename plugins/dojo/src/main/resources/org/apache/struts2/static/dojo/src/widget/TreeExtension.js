/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.TreeExtension");
dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.widget.TreeCommon");
dojo.widget.defineWidget("dojo.widget.TreeExtension", [dojo.widget.HtmlWidget, dojo.widget.TreeCommon], function () {
	this.listenedTrees = {};
}, {});

