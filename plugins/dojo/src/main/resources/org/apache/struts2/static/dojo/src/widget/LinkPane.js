/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.LinkPane");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.ContentPane");
dojo.require("dojo.html.style");
dojo.widget.defineWidget("dojo.widget.LinkPane", dojo.widget.ContentPane, {templateString:"<div class=\"dojoLinkPane\"></div>", fillInTemplate:function (args, frag) {
	var source = this.getFragNodeRef(frag);
	this.label += source.innerHTML;
	var source = this.getFragNodeRef(frag);
	dojo.html.copyStyle(this.domNode, source);
}});

