/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.Clock");
dojo.require("dojo.widget.*");
dojo.require("dojo.gfx.*");
dojo.require("dojo.uri.Uri");
dojo.require("dojo.lang.common");
dojo.require("dojo.lang.timing.Timer");
dojo.widget.defineWidget("dojo.widget.Clock", dojo.widget.HtmlWidget, function () {
	var self = this;
	this.timeZoneOffset = 0;
	this.label = "";
	this.date = new Date();
	this.handColor = "#788598";
	this.handStroke = "#6f7b8c";
	this.secondHandColor = [201, 4, 5, 0.8];
	this.topLabelColor = "#efefef";
	this.labelColor = "#fff";
	this.timer = new dojo.lang.timing.Timer(1000);
	this.center = {x:75, y:75};
	this.hands = {hour:null, minute:null, second:null};
	this.shadows = {hour:{shadow:null, shift:{dx:2, dy:2}}, minute:{shadow:null, shift:{dx:2, dy:3}}, second:{shadow:null, shift:{dx:4, dy:4}}};
	this.image = dojo.uri.moduleUri("dojo.widget", "templates/images/clock.png");
	this.surface = null;
	this.labelNode = null;
	this.topLabelNode = null;
	this.draw = function () {
		self.date = new Date();
		var h = (self.date.getHours() + self.timeZoneOffset) % 12;
		var m = self.date.getMinutes();
		var s = self.date.getSeconds();
		self.placeHour(h, m, s);
		self.placeMinute(m, s);
		self.placeSecond(s);
		self.topLabelNode.innerHTML = ((self.date.getHours() + self.timeZoneOffset) > 11) ? "PM" : "AM";
	};
	this.timer.onTick = self.draw;
}, {set:function (dt) {
	this.date = dt;
	if (!this.timer.isRunning) {
		this.draw();
	}
}, start:function () {
	this.timer.start();
}, stop:function () {
	this.timer.stop();
}, _initPoly:function (parent, points) {
	var path = parent.createPath();
	var first = true;
	dojo.lang.forEach(points, function (c) {
		if (first) {
			path.moveTo(c.x, c.y);
			first = false;
		} else {
			path.lineTo(c.x, c.y);
		}
	});
	return path;
}, _placeHand:function (shape, angle, shift) {
	var move = {dx:this.center.x + (shift ? shift.dx : 0), dy:this.center.y + (shift ? shift.dy : 0)};
	return shape.setTransform([move, dojo.gfx.matrix.rotateg(-angle)]);
}, placeHour:function (h, m, s) {
	var angle = 30 * (h + m / 60 + s / 3600);
	this._placeHand(this.hands.hour, angle);
	this._placeHand(this.shadows.hour.shadow, angle, this.shadows.hour.shift);
}, placeMinute:function (m, s) {
	var angle = 6 * (m + s / 60);
	this._placeHand(this.hands.minute, angle);
	this._placeHand(this.shadows.minute.shadow, angle, this.shadows.minute.shift);
}, placeSecond:function (s) {
	var angle = 6 * s;
	this._placeHand(this.hands.second, angle);
	this._placeHand(this.shadows.second.shadow, angle, this.shadows.second.shift);
}, init:function () {
	if (this.domNode.style.position != "absolute") {
		this.domNode.style.position = "relative";
	}
	while (this.domNode.childNodes.length > 0) {
		this.domNode.removeChild(this.domNode.childNodes[0]);
	}
	this.domNode.style.width = "150px";
	this.domNode.style.height = "150px";
	this.surface = dojo.gfx.createSurface(this.domNode, 150, 150);
	this.surface.createRect({width:150, height:150});
	this.surface.createImage({width:150, height:150, src:this.image + ""});
	var hP = [{x:-3, y:-4}, {x:3, y:-4}, {x:1, y:-27}, {x:-1, y:-27}, {x:-3, y:-4}];
	var mP = [{x:-3, y:-4}, {x:3, y:-4}, {x:1, y:-38}, {x:-1, y:-38}, {x:-3, y:-4}];
	var sP = [{x:-2, y:-2}, {x:2, y:-2}, {x:1, y:-45}, {x:-1, y:-45}, {x:-2, y:-2}];
	this.shadows.hour.shadow = this._initPoly(this.surface, hP).setFill([0, 0, 0, 0.1]);
	this.hands.hour = this._initPoly(this.surface, hP).setStroke({color:this.handStroke, width:1}).setFill({type:"linear", x1:0, y1:0, x2:0, y2:-27, colors:[{offset:0, color:"#fff"}, {offset:0.33, color:this.handColor}]});
	this.shadows.minute.shadow = this._initPoly(this.surface, mP).setFill([0, 0, 0, 0.1]);
	this.hands.minute = this._initPoly(this.surface, mP).setStroke({color:this.handStroke, width:1}).setFill({type:"linear", x1:0, y1:0, x2:0, y2:-38, colors:[{offset:0, color:"#fff"}, {offset:0.33, color:this.handColor}]});
	this.surface.createCircle({r:6}).setStroke({color:this.handStroke, width:2}).setFill("#fff").setTransform({dx:75, dy:75});
	this.shadows.second.shadow = this._initPoly(this.surface, sP).setFill([0, 0, 0, 0.1]);
	this.hands.second = this._initPoly(this.surface, sP).setFill(this.secondHandColor);
	this.surface.createCircle({r:4}).setFill(this.secondHandColor).setTransform({dx:75, dy:75});
	this.topLabelNode = document.createElement("div");
	with (this.topLabelNode.style) {
		position = "absolute";
		top = "3px";
		left = "0px";
		color = this.topLabelColor;
		textAlign = "center";
		width = "150px";
		fontFamily = "sans-serif";
		fontSize = "11px";
		textTransform = "uppercase";
		fontWeight = "bold";
	}
	this.topLabelNode.innerHTML = ((this.date.getHours() + this.timeZoneOffset) > 11) ? "PM" : "AM";
	this.domNode.appendChild(this.topLabelNode);
	this.labelNode = document.createElement("div");
	with (this.labelNode.style) {
		position = "absolute";
		top = "134px";
		left = "0px";
		color = this.labelColor;
		textAlign = "center";
		width = "150px";
		fontFamily = "sans-serif";
		fontSize = "10px";
		textTransform = "uppercase";
		fontWeight = "bold";
	}
	this.labelNode.innerHTML = this.label || "&nbsp;";
	this.domNode.appendChild(this.labelNode);
	this.draw();
}, postCreate:function () {
	this.init();
	this.start();
}});

