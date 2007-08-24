/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.RadioGroup");
dojo.require("dojo.lang.common");
dojo.require("dojo.event.browser");
dojo.require("dojo.html.selection");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.HtmlWidget");
dojo.widget.defineWidget("dojo.widget.RadioGroup", dojo.widget.HtmlWidget, function () {
	this.selectedItem = null;
	this.items = [];
	this.selected = [];
	this.groupCssClass = "radioGroup";
	this.selectedCssClass = "selected";
	this.itemContentCssClass = "itemContent";
}, {isContainer:false, templatePath:null, templateCssPath:null, postCreate:function () {
	this._parseStructure();
	dojo.html.addClass(this.domNode, this.groupCssClass);
	this._setupChildren();
	dojo.event.browser.addListener(this.domNode, "onclick", dojo.lang.hitch(this, "onSelect"));
	if (this.selectedItem) {
		this._selectItem(this.selectedItem);
	}
}, _parseStructure:function () {
	if (this.domNode.tagName.toLowerCase() != "ul" && this.domNode.tagName.toLowerCase() != "ol") {
		dojo.raise("RadioGroup: Expected ul or ol content.");
		return;
	}
	this.items = [];
	var nl = this.domNode.getElementsByTagName("li");
	for (var i = 0; i < nl.length; i++) {
		if (nl[i].parentNode == this.domNode) {
			this.items.push(nl[i]);
		}
	}
}, add:function (node) {
	if (node.parentNode != this.domNode) {
		this.domNode.appendChild(node);
	}
	this.items.push(node);
	this._setup(node);
}, remove:function (node) {
	var idx = -1;
	for (var i = 0; i < this.items.length; i++) {
		if (this.items[i] == node) {
			idx = i;
			break;
		}
	}
	if (idx < 0) {
		return;
	}
	this.items.splice(idx, 1);
	node.parentNode.removeChild(node);
}, clear:function () {
	for (var i = 0; i < this.items.length; i++) {
		this.domNode.removeChild(this.items[i]);
	}
	this.items = [];
}, clearSelections:function () {
	for (var i = 0; i < this.items.length; i++) {
		dojo.html.removeClass(this.items[i], this.selectedCssClass);
	}
	this.selectedItem = null;
}, _setup:function (node) {
	var span = document.createElement("span");
	dojo.html.disableSelection(span);
	dojo.html.addClass(span, this.itemContentCssClass);
	dojo.dom.moveChildren(node, span);
	node.appendChild(span);
	if (this.selected.length > 0) {
		var uid = dojo.html.getAttribute(node, "id");
		if (uid && uid == this.selected) {
			this.selectedItem = node;
		}
	}
	dojo.event.browser.addListener(node, "onclick", dojo.lang.hitch(this, "onItemSelect"));
	if (dojo.html.hasAttribute(node, "onitemselect")) {
		var tn = dojo.lang.nameAnonFunc(new Function(dojo.html.getAttribute(node, "onitemselect")), this);
		dojo.event.browser.addListener(node, "onclick", dojo.lang.hitch(this, tn));
	}
}, _setupChildren:function () {
	for (var i = 0; i < this.items.length; i++) {
		this._setup(this.items[i]);
	}
}, _selectItem:function (node, event, nofire) {
	if (this.selectedItem) {
		dojo.html.removeClass(this.selectedItem, this.selectedCssClass);
	}
	this.selectedItem = node;
	dojo.html.addClass(this.selectedItem, this.selectedCssClass);
	if (!dj_undef("currentTarget", event)) {
		return;
	}
	if (!nofire) {
		if (dojo.render.html.ie) {
			this.selectedItem.fireEvent("onclick");
		} else {
			var e = document.createEvent("MouseEvents");
			e.initEvent("click", true, false);
			this.selectedItem.dispatchEvent(e);
		}
	}
}, getValue:function () {
	return this.selectedItem;
}, onSelect:function (e) {
}, onItemSelect:function (e) {
	if (!dj_undef("currentTarget", e)) {
		this._selectItem(e.currentTarget, e);
	}
}});

