/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.HtmlWidget");
dojo.require("dojo.widget.DomWidget");
dojo.require("dojo.html.util");
dojo.require("dojo.html.display");
dojo.require("dojo.html.layout");
dojo.require("dojo.lang.extras");
dojo.require("dojo.lang.func");
dojo.require("dojo.lfx.toggle");
dojo.declare("dojo.widget.HtmlWidget", dojo.widget.DomWidget, {templateCssPath:null, templatePath:null, lang:"", toggle:"plain", toggleDuration:150, initialize:function (args, frag) {
}, postMixInProperties:function (args, frag) {
	if (this.lang === "") {
		this.lang = null;
	}
	this.toggleObj = dojo.lfx.toggle[this.toggle.toLowerCase()] || dojo.lfx.toggle.plain;
}, createNodesFromText:function (txt, wrap) {
	return dojo.html.createNodesFromText(txt, wrap);
}, destroyRendering:function (finalize) {
	try {
		if (this.bgIframe) {
			this.bgIframe.remove();
			delete this.bgIframe;
		}
		if (!finalize && this.domNode) {
			dojo.event.browser.clean(this.domNode);
		}
		dojo.widget.HtmlWidget.superclass.destroyRendering.call(this);
	}
	catch (e) {
	}
}, isShowing:function () {
	return dojo.html.isShowing(this.domNode);
}, toggleShowing:function () {
	if (this.isShowing()) {
		this.hide();
	} else {
		this.show();
	}
}, show:function () {
	if (this.isShowing()) {
		return;
	}
	this.animationInProgress = true;
	this.toggleObj.show(this.domNode, this.toggleDuration, null, dojo.lang.hitch(this, this.onShow), this.explodeSrc);
}, onShow:function () {
	this.animationInProgress = false;
	this.checkSize();
}, hide:function () {
	if (!this.isShowing()) {
		return;
	}
	this.animationInProgress = true;
	this.toggleObj.hide(this.domNode, this.toggleDuration, null, dojo.lang.hitch(this, this.onHide), this.explodeSrc);
}, onHide:function () {
	this.animationInProgress = false;
}, _isResized:function (w, h) {
	if (!this.isShowing()) {
		return false;
	}
	var wh = dojo.html.getMarginBox(this.domNode);
	var width = w || wh.width;
	var height = h || wh.height;
	if (this.width == width && this.height == height) {
		return false;
	}
	this.width = width;
	this.height = height;
	return true;
}, checkSize:function () {
	if (!this._isResized()) {
		return;
	}
	this.onResized();
}, resizeTo:function (w, h) {
	dojo.html.setMarginBox(this.domNode, {width:w, height:h});
	if (this.isShowing()) {
		this.onResized();
	}
}, resizeSoon:function () {
	if (this.isShowing()) {
		dojo.lang.setTimeout(this, this.onResized, 0);
	}
}, onResized:function () {
	dojo.lang.forEach(this.children, function (child) {
		if (child.checkSize) {
			child.checkSize();
		}
	});
}});

