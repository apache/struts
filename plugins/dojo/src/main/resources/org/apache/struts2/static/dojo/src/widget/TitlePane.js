/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.TitlePane");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.ContentPane");
dojo.require("dojo.html.style");
dojo.require("dojo.lfx.*");
dojo.widget.defineWidget("dojo.widget.TitlePane", dojo.widget.ContentPane, {labelNodeClass:"", containerNodeClass:"", label:"", open:true, templateString:"<div dojoAttachPoint=\"domNode\">\n<div dojoAttachPoint=\"labelNode\" dojoAttachEvent=\"onclick: onLabelClick\"></div>\n<div dojoAttachPoint=\"containerNode\"></div>\n</div>\n", postCreate:function () {
	if (this.label) {
		this.labelNode.appendChild(document.createTextNode(this.label));
	}
	if (this.labelNodeClass) {
		dojo.html.addClass(this.labelNode, this.labelNodeClass);
	}
	if (this.containerNodeClass) {
		dojo.html.addClass(this.containerNode, this.containerNodeClass);
	}
	if (!this.open) {
		dojo.html.hide(this.containerNode);
	}
	dojo.widget.TitlePane.superclass.postCreate.apply(this, arguments);
}, onLabelClick:function () {
	if (this.open) {
		dojo.lfx.wipeOut(this.containerNode, 250).play();
		this.open = false;
	} else {
		dojo.lfx.wipeIn(this.containerNode, 250).play();
		this.open = true;
	}
}, setLabel:function (label) {
	this.labelNode.innerHTML = label;
}});

