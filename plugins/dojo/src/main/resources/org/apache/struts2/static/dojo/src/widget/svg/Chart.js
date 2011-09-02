/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.svg.Chart");
dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.widget.Chart");
dojo.require("dojo.html.layout");
dojo.require("dojo.math");
dojo.require("dojo.svg");
dojo.require("dojo.gfx.color");
dojo.require("dojo.json");
dojo.widget.defineWidget("dojo.widget.svg.Chart", [dojo.widget.HtmlWidget, dojo.widget.Chart], function () {
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
	dojo.svg.g.suspend();
	if (this.vectorNode) {
		this.destroy();
	}
	this.vectorNode = document.createElementNS(dojo.svg.xmlns.svg, "svg");
	this.vectorNode.setAttribute("width", this.properties.width);
	this.vectorNode.setAttribute("height", this.properties.height);
	dojo.svg.g.resume();
}, drawPlotArea:function () {
	dojo.svg.g.suspend();
	if (this.plotArea) {
		this.plotArea.parentNode.removeChild(this.plotArea);
		this.plotArea = null;
	}
	var defs = document.createElementNS(dojo.svg.xmlns.svg, "defs");
	var clip = document.createElementNS(dojo.svg.xmlns.svg, "clipPath");
	clip.setAttribute("id", "plotClip" + this.widgetId);
	var rect = document.createElementNS(dojo.svg.xmlns.svg, "rect");
	rect.setAttribute("x", this.properties.padding.left);
	rect.setAttribute("y", this.properties.padding.top);
	rect.setAttribute("width", this.properties.width - this.properties.padding.left - this.properties.padding.right);
	rect.setAttribute("height", this.properties.height - this.properties.padding.top - this.properties.padding.bottom);
	clip.appendChild(rect);
	defs.appendChild(clip);
	this.vectorNode.appendChild(defs);
	this.plotArea = document.createElementNS(dojo.svg.xmlns.svg, "g");
	this.vectorNode.appendChild(this.plotArea);
	var rect = document.createElementNS(dojo.svg.xmlns.svg, "rect");
	rect.setAttribute("x", this.properties.padding.left);
	rect.setAttribute("y", this.properties.padding.top);
	rect.setAttribute("width", this.properties.width - this.properties.padding.left - this.properties.padding.right);
	rect.setAttribute("height", this.properties.height - this.properties.padding.top - this.properties.padding.bottom);
	rect.setAttribute("fill", "#fff");
	this.plotArea.appendChild(rect);
	dojo.svg.g.resume();
}, drawDataGroup:function () {
	dojo.svg.g.suspend();
	if (this.dataGroup) {
		this.dataGroup.parentNode.removeChild(this.dataGroup);
		this.dataGroup = null;
	}
	this.dataGroup = document.createElementNS(dojo.svg.xmlns.svg, "g");
	this.dataGroup.setAttribute("style", "clip-path:url(#plotClip" + this.widgetId + ");");
	this.plotArea.appendChild(this.dataGroup);
	dojo.svg.g.resume();
}, drawAxes:function () {
	dojo.svg.g.suspend();
	if (this.axisGroup) {
		this.axisGroup.parentNode.removeChild(this.axisGroup);
		this.axisGroup = null;
	}
	this.axisGroup = document.createElementNS(dojo.svg.xmlns.svg, "g");
	this.plotArea.appendChild(this.axisGroup);
	var stroke = 1;
	var line = document.createElementNS(dojo.svg.xmlns.svg, "line");
	var y = dojo.widget.svg.Chart.Plotter.getY(this.properties.axes.x.plotAt, this);
	line.setAttribute("y1", y);
	line.setAttribute("y2", y);
	line.setAttribute("x1", this.properties.padding.left - stroke);
	line.setAttribute("x2", this.properties.width - this.properties.padding.right);
	line.setAttribute("style", "stroke:#000;stroke-width:" + stroke + ";");
	this.axisGroup.appendChild(line);
	var textSize = 10;
	var text = document.createElementNS(dojo.svg.xmlns.svg, "text");
	text.setAttribute("x", this.properties.padding.left);
	text.setAttribute("y", this.properties.height - this.properties.padding.bottom + textSize + 2);
	text.setAttribute("style", "text-anchor:middle;font-size:" + textSize + "px;fill:#000;");
	text.appendChild(document.createTextNode(dojo.math.round(parseFloat(this.properties.axes.x.range.min), 2)));
	this.axisGroup.appendChild(text);
	var text = document.createElementNS(dojo.svg.xmlns.svg, "text");
	text.setAttribute("x", this.properties.width - this.properties.padding.right - (textSize / 2));
	text.setAttribute("y", this.properties.height - this.properties.padding.bottom + textSize + 2);
	text.setAttribute("style", "text-anchor:middle;font-size:" + textSize + "px;fill:#000;");
	text.appendChild(document.createTextNode(dojo.math.round(parseFloat(this.properties.axes.x.range.max), 2)));
	this.axisGroup.appendChild(text);
	var line = document.createElementNS(dojo.svg.xmlns.svg, "line");
	var x = dojo.widget.svg.Chart.Plotter.getX(this.properties.axes.y.plotAt, this);
	line.setAttribute("x1", x);
	line.setAttribute("x2", x);
	line.setAttribute("y1", this.properties.padding.top);
	line.setAttribute("y2", this.properties.height - this.properties.padding.bottom);
	line.setAttribute("style", "stroke:#000;stroke-width:" + stroke + ";");
	this.axisGroup.appendChild(line);
	var text = document.createElementNS(dojo.svg.xmlns.svg, "text");
	text.setAttribute("x", this.properties.padding.left - 4);
	text.setAttribute("y", this.properties.height - this.properties.padding.bottom);
	text.setAttribute("style", "text-anchor:end;font-size:" + textSize + "px;fill:#000;");
	text.appendChild(document.createTextNode(dojo.math.round(parseFloat(this.properties.axes.y.range.min), 2)));
	this.axisGroup.appendChild(text);
	var text = document.createElementNS(dojo.svg.xmlns.svg, "text");
	text.setAttribute("x", this.properties.padding.left - 4);
	text.setAttribute("y", this.properties.padding.top + (textSize / 2));
	text.setAttribute("style", "text-anchor:end;font-size:" + textSize + "px;fill:#000;");
	text.appendChild(document.createTextNode(dojo.math.round(parseFloat(this.properties.axes.y.range.max), 2)));
	this.axisGroup.appendChild(text);
	dojo.svg.g.resume();
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
	this.domNode.appendChild(this.vectorNode);
	this.assignColors();
	this._isInitialized = true;
}, destroy:function () {
	while (this.domNode.childNodes.length > 0) {
		this.domNode.removeChild(this.domNode.childNodes.item(0));
	}
	this.vectorNode = this.plotArea = this.dataGroup = this.axisGroup = null;
}, render:function () {
	dojo.svg.g.suspend();
	if (this.dataGroup) {
		while (this.dataGroup.childNodes.length > 0) {
			this.dataGroup.removeChild(this.dataGroup.childNodes.item(0));
		}
	} else {
		this.init();
	}
	for (var i = 0; i < this.series.length; i++) {
		dojo.widget.svg.Chart.Plotter.plot(this.series[i], this);
	}
	dojo.svg.g.resume();
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
dojo.widget.svg.Chart.Plotter = new function () {
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
		var xmin = chart.properties.padding.left;
		var xmax = chart.properties.width - chart.properties.padding.right;
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
		var ymin = chart.properties.height - chart.properties.padding.bottom;
		var ymax = chart.properties.padding.top;
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
			var yA = self.getY(chart.properties.axes.x.plotAt, chart);
			var y = self.getY(series.values[i].value, chart);
			var h = Math.abs(yA - y);
			if (parseFloat(series.values[i].value) < chart.properties.axes.x.plotAt) {
				var oy = yA;
				yA = y;
				y = oy;
			}
			var bar = document.createElementNS(dojo.svg.xmlns.svg, "rect");
			bar.setAttribute("fill", series.color);
			bar.setAttribute("title", series.label + ": " + series.values[i].value);
			bar.setAttribute("stroke-width", "0");
			bar.setAttribute("x", x);
			bar.setAttribute("y", y);
			bar.setAttribute("width", w);
			bar.setAttribute("height", h);
			bar.setAttribute("fill-opacity", "0.9");
			chart.dataGroup.appendChild(bar);
		}
	};
	plotters["line"] = function (series, chart) {
		var tension = 1.5;
		var line = document.createElementNS(dojo.svg.xmlns.svg, "path");
		line.setAttribute("fill", "none");
		line.setAttribute("stroke", series.color);
		line.setAttribute("stroke-width", "2");
		line.setAttribute("stroke-opacity", "0.85");
		line.setAttribute("title", series.label);
		chart.dataGroup.appendChild(line);
		var path = [];
		for (var i = 0; i < series.values.length; i++) {
			var x = self.getX(series.values[i].x, chart);
			var y = self.getY(series.values[i].value, chart);
			var dx = chart.properties.padding.left + 1;
			var dy = chart.properties.height - chart.properties.padding.bottom;
			if (i > 0) {
				dx = x - self.getX(series.values[i - 1].x, chart);
				dy = self.getY(series.values[i - 1].value, chart);
			}
			if (i == 0) {
				path.push("M");
			} else {
				path.push("C");
				var cx = x - (tension - 1) * (dx / tension);
				path.push(cx + "," + dy);
				cx = x - (dx / tension);
				path.push(cx + "," + y);
			}
			path.push(x + "," + y);
		}
		line.setAttribute("d", path.join(" "));
	};
	plotters["area"] = function (series, chart) {
		var tension = 1.5;
		var line = document.createElementNS(dojo.svg.xmlns.svg, "path");
		line.setAttribute("fill", series.color);
		line.setAttribute("fill-opacity", "0.4");
		line.setAttribute("stroke", series.color);
		line.setAttribute("stroke-width", "1");
		line.setAttribute("stroke-opacity", "0.8");
		line.setAttribute("title", series.label);
		chart.dataGroup.appendChild(line);
		var path = [];
		for (var i = 0; i < series.values.length; i++) {
			var x = self.getX(series.values[i].x, chart);
			var y = self.getY(series.values[i].value, chart);
			var dx = chart.properties.padding.left + 1;
			var dy = chart.properties.height - chart.properties.padding.bottom;
			if (i > 0) {
				dx = x - self.getX(series.values[i - 1].x, chart);
				dy = self.getY(series.values[i - 1].value, chart);
			}
			if (i == 0) {
				path.push("M");
			} else {
				path.push("C");
				var cx = x - (tension - 1) * (dx / tension);
				path.push(cx + "," + dy);
				cx = x - (dx / tension);
				path.push(cx + "," + y);
			}
			path.push(x + "," + y);
		}
		path.push("L");
		path.push(x + "," + self.getY(0, chart));
		path.push("L");
		path.push(self.getX(0, chart) + "," + self.getY(0, chart));
		path.push("Z");
		line.setAttribute("d", path.join(" "));
	}, plotters["scatter"] = function (series, chart) {
		var r = 7;
		for (var i = 0; i < series.values.length; i++) {
			var x = self.getX(series.values[i].x, chart);
			var y = self.getY(series.values[i].value, chart);
			var point = document.createElementNS(dojo.svg.xmlns.svg, "path");
			point.setAttribute("fill", series.color);
			point.setAttribute("stroke-width", "0");
			point.setAttribute("title", series.label + ": " + series.values[i].value);
			point.setAttribute("d", "M " + x + "," + (y - r) + " " + "Q " + x + "," + y + " " + (x + r) + "," + y + " " + "Q " + x + "," + y + " " + x + "," + (y + r) + " " + "Q " + x + "," + y + " " + (x - r) + "," + y + " " + "Q " + x + "," + y + " " + x + "," + (y - r) + " " + "Z");
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
			var point = document.createElementNS(dojo.svg.xmlns.svg, "circle");
			point.setAttribute("stroke-width", 0);
			point.setAttribute("fill", series.color);
			point.setAttribute("fill-opacity", "0.8");
			point.setAttribute("r", (parseFloat(size) * factor) / 2);
			point.setAttribute("cx", self.getX(series.values[i].x, chart));
			point.setAttribute("cy", self.getY(series.values[i].value, chart));
			point.setAttribute("title", series.label + ": " + series.values[i].value + " (" + size + ")");
			chart.dataGroup.appendChild(point);
		}
	};
}();

