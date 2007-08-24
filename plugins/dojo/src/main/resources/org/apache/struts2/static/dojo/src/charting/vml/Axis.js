/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.charting.vml.Axis");
dojo.require("dojo.lang.common");
if (dojo.render.vml.capable) {
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
		var g = this.nodes.lines = document.createElement("div");
		g.setAttribute("id", this.getId() + "-lines");
		for (var i = 0; i < this._labels.length; i++) {
			if (this._labels[i].value == this.origin) {
				continue;
			}
			var v = this.getCoord(this._labels[i].value, plotArea, plot);
			var l = document.createElement("v:line");
			var str = document.createElement("v:stroke");
			str.dashstyle = "dot";
			l.appendChild(str);
			l.setAttribute("strokecolor", "#666");
			l.setAttribute("strokeweight", "1px");
			var s = l.style;
			s.position = "absolute";
			s.top = "0px";
			s.left = "0px";
			s.antialias = "false";
			if (plane == "x") {
				l.setAttribute("from", v + "px," + area.top + "px");
				l.setAttribute("to", v + "px," + area.bottom + "px");
			} else {
				if (plane == "y") {
					l.setAttribute("from", area.left + "px," + v + "px");
					l.setAttribute("to", area.right + "px," + v + "px");
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
		var g = this.nodes.ticks = document.createElement("div");
		g.setAttribute("id", this.getId() + "-ticks");
		for (var i = 0; i < this._labels.length; i++) {
			var v = this.getCoord(this._labels[i].value, plotArea, plot);
			var l = document.createElement("v:line");
			l.setAttribute("strokecolor", "#000");
			l.setAttribute("strokeweight", "1px");
			var s = l.style;
			s.position = "absolute";
			s.top = "0px";
			s.left = "0px";
			s.antialias = "false";
			if (plane == "x") {
				l.setAttribute("from", v + "px," + coord + "px");
				l.setAttribute("to", v + "px," + (coord + 3) + "px");
			} else {
				if (plane == "y") {
					l.setAttribute("from", (coord - 2) + "px," + v + "px");
					l.setAttribute("to", (coord + 2) + "px," + v + "px");
				}
			}
			g.appendChild(l);
		}
		return g;
	}, renderLabels:function (plotArea, plot, plane, coord, textSize, anchor) {
		function createLabel(label, x, y, textSize, anchor) {
			var text = document.createElement("div");
			var s = text.style;
			text.innerHTML = label;
			s.fontSize = textSize + "px";
			s.fontFamily = "sans-serif";
			s.position = "absolute";
			s.top = y + "px";
			if (anchor == "center") {
				s.left = x + "px";
				s.textAlign = "center";
			} else {
				if (anchor == "left") {
					s.left = x + "px";
					s.textAlign = "left";
				} else {
					if (anchor == "right") {
						s.right = x + "px";
						s.textAlign = "right";
					}
				}
			}
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
		var g = this.nodes.labels = document.createElement("div");
		g.setAttribute("id", this.getId() + "-labels");
		for (var i = 0; i < this._labels.length; i++) {
			var v = this.getCoord(this._labels[i].value, plotArea, plot);
			if (plane == "x") {
				var node = createLabel(this._labels[i].label, v, coord, textSize, anchor);
				document.body.appendChild(node);
				node.style.left = v - (node.offsetWidth / 2) + "px";
				g.appendChild(node);
			} else {
				if (plane == "y") {
					var node = createLabel(this._labels[i].label, coord, v, textSize, anchor);
					document.body.appendChild(node);
					node.style.top = v - (node.offsetHeight / 2) + "px";
					g.appendChild(node);
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
		var g = this.nodes.main = document.createElement("div");
		g.setAttribute("id", this.getId());
		var line = this.nodes.axis = document.createElement("v:line");
		line.setAttribute("strokecolor", "#000");
		line.setAttribute("strokeweight", stroke + "px");
		var s = line.style;
		s.position = "absolute";
		s.top = "0px";
		s.left = "0px";
		s.antialias = "false";
		if (plane == "x") {
			line.setAttribute("from", area.left + "px," + coord + "px");
			line.setAttribute("to", area.right + "px," + coord + "px");
			var y = coord + Math.floor(textSize / 2);
			if (this.showLines) {
				g.appendChild(this.renderLines(plotArea, plot, plane, y));
			}
			if (this.showTicks) {
				g.appendChild(this.renderTicks(plotArea, plot, plane, coord));
			}
			if (this.showLabels) {
				g.appendChild(this.renderLabels(plotArea, plot, plane, y, textSize, "center"));
			}
			if (this.showLabel && this.label) {
				var x = plotArea.size.width / 2;
				var y = coord + Math.round(textSize * 1.5);
				var text = document.createElement("div");
				var s = text.style;
				text.innerHTML = this.label;
				s.fontSize = (textSize + 2) + "px";
				s.fontFamily = "sans-serif";
				s.fontWeight = "bold";
				s.position = "absolute";
				s.top = y + "px";
				s.left = x + "px";
				s.textAlign = "center";
				document.body.appendChild(text);
				text.style.left = x - (text.offsetWidth / 2) + "px";
				g.appendChild(text);
			}
		} else {
			line.setAttribute("from", coord + "px," + area.top + "px");
			line.setAttribute("to", coord + "px," + area.bottom + "px");
			var isMax = this.origin == drawAgainst.range.upper;
			var x = coord + 4;
			var anchor = "left";
			if (!isMax) {
				x = area.right - coord + textSize + 4;
				anchor = "right";
				if (coord == area.left) {
					x += (textSize * 2) - (textSize / 2);
				}
			}
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
				x += (textSize * 2) - 2;
				var y = plotArea.size.height / 2;
				var text = document.createElement("div");
				var s = text.style;
				text.innerHTML = this.label;
				s.fontSize = (textSize + 2) + "px";
				s.fontFamily = "sans-serif";
				s.fontWeight = "bold";
				s.position = "absolute";
				s.height = plotArea.size.height + "px";
				s.writingMode = "tb-rl";
				s.textAlign = "center";
				s[anchor] = x + "px";
				document.body.appendChild(text);
				s.top = y - (text.offsetHeight / 2) + "px";
				g.appendChild(text);
			}
		}
		g.appendChild(line);
		return g;
	}});
}

