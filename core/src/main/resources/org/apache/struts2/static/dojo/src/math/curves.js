/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.math.curves");
dojo.require("dojo.math");

/* Curves from Dan's 13th lib stuff.
 * See: http://pupius.co.uk/js/Toolkit.Drawing.js
 *      http://pupius.co.uk/dump/dojo/Dojo.Math.js
 */

dojo.math.curves = {
	Line: function(/* array */start, /* array */end) {
		//	summary
		//	Creates a straight line object
		this.start = start;
		this.end = end;
		this.dimensions = start.length;

		for(var i = 0; i < start.length; i++) {
			start[i] = Number(start[i]);
		}

		for(var i = 0; i < end.length; i++) {
			end[i] = Number(end[i]);
		}

		//simple function to find point on an n-dimensional, straight line
		this.getValue = function(/* float */n){
			//	summary
			//	Returns the point at point N (in terms of percentage) on this line.
			var retVal = new Array(this.dimensions);
			for(var i=0;i<this.dimensions;i++)
				retVal[i] = ((this.end[i] - this.start[i]) * n) + this.start[i];
			return retVal;	//	array
		}
		return this;	//	dojo.math.curves.Line
	},

	Bezier: function(/* array */pnts) {
		//	summary
		//	Creates a bezier curve
		//	Takes an array of points, the first is the start point, the last is end point and the ones in
		//	between are the Bezier control points.
		this.getValue = function(/* float */step) {
			//	summary
			//	Returns the point at point N (in terms of percentage) on this curve.
			if(step >= 1) return this.p[this.p.length-1];	// if step>=1 we must be at the end of the curve
			if(step <= 0) return this.p[0];					// if step<=0 we must be at the start of the curve
			var retVal = new Array(this.p[0].length);
			for(var k=0;j<this.p[0].length;k++) { retVal[k]=0; }
			for(var j=0;j<this.p[0].length;j++) {
				var C=0; var D=0;
				for(var i=0;i<this.p.length;i++) {
					C += this.p[i][j] * this.p[this.p.length-1][0]
						* dojo.math.bernstein(step,this.p.length,i);
				}
				for(var l=0;l<this.p.length;l++) {
					D += this.p[this.p.length-1][0] * dojo.math.bernstein(step,this.p.length,l);
				}
				retVal[j] = C/D;
			}
			return retVal;	//	array
		}
		this.p = pnts;
		return this;	//	dojo.math.curves.Bezier
	},

	CatmullRom : function(/* array */pnts, /* float */c) {
		//	summary
		//	Creates a catmull-rom spline curve with c tension.
		this.getValue = function(/* float */step) {
			//	summary
			//	Returns the point at point N (in terms of percentage) on this curve.
			var percent = step * (this.p.length-1);
			var node = Math.floor(percent);
			var progress = percent - node;

			var i0 = node-1; if(i0 < 0) i0 = 0;
			var i = node;
			var i1 = node+1; if(i1 >= this.p.length) i1 = this.p.length-1;
			var i2 = node+2; if(i2 >= this.p.length) i2 = this.p.length-1;

			var u = progress;
			var u2 = progress*progress;
			var u3 = progress*progress*progress;

			var retVal = new Array(this.p[0].length);
			for(var k=0;k<this.p[0].length;k++) {
				var x1 = ( -this.c * this.p[i0][k] ) + ( (2 - this.c) * this.p[i][k] ) + ( (this.c-2) * this.p[i1][k] ) + ( this.c * this.p[i2][k] );
				var x2 = ( 2 * this.c * this.p[i0][k] ) + ( (this.c-3) * this.p[i][k] ) + ( (3 - 2 * this.c) * this.p[i1][k] ) + ( -this.c * this.p[i2][k] );
				var x3 = ( -this.c * this.p[i0][k] ) + ( this.c * this.p[i1][k] );
				var x4 = this.p[i][k];

				retVal[k] = x1*u3 + x2*u2 + x3*u + x4;
			}
			return retVal;	//	array
		}

		if(!c) this.c = 0.7;
		else this.c = c;
		this.p = pnts;

		return this;	//	dojo.math.curves.CatmullRom
	},

	// FIXME: This is the bad way to do a partial-arc with 2 points. We need to have the user
	// supply the radius, otherwise we always get a half-circle between the two points.
	Arc : function(/* array */start, /* array */end, /* boolean? */ccw) {
		//	summary
		//	Creates an arc with a counter clockwise switch
		var center = dojo.math.points.midpoint(start, end);
		var sides = dojo.math.points.translate(dojo.math.points.invert(center), start);
		var rad = Math.sqrt(Math.pow(sides[0], 2) + Math.pow(sides[1], 2));
		var theta = dojo.math.radToDeg(Math.atan(sides[1]/sides[0]));
		if( sides[0] < 0 ) {
			theta -= 90;
		} else {
			theta += 90;
		}
		dojo.math.curves.CenteredArc.call(this, center, rad, theta, theta+(ccw?-180:180));
	},

	CenteredArc : function(/* array */center, /* float */radius, /* array */start, /* array */end) {
		//	summary
		// 	Creates an arc object, with center and radius (Top of arc = 0 degrees, increments clockwise)
		//  center => 2D point for center of arc
		//  radius => scalar quantity for radius of arc
		//  start  => to define an arc specify start angle (default: 0)
		//  end    => to define an arc specify start angle
		this.center = center;
		this.radius = radius;
		this.start = start || 0;
		this.end = end;

		this.getValue = function(/* float */n) {
			//	summary
			//	Returns the point at point N (in terms of percentage) on this curve.
			var retVal = new Array(2);
			var theta = dojo.math.degToRad(this.start+((this.end-this.start)*n));

			retVal[0] = this.center[0] + this.radius*Math.sin(theta);
			retVal[1] = this.center[1] - this.radius*Math.cos(theta);
	
			return retVal;	//	array
		}

		return this;	//	dojo.math.curves.CenteredArc
	},

	Circle : function(/* array */center, /* float */radius) {
		//	summary
		// Special case of Arc (start = 0, end = 360)
		dojo.math.curves.CenteredArc.call(this, center, radius, 0, 360);
		return this;	//	dojo.math.curves.Circle
	},

	Path : function() {
		//	summary
		// 	Generic path shape, created from curve segments
		var curves = [];
		var weights = [];
		var ranges = [];
		var totalWeight = 0;

		this.add = function(/* dojo.math.curves.* */curve, /* float */weight) {
			//	summary
			//	Add a curve segment to this path
			if( weight < 0 ) { dojo.raise("dojo.math.curves.Path.add: weight cannot be less than 0"); }
			curves.push(curve);
			weights.push(weight);
			totalWeight += weight;
			computeRanges();
		}

		this.remove = function(/* dojo.math.curves.* */curve) {
			//	summary
			//	Remove a curve segment from this path
			for(var i = 0; i < curves.length; i++) {
				if( curves[i] == curve ) {
					curves.splice(i, 1);
					totalWeight -= weights.splice(i, 1)[0];
					break;
				}
			}
			computeRanges();
		}

		this.removeAll = function() {
			//	summary
			//	Remove all curve segments
			curves = [];
			weights = [];
			totalWeight = 0;
		}

		this.getValue = function(/* float */n) {
			//	summary
			//	Returns the point at point N (in terms of percentage) on this curve.
			var found = false, value = 0;
			for(var i = 0; i < ranges.length; i++) {
				var r = ranges[i];
				//w(r.join(" ... "));
				if( n >= r[0] && n < r[1] ) {
					var subN = (n - r[0]) / r[2];
					value = curves[i].getValue(subN);
					found = true;
					break;
				}
			}

			// FIXME: Do we want to assume we're at the end?
			if( !found ) {
				value = curves[curves.length-1].getValue(1);
			}

			for(var j = 0; j < i; j++) {
				value = dojo.math.points.translate(value, curves[j].getValue(1));
			}
			return value;	//	array
		}

		function computeRanges() {
			var start = 0;
			for(var i = 0; i < weights.length; i++) {
				var end = start + weights[i] / totalWeight;
				var len = end - start;
				ranges[i] = [start, end, len];
				start = end;
			}
		}

		return this;	//	dojo.math.curves.Path
	}
};
