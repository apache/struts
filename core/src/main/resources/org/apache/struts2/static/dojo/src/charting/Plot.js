/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.charting.Plot");
dojo.require("dojo.lang.common");
dojo.require("dojo.charting.Axis");
dojo.require("dojo.charting.Series");

dojo.charting.RenderPlotSeries = { Singly:"single", Grouped:"grouped" };

dojo.charting.Plot = function(
	/* dojo.charting.Axis? */xaxis, 
	/* dojo.charting.Axis? */yaxis, 
	/* dojo.charting.Series[]? */series
){
	//	summary
	//	Creates a new instance of a Plot (X/Y Axis + n Series).
	var id = "dojo-charting-plot-"+dojo.charting.Plot.count++;
	this.getId=function(){ return id; };
	this.setId=function(key){ id = key; };
	this.axisX = null;
	this.axisY = null;
	this.series = [];
	this.dataNode = null;

	//	for bar charts, pie charts and stacked charts, change to Grouped.
	this.renderType = dojo.charting.RenderPlotSeries.Singly;
	if(xaxis){
		this.setAxis(xaxis,"x");
	}
	if(yaxis){
		this.setAxis(yaxis,"y");
	}
	if(series){
		for(var i=0; i<series.length; i++){ this.addSeries(series[i]); }
	}
}
dojo.charting.Plot.count=0;

dojo.extend(dojo.charting.Plot, {
	addSeries: function(
		/* dojo.charting.Series || object */series,
		/* function? */plotter
	){
		//	summary
		//	Add a new Series to this plot.  Can take the form of a Series, or an object
		//	of the form { series, plotter }
		if(series.plotter){
			this.series.push(series);
		} else {
			this.series.push({
				data: series,
				plotter: plotter || dojo.charting.Plotters["Default"]
			});
		}
	},
	setAxis: function(/* dojo.charting.Axis */axis, /* string */which){
		//	summary
		//	Set the axis on which plane.
		if(which.toLowerCase()=="x"){ this.axisX = axis; }
		else if(which.toLowerCase()=="y"){ this.axisY = axis; }
	},
	getRanges: function(){
		//	summary
		//	set the ranges on these axes.
		var xmin, xmax, ymin, ymax;
		xmin=ymin=Number.MAX_VALUE;
		xmax=ymax=Number.MIN_VALUE;
		for(var i=0; i<this.series.length; i++){
			var values = this.series[i].data.evaluate();	//	full data range.
			for(var j=0; j<values.length; j++){
				var comp=values[j];
				xmin=Math.min(comp.x, xmin);
				ymin=Math.min(comp.y, ymin);
				xmax=Math.max(comp.x, xmax);
				ymax=Math.max(comp.y, ymax);
			}
		}
		return {
			x:{ upper: xmax, lower:xmin },
			y:{ upper: ymax, lower:ymin },
			toString:function(){
				return "[ x:"+xmax+" - "+xmin+", y:"+ymax+" - "+ymin+"]";
			}
		};	//	object
	},
	destroy: function(){
		//	summary
		//	Clean out any existing DOM node references.
		var node=this.dataNode;
		while(node && node.childNodes && node.childNodes.length > 0){
			node.removeChild(node.childNodes[0]);
		}
		this.dataNode=null;
	}
});
