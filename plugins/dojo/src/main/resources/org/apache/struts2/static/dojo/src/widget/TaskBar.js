/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.TaskBar");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.FloatingPane");
dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.event.*");
dojo.require("dojo.html.selection");
dojo.widget.defineWidget("dojo.widget.TaskBarItem", dojo.widget.HtmlWidget, {iconSrc:"", caption:"Untitled", templateString:"<div class=\"dojoTaskBarItem\" dojoAttachEvent=\"onClick\">\n</div>\n", templateCssString:".dojoTaskBarItem {\n\tdisplay: inline-block;\n\tbackground-color: ThreeDFace;\n\tborder: outset 2px;\n\tmargin-right: 5px;\n\tcursor: pointer;\n\theight: 35px;\n\twidth: 100px;\n\tfont-size: 10pt;\n\twhite-space: nowrap;\n\ttext-align: center;\n\tfloat: left;\n\toverflow: hidden;\n}\n\n.dojoTaskBarItem img {\n\tvertical-align: middle;\n\tmargin-right: 5px;\n\tmargin-left: 5px;\t\n\theight: 32px;\n\twidth: 32px;\n}\n\n.dojoTaskBarItem a {\n\t color: black;\n\ttext-decoration: none;\n}\n\n\n", templateCssPath:dojo.uri.moduleUri("dojo.widget", "templates/TaskBar.css"), fillInTemplate:function () {
	if (this.iconSrc) {
		var img = document.createElement("img");
		img.src = this.iconSrc;
		this.domNode.appendChild(img);
	}
	this.domNode.appendChild(document.createTextNode(this.caption));
	dojo.html.disableSelection(this.domNode);
}, postCreate:function () {
	this.window = dojo.widget.getWidgetById(this.windowId);
	this.window.explodeSrc = this.domNode;
	dojo.event.connect(this.window, "destroy", this, "destroy");
}, onClick:function () {
	this.window.toggleDisplay();
}});
dojo.widget.defineWidget("dojo.widget.TaskBar", dojo.widget.FloatingPane, function () {
	this._addChildStack = [];
}, {resizable:false, titleBarDisplay:false, addChild:function (child) {
	if (!this.containerNode) {
		this._addChildStack.push(child);
	} else {
		if (this._addChildStack.length > 0) {
			var oarr = this._addChildStack;
			this._addChildStack = [];
			dojo.lang.forEach(oarr, this.addChild, this);
		}
	}
	var tbi = dojo.widget.createWidget("TaskBarItem", {windowId:child.widgetId, caption:child.title, iconSrc:child.iconSrc});
	dojo.widget.TaskBar.superclass.addChild.call(this, tbi);
}});

