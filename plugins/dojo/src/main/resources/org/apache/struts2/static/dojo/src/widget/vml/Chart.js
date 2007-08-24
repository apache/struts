/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.vml.Chart");
dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.widget.Chart");
dojo.require("dojo.math");
dojo.require("dojo.html.layout");
dojo.require("dojo.gfx.color");
dojo.widget.defineWidget("dojo.widget.vml.Chart", [dojo.widget.HtmlWidget, dojo.widget.Chart], function () {
	this.templatePath = null;
	this.templateCssPath = null;
	this._isInitialize = false;
	this.hasData = false;
	this.vectorNode = null;
	this.plotArea = null;
	this.dataGroup = null;
	this.axisGroup = null;
	this.properties = {height:0, width:0, defaultWidth:600, defaultHeight:400, plotType:null, padding:{top:10, bottom:2, left:60, right:30}, axes:{x:{plotAt:0, label:"", unitLabel:"", unitType:Number, nUnitsToShow:10, range:{min:0, max:200}}, y:{plotAt:0, label:"", unitLabel:"", unitType:Number, nUnitsToShow:10, range:{min:0, max:200}}}};
}, {parseProperties:function (node) {
	var bRangeX = false;
	var bRangeY = false;
	if (node.getAttribute("width")) {
		this.properties.width = node.getAttribute("width");
	}
	if (node.getAttribute("height")) {
		this.properties.height = node.getAttribute("height");
	}
	if (node.getAttribute("plotType")) {
		this.properties.plotType = node.getAttribute("plotType");
	}
	if (node.getAttribute("padding")) {
		if (node.getAttribute("padding").indexOf(",") > -1) {
			var p = node.getAttribute("padding").split(",");
		} else {
			var p = node.getAttribute("padding").split(" ");
		}
		if (p.length == 1) {
			var pad = parseFloat(p[0]);
			this.properties.padding.top = pad;
			this.properties.padding.right = pad;
			this.properties.padding.bottom = pad;
			this.properties.padding.left = pad;
		} else {
			if (p.length == 2) {
				var padV = parseFloat(p[0]);
				var padH = parseFloat(p[1]);
				this.properties.padding.top = padV;
				this.properties.padding.right = padH;
				this.properties.padding.bottom = padV;
				this.properties.padding.left = padH;
			} else {
				if (p.length == 4) {
					this.properties.padding.top = parseFloat(p[0]);
					this.properties.padding.right = parseFloat(p[1]);
					this.properties.padding.bottom = parseFloat(p[2]);
					this.properties.padding.left = parseFloat(p[3]);
				}
			}
		}
	}
	if (node.getAttribute("rangeX")) {
		var p = node.getAttribute("rangeX");
		if (p.indexOf(",") > -1) {
			p = p.split(",");
		} else {
			p = p.split(" ");
		}
		this.properties.axes.x.range.min = parseFloat(p[0]);
		this.properties.axes.x.range.max = parseFloat(p[1]);
		bRangeX = true;
	}
	if (node.getAttribute("rangeY")) {
		var p = node.getAttribute("rangeY");
		if (p.indexOf(",") > -1) {
			p = p.split(",");
		} else {
			p = p.split(" ");
		}
		this.properties.axes.y.range.min = parseFloat(p[0]);
		this.properties.axes.y.range.max = parseFloat(p[1]);
		bRangeY = true;
	}
	return {rangeX:bRangeX, rangeY:bRangeY};
}, setAxesPlot:function (table) {
	if (table.getAttribute("axisAt")) {
		var p = table.getAttribute("axisAt");
		if (p.indexOf(",") > -1) {
			p = p.split(",");
		} else {
			p = p.split(" ");
		}
		if (!isNaN(parseFloat(p[0]))) {
			this.properties.axes.x.plotAt = parseFloat(p[0]);
		} else {
			if (p[0].toLowerCase() == "ymin") {
				this.properties.axes.x.plotAt = this.properties.axes.y.range.min;
			} else {
				if (p[0].toLowerCase() == "ymax") {
					this.properties.axes.x.plotAt = this.properties.axes.y.range.max;
				}
			}
		}
		if (!isNaN(parseFloat(p[1]))) {
			this.properties.axes.y.plotAt = parseFloat(p[1]);
		} else {
			if (p[1].toLowerCase() == "xmin") {
				this.properties.axes.y.plotAt = this.properties.axes.x.range.min;
			} else {
				if (p[1].toLowerCase() == "xmax") {
					this.properties.axes.y.plotAt = this.properties.axes.x.range.max;
				}
			}
		}
	} else {
		this.properties.axes.x.plotAt = this.properties.axes.y.range.min;
		this.properties.axes.y.plotAt = this.properties.axes.x.range.min;
	}
}, drawVectorNode:function () {
	if (this.vectorNode) {
		this.destroy();
	}
	this.vectorNode = document.createElement("div");
	this.vectorNode.style.width = this.properties.width + "px";
	this.vectorNode.style.height = this.properties.height + "px";
	this.vectorNode.style.position = "relative";
	this.domNode.appendChild(this.vectorNode);
}, drawPlotArea:function () {
	var plotWidth = this.properties.width - this.properties.padding.left - this.properties.padding.right;
	var plotHeight = this.properties.height - this.properties.padding.top - this.properties.padding.bottom;
	if (this.plotArea) {
		this.plotArea.parentNode.removeChild(this.plotArea);
		this.plotArea = null;
	}
	this.plotArea = document.createElement("div");
	this.plotArea.style.position = "absolute";
	this.plotArea.style.backgroundColor = "#fff";
	this.plotArea.style.top = (this.properties.padding.top) - 2 + "px";
	this.plotArea.style.left = (this.properties.padding.left - 1) + "px";
	this.plotArea.style.width = plotWidth + "px";
	this.plotArea.style.height = plotHeight + "px";
	this.plotArea.style.clip = "rect(0 " + plotWidth + " " + plotHeight + " 0)";
	this.vectorNode.appendChild(this.plotArea);
}, drawDataGroup:function () {
	var plotWidth = this.properties.width - this.properties.padding.left - this.properties.padding.right;
	var plotHeight = this.properties.height - this.properties.padding.top - this.properties.padding.bottom;
	if (this.dataGroup) {
		this.dataGroup.parentNode.removeChild(this.dataGroup);
		this.dataGroup = null;
	}
	this.dataGroup = document.createElement("div");
	this.dataGroup.style.position = "absolute";
	this.dataGroup.setAttribute("title", "Data Group");
	this.dataGroup.style.top = "0px";
	this.dataGroup.style.left = "0px";
	this.dataGroup.style.width = plotWidth + "px";
	this.dataGroup.style.height = plotHeight + "px";
	this.plotArea.appendChild(this.dataGroup);
}, drawAxes:function () {
	var plotWidth = this.properties.width - this.properties.padding.left - this.properties.padding.right;
	var plotHeight = this.properties.height - this.properties.padding.top - this.properties.padding.bottom;
	if (this.axisGroup) {
		this.axisGroup.parentNode.removeChild(this.axisGroup);
		this.axisGroup = null;
	}
	this.axisGroup = document.createElement("div");
	this.axisGroup.style.position = "absolute";
	this.axisGroup.setAttribute("title", "Axis Group");
	this.axisGroup.style.top = "0px";
	this.axisGroup.style.left = "0px";
	this.axisGroup.style.width = plotWidth + "px";
	this.axisGroup.style.height = plotHeight + "px";
	this.plotArea.appendChild(this.axisGroup);
	var stroke = 1;
	var line = document.createElement("v:line");
	var y = dojo.widget.vml.Chart.Plotter.getY(this.properties.axes.x.plotAt, this);
	line.setAttribute("from", "0px," + y + "px");
	line.setAttribute("to", plotWidth + "px," + y + "px");
	line.style.position = "absolute";
	line.style.top = "0px";
	line.style.left = "0px";
	line.style.antialias = "false";
	line.setAttribute("strokecolor", "#666");
	line.setAttribute("strokeweight", stroke * 2 + "px");
	this.axisGroup.appendChild(line);
	var line = document.createElement("v:line");
	var x = dojo.widget.vml.Chart.Plotter.getX(this.properties.axes.y.plotAt, this);
	line.setAttribute("from", x + "px,0px");
	line.setAttribute("to", x + "px," + plotHeight + "px");
	line.style.position = "absolute";
	line.style.top = "0px";
	line.style.left = "0px";
	line.style.antialias = "false";
	line.setAttribute("strokecolor", "#666");
	line.setAttribute("strokeweight", stroke * 2 + "px");
	this.axisGroup.appendChild(line);
	var size = 10;
	var t = document.createElement("div");
	t.style.position = "absolute";
	t.style.top = (this.properties.height - this.properties.padding.bottom) + "px";
	t.style.left = this.properties.padding.left + "px";
	t.style.fontFamily = "sans-serif";
	t.style.fontSize = size + "px";
	t.innerHTML = dojo.math.round(parseFloat(this.properties.axes.x.range.min), 2);
	this.vectorNode.appendChild(t);
	t = document.createElement("div");
	t.style.position = "absolute";
	t.style.top = (this.properties.height - this.properties.padding.bottom) + "px";
	t.style.left = (this.properties.width - this.properties.padding.right - size) + "px";
	t.style.fontFamily = "sans-serif";
	t.style.fontSize = size + "px";
	t.innerHTML = dojo.math.round(parseFloat(this.properties.axes.x.range.max), 2);
	this.vectorNode.appendChild(t);
	t = document.createElement("div");
	t.style.position = "absolute";
	t.style.top = (size / 2) + "px";
	t.style.left = "0px";
	t.style.width = this.properties.padding.left + "px";
	t.style.textAlign = "right";
	t.style.paddingRight = "4px";
	t.style.fontFamily = "sans-serif";
	t.style.fontSize = size + "px";
	t.innerHTML = dojo.math.round(parseFloat(this.properties.axes.y.range.max), 2);
	this.vectorNode.appendChild(t);
	t = document.createElement("div");
	t.style.position = "absolute";
	t.style.top = (this.properties.height - this.properties.padding.bottom - size) + "px";
	t.style.left = "0px";
	t.style.width = this.properties.padding.left + "px";
	t.style.textAlign = "right";
	t.style.paddingRight = "4px";
	t.style.fontFamily = "sans-serif";
	t.style.fontSize = size + "px";
	t.innerHTML = dojo.math.round(parseFloat(this.properties.axes.y.range.min), 2);
	this.vectorNode.appendChild(t);
}, init:function () {
	if (!this.properties.width || !this.properties.height) {
		var box = dojo.html.getContentBox(this.domNode);
		if (!this.properties.width) {
			this.properties.width = (box.width < 32) ? this.properties.defaultWidth : box.width;
		}
		if (!this.properties.height) {
			this.properties.height = (box.height < 32) ? this.properties.defaultHeight : box.height;
		}
	}
	this.drawVectorNode();
	this.drawPlotArea();
	this.drawDataGroup();
	this.drawAxes();
	this.assignColors();
	this._isInitialized = true;
}, destroy:function () {
	while (this.domNode.childNodes.length > 0) {
		this.domNode.removeChild(this.domNode.childNodes[0]);
	}
	this.vectorNode = this.plotArea = this.dataGroup = this.axisGroup = null;
}, render:function () {
	if (this.dataGroup) {
		while (this.dataGroup.childNodes.length > 0) {
			this.dataGroup.removeChild(this.dataGroup.childNodes[0]);
		}
	} else {
		this.init();
	}
	for (var i = 0; i < this.series.length; i++) {
		dojo.widget.vml.Chart.Plotter.plot(this.series[i], this);
	}
}, postCreate:function () {
	var table = this.domNode.getElementsByTagName("table")[0];
	if (table) {
		var ranges = this.parseProperties(table);
		var bRangeX = false;
		var bRangeY = false;
		var axisValues = this.parseData(table);
		if (!bRangeX) {
			this.properties.axes.x.range = {min:axisValues.x.min, max:axisValues.x.max};
		}
		if (!bRangeY) {
			this.properties.axes.y.range = {min:axisValues.y.min, max:axisValues.y.max};
		}
		this.setAxesPlot(table);
		this.domNode.removeChild(table);
	}
	if (this.series.length > 0) {
		this.render();
	}
}});
dojo.widget.vml.Chart.Plotter = new function () {
	var self = this;
	var plotters = {};
	var types = dojo.widget.Chart.PlotTypes;
	this.getX = function (value, chart) {
		var v = parseFloat(value);
		var min = chart.properties.axes.x.range.min;
		var max = chart.properties.axes.x.range.max;
		var ofst = 0 - min;
		min += ofst;
		max += ofst;
		v += ofst;
		var xmin = 0;
		var xmax = chart.properties.width - chart.properties.padding.left - chart.properties.padding.right;
		var x = (v * ((xmax - xmin) / max)) + xmin;
		return x;
	};
	this.getY = function (value, chart) {
		var v = parseFloat(value);
		var max = chart.properties.axes.y.range.max;
		var min = chart.properties.axes.y.range.min;
		var ofst = 0;
		if (min < 0) {
			ofst += Math.abs(min);
		}
		min += ofst;
		max += ofst;
		v += ofst;
		var ymin = chart.properties.height - chart.properties.padding.top - chart.properties.padding.bottom;
		var ymax = 0;
		var y = (((ymin - ymax) / (max - min)) * (max - v)) + ymax;
		return y;
	};
	this.addPlotter = function (name, func) {
		plotters[name] = func;
	};
	this.plot = function (series, chart) {
		if (series.values.length == 0) {
			return;
		}
		if (series.plotType && plotters[series.plotType]) {
			return plotters[series.plotType](series, chart);
		} else {
			if (chart.plotType && plotters[chart.plotType]) {
				return plotters[chart.plotType](series, chart);
			}
		}
	};
	plotters["bar"] = function (series, chart) {
		var space = 1;
		var lastW = 0;
		var ys = [];
		var yAxis = self.getY(chart.properties.axes.x.plotAt, chart);
		var yA = yAxis;
		for (var i = 0; i < series.values.length; i++) {
			var x = self.getX(series.values[i].x, chart);
			var w;
			if (i == series.values.length - 1) {
				w = lastW;
			} else {
				w = self.getX(series.values[i + 1].x, chart) - x - space;
				lastW = w;
			}
			x -= (w / 2);
			var y = self.getY(series.values[i].value, chart);
			var h = Math.abs(yA - y);
			if (parseFloat(series.values[i].value) < chart.properties.axes.x.plotAt) {
				y = yA;
			}
			var bar = document.createElement("v:rect");
			bar.style.position = "absolute";
			bar.style.top = y + "px";
			bar.style.left = x + "px";
			bar.style.width = w + "px";
			bar.style.height = h + "px";
			bar.setAttribute("fillColor", series.color);
			bar.setAttribute("stroked", "false");
			bar.style.antialias = "false";
			bar.setAttribute("title", series.label + " (" + i + "): " + series.values[i].value);
			var fill = document.createElement("v:fill");
			fill.setAttribute("opacity", "0.9");
			bar.appendChild(fill);
			chart.dataGroup.appendChild(bar);
		}
	};
	plotters["line"] = function (series, chart) {
		var tension = 1.5;
		var line = document.createElement("v:shape");
		line.setAttribute("strokeweight", "2px");
		line.setAttribute("strokecolor", series.color);
		line.setAttribute("fillcolor", "none");
		line.setAttribute("filled", "false");
		line.setAttribute("title", series.label);
		line.setAttribute("coordsize", chart.properties.width + "," + chart.properties.height);
		line.style.position = "absolute";
		line.style.top = "0px";
		line.style.left = "0px";
		line.style.width = chart.properties.width + "px";
		line.style.height = chart.properties.height + "px";
		var stroke = document.createElement("v:stroke");
		stroke.setAttribute("opacity", "0.85");
		line.appendChild(stroke);
		var path = [];
		for (var i = 0; i < series.values.length; i++) {
			var x = Math.round(self.getX(series.values[i].x, chart));
			var y = Math.round(self.getY(series.values[i].value, chart));
			if (i == 0) {
				path.push("m");
				path.push(x + "," + y);
			} else {
				var lastx = Math.round(self.getX(series.values[i - 1].x, chart));
				var lasty = Math.round(self.getY(series.values[i - 1].value, chart));
				var dx = x - lastx;
				var dy = y - lasty;
				path.push("c");
				var cx = Math.round((x - (tension - 1) * (dx / tension)));
				path.push(cx + "," + lasty);
				cx = Math.round((x - (dx / tension)));
				path.push(cx + "," + y);
				path.push(x + "," + y);
			}
		}
		line.setAttribute("path", path.join(" ") + " e");
		chart.dataGroup.appendChild(line);
	};
	plotters["area"] = function (series, chart) {
		var tension = 1.5;
		var line = document.createElement("v:shape");
		line.setAttribute("strokeweight", "1px");
		line.setAttribute("strokecolor", series.color);
		line.setAttribute("fillcolor", series.color);
		line.setAttribute("title", series.label);
		line.setAttribute("coordsize", chart.properties.width + "," + chart.properties.height);
		line.style.position = "absolute";
		line.style.top = "0px";
		line.style.left = "0px";
		line.style.width = chart.properties.width + "px";
		line.style.height = chart.properties.height + "px";
		var stroke = document.createElement("v:stroke");
		stroke.setAttribute("opacity", "0.8");
		line.appendChild(stroke);
		var fill = document.createElement("v:fill");
		fill.setAttribute("opacity", "0.4");
		line.appendChild(fill);
		var path = [];
		for (var i = 0; i < series.values.length; i++) {
			var x = Math.round(self.getX(series.values[i].x, chart));
			var y = Math.round(self.getY(series.values[i].value, chart));
			if (i == 0) {
				path.push("m");
				path.push(x + "," + y);
			} else {
				var lastx = Math.round(self.getX(series.values[i - 1].x, chart));
				var lasty = Math.round(self.getY(series.values[i - 1].value, chart));
				var dx = x - lastx;
				var dy = y - lasty;
				path.push("c");
				var cx = Math.round((x - (tension - 1) * (dx / tension)));
				path.push(cx + "," + lasty);
				cx = Math.round((x - (dx / tension)));
				path.push(cx + "," + y);
				path.push(x + "," + y);
			}
		}
		path.push("l");
		path.push(x + "," + self.getY(0, chart));
		path.push("l");
		path.push(self.getX(0, chart) + "," + self.getY(0, chart));
		line.setAttribute("path", path.join(" ") + " x e");
		chart.dataGroup.appendChild(line);
	};
	plotters["scatter"] = function (series, chart) {
		var r = 6;
		for (var i = 0; i < series.values.length; i++) {
			var x = self.getX(series.values[i].x, chart);
			var y = self.getY(series.values[i].value, chart);
			var mod = r / 2;
			var point = document.createElement("v:rect");
			point.setAttribute("fillcolor", series.color);
			point.setAttribute("strokecolor", series.color);
			point.setAttribute("title", series.label + ": " + series.values[i].value);
			point.style.position = "absolute";
			point.style.rotation = "45";
			point.style.top = (y - mod) + "px";
			point.style.left = (x - mod) + "px";
			point.style.width = r + "px";
			point.style.height = r + "px";
			var fill = document.createElement("v:fill");
			fill.setAttribute("opacity", "0.6");
			point.appendChild(fill);
			chart.dataGroup.appendChild(point);
		}
	};
	plotters["bubble"] = function (series, chart) {
		var minR = 1;
		var min = chart.properties.axes.x.range.min;
		var max = chart.properties.axes.x.range.max;
		var ofst = 0 - min;
		min += ofst;
		max += ofst;
		var xmin = chart.properties.padding.left;
		var xmax = chart.properties.width - chart.properties.padding.right;
		var factor = (max - min) / (xmax - xmin) * 25;
		for (var i = 0; i < series.values.length; i++) {
			var size = series.values[i].size;
			if (isNaN(parseFloat(size))) {
				size = minR;
			}
			var radius = (parseFloat(size) * factor) / 2;
			var diameter = radius * 2;
			var cx = self.getX(series.values[i].x, chart);
			var cy = self.getY(series.values[i].value, chart);
			var top = cy - radius;
			var left = cx - radius;
			var point = document.createElement("v:oval");
			point.setAttribute("fillcolor", series.color);
			point.setAttribute("title", series.label + ": " + series.values[i].value + " (" + size + ")");
			point.setAttribute("stroked", "false");
			point.style.position = "absolute";
			point.style.top = top + "px";
			point.style.left = left + "px";
			point.style.width = diameter + "px";
			point.style.height = diameter + "px";
			var fill = document.createElement("v:fill");
			fill.setAttribute("opacity", "0.8");
			point.appendChild(fill);
			chart.dataGroup.appendChild(point);
		}
	};
}();

