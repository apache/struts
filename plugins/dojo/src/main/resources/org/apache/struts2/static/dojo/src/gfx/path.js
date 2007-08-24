/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.gfx.path");
dojo.require("dojo.math");
dojo.require("dojo.gfx.shape");
dojo.declare("dojo.gfx.path.Path", dojo.gfx.Shape, {initializer:function (rawNode) {
	this.shape = dojo.lang.shallowCopy(dojo.gfx.defaultPath, true);
	this.segments = [];
	this.absolute = true;
	this.last = {};
	this.attach(rawNode);
}, setAbsoluteMode:function (mode) {
	this.absolute = typeof (mode) == "string" ? (mode == "absolute") : mode;
	return this;
}, getAbsoluteMode:function () {
	return this.absolute;
}, getBoundingBox:function () {
	return "l" in this.bbox ? {x:this.bbox.l, y:this.bbox.t, width:this.bbox.r - this.bbox.l, height:this.bbox.b - this.bbox.t} : null;
}, getLastPosition:function () {
	return "x" in this.last ? this.last : null;
}, _updateBBox:function (x, y) {
	if ("l" in this.bbox) {
		if (this.bbox.l > x) {
			this.bbox.l = x;
		}
		if (this.bbox.r < x) {
			this.bbox.r = x;
		}
		if (this.bbox.t > y) {
			this.bbox.t = y;
		}
		if (this.bbox.b < y) {
			this.bbox.b = y;
		}
	} else {
		this.bbox = {l:x, b:y, r:x, t:y};
	}
}, _updateWithSegment:function (segment) {
	var n = segment.args;
	var l = n.length;
	switch (segment.action) {
	  case "M":
	  case "L":
	  case "C":
	  case "S":
	  case "Q":
	  case "T":
		for (var i = 0; i < l; i += 2) {
			this._updateBBox(this.bbox, n[i], n[i + 1]);
		}
		this.last.x = n[l - 2];
		this.last.y = n[l - 1];
		this.absolute = true;
		break;
	  case "H":
		for (var i = 0; i < l; ++i) {
			this._updateBBox(this.bbox, n[i], this.last.y);
		}
		this.last.x = n[l - 1];
		this.absolute = true;
		break;
	  case "V":
		for (var i = 0; i < l; ++i) {
			this._updateBBox(this.bbox, this.last.x, n[i]);
		}
		this.last.y = n[l - 1];
		this.absolute = true;
		break;
	  case "m":
		var start = 0;
		if (!("x" in this.last)) {
			this._updateBBox(this.bbox, this.last.x = n[0], this.last.y = n[1]);
			start = 2;
		}
		for (var i = start; i < l; i += 2) {
			this._updateBBox(this.bbox, this.last.x += n[i], this.last.y += n[i + 1]);
		}
		this.absolute = false;
		break;
	  case "l":
	  case "t":
		for (var i = 0; i < l; i += 2) {
			this._updateBBox(this.bbox, this.last.x += n[i], this.last.y += n[i + 1]);
		}
		this.absolute = false;
		break;
	  case "h":
		for (var i = 0; i < l; ++i) {
			this._updateBBox(this.bbox, this.last.x += n[i], this.last.y);
		}
		this.absolute = false;
		break;
	  case "v":
		for (var i = 0; i < l; ++i) {
			this._updateBBox(this.bbox, this.last.x, this.last.y += n[i]);
		}
		this.absolute = false;
		break;
	  case "c":
		for (var i = 0; i < l; i += 6) {
			this._updateBBox(this.bbox, this.last.x + n[i], this.last.y + n[i + 1]);
			this._updateBBox(this.bbox, this.last.x + n[i + 2], this.last.y + n[i + 3]);
			this._updateBBox(this.bbox, this.last.x += n[i + 4], this.last.y += n[i + 5]);
		}
		this.absolute = false;
		break;
	  case "s":
	  case "q":
		for (var i = 0; i < l; i += 4) {
			this._updateBBox(this.bbox, this.last.x + n[i], this.last.y + n[i + 1]);
			this._updateBBox(this.bbox, this.last.x += n[i + 2], this.last.y += n[i + 3]);
		}
		this.absolute = false;
		break;
	  case "A":
		for (var i = 0; i < l; i += 7) {
			this._updateBBox(this.bbox, n[i + 5], n[i + 6]);
		}
		this.last.x = n[l - 2];
		this.last.y = n[l - 1];
		this.absolute = true;
		break;
	  case "a":
		for (var i = 0; i < l; i += 7) {
			this._updateBBox(this.bbox, this.last.x += n[i + 5], this.last.y += n[i + 6]);
		}
		this.absolute = false;
		break;
	}
	var path = [segment.action];
	for (var i = 0; i < l; ++i) {
		path.push(dojo.gfx.formatNumber(n[i], true));
	}
	if (typeof (this.shape.path) == "string") {
		this.shape.path += path.join("");
	} else {
		this.shape.path = this.shape.path.concat(path);
	}
}, _validSegments:{m:2, l:2, h:1, v:1, c:6, s:4, q:4, t:2, a:7, z:0}, _pushSegment:function (action, args) {
	var group = this._validSegments[action.toLowerCase()];
	if (typeof (group) == "number") {
		if (group) {
			if (args.length >= group) {
				var segment = {action:action, args:args.slice(0, args.length - args.length % group)};
				this.segments.push(segment);
				this._updateWithSegment(segment);
			}
		} else {
			var segment = {action:action, args:[]};
			this.segments.push(segment);
			this._updateWithSegment(segment);
		}
	}
}, _collectArgs:function (array, args) {
	for (var i = 0; i < args.length; ++i) {
		var t = args[i];
		if (typeof (t) == "boolean") {
			array.push(t ? 1 : 0);
		} else {
			if (typeof (t) == "number") {
				array.push(t);
			} else {
				if (t instanceof Array) {
					this._collectArgs(array, t);
				} else {
					if ("x" in t && "y" in t) {
						array.push(t.x);
						array.push(t.y);
					}
				}
			}
		}
	}
}, moveTo:function () {
	var args = [];
	this._collectArgs(args, arguments);
	this._pushSegment(this.absolute ? "M" : "m", args);
	return this;
}, lineTo:function () {
	var args = [];
	this._collectArgs(args, arguments);
	this._pushSegment(this.absolute ? "L" : "l", args);
	return this;
}, hLineTo:function () {
	var args = [];
	this._collectArgs(args, arguments);
	this._pushSegment(this.absolute ? "H" : "h", args);
	return this;
}, vLineTo:function () {
	var args = [];
	this._collectArgs(args, arguments);
	this._pushSegment(this.absolute ? "V" : "v", args);
	return this;
}, curveTo:function () {
	var args = [];
	this._collectArgs(args, arguments);
	this._pushSegment(this.absolute ? "C" : "c", args);
	return this;
}, smoothCurveTo:function () {
	var args = [];
	this._collectArgs(args, arguments);
	this._pushSegment(this.absolute ? "S" : "s", args);
	return this;
}, qCurveTo:function () {
	var args = [];
	this._collectArgs(args, arguments);
	this._pushSegment(this.absolute ? "Q" : "q", args);
	return this;
}, qSmoothCurveTo:function () {
	var args = [];
	this._collectArgs(args, arguments);
	this._pushSegment(this.absolute ? "T" : "t", args);
	return this;
}, arcTo:function () {
	var args = [];
	this._collectArgs(args, arguments);
	for (var i = 2; i < args.length; i += 7) {
		args[i] = -args[i];
	}
	this._pushSegment(this.absolute ? "A" : "a", args);
	return this;
}, closePath:function () {
	this._pushSegment("Z", []);
	return this;
}, _setPath:function (path) {
	var p = path.match(dojo.gfx.pathRegExp);
	this.segments = [];
	this.absolute = true;
	this.bbox = {};
	this.last = {};
	if (!p) {
		return;
	}
	var action = "";
	var args = [];
	for (var i = 0; i < p.length; ++i) {
		var t = p[i];
		var x = parseFloat(t);
		if (isNaN(x)) {
			if (action) {
				this._pushSegment(action, args);
			}
			args = [];
			action = t;
		} else {
			args.push(x);
		}
	}
	this._pushSegment(action, args);
}, setShape:function (newShape) {
	this.shape = dojo.gfx.makeParameters(this.shape, typeof (newShape) == "string" ? {path:newShape} : newShape);
	var path = this.shape.path;
	this.shape.path = [];
	this._setPath(path);
	this.shape.path = this.shape.path.join("");
	return this;
}, _2PI:Math.PI * 2});

