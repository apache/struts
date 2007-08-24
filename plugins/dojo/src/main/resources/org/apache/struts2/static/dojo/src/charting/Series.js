/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.charting.Series");
dojo.require("dojo.lang.common");
dojo.require("dojo.charting.Plotters");
dojo.charting.Series = function (kwArgs) {
	var args = kwArgs || {length:1};
	this.dataSource = args.dataSource || null;
	this.bindings = {};
	this.color = args.color;
	this.label = args.label;
	if (args.bindings) {
		for (var p in args.bindings) {
			this.addBinding(p, args.bindings[p]);
		}
	}
};
dojo.extend(dojo.charting.Series, {bind:function (src, bindings) {
	this.dataSource = src;
	this.bindings = bindings;
}, addBinding:function (name, binding) {
	this.bindings[name] = binding;
}, evaluate:function (kwArgs) {
	var ret = [];
	var a = this.dataSource.getData();
	var l = a.length;
	var start = 0;
	var end = l;
	if (kwArgs) {
		if (kwArgs.between) {
			for (var i = 0; i < l; i++) {
				var fld = this.dataSource.getField(a[i], kwArgs.between.field);
				if (fld >= kwArgs.between.low && fld <= kwArgs.between.high) {
					var o = {src:a[i], series:this};
					for (var p in this.bindings) {
						o[p] = this.dataSource.getField(a[i], this.bindings[p]);
					}
					ret.push(o);
				}
			}
		} else {
			if (kwArgs.from || kwArgs.length) {
				if (kwArgs.from) {
					start = Math.max(kwArgs.from, 0);
					if (kwArgs.to) {
						end = Math.min(kwArgs.to, end);
					}
				} else {
					if (kwArgs.length < 0) {
						start = Math.max((end + length), 0);
					} else {
						end = Math.min((start + length), end);
					}
				}
				for (var i = start; i < end; i++) {
					var o = {src:a[i], series:this};
					for (var p in this.bindings) {
						o[p] = this.dataSource.getField(a[i], this.bindings[p]);
					}
					ret.push(o);
				}
			}
		}
	} else {
		for (var i = start; i < end; i++) {
			var o = {src:a[i], series:this};
			for (var p in this.bindings) {
				o[p] = this.dataSource.getField(a[i], this.bindings[p]);
			}
			ret.push(o);
		}
	}
	if (ret.length > 0 && typeof (ret[0].x) != "undefined") {
		ret.sort(function (a, b) {
			if (a.x > b.x) {
				return 1;
			}
			if (a.x < b.x) {
				return -1;
			}
			return 0;
		});
	}
	return ret;
}, trends:{createRange:function (values, len) {
	var idx = values.length - 1;
	var length = (len || values.length);
	return {"index":idx, "length":length, "start":Math.max(idx - length, 0)};
}, mean:function (values, len) {
	var range = this.createRange(values, len);
	if (range.index < 0) {
		return 0;
	}
	var total = 0;
	var count = 0;
	for (var i = range.index; i >= range.start; i--) {
		total += values[i].y;
		count++;
	}
	total /= Math.max(count, 1);
	return total;
}, variance:function (values, len) {
	var range = this.createRange(values, len);
	if (range.index < 0) {
		return 0;
	}
	var total = 0;
	var square = 0;
	var count = 0;
	for (var i = range.index; i >= range.start; i--) {
		total += values[i].y;
		square += Math.pow(values[i].y, 2);
		count++;
	}
	return (square / count) - Math.pow(total / count, 2);
}, standardDeviation:function (values, len) {
	return Math.sqrt(this.getVariance(values, len));
}, max:function (values, len) {
	var range = this.createRange(values, len);
	if (range.index < 0) {
		return 0;
	}
	var max = Number.MIN_VALUE;
	for (var i = range.index; i >= range.start; i--) {
		max = Math.max(values[i].y, max);
	}
	return max;
}, min:function (values, len) {
	var range = this.createRange(values, len);
	if (range.index < 0) {
		return 0;
	}
	var min = Number.MAX_VALUE;
	for (var i = range.index; i >= range.start; i--) {
		min = Math.min(values[i].y, min);
	}
	return min;
}, median:function (values, len) {
	var range = this.createRange(values, len);
	if (range.index < 0) {
		return 0;
	}
	var a = [];
	for (var i = range.index; i >= range.start; i--) {
		var b = false;
		for (var j = 0; j < a.length; j++) {
			if (values[i].y == a[j]) {
				b = true;
				break;
			}
		}
		if (!b) {
			a.push(values[i].y);
		}
	}
	a.sort();
	if (a.length > 0) {
		return a[Math.ceil(a.length / 2)];
	}
	return 0;
}, mode:function (values, len) {
	var range = this.createRange(values, len);
	if (range.index < 0) {
		return 0;
	}
	var o = {};
	var ret = 0;
	var median = Number.MIN_VALUE;
	for (var i = range.index; i >= range.start; i--) {
		if (!o[values[i].y]) {
			o[values[i].y] = 1;
		} else {
			o[values[i].y]++;
		}
	}
	for (var p in o) {
		if (median < o[p]) {
			median = o[p];
			ret = p;
		}
	}
	return ret;
}}});

