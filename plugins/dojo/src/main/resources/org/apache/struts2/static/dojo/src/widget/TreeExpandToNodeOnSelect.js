/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.TreeExpandToNodeOnSelect");
dojo.require("dojo.widget.HtmlWidget");
dojo.widget.defineWidget("dojo.widget.TreeExpandToNodeOnSelect", dojo.widget.HtmlWidget, {selector:"", controller:"", withSelected:false, initialize:function () {
	this.selector = dojo.widget.byId(this.selector);
	this.controller = dojo.widget.byId(this.controller);
	dojo.event.topic.subscribe(this.selector.eventNames.select, this, "onSelect");
}, onSelectEvent:function (message) {
	this.controller.expandToNode(message.node, this.withSelected);
}});

