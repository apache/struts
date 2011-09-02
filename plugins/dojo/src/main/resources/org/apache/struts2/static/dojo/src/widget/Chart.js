/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.Chart");
dojo.require("dojo.widget.*");
dojo.require("dojo.gfx.color");
dojo.require("dojo.gfx.color.hsl");
dojo.declare("dojo.widget.Chart", null, function () {
	this.series = [];
}, {isContainer:false, assignColors:function () {
	var hue = 30;
	var sat = 120;
	var lum = 120;
	var steps = Math.round(330 / this.series.length);
	for (var i = 0; i < this.series.length; i++) {
		var c = dojo.gfx.color.hsl2rgb(hue, sat, lum);
		if (!this.series[i].color) {
			this.series[i].color = dojo.gfx.color.rgb2hex(c[0], c[1], c[2]);
		}
		hue += steps;
	}
}, parseData:function (table) {
	var thead = table.getElementsByTagName("thead")[0];
	var tbody = table.getElementsByTagName("tbody")[0];
	if (!(thead && tbody)) {
		dojo.raise("dojo.widget.Chart: supplied table must define a head and a body.");
	}
	var columns = thead.getElementsByTagName("tr")[0].getElementsByTagName("th");
	for (var i = 1; i < columns.length; i++) {
		var key = "column" + i;
		var label = columns[i].innerHTML;
		var plotType = columns[i].getAttribute("plotType") || "line";
		var color = columns[i].getAttribute("color");
		var ds = new dojo.widget.Chart.DataSeries(key, label, plotType, color);
		this.series.push(ds);
	}
	var rows = tbody.rows;
	var xMin = Number.MAX_VALUE, xMax = Number.MIN_VALUE;
	var yMin = Number.MAX_VALUE, yMax = Number.MIN_VALUE;
	var ignore = ["accesskey", "align", "bgcolor", "class", "colspan", "height", "id", "nowrap", "rowspan", "style", "tabindex", "title", "valign", "width"];
	for (var i = 0; i < rows.length; i++) {
		var row = rows[i];
		var cells = row.cells;
		var x = Number.MIN_VALUE;
		for (var j = 0; j < cells.length; j++) {
			if (j == 0) {
				x = parseFloat(cells[j].innerHTML);
				xMin = Math.min(xMin, x);
				xMax = Math.max(xMax, x);
			} else {
				var ds = this.series[j - 1];
				var y = parseFloat(cells[j].innerHTML);
				yMin = Math.min(yMin, y);
				yMax = Math.max(yMax, y);
				var o = {x:x, value:y};
				var attrs = cells[j].attributes;
				for (var k = 0; k < attrs.length; k++) {
					var attr = attrs.item(k);
					var bIgnore = false;
					for (var l = 0; l < ignore.length; l++) {
						if (attr.nodeName.toLowerCase() == ignore[l]) {
							bIgnore = true;
							break;
						}
					}
					if (!bIgnore) {
						o[attr.nodeName] = attr.nodeValue;
					}
				}
				ds.add(o);
			}
		}
	}
	return {x:{min:xMin, max:xMax}, y:{min:yMin, max:yMax}};
}});
dojo.declare("dojo.widget.Chart.DataSeries", null, function (key, label, plotType, color) {
	this.id = "DataSeries" + dojo.widget.Chart.DataSeries.count++;
	this.key = key;
	this.label = label || this.id;
	this.plotType = plotType || "line";
	this.color = color;
	this.values = [];
}, {add:function (v) {
	if (v.x == null || v.value == null) {
		dojo.raise("dojo.widget.Chart.DataSeries.add: v must have both an 'x' and 'value' property.");
	}
	this.values.push(v);
}, clear:function () {
	this.values = [];
}, createRange:function (len) {
	var idx = this.values.length - 1;
	var length = (len || this.values.length);
	return {"index":idx, "length":length, "start":Math.max(idx - length, 0)};
}, getMean:function (len) {
	var range = this.createRange(len);
	if (range.index < 0) {
		return 0;
	}
	var t = 0;
	var c = 0;
	for (var i = range.index; i >= range.start; i--) {
		var n = parseFloat(this.values[i].value);
		if (!isNaN(n)) {
			t += n;
			c++;
		}
	}
	t /= Math.max(c, 1);
	return t;
}, getMovingAverage:function (len) {
	var range = this.createRange(len);
	if (range.index < 0) {
		return 0;
	}
	var t = 0;
	var c = 0;
	for (var i = range.index; i >= range.start; i--) {
		var n = parseFloat(this.values[i].value);
		if (!isNaN(n)) {
			t += n;
			c++;
		}
	}
	t /= Math.max(c, 1);
	return t;
}, getVariance:function (len) {
	var range = this.createRange(len);
	if (range.index < 0) {
		return 0;
	}
	var t = 0;
	var s = 0;
	var c = 0;
	for (var i = range.index; i >= range.start; i--) {
		var n = parseFloat(this.values[i].value);
		if (!isNaN(n)) {
			t += n;
			s += Math.pow(n, 2);
			c++;
		}
	}
	return (s / c) - Math.pow(t / c, 2);
}, getStandardDeviation:function (len) {
	return Math.sqrt(this.getVariance(len));
}, getMax:function (len) {
	var range = this.createRange(len);
	if (range.index < 0) {
		return 0;
	}
	var t = 0;
	for (var i = range.index; i >= range.start; i--) {
		var n = parseFloat(this.values[i].value);
		if (!isNaN(n)) {
			t = Math.max(n, t);
		}
	}
	return t;
}, getMin:function (len) {
	var range = this.createRange(len);
	if (range.index < 0) {
		return 0;
	}
	var t = 0;
	for (var i = range.index; i >= range.start; i--) {
		var n = parseFloat(this.values[i].value);
		if (!isNaN(n)) {
			t = Math.min(n, t);
		}
	}
	return t;
}, getMedian:function (len) {
	var range = this.createRange(len);
	if (range.index < 0) {
		return 0;
	}
	var a = [];
	for (var i = range.index; i >= range.start; i--) {
		var n = parseFloat(this.values[i].value);
		if (!isNaN(n)) {
			var b = false;
			for (var j = 0; j < a.length && !b; j++) {
				if (n == a[j]) {
					b = true;
				}
			}
			if (!b) {
				a.push(n);
			}
		}
	}
	a.sort();
	if (a.length > 0) {
		return a[Math.ceil(a.length / 2)];
	}
	return 0;
}, getMode:function (len) {
	var range = this.createRange(len);
	if (range.index < 0) {
		return 0;
	}
	var o = {};
	var ret = 0;
	var m = 0;
	for (var i = range.index; i >= range.start; i--) {
		var n = parseFloat(this.values[i].value);
		if (!isNaN(n)) {
			if (!o[this.values[i].value]) {
				o[this.values[i].value] = 1;
			} else {
				o[this.values[i].value]++;
			}
		}
	}
	for (var p in o) {
		if (m < o[p]) {
			m = o[p];
			ret = p;
		}
	}
	return parseFloat(ret);
}});
dojo.requireIf(dojo.render.svg.capable, "dojo.widget.svg.Chart");
dojo.requireIf(!dojo.render.svg.capable && dojo.render.vml.capable, "dojo.widget.vml.Chart");

