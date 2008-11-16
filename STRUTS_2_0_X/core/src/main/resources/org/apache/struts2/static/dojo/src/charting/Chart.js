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

dojo.charting.Chart = function(
	/* HTMLElement? */node, 
	/* string? */title, 
	/* string? */description
){
	//	summary
	//	Create the basic Chart object.
	this.node = node || null;
	this.title = title || "Chart";			//	pure string.
	this.description = description || "";	//	HTML is allowed.
	this.plotAreas = [];
};

dojo.extend(dojo.charting.Chart, {
	//	methods
	addPlotArea: function(/* object */obj, /* bool? */doRender){
		//	summary
		//	Add a PlotArea to this chart; object should be in the
		//	form of: { plotArea, (x, y) or (top, left) }
		if(obj.x && !obj.left){ obj.left = obj.x; }
		if(obj.y && !obj.top){ obj.top = obj.y; }
		this.plotAreas.push(obj);
		if(doRender){ this.render(); }
	},
	
	//	events
	onInitialize:function(chart){ },
	onRender:function(chart){ },
	onDestroy:function(chart){ },

	//	standard build methods
	initialize: function(){
		//	summary
		//	Initialize the Chart by rendering it.
		if(!this.node){ 
			dojo.raise("dojo.charting.Chart.initialize: there must be a root node defined for the Chart."); 
		}
		this.destroy();
		this.render();
		this.onInitialize(this);
	},
	render:function(){
		//	summary
		//	Render the chart in its entirety.
		if(this.node.style.position != "absolute"){
			this.node.style.position = "relative";
		}
		for(var i=0; i<this.plotAreas.length; i++){
			var area = this.plotAreas[i].plotArea;
			var node = area.initialize();
			node.style.position = "absolute";
			node.style.top = this.plotAreas[i].top + "px";
			node.style.left = this.plotAreas[i].left + "px";
			this.node.appendChild(node);
			area.render();
		}
	},
	destroy: function(){
		//	summary
		//	Destroy any nodes that have maintained references.

		//	kill any existing plotAreas
		for(var i=0; i<this.plotAreas.length; i++){
			this.plotAreas[i].plotArea.destroy();
		};
		//	clean out any child nodes.
		while(this.node && this.node.childNodes && this.node.childNodes.length > 0){ 
			this.node.removeChild(this.node.childNodes[0]); 
		}
	}
});
