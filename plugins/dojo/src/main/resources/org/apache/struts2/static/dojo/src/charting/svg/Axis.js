/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.charting.svg.Axis");
dojo.require("dojo.lang.common");
if (dojo.render.svg.capable) {
	dojo.extend(dojo.charting.Axis, {renderLines:function (plotArea, plot, plane) {
		if (this.nodes.lines) {
			while (this.nodes.lines.childNodes.length > 0) {
				this.nodes.lines.removeChild(this.nodes.lines.childNodes[0]);
			}
			if (this.nodes.lines.parentNode) {
				this.nodes.lines.parentNode.removeChild(this.nodes.lines);
				this.nodes.lines = null;
			}
		}
		var area = plotArea.getArea();
		var g = this.nodes.lines = document.createElementNS(dojo.svg.xmlns.svg, "g");
		g.setAttribute("id", this.getId() + "-lines");
		for (var i = 0; i < this._labels.length; i++) {
			if (this._labels[i].value == this.origin) {
				continue;
			}
			var v = this.getCoord(this._labels[i].value, plotArea, plot);
			var l = document.createElementNS(dojo.svg.xmlns.svg, "line");
			l.setAttribute("style", "stroke:#999;stroke-width:1px;stroke-dasharray:1,4;");
			if (plane == "x") {
				l.setAttribute("y1", area.top);
				l.setAttribute("y2", area.bottom);
				l.setAttribute("x1", v);
				l.setAttribute("x2", v);
			} else {
				if (plane == "y") {
					l.setAttribute("y1", v);
					l.setAttribute("y2", v);
					l.setAttribute("x1", area.left);
					l.setAttribute("x2", area.right);
				}
			}
			g.appendChild(l);
		}
		return g;
	}, renderTicks:function (plotArea, plot, plane, coord) {
		if (this.nodes.ticks) {
			while (this.nodes.ticks.childNodes.length > 0) {
				this.nodes.ticks.removeChild(this.nodes.ticks.childNodes[0]);
			}
			if (this.nodes.ticks.parentNode) {
				this.nodes.ticks.parentNode.removeChild(this.nodes.ticks);
				this.nodes.ticks = null;
			}
		}
		var g = this.nodes.ticks = document.createElementNS(dojo.svg.xmlns.svg, "g");
		g.setAttribute("id", this.getId() + "-ticks");
		for (var i = 0; i < this._labels.length; i++) {
			var v = this.getCoord(this._labels[i].value, plotArea, plot);
			var l = document.createElementNS(dojo.svg.xmlns.svg, "line");
			l.setAttribute("style", "stroke:#000;stroke-width:1pt;");
			if (plane == "x") {
				l.setAttribute("y1", coord);
				l.setAttribute("y2", coord + 3);
				l.setAttribute("x1", v);
				l.setAttribute("x2", v);
			} else {
				if (plane == "y") {
					l.setAttribute("y1", v);
					l.setAttribute("y2", v);
					l.setAttribute("x1", coord - 2);
					l.setAttribute("x2", coord + 2);
				}
			}
			g.appendChild(l);
		}
		return g;
	}, renderLabels:function (plotArea, plot, plane, coord, textSize, anchor) {
		function createLabel(label, x, y, textSize, anchor) {
			var text = document.createElementNS(dojo.svg.xmlns.svg, "text");
			text.setAttribute("x", x);
			text.setAttribute("y", (plane == "x" ? y : y + 2));
			text.setAttribute("style", "text-anchor:" + anchor + ";font-family:sans-serif;font-size:" + textSize + "px;fill:#000;");
			text.appendChild(document.createTextNode(label));
			return text;
		}
		if (this.nodes.labels) {
			while (this.nodes.labels.childNodes.length > 0) {
				this.nodes.labels.removeChild(this.nodes.labels.childNodes[0]);
			}
			if (this.nodes.labels.parentNode) {
				this.nodes.labels.parentNode.removeChild(this.nodes.labels);
				this.nodes.labels = null;
			}
		}
		var g = this.nodes.labels = document.createElementNS(dojo.svg.xmlns.svg, "g");
		g.setAttribute("id", this.getId() + "-labels");
		for (var i = 0; i < this._labels.length; i++) {
			var v = this.getCoord(this._labels[i].value, plotArea, plot);
			if (plane == "x") {
				g.appendChild(createLabel(this._labels[i].label, v, coord, textSize, anchor));
			} else {
				if (plane == "y") {
					g.appendChild(createLabel(this._labels[i].label, coord, v, textSize, anchor));
				}
			}
		}
		return g;
	}, render:function (plotArea, plot, drawAgainst, plane) {
		if (!this._rerender && this.nodes.main) {
			return this.nodes.main;
		}
		this._rerender = false;
		var area = plotArea.getArea();
		var stroke = 1;
		var style = "stroke:#000;stroke-width:" + stroke + "px;";
		var textSize = 10;
		var coord = drawAgainst.getCoord(this.origin, plotArea, plot);
		this.nodes.main = document.createElementNS(dojo.svg.xmlns.svg, "g");
		var g = this.nodes.main;
		g.setAttribute("id", this.getId());
		var line = this.nodes.axis = document.createElementNS(dojo.svg.xmlns.svg, "line");
		if (plane == "x") {
			line.setAttribute("y1", coord);
			line.setAttribute("y2", coord);
			line.setAttribute("x1", area.left - stroke);
			line.setAttribute("x2", area.right + stroke);
			line.setAttribute("style", style);
			var y = coord + textSize + 2;
			if (this.showLines) {
				g.appendChild(this.renderLines(plotArea, plot, plane, y));
			}
			if (this.showTicks) {
				g.appendChild(this.renderTicks(plotArea, plot, plane, coord));
			}
			if (this.showLabels) {
				g.appendChild(this.renderLabels(plotArea, plot, plane, y, textSize, "middle"));
			}
			if (this.showLabel && this.label) {
				var x = plotArea.size.width / 2;
				var text = document.createElementNS(dojo.svg.xmlns.svg, "text");
				text.setAttribute("x", x);
				text.setAttribute("y", (coord + (textSize * 2) + (textSize / 2)));
				text.setAttribute("style", "text-anchor:middle;font-family:sans-serif;font-weight:bold;font-size:" + (textSize + 2) + "px;fill:#000;");
				text.appendChild(document.createTextNode(this.label));
				g.appendChild(text);
			}
		} else {
			line.setAttribute("x1", coord);
			line.setAttribute("x2", coord);
			line.setAttribute("y1", area.top);
			line.setAttribute("y2", area.bottom);
			line.setAttribute("style", style);
			var isMax = this.origin == drawAgainst.range.upper;
			var x = coord + (isMax ? 4 : -4);
			var anchor = isMax ? "start" : "end";
			if (this.showLines) {
				g.appendChild(this.renderLines(plotArea, plot, plane, x));
			}
			if (this.showTicks) {
				g.appendChild(this.renderTicks(plotArea, plot, plane, coord));
			}
			if (this.showLabels) {
				g.appendChild(this.renderLabels(plotArea, plot, plane, x, textSize, anchor));
			}
			if (this.showLabel && this.label) {
				var x = isMax ? (coord + (textSize * 2) + (textSize / 2)) : (coord - (textSize * 4));
				var y = plotArea.size.height / 2;
				var text = document.createElementNS(dojo.svg.xmlns.svg, "text");
				text.setAttribute("x", x);
				text.setAttribute("y", y);
				text.setAttribute("transform", "rotate(90, " + x + ", " + y + ")");
				text.setAttribute("style", "text-anchor:middle;font-family:sans-serif;font-weight:bold;font-size:" + (textSize + 2) + "px;fill:#000;");
				text.appendChild(document.createTextNode(this.label));
				g.appendChild(text);
			}
		}
		g.appendChild(line);
		return g;
	}});
}

