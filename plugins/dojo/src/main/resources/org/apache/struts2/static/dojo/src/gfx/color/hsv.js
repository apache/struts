/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.gfx.color.hsv");
dojo.require("dojo.lang.array");
dojo.require("dojo.math");
dojo.lang.extend(dojo.gfx.color.Color, {toHsv:function () {
	return dojo.gfx.color.rgb2hsv(this.toRgb());
}});
dojo.gfx.color.rgb2hsv = function (r, g, b, options) {
	if (dojo.lang.isArray(r)) {
		if (g) {
			options = g;
		}
		b = r[2] || 0;
		g = r[1] || 0;
		r = r[0] || 0;
	}
	var opt = {inputRange:(options && options.inputRange) ? options.inputRange : 255, outputRange:(options && options.outputRange) ? options.outputRange : [255, 255, 255]};
	var h = null;
	var s = null;
	var v = null;
	switch (opt.inputRange) {
	  case 1:
		r = (r * 255);
		g = (g * 255);
		b = (b * 255);
		break;
	  case 100:
		r = (r / 100) * 255;
		g = (g / 100) * 255;
		b = (b / 100) * 255;
		break;
	  default:
		break;
	}
	var min = Math.min(r, g, b);
	v = Math.max(r, g, b);
	var delta = v - min;
	s = (v == 0) ? 0 : delta / v;
	if (s == 0) {
		h = 0;
	} else {
		if (r == v) {
			h = 60 * (g - b) / delta;
		} else {
			if (g == v) {
				h = 120 + 60 * (b - r) / delta;
			} else {
				if (b == v) {
					h = 240 + 60 * (r - g) / delta;
				}
			}
		}
		if (h <= 0) {
			h += 360;
		}
	}
	switch (opt.outputRange[0]) {
	  case 360:
		break;
	  case 100:
		h = (h / 360) * 100;
		break;
	  case 1:
		h = (h / 360);
		break;
	  default:
		h = (h / 360) * 255;
		break;
	}
	switch (opt.outputRange[1]) {
	  case 100:
		s = s * 100;
	  case 1:
		break;
	  default:
		s = s * 255;
		break;
	}
	switch (opt.outputRange[2]) {
	  case 100:
		v = (v / 255) * 100;
		break;
	  case 1:
		v = (v / 255);
		break;
	  default:
		break;
	}
	h = dojo.math.round(h);
	s = dojo.math.round(s);
	v = dojo.math.round(v);
	return [h, s, v];
};
dojo.gfx.color.hsv2rgb = function (h, s, v, options) {
	if (dojo.lang.isArray(h)) {
		if (s) {
			options = s;
		}
		v = h[2] || 0;
		s = h[1] || 0;
		h = h[0] || 0;
	}
	var opt = {inputRange:(options && options.inputRange) ? options.inputRange : [255, 255, 255], outputRange:(options && options.outputRange) ? options.outputRange : 255};
	switch (opt.inputRange[0]) {
	  case 1:
		h = h * 360;
		break;
	  case 100:
		h = (h / 100) * 360;
		break;
	  case 360:
		h = h;
		break;
	  default:
		h = (h / 255) * 360;
	}
	if (h == 360) {
		h = 0;
	}
	switch (opt.inputRange[1]) {
	  case 100:
		s /= 100;
		break;
	  case 255:
		s /= 255;
	}
	switch (opt.inputRange[2]) {
	  case 100:
		v /= 100;
		break;
	  case 255:
		v /= 255;
	}
	var r = null;
	var g = null;
	var b = null;
	if (s == 0) {
		r = v;
		g = v;
		b = v;
	} else {
		var hTemp = h / 60;
		var i = Math.floor(hTemp);
		var f = hTemp - i;
		var p = v * (1 - s);
		var q = v * (1 - (s * f));
		var t = v * (1 - (s * (1 - f)));
		switch (i) {
		  case 0:
			r = v;
			g = t;
			b = p;
			break;
		  case 1:
			r = q;
			g = v;
			b = p;
			break;
		  case 2:
			r = p;
			g = v;
			b = t;
			break;
		  case 3:
			r = p;
			g = q;
			b = v;
			break;
		  case 4:
			r = t;
			g = p;
			b = v;
			break;
		  case 5:
			r = v;
			g = p;
			b = q;
			break;
		}
	}
	switch (opt.outputRange) {
	  case 1:
		r = dojo.math.round(r, 2);
		g = dojo.math.round(g, 2);
		b = dojo.math.round(b, 2);
		break;
	  case 100:
		r = Math.round(r * 100);
		g = Math.round(g * 100);
		b = Math.round(b * 100);
		break;
	  default:
		r = Math.round(r * 255);
		g = Math.round(g * 255);
		b = Math.round(b * 255);
	}
	return [r, g, b];
};

