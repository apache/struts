/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.charting.Axis");
dojo.require("dojo.lang.common");
dojo.charting.Axis = function (label, scale, labels) {
	var id = "dojo-charting-axis-" + dojo.charting.Axis.count++;
	this.getId = function () {
		return id;
	};
	this.setId = function (key) {
		id = key;
	};
	this.scale = scale || "linear";
	this.label = label || "";
	this.showLabel = true;
	this.showLabels = true;
	this.showLines = false;
	this.showTicks = false;
	this.range = {upper:100, lower:0};
	this.origin = "min";
	this._origin = null;
	this.labels = labels || [];
	this._labels = [];
	this.nodes = {main:null, axis:null, label:null, labels:null, lines:null, ticks:null};
	this._rerender = false;
};
dojo.charting.Axis.count = 0;
dojo.extend(dojo.charting.Axis, {getCoord:function (val, plotArea, plot) {
	val = parseFloat(val, 10);
	var area = plotArea.getArea();
	if (plot.axisX == this) {
		var offset = 0 - this.range.lower;
		var min = this.range.lower + offset;
		var max = this.range.upper + offset;
		val += offset;
		return (val * ((area.right - area.left) / max)) + area.left;
	} else {
		var max = this.range.upper;
		var min = this.range.lower;
		var offset = 0;
		if (min < 0) {
			offset += Math.abs(min);
		}
		max += offset;
		min += offset;
		val += offset;
		var pmin = area.bottom;
		var pmax = area.top;
		return (((pmin - pmax) / (max - min)) * (max - val)) + pmax;
	}
}, initializeOrigin:function (drawAgainst, plane) {
	if (this._origin == null) {
		this._origin = this.origin;
	}
	if (isNaN(this._origin)) {
		if (this._origin.toLowerCase() == "max") {
			this.origin = drawAgainst.range[(plane == "y") ? "upper" : "lower"];
		} else {
			if (this._origin.toLowerCase() == "min") {
				this.origin = drawAgainst.range[(plane == "y") ? "lower" : "upper"];
			} else {
				this.origin = 0;
			}
		}
	}
}, initializeLabels:function () {
	this._labels = [];
	if (this.labels.length == 0) {
		this.showLabels = false;
		this.showLines = false;
		this.showTicks = false;
	} else {
		if (this.labels[0].label && this.labels[0].value != null) {
			for (var i = 0; i < this.labels.length; i++) {
				this._labels.push(this.labels[i]);
			}
		} else {
			if (!isNaN(this.labels[0])) {
				for (var i = 0; i < this.labels.length; i++) {
					this._labels.push({label:this.labels[i], value:this.labels[i]});
				}
			} else {
				var a = [];
				for (var i = 0; i < this.labels.length; i++) {
					a.push(this.labels[i]);
				}
				var s = a.shift();
				this._labels.push({label:s, value:this.range.lower});
				if (a.length > 0) {
					var s = a.pop();
					this._labels.push({label:s, value:this.range.upper});
				}
				if (a.length > 0) {
					var range = this.range.upper - this.range.lower;
					var step = range / (this.labels.length - 1);
					for (var i = 1; i <= a.length; i++) {
						this._labels.push({label:a[i - 1], value:this.range.lower + (step * i)});
					}
				}
			}
		}
	}
}, initialize:function (plotArea, plot, drawAgainst, plane) {
	this.destroy();
	this.initializeOrigin(drawAgainst, plane);
	this.initializeLabels();
	var node = this.render(plotArea, plot, drawAgainst, plane);
	return node;
}, destroy:function () {
	for (var p in this.nodes) {
		while (this.nodes[p] && this.nodes[p].childNodes.length > 0) {
			this.nodes[p].removeChild(this.nodes[p].childNodes[0]);
		}
		if (this.nodes[p] && this.nodes[p].parentNode) {
			this.nodes[p].parentNode.removeChild(this.nodes[p]);
		}
		this.nodes[p] = null;
	}
}});
dojo.requireIf(dojo.render.svg.capable, "dojo.charting.svg.Axis");
dojo.requireIf(dojo.render.vml.capable, "dojo.charting.vml.Axis");

