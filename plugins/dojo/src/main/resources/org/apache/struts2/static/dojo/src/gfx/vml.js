/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.gfx.vml");
dojo.require("dojo.dom");
dojo.require("dojo.math");
dojo.require("dojo.lang.declare");
dojo.require("dojo.lang.extras");
dojo.require("dojo.string.*");
dojo.require("dojo.html.metrics");
dojo.require("dojo.gfx.color");
dojo.require("dojo.gfx.common");
dojo.require("dojo.gfx.shape");
dojo.require("dojo.gfx.path");
dojo.require("dojo.experimental");
dojo.experimental("dojo.gfx.vml");
dojo.gfx.vml.xmlns = "urn:schemas-microsoft-com:vml";
dojo.gfx.vml._parseFloat = function (str) {
	return str.match(/^\d+f$/i) ? parseInt(str) / 65536 : parseFloat(str);
};
dojo.gfx.vml.cm_in_pt = 72 / 2.54;
dojo.gfx.vml.mm_in_pt = 7.2 / 2.54;
dojo.gfx.vml.px_in_pt = function () {
	return dojo.html.getCachedFontMeasurements()["12pt"] / 12;
};
dojo.gfx.vml.pt2px = function (len) {
	return len * this.px_in_pt();
};
dojo.gfx.vml.px2pt = function (len) {
	return len / this.px_in_pt();
};
dojo.gfx.vml.normalizedLength = function (len) {
	if (len.length == 0) {
		return 0;
	}
	if (len.length > 2) {
		var px_in_pt = this.px_in_pt();
		var val = parseFloat(len);
		switch (len.slice(-2)) {
		  case "px":
			return val;
		  case "pt":
			return val * px_in_pt;
		  case "in":
			return val * 72 * px_in_pt;
		  case "pc":
			return val * 12 * px_in_pt;
		  case "mm":
			return val / this.mm_in_pt * px_in_pt;
		  case "cm":
			return val / this.cm_in_pt * px_in_pt;
		}
	}
	return parseFloat(len);
};
dojo.lang.extend(dojo.gfx.Shape, {setFill:function (fill) {
	if (!fill) {
		this.fillStyle = null;
		this.rawNode.filled = false;
		return this;
	}
	if (typeof (fill) == "object" && "type" in fill) {
		switch (fill.type) {
		  case "linear":
			var f = dojo.gfx.makeParameters(dojo.gfx.defaultLinearGradient, fill);
			this.fillStyle = f;
			var s = "";
			for (var i = 0; i < f.colors.length; ++i) {
				f.colors[i].color = dojo.gfx.normalizeColor(f.colors[i].color);
				s += f.colors[i].offset.toFixed(8) + " " + f.colors[i].color.toHex() + ";";
			}
			var fo = this.rawNode.fill;
			fo.colors.value = s;
			fo.method = "sigma";
			fo.type = "gradient";
			fo.angle = (dojo.math.radToDeg(Math.atan2(f.x2 - f.x1, f.y2 - f.y1)) + 180) % 360;
			fo.on = true;
			break;
		  case "radial":
			var f = dojo.gfx.makeParameters(dojo.gfx.defaultRadialGradient, fill);
			this.fillStyle = f;
			var w = parseFloat(this.rawNode.style.width);
			var h = parseFloat(this.rawNode.style.height);
			var c = isNaN(w) ? 1 : 2 * f.r / w;
			var i = f.colors.length - 1;
			f.colors[i].color = dojo.gfx.normalizeColor(f.colors[i].color);
			var s = "0 " + f.colors[i].color.toHex();
			for (; i >= 0; --i) {
				f.colors[i].color = dojo.gfx.normalizeColor(f.colors[i].color);
				s += (1 - c * f.colors[i].offset).toFixed(8) + " " + f.colors[i].color.toHex() + ";";
			}
			var fo = this.rawNode.fill;
			fo.colors.value = s;
			fo.method = "sigma";
			fo.type = "gradientradial";
			if (isNaN(w) || isNaN(h)) {
				fo.focusposition = "0.5 0.5";
			} else {
				fo.focusposition = (f.cx / w).toFixed(8) + " " + (f.cy / h).toFixed(8);
			}
			fo.focussize = "0 0";
			fo.on = true;
			break;
		  case "pattern":
			var f = dojo.gfx.makeParameters(dojo.gfx.defaultPattern, fill);
			this.fillStyle = f;
			var fo = this.rawNode.fill;
			fo.type = "tile";
			fo.src = f.src;
			if (f.width && f.height) {
				fo.size.x = dojo.gfx.vml.px2pt(f.width);
				fo.size.y = dojo.gfx.vml.px2pt(f.height);
			}
			fo.alignShape = false;
			fo.position.x = 0;
			fo.position.y = 0;
			fo.origin.x = f.width ? f.x / f.width : 0;
			fo.origin.y = f.height ? f.y / f.height : 0;
			fo.on = true;
			break;
		}
		this.rawNode.fill.opacity = 1;
		return this;
	}
	this.fillStyle = dojo.gfx.normalizeColor(fill);
	this.rawNode.fillcolor = this.fillStyle.toHex();
	this.rawNode.fill.opacity = this.fillStyle.a;
	this.rawNode.filled = true;
	return this;
}, setStroke:function (stroke) {
	if (!stroke) {
		this.strokeStyle = null;
		this.rawNode.stroked = false;
		return this;
	}
	this.strokeStyle = dojo.gfx.makeParameters(dojo.gfx.defaultStroke, stroke);
	this.strokeStyle.color = dojo.gfx.normalizeColor(this.strokeStyle.color);
	var s = this.strokeStyle;
	this.rawNode.stroked = true;
	this.rawNode.strokecolor = s.color.toCss();
	this.rawNode.strokeweight = s.width + "px";
	if (this.rawNode.stroke) {
		this.rawNode.stroke.opacity = s.color.a;
		this.rawNode.stroke.endcap = this._translate(this._capMap, s.cap);
		if (typeof (s.join) == "number") {
			this.rawNode.stroke.joinstyle = "miter";
			this.rawNode.stroke.miterlimit = s.join;
		} else {
			this.rawNode.stroke.joinstyle = s.join;
		}
	}
	return this;
}, _capMap:{butt:"flat"}, _capMapReversed:{flat:"butt"}, _translate:function (dict, value) {
	return (value in dict) ? dict[value] : value;
}, _applyTransform:function () {
	var matrix = this._getRealMatrix();
	if (!matrix) {
		return this;
	}
	var skew = this.rawNode.skew;
	if (typeof (skew) == "undefined") {
		for (var i = 0; i < this.rawNode.childNodes.length; ++i) {
			if (this.rawNode.childNodes[i].tagName == "skew") {
				skew = this.rawNode.childNodes[i];
				break;
			}
		}
	}
	if (skew) {
		skew.on = false;
		var mt = matrix.xx.toFixed(8) + " " + matrix.xy.toFixed(8) + " " + matrix.yx.toFixed(8) + " " + matrix.yy.toFixed(8) + " 0 0";
		var offset = Math.floor(matrix.dx).toFixed() + "px " + Math.floor(matrix.dy).toFixed() + "px";
		var l = parseFloat(this.rawNode.style.left);
		var t = parseFloat(this.rawNode.style.top);
		var w = parseFloat(this.rawNode.style.width);
		var h = parseFloat(this.rawNode.style.height);
		if (isNaN(l)) {
			l = 0;
		}
		if (isNaN(t)) {
			t = 0;
		}
		if (isNaN(w)) {
			w = 1;
		}
		if (isNaN(h)) {
			h = 1;
		}
		var origin = (-l / w - 0.5).toFixed(8) + " " + (-t / h - 0.5).toFixed(8);
		skew.matrix = mt;
		skew.origin = origin;
		skew.offset = offset;
		skew.on = true;
	}
	return this;
}, setRawNode:function (rawNode) {
	rawNode.stroked = false;
	rawNode.filled = false;
	this.rawNode = rawNode;
}, attachFill:function (rawNode) {
	var fillStyle = null;
	var fo = rawNode.fill;
	if (rawNode) {
		if (fo.on && fo.type == "gradient") {
			var fillStyle = dojo.lang.shallowCopy(dojo.gfx.defaultLinearGradient, true);
			var rad = dojo.math.degToRad(fo.angle);
			fillStyle.x2 = Math.cos(rad);
			fillStyle.y2 = Math.sin(rad);
			fillStyle.colors = [];
			var stops = fo.colors.value.split(";");
			for (var i = 0; i < stops.length; ++i) {
				var t = stops[i].match(/\S+/g);
				if (!t || t.length != 2) {
					continue;
				}
				fillStyle.colors.push({offset:dojo.gfx.vml._parseFloat(t[0]), color:new dojo.gfx.color.Color(t[1])});
			}
		} else {
			if (fo.on && fo.type == "gradientradial") {
				var fillStyle = dojo.lang.shallowCopy(dojo.gfx.defaultRadialGradient, true);
				var w = parseFloat(rawNode.style.width);
				var h = parseFloat(rawNode.style.height);
				fillStyle.cx = isNaN(w) ? 0 : fo.focusposition.x * w;
				fillStyle.cy = isNaN(h) ? 0 : fo.focusposition.y * h;
				fillStyle.r = isNaN(w) ? 1 : w / 2;
				fillStyle.colors = [];
				var stops = fo.colors.value.split(";");
				for (var i = stops.length - 1; i >= 0; --i) {
					var t = stops[i].match(/\S+/g);
					if (!t || t.length != 2) {
						continue;
					}
					fillStyle.colors.push({offset:dojo.gfx.vml._parseFloat(t[0]), color:new dojo.gfx.color.Color(t[1])});
				}
			} else {
				if (fo.on && fo.type == "tile") {
					var fillStyle = dojo.lang.shallowCopy(dojo.gfx.defaultPattern, true);
					fillStyle.width = dojo.gfx.vml.pt2px(fo.size.x);
					fillStyle.height = dojo.gfx.vml.pt2px(fo.size.y);
					fillStyle.x = fo.origin.x * fillStyle.width;
					fillStyle.y = fo.origin.y * fillStyle.height;
					fillStyle.src = fo.src;
				} else {
					if (fo.on && rawNode.fillcolor) {
						fillStyle = new dojo.gfx.color.Color(rawNode.fillcolor + "");
						fillStyle.a = fo.opacity;
					}
				}
			}
		}
	}
	return fillStyle;
}, attachStroke:function (rawNode) {
	var strokeStyle = dojo.lang.shallowCopy(dojo.gfx.defaultStroke, true);
	if (rawNode && rawNode.stroked) {
		strokeStyle.color = new dojo.gfx.color.Color(rawNode.strokecolor.value);
		dojo.debug("We are expecting an .75pt here, instead of strokeweight = " + rawNode.strokeweight);
		strokeStyle.width = dojo.gfx.vml.normalizedLength(rawNode.strokeweight + "");
		strokeStyle.color.a = rawNode.stroke.opacity;
		strokeStyle.cap = this._translate(this._capMapReversed, rawNode.stroke.endcap);
		strokeStyle.join = rawNode.stroke.joinstyle == "miter" ? rawNode.stroke.miterlimit : rawNode.stroke.joinstyle;
	} else {
		return null;
	}
	return strokeStyle;
}, attachTransform:function (rawNode) {
	var matrix = {};
	if (rawNode) {
		var s = rawNode.skew;
		matrix.xx = s.matrix.xtox;
		matrix.xy = s.matrix.ytox;
		matrix.yx = s.matrix.xtoy;
		matrix.yy = s.matrix.ytoy;
		matrix.dx = dojo.gfx.vml.pt2px(s.offset.x);
		matrix.dy = dojo.gfx.vml.pt2px(s.offset.y);
	}
	return dojo.gfx.matrix.normalize(matrix);
}, attach:function (rawNode) {
	if (rawNode) {
		this.rawNode = rawNode;
		this.shape = this.attachShape(rawNode);
		this.fillStyle = this.attachFill(rawNode);
		this.strokeStyle = this.attachStroke(rawNode);
		this.matrix = this.attachTransform(rawNode);
	}
}});
dojo.declare("dojo.gfx.Group", dojo.gfx.shape.VirtualGroup, {add:function (shape) {
	if (this != shape.getParent()) {
		this.rawNode.appendChild(shape.rawNode);
		dojo.gfx.Group.superclass.add.apply(this, arguments);
	}
	return this;
}, remove:function (shape, silently) {
	if (this == shape.getParent()) {
		if (this.rawNode == shape.rawNode.parentNode) {
			this.rawNode.removeChild(shape.rawNode);
		}
		dojo.gfx.Group.superclass.remove.apply(this, arguments);
	}
	return this;
}, attach:function (rawNode) {
	if (rawNode) {
		this.rawNode = rawNode;
		this.shape = null;
		this.fillStyle = null;
		this.strokeStyle = null;
		this.matrix = null;
	}
}});
dojo.gfx.Group.nodeType = "group";
var zIndex = {moveToFront:function () {
	this.rawNode.parentNode.appendChild(this.rawNode);
	return this;
}, moveToBack:function () {
	this.rawNode.parentNode.insertBefore(this.rawNode, this.rawNode.parentNode.firstChild);
	return this;
}};
dojo.lang.extend(dojo.gfx.Shape, zIndex);
dojo.lang.extend(dojo.gfx.Group, zIndex);
delete zIndex;
dojo.declare("dojo.gfx.Rect", dojo.gfx.shape.Rect, {attachShape:function (rawNode) {
	var arcsize = rawNode.outerHTML.match(/arcsize = \"(\d*\.?\d+[%f]?)\"/)[1];
	arcsize = (arcsize.indexOf("%") >= 0) ? parseFloat(arcsize) / 100 : dojo.gfx.vml._parseFloat(arcsize);
	var style = rawNode.style;
	var width = parseFloat(style.width);
	var height = parseFloat(style.height);
	var o = dojo.gfx.makeParameters(dojo.gfx.defaultRect, {x:parseInt(style.left), y:parseInt(style.top), width:width, height:height, r:Math.min(width, height) * arcsize});
	return o;
}, setShape:function (newShape) {
	var shape = this.shape = dojo.gfx.makeParameters(this.shape, newShape);
	this.bbox = null;
	var style = this.rawNode.style;
	style.left = shape.x.toFixed();
	style.top = shape.y.toFixed();
	style.width = (typeof (shape.width) == "string" && shape.width.indexOf("%") >= 0) ? shape.width : shape.width.toFixed();
	style.height = (typeof (shape.width) == "string" && shape.height.indexOf("%") >= 0) ? shape.height : shape.height.toFixed();
	var r = Math.min(1, (shape.r / Math.min(parseFloat(shape.width), parseFloat(shape.height)))).toFixed(8);
	var parent = this.rawNode.parentNode;
	var before = null;
	if (parent) {
		if (parent.lastChild != this.rawNode) {
			for (var i = 0; i < parent.childNodes.length; ++i) {
				if (parent.childNodes[i] == this.rawNode) {
					before = parent.childNodes[i + 1];
					break;
				}
			}
		}
		parent.removeChild(this.rawNode);
	}
	this.rawNode.arcsize = r;
	if (parent) {
		if (before) {
			parent.insertBefore(this.rawNode, before);
		} else {
			parent.appendChild(this.rawNode);
		}
	}
	return this.setTransform(this.matrix);
}});
dojo.gfx.Rect.nodeType = "roundrect";
dojo.declare("dojo.gfx.Ellipse", dojo.gfx.shape.Ellipse, {attachShape:function (rawNode) {
	var style = this.rawNode.style;
	var rx = parseInt(style.width) / 2;
	var ry = parseInt(style.height) / 2;
	var o = dojo.gfx.makeParameters(dojo.gfx.defaultEllipse, {cx:parseInt(style.left) + rx, cy:parseInt(style.top) + ry, rx:rx, ry:ry});
	return o;
}, setShape:function (newShape) {
	var shape = this.shape = dojo.gfx.makeParameters(this.shape, newShape);
	this.bbox = null;
	var style = this.rawNode.style;
	style.left = (shape.cx - shape.rx).toFixed();
	style.top = (shape.cy - shape.ry).toFixed();
	style.width = (shape.rx * 2).toFixed();
	style.height = (shape.ry * 2).toFixed();
	return this.setTransform(this.matrix);
}});
dojo.gfx.Ellipse.nodeType = "oval";
dojo.declare("dojo.gfx.Circle", dojo.gfx.shape.Circle, {attachShape:function (rawNode) {
	var style = this.rawNode.style;
	var r = parseInt(style.width) / 2;
	var o = dojo.gfx.makeParameters(dojo.gfx.defaultCircle, {cx:parseInt(style.left) + r, cy:parseInt(style.top) + r, r:r});
	return o;
}, setShape:function (newShape) {
	var shape = this.shape = dojo.gfx.makeParameters(this.shape, newShape);
	this.bbox = null;
	var style = this.rawNode.style;
	style.left = (shape.cx - shape.r).toFixed();
	style.top = (shape.cy - shape.r).toFixed();
	style.width = (shape.r * 2).toFixed();
	style.height = (shape.r * 2).toFixed();
	return this;
}});
dojo.gfx.Circle.nodeType = "oval";
dojo.declare("dojo.gfx.Line", dojo.gfx.shape.Line, function (rawNode) {
	if (rawNode) {
		rawNode.setAttribute("dojoGfxType", "line");
	}
}, {attachShape:function (rawNode) {
	var p = rawNode.path.v.match(dojo.gfx.pathRegExp);
	var shape = {};
	do {
		if (p.length < 7 || p[0] != "m" || p[3] != "l" || p[6] != "e") {
			break;
		}
		shape.x1 = parseInt(p[1]);
		shape.y1 = parseInt(p[2]);
		shape.x2 = parseInt(p[4]);
		shape.y2 = parseInt(p[5]);
	} while (false);
	return dojo.gfx.makeParameters(dojo.gfx.defaultLine, shape);
}, setShape:function (newShape) {
	var shape = this.shape = dojo.gfx.makeParameters(this.shape, newShape);
	this.bbox = null;
	this.rawNode.path.v = "m" + shape.x1.toFixed() + " " + shape.y1.toFixed() + "l" + shape.x2.toFixed() + " " + shape.y2.toFixed() + "e";
	return this.setTransform(this.matrix);
}});
dojo.gfx.Line.nodeType = "shape";
dojo.declare("dojo.gfx.Polyline", dojo.gfx.shape.Polyline, function (rawNode) {
	if (rawNode) {
		rawNode.setAttribute("dojoGfxType", "polyline");
	}
}, {attachShape:function (rawNode) {
	var shape = dojo.lang.shallowCopy(dojo.gfx.defaultPolyline, true);
	var p = rawNode.path.v.match(dojo.gfx.pathRegExp);
	do {
		if (p.length < 3 || p[0] != "m") {
			break;
		}
		var x = parseInt(p[0]);
		var y = parseInt(p[1]);
		if (isNaN(x) || isNaN(y)) {
			break;
		}
		shape.points.push({x:x, y:y});
		if (p.length < 6 || p[3] != "l") {
			break;
		}
		for (var i = 4; i < p.length; i += 2) {
			x = parseInt(p[i]);
			y = parseInt(p[i + 1]);
			if (isNaN(x) || isNaN(y)) {
				break;
			}
			shape.points.push({x:x, y:y});
		}
	} while (false);
	return shape;
}, setShape:function (points, closed) {
	if (points && points instanceof Array) {
		this.shape = dojo.gfx.makeParameters(this.shape, {points:points});
		if (closed && this.shape.points.length) {
			this.shape.points.push(this.shape.points[0]);
		}
	} else {
		this.shape = dojo.gfx.makeParameters(this.shape, points);
	}
	this.bbox = null;
	var attr = [];
	var p = this.shape.points;
	if (p.length > 0) {
		attr.push("m");
		attr.push(p[0].x.toFixed());
		attr.push(p[0].y.toFixed());
		if (p.length > 1) {
			attr.push("l");
			for (var i = 1; i < p.length; ++i) {
				attr.push(p[i].x.toFixed());
				attr.push(p[i].y.toFixed());
			}
		}
	}
	attr.push("e");
	this.rawNode.path.v = attr.join(" ");
	return this.setTransform(this.matrix);
}});
dojo.gfx.Polyline.nodeType = "shape";
dojo.declare("dojo.gfx.Image", dojo.gfx.shape.Image, {getEventSource:function () {
	return this.rawNode ? this.rawNode.firstChild : null;
}, attachShape:function (rawNode) {
	var shape = dojo.lang.shallowCopy(dojo.gfx.defaultImage, true);
	shape.src = rawNode.firstChild.src;
	return shape;
}, setShape:function (newShape) {
	var shape = this.shape = dojo.gfx.makeParameters(this.shape, newShape);
	this.bbox = null;
	var firstChild = this.rawNode.firstChild;
	firstChild.src = shape.src;
	if (shape.width || shape.height) {
		firstChild.style.width = shape.width;
		firstChild.style.height = shape.height;
	}
	return this.setTransform(this.matrix);
}, setStroke:function () {
	return this;
}, setFill:function () {
	return this;
}, attachStroke:function (rawNode) {
	return null;
}, attachFill:function (rawNode) {
	return null;
}, attachTransform:function (rawNode) {
	var matrix = {};
	if (rawNode) {
		var m = rawNode.filters["DXImageTransform.Microsoft.Matrix"];
		matrix.xx = m.M11;
		matrix.xy = m.M12;
		matrix.yx = m.M21;
		matrix.yy = m.M22;
		matrix.dx = m.Dx;
		matrix.dy = m.Dy;
	}
	return dojo.gfx.matrix.normalize(matrix);
}, _applyTransform:function () {
	var matrix = this._getRealMatrix();
	if (!matrix) {
		return this;
	}
	with (this.rawNode.filters["DXImageTransform.Microsoft.Matrix"]) {
		M11 = matrix.xx;
		M12 = matrix.xy;
		M21 = matrix.yx;
		M22 = matrix.yy;
		Dx = matrix.dx;
		Dy = matrix.dy;
	}
	return this;
}});
dojo.gfx.Image.nodeType = "image";
dojo.gfx.path._calcArc = function (alpha) {
	var cosa = Math.cos(alpha);
	var sina = Math.sin(alpha);
	var p2 = {x:cosa + (4 / 3) * (1 - cosa), y:sina - (4 / 3) * cosa * (1 - cosa) / sina};
	return {s:{x:cosa, y:sina}, c1:p2, c2:{x:p2.x, y:-p2.y}, e:{x:cosa, y:-sina}};
};
dojo.declare("dojo.gfx.Path", dojo.gfx.path.Path, function (rawNode) {
	if (rawNode) {
		rawNode.setAttribute("dojoGfxType", "path");
	}
	this.vmlPath = "";
	this.lastControl = {};
}, {_updateWithSegment:function (segment) {
	var last = dojo.lang.shallowCopy(this.last);
	dojo.gfx.Path.superclass._updateWithSegment.apply(this, arguments);
	var path = this[this.renderers[segment.action]](segment, last);
	if (typeof (this.vmlPath) == "string") {
		this.vmlPath += path.join("");
	} else {
		this.vmlPath = this.vmlPath.concat(path);
	}
	if (typeof (this.vmlPath) == "string") {
		this.rawNode.path.v = this.vmlPath + " e";
	}
}, attachShape:function (rawNode) {
	var shape = dojo.lang.shallowCopy(dojo.gfx.defaultPath, true);
	var p = rawNode.path.v.match(dojo.gfx.pathRegExp);
	var t = [], skip = false;
	for (var i = 0; i < p.length; ++p) {
		var s = p[i];
		if (s in this._pathVmlToSvgMap) {
			skip = false;
			t.push(this._pathVmlToSvgMap[s]);
		} else {
			if (!skip) {
				var n = parseInt(s);
				if (isNaN(n)) {
					skip = true;
				} else {
					t.push(n);
				}
			}
		}
	}
	if (t.length) {
		shape.path = t.join(" ");
	}
	return shape;
}, setShape:function (newShape) {
	this.vmlPath = [];
	this.lastControl = {};
	dojo.gfx.Path.superclass.setShape.apply(this, arguments);
	this.vmlPath = this.vmlPath.join("");
	this.rawNode.path.v = this.vmlPath + " e";
	return this;
}, _pathVmlToSvgMap:{m:"M", l:"L", t:"m", r:"l", c:"C", v:"c", qb:"Q", x:"z", e:""}, renderers:{M:"_moveToA", m:"_moveToR", L:"_lineToA", l:"_lineToR", H:"_hLineToA", h:"_hLineToR", V:"_vLineToA", v:"_vLineToR", C:"_curveToA", c:"_curveToR", S:"_smoothCurveToA", s:"_smoothCurveToR", Q:"_qCurveToA", q:"_qCurveToR", T:"_qSmoothCurveToA", t:"_qSmoothCurveToR", A:"_arcTo", a:"_arcTo", Z:"_closePath", z:"_closePath"}, _addArgs:function (path, args, from, upto) {
	if (typeof (upto) == "undefined") {
		upto = args.length;
	}
	if (typeof (from) == "undefined") {
		from = 0;
	}
	for (var i = from; i < upto; ++i) {
		path.push(" ");
		path.push(args[i].toFixed());
	}
}, _addArgsAdjusted:function (path, last, args, from, upto) {
	if (typeof (upto) == "undefined") {
		upto = args.length;
	}
	if (typeof (from) == "undefined") {
		from = 0;
	}
	for (var i = from; i < upto; i += 2) {
		path.push(" ");
		path.push((last.x + args[i]).toFixed());
		path.push(" ");
		path.push((last.y + args[i + 1]).toFixed());
	}
}, _moveToA:function (segment) {
	var p = [" m"];
	var n = segment.args;
	var l = n.length;
	if (l == 2) {
		this._addArgs(p, n);
	} else {
		this._addArgs(p, n, 0, 2);
		p.push(" l");
		this._addArgs(p, n, 2);
	}
	this.lastControl = {};
	return p;
}, _moveToR:function (segment, last) {
	var p = ["x" in last ? " t" : " m"];
	var n = segment.args;
	var l = n.length;
	if (l == 2) {
		this._addArgs(p, n);
	} else {
		this._addArgs(p, n, 0, 2);
		p.push(" r");
		this._addArgs(p, n, 2);
	}
	this.lastControl = {};
	return p;
}, _lineToA:function (segment) {
	var p = [" l"];
	this._addArgs(p, segment.args);
	this.lastControl = {};
	return p;
}, _lineToR:function (segment) {
	var p = [" r"];
	this._addArgs(p, segment.args);
	this.lastControl = {};
	return p;
}, _hLineToA:function (segment, last) {
	var p = [" l"];
	var n = segment.args;
	var l = n.length;
	var y = " " + last.y.toFixed();
	for (var i = 0; i < l; ++i) {
		p.push(" ");
		p.push(n[i].toFixed());
		p.push(y);
	}
	this.lastControl = {};
	return p;
}, _hLineToR:function (segment) {
	var p = [" r"];
	var n = segment.args;
	var l = n.length;
	for (var i = 0; i < l; ++i) {
		p.push(" ");
		p.push(n[i].toFixed());
		p.push(" 0");
	}
	this.lastControl = {};
	return p;
}, _vLineToA:function (segment, last) {
	var p = [" l"];
	var n = segment.args;
	var l = n.length;
	var x = " " + last.x.toFixed();
	for (var i = 0; i < l; ++i) {
		p.push(x);
		p.push(" ");
		p.push(n[i].toFixed());
	}
	this.lastControl = {};
	return p;
}, _vLineToR:function (segment) {
	var p = [" r"];
	var n = segment.args;
	var l = n.length;
	for (var i = 0; i < l; ++i) {
		p.push(" 0 ");
		p.push(n[i].toFixed());
	}
	this.lastControl = {};
	return p;
}, _curveToA:function (segment) {
	var p = [];
	var n = segment.args;
	var l = n.length;
	for (var i = 0; i < l; i += 6) {
		p.push(" c");
		this._addArgs(p, n, i, i + 6);
	}
	this.lastControl = {x:n[l - 4], y:n[l - 3], type:"C"};
	return p;
}, _curveToR:function (segment, last) {
	var p = [];
	var n = segment.args;
	var l = n.length;
	for (var i = 0; i < l; i += 6) {
		p.push(" v");
		this._addArgs(p, n, i, i + 6);
		this.lastControl = {x:last.x + n[i + 2], y:last.y + n[i + 3]};
		last.x += n[i + 4];
		last.y += n[i + 5];
	}
	this.lastControl.type = "C";
	return p;
}, _smoothCurveToA:function (segment, last) {
	var p = [];
	var n = segment.args;
	var l = n.length;
	for (var i = 0; i < l; i += 4) {
		p.push(" c");
		if (this.lastControl.type == "C") {
			this._addArgs(p, [2 * last.x - this.lastControl.x, 2 * last.y - this.lastControl.y]);
		} else {
			this._addArgs(p, [last.x, last.y]);
		}
		this._addArgs(p, n, i, i + 4);
	}
	this.lastControl = {x:n[l - 4], y:n[l - 3], type:"C"};
	return p;
}, _smoothCurveToR:function (segment, last) {
	var p = [];
	var n = segment.args;
	var l = n.length;
	for (var i = 0; i < l; i += 4) {
		p.push(" v");
		if (this.lastControl.type == "C") {
			this._addArgs(p, [last.x - this.lastControl.x, last.y - this.lastControl.y]);
		} else {
			this._addArgs(p, [0, 0]);
		}
		this._addArgs(p, n, i, i + 4);
		this.lastControl = {x:last.x + n[i], y:last.y + n[i + 1]};
		last.x += n[i + 2];
		last.y += n[i + 3];
	}
	this.lastControl.type = "C";
	return p;
}, _qCurveToA:function (segment) {
	var p = [];
	var n = segment.args;
	var l = n.length;
	for (var i = 0; i < l; i += 4) {
		p.push(" qb");
		this._addArgs(p, n, i, i + 4);
	}
	this.lastControl = {x:n[l - 4], y:n[l - 3], type:"Q"};
	return p;
}, _qCurveToR:function (segment, last) {
	var p = [];
	var n = segment.args;
	var l = n.length;
	for (var i = 0; i < l; i += 4) {
		p.push(" qb");
		this._addArgsAdjusted(p, last, n, i, i + 4);
		this.lastControl = {x:last.x + n[i], y:last.y + n[i + 1]};
		last.x += n[i + 2];
		last.y += n[i + 3];
	}
	this.lastControl.type = "Q";
	return p;
}, _qSmoothCurveToA:function (segment, last) {
	var p = [];
	var n = segment.args;
	var l = n.length;
	for (var i = 0; i < l; i += 2) {
		p.push(" qb");
		if (this.lastControl.type == "Q") {
			this._addArgs(p, [this.lastControl.x = 2 * last.x - this.lastControl.x, this.lastControl.y = 2 * last.y - this.lastControl.y]);
		} else {
			this._addArgs(p, [this.lastControl.x = last.x, this.lastControl.y = last.y]);
		}
		this._addArgs(p, n, i, i + 2);
	}
	this.lastControl.type = "Q";
	return p;
}, _qSmoothCurveToR:function (segment, last) {
	var p = [];
	var n = segment.args;
	var l = n.length;
	for (var i = 0; i < l; i += 2) {
		p.push(" qb");
		if (this.lastControl.type == "Q") {
			this._addArgs(p, [this.lastControl.x = 2 * last.x - this.lastControl.x, this.lastControl.y = 2 * last.y - this.lastControl.y]);
		} else {
			this._addArgs(p, [this.lastControl.x = last.x, this.lastControl.y = last.y]);
		}
		this._addArgsAdjusted(p, last, n, i, i + 2);
	}
	this.lastControl.type = "Q";
	return p;
}, _PI4:Math.PI / 4, _curvePI4:dojo.gfx.path._calcArc(Math.PI / 8), _calcArcTo:function (path, last, rx, ry, xRotg, large, cw, x, y) {
	var m = dojo.gfx.matrix;
	var xRot = -dojo.math.degToRad(xRotg);
	var rx2 = rx * rx;
	var ry2 = ry * ry;
	var pa = m.multiplyPoint(m.rotate(-xRot), {x:(last.x - x) / 2, y:(last.y - y) / 2});
	var pax2 = pa.x * pa.x;
	var pay2 = pa.y * pa.y;
	var c1 = Math.sqrt((rx2 * ry2 - rx2 * pay2 - ry2 * pax2) / (rx2 * pay2 + ry2 * pax2));
	var ca = {x:c1 * rx * pa.y / ry, y:-c1 * ry * pa.x / rx};
	if (large == cw) {
		ca = {x:-ca.x, y:-ca.y};
	}
	var c = m.multiplyPoint([m.translate((last.x + x) / 2, (last.y + y) / 2), m.rotate(xRot)], ca);
	var startAngle = Math.atan2(c.y - last.y, last.x - c.x) - xRot;
	var endAngle = Math.atan2(c.y - y, x - c.x) - xRot;
	var theta = cw ? startAngle - endAngle : endAngle - startAngle;
	if (theta < 0) {
		theta += this._2PI;
	} else {
		if (theta > this._2PI) {
			theta = this._2PI;
		}
	}
	var elliptic_transform = m.normalize([m.translate(c.x, c.y), m.rotate(xRot), m.scale(rx, ry)]);
	var alpha = this._PI4 / 2;
	var curve = this._curvePI4;
	var step = cw ? -alpha : alpha;
	for (var angle = theta; angle > 0; angle -= this._PI4) {
		if (angle < this._PI4) {
			alpha = angle / 2;
			curve = dojo.gfx.path._calcArc(alpha);
			step = cw ? -alpha : alpha;
		}
		var c1, c2, e;
		var M = m.normalize([elliptic_transform, m.rotate(startAngle + step)]);
		if (cw) {
			c1 = m.multiplyPoint(M, curve.c2);
			c2 = m.multiplyPoint(M, curve.c1);
			e = m.multiplyPoint(M, curve.s);
		} else {
			c1 = m.multiplyPoint(M, curve.c1);
			c2 = m.multiplyPoint(M, curve.c2);
			e = m.multiplyPoint(M, curve.e);
		}
		path.push(" c");
		this._addArgs(path, [c1.x, c1.y, c2.x, c2.y, e.x, e.y]);
		startAngle += 2 * step;
	}
}, _arcTo:function (segment, last) {
	var p = [];
	var n = segment.args;
	var l = n.length;
	var relative = segment.action == "a";
	for (var i = 0; i < l; i += 7) {
		var x1 = n[i + 5];
		var y1 = n[i + 6];
		if (relative) {
			x1 += last.x;
			y1 += last.y;
		}
		this._calcArcTo(p, last, n[i], n[i + 1], n[i + 2], n[i + 3] ? 1 : 0, n[i + 4] ? 1 : 0, x1, y1);
		last = {x:x1, y:y1};
	}
	this.lastControl = {};
	return p;
}, _closePath:function () {
	this.lastControl = {};
	return ["x"];
}});
dojo.gfx.Path.nodeType = "shape";
dojo.gfx._creators = {createPath:function (path) {
	return this.createObject(dojo.gfx.Path, path, true);
}, createRect:function (rect) {
	return this.createObject(dojo.gfx.Rect, rect);
}, createCircle:function (circle) {
	return this.createObject(dojo.gfx.Circle, circle);
}, createEllipse:function (ellipse) {
	return this.createObject(dojo.gfx.Ellipse, ellipse);
}, createLine:function (line) {
	return this.createObject(dojo.gfx.Line, line, true);
}, createPolyline:function (points) {
	return this.createObject(dojo.gfx.Polyline, points, true);
}, createImage:function (image) {
	if (!this.rawNode) {
		return null;
	}
	var shape = new dojo.gfx.Image();
	var node = document.createElement("div");
	node.style.position = "relative";
	node.style.width = this.rawNode.style.width;
	node.style.height = this.rawNode.style.height;
	node.style.filter = "progid:DXImageTransform.Microsoft.Matrix(M11=1, M12=0, M21=0, M22=1, Dx=0, Dy=0)";
	var img = document.createElement("img");
	node.appendChild(img);
	shape.setRawNode(node);
	this.rawNode.appendChild(node);
	shape.setShape(image);
	this.add(shape);
	return shape;
}, createGroup:function () {
	return this.createObject(dojo.gfx.Group, null, true);
}, createObject:function (shapeType, rawShape, overrideSize) {
	if (!this.rawNode) {
		return null;
	}
	var shape = new shapeType();
	var node = document.createElement("v:" + shapeType.nodeType);
	shape.setRawNode(node);
	this.rawNode.appendChild(node);
	if (overrideSize) {
		this._overrideSize(node);
	}
	shape.setShape(rawShape);
	this.add(shape);
	return shape;
}, _overrideSize:function (node) {
	node.style.width = this.rawNode.style.width;
	node.style.height = this.rawNode.style.height;
	node.coordsize = parseFloat(node.style.width) + " " + parseFloat(node.style.height);
}};
dojo.lang.extend(dojo.gfx.Group, dojo.gfx._creators);
dojo.lang.extend(dojo.gfx.Surface, dojo.gfx._creators);
delete dojo.gfx._creators;
dojo.gfx.attachNode = function (node) {
	if (!node) {
		return null;
	}
	var s = null;
	switch (node.tagName.toLowerCase()) {
	  case dojo.gfx.Rect.nodeType:
		s = new dojo.gfx.Rect();
		break;
	  case dojo.gfx.Ellipse.nodeType:
		s = (node.style.width == node.style.height) ? new dojo.gfx.Circle() : new dojo.gfx.Ellipse();
		break;
	  case dojo.gfx.Path.nodeType:
		switch (node.getAttribute("dojoGfxType")) {
		  case "line":
			s = new dojo.gfx.Line();
			break;
		  case "polyline":
			s = new dojo.gfx.Polyline();
			break;
		  case "path":
			s = new dojo.gfx.Path();
			break;
		}
		break;
	  case dojo.gfx.Image.nodeType:
		s = new dojo.gfx.Image();
		break;
	  default:
		dojo.debug("FATAL ERROR! tagName = " + node.tagName);
	}
	s.attach(node);
	return s;
};
dojo.lang.extend(dojo.gfx.Surface, {setDimensions:function (width, height) {
	if (!this.rawNode) {
		return this;
	}
	this.rawNode.style.width = width;
	this.rawNode.style.height = height;
	this.rawNode.coordsize = width + " " + height;
	return this;
}, getDimensions:function () {
	return this.rawNode ? {width:this.rawNode.style.width, height:this.rawNode.style.height} : null;
}, add:function (shape) {
	var oldParent = shape.getParent();
	if (this != oldParent) {
		this.rawNode.appendChild(shape.rawNode);
		if (oldParent) {
			oldParent.remove(shape, true);
		}
		shape._setParent(this, null);
	}
	return this;
}, remove:function (shape, silently) {
	if (this == shape.getParent()) {
		if (this.rawNode == shape.rawNode.parentNode) {
			this.rawNode.removeChild(shape.rawNode);
		}
		shape._setParent(null, null);
	}
	return this;
}});
dojo.gfx.createSurface = function (parentNode, width, height) {
	var s = new dojo.gfx.Surface();
	s.rawNode = document.createElement("v:group");
	s.rawNode.style.width = width ? width : "100%";
	s.rawNode.style.height = height ? height : "100%";
	s.rawNode.coordsize = (width && height) ? (parseFloat(width) + " " + parseFloat(height)) : "100% 100%";
	s.rawNode.coordorigin = "0 0";
	dojo.byId(parentNode).appendChild(s.rawNode);
	return s;
};
dojo.gfx.attachSurface = function (node) {
	var s = new dojo.gfx.Surface();
	s.rawNode = node;
	return s;
};

