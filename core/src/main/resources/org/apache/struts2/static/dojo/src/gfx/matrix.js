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

dojo.gfx.matrix.Matrix2D = function(/* Matrix2D */ arg){
	// summary: a constructor for 2D matrix
	// arg: a matrix-like object
	if(arg){
		if(arg instanceof Array){
			if(arg.length > 0){
				var m = dojo.gfx.matrix.normalize(arg[0]);
				// combine matrices
				for(var i = 1; i < arg.length; ++i){
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
		}else{
			dojo.mixin(this, arg);
		}
	}
};

// the default (identity) matrix, which is used to fill in missing values
dojo.extend(dojo.gfx.matrix.Matrix2D, {xx: 1, xy: 0, yx: 0, yy: 1, dx: 0, dy: 0});

dojo.mixin(dojo.gfx.matrix, {
	// summary: class constants, and methods of dojo.gfx.matrix.Matrix2D
	
	// matrix constants
	identity: new dojo.gfx.matrix.Matrix2D(),
	flipX:    new dojo.gfx.matrix.Matrix2D({xx: -1}),
	flipY:    new dojo.gfx.matrix.Matrix2D({yy: -1}),
	flipXY:   new dojo.gfx.matrix.Matrix2D({xx: -1, yy: -1}),
	
	// matrix creators
	translate: function(/* Number||Point */ a, /* Number, optional */ b){
		// summary: forms a translation matrix
		// a: an X coordinate value (a number), or a point object
		// b: an optional Y coordinate value
		return arguments.length > 1 ? new dojo.gfx.matrix.Matrix2D({dx: a, dy: b}) : new dojo.gfx.matrix.Matrix2D({dx: a.x, dy: a.y}); // dojo.gfx.matrix.Matrix2D
	},
	scale: function(/* Number||Point */ a, /* Number||Nothing */ b){
		// summary: forms a scaling matrix
		// a: a scaling factor used for X, or a point object
		// b: an optional scaling factor for Y
		return arguments.length > 1 ? new dojo.gfx.matrix.Matrix2D({xx: a, yy: b}) : typeof a == "number" ? new dojo.gfx.matrix.Matrix2D({xx: a, yy: a}) : new dojo.gfx.matrix.Matrix2D({xx: a.x, yy: a.y}); // dojo.gfx.matrix.Matrix2D
	},
	rotate: function(/* Number */ angle){
		// summary: forms a rotating matrix
		// angle: an angle of rotation in radians (>0 for CCW)
		var c = Math.cos(angle);
		var s = Math.sin(angle);
		return new dojo.gfx.matrix.Matrix2D({xx: c, xy: s, yx: -s, yy: c}); // dojo.gfx.matrix.Matrix2D
	},
	rotateg: function(/* Number */ degree){
		// summary: forms a rotating matrix
		// degree: an angle of rotation in degrees (>0 for CCW)
		return dojo.gfx.matrix.rotate(dojo.math.degToRad(degree)); // dojo.gfx.matrix.Matrix2D
	},
	skewX: function(/* Number */ angle) {
		// summary: forms an X skewing matrix
		// angle: an skewing angle in radians
		return new dojo.gfx.matrix.Matrix2D({xy: Math.tan(angle)}); // dojo.gfx.matrix.Matrix2D
	},
	skewXg: function(/* Number */ degree){
		// summary: forms an X skewing matrix
		// degree: an skewing angle in degrees
		return dojo.gfx.matrix.skewX(dojo.math.degToRad(degree)); // dojo.gfx.matrix.Matrix2D
	},
	skewY: function(/* Number */ angle){
		// summary: forms a Y skewing matrix
		// angle: an skewing angle in radians
		return new dojo.gfx.matrix.Matrix2D({yx: -Math.tan(angle)}); // dojo.gfx.matrix.Matrix2D
	},
	skewYg: function(/* Number */ degree){
		// summary: forms a Y skewing matrix
		// degree: an skewing angle in degrees
		return dojo.gfx.matrix.skewY(dojo.math.degToRad(degree)); // dojo.gfx.matrix.Matrix2D
	},
	
	// ensure matrix 2D conformance
	normalize: function(/* Matrix2D */ matrix){
		// summary: converts an object to a matrix, if necessary
		// matrix: an object, which is converted to a matrix, if necessary
		return (matrix instanceof dojo.gfx.matrix.Matrix2D) ? matrix : new dojo.gfx.matrix.Matrix2D(matrix); // dojo.gfx.matrix.Matrix2D
	},
	
	// common operations
	clone: function(/* Matrix2D */ matrix){
		// summary: creates a copy of a matrix
		// matrix: a matrix object to be cloned
		var obj = new dojo.gfx.matrix.Matrix2D();
		for(var i in matrix){
			if(typeof(matrix[i]) == "number" && typeof(obj[i]) == "number" && obj[i] != matrix[i]) obj[i] = matrix[i];
		}
		return obj; // dojo.gfx.matrix.Matrix2D
	},
	invert: function(/* Matrix2D */ matrix){
		// summary: inverts a matrix
		// matrix: a matrix object to be inverted
		var m = dojo.gfx.matrix.normalize(matrix);
		var D = m.xx * m.yy - m.xy * m.yx;
		return new dojo.gfx.matrix.Matrix2D({xx: m.yy/D, xy: -m.xy/D, yx: -m.yx/D, yy: m.xx/D, dx: (m.yx * m.dy - m.yy * m.dx) / D, dy: (m.xy * m.dx - m.xx * m.dy) / D}); // dojo.gfx.matrix.Matrix2D
	},
	_multiplyPoint: function(/* Matrix2D */ m, /* Number */ x, /* Number */ y){
		// summary: applies a matrix to a point
		// matrix: a matrix object to be applied
		// a: an X coordinate of a point
		// b: a Y coordinate of a point
		return {x: m.xx * x + m.xy * y + m.dx, y: m.yx * x + m.yy * y + m.dy}; // Point
	},
	multiplyPoint: function(/* Matrix */ matrix, /* Number||Point */ a, /* Number, optional */ b){
		// summary: applies a matrix to a point
		// matrix: a matrix-like object to be applied
		// a: an X coordinate of a point, or a point object
		// b: an optional Y coordinate of a point
		var m = dojo.gfx.matrix.normalize(matrix);
		if(typeof a == "number" && typeof b == "number"){
			return dojo.gfx.matrix._multiplyPoint(m, a, b);
		}
		return dojo.gfx.matrix._multiplyPoint(m, a.x, a.y); // Point
	},
	multiply: function(/* Matrix */ matrix){
		// summary: combines matrices by multiplying them
		// matrix: a matrix-like object, all subsequent arguments are matrix-like objects too.
		var m = dojo.gfx.matrix.normalize(matrix);
		// combine matrices
		for(var i = 1; i < arguments.length; ++i){
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
		return m; // dojo.gfx.matrix.Matrix2D
	},
	
	// high level operations
	_sandwich: function(/* Matrix */ m, /* Number */ x, /* Number */ y){
		// summary: applies a matrix at a centrtal point
		// m: a matrix-like object, which is applied at the point
		// x: an X component of the point
		// y: a Y component of the point
		return dojo.gfx.matrix.multiply(dojo.gfx.matrix.translate(x, y), m, dojo.gfx.matrix.translate(-x, -y)); // dojo.gfx.matrix.Matrix2D
	},
	scaleAt: function(/* Number */ a, /* Number, optional */ b, /* Number||Point */ c, /* Number, optional */ d){
		// summary: scales a picture using a specified point as a center of scaling
		
		// accepts several signatures:
		//	1) uniform scale factor, Point
		//	2) uniform scale factor, x, y
		//	3) x scale, y scale, Point
		//	4) x scale, y scale, x, y
		switch(arguments.length){
			case 2:
				// a is a scale factor, b is a point
				return dojo.gfx.matrix._sandwich(dojo.gfx.matrix.scale(a), b.x, b.y); // dojo.gfx.matrix.Matrix2D
			case 3:
				if(typeof c == "number"){
					// a is scale factor, b and c are x and y components of a point
					return dojo.gfx.matrix._sandwich(dojo.gfx.matrix.scale(a), b, c); // dojo.gfx.matrix.Matrix2D
				}
				// a and b are scale factor components, c is a point
				return dojo.gfx.matrix._sandwich(dojo.gfx.matrix.scale(a, b), c.x, c.y); // dojo.gfx.matrix.Matrix2D
		}
		// a and b are scale factor components, c and d are components of a point
		return dojo.gfx.matrix._sandwich(dojo.gfx.matrix.scale(a, b), c, d); // dojo.gfx.matrix.Matrix2D
	},
	rotateAt: function(/* Number */ angle, /* Number||Point */ a, /* Number, optional */ b){
		// summary: rotates a picture using a specified point as a center of rotation
		
		// accepts several signatures:
		//	1) rotation angle in radians, Point
		//	2) rotation angle in radians, x, y
		return arguments.length > 1 ? dojo.gfx.matrix._sandwich(dojo.gfx.matrix.rotate(angle), a, b) : dojo.gfx.matrix._sandwich(dojo.gfx.matrix.rotate(angle), a.x, a.y); // dojo.gfx.matrix.Matrix2D
	},
	rotategAt: function(/* Number */ degree, /* Number||Point */ a, /* Number, optional */ b){
		// summary: rotates a picture using a specified point as a center of rotation
		
		// accepts several signatures:
		//	1) rotation angle in degrees, Point
		//	2) rotation angle in degrees, x, y
		return arguments.length > 1 ? dojo.gfx.matrix._sandwich(dojo.gfx.matrix.rotateg(degree), a, b) : dojo.gfx.matrix._sandwich(dojo.gfx.matrix.rotateg(degree), a.x, a.y); // dojo.gfx.matrix.Matrix2D
	},
	skewXAt: function(/* Number */ angle, /* Number||Point */ a, /* Number, optional */ b){
		// summary: skews a picture along the X axis using a specified point as a center of skewing
		
		// accepts several signatures:
		//	1) skew angle in radians, Point
		//	2) skew angle in radians, x, y
		return arguments.length > 1 ? dojo.gfx.matrix._sandwich(dojo.gfx.matrix.skewX(angle), a, b) : dojo.gfx.matrix._sandwich(dojo.gfx.matrix.skewX(angle), a.x, a.y); // dojo.gfx.matrix.Matrix2D
	},
	skewXgAt: function(/* Number */ degree, /* Number||Point */ a, /* Number, optional */ b){
		// summary: skews a picture along the X axis using a specified point as a center of skewing
		
		// accepts several signatures:
		//	1) skew angle in degrees, Point
		//	2) skew angle in degrees, x, y
		return arguments.length > 1 ? dojo.gfx.matrix._sandwich(dojo.gfx.matrix.skewXg(degree), a, b) : dojo.gfx.matrix._sandwich(dojo.gfx.matrix.skewXg(degree), a.x, a.y); // dojo.gfx.matrix.Matrix2D
	},
	skewYAt: function(/* Number */ angle, /* Number||Point */ a, /* Number, optional */ b){
		// summary: skews a picture along the Y axis using a specified point as a center of skewing
		
		// accepts several signatures:
		//	1) skew angle in radians, Point
		//	2) skew angle in radians, x, y
		return arguments.length > 1 ? dojo.gfx.matrix._sandwich(dojo.gfx.matrix.skewY(angle), a, b) : dojo.gfx.matrix._sandwich(dojo.gfx.matrix.skewY(angle), a.x, a.y); // dojo.gfx.matrix.Matrix2D
	},
	skewYgAt: function(/* Number */ degree, /* Number||Point */ a, /* Number, optional */ b){
		// summary: skews a picture along the Y axis using a specified point as a center of skewing
		
		// accepts several signatures:
		//	1) skew angle in degrees, Point
		//	2) skew angle in degrees, x, y
		return arguments.length > 1 ? dojo.gfx.matrix._sandwich(dojo.gfx.matrix.skewYg(degree), a, b) : dojo.gfx.matrix._sandwich(dojo.gfx.matrix.skewYg(degree), a.x, a.y); // dojo.gfx.matrix.Matrix2D
	}
	// TODO: rect-to-rect mapping, scale-to-fit (isotropic and anisotropic versions)
});
