/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.SvgButton");
dojo.require("dojo.experimental");
dojo.experimental("dojo.widget.SvgButton");
dojo.widget.SvgButton = function () {
	dojo.widget.DomButton.call(this);
	dojo.widget.SvgWidget.call(this);
	this.onFoo = function () {
		alert("bar");
	};
	this.label = "huzzah!";
	this.setLabel = function (x, y, textSize, label, shape) {
		var coords = dojo.widget.SvgButton.prototype.coordinates(x, y, textSize, label, shape);
		var textString = "";
		switch (shape) {
		  case "ellipse":
			textString = "<text x='" + coords[6] + "' y='" + coords[7] + "'>" + label + "</text>";
			break;
		  case "rectangle":
			textString = "";
			break;
		  case "circle":
			textString = "";
			break;
		}
		return textString;
	};
	this.fillInTemplate = function (x, y, textSize, label, shape) {
		this.textSize = textSize || 12;
		this.label = label;
		var textWidth = this.label.length * this.textSize;
	};
};
dojo.inherits(dojo.widget.SvgButton, dojo.widget.DomButton);
dojo.widget.SvgButton.prototype.shapeString = function (x, y, textSize, label, shape) {
	switch (shape) {
	  case "ellipse":
		var coords = dojo.widget.SvgButton.prototype.coordinates(x, y, textSize, label, shape);
		return "<ellipse cx='" + coords[4] + "' cy='" + coords[5] + "' rx='" + coords[2] + "' ry='" + coords[3] + "'/>";
		break;
	  case "rect":
		return "";
		break;
	  case "circle":
		return "";
		break;
	}
};
dojo.widget.SvgButton.prototype.coordinates = function (x, y, textSize, label, shape) {
	switch (shape) {
	  case "ellipse":
		var buttonWidth = label.length * textSize;
		var buttonHeight = textSize * 2.5;
		var rx = buttonWidth / 2;
		var ry = buttonHeight / 2;
		var cx = rx + x;
		var cy = ry + y;
		var textX = cx - rx * textSize / 25;
		var textY = cy * 1.1;
		return [buttonWidth, buttonHeight, rx, ry, cx, cy, textX, textY];
		break;
	  case "rectangle":
		return "";
		break;
	  case "circle":
		return "";
		break;
	}
};
dojo.widget.SvgButton.prototype.labelString = function (x, y, textSize, label, shape) {
	var textString = "";
	var coords = dojo.widget.SvgButton.prototype.coordinates(x, y, textSize, label, shape);
	switch (shape) {
	  case "ellipse":
		textString = "<text x='" + coords[6] + "' y='" + coords[7] + "'>" + label + "</text>";
		break;
	  case "rectangle":
		textString = "";
		break;
	  case "circle":
		textString = "";
		break;
	}
	return textString;
};
dojo.widget.SvgButton.prototype.templateString = function (x, y, textSize, label, shape) {
	return "<g class='dojoButton' dojoAttachEvent='onClick; onMouseMove: onFoo;' dojoAttachPoint='labelNode'>" + dojo.widgets.SVGButton.prototype.shapeString(x, y, textSize, label, shape) + dojo.widget.SVGButton.prototype.labelString(x, y, textSize, label, shape) + "</g>";
};

