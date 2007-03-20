/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.charting.svg.Plotters");
dojo.require("dojo.lang.common");
dojo.require("dojo.svg");

//	TODO for 0.5: look at replacing manual plotting with dojo.gfx.

//	Mixin the SVG-specific plotter object.
dojo.mixin(dojo.charting.Plotters, {
	/*********************************************************
	 *	Grouped plotters: need all series on a plot at once.
	 *********************************************************/
	Bar: function(
		/* dojo.charting.PlotArea */plotarea,
		/* dojo.charting.Plot */plot,
		/* object? */kwArgs,
		/* function? */applyTo
	){
		//	summary
		//	Plots a set of grouped bars.
		//	Bindings: y
		var area = plotarea.getArea();
		var group = document.createElementNS(dojo.svg.xmlns.svg, "g");
		
		//	precompile the data
		var n = plot.series.length;	//	how many series
		var data = [];
		for(var i=0; i<n; i++){
			var tmp = plot.series[i].data.evaluate(kwArgs);
			data.push(tmp);
		}

		//	calculate the width of each bar.
		var space = 8;
		var nPoints = data[0].length;
		var width = ((area.right-area.left)-(space*(nPoints-1)))/nPoints;	//	the width of each group.
		var barWidth = width/n;	//	the width of each bar, no spaces.
		var yOrigin = plot.axisY.getCoord(plot.axisX.origin, plotarea, plot);

		for(var i=0; i<nPoints; i++){
			//	calculate offset
			var xStart = area.left+(width*i)+(space*i);
			for(var j=0; j<n; j++){
				var value = data[j][i].y;
				var yA = yOrigin;
				var x = xStart + (barWidth*j);
				var y = plot.axisY.getCoord(value, plotarea, plot);
				var h = Math.abs(yA-y);
				if(value < plot.axisX.origin){
					yA = y;
					y = yOrigin;
				}
				
				var bar=document.createElementNS(dojo.svg.xmlns.svg, "rect");
				bar.setAttribute("fill", data[j][i].series.color);
				bar.setAttribute("stroke-width", "0");
				bar.setAttribute("x", x);
				bar.setAttribute("y", y);
				bar.setAttribute("width", barWidth);
				bar.setAttribute("height", h);
				bar.setAttribute("fill-opacity", "0.6");
				if(applyTo){ applyTo(bar, data[j][i].src); }
				group.appendChild(bar);
			}
		}
		return group;	// SVGGElement
	},
	HorizontalBar: function(
		/* dojo.charting.PlotArea */plotarea,
		/* dojo.charting.Plot */plot,
		/* object? */kwArgs,
		/* function? */applyTo
	){
		//	summary
		//	Plots data in a set of grouped bars horizontally.
		//	Bindings: y
		var area = plotarea.getArea();
		var group = document.createElementNS(dojo.svg.xmlns.svg, "g");
		
		//	precompile the data
		var n = plot.series.length;	//	how many series
		var data = [];
		for(var i=0; i<n; i++){
			var tmp = plot.series[i].data.evaluate(kwArgs);
			data.push(tmp);
		}

		var space = 6;
		var nPoints = data[0].length;
		var h = ((area.bottom-area.top)-(space*(nPoints-1)))/nPoints;
		var barH = h/n;
		var xOrigin = plot.axisX.getCoord(0, plotarea, plot);

		for(var i=0; i<nPoints; i++){
			//	calculate offset
			var yStart = area.top+(h*i)+(space*i);
			for(var j=0; j<n; j++){
				var value = data[j][i].y;
				var y = yStart + (barH*j);
				var xA = xOrigin;
				var x = plot.axisX.getCoord(value, plotarea, plot);
				var w = Math.abs(x-xA);
				if(value > 0){
					x = xOrigin;
				}
				
				var bar=document.createElementNS(dojo.svg.xmlns.svg, "rect");
				bar.setAttribute("fill", data[j][i].series.color);
				bar.setAttribute("stroke-width", "0");
				bar.setAttribute("x", xA);
				bar.setAttribute("y", y);
				bar.setAttribute("width", w);
				bar.setAttribute("height", barH);
				bar.setAttribute("fill-opacity", "0.6");
				if(applyTo){ applyTo(bar, data[j][i].src); }
				group.appendChild(bar);
			}
		}
		return group;	//	SVGGElement
	},
	Gantt: function(
		/* dojo.charting.PlotArea */plotarea,
		/* dojo.charting.Plot */plot,
		/* object? */kwArgs,
		/* function? */applyTo
	){
		//	summary
		//	Plots a grouped set of Gantt bars
		//	Bindings: high/low
		var area = plotarea.getArea();
		var group = document.createElementNS(dojo.svg.xmlns.svg, "g");

		//	precompile the data
		var n = plot.series.length;	//	how many series
		var data = [];
		for(var i=0; i<n; i++){
			var tmp = plot.series[i].data.evaluate(kwArgs);
			data.push(tmp);
		}

		var space = 2;
		var nPoints = data[0].length;
		var h = ((area.bottom-area.top)-(space*(nPoints-1)))/nPoints;
		var barH = h/n;
		for(var i=0; i<nPoints; i++){
			//	calculate offset
			var yStart = area.top+(h*i)+(space*i);
			for(var j=0; j<n; j++){
				var high = data[j][i].high;
				var low = data[j][i].low;
				if(low > high){
					var t = high;
					high = low;
					low = t;
				}
				var x = plot.axisX.getCoord(low, plotarea, plot);
				var w = plot.axisX.getCoord(high, plotarea, plot) - x;
				var y = yStart + (barH*j);
				
				var bar=document.createElementNS(dojo.svg.xmlns.svg, "rect");
				bar.setAttribute("fill", data[j][i].series.color);
				bar.setAttribute("stroke-width", "0");
				bar.setAttribute("x", x);
				bar.setAttribute("y", y);
				bar.setAttribute("width", w);
				bar.setAttribute("height", barH);
				bar.setAttribute("fill-opacity", "0.6");
				if(applyTo){ applyTo(bar, data[j][i].src); }
				group.appendChild(bar);
			}
		}
		return group;	//	SVGGElement
	},
	StackedArea: function(
		/* dojo.charting.PlotArea */plotarea,
		/* dojo.charting.Plot */plot,
		/* object? */kwArgs,
		/* function? */applyTo
	){
		//	summary
		//	Plots a set of stacked areas.
		//	Bindings: x/y
		var area = plotarea.getArea();
		var group = document.createElementNS(dojo.svg.xmlns.svg, "g");

		//	precompile the data
		var n = plot.series.length;	//	how many series
		var data = [];
		var totals = [];

		//	we're assuming that all series for this plot has the name x assignment for now.
		for(var i=0; i<n; i++){
			var tmp = plot.series[i].data.evaluate(kwArgs);
			//	run through and add current totals
			for(var j=0; j<tmp.length; j++){
				if(i==0){ totals.push(tmp[j].y); }
				else { totals[j] += tmp[j].y; }
				tmp[j].y = totals[j];
			}
			data.push(tmp);
		}

		for(var i=n-1; i>=0; i--){
			var path = document.createElementNS(dojo.svg.xmlns.svg, "path");
			path.setAttribute("fill", data[i][0].series.color);
			path.setAttribute("fill-opacity", "0.4");
			path.setAttribute("stroke", data[i][0].series.color);
			path.setAttribute("stroke-width" , "1");
			path.setAttribute("stroke-opacity", "0.85");

			var cmd = [];
			var r=3;
			for(var j=0; j<data[i].length; j++){
				var values = data[i];
				var x = plot.axisX.getCoord(values[j].x, plotarea, plot);
				var y = plot.axisY.getCoord(values[j].y, plotarea, plot);

				if(j==0){ cmd.push("M"); }
				else { cmd.push("L"); }
				cmd.push(x+","+y);
				
				//	points on the line
				var c=document.createElementNS(dojo.svg.xmlns.svg, "circle");
				c.setAttribute("cx",x);
				c.setAttribute("cy",y);
				c.setAttribute("r","3");
				c.setAttribute("fill", values[j].series.color);
				c.setAttribute("fill-opacity", "0.6");
				c.setAttribute("stroke-width", "1");
				c.setAttribute("stroke-opacity", "0.85");
				group.appendChild(c);
				if(applyTo){ applyTo(c, data[i].src); }
			}

			//	now run the path backwards from the previous series.
			if(i == 0){
				cmd.push("L");
				cmd.push(x + "," + plot.axisY.getCoord(plot.axisX.origin, plotarea, plot));
				cmd.push("L");
				cmd.push(plot.axisX.getCoord(data[0][0].x, plotarea, plot) + "," +  plot.axisY.getCoord(plot.axisX.origin, plotarea, plot));
				cmd.push("Z");
			} else {
				var values = data[i-1];
				cmd.push("L");
				cmd.push(x + "," + Math.round(plot.axisY.getCoord(values[values.length-1].y, plotarea, plot)));
				for(var j=values.length-2; j>=0; j--){
					var x = plot.axisX.getCoord(values[j].x, plotarea, plot);
					var y = plot.axisY.getCoord(values[j].y, plotarea, plot);
					cmd.push("L");
					cmd.push(x+","+y);
				}
			}
			path.setAttribute("d", cmd.join(" ")+ " Z");
			group.appendChild(path);
		}
		return group;	//	SVGGElement
	},
	StackedCurvedArea: function(
		/* dojo.charting.PlotArea */plotarea,
		/* dojo.charting.Plot */plot,
		/* object? */kwArgs,
		/* function? */applyTo
	){
		//	summary
		//	Plots a set of stacked areas, using a tensioning factor to soften points.
		//	Bindings: x/y
		var tension = 3;
		var area = plotarea.getArea();
		var group = document.createElementNS(dojo.svg.xmlns.svg, "g");

		//	precompile the data
		var n = plot.series.length;	//	how many series
		var data = [];
		var totals = [];

		//	we're assuming that all series for this plot has the name x assignment for now.
		for(var i=0; i<n; i++){
			var tmp = plot.series[i].data.evaluate(kwArgs);
			//	run through and add current totals
			for(var j=0; j<tmp.length; j++){
				if(i==0){ totals.push(tmp[j].y); }
				else { totals[j] += tmp[j].y; }
				tmp[j].y = totals[j];
			}
			data.push(tmp);
		}

		for(var i=n-1; i>=0; i--){
			var path = document.createElementNS(dojo.svg.xmlns.svg, "path");
			path.setAttribute("fill", data[i][0].series.color);
			path.setAttribute("fill-opacity", "0.4");
			path.setAttribute("stroke", data[i][0].series.color);
			path.setAttribute("stroke-width" , "1");
			path.setAttribute("stroke-opacity", "0.85");

			var cmd = [];
			var r=3;
			for(var j=0; j<data[i].length; j++){
				var values = data[i];
				var x = plot.axisX.getCoord(values[j].x, plotarea, plot);
				var y = plot.axisY.getCoord(values[j].y, plotarea, plot);
				var dx = area.left + 1;
				var dy = area.bottom;
				if(j>0){
					dx = x - plot.axisX.getCoord(values[j-1].x, plotarea, plot);
					dy = plot.axisY.getCoord(values[j-1].y, plotarea, plot);
				}

				if(j==0){ cmd.push("M"); }
				else {
					cmd.push("C");
					var cx = x-(tension-1) * (dx/tension);
					cmd.push(cx + "," + dy);
					cx = x - (dx/tension);
					cmd.push(cx + "," + y);
				}
				cmd.push(x+","+y);
				
				//	points on the line
				var c=document.createElementNS(dojo.svg.xmlns.svg, "circle");
				c.setAttribute("cx",x);
				c.setAttribute("cy",y);
				c.setAttribute("r","3");
				c.setAttribute("fill", values[j].series.color);
				c.setAttribute("fill-opacity", "0.6");
				c.setAttribute("stroke-width", "1");
				c.setAttribute("stroke-opacity", "0.85");
				group.appendChild(c);
				if(applyTo){ applyTo(c, data[i].src); }
			}

			//	now run the path backwards from the previous series.
			if(i == 0){
				cmd.push("L");
				cmd.push(x + "," + plot.axisY.getCoord(plot.axisX.origin, plotarea, plot));
				cmd.push("L");
				cmd.push(plot.axisX.getCoord(data[0][0].x, plotarea, plot) + "," +  plot.axisY.getCoord(plot.axisX.origin, plotarea, plot));
				cmd.push("Z");
			} else {
				var values = data[i-1];
				cmd.push("L");
				cmd.push(x + "," + Math.round(plot.axisY.getCoord(values[values.length-1].y, plotarea, plot)));
				for(var j=values.length-2; j>=0; j--){
					var x = plot.axisX.getCoord(values[j].x, plotarea, plot);
					var y = plot.axisY.getCoord(values[j].y, plotarea, plot);
					var dx = x - plot.axisX.getCoord(values[j+1].x, plotarea, plot);
					var dy = plot.axisY.getCoord(values[j+1].y, plotarea, plot);

					cmd.push("C");
					var cx = x-(tension-1) * (dx/tension);
					cmd.push(cx + "," + dy);
					cx = x - (dx/tension);
					cmd.push(cx + "," + y);
					cmd.push(x+","+y);
				}
			}
			path.setAttribute("d", cmd.join(" ")+ " Z");
			group.appendChild(path);
		}
		return group;	//	SVGGElement
	},

	/*********************************************************
	 *	Single plotters: one series at a time.
	 *********************************************************/
	DataBar: function(
		/* array */data, 
		/* dojo.charting.PlotArea */plotarea,
		/* dojo.charting.Plot */plot,
		/* function? */applyTo
	){
		//	summary
		//	Plots a set of bars in relation to y==0.
		//	Bindings: x/y
		var area = plotarea.getArea();
		var group = document.createElementNS(dojo.svg.xmlns.svg, "g");
		
		var n = data.length;
		var w = (area.right-area.left)/(plot.axisX.range.upper - plot.axisX.range.lower);	//	the width of each group.
		var yOrigin = plot.axisY.getCoord(plot.axisX.origin, plotarea, plot);

		for(var i=0; i<n; i++){
			//	calculate offset
			var value = data[i].y;
			var yA = yOrigin;
			var x = plot.axisX.getCoord(data[i].x, plotarea, plot) - (w/2);
			var y = plot.axisY.getCoord(value, plotarea, plot);
			var h = Math.abs(yA-y);
			if(value < plot.axisX.origin){
				yA = y;
				y = yOrigin;
			}
			var bar=document.createElementNS(dojo.svg.xmlns.svg, "rect");
			bar.setAttribute("fill", data[i].series.color);
			bar.setAttribute("stroke-width", "0");
			bar.setAttribute("x", x);
			bar.setAttribute("y", y);
			bar.setAttribute("width", w);
			bar.setAttribute("height", h);
			bar.setAttribute("fill-opacity", "0.6");
			if(applyTo){ applyTo(bar, data[i].src); }
			group.appendChild(bar);
		}
		return group;	//	SVGGElement
	},
	Line: function(
		/* array */data, 
		/* dojo.charting.PlotArea */plotarea,
		/* dojo.charting.Plot */plot,
		/* function? */applyTo
	){
		//	summary
		//	Plots the series as a line.
		//	Bindings: x/y
		var area = plotarea.getArea();
		var line = document.createElementNS(dojo.svg.xmlns.svg, "g");
		var path = document.createElementNS(dojo.svg.xmlns.svg, "path");
		line.appendChild(path);

		path.setAttribute("fill", "none");
		path.setAttribute("stroke", data[0].series.color);
		path.setAttribute("stroke-width" , "2");
		path.setAttribute("stroke-opacity", "0.85");
		if(data[0].series.label != null){
			path.setAttribute("title", data[0].series.label);
		}

		var cmd=[];
		for(var i=0; i<data.length; i++){
			var x = plot.axisX.getCoord(data[i].x, plotarea, plot);
			var y = plot.axisY.getCoord(data[i].y, plotarea, plot);
			if(i==0){ cmd.push("M"); }
			else { cmd.push("L"); }
			cmd.push(x+","+y);
			
			//	points on the line
			var c=document.createElementNS(dojo.svg.xmlns.svg, "circle");
			c.setAttribute("cx",x);
			c.setAttribute("cy",y);
			c.setAttribute("r","3");
			c.setAttribute("fill", data[i].series.color);
			c.setAttribute("fill-opacity", "0.6");
			c.setAttribute("stroke-width", "1");
			c.setAttribute("stroke-opacity", "0.85");
			line.appendChild(c);
			if(applyTo){ applyTo(c, data[i].src); }
		}
		path.setAttribute("d", cmd.join(" "));
		return line;	//	SVGGElement
	},
	CurvedLine: function(
		/* array */data, 
		/* dojo.charting.PlotArea */plotarea,
		/* dojo.charting.Plot */plot,
		/* function? */applyTo
	){
		//	summary
		//	Plots the series as a line with a tension factor for softening.
		//	Bindings: x/y
		var tension = 3;
		var area = plotarea.getArea();
		var line = document.createElementNS(dojo.svg.xmlns.svg, "g");
		var path = document.createElementNS(dojo.svg.xmlns.svg, "path");
		line.appendChild(path);

		path.setAttribute("fill", "none");
		path.setAttribute("stroke", data[0].series.color);
		path.setAttribute("stroke-width" , "2");
		path.setAttribute("stroke-opacity", "0.85");
		if(data[0].series.label != null){
			path.setAttribute("title", data[0].series.label);
		}

		var cmd=[];
		for(var i=0; i<data.length; i++){
			var x = plot.axisX.getCoord(data[i].x, plotarea, plot);
			var y = plot.axisY.getCoord(data[i].y, plotarea, plot);
			var dx = area.left + 1;
			var dy = area.bottom;
			if(i>0){
				dx = x - plot.axisX.getCoord(data[i-1].x, plotarea, plot);
				dy = plot.axisY.getCoord(data[i-1].y, plotarea, plot);
			}

			if(i==0){ cmd.push("M"); }
			else {
				cmd.push("C");
				var cx = x-(tension-1) * (dx/tension);
				cmd.push(cx + "," + dy);
				cx = x - (dx/tension);
				cmd.push(cx + "," + y);
			}
			cmd.push(x+","+y);
			
			//	points on the line
			var c=document.createElementNS(dojo.svg.xmlns.svg, "circle");
			c.setAttribute("cx",x);
			c.setAttribute("cy",y);
			c.setAttribute("r","3");
			c.setAttribute("fill", data[i].series.color);
			c.setAttribute("fill-opacity", "0.6");
			c.setAttribute("stroke-width", "1");
			c.setAttribute("stroke-opacity", "0.85");
			line.appendChild(c);
			if(applyTo){ applyTo(c, data[i].src); }
		}
		path.setAttribute("d", cmd.join(" "));
		return line;	// SVGGElement
	},
	Area: function(
		/* array */data, 
		/* dojo.charting.PlotArea */plotarea,
		/* dojo.charting.Plot */plot,
		/* function? */applyTo
	){
		//	summary
		//	Plots the series as an area.
		//	Bindings: x/y
		var area = plotarea.getArea();
		var line = document.createElementNS(dojo.svg.xmlns.svg, "g");
		var path = document.createElementNS(dojo.svg.xmlns.svg, "path");
		line.appendChild(path);

		path.setAttribute("fill", data[0].series.color);
		path.setAttribute("fill-opacity", "0.4");
		path.setAttribute("stroke", data[0].series.color);
		path.setAttribute("stroke-width" , "1");
		path.setAttribute("stroke-opacity", "0.85");
		if(data[0].series.label != null){
			path.setAttribute("title", data[0].series.label);
		}

		var cmd=[];
		for(var i=0; i<data.length; i++){
			var x = plot.axisX.getCoord(data[i].x, plotarea, plot);
			var y = plot.axisY.getCoord(data[i].y, plotarea, plot);
			if(i==0){ cmd.push("M"); }
			else { cmd.push("L"); }
			cmd.push(x+","+y);
			
			//	points on the line
			var c=document.createElementNS(dojo.svg.xmlns.svg, "circle");
			c.setAttribute("cx",x);
			c.setAttribute("cy",y);
			c.setAttribute("r","3");
			c.setAttribute("fill", data[i].series.color);
			c.setAttribute("fill-opacity", "0.6");
			c.setAttribute("stroke-width", "1");
			c.setAttribute("stroke-opacity", "0.85");
			line.appendChild(c);
			if(applyTo){ applyTo(c, data[i].src); }
		}
		//	finish it off
		cmd.push("L");
		cmd.push(x + "," + plot.axisY.getCoord(plot.axisX.origin, plotarea, plot));
		cmd.push("L");
		cmd.push(plot.axisX.getCoord(data[0].x, plotarea, plot) + "," +  plot.axisY.getCoord(plot.axisX.origin, plotarea, plot));
		cmd.push("Z");
		path.setAttribute("d", cmd.join(" "));
		return line;	//	SVGGElement
	},
	CurvedArea: function(
		/* array */data, 
		/* dojo.charting.PlotArea */plotarea,
		/* dojo.charting.Plot */plot,
		/* function? */applyTo
	){
		//	summary
		//	Plots the series as an area with a tension for softening.
		//	Bindings: x/y
		var tension = 3;
		var area = plotarea.getArea();
		var line = document.createElementNS(dojo.svg.xmlns.svg, "g");
		var path = document.createElementNS(dojo.svg.xmlns.svg, "path");
		line.appendChild(path);

		path.setAttribute("fill", data[0].series.color);
		path.setAttribute("fill-opacity", "0.4");
		path.setAttribute("stroke", data[0].series.color);
		path.setAttribute("stroke-width" , "1");
		path.setAttribute("stroke-opacity", "0.85");
		if(data[0].series.label != null){
			path.setAttribute("title", data[0].series.label);
		}

		var cmd=[];
		for(var i=0; i<data.length; i++){
			var x = plot.axisX.getCoord(data[i].x, plotarea, plot);
			var y = plot.axisY.getCoord(data[i].y, plotarea, plot);
			var dx = area.left + 1;
			var dy = area.bottom;
			if(i>0){
				dx = x - plot.axisX.getCoord(data[i-1].x, plotarea, plot);
				dy = plot.axisY.getCoord(data[i-1].y, plotarea, plot);
			}

			if(i==0){ cmd.push("M"); }
			else {
				cmd.push("C");
				var cx = x-(tension-1) * (dx/tension);
				cmd.push(cx + "," + dy);
				cx = x - (dx/tension);
				cmd.push(cx + "," + y);
			}
			cmd.push(x+","+y);
			
			//	points on the line
			var c=document.createElementNS(dojo.svg.xmlns.svg, "circle");
			c.setAttribute("cx",x);
			c.setAttribute("cy",y);
			c.setAttribute("r","3");
			c.setAttribute("fill", data[i].series.color);
			c.setAttribute("fill-opacity", "0.6");
			c.setAttribute("stroke-width", "1");
			c.setAttribute("stroke-opacity", "0.85");
			line.appendChild(c);
			if(applyTo){ applyTo(c, data[i].src); }
		}
		//	finish it off
		cmd.push("L");
		cmd.push(x + "," + plot.axisY.getCoord(plot.axisX.origin, plotarea, plot));
		cmd.push("L");
		cmd.push(plot.axisX.getCoord(data[0].x, plotarea, plot) + "," +  plot.axisY.getCoord(plot.axisX.origin, plotarea, plot));
		cmd.push("Z");
		path.setAttribute("d", cmd.join(" "));
		return line;	//	SVGGElement
	},
	HighLow: function(
		/* array */data, 
		/* dojo.charting.PlotArea */plotarea,
		/* dojo.charting.Plot */plot,
		/* function? */applyTo
	){
		//	summary
		//	Plots the series as a set of high/low bars.
		//	Bindings: x/high/low
		var area = plotarea.getArea();
		var group = document.createElementNS(dojo.svg.xmlns.svg, "g");
		
		var n = data.length;
		var part = ((area.right-area.left)/(plot.axisX.range.upper - plot.axisX.range.lower))/4;
		var w = part*2;

		for(var i=0; i<n; i++){
			var high = data[i].high;
			var low = data[i].low;
			if(low > high){
				var t = low;
				low = high;
				high = t;
			}

			var x = plot.axisX.getCoord(data[i].x, plotarea, plot) - (w/2);
			var y = plot.axisY.getCoord(high, plotarea, plot);
			var h = plot.axisY.getCoord(low, plotarea, plot)-y;

			//	high + low
			var bar=document.createElementNS(dojo.svg.xmlns.svg, "rect");
			bar.setAttribute("fill", data[i].series.color);
			bar.setAttribute("stroke-width", "0");
			bar.setAttribute("x", x);
			bar.setAttribute("y", y);
			bar.setAttribute("width", w);
			bar.setAttribute("height", h);
			bar.setAttribute("fill-opacity", "0.6");
			if(applyTo){ applyTo(bar, data[i].src); }
			group.appendChild(bar);
		}
		return group;	//	SVGGElement
	},
	HighLowClose: function(
		/* array */data, 
		/* dojo.charting.PlotArea */plotarea,
		/* dojo.charting.Plot */plot,
		/* function? */applyTo
	){
		//	summary
		//	Plots the series as a set of high/low bars with a close indicator.
		//	Bindings: x/high/low/close
		var area = plotarea.getArea();
		var group = document.createElementNS(dojo.svg.xmlns.svg, "g");
		
		var n = data.length;
		var part = ((area.right-area.left)/(plot.axisX.range.upper - plot.axisX.range.lower))/4;
		var w = part*2;

		for(var i=0; i<n; i++){
			var high = data[i].high;
			var low = data[i].low;
			if(low > high){
				var t = low;
				low = high;
				high = t;
			}
			var c = data[i].close;

			var x = plot.axisX.getCoord(data[i].x, plotarea, plot) - (w/2);
			var y = plot.axisY.getCoord(high, plotarea, plot);
			var h = plot.axisY.getCoord(low, plotarea, plot)-y;
			var close = plot.axisY.getCoord(c, plotarea, plot);

			var g = document.createElementNS(dojo.svg.xmlns.svg, "g");

			//	high + low
			var bar=document.createElementNS(dojo.svg.xmlns.svg, "rect");
			bar.setAttribute("fill", data[i].series.color);
			bar.setAttribute("stroke-width", "0");
			bar.setAttribute("x", x);
			bar.setAttribute("y", y);
			bar.setAttribute("width", w);
			bar.setAttribute("height", h);
			bar.setAttribute("fill-opacity", "0.6");
			g.appendChild(bar);

			//	close
			var line=document.createElementNS(dojo.svg.xmlns.svg, "line");
			line.setAttribute("x1", x);
			line.setAttribute("x2", x+w+(part*2));
			line.setAttribute("y1", close);
			line.setAttribute("y2", close);
			line.setAttribute("style", "stroke:"+data[i].series.color+";stroke-width:1px;stroke-opacity:0.6;");
			g.appendChild(line);

			if(applyTo){ applyTo(g, data[i].src); }
			group.appendChild(g);
		}
		return group;	//	SVGGElement
	},
	HighLowOpenClose: function(
		/* array */data, 
		/* dojo.charting.PlotArea */plotarea,
		/* dojo.charting.Plot */plot,
		/* function? */applyTo
	){
		//	summary
		//	Plots the series as a set of high/low bars with open and close indicators.
		//	Bindings: x/high/low/open/close
		var area = plotarea.getArea();
		var group = document.createElementNS(dojo.svg.xmlns.svg, "g");
		
		var n = data.length;
		var part = ((area.right-area.left)/(plot.axisX.range.upper - plot.axisX.range.lower))/4;
		var w = part*2;

		for(var i=0; i<n; i++){
			var high = data[i].high;
			var low = data[i].low;
			if(low > high){
				var t = low;
				low = high;
				high = t;
			}
			var o = data[i].open;
			var c = data[i].close;

			var x = plot.axisX.getCoord(data[i].x, plotarea, plot) - (w/2);
			var y = plot.axisY.getCoord(high, plotarea, plot);
			var h = plot.axisY.getCoord(low, plotarea, plot)-y;
			var open = plot.axisY.getCoord(o, plotarea, plot);
			var close = plot.axisY.getCoord(c, plotarea, plot);

			var g = document.createElementNS(dojo.svg.xmlns.svg, "g");

			//	high + low
			var bar=document.createElementNS(dojo.svg.xmlns.svg, "rect");
			bar.setAttribute("fill", data[i].series.color);
			bar.setAttribute("stroke-width", "0");
			bar.setAttribute("x", x);
			bar.setAttribute("y", y);
			bar.setAttribute("width", w);
			bar.setAttribute("height", h);
			bar.setAttribute("fill-opacity", "0.6");
			g.appendChild(bar);

			//	open
			var line=document.createElementNS(dojo.svg.xmlns.svg, "line");
			line.setAttribute("x1", x-(part*2));
			line.setAttribute("x2", x+w);
			line.setAttribute("y1", open);
			line.setAttribute("y2", open);
			line.setAttribute("style", "stroke:"+data[i].series.color+";stroke-width:1px;stroke-opacity:0.6;");
			g.appendChild(line);

			//	close
			var line=document.createElementNS(dojo.svg.xmlns.svg, "line");
			line.setAttribute("x1", x);
			line.setAttribute("x2", x+w+(part*2));
			line.setAttribute("y1", close);
			line.setAttribute("y2", close);
			line.setAttribute("style", "stroke:"+data[i].series.color+";stroke-width:1px;stroke-opacity:0.6;");
			g.appendChild(line);

			if(applyTo){ applyTo(g, data[i].src); }
			group.appendChild(g);
		}
		return group;	//	SVGGElement
	},
	Scatter: function(
		/* array */data, 
		/* dojo.charting.PlotArea */plotarea,
		/* dojo.charting.Plot */plot,
		/* function? */applyTo
	){
		//	summary
		//	Plots the series as a set of points.
		//	Bindings: x/y
		var r=7;
		var group = document.createElementNS(dojo.svg.xmlns.svg, "g");
		for (var i=0; i<data.length; i++){
			var x = plot.axisX.getCoord(data[i].x, plotarea, plot);
			var y = plot.axisY.getCoord(data[i].y, plotarea, plot);
			var point = document.createElementNS(dojo.svg.xmlns.svg, "path");
			point.setAttribute("fill", data[i].series.color);
			point.setAttribute("stroke-width", "0");
			point.setAttribute("d",
				"M " + x + "," + (y-r) + " " +
				"Q " + x + "," + y + " " + (x+r) + "," + y + " " +
				"Q " + x + "," + y + " " + x + "," + (y+r) + " " +
				"Q " + x + "," + y + " " + (x-r) + "," + y + " " +
				"Q " + x + "," + y + " " + x + "," + (y-r) + " " +
				"Z"
			);
			if(applyTo){ applyTo(point, data[i].src); }
			group.appendChild(point);
		}
		return group;	//	SVGGElement
	},
	Bubble: function(
		/* array */data, 
		/* dojo.charting.PlotArea */plotarea,
		/* dojo.charting.Plot */plot,
		/* function? */applyTo
	){
		//	summary
		//	Plots the series as a set of points with a size factor.
		//	Bindings: x/y/size
		var group = document.createElementNS(dojo.svg.xmlns.svg, "g");
		var sizeFactor=1;
		for (var i=0; i<data.length; i++){
			var x = plot.axisX.getCoord(data[i].x, plotarea, plot);
			var y = plot.axisY.getCoord(data[i].y, plotarea, plot);
			if(i==0){
				//	figure out the size factor, start with the axis with the greater range.
				var raw = data[i].size;
				var dy = plot.axisY.getCoord(data[i].y + raw, plotarea, plot)-y;
				sizeFactor = dy/raw;
			}
			if(sizeFactor<1) { sizeFactor = 1; }
			var point = document.createElementNS(dojo.svg.xmlns.svg, "circle");
			point.setAttribute("fill", data[i].series.color);
			point.setAttribute("fill-opacity", "0.8");
			point.setAttribute("stroke", data[i].series.color);
			point.setAttribute("stroke-width", "1");
			point.setAttribute("cx",x);
			point.setAttribute("cy",y);
			point.setAttribute("r", (data[i].size/2)*sizeFactor);
			if(applyTo){ applyTo(point, data[i].src); }
			group.appendChild(point);
		}
		return group;	//	SVGGElement
	}
});
dojo.charting.Plotters["Default"] = dojo.charting.Plotters.Line;
