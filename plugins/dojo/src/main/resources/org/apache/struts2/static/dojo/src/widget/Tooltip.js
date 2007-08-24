/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.Tooltip");
dojo.require("dojo.widget.ContentPane");
dojo.require("dojo.widget.PopupContainer");
dojo.require("dojo.uri.Uri");
dojo.require("dojo.widget.*");
dojo.require("dojo.event.*");
dojo.require("dojo.html.style");
dojo.require("dojo.html.util");
dojo.widget.defineWidget("dojo.widget.Tooltip", [dojo.widget.ContentPane, dojo.widget.PopupContainerBase], {caption:"", showDelay:500, hideDelay:100, connectId:"", templateCssString:".dojoTooltip {\n\tborder: solid black 1px;\n\tbackground: beige;\n\tcolor: black;\n\tposition: absolute;\n\tfont-size: small;\n\tpadding: 2px 2px 2px 2px;\n\tz-index: 10;\n\tdisplay: block;\n}\n", templateCssPath:dojo.uri.moduleUri("dojo.widget", "templates/TooltipTemplate.css"), fillInTemplate:function (args, frag) {
	if (this.caption != "") {
		this.domNode.appendChild(document.createTextNode(this.caption));
	}
	this._connectNode = dojo.byId(this.connectId);
	dojo.widget.Tooltip.superclass.fillInTemplate.call(this, args, frag);
	this.addOnLoad(this, "_loadedContent");
	dojo.html.addClass(this.domNode, "dojoTooltip");
	var source = this.getFragNodeRef(frag);
	dojo.html.copyStyle(this.domNode, source);
	this.applyPopupBasicStyle();
}, postCreate:function (args, frag) {
	dojo.event.connect(this._connectNode, "onmouseover", this, "_onMouseOver");
	dojo.widget.Tooltip.superclass.postCreate.call(this, args, frag);
}, _onMouseOver:function (e) {
	this._mouse = {x:e.pageX, y:e.pageY};
	if (!this._tracking) {
		dojo.event.connect(document.documentElement, "onmousemove", this, "_onMouseMove");
		this._tracking = true;
	}
	this._onHover(e);
}, _onMouseMove:function (e) {
	this._mouse = {x:e.pageX, y:e.pageY};
	if (dojo.html.overElement(this._connectNode, e) || dojo.html.overElement(this.domNode, e)) {
		this._onHover(e);
	} else {
		this._onUnHover(e);
	}
}, _onHover:function (e) {
	if (this._hover) {
		return;
	}
	this._hover = true;
	if (this._hideTimer) {
		clearTimeout(this._hideTimer);
		delete this._hideTimer;
	}
	if (!this.isShowingNow && !this._showTimer) {
		this._showTimer = setTimeout(dojo.lang.hitch(this, "open"), this.showDelay);
	}
}, _onUnHover:function (e) {
	if (!this._hover) {
		return;
	}
	this._hover = false;
	if (this._showTimer) {
		clearTimeout(this._showTimer);
		delete this._showTimer;
	}
	if (this.isShowingNow && !this._hideTimer) {
		this._hideTimer = setTimeout(dojo.lang.hitch(this, "close"), this.hideDelay);
	}
	if (!this.isShowingNow) {
		dojo.event.disconnect(document.documentElement, "onmousemove", this, "_onMouseMove");
		this._tracking = false;
	}
}, open:function () {
	if (this.isShowingNow) {
		return;
	}
	dojo.widget.PopupContainerBase.prototype.open.call(this, this._mouse.x, this._mouse.y, null, [this._mouse.x, this._mouse.y], "TL,TR,BL,BR", [10, 15]);
}, close:function () {
	if (this.isShowingNow) {
		if (this._showTimer) {
			clearTimeout(this._showTimer);
			delete this._showTimer;
		}
		if (this._hideTimer) {
			clearTimeout(this._hideTimer);
			delete this._hideTimer;
		}
		dojo.event.disconnect(document.documentElement, "onmousemove", this, "_onMouseMove");
		this._tracking = false;
		dojo.widget.PopupContainerBase.prototype.close.call(this);
	}
}, _position:function () {
	this.move(this._mouse.x, this._mouse.y, [10, 15], "TL,TR,BL,BR");
}, _loadedContent:function () {
	if (this.isShowingNow) {
		this._position();
	}
}, checkSize:function () {
}, uninitialize:function () {
	this.close();
	dojo.event.disconnect(this._connectNode, "onmouseover", this, "_onMouseOver");
}});

