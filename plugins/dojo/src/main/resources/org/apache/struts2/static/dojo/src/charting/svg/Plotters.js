/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.charting.svg.Plotters");
dojo.require("dojo.lang.common");
if (dojo.render.svg.capable) {
	dojo.require("dojo.svg");
	dojo.mixin(dojo.charting.Plotters, {Bar:function (plotarea, plot, kwArgs, applyTo) {
		var area = plotarea.getArea();
		var group = document.createElementNS(dojo.svg.xmlns.svg, "g");
		var n = plot.series.length;
		var data = [];
		for (var i = 0; i < n; i++) {
			var tmp = plot.series[i].data.evaluate(kwArgs);
			data.push(tmp);
		}
		var space = 8;
		var nPoints = data[0].length;
		if (nPoints == 0) {
			return group;
		}
		var width = ((area.right - area.left) - (space * (nPoints - 1))) / nPoints;
		var barWidth = width / n;
		var yOrigin = plot.axisY.getCoord(plot.axisX.origin, plotarea, plot);
		for (var i = 0; i < nPoints; i++) {
			var xStart = area.left + (width * i) + (space * i);
			for (var j = 0; j < n; j++) {
				var value = data[j][i].y;
				var yA = yOrigin;
				var x = xStart + (barWidth * j);
				var y = plot.axisY.getCoord(value, plotarea, plot);
				var h = Math.abs(yA - y);
				if (value < plot.axisX.origin) {
					yA = y;
					y = yOrigin;
				}
				var bar = document.createElementNS(dojo.svg.xmlns.svg, "rect");
				bar.setAttribute("fill", data[j][i].series.color);
				bar.setAttribute("stroke-width", "0");
				bar.setAttribute("x", x);
				bar.setAttribute("y", y);
				bar.setAttribute("width", barWidth);
				bar.setAttribute("height", h);
				bar.setAttribute("fill-opacity", "0.6");
				if (applyTo) {
					applyTo(bar, data[j][i].src);
				}
				group.appendChild(bar);
			}
		}
		return group;
	}, HorizontalBar:function (plotarea, plot, kwArgs, applyTo) {
		var area = plotarea.getArea();
		var group = document.createElementNS(dojo.svg.xmlns.svg, "g");
		var n = plot.series.length;
		var data = [];
		for (var i = 0; i < n; i++) {
			var tmp = plot.series[i].data.evaluate(kwArgs);
			data.push(tmp);
		}
		var space = 6;
		var nPoints = data[0].length;
		if (nPoints == 0) {
			return group;
		}
		var h = ((area.bottom - area.top) - (space * (nPoints - 1))) / nPoints;
		var barH = h / n;
		var xOrigin = plot.axisX.getCoord(0, plotarea, plot);
		for (var i = 0; i < nPoints; i++) {
			var yStart = area.top + (h * i) + (space * i);
			for (var j = 0; j < n; j++) {
				var value = data[j][i].y;
				var y = yStart + (barH * j);
				var xA = xOrigin;
				var x = plot.axisX.getCoord(value, plotarea, plot);
				var w = Math.abs(x - xA);
				if (value > 0) {
					x = xOrigin;
				}
				var bar = document.createElementNS(dojo.svg.xmlns.svg, "rect");
				bar.setAttribute("fill", data[j][i].series.color);
				bar.setAttribute("stroke-width", "0");
				bar.setAttribute("x", xA);
				bar.setAttribute("y", y);
				bar.setAttribute("width", w);
				bar.setAttribute("height", barH);
				bar.setAttribute("fill-opacity", "0.6");
				if (applyTo) {
					applyTo(bar, data[j][i].src);
				}
				group.appendChild(bar);
			}
		}
		return group;
	}, Gantt:function (plotarea, plot, kwArgs, applyTo) {
		var area = plotarea.getArea();
		var group = document.createElementNS(dojo.svg.xmlns.svg, "g");
		var n = plot.series.length;
		var data = [];
		for (var i = 0; i < n; i++) {
			var tmp = plot.series[i].data.evaluate(kwArgs);
			data.push(tmp);
		}
		var space = 2;
		var nPoints = data[0].length;
		if (nPoints == 0) {
			return group;
		}
		var h = ((area.bottom - area.top) - (space * (nPoints - 1))) / nPoints;
		var barH = h / n;
		for (var i = 0; i < nPoints; i++) {
			var yStart = area.top + (h * i) + (space * i);
			for (var j = 0; j < n; j++) {
				var high = data[j][i].high;
				var low = data[j][i].low;
				if (low > high) {
					var t = high;
					high = low;
					low = t;
				}
				var x = plot.axisX.getCoord(low, plotarea, plot);
				var w = plot.axisX.getCoord(high, plotarea, plot) - x;
				var y = yStart + (barH * j);
				var bar = document.createElementNS(dojo.svg.xmlns.svg, "rect");
				bar.setAttribute("fill", data[j][i].series.color);
				bar.setAttribute("stroke-width", "0");
				bar.setAttribute("x", x);
				bar.setAttribute("y", y);
				bar.setAttribute("width", w);
				bar.setAttribute("height", barH);
				bar.setAttribute("fill-opacity", "0.6");
				if (applyTo) {
					applyTo(bar, data[j][i].src);
				}
				group.appendChild(bar);
			}
		}
		return group;
	}, StackedArea:function (plotarea, plot, kwArgs, applyTo) {
		var area = plotarea.getArea();
		var group = document.createElementNS(dojo.svg.xmlns.svg, "g");
		var n = plot.series.length;
		var data = [];
		var totals = [];
		for (var i = 0; i < n; i++) {
			var tmp = plot.series[i].data.evaluate(kwArgs);
			for (var j = 0; j < tmp.length; j++) {
				if (i == 0) {
					totals.push(tmp[j].y);
				} else {
					totals[j] += tmp[j].y;
				}
				tmp[j].y = totals[j];
			}
			data.push(tmp);
		}
		for (var i = n - 1; i >= 0; i--) {
			var path = document.createElementNS(dojo.svg.xmlns.svg, "path");
			path.setAttribute("fill", data[i][0].series.color);
			path.setAttribute("fill-opacity", "0.4");
			path.setAttribute("stroke", data[i][0].series.color);
			path.setAttribute("stroke-width", "1");
			path.setAttribute("stroke-opacity", "0.85");
			var cmd = [];
			var r = 3;
			for (var j = 0; j < data[i].length; j++) {
				var values = data[i];
				var x = plot.axisX.getCoord(values[j].x, plotarea, plot);
				var y = plot.axisY.getCoord(values[j].y, plotarea, plot);
				if (j == 0) {
					cmd.push("M");
				} else {
					cmd.push("L");
				}
				cmd.push(x + "," + y);
				var c = document.createElementNS(dojo.svg.xmlns.svg, "circle");
				c.setAttribute("cx", x);
				c.setAttribute("cy", y);
				c.setAttribute("r", "3");
				c.setAttribute("fill", values[j].series.color);
				c.setAttribute("fill-opacity", "0.6");
				c.setAttribute("stroke-width", "1");
				c.setAttribute("stroke-opacity", "0.85");
				group.appendChild(c);
				if (applyTo) {
					applyTo(c, data[i].src);
				}
			}
			if (i == 0) {
				cmd.push("L");
				cmd.push(x + "," + plot.axisY.getCoord(plot.axisX.origin, plotarea, plot));
				cmd.push("L");
				cmd.push(plot.axisX.getCoord(data[0][0].x, plotarea, plot) + "," + plot.axisY.getCoord(plot.axisX.origin, plotarea, plot));
				cmd.push("Z");
			} else {
				var values = data[i - 1];
				cmd.push("L");
				cmd.push(x + "," + Math.round(plot.axisY.getCoord(values[values.length - 1].y, plotarea, plot)));
				for (var j = values.length - 2; j >= 0; j--) {
					var x = plot.axisX.getCoord(values[j].x, plotarea, plot);
					var y = plot.axisY.getCoord(values[j].y, plotarea, plot);
					cmd.push("L");
					cmd.push(x + "," + y);
				}
			}
			path.setAttribute("d", cmd.join(" ") + " Z");
			group.appendChild(path);
		}
		return group;
	}, StackedCurvedArea:function (plotarea, plot, kwArgs, applyTo) {
		var tension = 3;
		var area = plotarea.getArea();
		var group = document.createElementNS(dojo.svg.xmlns.svg, "g");
		var n = plot.series.length;
		var data = [];
		var totals = [];
		for (var i = 0; i < n; i++) {
			var tmp = plot.series[i].data.evaluate(kwArgs);
			for (var j = 0; j < tmp.length; j++) {
				if (i == 0) {
					totals.push(tmp[j].y);
				} else {
					totals[j] += tmp[j].y;
				}
				tmp[j].y = totals[j];
			}
			data.push(tmp);
		}
		for (var i = n - 1; i >= 0; i--) {
			var path = document.createElementNS(dojo.svg.xmlns.svg, "path");
			path.setAttribute("fill", data[i][0].series.color);
			path.setAttribute("fill-opacity", "0.4");
			path.setAttribute("stroke", data[i][0].series.color);
			path.setAttribute("stroke-width", "1");
			path.setAttribute("stroke-opacity", "0.85");
			var cmd = [];
			var r = 3;
			for (var j = 0; j < data[i].length; j++) {
				var values = data[i];
				var x = plot.axisX.getCoord(values[j].x, plotarea, plot);
				var y = plot.axisY.getCoord(values[j].y, plotarea, plot);
				var dx = area.left + 1;
				var dy = area.bottom;
				if (j > 0) {
					dx = x - plot.axisX.getCoord(values[j - 1].x, plotarea, plot);
					dy = plot.axisY.getCoord(values[j - 1].y, plotarea, plot);
				}
				if (j == 0) {
					cmd.push("M");
				} else {
					cmd.push("C");
					var cx = x - (tension - 1) * (dx / tension);
					cmd.push(cx + "," + dy);
					cx = x - (dx / tension);
					cmd.push(cx + "," + y);
				}
				cmd.push(x + "," + y);
				var c = document.createElementNS(dojo.svg.xmlns.svg, "circle");
				c.setAttribute("cx", x);
				c.setAttribute("cy", y);
				c.setAttribute("r", "3");
				c.setAttribute("fill", values[j].series.color);
				c.setAttribute("fill-opacity", "0.6");
				c.setAttribute("stroke-width", "1");
				c.setAttribute("stroke-opacity", "0.85");
				group.appendChild(c);
				if (applyTo) {
					applyTo(c, data[i].src);
				}
			}
			if (i == 0) {
				cmd.push("L");
				cmd.push(x + "," + plot.axisY.getCoord(plot.axisX.origin, plotarea, plot));
				cmd.push("L");
				cmd.push(plot.axisX.getCoord(data[0][0].x, plotarea, plot) + "," + plot.axisY.getCoord(plot.axisX.origin, plotarea, plot));
				cmd.push("Z");
			} else {
				var values = data[i - 1];
				cmd.push("L");
				cmd.push(x + "," + Math.round(plot.axisY.getCoord(values[values.length - 1].y, plotarea, plot)));
				for (var j = values.length - 2; j >= 0; j--) {
					var x = plot.axisX.getCoord(values[j].x, plotarea, plot);
					var y = plot.axisY.getCoord(values[j].y, plotarea, plot);
					var dx = x - plot.axisX.getCoord(values[j + 1].x, plotarea, plot);
					var dy = plot.axisY.getCoord(values[j + 1].y, plotarea, plot);
					cmd.push("C");
					var cx = x - (tension - 1) * (dx / tension);
					cmd.push(cx + "," + dy);
					cx = x - (dx / tension);
					cmd.push(cx + "," + y);
					cmd.push(x + "," + y);
				}
			}
			path.setAttribute("d", cmd.join(" ") + " Z");
			group.appendChild(path);
		}
		return group;
	}, DataBar:function (data, plotarea, plot, applyTo) {
		var area = plotarea.getArea();
		var group = document.createElementNS(dojo.svg.xmlns.svg, "g");
		var n = data.length;
		var w = (area.right - area.left) / (plot.axisX.range.upper - plot.axisX.range.lower);
		var yOrigin = plot.axisY.getCoord(plot.axisX.origin, plotarea, plot);
		for (var i = 0; i < n; i++) {
			var value = data[i].y;
			var yA = yOrigin;
			var x = plot.axisX.getCoord(data[i].x, plotarea, plot) - (w / 2);
			var y = plot.axisY.getCoord(value, plotarea, plot);
			var h = Math.abs(yA - y);
			if (value < plot.axisX.origin) {
				yA = y;
				y = yOrigin;
			}
			var bar = document.createElementNS(dojo.svg.xmlns.svg, "rect");
			bar.setAttribute("fill", data[i].series.color);
			bar.setAttribute("stroke-width", "0");
			bar.setAttribute("x", x);
			bar.setAttribute("y", y);
			bar.setAttribute("width", w);
			bar.setAttribute("height", h);
			bar.setAttribute("fill-opacity", "0.6");
			if (applyTo) {
				applyTo(bar, data[i].src);
			}
			group.appendChild(bar);
		}
		return group;
	}, Line:function (data, plotarea, plot, applyTo) {
		var area = plotarea.getArea();
		var line = document.createElementNS(dojo.svg.xmlns.svg, "g");
		if (data.length == 0) {
			return line;
		}
		var path = document.createElementNS(dojo.svg.xmlns.svg, "path");
		line.appendChild(path);
		path.setAttribute("fill", "none");
		path.setAttribute("stroke", data[0].series.color);
		path.setAttribute("stroke-width", "2");
		path.setAttribute("stroke-opacity", "0.85");
		if (data[0].series.label != null) {
			path.setAttribute("title", data[0].series.label);
		}
		var cmd = [];
		for (var i = 0; i < data.length; i++) {
			var x = plot.axisX.getCoord(data[i].x, plotarea, plot);
			var y = plot.axisY.getCoord(data[i].y, plotarea, plot);
			if (i == 0) {
				cmd.push("M");
			} else {
				cmd.push("L");
			}
			cmd.push(x + "," + y);
			var c = document.createElementNS(dojo.svg.xmlns.svg, "circle");
			c.setAttribute("cx", x);
			c.setAttribute("cy", y);
			c.setAttribute("r", "3");
			c.setAttribute("fill", data[i].series.color);
			c.setAttribute("fill-opacity", "0.6");
			c.setAttribute("stroke-width", "1");
			c.setAttribute("stroke-opacity", "0.85");
			line.appendChild(c);
			if (applyTo) {
				applyTo(c, data[i].src);
			}
		}
		path.setAttribute("d", cmd.join(" "));
		return line;
	}, CurvedLine:function (data, plotarea, plot, applyTo) {
		var tension = 3;
		var area = plotarea.getArea();
		var line = document.createElementNS(dojo.svg.xmlns.svg, "g");
		if (data.length == 0) {
			return line;
		}
		var path = document.createElementNS(dojo.svg.xmlns.svg, "path");
		line.appendChild(path);
		path.setAttribute("fill", "none");
		path.setAttribute("stroke", data[0].series.color);
		path.setAttribute("stroke-width", "2");
		path.setAttribute("stroke-opacity", "0.85");
		if (data[0].series.label != null) {
			path.setAttribute("title", data[0].series.label);
		}
		var cmd = [];
		for (var i = 0; i < data.length; i++) {
			var x = plot.axisX.getCoord(data[i].x, plotarea, plot);
			var y = plot.axisY.getCoord(data[i].y, plotarea, plot);
			var dx = area.left + 1;
			var dy = area.bottom;
			if (i > 0) {
				dx = x - plot.axisX.getCoord(data[i - 1].x, plotarea, plot);
				dy = plot.axisY.getCoord(data[i - 1].y, plotarea, plot);
			}
			if (i == 0) {
				cmd.push("M");
			} else {
				cmd.push("C");
				var cx = x - (tension - 1) * (dx / tension);
				cmd.push(cx + "," + dy);
				cx = x - (dx / tension);
				cmd.push(cx + "," + y);
			}
			cmd.push(x + "," + y);
			var c = document.createElementNS(dojo.svg.xmlns.svg, "circle");
			c.setAttribute("cx", x);
			c.setAttribute("cy", y);
			c.setAttribute("r", "3");
			c.setAttribute("fill", data[i].series.color);
			c.setAttribute("fill-opacity", "0.6");
			c.setAttribute("stroke-width", "1");
			c.setAttribute("stroke-opacity", "0.85");
			line.appendChild(c);
			if (applyTo) {
				applyTo(c, data[i].src);
			}
		}
		path.setAttribute("d", cmd.join(" "));
		return line;
	}, Area:function (data, plotarea, plot, applyTo) {
		var area = plotarea.getArea();
		var line = document.createElementNS(dojo.svg.xmlns.svg, "g");
		if (data.length == 0) {
			return line;
		}
		var path = document.createElementNS(dojo.svg.xmlns.svg, "path");
		line.appendChild(path);
		path.setAttribute("fill", data[0].series.color);
		path.setAttribute("fill-opacity", "0.4");
		path.setAttribute("stroke", data[0].series.color);
		path.setAttribute("stroke-width", "1");
		path.setAttribute("stroke-opacity", "0.85");
		if (data[0].series.label != null) {
			path.setAttribute("title", data[0].series.label);
		}
		var cmd = [];
		for (var i = 0; i < data.length; i++) {
			var x = plot.axisX.getCoord(data[i].x, plotarea, plot);
			var y = plot.axisY.getCoord(data[i].y, plotarea, plot);
			if (i == 0) {
				cmd.push("M");
			} else {
				cmd.push("L");
			}
			cmd.push(x + "," + y);
			var c = document.createElementNS(dojo.svg.xmlns.svg, "circle");
			c.setAttribute("cx", x);
			c.setAttribute("cy", y);
			c.setAttribute("r", "3");
			c.setAttribute("fill", data[i].series.color);
			c.setAttribute("fill-opacity", "0.6");
			c.setAttribute("stroke-width", "1");
			c.setAttribute("stroke-opacity", "0.85");
			line.appendChild(c);
			if (applyTo) {
				applyTo(c, data[i].src);
			}
		}
		cmd.push("L");
		cmd.push(x + "," + plot.axisY.getCoord(plot.axisX.origin, plotarea, plot));
		cmd.push("L");
		cmd.push(plot.axisX.getCoord(data[0].x, plotarea, plot) + "," + plot.axisY.getCoord(plot.axisX.origin, plotarea, plot));
		cmd.push("Z");
		path.setAttribute("d", cmd.join(" "));
		return line;
	}, CurvedArea:function (data, plotarea, plot, applyTo) {
		var tension = 3;
		var area = plotarea.getArea();
		var line = document.createElementNS(dojo.svg.xmlns.svg, "g");
		if (data.length == 0) {
			return line;
		}
		var path = document.createElementNS(dojo.svg.xmlns.svg, "path");
		line.appendChild(path);
		path.setAttribute("fill", data[0].series.color);
		path.setAttribute("fill-opacity", "0.4");
		path.setAttribute("stroke", data[0].series.color);
		path.setAttribute("stroke-width", "1");
		path.setAttribute("stroke-opacity", "0.85");
		if (data[0].series.label != null) {
			path.setAttribute("title", data[0].series.label);
		}
		var cmd = [];
		for (var i = 0; i < data.length; i++) {
			var x = plot.axisX.getCoord(data[i].x, plotarea, plot);
			var y = plot.axisY.getCoord(data[i].y, plotarea, plot);
			var dx = area.left + 1;
			var dy = area.bottom;
			if (i > 0) {
				dx = x - plot.axisX.getCoord(data[i - 1].x, plotarea, plot);
				dy = plot.axisY.getCoord(data[i - 1].y, plotarea, plot);
			}
			if (i == 0) {
				cmd.push("M");
			} else {
				cmd.push("C");
				var cx = x - (tension - 1) * (dx / tension);
				cmd.push(cx + "," + dy);
				cx = x - (dx / tension);
				cmd.push(cx + "," + y);
			}
			cmd.push(x + "," + y);
			var c = document.createElementNS(dojo.svg.xmlns.svg, "circle");
			c.setAttribute("cx", x);
			c.setAttribute("cy", y);
			c.setAttribute("r", "3");
			c.setAttribute("fill", data[i].series.color);
			c.setAttribute("fill-opacity", "0.6");
			c.setAttribute("stroke-width", "1");
			c.setAttribute("stroke-opacity", "0.85");
			line.appendChild(c);
			if (applyTo) {
				applyTo(c, data[i].src);
			}
		}
		cmd.push("L");
		cmd.push(x + "," + plot.axisY.getCoord(plot.axisX.origin, plotarea, plot));
		cmd.push("L");
		cmd.push(plot.axisX.getCoord(data[0].x, plotarea, plot) + "," + plot.axisY.getCoord(plot.axisX.origin, plotarea, plot));
		cmd.push("Z");
		path.setAttribute("d", cmd.join(" "));
		return line;
	}, HighLow:function (data, plotarea, plot, applyTo) {
		var area = plotarea.getArea();
		var group = document.createElementNS(dojo.svg.xmlns.svg, "g");
		var n = data.length;
		var part = ((area.right - area.left) / (plot.axisX.range.upper - plot.axisX.range.lower)) / 4;
		var w = part * 2;
		for (var i = 0; i < n; i++) {
			var high = data[i].high;
			var low = data[i].low;
			if (low > high) {
				var t = low;
				low = high;
				high = t;
			}
			var x = plot.axisX.getCoord(data[i].x, plotarea, plot) - (w / 2);
			var y = plot.axisY.getCoord(high, plotarea, plot);
			var h = plot.axisY.getCoord(low, plotarea, plot) - y;
			var bar = document.createElementNS(dojo.svg.xmlns.svg, "rect");
			bar.setAttribute("fill", data[i].series.color);
			bar.setAttribute("stroke-width", "0");
			bar.setAttribute("x", x);
			bar.setAttribute("y", y);
			bar.setAttribute("width", w);
			bar.setAttribute("height", h);
			bar.setAttribute("fill-opacity", "0.6");
			if (applyTo) {
				applyTo(bar, data[i].src);
			}
			group.appendChild(bar);
		}
		return group;
	}, HighLowClose:function (data, plotarea, plot, applyTo) {
		var area = plotarea.getArea();
		var group = document.createElementNS(dojo.svg.xmlns.svg, "g");
		var n = data.length;
		var part = ((area.right - area.left) / (plot.axisX.range.upper - plot.axisX.range.lower)) / 4;
		var w = part * 2;
		for (var i = 0; i < n; i++) {
			var high = data[i].high;
			var low = data[i].low;
			if (low > high) {
				var t = low;
				low = high;
				high = t;
			}
			var c = data[i].close;
			var x = plot.axisX.getCoord(data[i].x, plotarea, plot) - (w / 2);
			var y = plot.axisY.getCoord(high, plotarea, plot);
			var h = plot.axisY.getCoord(low, plotarea, plot) - y;
			var close = plot.axisY.getCoord(c, plotarea, plot);
			var g = document.createElementNS(dojo.svg.xmlns.svg, "g");
			var bar = document.createElementNS(dojo.svg.xmlns.svg, "rect");
			bar.setAttribute("fill", data[i].series.color);
			bar.setAttribute("stroke-width", "0");
			bar.setAttribute("x", x);
			bar.setAttribute("y", y);
			bar.setAttribute("width", w);
			bar.setAttribute("height", h);
			bar.setAttribute("fill-opacity", "0.6");
			g.appendChild(bar);
			var line = document.createElementNS(dojo.svg.xmlns.svg, "line");
			line.setAttribute("x1", x);
			line.setAttribute("x2", x + w + (part * 2));
			line.setAttribute("y1", close);
			line.setAttribute("y2", close);
			line.setAttribute("style", "stroke:" + data[i].series.color + ";stroke-width:1px;stroke-opacity:0.6;");
			g.appendChild(line);
			if (applyTo) {
				applyTo(g, data[i].src);
			}
			group.appendChild(g);
		}
		return group;
	}, HighLowOpenClose:function (data, plotarea, plot, applyTo) {
		var area = plotarea.getArea();
		var group = document.createElementNS(dojo.svg.xmlns.svg, "g");
		var n = data.length;
		var part = ((area.right - area.left) / (plot.axisX.range.upper - plot.axisX.range.lower)) / 4;
		var w = part * 2;
		for (var i = 0; i < n; i++) {
			var high = data[i].high;
			var low = data[i].low;
			if (low > high) {
				var t = low;
				low = high;
				high = t;
			}
			var o = data[i].open;
			var c = data[i].close;
			var x = plot.axisX.getCoord(data[i].x, plotarea, plot) - (w / 2);
			var y = plot.axisY.getCoord(high, plotarea, plot);
			var h = plot.axisY.getCoord(low, plotarea, plot) - y;
			var open = plot.axisY.getCoord(o, plotarea, plot);
			var close = plot.axisY.getCoord(c, plotarea, plot);
			var g = document.createElementNS(dojo.svg.xmlns.svg, "g");
			var bar = document.createElementNS(dojo.svg.xmlns.svg, "rect");
			bar.setAttribute("fill", data[i].series.color);
			bar.setAttribute("stroke-width", "0");
			bar.setAttribute("x", x);
			bar.setAttribute("y", y);
			bar.setAttribute("width", w);
			bar.setAttribute("height", h);
			bar.setAttribute("fill-opacity", "0.6");
			g.appendChild(bar);
			var line = document.createElementNS(dojo.svg.xmlns.svg, "line");
			line.setAttribute("x1", x - (part * 2));
			line.setAttribute("x2", x + w);
			line.setAttribute("y1", open);
			line.setAttribute("y2", open);
			line.setAttribute("style", "stroke:" + data[i].series.color + ";stroke-width:1px;stroke-opacity:0.6;");
			g.appendChild(line);
			var line = document.createElementNS(dojo.svg.xmlns.svg, "line");
			line.setAttribute("x1", x);
			line.setAttribute("x2", x + w + (part * 2));
			line.setAttribute("y1", close);
			line.setAttribute("y2", close);
			line.setAttribute("style", "stroke:" + data[i].series.color + ";stroke-width:1px;stroke-opacity:0.6;");
			g.appendChild(line);
			if (applyTo) {
				applyTo(g, data[i].src);
			}
			group.appendChild(g);
		}
		return group;
	}, Scatter:function (data, plotarea, plot, applyTo) {
		var r = 7;
		var group = document.createElementNS(dojo.svg.xmlns.svg, "g");
		for (var i = 0; i < data.length; i++) {
			var x = plot.axisX.getCoord(data[i].x, plotarea, plot);
			var y = plot.axisY.getCoord(data[i].y, plotarea, plot);
			var point = document.createElementNS(dojo.svg.xmlns.svg, "path");
			point.setAttribute("fill", data[i].series.color);
			point.setAttribute("stroke-width", "0");
			point.setAttribute("d", "M " + x + "," + (y - r) + " " + "Q " + x + "," + y + " " + (x + r) + "," + y + " " + "Q " + x + "," + y + " " + x + "," + (y + r) + " " + "Q " + x + "," + y + " " + (x - r) + "," + y + " " + "Q " + x + "," + y + " " + x + "," + (y - r) + " " + "Z");
			if (applyTo) {
				applyTo(point, data[i].src);
			}
			group.appendChild(point);
		}
		return group;
	}, Bubble:function (data, plotarea, plot, applyTo) {
		var group = document.createElementNS(dojo.svg.xmlns.svg, "g");
		var sizeFactor = 1;
		for (var i = 0; i < data.length; i++) {
			var x = plot.axisX.getCoord(data[i].x, plotarea, plot);
			var y = plot.axisY.getCoord(data[i].y, plotarea, plot);
			if (i == 0) {
				var raw = data[i].size;
				var dy = plot.axisY.getCoord(data[i].y + raw, plotarea, plot) - y;
				sizeFactor = dy / raw;
			}
			if (sizeFactor < 1) {
				sizeFactor = 1;
			}
			var point = document.createElementNS(dojo.svg.xmlns.svg, "circle");
			point.setAttribute("fill", data[i].series.color);
			point.setAttribute("fill-opacity", "0.8");
			point.setAttribute("stroke", data[i].series.color);
			point.setAttribute("stroke-width", "1");
			point.setAttribute("cx", x);
			point.setAttribute("cy", y);
			point.setAttribute("r", (data[i].size / 2) * sizeFactor);
			if (applyTo) {
				applyTo(point, data[i].src);
			}
			group.appendChild(point);
		}
		return group;
	}});
	dojo.charting.Plotters["Default"] = dojo.charting.Plotters.Line;
}

