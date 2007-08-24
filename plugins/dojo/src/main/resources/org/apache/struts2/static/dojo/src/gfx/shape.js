/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.gfx.shape");
dojo.require("dojo.lang.declare");
dojo.require("dojo.gfx.common");
dojo.declare("dojo.gfx.Shape", null, {initializer:function () {
	this.rawNode = null;
	this.shape = null;
	this.matrix = null;
	this.fillStyle = null;
	this.strokeStyle = null;
	this.bbox = null;
	this.parent = null;
	this.parentMatrix = null;
}, getNode:function () {
	return this.rawNode;
}, getShape:function () {
	return this.shape;
}, getTransform:function () {
	return this.matrix;
}, getFill:function () {
	return this.fillStyle;
}, getStroke:function () {
	return this.strokeStyle;
}, getParent:function () {
	return this.parent;
}, getBoundingBox:function () {
	return this.bbox;
}, getEventSource:function () {
	return this.rawNode;
}, setShape:function (shape) {
	return this;
}, setFill:function (fill) {
	return this;
}, setStroke:function (stroke) {
	return this;
}, moveToFront:function () {
	return this;
}, moveToBack:function () {
	return this;
}, setTransform:function (matrix) {
	this.matrix = dojo.gfx.matrix.clone(matrix ? dojo.gfx.matrix.normalize(matrix) : dojo.gfx.identity, true);
	return this._applyTransform();
}, applyRightTransform:function (matrix) {
	return matrix ? this.setTransform([this.matrix, matrix]) : this;
}, applyLeftTransform:function (matrix) {
	return matrix ? this.setTransform([matrix, this.matrix]) : this;
}, applyTransform:function (matrix) {
	return matrix ? this.setTransform([this.matrix, matrix]) : this;
}, remove:function (silently) {
	if (this.parent) {
		this.parent.remove(this, silently);
	}
	return this;
}, _setParent:function (parent, matrix) {
	this.parent = parent;
	return this._updateParentMatrix(matrix);
}, _updateParentMatrix:function (matrix) {
	this.parentMatrix = matrix ? dojo.gfx.matrix.clone(matrix) : null;
	return this._applyTransform();
}, _getRealMatrix:function () {
	return this.parentMatrix ? new dojo.gfx.matrix.Matrix2D([this.parentMatrix, this.matrix]) : this.matrix;
}});
dojo.declare("dojo.gfx.shape.VirtualGroup", dojo.gfx.Shape, {initializer:function () {
	this.children = [];
}, add:function (shape) {
	var oldParent = shape.getParent();
	if (oldParent) {
		oldParent.remove(shape, true);
	}
	this.children.push(shape);
	return shape._setParent(this, this._getRealMatrix());
}, remove:function (shape, silently) {
	for (var i = 0; i < this.children.length; ++i) {
		if (this.children[i] == shape) {
			if (silently) {
			} else {
				shape._setParent(null, null);
			}
			this.children.splice(i, 1);
			break;
		}
	}
	return this;
}, _applyTransform:function () {
	var matrix = this._getRealMatrix();
	for (var i = 0; i < this.children.length; ++i) {
		this.children[i]._updateParentMatrix(matrix);
	}
	return this;
}});
dojo.declare("dojo.gfx.shape.Rect", dojo.gfx.Shape, {initializer:function (rawNode) {
	this.shape = dojo.lang.shallowCopy(dojo.gfx.defaultRect, true);
	this.attach(rawNode);
}, getBoundingBox:function () {
	return this.shape;
}});
dojo.declare("dojo.gfx.shape.Ellipse", dojo.gfx.Shape, {initializer:function (rawNode) {
	this.shape = dojo.lang.shallowCopy(dojo.gfx.defaultEllipse, true);
	this.attach(rawNode);
}, getBoundingBox:function () {
	if (!this.bbox) {
		var shape = this.shape;
		this.bbox = {x:shape.cx - shape.rx, y:shape.cy - shape.ry, width:2 * shape.rx, height:2 * shape.ry};
	}
	return this.bbox;
}});
dojo.declare("dojo.gfx.shape.Circle", dojo.gfx.Shape, {initializer:function (rawNode) {
	this.shape = dojo.lang.shallowCopy(dojo.gfx.defaultCircle, true);
	this.attach(rawNode);
}, getBoundingBox:function () {
	if (!this.bbox) {
		var shape = this.shape;
		this.bbox = {x:shape.cx - shape.r, y:shape.cy - shape.r, width:2 * shape.r, height:2 * shape.r};
	}
	return this.bbox;
}});
dojo.declare("dojo.gfx.shape.Line", dojo.gfx.Shape, {initializer:function (rawNode) {
	this.shape = dojo.lang.shallowCopy(dojo.gfx.defaultLine, true);
	this.attach(rawNode);
}, getBoundingBox:function () {
	if (!this.bbox) {
		var shape = this.shape;
		this.bbox = {x:Math.min(shape.x1, shape.x2), y:Math.min(shape.y1, shape.y2), width:Math.abs(shape.x2 - shape.x1), height:Math.abs(shape.y2 - shape.y1)};
	}
	return this.bbox;
}});
dojo.declare("dojo.gfx.shape.Polyline", dojo.gfx.Shape, {initializer:function (rawNode) {
	this.shape = dojo.lang.shallowCopy(dojo.gfx.defaultPolyline, true);
	this.attach(rawNode);
}, getBoundingBox:function () {
	if (!this.bbox && this.shape.points.length) {
		var p = this.shape.points;
		var l = p.length;
		var t = p[0];
		var bbox = {l:t.x, t:t.y, r:t.x, b:t.y};
		for (var i = 1; i < l; ++i) {
			t = p[i];
			if (bbox.l > t.x) {
				bbox.l = t.x;
			}
			if (bbox.r < t.x) {
				bbox.r = t.x;
			}
			if (bbox.t > t.y) {
				bbox.t = t.y;
			}
			if (bbox.b < t.y) {
				bbox.b = t.y;
			}
		}
		this.bbox = {x:bbox.l, y:bbox.t, width:bbox.r - bbox.l, height:bbox.b - bbox.t};
	}
	return this.bbox;
}});
dojo.declare("dojo.gfx.shape.Image", dojo.gfx.Shape, {initializer:function (rawNode) {
	this.shape = dojo.lang.shallowCopy(dojo.gfx.defaultImage, true);
	this.attach(rawNode);
}, getBoundingBox:function () {
	if (!this.bbox) {
		var shape = this.shape;
		this.bbox = {x:0, y:0, width:shape.width, height:shape.height};
	}
	return this.bbox;
}});

