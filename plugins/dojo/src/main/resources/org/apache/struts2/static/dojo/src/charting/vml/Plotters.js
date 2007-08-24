/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.charting.vml.Plotters");
dojo.require("dojo.lang.common");
if (dojo.render.vml.capable) {
	dojo.mixin(dojo.charting.Plotters, {_group:function (plotarea) {
		var group = document.createElement("div");
		group.style.position = "absolute";
		group.style.top = "0px";
		group.style.left = "0px";
		group.style.width = plotarea.size.width + "px";
		group.style.height = plotarea.size.height + "px";
		return group;
	}, Bar:function (plotarea, plot, kwArgs, applyTo) {
		var area = plotarea.getArea();
		var group = dojo.charting.Plotters._group(plotarea);
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
		var barWidth = Math.round(width / n);
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
				var bar = document.createElement("v:rect");
				bar.style.position = "absolute";
				bar.style.top = y + 1 + "px";
				bar.style.left = x + "px";
				bar.style.width = barWidth + "px";
				bar.style.height = h + "px";
				bar.setAttribute("fillColor", data[j][i].series.color);
				bar.setAttribute("stroked", "false");
				bar.style.antialias = "false";
				var fill = document.createElement("v:fill");
				fill.setAttribute("opacity", "0.6");
				bar.appendChild(fill);
				if (applyTo) {
					applyTo(bar, data[j][i].src);
				}
				group.appendChild(bar);
			}
		}
		return group;
	}, HorizontalBar:function (plotarea, plot, kwArgs, applyTo) {
		var area = plotarea.getArea();
		var group = dojo.charting.Plotters._group(plotarea);
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
				var bar = document.createElement("v:rect");
				bar.style.position = "absolute";
				bar.style.top = y + 1 + "px";
				bar.style.left = xA + "px";
				bar.style.width = w + "px";
				bar.style.height = barH + "px";
				bar.setAttribute("fillColor", data[j][i].series.color);
				bar.setAttribute("stroked", "false");
				bar.style.antialias = "false";
				var fill = document.createElement("v:fill");
				fill.setAttribute("opacity", "0.6");
				bar.appendChild(fill);
				if (applyTo) {
					applyTo(bar, data[j][i].src);
				}
				group.appendChild(bar);
			}
		}
		var space = 4;
		var n = plot.series.length;
		var h = ((area.bottom - area.top) - (space * (n - 1))) / n;
		var xOrigin = plot.axisX.getCoord(0, plotarea, plot);
		for (var i = 0; i < n; i++) {
			var series = plot.series[i];
			var data = series.data.evaluate(kwArgs);
			var y = area.top + (h * i) + (space * i);
			var value = data[data.length - 1].y;
			var xA = xOrigin;
			var x = plot.axisX.getCoord(value, plotarea, plot);
			var w = Math.abs(xA - x);
			if (value > 0) {
				xA = x;
				x = xOrigin;
			}
		}
		return group;
	}, Gantt:function (plotarea, plot, kwArgs, applyTo) {
		var area = plotarea.getArea();
		var group = dojo.charting.Plotters._group(plotarea);
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
				var bar = document.createElement("v:rect");
				bar.style.position = "absolute";
				bar.style.top = y + 1 + "px";
				bar.style.left = x + "px";
				bar.style.width = w + "px";
				bar.style.height = barH + "px";
				bar.setAttribute("fillColor", data[j][i].series.color);
				bar.setAttribute("stroked", "false");
				bar.style.antialias = "false";
				var fill = document.createElement("v:fill");
				fill.setAttribute("opacity", "0.6");
				bar.appendChild(fill);
				if (applyTo) {
					applyTo(bar, data[j][i].src);
				}
				group.appendChild(bar);
			}
		}
		return group;
	}, StackedArea:function (plotarea, plot, kwArgs, applyTo) {
		var area = plotarea.getArea();
		var group = dojo.charting.Plotters._group(plotarea);
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
			var path = document.createElement("v:shape");
			path.setAttribute("strokeweight", "1px");
			path.setAttribute("strokecolor", data[i][0].series.color);
			path.setAttribute("fillcolor", data[i][0].series.color);
			path.setAttribute("coordsize", (area.right - area.left) + "," + (area.bottom - area.top));
			path.style.position = "absolute";
			path.style.top = "0px";
			path.style.left = "0px";
			path.style.width = area.right - area.left + "px";
			path.style.height = area.bottom - area.top + "px";
			var stroke = document.createElement("v:stroke");
			stroke.setAttribute("opacity", "0.8");
			path.appendChild(stroke);
			var fill = document.createElement("v:fill");
			fill.setAttribute("opacity", "0.4");
			path.appendChild(fill);
			var cmd = [];
			var r = 3;
			for (var j = 0; j < data[i].length; j++) {
				var values = data[i];
				var x = Math.round(plot.axisX.getCoord(values[j].x, plotarea, plot));
				var y = Math.round(plot.axisY.getCoord(values[j].y, plotarea, plot));
				if (j == 0) {
					cmd.push("m");
					cmd.push(x + "," + y);
				} else {
					cmd.push("l");
					cmd.push(x + "," + y);
				}
				var c = document.createElement("v:oval");
				c.setAttribute("strokeweight", "1px");
				c.setAttribute("strokecolor", values[j].series.color);
				c.setAttribute("fillcolor", values[j].series.color);
				var str = document.createElement("v:stroke");
				str.setAttribute("opacity", "0.8");
				c.appendChild(str);
				str = document.createElement("v:fill");
				str.setAttribute("opacity", "0.6");
				c.appendChild(str);
				var s = c.style;
				s.position = "absolute";
				s.top = (y - r) + "px";
				s.left = (x - r) + "px";
				s.width = (r * 2) + "px";
				s.height = (r * 2) + "px";
				group.appendChild(c);
				if (applyTo) {
					applyTo(c, data[j].src);
				}
			}
			if (i == 0) {
				cmd.push("l");
				cmd.push(x + "," + Math.round(plot.axisY.getCoord(plot.axisX.origin, plotarea, plot)));
				cmd.push("l");
				cmd.push(Math.round(plot.axisX.getCoord(data[0][0].x, plotarea, plot)) + "," + Math.round(plot.axisY.getCoord(plot.axisX.origin, plotarea, plot)));
			} else {
				var values = data[i - 1];
				cmd.push("l");
				cmd.push(x + "," + Math.round(plot.axisY.getCoord(values[values.length - 1].y, plotarea, plot)));
				for (var j = values.length - 2; j >= 0; j--) {
					var x = Math.round(plot.axisX.getCoord(values[j].x, plotarea, plot));
					var y = Math.round(plot.axisY.getCoord(values[j].y, plotarea, plot));
					cmd.push("l");
					cmd.push(x + "," + y);
				}
			}
			path.setAttribute("path", cmd.join(" ") + " x e");
			group.appendChild(path);
		}
		return group;
	}, StackedCurvedArea:function (plotarea, plot, kwArgs, applyTo) {
		var tension = 3;
		var area = plotarea.getArea();
		var group = dojo.charting.Plotters._group(plotarea);
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
			var path = document.createElement("v:shape");
			path.setAttribute("strokeweight", "1px");
			path.setAttribute("strokecolor", data[i][0].series.color);
			path.setAttribute("fillcolor", data[i][0].series.color);
			path.setAttribute("coordsize", (area.right - area.left) + "," + (area.bottom - area.top));
			path.style.position = "absolute";
			path.style.top = "0px";
			path.style.left = "0px";
			path.style.width = area.right - area.left + "px";
			path.style.height = area.bottom - area.top + "px";
			var stroke = document.createElement("v:stroke");
			stroke.setAttribute("opacity", "0.8");
			path.appendChild(stroke);
			var fill = document.createElement("v:fill");
			fill.setAttribute("opacity", "0.4");
			path.appendChild(fill);
			var cmd = [];
			var r = 3;
			for (var j = 0; j < data[i].length; j++) {
				var values = data[i];
				var x = Math.round(plot.axisX.getCoord(values[j].x, plotarea, plot));
				var y = Math.round(plot.axisY.getCoord(values[j].y, plotarea, plot));
				if (j == 0) {
					cmd.push("m");
					cmd.push(x + "," + y);
				} else {
					var lastx = Math.round(plot.axisX.getCoord(values[j - 1].x, plotarea, plot));
					var lasty = Math.round(plot.axisY.getCoord(values[j - 1].y, plotarea, plot));
					var dx = x - lastx;
					var dy = y - lasty;
					cmd.push("c");
					var cx = Math.round((x - (tension - 1) * (dx / tension)));
					cmd.push(cx + "," + lasty);
					cx = Math.round((x - (dx / tension)));
					cmd.push(cx + "," + y);
					cmd.push(x + "," + y);
				}
				var c = document.createElement("v:oval");
				c.setAttribute("strokeweight", "1px");
				c.setAttribute("strokecolor", values[j].series.color);
				c.setAttribute("fillcolor", values[j].series.color);
				var str = document.createElement("v:stroke");
				str.setAttribute("opacity", "0.8");
				c.appendChild(str);
				str = document.createElement("v:fill");
				str.setAttribute("opacity", "0.6");
				c.appendChild(str);
				var s = c.style;
				s.position = "absolute";
				s.top = (y - r) + "px";
				s.left = (x - r) + "px";
				s.width = (r * 2) + "px";
				s.height = (r * 2) + "px";
				group.appendChild(c);
				if (applyTo) {
					applyTo(c, data[j].src);
				}
			}
			if (i == 0) {
				cmd.push("l");
				cmd.push(x + "," + Math.round(plot.axisY.getCoord(plot.axisX.origin, plotarea, plot)));
				cmd.push("l");
				cmd.push(Math.round(plot.axisX.getCoord(data[0][0].x, plotarea, plot)) + "," + Math.round(plot.axisY.getCoord(plot.axisX.origin, plotarea, plot)));
			} else {
				var values = data[i - 1];
				cmd.push("l");
				cmd.push(x + "," + Math.round(plot.axisY.getCoord(values[values.length - 1].y, plotarea, plot)));
				for (var j = values.length - 2; j >= 0; j--) {
					var x = Math.round(plot.axisX.getCoord(values[j].x, plotarea, plot));
					var y = Math.round(plot.axisY.getCoord(values[j].y, plotarea, plot));
					var lastx = Math.round(plot.axisX.getCoord(values[j + 1].x, plotarea, plot));
					var lasty = Math.round(plot.axisY.getCoord(values[j + 1].y, plotarea, plot));
					var dx = x - lastx;
					var dy = y - lasty;
					cmd.push("c");
					var cx = Math.round((x - (tension - 1) * (dx / tension)));
					cmd.push(cx + "," + lasty);
					cx = Math.round((x - (dx / tension)));
					cmd.push(cx + "," + y);
					cmd.push(x + "," + y);
				}
			}
			path.setAttribute("path", cmd.join(" ") + " x e");
			group.appendChild(path);
		}
		return group;
	}, DataBar:function (data, plotarea, plot, applyTo) {
		var area = plotarea.getArea();
		var group = dojo.charting.Plotters._group(plotarea);
		var n = data.length;
		var w = (area.right - area.left) / (plot.axisX.range.upper - plot.axisX.range.lower);
		var yOrigin = plot.axisY.getCoord(plot.axisX.origin, plotarea, plot);
		for (var i = 0; i < n; i++) {
			var value = data[i].y;
			var yA = yOrigin;
			var x = plot.axisX.getCoord(data[i].x, plotarea, plot) - (w / 2) + 1;
			var y = plot.axisY.getCoord(value, plotarea, plot);
			var h = Math.abs(yA - y);
			if (value < plot.axisX.origin) {
				yA = y;
				y = yOrigin;
			}
			var bar = document.createElement("v:rect");
			bar.style.position = "absolute";
			bar.style.top = y + 1 + "px";
			bar.style.left = x + "px";
			bar.style.width = w + "px";
			bar.style.height = h + "px";
			bar.setAttribute("fillColor", data[i].series.color);
			bar.setAttribute("stroked", "false");
			bar.style.antialias = "false";
			var fill = document.createElement("v:fill");
			fill.setAttribute("opacity", "0.6");
			bar.appendChild(fill);
			if (applyTo) {
				applyTo(bar, data[i].src);
			}
			group.appendChild(bar);
		}
		return group;
	}, Line:function (data, plotarea, plot, applyTo) {
		var area = plotarea.getArea();
		var group = dojo.charting.Plotters._group(plotarea);
		if (data.length == 0) {
			return group;
		}
		var path = document.createElement("v:shape");
		path.setAttribute("strokeweight", "2px");
		path.setAttribute("strokecolor", data[0].series.color);
		path.setAttribute("fillcolor", "none");
		path.setAttribute("filled", "false");
		path.setAttribute("coordsize", (area.right - area.left) + "," + (area.bottom - area.top));
		path.style.position = "absolute";
		path.style.top = "0px";
		path.style.left = "0px";
		path.style.width = area.right - area.left + "px";
		path.style.height = area.bottom - area.top + "px";
		var stroke = document.createElement("v:stroke");
		stroke.setAttribute("opacity", "0.8");
		path.appendChild(stroke);
		var cmd = [];
		var r = 3;
		for (var i = 0; i < data.length; i++) {
			var x = Math.round(plot.axisX.getCoord(data[i].x, plotarea, plot));
			var y = Math.round(plot.axisY.getCoord(data[i].y, plotarea, plot));
			if (i == 0) {
				cmd.push("m");
				cmd.push(x + "," + y);
			} else {
				cmd.push("l");
				cmd.push(x + "," + y);
			}
			var c = document.createElement("v:oval");
			c.setAttribute("strokeweight", "1px");
			c.setAttribute("strokecolor", data[i].series.color);
			c.setAttribute("fillcolor", data[i].series.color);
			var str = document.createElement("v:stroke");
			str.setAttribute("opacity", "0.8");
			c.appendChild(str);
			str = document.createElement("v:fill");
			str.setAttribute("opacity", "0.6");
			c.appendChild(str);
			var s = c.style;
			s.position = "absolute";
			s.top = (y - r) + "px";
			s.left = (x - r) + "px";
			s.width = (r * 2) + "px";
			s.height = (r * 2) + "px";
			group.appendChild(c);
			if (applyTo) {
				applyTo(c, data[i].src);
			}
		}
		path.setAttribute("path", cmd.join(" ") + " e");
		group.appendChild(path);
		return group;
	}, CurvedLine:function (data, plotarea, plot, applyTo) {
		var tension = 3;
		var area = plotarea.getArea();
		var group = dojo.charting.Plotters._group(plotarea);
		if (data.length == 0) {
			return group;
		}
		var path = document.createElement("v:shape");
		path.setAttribute("strokeweight", "2px");
		path.setAttribute("strokecolor", data[0].series.color);
		path.setAttribute("fillcolor", "none");
		path.setAttribute("filled", "false");
		path.setAttribute("coordsize", (area.right - area.left) + "," + (area.bottom - area.top));
		path.style.position = "absolute";
		path.style.top = "0px";
		path.style.left = "0px";
		path.style.width = area.right - area.left + "px";
		path.style.height = area.bottom - area.top + "px";
		var stroke = document.createElement("v:stroke");
		stroke.setAttribute("opacity", "0.8");
		path.appendChild(stroke);
		var cmd = [];
		var r = 3;
		for (var i = 0; i < data.length; i++) {
			var x = Math.round(plot.axisX.getCoord(data[i].x, plotarea, plot));
			var y = Math.round(plot.axisY.getCoord(data[i].y, plotarea, plot));
			if (i == 0) {
				cmd.push("m");
				cmd.push(x + "," + y);
			} else {
				var lastx = Math.round(plot.axisX.getCoord(data[i - 1].x, plotarea, plot));
				var lasty = Math.round(plot.axisY.getCoord(data[i - 1].y, plotarea, plot));
				var dx = x - lastx;
				var dy = y - lasty;
				cmd.push("c");
				var cx = Math.round((x - (tension - 1) * (dx / tension)));
				cmd.push(cx + "," + lasty);
				cx = Math.round((x - (dx / tension)));
				cmd.push(cx + "," + y);
				cmd.push(x + "," + y);
			}
			var c = document.createElement("v:oval");
			c.setAttribute("strokeweight", "1px");
			c.setAttribute("strokecolor", data[i].series.color);
			c.setAttribute("fillcolor", data[i].series.color);
			var str = document.createElement("v:stroke");
			str.setAttribute("opacity", "0.8");
			c.appendChild(str);
			str = document.createElement("v:fill");
			str.setAttribute("opacity", "0.6");
			c.appendChild(str);
			var s = c.style;
			s.position = "absolute";
			s.top = (y - r) + "px";
			s.left = (x - r) + "px";
			s.width = (r * 2) + "px";
			s.height = (r * 2) + "px";
			group.appendChild(c);
			if (applyTo) {
				applyTo(c, data[i].src);
			}
		}
		path.setAttribute("path", cmd.join(" ") + " e");
		group.appendChild(path);
		return group;
	}, Area:function (data, plotarea, plot, applyTo) {
		var area = plotarea.getArea();
		var group = dojo.charting.Plotters._group(plotarea);
		if (data.length == 0) {
			return group;
		}
		var path = document.createElement("v:shape");
		path.setAttribute("strokeweight", "1px");
		path.setAttribute("strokecolor", data[0].series.color);
		path.setAttribute("fillcolor", data[0].series.color);
		path.setAttribute("coordsize", (area.right - area.left) + "," + (area.bottom - area.top));
		path.style.position = "absolute";
		path.style.top = "0px";
		path.style.left = "0px";
		path.style.width = area.right - area.left + "px";
		path.style.height = area.bottom - area.top + "px";
		var stroke = document.createElement("v:stroke");
		stroke.setAttribute("opacity", "0.8");
		path.appendChild(stroke);
		var fill = document.createElement("v:fill");
		fill.setAttribute("opacity", "0.4");
		path.appendChild(fill);
		var cmd = [];
		var r = 3;
		for (var i = 0; i < data.length; i++) {
			var x = Math.round(plot.axisX.getCoord(data[i].x, plotarea, plot));
			var y = Math.round(plot.axisY.getCoord(data[i].y, plotarea, plot));
			if (i == 0) {
				cmd.push("m");
				cmd.push(x + "," + y);
			} else {
				cmd.push("l");
				cmd.push(x + "," + y);
			}
			var c = document.createElement("v:oval");
			c.setAttribute("strokeweight", "1px");
			c.setAttribute("strokecolor", data[i].series.color);
			c.setAttribute("fillcolor", data[i].series.color);
			var str = document.createElement("v:stroke");
			str.setAttribute("opacity", "0.8");
			c.appendChild(str);
			str = document.createElement("v:fill");
			str.setAttribute("opacity", "0.6");
			c.appendChild(str);
			var s = c.style;
			s.position = "absolute";
			s.top = (y - r) + "px";
			s.left = (x - r) + "px";
			s.width = (r * 2) + "px";
			s.height = (r * 2) + "px";
			group.appendChild(c);
			if (applyTo) {
				applyTo(c, data[i].src);
			}
		}
		cmd.push("l");
		cmd.push(x + "," + Math.round(plot.axisY.getCoord(plot.axisX.origin, plotarea, plot)));
		cmd.push("l");
		cmd.push(Math.round(plot.axisX.getCoord(data[0].x, plotarea, plot)) + "," + Math.round(plot.axisY.getCoord(plot.axisX.origin, plotarea, plot)));
		path.setAttribute("path", cmd.join(" ") + " x e");
		group.appendChild(path);
		return group;
	}, CurvedArea:function (data, plotarea, plot, applyTo) {
		var tension = 3;
		var area = plotarea.getArea();
		var group = dojo.charting.Plotters._group(plotarea);
		if (data.length == 0) {
			return group;
		}
		var path = document.createElement("v:shape");
		path.setAttribute("strokeweight", "1px");
		path.setAttribute("strokecolor", data[0].series.color);
		path.setAttribute("fillcolor", data[0].series.color);
		path.setAttribute("coordsize", (area.right - area.left) + "," + (area.bottom - area.top));
		path.style.position = "absolute";
		path.style.top = "0px";
		path.style.left = "0px";
		path.style.width = area.right - area.left + "px";
		path.style.height = area.bottom - area.top + "px";
		var stroke = document.createElement("v:stroke");
		stroke.setAttribute("opacity", "0.8");
		path.appendChild(stroke);
		var fill = document.createElement("v:fill");
		fill.setAttribute("opacity", "0.4");
		path.appendChild(fill);
		var cmd = [];
		var r = 3;
		for (var i = 0; i < data.length; i++) {
			var x = Math.round(plot.axisX.getCoord(data[i].x, plotarea, plot));
			var y = Math.round(plot.axisY.getCoord(data[i].y, plotarea, plot));
			if (i == 0) {
				cmd.push("m");
				cmd.push(x + "," + y);
			} else {
				var lastx = Math.round(plot.axisX.getCoord(data[i - 1].x, plotarea, plot));
				var lasty = Math.round(plot.axisY.getCoord(data[i - 1].y, plotarea, plot));
				var dx = x - lastx;
				var dy = y - lasty;
				cmd.push("c");
				var cx = Math.round((x - (tension - 1) * (dx / tension)));
				cmd.push(cx + "," + lasty);
				cx = Math.round((x - (dx / tension)));
				cmd.push(cx + "," + y);
				cmd.push(x + "," + y);
			}
			var c = document.createElement("v:oval");
			c.setAttribute("strokeweight", "1px");
			c.setAttribute("strokecolor", data[i].series.color);
			c.setAttribute("fillcolor", data[i].series.color);
			var str = document.createElement("v:stroke");
			str.setAttribute("opacity", "0.8");
			c.appendChild(str);
			str = document.createElement("v:fill");
			str.setAttribute("opacity", "0.6");
			c.appendChild(str);
			var s = c.style;
			s.position = "absolute";
			s.top = (y - r) + "px";
			s.left = (x - r) + "px";
			s.width = (r * 2) + "px";
			s.height = (r * 2) + "px";
			group.appendChild(c);
			if (applyTo) {
				applyTo(c, data[i].src);
			}
		}
		cmd.push("l");
		cmd.push(x + "," + Math.round(plot.axisY.getCoord(plot.axisX.origin, plotarea, plot)));
		cmd.push("l");
		cmd.push(Math.round(plot.axisX.getCoord(data[0].x, plotarea, plot)) + "," + Math.round(plot.axisY.getCoord(plot.axisX.origin, plotarea, plot)));
		path.setAttribute("path", cmd.join(" ") + " x e");
		group.appendChild(path);
		return group;
	}, HighLow:function (data, plotarea, plot, applyTo) {
		var area = plotarea.getArea();
		var group = dojo.charting.Plotters._group(plotarea);
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
			var bar = document.createElement("v:rect");
			bar.style.position = "absolute";
			bar.style.top = y + 1 + "px";
			bar.style.left = x + "px";
			bar.style.width = w + "px";
			bar.style.height = h + "px";
			bar.setAttribute("fillColor", data[i].series.color);
			bar.setAttribute("stroked", "false");
			bar.style.antialias = "false";
			var fill = document.createElement("v:fill");
			fill.setAttribute("opacity", "0.6");
			bar.appendChild(fill);
			if (applyTo) {
				applyTo(bar, data[i].src);
			}
			group.appendChild(bar);
		}
		return group;
	}, HighLowClose:function (data, plotarea, plot, applyTo) {
		var area = plotarea.getArea();
		var group = dojo.charting.Plotters._group(plotarea);
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
			var g = document.createElement("div");
			var bar = document.createElement("v:rect");
			bar.style.position = "absolute";
			bar.style.top = y + 1 + "px";
			bar.style.left = x + "px";
			bar.style.width = w + "px";
			bar.style.height = h + "px";
			bar.setAttribute("fillColor", data[i].series.color);
			bar.setAttribute("stroked", "false");
			bar.style.antialias = "false";
			var fill = document.createElement("v:fill");
			fill.setAttribute("opacity", "0.6");
			bar.appendChild(fill);
			g.appendChild(bar);
			var line = document.createElement("v:line");
			line.setAttribute("strokecolor", data[i].series.color);
			line.setAttribute("strokeweight", "1px");
			line.setAttribute("from", x + "px," + close + "px");
			line.setAttribute("to", (x + w + (part * 2) - 2) + "px," + close + "px");
			var s = line.style;
			s.position = "absolute";
			s.top = "0px";
			s.left = "0px";
			s.antialias = "false";
			var str = document.createElement("v:stroke");
			str.setAttribute("opacity", "0.6");
			line.appendChild(str);
			g.appendChild(line);
			if (applyTo) {
				applyTo(g, data[i].src);
			}
			group.appendChild(g);
		}
		return group;
	}, HighLowOpenClose:function (data, plotarea, plot, applyTo) {
		var area = plotarea.getArea();
		var group = dojo.charting.Plotters._group(plotarea);
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
			var g = document.createElement("div");
			var bar = document.createElement("v:rect");
			bar.style.position = "absolute";
			bar.style.top = y + 1 + "px";
			bar.style.left = x + "px";
			bar.style.width = w + "px";
			bar.style.height = h + "px";
			bar.setAttribute("fillColor", data[i].series.color);
			bar.setAttribute("stroked", "false");
			bar.style.antialias = "false";
			var fill = document.createElement("v:fill");
			fill.setAttribute("opacity", "0.6");
			bar.appendChild(fill);
			g.appendChild(bar);
			var line = document.createElement("v:line");
			line.setAttribute("strokecolor", data[i].series.color);
			line.setAttribute("strokeweight", "1px");
			line.setAttribute("from", (x - (part * 2)) + "px," + open + "px");
			line.setAttribute("to", (x + w - 2) + "px," + open + "px");
			var s = line.style;
			s.position = "absolute";
			s.top = "0px";
			s.left = "0px";
			s.antialias = "false";
			var str = document.createElement("v:stroke");
			str.setAttribute("opacity", "0.6");
			line.appendChild(str);
			g.appendChild(line);
			var line = document.createElement("v:line");
			line.setAttribute("strokecolor", data[i].series.color);
			line.setAttribute("strokeweight", "1px");
			line.setAttribute("from", x + "px," + close + "px");
			line.setAttribute("to", (x + w + (part * 2) - 2) + "px," + close + "px");
			var s = line.style;
			s.position = "absolute";
			s.top = "0px";
			s.left = "0px";
			s.antialias = "false";
			var str = document.createElement("v:stroke");
			str.setAttribute("opacity", "0.6");
			line.appendChild(str);
			g.appendChild(line);
			if (applyTo) {
				applyTo(g, data[i].src);
			}
			group.appendChild(g);
		}
		return group;
	}, Scatter:function (data, plotarea, plot, applyTo) {
		var r = 6;
		var mod = r / 2;
		var area = plotarea.getArea();
		var group = dojo.charting.Plotters._group(plotarea);
		for (var i = 0; i < data.length; i++) {
			var x = Math.round(plot.axisX.getCoord(data[i].x, plotarea, plot));
			var y = Math.round(plot.axisY.getCoord(data[i].y, plotarea, plot));
			var point = document.createElement("v:rect");
			point.setAttribute("strokecolor", data[i].series.color);
			point.setAttribute("fillcolor", data[i].series.color);
			var fill = document.createElement("v:fill");
			fill.setAttribute("opacity", "0.6");
			point.appendChild(fill);
			var s = point.style;
			s.position = "absolute";
			s.rotation = "45";
			s.top = (y - mod) + "px";
			s.left = (x - mod) + "px";
			s.width = r + "px";
			s.height = r + "px";
			group.appendChild(point);
			if (applyTo) {
				applyTo(point, data[i].src);
			}
		}
		return group;
	}, Bubble:function (data, plotarea, plot, applyTo) {
		var sizeFactor = 1;
		var area = plotarea.getArea();
		var group = dojo.charting.Plotters._group(plotarea);
		for (var i = 0; i < data.length; i++) {
			var x = Math.round(plot.axisX.getCoord(data[i].x, plotarea, plot));
			var y = Math.round(plot.axisY.getCoord(data[i].y, plotarea, plot));
			if (i == 0) {
				var raw = data[i].size;
				var dy = plot.axisY.getCoord(data[i].y + raw, plotarea, plot) - y;
				sizeFactor = dy / raw;
			}
			if (sizeFactor < 1) {
				sizeFactor = 1;
			}
			var r = (data[i].size / 2) * sizeFactor;
			var point = document.createElement("v:oval");
			point.setAttribute("strokecolor", data[i].series.color);
			point.setAttribute("fillcolor", data[i].series.color);
			var fill = document.createElement("v:fill");
			fill.setAttribute("opacity", "0.6");
			point.appendChild(fill);
			var s = point.style;
			s.position = "absolute";
			s.rotation = "45";
			s.top = (y - r) + "px";
			s.left = (x - r) + "px";
			s.width = (r * 2) + "px";
			s.height = (r * 2) + "px";
			group.appendChild(point);
			if (applyTo) {
				applyTo(point, data[i].src);
			}
		}
		return group;
	}});
	dojo.charting.Plotters["Default"] = dojo.charting.Plotters.Line;
}

