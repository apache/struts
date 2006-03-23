/*
	Copyright (c) 2004-2005, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.ResizeHandle");
dojo.provide("dojo.widget.html.ResizeHandle");

dojo.require("dojo.widget.*");
dojo.require("dojo.html");
dojo.require("dojo.style");
dojo.require("dojo.dom");
dojo.require("dojo.event");

dojo.widget.html.ResizeHandle = function(){
	dojo.widget.HtmlWidget.call(this);
}

dojo.inherits(dojo.widget.html.ResizeHandle, dojo.widget.HtmlWidget);

dojo.lang.extend(dojo.widget.html.ResizeHandle, {
	widgetType: "ResizeHandle",

	isSizing: false,
	startPoint: null,
	startSize: null,

	grabImg: null,

	targetElmId: '',
	imgSrc: dojo.uri.dojoUri("src/widget/templates/grabCorner.gif"),

	templateCssPath: dojo.uri.dojoUri("src/widget/templates/HtmlResizeHandle.css"),
	templateString: '<div dojoAttachPoint="domNode"><img dojoAttachPoint="grabImg" /></div>',

	fillInTemplate: function(){

		dojo.style.insertCssFile(this.templateCssPath);

		dojo.html.addClass(this.domNode, 'dojoHtmlResizeHandle');
		dojo.html.addClass(this.grabImg, 'dojoHtmlResizeHandleImage');

		this.grabImg.src = this.imgSrc;
	},

	postCreate: function(){
		dojo.event.connect(this.domNode, "onmousedown", this, "beginSizing");
	},

	beginSizing: function(e){
		if (this.isSizing){ return false; }

		this.targetElm = dojo.widget.getWidgetById(this.targetElmId);
		if (!this.targetElm){ return; }

		var screenX = window.event ? window.event.clientX : e.pageX;
		var screenY = window.event ? window.event.clientY : e.pageY;

		this.isSizing = true;
		this.startPoint  = {'x':e.clientX, 'y':e.clientY};
		this.startSize  = {'w':dojo.style.getOuterWidth(this.targetElm.domNode), 'h':dojo.style.getOuterHeight(this.targetElm.domNode)};

		dojo.event.connect(document.documentElement, "onmousemove", this, "changeSizing");
		dojo.event.connect(document.documentElement, "onmouseup", this, "endSizing");

		e.preventDefault();
	},

	changeSizing: function(e){
		var dx = this.startPoint.x - e.clientX;
		var dy = this.startPoint.y - e.clientY;
		this.targetElm.resizeTo(this.startSize.w - dx, this.startSize.h - dy);

		e.preventDefault();
	},

	endSizing: function(e){
		dojo.event.disconnect(document.documentElement, "onmousemove", this, "changeSizing");
		dojo.event.disconnect(document.documentElement, "onmouseup", this, "endSizing");

		this.isSizing = false;
	}


});

dojo.widget.tags.addParseTreeHandler("dojo:ResizeHandle");
