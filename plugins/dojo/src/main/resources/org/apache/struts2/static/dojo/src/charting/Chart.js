/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.charting.Chart");
dojo.require("dojo.lang.common");
dojo.require("dojo.charting.PlotArea");
dojo.charting.Chart = function (node, title, description) {
	this.node = node || null;
	this.title = title || "Chart";
	this.description = description || "";
	this.plotAreas = [];
};
dojo.extend(dojo.charting.Chart, {addPlotArea:function (obj, doRender) {
	if (obj.x != null && obj.left == null) {
		obj.left = obj.x;
	}
	if (obj.y != null && obj.top == null) {
		obj.top = obj.y;
	}
	this.plotAreas.push(obj);
	if (doRender) {
		this.render();
	}
}, onInitialize:function (chart) {
}, onRender:function (chart) {
}, onDestroy:function (chart) {
}, initialize:function () {
	if (!this.node) {
		dojo.raise("dojo.charting.Chart.initialize: there must be a root node defined for the Chart.");
	}
	this.destroy();
	this.render();
	this.onInitialize(this);
}, render:function () {
	if (this.node.style.position != "absolute") {
		this.node.style.position = "relative";
	}
	for (var i = 0; i < this.plotAreas.length; i++) {
		var area = this.plotAreas[i].plotArea;
		var node = area.initialize();
		node.style.position = "absolute";
		node.style.top = this.plotAreas[i].top + "px";
		node.style.left = this.plotAreas[i].left + "px";
		this.node.appendChild(node);
		area.render();
	}
}, destroy:function () {
	for (var i = 0; i < this.plotAreas.length; i++) {
		this.plotAreas[i].plotArea.destroy();
	}
	while (this.node && this.node.childNodes && this.node.childNodes.length > 0) {
		this.node.removeChild(this.node.childNodes[0]);
	}
}});

