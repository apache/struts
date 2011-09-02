/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.gfx.matrix");
dojo.require("dojo.lang.common");
dojo.require("dojo.math.*");
dojo.gfx.matrix.Matrix2D = function (arg) {
	if (arg) {
		if (arg instanceof Array) {
			if (arg.length > 0) {
				var m = dojo.gfx.matrix.normalize(arg[0]);
				for (var i = 1; i < arg.length; ++i) {
					var l = m;
					var r = dojo.gfx.matrix.normalize(arg[i]);
					m = new dojo.gfx.matrix.Matrix2D();
					m.xx = l.xx * r.xx + l.xy * r.yx;
					m.xy = l.xx * r.xy + l.xy * r.yy;
					m.yx = l.yx * r.xx + l.yy * r.yx;
					m.yy = l.yx * r.xy + l.yy * r.yy;
					m.dx = l.xx * r.dx + l.xy * r.dy + l.dx;
					m.dy = l.yx * r.dx + l.yy * r.dy + l.dy;
				}
				dojo.mixin(this, m);
			}
		} else {
			dojo.mixin(this, arg);
		}
	}
};
dojo.extend(dojo.gfx.matrix.Matrix2D, {xx:1, xy:0, yx:0, yy:1, dx:0, dy:0});
dojo.mixin(dojo.gfx.matrix, {identity:new dojo.gfx.matrix.Matrix2D(), flipX:new dojo.gfx.matrix.Matrix2D({xx:-1}), flipY:new dojo.gfx.matrix.Matrix2D({yy:-1}), flipXY:new dojo.gfx.matrix.Matrix2D({xx:-1, yy:-1}), translate:function (a, b) {
	if (arguments.length > 1) {
		return new dojo.gfx.matrix.Matrix2D({dx:a, dy:b});
	}
	return new dojo.gfx.matrix.Matrix2D({dx:a.x, dy:a.y});
}, scale:function (a, b) {
	if (arguments.length > 1) {
		return new dojo.gfx.matrix.Matrix2D({xx:a, yy:b});
	}
	if (typeof a == "number") {
		return new dojo.gfx.matrix.Matrix2D({xx:a, yy:a});
	}
	return new dojo.gfx.matrix.Matrix2D({xx:a.x, yy:a.y});
}, rotate:function (angle) {
	var c = Math.cos(angle);
	var s = Math.sin(angle);
	return new dojo.gfx.matrix.Matrix2D({xx:c, xy:s, yx:-s, yy:c});
}, rotateg:function (degree) {
	return dojo.gfx.matrix.rotate(dojo.math.degToRad(degree));
}, skewX:function (angle) {
	return new dojo.gfx.matrix.Matrix2D({xy:Math.tan(angle)});
}, skewXg:function (degree) {
	return dojo.gfx.matrix.skewX(dojo.math.degToRad(degree));
}, skewY:function (angle) {
	return new dojo.gfx.matrix.Matrix2D({yx:-Math.tan(angle)});
}, skewYg:function (degree) {
	return dojo.gfx.matrix.skewY(dojo.math.degToRad(degree));
}, normalize:function (matrix) {
	return (matrix instanceof dojo.gfx.matrix.Matrix2D) ? matrix : new dojo.gfx.matrix.Matrix2D(matrix);
}, clone:function (matrix) {
	var obj = new dojo.gfx.matrix.Matrix2D();
	for (var i in matrix) {
		if (typeof (matrix[i]) == "number" && typeof (obj[i]) == "number" && obj[i] != matrix[i]) {
			obj[i] = matrix[i];
		}
	}
	return obj;
}, invert:function (matrix) {
	var m = dojo.gfx.matrix.normalize(matrix);
	var D = m.xx * m.yy - m.xy * m.yx;
	var M = new dojo.gfx.matrix.Matrix2D({xx:m.yy / D, xy:-m.xy / D, yx:-m.yx / D, yy:m.xx / D, dx:(m.yx * m.dy - m.yy * m.dx) / D, dy:(m.xy * m.dx - m.xx * m.dy) / D});
	return M;
}, _multiplyPoint:function (m, x, y) {
	return {x:m.xx * x + m.xy * y + m.dx, y:m.yx * x + m.yy * y + m.dy};
}, multiplyPoint:function (matrix, a, b) {
	var m = dojo.gfx.matrix.normalize(matrix);
	if (typeof a == "number" && typeof b == "number") {
		return dojo.gfx.matrix._multiplyPoint(m, a, b);
	}
	return dojo.gfx.matrix._multiplyPoint(m, a.x, a.y);
}, multiply:function (matrix) {
	var m = dojo.gfx.matrix.normalize(matrix);
	for (var i = 1; i < arguments.length; ++i) {
		var l = m;
		var r = dojo.gfx.matrix.normalize(arguments[i]);
		m = new dojo.gfx.matrix.Matrix2D();
		m.xx = l.xx * r.xx + l.xy * r.yx;
		m.xy = l.xx * r.xy + l.xy * r.yy;
		m.yx = l.yx * r.xx + l.yy * r.yx;
		m.yy = l.yx * r.xy + l.yy * r.yy;
		m.dx = l.xx * r.dx + l.xy * r.dy + l.dx;
		m.dy = l.yx * r.dx + l.yy * r.dy + l.dy;
	}
	return m;
}, _sandwich:function (m, x, y) {
	return dojo.gfx.matrix.multiply(dojo.gfx.matrix.translate(x, y), m, dojo.gfx.matrix.translate(-x, -y));
}, scaleAt:function (a, b, c, d) {
	switch (arguments.length) {
	  case 4:
		return dojo.gfx.matrix._sandwich(dojo.gfx.matrix.scale(a, b), c, d);
	  case 3:
		if (typeof c == "number") {
			return dojo.gfx.matrix._sandwich(dojo.gfx.matrix.scale(a), b, c);
		}
		return dojo.gfx.matrix._sandwich(dojo.gfx.matrix.scale(a, b), c.x, c.y);
	}
	return dojo.gfx.matrix._sandwich(dojo.gfx.matrix.scale(a), b.x, b.y);
}, rotateAt:function (angle, a, b) {
	if (arguments.length > 2) {
		return dojo.gfx.matrix._sandwich(dojo.gfx.matrix.rotate(angle), a, b);
	}
	return dojo.gfx.matrix._sandwich(dojo.gfx.matrix.rotate(angle), a.x, a.y);
}, rotategAt:function (degree, a, b) {
	if (arguments.length > 2) {
		return dojo.gfx.matrix._sandwich(dojo.gfx.matrix.rotateg(degree), a, b);
	}
	return dojo.gfx.matrix._sandwich(dojo.gfx.matrix.rotateg(degree), a.x, a.y);
}, skewXAt:function (angle, a, b) {
	if (arguments.length > 2) {
		return dojo.gfx.matrix._sandwich(dojo.gfx.matrix.skewX(angle), a, b);
	}
	return dojo.gfx.matrix._sandwich(dojo.gfx.matrix.skewX(angle), a.x, a.y);
}, skewXgAt:function (degree, a, b) {
	if (arguments.length > 2) {
		return dojo.gfx.matrix._sandwich(dojo.gfx.matrix.skewXg(degree), a, b);
	}
	return dojo.gfx.matrix._sandwich(dojo.gfx.matrix.skewXg(degree), a.x, a.y);
}, skewYAt:function (angle, a, b) {
	if (arguments.length > 2) {
		return dojo.gfx.matrix._sandwich(dojo.gfx.matrix.skewY(angle), a, b);
	}
	return dojo.gfx.matrix._sandwich(dojo.gfx.matrix.skewY(angle), a.x, a.y);
}, skewYgAt:function (degree, a, b) {
	if (arguments.length > 2) {
		return dojo.gfx.matrix._sandwich(dojo.gfx.matrix.skewYg(degree), a, b);
	}
	return dojo.gfx.matrix._sandwich(dojo.gfx.matrix.skewYg(degree), a.x, a.y);
}});

