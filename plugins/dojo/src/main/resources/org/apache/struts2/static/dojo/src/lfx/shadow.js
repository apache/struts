/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.lfx.shadow");
dojo.require("dojo.lang.common");
dojo.require("dojo.uri.Uri");
dojo.lfx.shadow = function (node) {
	this.shadowPng = dojo.uri.moduleUri("dojo.html", "images/shadow");
	this.shadowThickness = 8;
	this.shadowOffset = 15;
	this.init(node);
};
dojo.extend(dojo.lfx.shadow, {init:function (node) {
	this.node = node;
	this.pieces = {};
	var x1 = -1 * this.shadowThickness;
	var y0 = this.shadowOffset;
	var y1 = this.shadowOffset + this.shadowThickness;
	this._makePiece("tl", "top", y0, "left", x1);
	this._makePiece("l", "top", y1, "left", x1, "scale");
	this._makePiece("tr", "top", y0, "left", 0);
	this._makePiece("r", "top", y1, "left", 0, "scale");
	this._makePiece("bl", "top", 0, "left", x1);
	this._makePiece("b", "top", 0, "left", 0, "crop");
	this._makePiece("br", "top", 0, "left", 0);
}, _makePiece:function (name, vertAttach, vertCoord, horzAttach, horzCoord, sizing) {
	var img;
	var url = this.shadowPng + name.toUpperCase() + ".png";
	if (dojo.render.html.ie55 || dojo.render.html.ie60) {
		img = dojo.doc().createElement("div");
		img.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='" + url + "'" + (sizing ? ", sizingMethod='" + sizing + "'" : "") + ")";
	} else {
		img = dojo.doc().createElement("img");
		img.src = url;
	}
	img.style.position = "absolute";
	img.style[vertAttach] = vertCoord + "px";
	img.style[horzAttach] = horzCoord + "px";
	img.style.width = this.shadowThickness + "px";
	img.style.height = this.shadowThickness + "px";
	this.pieces[name] = img;
	this.node.appendChild(img);
}, size:function (width, height) {
	var sideHeight = height - (this.shadowOffset + this.shadowThickness + 1);
	if (sideHeight < 0) {
		sideHeight = 0;
	}
	if (height < 1) {
		height = 1;
	}
	if (width < 1) {
		width = 1;
	}
	with (this.pieces) {
		l.style.height = sideHeight + "px";
		r.style.height = sideHeight + "px";
		b.style.width = (width - 1) + "px";
		bl.style.top = (height - 1) + "px";
		b.style.top = (height - 1) + "px";
		br.style.top = (height - 1) + "px";
		tr.style.left = (width - 1) + "px";
		r.style.left = (width - 1) + "px";
		br.style.left = (width - 1) + "px";
	}
}});

