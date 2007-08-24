/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.charting.PlotArea");
dojo.require("dojo.lang.common");
dojo.require("dojo.gfx.color");
dojo.require("dojo.gfx.color.hsl");
dojo.require("dojo.charting.Plot");
dojo.charting.PlotArea = function () {
	var id = "dojo-charting-plotarea-" + dojo.charting.PlotArea.count++;
	this.getId = function () {
		return id;
	};
	this.setId = function (key) {
		id = key;
	};
	this.areaType = "standard";
	this.plots = [];
	this.size = {width:600, height:400};
	this.padding = {top:10, right:10, bottom:20, left:20};
	this.nodes = {main:null, area:null, background:null, axes:null, plots:null};
	this._color = {h:140, s:120, l:120, step:27};
};
dojo.charting.PlotArea.count = 0;
dojo.extend(dojo.charting.PlotArea, {nextColor:function () {
	var rgb = dojo.gfx.color.hsl2rgb(this._color.h, this._color.s, this._color.l);
	this._color.h = (this._color.h + this._color.step) % 360;
	while (this._color.h < 140) {
		this._color.h += this._color.step;
	}
	return dojo.gfx.color.rgb2hex(rgb[0], rgb[1], rgb[2]);
}, getArea:function () {
	return {left:this.padding.left, right:this.size.width - this.padding.right, top:this.padding.top, bottom:this.size.height - this.padding.bottom, toString:function () {
		var a = [this.top, this.right, this.bottom, this.left];
		return "[" + a.join() + "]";
	}};
}, getAxes:function () {
	var axes = {};
	for (var i = 0; i < this.plots.length; i++) {
		var plot = this.plots[i];
		axes[plot.axisX.getId()] = {axis:plot.axisX, drawAgainst:plot.axisY, plot:plot, plane:"x"};
		axes[plot.axisY.getId()] = {axis:plot.axisY, drawAgainst:plot.axisX, plot:plot, plane:"y"};
	}
	return axes;
}, getLegendInfo:function () {
	var a = [];
	for (var i = 0; i < this.plots.length; i++) {
		for (var j = 0; j < this.plots[i].series.length; j++) {
			var data = this.plots[i].series[j].data;
			a.push({label:data.label, color:data.color});
		}
	}
	return a;
}, setAxesRanges:function () {
	var ranges = {};
	var axes = {};
	for (var i = 0; i < this.plots.length; i++) {
		var plot = this.plots[i];
		var ranges = plot.getRanges();
		var x = ranges.x;
		var y = ranges.y;
		var ax, ay;
		if (!axes[plot.axisX.getId()]) {
			axes[plot.axisX.getId()] = plot.axisX;
			ranges[plot.axisX.getId()] = {upper:x.upper, lower:x.lower};
		}
		ax = ranges[plot.axisX.getId()];
		ax.upper = Math.max(ax.upper, x.upper);
		ax.lower = Math.min(ax.lower, x.lower);
		if (!axes[plot.axisY.getId()]) {
			axes[plot.axisY.getId()] = plot.axisY;
			ranges[plot.axisY.getId()] = {upper:y.upper, lower:y.lower};
		}
		ay = ranges[plot.axisY.getId()];
		ay.upper = Math.max(ay.upper, y.upper);
		ay.lower = Math.min(ay.lower, y.lower);
	}
	for (var p in axes) {
		axes[p].range = ranges[p];
	}
}, render:function (kwArgs, applyToData) {
	if (!this.nodes.main || !this.nodes.area || !this.nodes.background || !this.nodes.plots || !this.nodes.axes) {
		this.initialize();
	}
	this.resize();
	for (var i = 0; i < this.plots.length; i++) {
		var plot = this.plots[i];
		if (plot.dataNode) {
			this.nodes.plots.removeChild(plot.dataNode);
		}
		var target = this.initializePlot(plot);
		switch (plot.renderType) {
		  case dojo.charting.RenderPlotSeries.Grouped:
			if (plot.series[0]) {
				target.appendChild(plot.series[0].plotter(this, plot, kwArgs, applyToData));
			}
			break;
		  case dojo.charting.RenderPlotSeries.Singly:
		  default:
			for (var j = 0; j < plot.series.length; j++) {
				var series = plot.series[j];
				var data = series.data.evaluate(kwArgs);
				target.appendChild(series.plotter(data, this, plot, applyToData));
			}
		}
		this.nodes.plots.appendChild(target);
	}
}, destroy:function () {
	for (var i = 0; i < this.plots.length; i++) {
		this.plots[i].destroy();
	}
	for (var p in this.nodes) {
		var node = this.nodes[p];
		if (!node) {
			continue;
		}
		if (!node.childNodes) {
			continue;
		}
		while (node.childNodes.length > 0) {
			node.removeChild(node.childNodes[0]);
		}
		this.nodes[p] = null;
	}
}});
dojo.requireIf(dojo.render.svg.capable, "dojo.charting.svg.PlotArea");
dojo.requireIf(dojo.render.vml.capable, "dojo.charting.vml.PlotArea");

