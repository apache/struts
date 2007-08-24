/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.TreeEmphasizeOnSelect");
dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.widget.TreeSelectorV3");
dojo.require("dojo.html.selection");
dojo.widget.defineWidget("dojo.widget.TreeEmphasizeOnSelect", dojo.widget.HtmlWidget, {selector:"", initialize:function () {
	this.selector = dojo.widget.byId(this.selector);
	dojo.event.topic.subscribe(this.selector.eventNames.select, this, "onSelect");
	dojo.event.topic.subscribe(this.selector.eventNames.deselect, this, "onDeselect");
}, onSelect:function (message) {
	message.node.viewEmphasize();
}, onDeselect:function (message) {
	message.node.viewUnemphasize();
}});

