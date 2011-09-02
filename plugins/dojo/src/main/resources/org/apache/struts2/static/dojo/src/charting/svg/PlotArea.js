/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.charting.svg.PlotArea");
dojo.require("dojo.lang.common");
if (dojo.render.svg.capable) {
	dojo.require("dojo.svg");
	dojo.extend(dojo.charting.PlotArea, {resize:function () {
		var area = this.getArea();
		this.nodes.area.setAttribute("width", this.size.width);
		this.nodes.area.setAttribute("height", this.size.height);
		var rect = this.nodes.area.getElementsByTagName("rect")[0];
		rect.setAttribute("x", area.left);
		rect.setAttribute("y", area.top);
		rect.setAttribute("width", area.right - area.left);
		rect.setAttribute("height", area.bottom - area.top);
		this.nodes.background.setAttribute("width", this.size.width);
		this.nodes.background.setAttribute("height", this.size.height);
		if (this.nodes.plots) {
			this.nodes.area.removeChild(this.nodes.plots);
			this.nodes.plots = null;
		}
		this.nodes.plots = document.createElementNS(dojo.svg.xmlns.svg, "g");
		this.nodes.plots.setAttribute("id", this.getId() + "-plots");
		this.nodes.plots.setAttribute("style", "clip-path:url(#" + this.getId() + "-clip);");
		this.nodes.area.appendChild(this.nodes.plots);
		for (var i = 0; i < this.plots.length; i++) {
			this.nodes.plots.appendChild(this.initializePlot(this.plots[i]));
		}
		if (this.nodes.axes) {
			this.nodes.area.removeChild(this.nodes.axes);
			this.nodes.axes = null;
		}
		this.nodes.axes = document.createElementNS(dojo.svg.xmlns.svg, "g");
		this.nodes.axes.setAttribute("id", this.getId() + "-axes");
		this.nodes.area.appendChild(this.nodes.axes);
		var axes = this.getAxes();
		for (var p in axes) {
			var obj = axes[p];
			this.nodes.axes.appendChild(obj.axis.initialize(this, obj.plot, obj.drawAgainst, obj.plane));
		}
	}, initializePlot:function (plot) {
		plot.destroy();
		plot.dataNode = document.createElementNS(dojo.svg.xmlns.svg, "g");
		plot.dataNode.setAttribute("id", plot.getId());
		return plot.dataNode;
	}, initialize:function () {
		this.destroy();
		this.nodes.main = document.createElement("div");
		this.nodes.area = document.createElementNS(dojo.svg.xmlns.svg, "svg");
		this.nodes.area.setAttribute("id", this.getId());
		this.nodes.main.appendChild(this.nodes.area);
		var defs = document.createElementNS(dojo.svg.xmlns.svg, "defs");
		var clip = document.createElementNS(dojo.svg.xmlns.svg, "clipPath");
		clip.setAttribute("id", this.getId() + "-clip");
		var rect = document.createElementNS(dojo.svg.xmlns.svg, "rect");
		clip.appendChild(rect);
		defs.appendChild(clip);
		this.nodes.area.appendChild(defs);
		this.nodes.background = document.createElementNS(dojo.svg.xmlns.svg, "rect");
		this.nodes.background.setAttribute("id", this.getId() + "-background");
		this.nodes.background.setAttribute("fill", "#fff");
		this.nodes.area.appendChild(this.nodes.background);
		this.resize();
		return this.nodes.main;
	}});
}

